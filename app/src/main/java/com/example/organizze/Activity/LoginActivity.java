package com.example.organizze.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguraçãoFirebase;
import com.example.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail,editSenha;
    private Button buttonEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticação;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //////////////////////////////////////////////////
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        buttonEntrar = findViewById(R.id.buttonEntrar);
        getSupportActionBar().setTitle("Login");
        //////////////////////////////////////////////////

        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoEmail =editEmail.getText().toString();
                String textoSenha =editSenha.getText().toString();

                if (!textoEmail.isEmpty() ){
                    if(!textoSenha.isEmpty()){


                            usuario = new Usuario();
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);

                            validarLogin();

                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Preencha a senha !", Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    Toast.makeText(LoginActivity.this,"Preencha o email !", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public void validarLogin (){
        autenticação = ConfiguraçãoFirebase.getFirebaseAutenticação();
        autenticação.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                abritelaprincipal();
                }
                else {
                    String exeção ="";

                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e ){
                        exeção = "Email e senha não correspondem a um usuário cadastrado.";
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        exeção = "Usúario não esta cadastrado.";
                    }
                    catch (Exception e){
                        exeção = "Erro ao logar usuário : " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,exeção,Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void abritelaprincipal (){
        startActivity(new Intent(this, PrincipalActivity.class));
    }

}
