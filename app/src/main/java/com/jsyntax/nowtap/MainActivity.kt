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
import java.util.Calendar

private const val ACTION_WALLET = "wallet"
private const val ACTION_UNKNOWN = "unknown"
private const val ACTION_FLASHLIGHT = "flashlight"

class MainActivity : Activity() {
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private lateinit var vibrator: Vibrator

    companion object {
        // ***********************
        // UPDATE THESE TO YOUR APPS
        // REBUILD APK and INSTALL if you want to customize
        private const val WALLET_PACKAGE = "com.google.android.apps.walletnfcrel"
        // ***********************

        private var LUX_THRESHOLD: Float = 0f // near pitch black, above 10 is dim but not a situation where you'd use a flashlight, 20 is quite bright
        private const val TAPPED_TWICE_THRESHOLD_MILLISECONDS: Long = 3000 // the time between the user invoking this shortcut and the next time they can invoke it again
        private const val TAG = "NowTap"
        private const val SHOW_LUX_TEST = false
        private const val PREFS_NAME = "NowTapPreferences"
        private const val LAST_TAP_TIME_KEY = "lastTapTime"
        private const val LAST_ACTION_KEY = "lastAction"
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
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
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
            Log.d(TAG, "decideBasedOnBrightnessAndTime")
            launchWallet()
        } else {
            launchFlashlightMaybe()
        }
    }

    private fun decideBasedOnTimeOnly() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (isTimeForFlashlight(currentHour)) {
            launchFlashlightMaybe()
        } else {
            Log.d(TAG, "decideBasedOnTimeOnly")
            launchWallet()
        }
    }

    private fun isTimeForFlashlight(hour: Int): Boolean = hour in 23..24 || hour in 0..6

    private fun launchFlashlightMaybe() {
        val (sharedPreferences, lastTapTime, lastAction) = getLastTapTimeAndAction()
        val currentTime = System.currentTimeMillis()

        val userTappedTwiceQuickly = currentTime - lastTapTime < TAPPED_TWICE_THRESHOLD_MILLISECONDS

        if (userTappedTwiceQuickly && lastAction == ACTION_FLASHLIGHT) {
            Log.d(TAG, "User tapped twice quickly. Flashlight already launched, launching wallet.")
            launchWallet()
        } else {
            Log.d(TAG, "Launching flashlight.")
            launchFlashlight(sharedPreferences, currentTime)
        }
    }

    private fun launchFlashlight(sharedPreferences: SharedPreferences, currentTime: Long) {
        val intent = Intent(this, FlashlightActivity::class.java)
        startActivity(intent)
        finish()
        updateLastTapAndAction(sharedPreferences, currentTime, ACTION_FLASHLIGHT)
    }

    private fun launchWallet() {
        val (sharedPreferences) = getLastTapTimeAndAction()
        Log.d(TAG, "opening wallet")
        _launchAppWithCheck(WALLET_PACKAGE)
        updateLastTapAndAction(sharedPreferences, System.currentTimeMillis(), ACTION_WALLET)
    }

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
