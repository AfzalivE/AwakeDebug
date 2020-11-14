package com.afzaln.awakedebug.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afzaln.awakedebug.Injector

/**
 * Transparent activity that is started
 * when the user taps one of the shortcuts
 */
class AppShortcutActivity : AppCompatActivity() {

    private val statusController by lazy(Injector::toggleController)
    private val shortcuts by lazy(Injector::shortcuts)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent != null && intent.action == Intent.ACTION_RUN) {
            if (intent.hasExtra(EXTRA_TOGGLE_DEBUG_AWAKE)) {
                statusController.toggle(intent.getBooleanExtra(EXTRA_TOGGLE_DEBUG_AWAKE, false))
            }
        }
        shortcuts.updateShortcuts()
        finish()
    }

    companion object {
        const val EXTRA_TOGGLE_DEBUG_AWAKE = "toggle_debug_awake"
    }
}
