package com.example.nobelprizes

import android.app.Application
import com.example.nobelprizes.data.PhotoApi
import com.example.nobelprizes.data.PhotoRepositoryImpl
import com.example.nobelprizes.domain.GetPhotosUseCase
import com.example.nobelprizes.domain.PhotoRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : Application() {

    // Простая ручная реализация внедрения зависимостей (DI)
    lateinit var getPhotosUseCase: GetPhotosUseCase
        private set

    override fun onCreate() {
        super.onCreate()

        // Создаем Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем API
        val api = retrofit.create(PhotoApi::class.java)

        // Инициализируем репозиторий и UseCase
        val repository: PhotoRepository = PhotoRepositoryImpl(api)
        getPhotosUseCase = GetPhotosUseCase(repository)
    }
}

