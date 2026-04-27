package com.kov.module_5.domain.model

import android.net.Uri

data class Photo(
    val uri: Uri,
    val name: String,
    val dateAdded: Long
)

