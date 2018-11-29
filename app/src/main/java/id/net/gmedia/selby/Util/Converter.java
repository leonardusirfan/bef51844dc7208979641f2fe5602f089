package id.net.gmedia.selby.Util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Converter {
    private static SimpleDateFormat dtt_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat dt_format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static SimpleDateFormat d_format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String DTTToString(int year, int month, int date, int hour, int minute, int second){
        return String.format(Locale.getDefault(), "%4d-%02d-%02d %02d:%02d:%02d",
                year, month, date, hour, minute, second);
    }

    public static String DToString(int year, int month, int date){
        return String.format(Locale.getDefault(), "%4d-%02d-%02d",
                year, month, date);
    }

    public static String DateToString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String bulan = "";
        switch (calendar.get(Calendar.MONTH)){
            case 0:bulan = "januari";break;
            case 1:bulan = "februari";break;
            case 2:bulan = "maret";break;
            case 3:bulan = "april";break;
            case 4:bulan = "mei";break;
            case 5:bulan = "juni";break;
            case 6:bulan = "juli";break;
            case 7:bulan = "agustus";break;
            case 8:bulan = "september";break;
            case 9:bulan = "oktober";break;
            case 10:bulan = "november";break;
            case 11:bulan = "desember";break;

        }
        return String.format(Locale.getDefault(), "%d %s %d", calendar.get(Calendar.DATE), bulan, calendar.get(Calendar.YEAR));
    }

    public static Date stringDTTToDate(String date){
        try {
            return dtt_format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date stringDTToDate(String date){
        try {
            return dt_format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date stringDToDate(String date){
        try {
            return d_format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doubleToRupiah(double value){
        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        return "Rp " + rupiahFormat.format(Double.parseDouble(String.valueOf(value)));
    }

    public static int dpToPixel(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}

