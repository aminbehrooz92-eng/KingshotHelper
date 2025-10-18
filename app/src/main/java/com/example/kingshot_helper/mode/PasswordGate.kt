package com.example.kingshot_helper.mode

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object PasswordGate {

    // Default sample passwords (10 digits). Replace if needed.
    private const val MASTER_PASS = "5333797910"  // permanent
    private const val TEMP_PASS   = "7633244613"  // one-time, 2h

    enum class UnlockResult { PERM_UNLOCKED, TEMP_UNLOCKED, INVALID, TEMP_ALREADY_USED }

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (this::prefs.isInitialized) return
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            context,
            "password_gate",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isAdvancedPermEnabled(): Boolean = prefs.getBoolean("perm", false)

    fun isTempActive(nowMs: Long = System.currentTimeMillis()): Boolean {
        val exp = prefs.getLong("temp_exp", 0L)
        return exp > nowMs
    }

    fun tryUnlock(input: String, nowMs: Long = System.currentTimeMillis()): UnlockResult {
        if (input == MASTER_PASS) {
            prefs.edit().putBoolean("perm", true).apply()
            return UnlockResult.PERM_UNLOCKED
        }
        if (input == TEMP_PASS) {
            if (prefs.getBoolean("temp_used", false)) {
                return UnlockResult.TEMP_ALREADY_USED
            }
            val twoHoursMs = 2L * 60L * 60L * 1000L
            prefs.edit()
                .putBoolean("temp_used", true)
                .putLong("temp_exp", nowMs + twoHoursMs)
                .apply()
            return UnlockResult.TEMP_UNLOCKED
        }
        return UnlockResult.INVALID
    }
}
