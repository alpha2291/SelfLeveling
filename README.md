# SelfEmployment

An Android app connecting self-employed professionals and gig workers with clients — featuring job postings, reels-style video content, real-time chat, enquiries, and multilingual support.

## Tech Stack

- **UI:** Jetpack Compose (Material 3 + expressive), Navigation 3
- **Architecture:** MVVM with Koin dependency injection
- **Networking:** Retrofit + OkHttp (auth interceptor, logging)
- **Media:** Media3 ExoPlayer (HLS/DASH), Coil, YouTube Player
- **Backend:** Firebase Realtime Database (chat), custom REST API
- **Local storage:** DataStore Preferences
- **Localization:** Google Cloud Translation API integration




https://github.com/user-attachments/assets/31a847ba-4791-4486-b287-aff3971493f6



## Setup

### 1. Firebase

Copy the template and fill in your own Firebase project values:

```bash
cp app/google-services.example.json app/google-services.json
```

Get your real `google-services.json` from the [Firebase Console](https://console.firebase.google.com/) → Project Settings → Your apps.

### 2. Configuration

Add these keys to `local.properties` (already gitignored):

```properties
sdk.dir=/path/to/your/Android/sdk
BASE_URL=https://your-backend.example.com/api/
TRANSLATE_API_KEY=your-google-cloud-api-key   # optional — translation is disabled if blank
```

`BASE_URL` and `TRANSLATE_API_KEY` are injected into `BuildConfig` at build time — no secrets live in source control.

### 3. Build

```bash
./gradlew assembleDebug
```

## Security Notes

- `google-services.json`, `local.properties`, and keystores are gitignored.
- API keys are injected via `BuildConfig` from `local.properties`, never hardcoded.
- The Firebase API key should additionally be restricted in Google Cloud Console (Android app restriction: package name + SHA-1).
