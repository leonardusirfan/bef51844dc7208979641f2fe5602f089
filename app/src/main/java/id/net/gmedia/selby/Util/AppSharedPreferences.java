package id.net.gmedia.selby.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class AppSharedPreferences {
    private static final String LOGIN_PREF = "login_status";
    private static final String STATUS_PREF = "user_status";
    private static final String FCM_PREF = "fcm_id";

    private static final String KERANJANG_PREF = "list_keranjang";

    private static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getFcmId(Context context){
        return getPreferences(context).getString(FCM_PREF, "");
    }

    public static void setFcmId(Context context, String fcm){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(FCM_PREF, fcm);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context){
        return getPreferences(context).getBoolean(LOGIN_PREF, false);
    }

    public static void setLoggedIn(Context context, boolean login){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, login);
        editor.apply();
    }

    public static void setUnupdatedKeranjang(Context context, Set<String> listKeranjang){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putStringSet(KERANJANG_PREF, listKeranjang);
        editor.apply();
    }

    public static Set<String> getUnupdatedKeranjang(Context context){
       return getPreferences(context).getStringSet(KERANJANG_PREF, new HashSet<String>());
    }

    public static void setStatusPref(Context context, boolean penjual){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(STATUS_PREF, penjual);
        editor.apply();
    }
}
