package com.example.organizze.Helper;

import android.util.Base64;

public class Base64Custom {

    //metodo para codificar
    public static String codificarBase64(String texto){
        //Retorna o texto codificado em base64 e troca caracteries como espaços: no começo (\\n) e ao final (\\r)
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT ).replaceAll("(\\n|\\r)","");
    }

    //metodo para decodificar
    public static String decodificarBase64 (String textocodificado) {
        //Retorna o texto decodificado em Base64
        return new String(Base64.decode(textocodificado,Base64.DEFAULT));
    }

}
