package com.example.tradeabledemo

import android.app.Application
import android.util.Log
import com.example.tradeabledemo.BuildConfig
import com.tradeable.sdk.config.TradeableAnalyticsEvent
import com.tradeable.sdk.config.TradeableCallbackEvent
import com.tradeable.sdk.config.TradeableConfig
import com.tradeable.sdk.config.TradeableCredentials
import com.tradeable.sdk.core.TradeableSDK

/**
 * Application class that initializes the Tradeable SDK.
 * 
 * This is where you configure the SDK with your credentials and callbacks.
 */
class TradeableDemoApplication : Application() {
    
    companion object {
        private const val TAG = "TradeableDemoApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        initializeTradeableSDK()
    }
    
    private fun initializeTradeableSDK() {
        Log.d(TAG, "Initializing Tradeable SDK...")
        
        TradeableSDK.initialize(
            context = this,
            config = TradeableConfig(
                baseUrl = "https://api.tradeable.com", // Replace with your actual base URL
                
                // Callback to refresh credentials when needed
                onRefreshCredentials = {
                    // TODO: Replace with actual credential fetching logic
                    // This would typically call your auth service
                    Log.d(TAG, "Refreshing credentials...")
                    
                    TradeableCredentials(
                        authorization = "Bearer your-auth-token",
                        portalToken = "your-portal-token",
                        appId = "your-app-id",
                        clientId = "your-client-id",
                        publicKey = "your-public-key"
                    )
                },
                
                // Handle analytics events from Flutter SDK
                onAnalyticsEvent = { event: TradeableAnalyticsEvent ->
                    Log.d(TAG, "Analytics Event: ${event.eventName}")
                    event.data?.let { data ->
                        Log.d(TAG, "Event Data: $data")
                    }
                    
                    // TODO: Forward to your analytics service
                    // analyticsService.track(event.eventName, event.data)
                },
                
                // Handle callback events from Flutter views
                onCallback = { event: TradeableCallbackEvent ->
                    Log.d(TAG, "Callback: ${event.action}")
                    event.route?.let { route ->
                        Log.d(TAG, "Route: $route")
                    }
                    event.parameters?.let { params ->
                        Log.d(TAG, "Parameters: $params")
                    }
                    
                    // TODO: Handle navigation or actions as needed
                    // For example, navigate to a native screen based on the callback
                },
                
                // Enable debug logging during development
                debugMode = BuildConfig.DEBUG
            )
        )
        
        Log.d(TAG, "Tradeable SDK initialization complete")
    }
}
