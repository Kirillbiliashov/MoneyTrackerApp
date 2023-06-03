package com.example.moneytrackerapp.workers

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class SaveFileWorker(val ctxt: Context, params: WorkerParameters) : CoroutineWorker(ctxt, params) {
    override suspend fun doWork(): Result {
        val outputStr = inputData.getString("expenses")
        return withContext(Dispatchers.IO) {
            return@withContext try {
                requireNotNull(outputStr)
                val uri = writeToFile(outputStr)
                println("uri: $uri")
                Result.success()
            } catch (throwable: Throwable) {
                Result.failure()
            }
        }
    }

    private fun writeToFile(data: String): Uri {
        val dir = "expenses_data"
        val outputDir = File(ctxt.filesDir, dir)
        if (!outputDir.exists()) {
            outputDir.mkdirs() // should succeed
        }
        val uniqueName = String.format("expenses_%s.txt", UUID.randomUUID().toString())
        val outputFile = File(outputDir, uniqueName)
        outputFile.appendText(data)
        return Uri.fromFile(outputFile)
    }

}