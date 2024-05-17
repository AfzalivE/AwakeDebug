package com.afzaln.awakedebug.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.afzaln.awakedebug.DebuggingType
import com.afzaln.awakedebug.Injector
import com.afzaln.awakedebug.R
import com.afzaln.awakedebug.databinding.ToggleFragmentBinding
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

/**
 * View responsible for allowing the user
 * to enable and disable Awake for Debug
 */
class ToggleFragment : Fragment() {

    companion object {
        fun newInstance() = ToggleFragment()
    }

    private var _binding: ToggleFragmentBinding? = null
    private val binding get() = _binding!!

    private val systemSettings by lazy(Injector::systemSettings)
    private val viewModel by viewModels<ToggleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ToggleFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        if (!systemSettings.hasAllPermissions) {
            (activity as MainActivity).navigateToPermissionFragment()
        } else {
            systemSettings.refreshScreenTimeout()
        }
    }

    private val groupChangeListener: MaterialButtonToggleGroup.OnButtonCheckedListener =
        MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.usb_debugging -> viewModel.toggleUsbDebugging(isChecked)
                R.id.wifi_debugging -> viewModel.toggleWifiDebugging(isChecked)
            }
        }

    private val toggleClickListener: (v: View) -> Unit = {
        viewModel.setDebugAwake(binding.toggleDebug.isChecked)
    }

    @ExperimentalTime
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // no wireless debugging type in versions below Android R
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.debuggingTypeGroup.visibility = View.VISIBLE
            binding.debuggingTypeLabel.visibility = View.VISIBLE
        } else {
            binding.debuggingTypeGroup.visibility = View.GONE
            binding.debuggingTypeLabel.visibility = View.GONE
        }

        binding.toggleDebug.setOnClickListener(toggleClickListener)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiStateFlow.collect { state ->
                    val displayTimeoutMs = state.screenTimeout.milliseconds
                    val displayTimeout = if (displayTimeoutMs.inWholeMinutes > 0) {
                        "${displayTimeoutMs.inWholeMinutes}m"
                    } else {
                        "${displayTimeoutMs.inWholeSeconds}s"
                    }
                    binding.toggleDebug.isChecked = state.debugAwake

                    updateDebuggingTypeButtons(state.usbDebugging, state.wifiDebugging)

                    binding.displayTimeout.text = displayTimeout
                    Timber.d("Screen timeout UI: ${state.screenTimeout}")
                    binding.debuggingStatus.text = getDebuggingStatusString(state.debuggingStatus)
                }
            }
        }
    }

    private fun updateDebuggingTypeButtons(usbDebugging: Boolean, wifiDebugging: Boolean) {
        // Remove listeners before updating so that the listener doesn't get called when we update.
        binding.debuggingTypeGroup.clearOnButtonCheckedListeners()
        binding.usbDebugging.isChecked = usbDebugging
        binding.wifiDebugging.isChecked = wifiDebugging
        binding.debuggingTypeGroup.addOnButtonCheckedListener(groupChangeListener)
    }

    private fun getDebuggingStatusString(debuggingStatus: List<DebuggingType>): CharSequence {
        return when {
            debuggingStatus.isEmpty() -> getString(DebuggingType.NONE.stringRes())
            debuggingStatus.size == 1 -> getString(debuggingStatus.first().stringRes())
            debuggingStatus == DebuggingType.usbWifiTypes -> getString(
                R.string.first_and_second,
                getString(DebuggingType.USB.stringRes()),
                getString(DebuggingType.WIFI.stringRes())
            )
            else -> getString(R.string.not_active)
        }
    }

    @StringRes
    fun DebuggingType.stringRes(): Int {
        return when (this) {
            DebuggingType.USB -> R.string.usb
            DebuggingType.WIFI -> R.string.wireless
            DebuggingType.NONE -> R.string.not_active
        }
    }
}

