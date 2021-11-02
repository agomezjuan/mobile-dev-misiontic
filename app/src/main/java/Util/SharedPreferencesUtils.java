package Util;


import static Util.constante.PREFS_NAME;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by miguelangelbuenoperez on 31/10/21...
 */

public class SharedPreferencesUtils {

    private static final String PREFS_FIRST_RUN = "PREFS_FIRST_RUN";
    private static String language;

    private SharedPreferencesUtils() {
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Boolean isTheFirstRun(Context context) {
        return getPreferences(context).getBoolean(PREFS_FIRST_RUN, true);
    }

    public static void disableFirstRun(Context context) {
        getPreferences(context).edit().putBoolean(PREFS_FIRST_RUN, false).apply();
    }

    public static void enabledFirstRun(Context context) {
        getPreferences(context).edit().putBoolean(PREFS_FIRST_RUN, true).apply();
    }

    public static void setvariable(Context context, String namevar, String valuevar) {
        getPreferences(context).edit().putString(namevar, valuevar).apply();
    }

    public static String getvariable(Context context, String namevar) {
        return getPreferences(context).getString(namevar, "");
    }

    public static void setvariableInt(Context context, String namevar, int valuevar) {
        getPreferences(context).edit().putInt(namevar, valuevar).apply();
    }

    public static int getvariableInt(Context context, String namevar) {
        return getPreferences(context).getInt(namevar, 1);
    }

    public static void setvariableBool(Context context, String namevar, Boolean valuevar) {
        getPreferences(context).edit().putBoolean(namevar, valuevar).apply();
    }

    public static boolean getvariableBool(Context context, String namevar) {
        return getPreferences(context).getBoolean(namevar, true);
    }
}
