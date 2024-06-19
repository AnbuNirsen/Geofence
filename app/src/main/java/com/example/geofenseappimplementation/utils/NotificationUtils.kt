package com.example.geofenseappimplementation.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.geofenseappimplementation.R
import java.util.Date
import java.util.Random
import java.util.UUID
import javax.inject.Inject

class NotificationUtils @Inject constructor() {

    fun sendGeofenceNotification(
        context: Context,
        message: String,
        activityName: Class<*>
    ) {
        val notificationTitle = context.getString(R.string.geofence_notification)
        val builder = NotificationCompat.Builder(context, GEOFENCE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat.BigTextStyle().setBigContentTitle(notificationTitle)
                    .bigText(message)
            )
            .setContentIntent(getPendingIntent(context, activityName))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                context,
                GEOFENCE_NOTIFICATION_CHANNEL_ID,
                GEOFENCE_NOTIFICATION_CHANNEL,
                NotificationManager.IMPORTANCE_HIGH
            ).also {
                it.enableLights(true)
                it.enableVibration(true)
                builder.setChannelId(it.id)
            }
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(getRandomNotificationId(), builder.build())
    }

    private fun getPendingIntent(context: Context, activityName: Class<*>): PendingIntent {
        val intent = Intent(context, activityName)
        return PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        context: Context,
        channelId: String,
        name: String,
        notificationImportance: Int = NotificationManager.IMPORTANCE_HIGH
    ): NotificationChannel {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return NotificationChannel(
            channelId, name, notificationImportance
        ).also { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getRandomNotificationId(): Int {
        val time = Date().time
        val tmpStr = time.toString()
        val last4Str = tmpStr.substring(tmpStr.length - 5);
        return last4Str.toInt()
    }

    companion object {
        const val GEOFENCE_NOTIFICATION_CHANNEL_ID = "geofence_notification_channel_100"
        const val GEOFENCE_NOTIFICATION_CHANNEL = "geofence_notification_channel"
        const val NOTIFICATION_REQUEST_CODE = 101
    }

}