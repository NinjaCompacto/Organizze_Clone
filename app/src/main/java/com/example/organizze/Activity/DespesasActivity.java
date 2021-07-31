package com.example.organizze.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.Helper.Base64Custom;
import com.example.organizze.Helper.DataCustom;
import com.example.organizze.R;
import com.example.organizze.config.ConfiguraçãoFirebase;
import com.example.organizze.model.Movimentação;
import com.example.organizze.model.Usuario;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

public class DespesasActivity extends AppCompatActivity {

    public EditText editValor;
    public TextInputEditText editData,editCategoria,editDescrição;
    public FloatingActionButton fabSalvar;
    public Movimentação movimentação;
    public DatabaseReference firebaseref = ConfiguraçãoFirebase.getFirebaseDatabase();
    public FirebaseAuth autenticação = ConfiguraçãoFirebase.getFirebaseAutenticação();
    private DatabaseReference usuarioref;
    private ValueEventListener valueEventListener;
    public Double despesaTotal;
    public Double despesaAtualizada;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

////////////////////////////////////////////////////////
        editData = findViewById(R.id.editData);
        editCategoria = findViewById(R.id.editCategoria);
        editDescrição = findViewById(R.id.editDescrição);
        editValor = findViewById(R.id.editValor);
////////////////////////////////////////////////////////

        //setando data autal no editData
        editData.setText(DataCustom.dataAtual());

    }

    public void salvardespesa (View view){

        String data = editData.getText().toString();
        Double valor = Double.parseDouble(editValor.getText().toString());

        if (validarcampos()) {
            ////////////////////////////////////////////////////////////////////////
            movimentação = new Movimentação();
            movimentação.setValor(valor);
            movimentação.setCateogira(editCategoria.getText().toString());
            movimentação.setDescrição(editDescrição.getText().toString());
            movimentação.setData(data);
            movimentação.setTipo("d");
            movimentação.salvar(data);
            ////////////////////////////////////////////////////////////////////////

            despesaAtualizada = despesaTotal + valor;

            atualizardDespesas(despesaAtualizada);

            Toast.makeText(DespesasActivity.this,"Despesa salva com sucesso !",Toast.LENGTH_LONG);
            finish();
        }
    }

    @Override
    protected void onStart() {
        recuperarDespesaTotal();
        super.onStart();
    }

    public void recuperarDespesaTotal () {

        String emairef = autenticação.getCurrentUser().getEmail();
        String idusuario = Base64Custom.codificarBase64(emairef);
        usuarioref = firebaseref.child("usuario").child(idusuario);

        valueEventListener = usuarioref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean validarcampos () {
        String textoValor = editValor.getText().toString();
        String textoData = editData.getText().toString();
        String textoCategoria = editCategoria.getText().toString();
        String textoDescrição = editDescrição.getText().toString();


        if (!textoValor.isEmpty() ){
            if (!textoData.isEmpty()){
                if (!textoCategoria.isEmpty()){
                    if(!textoDescrição.isEmpty()){
                        return true;
                    }
                    else {
                        Toast.makeText(DespesasActivity.this,"A descrição Não foi preenchida !",Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                else {
                    Toast.makeText(DespesasActivity.this,"A categoria Não foi preenchida !",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            else {
                Toast.makeText(DespesasActivity.this,"A data Não foi preenchida !",Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else{
            Toast.makeText(DespesasActivity.this,"O valor Não foi preenchido !",Toast.LENGTH_LONG).show();
            return false;
        }

    }

    public void atualizardDespesas (Double despesa) {
        String emairef = autenticação.getCurrentUser().getEmail();
        String idusuario = Base64Custom.codificarBase64(emairef);
        DatabaseReference usuarioref = firebaseref.child("usuario").child(idusuario);

        usuarioref.child("despesaTotal").setValue(despesa);
    }

    @Override
    protected void onStop() {
        usuarioref.removeEventListener(valueEventListener);
        super.onStop();
    }
}
