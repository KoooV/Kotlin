package com.example.kotlin48_414.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class CompressPhotoWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val photoName = inputData.getString(KEY_PHOTO_NAME) ?: return Result.failure()

        // Имитация сжатия фото с прогрессом
        for (i in 0..100 step 10) {
            if (isStopped) return Result.failure()
            setProgress(workDataOf(KEY_PROGRESS to i))
            delay(150)
        }

        val compressedName = "compressed_$photoName"
        return Result.success(workDataOf(KEY_OUTPUT_FILE to compressedName))
    }

    companion object {
        const val KEY_PHOTO_NAME = "photo_name"
        const val KEY_OUTPUT_FILE = "output_file"
        const val KEY_PROGRESS = "progress"
    }
}

