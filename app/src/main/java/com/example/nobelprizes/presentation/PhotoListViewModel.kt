package com.example.nobelprizes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nobelprizes.domain.GetPhotosUseCase
import com.example.nobelprizes.domain.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PhotoListState {
    data object Loading : PhotoListState
    data class Success(val data: List<Photo>) : PhotoListState
    data class Error(val message: String) : PhotoListState
}

class PhotoListViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PhotoListState>(PhotoListState.Loading)
    val state: StateFlow<PhotoListState> = _state.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _state.value = PhotoListState.Loading

            getPhotosUseCase().fold(
                onSuccess = { photos ->
                    _state.value = PhotoListState.Success(photos)
                },
                onFailure = { error ->
                    _state.value = PhotoListState.Error(error.localizedMessage ?: "An unexpected error occurred")
                }
            )
        }
    }
}

