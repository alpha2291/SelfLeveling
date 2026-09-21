package com.alpha.selfemployment.Views.Home.TTF

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TTSManager(context: Context) {

    var tts: TextToSpeech? = null

    var startIndex by mutableStateOf(0)
    var endIndex by mutableStateOf(0)

    var isSpeaking by mutableStateOf(false)
    var hasStarted by mutableStateOf(false)

    private var fullText: String = ""
    private var pausedAt: Int = 0
    private var currentSpeed: Float = 1f  // track speed so resume always uses correct rate

    init {
        tts = TextToSpeech(context) { }
    }

    fun setListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String) {
                isSpeaking = true
                hasStarted = true
            }

            override fun onDone(utteranceId: String) {
                isSpeaking = false
                hasStarted = false
                startIndex = 0
                endIndex = 0
                pausedAt = 0
                fullText = ""
            }

            override fun onError(utteranceId: String) {
                isSpeaking = false
                hasStarted = false
            }

            override fun onRangeStart(utteranceId: String, start: Int, end: Int, frame: Int) {
                startIndex = start + pausedAt
                endIndex = end + pausedAt
            }
        })
    }

    fun speak(text: String) {
        fullText = text
        pausedAt = 0
        tts?.setSpeechRate(currentSpeed)  // always apply current speed before speaking
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "reader")
    }

    fun pause() {
        pausedAt = startIndex
        tts?.stop()
        isSpeaking = false
    }

    fun resume() {
        if (fullText.isEmpty()) return
        val remaining = fullText.substring(pausedAt)
        tts?.setSpeechRate(currentSpeed)  // apply speed before resuming
        tts?.speak(remaining, TextToSpeech.QUEUE_FLUSH, null, "reader")
    }

    fun setSpeed(speed: Float) {
        currentSpeed = speed
        tts?.setSpeechRate(speed)

        // If currently speaking — restart from current word at new speed instantly
        if (isSpeaking) {
            val resumeFrom = startIndex
            pausedAt = resumeFrom
            val remaining = fullText.substring(resumeFrom)
            tts?.speak(remaining, TextToSpeech.QUEUE_FLUSH, null, "reader")
        }
        // If paused — speed is saved in currentSpeed, resume() will pick it up
    }

    fun setLocale(locale: java.util.Locale) {
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // fallback to default
            tts?.setLanguage(java.util.Locale.ENGLISH)
        }
    }

    fun release() {
        tts?.shutdown()
    }
}