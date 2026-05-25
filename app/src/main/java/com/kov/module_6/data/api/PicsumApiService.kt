package com.kov.module_6.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApiService {
    @GET("/v2/list")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100
    ): List<PicsumDto>
}

