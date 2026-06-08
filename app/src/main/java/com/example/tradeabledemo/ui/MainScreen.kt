package com.example.tradeabledemo.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tradeable.sdk.config.TradeablePageParams
import com.tradeable.sdk.core.TradeableSDK
import com.tradeable.sdk.ui.TradeableFlutterView
import com.tradeable.sdk.ui.DisplayMode
import com.tradeable.sdk.ui.TradeableFlutterActivity
import com.tradeable.sdk.android.wrapper.FlutterBridge
import androidx.compose.foundation.layout.fillMaxHeight

data class DemoCard(
    val id: String,
    val title: String,
    val subtitle: String,
    val flutterType: String,
    val parameters: Map<String, String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val bridge = remember { FlutterBridge.getInstance(context) }
    val isSDKInitialized by TradeableSDK.isInitialized.collectAsState()
    var isCardFlipped by remember { mutableStateOf(false) }
    var showNativeDrawer by remember { mutableStateOf(false) }
    val sideDrawerPageId by remember { mutableStateOf(6) }

    DisposableEffect(Unit) {
        bridge.setupDataHandler { payload ->
            val action = payload["action"] as? String ?: return@setupDataHandler
            activity?.runOnUiThread {
                showNativeDrawer = false

                when (action) {
                    "openTopic" -> {
                        val topicId = (payload["topicId"] as? Int) ?: 0
                        if (topicId > 0) {
                            val intent = Intent(context, TradeableFlutterActivity::class.java).apply {
                                putExtra("mode", "fullscreen")
                                putExtra("text", payload["title"] as? String ?: "Topic Detail")
                                putExtra("topicId", topicId)
                            }
                            context.startActivity(intent)
                        }
                    }
                    "openDashboard" -> {
                        val intent = Intent(context, TradeableFlutterActivity::class.java).apply {
                            putExtra("mode", "dashboard")
                            putExtra("text", payload["title"] as? String ?: "Learn Dashboard")
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }

        onDispose {
            bridge.setupDataHandler(null)
        }
    }
    
    val cards = remember {
        listOf(
            DemoCard("1", "Introduction to Trading", "Learn the basics", "course_card", mapOf("courseId" to "intro_101")),
            DemoCard("2", "Technical Analysis", "Chart patterns & indicators", "course_card", mapOf("courseId" to "tech_201")),
            DemoCard("3", "Risk Management", "Protect your capital", "course_card", mapOf("courseId" to "risk_301")),
            DemoCard("4", "Options Trading", "Advanced strategies", "course_card", mapOf("courseId" to "options_401")),
            DemoCard("5", "Market Psychology", "Trading mindset", "course_card", mapOf("courseId" to "psych_501"))
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tradeable Demo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = {
                            activity?.let {
                                TradeableSDK.openFullPage(
                                    activity = it,
                                    params = TradeablePageParams(route = "/dashboard", title = "Tradeable Learn")
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.School, "Open Tradeable Learn", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { activity?.let { TradeableSDK.openDashboard(it) } },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.AutoGraph, "Open Dashboard")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SDKStatusBanner(isInitialized = isSDKInitialized)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Section 1: Direct Flutter Widget Display
                item(key = "flutter_direct") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "1. Direct Flutter Widget",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        // Simplified API matching iOS
                        TradeableFlutterView(
                            mode = DisplayMode.DIRECT,
                            width = 320.dp,
                            height = 220.dp,
                            data = mapOf("text" to "Trading Widget"),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
                
                // Section 2: Card Flip Demo
                item(key = "card_flip") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "2. Card Flip Animation",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        // Simplified API matching iOS
                        TradeableFlutterView(
                            mode = DisplayMode.CARD_FLIP,
                            width = 320.dp,
                            height = 220.dp,
                            data = mapOf("text" to "Tap to Flip"),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
                
                // Section 3: Fullscreen Mode
                item(key = "flutter_fullscreen") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "3. Fullscreen Mode",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        // Simplified API matching iOS
                        TradeableFlutterView(
                            mode = DisplayMode.FULLSCREEN,
                            data = mapOf("text" to "Open Fullscreen"),
                            topicId = 6,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                showNativeDrawer = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Open Side Drawer",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Open Tradeable Side Drawer")
                        }
                    }
                }
                
                // Section 4: Course Cards
                item {
                    Text(
                        "4. Course Cards",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                
                items(cards, key = { it.id }) { card ->
                    FlippableCard(card = card)
                }
            }
            }

            if (showNativeDrawer) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clickable { showNativeDrawer = false }
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight()
                        .background(Color.White)
                ) {
                    TradeableFlutterView(
                        mode = DisplayMode.SIDE_DRAWER,
                        width = 360.dp,
                        height = 720.dp,
                        data = mapOf("text" to "Native Side Drawer"),
                        pageId = sideDrawerPageId,
                        onCloseSideDrawer = { showNativeDrawer = false },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun SDKStatusBanner(isInitialized: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isInitialized) Color(0xFF4CAF50) else Color(0xFFFF9800))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isInitialized) "SDK Initialized" else "SDK Initializing...",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun FlippableCard(card: DemoCard) {
    var isFlipped by remember { mutableStateOf(false) }
    
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "card_flip"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (rotation <= 90f) {
                // Front of card
                CardFront(card = card, onFlipClick = { isFlipped = true })
            } else {
                // Back of card (Flutter view) - rotated to appear correct
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    CardBack(card = card, onFlipClick = { isFlipped = false })
                }
            }
        }
    }
}

@Composable
private fun CardFront(card: DemoCard, onFlipClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flip button on the left
        IconButton(
            onClick = onFlipClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Default.FlipCameraAndroid,
                contentDescription = "Flip to see Flutter view",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Card content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = card.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap flip to see Flutter content",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CardBack(card: DemoCard, onFlipClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Flip button
        IconButton(
            onClick = onFlipClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.FlipCameraAndroid,
                contentDescription = "Flip back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        
        // ============================================================
        // UNCOMMENT THIS SECTION when using actual Tradeable SDK AAR
        // This will show the actual Flutter widget from the SDK
        // ============================================================
        /*
        TradeableFlutterView(
            height = 160.dp,
            width = 280.dp,
            type = card.flutterType,
            parameters = card.parameters,
            onCallback = { event ->
                // Handle callback from Flutter
                Log.d("DemoApp", "Flutter callback: ${event.action}")
            },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 48.dp) // Offset for flip button
        )
        */
        
        // Placeholder (remove when using actual SDK)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 48.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Flutter View Placeholder",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Type: ${card.flutterType}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "ID: ${card.parameters["courseId"]}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
