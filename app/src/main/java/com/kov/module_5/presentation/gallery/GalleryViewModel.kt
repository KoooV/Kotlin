package com.kov.module_5.presentation.gallery

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kov.module_5.domain.model.Photo
import com.kov.module_5.domain.usecase.ExportPhotoUseCase
import com.kov.module_5.domain.usecase.GetPhotosUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val exportPhotoUseCase: ExportPhotoUseCase
) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _events = MutableSharedFlow<GalleryEvent>()
    val events: SharedFlow<GalleryEvent> = _events.asSharedFlow()

    fun loadPhotos() {
        viewModelScope.launch {
            _photos.value = getPhotosUseCase()
        }
    }

    fun exportPhoto(uri: Uri, name: String) {
        viewModelScope.launch {
            val success = exportPhotoUseCase(uri, name)
            if (success) {
                _events.emit(GalleryEvent.ShowSnackbar("Фото добавлено в галерею"))
            } else {
                _events.emit(GalleryEvent.ShowSnackbar("Ошибка экспорта"))
            }
        }
    }
}

sealed class GalleryEvent {
    data class ShowSnackbar(val message: String) : GalleryEvent()
}

class GalleryViewModelFactory(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val exportPhotoUseCase: ExportPhotoUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalleryViewModel(getPhotosUseCase, exportPhotoUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

