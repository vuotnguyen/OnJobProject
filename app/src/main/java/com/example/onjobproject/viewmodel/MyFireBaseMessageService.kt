package com.example.onjobproject.viewmodel

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.onjobproject.R
import com.example.onjobproject.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFireBaseMessageService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        Log.i("TAG", "onNewToken: "+ p0)

    }
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        if(p0.data != null){
            sendNotifycation(p0.data["title"], p0.data["body"])
            Log.i("TAG", "onMessageReceived: "+p0.data.get("title"))
        }

    }

    @SuppressLint("ServiceCast")
    fun sendNotifycation(title: String?, body: String?) {
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        val notificationCompat = NotificationCompat.Builder(applicationContext,"channel_default")
            .setSmallIcon(R.drawable.facebook)
            .setContentTitle(title)
            .setAutoCancel(false)
            .setVibrate(longArrayOf(1000,1000,1000,1000,1000))
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH).build()
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel("channel_default", "abc", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(123,notificationCompat)
    }
}