package com.afzaln.awakedebug

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.afzaln.awakedebug.data.Prefs
import com.afzaln.awakedebug.data.SystemSettings
import timber.log.Timber

/**
 * Responsible for holding visible debug notifications
 * and toggling the screen timeout based on
 * active notifications and user settings.
 */
class ToggleController(
    private val prefs: Prefs = Injector.prefs,
    private val systemSettings: SystemSettings = Injector.systemSettings
) {
    private val _visibleDebugNotifications: MutableLiveData<List<DebuggingType>> = MutableLiveData(listOf())
    val visibleDebugNotifications: LiveData<List<DebuggingType>>
        get() = _visibleDebugNotifications

    fun toggleFromNotification(visibleDebugNotifications: List<DebuggingType>) {
        _visibleDebugNotifications.value = visibleDebugNotifications
        toggle(prefs.awakeDebug)
    }

    fun toggle(shouldEnable: Boolean) {
        prefs.awakeDebug = shouldEnable
        val enabledDebugNotificationsExist = visibleDebugNotifications.value?.any {
            it in prefs.enabledDebuggingTypes
        } ?: false

        if (shouldEnable && enabledDebugNotificationsExist) {
            Timber.d("Debugging enabled")
            systemSettings.extendScreenTimeout()
        } else {
            Timber.d("Debugging disabled")
            systemSettings.restoreScreenTimeout()
        }
    }
}

enum class DebuggingType {
    USB, WIFI, NONE;

    companion object {
        val usbWifiTypes: List<DebuggingType> = listOf(USB, WIFI)
    }
}
