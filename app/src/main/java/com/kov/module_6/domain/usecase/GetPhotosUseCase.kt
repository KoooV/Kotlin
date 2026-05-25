package com.kov.module_6.domain.usecase

import com.kov.module_6.domain.model.Photo
import com.kov.module_6.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class GetPhotosUseCase(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(page: Int = 1): Flow<List<Photo>> {
        return photoRepository.getPhotos(page)
    }
}

