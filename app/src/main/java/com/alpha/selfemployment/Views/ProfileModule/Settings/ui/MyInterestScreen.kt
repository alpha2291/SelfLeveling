package com.alpha.selfemployment.Views.ProfileModule.Settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsApiViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpha.selfemployment.R
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.model.MyInterestResponseData
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Composable
fun MyInterestScreen1(
    settingsApiViewModel: SettingsApiViewModel = koinViewModel(),
    appPreferences: AppPreferences = koinInject()
){
    LaunchedEffect(Unit) {
        settingsApiViewModel.myInterest(
            user_id = appPreferences.getUserId(),
            interest_id = TODO(),
            status = TODO(),
            resultHandler = TODO()
        )
    }
}



// ─── Domain Model ───────────────────────────────────────────────────────────

// Already have MyInterestResponse & MyInterestResponseData

// Add this for the full interests list:
@kotlinx.serialization.Serializable
data class AllInterestsResponse(
    @SerialName("data") val data: List<InterestItem>,
    @SerialName("error") val error: String,
    @SerialName("message") val message: String,
    @SerialName("result") val result: String
)

@Serializable
data class InterestItem(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String   // "Beauty & Grooming", "Tailoring", etc.
)


// ─── UI State (in SettingsApiViewModel) ─────────────────────────────────────

data class MyInterestUiState(
    val isLoading: Boolean = false,
    val myInterestOrfs: List<MyInterestResponseData> = emptyList(),
    val error: String = ""
)

data class AllInterestsUiState(
    val isLoading: Boolean = false,
    val interests: List<InterestItem> = emptyList(),
    val error: String = ""
)


// ─── ViewModel additions ─────────────────────────────────────────────────────

//


// ─── MyInterestScreen.kt ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInterestScreen222(
    onBack: () -> Unit = {},
    settingsApiViewModel: SettingsApiViewModel = koinViewModel(),
    appPreferences: AppPreferences = koinInject()
) {
    val userId = appPreferences.getUserId()

    val myInterestState by settingsApiViewModel.myInterest.collectAsState()
    val allInterestsState by settingsApiViewModel.allInterests.collectAsState()

    // IDs already saved on server (from GET call)
    val savedIds = remember(myInterestState.myInterestOrfs) {
        myInterestState.myInterestOrfs.map { it.interest_id }.toSet()
    }

    // Local selection state — initialised once savedIds arrive
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var originalIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var initialised by remember { mutableStateOf(false) }

    // Toast state
    var showToast by remember { mutableStateOf(false) }

    // Seed selection once we have saved data
    LaunchedEffect(savedIds) {
        if (!initialised && savedIds.isNotEmpty()) {
            selectedIds = savedIds
            originalIds = savedIds
            initialised = true
        }
    }

    // On first load: fetch all interests + fetch user's saved interests
    LaunchedEffect(Unit) {
        settingsApiViewModel.myInterest(
            user_id = 1,
            interest_id = "",       // empty for GET
            status = "3",           // 3 = get
            resultHandler = {}
        )
    }

    // Auto-hide toast
    LaunchedEffect(showToast) {
        if (showToast) {
            delay(2000)
            showToast = false
        }
    }

    val hasChanges = selectedIds != originalIds
    val isLoading = myInterestState.isLoading || allInterestsState.isLoading

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "My Interest",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Image(painter = painterResource(R.drawable.left_arrow) ,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            bottomBar = {
                BottomActionBar(
                    hasChanges = hasChanges,
                    isLoading = isLoading,
                    onReset = {
                        selectedIds = originalIds   // revert to last saved state
                    },
                    onSave = {
                        val interestIdsCsv = selectedIds.joinToString(",")
                        // Use status "1" if saving for first time, "2" for update
                        val status = if (originalIds.isEmpty()) "1" else "2"
                        settingsApiViewModel.myInterest(
                            user_id = userId,
                            interest_id = interestIdsCsv,
                            status = status,
                            resultHandler = {}
                        )
                        // Optimistically update original & show toast
                        originalIds = selectedIds
                        showToast = true
                    }
                )
            },
            containerColor = Color.White
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Explore all topics available in the app.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF1A6B3A))
                        }
                    }

                    allInterestsState.error.isNotEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = allInterestsState.error, color = Color.Red)
                        }
                    }

                    else -> {
                        InterestChipsGrid(
                            interests = allInterestsState.interests,
                            selectedIds = selectedIds,
                            onChipToggle = { interestId ->
                                selectedIds = if (selectedIds.contains(interestId)) {
                                    selectedIds - interestId
                                } else {
                                    selectedIds + interestId
                                }
                            }
                        )
                    }
                }
            }
        }

        // "Interests Saved" Toast — bottom centre, above bottom bar
        AnimatedVisibility(
            visible = showToast,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            InterestsSavedToast()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MyInterestScreen(
    onBack: () -> Unit = {},
    settingsApiViewModel: SettingsApiViewModel = koinViewModel(),
    appPreferences: AppPreferences = koinInject()
) {
    val userId = appPreferences.getUserId()
    val myInterestState by settingsApiViewModel.myInterest.collectAsState()

    // Full interest list from GET (status=3) response
    val allInterests = myInterestState.myInterestOrfs

    // IDs that were saved on server — used to seed selection
    // NOTE: On GET (status=3), the API returns ALL interests
    // On a saved state, interest_id is the saved one
    // We track selection locally
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var originalIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var initialised by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }

    // Once GET data arrives, seed the selection
    LaunchedEffect(allInterests) {
        if (!initialised && allInterests.isNotEmpty()) {
            // interest_id from GET response = the interest's own ID
            val preSelected = allInterests
                .filter { it.user_id != 0 } // if user_id present = user has saved it
                .map { it.interest_id }
                .toSet()
            selectedIds = preSelected
            originalIds = preSelected
            initialised = true
        }
    }

    // Fetch on screen open (status=3 = GET all interests for this user)
    LaunchedEffect(Unit) {
        settingsApiViewModel.myInterest(
            user_id = userId,
            interest_id = "",
            status = "3",
            resultHandler = {}
        )
    }

    // Auto-hide toast
    LaunchedEffect(showToast) {
        if (showToast) {
            delay(2000)
            showToast = false
        }
    }

    val hasChanges = selectedIds != originalIds
    val isLoading = myInterestState.isLoading

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "My Interest",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Image(
                                painter = painterResource(R.drawable.left_arrow),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            bottomBar = {
                BottomActionBar(
                    hasChanges = hasChanges,
                    isLoading = isLoading,
                    onReset = {
                        selectedIds = originalIds
                    },
                    onSave = {
                        val interestIdsCsv = selectedIds.joinToString(",")
                        val status = if (originalIds.isEmpty()) "1" else "2"
                        settingsApiViewModel.myInterest(
                            user_id = userId,
                            interest_id = interestIdsCsv,
                            status = status,
                            resultHandler = {}
                        )
                        originalIds = selectedIds
                        showToast = true
                    }
                )
            },
            containerColor = Color.White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Explore all topics available in the app.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1A6B3A))
                        }
                    }

                    myInterestState.error.isNotEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = myInterestState.error, color = Color.Red)
                        }
                    }

                    else -> {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            allInterests.forEach { interest ->
                                val isSelected = selectedIds.contains(interest.interest_id)
                                InterestChip(
                                    label = interest.interest_id, // ⚠️ replace with name field if API returns it
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedIds = if (isSelected) {
                                            selectedIds - interest.interest_id
                                        } else {
                                            selectedIds + interest.interest_id
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showToast,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            InterestsSavedToast()
        }
    }
}


// ─── InterestChipsGrid ───────────────────────────────────────────────────────

@Composable
fun InterestChipsGrid(
    interests: List<InterestItem>,
    selectedIds: Set<String>,
    onChipToggle: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        interests.forEach { interest ->
            val isSelected = selectedIds.contains(interest.id.toString())
            InterestChip(
                label = interest.name,
                isSelected = isSelected,
                onClick = { onChipToggle(interest.id.toString()) }
            )
        }
    }
}


// ─── InterestChip ────────────────────────────────────────────────────────────

@Composable
fun InterestChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF1A6B3A) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color(0xFF333333)
    val borderColor = if (isSelected) Color(0xFF1A6B3A) else Color(0xFFCCCCCC)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}


// ─── BottomActionBar ─────────────────────────────────────────────────────────

@Composable
fun BottomActionBar(
    hasChanges: Boolean,
    isLoading: Boolean,
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Reset Changes
        OutlinedButton(
            onClick = onReset,
            enabled = hasChanges && !isLoading,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, if (hasChanges) Color(0xFFCCCCCC) else Color(0xFFEEEEEE)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF333333),
                disabledContentColor = Color(0xFFAAAAAA)
            )
        ) {
            Text("Reset Changes")
        }

        // Save Interests
        Button(
            onClick = onSave,
            enabled = hasChanges && !isLoading,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A),
                disabledContainerColor = Color(0xFFAAAAAA)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Interests", color = Color.White)
            }
        }
    }
}


// ─── Toast ───────────────────────────────────────────────────────────────────

@Composable
fun InterestsSavedToast() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A6B3A))
            )
            Text(
                text = "Interests Saved",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}