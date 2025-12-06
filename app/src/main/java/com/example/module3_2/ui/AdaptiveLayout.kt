package com.example.module3_2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Card
import androidx.compose.ui.unit.dp

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun AdaptiveLayout(
    windowWidthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    when (windowWidthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            // Одна колонка: список над деталями
            Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Top) {
                Text(text = "Compact layout (single column)", style = MaterialTheme.typography.titleMedium)
                // Пример: список
                SampleList()
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                // Пример: детали
                SampleDetails()
            }
        }
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
            // Две колонки: список слева, детали справа
            Row(modifier = modifier.fillMaxSize().padding(16.dp)) {
                Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                    Text(text = "Two-column layout", style = MaterialTheme.typography.titleMedium)
                    SampleList()
                }
                Spacer(modifier = Modifier.size(16.dp))
                Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                    SampleDetails()
                }
            }
        }
        else -> {
            // По умолчанию комбинированная одна колонка
            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
                SampleList()
                SampleDetails()
            }
        }
    }
}

@Composable
private fun SampleList() {
    Column {
        for (i in 1..5) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Item #$i")
                }
            }
        }
    }
}

@Composable
private fun SampleDetails() {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Box(modifier = Modifier.padding(12.dp)) {
            Text(text = "Details content goes here.\nThis is sample detail text.")
        }
    }
}
