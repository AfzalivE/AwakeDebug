package com.afzaln.awakedebug.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afzaln.awakedebug.Injector
import com.afzaln.awakedebug.databinding.PermissionFragmentBinding

/**
 * Responsible for showing the UI related to
 * requesting permissions from the user
 */
class PermissionFragment : Fragment() {

    private lateinit var binding: PermissionFragmentBinding
    private val systemSettings by lazy(Injector::systemSettings)

    companion object {
        fun newInstance() = PermissionFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PermissionFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationPermission.setOnClickListener {
            requestNotificationPermission()
        }

        binding.modifySettingsPermission.setOnClickListener {
            showPermissionActivity()
        }

        binding.continueBtn.setOnClickListener {
            (activity as MainActivity).navigateToToggleFragment()
        }
    }

    override fun onStart() {
        super.onStart()

        binding.notificationPermissionFlipper.displayedChild = if (systemSettings.hasNotificationPermission) 1 else 0
        binding.modifySettingsFlipper.displayedChild = if (systemSettings.hasModifyPermission) 1 else 0
        binding.continueBtn.visibility = if (systemSettings.hasAllPermissions) View.VISIBLE else View.GONE
    }

    private fun showPermissionActivity() {
        val context = context ?: return
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        startActivity(intent)
    }

    private fun requestNotificationPermission() {
        activity?.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}
