package com.example.practica1

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.practica1.Chat.ChatActivity


class MiFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "CHAT_MESSAGES_HIGH"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        guardarToken(token)
    }

    private fun guardarToken(token: String) {
        val uid = FirebaseAuth.getInstance().uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("fcmToken")
            .setValue(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val senderUid = remoteMessage.data["senderUid"] ?: return

        // 🚫 Si el chat ya está abierto
        if (ChatActivity.chatAbiertoConUid == senderUid) return

        val title = remoteMessage.data["title"] ?: "Nuevo mensaje"
        val body = remoteMessage.data["body"] ?: ""

        mostrarNotificacion(title, body, senderUid)
    }

    private fun mostrarNotificacion(title: String, body: String, senderUid: String) {

        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("uid", senderUid)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            senderUid.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Mensajes de chat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 🔥 HEADS-UP
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

}

