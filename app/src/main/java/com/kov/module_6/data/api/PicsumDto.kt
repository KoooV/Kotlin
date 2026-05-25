package com.kov.module_6.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PicsumDto(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    @SerialName("download_url")
    val downloadUrl: String
)

