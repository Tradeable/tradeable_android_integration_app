package com.example.tradeabledemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tradeabledemo.ui.MainScreen
import com.example.tradeabledemo.theme.TradeableDemoTheme
import com.tradeable.sdk.android.wrapper.FlutterBridge

class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
        private const val TFS_INIT_DELAY_MS = 500L // Reduced from 1000ms
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        Log.d(TAG, "onCreate - Initializing Flutter Bridge")
        
        // Initialize Flutter Bridge to enable Flutter integration
        val bridge = FlutterBridge.getInstance(this)
        bridge.initialize(this)
        
        // Initialize TFS after a short delay to ensure Flutter engine is ready
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "Initializing TFS...")
            try {
                bridge.initializeTFS(
                    baseUrl = "https://dev.api.tradeable.app/axis/",
                    authToken = "",
                    portalToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMSIsIm9pZCI6MiwiaWF0IjoxNzQyNDkwOTY0LCJleHAiOjk5OTk5OTk5OTl9.BbSv_2Z9JgE53bIMbFzg2MaeeCFsrza-epaay7BfEj0",
                    appId = "",
                    clientId = "",
                    publicKey = ""
                )
                Log.d(TAG, "TFS initialization called successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing TFS", e)
            }
        }, TFS_INIT_DELAY_MS)
        
        setContent {
            TradeableDemoTheme {
                MainScreen()
            }
        }
    }
    
    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }
}