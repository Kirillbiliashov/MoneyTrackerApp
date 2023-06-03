package com.example.moneytrackerapp.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.moneytrackerapp.R

object NotificationHandler {

    private const val CHANNEL_NAME = "Save file channel"
    private const val CHANNEL_DESC =
        "Notification channel for notifying that file was successfully saved"
    private const val CHANNEL_ID = "CHANNEL_ID"

    @SuppressLint("MissingPermission")
    fun displayNotification(ctxt: Context, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = CHANNEL_DESC
            val notificationManager =
                ctxt.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(ctxt, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("File Save")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
        NotificationManagerCompat.from(ctxt).notify(1, builder.build())
    }

}