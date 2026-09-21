package com.alpha.selfemployment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alpha.selfemployment.Startup.ui.ChooseLanguageScreen
import com.alpha.selfemployment.Startup.ui.onboarding.OnBoardingScreen
import com.alpha.selfemployment.Views.Explore.ui.ExploreMainScreen
import com.alpha.selfemployment.Views.Home.ui.HomeScreen
import com.alpha.selfemployment.Views.Home.viewDetails.ui.ViewDetailsScreen
import com.alpha.selfemployment.Views.Message.FirebaseChat.ui.RealUI.MessagesScreen
import com.alpha.selfemployment.Views.Notitifcation.ui.NotificationScreen
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.ui.MyProfile
import com.alpha.selfemployment.Views.ProfileModule.OtherProfile.ui.OthersProfile
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.ContactSettings
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.Edit_Profile
import com.alpha.selfemployment.navigation.AppNavigatorHost
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.ui.theme.SelfLevelingTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            setSystemUIVisibility(true,this)


            SelfLevelingTheme {

                utils.activity = this

                AppNavigatorHost()



            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val configuration = newBase.resources.configuration
        configuration.fontScale = 1.0f
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }
}

// MyApp.kt
@Composable
fun LaunchScreen() {


    val items by utils.btmBarItems.collectAsStateWithLifecycle()
    val selectedItem by utils.selectedItem.collectAsStateWithLifecycle()


//    var selectedVideoUri = remember { mutableStateOf<Uri?>(null) }

    val navigator = LocalNavigator.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        navigator.navigate(Screen.UploadVideoScreen(it ))
    }


    Box() {

//        ViewportColorLazyColumn()

//        ParticleBurstAnimation()

//        GravityParticleBurst()

//        FanWithSpeedKnob()

        //FruitNinjaTest()

//        MainScreen() // shared tranistion

//        ChartsPreviewScreen()

//        OnBoardingScreen()


        Column(
            modifier = Modifier
                .fillMaxSize()
        )
        {
            Column(
                modifier = Modifier.weight(9f)
            ) {
                when (selectedItem) {
                    0 -> {
                        HomeScreen()
                    }

                    1 -> ExploreMainScreen()
                        //OthersProfile(2)
                        //StateTypesScreen()
                        //ReelsScreenPreload()
                        //BookPageCurlVideo()



                    3 -> {
                        MessagesScreen()
//                        val navigator = LocalNavigator.current
//                        Button(onClick = { navigator.navigate(Screen.DummyChatHome) }) {
//                            Text("Open Chat Prototype")
//                        }
                    }
                        //YoutubePreviewTrial0()

                    4 ->
                        MyProfile()
                }
            }


            /// OVERLAY BOTTOM BAR
            OverlayBottomBar(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { id ->
                    if (id != 2)
                    utils.onItemSelected(id)
                    else
                        utils.open_PostUploadBtm()
                },
                modifier = Modifier.weight(1f)
                //.align(Alignment.BottomCenter)
            )


        }



        PostUploadFormatBtmSheet(
            onArticleClick = {
                navigator.navigate(Screen.UploadArticleDetails)
            },
            onVideoClick = {

                launcher.launch("video/*")
            },
            onYoutubeVideoClick = {
                navigator.navigate(Screen.UploadYoutubeDetails)
            }
        )

        GlobalSnackbarHost()
    }

//        ChooseLanguageScreen()
//        MyProfile()
        //FF_Views()
//        Contact()
//        NotificationSettings()
        //EnquiriesScreen()
//        TextToSpeechPlayer()

//        CategoryDetailsScreen()


       /* val dummyReels = listOf(
            ReelItem(
                id = "1",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                username = "nature_vibes",
                userAvatar = "",
                caption = "Big Buck Bunny 🐰 so cute! #nature #animation",
                likeCount = 48200,
                commentCount = 1340,
                shareCount = 892,
                isLiked = true,
                audioTrack = "Original Audio"
            ),
            ReelItem(
                id = "2",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                username = "dream_world",
                userAvatar = "",
                caption = "Elephants Dream 🐘✨ #art #creative",
                likeCount = 120500,
                commentCount = 4200,
                shareCount = 3100,
                isLiked = false,
                audioTrack = "Ambient Mix"
            ),
            ReelItem(
                id = "3",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                username = "fire_starter",
                userAvatar = "",
                caption = "🔥 For Bigger Blazes #epic #fire",
                likeCount = 9800,
                commentCount = 430,
                shareCount = 210,
                isLiked = false,
                audioTrack = "Epic Beats"
            ),
            ReelItem(
                id = "4",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                username = "car_lover",
                userAvatar = "",
                caption = "Subaru on dirt roads 🚗💨 #cars #offroad",
                likeCount = 33000,
                commentCount = 870,
                shareCount = 540,
                isLiked = true,
                audioTrack = "Road Trip Vibes"
            )
        )*/

//
//        ReelsScreen(
//            reels = dummyReels,
//            onLike = { reelId ->
//                println("Liked reel: $reelId")
//            },
//            onComment = { reelId ->
//                println("Comment on: $reelId")
//            },
//            onShare = { reelId ->
//                println("Share reel: $reelId")
//            },
//            onFollow = { reelId ->
//                println("Follow user of reel: $reelId")
//            },
//            onBack = {
//               // finish() // or navController.popBackStack()
//            }
//        )

        //LocalizationPreviewScreen()

//        YoutubePreviewTrial()
//    }
}