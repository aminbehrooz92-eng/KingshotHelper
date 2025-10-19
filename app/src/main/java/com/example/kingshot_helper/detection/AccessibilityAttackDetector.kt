package com.example.kingshot_helper.detection

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AccessibilityAttackDetector : AccessibilityService(), AttackDetector {
    private val _events = MutableSharedFlow<AttackEvent>(extraBufferCapacity = 8)
    override val events = _events.asSharedFlow()
    @Volatile private var active = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!active || event == null) return
        val txt = (event.text?.joinToString(" ") ?: "").lowercase()
        if (txt.contains("under attack") || txt.contains("attack")) {
            _events.tryEmit(AttackEvent.AttackDetected)
        }
    }
    override fun onInterrupt() { }

    override fun start() { active = true }
    override fun stop()  { active = false }
}
