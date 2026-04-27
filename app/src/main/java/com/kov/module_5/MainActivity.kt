package com.kov.module_5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kov.module_5.data.repository.PhotoRepositoryImpl
import com.kov.module_5.domain.usecase.ExportPhotoUseCase
import com.kov.module_5.domain.usecase.GetPhotosUseCase
import com.kov.module_5.presentation.gallery.GalleryScreen
import com.kov.module_5.presentation.gallery.GalleryViewModel
import com.kov.module_5.presentation.gallery.GalleryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = PhotoRepositoryImpl(applicationContext)
            val getPhotosUseCase = GetPhotosUseCase(repository)
            val exportPhotoUseCase = ExportPhotoUseCase(repository)
            val factory = GalleryViewModelFactory(getPhotosUseCase, exportPhotoUseCase)
            val viewModel: GalleryViewModel = viewModel(factory = factory)

            GalleryScreen(viewModel = viewModel)
        }
    }
}
