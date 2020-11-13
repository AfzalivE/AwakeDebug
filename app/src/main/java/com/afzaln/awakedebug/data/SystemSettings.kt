package com.afzaln.awakedebug.data

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import com.afzaln.awakedebug.MyApplication
import com.afzaln.awakedebug.NotificationListener
import timber.log.Timber

const val MAX_TIMEOUT = 30 * 60 * 1000

/**
 * Responsible for checking system permissions
 * and modifying the display timeout system setting
 */
class SystemSettings(
    val app: MyApplication,
    private val prefs: Prefs
) {
    val hasNotificationPermission: Boolean
        get() {
            val notificationManager = app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationListenerComponent = ComponentName(app, NotificationListener::class.java)
            return notificationManager.isNotificationListenerAccessGranted(notificationListenerComponent)
        }

    val hasModifyPermission: Boolean
        get() {
            return Settings.System.canWrite(app)
        }

    val hasAllPermissions: Boolean
        get() = hasNotificationPermission && hasModifyPermission

    private val screenOffTimeout: Int
        get() {
            try {
                return Settings.System.getInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
            } catch (e: SettingNotFoundException) {
                Timber.e(e)
            }
            return 0
        }

    fun extendScreenTimeout() {
        if (hasModifyPermission && screenOffTimeout < MAX_TIMEOUT) {
            prefs.savedTimeout = screenOffTimeout
            Settings.System.putInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, MAX_TIMEOUT)
        }
    }

    fun restoreScreenTimeout() {
        if (hasModifyPermission && screenOffTimeout == MAX_TIMEOUT) {
            Settings.System.putInt(app.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, prefs.savedTimeout)
        }
    }
}
