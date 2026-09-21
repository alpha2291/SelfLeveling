package com.alpha.selfemployment.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.alpha.selfemployment.AppLocaleProvider
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.LaunchScreen
import com.alpha.selfemployment.Startup.ui.ChooseLanguageScreen
import com.alpha.selfemployment.Startup.ui.CredentialScreen
import com.alpha.selfemployment.Startup.ui.onboarding.OnBoardingScreen
import com.alpha.selfemployment.Views.CommonView.CommonReelView
import com.alpha.selfemployment.Views.Explore.ui.ExploreSearchPropertyResults
import com.alpha.selfemployment.Views.Home.viewDetails.ui.ViewDetailsScreen
import com.alpha.selfemployment.Views.Message.FirebaseChat.ui.RealUI.ConversationScreen
import com.alpha.selfemployment.Views.Message.FirebaseChat.ui.RealUI.MessagesScreen
import com.alpha.selfemployment.Views.Message.FirebaseChat.ui.RealUI.PostChatUsersScreen
import com.alpha.selfemployment.Views.PostUpload.ui.ArticleUploadPreview
import com.alpha.selfemployment.Views.PostUpload.ui.UploadArticleDetails
import com.alpha.selfemployment.Views.PostUpload.ui.UploadVideoDetails
import com.alpha.selfemployment.Views.PostUpload.ui.UploadVideoScreen
import com.alpha.selfemployment.Views.PostUpload.ui.UploadYoutubeDetails
import com.alpha.selfemployment.Views.PostUpload.ui.VideoTrimScreen
import com.alpha.selfemployment.Views.PostUpload.ui.VideoUploadPreview
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.FF_Profile_UI
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.ui.MyProfile
import com.alpha.selfemployment.Views.ProfileModule.OtherProfile.ui.OthersProfile
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.AccountSettings
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.BlockListScreen
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.ContactSettings
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.DeleteAccount
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.Edit_Profile
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.MyInterestScreen
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.NotificationSettings
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.Settings
import com.alpha.selfemployment.YoutubePreviewTrial
import org.koin.compose.koinInject

@Composable
fun AppNavigatorHost(
    appPreferences: AppPreferences = koinInject()
) {
    val startDestination: Screen =
        when {
            !appPreferences.getOnboardingCompleted() -> Screen.OnBoardingScreen
            appPreferences.getOnboardingCompleted() && appPreferences.getUserToken().isEmpty() -> Screen.CredentialScreen
            appPreferences.getOnboardingCompleted() && appPreferences.getUserToken().isNotEmpty() && !appPreferences.getLanguageCompleted() -> Screen.ChooseLanguageScreen
            appPreferences.getOnboardingCompleted() && appPreferences.getUserId() != -1 -> Screen.LaunchScreen
            else -> Screen.LaunchScreen
        }

    val navigator = remember(startDestination) {
        AppNavigator().apply {
            backStack.clear()
            backStack.add(startDestination)
        }
    }


    // ── Language state lives here — drives AppLocaleProvider ─────────────────
    var languageCode by remember {
        mutableStateOf(
            appPreferences.getAppLanguage().ifEmpty { "en" }  // fallback to en
        )
    }

    AppLocaleProvider(languageCode = languageCode) {
        Box {
            CompositionLocalProvider(LocalNavigator provides navigator) {
                NavDisplay(
                    backStack = navigator.backStack,
                    onBack = { navigator.pop() },
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it })
                    },
                    popTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    },
                    entryProvider = entryProvider {
                        entry<Screen.OnBoardingScreen> { OnBoardingScreen() }
                        entry<Screen.CredentialScreen> { CredentialScreen() }
                        entry<Screen.MyProfile> { MyProfile() }
                        entry<Screen.Settings> { Settings() }
                        entry<Screen.AccountSettings> { AccountSettings() }
                        entry<Screen.DeleteAccount> { DeleteAccount() }
                        entry<Screen.LaunchScreen> { LaunchScreen() }
                        entry<Screen.ViewDetailsScreen> {
                            screen ->
                            ViewDetailsScreen(screen.postId)
                        }
                        entry<Screen.Edit_Profile> { Edit_Profile() }
                        entry<Screen.UploadArticleDetails> { UploadArticleDetails() }
                        entry<Screen.UploadYoutubeDetails> { UploadYoutubeDetails() }
                        entry<Screen.ArticleUploadPreview> { ArticleUploadPreview() }
                        entry<Screen.ContactSettings> { ContactSettings() }
                        entry<Screen.BlockListScreen> { BlockListScreen() }
                        entry<Screen.MyInterestScreen> { MyInterestScreen() }


                        entry<Screen.MessagesScreen> { MessagesScreen() }
                        entry<Screen.CommonReelView> { CommonReelView() }
                        entry<Screen.NotificationSettings> { NotificationSettings() }



                        entry<Screen.ExploreSearchPropertyResults> {
                            searchText ->
                            ExploreSearchPropertyResults(searchText.searchText) }

                        // ── Pass onLanguageChange only to ChooseLanguageScreen ─
                        entry<Screen.ChooseLanguageScreen> {
                            ChooseLanguageScreen(
                                onLanguageChange = { code -> languageCode = code }
                            )
                        }

                        entry<Screen.UploadVideoScreen> { screen ->
                            UploadVideoScreen(screen.videoUri)
                        }
                        entry<Screen.OthersProfile> { screen ->
                            OthersProfile(screen.userId)
                        }
                        entry<Screen.FF_Profile> { screen ->
                            FF_Profile_UI(
                                userId     = screen.userId,
                                username   = screen.username,
                                initialTab = screen.initialTab,
                                followersCount = screen.followersCount,
                                followingCount = screen.followingCount
                            )
                        }
                        entry<Screen.VideoUploadPreview> { screen ->
                            VideoUploadPreview(screen.videoUri!!)
                        }
                        entry<Screen.UploadVideoDetails> { screen ->
                            UploadVideoDetails(screen.videoUri)
                        }

                        /// chat


                        entry<Screen.PostChatUsers> { screen ->
                            val navigator = LocalNavigator.current

                            PostChatUsersScreen(
                                postId = screen.postId,

                            )
                        }

                        entry<Screen.Conversation> { screen ->
                            ConversationScreen(
                                chatId = screen.chatId,
                                postId = screen.postId,
                                currentUserId = screen.currentUserId,
                                otherUserId = screen.otherUserId,
                                otherUserName = screen.otherUserName
                            )
                        }
                    }
                )
            }
        }
    }
}

