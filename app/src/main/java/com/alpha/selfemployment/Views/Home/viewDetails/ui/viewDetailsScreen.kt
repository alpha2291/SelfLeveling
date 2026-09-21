package com.alpha.selfemployment.Views.Home.viewDetails.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import com.alpha.selfemployment.R
import com.alpha.selfemployment.Views.Home.TTF.TTSManager
import com.alpha.selfemployment.Views.Home.TTF.highlightedText
import com.alpha.selfemployment.formatTime
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.grayF5
import com.alpha.selfemployment.ui.theme.brandBlue
import com.alpha.selfemployment.ui.theme.lightBlueF0
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.zText
import kotlinx.coroutines.delay

// ── All content in one place — UI and TTS both use the exact same strings ───

/*private const val SHORT_TEXT =
    "Successful crop growth begins with selecting suitable seeds for your local climate and soil. " +
            "Improve soil fertility by tilling and adding organic matter."

private const val LONG_TEXT =
    "To successfully grow crops, start by selecting the right seeds for your climate and soil type. " +
            "Prepare your soil by tilling and adding organic matter to enhance fertility. " +
            "Ensure proper irrigation by setting up a watering schedule that meets the needs of your plants. " +
            "Regularly monitor for pests and diseases, and apply organic pesticides when necessary. " +
            "Finally, harvest your crops at the right time to enjoy the best flavor and nutrition. " +
            "To successfully grow crops, start by selecting the right seeds for your climate and soil type. " +
            "Prepare your soil by tilling and adding organic matter to enhance fertility. " +
            "Ensure proper irrigation by setting up a watering schedule that meets the needs of your plants. " +
            "Regularly monitor for pests and diseases, and apply organic pesticides when necessary. " +
            "Finally, harvest your crops at the right time to enjoy the best flavor and nutrition. " +
            "To successfully grow crops, start by selecting the right seeds for your climate and soil type. " +
            "Prepare your soil by tilling and adding organic matter to enhance fertility. " +
            "Ensure proper irrigation by setting up a watering schedule that meets the needs of your plants. " +
            "Regularly monitor for pests and diseases, and apply organic pesticides when necessary. " +
            "Finally, harvest your crops at the right time to enjoy the best flavor and nutrition."

// TTS speaks this exact string — short → long, no stop
private const val FULL_TTS_TEXT = "$SHORT_TEXT $LONG_TEXT"

// Index where long text starts inside FULL_TTS_TEXT (used for highlight offset)
private val LONG_TEXT_OFFSET = SHORT_TEXT.length + 1

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ViewDetailsScreen() {

    val context = LocalContext.current
    val tts = remember { TTSManager(context) }
    val listState = rememberLazyListState()

    // navigator
    val navigator = LocalNavigator.current

    DisposableEffect(Unit) {
        tts.setListener()
        onDispose { tts.release() }
    }

    // Auto-scroll: when the highlighted word moves past the short description,


    LaunchedEffect(tts.startIndex) {
        if (!tts.hasStarted) return@LaunchedEffect

        val layoutInfo = listState.layoutInfo
        val viewportEnd = layoutInfo.viewportEndOffset

        // Find the last fully visible item index
        val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@LaunchedEffect

        if (tts.startIndex <= SHORT_TEXT.length) {
            // Still in short description (item 2) — only scroll up if not visible
            if (lastVisibleIndex < 2) {
                listState.animateScrollToItem(index = 2)
            }
        } else {
            // In long description (item 3)
            val offsetInLong = (tts.startIndex - LONG_TEXT_OFFSET).coerceAtLeast(0)

            // Check if item 3 is already visible
            val item3Info = layoutInfo.visibleItemsInfo.firstOrNull { it.index == 3 }

            if (item3Info != null) {
                // Item 3 is on screen — calculate approx y of current word within it
                val charsPerLine = 45
                val lineHeight = 60 // px
                val approxLineInItem = offsetInLong / charsPerLine
                val approxYInItem = approxLineInItem * lineHeight
                val wordY = item3Info.offset + approxYInItem

                // Only scroll if the word is within 120px of the bottom edge (about to go offscreen)
                if (wordY > viewportEnd - 120) {
                    listState.animateScrollToItem(
                        index = 3,
                        scrollOffset = (approxYInItem - viewportEnd / 2).coerceAtLeast(0)
                    )
                }
                // If word is comfortably visible — do nothing, no jump
            } else if (lastVisibleIndex < 3) {
                // Item 3 not on screen yet — scroll to it
                listState.animateScrollToItem(index = 3)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = rememberNotchHeightDp().value),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(painterResource(R.drawable.left_arrow), "",
                modifier = Modifier.shrinkClick{
                    navigator.pop()
                })
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(painter = painterResource(R.drawable.translate), "")
                Icon(painterResource(R.drawable.more_vert), "")
            }
        }

        Box(
            modifier = Modifier
                .weight(9f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {

                // item 0 — header
                item {
                    zText(
                        "Aquaculture", brandBlue, 12, 0,
                        modifier = Modifier
                            .background(lightBlueF0)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    spacer(8)
                    zText("Best Practices to Grow Fish", gray48, 18, 0)
                    spacer(8)
                }

                // item 1 — media area
                item {
                    MediaAreaVideo()
                }

                // item 2 — short description with highlight
                item {
                    val annotatedShort = remember(tts.startIndex, tts.endIndex) {
                        highlightedText(SHORT_TEXT, tts.startIndex, tts.endIndex)
                    }
                    Column {
                        spacer(8)
                        zText("Short Description", gray48, 16, 0)
                        spacer(4)
                        Text(
                            text = annotatedShort,
                            fontSize = textUnit(14),
                            color = gray48,
                            lineHeight = textUnit(24)
                        )
                        spacer(16)
                    }
                }

                // item 3 — long description with highlight
                // Highlight indices are offset by LONG_TEXT_OFFSET so they map correctly
                item {
                    val annotatedLong = remember(tts.startIndex, tts.endIndex) {
                        val localStart = (tts.startIndex - LONG_TEXT_OFFSET).coerceAtLeast(0)
                        val localEnd   = (tts.endIndex   - LONG_TEXT_OFFSET).coerceAtLeast(0)
                        highlightedText(LONG_TEXT, localStart, localEnd)
                    }
                    Column {
                        zText("Long Description", gray48, 16, 0)
                        spacer(4)
                       Text(
                            text = annotatedLong,
                            fontSize = textUnit(14),
                            color = gray48,
                            lineHeight = textUnit(24)
                        )
                        spacer(100)
                    }
                }
            }

            FloatingActionBar(
                tts = tts,
                fullText = FULL_TTS_TEXT,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaAreaVideo() {

    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(
                MediaItem.fromUri(
                    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                )
            )
            prepare()
        }
    }

    var duration by remember { mutableStateOf(0L) }
    var position by remember { mutableStateOf(0L) }
    var sliderPosition by remember { mutableStateOf(0f) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    /// Player Listener
    DisposableEffect(player) {

        val listener = object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                isLoading = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    duration = player.duration
                }
            }

            override fun onIsPlayingChanged(play: Boolean) {
                isPlaying = play
            }
        }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    /// Position updater
    LaunchedEffect(player) {
        while (true) {
            position = player.currentPosition
            sliderPosition = position.toFloat()
            delay(500)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(264.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(24.dp)
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            /// Video
            ContentFrame(
                player = player,
                contentScale = ContentScale.FillBounds
            )

            /// Loading
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator(color = Color.White)
                }
            }

            /// Play Button
            if (!isPlaying && !isLoading) {
                Image(
                    painter = painterResource(R.drawable.video_play),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .shrinkClick {
                            if (player.isPlaying) player.pause()
                            else player.play()
                        }
                )
            }

            /// Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = {
                        player.seekTo(sliderPosition.toLong())
                    },
                    valueRange = 0f..duration.toFloat(),

                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color.White, CircleShape)
                        )
                    },

                    modifier = Modifier
                        .weight(1f)
                        .scale(scaleY = 0.8f , scaleX = 1f),

                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(.3f)
                    )
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "${formatTime(position)} / ${formatTime(duration)}",
                    color = Color.White,
                    fontSize = textUnit(12)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingActionBar(
    tts: TTSManager,
    fullText: String,
    modifier: Modifier
) {
    var speed by remember { mutableStateOf(1f) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        primaryWhite.copy(0f),
                        primaryWhite.copy(.85f),
                        primaryWhite
                    )
                )
            )
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                //.padding(bottom = 8.dp)
                .clip(RoundedCornerShape(6.dp))
                .then(
                    if (tts.hasStarted)
                        Modifier.border(1.dp , black1A , RoundedCornerShape(6.dp))
                    else Modifier
                )

            , verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // TTS controls — slide up when speaking, hidden otherwise
            AnimatedVisibility(
                visible = tts.hasStarted,
                enter = expandVertically(expandFrom = Alignment.Bottom),
                exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryWhite)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(
                            if (tts.isSpeaking) R.drawable.pause_icon else R.drawable.play_icon
                        ),
                        contentDescription = "Play/Pause",
                        modifier = Modifier
                            .size(22.dp)
//                            .shrinkClick {
//                                if (tts.isSpeaking) tts.pause() else tts.speak(fullText)
//                            }

                            .shrinkClick {
                                when {
                                    tts.isSpeaking        -> tts.pause()
                                    tts.hasStarted        -> tts.resume()   // paused mid-way — resume
                                    else                  -> tts.speak(fullText)  // fresh start
                                }
                            }
                    )

                    Slider(
                        value = tts.startIndex.toFloat(),
                        onValueChange = {},
                        valueRange = 0f..fullText.length.toFloat(),
                        enabled = false,
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(brandBlue, CircleShape)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            disabledThumbColor = brandBlue,
                            disabledActiveTrackColor = brandBlue,
                            disabledInactiveTrackColor = brandBlue.copy(.25f)
                        )
                    )

                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                //.background(brandBlue.copy(.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .shrinkClick { expanded = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${speed}x", color = black1A, fontSize = textUnit(14))
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf(1f, 1.5f, 2f, 3f).forEach { s ->
                                DropdownMenuItem(onClick = {
                                    speed = s
                                    tts.setSpeed(s)
                                    expanded = false
                                }) {
                                    Text("${s}x")
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .dropShadow(
                        shape = RoundedCornerShape(4.dp),
                        shadow = Shadow(radius = 4.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(7f)
                        .background(grayF5),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(painter = painterResource(R.drawable.like_vd), "")
                    zText("20.1k", gray48, 16, 2)
                    Image(painter = painterResource(R.drawable.comment_vd), "")
                    zText("20.1k", gray48, 16, 2)
                    Image(painter = painterResource(R.drawable.save_vd), "")
                    zText("Save", gray48, 16, 2)
                }

                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(3f)
                        .background(brandBlue)
                        .shrinkClick {
                            if (tts.isSpeaking) tts.pause() else tts.speak(fullText)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(painter = painterResource(R.drawable.audio_ttf), "")
                    zText("Audio", primaryWhite, 16, 2)
                }
            }
        }
    }
}*/


import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alpha.selfemployment.CommonMoreOptions
import com.alpha.selfemployment.Views.Home.GoogleTranslate.LanguageMapper
import com.alpha.selfemployment.Views.Home.GoogleTranslate.TranslationHelper
import com.alpha.selfemployment.Views.Home.Videos.domain.model.PostPropertyData
import com.alpha.selfemployment.Views.SharedRepository
import com.alpha.selfemployment.YoutubePlayerScreen
import com.alpha.selfemployment.dateAgo
import com.alpha.selfemployment.extractYoutubeId
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.util.Locale
import kotlin.math.absoluteValue

// ── All content in one place — UI and TTS both use the exact same strings ───

//private const val SHORT_TEXT =
//    "Successful crop growth begins with selecting suitable seeds for your local climate and soil. " +
//            "Improve soil fertility by tilling and adding organic matter."
//
//private const val LONG_TEXT =
//    "To successfully grow crops, start by selecting the right seeds for your climate and soil type. " +
//            "Prepare your soil by tilling and adding organic matter to enhance fertility. " +
//            "Ensure proper irrigation by setting up a watering schedule that meets the needs of your plants. " +
//            "Regularly monitor for pests and diseases, and apply organic pesticides when necessary. " +
//            "Finally, harvest your crops at the right time to enjoy the best flavor and nutrition. " +
//            "To successfully grow crops, start by selecting the right seeds for your climate and soil type. " +
//            "Prepare your soil by tilling and adding organic matter to enhance fertility. " +
//            "Ensure proper irrigation by setting up a watering schedule that meets the needs of your plants. " +
//            "Regularly monitor for pests and diseases, and apply organic pesticides when necessary. " +
//            "Finally, harvest your crops at the right time to enjoy the best flavor and nutrition. " +
//            "To successfully grow crops, start by selecting the right seeds for your climate and soil type. " +
//            "Prepare your soil by tilling and adding organic matter to enhance fertility. " +
//            "Ensure proper irrigation by setting up a watering schedule that meets the needs of your plants. " +
//            "Regularly monitor for pests and diseases, and apply organic pesticides when necessary. " +
//            "Finally, harvest your crops at the right time to enjoy the best flavor and nutrition."
//
//// TTS speaks this exact string — short → long, no stop
//private const val FULL_TTS_TEXT = "$SHORT_TEXT $LONG_TEXT"

// Index where long text starts inside FULL_TTS_TEXT (used for highlight offset)
//private val LONG_TEXT_OFFSET = SHORT_TEXT.length + 1

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ViewDetailsScreen(
    postId : Int,
    sharedRepo : SharedRepository = koinInject()
) {


    val postFlow = remember(postId) {
        sharedRepo.getPostByIdFlow(postId)
    }

    val data by postFlow.collectAsStateWithLifecycle(initialValue = null)

    val context = LocalContext.current
    val tts = remember { TTSManager(context) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // navigator
    val navigator = LocalNavigator.current

    val FULL_TTS_TEXT = "${data?.post_property?.short_description} ${data?.post_property?.long_description}"

    // ── Read app language from SharedPreferences ──────────────────────────────
    // Replace "app_prefs" and "app_language" with your actual prefs name & key
    val prefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    val appLanguage = remember { prefs.getString("app_language", "english") ?: "english" }

    // ── Translation state ─────────────────────────────────────────────────────
    var isTranslated by remember { mutableStateOf(false) }
    var isTranslating by remember { mutableStateOf(false) }
    var translatedShortText by remember { mutableStateOf("") }
    var translatedLongText by remember { mutableStateOf("") }
    var translatedFullText by remember { mutableStateOf("") }

    // ── Active display texts — switches between original and translated ────────
    val activeShortText = if (isTranslated) translatedShortText else data?.post_property?.short_description
    val activeLongText = if (isTranslated) translatedLongText else data?.post_property?.long_description
    val activeFullText = if (isTranslated) translatedFullText else FULL_TTS_TEXT

    // Recalculated offset — translated short text may differ in length
    val activeLongTextOffset = activeShortText?.length?.plus(1)

    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    DisposableEffect(Unit) {
        tts.setListener()
        onDispose { tts.release() }
    }

    // Auto-scroll: when the highlighted word moves past the short description,
    LaunchedEffect(tts.startIndex) {
        if (!tts.hasStarted) return@LaunchedEffect

        val layoutInfo = listState.layoutInfo
        val viewportEnd = layoutInfo.viewportEndOffset

        // Find the last fully visible item index
        val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@LaunchedEffect

        if (tts.startIndex <= (activeShortText?.length ?: 0)) {
            // Still in short description (item 2) — only scroll up if not visible
            if (lastVisibleIndex < 2) {
                listState.animateScrollToItem(index = 2)
            }
        } else {
            // In long description (item 3)
            val offsetInLong = (tts.startIndex - (activeLongTextOffset ?: 0)).coerceAtLeast(0)

            // Check if item 3 is already visible
            val item3Info = layoutInfo.visibleItemsInfo.firstOrNull { it.index == 3 }

            if (item3Info != null) {
                // Item 3 is on screen — calculate approx y of current word within it
                val charsPerLine = 45
                val lineHeight = 60 // px
                val approxLineInItem = offsetInLong / charsPerLine
                val approxYInItem = approxLineInItem * lineHeight
                val wordY = item3Info.offset + approxYInItem

                // Only scroll if the word is within 120px of the bottom edge (about to go offscreen)
                if (wordY > viewportEnd - 120) {
                    listState.animateScrollToItem(
                        index = 3,
                        scrollOffset = (approxYInItem - viewportEnd / 2).coerceAtLeast(0)
                    )
                }
                // If word is comfortably visible — do nothing, no jump
            } else if (lastVisibleIndex < 3) {
                // Item 3 not on screen yet — scroll to it
                listState.animateScrollToItem(index = 3)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = rememberNotchHeightDp().value),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(painterResource(R.drawable.left_arrow), "",
                modifier = Modifier.shrinkClick {
                    navigator.pop()
                })
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                // ── Translate icon with loading + toggle ──────────────────────
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTranslating) {
                        // Show spinner while translation API call is in progress
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = brandBlue
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.translate),
                            contentDescription = "Translate",
                            // Tint green when translated is active so user sees the toggle state
                            colorFilter = if (isTranslated) ColorFilter.tint(brandBlue) else null,
                            modifier = Modifier.shrinkClick {
                                if (isTranslated) {
                                    // ── Toggle back to original ───────────────
                                    isTranslated = false
                                    tts.pause()
                                    tts.setLocale(Locale.ENGLISH)
                                    tts.speak(FULL_TTS_TEXT)
                                } else {
                                    // ── Translate then speak ──────────────────
                                    scope.launch {
                                        isTranslating = true
                                        tts.pause()

                                        val (langCode, locale) = LanguageMapper.getLanguageConfig(appLanguage)

                                        // Translate short + long concurrently
                                        val shortDeferred = async {
                                            TranslationHelper.translate(data?.post_property?.short_description ?:"", langCode)
                                        }
                                        val longDeferred = async {
                                            TranslationHelper.translate(data?.post_property?.long_description ?: "", langCode)
                                        }

                                        translatedShortText = shortDeferred.await()
                                        translatedLongText = longDeferred.await()
                                        translatedFullText = "${translatedShortText} ${translatedLongText}"

                                        isTranslating = false
                                        isTranslated = true

                                        // Set locale THEN speak from start in translated language
                                        tts.setLocale(locale)
                                        tts.speak(translatedFullText)
                                    }
                                }
                            }
                        )
                    }
                }

                CommonMoreOptions()
            }
        }

        Box(
            modifier = Modifier
                .weight(9f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {

                // item 0 — header
                item {
                    zText(
                        data?.post_property?.category ?: "", brandBlue, 12, 0,
                        modifier = Modifier
                            .background(lightBlueF0)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    spacer(8)
                    zText(data?.post_property?.title ?: "", gray48, 18, 0)
                    spacer(8)
                }

                // item 1 — media area
                item {
                    when(data?.post_property?.post_type){
                        "1" -> {
                            MediaAreaVideo(data)
                        }
                        "2" -> {
                            MediaAreaYoutube(data)
                        }
                        "3" -> {
                            MediaAreaImages(data)
                        }
                    }

                }

                // item 2 — short description with highlight
                item {
                    val annotatedShort = remember(tts.startIndex, tts.endIndex, activeShortText) {
                        highlightedText(activeShortText ?:"", tts.startIndex, tts.endIndex)
                    }
                    Column {
                        spacer(8)
                        zText("Short Description", gray48, 16, 0)
                        spacer(4)
                        Text(
                            text = annotatedShort,
                            fontSize = textUnit(14),
                            color = gray48,
                            lineHeight = textUnit(24)
                        )
                        spacer(16)
                    }
                }

                // item 3 — long description with highlight
                // Highlight indices are offset by activeLongTextOffset so they map correctly
                item {
                    val annotatedLong = remember(tts.startIndex, tts.endIndex, activeLongText) {
                        val localStart = (tts.startIndex - (activeLongTextOffset ?: 0)).coerceAtLeast(0)
                        val localEnd = (tts.endIndex - (activeLongTextOffset ?: 0)).coerceAtLeast(0)
                        highlightedText(activeLongText ?: "", localStart, localEnd)
                    }
                    Column {
                        zText("Long Description", gray48, 16, 0)
                        spacer(4)
                        Text(
                            text = annotatedLong,
                            fontSize = textUnit(14),
                            color = gray48,
                            lineHeight = textUnit(24)
                        )
                        spacer(100)
                    }
                }
            }

            if (showScrollToTop) {
                Image(
                    painter = painterResource(R.drawable.scroll_to_top),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .align(Alignment.BottomEnd)
                        .shrinkClick {
                            scope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                )
            }

            FloatingActionBar(
                tts = tts,
                fullText = activeFullText,   // ← always active text so play/pause/resume works correctly
                data
                , onLikeClick = {
                    if (data?.post_property?.post_type == "1" || data?.post_property?.post_type == "2" )
                        sharedRepo.toggleLike(data?.user_post_id ?: 0)
                    else
                        sharedRepo.toggleLikeArticles(data?.user_post_id ?: 0)
                }
                , onSaveClick = {
                    if (data?.post_property?.post_type == "1" || data?.post_property?.post_type == "2" )
                        sharedRepo.toggleSave(data?.user_post_id ?: 0)
                    else
                        sharedRepo.toggleSaveArticles(data?.user_post_id ?: 0)
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ── MediaAreaVideo — unchanged ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaAreaVideo(data: PostPropertyData?) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(
                MediaItem.fromUri(
                    data?.post_property?.video ?: ""
                )
            )
            prepare()
        }
    }

    var duration by remember { mutableStateOf(0L) }
    var position by remember { mutableStateOf(0L) }
    var sliderPosition by remember { mutableStateOf(0f) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isLoading = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    duration = player.duration
                }
            }
            override fun onIsPlayingChanged(play: Boolean) {
                isPlaying = play
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player) {
        while (true) {
            position = player.currentPosition
            sliderPosition = position.toFloat()
            delay(500)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(264.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(24.dp)
    )
    {
        Box(modifier = Modifier.fillMaxSize()) {

            ContentFrame(player = player, contentScale = ContentScale.FillBounds)

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularWavyProgressIndicator(color = Color.White)
                }
            }

            if (!isPlaying && !isLoading) {
                Image(
                    painter = painterResource(R.drawable.video_play),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .shrinkClick {
                            if (player.isPlaying) player.pause()
                            else player.play()
                        }
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = { player.seekTo(sliderPosition.toLong()) },
                    valueRange = 0f..duration.toFloat(),
                    thumb = {
                        Box(modifier = Modifier.size(12.dp).background(Color.White, CircleShape))
                    },
                    modifier = Modifier.weight(1f).scale(scaleY = 0.8f, scaleX = 1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(.3f)
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text("${formatTime(position)} / ${formatTime(duration)}", color = Color.White, fontSize = textUnit(12))
            }
        }
    }
}


@Composable
fun MediaAreaYoutube(data: PostPropertyData?){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(264.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(24.dp)
    )
    {
        Box(modifier = Modifier.fillMaxSize()) {
            val videoId = remember(data?.post_property?.video) {
                extractYoutubeId(data?.post_property?.video ?: "")
            }
            YoutubePlayerScreen(videoId,
                true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}



@Composable
fun MediaAreaImages(data: PostPropertyData?){

    data?.post_property?.images?.let { images ->

        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            val pagerState = rememberPagerState(
                pageCount = { images.size }
            )

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 4.dp), // 👈 4dp peek on both sides
                pageSpacing = 0.dp, // 👈 no gap needed
                modifier = Modifier
                    .fillMaxWidth()
                    .height(264.dp)
                , verticalAlignment = Alignment.CenterVertically
            ) { page ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth() // 👈 full width (minus the 4dp padding on each side)
                        .graphicsLayer {
                            val pageOffset =
                                ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                            val scale = lerp(0.92f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                            scaleX = scale
                            scaleY = scale

                            alpha = lerp(0.6f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFD0E8C8)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = data?.post_property?.images?.get(page)?.articles_photo,
                            contentDescription = null
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            //PagerIndicator(pageCount = data.post_property.images.size, currentPage = pagerState.currentPage)

            Spacer(Modifier.height(8.dp))
        }

    }

}


// ── FloatingActionBar — unchanged ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingActionBar(
    tts: TTSManager,
    fullText: String,
    data: PostPropertyData?,
    onLikeClick : () -> Unit,
    onSaveClick : () -> Unit,
    modifier: Modifier
) {
    var speed by remember { mutableStateOf(1f) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(primaryWhite.copy(0f), primaryWhite.copy(.85f), primaryWhite)
                )
            )
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .then(
                    if (tts.hasStarted)
                        Modifier.border(1.dp, black1A, RoundedCornerShape(6.dp))
                    else Modifier
                ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AnimatedVisibility(
                visible = tts.hasStarted,
                enter = expandVertically(expandFrom = Alignment.Bottom),
                exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryWhite)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(
                            if (tts.isSpeaking) R.drawable.pause_icon else R.drawable.play_icon
                        ),
                        contentDescription = "Play/Pause",
                        modifier = Modifier
                            .size(22.dp)
                            .shrinkClick {
                                when {
                                    tts.isSpeaking -> tts.pause()
                                    tts.hasStarted -> tts.resume()
                                    else -> tts.speak(fullText)
                                }
                            }
                    )
                    Slider(
                        value = tts.startIndex.toFloat(),
                        onValueChange = {},
                        valueRange = 0f..fullText.length.toFloat(),
                        enabled = false,
                        thumb = {
                            Box(modifier = Modifier.size(10.dp).background(brandBlue, CircleShape))
                        },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            disabledThumbColor = brandBlue,
                            disabledActiveTrackColor = brandBlue,
                            disabledInactiveTrackColor = brandBlue.copy(.25f)
                        )
                    )
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .shrinkClick { expanded = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${speed}x", color = black1A, fontSize = textUnit(14))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf(1f, 1.5f, 2f, 3f).forEach { s ->
                                DropdownMenuItem(onClick = {
                                    speed = s
                                    tts.setSpeed(s)
                                    expanded = false
                                }) {
                                    Text("${s}x")
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .dropShadow(shape = RoundedCornerShape(4.dp), shadow = Shadow(radius = 4.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(7f)
                        .background(grayF5),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(painter = painterResource(if (data?.is_liked == 1)R.drawable.like_filled else R.drawable.like_vd), "",
                        modifier = Modifier.size(16.dp).shrinkClick{
                            onLikeClick()
                        })

                    zText(data?.total_likes.toString(), gray48, 16, 2)

                    Image(painter = painterResource(R.drawable.comment_vd), "")

                    zText(data?.total_comments.toString(), gray48, 16, 2)

                    Image(painter = painterResource(if (data?.is_saved == 1)R.drawable.save_filled else R.drawable.save_vd), "",
                        modifier = Modifier.size(16.dp).shrinkClick{
                            onSaveClick()
                        })

                    zText("Save", gray48, 16, 2)
                }
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(3f)
                        .background(brandBlue)
                        .shrinkClick {
                            if (tts.isSpeaking) tts.pause() else tts.speak(fullText)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(painter = painterResource(R.drawable.audio_ttf), "")
                    zText("Audio", primaryWhite, 16, 2)
                }
            }
        }
    }
}




