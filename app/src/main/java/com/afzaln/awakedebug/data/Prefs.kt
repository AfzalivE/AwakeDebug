package com.afzaln.awakedebug.data

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.afzaln.awakedebug.MyApplication
import timber.log.Timber

private const val KEY_USB_DEBUG_ENABLED = "UsbDebugEnabled"
private const val KEY_WIFI_DEBUG_ENABLED = "WifiDebugEnabled"
private const val KEY_AWAKE_DEBUG_ENABLED = "AwakeDebugEnabled"
private const val KEY_DISPLAY_TIMEOUT = "DisplayTimeout"

enum class DebuggingType {
    USB, WIFI, NONE
}

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

    val enabledDebuggingTypes: List<DebuggingType>
        get() {
            return arrayListOf<DebuggingType>().apply {
                if (usbDebugging) add(DebuggingType.USB)
                if (wifiDebugging) add(DebuggingType.WIFI)
            }
        }

    var usbDebugging: Boolean
        get() {
            val usbDebugging = preferences.getBoolean(KEY_USB_DEBUG_ENABLED, true)
            Timber.d("$KEY_USB_DEBUG_ENABLED: %s", usbDebugging)
            return usbDebugging
        }
        set(value) {
            Timber.d("Setting $KEY_USB_DEBUG_ENABLED: %s", value)
            preferences.edit().putBoolean(KEY_USB_DEBUG_ENABLED, value).apply()
        }

    var wifiDebugging: Boolean
        get() {
            val usbDebugging = preferences.getBoolean(KEY_WIFI_DEBUG_ENABLED, true)
            Timber.d("$KEY_WIFI_DEBUG_ENABLED: %s", usbDebugging)
            return usbDebugging
        }
        set(value) {
            Timber.d("Setting $KEY_WIFI_DEBUG_ENABLED: %s", value)
            preferences.edit().putBoolean(KEY_WIFI_DEBUG_ENABLED, value).apply()
        }

    var awakeDebug: Boolean
        get() {
            val awakeDebugEnabled = preferences.getBoolean(KEY_AWAKE_DEBUG_ENABLED, false)
            Timber.d("$KEY_AWAKE_DEBUG_ENABLED: %s", awakeDebugEnabled)
            return awakeDebugEnabled
        }
        set(value) {
            Timber.d("Setting $KEY_AWAKE_DEBUG_ENABLED: %s", value)
            preferences.edit().putBoolean(KEY_AWAKE_DEBUG_ENABLED, value).apply()
        }

    var savedTimeout: Int
        get() {
            return preferences.getInt(KEY_DISPLAY_TIMEOUT, 60000)
        }
        set(value) {
            Timber.d("Setting $KEY_DISPLAY_TIMEOUT = %s", value)
            preferences.edit().putInt(KEY_DISPLAY_TIMEOUT, value).apply()
        }
}
