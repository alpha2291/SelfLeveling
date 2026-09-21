package com.alpha.selfemployment

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.MainSettingItems
import com.alpha.selfemployment.Views.ProfileModule.Settings.ui.NotificationSettingSubItems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.listOf


data class BtmBarItem(
    var id : Int,
    var title : Int,
    var selectedIcon : Int,
    var unSelectedIcon : Int
)

enum class HomeScreenFlow {
    Videos , Articles
}




class utils {
    companion object {

        lateinit var activity: MainActivity


        /// home tab row

        private var _homeTabFlow = MutableStateFlow<HomeScreenFlow>(HomeScreenFlow.Videos)
        var homeTabFlow : StateFlow<HomeScreenFlow> = _homeTabFlow.asStateFlow()

        fun set_HomeTabFlow(tab : HomeScreenFlow){
            _homeTabFlow.update { tab }
        }

        fun clear_HomeTabFlow(){
            _homeTabFlow.value = HomeScreenFlow.Videos
        }


        /// btm bar hide / show
        private var _showBottomBar = MutableStateFlow(true)
        var showBottomBar : StateFlow<Boolean> = _showBottomBar.asStateFlow()

        fun hide_BtmBar(){
            _showBottomBar.value = false
        }

        fun show_BtmBar(){
            _showBottomBar.value = true
        }


        ////////////  comment

        private var _commentState = MutableStateFlow(0)
        var commentState : StateFlow<Int> = _commentState.asStateFlow()


        fun enable_Comment(id : Int){
            _commentState.update { id }
        }

        fun disable_Comment(){
            _commentState.update { 0 }
        }


        /// btm bar



        private val _btmBarItems = MutableStateFlow(
            listOf(
                BtmBarItem(
                    0,
                    R.string.home,
                    R.drawable.selectedhome,
                    R.drawable.unselectedhome
                ),
                BtmBarItem(
                    1,
                    R.string.explore,
                    R.drawable.selectedsearch,
                    R.drawable.explore_btm_bar
                ),
                BtmBarItem(
                    2,
                    R.string.upload,
                    R.drawable.selectedupload,
                    R.drawable.upload_btm_bar
                ),
                BtmBarItem(
                    3,
                    R.string.messages,
                    R.drawable.selectedmessages,
                    R.drawable.messages_btm_bar
                ),
                BtmBarItem(
                    4,
                    R.string.profile,
                    R.drawable.selectedprofile,
                    R.drawable.profile_btm_bar
                ),
            )
        )

        val btmBarItems = _btmBarItems

        private val _selectedItem = MutableStateFlow(0)
        val selectedItem = _selectedItem

        fun onItemSelected(id: Int) {
            _selectedItem.value = id
        }


        /// settings item

        private var _settingsItems = MutableStateFlow(
            listOf(
                MainSettingItems(
                    0,
                    R.string.account_settings,
                    R.drawable.accountsettings
                ),
                MainSettingItems(
                    1,
                    R.string.my_interests,
                    R.drawable.myinterests
                ),
                MainSettingItems(
                    2,
                    R.string.saved_business,
                    R.drawable.savedbusiness
                ),
                MainSettingItems(
                    3,
                    R.string.share_app,
                    R.drawable.shareapp
                ),
                MainSettingItems(
                    4,
                    R.string.app_language,
                    R.drawable.app_language_settings
                ),
                MainSettingItems(
                    5,
                    R.string.rate_us,
                    R.drawable.rateus
                ),
                MainSettingItems(
                    6,
                    R.string.feedBack,
                    R.drawable.feedback
                ),
                MainSettingItems(
                    7,
                     R.string.about_us,
                    R.drawable.aboutus
                ),
                MainSettingItems(
                    8,
                    R.string.terms_conditions,
                    R.drawable.termsandconsditions
                ),
                MainSettingItems(
                    9,
                    R.string.privacy_policy,
                    R.drawable.privacypolicy
                ),
            )
        )


        var settingsItems = _settingsItems



        private var _notificationSubSettings = MutableStateFlow(
            listOf(
                NotificationSettingSubItems(
                    id = 0,
                    title = "Followers"
                ),
                NotificationSettingSubItems(
                    id = 1,
                    title = "Likes"
                ),
                NotificationSettingSubItems(
                    id = 2,
                    title = "Comments"
                ),
                NotificationSettingSubItems(
                    id = 3,
                    title = "New Video Posts"
                ),
            )
        )

        var notificationSubSettings = _notificationSubSettings


        fun toggleNotificationSubSettings(nsid: Int) {
            _notificationSubSettings.value =
                _notificationSubSettings.value.map { item ->
                    if (item.id == nsid) {
                        item.copy(isChecked = !item.isChecked)
                    } else {
                        item
                    }
                }
        }


        /// reprot btm enable

        private var _invokeReportBtmSheet = MutableStateFlow(false)
        var invokeReportBtmSheet : StateFlow<Boolean> = _invokeReportBtmSheet.asStateFlow()


        fun invokeReportBtm(){
            _invokeReportBtmSheet.value = true
        }

        fun revokeReportBtm(){
            _invokeReportBtmSheet.value = false
        }


        /// report btm sheet options



        private val _reportOptionItems = MutableStateFlow(
            listOf(
                ReportBtmSheetItems(1, R.string.inappropriate_content, false),
                ReportBtmSheetItems(2, R.string.spam_irrelevant_content, false),
                ReportBtmSheetItems(3, R.string.copyright_infringement, false),
                ReportBtmSheetItems(4, R.string.technical_issues, false),
                ReportBtmSheetItems(5, R.string.privacy_violation, false),
                ReportBtmSheetItems(6, R.string.cultural_insensitivity, false),
                ReportBtmSheetItems(7, R.string.something_else, false),
            )
        )

        val reportOptionsItems: StateFlow<List<ReportBtmSheetItems>> = _reportOptionItems.asStateFlow()

        fun toggleReportOptionItems(id: Int) {
            println("toggle id = $id")
            _reportOptionItems.value =
                _reportOptionItems.value.map { item ->
                    item.copy(isSelected = item.id == id)
                }
            println("after toggle = ${_reportOptionItems.value}")
        }

        fun getSelectedReportOptionId(): Int {
            println("current list = ${_reportOptionItems.value}")
            return _reportOptionItems.value.firstOrNull { it.isSelected }?.id ?: 0
        }

        /// reals optino
        private var _reelsOptionBtm = MutableStateFlow(false)
        var reelsOptionsBtmSheet : StateFlow<Boolean> = _reelsOptionBtm.asStateFlow()


        fun open_ReelsOptionsBtm(){
            _reelsOptionBtm.value = true
        }

        fun close_ReelsOptionsBtm(){
            _reelsOptionBtm.value = false
        }

        /// post upload format
        private var _postUploadFormat = MutableStateFlow(false)
        var postUploadFormat : StateFlow<Boolean> = _postUploadFormat.asStateFlow()


        fun open_PostUploadBtm(){
            _postUploadFormat.value = true
        }

        fun close_PostUploadBtm(){
            _postUploadFormat.value = false
        }


        /// yoyutbe reels fullview
        private var _youtubeReelsFullView = MutableStateFlow("")
        var youtubeReelsFullView : StateFlow<String> = _youtubeReelsFullView.asStateFlow()


        fun open_youtubeReelBtm(videoid : String){
            _youtubeReelsFullView.value = videoid
        }

        fun close_youtubeReelBtm(){
            _youtubeReelsFullView.value = ""
        }

        /// own post optins
        private var _ownPostOptionsBtmSheet = MutableStateFlow(false)
        var ownPostOptionsBtmSheet : StateFlow<Boolean> = _ownPostOptionsBtmSheet.asStateFlow()


        fun open_OwnPostOptionsBtm(){
            _ownPostOptionsBtmSheet.value = true
        }

        fun close_OwnPostOptionsBtm(){
            _ownPostOptionsBtmSheet.value = false
        }


        /// number or email change verification sheet
        private var _numberemailchangeVerify = MutableStateFlow(false)
        var numberemailchangeVerify : StateFlow<Boolean> = _numberemailchangeVerify.asStateFlow()


        fun open_numberemailchangeVerify(){
            _numberemailchangeVerify.value = true
        }

        fun close_numberemailchangeVerify(){
            _numberemailchangeVerify.value = false
        }




    }
}








fun Modifier.shrinkClick(
    scaleDown: Float = 0.92f,
    debounceMillis: Long = 400L,
    onClick: () -> Unit
): Modifier = composed {

    val scale = remember { Animatable(1f) }
    var lastClickTime by remember { mutableStateOf(0L) }

    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                scale.animateTo(scaleDown, tween(120))
                try {
                    awaitRelease()
                } finally {
                    scale.animateTo(1f, tween(150))
                }
            },
            onTap = {
                val now = System.currentTimeMillis()
                if (now - lastClickTime > debounceMillis) {
                    lastClickTime = now
                    onClick()
                }
            }
        )
    }
}



@Composable
fun zText(
    text: String,
    color: Color,
    fontSize: Int,
    fontFamily: Int,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Unspecified
) {

    Text(
        text = text,
        color = color,
        fontSize = textUnit(fontSize),
        fontFamily = fontFamily(fontFamily),
        textAlign = textAlign,
        style = TextStyle(
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        ),
        modifier = modifier
    )

}



@SuppressLint("LocalContextConfigurationRead")
@Composable
fun forTab(): Boolean {
    val context = LocalContext.current
    val configuration = context.resources.configuration
    val screenLayout = configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    return screenLayout >= Configuration.SCREENLAYOUT_SIZE_LARGE
}

val Int.scaledSp  @Composable get() = (this / LocalDensity.current.fontScale).sp


@Composable
fun textUnit(size: Int): TextUnit {
    val isTablet = forTab() // from your earlier Compose helper

    val final = when (size) {
        24 -> if (isTablet) 28.scaledSp else 24.scaledSp
        20 -> if (isTablet) 24.scaledSp else 20.scaledSp
        15 -> if (isTablet) 18.scaledSp else 15.scaledSp
        12 -> if (isTablet) 15.scaledSp else 12.scaledSp
        14 -> if (isTablet) 17.scaledSp else 14.scaledSp
        16 -> if (isTablet) 20.scaledSp else 16.scaledSp
        else -> size.scaledSp
    }

    return final
}



@Composable
fun spacer(size: Int) {
    val isTablet = forTab() // or forTab()

    val space =  when (size) {
        2 -> if (isTablet) 4.dp else 2.dp
        4 -> if (isTablet) 8.dp else 4.dp
        6 -> if (isTablet) 10.dp else 6.dp
        8 -> if (isTablet) 12.dp else 8.dp
        12 -> if (isTablet) 16.dp else 12.dp
        16 -> if (isTablet) 20.dp else 16.dp
        20 -> if (isTablet) 24.dp else 20.dp
        40 -> if (isTablet) 60.dp else 40.dp
        else -> size.dp
    }

    Spacer(modifier = Modifier.padding(space))
}

fun fontFamily(isWhat : Int): FontFamily{
    return when (isWhat){
        0 -> androidx.compose.ui.text.font.FontFamily(Font(R.font.popbold))
        1 -> androidx.compose.ui.text.font.FontFamily(Font(R.font.popsemibold))
        2 -> androidx.compose.ui.text.font.FontFamily(Font(R.font.popmedium))
        3 -> androidx.compose.ui.text.font.FontFamily(Font(R.font.popregular))
        4 -> androidx.compose.ui.text.font.FontFamily(Font(R.font.popthin))
        else -> {
            androidx.compose.ui.text.font.FontFamily(Font((R.font.popregular)))
        }
    }
}

