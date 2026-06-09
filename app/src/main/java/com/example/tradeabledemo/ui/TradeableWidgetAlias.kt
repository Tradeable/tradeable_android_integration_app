package com.example.tradeabledemo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tradeable.sdk.ui.DisplayMode
import com.tradeable.sdk.ui.TradeableFlutterView

/**
 * Integration-app level alias so usage stays consistent with iOS style naming.
 *
 * This allows calling TradeableFlutterWidget(...) even when consuming wrapper
 * versions that only expose TradeableFlutterView(...).
 */
@Composable
fun TradeableFlutterWidget(
    mode: DisplayMode = DisplayMode.DIRECT,
    width: Dp = 320.dp,
    height: Dp = 220.dp,
    data: Map<String, Any> = emptyMap(),
    topicId: Int? = null,
    pageId: Int? = null,
    onCloseSideDrawer: (() -> Unit)? = null,
    onCloseFullscreen: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TradeableFlutterView(
        mode = mode,
        width = width,
        height = height,
        data = data,
        topicId = topicId,
        pageId = pageId,
        onCloseSideDrawer = onCloseSideDrawer,
        onCloseFullscreen = onCloseFullscreen,
        modifier = modifier
    )
}
