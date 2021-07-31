package com.example.organizze.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguraçãoFirebase {

    private static FirebaseAuth autenticação;
    private static DatabaseReference referenceDatabase;

    //metodo que retorna instancia do fire base database
    public static DatabaseReference getFirebaseDatabase(){

        if (referenceDatabase == null){
            referenceDatabase = FirebaseDatabase.getInstance().getReference();
        }
    //retorna a referencia do database em uma variavel statica
        return referenceDatabase;
    }

    //metodo que retorna instancia do fire base auth
    public static FirebaseAuth getFirebaseAutenticação(){
        if (autenticação == null){
            autenticação = FirebaseAuth.getInstance();
        }

        return autenticação;

    }
}
