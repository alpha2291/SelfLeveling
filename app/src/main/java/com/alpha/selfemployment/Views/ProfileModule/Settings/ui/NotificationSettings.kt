package com.alpha.selfemployment.Views.ProfileModule.Settings.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.GlobalSnackbar
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsUIViewModel
import com.alpha.selfemployment.navigation.LocalNavigator
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.NotificationOption
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsApiViewModel
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.brandBlue
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText


/**
 * NOTIFICATION SETTINGS FLOW:
 *
 * 1. Permission DENIED (system level):
 *    - Load API preferences
 *    - Main switch: OFF
 *    - Sub-items: OFF (disabled state)
 *
 * 2. Permission ALLOWED (system level):
 *    - If coming from permission request: Load API prefs, auto-enable main switch + all sub-items (unsaved state)
 *    - If normal entry: Load API prefs as-is
 *
 * 3. Save Button Logic:
 *    - ONLY enabled when isDirty = true
 *    - isDirty = true when: main switch changed OR any sub-item changed
 *    - isDirty tracks changes from the BASELINE (what API returned)
 *
 * 4. Main Switch Logic:
 *    - If all sub-items OFF → main switch must be OFF (even if user didn't explicitly turn it off)
 *    - If at least 1 sub-item ON → main switch can be ON
 *    - Turning OFF main switch → all sub-items turn OFF
 *    - Turning ON main switch → keeps current sub-item states (or turn all ON if coming from OFF)
 */


/**
 * CRITICAL FIX:
 *
 * Old accounts have notifications enabled in API.
 * When loading: API returns "[email,push,sms]" or similar
 * We need to:
 * 1. Parse the API response
 * 2. Turn ON main switch (if any items enabled)
 * 3. Enable all returned items
 * 4. Set baseline to match
 * 5. isDirty = false (no changes yet)
 */



@Composable
fun NotificationSettings(
    commonViewModel: SettingsUIViewModel = koinInject(),
    commonApiViewModel: SettingsApiViewModel = koinViewModel(),
    appPreferences: AppPreferences = koinInject()
) {
    val navigator = LocalNavigator.current

    val isDirty by commonViewModel.isNotificationDirty.collectAsStateWithLifecycle()
    val notificationPrefered by commonViewModel.notificationOptions.collectAsStateWithLifecycle()

    var isSaving by remember { mutableStateOf(false) }


    val switch by commonViewModel.notificationsAllowed.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
//                .background(primaryBlue)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(R.drawable.left_arrow),
                contentDescription = "",
                colorFilter = ColorFilter.tint(primaryWhite),
                modifier = Modifier
                    .size(32.dp)
                    .shrinkClick {
                        navigator.pop()
                    }
            )

            zText("Notifications", primaryWhite, 18, 2)
        }

        // Settings Content
        NotificationSettingsContent(
            commonViewModel,
            modifier = Modifier
                .fillMaxWidth()
                .weight(8f)
        )

        // Save Button
        NotificationOptionSavePrefs(
            isDirty = isDirty,
            isSaving = isSaving,
            onSaveAction = {
                isSaving = true

                val enabledIds = notificationPrefered
                    .filter { it.isEnabled }
                    .map { it.id }
                    .joinToString(",")

                val formattedIds = "[$enabledIds]"

                println("💾 Saving notification prefs: $formattedIds")

                commonApiViewModel.notification_Settings(
                    appPreferences.getUserId(),
                    if(switch) "1" else "0",
                    formattedIds,
                    "1"
                )
                { result ->
                    isSaving = false

                    when (result) {
                        is ResultHandler.Error -> {
                            println("❌ Save failed: ${result.message}")
                            GlobalSnackbar.show(id = R.string.apiError)
                        }
                        is ResultHandler.Success<*> -> {
                            println("✅ Save successful")

                            // ✅ Mark current state as saved baseline
                            commonViewModel.markNotificationSaved()

                            // ✅ Refresh API cache to reflect server state
                            commonApiViewModel.notification_Settings(
                                appPreferences.getUserId(),
                                "",
                                "",
                                "2"
                            ){}

                            GlobalSnackbar.show(id = R.string.preferences_saved_successfully)
                            navigator.pop()
                        }
                        else -> {
                            isSaving = false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}


@Composable
fun NotificationSettingsContent(
    commonViewModel: SettingsUIViewModel,
    modifier: Modifier,
    commonApiViewModel: SettingsApiViewModel = koinViewModel(),
    appPreferences: AppPreferences = koinInject()
) {
    val switch by commonViewModel.notificationsAllowed.collectAsStateWithLifecycle()
    val options by commonViewModel.notificationOptions.collectAsStateWithLifecycle()
    val notificationState by commonApiViewModel.notificationPrefsState.collectAsStateWithLifecycle()

    // ✅ Track if we've already initialized from API
    var apiInitialized by remember { mutableStateOf(false) }

    // ✅ STATIC OPTIONS - These are your available notification types
    val staticNotificationOptions = listOf(
        NotificationOption("1", "New Followers", false),
        NotificationOption("2", "New Likes", false),
        NotificationOption("3", "New Comments", false),
        NotificationOption("4", "New Content Post", false),
        NotificationOption("5", "Profile Visit", false),
        NotificationOption("6", "Content Reporting Notifications", false),
    )

    // ✅ STEP 1: Initialize options on first launch
    LaunchedEffect(Unit) {
        println("🔧 Step 1: Initializing notification options...")
        commonViewModel.initializeNotificationOptions(staticNotificationOptions)

        println("📥 Step 2: Loading preferences from API...")
        commonApiViewModel.notification_Settings(
            appPreferences.getUserId(),
            allow_notification = "",
            notification_type = "",
            status = "2",
        ){}
    }

    // ✅ STEP 2: Apply API preferences once they load
    LaunchedEffect(notificationState.notificationPrefs.firstOrNull()?.notification_type) {
        if (apiInitialized) {
            println("⏭️ Already initialized, skipping...")
            return@LaunchedEffect
        }

        val savedSettings = notificationState.notificationPrefs.firstOrNull()?.notification_type

        println("📨 Step 3: API returned settings: '$savedSettings'")

        if (savedSettings != null && savedSettings.isNotEmpty()) {
            println("✅ Applying notification history from API...")
            commonViewModel.applyNotificationHistory(savedSettings)
            appPreferences.save_NotificationEnabled(true)
            apiInitialized = true

            println("✅ Initialization complete - main switch should reflect API state")
            commonViewModel.debugPrintState()
        } else if (savedSettings != null && savedSettings.isEmpty()) {
            println("🔕 API returned empty - notifications disabled")
            commonViewModel.toggleNotifications(false)
            appPreferences.save_NotificationEnabled(false)
            apiInitialized = true
        }
    }

    Column(
        modifier.padding(horizontal = 12.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Allow Notification Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            zText("Allow Notification", black1A, 16, 1)

            Switch(
                checked = switch,
                onCheckedChange = { newValue ->
                    println("🔄 Main switch changed: $newValue (was: $switch)")

                    // ✅ NO DIRECT API CALL - just toggle local state
                    commonViewModel.toggleNotifications(newValue)

                    if (newValue) {
                        // ✅ When turning ON: Enable ALL sub-items (unsaved)
                        println("✅ Enabling all sub-items because switch turned ON")
                        options.forEach { option ->
                            commonViewModel.enableNotificationOption(option.id)
                        }
                    } else {
                        // ✅ When turning OFF: Disable ALL sub-items (unsaved)
                        println("❌ Disabling all sub-items because switch turned OFF")
                        options.forEach { option ->
                            if (option.isEnabled) {
                                commonViewModel.disableNotificationOption(option.id)
                            }
                        }
                    }

                    println("💾 User must click Save to persist changes")
                }
            )
        }

        // Notification Options Section
        when {
            options.isEmpty() && notificationState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    println("⏳ Showing loading indicator - options empty and API loading...")
                    CircularProgressIndicator()
                }
            }

            options.isNotEmpty() -> {
                println("✅ Showing ${options.size} options with switch=$switch")

                // ✅ Show options when switch is ON
                AnimatedVisibility(
                    visible = switch,
                    enter = fadeIn() + slideInVertically { -it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        options.forEach { option ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                zText(option.option, black1A, 14, 3)

                                Checkbox(
                                    checked = option.isEnabled,
                                    onCheckedChange = {
                                        println("🔄 Toggling ${option.id}: ${option.isEnabled} → ${!option.isEnabled}")

                                        // ✅ NO DIRECT API CALL - just toggle local state
                                        commonViewModel.toggleNotificationOptions(option.id)

                                        // ✅ If all sub-items become OFF, turn OFF main switch
                                        val newOptions = options.toMutableList()
                                        val index = newOptions.indexOfFirst { it.id == option.id }
                                        if (index != -1) {
                                            newOptions[index] = newOptions[index].copy(
                                                isEnabled = !newOptions[index].isEnabled
                                            )
                                        }

                                        val hasAnyEnabled = newOptions.any { it.isEnabled }
                                        if (!hasAnyEnabled && switch) {
                                            println("⚠️ All disabled - turning OFF main switch")
                                            commonViewModel.toggleNotifications(false)
                                        } else if (hasAnyEnabled && !switch) {
                                            println("✅ At least one enabled - turning ON main switch")
                                            commonViewModel.toggleNotifications(true)
                                        }

                                        println("💾 User must click Save to persist changes")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            notificationState.error.isNotEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    zText(
                        "Failed to load preferences",
                        black1A,
                        14,
                        2
                    )
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    println("⏳ Loading options...")
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@Composable
fun NotificationOptionSavePrefs(
    isDirty: Boolean,
    isSaving: Boolean = false,
    onSaveAction: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(.9f)
                .background(
                    when {
                        isSaving -> gray48
                        isDirty -> brandBlue
                        else -> gray66
                    }
                )
                .shrinkClick {
                    when {
                        isSaving -> {
                            // Do nothing while saving
                        }
                        !isDirty -> {
                            GlobalSnackbar.show(id = R.string.no_changes_to_save)
                        }
                        else -> {
                            onSaveAction()
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = brandBlue,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                zText(
                    "Save",
                    if (isDirty) primaryWhite else gray48,
                    14,
                    2
                )
            }
        }
    }
}