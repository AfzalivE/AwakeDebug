package com.afzaln.awakedebug

import android.app.Notification
import android.content.res.Resources
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import timber.log.Timber

/**
 * Responsible for observing notifications,
 * handling debug notifications when the debug awake
 * preference is enabled and setting the screen timeout
 * when conditions match.
 */
class NotificationListener : NotificationListenerService() {

    private val toggleController by lazy(Injector::toggleController)

    override fun onListenerConnected() = updateActiveDebuggingNotifications()
    override fun onNotificationPosted(sbn: StatusBarNotification) = updateActiveDebuggingNotifications()
    override fun onNotificationRemoved(sbn: StatusBarNotification) = updateActiveDebuggingNotifications()

    private fun updateActiveDebuggingNotifications() {
        try {
            val visibleDebugNotifications = activeNotifications.filter {
                it.isDebugNotification()
            }.map { it.notification.getDebugNotificationType() }

            Timber.d("Found $visibleDebugNotifications")

            toggleController.toggleFromNotification(visibleDebugNotifications)
        } catch (ex: SecurityException) {
            Timber.e(ex, "Unable to get active notifications")
        }
    }
}

private fun StatusBarNotification.isDebugNotification(
    ofTypes: List<DebuggingType> = DebuggingType.usbWifiTypes
): Boolean {
    return notification.channelId == "DEVELOPER_IMPORTANT" && notification.getDebugNotificationType() in ofTypes
}

private fun Notification.getDebugNotificationType(): DebuggingType {
    fun getSystemString(string: String): String {
        return Resources.getSystem().getString(Resources.getSystem().getIdentifier(string, "string", "android"))
    }

    val adbActiveNotificationTitle = getSystemString("adb_active_notification_title")
    val wifiAdbActiveNotificationTitle = getSystemString("adbwifi_active_notification_title")

    return when (extras["android.title"]) {
        adbActiveNotificationTitle -> DebuggingType.USB
        wifiAdbActiveNotificationTitle -> DebuggingType.WIFI
        else                           -> DebuggingType.NONE
    }
}
