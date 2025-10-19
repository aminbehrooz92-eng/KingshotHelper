package com.example.kingshot_helper.detection

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationAttackDetector : NotificationListenerService(), AttackDetector {

    private val _events = MutableSharedFlow<AttackEvent>(extraBufferCapacity = 8)
    override val events = _events.asSharedFlow()

    private var active = false

    override fun onListenerConnected() { /* ready */ }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (!active) return
        val pkg = sbn.packageName ?: return
        val text = sbn.notification.extras.getCharSequence("android.text")?.toString() ?: ""
        val title = sbn.notification.extras.getCharSequence("android.title")?.toString() ?: ""

        // TODO: نام پکیج بازی را بگذار
        if (pkg == "com.run.tower.defense") {
            val payload = (title + " " + text).lowercase()
            if (payload.contains("under attack") || payload.contains("attack")) {
                _events.tryEmit(AttackEvent.AttackDetected)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (!active) return
        if (sbn.packageName == "com.run.tower.defense") {
            _events.tryEmit(AttackEvent.Cleared)
        }
    }

    override fun start() { active = true }
    override fun stop()  { active = false }
}
