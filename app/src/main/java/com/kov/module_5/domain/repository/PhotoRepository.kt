package com.kov.module_5.domain.repository

import android.net.Uri
import com.kov.module_5.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): List<Photo>
    suspend fun exportPhotoToGallery(photoUri: Uri, fileName: String): Boolean
}

