package com.jsyntax.nowtap

import android.content.Context
import android.util.Log
import android.widget.Toast

// ***********************
// UPDATE THIS TO YOUR APP
// REBUILD APK and INSTALL if you want to customize
const val WALLET_PACKAGE = "com.google.android.apps.walletnfcrel"
// ***********************

private const val TAG = "NowTap"

/**
 * Launches [packageName], or toasts when it isn't installed.
 * Returns true when the app was actually launched, so callers can decide whether to finish.
 */
fun Context.launchApp(packageName: String): Boolean {
    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
    if (launchIntent == null) {
        Log.d(TAG, "App cannot be launched: $packageName")
        Toast.makeText(this, "App not found: $packageName", Toast.LENGTH_LONG).show()
        return false
    }

    startActivity(launchIntent)
    Log.d(TAG, "Launching app: $packageName")
    return true
}
