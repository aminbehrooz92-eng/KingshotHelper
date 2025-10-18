package com.example.kingshot_helper

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kingshot_helper.i18n.LocaleManager
import com.example.kingshot_helper.mode.PasswordGate

class GateActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.updateContextLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PasswordGate.init(this)
        if (PasswordGate.isAdvancedPermEnabled() || PasswordGate.isTempActive()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    GateUI()
                }
            }
        }
    }

    @Composable
    private fun GateUI() {
        var pwd by remember { mutableStateOf("") }
        var status by remember { mutableStateOf(getString(R.string.gate_prompt)) }
        var lang by remember { mutableStateOf(LocaleManager.getLanguage(this@GateActivity)) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(getString(R.string.gate_title), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            // Language selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(getString(R.string.language))
                Spacer(Modifier.width(8.dp))
                LanguageMenu(
                    current = lang,
                    onChange = {
                        lang = it
                        LocaleManager.setLanguage(this@GateActivity, it)
                        recreate() // reload with new locale
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = pwd,
                onValueChange = { if (it.length <= 10) pwd = it.filter { ch -> ch.isDigit() } },
                label = { Text(getString(R.string.gate_pwd_label)) },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                val res = PasswordGate.tryUnlock(pwd)
                status = when (res) {
                    PasswordGate.UnlockResult.PERM_UNLOCKED -> getString(R.string.gate_perm_ok)
                    PasswordGate.UnlockResult.TEMP_UNLOCKED -> getString(R.string.gate_temp_ok)
                    PasswordGate.UnlockResult.INVALID -> getString(R.string.gate_invalid)
                    PasswordGate.UnlockResult.TEMP_ALREADY_USED -> getString(R.string.gate_temp_used)
                }
                if (res == PasswordGate.UnlockResult.PERM_UNLOCKED || res == PasswordGate.UnlockResult.TEMP_UNLOCKED) {
                    startActivity(Intent(this@GateActivity, MainActivity::class.java))
                    finish()
                }
            }) { Text(getString(R.string.activate)) }
            Spacer(Modifier.height(8.dp))
            Text(status)
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
}
