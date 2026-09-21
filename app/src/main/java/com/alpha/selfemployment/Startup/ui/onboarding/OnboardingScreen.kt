package com.alpha.selfemployment.Startup.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.R
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


data class OnboardingScreenItems(
    var id : Int,
    var title : String ,
    var description  :String ,
    var image : Int
)


@Composable
fun OnBoardingScreen(
    appPrefs : AppPreferences = koinInject()
){


    val navigator = LocalNavigator.current


    val scope = rememberCoroutineScope()

    var onboardingItems = listOf(
        OnboardingScreenItems(
            0,
            "Find Your Work",
            "See many types of self-employment ideas. Choose the one that fits you best.",
            R.drawable.onboardingone
        ),
        OnboardingScreenItems(
            0,
            "Talk with Others",
            "Ask questions and share your thoughts in the forum. Learn from other people like you.",
            R.drawable.onboardingtwo
        ),
        OnboardingScreenItems(
            0,
            "Send Enquiries",
            "Want to know more about a service or opportunity? Send an enquiry and get the details you need.",
            R.drawable.onboardingthree
        ),
    )

    val pagerState = rememberPagerState (pageCount = { onboardingItems.size } )

    HorizontalPager(
        pagerState
        ,modifier = Modifier.fillMaxSize()
        , userScrollEnabled = false

    ) {
        page ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = rememberNotchHeightDp().value)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                , verticalAlignment = Alignment.CenterVertically
                , horizontalArrangement = Arrangement.SpaceBetween
            ) {
                zText(onboardingItems[page].title , black1A , 24 , 0)

                if (pagerState.currentPage < 2 ) {

                    zText(
                        "Skip", gray66, 14, 1,
                        modifier = Modifier.shrinkClick {
                            scope.launch {
//                                pagerState.animateScrollToPage(3)
                                navigator.clearAndNavigate(Screen.CredentialScreen)
                                appPrefs.saveOnboardingCompleted(true)
                            }
                        })
                }
            }

            zText(onboardingItems[page].description , gray48 , 14 , 2,
                modifier = Modifier .padding(horizontal = 16.dp))


            Image(painter = painterResource(onboardingItems[page].image)
                , ""
                , modifier = Modifier
                    .fillMaxWidth()
                    .weight(8f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                , contentAlignment = Alignment.Center
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .fillMaxHeight(.8f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(black1A)
                        .shrinkClick {
                            if (pagerState.currentPage < 2){
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                            else {
                                println("123erfge1w2edfrvrew12derewdwe")

                                navigator.clearAndNavigate(Screen.CredentialScreen)

                                appPrefs.saveOnboardingCompleted(true)
                            }
                        }
                    , contentAlignment = Alignment.Center
                ){
                   zText(if (pagerState.currentPage < 2)"NEXT" else "GET STARTED" , primaryWhite ,14 , 0 )
                }
            }
        }
    }
}