package com.kov.module_6.di

import com.kov.module_6.data.api.PicsumApiService
import com.kov.module_6.data.datasource.PicsumRemoteDataSource
import com.kov.module_6.data.repository.PhotoRepositoryImpl
import com.kov.module_6.domain.repository.PhotoRepository
import com.kov.module_6.domain.usecase.GetPhotoDetailUseCase
import com.kov.module_6.domain.usecase.GetPhotosUseCase
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object AppModule {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val jsonSerializer: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl("https://picsum.photos")
            .client(okHttpClient)
            .addConverterFactory(jsonSerializer.asConverterFactory(contentType))
            .build()
    }

    val picsumApiService: PicsumApiService by lazy {
        retrofit.create(PicsumApiService::class.java)
    }

    val remoteDataSource: PicsumRemoteDataSource by lazy {
        PicsumRemoteDataSource(picsumApiService)
    }

    val photoRepository: PhotoRepository by lazy {
        PhotoRepositoryImpl(remoteDataSource)
    }

    val getPhotosUseCase: GetPhotosUseCase by lazy {
        GetPhotosUseCase(photoRepository)
    }

    val getPhotoDetailUseCase: GetPhotoDetailUseCase by lazy {
        GetPhotoDetailUseCase(photoRepository)
    }
}


