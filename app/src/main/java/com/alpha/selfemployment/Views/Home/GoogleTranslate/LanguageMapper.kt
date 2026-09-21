package com.alpha.selfemployment.Views.Home.GoogleTranslate

import java.util.Locale

// LanguageMapper.kt
object LanguageMapper {

    // Maps your app's onboarding language name → (Google translate code, TTS Locale)
    fun getLanguageConfig(appLanguage: String): Pair<String, Locale> {
        return when (appLanguage.lowercase()) {
            "tamil"     -> Pair("ta", Locale("ta", "IN"))
            "hindi"     -> Pair("hi", Locale("hi", "IN"))
            else        -> Pair("en", Locale.ENGLISH)
        }
    }
}