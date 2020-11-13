package com.afzaln.awakedebug

import android.app.Notification
import android.content.res.Resources
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.afzaln.awakedebug.data.DebuggingType
import timber.log.Timber

/**
 * Singleton to track which debug notifications are active.
 * Didn't really see the benefit of using a non-singleton
 * for this.
 */
object DebugNotification {
    var activeDebugNotifications: List<DebuggingType> = listOf()
}

/**
 * Responsible for observing notifications,
 * handling debug notifications when the debug awake
 * preference is enabled and setting the screen timeout
 * when conditions match.
 */
class NotificationListener : NotificationListenerService() {

    private val prefUtils by lazy(Injector::prefs)
    private val systemSettings by lazy(Injector::systemSettings)

    override fun onListenerConnected() {
        super.onListenerConnected()
        updateActiveDebuggingNotifications()
    }

    private fun updateActiveDebuggingNotifications() {
        try {
            DebugNotification.activeDebugNotifications = activeNotifications.filter {
                it.isDebugNotification()
            }.map {
                return@map it.notification.getDebugNotificationType()
            }
        } catch (ex: SecurityException) {
            Timber.e(ex)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        updateActiveDebuggingNotifications()
        if (sbn.isDebugNotification(prefUtils.enabledDebuggingTypes)) {
            if (prefUtils.awakeDebug) {
                Timber.d("Debugging enabled: $sbn")
                systemSettings.extendScreenTimeout()
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        updateActiveDebuggingNotifications()
        if (sbn.isDebugNotification(prefUtils.enabledDebuggingTypes)) {
            Timber.d("Debugging disabled: $sbn")
            systemSettings.restoreScreenTimeout()
        }
    }

    private fun StatusBarNotification.isDebugNotification(
        ofTypes: List<DebuggingType> = listOf(DebuggingType.USB, DebuggingType.WIFI)
    ): Boolean {
        return notification.channelId == "DEVELOPER_IMPORTANT" && notification.getDebugNotificationType() in ofTypes
    }

    private fun Notification.getDebugNotificationType(): DebuggingType {
        val adbActiveNotificationTitle = getSystemString("adb_active_notification_title")
        val wifiAdbActiveNotificationTitle = getSystemString("adbwifi_active_notification_title")

        return when (extras["android.title"]) {
            adbActiveNotificationTitle -> DebuggingType.USB
            wifiAdbActiveNotificationTitle -> DebuggingType.WIFI
            else                           -> DebuggingType.NONE
        }
    }

    private fun getSystemString(string: String): String {
        return Resources.getSystem().getString(Resources.getSystem().getIdentifier(string, "string", "android"))
    }
}
