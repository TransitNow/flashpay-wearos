package com.jsyntax.nowtap

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class VoiceActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice)

        val i = Intent()
        i.setClassName("com.google.android.apps.assistant", "com.google.android.apps.assistant.go.MainActivity")
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }
}