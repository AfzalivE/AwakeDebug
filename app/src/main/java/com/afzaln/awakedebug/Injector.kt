package com.afzaln.awakedebug

import com.afzaln.awakedebug.data.Prefs
import com.afzaln.awakedebug.data.Shortcuts
import com.afzaln.awakedebug.data.SystemSettings

object Injector {
    private lateinit var app: MyApplication
    fun init(app: MyApplication) {
        this.app = app
    }

    val prefs: Prefs by lazy { Prefs(app) }
    val systemSettings: SystemSettings by lazy { SystemSettings(app, prefs) }
    val shortcuts: Shortcuts by lazy { Shortcuts(app) }
}
