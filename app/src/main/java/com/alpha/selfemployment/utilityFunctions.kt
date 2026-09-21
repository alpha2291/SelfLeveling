package com.alpha.selfemployment

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


// GlobalSnackbar.kt
import androidx.compose.foundation.border
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


// GlobalSnackbarHostComposable.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.alpha.selfemployment.ui.theme.black1A
import com.alpha.selfemployment.ui.theme.lightGreenEBF
import kotlinx.coroutines.flow.collectLatest


import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

fun createVideoThumbnail(
    context: Context,
    videoUri: Uri
): Uri? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)

        val bitmap = retriever.getFrameAtTime(
            0,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        )
        retriever.release()

        if (bitmap != null) {
            val thumbFile = File(
                context.cacheDir,
                "thumb_${System.currentTimeMillis()}.jpg"
            )

            FileOutputStream(thumbFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }

            thumbFile.toUri()
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun timeAgo(isoTime: String): String {
    return try {
        val instant = Instant.parse(isoTime)
        val now = Instant.now()

        val duration = Duration.between(instant, now)

        val seconds = duration.seconds

        when {
            seconds < 10 -> "Just now"
            seconds < 60 -> "Just now"

            seconds < 60 * 60 -> {
                val minutes = seconds / 60
                "$minutes min ago"
            }

            seconds < 60 * 60 * 24 -> {
                val hours = seconds / (60 * 60)
                "$hours hr ago"
            }

            seconds < 60 * 60 * 24 * 7 -> {
                val days = seconds / (60 * 60 * 24)
                "$days day${if (days > 1) "s" else ""} ago"
            }

            seconds < 60 * 60 * 24 * 30 -> {
                val weeks = seconds / (60 * 60 * 24 * 7)
                "$weeks week${if (weeks > 1) "s" else ""} ago"
            }

            seconds < 60 * 60 * 24 * 365 -> {
                val months = seconds / (60 * 60 * 24 * 30)
                "$months month${if (months > 1) "s" else ""} ago"
            }

            else -> {
                val years = seconds / (60 * 60 * 24 * 365)
                "$years year${if (years > 1) "s" else ""} ago"
            }
        }
    } catch (e: Exception) {
        ""
    }
}


fun dateAgo(
    isoTime: String,
    pattern: String = "dd MMM yyyy"
): String {
    return try {
        val instant = Instant.parse(isoTime)
        val zoneId = ZoneId.systemDefault()

        val localDate = instant.atZone(zoneId).toLocalDate()

        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
        localDate.format(formatter)
    } catch (e: Exception) {
        ""
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getDaysInMonth(yearMonth: YearMonth): List<LocalDate> {
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()

    val days = mutableListOf<LocalDate>()

    // Fill from previous month to align start day
    val startDayOfWeek = firstDay.dayOfWeek.value % 7
    val prevMonthDays = (1..startDayOfWeek).map {
        firstDay.minusDays(it.toLong())
    }.reversed()
    days.addAll(prevMonthDays)

    // Current month
    days.addAll((1..yearMonth.lengthOfMonth()).map { day ->
        yearMonth.atDay(day)
    })

    // Fill remaining to make full weeks
    while (days.size % 7 != 0) {
        days.add(days.last().plusDays(1))
    }

    return days
}

private var currentToast: Toast? = null

fun toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    currentToast?.cancel() // cancel previous toast
    currentToast = Toast.makeText(utils.activity, message, length)
    currentToast?.show()
}

private var currentToastNoInternet: Toast? = null

fun networkToast( length: Int = Toast.LENGTH_SHORT) {
    currentToastNoInternet?.cancel() // cancel previous toast
    currentToastNoInternet = Toast.makeText(utils.activity, utils.activity.getString(R.string.noInternet), length)
    currentToastNoInternet?.show()
}


private var currentToastApiError: Toast? = null

fun apiErrorToast( length: Int = Toast.LENGTH_SHORT) {
    currentToastApiError?.cancel() // cancel previous toast
    currentToastApiError = Toast.makeText(utils.activity, utils.activity.getString(R.string.apiError), length)
    currentToastApiError?.show()
}



@Composable
fun rememberNotchHeightDp(): State<Dp> {
    val context = LocalContext.current
    val density = LocalDensity.current

    val notchHeight = remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && context is Activity) {
            val insets = context.window.decorView.rootWindowInsets
            val cutout = insets?.displayCutout
            val notchPx = cutout?.safeInsetTop ?: 0

            notchHeight.value = with(density) { notchPx.toDp() } // ✅ convert px to dp
        }
    }

    return notchHeight
}



fun setSystemUIVisibility(hide: Boolean, mainActivity: MainActivity) {
    val window = mainActivity.window
    val controller = WindowCompat.getInsetsController(window, window.decorView)

    if (hide) {
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
        controller.show(WindowInsetsCompat.Type.systemBars())
    }
}

fun formatTime(ms: Long): String {

    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d", minutes, seconds)
}



fun Modifier.roundedDashedBorder(
    color: Color,
    strokeWidth: Dp,
    dashWidth: Dp,
    dashGap: Dp,
    cornerRadius: Dp
): Modifier = this.then(
    Modifier.drawWithCache {

        val stroke = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashWidth.toPx(), dashGap.toPx()),
                0f
            )
        )

        val radius = cornerRadius.toPx()

        onDrawBehind {
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(radius, radius),
                style = stroke
            )
        }
    }
)



object GlobalSnackbar {
    private val _messages = MutableSharedFlow<SnackbarData>(extraBufferCapacity = 64)
    val messages = _messages.asSharedFlow()

    data class SnackbarData(
//        val message: String,
        val duration: SnackbarDuration, val id : Int)

    /**
     * Non-suspending, safe call from any thread/composable/ViewModel.
     * It tries to emit synchronously and falls back to posting on Main if buffer is full.
     */
    fun show(
//        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,id : Int) {
        val emitted = _messages.tryEmit(SnackbarData(
//            message
             duration
            ,id))
        if (!emitted) {
            // fallback: ensure the message gets queued on Main
            MainScope().launch { _messages.emit(SnackbarData(
//                message,
                duration,id)) }
        }
    }

    /** Suspended variant if you prefer to call from coroutine */
//    suspend fun showSuspending(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
//        _messages.emit(SnackbarData(message, duration))
//    }
}


@Composable
fun GlobalSnackbarHost(
    hostModifier: Modifier = Modifier

) {
    // One hostState local to the composable (kept during recompositions)
    val hostState = remember { SnackbarHostState() }


    val context = LocalContext.current

    // Collect global messages and forward to hostState.showSnackbar
    LaunchedEffect(Unit) {
        GlobalSnackbar.messages.collectLatest { data ->

            val s = context.resources.getString(data.id)
            // this runs on Main (LaunchedEffect), so it's safe to call showSnackbar
            hostState.showSnackbar(message = s, duration = data.duration)
        }
    }

    // Place the visual host somewhere (bottom center usually)
    // Make sure this composable is added once at the root of your screen/navigation
    Box(modifier = Modifier.fillMaxSize()) {
//        SnackbarHost(
//            hostState = hostState,
//            modifier = hostModifier
//                .align(Alignment.BottomCenter)
//                .padding(24.dp)
//
//        )

        SnackbarHost(
            hostState = hostState,
            modifier = Modifier
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter),
            snackbar = { data ->
                Snackbar(
                    containerColor = Color(0xffF2F2F2), // 👈 set background color
                    contentColor = Color.Black, // 👈 set text/icon
                    modifier = Modifier
                        .padding(8.dp)
                        .border(1.dp, lightGreenEBF, shape = RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(text = data.visuals.message
                        , color = black1A,fontSize = textUnit(16)
                        , fontFamily = fontFamily(0)
                        , textAlign = TextAlign.Center
                    )
                }
            }
        )
    }


}