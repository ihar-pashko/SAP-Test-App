package com.sap.codelab.presentation.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sap.codelab.R
import com.sap.codelab.domain.model.Memo

class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMemoNotification(memo: Memo) {
        val notificationId = memo.id.toInt()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(memo.title)
            .setContentText(memo.description.take(140))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            notificationManager.notify(notificationId, builder.build())
            Log.i("NotificationHelper", "Notification shown for memo ID: ${memo.id}")
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Missing POST_NOTIFICATIONS permission?", e)
        }
    }

    companion object {
        private const val CHANNEL_ID = "memo_location_channel"
        private const val CHANNEL_NAME = "Location Reminders"
        private const val CHANNEL_DESCRIPTION = "Notifications for location-based memos"
    }
}
