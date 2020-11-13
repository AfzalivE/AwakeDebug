package com.afzaln.awakedebug.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

/**
 * Transparent activity that is started
 * when the user taps one of the shortcuts
 */
class AppShortcutActivity : AppCompatActivity() {

    private val viewModel by viewModels<ToggleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent != null && intent.action == Intent.ACTION_RUN) {
            if (intent.hasExtra(EXTRA_TOGGLE_DEBUG_AWAKE)) {
                toggleDebugAwake()
            }
        }
        finish()
    }

    private fun toggleDebugAwake() {
        viewModel.toggleDebugAwake()
    }

    companion object {
        const val EXTRA_TOGGLE_DEBUG_AWAKE = "toggle_debug_awake"
    }
}
