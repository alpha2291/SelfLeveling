package com.alpha.selfemployment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.alpha.selfemployment.ui.theme.primaryBlack
import com.alpha.selfemployment.ui.theme.primaryWhite


import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


fun extractYoutubeId_old(url: String): String? {
    val regex = "(?<=v=|youtu.be/|embed/|shorts/)[^#&?]*".toRegex()
    return regex.find(url)?.value
}

// Utility to extract videoId from any YouTube URL format
fun extractYoutubeId(url: String): String {
    // handles youtu.be/ID and youtube.com/watch?v=ID
    val patterns = listOf(
        Regex("youtu\\.be/([a-zA-Z0-9_-]{11})"),
        Regex("[?&]v=([a-zA-Z0-9_-]{11})")
    )
    for (pattern in patterns) {
        val match = pattern.find(url)
        if (match != null) return match.groupValues[1]
    }
    return url // fallback
}




fun getYoutubeThumbnail(videoId: String): String {
    return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
}




@Composable
fun YoutubePreviewTrial11(videoId: String) {

    var url by remember { mutableStateOf("") }
    var thumbnail by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 Blurred background
        thumbnail?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(25.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                textStyle = TextStyle(
                    fontSize = textUnit(12),
                    fontFamily = fontFamily(3),
                    color = primaryBlack
                ),
                label = { Text("Paste YouTube URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val id = extractYoutubeId(url)
                    if (id != null) {
                        thumbnail = getYoutubeThumbnail(id)
                    }
                }
            ) {
                Text("Preview")
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 🔹 Preview Card
            thumbnail?.let {

                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column {

//                        AsyncImage(
//                            model = it,
//                            contentDescription = null,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(200.dp),
//                            contentScale = ContentScale.Crop
//                        )

//                        YoutubePlayerScreen(videoId)

                        Text(
                            text = "YouTube Video Preview",
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = url,
                            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YoutubePreviewTrial_old(videoId: String, isActive: Boolean, onCardClick: () -> Unit) {

    val thumbnail = getYoutubeThumbnail(videoId)
    val videoUrl = "https://www.youtube.com/watch?v=$videoId"


    // 🔹 Fetch video title from oEmbed — no API key needed
    var videoTitle by remember { mutableStateOf("YouTube Video Preview") }

    LaunchedEffect(videoId) {
        try {
            val response = withContext(Dispatchers.IO) {
                val url = java.net.URL("https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.inputStream.bufferedReader().readText()
            }
            // Parse just the title field from JSON
            val title = org.json.JSONObject(response).getString("title")
            videoTitle = title
        } catch (e: Exception) {
            videoTitle = "YouTube Video Preview"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .blur(25.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(20.dp)
                .shrinkClick {
                    println("## root youtube card click")
                    onCardClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            // 🔹 Preview Card
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp , primaryWhite ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box() {
                    AsyncImage(
                        model = thumbnail,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize()
                            .blur(25.dp)
                    )

                    Column(
                        modifier = Modifier
                            .background(Color.Transparent)
                    ) {
                        YoutubePlayerScreen(
                            videoId,
                            isActive = isActive,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        )

                        // 🔹 Now shows real video title
                        Text(
                            text = videoTitle,
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = videoUrl,
                            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun YoutubePreviewTrial(videoId: String, isActive: Boolean, onCardClick: () -> Unit) {

    val thumbnail = getYoutubeThumbnail(videoId)
    val videoUrl = "https://www.youtube.com/watch?v=$videoId"

    var videoTitle by remember { mutableStateOf("YouTube Video Preview") }

    LaunchedEffect(videoId) {
        try {
            val response = withContext(Dispatchers.IO) {
                val url = java.net.URL("https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.inputStream.bufferedReader().readText()
            }
            val title = org.json.JSONObject(response).getString("title")
            videoTitle = title
        } catch (e: Exception) {
            videoTitle = "YouTube Video Preview"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .blur(25.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(20.dp),
            // ✅ REMOVED shrinkClick from here
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, primaryWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    AsyncImage(
                        model = thumbnail,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .background(primaryBlack)
                            .matchParentSize()
                            .blur(25.dp)
                    )

                    Column(
                        modifier = Modifier.background(Color.Transparent)
                    ) {
                        // YouTube player in a Box so we can overlay tap catcher
                        Box {
                            YoutubePlayerScreen(
                                videoId,
                                isActive = isActive,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                            )

                            // ✅ Transparent overlay catches taps that WebView would steal
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                println("## root youtube card click")
                                                onCardClick()
                                            }
                                        )
                                    }
                            )
                        }

                        Text(
                            text = videoTitle,
                            color = primaryWhite,
                            modifier = Modifier
                                .padding(12.dp)
                                .shrinkClick {
                                    println("## root youtube card click")
                                    onCardClick()
                                },
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = videoUrl,
                            color = primaryWhite,
                            modifier = Modifier
                                .padding(start = 12.dp, bottom = 12.dp)
                                .shrinkClick {
                                    println("## root youtube card click")
                                    onCardClick()
                                },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun YoutubePlayerScreen(
    videoId: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val youTubePlayerView = remember {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
        }
    }

    var player: YouTubePlayer? by remember { mutableStateOf(null) }
    var isPlayerReady by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(p: YouTubePlayer) {
                player = p
                isPlayerReady = true
                // loadVideo auto-plays, cueVideo does not
                if (isActive) {
                    p.loadVideo(videoId, 0f) // ✅ loads AND plays
                } else {
                    p.cueVideo(videoId, 0f)  // just buffer, don't play
                }
            }
        })

        onDispose {
            player = null
            isPlayerReady = false
            youTubePlayerView.release()
        }
    }

    // Only control playback after player is ready
    LaunchedEffect(isActive, isPlayerReady) {
        if (!isPlayerReady) return@LaunchedEffect
        player?.let {
            if (isActive) it.play() else it.pause()
        }
    }

    AndroidView(
        factory = { youTubePlayerView },
        modifier = modifier
    )
}



@Composable
fun YoutubePlayerScreen000(videoId: String , modifier: Modifier) {

    AndroidView(
        modifier = modifier,
        factory = { context ->

            val view = YouTubePlayerView(context)

            view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    player.loadVideo(videoId, 0f)
                }
            })

            view
        }
    )
}


@Composable
fun YoutubePreviewTrial0() {

    var url by remember { mutableStateOf("") }
    var videoId by remember { mutableStateOf<String?>(null) }
    var playVideo by remember { mutableStateOf(false) }

    if (playVideo && videoId != null) {
//        YoutubePlayerScreen(videoId!!)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            textStyle = TextStyle(
                fontSize = textUnit(12),
                fontFamily = fontFamily(3),
                color = primaryBlack
            ),
            label = { Text("Paste YouTube URL") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            videoId = extractYoutubeId(url)
        }) {
            Text("Preview")
        }

        Spacer(modifier = Modifier.height(30.dp))

        videoId?.let {

            val thumb = getYoutubeThumbnail(it)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { playVideo = true }
            ) {

                AsyncImage(
                    model = thumb,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}






