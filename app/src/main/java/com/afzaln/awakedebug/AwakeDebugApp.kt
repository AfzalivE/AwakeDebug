package com.afzaln.awakedebug

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Created by afzal on 15-06-03.
 */
class AwakeDebugApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Injector.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}
