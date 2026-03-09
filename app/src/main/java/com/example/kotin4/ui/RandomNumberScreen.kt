package com.example.kotin4.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotin4.service.RandomNumberService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomNumberScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    // Ссылка на сервис (null — пока не подключены)
    var service by remember { mutableStateOf<RandomNumberService?>(null) }
    var bound by remember { mutableStateOf(false) }

    // ServiceConnection отслеживает подключение / отключение
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val b = binder as? RandomNumberService.RandomBinder
                service = b?.getService()
                bound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
                bound = false
            }
        }
    }

    // Отключаемся при уходе с экрана, если забыли нажать «Отключиться»
    DisposableEffect(Unit) {
        onDispose {
            if (bound) {
                context.unbindService(connection)
                bound = false
                service = null
            }
        }
    }

    // Подписываемся на StateFlow сервиса, если подключены
    val numberFlow: StateFlow<Int?>? = service?.number
    val number by (numberFlow ?: MutableStateFlow<Int?>(null)).collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Случайное число") })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Статус подключения
            Text(
                text = if (bound) "Подключено" else "Отключено",
                style = MaterialTheme.typography.titleMedium,
                color = if (bound)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(48.dp))

            // Крупное число с анимацией смены
            AnimatedContent(
                targetState = number,
                transitionSpec = {
                    (slideInVertically { it } + fadeIn()) togetherWith
                            (slideOutVertically { -it } + fadeOut())
                },
                label = "number_anim"
            ) { n ->
                Text(
                    text = n?.toString() ?: "—",
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "последнее число (0–100)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(64.dp))

            // Кнопки Подключиться / Отключиться
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { bindToService(context, connection) },
                    enabled = !bound,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Подключиться")
                }

                OutlinedButton(
                    onClick = {
                        context.unbindService(connection)
                        bound = false
                        service = null
                    },
                    enabled = bound,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отключиться")
                }
            }
        }
    }
}

// Запускаем bindService
private fun bindToService(context: Context, connection: ServiceConnection) {
    val intent = Intent(context, RandomNumberService::class.java)
    context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
}

