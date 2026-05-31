package com.example.nobelprizes.domain

data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val thumbnailUrl: String,
    val fullUrl: String
)

