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
- ✅ Native side drawer + Flutter drawer content
- ✅ Native fullscreen topic/dashboard content launched from Flutter drawer actions

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
- **Side Drawer Button**: Opens native side drawer that hosts Flutter drawer content

### New Views Added

This integration app now demonstrates the new wrapper display modes:

- `DisplayMode.SIDE_DRAWER`
- `DisplayMode.FULLSCREEN_CONTENT`
- `DisplayMode.DASHBOARD_CONTENT`

Flow implemented in app:

1. Native opens drawer using `DisplayMode.SIDE_DRAWER`.
2. Flutter drawer sends an action over navigation channel (`sendData`).
3. Native closes drawer and opens a new fullscreen screen.
4. Fullscreen content is rendered via `DisplayMode.FULLSCREEN_CONTENT` or `DisplayMode.DASHBOARD_CONTENT`.

## Usage

Use the wrapper composable directly (or the alias `TradeableFlutterWidget`) from your screen.

```kotlin
// Direct mode
TradeableFlutterWidget(
    mode = DisplayMode.DIRECT,
    width = 320.dp,
    height = 220.dp,
    data = mapOf("text" to "Trading Widget")
)

// Card flip mode
TradeableFlutterWidget(
    mode = DisplayMode.CARD_FLIP,
    width = 320.dp,
    height = 220.dp,
    data = mapOf("text" to "Tap to Flip")
)

// Fullscreen launcher mode
TradeableFlutterWidget(
    mode = DisplayMode.FULLSCREEN,
    data = mapOf("text" to "Open Fullscreen"),
    topicId = 6
)
```

Native side-nav usage:

```kotlin
TradeableFlutterWidget(
    mode = DisplayMode.SIDE_DRAWER,
    width = 360.dp,
    height = 720.dp,
    data = mapOf("text" to "Native Side Drawer"),
    pageId = 6,
    onCloseSideDrawer = { showNativeDrawer = false }
)
```

Handle drawer actions and open new fullscreen screens natively:

```kotlin
bridge.setupDataHandler { payload ->
    when (payload["action"]) {
        "openTopic" -> {
            val topicId = payload["topicId"] as? Int ?: return@setupDataHandler
            startActivity(
                Intent(context, TradeableFlutterActivity::class.java).apply {
                    putExtra("mode", "fullscreen")
                    putExtra("topicId", topicId)
                    putExtra("text", payload["title"] as? String ?: "Topic Detail")
                }
            )
        }
        "openDashboard" -> {
            startActivity(
                Intent(context, TradeableFlutterActivity::class.java).apply {
                    putExtra("mode", "dashboard")
                    putExtra("text", payload["title"] as? String ?: "Learn Dashboard")
                }
            )
        }
    }
}
```

## Method Channels for Integration Apps

Channel names:

- `embedded_flutter`
- `embedded_flutter/auth`
- `embedded_flutter/navigation`

Host -> Flutter calls (used by wrapper):

- `embedded_flutter.setData`
    - payload keys: `mode`, `text`, `width`, `height`, `topicId`, `pageId`
- `embedded_flutter/auth.initializeTFS`
    - payload keys: `baseUrl`, `authToken`, `portalToken`, `appId`, `clientId`, `publicKey`
- `embedded_flutter/navigation.openTradeableSideDrawer`
    - payload can include `pageId`

Flutter -> Host callbacks to handle in integration app:

- On `embedded_flutter`
    - `closeCard`
    - `closeFullscreen`
    - `closeSideDrawer`
- On `embedded_flutter/navigation`
    - `sendData`
        - topic open action:
            - `{ "action": "openTopic", "topicId": <int>, "title": <string> }`
        - dashboard open action:
            - `{ "action": "openDashboard", "title": "Learn Dashboard" }`

In this app, these are wired through wrapper bridge handlers in the main screen.

### Flipping Cards

Each card has:
- **Front**: Native Android content with flip button
- **Back**: Flutter view placeholder (or actual TradeableFlutterView when SDK is integrated)

## Enabling Real Flutter Views

Once you have the actual AAR with Flutter:

No extra code toggles are required in current implementation. The demo already renders live Flutter content through `TradeableFlutterWidget` / `TradeableFlutterView`.

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
```

## License

MIT
