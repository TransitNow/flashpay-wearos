package com.jsyntax.nowtap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import java.util.*

class MainActivity : Activity() {
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private lateinit var vibrator: Vibrator

    companion object {
        // ***********************
        // UPDATE THESE TO YOUR APPS
        // REBUILD APK and INSTALL if you want to customize
        private const val WALLET_PACKAGE = "com.google.android.apps.walletnfcrel"
        private const val REWARDS_APP_PACKAGE = "de.stocard.stocard"
        // ***********************
        // ***********************


        private var LUX_THRESHOLD: Float = 0f // near pitch black, above 10 is dim but not a situation where you'd use a flashlight, 20 is quite bright
        private const val QUAD_TAP_WINDOW: Long =
            3000 // Assume this is used for Galaxy Watch where double tap opens this app
        private const val TAG = "NowTap"
        private const val SHOW_LUX_TEST = false
        private const val PREFS_NAME = "NowTapPreferences"
        private const val LAST_TAP_TIME_KEY = "lastTapTime"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        registerLightSensorOrDecideBasedOnTime()
        vibrator.vibrate(50)
    }

    private fun registerLightSensorOrDecideBasedOnTime() {
        if (lightSensor != null) {
            sensorManager.registerListener(
                lightSensorListener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else {
            decideBasedOnTimeOnly()
        }
    }

    private val lightSensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            val lux = event.values[0]
            Log.d(TAG, "Current brightness (lux): $lux")
            if (SHOW_LUX_TEST) {
                Toast.makeText(this@MainActivity, "Lux: $lux", Toast.LENGTH_LONG).show()
            }
            decideBasedOnBrightnessAndTime(lux)
            sensorManager.unregisterListener(this)
        }
    }

    private fun decideBasedOnBrightnessAndTime(brightness: Float) {
        val isBrightEnough = brightness > LUX_THRESHOLD
        if (isBrightEnough) {
            launchWallet()
        } else {
            launchFlashlight()
        }
    }

    private fun decideBasedOnTimeOnly() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (isTimeForFlashlight(currentHour)) {
            launchFlashlight()
        } else {
            launchWallet()
        }
    }

    private fun isTimeForFlashlight(hour: Int): Boolean = hour in 23..24 || hour in 0..6

    private fun launchFlashlight() {
        val (sharedPreferences, lastTapTime) = getLastTapTime()
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastTapTime < QUAD_TAP_WINDOW) {
            launchWallet()
        } else {
            val intent = Intent(this, FlashlightActivity::class.java)
            startActivity(intent)
            finish()
        }

        updateLastTapTime(sharedPreferences, currentTime)
    }

    private fun launchWallet() {
        val (sharedPreferences, lastTapTime) = getLastTapTime()
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastTapTime < QUAD_TAP_WINDOW) {
            lightSensor?.also {
                sensorManager.unregisterListener(lightSensorListener)
            }
            _launchAppWithCheck(REWARDS_APP_PACKAGE)
        } else {
            _launchAppWithCheck(WALLET_PACKAGE)
        }

        updateLastTapTime(sharedPreferences, currentTime)
    }

    private fun updateLastTapTime(
        sharedPreferences: SharedPreferences,
        currentTime: Long
    ) {
        with(sharedPreferences.edit()) {
            putLong(LAST_TAP_TIME_KEY, currentTime)
            apply()
        }
    }

    private fun getLastTapTime(): Pair<SharedPreferences, Long> {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val lastTapTime = sharedPreferences.getLong(LAST_TAP_TIME_KEY, 0)
        return Pair(sharedPreferences, lastTapTime)
    }

    private fun Context._launchAppWithCheck(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
            Log.d(TAG, "Launching app: $packageName")
            finish()
        } else {
            Log.d(TAG, "App cannot be launched: $packageName")
            Toast.makeText(this, "App not found: $packageName", Toast.LENGTH_LONG).show()
        }
    }
}
