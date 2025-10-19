package com.example.kingshot_helper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.kingshot_helper.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var bind: ActivityMainBinding

    private val notifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* ignore */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Android 13+: اجازه نوتیفیکیشن برای فورگراند‌سرویس
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        bind.btnStartAlarm.setOnClickListener {
            ContextCompat.startForegroundService(
                this, AlarmService.intent(this, AlarmService.ACTION_START)
            )
        }

        bind.btnStopAlarm.setOnClickListener {
            startService(AlarmService.intent(this, AlarmService.ACTION_STOP))
        }

        bind.btnOpenGame.setOnClickListener {
            openGame("com.run.tower.defense")
        }
    }

    private fun openGame(pkg: String) {
        val launch = packageManager.getLaunchIntentForPackage(pkg)
        if (launch != null) {
            startActivity(launch)
        } else {
            // اگر نصب نبود، صفحه‌ی جزییات نصب (اختیاری)
            val uri = Uri.parse("market://details?id=$pkg")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }
}
