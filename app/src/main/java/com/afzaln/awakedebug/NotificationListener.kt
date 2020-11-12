package com.afzaln.awakedebug

import android.content.res.Resources
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import timber.log.Timber

/**
 * Singleton to track is debug notification is active.
 * Didn't really see the benefit of using a non-singleton
 * for this.
 */
object DebugNotification {
    var isActive: Boolean = false
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
        DebugNotification.isActive = activeNotifications.any {
            isDebuggingNotification(it)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (isDebuggingNotification(sbn)) {
            DebugNotification.isActive = true
            if (prefUtils.awakeDebug) {
                Timber.d("Debugging enabled: $sbn")
                systemSettings.extendScreenTimeout()
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (isDebuggingNotification(sbn)) {
            DebugNotification.isActive = false
            Timber.d("Debugging disabled: $sbn")
            systemSettings.restoreScreenTimeout()
        }
    }

    private fun isDebuggingNotification(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification
        val title = notification.extras["android.title"]

        val adbActiveNotificationTitle = getSystemString("adb_active_notification_title")
        val wifiAdbActiveNotificationTitle = getSystemString("adbwifi_active_notification_title")

        return notification.channelId == "DEVELOPER_IMPORTANT" && title in listOf(
            adbActiveNotificationTitle, wifiAdbActiveNotificationTitle
        )
    }

    private fun getSystemString(string: String): String {
        return Resources.getSystem().getString(Resources.getSystem().getIdentifier(string, "string", "android"))
    }
}
