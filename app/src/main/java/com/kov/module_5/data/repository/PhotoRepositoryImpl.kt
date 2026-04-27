package com.kov.module_5.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import com.kov.module_5.domain.model.Photo
import com.kov.module_5.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PhotoRepositoryImpl(private val context: Context) : PhotoRepository {

    override suspend fun getPhotos(): List<Photo> = withContext(Dispatchers.IO) {
        val photosDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (photosDir != null && photosDir.exists()) {
            val files = photosDir.listFiles()?.filter { it.extension == "jpg" || it.extension == "jpeg" || it.extension == "png" }
            files?.map { file ->
                Photo(
                    uri = file.toUri(),
                    name = file.name,
                    dateAdded = file.lastModified()
                )
            }?.sortedByDescending { it.dateAdded } ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun exportPhotoToGallery(photoUri: Uri, fileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Module5App")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    resolver.openInputStream(photoUri)?.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

