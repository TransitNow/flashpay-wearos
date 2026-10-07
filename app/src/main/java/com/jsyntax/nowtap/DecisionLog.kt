package com.jsyntax.nowtap

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Executors

/**
 * An on-watch record of every button press: what the light sensor said, and what the app opened
 * because of it. One JSON object per line, appended off the main thread so the trampoline still
 * acts on its first frame.
 *
 * It lives in the app's external files dir because that is the one place a release (non-debuggable)
 * build can write and `adb` can read without `run-as`:
 *
 *   adb pull /sdcard/Android/data/com.jsyntax.nowtap/files/decisions.jsonl
 *
 * Nothing here leaves the watch. The file is capped at [MAX_BYTES] and rotated into a single
 * `.1` backup, so it can never grow past twice that.
 */
object DecisionLog {
    private const val TAG = "NowTap"
    private const val FILE_NAME = "decisions.jsonl"
    private const val BACKUP_FILE_NAME = "decisions.1.jsonl"
    private const val MAX_BYTES = 64 * 1024L

    private val writer = Executors.newSingleThreadExecutor()

    fun append(context: Context, record: JSONObject) {
        val line = record.toString()
        // Logcat too, for when the watch is connected while it happens: adb logcat -s NowTap
        Log.i(TAG, "decision $line")
        val dir = context.applicationContext.getExternalFilesDir(null) ?: return
        writer.execute {
            try {
                val file = File(dir, FILE_NAME)
                if (file.length() > MAX_BYTES) {
                    file.renameTo(File(dir, BACKUP_FILE_NAME))
                }
                File(dir, FILE_NAME).appendText(line + "\n")
            } catch (e: Exception) {
                Log.w(TAG, "decision log write failed", e)
            }
        }
    }
}
