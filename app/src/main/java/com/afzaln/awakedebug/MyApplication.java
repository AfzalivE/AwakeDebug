package com.afzaln.awakedebug;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Created by afzal on 15-06-03.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        } else {
            Timber.plant(new DebugTree());
        }
    }
}
