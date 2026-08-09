package com.jsyntax.nowtap

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.Vibrator
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Full-screen screen-as-flashlight. The face is split into four tap targets, in reading order:
 *
 *   top-left  WALLET  — override the flashlight and jump straight to Google Wallet
 *   top-right COLOR   — toggle white / red (red preserves night vision)
 *   bot-left  BRIGHT  — step the screen brightness down through 5 levels, wrapping back to full
 *   bot-right TIMER   — cycle the auto-off duration, restarting the countdown from the new value
 *
 * Colour, brightness and timer choices persist, so the light comes back the way you left it.
 */
class FlashlightActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private val autoOffRunnable = Runnable {
        Log.d(TAG, "Runnable executing; finishing activity")
        finish()
    }
    private val tickRunnable = object : Runnable {
        override fun run() {
            timerLabel.text = timerText()
            handler.postDelayed(this, TICK_INTERVAL_MILLIS)
        }
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var vibrator: Vibrator
    private lateinit var rootLayout: FrameLayout
    private lateinit var colorLabel: TextView
    private lateinit var brightnessLabel: TextView
    private lateinit var timerLabel: TextView

    private var isRedMode = false
    private var brightnessStep = BRIGHTNESS_STEPS
    private var timerIndex = DEFAULT_TIMER_INDEX
    private var autoOffAtUptime = 0L

    companion object {
        private const val TAG = "FlashlightActivity"
        private const val PREFS_NAME = "FlashlightPreferences"
        private const val IS_RED_MODE_KEY = "isRedMode"
        private const val BRIGHTNESS_STEP_KEY = "brightnessStep"
        private const val TIMER_INDEX_KEY = "timerIndex"

        private const val WHITE_COLOR = 0xFFFFFFFF.toInt()
        private const val RED_COLOR = 0xFFFF0000.toInt()
        private const val LABEL_COLOR = 0xFF000000.toInt()
        private const val DIVIDER_COLOR = 0x22000000

        private const val BRIGHTNESS_STEPS = 5

        // Tapping the timer quadrant walks this list and restarts the countdown from the new value,
        // so two taps from 5:00 lands on 1:00 and counts down from there.
        private val TIMER_OPTIONS_SECONDS = intArrayOf(30, 60, 120, 240, 300)
        private const val DEFAULT_TIMER_INDEX = 4 // 5 minutes, matching the original auto-off

        private const val TICK_INTERVAL_MILLIS = 1000L
        private const val TAP_VIBRATION_MILLIS = 20L
        private const val LABEL_TEXT_SIZE_SP = 12f
        private const val LABEL_PADDING_DP = 4f
        private const val DIVIDER_THICKNESS_DP = 1f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        isRedMode = sharedPreferences.getBoolean(IS_RED_MODE_KEY, false)
        brightnessStep = sharedPreferences.getInt(BRIGHTNESS_STEP_KEY, BRIGHTNESS_STEPS)
        timerIndex = sharedPreferences.getInt(TIMER_INDEX_KEY, DEFAULT_TIMER_INDEX)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setContentView(buildQuadrantLayout())
        applyColor()
        applyBrightness()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        restartAutoOff()
    }

    // ---- layout -------------------------------------------------------------------------------

    private fun buildQuadrantLayout(): View {
        colorLabel = buildQuadrant(colorText()) { toggleColor() }
        brightnessLabel = buildQuadrant(brightnessText()) { cycleBrightness() }
        timerLabel = buildQuadrant(timerText()) { cycleTimer() }
        val walletLabel = buildQuadrant("WALLET") { openWallet() }

        val quadrants = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(buildRow(walletLabel, colorLabel), LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
            addView(buildRow(brightnessLabel, timerLabel), LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
        }

        val thickness = dp(DIVIDER_THICKNESS_DP).coerceAtLeast(1)
        rootLayout = FrameLayout(this).apply {
            addView(quadrants, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
            addView(buildDivider(MATCH_PARENT, thickness))
            addView(buildDivider(thickness, MATCH_PARENT))
        }
        return rootLayout
    }

    private fun buildRow(left: TextView, right: TextView): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(left, LinearLayout.LayoutParams(0, MATCH_PARENT, 1f))
            addView(right, LinearLayout.LayoutParams(0, MATCH_PARENT, 1f))
        }

    private fun buildQuadrant(text: String, onTap: () -> Unit): TextView =
        TextView(this).apply {
            this.text = text
            gravity = Gravity.CENTER
            setTextColor(LABEL_COLOR)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, LABEL_TEXT_SIZE_SP)
            dp(LABEL_PADDING_DP).let { setPadding(it, it, it, it) }
            setOnClickListener {
                vibrator.vibrate(TAP_VIBRATION_MILLIS)
                onTap()
            }
        }

    private fun buildDivider(widthPx: Int, heightPx: Int): View =
        View(this).apply {
            setBackgroundColor(DIVIDER_COLOR)
            layoutParams = FrameLayout.LayoutParams(widthPx, heightPx, Gravity.CENTER)
        }

    private fun dp(value: Float): Int = (value * resources.displayMetrics.density).toInt()

    // ---- quadrant actions ---------------------------------------------------------------------

    private fun openWallet() {
        Log.d(TAG, "Wallet quadrant tapped")
        if (launchApp(WALLET_PACKAGE)) {
            finish()
        }
    }

    private fun toggleColor() {
        isRedMode = !isRedMode
        applyColor()
        sharedPreferences.edit().putBoolean(IS_RED_MODE_KEY, isRedMode).apply()
        Log.d(TAG, "Color toggled to: ${if (isRedMode) "RED" else "WHITE"}")
    }

    private fun cycleBrightness() {
        // Steps down from full and wraps, so the first tap dims rather than jumping to the bottom.
        brightnessStep = if (brightnessStep <= 1) BRIGHTNESS_STEPS else brightnessStep - 1
        applyBrightness()
        sharedPreferences.edit().putInt(BRIGHTNESS_STEP_KEY, brightnessStep).apply()
        Log.d(TAG, "Brightness set to step $brightnessStep/$BRIGHTNESS_STEPS")
    }

    private fun cycleTimer() {
        timerIndex = (timerIndex + 1) % TIMER_OPTIONS_SECONDS.size
        sharedPreferences.edit().putInt(TIMER_INDEX_KEY, timerIndex).apply()
        restartAutoOff()
        Log.d(TAG, "Auto-off set to ${TIMER_OPTIONS_SECONDS[timerIndex]}s")
    }

    // ---- applying state -----------------------------------------------------------------------

    private fun applyColor() {
        rootLayout.setBackgroundColor(if (isRedMode) RED_COLOR else WHITE_COLOR)
        colorLabel.text = colorText()
    }

    private fun applyBrightness() {
        try {
            val params = window.attributes
            params.screenBrightness = brightnessStep.toFloat() / BRIGHTNESS_STEPS
            window.attributes = params
        } catch (e: Exception) {
            e.printStackTrace()
        }
        brightnessLabel.text = brightnessText()
    }

    private fun restartAutoOff() {
        val durationMillis = TIMER_OPTIONS_SECONDS[timerIndex] * 1000L
        autoOffAtUptime = SystemClock.uptimeMillis() + durationMillis

        handler.removeCallbacks(autoOffRunnable)
        handler.removeCallbacks(tickRunnable)
        handler.postDelayed(autoOffRunnable, durationMillis)
        handler.postDelayed(tickRunnable, TICK_INTERVAL_MILLIS)

        timerLabel.text = timerText()
    }

    // ---- labels -------------------------------------------------------------------------------

    private fun colorText(): String = "COLOR\n" + if (isRedMode) "RED" else "WHITE"

    private fun brightnessText(): String = "BRIGHT\n$brightnessStep/$BRIGHTNESS_STEPS"

    private fun timerText(): String {
        // Round up so a fresh 5 minute timer reads 5:00 rather than 4:59.
        val remaining = ((autoOffAtUptime - SystemClock.uptimeMillis() + 999) / 1000).coerceAtLeast(0)
        return "TIMER\n${remaining / 60}:${(remaining % 60).toString().padStart(2, '0')}"
    }

    // ---- lifecycle ----------------------------------------------------------------------------

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called; removing callbacks")
        handler.removeCallbacks(autoOffRunnable)
        handler.removeCallbacks(tickRunnable)
        finish()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called; restarting auto-off")
        restartAutoOff()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called; removing callbacks")
        handler.removeCallbacks(autoOffRunnable)
        handler.removeCallbacks(tickRunnable)
    }
}
