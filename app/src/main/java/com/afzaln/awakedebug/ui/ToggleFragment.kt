package com.afzaln.awakedebug.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.afzaln.awakedebug.Injector
import com.afzaln.awakedebug.R
import com.afzaln.awakedebug.databinding.ToggleFragmentBinding
import kotlinx.coroutines.launch

/**
 * View responsible for allowing the user
 * to enable and disable Awake for Debug
 */
class ToggleFragment : Fragment() {

    companion object {
        fun newInstance() = ToggleFragment()
    }

    private lateinit var binding: ToggleFragmentBinding
    private val systemSettings by lazy(Injector::systemSettings)
    private val viewModel by viewModels<ToggleViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ToggleFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (!systemSettings.hasAllPermissions) {
            (activity as MainActivity).navigateToPermissionFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.debuggingTypeLayout.visibility = View.VISIBLE
            binding.debuggingTypeGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                when (checkedId) {
                    R.id.usb_debugging -> viewModel.toggleUsbDebugging(isChecked)
                    R.id.wifi_debugging -> viewModel.toggleWifiDebugging(isChecked)
                }
            }

        } else {
            binding.debuggingTypeLayout.visibility = View.GONE
        }

        binding.toggleDebug.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDebugAwake(isChecked)
        }

        viewModel.settingsLiveData.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                binding.toggleDebug.isChecked = it.debugAwake
                binding.usbDebugging.isChecked = it.usbDebugging
                binding.wifiDebugging.isChecked = it.wifiDebugging
            }
        }
    }
}

