package com.afzaln.awakedebug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import timber.log.Timber;

/**
 * Receives the power connected/disconnected intents from the system
 */
public class PowerConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isAwakeDebugEnabled = PrefUtils.isAwakeDebugEnabled(context.getApplicationContext());
        boolean isAwakeAcEnabled = PrefUtils.isAwakeAcEnabled(context.getApplicationContext());

        if (isAwakeDebugEnabled || isAwakeAcEnabled) {
            toggleStayAwake(context);
        }
    }

    public static void toggleStayAwake(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.getApplicationContext().registerReceiver(null, filter);

        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB && PrefUtils.isAwakeDebugEnabled(context);
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC && PrefUtils.isAwakeAcEnabled(context);

        int adb;
        if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR1) {
            adb = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0);
        } else {
            adb = Settings.Global.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        }

        if ((usbCharge && adb == 1) || acCharge) {
            enableStayAwake(context);
        } else {
            disableStayAwake(context);
        }
    }

    public static void disableStayAwake(Context context) {
        int timeout = getScreenOffTimeout(context);
        if (timeout == Integer.MAX_VALUE) {
            int savedTimeout = PrefUtils.getSavedTimeout(context);
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
        PrefUtils.setSavedTimeout(context, oldTimeout);
    }

    private static void setScreenOffTimeout(Context context, int timeout) {
        if (hasPermission(context)) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
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

    public static boolean hasPermission(Context context) {
        if (VERSION.SDK_INT >= VERSION_CODES.M && !Settings.System.canWrite(context)) {
            return false;
        }
        return true;
    }

    static void toggleAwake(Context context, boolean isChecked) {
        if (isChecked) {
            PowerConnectionReceiver.toggleStayAwake(context);
        } else {
            PowerConnectionReceiver.disableStayAwake(context);
        }
    }
}
