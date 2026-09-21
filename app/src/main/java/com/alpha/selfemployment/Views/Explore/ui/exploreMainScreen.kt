package com.alpha.selfemployment.Views.Explore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.ExploreSearchField
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.Explore.di.ExploreAPIViewModel
import com.alpha.selfemployment.Views.Explore.di.ExploreUIViewModel
import com.alpha.selfemployment.Views.Explore.domain.model.SearchHistoryResponse
import com.alpha.selfemployment.Views.Explore.domain.model.SearchHistoryResponseData
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.ui.resolveStaticThumbnail
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.createVideoThumbnail
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.grayE8
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ExploreMainScreen(
    exploreAPIVm: ExploreAPIViewModel = koinViewModel(),
    appPrefs: AppPreferences = koinInject()
) {
    var searchHistoryList by remember { mutableStateOf<List<SearchHistoryResponseData>>(emptyList()) }
    var mostPopularList by remember { mutableStateOf<List<PostPropertyData>>(emptyList()) }

    LaunchedEffect(Unit) {
        exploreAPIVm.searchHistory(appPrefs.getUserId()) { result ->
            when (result) {
                is ResultHandler.Success -> searchHistoryList = result.data.data
                else -> Unit
            }
        }
    }

    LaunchedEffect(Unit) {
        exploreAPIVm.exploreMostPopular(appPrefs.getUserId()) { result ->
            when (result) {
                is ResultHandler.Success -> mostPopularList = result.data.data
                else -> Unit
            }
        }
    }

    val categoryList = (1..10).toList()

    Column(modifier = Modifier.fillMaxSize().background(primaryWhite))
    {
        // Fixed header
        Box(
            modifier = Modifier
                .padding(vertical = rememberNotchHeightDp().value)
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            zText(str(R.string.explore), black1A, 24, 0)
        }

        ExploreSearchField(modifier = Modifier.fillMaxWidth().height(48.dp))

        spacer(4)


        val listState = rememberLazyListState()



        LaunchedEffect(Unit) {
            listState.scrollToItem(0)  // force scroll to top on first load
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            item(key = "search_history") {
                Box(modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 1.dp)) {
                    SearchHistoryContent(
                        list = searchHistoryList,
                        onClearAll = { },
                        onDeleteItem = { }
                    )
                }
            }

            item(key = "most_popular") {
                Box(modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 1.dp)) {
                    ExploreMostPopularContent(list = mostPopularList)
                }
            }

            item(key = "categories_header") {
                Column {
                    spacer(6)
                    zText(str(R.string.all_categories), black1A, 18, 0)
                    spacer(6)
                }
            }

            items(categoryList.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { _ ->
                        ExploreAllCategories(modifier = Modifier.weight(1f))
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExploreMostPopularContent(
    list: List<PostPropertyData>
    ,sharedRepository: SharedRepository = koinInject()
) {
    val context = LocalContext.current

    val navigator = LocalNavigator.current

    if (list.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            spacer(6)
            zText(str(R.string.most_popular), black1A, 18, 0)
            spacer(6)

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(186.dp)
            ) {
                itemsIndexed(list) { _, item ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .width(156.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Yellow)
                            .shrinkClick {
                                sharedRepository.setPostCommonItems(list , true)
                                navigator.navigate(Screen.CommonReelView)
                            }
                    )
                    {
                        val imageModel = when (item.post_property.post_type) {
                            "1" -> ImageRequest.Builder(context)
                                .data(item.post_property.video)
                                .videoFrameMillis(1000)
                                .crossfade(true)
                                .build()
                            "3" -> item.post_property.images.firstOrNull()?.articles_photo
                            "2" -> item.post_property.video
                            else -> resolveStaticThumbnail(item)
                        }

                        SubcomposeAsyncImage(
                            model = imageModel,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            SubcomposeAsyncImageContent()
                        }

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                SubcomposeAsyncImage(
                                    model = item.profile_image,
                                    "",
                                    modifier = Modifier
                                        .size(24.dp)
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


@Composable
fun SearchHistoryContent(
    exploreUIVm: ExploreUIViewModel = koinViewModel(),
    list: List<SearchHistoryResponseData>,
    onClearAll: () -> Unit,
    onDeleteItem: (Int) -> Unit
) {
    val navigator = LocalNavigator.current

    if (list.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            spacer(4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                zText("Recent Search", black1A, 18, 0)
                zText(
                    "clear all", gray66, 12, 0,
                    modifier = Modifier.shrinkClick { onClearAll() }
                )
            }

            list.forEach { item ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .shrinkClick {
                                exploreUIVm.enterText(item.search_text)
                                navigator.navigate(Screen.ExploreSearchPropertyResults(item.search_text))
                            }
                        , verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.searchhistory_icon),
                                "",
                                modifier = Modifier.size(14.dp)
                            )
                            spacer(4)
                            Text(text = item.search_text)
                        }
                        Image(
                            painter = painterResource(R.drawable.clear),
                            "",
                            modifier = Modifier
                                .size(14.dp)
                                .shrinkClick { onDeleteItem(item.search_id) }
                        )
                    }
                    spacer(2)
                    HorizontalDivider()
                }
            }
        }
    }
}


@Composable
fun ExploreAllCategories(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .height(186.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Red)
            .border(1.dp, grayE8, RoundedCornerShape(6.dp))
    ) {
        SubcomposeAsyncImage(
            model = R.drawable.verified,
            "",
            modifier = Modifier
                .weight(8f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(Color.Green)
        ) { }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .background(primaryWhite)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            zText("category", gray48, 14, 2)
        }
    }
}