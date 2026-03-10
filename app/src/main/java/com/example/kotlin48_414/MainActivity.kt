package com.example.kotlin48_414

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    PhotoProcessingScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PhotoProcessingScreen(
    modifier: Modifier = Modifier,
    viewModel: PhotoProcessingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val isWorking = state.step == ProcessingStep.COMPRESSING ||
            state.step == ProcessingStep.WATERMARKING ||
            state.step == ProcessingStep.UPLOADING

    val statusText = when (state.step) {
        ProcessingStep.IDLE -> "Нажмите кнопку, чтобы начать"
        ProcessingStep.COMPRESSING -> "Сжимаем фото…"
        ProcessingStep.WATERMARKING -> "Добавляем водяной знак…"
        ProcessingStep.UPLOADING -> "Загружаем в облако…"
        ProcessingStep.DONE -> "Готово! Фото загружено ✅"
        ProcessingStep.ERROR -> "❌ Ошибка обработки"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Обработка фото",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Текущий статус
        Text(
            text = statusText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = when (state.step) {
                ProcessingStep.ERROR -> MaterialTheme.colorScheme.error
                ProcessingStep.DONE -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onBackground
            }
        )

        // Прогресс-бар — только во время работы
        if (isWorking) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
                Text(
                    text = "${(state.progress * 100).toInt()}%",
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 14.sp
                )
            }
        }

        // Результат
        if (state.step == ProcessingStep.DONE && state.resultFileName != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Файл успешно загружен:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.resultFileName!!,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Сообщение об ошибке
        if (state.step == ProcessingStep.ERROR && state.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = state.errorMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка
        Button(
            onClick = {
                if (state.step == ProcessingStep.DONE || state.step == ProcessingStep.ERROR) {
                    viewModel.reset()
                } else {
                    viewModel.startProcessing("my_photo.jpg")
                }
            },
            enabled = !isWorking,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = when (state.step) {
                    ProcessingStep.DONE, ProcessingStep.ERROR -> "Начать заново"
                    else -> "Начать обработку и загрузку"
                },
                fontSize = 16.sp
            )
        }
    }
}