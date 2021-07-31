package com.example.organizze.Helper;

import java.text.SimpleDateFormat;

public class DataCustom {

    public static  String dataAtual (){

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(data);
        return dataString;
    }

    public static String mesAnoDataEscolhida (String data){

        // com o slit a data vai se quebrada nas barras Ex: 20/07/2021  --- [0]20 , [1] --- 07 , [2] --- 2021
        String retornodata [] = data.split("/");
        String dia = retornodata[0];//dia
        String mes = retornodata[1];//mes
        String ano = retornodata[2];//ano

        String mesano = mes + ano;

        return mesano;
    }
}
