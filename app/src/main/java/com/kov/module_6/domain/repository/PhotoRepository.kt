package com.kov.module_6.domain.repository

import com.kov.module_6.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotos(page: Int = 1): Flow<List<Photo>>
    fun getPhotoDetail(id: String): Flow<Photo>
}

