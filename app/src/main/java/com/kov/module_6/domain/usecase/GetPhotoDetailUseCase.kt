package com.kov.module_6.domain.usecase

import com.kov.module_6.domain.model.Photo
import com.kov.module_6.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class GetPhotoDetailUseCase(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(id: String): Flow<Photo> {
        return photoRepository.getPhotoDetail(id)
    }
}

