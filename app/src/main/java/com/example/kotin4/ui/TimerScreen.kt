package com.example.kotin4.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotin4.service.TimerService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val seconds by TimerService.seconds.collectAsState()
    val running by TimerService.running.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Счётчик времени") })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ─── Крупный счётчик ────────────────────────
            Text(
                text = formatTime(seconds),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$seconds сек.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(48.dp))

            // ─── Кнопки Старт / Стоп ───────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Button(
                    onClick = { startTimer(context) },
                    enabled = !running,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("▶  Старт", fontSize = 18.sp)
                }

                Button(
                    onClick = { stopTimer(context) },
                    enabled = running,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("■  Стоп", fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ─── Статус ─────────────────────────────────
            Text(
                text = if (running) "Таймер запущен" else "Таймер остановлен",
                style = MaterialTheme.typography.bodyLarge,
                color = if (running)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Helpers ────────────────────────────────────────────

private fun formatTime(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) {
        "%d:%02d:%02d".format(h, m, s)
    } else {
        "%02d:%02d".format(m, s)
    }
}

private fun startTimer(context: Context) {
    val intent = Intent(context, TimerService::class.java)
    context.startForegroundService(intent)
}

private fun stopTimer(context: Context) {
    val intent = Intent(context, TimerService::class.java)
    context.stopService(intent)
}

