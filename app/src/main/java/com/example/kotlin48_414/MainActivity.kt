package com.example.kotlin48_414

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin48_414.ui.theme.Kotlin48414Theme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlin48414Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CurrencyScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrencyViewModel = viewModel()
) {
    val rate by viewModel.rate.collectAsStateWithLifecycle()
    val previousRate by viewModel.previousRate.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val diff = rate - previousRate
    val direction = when {
        diff > 0.0 -> Direction.UP
        diff < 0.0 -> Direction.DOWN
        else -> Direction.SAME
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок
        Text(
            text = "💱",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Курс USD / RUB",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Обновляется каждые 5 секунд",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(36.dp))

        // Карточка с курсом
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "1 USD =",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Курс + стрелка
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(
                        targetState = rate,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(300)) +
                                    slideInVertically(animationSpec = tween(300)) {
                                        if (diff >= 0) -it else it
                                    })
                                .togetherWith(
                                    fadeOut(animationSpec = tween(150)) +
                                            slideOutVertically(animationSpec = tween(150)) {
                                                if (diff >= 0) it else -it
                                            }
                                )
                        },
                        label = "rate_animation"
                    ) { targetRate ->
                        Text(
                            text = String.format(Locale.US, "%.2f ₽", targetRate),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Стрелка вверх/вниз
                    AnimatedContent(
                        targetState = direction,
                        transitionSpec = {
                            fadeIn(tween(300)).togetherWith(fadeOut(tween(150)))
                        },
                        label = "arrow_animation"
                    ) { dir ->
                        when (dir) {
                            Direction.UP -> Text(
                                text = "▲",
                                fontSize = 32.sp,
                                color = Color(0xFF4CAF50) // зелёная
                            )
                            Direction.DOWN -> Text(
                                text = "▼",
                                fontSize = 32.sp,
                                color = Color(0xFFF44336) // красная
                            )
                            Direction.SAME -> Text(
                                text = "●",
                                fontSize = 24.sp,
                                color = Color(0xFF9E9E9E) // серая
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Изменение
                val changeText = when {
                    diff > 0 -> String.format(Locale.US, "+%.2f", diff)
                    diff < 0 -> String.format(Locale.US, "%.2f", diff)
                    else -> "0.00"
                }
                val changeColor = when (direction) {
                    Direction.UP -> Color(0xFF4CAF50)
                    Direction.DOWN -> Color(0xFFF44336)
                    Direction.SAME -> Color(0xFF9E9E9E)
                }
                Text(
                    text = changeText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = changeColor
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Индикатор автообновления
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Кнопка
        Button(
            onClick = { viewModel.refreshNow() },
            enabled = !isRefreshing,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isRefreshing) "Обновление…" else "🔄 Обновить сейчас",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private enum class Direction { UP, DOWN, SAME }

