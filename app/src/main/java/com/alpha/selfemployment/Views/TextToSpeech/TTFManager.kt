package com.alpha.selfemployment.Views.TextToSpeech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale


class TTSManager(context: Context) {

    private var tts: TextToSpeech? = null
    var isReady = false

    var onChunkCompleted: ((Int) -> Unit)? = null  // callback when a chunk finishes
    var onAllCompleted: (() -> Unit)? = null        // callback when all chunks finish

    private var chunks: List<String> = emptyList()
    private var currentChunkIndex = 0

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isReady = true
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        val idx = utteranceId?.removePrefix("chunk-")?.toIntOrNull() ?: return
                        onChunkCompleted?.invoke(idx)

                        val nextIndex = idx + 1
                        if (nextIndex < chunks.size) {
                            currentChunkIndex = nextIndex
                            speakChunk(nextIndex)
                        } else {
                            onAllCompleted?.invoke()
                        }
                    }

                    override fun onError(utteranceId: String?) {}
                })
            }
        }
    }

    fun speakFromChunk(allChunks: List<String>, fromIndex: Int, speed: Float) {
        if (!isReady) return
        chunks = allChunks
        currentChunkIndex = fromIndex
        tts?.setSpeechRate(speed)
        tts?.stop()
        speakChunk(fromIndex)
    }

    private fun speakChunk(index: Int) {
        if (index >= chunks.size) return
        tts?.speak(chunks[index], TextToSpeech.QUEUE_FLUSH, null, "chunk-$index")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
    }
}