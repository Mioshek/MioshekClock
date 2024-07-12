package com.mioshek.theclock.extensions.permissions

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

enum class RuntimePermissions(val permission: String) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU) NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS),
    @RequiresApi(Build.VERSION_CODES.TIRAMISU) MEDIA_AUDIO(READ_MEDIA_AUDIO),
    EXTERNAL_STORAGE(READ_EXTERNAL_STORAGE)
}

class PermissionManager{
    companion object{
        fun checkPermission(context: Context, permission: String): Boolean {
            return if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
            }
            else{
                ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
            }
        }

        fun requestPermission(activity: Activity, permission: String){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    1
                )
            }
            else{
                if (!NotificationManagerCompat.from(activity.applicationContext).areNotificationsEnabled()) {
                    Toast.makeText(activity.applicationContext, "Please enable notifications in settings", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
                    }
                    ContextCompat.startActivity(activity.applicationContext, intent, null)
                }
            }

        }
    }
}