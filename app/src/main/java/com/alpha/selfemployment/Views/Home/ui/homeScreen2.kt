package com.alpha.selfemployment.Views.Home.ui
//
//import androidx.compose.animation.core.Animatable
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.zIndex
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.alpha.selfemployment.HomeScreenFlow
//import com.alpha.selfemployment.R
//import com.alpha.selfemployment.Views.Home.Videos.ui.ReelsScreen
//import com.alpha.selfemployment.shrinkClick
//import com.alpha.selfemployment.ui.theme.primaryBlack
//import com.alpha.selfemployment.utils
//import com.alpha.selfemployment.zText
//import kotlinx.coroutines.launch
//import kotlin.math.abs
//
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.pager.VerticalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.material3.*
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import coil.compose.rememberAsyncImagePainter
//import com.alpha.selfemployment.NetworkStatus
//import com.alpha.selfemployment.ReelsOptionBtmSheet
//import com.alpha.selfemployment.ReportBtmSheet
//import com.alpha.selfemployment.ResultHandler
//import com.alpha.selfemployment.Views.CommonView.CommentUI
//import com.alpha.selfemployment.Views.Home.Videos.di.HomeAPIViewModel
//import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
//import com.alpha.selfemployment.Views.Home.Videos.ui.ReelsBottomDetailsOverLay
//import com.alpha.selfemployment.Views.SharedRepository
//import com.alpha.selfemployment.YoutubeReelsPlayerBtm
//import com.alpha.selfemployment.navigation.LocalNavigator
//import com.alpha.selfemployment.navigation.Screen
//import com.alpha.selfemployment.networkToast
//import com.alpha.selfemployment.rememberNetworkStatus
//import com.alpha.selfemployment.spacer
//import com.alpha.selfemployment.str
//import com.alpha.selfemployment.ui.theme.primaryWhite
//import org.koin.androidx.compose.koinViewModel
//import org.koin.compose.koinInject
//
//@Composable
//fun HomeScreen() {
//
//    val selectedTab by utils.homeTabFlow.collectAsStateWithLifecycle()
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(primaryBlack)
//    ) {
//
//        /// MAIN CONTENT
//        Box(
//            modifier = Modifier.fillMaxSize()
//        )
//        {
//            when (selectedTab) {
//                HomeScreenFlow.Videos -> {
//                    ReelsScreen(
//                        onBack = {}
//                    )
//                }
//
//                HomeScreenFlow.Articles -> {
//                    BlogPager()
//                }
//            }
//        }
//
//        /// TOP BAR (OVERLAY)
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(72.dp)
//                .align(Alignment.TopCenter)
//                .background( brush = Brush.radialGradient(
//                    colors = listOf(
//                        Color.White.copy(alpha = 0.3f),
//                        Color.White.copy(alpha = 0.1f),
//                        Color.White.copy(alpha = 0.05f)
//                    )
//                ))
//                .zIndex(1f)
//        )
//        {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight()
//                    .padding(horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            )
//            {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(16.dp)
//                )
//                {
//                    val isVideos = selectedTab == HomeScreenFlow.Videos
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.shrinkClick {
//                            utils.set_HomeTabFlow(HomeScreenFlow.Videos)
//                        }
//                    )
//                    {
//                        zText(
//                            str(R.string.videos),
//                            if (isVideos) primaryWhite else primaryWhite.copy(.2f),
//                            16,
//                            0
//                        )
//                        spacer(2)
//                        if (isVideos) {
//                            Box(
//                                modifier = Modifier.width(32.dp).height(4.dp)
//                                    .background(primaryWhite)
//                            )
//                        }
//                    }
//
//                    val isArticles = selectedTab == HomeScreenFlow.Articles
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.shrinkClick {
//                            utils.set_HomeTabFlow(HomeScreenFlow.Articles)
//                        }
//                    )
//                    {
//                        zText(
//                            str(R.string.articles),
//                            if (isArticles) primaryWhite else primaryWhite.copy(.2f),
//                            16,
//                            0
//                        )
//                        spacer(2)
//                        if (isArticles) {
//                            Box(
//                                modifier = Modifier.width(32.dp).height(4.dp)
//                                    .background(primaryWhite)
//                            )
//                        }
//                    }
//                }
//
//                Image(
//                    painter = painterResource(R.drawable.notification_icon),
//                    contentDescription = "",
//                    modifier = Modifier.size(28.dp)
//                )
//            }
//
//        }
//
//        }
//
//    ReelsOptionBtmSheet(
//        onNotInterestClick = {},
//        onReportClick = {
//            utils.invokeReportBtm()
//        }
//    )
//
//
//    YoutubeReelsPlayerBtm()
//
//    ReportBtmSheet()
//
//
//}
//
//
//@Composable
//fun BlogPager(
//    homeVm: HomeAPIViewModel = koinViewModel(),
//    sharedRepo: SharedRepository = koinInject(),
//){
//
//   val network by rememberNetworkStatus()
//
//    var intialLoad by remember { mutableStateOf(false) }
//
//
//    if (network == NetworkStatus.Online) {
//        // 🔥 First Load
//        LaunchedEffect(Unit) {
//            intialLoad = true
//            homeVm.getArticles(
//                user_id = 1,
//                user_post_id = 0
//            )
//        }
//    }
//
//    // 🔥 State
//    val articles by sharedRepo.articlesItems.collectAsState()
//
//    val isLoading by sharedRepo.isLoading.collectAsState()
//
//    val isError by sharedRepo.error.collectAsState()
//
//
//
//    val pagerState = rememberPagerState (pageCount = {articles.size} )
//
//    val navigator = LocalNavigator.current
//
//    val commentState by utils.commentState.collectAsStateWithLifecycle()
//
//
//    // 🚀 PAGINATION (BEST PRACTICE)
//    if (network == NetworkStatus.Online) {
//        LaunchedEffect(pagerState) {
//            snapshotFlow { pagerState.currentPage }
//                .collect { page ->
//
//                    if (
//                        page >= articles.size - 2 &&
//                        !isLoading &&
//                        articles.isNotEmpty()
//                    ) {
//                        homeVm.getArticles(
//                            user_id = 1,
//                            user_post_id = 0,
//                            loadMore = true
//                        )
//                    }
//                }
//        }
//
//    }
//
//
//
//    Box(){
//
//        // no internet
//        if (network == NetworkStatus.Offline){
//            intialLoad = false
//        }
//
//        // api fail
//        if (isError?.isNotEmpty() == true){
//            intialLoad = false
//        }
//
//
//
//        // 🔥 FIRST LOAD LOADER
//        if (isLoading && articles.isEmpty()) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//
//
//
//        // 🔥 PAGINATION LOADER (bottom feel)
//        if (isLoading && articles.isNotEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(bottom = 50.dp),
//                contentAlignment = Alignment.BottomCenter
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//
//
//
//        // 🔥 EMPTY STATE
//        if (!isLoading && articles.isEmpty() && !intialLoad ) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "No reels available",
//                    color = Color.White
//                )
//            }
//        }
//
//
//        if (articles.isNotEmpty()){
//            intialLoad = false
//
//            VerticalPager (
//                state = pagerState
//            )
//            {
//                    pageIndex ->
//
//                val reel = articles.getOrNull(pageIndex)
//
//                reel?.let {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.Black)
//                    )
//                    {
//
//                        BlogReelsContent(
//                            reel = it,
//                        )
//
//                        // ── Bottom info + slider ───────────────────────
//                        Column(
//                            modifier = Modifier
//                                .zIndex(3f)
//                                .align(Alignment.BottomCenter)
//                                .fillMaxWidth()
//                        )
//                        {
//                            // Username & caption
//
//                            ReelsBottomDetailsOverLay(
//                                reel,
//                                onLikeClick = {
//                                        id ->
//                                    if (network == NetworkStatus.Online) {
//                                        homeVm.postLike(
//                                            user_id = 1,
//                                            user_post_id = id,
//                                            status = if (it.is_liked == 1) "2" else "1",
//                                        ) { result ->
//                                            when (result) {
//                                                is ResultHandler.Success -> {
//                                                    sharedRepo.toggleLikeArticles(id)
//                                                }
//
//                                                is ResultHandler.Error -> {
//
//                                                }
//
//                                                else -> {}
//                                            }
//                                        }
//                                    }
//                                    else {
//                                        networkToast()
//                                    }
//
//                                },
//                                onSaveClick = {
//                                        id ->
//                                    if (network == NetworkStatus.Online) {
//                                        homeVm.postSave(
//                                            user_id = 1,
//                                            user_post_id = id,
//                                            status = if (it.is_liked == 1) "2" else "1",
//                                        ) { result ->
//                                            when (result) {
//                                                is ResultHandler.Success -> {
//                                                    sharedRepo.toggleSaveArticles(id)
//                                                }
//
//                                                is ResultHandler.Error -> {
//
//                                                }
//
//                                                else -> {}
//                                            }
//                                        }
//                                    }
//                                    else {
//                                        networkToast()
//                                    }
//
//                                },
//                                onCommentClick = {
//                                    id -> utils.enable_Comment(id)
//                                }
//                            )
//
//                            // ── View Details Button ────────────────────────
//
//                            spacer(2)
//
//                            Box(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp)
//                                    .fillMaxWidth()
//                                    .height(42.dp)
//                                    .background(Color.Transparent, RoundedCornerShape(8.dp))
//                                    .border(1.dp, primaryWhite, RoundedCornerShape(8.dp))
//                                    .shrinkClick {
//                                        navigator.navigate(Screen.ViewDetailsScreen(reel.user_post_id))
//                                    }
//                                , contentAlignment = Alignment.Center
//                            ) {
//                                zText(str(R.string.view_business_details), primaryWhite, 14, 0)
//                            }
//
//
//                            spacer(12)
//
//                        }
//                    }
//                }
//
//
//            }
//        }
//    }
//
//
//    if (commentState != 0) {
//
//        CommentUI(
//            postId = commentState,
//            onDismiss = {
//                utils.disable_Comment()
//                // Resume active video after comment UI closes
//
//            }
//        )
//    }
//
//
////    ReelsOptionBtmSheet(
////        onNotInterestClick = {},
////        onReportClick = {
////            utils.invokeReportBtm()
////        }
////    )
//
//
//
//    ReportBtmSheet()
//}
//
//
//@Composable
//fun BlogReelsContent(
//    reel: PostPropertyData,
//) {
//
//    val allCards = reel.post_property.images.filterNotNull()
//
//    if (allCards.isEmpty()) return
//
//    var currentIndex by remember { mutableStateOf(0) }
//    val offsetX = remember { Animatable(0f) }
//    val scope = rememberCoroutineScope()
//
//    val rotation by remember { derivedStateOf { offsetX.value / 22f } }
//    val visible = (0 until minOf(3, allCards.size)).map {
//        allCards[(currentIndex + it) % allCards.size]
//    }
//
//    val cardWidth = 340.dp
//    val stackShrinkPerLayer = 16.dp
//    val stackPeekY = 14.dp
//
//    Box(
//        modifier = Modifier
//            .padding(bottom = 108.dp)
//            .fillMaxSize()
//            .background(Color(0xFF0a0a0a)),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier.fillMaxSize()
//        ) {
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            Box(
//                modifier = Modifier
//                    .width(cardWidth)
//                    .height(360.dp),
//                contentAlignment = Alignment.BottomCenter
//            ) {
//
//                visible.reversed().forEachIndexed { revIdx, image ->
//
//                    val idx = visible.size - 1 - revIdx
//                    val isTop = idx == 0
//
//                    val horizontalInset = stackShrinkPerLayer * idx
//                    val peekOffsetY = -(stackPeekY * idx)
//                    val stackTilt = when (idx) {
//                        1 -> 2.5f
//                        2 -> 4.5f
//                        else -> 0f
//                    }
//
//                    Card(
//                        modifier = Modifier
//                            .width(cardWidth - horizontalInset * 2)
//                            .height(440.dp)
//                            .offset(
//                                x = if (isTop) offsetX.value.dp else 0.dp,
//                                y = if (isTop) 0.dp else peekOffsetY
//                            )
//                            .graphicsLayer {
//                                rotationZ = if (isTop) rotation else stackTilt
//                                shadowElevation = if (isTop) 60f else (30f - idx * 10f)
//                            }
//                            .then(
//                                if (isTop) Modifier.pointerInput(currentIndex) {
//                                    detectDragGestures(
//                                        onDrag = { change, drag ->
//                                            change.consume()
//                                            scope.launch {
//                                                offsetX.snapTo(offsetX.value + drag.x)
//                                            }
//                                        },
//                                        onDragEnd = {
//                                            scope.launch {
//                                                if (abs(offsetX.value) > 250f) {
//
//                                                    offsetX.animateTo(
//                                                        if (offsetX.value > 0) 1400f else -1400f,
//                                                        animationSpec = tween(300)
//                                                    )
//
//                                                    currentIndex =
//                                                        (currentIndex + 1) % allCards.size
//
//                                                    offsetX.snapTo(0f)
//
//                                                } else {
//                                                    offsetX.animateTo(
//                                                        0f,
//                                                        animationSpec = spring(
//                                                            dampingRatio = Spring.DampingRatioMediumBouncy,
//                                                            stiffness = Spring.StiffnessMediumLow
//                                                        )
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    )
//                                } else Modifier
//                            ),
//                        shape = RoundedCornerShape(24.dp),
//                        elevation = CardDefaults.cardElevation(0.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
//                    ) {
//
//                        // 🔥 IMAGE
//                        Image(
//                            painter = rememberAsyncImagePainter(image.articles_photo),
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(28.dp))
//
//            // ✅ INDICATOR (ALL WHITE)
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(6.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                allCards.forEachIndexed { i, _ ->
//
//                    val isActive = i == currentIndex
//
//                    val dotWidth by animateDpAsState(
//                        targetValue = if (isActive) 20.dp else 6.dp,
//                        label = ""
//                    )
//
//                    Box(
//                        modifier = Modifier
//                            .width(dotWidth)
//                            .height(6.dp)
//                            .clip(RoundedCornerShape(3.dp))
//                            .background(Color.White) // ✅ always white
//                    )
//                }
//            }
//        }
//    }
//}