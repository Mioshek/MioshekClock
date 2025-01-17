package com.mioshek.theclock.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Notification
import android.app.Notification.Action
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.mioshek.theclock.R
import com.mioshek.theclock.data.Storage
import com.mioshek.theclock.extensions.permissions.PermissionManager.Companion.checkPermission
import com.mioshek.theclock.extensions.permissions.RuntimePermissions

class NotificationsManager(
    private val application: Application,
    private val CHANNEL_CODE: String,
    importance: Int,
    name: String,
    descriptionText: String,
) {
    lateinit var builder: NotificationCompat.Builder

    init {
        createNotificationChannel(importance, name, descriptionText, CHANNEL_CODE)
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun showPopupNotification(
        NOTIFICATION_ID: Int,
        smallIcon: Int,
        title: String,
        content: String,
        pendingIntent: PendingIntent,
        priority: Int
    ) {

        // Use the class-level builder property
        builder = NotificationCompat.Builder(application, CHANNEL_CODE)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setPriority(priority)
            .addAction(R.drawable.stop, "Dismiss", pendingIntent)
        // Set a dummy content intent or remove this line if no action is needed on click
        // .setContentIntent(contentIntent)

        with(NotificationManagerCompat.from(application)) {
            // Check if notification permission is granted
            if (!checkPermission(application.applicationContext, RuntimePermissions.NOTIFICATIONS.permission)) {
                return
            }
            // Check notification settings (e.g., channel existence)
            checkNotificationSettings()

            // Notify with the built notification
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    fun showLockScreenNotification(
        smallIcon: Int,
        title: String,
        content: String,
        priority: Int,
        NOTIFICATION_ID: Int,
        actions: Array<Triple<Int, String, Class<*>>>
    ) {
        builder = NotificationCompat.Builder(application, CHANNEL_CODE)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(priority)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setDefaults(Notification.DEFAULT_VIBRATE)

        actions.forEachIndexed { index, action ->
            val intent = Intent(application, action.third)
            val pendingIntent = PendingIntent.getBroadcast(
                application,
                index + 100,  // Ensure unique request code for each action
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(action.first, action.second, pendingIntent)
        }
        val notification = builder.build()

        with(NotificationManagerCompat.from(application)) {

            checkNotificationSettings()
            if (ActivityCompat.checkSelfPermission(
                    application.applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, notification)
        }
    }

    @SuppressLint("NewApi")
    fun updateNotification(NOTIFICATION_ID: Int, content: String) {
        builder.setContentText(content)
        with(NotificationManagerCompat.from(application)) {
            if (!checkPermission(application.applicationContext, RuntimePermissions.NOTIFICATIONS.permission)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    private fun createNotificationChannel(importance: Int, name: String, descriptionText: String, channelCode: String) {
        val channel = NotificationChannel(channelCode, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun checkNotificationSettings() {
        if (!NotificationManagerCompat.from(application.applicationContext).areNotificationsEnabled()) {

            Toast.makeText(application.applicationContext, "Please enable notifications in settings", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Settings.EXTRA_APP_PACKAGE, application.packageName)
            }
            ContextCompat.startActivity(application.applicationContext, intent, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission(permissions: Array<String>, activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            1
        )
    }

    fun discardNotification(notificationId: Int) {
        val notificationManager = application.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
