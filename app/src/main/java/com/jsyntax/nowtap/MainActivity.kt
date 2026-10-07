package com.jsyntax.nowtap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val ACTION_WALLET = "wallet"
private const val ACTION_UNKNOWN = "unknown"
private const val ACTION_FLASHLIGHT = "flashlight"

class MainActivity : Activity() {
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private lateinit var vibrator: Vibrator

    private val handler = Handler(Looper.getMainLooper())
    private var createdAtUptime = 0L
    private var decided = false

    // Every light sample seen before deciding, for the decision log: [ms after onCreate, lux, sample age ms].
    // A large age on the first sample means the system handed over a cached reading, not a fresh one.
    private val samples = JSONArray()
    private var darkestLux = Float.MAX_VALUE

    // Snapshot of the previous press, read before this press overwrites it.
    private var previousTapTime = 0L
    private var previousAction = ACTION_UNKNOWN

    companion object {
        // WALLET_PACKAGE lives in AppLauncher.kt — update it there to point at a different app.

        // At or below this many lux is "dark" and opens the flashlight. This used to be 0, which only
        // worked on sensors that floor at exactly 0.0 in the dark; a sensor that reads 1-3 lux in a
        // pitch-black room (suspected on the Galaxy Watch 9 — DecisionLog will say) sent every dark
        // press to Wallet.
        // For scale: a room lit only by a phone screen or a streetlight through a window is ~1-5 lux,
        // a dim restaurant is 20+.
        private const val LUX_THRESHOLD: Float = 5f

        // The first sample after registering can be the system's cached reading from whenever the
        // sensor last ran, rather than the room you're in now. Darkness is acted on immediately, but a
        // bright first sample is held this long in case a darker, fresher one follows. Costs only the
        // wallet path, and Wallet takes far longer than this to draw anyway.
        private const val BRIGHT_CONFIRM_MILLIS = 200L

        // A light sensor is meant to report once on registration. If it never does, decide by the
        // clock rather than leave an invisible activity on screen. Kept generous on purpose: a sensor
        // that was off needs an integration period before its first sample, and timing out early
        // would decide by the clock — which at 9pm in a dark room is Wallet, the very bug.
        private const val SENSOR_TIMEOUT_MILLIS = 1500L

        private const val TAPPED_TWICE_THRESHOLD_MILLISECONDS: Long = 3000 // the time between the user invoking this shortcut and the next time they can invoke it again
        private const val TAG = "NowTap"
        private const val SHOW_LUX_TEST = false
        private const val PREFS_NAME = "NowTapPreferences"
        private const val LAST_TAP_TIME_KEY = "lastTapTime"
        private const val LAST_ACTION_KEY = "lastAction"
    }

    private val sensorTimeout = Runnable {
        Log.d(TAG, "No light sample within ${SENSOR_TIMEOUT_MILLIS}ms; deciding by time")
        decideBasedOnTimeOnly("sensor_timeout")
    }

    private val confirmBright = Runnable { decideBasedOnBrightnessAndTime(darkestLux, "bright") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createdAtUptime = SystemClock.uptimeMillis()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val (_, lastTapTime, lastAction) = getLastTapTimeAndAction()
        previousTapTime = lastTapTime
        previousAction = lastAction
        registerLightSensorOrDecideBasedOnTime()
        vibrator.vibrate(50)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        sensorManager.unregisterListener(lightSensorListener)
        super.onDestroy()
    }

    private fun registerLightSensorOrDecideBasedOnTime() {
        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            handler.postDelayed(sensorTimeout, SENSOR_TIMEOUT_MILLIS)
        } else {
            decideBasedOnTimeOnly("no_sensor")
        }
    }

    private val lightSensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            if (decided) return
            val lux = event.values[0]
            val ageMillis = (SystemClock.elapsedRealtimeNanos() - event.timestamp) / 1_000_000
            Log.d(TAG, "Current brightness (lux): $lux, sample age ${ageMillis}ms")
            if (SHOW_LUX_TEST) {
                Toast.makeText(this@MainActivity, "Lux: $lux", Toast.LENGTH_LONG).show()
            }
            samples.put(JSONArray().put(elapsedMillis()).put(lux.toDouble()).put(ageMillis))
            val isFirstSample = darkestLux == Float.MAX_VALUE
            darkestLux = minOf(darkestLux, lux)

            if (darkestLux <= LUX_THRESHOLD) {
                decideBasedOnBrightnessAndTime(darkestLux, "dark")
            } else if (isFirstSample) {
                handler.removeCallbacks(sensorTimeout)
                handler.postDelayed(confirmBright, BRIGHT_CONFIRM_MILLIS)
            }
        }
    }

    private fun decideBasedOnBrightnessAndTime(brightness: Float, reason: String) {
        if (!markDecided()) return
        val isBrightEnough = brightness > LUX_THRESHOLD
        if (isBrightEnough) {
            Log.d(TAG, "decideBasedOnBrightnessAndTime")
            launchWallet(reason)
        } else {
            launchFlashlightMaybe(reason)
        }
    }

    private fun decideBasedOnTimeOnly(reason: String) {
        if (!markDecided()) return
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (isTimeForFlashlight(currentHour)) {
            launchFlashlightMaybe("${reason}_night")
        } else {
            Log.d(TAG, "decideBasedOnTimeOnly")
            launchWallet("${reason}_day")
        }
    }

    /** Returns false if a decision was already made; otherwise stops listening and claims it. */
    private fun markDecided(): Boolean {
        if (decided) return false
        decided = true
        handler.removeCallbacksAndMessages(null)
        sensorManager.unregisterListener(lightSensorListener)
        return true
    }

    private fun isTimeForFlashlight(hour: Int): Boolean = hour in 23..24 || hour in 0..6

    private fun launchFlashlightMaybe(reason: String) {
        val (sharedPreferences, lastTapTime, lastAction) = getLastTapTimeAndAction()
        val currentTime = System.currentTimeMillis()

        val userTappedTwiceQuickly = currentTime - lastTapTime < TAPPED_TWICE_THRESHOLD_MILLISECONDS

        if (userTappedTwiceQuickly && lastAction == ACTION_FLASHLIGHT) {
            Log.d(TAG, "User tapped twice quickly. Flashlight already launched, launching wallet.")
            launchWallet("$reason+second_press")
        } else {
            Log.d(TAG, "Launching flashlight.")
            launchFlashlight(sharedPreferences, currentTime)
            logDecision(ACTION_FLASHLIGHT, reason)
        }
    }

    private fun launchFlashlight(sharedPreferences: SharedPreferences, currentTime: Long) {
        val intent = Intent(this, FlashlightActivity::class.java)
        startActivity(intent)
        finish()
        updateLastTapAndAction(sharedPreferences, currentTime, ACTION_FLASHLIGHT)
    }

    private fun launchWallet(reason: String) {
        val (sharedPreferences) = getLastTapTimeAndAction()
        Log.d(TAG, "opening wallet")
        val launched = launchApp(WALLET_PACKAGE)
        updateLastTapAndAction(sharedPreferences, System.currentTimeMillis(), ACTION_WALLET)
        if (!launched) {
            // Wallet is not installed. This activity is translucent and holds no content, so
            // finishing on its own would read as a blank screen that never goes away — the state a
            // Play reviewer on a watch without Wallet would land in. Show the menu instead.
            startActivity(Intent(this, MenuActivity::class.java))
        }
        finish()
        logDecision(if (launched) ACTION_WALLET else "menu_no_wallet", reason)
    }

    private fun logDecision(action: String, reason: String) {
        val sensor = lightSensor
        val record = JSONObject()
            .put("time", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).format(Date()))
            .put("action", action)
            .put("reason", reason)
            .put("thresholdLux", LUX_THRESHOLD.toDouble())
            .put("decidedAfterMs", elapsedMillis())
            .put("samples", samples)
            .put("msSincePreviousPress", System.currentTimeMillis() - previousTapTime)
            .put("previousAction", previousAction)
            .put("screenInteractive", (getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive)
            .put("referrer", referrer?.toString())
            .put("device", "${Build.MANUFACTURER} ${Build.MODEL} / API ${Build.VERSION.SDK_INT}")
            .put("app", "${packageManager.getPackageInfo(packageName, 0).versionName}")
        if (sensor != null) {
            record.put(
                "sensor",
                JSONObject()
                    .put("name", sensor.name)
                    .put("vendor", sensor.vendor)
                    .put("version", sensor.version)
                    .put("maxRange", sensor.maximumRange.toDouble())
                    .put("resolution", sensor.resolution.toDouble())
                    .put("minDelayUs", sensor.minDelay)
                    .put("reportingMode", sensor.reportingMode),
            )
        }
        record.put("allLightSensors", JSONArray(sensorManager.getSensorList(Sensor.TYPE_LIGHT).map { "${it.name} (${it.vendor})" }))
        DecisionLog.append(this, record)
    }

    private fun elapsedMillis(): Long = SystemClock.uptimeMillis() - createdAtUptime

    private fun updateLastTapAndAction(sharedPreferences: SharedPreferences, lastTapTime: Long, lastAction: String) {
        with(sharedPreferences.edit()) {
            putLong(LAST_TAP_TIME_KEY, lastTapTime)
            putString(LAST_ACTION_KEY, lastAction)
            apply()
        }
    }

    private fun getLastTapTimeAndAction(): Triple<SharedPreferences, Long, String> {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val lastTapTime = sharedPreferences.getLong(LAST_TAP_TIME_KEY, 0)
        val lastAction = sharedPreferences.getString(LAST_ACTION_KEY, "unknown") ?: "unknown"

        return Triple(sharedPreferences, lastTapTime, lastAction)
    }
}
