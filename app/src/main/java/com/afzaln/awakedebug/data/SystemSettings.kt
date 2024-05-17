package com.afzaln.awakedebug.data

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import com.afzaln.awakedebug.AwakeDebugApp
import com.afzaln.awakedebug.NotificationListener
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

const val MAX_TIMEOUT = 30 * 60 * 1000

/**
 * Responsible for checking system permissions
 * and modifying the display timeout system setting
 */
class SystemSettings(
    val app: AwakeDebugApp,
    private val prefs: Prefs,
) {
    val hasNotificationPermission: Boolean
        get() {
            val notificationManager =
                app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationListenerComponent = ComponentName(app, NotificationListener::class.java)
            return notificationManager.isNotificationListenerAccessGranted(
                notificationListenerComponent
            )
        }

    val hasModifyPermission: Boolean
        get() {
            return Settings.System.canWrite(app)
        }

    val hasAllPermissions: Boolean
        get() = hasNotificationPermission && hasModifyPermission

    val screenTimeoutFlow = MutableStateFlow(screenOffTimeout)

    private val screenOffTimeout: Int
        get() {
            try {
                return Settings.System.getInt(
                    app.contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT
                )
            } catch (e: SettingNotFoundException) {
                Timber.e(e)
            }
            return 0
        }

    fun refreshScreenTimeout() {
        if (hasModifyPermission) {
            screenTimeoutFlow.value = screenOffTimeout
            Timber.d("Refreshed screen timeout to ${screenTimeoutFlow.value}")
        }
    }

    internal fun extendScreenTimeout() {
        if (hasModifyPermission) {
            prefs.savedTimeout = screenOffTimeout
            Settings.System.putInt(
                app.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                MAX_TIMEOUT,
            )
            Timber.d("Screen timeout is now $MAX_TIMEOUT")
            screenTimeoutFlow.value = MAX_TIMEOUT
        }
    }

    internal fun restoreScreenTimeout() {
        if (hasModifyPermission) {
            Settings.System.putInt(
                app.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                prefs.savedTimeout,
            )
            Timber.d("Screen timeout is now ${prefs.savedTimeout}")
            screenTimeoutFlow.value = prefs.savedTimeout
        }
    }
}
