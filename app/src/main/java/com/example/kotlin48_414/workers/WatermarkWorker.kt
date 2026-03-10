package com.example.kotlin48_414.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class WatermarkWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val inputFile = inputData.getString(KEY_INPUT_FILE) ?: return Result.failure()

        // Имитация добавления водяного знака с прогрессом
        for (i in 0..100 step 10) {
            if (isStopped) return Result.failure()
            setProgress(workDataOf(KEY_PROGRESS to i))
            delay(150)
        }

        val watermarkedName = "watermarked_$inputFile"
        return Result.success(workDataOf(KEY_OUTPUT_FILE to watermarkedName))
    }

    companion object {
        const val KEY_INPUT_FILE = "output_file"  // совпадает с KEY_OUTPUT_FILE из предыдущего Worker
        const val KEY_OUTPUT_FILE = "output_file"
        const val KEY_PROGRESS = "progress"
    }
}
