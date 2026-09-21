package com.alpha.selfemployment.Views.Notitifcation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.R
import com.alpha.selfemployment.Views.Notitifcation.di.viewModels.NotificationUIViewModels
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.zText
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationScreen(){
    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(R.drawable.left_arrow), "")

            zText("Notification", black1A, 24, 0)
        }

        NS_TopTabRow()
    }
}



@Composable
fun NS_TopTabRow(nsUIVm : NotificationUIViewModels = koinViewModel ()){


    val itemList by  nsUIVm.notificationTabRowItems.collectAsStateWithLifecycle()

    val selectedItem by nsUIVm.currentSeletedNotificationTabRowItem.collectAsStateWithLifecycle()


    val listState = rememberLazyListState()

    LaunchedEffect(selectedItem) {
        val index = selectedItem - 1
        val visibleItems = listState.layoutInfo.visibleItemsInfo

        val item = visibleItems.firstOrNull { it.index == index }

        if (item != null) {
            val center = listState.layoutInfo.viewportSize.width / 2
            val childCenter = item.offset + item.size / 2
            listState.animateScrollBy((childCenter - center).toFloat())
        } else {
            listState.animateScrollToItem(index)
        }
    }

    LazyRow(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(46.dp)
        , state = listState
    ) {
        itemsIndexed(itemList){ index , item ->

            Column(
                modifier = Modifier.shrinkClick{
                    nsUIVm.selectNotificationItem(index + 1)
                },
                verticalArrangement = Arrangement.Center
                , horizontalAlignment = Alignment.CenterHorizontally
            ) {
                zText(item.title ,   if (selectedItem == index +1) black1A else gray66 , 14 ,1)

                if (selectedItem == index +1){
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(16.dp)
                            .background(black1A)
                    ) { }
                }
            }

            spacer(16)

        }
    }
}