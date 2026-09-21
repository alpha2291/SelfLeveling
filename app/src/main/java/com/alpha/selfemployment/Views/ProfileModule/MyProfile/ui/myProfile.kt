package com.alpha.selfemployment.Views.ProfileModule.MyProfile.ui



import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.ExpandableBio
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.R
import com.alpha.selfemployment.UiState
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FF_Profile_TabRow
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.di.ProfileAPIViewModel
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.domain.model.Profile_ResponseData
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.grayB8
import com.alpha.selfemployment.ui.theme.green3A8
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.compose.SubcomposeAsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.videoFrameMillis
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.repository.ChatRepository
import com.alpha.selfemployment.createVideoThumbnail
import kotlinx.coroutines.launch


// How much the FF card overlaps the green section
private val FF_CARD_OVERLAP = 33.dp
private val FF_CARD_HEIGHT   = 66.dp


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MyProfile(
    profileAPIVm: ProfileAPIViewModel = koinViewModel(),
    sharedRepository: SharedRepository = koinInject(),
    chatRepository : ChatRepository = koinInject(),
    appPrefs: AppPreferences = koinInject()
) {
    val navigator = LocalNavigator.current
    var selectedTab by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    val network by rememberNetworkStatus()

    val isTabSticky by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex >= 2
        }
    }

    val scope = rememberCoroutineScope()

    val profileState by profileAPIVm.profileData.collectAsStateWithLifecycle()

    val postData by sharedRepository.profilePosts.collectAsStateWithLifecycle()
    val postLoading by sharedRepository.isLoading.collectAsStateWithLifecycle()
    val postError by sharedRepository.error.collectAsStateWithLifecycle()

    // ✅ Map tab to post_type
    val postType = remember(selectedTab) {
        when (selectedTab) {
            0 -> ""   // All
            1 -> "2"  // Videos
            2 -> "3"  // Articles
            else -> ""
        }
    }

    // =========================
    // Profile API
    // =========================
    LaunchedEffect(Unit) {
        profileAPIVm.getProfile(
            user_id = appPrefs.getUserId(),
            others_id = 0,
            device_id = "1",
            device_type = "1",
            device_token = "1",
            token = "",
        )
    }

    // =========================
    // Posts API on tab change
    // =========================
    LaunchedEffect(selectedTab, network) {
        if (network == NetworkStatus.Online) {
            listState.scrollToItem(0) // optional UX improvement
            profileAPIVm.getProfilePosts(
                user_id = appPrefs.getUserId(),
                others_id = 0,
                post_type = postType,
                loadMore = false
            )
        }
    }

    // =========================
    // Pagination
    // =========================
    LaunchedEffect(listState, postData, postLoading, selectedTab, network) {
        if (network != NetworkStatus.Online) return@LaunchedEffect

        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItemIndex to totalItemsCount
        }.collect { (lastVisibleItemIndex, totalItemsCount) ->

            val shouldLoadMore =
                lastVisibleItemIndex >= totalItemsCount - 3 &&
                        !postLoading &&
                        postData.isNotEmpty()

            if (shouldLoadMore) {
                profileAPIVm.getProfilePosts(
                    user_id = appPrefs.getUserId(),
                    others_id = 0,
                    post_type = postType,
                    loadMore = true
                )
            }
        }
    }

    when (profileState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularWavyProgressIndicator()
            }
        }

        is UiState.Success -> {
            val data = profileState as UiState.Success



            LaunchedEffect(Unit) {
                //scope.launch {
                    chatRepository.createOrUpdateUserNode(
                        userId = data.data.user_id.toString(),
                        name = data.data.name,
                        username = data.data.username,
                        phone = data.data.phone_num,
                        avatarUrl = data.data.profile_image,
                        email = ""
                    )
               // }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(primaryWhite)
            ) {

                // ── item 0 — green top section + overlapping FF card ──────────────
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                MyProfileTopBarContent(
                                    data.data,
                                    onSettingClick = { navigator.navigate(Screen.Settings) }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(.88f)
                                .height(FF_CARD_HEIGHT)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    clip = false
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .background(primaryWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            MyProfileFFOptions(data.data)
                        }
                    }
                }

                item {
                    spacer(6)
                }

                // ── sticky header — tab row ───────────────────────────────────────
                stickyHeader {
                    MyProfileTabRow(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        topPadding = if (isTabSticky) rememberNotchHeightDp().value else 0.dp
                    )
                }

                // ── Posts content ────────────────────────────────────────────────
                item {
                    Box {
                        when {
                            postLoading && postData.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularWavyProgressIndicator()
                                }
                            }

                            postError?.isNotEmpty() == true && postData.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    zText(postError ?: "Something went wrong", primaryBlack, 14, 0)
                                }
                            }

                            postData.isEmpty() && !postLoading -> {
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
                                    postData = postData,
                                    selectedTab = selectedTab
                                )
                            }
                        }

                        // Bottom pagination loader
                        if (postLoading && postData.isNotEmpty()) {
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

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                zText("Something went wrong", primaryBlack, 14, 0)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MyProfileTopBarContent(data: Profile_ResponseData, onSettingClick: () -> Unit) {
    val navigator = LocalNavigator.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(bottom = 24.dp)
            .fillMaxWidth()
    ) {

        // ── username + settings icon ──────────────────────────────────────
        Box(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            zText(
               data.username , primaryWhite, 20, 0,
                modifier = Modifier.align(Alignment.Center)
            )
            Image(
                painter = painterResource(R.drawable.myprofilesettings),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(32.dp)
                    .shrinkClick { onSettingClick() }
            )
        }

        // ── profile image ─────────────────────────────────────────────────
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
            ) { }
        }

        spacer(6)

        // ── display name ──────────────────────────────────────────────────
        zText(data.name, primaryWhite, 16, 0)

        spacer(4)

        // ── bio ───────────────────────────────────────────────────────────
        ExpandableBio(
            bio = data.bio
        )

        spacer(8)

        // ── edit profile button — full width ──────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth(.78f)
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.5.dp, primaryWhite, RoundedCornerShape(8.dp))
                .shrinkClick { navigator.navigate(Screen.Edit_Profile) },
            contentAlignment = Alignment.Center
        ) {
            zText(str(R.string.edit_Profile), primaryWhite, 14, 0)
        }

        spacer(16)
    }
}

// ─────────────────────────────────────────────────────────────────────────────


@Composable
fun MyProfileFFOptions(
    data: Profile_ResponseData,
    appPrefs: AppPreferences = koinInject()
) {
    val navigator = LocalNavigator.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Posts — no navigation
        Column(
            modifier                = Modifier.weight(1f),
            verticalArrangement     = Arrangement.SpaceEvenly,
            horizontalAlignment     = Alignment.CenterHorizontally
        ) {
            zText(data.posts.toString(), black1A, 16, 0)
            spacer(2)
            zText(str(R.string.videos), black1A, 12, 3)
        }

        VerticalDivider(modifier = Modifier.height(36.dp), color = grayB8)

        // Followers — tappable ✅
        Column(
            modifier = Modifier
                .weight(1f)
                .shrinkClick {
                    navigator.navigate(
                        Screen.FF_Profile(
                            userId     = appPrefs.getUserId(),
                            username   = data.username,
                            initialTab = FF_Profile_TabRow.FOLLOWERS,
                            followersCount = data.followers,    // ✅ real value from API
                            followingCount = data.following
                        )
                    )
                },
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            zText(data.followers.toString(), black1A, 16, 0)
            spacer(2)
            zText(str(R.string.followers), black1A, 12, 3)
        }

        VerticalDivider(modifier = Modifier.height(36.dp), color = grayB8)

        // Following — tappable ✅
        Column(
            modifier = Modifier
                .weight(1f)
                .shrinkClick {
                    navigator.navigate(
                        Screen.FF_Profile(
                            userId     = appPrefs.getUserId(),
                            username   = data.username,
                            initialTab = FF_Profile_TabRow.FOLLOWING,
                            followersCount = data.followers,    // ✅ real value from API
                            followingCount = data.following
                        )
                    )
                },
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            zText(data.following.toString(), black1A, 16, 0)
            spacer(2)
            zText(str(R.string.following), black1A, 12, 3)
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MyProfileTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    topPadding: Dp = 0.dp          // ← new param

) {
    val tabs = listOf(R.string.all, R.string.videos, R.string.articles)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryWhite)
            .padding(horizontal = 16.dp)
            .padding(top = topPadding)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor   = Color.White,
            contentColor     = primaryBlack,
            indicator        = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color    = primaryBlack,
                    height   = 2.dp
                )
            },
            divider = { Divider(color = grayB8, thickness = 1.dp) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick  = { onTabSelected(index) },
                    text     = {
                        Text(
                            text       = str(title),
                            fontSize   = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color      = if (selectedTab == index) primaryBlack else Color.Gray
                        )
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MyProfilePostGridContent(
    postData: List<PostPropertyData>,
    selectedTab: Int,
    sharedRepository: SharedRepository = koinInject()
) {
    val context = LocalContext.current
    val videoImageLoader = rememberVideoFrameImageLoader(context)

    val navigator = LocalNavigator.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(4.dp)
            .heightIn(min = 200.dp, max = 5000.dp),
        userScrollEnabled = false,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(postData.size) { index ->
            val item = postData[index]
            val isNormalVideo = isNormalVideoPost(item)
            val isYoutubeVideo = isYoutubeVideoPost(item)
            val isVideo = isNormalVideoPost(item)
            val isArticle = isArticlePost(item)

            val staticThumbnail = resolveStaticThumbnail(item)
            val videoUrl = item.post_property.video

            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFEFEF))
                    .shrinkClick {
                        sharedRepository.setPostCommonItems(postData , true)
                        navigator.navigate(Screen.CommonReelView)
                    }
            ) {

                when {
                    // =========================
                    // Normal video thumbnail from video URL
                    // =========================
                    isNormalVideo && videoUrl.isNotBlank() -> {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(videoUrl)
                                .videoFrameMillis(1000) // frame at 1 second
                                .crossfade(true)
                                .build(),
                            imageLoader = videoImageLoader,
                            contentDescription = "Video Thumbnail",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // =========================
                    // YouTube video thumbnail
                    // =========================
                    !staticThumbnail.isNullOrBlank() -> {
                        SubcomposeAsyncImage(
                            model = staticThumbnail,
                            contentDescription = "Post Thumbnail",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularWavyProgressIndicator()
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    zText("No Preview", primaryBlack, 12, 0)
                                }
                            }
                        )
                    }

                    // =========================
                    // Fallback
                    // =========================
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            zText("No Preview", primaryBlack, 12, 0)
                        }
                    }
                }

                // ▶ Video badge
                if (isVideo) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.video_icon),
                            contentDescription = "Video",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                // ▶ youtube badge
                if (isYoutubeVideo) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.video_icon),
                            contentDescription = "Video",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                // ▶ youtube badge
                if (isYoutubeVideo) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.youtube_profile_post_icon),
                            contentDescription = "Video",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                // ▶ article badge
                if (isArticle) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.articles_profile_post_icon),
                            contentDescription = "Video",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}



fun isYoutubeVideoPost(post: PostPropertyData): Boolean {
    return  post.post_property.post_type == "2"
}


fun isNormalVideoPost(post: PostPropertyData): Boolean {
    return post.post_property.post_type == "1"
}


fun isArticlePost(post: PostPropertyData): Boolean {
    return post.post_property.post_type == "3"
}





// ─────────────────────────────────────────────────────────────────────────────
// Utility: extract YouTube video ID from various URL formats
// ─────────────────────────────────────────────────────────────────────────────


@Composable
fun rememberVideoFrameImageLoader(context: Context): ImageLoader {
    return remember {
        ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }
}




fun extractYouTubeVideoId(url: String?): String? {
    if (url.isNullOrBlank()) return null

    val patterns = listOf(
        Regex("""youtu\.be/([A-Za-z0-9_\-]{11})"""),
        Regex("""[?&]v=([A-Za-z0-9_\-]{11})"""),
        Regex("""youtube\.com/embed/([A-Za-z0-9_\-]{11})"""),
        Regex("""youtube\.com/shorts/([A-Za-z0-9_\-]{11})""")
    )

    for (pattern in patterns) {
        val match = pattern.find(url) ?: continue
        return match.groupValues[1]
    }

    return null
}

fun youTubeThumbnailUrl(videoUrl: String?): String? {
    val id = extractYouTubeVideoId(videoUrl) ?: return null
    return "https://img.youtube.com/vi/$id/hqdefault.jpg"
}

fun getFirstImage(post: PostPropertyData): String? {
    return post.post_property.images
        .firstOrNull()
        ?.articles_photo
        ?.takeIf { it.isNotBlank() }
}

fun resolveStaticThumbnail(post: PostPropertyData): String? {
    return when (post.post_property.post_type) {


        // YouTube video
        "2" -> {
            youTubeThumbnailUrl(post.post_property.video)
        }


        // Image/article post
        else -> {
            getFirstImage(post)
        }
    }
}

