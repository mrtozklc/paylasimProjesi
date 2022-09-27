package com.example.paylasim.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.paylasim.R
import com.example.paylasim.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class messagingService:FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
     var  bildirimTitle=message.notification!!.title
        var bildirimBody=message.notification!!.body
        var bildirimData=message.data.get("konusulacakKisi")

        Log.e("bildirim", "bildirim$bildirimTitle$bildirimBody$bildirimData")

        yeniMesajBildirimi(bildirimTitle,bildirimBody,bildirimData
        )
    }

    private fun yeniMesajBildirimi(bildirimTitle: String?, bildirimBody: String?, gidilecekUserID: String?) {

        var pendingIntent= Intent(this,MainActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("konusulacakKisi",gidilecekUserID)

        var bildirimPendingIntent= PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder= NotificationCompat.Builder(this,"Yeni Mesaj")
            .setSmallIcon(R.drawable.ic_launcher_addphoto_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_mesaj2_foreground))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(bildirimTitle)
            .setContentText(bildirimBody)
            .setColor(getColor(R.color.mavi)).setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(bildirimPendingIntent)
            .build()

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        @RequiresApi(O)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Yeni Mesaj", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(bildirimIDOlustur(gidilecekUserID!!),builder)




    }


    private fun bildirimIDOlustur(gidilecekUserID: String): Int{
        var id= 0

        for(i in 0..5){
            id= id + gidilecekUserID[i].toInt()
        }

        return id
    }


    override fun onNewToken(token: String) {
     var newToken=token!!

     newTokenAl(newToken)
    }

    private fun newTokenAl(newToken: String) {

        if (FirebaseAuth.getInstance().currentUser!=null){





        }

    }
}