package com.afzaln.awakedebug.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.afzaln.awakedebug.Injector
import com.afzaln.awakedebug.R
import com.afzaln.awakedebug.databinding.ActivityMainBinding
import com.mikepenz.aboutlibraries.LibsBuilder

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val systemSettings by lazy(Injector::systemSettings)
    private val shortcuts by lazy(Injector::shortcuts)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.inflateMenu(R.menu.main)
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.about) {
                navigateToAboutFragment()
            }
            true
        }

        supportFragmentManager.addOnBackStackChangedListener {
            updateToolbar()
        }

        binding.toolbar.setNavigationOnClickListener {
            supportFragmentManager.popBackStack()
        }

        if (savedInstanceState == null) {
            setFragment()
        } else {
            updateToolbar()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateToolbar() {
        if (supportFragmentManager.peekBackstackEntryOrNull()?.name == ABOUT_BACKSTACK_ENTRY) {
            binding.toolbar.setTitle(R.string.about)
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            binding.toolbar.setNavigationContentDescription(R.string.back_navigation_description)
            binding.toolbar.menu.findItem(R.id.about).isVisible = false
        } else {
            binding.toolbar.menu.findItem(R.id.about).isVisible = true
            binding.toolbar.navigationIcon = null
            binding.toolbar.setTitle(R.string.app_name)
        }
    }

    private fun setFragment() = if (systemSettings.hasAllPermissions) {
        navigateToToggleFragment()
    } else {
        navigateToPermissionFragment()
    }

    fun navigateToPermissionFragment() {
        shortcuts.clear()
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.content_frame, PermissionFragment.newInstance())
            .commit()
    }

    fun navigateToToggleFragment() {
        shortcuts.updateShortcuts()
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.content_frame, ToggleFragment.newInstance())
            .commit()
    }

    private fun navigateToAboutFragment() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.content_frame, AboutFragment.newInstance())
            .addToBackStack(ABOUT_BACKSTACK_ENTRY)
            .commit()
    }

    private fun FragmentManager.peekBackstackEntryOrNull(): FragmentManager.BackStackEntry? {
        if (backStackEntryCount == 0) return null
        return getBackStackEntryAt(backStackEntryCount - 1)
    }

    fun navigateToLicenses() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, LibsBuilder().supportFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(ABOUT_BACKSTACK_ENTRY)
            .commit()
    }

    companion object {
        const val ABOUT_BACKSTACK_ENTRY = "about"
    }
}
