package com.kov.module_6.presentation.detail

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kov.module_6.di.AppModule
import com.kov.module_6.domain.model.Photo
import com.kov.module_6.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val getPhotoDetailUseCase = AppModule.getPhotoDetailUseCase

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])

    private val _uiState = MutableStateFlow<UiState<Photo>>(UiState.Loading())
    val uiState: StateFlow<UiState<Photo>> = _uiState.asStateFlow()

    private val _downloadMessage = MutableStateFlow<String?>(null)
    val downloadMessage: StateFlow<String?> = _downloadMessage.asStateFlow()

    init {
        loadPhotoDetail()
    }

    private fun loadPhotoDetail() {
        viewModelScope.launch {
            try {
                getPhotoDetailUseCase(photoId).collect { photo ->
                    _uiState.value = UiState.Success(photo)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unable to load photo details")
            }
        }
    }

    fun downloadPhoto(context: Context, photo: Photo) {
        viewModelScope.launch {
            try {
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                val request = DownloadManager.Request(Uri.parse(photo.downloadUrl))
                    .setTitle("${photo.author}'s Photo")
                    .setDescription("Downloading photo...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "photo_${photo.id}.jpg"
                    )

                downloadManager.enqueue(request)
                _downloadMessage.value = "Download started: photo_${photo.id}.jpg"
            } catch (e: Exception) {
                _downloadMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun clearDownloadMessage() {
        _downloadMessage.value = null
    }

    fun retryLoadPhotoDetail() {
        loadPhotoDetail()
    }
}

