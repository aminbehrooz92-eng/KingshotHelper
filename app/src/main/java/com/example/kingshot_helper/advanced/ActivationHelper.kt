package com.example.kingshot_helper.advanced

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.example.kingshot_helper.mode.PasswordGate

object ActivationHelper {
    private val handler = Handler(Looper.getMainLooper())

    fun startAdvancedActivation(context: Context, packageName: String, deepLinkUri: String?) {
        // Allow only if gate permits
        PasswordGate.init(context)
        if (!(PasswordGate.isAdvancedPermEnabled() || PasswordGate.isTempActive())) return

        val launch = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launch != null) {
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launch)
        } else return

        handler.postDelayed({
            if (!deepLinkUri.isNullOrBlank()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        setPackage(packageName)
                    }
                    context.startActivity(intent)
                } catch (_: ActivityNotFoundException) { /* ignore */ }
            }
        }, 1100L)
    }
}
