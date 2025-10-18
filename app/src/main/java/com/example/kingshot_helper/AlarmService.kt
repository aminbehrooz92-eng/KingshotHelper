package com.example.kingshot_helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
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
        const val CHANNEL_ID = "kingshot_alarm_channel"
        const val NOTIF_ID = 1201
        const val ACTION_START = "kingshot.ACTION_START_ALARM"
        const val ACTION_STOP = "kingshot.ACTION_STOP_ALARM"

        fun start(context: Context) {
            val i = Intent(context, AlarmService::class.java).apply { action = ACTION_START }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(i)
            } else {
                context.startService(i)
            }
        }

        fun stop(context: Context) {
            val i = Intent(context, AlarmService::class.java).apply { action = ACTION_STOP }
            context.startService(i)
        }
    }

    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startAlarm()
            ACTION_STOP  -> stopAlarm()
        }
        return START_STICKY
    }

    private fun startAlarm() {
        // دکمه STOP در اعلان
        val stopIntent = Intent(this, AlarmService::class.java).apply { action = ACTION_STOP }
        val stopPI = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // یک آیکون برداری در res/drawable بگذارید
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.alarm_active))
            .setOngoing(true)
            .addAction(0, getString(R.string.stop_alarm), stopPI)
            .build()

        startForeground(NOTIF_ID, notification)

        // پخش صدا از res/raw/alarm.mp3
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.alarm).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                isLooping = true
                start()
            }
        } else if (player?.isPlaying == false) {
            player?.start()
        }

        // ویبرهٔ تکرارشونده
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(
                        longArrayOf(0, 600, 400), /* delay, vibrate, pause */
                        0 /* تکرار از ابتدا */
                    )
                    v.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 600, 400), 0)
                }
            }
        } catch (_: Exception) { }
    }

    private fun stopAlarm() {
        try { vibrator?.cancel() } catch (_: Exception) { }

        player?.let { mp ->
            try { if (mp.isPlaying) mp.stop() } catch (_: Exception) { }
            try { mp.release() } catch (_: Exception) { }
        }
        player = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
                val ch = NotificationChannel(
                    CHANNEL_ID,
                    "Kingshot Alarm",
                    NotificationManager.IMPORTANCE_HIGH
                )
                mgr.createNotificationChannel(ch)
            }
        }
    }
}
