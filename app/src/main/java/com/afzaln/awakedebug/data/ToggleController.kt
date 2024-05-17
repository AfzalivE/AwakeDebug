package com.afzaln.awakedebug

import com.afzaln.awakedebug.data.Prefs
import com.afzaln.awakedebug.data.SystemSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

/**
 * Responsible for holding visible debug notifications
 * and toggling the screen timeout based on
 * active notifications and user settings.
 */
class ToggleController(
    private val prefs: Prefs = Injector.prefs,
    private val systemSettings: SystemSettings = Injector.systemSettings,
) {
    private val _visibleDebugNotificationsFlow: MutableStateFlow<List<DebuggingType>> =
        MutableStateFlow(listOf())
    val visibleDebugNotificationsFlow: StateFlow<List<DebuggingType>>
        get() = _visibleDebugNotificationsFlow

    fun toggleFromNotification(visibleDebugNotifications: List<DebuggingType>) {
        _visibleDebugNotificationsFlow.value = visibleDebugNotifications
        toggle(prefs.awakeDebug)
    }

    /**
     * Extends or Restores system screen timeout based
     * on the notifications, the awake debug toggle,
     * and the current state of awake debug.
     */
    fun toggle(shouldEnable: Boolean) {
        prefs.awakeDebug = shouldEnable
        val enabledDebugNotificationsExist = visibleDebugNotificationsFlow.value.any {
            it in prefs.enabledDebuggingTypes
        }

        if (shouldEnable && enabledDebugNotificationsExist) {
            if (!prefs.awakeDebugActive) {
                Timber.d("Debugging enabled")
                systemSettings.extendScreenTimeout()
                prefs.awakeDebugActive = true
            }
        } else {
            if (prefs.awakeDebugActive) {
                Timber.d("Debugging disabled")
                systemSettings.restoreScreenTimeout()
                prefs.awakeDebugActive = false
            }
        }
    }
}

enum class DebuggingType {
    USB, WIFI, NONE;

    companion object {
        val usbWifiTypes: List<DebuggingType> = listOf(USB, WIFI)
    }
}
