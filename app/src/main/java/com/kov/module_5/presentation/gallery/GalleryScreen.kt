package com.kov.module_5.presentation.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel) {
    val context = LocalContext.current
    val photos by viewModel.photos.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPhotos()
        viewModel.events.collect { event ->
            when (event) {
                is GalleryEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.loadPhotos()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val storageGranted = true

        if (cameraGranted && storageGranted) {
            val photoFile = createPhotoFile(context)
            currentPhotoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(currentPhotoUri!!)
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Permissions required")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Личная галерея") })
        },
        floatingActionButton = {
            if (photos.isNotEmpty()) {
                FloatingActionButton(onClick = {
                    val permissions = arrayOf(Manifest.permission.CAMERA)
                    permissionLauncher.launch(permissions)
                }) {
                    Text("Сделать фото", modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    ) { padding ->
        if (photos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("У вас пока нет фото", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val permissions = arrayOf(Manifest.permission.CAMERA)
                    permissionLauncher.launch(permissions)
                }) {
                    Text("Сделать первое фото")
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(photos) { photo ->
                    var showDialog by remember { mutableStateOf(false) }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Экспорт фото") },
                            text = { Text("Экспортировать фото в общую галерею?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.exportPhoto(photo.uri, photo.name)
                                    showDialog = false
                                }) {
                                    Text("Да")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Отмена")
                                }
                            }
                        )
                    }

                    AsyncImage(
                        model = photo.uri,
                        contentDescription = photo.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { showDialog = true }
                    )
                }
            }
        }
    }
}

private fun createPhotoFile(context: android.content.Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File(storageDir, "IMG_${timeStamp}.jpg")
}
