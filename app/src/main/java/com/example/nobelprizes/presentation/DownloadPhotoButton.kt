package com.example.nobelprizes.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun DownloadPhotoButton(
    fullUrl: String,
    photoId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Инициализация лаунчера SAF для создания документа
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri: Uri? ->
        // Если пользователь выбрал место и система вернула Uri
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    // Открываем поток для скачивания (можно заменить на OkHttpClient)
                    URL(fullUrl).openStream().use { inputStream ->
                        // Открываем поток для записи по полученному от SAF Uri
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            inputStream.copyTo(outputStream)
                        } ?: throw IllegalStateException("Не удалось открыть OutputStream")
                    }

                    // Переключаемся на главный поток для показа Toast
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Фото успешно сохранено", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ошибка при сохранении: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            // Пользователь отменил выбор директории
            Toast.makeText(context, "Сохранение отменено", Toast.LENGTH_SHORT).show()
        }
    }

    Button(
        onClick = {
            // Запускаем системное окно с предложением имени файла по умолчанию
            launcher.launch("photo_$photoId.jpg")
        },
        modifier = modifier.padding(16.dp)
    ) {
        Text("Скачать фото")
    }
}