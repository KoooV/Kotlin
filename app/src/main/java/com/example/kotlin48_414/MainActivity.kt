package com.example.kotlin48_414

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin48_414.ui.theme.Kotlin48414Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlin48414Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherForecastScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WeatherForecastScreen(
    modifier: Modifier = Modifier,
    viewModel: PhotoProcessingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Запрос разрешения на уведомления (Android 13+)
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasNotificationPermission = granted }

    val isWorking = state.step == WeatherStep.LOADING || state.step == WeatherStep.REPORT

    val statusText = when (state.step) {
        WeatherStep.IDLE -> "Нажмите кнопку, чтобы собрать прогноз погоды"
        WeatherStep.LOADING -> {
            val done = state.cities.count { it.done }
            val total = state.cities.size
            if (done == 0) "Загружаем погоду для $total городов…"
            else {
                val doneCities = state.cities.filter { it.done }.joinToString(", ") { it.city }
                val pending = state.cities.filter { !it.done }.joinToString(", ") { it.city }
                if (pending.isEmpty()) "Все данные получены, формируем отчёт…"
                else "Готово: $doneCities\n$pending в процессе…"
            }
        }
        WeatherStep.REPORT -> "Все данные получены, формируем отчёт…"
        WeatherStep.DONE -> "Отчёт готов! ✅"
        WeatherStep.ERROR -> "❌ Ошибка"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "☁ Прогноз погоды",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Статус
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (state.step) {
                    WeatherStep.ERROR -> MaterialTheme.colorScheme.errorContainer
                    WeatherStep.DONE -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Text(
                text = statusText,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }

        // Прогресс
        if (isWorking) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }

        // Список городов
        if (state.cities.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Города:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    state.cities.forEach { cityState ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = cityState.city, fontSize = 15.sp)
                            if (cityState.done && cityState.temperature != null) {
                                val sign = if (cityState.temperature >= 0) "+" else ""
                                Text(
                                    text = "$sign${cityState.temperature}°C ✅",
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            } else if (isWorking || state.step == WeatherStep.REPORT) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Итоговый отчёт
        if (state.step == WeatherStep.DONE && state.report != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Итоговый отчёт",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.report!!,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Ошибка
        if (state.step == WeatherStep.ERROR && state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка
        Button(
            onClick = {
                if (state.step == WeatherStep.DONE || state.step == WeatherStep.ERROR) {
                    viewModel.reset()
                } else {
                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.startForecast()
                    }
                }
            },
            enabled = !isWorking,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = when (state.step) {
                    WeatherStep.DONE, WeatherStep.ERROR -> "Начать заново"
                    else -> "☁ Собрать прогноз"
                },
                fontSize = 16.sp
            )
        }
    }
}