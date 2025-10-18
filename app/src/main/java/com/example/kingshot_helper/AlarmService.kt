package com.example.kingshot_helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.kingshot_helper.ui.FullScreenAlarmActivity

class AlarmService : Service() {
    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ch = "kingshot_alarm_channel"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(NotificationChannel(ch, "Kingshot Alarm", NotificationManager.IMPORTANCE_HIGH))
        }

        val fullIntent = Intent(this, FullScreenAlarmActivity::class.java)
        val pi = PendingIntent.getActivity(this, 2001, fullIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(this, ch)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(getString(R.string.attack_detected))
            .setContentText(getString(R.string.tap_to_activate))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(pi, true)
            .setAutoCancel(true)
            .build()

        nm.notify(2002, notif)
        stopSelf()
        return START_NOT_STICKY
    }
}
