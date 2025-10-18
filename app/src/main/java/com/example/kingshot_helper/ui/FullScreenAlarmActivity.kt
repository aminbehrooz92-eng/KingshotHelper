package com.example.kingshot_helper.ui

import androidx.compose.ui.res.stringResource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kingshot_helper.advanced.ActivationHelper
import com.example.kingshot_helper.advanced.DeepLinkConfig
import com.example.kingshot_helper.R

class FullScreenAlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    AlarmUI(
                        onActivate = {
                            ActivationHelper.startAdvancedActivation(
                                this@FullScreenAlarmActivity,
                                packageName = "com.run.tower.defense",
                                deepLinkUri = DeepLinkConfig.DEEP_LINK_URI
                            )
                            finish()
                        },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun AlarmUI(onActivate: ()->Unit, onCancel: ()->Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.attack_detected), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Text(stringResource(id = R.string.activate_shield_q))
        Spacer(Modifier.height(24.dp))
        Row {
            Button(onClick = onActivate) { Text(stringResource(id = R.string.activate_shield_2h)) }
            Spacer(Modifier.width(12.dp))
            OutlinedButton(onClick = onCancel) { Text(stringResource(id = R.string.stop_alarm)) }
        }
    }
}
