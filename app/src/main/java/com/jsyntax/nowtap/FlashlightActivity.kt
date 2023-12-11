package com.jsyntax.nowtap

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.RelativeLayout

class FlashlightActivity : Activity() {

    private var originalBrightness: Float = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

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
    }

    override fun onStop() {
        super.onStop()

        try {
            window.attributes = window.attributes.apply {
                screenBrightness = originalBrightness
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
