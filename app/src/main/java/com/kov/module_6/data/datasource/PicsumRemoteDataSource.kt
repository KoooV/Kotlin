package com.kov.module_6.data.datasource

import com.kov.module_6.data.api.PicsumApiService
import com.kov.module_6.data.api.PicsumDto

class PicsumRemoteDataSource(
    private val apiService: PicsumApiService
) {
    suspend fun getPhotos(page: Int = 1): List<PicsumDto> {
        return apiService.getPhotos(page)
    }
}

