package com.jsyntax.nowtap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView

/**
 * What a launcher-icon tap opens.
 *
 * The hardware-button paths deliberately skip this screen — [MainActivity] decides and acts in one
 * frame, which is the entire point of the app — but an icon tap has to show something. Before this
 * existed, a watch without Google Wallet installed got a translucent activity holding no content
 * and no way out of it.
 *
 * Two rows only, single line each, laid out by [RoundSafeColumn]: nothing here scrolls, so there is
 * no mid-scroll state in which something could sit sliced by the bezel.
 */
class MenuActivity : Activity() {

    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setContentView(
            RoundSafeColumn(this).apply {
                setBackgroundColor(BACKGROUND_COLOR)
                gapPx = dp(GAP_DP)
                addView(buildTitle())
                addView(buildRow(getString(R.string.menu_flashlight)) { openFlashlight() })
                addView(buildRow(getString(R.string.menu_wallet)) { openWallet() })
            },
        )
    }

    private fun buildTitle(): TextView =
        TextView(this).apply {
            text = getString(R.string.app_name)
            gravity = Gravity.CENTER
            isSingleLine = true
            setTextColor(TITLE_COLOR)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, TITLE_TEXT_SIZE_SP)
            setPadding(0, 0, 0, dp(TITLE_BOTTOM_PADDING_DP))
        }

    private fun buildRow(label: String, onTap: () -> Unit): TextView =
        TextView(this).apply {
            text = label
            gravity = Gravity.CENTER
            isSingleLine = true
            setTextColor(ROW_TEXT_COLOR)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, ROW_TEXT_SIZE_SP)
            minHeight = dp(ROW_MIN_HEIGHT_DP)
            background = rowBackground()
            setOnClickListener {
                vibrator.vibrate(TAP_VIBRATION_MILLIS)
                onTap()
            }
        }

    /** A pill that darkens while pressed — the only touch feedback on a screen with no ripple. */
    private fun rowBackground(): StateListDrawable {
        fun pill(color: Int) = GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(ROW_CORNER_RADIUS_DP).toFloat()
        }
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), pill(ROW_PRESSED_COLOR))
            addState(intArrayOf(), pill(ROW_COLOR))
        }
    }

    private fun openFlashlight() {
        Log.d(TAG, "Menu: flashlight")
        startActivity(Intent(this, FlashlightActivity::class.java))
        finish()
    }

    private fun openWallet() {
        Log.d(TAG, "Menu: wallet")
        // launchApp already toasts when the app is missing; staying put leaves the menu on screen
        // rather than dropping the user onto nothing.
        if (launchApp(WALLET_PACKAGE)) {
            finish()
        }
    }

    private fun dp(value: Float): Int = (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val TAG = "MenuActivity"

        const val BACKGROUND_COLOR = 0xFF000000.toInt()
        const val TITLE_COLOR = 0xFF9E9E9E.toInt()
        const val ROW_COLOR = 0xFF1F1F1F.toInt()
        const val ROW_PRESSED_COLOR = 0xFF3A3A3A.toInt()
        const val ROW_TEXT_COLOR = 0xFFFFFFFF.toInt()

        const val TITLE_TEXT_SIZE_SP = 13f
        const val TITLE_BOTTOM_PADDING_DP = 2f
        const val ROW_TEXT_SIZE_SP = 15f
        const val ROW_MIN_HEIGHT_DP = 48f
        const val ROW_CORNER_RADIUS_DP = 24f
        const val GAP_DP = 8f
        const val TAP_VIBRATION_MILLIS = 20L
    }
}
