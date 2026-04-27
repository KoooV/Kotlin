package com.kov.module_5.domain.usecase

import com.kov.module_5.domain.model.Photo
import com.kov.module_5.domain.repository.PhotoRepository

class GetPhotosUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(): List<Photo> {
        return repository.getPhotos()
    }
}

