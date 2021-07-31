package com.example.organizze.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class ReceitasActivity extends AppCompatActivity {


    //////////////////////////////////////////////////////////////////////////////////
    /*Firebase variaveis de instância*/
    public DatabaseReference firebase = ConfiguraçãoFirebase.getFirebaseDatabase();
    public FirebaseAuth autenticação = ConfiguraçãoFirebase.getFirebaseAutenticação();
    private DatabaseReference usuarioref ;
    private ValueEventListener valueEventListener;
    //////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////
    /*Variaveis de manipulação matematica*/
    public Double ReceitaTotal;
    public Double ReceitaAtualizada;
    /////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////
    /*Variaveis de intancia de componentes XML*/
    public EditText editValor;
    public TextInputEditText editData,editCategoria,editDescrição;
    public FloatingActionButton fabSalvar;
    public Movimentação movimentação;
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        /////////////////////////////////////////////////
        editValor = findViewById(R.id.editValor);
        editData = findViewById(R.id.editData);
        editCategoria = findViewById(R.id.editCategoria);
        editDescrição = findViewById(R.id.editDescrição);
        /////////////////////////////////////////////////

        //setando data atual automaticamanete
        editData.setText(DataCustom.dataAtual());

    }

    @Override
    protected void onStart() {
        //recupera a receitatotal atual para futuras atualizações
        recuperarReceitaTotal();
        super.onStart();
    }

    public void salvaReiceita (View view) {

        Double valor = Double.parseDouble(editValor.getText().toString());
        String data1 = editData.getText().toString();

        //verificação de preenchimento dos campos
        if (verificaçãodecampos()){
        //Setando valores para a classe movimentação salvar
        movimentação = new Movimentação();
        movimentação.setValor(valor);
        movimentação.setData(data1);
        movimentação.setCateogira(editCategoria.getText().toString());
        movimentação.setDescrição(editDescrição.getText().toString());
        movimentação.setTipo("r");
        movimentação.salvar(data1);

        //Atribuindo valor atual a receitaatualizada, pós adicionar nova receita
        ReceitaAtualizada = ReceitaTotal + valor;

        //atualizando a receita total do usuario
        atualizarReceitatotal(ReceitaAtualizada);

        Toast.makeText(ReceitasActivity.this,"Receita salva com sucesso !",Toast.LENGTH_LONG).show();
        finish();

        }

    }

    public boolean verificaçãodecampos () {
        String campoValor = editValor.getText().toString();
        String campoData = editData.getText().toString();
        String campoCategoria = editCategoria.getText().toString();
        String campoDescrição = editDescrição.getText().toString();

        if (!campoValor.isEmpty()){
            if (!campoData.isEmpty()){
                if (!campoCategoria.isEmpty()){
                    if (!campoDescrição.isEmpty()){
                        return true;
                    }
                    else {
                        Toast.makeText(ReceitasActivity.this,"A descrição não foi preenchida !",Toast.LENGTH_LONG).show();
                        return  false;
                    }
                }
                else {
                    Toast.makeText(ReceitasActivity.this,"A Categoria não foi preenchida !",Toast.LENGTH_LONG).show();
                    return  false;
                }
            }
            else {
                Toast.makeText(ReceitasActivity.this,"A data não foi preenchida !",Toast.LENGTH_LONG).show();
                return  false;
            }
        }
        else{
            Toast.makeText(ReceitasActivity.this,"O valor não foi preenchido !",Toast.LENGTH_LONG).show();
            return  false;
        }
    }

    public void recuperarReceitaTotal (){

        //recupera o id do usuario em base64
        String emailref = autenticação.getCurrentUser().getEmail();
        String idusuario = Base64Custom.codificarBase64(emailref);
        usuarioref =firebase.child("usuario").child(idusuario);


        valueEventListener = usuarioref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                ReceitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void atualizarReceitatotal (Double receita) {
        String emailref = autenticação.getCurrentUser().getEmail();
        String idusuario = Base64Custom.codificarBase64(emailref);
        DatabaseReference usuarioref =firebase.child("usuario").child(idusuario);

        usuarioref.child("receitaTotal").setValue(receita);

    }

    @Override
    protected void onStop() {
        usuarioref.removeEventListener(valueEventListener);
        super.onStop();
    }
}
