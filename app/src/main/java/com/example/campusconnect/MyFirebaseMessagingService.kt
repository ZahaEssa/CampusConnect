package com.example.campusconnect

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val channelId = "NotificationChannel"
    private val channelName = "CyberSecurityClub"

    // Create the notification channel for Android O and higher
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for app notifications"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            // Log confirmation that the channel was created
            Log.d("FCM", "Notification channel created with ID: $channelId")
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if there is a notification payload
        if (remoteMessage.notification != null) {
            // Log notification content
            Log.d("FCM Notification", "Title: ${remoteMessage.notification?.title}")
            Log.d("FCM Notification", "Body: ${remoteMessage.notification?.body}")

            // Generate the notification
            generateNotification(
                remoteMessage.notification?.title ?: "Default Title",
                remoteMessage.notification?.body ?: "Default Message"
            )
        } else if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Default Title"
            val message = remoteMessage.data["message"] ?: "Default Message"
            generateNotification(title, message)
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews(packageName, R.layout.notification)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.drakebird)  // Ensure correct image resource
        return remoteView
    }

    // Function to generate and show the notification
    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val remoteView = getRemoteView(title, message)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.drakebird)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setCustomContentView(remoteView)  // Use setCustomContentView instead

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Issue the notification
        notificationManager.notify(0, builder.build())
    }
}
