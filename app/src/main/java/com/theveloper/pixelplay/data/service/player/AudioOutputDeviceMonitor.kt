package com.theveloper.pixelplay.data.service.player

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

enum class OutputRoutingType {
    BUILTIN_SPEAKER,
    WIRED_HEADPHONES,
    USB_DAC,
    BLUETOOTH,
    OTHER
}

data class OutputDeviceInfo(
    val type: OutputRoutingType,
    val name: String,
    val isDirectHiresCapable: Boolean
)

/**
 * Monitors active audio output device routes to assist Bit-Perfect pipeline.
 * Detects USB DAC connections and wired Hi-Res paths.
 */
class AudioOutputDeviceMonitor(
    private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _currentOutput = MutableStateFlow(resolveCurrentOutput())
    val currentOutput: StateFlow<OutputDeviceInfo> = _currentOutput.asStateFlow()

    private val deviceCallback = object : android.media.AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
            updateCurrentOutput()
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
            updateCurrentOutput()
        }
    }

    private var isRegistered = false

    fun startMonitoring() {
        if (isRegistered) return
        try {
            audioManager.registerAudioDeviceCallback(deviceCallback, null)
            isRegistered = true
            updateCurrentOutput()
        } catch (e: Exception) {
            Timber.tag("AudioOutputDeviceMonitor").e(e, "Failed to register audio device callback")
        }
    }

    fun stopMonitoring() {
        if (!isRegistered) return
        try {
            audioManager.unregisterAudioDeviceCallback(deviceCallback)
            isRegistered = false
        } catch (e: Exception) {
            Timber.tag("AudioOutputDeviceMonitor").e(e, "Failed to unregister audio device callback")
        }
    }

    private fun updateCurrentOutput() {
        _currentOutput.value = resolveCurrentOutput()
    }

    private fun resolveCurrentOutput(): OutputDeviceInfo {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        // Prioritize USB DAC / USB Audio
        val usbDevice = devices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_USB_DEVICE ||
            it.type == AudioDeviceInfo.TYPE_USB_HEADSET ||
            it.type == AudioDeviceInfo.TYPE_USB_ACCESSORY
        }
        if (usbDevice != null) {
            val name = usbDevice.productName?.toString().takeUnless { it.isNullOrBlank() } ?: "USB DAC"
            return OutputDeviceInfo(OutputRoutingType.USB_DAC, name, isDirectHiresCapable = true)
        }

        // Check Wired Headset / Headphones
        val wiredDevice = devices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
            it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
            it.type == AudioDeviceInfo.TYPE_LINE_ANALOG ||
            it.type == AudioDeviceInfo.TYPE_LINE_DIGITAL
        }
        if (wiredDevice != null) {
            val name = wiredDevice.productName?.toString().takeUnless { it.isNullOrBlank() } ?: "Wired Audio"
            return OutputDeviceInfo(OutputRoutingType.WIRED_HEADPHONES, name, isDirectHiresCapable = true)
        }

        // Check Bluetooth
        val btDevice = devices.firstOrNull {
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
            it.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
            it.type == AudioDeviceInfo.TYPE_BLE_SPEAKER
        }
        if (btDevice != null) {
            val name = btDevice.productName?.toString().takeUnless { it.isNullOrBlank() } ?: "Bluetooth Device"
            return OutputDeviceInfo(OutputRoutingType.BLUETOOTH, name, isDirectHiresCapable = false)
        }

        return OutputDeviceInfo(OutputRoutingType.BUILTIN_SPEAKER, "Internal Speaker", isDirectHiresCapable = false)
    }
}
