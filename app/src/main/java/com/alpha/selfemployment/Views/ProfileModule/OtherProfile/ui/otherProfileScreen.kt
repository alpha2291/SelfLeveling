
package com.alpha.selfemployment.Views.ProfileModule.OtherProfile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.ExpandableBio
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.R
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FFApiViewModel
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FF_Profile_TabRow
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.di.ProfileAPIViewModel
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.domain.model.Profile_ResponseData
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.ui.MyProfilePostGridContent
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.grayB8
import com.alpha.selfemployment.ui.theme.green3A8
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

private val FF_CARD_OVERLAP = 33.dp
private val FF_CARD_HEIGHT = 66.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OthersProfile(
    userId: Int,
    appPrefs: AppPreferences = koinInject(),
    sharedRepo: SharedRepository = koinInject()
) {
    val navigator = LocalNavigator.current
    val listState = rememberLazyListState()
    var selectedTab by remember { mutableStateOf(0) }

    // Scoped ViewModel per userId so each profile keeps its own state
    val profileVm: ProfileAPIViewModel = koinViewModel(key = "others_profile_$userId") {
        parametersOf(userId)
    }

    val profileState by profileVm.profileData.collectAsStateWithLifecycle()
    val isFollowActionLoading by profileVm.isFollowActionLoading.collectAsStateWithLifecycle()

    // ✅ Reuse same post state from My Profile flow
    val profilePosts by sharedRepo.profilePosts.collectAsStateWithLifecycle()
    val isPostsLoading by sharedRepo.isLoading.collectAsStateWithLifecycle()
    val errorMessage by sharedRepo.error.collectAsStateWithLifecycle()

    val isTabSticky by remember {
        derivedStateOf { listState.firstVisibleItemIndex >= 2 }
    }

    // Load profile
    LaunchedEffect(userId) {
        profileVm.getProfile(
            user_id = appPrefs.getUserId(),
            others_id = userId,
            device_id = "1",
            device_type = "1",
            device_token = "1",
            token = ""
        )
    }

    // ✅ Load posts exactly like My Profile, but for other user
    LaunchedEffect(selectedTab, userId) {
        val postType = when (selectedTab) {
            1 -> "1" // Videos
            2 -> "2" // Articles
            else -> "" // All
        }

        profileVm.getProfilePosts(
            user_id = appPrefs.getUserId(),
            others_id = userId,
            post_type = postType,
            loadMore = false
        )
    }

    when (profileState) {

        is UiState.Loading, UiState.Idle -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    zText("Something went wrong", primaryBlack, 14, 3)
                    spacer(12)
                    Button(onClick = {
                        profileVm.getProfile(
                            user_id = appPrefs.getUserId(),
                            others_id = userId,
                            device_id = "1",
                            device_type = "1",
                            device_token = "1",
                            token = ""
                        )
                    }) {
                        Text("Retry")
                    }
                }
            }
        }

        is UiState.Success -> {
            val data = (profileState as UiState.Success<Profile_ResponseData>).data

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(primaryWhite)
            ) {

                // ── Green header + overlapping FF card ──────────────────────────
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = FF_CARD_OVERLAP)
                                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                                .background(green3A8)
                                .padding(top = rememberNotchHeightDp().value)
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(R.drawable.credentialsbackground),
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize()
                                )

                                OthersProfileTopBarContent(
                                    data = data,
                                    profileVm = profileVm,
                                    onBackClick = { navigator.pop() },
                                    onReportClick = {},
                                    onBlockClick = { actionValue ->
                                        profileVm.userBlockOrUnblock(
                                            user_id = appPrefs.getUserId(),
                                            blocker_id = data.user_id,
                                            status = if (actionValue == 0) "2" else "1",
                                        ) { result ->
                                            when (result) {
                                                is ResultHandler.Success<*> -> {
                                                    if (actionValue == 0) profileVm.onUnblock()
                                                    else profileVm.onBlock()
                                                }
                                                is ResultHandler.Error -> {}
                                                else -> {}
                                            }
                                        }
                                    },
                                    isFollowActionLoading = isFollowActionLoading
                                )
                            }
                        }

                        // FF stats card
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(.88f)
                                .height(FF_CARD_HEIGHT)
                                .shadow(8.dp, RoundedCornerShape(12.dp), clip = false)
                                .clip(RoundedCornerShape(12.dp))
                                .background(primaryWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            OthersProfileFFOptions(
                                data = data,
                                onFollowersClick = {
                                    navigator.navigate(
                                        Screen.FF_Profile(
                                            userId = userId,
                                            username = data.username,
                                            initialTab = FF_Profile_TabRow.FOLLOWERS,
                                            followersCount = data.followers,
                                            followingCount = data.following
                                        )
                                    )
                                },
                                onFollowingClick = {
                                    navigator.navigate(
                                        Screen.FF_Profile(
                                            userId = userId,
                                            username = data.username,
                                            initialTab = FF_Profile_TabRow.FOLLOWING,
                                            followersCount = data.followers,
                                            followingCount = data.following
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                item { spacer(6) }

                // ── Sticky tab row ──────────────────────────────────────────────
                stickyHeader {
                    OthersProfileTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        topPadding = if (isTabSticky) rememberNotchHeightDp().value else 0.dp
                    )
                }

                // ── SAME POSTS UI AS MY PROFILE ─────────────────────────────────
                item {
                    Box {
                        when {
                            isPostsLoading && profilePosts.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularWavyProgressIndicator()
                                }
                            }

                            errorMessage?.isNotEmpty() == true && profilePosts.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    zText(errorMessage ?: "Something went wrong", primaryBlack, 14, 0)
                                }
                            }

                            profilePosts.isEmpty() && !isPostsLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    zText("No posts yet", primaryBlack, 14, 0)
                                }
                            }

                            else -> {
                                MyProfilePostGridContent(
                                    postData = profilePosts,
                                    selectedTab = selectedTab
                                )
                            }
                        }

                        // Bottom pagination loader
                        if (isPostsLoading && profilePosts.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularWavyProgressIndicator()
                            }
                        }
                    }
                }
            }
        }

        else -> Unit
    }
}

@Composable
fun OthersProfileTopBarContent(
    data: Profile_ResponseData,
    profileVm: ProfileAPIViewModel,
    onBackClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockClick: (Int) -> Unit,
    appPrefs: AppPreferences = koinInject(),
    ffApiVm: FFApiViewModel = koinViewModel(),
    isFollowActionLoading: Boolean
) {
    var showMenu by remember { mutableStateOf(false) }

    val isBlocked = data.is_blocked == 1
    val blockActionValue = if (isBlocked) 0 else 1
    val blockMenuText = if (isBlocked) "Unblock" else "Block"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(bottom = 24.dp)
            .fillMaxWidth()
    ) {

        // Top bar — back arrow + username + more menu
        Box(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Back
            Image(
                painter = painterResource(R.drawable.left_arrow),
                contentDescription = "Back",
                colorFilter = ColorFilter.tint(primaryWhite),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(28.dp)
                    .shrinkClick { onBackClick() }
            )

            // Username
            zText(
                data.username,
                primaryWhite,
                20,
                0,
                modifier = Modifier.align(Alignment.Center)
            )

            // More icon + dropdown
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Image(
                    painter = painterResource(R.drawable.more_vert),
                    contentDescription = "More options",
                    colorFilter = ColorFilter.tint(primaryWhite),
                    modifier = Modifier
                        .size(28.dp)
                        .shrinkClick { showMenu = true }
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Report") },
                        onClick = {
                            showMenu = false
                            onReportClick()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(blockMenuText) },
                        onClick = {
                            showMenu = false
                            onBlockClick(blockActionValue)
                        }
                    )
                }
            }
        }

        // Profile image
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(primaryWhite),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = data.profile_image,
                contentDescription = "",
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(2.dp)
            ) {}
        }

        spacer(6)
        zText(data.name, primaryWhite, 16, 0)
        spacer(4)
        ExpandableBio(bio = data.bio)
        spacer(8)

        // Follow / Unfollow / Follow Back button
        OthersProfileFollowButton(
            data = data,
            isLoading = isFollowActionLoading,
            onUnblockClick = {
                if (isFollowActionLoading) return@OthersProfileFollowButton

                profileVm.userBlockOrUnblock(
                    user_id = appPrefs.getUserId(),
                    blocker_id = data.user_id,
                    status = "2",
                ) { result ->
                    when (result) {
                        is ResultHandler.Success -> profileVm.onUnblock()
                        is ResultHandler.Error -> {}
                        else -> {}
                    }
                }
            },
            onFollowClick = {
                if (isFollowActionLoading) return@OthersProfileFollowButton

                profileVm.setFollowActionLoading(true)

                ffApiVm.followUnfollowApi(
                    user_id = appPrefs.getUserId(),
                    following_id = data.user_id,
                    status = "1"
                ) { success ->
                    profileVm.setFollowActionLoading(false)
                    if (success) profileVm.onFollow()
                }
            },
            onFollowBackClick = {
                if (isFollowActionLoading) return@OthersProfileFollowButton

                profileVm.setFollowActionLoading(true)

                ffApiVm.followUnfollowApi(
                    user_id = appPrefs.getUserId(),
                    following_id = data.user_id,
                    status = "1"
                ) { success ->
                    profileVm.setFollowActionLoading(false)
                    if (success) profileVm.onFollowBack()
                }
            },
            onUnfollowClick = {
                if (isFollowActionLoading) return@OthersProfileFollowButton

                profileVm.setFollowActionLoading(true)

                ffApiVm.followUnfollowApi(
                    user_id = appPrefs.getUserId(),
                    following_id = data.user_id,
                    status = "2"
                ) { success ->
                    profileVm.setFollowActionLoading(false)
                    if (success) profileVm.onUnfollow()
                }
            }
        )

        spacer(16)
    }
}

enum class FollowButtonState {
    UNBLOCK,
    FOLLOWING,
    FOLLOW_BACK,
    FOLLOW
}

@Composable
fun OthersProfileFollowButton(
    data: Profile_ResponseData,
    isLoading: Boolean,
    onUnblockClick: () -> Unit,
    onFollowClick: () -> Unit,
    onFollowBackClick: () -> Unit,
    onUnfollowClick: () -> Unit
) {
    val buttonState = when {
        data.is_blocked == 1 -> FollowButtonState.UNBLOCK
        data.im_followed == 1 -> FollowButtonState.FOLLOWING
        data.is_followed == 1 -> FollowButtonState.FOLLOW_BACK
        else -> FollowButtonState.FOLLOW
    }

    val label = when {
        isLoading -> "Please wait..."
        buttonState == FollowButtonState.UNBLOCK -> "Unblock"
        buttonState == FollowButtonState.FOLLOWING -> "Following"
        buttonState == FollowButtonState.FOLLOW_BACK -> "Follow Back"
        else -> "Follow"
    }

    val bgColor = if (buttonState == FollowButtonState.FOLLOWING) {
        Color.Transparent
    } else {
        black1A
    }

    val borderMod = if (buttonState == FollowButtonState.FOLLOWING) {
        Modifier.border(1.5.dp, primaryWhite, RoundedCornerShape(8.dp))
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(.78f)
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(borderMod)
            .then(
                if (!isLoading) {
                    Modifier.shrinkClick {
                        when (buttonState) {
                            FollowButtonState.UNBLOCK -> onUnblockClick()
                            FollowButtonState.FOLLOWING -> onUnfollowClick()
                            FollowButtonState.FOLLOW_BACK -> onFollowBackClick()
                            FollowButtonState.FOLLOW -> onFollowClick()
                        }
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        zText(label, primaryWhite, 14, 0)
    }
}

@Composable
fun OthersProfileFFOptions(
    data: Profile_ResponseData,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Posts
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            zText(data.posts.toString(), black1A, 16, 0)
            spacer(2)
            zText("Posts", black1A, 12, 3)
        }

        VerticalDivider(modifier = Modifier.height(36.dp), color = grayB8)

        // Followers
        Column(
            modifier = Modifier
                .weight(1f)
                .shrinkClick { onFollowersClick() },
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            zText(data.followers.toString(), black1A, 16, 0)
            spacer(2)
            zText("Followers", black1A, 12, 3)
        }

        VerticalDivider(modifier = Modifier.height(36.dp), color = grayB8)

        // Following
        Column(
            modifier = Modifier
                .weight(1f)
                .shrinkClick { onFollowingClick() },
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            zText(data.following.toString(), black1A, 16, 0)
            spacer(2)
            zText("Following", black1A, 12, 3)
        }
    }
}

@Composable
fun OthersProfileTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    topPadding: Dp = 0.dp
) {
    val tabs = listOf("All", "Videos", "Article")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryWhite)
            .padding(top = topPadding)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = primaryBlack,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = primaryBlack,
                    height = 2.dp
                )
            },
            divider = { Divider(color = grayB8, thickness = 1.dp) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) primaryBlack else Color.Gray
                        )
                    }
                )
            }
        }
    }
}


