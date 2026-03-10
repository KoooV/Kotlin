package com.example.kotlin48_414

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import androidx.work.WorkManager
import com.example.kotlin48_414.workers.CompressPhotoWorker
import com.example.kotlin48_414.workers.UploadWorker
import com.example.kotlin48_414.workers.WatermarkWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class ProcessingStep { IDLE, COMPRESSING, WATERMARKING, UPLOADING, DONE, ERROR }

data class PhotoProcessingState(
    val step: ProcessingStep = ProcessingStep.IDLE,
    val progress: Float = 0f,
    val resultFileName: String? = null,
    val errorMessage: String? = null
)

class PhotoProcessingViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    private val _state = MutableStateFlow(PhotoProcessingState())
    val state: StateFlow<PhotoProcessingState> = _state

    fun startProcessing(photoName: String = "photo.jpg") {
        _state.value = PhotoProcessingState(step = ProcessingStep.COMPRESSING, progress = 0f)

        // Worker 1: Сжатие
        val compressRequest = OneTimeWorkRequestBuilder<CompressPhotoWorker>()
            .setInputData(workDataOf(CompressPhotoWorker.KEY_PHOTO_NAME to photoName))
            .build()

        // Worker 2: Водяной знак (получает output_file из Worker 1)
        val watermarkRequest = OneTimeWorkRequestBuilder<WatermarkWorker>()
            .setInputMerger(OverwritingInputMerger::class)
            .build()

        // Worker 3: Загрузка (получает output_file из Worker 2)
        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputMerger(OverwritingInputMerger::class)
            .build()

        // Последовательная цепочка
        workManager.beginWith(compressRequest)
            .then(watermarkRequest)
            .then(uploadRequest)
            .enqueue()

        // Наблюдаем за Worker 1
        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(compressRequest.id).collect { info ->
                if (info == null) return@collect
                when (info.state) {
                    WorkInfo.State.RUNNING -> {
                        val progress = info.progress.getInt(CompressPhotoWorker.KEY_PROGRESS, 0)
                        _state.value = _state.value.copy(
                            step = ProcessingStep.COMPRESSING,
                            progress = progress / 100f
                        )
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                        _state.value = _state.value.copy(
                            step = ProcessingStep.ERROR,
                            errorMessage = "Ошибка при сжатии фото"
                        )
                    }
                    else -> {}
                }
            }
        }

        // Наблюдаем за Worker 2
        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(watermarkRequest.id).collect { info ->
                if (info == null) return@collect
                when (info.state) {
                    WorkInfo.State.RUNNING -> {
                        val progress = info.progress.getInt(WatermarkWorker.KEY_PROGRESS, 0)
                        _state.value = _state.value.copy(
                            step = ProcessingStep.WATERMARKING,
                            progress = progress / 100f
                        )
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                        _state.value = _state.value.copy(
                            step = ProcessingStep.ERROR,
                            errorMessage = "Ошибка при добавлении водяного знака"
                        )
                    }
                    else -> {}
                }
            }
        }

        // Наблюдаем за Worker 3
        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(uploadRequest.id).collect { info ->
                if (info == null) return@collect
                when (info.state) {
                    WorkInfo.State.RUNNING -> {
                        val progress = info.progress.getInt(UploadWorker.KEY_PROGRESS, 0)
                        _state.value = _state.value.copy(
                            step = ProcessingStep.UPLOADING,
                            progress = progress / 100f
                        )
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        val outputFile = info.outputData.getString(UploadWorker.KEY_OUTPUT_FILE)
                        _state.value = _state.value.copy(
                            step = ProcessingStep.DONE,
                            progress = 1f,
                            resultFileName = outputFile
                        )
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                        _state.value = _state.value.copy(
                            step = ProcessingStep.ERROR,
                            errorMessage = "Ошибка при загрузке фото"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    fun reset() {
        _state.value = PhotoProcessingState()
    }
}

