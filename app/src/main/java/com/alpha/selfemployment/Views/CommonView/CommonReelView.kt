package com.alpha.selfemployment.Views.CommonView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.Home.Videos.di.HomeAPIViewModel
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import com.alpha.selfemployment.Views.Home.Videos.ui.ReelItemView
import com.alpha.selfemployment.Views.Home.Videos.ui.ReelsBottomDetailsOverLay
import com.alpha.selfemployment.Views.Home.ui.BlogReelsContent
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.networkToast
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun CommonReelView(
    sharedRepository: SharedRepository = koinInject()
){


    val data by sharedRepository.postCommonItems.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState (pageCount = {data.size })



    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
            , contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(R.drawable.reels_back_overlay) , "",
                modifier = Modifier.align(Alignment.CenterStart))
        }

        VerticalPager(
            state = pagerState
        ) {
            page ->
            if (data[page].post_property.post_type == "1" || data[page].post_property.post_type == "2") {
                CommonReelVideoView(data[page] , pagerState ,page)
            }
            else {
                CommonReelImageView(data[page])
            }
        }

    }
}

@Composable
fun CommonReelVideoView(
    data: PostPropertyData,
    pagerState: PagerState,
    pageIndex: Int ,
    homeVm: HomeAPIViewModel = koinViewModel(),
    sharedRepo: SharedRepository = koinInject()
) {

    val network by rememberNetworkStatus()

    ReelItemView(
        reel = data,
        isActive = pagerState.currentPage == pageIndex,
//                        isMuted = isMuted,
        // 👉 Direct state updates (no callbacks needed)
        onLike = {
                id ->
            if (network == NetworkStatus.Online) {
                homeVm.postLike(
                    user_id = 1,
                    user_post_id = id,
                    status = if (data.is_liked == 1) "2" else "1",
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
        onComment = { /* optional */ },
        onSave = {
                id ->
            if (network == NetworkStatus.Online) {
                homeVm.postSave(
                    user_id = 1,
                    user_post_id = id,
                    status = if (data.is_liked == 1) "2" else "1",
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
            sharedRepo.reportPost(data.user_post_id)
        },
        onOptions = { /* no options sheet in common reel view */ },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CommonReelImageView(
    data: PostPropertyData,
    homeVm: HomeAPIViewModel = koinViewModel(),
    sharedRepo: SharedRepository = koinInject()
) {

    val network by rememberNetworkStatus()

    val navigator = LocalNavigator.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
    {
        println("data -- ${data.post_property.images}")

        BlogReelsContent(
            reel = data,
        )

        // ── Bottom info + slider ───────────────────────
        Column(
            modifier = Modifier
                .zIndex(3f)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
        {
            // Username & caption

            ReelsBottomDetailsOverLay(data,
                onLikeClick = {
                        id ->
                    if (network == NetworkStatus.Online) {
                        homeVm.postLike(
                            user_id = 1,
                            user_post_id = id,
                            status = if (data.is_liked == 1) "2" else "1",
                        ) { result ->
                            when (result) {
                                is ResultHandler.Success -> {
                                    sharedRepo.toggleLikeArticles(id)
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

                onSaveClick = {
                        id ->
                    if (network == NetworkStatus.Online) {
                        homeVm.postSave(
                            user_id = 1,
                            user_post_id = id,
                            status = if (data.is_liked == 1) "2" else "1",
                        ) { result ->
                            when (result) {
                                is ResultHandler.Success -> {
                                    sharedRepo.toggleSaveArticles(id)
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
                onCommentClick ={

                },
                onOptionClick = { /* no options sheet in common reel view */ }
            )

            // ── View Details Button ────────────────────────

            spacer(2)

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color.Transparent, RoundedCornerShape(8.dp))
                    .border(1.dp, primaryWhite, RoundedCornerShape(8.dp))
                    .shrinkClick {
                        navigator.navigate(Screen.ViewDetailsScreen(data.user_post_id))
                    }
                , contentAlignment = Alignment.Center
            ) {
                zText(str(R.string.view_business_details), primaryWhite, 14, 0)
            }


            spacer(12)

        }
    }
}