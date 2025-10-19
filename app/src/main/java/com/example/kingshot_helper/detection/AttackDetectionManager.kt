package com.example.kingshot_helper.detection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AttackDetectionManager(
    private val detectors: List<AttackDetector>,
    private val onAttack: () -> Unit,
    private val onCleared: () -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    fun start() {
        job = scope.launch {
            detectors.forEach { it.start() }
            detectors.forEach { detector ->
                launch {
                    detector.events.collectLatest { e ->
                        when (e) {
                            is AttackEvent.AttackDetected -> onAttack()
                            is AttackEvent.Cleared       -> onCleared()
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        detectors.forEach { it.stop() }
        job?.cancel()
    }
}
