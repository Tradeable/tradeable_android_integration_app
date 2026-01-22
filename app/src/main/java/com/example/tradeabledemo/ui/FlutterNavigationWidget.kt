package com.example.tradeabledemo.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.widget.Toast
import com.tradeable.sdk.ui.TradeableFlutterActivity
import com.tradeable.sdk.android.wrapper.FlutterBridge

/**
 * FlutterNavigationWidget - Button to navigate to full Flutter screen
 * Opens fullscreen Flutter with topicId (matching iOS pattern)
 */
@Composable
fun FlutterNavigationWidget(
    modifier: Modifier = Modifier,
    title: String = "Open Flutter Screen",
    description: String = "Navigate to a full Flutter screen with Topic Details",
    topicId: Int = 6,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = context as? Activity
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            
            Text(
                description,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(
                onClick = {
                    onClick?.invoke() ?: run {
                        activity?.let {
                            // Check if TFS is initialized before opening fullscreen
                            val bridge = FlutterBridge.getInstance(context)
                            if (!bridge.isTFSInitialized()) {
                                Toast.makeText(context, "Initializing SDK, opening shortly...", Toast.LENGTH_SHORT).show()
                                // Fallback: open after short delay to avoid deadlock
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    val intent = Intent(it, TradeableFlutterActivity::class.java).apply {
                                        putExtra("mode", "fullscreen")
                                        putExtra("text", "Open Fullscreen")
                                        putExtra("topicId", topicId)
                                    }
                                    it.startActivity(intent)
                                }, 1200)
                            } else {
                                // Open immediately
                                val intent = Intent(it, TradeableFlutterActivity::class.java).apply {
                                    putExtra("mode", "fullscreen")
                                    putExtra("text", "Open Fullscreen")
                                    putExtra("topicId", topicId)
                                }
                                it.startActivity(intent)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.OpenInNew,
                    contentDescription = "Open",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    "Launch Flutter UI",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
