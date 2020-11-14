package com.afzaln.awakedebug

import com.afzaln.awakedebug.data.Prefs
import com.afzaln.awakedebug.ui.Shortcuts
import com.afzaln.awakedebug.data.SystemSettings

object Injector {
    private lateinit var app: AwakeDebugApp
    fun init(app: AwakeDebugApp) {
        this.app = app
    }

    val prefs: Prefs by lazy { Prefs(app) }
    val systemSettings: SystemSettings by lazy { SystemSettings(app, prefs) }
    val shortcuts: Shortcuts by lazy { Shortcuts(app) }
    val toggleController: ToggleController by lazy { ToggleController(prefs, systemSettings)}
}
