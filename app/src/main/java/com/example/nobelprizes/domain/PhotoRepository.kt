package com.example.nobelprizes.domain

interface PhotoRepository {
    suspend fun getPhotos(): Result<List<Photo>>
}

