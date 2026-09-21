package com.alpha.selfemployment.Views.TextToSpeech

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
/*

@Composable
fun TextToSpeechPlayer() {

    val context = LocalContext.current
    val ttsManager = remember { TTSManager(context) }

    var isPlaying by remember { mutableStateOf(false) }
    var currentChunk by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf("1x") }
    var isDragging by remember { mutableStateOf(false) }

    val speedOptions = listOf("0.5x", "1x", "1.5x", "2x")
    val speedFloat = speed.replace("x", "").toFloat()

    val textToRead = "Farming is the practice of cultivating soil, growing crops and raising animals for food. ".repeat(11)

    // Split text into sentence-level chunks
    val chunks = remember(textToRead) {
        textToRead.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
    }

    val totalChunks = chunks.size
    val progress = if (totalChunks > 0) currentChunk.toFloat() / totalChunks else 0f

    // Register TTS callbacks
    LaunchedEffect(ttsManager) {
        ttsManager.onChunkCompleted = { completedIndex ->
            currentChunk = completedIndex + 1
        }
        ttsManager.onAllCompleted = {
            isPlaying = false
            currentChunk = 0
        }
    }

    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Play / Pause button
            IconButton(
                onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        ttsManager.speakFromChunk(chunks, currentChunk, speedFloat)
                    } else {
                        ttsManager.stop()
                    }
                }
            ) {
                Icon(
                    painter = if (isPlaying) Icons.Default.Home else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            Spacer(Modifier.width(6.dp))

            // Progress slider — dragging seeks to that chunk
            Slider(
                value = progress,
                onValueChange = { newValue ->
                    isDragging = true
                    currentChunk = (newValue * totalChunks).toInt().coerceIn(0, totalChunks - 1)
                },
                onValueChangeFinished = {
                    isDragging = false
                    // If playing, resume from the new chunk position
                    if (isPlaying) {
                        ttsManager.speakFromChunk(chunks, currentChunk, speedFloat)
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(10.dp))

            Divider(
                modifier = Modifier
                    .height(32.dp)
                    .width(2.dp)
            )

            Spacer(Modifier.width(10.dp))

            // Speed selector
            Box {
                Row(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = speed, fontSize = 16.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    speedOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                speed = option
                                expanded = false
                                if (isPlaying) {
                                    ttsManager.speakFromChunk(
                                        chunks,
                                        currentChunk,
                                        option.replace("x", "").toFloat()
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Optional: show current chunk progress label
        Text(
            text = "Sentence ${currentChunk + 1} of $totalChunks",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}*/
