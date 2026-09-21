package com.alpha.selfemployment.Views.Home.Videos.ui

import androidx.compose.runtime.Composable


import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import coil.compose.SubcomposeAsyncImage
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.ExpandableMessage
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ReelsOptionBtmSheet
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.CommonView.CommentUI
import com.alpha.selfemployment.Views.Home.Videos.di.HomeAPIViewModel
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.YoutubePreviewTrial
import com.alpha.selfemployment.extractYoutubeId
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.networkToast
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.utils
import com.alpha.selfemployment.zText
import kotlinx.coroutines.*
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

// ─────────────────────────────────────────────


// ─────────────────────────────────────────────
//  Gradient Progress Slider
// ─────────────────────────────────────────────
@Composable
fun GradientProgressSlider(
    progress: Float,           // 0f..1f
    buffered: Float,           // 0f..1f  (buffer ahead)
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 6.dp,
    thumbSize: Dp = 14.dp,
    gradientColors: List<Color> = listOf(
        Color(0xff3A8154),
        Color(0xffFFF825),
//        Color(0xFFFF6FCF),
//        Color(0xFFA78BFA)
    )
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(progress) }
    val displayProgress = if (isDragging) dragProgress else progress

//    val thumbScale by animateFloatAsState(
//        targetValue = if (isDragging) 1.3f else 1f,
//        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
//        label = "thumb_scale"
//    )

    val trackAlpha by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0.85f,
        label = "track_alpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .height(thumbSize + 8.dp)
            .fillMaxWidth()
    ) {
        val totalWidth = constraints.maxWidth.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragProgress = (offset.x / totalWidth).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            isDragging = false
                            onSeek(dragProgress)
                        },
                        onDragCancel = { isDragging = false },
                        onHorizontalDrag = { _, dragAmount ->
                            dragProgress =
                                (dragProgress + dragAmount / totalWidth).coerceIn(0f, 1f)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val tapped = (offset.x / totalWidth).coerceIn(0f, 1f)
                        onSeek(tapped)
                    }
                }
        ) {
            val cy = size.height / 2f
            val trackH = trackHeight.toPx()
            val thumbR = (thumbSize / 2).toPx()
            val trackTop = cy - trackH / 2

            // Background track
            drawRoundRect(
                color = Color.White.copy(alpha = 0.2f * trackAlpha),
                topLeft = Offset(0f, trackTop),
                size = Size(size.width, trackH),
                cornerRadius = CornerRadius(trackH / 2)
            )

            // Buffer track
            drawRoundRect(
                color = Color.White.copy(alpha = 0.35f * trackAlpha),
                topLeft = Offset(0f, trackTop),
                size = Size(size.width * buffered, trackH),
                cornerRadius = CornerRadius(trackH / 2)
            )

            // Gradient progress track
            val progressWidth = size.width * displayProgress
            if (progressWidth > 0f) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset(0f, cy),
                        end = Offset(size.width, cy)
                    ),
                    topLeft = Offset(0f, trackTop),
                    size = Size(progressWidth, trackH),
                    cornerRadius = CornerRadius(trackH / 2),
                    alpha = trackAlpha
                )
            }

            // Thumb
            /*val thumbX = size.width * displayProgress
            scale(thumbScale, pivot = Offset(thumbX, cy)) {
                // Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            gradientColors.last().copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(thumbX, cy),
                        radius = thumbR * 2.5f
                    ),
                    radius = thumbR * 2.5f,
                    center = Offset(thumbX, cy)
                )
                // Main thumb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = gradientColors.reversed(),
                        center = Offset(thumbX, cy),
                        radius = thumbR
                    ),
                    radius = thumbR,
                    center = Offset(thumbX, cy)
                )
                // White inner dot
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = thumbR * 0.35f,
                    center = Offset(thumbX, cy)
                )
            }*/
        }
    }
}


// ─────────────────────────────────────────────
//  Single Reel Item
// ─────────────────────────────────────────────




@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReelItemView(
    reel: PostPropertyData,
    isActive: Boolean,
    onLike: (Int) -> Unit,
    onComment: (Int) -> Unit,
    onSave: (Int) -> Unit,
    onOptions: (Int) -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier,
    homeVm: HomeAPIViewModel = koinViewModel(),
    sharedRepo: SharedRepository = koinInject(),
    appPreferences: AppPreferences = koinInject()
) {
    val network by rememberNetworkStatus()
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    // ⚡ CRITICAL FIX: Determine video type FIRST, before any state initialization
    val isYouTubeVideo = reel.post_property.post_type == "2"

    // Player state - only initialize for non-YouTube videos
    var player by remember(reel.user_post_id) { mutableStateOf<ExoPlayer?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    var buffered by remember { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(false) }
    var firstFrameReady by remember { mutableStateOf(isYouTubeVideo) } // YouTube = instant ready
    var hasError by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(1L) }

    // Like animation
    var showHeartBurst by remember { mutableStateOf(false) }
    val heartScale by animateFloatAsState(
        targetValue = if (showHeartBurst) 1.4f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heart"
    )


    LaunchedEffect(showHeartBurst) {
        if (showHeartBurst) {
            delay(600)
            showHeartBurst = false
        }
    }

    // ⚡ CRITICAL: Only create ExoPlayer for type "1" (direct video files)
    DisposableEffect(reel.user_post_id) {
        if (isYouTubeVideo) {
            // Skip ExoPlayer entirely for YouTube
            println("### Skipping ExoPlayer - YouTube video detected")
            onDispose { }
           // return@DisposableEffect
        }
        else {


            println("### Creating ExoPlayer for direct video: ${reel.post_property.video}")
            val exo = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(reel.post_property.video ?:""))
                repeatMode = Player.REPEAT_MODE_ONE
                prepare()
            }

            val listener = object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    firstFrameReady = true
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }

                override fun onPlayerError(error: PlaybackException) {
                    hasError = true
                    println("### ExoPlayer error: ${error.message}")
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        firstFrameReady = true
                        exo.duration.takeIf { it > 0 }?.let { duration = it }
                    }
                }
            }
            exo.addListener(listener)
            player = exo


            onDispose {
                println("### Disposing ExoPlayer")
                exo.removeListener(listener)
                exo.release()
                player = null
            }
        }
    }

    // Play/pause based on active page (ExoPlayer only)
    LaunchedEffect(isActive, player) {
        if (!isYouTubeVideo) {
            player?.playWhenReady = isActive
        }
    }

    // Progress polling (ExoPlayer only)
    LaunchedEffect(isActive, player) {
        if (isYouTubeVideo) return@LaunchedEffect

        while (isActive) {
            player?.let { p ->
                if (p.duration > 0) {
                    progress = p.currentPosition.toFloat() / p.duration.toFloat()
                    buffered = p.bufferedPosition.toFloat() / p.duration.toFloat()
                    duration = p.duration
                }
            }
            delay(200)
        }
    }

    val gradientSliderColors = listOf(
        Color(0xffFFF825),
        Color(0xff3A8154),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        println("player -- $player, isYouTube: $isYouTubeVideo, post_type: ${reel.post_property.post_type}")

        if (isYouTubeVideo && isActive) {
            val videoId = remember(reel.post_property.video) {
                extractYoutubeId(reel.post_property.video)
            }
            YoutubePreviewTrial(
                videoId = videoId,  // ✅ Pass clean ID, not full URL
                isActive = isActive,
                onCardClick = {
                    utils.open_youtubeReelBtm(reel.post_property.video)
                }
            )
        }
        else {
            player?.let { p ->
                ContentFrame(
                    player = p,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }


        // ── Loading (ExoPlayer only) ──────────────────
        if (!isYouTubeVideo && !firstFrameReady && !hasError) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularWavyProgressIndicator()
            }
        }

        // ── Double-tap gestures ────────────────────────
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (!isYouTubeVideo) {
                                if (isPlaying) {
                                    player?.pause()
                                } else {
                                    player?.play()
                                }
                            }
                        },
                        onDoubleTap = {
                            onLike(reel.user_post_id)
                            showHeartBurst = true
                        }
                    )
                }
        )

        if (showHeartBurst) {
            Icon(
                painter = painterResource(R.drawable.favorite),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(90.dp)
                    .scale(heartScale)
                    .alpha(heartScale / 1.4f)
            )
        }

        // ── Play button (ExoPlayer only) ──────────────
        if (!isYouTubeVideo) {
            player?.isPlaying?.let {
                if (!it && firstFrameReady) {
                    Image(
                        painter = painterResource(R.drawable.play_filled),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // ── Gradients ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .align(Alignment.BottomCenter)
        )

        // ── Bottom info + slider ───────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            ReelsBottomDetailsOverLay(
                reel,
                onLikeClick = { id -> onLike(id) },
                onSaveClick = { id -> onSave(id) },
                onCommentClick = { id -> onComment(id) },
                onOptionClick = { id -> onOptions(id) }
            )

            spacer(2)

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color.Transparent, RoundedCornerShape(8.dp))
                    .border(1.dp, primaryWhite, RoundedCornerShape(8.dp))
                    .shrinkClick {
                        navigator.navigate(Screen.ViewDetailsScreen(reel.user_post_id))
                    },
                contentAlignment = Alignment.Center
            ) {
                zText(str(R.string.view_business_details), primaryWhite, 14, 0)
            }

            spacer(4)

            // ── Progress Slider (ExoPlayer only) ──────────
            if (!isYouTubeVideo) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GradientProgressSlider(
                        progress = progress,
                        buffered = buffered,
                        onSeek = { fraction ->
                            player?.let { p ->
                                p.seekTo((fraction * p.duration).toLong())
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        gradientColors = gradientSliderColors
                    )
                }
            }

            spacer(4)
        }
    }




}


@Composable
fun ReelsBottomDetailsOverLay(
    reel1: PostPropertyData
    , onLikeClick : (Int) -> Unit
    , onSaveClick : (Int) -> Unit
    , onOptionClick : (Int) -> Unit
    ,onCommentClick : (Int) -> Unit
) {

    Row(
        verticalAlignment = Alignment.Bottom
        , horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier

                .weight(.8f)
                //.padding(horizontal = 16.dp)

        )
        {


            ListItem(
                overlineContent = {
                    Column() {
                        zText(
                            reel1.post_property.category, primaryWhite, 12, 0,
                            modifier = Modifier
                                .background(primaryWhite.copy(.4f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )

                        spacer(4)
                    }
                },
                headlineContent = {
                    Column() {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            SubcomposeAsyncImage(
                                model = reel1.profile_image,
                                "", modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(1.dp , primaryWhite , CircleShape)
                            ) { }

                            spacer(4)

                            zText(reel1.username, primaryWhite, 16, 1,)
                        }

                        spacer(4)
                    }
                },
                supportingContent = {
                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
//                                .weight(8f)
                                , verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start
                            ) {

                                zText(reel1.post_property.title, primaryWhite, 16, 0)

                                ExpandableMessage(reel1.post_property.short_description)

                                spacer(2)

                                zText(str(R.string.translate), primaryWhite, 14, 1)

                            }
                        }
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )

        }

        Column(
            modifier = Modifier
                .weight(.2f)
            , verticalArrangement = Arrangement.spacedBy(24.dp)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ReelsOverlayActionChips(
                icon = if (reel1.is_liked == 0)R.drawable.like_vd else R.drawable.like_filled,
                text = reel1.total_likes.toString(),
                onClick = {
                    onLikeClick(reel1.user_post_id)
                }
            )

            ReelsOverlayActionChips(
                icon = R.drawable.comment_vd,
                text = reel1.total_comments.toString(),
                onClick = {
                    onCommentClick(reel1.user_post_id)
                }
            )

            ReelsOverlayActionChips(
                icon = if (reel1.is_saved == 0) R.drawable.save_vd else R.drawable.save_filled,
                text = "",
                onClick = {
                    onSaveClick(reel1.user_post_id)
                }
            )

            ReelsOverlayActionChips(
                icon = R.drawable.more_horizontal,
                text = "",
                onClick = {
                    onOptionClick(reel1.user_post_id)
                    utils.open_ReelsOptionsBtm()
                }
            )

            spacer(24)

            Box(
                modifier = Modifier, contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.send_message), "",
                    modifier = Modifier.size(42.dp)
                )
            }

            spacer(8)
        }
    }

}



@Composable
fun ReelsOverlayActionChips(
    icon : Int,
    text : String
    ,onClick: () -> Unit
){
    Column(
        modifier = Modifier
            .shrinkClick {
                onClick()
            }
        , verticalArrangement = Arrangement.Center
        , horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(icon), "",
            colorFilter = ColorFilter.tint(primaryWhite)
            , modifier = Modifier.size(24.dp)
        )

        spacer(4)

        zText(text , primaryWhite , 12, 3)
    }
}



// ─────────────────────────────────────────────
//  Main Reels Screen
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelsScreen(
    homeVm: HomeAPIViewModel = koinViewModel(),
    sharedRepo: SharedRepository = koinInject(),
    appPreferences: AppPreferences = koinInject(),
    onBack: () -> Unit = {}
) {

    val network by rememberNetworkStatus()

    var intialLoad by remember { mutableStateOf(false) }



    if (network == NetworkStatus.Online) {
        // 🔥 First Load
        LaunchedEffect(Unit) {
            intialLoad = true
            homeVm.getReels(
                user_id = 1,
                user_post_id = 0
            )
        }
    }

    val commentState by utils.commentState.collectAsStateWithLifecycle()

    // 🔥 State
    val reels by sharedRepo.reelsItems.collectAsState()
    val isLoading by sharedRepo.isLoading.collectAsState()
    val isError by sharedRepo.error.collectAsState()

    val pagerState = rememberPagerState { reels.size }
    var isMuted by remember { mutableStateOf(false) }

    if (network == NetworkStatus.Online) {
        // 🚀 PAGINATION (BEST PRACTICE)
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .collect { page ->

                    if (
                        page >= reels.size - 2 &&
                        !isLoading &&
                        reels.isNotEmpty()
                    ) {
                        homeVm.getReels(
                            user_id = 1,
                            user_post_id = 0,
                            loadMore = true
                        )
                    }
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // no internet
        if (network == NetworkStatus.Offline){
            intialLoad = false
        }

        // api fail

        if (isError?.isNotEmpty() == true){
            intialLoad = false
        }



        // 🔥 MAIN CONTENT
        if (reels.isNotEmpty()) {
            intialLoad = false

            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->

                val reel = reels.getOrNull(pageIndex)

                reel?.let {

                    println("1234567890-qwertyuiop --- ${reel}")

                    ReelItemView(
                        reel = it,
                        isActive = pagerState.currentPage == pageIndex,
//                        isMuted = isMuted,

                        // 👉 Direct state updates (no callbacks needed)
                        onLike = {
                            id ->
                            if (network == NetworkStatus.Online) {
                                homeVm.postLike(
                                    user_id = 1,
                                    user_post_id = id,
                                    status = if (it.is_liked == 1) "2" else "1",
                                ) { result ->
                                    when (result) {

                                        is ResultHandler.Success -> {
                                            sharedRepo.toggleLike(id)
                                        }

                                        is ResultHandler.Error -> {

                                        }

                                        else -> {

                                        }

                                    }
                                }
                            }
                            else {
                                networkToast()
                            }

                        },
                        onComment = {
                            id -> utils.enable_Comment(id)
                        },
                        onSave = {
                                id ->
                            if (network == NetworkStatus.Online) {
                                homeVm.postSave(
                                    user_id = 1,
                                    user_post_id = id,
                                    status = if (it.is_liked == 1) "2" else "1",
                                ) { result ->
                                    when (result) {
                                        is ResultHandler.Success -> {
                                            sharedRepo.toggleSave(id)
                                        }

                                        is ResultHandler.Error -> {

                                        }

                                        else -> {}
                                    }
                                }
                            }
                            else {
                                networkToast()
                            }

                        },
                        onReport = {
                            sharedRepo.reportPost(it.user_post_id)
                        },
                        onOptions = {

                        },

//                        onMuteToggle = { isMuted = !isMuted },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }



        // 🔥 FIRST LOAD LOADER
        if (isLoading && reels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }



        // 🔥 PAGINATION LOADER (bottom feel)
        if (isLoading && reels.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                CircularProgressIndicator()
            }
        }



        // 🔥 EMPTY STATE
        if (!isLoading && reels.isEmpty() && !intialLoad ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No reels available",
                    color = Color.White
                )
            }
        }





////// comment
        if (commentState != 0) {

            CommentUI(
                postId = commentState,
                onDismiss = {
                    utils.disable_Comment()
                    // Resume active video after comment UI closes

                }
            )
        }
    }
}
