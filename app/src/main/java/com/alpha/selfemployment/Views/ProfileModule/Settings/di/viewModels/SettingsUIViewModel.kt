package com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


/**
 * Notification Option Data Class
 */
data class NotificationOption(
    val id: String,
    val option: String,
    val isEnabled: Boolean
)


class SettingsUIViewModel : ViewModel()
{

    private val _notificationsAllowed = MutableStateFlow(false)
    val notificationsAllowed: StateFlow<Boolean> = _notificationsAllowed.asStateFlow()

    private val _notificationOptions = MutableStateFlow<List<NotificationOption>>(emptyList())
    val notificationOptions: StateFlow<List<NotificationOption>> = _notificationOptions.asStateFlow()

    // ✅ BASELINE: State from API (last saved)
    private val _notificationBaselineOptions = MutableStateFlow<List<NotificationOption>>(emptyList())
    private val notificationBaselineOptions: StateFlow<List<NotificationOption>> = _notificationBaselineOptions.asStateFlow()

    // ✅ BASELINE: Main switch state from API
    private val _notificationBaselineAllowed = MutableStateFlow(false)
    private val notificationBaselineAllowed: StateFlow<Boolean> = _notificationBaselineAllowed.asStateFlow()

    // ✅ DIRTY STATE: Tracks if current state differs from baseline
    private val _isNotificationDirty = MutableStateFlow(false)
    val isNotificationDirty: StateFlow<Boolean> = _isNotificationDirty.asStateFlow()


    // ============================================
    // NOTIFICATION OPERATIONS
    // ============================================

    /**
     * Toggle the main notification switch ON/OFF
     */
    fun toggleNotifications(enabled: Boolean) {
        val wasChanged = _notificationsAllowed.value != enabled
        _notificationsAllowed.value = enabled

        if (wasChanged) {
            println("🔄 toggleNotifications: $enabled (was: ${!enabled})")
            updateDirtyState()
        }
    }

    /**
     * Toggle a specific notification option
     */
    fun toggleNotificationOptions(optionId: String) {
        val current = _notificationOptions.value.toMutableList()
        val index = current.indexOfFirst { it.id == optionId }

        if (index != -1) {
            current[index] = current[index].copy(isEnabled = !current[index].isEnabled)
            _notificationOptions.value = current
            println("🔄 toggleNotificationOptions: $optionId -> ${current[index].isEnabled}")
            updateDirtyState()
        }
    }

    /**
     * Enable a specific notification option (idempotent)
     */
    fun enableNotificationOption(optionId: String) {
        val current = _notificationOptions.value.toMutableList()
        val index = current.indexOfFirst { it.id == optionId }

        if (index != -1 && !current[index].isEnabled) {
            current[index] = current[index].copy(isEnabled = true)
            _notificationOptions.value = current
            println("✅ enableNotificationOption: $optionId")
            updateDirtyState()
        }
    }

    /**
     * Disable a specific notification option (idempotent)
     */
    fun disableNotificationOption(optionId: String) {
        val current = _notificationOptions.value.toMutableList()
        val index = current.indexOfFirst { it.id == optionId }

        if (index != -1 && current[index].isEnabled) {
            current[index] = current[index].copy(isEnabled = false)
            _notificationOptions.value = current
            println("❌ disableNotificationOption: $optionId")
            updateDirtyState()
        }
    }

    /**
     * ✅ CRITICAL FIX: Apply notification preferences from API response
     *
     * This function:
     * 1. Parses the settings string format: "[id1,id2,id3]"
     * 2. Enables matching options
     * 3. AUTOMATICALLY sets main switch ON if ANY items enabled
     * 4. Sets BOTH current and baseline to match API state
     * 5. isDirty = false (no changes from API)
     */
    fun applyNotificationHistory(settingsString: String) {
        println("📥 applyNotificationHistory called with: '$settingsString'")

        // Parse the settings string format: [id1,id2,id3]
        val enabledIds = settingsString
            .replace("[", "")
            .replace("]", "")
            .split(",")
            .filter { it.isNotEmpty() }
            .toSet()

        println("📋 Parsed enabled IDs: $enabledIds")

        // Update options to reflect enabled ones
        val current = _notificationOptions.value.toMutableList()
        current.forEach { option ->
            val index = current.indexOfFirst { it.id == option.id }
            if (index != -1) {
                val shouldBeEnabled = enabledIds.contains(option.id)
                if (current[index].isEnabled != shouldBeEnabled) {
                    current[index] = current[index].copy(isEnabled = shouldBeEnabled)
                    println("  ↳ ${option.id}: ${current[index].isEnabled}")
                }
            }
        }

        _notificationOptions.value = current

        // ✅ CRITICAL: Set main switch based on enabled items
        val hasAnyEnabled = current.any { it.isEnabled }

        println("✅ Setting main switch to: $hasAnyEnabled (${current.count { it.isEnabled }} items enabled)")

        _notificationsAllowed.value = hasAnyEnabled

        // ✅ Set baseline to match (API response is now baseline)
        _notificationBaselineOptions.value = current.toList()
        _notificationBaselineAllowed.value = hasAnyEnabled

        println("✅ Baseline updated:")
        println("   Main switch: $hasAnyEnabled")
        println("   Items: ${current.map { "${it.id}=${it.isEnabled}" }}")

        updateDirtyState()
    }

    /**
     * Initialize notification options with default structure
     * Call this once on ViewModel creation or when loading options list
     */
    fun initializeNotificationOptions(options: List<NotificationOption>) {
        println("🔧 initializeNotificationOptions: ${options.map { it.id }}")
        _notificationOptions.value = options
        _notificationBaselineOptions.value = options.toList()
        updateDirtyState()
    }

    /**
     * Mark current state as saved (update baseline)
     * Called after successful API save
     */
    fun markNotificationSaved() {
        println("💾 markNotificationSaved - updating baseline")

        println("   Current switch: ${_notificationsAllowed.value}")
        println("   Current items: ${_notificationOptions.value.map { "${it.id}=${it.isEnabled}" }}")

        _notificationBaselineOptions.value = _notificationOptions.value.toList()
        _notificationBaselineAllowed.value = _notificationsAllowed.value

        updateDirtyState()
    }

    /**
     * Check if current state differs from baseline
     */
    private fun updateDirtyState() {
        val isDirty = hasNotificationChanges()

        val previousDirty = _isNotificationDirty.value
        if (previousDirty != isDirty) {
            _isNotificationDirty.value = isDirty
            println("🔍 isDirty changed: $previousDirty → $isDirty")
        }
    }

    /**
     * Determine if there are unsaved changes
     */
    private fun hasNotificationChanges(): Boolean {
        // Check main switch
        if (_notificationsAllowed.value != _notificationBaselineAllowed.value) {
            println("  ⚠️ Main switch differs: ${_notificationsAllowed.value} vs ${_notificationBaselineAllowed.value}")
            return true
        }

        // Check sub-items
        val current = _notificationOptions.value
        val baseline = _notificationBaselineOptions.value

        if (current.size != baseline.size) {
            println("  ⚠️ Options count differs: ${current.size} vs ${baseline.size}")
            return true
        }

        current.forEachIndexed { index, option ->
            if (index < baseline.size && option.isEnabled != baseline[index].isEnabled) {
                println("  ⚠️ Option differs: ${option.id} = ${option.isEnabled} vs ${baseline[index].isEnabled}")
                return true
            }
        }

        return false
    }

    /**
     * Reset to last saved state (discard changes)
     */
    fun discardNotificationChanges() {
        println("↩️ discardNotificationChanges")

        _notificationsAllowed.value = _notificationBaselineAllowed.value
        _notificationOptions.value = _notificationBaselineOptions.value.toList()

        updateDirtyState()
    }

    /**
     * DEBUG: Print current state
     */
    fun debugPrintState() {
        println("=== NOTIFICATION STATE ===")
        println("Switch: ${_notificationsAllowed.value} (baseline: ${_notificationBaselineAllowed.value})")
        println("Options:")
        _notificationOptions.value.forEach { option ->
            val baselineOption = _notificationBaselineOptions.value.find { it.id == option.id }
            println("  ${option.id}: ${option.isEnabled} (baseline: ${baselineOption?.isEnabled})")
        }
        println("isDirty: ${_isNotificationDirty.value}")
        println("========================")
    }
}