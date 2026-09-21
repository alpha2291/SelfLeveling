package com.alpha.selfemployment.navigation


import android.net.Uri
import kotlinx.serialization.Serializable

import androidx.compose.runtime.*
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FF_Profile_TabRow


sealed interface Screen {

    @Serializable
    data object SplashScreen : Screen




    @Serializable
    data object OnBoardingScreen : Screen


    @Serializable
    data object CredentialScreen : Screen


    @Serializable
    data object MyProfile : Screen

    @Serializable
    data object LaunchScreen : Screen


    @Serializable
    data object Settings : Screen

    @Serializable
    data object AccountSettings : Screen

    @Serializable
    data object BlockListScreen : Screen

    @Serializable
    data object MyInterestScreen : Screen

    @Serializable
    data object DeleteAccount : Screen

    @Serializable
    data class ViewDetailsScreen(val postId : Int) : Screen



    @Serializable
    data object Edit_Profile : Screen


    @Serializable
    data class OthersProfile(val userId: Int) : Screen

    @Serializable
    data class ExploreSearchPropertyResults(val searchText: String) : Screen

    @Serializable
    data class FF_Profile(
        val userId     : Int,
        val username   : String,
        val initialTab : FF_Profile_TabRow ,  // FOLLOWERS or FOLLOWING,
        val followersCount : Int,
        val followingCount : Int,

    ) : Screen

    @Serializable
    data object ChooseLanguageScreen : Screen


    @Serializable
    data object UploadArticleDetails : Screen



    @Serializable
    data object UploadYoutubeDetails : Screen



    @Serializable
    data object ArticleUploadPreview : Screen




    @Serializable
    data class YoutubePreviewTrial (val videoId : String ) : Screen






    @Serializable
    data object ContactSettings : Screen








    @Serializable
    data class UploadVideoScreen(
       val  videoUri : Uri?
    ) : Screen



    @Serializable
    data class VideoUploadPreview(
       val  videoUri : Uri?
    ) : Screen


    @Serializable
    data class UploadVideoDetails(
       val  videoUri : Uri?
    ) : Screen



    ///

    @Serializable
    data object DummyChatHome : Screen

    ///

    @Serializable
    data object MessagesScreen : Screen


    @Serializable
    data object NotificationSettings : Screen


    @Serializable
    data object CommonReelView : Screen

    @Serializable
    data class PostChatUsers(
        val postId: String
    ) : Screen

    @Serializable
    data class Conversation(
        val chatId: String,
        val postId: String,
        val currentUserId: String,
        val otherUserId: String,
        val otherUserName: String
    ) : Screen

}














/////////////////////////////////////////