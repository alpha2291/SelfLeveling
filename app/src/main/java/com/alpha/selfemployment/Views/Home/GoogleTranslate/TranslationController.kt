package com.alpha.selfemployment.Views.Home.GoogleTranslate

import com.alpha.selfemployment.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// TranslationHelper.kt
object TranslationHelper {

    private val apiKey = BuildConfig.TRANSLATE_API_KEY
    private const val ENDPOINT = "https://translation.googleapis.com/language/translate/v2"

    suspend fun translate(text: String, targetLanguage: String): String {
        if (apiKey.isBlank()) return text // no key configured — return original
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$ENDPOINT?key=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val body = JSONObject().apply {
                    put("q", text)
                    put("target", targetLanguage)   // e.g. "ta" for Tamil, "hi" for Hindi
                    put("format", "text")
                }.toString()

                connection.outputStream.write(body.toByteArray())

                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                json.getJSONObject("data")
                    .getJSONArray("translations")
                    .getJSONObject(0)
                    .getString("translatedText")
            } catch (e: Exception) {
                text // fallback to original on error
            }
        }
    }
}