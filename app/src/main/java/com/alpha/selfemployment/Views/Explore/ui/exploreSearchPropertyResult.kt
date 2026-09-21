package com.alpha.selfemployment.Views.Explore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.R
import com.alpha.selfemployment.Views.Explore.di.ExploreAPIViewModel
import com.alpha.selfemployment.Views.Explore.di.ExploreUIViewModel
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.ui.rememberVideoFrameImageLoader
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.ui.resolveStaticThumbnail
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExploreSearchPropertyResults(
    searchtext : String ,
    exploreUIVm: ExploreUIViewModel = koinViewModel(),
    exploreAPIVm: ExploreAPIViewModel = koinViewModel(),
    sharedRepo: SharedRepository = koinInject(),
    appPrefs: AppPreferences = koinInject(),
){
    val network by rememberNetworkStatus()


    val searchResult by sharedRepo.postCommonItems.collectAsStateWithLifecycle()

    val isLoading by sharedRepo.isLoading.collectAsStateWithLifecycle()
    val isError by sharedRepo.error.collectAsStateWithLifecycle()

    val listState = rememberLazyGridState()

    val context = LocalContext.current

    val navigator = LocalNavigator.current

    val videoImageLoader = rememberVideoFrameImageLoader(context)



    LaunchedEffect(Unit) {
        exploreAPIVm.getSearchProperty(
            user_id = appPrefs.getUserId(),
            search_text = searchtext
        )
    }

    // =========================
    LaunchedEffect(listState) {
        if (network != NetworkStatus.Online) return@LaunchedEffect

        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItemIndex to totalItemsCount
        }.collect { (lastVisibleItemIndex, totalItemsCount) ->

            val shouldLoadMore =
                lastVisibleItemIndex >= totalItemsCount - 3 &&
                        !isLoading &&
                        searchResult.isNotEmpty()

            if (shouldLoadMore) {
                exploreAPIVm.getSearchProperty(
                    user_id = appPrefs.getUserId(),
                    search_text = searchtext,
                    loadMore = true
                )
            }
        }
    }


    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
            , contentAlignment = Alignment.Center
        ){
            Image(painter = painterResource(R.drawable.left_arrow) , "",
                modifier = Modifier.align(Alignment.CenterStart).shrinkClick {
                    navigator.pop()
                })

            zText("Explore" , black1A , 20 , 0,
                modifier = Modifier.align(Alignment.Center))
        }


        Box(
            modifier = Modifier
                .weight(8.5f)
                .fillMaxWidth()
            , contentAlignment = Alignment.CenterStart
        ){
            if (isLoading){
                CircularWavyProgressIndicator()
            }

            if (isError?.isNotEmpty() == true){

            }

            if (searchResult.isNotEmpty()) {
                LazyVerticalGrid(
                    GridCells.Fixed(2)
                )
                {
                    itemsIndexed(searchResult) { index, item ->

                        val staticThumbnail = resolveStaticThumbnail(item)


                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(186.dp)
                                .width(156.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Yellow)
                                .shrinkClick{
                                    navigator.navigate(Screen.CommonReelView)
                                }
                        )
                        {

                            when (item.post_property.post_type){
                                "1" -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(item.post_property.video)
                                            .videoFrameMillis(1000) // frame at 1 second
                                            .crossfade(true)
                                            .build(),
                                        imageLoader = videoImageLoader,
                                        contentDescription = "Video Thumbnail",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                else -> {
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
                            }


                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    SubcomposeAsyncImage(
                                        model = R.drawable.verified,
                                        "", modifier = Modifier.size(18.dp)
                                            .clip(CircleShape)
                                            .background(primaryWhite)
                                    ) { }

                                    spacer(2)

                                    zText(item.username, primaryWhite, 12, 1)
                                }


                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.like_vd), "",
                                        colorFilter = ColorFilter.tint(primaryWhite)
                                    )

                                    spacer(4)

                                    zText(item.total_likes.toString(), primaryWhite, 10, 3)
                                }
                            }
                        }
                    }
                }
            }
        }



    }
}