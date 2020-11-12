package com.afzaln.awakedebug.data

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.afzaln.awakedebug.MyApplication
import timber.log.Timber

/**
 * Responsible for keeping user choices for
 * Awake Debug setting and the last system
 * display timeout, for restoring.
 *
 * Created by Dmytro Karataiev on 3/29/17.
 */
class Prefs(val app: MyApplication) {
    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(app)

    var awakeDebug: Boolean
        get() {
            val awakeDebugEnabled = preferences.getBoolean("AwakeDebugEnabled", false)
            Timber.d("isAwakeDebugEnabled: %s", awakeDebugEnabled)
            return awakeDebugEnabled
        }
        set(value) {
            Timber.d("setAwakeDebugEnabled: %s", value)
            preferences.edit().putBoolean("AwakeDebugEnabled", value).apply()
        }

    var savedTimeout: Int
        get() {
            return preferences.getInt("AwakeDebug", 60000)
        }
        set(value) {
            Timber.d("savedTimeout = %s", value)
            preferences.edit().putInt("AwakeDebug", value).apply()
        }
}
