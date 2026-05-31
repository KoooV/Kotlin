package com.example.nobelprizes.data

import com.example.nobelprizes.domain.Photo
import com.example.nobelprizes.domain.PhotoRepository

class PhotoRepositoryImpl(
    private val api: PhotoApi
) : PhotoRepository {
    override suspend fun getPhotos(): Result<List<Photo>> {
        return try {
            val dtoList = api.getPhotos()
            val photos = dtoList.map { it.toDomain() }
            Result.success(photos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

