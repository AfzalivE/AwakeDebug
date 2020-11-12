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
        val settingsUiState = SettingsUiState(prefs.awakeDebug)
        _settingsLiveData.value = settingsUiState
    }

    fun toggleDebugAwake() {
        setDebugAwake(!prefs.awakeDebug)
    }

    fun setDebugAwake(shouldEnable: Boolean) {
        if (!shouldEnable) {
            systemSettings.restoreScreenTimeout()
        } else {
            if (DebugNotification.isActive) {
                systemSettings.extendScreenTimeout()
            }
        }
        prefs.awakeDebug = shouldEnable
        shortcuts.updateShortcuts()
    }

    data class SettingsUiState(
        val debugAwake: Boolean = false,
    )
}
