package com.alpha.selfemployment.Views.ProfileModule.Following.Followers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.zText

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.alpha.selfemployment.AppPreferences
import com.alpha.selfemployment.GlobalSnackbar
import com.alpha.selfemployment.NetworkStatus
import com.alpha.selfemployment.R
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FFApiViewModel
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FFRequestKey
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FFUiViewModel
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FF_Profile_TabRow
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.model.FollowersFollowingResponseData
import com.alpha.selfemployment.fontFamily
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.rememberNetworkStatus
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.grayB8
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.ui.theme.redE54
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf


// ── Data ─────────────────────────────────────────────────────────────────────
data class UserItem(val username: String, val displayName: String)

private val sampleUsers = listOf(
    UserItem("user_rahul24",  "Rahul Verma"),
    UserItem("anusha.xo",     "Anusha Iyer"),
    UserItem("the_real_msk",  "Mahesh Kumar"),
    UserItem("nishaa_07",     "Nisha Sharma"),
    UserItem("aj.mehta99",    "Ajay Mehta"),
    UserItem("king_roshan",   "Roshan Patel"),
    UserItem("arun_360",      "Jennifer"),
)

// ── Main screen ──────────────────────────────────────────────────────────────
@Composable
fun FF_Views(
    username: String         = "Busine_Expert",
    followingCount: Int      = 100,
    followersCount: Int      = 2545,
    onBackClick: () -> Unit  = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf(
        "$followingCount Following",
        "${followersCount.formatCount()} Followers"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ── Top bar ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick   = onBackClick,
                modifier  = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painterResource(R.drawable.left_arrow),
                    contentDescription = "Back",
                    tint               = primaryBlack,
                    modifier           = Modifier.size(28.dp)
                )
            }

            zText(
                text     = username,
                color    = primaryBlack,
                20,
                0
                //size     = 20,
                //weight   = 1,
                //modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Tab row ──────────────────────────────────────────────────────────
        TabRow(
            selectedTabIndex  = selectedTab,
            containerColor    = Color.White,
            contentColor      = primaryBlack,
            indicator         = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier  = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color     = primaryBlack,
                    height    = 2.dp
                )
            },
            divider = { Divider(color = grayB8, thickness = 1.dp) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected     = selectedTab == index,
                    onClick      = { selectedTab = index },
                    text         = {
                        Text(
                            text       = title,
                            fontSize   = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold
                            else FontWeight.Normal,
                            color      = if (selectedTab == index) primaryBlack
                            else Color.Gray
                        )
                    }
                )
            }
        }

        // ── Search bar ───────────────────────────────────────────────────────
        OutlinedTextField(
            value         = searchQuery,
            onValueChange = { searchQuery = it },
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            placeholder   = { Text("AI", color = Color.Gray, fontSize = 14.sp) },
            leadingIcon   = {
                Icon(
                    painterResource(R.drawable.search),
                    contentDescription = null,
                   // tint               = primaryPurple,
                    modifier           = Modifier.size(20.dp)
                )
            },
            trailingIcon  = {
                Icon(
                    painterResource(R.drawable.person),          // QR-style placeholder
                    contentDescription = null,
                    //tint               = primaryPurple,
                    modifier           = Modifier.size(20.dp)
                )
            },
            shape         = RoundedCornerShape(10.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = primaryBlack,
                unfocusedBorderColor = primaryBlack,
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            singleLine    = true,
            modifier      = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // ── User list ────────────────────────────────────────────────────────
        val filtered = sampleUsers.filter {
            searchQuery.isEmpty() ||
                    it.username.contains(searchQuery, ignoreCase = true) ||
                    it.displayName.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filtered) { user ->
                UserRow(user = user)
                Divider(color = grayB8, thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 72.dp))
            }
        }
    }
}

// ── Single user row ──────────────────────────────────────────────────────────
@Composable
private fun UserRow(user: UserItem) {
    var following by remember { mutableStateOf(true) }

    Row(
        modifier        = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar placeholder
        Box(
            modifier        = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(gray66)
                .border(1.dp, grayB8, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.person),
                contentDescription = null,
                tint               = Color.Gray,
                modifier           = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name block
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = user.username,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = primaryBlack
            )
            Text(
                text     = user.displayName,
                fontSize = 12.sp,
                color    = Color.Gray
            )
        }

        // Unfollow / Follow button
        OutlinedButton(
            onClick      = { following = !following },
            shape        = RoundedCornerShape(8.dp),
            border       = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            ),
            colors       = ButtonDefaults.outlinedButtonColors(
                contentColor = primaryBlack
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier     = Modifier.height(34.dp)
        ) {
            Icon(
                painterResource(R.drawable.unfollow_icon),
                contentDescription = null,
                modifier           = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text     = if (following) "Unfollow" else "Follow",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FF_Profile_UI(
    userId : Int,
    username: String,
    initialTab: FF_Profile_TabRow,
    // , profileApiVm: ProfileApiViewModel = koinViewModel()
    followersCount : Int = 0,   // ← pass from MyProfile / OthersProfile
        followingCount : Int = 0
    , appPrefs : AppPreferences = koinInject()
){
    val ffApiVm: FFApiViewModel = koinViewModel(key = "ff_api_$userId") {
        parametersOf(userId)
    }

    val network by rememberNetworkStatus()


    val state by ffApiVm.ffState.collectAsStateWithLifecycle()

    val ffUiVm: FFUiViewModel =
        koinViewModel(key = "ff_$userId") {
            parametersOf(userId)
        }


    LaunchedEffect(Unit) {
        ffUiVm.switchTab(initialTab)
        ffUiVm.initCounts(followers = followersCount, following = followingCount)
    }



    val onClickedFollowingFollowers by ffUiVm.currentTab.collectAsState()
    val searchTextFollowers = ffUiVm.followersSearch.collectAsState()
    val searchTextFollowing = ffUiVm.followingSearch.collectAsState()

    val requestKey = remember(
        userId,
        onClickedFollowingFollowers,
        searchTextFollowers.value,
        searchTextFollowing.value
    ) {
        FFRequestKey(
            userId = userId,
            tab = onClickedFollowingFollowers,
            search = if (onClickedFollowingFollowers == FF_Profile_TabRow.FOLLOWERS)
                searchTextFollowers.value
            else
                searchTextFollowing.value
        )
    }

    var previousTab by remember { mutableStateOf(onClickedFollowingFollowers) }
    var isTabSwitching by remember { mutableStateOf(false) }

// When tab changes, immediately mark as switching
    LaunchedEffect(onClickedFollowingFollowers) {
        if (previousTab != onClickedFollowingFollowers) {
            isTabSwitching = true
            previousTab = onClickedFollowingFollowers
        }
    }

// Clear switching flag once loading completes
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            isTabSwitching = false
        }
    }

//    val listStates = remember { mutableMapOf<Int, LazyListState>() }
//    val listState = listStates.getOrPut(userId) { LazyListState() }




    if (network == NetworkStatus.Online) {

        LaunchedEffect(requestKey) {
            delay(400) // debounce (optional)
            println("THIS CALLS HAPPENING ")

            ffApiVm.tryLoadFF(
                requestKey = requestKey,
                myUserId = appPrefs.getUserId()
            )
        }
    }


    var unfollow by remember { mutableStateOf(false) }
    var follow by remember { mutableStateOf(false) }
    var followBack by remember { mutableStateOf(false) }


    var getUserName = retain { mutableStateOf("") }
    var getUserId = retain { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryWhite)

    ) {
        FF_Profile_Header(
            modifier = Modifier
                .padding(top = rememberNotchHeightDp().value)
                .fillMaxWidth()
                .height(64.dp)
            ,username
            ,ffApiVm
        )

        spacer(4)

        FF_Profile_TabRow(ffUiVm, modifier = Modifier
            .fillMaxWidth()
            .weight(.6f)
            .padding(horizontal = 16.dp))

        spacer(8)

        FF_Profile_Search_Bar(
            ffUiVm
            , modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
            ,onClickedFollowingFollowers
        )

        spacer(8)

        when {
            network == NetworkStatus.Offline -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(8f)
                    , contentAlignment = Alignment.Center
                ) {
//                    NoInternetAnimation()
                }
            }

            // ✅ Show full loader for BOTH initial load AND tab switch
            state.isLoading || isTabSwitching -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(8f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(8f)
                    , contentAlignment = Alignment.Center
                ) {
//                    ApiFail {
//                        profileApiVm.tryLoadFF(
//                            requestKey = requestKey,
//                            myUserId = appPrefs.getUserId()
//                        )
//                    }
                }
            }

            state.list.isEmpty() && !state.isLoading -> {
                if(searchTextFollowers.value.isNotEmpty() || searchTextFollowing.value.isNotEmpty()){
//                    NoSearchDataView()
                }
                else {
                    if (onClickedFollowingFollowers == FF_Profile_TabRow.FOLLOWERS){
//                        NoFollowers(modifier = Modifier.fillMaxWidth() .weight(8f))
                    }
                    else {
//                        NoFollowings(modifier = Modifier.fillMaxWidth() .weight(8f))
                    }
                }
            }

            state.list.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(8f)

                )
                {

                    itemsIndexed(
                        items = state.list,
                        //key = { _, item -> item.user_id }
                        key = { index, item -> "${item.user_id}_$index" }
                    ) { index, item ->


                        // Pagination trigger
                        if (network == NetworkStatus.Online) {
                            if (
                                index == state.list.lastIndex &&
                                !state.isPaging &&
                                !state.isLastPage
                            ) {
                                LaunchedEffect(state.list.size) {
                                    println("THIS CALLS HAPPENING pagination ")
                                    if (state.isSearch && searchTextFollowers.value.length > 3 || searchTextFollowing.value.length > 3) {
                                        println("PAGING LAUNCHED EFFECT")
//                                        ffApiVm.loadFollowersFollowing(
//                                            appPrefs.getUserId(),
//                                            others_id = userId,
//                                            status = if (onClickedFollowingFollowers == FF_Profile_TabRow.FOLLOWING) "2" else "1",
//                                            search = if (onClickedFollowingFollowers == FF_Profile_TabRow.FOLLOWERS) searchTextFollowers.value else searchTextFollowing.value,
//                                        )
                                    } else {
                                        ffApiVm.loadFollowersFollowing(
                                            appPrefs.getUserId(),
                                            othersId = userId,
                                            status = if (onClickedFollowingFollowers == FF_Profile_TabRow.FOLLOWING) "2" else "1",
                                            loadNext = true
                                        )
                                    }
                                }
                            }
                        }

                        FF_Profile_UserItem(item, modifier = Modifier
                            , onFollowBackClick = {
                                    name , userId ->
                                if (network == NetworkStatus.Online){
                                    ffApiVm.followUnfollowApi(
                                        appPrefs.getUserId(),
                                        userId,
                                        status = "1"
                                    ) {
                                            state ->
                                        if (state){
                                            println("ajfhbwsefhjbwegvhwbhjebvhbwebv 3333")
                                            ffApiVm.updateFollowState(
                                                userId = userId,
                                                isFollowed = 1,
                                                imFollowed = 1
                                            )

                                            ffUiVm.onFollowSuccess()
                                        }
//                                    profileApiVm.resetToIdle()
                                    }
                                }
                                else {
                                    GlobalSnackbar.show(id = R.string.noInternet)
                                }
                            }
                            , onUnfollowClick =
                                {
                                        name , userId ->
                                    println("ajfhbwsefhjbwegvhwbhjebvhbwebv 22222")
                                    getUserName.value = name
                                    getUserId.value = userId
                                    unfollow = true
//                            profileApiVm.showUnFollowPopup(userId)
                                },
                            onFollowClick = {
                                    name , userId ->

                                if (network == NetworkStatus.Online){
                                    ffApiVm.followUnfollowApi(
                                        appPrefs.getUserId(),
                                        userId,
                                        status = "1"
                                    ) {
                                            state ->
                                        if (state){
                                            println("ajfhbwsefhjbwegvhwbhjebvhbwebv 5555")
                                            ffApiVm.updateFollowState(
                                                userId = userId,
                                                isFollowed = 1,
                                                imFollowed = 1
                                            )

                                            ffUiVm.onFollowSuccess()
                                        }
//                                    profileApiVm.resetToIdle()
                                    }
                                }
                                else {
                                    GlobalSnackbar.show(id = R.string.noInternet)
                                }
                            },
                            currentTab = onClickedFollowingFollowers,
                            onRemoveClick     = { name, userId ->
                                if (network == NetworkStatus.Online) {
                                    ffApiVm.followUnfollowApi(
                                        appPrefs.getUserId(), userId, status = "3"  // status "3" = remove follower
                                    ) { success ->
                                        if (success) {
                                            ffApiVm.removeWhenBlocked(userId)       // reuse remove helper
                                            ffUiVm.onRemoveSuccess()                // decrement count
                                        }
                                    }
                                }
                            }
                        )

                        spacer(4)

                    }



                    // Pagination loader
                    if (state.isPaging) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    // Error
                    state.error?.let {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(8f)
                                , contentAlignment = Alignment.Center
                            ) {
//                                ApiFail {
//                                    profileApiVm.tryLoadFF(
//                                        requestKey = requestKey,
//                                        myUserId = appPrefs.getUserId()
//                                    )
//                                }
                            }
                        }
                    }
                }
            }


            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(8f)
                    , contentAlignment = Alignment.Center
                ) {
                    // CircularProgressIndicator()
                }
            }


        }


        println("fgdxszfxb -- ")

//        /// unfollow
//        LaunchedEffect(follow) {
//
//        }

        if (unfollow) {
            BasicAlertDialog(
                onDismissRequest = {
                    unfollow =false

                }
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryWhite)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    spacer(8)


                    zText(
                        "Unfollow  ${getUserName.value} ?",
                        primaryBlack,
                        16,
                        2
                    )

                    spacer(8)

                    zText(
                        "Then you can again follow them anytime you want ",
                        primaryBlack,
                        16,
                        3
                    )

                    spacer(8)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    )
                    {
                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .width(118.dp)
                                .background(gray48)
                                .shrinkClick {
//                                    profileApiVm.hideUnFollowPopup(userId)
                                    unfollow = false
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            zText("Cancel", grayB8, 16, 3)
                        }

                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .width(118.dp)
                                .background(redE54)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .shrinkClick {
                                    if (network == NetworkStatus.Online) {
                                        ffApiVm.followUnfollowApi(
                                            user_id = appPrefs.getUserId(),
                                            following_id = getUserId.value,
                                            status = "2"
                                        ) { success ->
                                            if (success) {
                                                ffApiVm.updateFollowState(
                                                    userId = getUserId.value,
                                                    isFollowed = 0,
                                                    imFollowed = 0
                                                )
                                                // Update state directly
//                                            profileApiVm.updateProfileFollowStateDirect(userId, 0, 0)
//                                            profileApiVm.hideUnFollowPopup(userId)

                                                ffUiVm.onUnfollowSuccess()
                                                unfollow = false
                                                GlobalSnackbar.show(id = R.string.user_unfollowed_successfully)
                                            }
                                        }
                                    } else {
                                        GlobalSnackbar.show(id = R.string.noInternet)
                                    }

                                }, contentAlignment = Alignment.Center
                        ) {
                            zText("UnFollow", primaryWhite, 16, 3)

                        }
                    }

                    spacer(8)
                }
            }
        }
    }
}

/// FF Header
@Composable
fun FF_Profile_Header(modifier: Modifier, username: String, profileApiVm: FFApiViewModel) {

    val navigator = LocalNavigator.current

    /// header
    Row(
        modifier = modifier
            .fillMaxWidth()
//            .background()
            .padding(horizontal = 16.dp)
        , verticalAlignment = Alignment.CenterVertically
        , horizontalArrangement = Arrangement.Start
    ) {
        Image(painter = painterResource(R.drawable.left_arrow) , "",
            colorFilter = ColorFilter.tint(primaryBlack)
            , modifier = Modifier
                .size(32.dp)
                .shrinkClick {
                    //profileApiVm.clear_FF_List()
                    navigator.pop()
                }
        )

        spacer(8)

        zText(username , primaryBlack , 20 , 1)
    }
}



// FF Search bar

@Composable
fun FF_Profile_Search_Bar(
    viewModel: FFUiViewModel,
    modifier: Modifier,
    onClickedFollowingFollowers: FF_Profile_TabRow?
) {

    val searchtextfollowers = viewModel.followersSearch.collectAsState()
    val searchtextfollowing = viewModel.followingSearch.collectAsState()

    OutlinedTextField(
        value = if (onClickedFollowingFollowers == FF_Profile_TabRow.FOLLOWERS) searchtextfollowers.value else searchtextfollowing.value
        , onValueChange = {
            if (onClickedFollowingFollowers == FF_Profile_TabRow.FOLLOWERS){
                viewModel.setFollowersSearch(it)
            }
            else {
                viewModel.setFollowingSearch(it)
            }
        }
        , placeholder = {
            Text( "Search users by name ..")
        }
        , leadingIcon = {
            Image(painter = painterResource(R.drawable.search) , "")
        },
        trailingIcon = {
            if ( searchtextfollowers.value.isNotEmpty() ||searchtextfollowing.value.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.clear), "",
                    modifier = Modifier.shrinkClick {
                        if (onClickedFollowingFollowers == FF_Profile_TabRow.FOLLOWERS) {

                            viewModel.clearFollowersSearch()
                        } else {
                            viewModel.clearFollowingSearch()
                        }
                    }
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
            , keyboardType = KeyboardType.Text
        ),
        singleLine = true
        ,modifier = modifier.fillMaxWidth()
        , colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = primaryWhite
            , unfocusedBorderColor = gray48
            , focusedBorderColor = primaryBlack
            , focusedTextColor = primaryBlack
            , unfocusedTextColor = primaryBlack
        )

    )

}




/// FF Item
enum class FF_Type { FOLLOW, FOLLOWING, FOLLOW_BACK }


@Composable
fun FF_Profile_UserItem(
    item             : FollowersFollowingResponseData?,
    appPrefs         : AppPreferences = koinInject(),
    profileApiVm     : FFApiViewModel = koinViewModel(),
    modifier         : Modifier,
    currentTab       : FF_Profile_TabRow,          // ← new
    onUnfollowClick  : (String, Int) -> Unit,
    onFollowClick    : (String, Int) -> Unit,
    onFollowBackClick: (String, Int) -> Unit,
    onRemoveClick    : (String, Int) -> Unit        // ← new
) {
    val network  by rememberNetworkStatus()
    val navigator = LocalNavigator.current

    val followType = when {
        item?.is_followed == 0 && item.im_followed == 0 -> FF_Type.FOLLOW
        item?.is_followed == 1 && item.im_followed == 0 -> FF_Type.FOLLOW_BACK
        else                                             -> FF_Type.FOLLOWING
    }

    ListItem(
        leadingContent = {
            Box(
                modifier         = Modifier.size(56.dp).clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model              = item?.profile_image ?: "",
                    modifier           = Modifier.fillMaxSize(),
                    contentDescription = "",
                    contentScale       = ContentScale.FillBounds
                ) {
                    val painterState = painter.state
                    if (painterState is AsyncImagePainter.State.Loading ||
                        painterState is AsyncImagePainter.State.Error
                    ) {
                        Box(
                            modifier         = Modifier.fillMaxSize().clip(CircleShape).background(primaryWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = item?.username.takeIf { it?.isNotEmpty() == true }?.take(1)?.uppercase() ?: "",
                                fontSize   = textUnit(14),
                                fontFamily = fontFamily(1),
                                color      = primaryBlack
                            )
                        }
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
        },
        headlineContent   = { zText(item?.username ?: "username", primaryBlack, 14, 1) },
        supportingContent = { zText(item?.name ?: "name", primaryBlack, 12, 3) },
        trailingContent   = {
            if (item?.is_blocked == 0) {
                if (network == NetworkStatus.Online) {
                    if (item.user_id != appPrefs.getUserId()) {
                        FFButtons(
                            type         = followType,
                            currentTab   = currentTab,
                            onFollow     = { onFollowClick(item.username, item.user_id) },
                            onFollowing  = { onUnfollowClick(item.username, item.user_id) },
                            onFollowBack = { onFollowBackClick(item.username, item.user_id) },
                            onRemove     = { onRemoveClick(item.username, item.user_id) }
                        )
                    }
                }
            }
        },
        colors   = ListItemDefaults.colors(containerColor = primaryWhite),
        modifier = modifier
            .padding(horizontal = 4.dp)
            .shadow(4.dp)
            .shrinkClick {
                if (item?.user_id != appPrefs.getUserId()) {
                    item?.user_id?.let { id ->
                        navigator.navigate(Screen.OthersProfile(userId = id))
                    }
                }
            }
    )
}

@Composable
fun FF_Profile_TabRow(viewModel: FFUiViewModel, modifier: Modifier) {

    val rawState       by viewModel.currentTab.collectAsState()
    val followersCount by viewModel.followersCount.collectAsState()
    val followingCount by viewModel.followingCount.collectAsState()

    val selectedIndex = if (rawState == FF_Profile_TabRow.FOLLOWING) 1 else 0

    val tabLabels = listOf(
        "${followersCount.formatCount()} Followers",
        "${followingCount.formatCount()} Following"
    )

    Column(modifier = modifier.fillMaxWidth().background(primaryWhite)) {
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor   = primaryWhite,
            contentColor     = primaryBlack,
            // ✅ Real indicator — was {} before which removed it entirely
            indicator        = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color    = primaryBlack,
                    height   = 2.dp
                )
            },
            divider = { Divider(color = grayB8, thickness = 1.dp) }
        ) {
            tabLabels.forEachIndexed { index, label ->
                Tab(
                    selected = selectedIndex == index,
                    onClick  = {
                        viewModel.switchTab(
                            if (index == 1) FF_Profile_TabRow.FOLLOWING
                            else FF_Profile_TabRow.FOLLOWERS
                        )
                    },
                    text = {
                        Text(
                            text       = label,
                            fontSize   = 14.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold
                            else FontWeight.Normal,
                            color      = if (selectedIndex == index) primaryBlack
                            else Color.Gray
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun FF_Profile_TabRowOLd(viewModel: FFUiViewModel, modifier: Modifier) {

    val rawState        by viewModel.currentTab.collectAsState()
    val followersCount  by viewModel.followersCount.collectAsState()
    val followingCount  by viewModel.followingCount.collectAsState()

    val state = if (rawState == FF_Profile_TabRow.FOLLOWING) 1 else 0

    // Tab labels with live counts
    val tabLabels = listOf(
        "${followersCount.formatCount()} Followers",
        "${followingCount.formatCount()} Following"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(primaryWhite)
    ) {
        SecondaryTabRow(
            selectedTabIndex = state,
            indicator        = {},
            divider          = {},
            modifier         = Modifier.background(primaryWhite),
            containerColor   = primaryWhite
        ) {
            viewModel.ff_Profile_TabRow.forEachIndexed { index, _ ->

                val label = tabLabels[index]

                if (state == index) {
                    // Selected tab
                    Box(
                        modifier         = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(.9f)
                                .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                                .padding(horizontal = 4.dp)
                        ) {
                            Tab(
                                selected = true,
                                onClick  = {},
                                text     = { zText(label, black1A, 16, 2) }
                            )
                        }
                    }
                } else {
                    // Unselected tab
                    Box(
                        modifier         = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(primaryWhite),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(.9f)
                                .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                                .background(primaryWhite)
                                .padding(horizontal = 4.dp)
                        ) {
                            Tab(
                                selected = false,
                                onClick  = {
                                    viewModel.switchTab(
                                        if (index == 1) FF_Profile_TabRow.FOLLOWING
                                        else FF_Profile_TabRow.FOLLOWERS
                                    )
                                },
                                text = { zText(label, primaryBlack, 16, 2) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Helper ────────────────────────────────────────────────────────────────────
fun Int.formatCount(): String =
    when {
        this >= 1_000_000 -> "${"%.1f".format(this / 1_000_000.0)}M"
        this >= 1_000     -> "${"%.1f".format(this / 1_000.0)}K"
        else              -> toString()
    }




// ─────────────────────────────────────────────────────────────────────────────
// 1. FFButtons — correct per Figma
//    Following tab → Unfollow only
//    Followers tab → Follow back / Following + ❌ remove
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FFButtons(
    type         : FF_Type,
    currentTab   : FF_Profile_TabRow,   // ← which tab is active
    onFollow     : () -> Unit,
    onFollowing  : () -> Unit,
    onFollowBack : () -> Unit,
    onRemove     : () -> Unit            // ← only shown in Followers tab
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        when (type) {

            FF_Type.FOLLOW -> {
                FF_ActionButton(label = "Follow", icon = R.drawable.person, onClick = onFollow)
            }

            FF_Type.FOLLOWING -> {
                FF_ActionButton(label = "Unfollow", icon = R.drawable.unfollow_icon, onClick = onFollowing)
                // ❌ only in Followers tab
                if (currentTab == FF_Profile_TabRow.FOLLOWERS) {
                    FF_RemoveButton(onClick = onRemove)
                }
            }

            FF_Type.FOLLOW_BACK -> {
                FF_ActionButton(label = "Follow back", icon = R.drawable.person, onClick = onFollowBack)
                // ❌ only in Followers tab
                if (currentTab == FF_Profile_TabRow.FOLLOWERS) {
                    FF_RemoveButton(onClick = onRemove)
                }
            }
        }
    }
}

// ── Shared outlined action button ─────────────────────────────────────────────
@Composable
fun FF_ActionButton(label: String, icon: Int, onClick: () -> Unit) {
    OutlinedButton(
        onClick        = onClick,
        shape          = RoundedCornerShape(6.dp),
        border         = BorderStroke(1.dp, primaryBlack),
        colors         = ButtonDefaults.outlinedButtonColors(contentColor = primaryBlack),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        modifier       = Modifier.height(30.dp)
    ) {
        Icon(
            painter            = painterResource(icon),
            contentDescription = null,
            modifier           = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// ── ❌ Remove icon button (Followers tab only) ─────────────────────────────────
@Composable
fun FF_RemoveButton(onClick: () -> Unit) {
    Box(
        modifier         = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, grayB8, RoundedCornerShape(4.dp))
            .shrinkClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter            = painterResource(R.drawable.clear), // your X icon
            contentDescription = "Remove",
            tint               = primaryBlack,
            modifier           = Modifier.size(14.dp)
        )
    }
}


