package com.afzaln.awakedebug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import timber.log.Timber;

/**
 * Receives the power connected/disconnected intents from the system
 */
public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEnabled = getPrefEnabled(context.getApplicationContext());
        Timber.d("getPrefEnabled: " + isEnabled);

        if (isEnabled) {
            toggleStayAwake(context);
        }
    }

    public static void toggleStayAwake(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.getApplicationContext().registerReceiver(null, filter);

        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB ||
                (chargePlug == BatteryManager.BATTERY_PLUGGED_AC && Utils.getAcPowerOn(context));

        int adb;
        if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR1) {
            adb = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0);
        } else {
            adb = Settings.Global.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        }

        if (usbCharge && adb == 1) {
            enableStayAwake(context);
        } else {
            disableStayAwake(context);
        }
    }

    public static void disableStayAwake(Context context) {
        int timeout = getScreenOffTimeout(context);
        if (timeout == Integer.MAX_VALUE) {
            int savedTimeout = getSavedTimeout(context);
            changeScreenOffTimeout(context, savedTimeout);
        }
    }

    public static void enableStayAwake(Context context) {
        int timeout = getScreenOffTimeout(context);
        if (timeout != Integer.MAX_VALUE) {
            changeScreenOffTimeout(context, Integer.MAX_VALUE, timeout);
        }
    }

    private static void changeScreenOffTimeout(Context context, int newTimeout) {
        setScreenOffTimeout(context, newTimeout);
    }

    private static void changeScreenOffTimeout(Context context, int newTimeout, int oldTimeout) {
        setScreenOffTimeout(context, newTimeout);
        setPrefSavedTimeout(context, oldTimeout);
    }

    private static void setScreenOffTimeout(Context context, int timeout) {
        if (hasPermission(context)) {
            Settings.System.putInt(context.getContentResolver(), "screen_off_timeout", timeout);
        }
    }

    private static int getScreenOffTimeout(Context context) {
        try {
            int timeout = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            return timeout;
        } catch (SettingNotFoundException e) {
            Timber.e(e.getMessage(), e);
        }

        return 0;
    }

    private static int getSavedTimeout(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("AwakeDebug", 60000);
    }

    private static void setPrefSavedTimeout(Context context, int timeout) {
        Timber.d("savedTimeout = " + timeout);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("AwakeDebug", timeout).commit();
    }

    public static boolean getPrefEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("AwakeDebugEnabled", false);
    }

    public static void setPrefEnabled(Context context, boolean isEnabled) {
        Timber.d("setPrefEnabled: " + isEnabled);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("AwakeDebugEnabled", isEnabled).commit();
    }

    public static boolean hasPermission(Context context) {
        if (VERSION.SDK_INT >= VERSION_CODES.M && !Settings.System.canWrite(context)) {
            return false;
        }
        return true;
    }
}
