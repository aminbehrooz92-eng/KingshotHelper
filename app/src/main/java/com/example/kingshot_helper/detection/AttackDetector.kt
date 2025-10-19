package com.example.kingshot_helper.detection

import kotlinx.coroutines.flow.Flow

sealed class AttackEvent {
    data object AttackDetected : AttackEvent()
    data object Cleared : AttackEvent()
}

interface AttackDetector {
    /** استریم رویدادها؛ وقتی حمله تشخیص شد AttackDetected می‌فرستد */
    val events: Flow<AttackEvent>

    /** شروع/توقف شنود */
    fun start()
    fun stop()
}
