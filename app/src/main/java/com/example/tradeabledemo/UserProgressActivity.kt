package com.example.tradeabledemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.tradeabledemo.theme.TradeableDemoTheme
import com.example.tradeabledemo.ui.TradeableFlutterWidget
import com.tradeable.sdk.android.wrapper.FlutterBridge
import com.tradeable.sdk.ui.DisplayMode
import com.tradeable.sdk.ui.TradeableFlutterActivity
import java.util.UUID

private const val USER_PROGRESS_TAG = "UserProgress"

class UserProgressActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent { TradeableDemoTheme { UserProgressScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserProgressScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity
    val bridge = remember { FlutterBridge.getInstance(context) }
    val ownerKey = remember { "user-progress:${UUID.randomUUID()}" }

    DisposableEffect(lifecycleOwner, ownerKey) {
        bridge.registerDataHandler(ownerKey) { payload ->
            val action = payload["action"] as? String ?: return@registerDataHandler
            activity?.runOnUiThread {
                when (action) {
                    "openTopic" -> {
                        val topicId = (payload["topicId"] as? Int) ?: 0
                        if (topicId > 0) {
                            val intent =
                                    Intent(context, TradeableFlutterActivity::class.java).apply {
                                        putExtra("mode", "fullscreen")
                                        putExtra(
                                                "text",
                                                payload["title"] as? String ?: "Topic Detail"
                                        )
                                        putExtra("topicId", topicId)
                                    }
                            context.startActivity(intent)
                        }
                    }
                    "openCourseDetails" -> {
                        val courseId = (payload["courseId"] as? Int) ?: 0
                        if (courseId > 0) {
                            val intent =
                                    Intent(context, TradeableFlutterActivity::class.java).apply {
                                        putExtra("mode", "courseDetailsScreen")
                                        putExtra(
                                                "text",
                                                payload["title"] as? String ?: "Course Details"
                                        )
                                        putExtra("courseId", courseId)
                                    }
                            context.startActivity(intent)
                        }
                    }
                    "openDashboard" -> {
                        val intent =
                                Intent(context, TradeableFlutterActivity::class.java).apply {
                                    putExtra("mode", "dashboard")
                                    putExtra(
                                            "text",
                                            payload["title"] as? String ?: "Learn Dashboard"
                                    )
                                }
                        context.startActivity(intent)
                    }
                    "openUserProgress" -> {
                        val intent =
                                Intent(context, TradeableFlutterActivity::class.java).apply {
                                    putExtra("mode", "userProgressScreen")
                                    putExtra("text", payload["title"] as? String ?: "My Activity")
                                }
                        context.startActivity(intent)
                    }
                }
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                bridge.activateOwner(ownerKey)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            bridge.clearOwnerHandlers(ownerKey)
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("User Progress", fontWeight = FontWeight.Bold) },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                )
            }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(12.dp)) {
            TradeableFlutterWidget(
                    mode = DisplayMode.USER_PROGRESS,
                    width = 390.dp,
                    height = 420.dp,
                    modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
