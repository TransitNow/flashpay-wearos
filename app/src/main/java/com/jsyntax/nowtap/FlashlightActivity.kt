package com.jsyntax.nowtap

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout

class FlashlightActivity : Activity() {

    private var originalBrightness: Float = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    private val handler = Handler(Looper.getMainLooper())
    private val autoOffRunnable = Runnable {
        Log.d("FlashlightActivity", "Runnable executing; finishing activity")
        finish()
    }
    private val autoOffDelayMillis: Long = 300_000
    
    private lateinit var layout: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences
    private var isRedMode = false
    
    companion object {
        private const val PREFS_NAME = "FlashlightPreferences"
        private const val IS_RED_MODE_KEY = "isRedMode"
        private const val WHITE_COLOR = 0xFFFFFFFF.toInt()
        private const val RED_COLOR = 0xFFFF0000.toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FlashlightActivity", "onCreate called")

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        isRedMode = sharedPreferences.getBoolean(IS_RED_MODE_KEY, false)

        layout = RelativeLayout(this).apply {
            setBackgroundColor(if (isRedMode) RED_COLOR else WHITE_COLOR)
            setOnClickListener { toggleColor() }
        }
        setContentView(layout)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        try {
            val params = window.attributes
            originalBrightness = params.screenBrightness

            params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            window.attributes = params
        } catch (e: Exception) {
            e.printStackTrace()
        }

        handler.postDelayed(autoOffRunnable, autoOffDelayMillis)
    }

    private fun toggleColor() {
        isRedMode = !isRedMode
        layout.setBackgroundColor(if (isRedMode) RED_COLOR else WHITE_COLOR)
        
        sharedPreferences.edit().apply {
            putBoolean(IS_RED_MODE_KEY, isRedMode)
            apply()
        }
        
        Log.d("FlashlightActivity", "Color toggled to: ${if (isRedMode) "RED" else "WHITE"}")
    }

    override fun onPause() {
        super.onPause()
        Log.d("FlashlightActivity", "onPause called; removing callbacks")
        handler.removeCallbacks(autoOffRunnable)
        finish()
    }

    override fun onResume() {
        super.onResume()
        Log.d("FlashlightActivity", "onResume called; posting delayed runnable")
        handler.postDelayed(autoOffRunnable, autoOffDelayMillis)
    }

    override fun onStop() {
        super.onStop()
        Log.d("FlashlightActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FlashlightActivity", "onDestroy called; removing callbacks")
        handler.removeCallbacks(autoOffRunnable)
    }
}
