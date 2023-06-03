package com.example.moneytrackerapp.workers

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneytrackerapp.R
import com.example.moneytrackerapp.utils.NotificationHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class SaveFileWorker(val ctxt: Context, params: WorkerParameters) : CoroutineWorker(ctxt, params) {

    private val DIR = "expenses_data"
    private val FILENAME_TEMPLATE = "expenses_%s.txt"

    override suspend fun doWork(): Result {
        val outputStr = inputData.getString(EXPENSES_KEY)
        return withContext(Dispatchers.IO) {
            return@withContext try {
                requireNotNull(outputStr)
                val uri = writeToFile(outputStr)
                val successMsg = "Successfully saved file to $uri"
                NotificationHandler.displayNotification(ctxt, successMsg)
                Result.success()
            } catch (throwable: Throwable) {
                val errorMsg = "File wasn't saved due to an error"
                NotificationHandler.displayNotification(ctxt, errorMsg)
                Result.failure()
            }
        }
    }

    private fun writeToFile(data: String): Uri {
        val outputDir = File(ctxt.filesDir, DIR)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val uniqueName = String.format(
            FILENAME_TEMPLATE,
            UUID.randomUUID().toString()
        )
        val outputFile = File(outputDir, uniqueName)
        outputFile.appendText(data)
        return Uri.fromFile(outputFile)
    }

    companion object {
        val EXPENSES_KEY = "expenses"
    }

}