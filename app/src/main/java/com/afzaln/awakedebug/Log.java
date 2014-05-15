package com.afzaln.awakedebug;

/**
 * Created by afzal on 2014-05-15.
 */
public class Log {
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable ex) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, message, ex);
        }
    }
}
