# Tradeable Demo App

Example Android application demonstrating how to integrate the `tradeable-android-wrapper` AAR.

## Features Demonstrated

- ✅ SDK initialization in Application class
- ✅ Credential management with refresh callback
- ✅ Analytics event handling
- ✅ Card list with flipping animation
- ✅ Embedded Flutter views (TradeableFlutterView)
- ✅ Full-page Flutter navigation
- ✅ Callback handling from Flutter

## Setup

### 1. Clone and Build Wrapper (Git-based)

This integration app expects the wrapper at `../tradeable_android_wrapper`.

Default wrapper git source:

https://github.com/deepakgrandhi/tradeable_android_wrapper.git

Run:

```bash
./setup_wrapper.sh
```

This command will:
- Clone the wrapper repo if missing
- Build Flutter artifacts and wrapper AAR
- Copy `tradeable-android-wrapper.aar` into `app/libs/`

You can override defaults with env vars:

```bash
TRADEABLE_WRAPPER_GIT_URL=https://github.com/<your-org>/<your-wrapper>.git \
TRADEABLE_WRAPPER_DIR=../custom-wrapper-path \
./setup_wrapper.sh
```

### 2. Configure Credentials

Edit `TradeableDemoApplication.kt` and update:

```kotlin
onRefreshCredentials = {
    // Return your actual credentials
    TradeableCredentials(
        authorization = "Bearer your-actual-token",
        portalToken = "your-portal-token",
        appId = "your-app-id",
        clientId = "your-client-id",
        publicKey = "your-public-key"
    )
}
```

### 3. Update Base URL

```kotlin
TradeableConfig(
    baseUrl = "https://your-actual-api.com",
    // ...
)
```

### 4. Build and Run

```bash
./gradlew assembleDebug
```

Or open in Android Studio and run.

## Project Structure

```
app/src/main/java/com/example/tradeabledemo/
├── TradeableDemoApplication.kt  # SDK initialization
├── MainActivity.kt              # Main activity
├── ui/
│   └── MainScreen.kt            # Main UI with card list
└── theme/
    └── Theme.kt                 # App theme
```

## UI Overview

### Main Screen

- **Top App Bar**: Shows app title with a button to open full-page Flutter view
- **Status Banner**: Shows SDK initialization status
- **Card List**: List of demo cards with flip animation
- **FAB**: Opens the Tradeable dashboard

### Flipping Cards

Each card has:
- **Front**: Native Android content with flip button
- **Back**: Flutter view placeholder (or actual TradeableFlutterView when SDK is integrated)

## Enabling Real Flutter Views

Once you have the actual AAR with Flutter:

1. In `MainScreen.kt`, find the `CardBack` composable
2. Uncomment the `TradeableFlutterView` section
3. Remove/comment the placeholder code

```kotlin
// Uncomment this:
TradeableFlutterView(
    height = 160.dp,
    width = 280.dp,
    type = card.flutterType,
    parameters = card.parameters,
    onCallback = { event ->
        Log.d("DemoApp", "Flutter callback: ${event.action}")
    },
    modifier = Modifier.align(Alignment.Center)
)

// Remove this placeholder:
// Column(...) { ... }
```

## Dependencies

The app requires these dependencies (already configured):

```kotlin
// Tradeable SDK
implementation(files("libs/tradeable-android-wrapper.aar"))

// Required transitive dependencies
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.activity:activity-compose:1.8.2")
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

## License

MIT
