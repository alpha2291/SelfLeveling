package com.alpha.selfemployment

// LocaleHelper.kt

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

// ── Composition Local ─────────────────────────────────────────────────────────
// LocaleHelper.kt

val LocalStr = staticCompositionLocalOf<(Int) -> String> {
    error("LocalStr not provided — wrap your app with AppLocaleProvider")
}

fun Context.applyLocale(languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

@Composable
fun AppLocaleProvider(
    languageCode: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val localizedContext = remember(languageCode) {
        context.applyLocale(languageCode)
    }

    val strFn: (Int) -> String = remember(localizedContext) {
        { resId -> localizedContext.getString(resId) }
    }

    CompositionLocalProvider(LocalStr provides strFn) {
        content()
    }
}


@Composable
fun str(id: Int): String = LocalStr.current(id)
