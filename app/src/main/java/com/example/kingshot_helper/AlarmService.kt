// app/src/main/java/com/example/kingshot_helper/AlarmService.kt
package com.example.kingshot_helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    companion object {
        const val ACTION_START = "start"
        const val ACTION_STOP  = "stop"
        private const val CH_ID = "alarm_channel"

        fun intent(ctx: Context, action: String) =
            Intent(ctx, AlarmService::class.java).setAction(action)
    }

    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onStartCommand(i: Intent?, flags: Int, startId: Int): Int {
        when (i?.action) {
            ACTION_START -> startAlarm()
            ACTION_STOP  -> stopSelf()
        }
        return START_STICKY
    }

    private fun startAlarm() {
        createChannel()

        val fullIntent = Intent(this, ui.FullScreenAlarmActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = PendingIntent.getActivity(
            this, 1, fullIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = intent(this, ACTION_STOP)
        val stopPi = PendingIntent.getService(
            this, 2, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(this, CH_ID)
            .setSmallIcon(R.drawable.ic_notification) // مطمئن شو وجود دارد
            .setContentTitle(getString(R.string.alarm))
            .setContentText(getString(R.string.activating_shield))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(pi, true)
            .addAction(0, getString(R.string.stop), stopPi)
            .build()

        startForeground(1001, notif)

        // پخش صدا
        player?.release()
        player = MediaPlayer.create(this, R.raw.alarm).apply {
            isLooping = true
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            start()
        }

        // ویبره
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val effect = if (Build.VERSION.SDK_INT >= 26)
            VibrationEffect.createWaveform(longArrayOf(0, 600, 400), 0)
        else null
        if (effect != null) vibrator?.vibrate(effect) else vibrator?.vibrate(600)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(CH_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        CH_ID,
                        getString(R.string.channel_name),
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply { description = getString(R.string.channel_desc) }
                )
            }
        }
    }

    override fun onDestroy() {
        player?.stop(); player?.release(); player = null
        vibrator?.cancel(); vibrator = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
