package com.kov.module_6.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kov.module_6.di.AppModule
import com.kov.module_6.domain.model.Photo
import com.kov.module_6.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PhotoListViewModel : ViewModel() {

    private val getPhotosUseCase = AppModule.getPhotosUseCase

    private val _uiState = MutableStateFlow<UiState<List<Photo>>>(UiState.Loading())
    val uiState: StateFlow<UiState<List<Photo>>> = _uiState.asStateFlow()

    private val _allPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val allPhotos: StateFlow<List<Photo>> = _allPhotos.asStateFlow()

    private var currentPage = 1

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        currentPage = 1
        _uiState.value = UiState.Loading()

        viewModelScope.launch {
            try {
                getPhotosUseCase(currentPage).collect { photos ->
                    _allPhotos.value = photos
                    _uiState.value = UiState.Success(photos)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadMorePhotos() {
        viewModelScope.launch {
            try {
                currentPage++
                getPhotosUseCase(currentPage).collect { photos ->
                    _allPhotos.value = (_allPhotos.value + photos).distinctBy { it.id }
                    _uiState.value = UiState.Success(_allPhotos.value)
                }
            } catch (e: Exception) {
                currentPage--
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retryLoadPhotos() {
        loadPhotos()
    }
}

