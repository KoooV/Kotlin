package com.example.nobelprizes.domain

class GetPhotosUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(): Result<List<Photo>> {
        return repository.getPhotos()
    }
}

