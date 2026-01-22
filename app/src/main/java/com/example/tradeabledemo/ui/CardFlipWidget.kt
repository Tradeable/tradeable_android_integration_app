package com.example.tradeabledemo.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import com.tradeable.sdk.android.wrapper.FlutterBridge

/**
 * CardFlipWidget - Demonstrates card flip animation
 * Front: Compose widget
 * Back: Flutter widget (via platform view - courses_list_page)
 */
@Composable
fun CardFlipWidget(
    modifier: Modifier = Modifier,
    isFlipped: Boolean,
    onFlipChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    
    DisposableEffect(isFlipped) {
        if (isFlipped) {
            val flutterBridge = FlutterBridge.getInstance(context)
            // Send view state to Flutter (mode: card)
            flutterBridge.sendViewState(
                mode = "card",
                text = "Card Back Side\nFlutter Content",
                width = 350.0,
                height = 380.0
            )
        }
        onDispose { }
    }
    
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "CardFlip"
    )
    
    Card(
        modifier = modifier
            .height(380.dp)
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlipChange(!isFlipped) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    if (rotation > 90f) {
                        rotationY = (rotation % 360f) - 360f
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (rotation < 90f) {
                // Front side - Compose content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.FlipCameraAndroid,
                        contentDescription = "Flip",
                        modifier = Modifier.height(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Click to Flip",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        "Compose Widget",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                // Back side - Real Flutter widget (Courses List)
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndroidView(
                        factory = { ctx ->
                            FlutterBridge.getInstance(ctx).createFlutterView()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    // Transparent overlay to intercept taps above AndroidView
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        // Flip back and restore direct mode on Flutter side
                                        onFlipChange(false)
                                        val flutterBridge = FlutterBridge.getInstance(context)
                                        flutterBridge.sendViewState(
                                            mode = "direct",
                                            text = "Direct Flutter Widget\nEmbedded in Android",
                                            width = 350.0,
                                            height = 350.0
                                        )
                                    }
                                )
                            }
                    )
                }
            }
        }
    }
}
