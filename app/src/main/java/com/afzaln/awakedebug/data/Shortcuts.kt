package com.afzaln.awakedebug.data

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.afzaln.awakedebug.Injector.prefs
import com.afzaln.awakedebug.MyApplication
import com.afzaln.awakedebug.R
import com.afzaln.awakedebug.ui.AppShortcutActivity

/**
 * Responsible for updating launcher shortcuts.
 *
 * Original version contributed by @dmytroKarataiev
 */
class Shortcuts(val app: MyApplication) {

    fun updateShortcuts() {
        // create and set dynamic shortcuts
        val debugShortcut = createShortcut(
            context = app,
            enabled = prefs.awakeDebug,
            shortLabel = app.getString(R.string.awake_debug_toggle_short),
            longLabel = app.getString(R.string.awake_debug_toggle_long)
        )

        val shortcutManager = app.getSystemService(ShortcutManager::class.java)
        val existingShortcuts = shortcutManager.dynamicShortcuts
        val shortcuts = listOf(debugShortcut)

        if (existingShortcuts.isEmpty()) {
            shortcutManager.dynamicShortcuts = shortcuts
        } else {
            shortcutManager.updateShortcuts(shortcuts)
        }
    }

    private fun createShortcut(
        context: Context,
        extraName: String = AppShortcutActivity.EXTRA_TOGGLE_DEBUG_AWAKE,
        enabled: Boolean,
        shortcutId: String = DEBUG_SHORTCUT_ID,
        shortLabel: String,
        longLabel: String,
        rank: Int = 1
    ): ShortcutInfo {
        val intent = Intent(context, AppShortcutActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = Intent.ACTION_RUN
        intent.putExtra(extraName, enabled)
        val acIcon = Icon.createWithResource(context, if (enabled) R.drawable.ic_check_box else R.drawable.ic_check_box_blank)
        return ShortcutInfo.Builder(context.applicationContext, shortcutId)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setIcon(acIcon)
            .setRank(rank)
            .setIntent(intent)
            .build()
    }

    fun clear() {
        val shortcutManager = app.getSystemService(ShortcutManager::class.java)
        shortcutManager.removeAllDynamicShortcuts()
    }

    companion object {
        private const val DEBUG_SHORTCUT_ID = "debug_power"
    }
}
