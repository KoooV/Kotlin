package com.example.pr1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                        //Hello(name = "Александр")
                        // Для тестирования с null раскомментируйте следующую строку:
                        Hello(name = null)
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
        Hello(name = "Иван")
    }
}

@Preview(showBackground = true)
@Composable
fun HelloPreviewWithNull() {
    Pr1Theme {
        Hello(name = null)
    }
}
