package id.net.gmedia.selby.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPreferences {
    private static final String LOGIN_PREF = "login_status";
    private static final String STATUS_PREF = "user_status";

    private static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLoggedIn(Context context){
        return getPreferences(context).getBoolean(LOGIN_PREF, false);
    }

    public static boolean isPenjual(Context context){
        return getPreferences(context).getBoolean(STATUS_PREF, false);
    }

    public static void setLoggedIn(Context context, boolean login){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGIN_PREF, login);
        editor.apply();
    }

    public static void setStatusPref(Context context, boolean penjual){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(STATUS_PREF, penjual);
        editor.apply();
    }
}
