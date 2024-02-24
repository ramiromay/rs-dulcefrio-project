package com.realssoft.dulcefrio.api.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils
{

    private static final SimpleDateFormat DATE_OUTPUT_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATE_MYSQL_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date convertedDateMySqlFormat(String date)
    {
        try
        {
            Date outputDate = DATE_OUTPUT_FORMAT.parse(date);
            String mySqlDate = DATE_MYSQL_FORMAT.format(outputDate);
            return new Date(DATE_MYSQL_FORMAT.parse(mySqlDate).getTime());
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public static String formatDate(Date date) {
        return DATE_OUTPUT_FORMAT.format(date);
    }

    public static Timestamp parseTimestamp(String timestamp) {
        try {
            return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isDateValid(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return date.matches("\\d{2}/\\d{2}/\\d{4}");
        } catch (ParseException e) {
            e.printStackTrace();
            return false;

        }
    }

    public static boolean isDateBeforeCurrent(String date, String currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {
            // Parsear las fechas
            Date date1 = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(currentDate));
            // Establecer la componente de tiempo a medianoche
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date today = calendar.getTime();

            // Comparar solo año, mes y día
            return today.after(date1);
        } catch (ParseException e) {
            // Manejar la excepción si hay un problema al parsear
            e.printStackTrace();
            return false; // Puedes ajustar este valor según tus necesidades
        }
    }

    public static boolean isDateBeforeCurrent(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {
            // Parsear las fechas
            Date date1 = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            // Establecer la componente de tiempo a medianoche
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date today = calendar.getTime();

            System.out.println(date1);
            System.out.println(today);
            // Comparar solo año, mes y día
            return today.after(date1);
        } catch (ParseException e) {
            // Manejar la excepción si hay un problema al parsear
            e.printStackTrace();
            return false; // Puedes ajustar este valor según tus necesidades
        }
    }

    public static void displayDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(date);

        System.out.println(formattedDate);
    }


    public static String getTime(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(time);
    }

    public static boolean isAfter8AM(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Obtener la hora del día
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        // Verificar si la hora es mayor a las 8 de la mañana
        return hourOfDay >= 8;
    }


}
