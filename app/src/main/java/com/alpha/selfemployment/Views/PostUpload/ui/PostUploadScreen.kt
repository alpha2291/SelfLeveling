package com.alpha.selfemployment.Views.PostUpload.ui

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.media3.ui.compose.ContentFrame
import coil.compose.SubcomposeAsyncImage
import com.alpha.selfemployment.R
import com.alpha.selfemployment.RequiredTitle
import com.alpha.selfemployment.ResultHandler
import com.alpha.selfemployment.UA_SpecificCategory
import com.alpha.selfemployment.UploadDropdownField
import com.alpha.selfemployment.UploadTextField
import com.alpha.selfemployment.Views.PostUpload.domain.model.PostUploadGetCategoryResponseData
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadAPIViewModel
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadUIViewModel
import com.alpha.selfemployment.createVideoThumbnail
import com.alpha.selfemployment.fontFamily
import com.alpha.selfemployment.navigation.LocalNavigator
import com.alpha.selfemployment.navigation.Screen
import com.alpha.selfemployment.rememberNotchHeightDp
import com.alpha.selfemployment.shrinkClick
import com.alpha.selfemployment.spacer
import com.alpha.selfemployment.str
import com.alpha.selfemployment.textUnit
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.gray48
import com.alpha.selfemployment.ui.theme.gray66
import com.alpha.selfemployment.ui.theme.lightBlueF0
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite
import com.alpha.selfemployment.ui.theme.redE54
import com.alpha.selfemployment.zText
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt

// ─── Navigation state ─────────────────────────────────────────────────────────


//private sealed class Screen {
//    object Upload : Screen()
//    data class Trim(val uri: Uri) : Screen()
//    data class Result(val outputPath: String, val durationMs: Long) : Screen()
//}

// ─── Root ─────────────────────────────────────────────────────────────────────

/*@Composable
fun VideoEditorScreen() {
    var screen by remember { mutableStateOf<Screen>(Screen.Upload) }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            (slideInHorizontally { it } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it } + fadeOut())
        },
        label = "screen_transition"
    ) { current ->
        when (current) {
//            is Screen.Upload -> UploadVideoScreen(
//                onVideoSelected = { screen = Screen.Trim(it) }
//            )
            is Screen.Trim -> VideoTrimScreen(
                videoUri = current.uri,
                onBack   = { screen = Screen.Upload },
                onSaved  = { path, dur -> screen = Screen.Result(path, dur) }
            )
            is Screen.Result -> TrimmedVideoScreen(
                outputPath = current.outputPath,
                durationMs = current.durationMs,
                onRetrim   = { screen = Screen.Upload },
                onConfirm  = { *//* TODO: proceed to post upload flow *//* }
            )
            else -> {}
        }
    }

    // ─── Trimmed result screen ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrimmedVideoScreen(
    outputPath: String,
    durationMs: Long,
    onRetrim: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    val uri     = remember { Uri.fromFile(File(outputPath)) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }

    fun formatMs(ms: Long): String {
        val s = ms / 1000
        return "%d:%02d".format(s / 60, s % 60)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        TopAppBar(
            title = { Text("Preview", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onRetrim) {
                    Icon(
                        painter        = painterResource(R.drawable.left_arrow),
                        contentDescription = "Back",
                        tint               = Color.White
                    )
                }
            }
        )

        // Full video preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory  = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    }
                }
            )
        }

        // Bottom info + actions card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status row
            Row(
                modifier             = Modifier.fillMaxWidth(),
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Duration pill
                Row(
                    modifier          = Modifier
                        .background(Color(0xFF2C2C2C), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter        = painterResource(R.drawable.play),
                        contentDescription = null,
                        tint               = Color(0xFFFFCC00),
                        modifier           = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text       = formatMs(durationMs),
                        color      = Color.White,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Success badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = "Trimmed successfully",
                        color      = Color(0xFF4CAF50),
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier             = Modifier
                            .size(20.dp)
                            .background(Color(0xFF4CAF50), CircleShape),
                        contentAlignment     = Alignment.Center
                    ) {
                        Icon(
                            painter        = painterResource(R.drawable.check_circle),
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // File name
            Text(
                text     = outputPath.substringAfterLast("/"),
                color    = Color(0xFF555555),
                fontSize = 11.sp,
            )

            // Buttons
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick  = onRetrim,
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
//                    Icon(
//                        Icons.Default.Refresh,
//                        contentDescription = null,
//                        modifier           = Modifier.size(16.dp)
//                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Re-trim")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick  = onConfirm,
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
                ) {
                    Text(
                        text       = "Use Video",
                        color      = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FilmstripTrimSlider11111(
    frames: List<Bitmap>,
    durationMs: Long,
    startMs: Long,
    endMs: Long,
    currentPositionMs: Long,
    onStartChange: (Long) -> Unit,
    onEndChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    stripHeight: Dp = 64.dp,
    handleWidth: Dp = 18.dp,
) {
    val handleColor = black1A
    val overlayColor = Color(0x88000000)
    val borderColor = black1A
    val bgColor = Color(0xFF1A1A1A)

    val density = LocalDensity.current
    val handleWidthPx = with(density) { handleWidth.toPx() }

    var totalWidthPx by remember { mutableIntStateOf(0) }

    fun msToFrac(ms: Long): Float {
        return if (durationMs > 0L) {
            (ms.toFloat() / durationMs).coerceIn(0f, 1f)
        } else 0f
    }

    fun fracToMs(f: Float): Long {
        return (f.coerceIn(0f, 1f) * durationMs).toLong()
    }

    fun formatMs(ms: Long): String {
        val s = ms / 1000
        return "%d:%02d".format(s / 60, s % 60)
    }

    var startFrac by remember(startMs, durationMs) {
        mutableFloatStateOf(msToFrac(startMs))
    }

    var endFrac by remember(endMs, durationMs) {
        mutableFloatStateOf(msToFrac(endMs))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(stripHeight)
                .background(bgColor, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .onGloballyPositioned { totalWidthPx = it.size.width }
        ) {
            if (totalWidthPx <= 0) return@Box

            Row(modifier = Modifier.fillMaxSize()) {
                frames.forEach { frame ->
                    Image(
                        bitmap = frame.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val startX = startFrac * w
                val endX = endFrac * w
                val stroke = 3.dp.toPx()

                val rawPlayheadX = msToFrac(currentPositionMs) * w
                val playheadX = rawPlayheadX.coerceIn(startX, endX)

                if (startX > 0f) {
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset.Zero,
                        size = Size(startX, h)
                    )
                }

                if (endX < w) {
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset(endX, 0f),
                        size = Size(w - endX, h)
                    )
                }

                drawRect(
                    color = borderColor,
                    topLeft = Offset(startX, 0f),
                    size = Size(endX - startX, stroke)
                )

                drawRect(
                    color = borderColor,
                    topLeft = Offset(startX, h - stroke),
                    size = Size(endX - startX, stroke)
                )

                drawLine(
                    color = Color.Black,
                    start = Offset(playheadX, 0f),
                    end = Offset(playheadX, h),
                    strokeWidth = 2.dp.toPx()
                )
            }

            val maxOffset = max(0, totalWidthPx - handleWidthPx.roundToInt())

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (startFrac * totalWidthPx - handleWidthPx / 2)
                                .roundToInt()
                                .coerceIn(0, maxOffset),
                            y = 0
                        )
                    }
                    .width(handleWidth)
                    .fillMaxHeight()
                    .background(
                        color = handleColor,
                        shape = RoundedCornerShape(
                            topStart = 6.dp,
                            bottomStart = 6.dp
                        )
                    )
                    .pointerInput(totalWidthPx, durationMs, endFrac) {
                        detectHorizontalDragGestures { _, drag ->
                            val newFrac = (startFrac + drag / totalWidthPx)
                                .coerceIn(0f, endFrac - 0.01f)
                            startFrac = newFrac
                            onStartChange(fracToMs(newFrac))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                HandleGrip()
            }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (endFrac * totalWidthPx - handleWidthPx / 2)
                                .roundToInt()
                                .coerceIn(0, maxOffset),
                            y = 0
                        )
                    }
                    .width(handleWidth)
                    .fillMaxHeight()
                    .background(
                        color = handleColor,
                        shape = RoundedCornerShape(
                            topEnd = 6.dp,
                            bottomEnd = 6.dp
                        )
                    )
                    .pointerInput(totalWidthPx, durationMs, startFrac) {
                        detectHorizontalDragGestures { _, drag ->
                            val newFrac = (endFrac + drag / totalWidthPx)
                                .coerceIn(startFrac + 0.01f, 1f)
                            endFrac = newFrac
                            onEndChange(fracToMs(newFrac))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                HandleGrip()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            Text(
                text = formatMs(startMs),
                color = black1A,
                fontSize = textUnit(14),
                fontFamily = fontFamily(3),
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = "Drag to adjust video",
                color = Color.Gray,
                fontSize = textUnit(14),
                fontFamily = fontFamily(3),
                modifier = Modifier.align(Alignment.Center)
            )

            Text(
                text = formatMs(endMs),
                color = black1A,
                fontSize = textUnit(14),
                fontFamily = fontFamily(3),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun HandleGrip11111() {
    Canvas(
        modifier = Modifier
            .width(10.dp)
            .height(24.dp)
    ) {
        val stroke = 1.5.dp.toPx()
        val spacing = size.width / 4f

        for (i in 1..3) {
            val x = i * spacing
            drawLine(
                color = Color.White.copy(alpha = 0.85f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = stroke
            )
        }
    }
}





// ─── Trim helper ──────────────────────────────────────────────────────────────

fun trimVideowithpath(
    context: Context,
    uri: Uri,
    startMs: Long,
    endMs: Long,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val out = File(context.cacheDir, "trimmed_${System.currentTimeMillis()}.mp4")
    val mediaItem = MediaItem.Builder()
        .setUri(uri)
        .setClippingConfiguration(
            MediaItem.ClippingConfiguration.Builder()
                .setStartPositionMs(startMs)
                .setEndPositionMs(endMs)
                .build()
        ).build()

    Transformer.Builder(context)
        .addListener(object : Transformer.Listener {
            override fun onCompleted(
                composition: Composition,
                exportResult: ExportResult
            ) = onSuccess(out.absolutePath)

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException
            ) = onError(exportException.message ?: "Unknown error")
        })
        .build()
        .start(mediaItem, out.absolutePath)
}

}*/

// ─── Upload ───────────────────────────────────────────────────────────────────

@Composable
fun UploadVideoScreen(videoUri: Uri?) {

    val navigator = LocalNavigator.current

    videoUri?.let { it ->
        VideoTrimScreen(
            videoUri = it,
            onBack = {
                navigator.pop()
            },
            onSaved = {  videoUri, durationMs  ->
                navigator.navigate(Screen.UploadVideoDetails(videoUri = videoUri))
            }
        )
    }

}

// ─── Trim screen ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VideoTrimScreen(
    videoUri: Uri,
    onBack: () -> Unit = {},
    onSaved: (outputUri: Uri, durationMs: Long) -> Unit
) {
    val context = LocalContext.current
    val frames  = remember { mutableStateListOf<Bitmap>() }

    var durationMs by remember { mutableLongStateOf(0L) }
    var startMs    by remember { mutableLongStateOf(0L) }
    var endMs      by remember { mutableLongStateOf(0L) }
    var isTrimming by remember { mutableStateOf(false) }
    var trimError  by remember { mutableStateOf<String?>(null) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }

    // Add this state variable near your other remember variables:
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

// Add this listener inside your existing DisposableEffect(player) block,
// alongside the existing listener — or create a new one:
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            // player.release() — keep this if it's already in your DisposableEffect
        }
    }


    LaunchedEffect(player, startMs, endMs) {
        while (true) {
            val pos = player.currentPosition
            when {
                // If player goes past endMs — loop back to startMs
                pos >= endMs -> player.seekTo(startMs)
                // If player somehow went before startMs — nudge it forward
                pos < startMs -> player.seekTo(startMs)
            }
            delay(100L)
        }
    }

    LaunchedEffect(videoUri) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        durationMs = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLong() ?: 0L
        endMs = durationMs
        val frameCount = 10
        val intervalUs = if (frameCount > 0) durationMs * 1_000L / frameCount else 0L
        repeat(frameCount) { i ->
            retriever.getFrameAtTime(
                i * intervalUs,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )?.let { frames.add(it) }
        }
        retriever.release()
    }

    var currentPosition by remember { mutableLongStateOf(0L) }

    LaunchedEffect(player, startMs, endMs) {
        while (true) {
            val pos = player.currentPosition
            currentPosition = pos

            when {
                pos >= endMs -> player.seekTo(startMs)
                pos < startMs -> player.seekTo(startMs)
            }

            delay(50L)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
                .padding(horizontal = 16.dp)
                , contentAlignment = Alignment.Center
        )
        {
            Image(painter = painterResource(R.drawable.left_arrow) , "",
                modifier = Modifier.align(Alignment.CenterStart).shrinkClick{
                    onBack()
                })

            zText("Upload Video" , black1A , 24 , 0, modifier = Modifier.align(Alignment.Center))

            zText("Max 2min" , gray48, 14 , 2, modifier = Modifier.align(Alignment.CenterEnd))


        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(6f),
            contentAlignment = Alignment.Center
        )
        {
            ContentFrame(
                player = player,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (player.isPlaying) player.pause() else player.play()
                        }
                    )
                }
            )

            if (!isPlaying) {
                Image(
                    painter = painterResource(R.drawable.play_filled),
                    contentDescription = ""
                )
            }
        }




        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth().weight(1.5f)
        ) {
            if (durationMs > 0L && frames.isNotEmpty()) {
                FilmstripTrimSlider(
                    frames = frames,
                    durationMs = durationMs,
                    startMs = startMs,
                    endMs = endMs,
                    currentPositionMs = currentPosition,

                    onStartChange = { s ->
                        startMs = s
                        // If current position is now before the new start, seek to it
                        if (player.currentPosition < s) player.seekTo(s)
                    },
                    onEndChange = { e ->
                        endMs = e
                        // If current position is now past the new end, seek back to start
                        if (player.currentPosition > e) player.seekTo(startMs)
                    },
//                onStartChange = { s -> startMs = s; player.seekTo(s) },
//                onEndChange   = { e -> endMs = e },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(Color(0xFF2A2A2A), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        trimError?.let {
            Text(
                text     = "Error: $it",
                color    = Color.Red,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        spacer(4)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            , contentAlignment = Alignment.Center
        ){
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(black1A)
                    .shrinkClick{
                        if (!isTrimming && durationMs > 0L) {
                            isTrimming = true
                            trimError = null
                            val trimmedDuration = endMs - startMs
                            trimVideo(
                                context = context,
                                uri = videoUri,
                                startMs = startMs,
                                endMs = endMs,
                                onSuccess = { path ->
                                    isTrimming = false; onSaved(
                                    path,
                                    trimmedDuration
                                )
                                },
                                onError = { err -> isTrimming = false; trimError = err }
                            )
                        }
                        else {}
                    }
                , contentAlignment = Alignment.Center
            )
            {

                if (isTrimming) {
                    CircularWavyProgressIndicator(
                        modifier    = Modifier.size(18.dp),
                        color       = Color.White,
//                    strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                else {
                    zText("Next" , primaryWhite ,14 , 0)
                }
            }
        }


        spacer(8)
//        Button(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            enabled  = ,
//            onClick  = {
//
//            }
//        ) {
//
//        }
    }
}


@Composable
fun FilmstripTrimSlider(
    frames: List<Bitmap>,
    durationMs: Long,
    startMs: Long,
    endMs: Long,
    currentPositionMs: Long,
    onStartChange: (Long) -> Unit,
    onEndChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    stripHeight: Dp = 64.dp,
    handleWidth: Dp = 18.dp,
) {
    val handleColor = black1A
    val overlayColor = Color(0x88000000)
    val borderColor = black1A
    val bgColor = Color(0xFF1A1A1A)

    val density = LocalDensity.current
    val handleWidthPx = with(density) { handleWidth.toPx() }

    var totalWidthPx by remember { mutableIntStateOf(0) }

    val effectiveMinDurationMs = minOf(10_000L, durationMs)
    val minDurationFrac = if (durationMs > 0L) {
        effectiveMinDurationMs.toFloat() / durationMs.toFloat()
    } else 0f

    fun msToFrac(ms: Long): Float {
        return if (durationMs > 0L) {
            (ms.toFloat() / durationMs).coerceIn(0f, 1f)
        } else 0f
    }

    fun fracToMs(f: Float): Long {
        return (f.coerceIn(0f, 1f) * durationMs).toLong()
    }

    fun formatMs(ms: Long): String {
        val s = ms / 1000
        return "%d:%02d".format(s / 60, s % 60)
    }

    var startFrac by remember(startMs, durationMs) {
        mutableFloatStateOf(msToFrac(startMs))
    }

    var endFrac by remember(endMs, durationMs) {
        mutableFloatStateOf(msToFrac(endMs))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(stripHeight)
                .background(bgColor, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .onGloballyPositioned { totalWidthPx = it.size.width }
        ) {
            if (totalWidthPx <= 0) return@Box

            Row(modifier = Modifier.fillMaxSize()) {
                frames.forEach { frame ->
                    Image(
                        bitmap = frame.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val startX = startFrac * w
                val endX = endFrac * w
                val stroke = 3.dp.toPx()

                val rawPlayheadX = msToFrac(currentPositionMs) * w
                val playheadX = rawPlayheadX.coerceIn(startX, endX)

                if (startX > 0f) {
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset.Zero,
                        size = Size(startX, h)
                    )
                }

                if (endX < w) {
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset(endX, 0f),
                        size = Size(w - endX, h)
                    )
                }

                drawRect(
                    color = borderColor,
                    topLeft = Offset(startX, 0f),
                    size = Size(endX - startX, stroke)
                )

                drawRect(
                    color = borderColor,
                    topLeft = Offset(startX, h - stroke),
                    size = Size(endX - startX, stroke)
                )

                drawLine(
                    color = Color.Black,
                    start = Offset(playheadX, 0f),
                    end = Offset(playheadX, h),
                    strokeWidth = 2.dp.toPx()
                )
            }

            val maxOffset = max(0, totalWidthPx - handleWidthPx.roundToInt())

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (startFrac * totalWidthPx - handleWidthPx / 2)
                                .roundToInt()
                                .coerceIn(0, maxOffset),
                            y = 0
                        )
                    }
                    .width(handleWidth)
                    .fillMaxHeight()
                    .background(
                        color = handleColor,
                        shape = RoundedCornerShape(
                            topStart = 6.dp,
                            bottomStart = 6.dp
                        )
                    )
                    .pointerInput(totalWidthPx, durationMs, endFrac) {
                        detectHorizontalDragGestures { _, drag ->
                            val newFrac = (startFrac + drag / totalWidthPx)
                                .coerceIn(0f, endFrac - minDurationFrac)

                            startFrac = newFrac
                            onStartChange(fracToMs(newFrac))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                HandleGrip(isStartHandle = true)
            }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (endFrac * totalWidthPx - handleWidthPx / 2)
                                .roundToInt()
                                .coerceIn(0, maxOffset),
                            y = 0
                        )
                    }
                    .width(handleWidth)
                    .fillMaxHeight()
                    .background(
                        color = handleColor,
                        shape = RoundedCornerShape(
                            topEnd = 6.dp,
                            bottomEnd = 6.dp
                        )
                    )
                    .pointerInput(totalWidthPx, durationMs, startFrac) {
                        detectHorizontalDragGestures { _, drag ->
                            val newFrac = (endFrac + drag / totalWidthPx)
                                .coerceIn(startFrac + minDurationFrac, 1f)

                            endFrac = newFrac
                            onEndChange(fracToMs(newFrac))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                HandleGrip(isStartHandle = false)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            Text(
                text = formatMs(startMs),
                color = black1A,
                fontSize = textUnit(14),
                fontFamily = fontFamily(3),
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = "Drag to adjust video",
                color = Color.Gray,
                fontSize = textUnit(14),
                fontFamily = fontFamily(3),
                modifier = Modifier.align(Alignment.Center)
            )

            Text(
                text = formatMs(endMs),
                color = black1A,
                fontSize = textUnit(14),
                fontFamily = fontFamily(3),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun HandleGrip(
    isStartHandle: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                if (isStartHandle) R.drawable.left_arrow else R.drawable.right_arrow
            ),
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

fun trimVideo(
    context: Context,
    uri: Uri,
    startMs: Long,
    endMs: Long,
    onSuccess: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    val outFile = File(context.cacheDir, "trimmed_${System.currentTimeMillis()}.mp4")

    val mediaItem = MediaItem.Builder()
        .setUri(uri)
        .setClippingConfiguration(
            MediaItem.ClippingConfiguration.Builder()
                .setStartPositionMs(startMs)
                .setEndPositionMs(endMs)
                .build()
        )
        .build()

    Transformer.Builder(context)
        .addListener(object : Transformer.Listener {
            override fun onCompleted(
                composition: Composition,
                exportResult: ExportResult
            ) {
                onSuccess(outFile.toUri())
            }

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException
            ) {
                onError(exportException.message ?: "Unknown error")
            }
        })
        .build()
        .start(mediaItem, outFile.absolutePath)
}



@Composable
fun UploadVideoDetails(
    videoUri: Uri?,
    uiVm: PostUploadUIViewModel = koinViewModel(),
    postUploadApiVm: PostUploadAPIViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    val fields by uiVm.videoPostFields.collectAsStateWithLifecycle()
    val errors by uiVm.videoPostErrors.collectAsStateWithLifecycle()


    val isFormValid = remember(fields) {
        uiVm.isVideoFormValid()
    }

    LaunchedEffect(videoUri) {
        uiVm.setVideoUri(videoUri)
    }


    // Replace these with your actual api lists
    val categoryItems  = remember { mutableStateListOf<PostUploadGetCategoryResponseData>() }
    val languageItems  = remember { mutableStateListOf<PostUploadGetCategoryResponseData>() }


    LaunchedEffect(Unit) {
        postUploadApiVm.getCategories {
            result ->
            when(result) {
                is ResultHandler.Success -> {
                    categoryItems.clear()
                    categoryItems.addAll(result.data.data)
                }
                is ResultHandler.Error -> {

                }
                else -> {}
            }
        }
        postUploadApiVm.get_Language {
                result ->
            when(result) {
                is ResultHandler.Success -> {
                    languageItems.clear()
                    languageItems.addAll(result.data.data)
                }
                is ResultHandler.Error -> {}
                else -> {}
            }
        }
    }

//    val languageItems = listOf("English", "Hindi", "Tamil", "Telugu")

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.left_arrow),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .shrinkClick { navigator.pop() }
            )

            zText(
                str(R.string.upload_video),
                primaryBlack,
                24,
                0,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(8f)
                .padding(horizontal = 16.dp)
        )
        {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(.7f)
                            .height(360.dp)
                            .background(lightBlueF0),
                        contentAlignment = Alignment.Center
                    ) {
                        val thumbnail = remember(videoUri) {
                            videoUri?.let { createVideoThumbnail(context, it) }
                        }

                        SubcomposeAsyncImage(
                            model = thumbnail,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                spacer(8)
            }

            item {
                RequiredTitle(R.string.category)
                spacer(2)

                UploadDropdownField(
                    value = fields.category,
                    placeholder = R.string.select_category,
                    items = categoryItems,
                    error = errors.category,
                    onItemSelected = uiVm::updateVideoCategory
                )


                if (fields.category.equals("Others", ignoreCase = true)) {
                    spacer(8)

                    RequiredTitle(R.string.specific_category)
                    spacer(2)

                    UA_SpecificCategory(
                        value = fields.specificCategory,
                        error = fields.specificCategory,
                        onValueChange = uiVm::updateVideoSpecificCategory
                    )
                }

                spacer(8)

                RequiredTitle(R.string.title)
                spacer(2)

                UploadTextField(
                    value = fields.title,
                    onValueChange = uiVm::updateVideoTitle,
                    placeholder = R.string.give_a_title,
                    error = errors.title
                )

                spacer(8)

                RequiredTitle(R.string.language)
                spacer(2)

                zText(
                    str(R.string.language_sub_header),
                    gray66,
                    12,
                    2
                )

                spacer(2)

                UploadDropdownField(
                    value = fields.language,
                    placeholder = R.string.select_language,
                    items = languageItems,
                    error = errors.language,
                    onItemSelected = uiVm::updateVideoLanguage
                )

                spacer(8)

                RequiredTitle(R.string.short_description)
                spacer(2)

                UploadTextField(
                    value = fields.shortDesc,
                    onValueChange = uiVm::updateVideoShortDesc,
                    placeholder = R.string.start_typing_here,
                    error = errors.shortDesc,
                    minHeight = 112.dp,
                    singleLine = false
                )

                spacer(8)

                RequiredTitle(R.string.long_description)
                spacer(2)

                UploadTextField(
                    value = fields.longDesc,
                    onValueChange = uiVm::updateVideoLongDesc,
                    placeholder = R.string.start_typing_here,
                    error = errors.longDesc,
                    minHeight = 240.dp,
                    singleLine = false
                )

                spacer(16)
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            , verticalArrangement = Arrangement.Center
            , horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            HorizontalDivider()

            spacer(4)

            Box(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.8f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isFormValid) black1A else black1A.copy(.4f))
                    .shrinkClick {
                        if (uiVm.validateVideoForm()) {
                            navigator.navigate(Screen.VideoUploadPreview(videoUri))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                zText("Preview", primaryWhite, 16, 0)
            }
        }
    }
}


@Composable
fun VideoUploadPreview(videoUri: Uri){

    val context = LocalContext.current

    val navigator = LocalNavigator.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }



    // Add this state variable near your other remember variables:
    var isPlaying by remember { mutableStateOf(player.isPlaying) }


    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            // player.release() — keep this if it's already in your DisposableEffect
        }
    }



    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
            , contentAlignment = Alignment.Center
        ){
            Image(painter = painterResource(R.drawable.left_arrow) , "",
                modifier = Modifier.align(Alignment.CenterStart)
                    .shrinkClick{
                        navigator.pop()
                    }
            )

            zText("Preview", black1A, 24, 0)
        }


        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .weight(8f)
                .clip(RoundedCornerShape(8.dp))
            , contentAlignment = Alignment.Center
        ){
            ContentFrame(
                player = player,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (player.isPlaying) player.pause() else player.play()
                        }
                    )
                }
            )

            if (!isPlaying) {
                Image(
                    painter = painterResource(R.drawable.play_filled),
                    contentDescription = ""
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            , verticalArrangement = Arrangement.Center
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider()

            spacer(4)

            Box(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.8f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(  black1A )
                    .shrinkClick {

                    },
                contentAlignment = Alignment.Center
            ) {
                zText("Post now", primaryWhite, 16, 0)
            }
        }
    }
}


