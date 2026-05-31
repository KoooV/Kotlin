package com.example.nobelprizes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nobelprizes.domain.Photo
import com.example.nobelprizes.presentation.PhotoDetailScreen
import com.example.nobelprizes.presentation.PhotoListScreen
import com.example.nobelprizes.presentation.PhotoListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем экземпляр нашего Application-класса с зависимостями
        val app = application as MyApplication

        // Создаем фабрику для прокидывания GetPhotosUseCase во ViewModel
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PhotoListViewModel(app.getPhotosUseCase) as T
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen(factory)
                }
            }
        }
    }
}

@Composable
fun AppScreen(factory: ViewModelProvider.Factory) {
    // Получаем ViewModel с помощью нашей фабрики
    val viewModel: PhotoListViewModel = viewModel(factory = factory)

    // Подписываемся на StateFlow
    val state by viewModel.state.collectAsState()

    // Состояние для навигации: если photo == null, мы на экране списка, иначе - на экране деталей
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }
    val context = LocalContext.current

    if (selectedPhoto == null) {
        PhotoListScreen(
            state = state,
            onPhotoClick = { selectedPhoto = it },
            onRetryClick = { viewModel.loadPhotos() }
        )
    } else {
        // Обработка системной кнопки "Назад" (на Android аппаратах или жестом свайпа)
        BackHandler {
            selectedPhoto = null
        }

        PhotoDetailScreen(
            photo = selectedPhoto!!,
            onDownloadClick = {
                // Заглушка, здесь можно реализовать реальное скачивание файла
                Toast.makeText(context, "Начато скачивание...", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

