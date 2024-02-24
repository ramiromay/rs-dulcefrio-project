package com.realssoft.dulcefrio.api.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.JLabel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyUtils
{

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("$#,###.##", DecimalFormatSymbols.getInstance(Locale.US));

    public static String putFormat(double value)
    {
        return MONEY_FORMAT.format(value);
    }

    public static Number removeFormat(String value)
    {
        try {
            return MONEY_FORMAT.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getCents(double amount) {
        try {
            // Crear un formato para obtener los centavos
            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            // Obtener la representación de la cadena con dos decimales
            String formattedAmount = decimalFormat.format(amount);

            // Parsear la cadena para obtener el valor numérico
            Number number = decimalFormat.parse(formattedAmount);

            // Obtener los centavos
            return (int) (number.doubleValue() * 100) % 100;
        } catch (ParseException e) {
            // Manejar la excepción si hay un problema al parsear
            e.printStackTrace();
            return 0; // Devolver 0 si hay un problema
        }
    }

    public static String descomponerNum(double num){
        DecimalFormat f = new DecimalFormat("##");
        int limite = 0;
        String numLetra = "";
        boolean si = false;
        for (int i = 0; i < String.valueOf(num).length(); i++) {
            char n = String.valueOf(num).charAt(i);
            if( n == '.'){
                i = String.valueOf(num).length();
            }
            else{
                limite++;
            }
        }
        for (int i = 0; i < limite; i++) {
            char n = String.valueOf(num).charAt(i);
            char n1 = String.valueOf(num).charAt(i+1);
            char n2 = String.valueOf(num).charAt(i+2);
            if(limite-i == 6 || limite-i == 3){
                si = false;
                switch (n) {
                    case '1':{
                        if(n1 == '0' && n2 =='0'){
                            numLetra = numLetra + "CIEN";
                        }
                        else{
                            numLetra = numLetra + "CIENTO";
                        }
                        break;
                    }
                    case '2':{
                        numLetra = numLetra + "DOSCIENTOS";
                        break;
                    }
                    case '3':{
                        numLetra = numLetra + "TRESCIENTOS";
                        break;
                    }
                    case '4':{
                        numLetra = numLetra + "CUATROCIENTOS";
                        break;
                    }
                    case '5':{
                        numLetra = numLetra + "QUINIENTOS";
                        break;
                    }
                    case '6':{
                        numLetra = numLetra + "SEISCIENTOS";
                        break;
                    }
                    case '7':{
                        numLetra = numLetra + "SETECIENTOS";
                        break;
                    }
                    case '8':{
                        numLetra = numLetra + "OCHOCIENTOS";
                        break;
                    }
                    case '9':{
                        numLetra = numLetra + "NOVECIENTOS";
                        break;
                    }
                }
                if((n != '0' && n1 != '0' && n2 != '0') || (n != '0' && n1 != '0') || (n != '0' && n2 != '0')){
                    numLetra = numLetra + " ";
                }
            }
            else if(limite-i == 5 || limite-i == 2){
                switch (n) {
                    case '1':{
                        si = true;
                        switch (n1) {
                            case '1':{
                                numLetra = numLetra + "ONCE";
                                if(limite <= 3){
                                    i = limite;
                                    numLetra = centavos(num, f, numLetra);
                                }
                                break;
                            }
                            case '2':{
                                numLetra = numLetra + "DOCE";
                                if(limite <= 3){
                                    i = limite;
                                    numLetra = centavos(num, f, numLetra);
                                }
                                break;
                            }
                            case '3':{
                                numLetra = numLetra + "TRECE";
                                if(limite <= 3){
                                    i = limite;
                                    numLetra = centavos(num, f, numLetra);
                                }
                                break;
                            }
                            case '4':{
                                numLetra = numLetra + "CATORCE";
                                if(limite <= 3){
                                    i = limite;
                                    numLetra = centavos(num, f, numLetra);
                                }
                                break;
                            }
                            case '5':{;
                                numLetra = numLetra + "QUINCE";
                                if(limite <= 3){
                                    i = limite;
                                    numLetra = centavos(num, f, numLetra);
                                }
                                break;
                            }
                            default:
                                si = false;
                                numLetra = numLetra + "DIEZ";
                                break;
                        }
                        break;
                    }
                    case '2':{
                        if(n1 == '0'){
                            numLetra = numLetra + "VEINTE";
                        }
                        else{
                            numLetra = numLetra + "VEINTI";
                        }
                        break;
                    }
                    case '3':{
                        numLetra = numLetra + "TREINTA";
                        break;
                    }
                    case '4':{
                        numLetra = numLetra + "CUARENTA";
                        break;
                    }
                    case '5':{
                        numLetra = numLetra + "CINCUENTA";
                        break;
                    }
                    case '6':{
                        numLetra = numLetra + "SESENTA";
                        break;
                    }
                    case '7':{
                        numLetra = numLetra + "SETENTA";
                        break;
                    }
                    case '8':{
                        numLetra = numLetra + "OCHENTA";
                        break;
                    }
                    case '9':{
                        numLetra = numLetra + "NOVENTA";
                        break;
                    }
                }
                if(!si){
                    if(n != '0'&&n != '2' && n1 != '0'){
                        numLetra = numLetra + " Y ";
                    }
                }
            }
            else if(limite-i == 4 || limite-i == 1){
                if(!si){
                    switch (n) {
                        case '1':{
                            numLetra = numLetra + "UN";
                            break;
                        }
                        case '2':{
                            numLetra = numLetra + "DOS";
                            break;
                        }
                        case '3':{
                            numLetra = numLetra + "TRES";
                            break;
                        }
                        case '4':{
                            numLetra = numLetra + "CUATRO";
                            break;
                        }
                        case '5':{
                            numLetra = numLetra + "CINCO";
                            break;
                        }
                        case '6':{
                            numLetra = numLetra + "SEIS";
                            break;
                        }
                        case '7':{
                            numLetra = numLetra + "SIETE";
                            break;
                        }
                        case '8':{
                            numLetra = numLetra + "OCHO";
                            break;
                        }
                        case '9':{
                            numLetra = numLetra + "NUEVE";
                            break;
                        }
                    }
                }
                if(limite-i == 4){
                    char n3 = String.valueOf(num).charAt(i+3);
                    if(n1 == '0' && n2 == '0' && n3 =='0'){
                        numLetra = numLetra + " MIL";
                    }
                    else{
                        numLetra = numLetra + " MIL ";
                    }

                }
                else{
                    numLetra = centavos(num, f, numLetra);
                }
            }
        }
        return numLetra;
    }

    static private String centavos(double num, DecimalFormat f, String numLetra){
        boolean punto = false;
        String decimales = "";
        for (int i = 0; i < String.valueOf(num).length(); i++) {
            char n = String.valueOf(num).charAt(i);
            if(punto){
                decimales = decimales + String.valueOf(n);
            }
            if(n == '.'){
                punto = true;
            }
        }
        numLetra = numLetra + " PESOS " + f.format(Double.parseDouble(decimales))+ "/100 M.N.";
        return numLetra;
    }


}
