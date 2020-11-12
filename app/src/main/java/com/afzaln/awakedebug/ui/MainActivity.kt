package com.afzaln.awakedebug.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afzaln.awakedebug.Injector
import com.afzaln.awakedebug.R
import com.afzaln.awakedebug.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val systemSettings by lazy(Injector::systemSettings)
    private val shortcuts by lazy(Injector::shortcuts)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment()
    }

    private fun setFragment() = if (systemSettings.hasAllPermissions) {
        navigateToToggleFragment()
    } else {
        navigateToPermissionFragment()
    }

    fun navigateToPermissionFragment() {
        shortcuts.clear()
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, PermissionFragment.newInstance())
            .commit()
    }

    fun navigateToToggleFragment() {
        shortcuts.updateShortcuts()
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, ToggleFragment.newInstance())
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .commit()
    }
}
