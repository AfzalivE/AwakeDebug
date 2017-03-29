package com.afzaln.awakedebug;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * A complimentary utility class.
 * Created by Dmytro Karataiev on 3/29/17.
 */
public class Utils {

    static void setAcPowerOn(Context context, boolean isChecked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences
                .edit()
                .putBoolean(context.getString(R.string.sharedpref_ac_key), isChecked)
                .apply();
    }

    public static boolean getAcPowerOn(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.sharedpref_ac_key), false);
    }
}
