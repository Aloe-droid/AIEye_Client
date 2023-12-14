package com.example.client

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class FCM : FirebaseMessagingService() {

    companion object {
        const val EVENT = "Event"
        const val ID = 0
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (isForeground()) return

        if (message.data.isNotEmpty()) {
            Log.d("message!", "${message.data}")

            val title = message.data["title"]
            val body = message.data["body"]
            title?.let { body?.let { notification(title, body) } }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("token!!", token)
    }

    @Throws(SecurityException::class)
    private fun notification(title: String, body: String) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(EVENT, EVENT, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val jsonObject = JSONObject(body)
        val cameraId = jsonObject.get("cameraId") as Int
        val msg = jsonObject.get("description") as String
        Log.d("TAG", "notification: $cameraId, $msg, $title")
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, EVENT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setContentTitle("Event 발생!")
            .setContentText(msg)
            .setContentIntent(pendingIntent).setSmallIcon(R.drawable.robot).setAutoCancel(true)

        WebViewActivity.CAMERA_ID = cameraId
        
        with(NotificationManagerCompat.from(this)) {
            notify(ID, builder.build())
        }
    }

    private fun isForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        appProcesses.forEach {
            if (it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }
}