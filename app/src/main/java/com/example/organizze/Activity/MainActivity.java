package com.example.organizze.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.organizze.Activity.CadastroActivity;
import com.example.organizze.Activity.LoginActivity;
import com.example.organizze.R;
import com.example.organizze.config.ConfiguraçãoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticação;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.um)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.dois)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.tres)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.quatro)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarusuariologado();
    }

    public void btentrar(View view) {
        startActivity(new Intent(this, LoginActivity.class));

    }
    public  void btcadastrar(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }
    public void verificarusuariologado (){
        autenticação = ConfiguraçãoFirebase.getFirebaseAutenticação();
        //autenticação.signOut();
        if (autenticação.getCurrentUser() != null){
            abrirtelaprincipal();
        }
    }
    public void abrirtelaprincipal (){
        startActivity(new Intent(this,PrincipalActivity.class));
    }
}
