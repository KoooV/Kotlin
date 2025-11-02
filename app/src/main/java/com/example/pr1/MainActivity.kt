package com.example.pr1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pr1.R
import com.example.pr1.ui.theme.Pr1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pr1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Вызов функции с конкретным именем
                        Hello(name = "Александр")
                        // Для тестирования с null раскомментируйте следующую строку:
                        // Hello(name = null)
                    }
                }
            }
        }
    }
}

@Composable
fun Hello(name: String?) {
    val greetingText = if (name == null) {
        "Имя не задано"
    } else {
        "Привет, $name!"
    }
    
    Text(
        text = greetingText,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Preview(showBackground = true)
@Composable
fun HelloPreviewWithName() {
    Pr1Theme {
        Hello(name = "Александр")
    }
}

@Preview(showBackground = true)
@Composable
fun HelloPreviewWithNull() {
    Pr1Theme {
        Hello(name = null)
    }
}

@Preview(
    name = "Portrait",
    showBackground = true,
    showSystemUi = true,
    device = "spec:shape=Normal,width=411,height=891,unit=dp,dpi=420"
)
@Composable
fun HelloPreviewPortrait() {
    Pr1Theme {
        Hello(name = "Александр")
    }
}

@Preview(
    name = "Landscape",
    showBackground = true,
    showSystemUi = true,
    device = "spec:shape=Normal,width=891,height=411,unit=dp,dpi=420"
)
@Composable
fun HelloPreviewLandscape() {
    Pr1Theme {
        Hello(name = "Александр")
    }
}

@Preview(
    name = "Round",
    showBackground = true,
    showSystemUi = true,
    device = "spec:shape=Round,width=200,height=200,unit=dp,dpi=420",
    backgroundColor = 0xFFFFFF00
)
@Composable
fun HelloPreviewRound() {
    Pr1Theme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Yellow),
            contentAlignment = Alignment.Center
        ) {
            Hello(name = "Александр")
        }
    }
}

// Вариант 1: Зелёный цвет, 16.sp, курсив, выравнивание по центру
@Preview(showBackground = true)
@Composable
fun ComposeDescriptionPreview1() {
    Pr1Theme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.compose_description),
                color = Color.Green,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Вариант 2: Одна строка с многоточием
@Preview(showBackground = true)
@Composable
fun ComposeDescriptionPreview2() {
    Pr1Theme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.compose_description),
                maxLines = 1
            )
        }
    }
}

// Вариант 3: Отступ 48.sp перед первой строкой, 24.sp размер, чёрный цвет, зелёный фон, подчёркнутый
@Preview(showBackground = true)
@Composable
fun ComposeDescriptionPreview3() {
    Pr1Theme {
        val density = LocalDensity.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(with(density) { 48.sp.toDp() }))
                Text(
                    text = stringResource(R.string.compose_description),
                    color = Color.Black,
                    fontSize = 24.sp,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}
