package com.afzaln.awakedebug;

import android.content.Context;
import android.preference.PreferenceManager;

import timber.log.Timber;

/**
 * A complimentary utility class.
 * Created by Dmytro Karataiev on 3/29/17.
 */
public class PrefUtils {

    static void setAwakeAc(Context context, boolean isEnabled) {
        Timber.d("setAwakeAcEnabled: " + isEnabled);
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putBoolean("AwakeAcEnabled", isEnabled).commit();
    }

    public static boolean isAwakeAcEnabled(Context context) {
        boolean awakeAcEnabled = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getBoolean("AwakeAcEnabled", false);
        Timber.d("isAwakeAcEnabled: " + awakeAcEnabled);
        return awakeAcEnabled;
    }

    public static void setAwakeDebugEnabled(Context context, boolean isEnabled) {
        Timber.d("setAwakeDebugEnabled: " + isEnabled);
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putBoolean("AwakeDebugEnabled", isEnabled).commit();
    }

    public static boolean isAwakeDebugEnabled(Context context) {
        boolean awakeDebugEnabled = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getBoolean("AwakeDebugEnabled", false);
        Timber.d("isAwakeDebugEnabled: " + awakeDebugEnabled);
        return awakeDebugEnabled;
    }

    static int getSavedTimeout(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt("AwakeDebug", 60000);
    }

    static void setSavedTimeout(Context context, int timeout) {
        Timber.d("savedTimeout = " + timeout);
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putInt("AwakeDebug", timeout).commit();
    }

}
