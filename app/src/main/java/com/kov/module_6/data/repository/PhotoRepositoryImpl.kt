package com.kov.module_6.data.repository

import com.kov.module_6.data.api.PicsumDto
import com.kov.module_6.data.datasource.PicsumRemoteDataSource
import com.kov.module_6.domain.model.Photo
import com.kov.module_6.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PhotoRepositoryImpl(
    private val remoteDataSource: PicsumRemoteDataSource
) : PhotoRepository {

    override fun getPhotos(page: Int): Flow<List<Photo>> = flow {
        try {
            val photos = remoteDataSource.getPhotos(page)
            emit(photos.map { it.toPhoto() })
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getPhotoDetail(id: String): Flow<Photo> = flow {
        try {
            // For this API, we get the detail from the list and filter
            val photos = remoteDataSource.getPhotos()
            val photo = photos.find { it.id == id }
            if (photo != null) {
                emit(photo.toPhoto())
            } else {
                throw Exception("Photo not found")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun PicsumDto.toPhoto(): Photo {
        return Photo(
            id = id,
            author = author,
            width = width,
            height = height,
            url = url,
            downloadUrl = downloadUrl
        )
    }
}

