package com.afzaln.awakedebug.ui

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afzaln.awakedebug.DebuggingType
import com.afzaln.awakedebug.Injector
import com.afzaln.awakedebug.ToggleController
import com.afzaln.awakedebug.data.Prefs
import com.afzaln.awakedebug.data.SystemSettings

/**
 * Responsible for handling logic related to
 * enabling and disabling the Awake for Debug
 * setting. Also, for publishing the UI state
 * to liveData observers.
 */
class ToggleViewModel(
    private val systemSettings: SystemSettings = Injector.systemSettings,
    private val prefs: Prefs = Injector.prefs,
    private val shortcuts: Shortcuts = Injector.shortcuts,
    private val toggleController: ToggleController = Injector.toggleController
) : ViewModel() {

    private val settingsLiveData = MutableLiveData(SettingsUiState())

    val uiStateLiveData = MediatorLiveData<SettingsUiState>().apply {
        addSource(settingsLiveData) { emit () }
        addSource(toggleController.visibleDebugNotifications) { emit() }
        addSource(systemSettings.screenTimeoutLiveData) { emit() }
    }

    private fun MediatorLiveData<SettingsUiState>.emit() {
        val settings = settingsLiveData.value ?: return
        val activeNotification = toggleController.visibleDebugNotifications.value ?: return
        val screenTimeout = systemSettings.screenTimeoutLiveData.value ?: return

        value = settings.copy(
            screenTimeout = screenTimeout,
            debuggingStatus = activeNotification
        )
    }

    init {
        val settingsUiState = SettingsUiState(prefs.awakeDebug, prefs.usbDebugging, prefs.wifiDebugging)
        settingsLiveData.value = settingsUiState
    }

    fun setDebugAwake(shouldEnable: Boolean) {
        toggleController.toggle(shouldEnable)
        shortcuts.updateShortcuts()

        settingsLiveData.value = SettingsUiState(
            debugAwake = prefs.awakeDebug,
            usbDebugging = prefs.usbDebugging,
            wifiDebugging = prefs.wifiDebugging,
            screenTimeout = systemSettings.screenTimeoutLiveData.value ?: 0
        )
    }

    fun toggleUsbDebugging(shouldEnable: Boolean) {
        prefs.usbDebugging = shouldEnable
        setDebugAwake(prefs.awakeDebug)
    }

    fun toggleWifiDebugging(shouldEnable: Boolean) {
        prefs.wifiDebugging = shouldEnable
        setDebugAwake(prefs.awakeDebug)
    }

    data class SettingsUiState(
        val debugAwake: Boolean = false,
        val usbDebugging: Boolean = true,
        val wifiDebugging: Boolean = true,
        val screenTimeout: Int = 0,
        val debuggingStatus: List<DebuggingType> = listOf(DebuggingType.NONE)
    )
}
