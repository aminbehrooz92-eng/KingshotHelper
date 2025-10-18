package com.example.kingshot_helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kingshot_helper.advanced.ActivationHelper
import com.example.kingshot_helper.advanced.DeepLinkConfig
import com.example.kingshot_helper.i18n.LocaleManager
import com.example.kingshot_helper.mode.PasswordGate
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.updateContextLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PasswordGate.init(this)
        setContent { App() }
    }

    @Composable
    fun App() {
        val perm = remember { PasswordGate.isAdvancedPermEnabled() }
        val tempActive = remember { PasswordGate.isTempActive() }
        val advancedAllowed = perm || tempActive
        var minutes by remember { mutableStateOf("1") }
        var status by remember { mutableStateOf("—") }
        var lang by remember { mutableStateOf(LocaleManager.getLanguage(this@MainActivity)) }

        MaterialTheme {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Language selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(getString(R.string.language))
                        Spacer(Modifier.width(8.dp))
                        LanguageMenu(current = lang) {
                            lang = it
                            LocaleManager.setLanguage(this@MainActivity, it)
                            recreate()
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(getString(R.string.app_title), style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(12.dp))

                    if (!advancedAllowed) {
                        Text(getString(R.string.adv_locked), color = MaterialTheme.colorScheme.error)
                    } else {
                        OutlinedTextField(
                            value = minutes,
                            onValueChange = { minutes = it },
                            label = { Text(getString(R.string.minutes_from_now)) }
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            val m = minutes.toIntOrNull() ?: 1
                            schedule(m)
                            status = getString(R.string.scheduled_in, m)
                        }) { Text(getString(R.string.schedule_attack)) }

                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            ActivationHelper.startAdvancedActivation(
                                this@MainActivity,
                                packageName = "com.run.tower.defense",
                                deepLinkUri = DeepLinkConfig.DEEP_LINK_URI
                            )
                            status = getString(R.string.triggered_activation)
                        }) { Text(getString(R.string.run_advanced_activation)) }

                        if (tempActive && !perm) {
                            Spacer(Modifier.height(8.dp))
                            Text(getString(R.string.temp_active_note), style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(getString(R.string.last_status, status))
                }
            }
        }
    }

    @Composable
    private fun LanguageMenu(current: String, onChange: (String)->Unit) {
        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { expanded = true }) {
                Text( when (current) {
                    "fa" -> "فارسی"
                    "pt" -> "Português"
                    else -> "English"
                })
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("English") }, onClick = { expanded = false; onChange("en") })
                DropdownMenuItem(text = { Text("فارسی") },   onClick = { expanded = false; onChange("fa") })
                DropdownMenuItem(text = { Text("Português") }, onClick = { expanded = false; onChange("pt") })
            }
        }
    }

    private fun schedule(min: Int) {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(this, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(this, 1003, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(min.toLong()), pi)
    }
}
