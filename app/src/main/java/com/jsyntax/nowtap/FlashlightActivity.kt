package com.jsyntax.nowtap

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.RelativeLayout

class FlashlightActivity : Activity() {

    private var originalBrightness: Float = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    private val handler = Handler(Looper.getMainLooper())
    private val autoOffRunnable = Runnable { finish() }
    private val autoOffDelayMillis: Long = 60_000 // 60 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onStop() {
        super.onStop()

        handler.removeCallbacks(autoOffRunnable)

        try {
            window.attributes = window.attributes.apply {
                screenBrightness = originalBrightness
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoOffRunnable)
    }
}
