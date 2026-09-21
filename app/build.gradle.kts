import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
}

// Load config/secrets from local.properties (gitignored — never commit real keys)
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.alpha.selfemployment"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.alpha.selfemployment"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Injected from local.properties — see README "Configuration"
        buildConfigField(
            "String",
            "BASE_URL",
            "\"${localProperties.getProperty("BASE_URL", "https://api.example.com/")}\""
        )
        buildConfigField(
            "String",
            "TRANSLATE_API_KEY",
            "\"${localProperties.getProperty("TRANSLATE_API_KEY", "")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // ── Androidx core ────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)          // from version catalog, don't repeat

    // ── Compose (BOM controls all compose versions) ──────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)         // BOM-managed, removed hardcoded alpha
    implementation("androidx.compose.material:material:1.7.5")
    implementation(libs.volley)
    // REMOVED: material3:1.5.0-alpha07 — was overriding BOM, keep only if you specifically need it
    // REMOVED: animation:1.7.0 — BOM manages this; add back only if you need a version override

    // ── Test ─────────────────────────────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ── Image loading ─────────────────────────────────────────
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-video:2.7.0")

    // ── Serialization ─────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation(libs.kotlinx.serialization.core)

    // ── DataStore ─────────────────────────────────────────────
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    // ── Kotlin reflect ────────────────────────────────────────
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // ── Navigation 3 ─────────────────────────────────────────
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)

    // ── Foundation ────────────────────────────────────────────
    implementation("androidx.compose.foundation:foundation:1.9.4")

    // ── Networking ────────────────────────────────────────────
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    implementation("com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0")

    // ── Dependency Injection (Koin) ───────────────────────────
    implementation("io.insert-koin:koin-android:4.1.1")
    implementation("io.insert-koin:koin-androidx-compose:4.1.1")

    // ── Media3 (single version — no more 1.3.1 leftovers) ────
    val media3 = "1.9.3"
    implementation("androidx.media3:media3-exoplayer:$media3")
    implementation("androidx.media3:media3-common:$media3")
    implementation("androidx.media3:media3-ui-compose:$media3")
    implementation("androidx.media3:media3-ui-compose-material3:$media3")
    implementation("androidx.media3:media3-exoplayer-hls:$media3")
    implementation("androidx.media3:media3-exoplayer-dash:$media3")
    implementation("androidx.media3:media3-datasource:${media3}")
    implementation("androidx.media3:media3-transformer:$media3")  // upgraded from 1.3.1

    // ── YouTube Player ────────────────────────────────────────
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:13.0.0")

    // ── Palette ───────────────────────────────────────────────
    implementation("androidx.palette:palette-ktx:1.0.0")


    // material 3 expressive
    implementation("androidx.compose.material3:material3:1.5.0-alpha07")

    // firebase
// Firebase BoM — manages all Firebase versions automatically
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Now NO version numbers needed — BoM handles it
    implementation("com.google.firebase:firebase-database-ktx")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")




}
