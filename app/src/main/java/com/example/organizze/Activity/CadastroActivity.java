package com.example.organizze.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.Helper.Base64Custom;
import com.example.organizze.R;
import com.example.organizze.config.ConfiguraçãoFirebase;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.io.UnsupportedEncodingException;

public class CadastroActivity extends AppCompatActivity {
    private EditText editNome,editEmail,editSenha;
    private Button buttonCadastrar;
    private FirebaseAuth autenticação;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        ////////////////////////////////////////////////////
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        getSupportActionBar().setTitle("Cadastro");
        ////////////////////////////////////////////////////

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoNome = editNome.getText().toString();
                String textoEmail = editEmail.getText().toString();
                String textoSenha = editSenha.getText().toString();

                //validar o preenchimento dos campos
                if (!textoNome.isEmpty() ){
                    if(!textoEmail.isEmpty()){
                        if (!textoSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);

                            cadastrarUsuario();
                        }
                        else{
                            Toast.makeText(CadastroActivity.this,"Preencha a senha !", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(CadastroActivity.this,"Preencha o email !", Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    Toast.makeText(CadastroActivity.this,"Preencha o nome !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cadastrarUsuario(){
        //cadastrar usuario no firebase
        autenticação = ConfiguraçãoFirebase.getFirebaseAutenticação();
        autenticação.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"Sucesso ao Cadastrar Usuário",Toast.LENGTH_LONG).show();
                    //codifica o email do usuario
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    //passa o idUsuario codificado para a classe usuario
                    usuario.setIdUsuario(idUsuario);
                    //salvar dados no banco de dados;
                    usuario.salvar();
                    finish();

                }
                else {
                    String exeção ="";
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        exeção = "Digite uma senha mais forte";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e ){
                        exeção = "Digite um email valido";
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        exeção = "Esta conta de email ja foi cadastrado";
                    }
                    catch (Exception e){
                        exeção = "Erro ao cadastrar usuário : " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this,exeção,Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
