package com.example.nobelprizes.data

import retrofit2.http.GET

interface PhotoApi {
    @GET("v2/list")
    suspend fun getPhotos(): List<PhotoDto>
}

