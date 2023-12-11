package com.jsyntax.nowtap

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.RelativeLayout

class FlashlightActivity : Activity() {

    private var originalBrightness: Float = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    private val handler = Handler(Looper.getMainLooper())
    private val autoOffRunnable = Runnable {
        Log.d("FlashlightActivity", "Runnable executing; finishing activity")
        finish()
    }
    private val autoOffDelayMillis: Long = 45_000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FlashlightActivity", "onCreate called")

        val layout = RelativeLayout(this).apply {
            setBackgroundColor(0xFFFFFFFF.toInt())
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
