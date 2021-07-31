package com.example.organizze.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.organizze.Helper.Base64Custom;
import com.example.organizze.adapter.AdapterMovimentacao;
import com.example.organizze.config.ConfiguraçãoFirebase;
import com.example.organizze.model.Movimentação;
import com.example.organizze.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    //XML
    ///////////////////////////////////////////////////////////////////////////////////
    private MaterialCalendarView materialCalendarView;
    private TextView textosaldação,textosaldo;
    private RecyclerView recyclerMovimentação;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentação> movimentações = new ArrayList<>();
    private Movimentação movimentação;
    ///////////////////////////////////////////////////////////////////////////////////

    //Númeração
    ///////////////////////////////////////////////////////////////////////////////////
    private double receitaTotal = 0.0;
    private double despesaTotal = 0.0;
    private double resumoUsuario = 0.0;
    private String mesanoSelecionado;
    ///////////////////////////////////////////////////////////////////////////////////

    //Firebase
    ///////////////////////////////////////////////////////////////////////////////////
    private FirebaseAuth autenticação = ConfiguraçãoFirebase.getFirebaseAutenticação();
    private DatabaseReference database = ConfiguraçãoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioref ;
    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListenerMovimentações;
    private DatabaseReference movimentaçãoref;
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Organizze");

        //instância de XML
        ////////////////////////////////////////////////////////
        textosaldação = findViewById(R.id.textosaldação);
        textosaldo = findViewById(R.id.textosaldo);
        materialCalendarView = findViewById(R.id.calendarView);
        recyclerMovimentação = findViewById(R.id.recyclerMovimentos);
        ////////////////////////////////////////////////////////

        configurarcalendario();
        swipe();

        //RecyclerView:
        adapterMovimentacao = new AdapterMovimentacao(movimentações,this);
        //configurar adpter

        //conficurar Recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerMovimentação.setLayoutManager(layoutManager);
        recyclerMovimentação.setHasFixedSize(true);
        recyclerMovimentação.setAdapter(adapterMovimentacao);

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void swipe(){

        ItemTouchHelper.Callback itemtouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragflags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeflags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragflags,swipeflags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirmovimentação(viewHolder);
            }
        };
        new ItemTouchHelper(itemtouch).attachToRecyclerView(recyclerMovimentação);
    }

    public  void  excluirmovimentação (final RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Excluir Movimentação da Conta");
        alert.setMessage("Esta Movimentação será excluida !");
        alert.setCancelable(false);

        alert.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentação = movimentações.get(position);

                String emairef = autenticação.getCurrentUser().getEmail();
                String idusuario = Base64Custom.codificarBase64(emairef);
                movimentaçãoref = database.child("movimentação")
                        .child(idusuario)
                        .child(mesanoSelecionado);

                movimentaçãoref.child(movimentação.getKey()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);
                atualizarsaldo();
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PrincipalActivity.this,"Cancelado", Toast.LENGTH_LONG).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public void recuperarresumo () {

        String emairef = autenticação.getCurrentUser().getEmail();
        String idusuario = Base64Custom.codificarBase64(emairef);
        usuarioref = database.child("usuario").child(idusuario);

        valueEventListener = usuarioref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format(resumoUsuario);

                textosaldação.setText("Olá, " + usuario.getNome());
                textosaldo.setText("R$ "+resultadoFormatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void atualizarsaldo(){

        if (movimentação.getTipo().equals("r")){
            receitaTotal = receitaTotal - movimentação.getValor();
            String emairef = autenticação.getCurrentUser().getEmail();
            String idusuario = Base64Custom.codificarBase64(emairef);
            usuarioref = database.child("usuario").child(idusuario);
            usuarioref.child("receitaTotal").setValue(receitaTotal);
        }

        if(movimentação.getTipo().equals("d")){
            despesaTotal = despesaTotal - movimentação.getValor();
            String emairef = autenticação.getCurrentUser().getEmail();
            String idusuario = Base64Custom.codificarBase64(emairef);
            usuarioref = database.child("usuario").child(idusuario);
            usuarioref.child("despesaTotal").setValue(despesaTotal);
        }
    }

    public void recuperarMovimentações () {
        String emairef = autenticação.getCurrentUser().getEmail();
        String idusuario = Base64Custom.codificarBase64(emairef);
        movimentaçãoref = database.child("movimentação")
                        .child(idusuario)
                        .child(mesanoSelecionado);
        valueEventListenerMovimentações = movimentaçãoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movimentações.clear();

                for (DataSnapshot dados : snapshot.getChildren()){
                    Movimentação movimentação = dados.getValue(Movimentação.class);
                    movimentação.setKey(dados.getKey());
                    movimentações.add(movimentação);
                }
                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                autenticação.signOut();
                startActivity(new Intent(this,MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarReceita (View view){
        startActivity(new Intent(this,ReceitasActivity.class));

    }
    public void adicionarDespesa (View view){
        startActivity(new Intent(this,DespesasActivity.class));

    }

    public void configurarcalendario (){
        CharSequence meses [] = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Novembro","Outubro","Desembro"};
        materialCalendarView.setTitleMonths(meses);
        CalendarDay data = materialCalendarView.getCurrentDate();
        String messelecionado = String.format("%02d",data.getMonth());
        mesanoSelecionado = String.valueOf( messelecionado + "" + data.getYear() );

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String messelecionado = String.format("%02d",date.getMonth());
                mesanoSelecionado = String.valueOf( messelecionado  + "" + date.getYear());
                movimentaçãoref.removeEventListener(valueEventListener);
                recuperarMovimentações();
            }
        });
    }

    @Override
    protected void onStart() {
        recuperarresumo();
        recuperarMovimentações();
        super.onStart();
    }

    @Override
    protected void onStop() {
        usuarioref.removeEventListener(valueEventListener);
        movimentaçãoref.removeEventListener(valueEventListenerMovimentações);
        super.onStop();
    }
}
