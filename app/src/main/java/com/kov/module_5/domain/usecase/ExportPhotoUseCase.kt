package com.kov.module_5.domain.usecase

import android.net.Uri
import com.kov.module_5.domain.repository.PhotoRepository

class ExportPhotoUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(photoUri: Uri, fileName: String): Boolean {
        return repository.exportPhotoToGallery(photoUri, fileName)
    }
}

