package com.alpha.selfemployment.Views.ProfileModule.Settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.GlobalSnackbar
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.R
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FFApiViewModel
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.di.ProfileAPIViewModel
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsApiViewModel
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.networkToast
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray96
import com.alpha.selfemployment.ui.theme.grayE8
import com.alpha.selfemployment.ui.theme.brandBlue
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun  BlockListScreen(
    settingsApiViewModel: SettingsApiViewModel = koinViewModel(),
    sharedRepository: SharedRepository = koinInject(),
    appPrefs : AppPreferences = koinInject(),
    profileApi: ProfileAPIViewModel = koinViewModel()
){


    val listState = rememberLazyListState()

    val  network by rememberNetworkStatus()

    val isLoading by sharedRepository.isLoading.collectAsStateWithLifecycle()

    val isError by sharedRepository.error.collectAsStateWithLifecycle()

    val blockList by sharedRepository.blockList.collectAsStateWithLifecycle()

    if (network == NetworkStatus.Online) {

        LaunchedEffect(Unit) {
            settingsApiViewModel.getBlockedList(
                appPrefs.getUserId()
            )
        }
    }

    if (network == NetworkStatus.Online) {
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
                            blockList.isNotEmpty()

                if (shouldLoadMore) {
                    settingsApiViewModel.getBlockedList(
                        user_id = appPrefs.getUserId(),
                        loadMore = true
                    )
                }
            }
        }
    }





    Column(
        modifier = Modifier
            .padding(start = 16.dp , end = 16.dp , top = rememberNotchHeightDp().value)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            , contentAlignment = Alignment.Center
        ){
            Image(painter = painterResource(R.drawable.left_arrow) , "",
                modifier = Modifier.align(Alignment.CenterStart) )

            zText(
                str(R.string.my_blocklist), black1A , 24, 0,
                modifier = Modifier.align(Alignment.Center))
        }


        LazyColumn (
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(9f)
        ) {

            if (network == NetworkStatus.Offline){
                ///
            }

            if (isLoading){

                item {
                    CircularWavyProgressIndicator()
                }
            }

            if (isError?.isNotEmpty() == true){

            }

            if (blockList.isEmpty()){

            }

            if (blockList.isNotEmpty()){
                itemsIndexed(blockList){
                    index , item ->

                    Column() {
                        ListItem(
                            overlineContent = {
                                zText(item.name, black1A, 16, 1)
                            },
                            headlineContent = {
                                zText(item.name, gray96, 12, 2)
                            },
                            leadingContent = {
                                SubcomposeAsyncImage(
                                    model = item.profile_image,
                                    "",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape), loading = {

                                    }, error = {}
                                )
                            },
                            trailingContent = {
                                Row(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .clip(RoundedCornerShape(2.dp))
                                        .border(1.dp, grayE8, RoundedCornerShape(2.dp))
                                        .shrinkClick {
                                            if (network == NetworkStatus.Online) {
                                                profileApi.userBlockOrUnblock(
                                                    user_id = appPrefs.getUserId(),
                                                    blocker_id = item.user_id,
                                                    status = "2",
                                                ) { res ->
                                                    when (res) {
                                                        is ResultHandler.Success -> {
                                                            sharedRepository.unblockRemove(item.user_id)
                                                        }

                                                        is ResultHandler.Error -> {}
                                                        else -> {}
                                                    }
                                                }
                                            }
                                            else {
                                                GlobalSnackbar.show(id = R.string.noInternet)
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)

                                    , verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(painter = painterResource(R.drawable.block_ivon), "")

                                    spacer(4)

                                    zText("Unblock", brandBlue, 12, 1)
                                }
                            }
                            , colors = ListItemDefaults.colors(
                                containerColor = primaryWhite
                            )
                        )

                        HorizontalDivider()
                    }
                }
            }
        }





    }
}