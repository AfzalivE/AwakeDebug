package com.afzaln.awakedebug.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afzaln.awakedebug.*
import com.afzaln.awakedebug.data.Prefs
import com.afzaln.awakedebug.data.Shortcuts
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
    private val shortcuts: Shortcuts = Injector.shortcuts
) : ViewModel() {

    private val _settingsLiveData = MutableLiveData(SettingsUiState())
    val settingsLiveData: LiveData<SettingsUiState>
        get() = _settingsLiveData

    init {
        val settingsUiState = SettingsUiState(prefs.awakeDebug, prefs.usbDebugging, prefs.wifiDebugging)
        _settingsLiveData.value = settingsUiState
    }

    fun toggleDebugAwake() {
        setDebugAwake(!prefs.awakeDebug)
    }

    fun setDebugAwake(shouldEnable: Boolean) {
        if (!shouldEnable) {
            systemSettings.restoreScreenTimeout()
        } else {
            if ((DebugNotification.activeDebugNotifications intersect prefs.enabledDebuggingTypes).isNotEmpty()) {
                systemSettings.extendScreenTimeout()
            } else {
                systemSettings.restoreScreenTimeout()
            }
        }
        prefs.awakeDebug = shouldEnable
        shortcuts.updateShortcuts()
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
        val wifiDebugging: Boolean = true
    )
}
