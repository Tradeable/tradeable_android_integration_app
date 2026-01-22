package com.example.tradeabledemo.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tradeable.sdk.android.wrapper.FlutterBridge

/**
 * DirectFlutterWidget - Displays Flutter widget directly with custom content
 * Uses FlutterBridge to embed Flutter content in Android UI
 */
@Composable
fun DirectFlutterWidget(
    modifier: Modifier = Modifier,
    title: String = "Direct Flutter Widget",
    enabled: Boolean = true
) {
    val context = LocalContext.current
    
    if (enabled) {
        DisposableEffect(Unit) {
            val flutterBridge = FlutterBridge.getInstance(context)
            // Send view state to Flutter (mode: direct)
            flutterBridge.sendViewState(
                mode = "direct",
                text = "Direct Flutter Widget\nEmbedded in Android",
                width = 350.0,
                height = 350.0
            )
            onDispose { }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (enabled) {
            AndroidView(
                factory = { ctx ->
                    FlutterBridge.getInstance(ctx).createFlutterView()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )
        }
    }
}
