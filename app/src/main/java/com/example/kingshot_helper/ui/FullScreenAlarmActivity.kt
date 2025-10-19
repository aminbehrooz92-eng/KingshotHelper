// app/src/main/java/com/example/kingshot_helper/ui/FullScreenAlarmActivity.kt
package com.example.kingshot_helper.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import com.example.kingshot_helper.AlarmService
import com.example.kingshot_helper.databinding.ActivityFullscreenTriggerBinding
import androidx.core.content.ContextCompat

class FullScreenAlarmActivity : ComponentActivity() {
    private lateinit var bind: ActivityFullscreenTriggerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityFullscreenTriggerBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // با اولین لمس، آلارم قطع شود
        bind.root.setOnTouchListener { _, _ ->
            stopAlarm(); true
        }
        bind.btnStop.setOnClickListener { stopAlarm() }
    }
    private fun stopAlarm() {
        startService(AlarmService.intent(this, AlarmService.ACTION_STOP))
        finish()
    }
}
