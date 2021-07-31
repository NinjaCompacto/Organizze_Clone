package com.example.organizze.model;

import com.example.organizze.Helper.Base64Custom;
import com.example.organizze.Helper.DataCustom;
import com.example.organizze.config.ConfiguraçãoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentação {

    private String data,cateogira,descrição,tipo,key;
    private Double valor;
    private Usuario usuario;


    public Movimentação() {
    }

    public void salvar (String dataEscolhida){

        DatabaseReference firebase = ConfiguraçãoFirebase.getFirebaseDatabase();
        FirebaseAuth autenticação = ConfiguraçãoFirebase.getFirebaseAutenticação();

        String idusuario = Base64Custom.codificarBase64(autenticação.getCurrentUser().getEmail());
        String mesano = DataCustom.mesAnoDataEscolhida(dataEscolhida);

        firebase.child("movimentação")
                .child(idusuario)
                .child(mesano)
                .push()
                .setValue(this);
    }

/////////////////////////////////////////////////////////////////////////////

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCateogira() {
        return cateogira;
    }

    public void setCateogira(String cateogira) {
        this.cateogira = cateogira;
    }

    public String getDescrição() {
        return descrição;
    }

    public void setDescrição(String descrição) {
        this.descrição = descrição;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    /////////////////////////////////////////////////////////////////////////////

}
