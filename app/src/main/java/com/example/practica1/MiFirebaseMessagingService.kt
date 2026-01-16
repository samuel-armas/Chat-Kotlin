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
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.practica1.Chat.ChatActivity


class MiFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        guardarToken(token)
    }

    private fun guardarToken(token: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)

        ref.child("fcmToken").setValue(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val titulo = message.notification?.title ?: "Nuevo mensaje"
        val cuerpo = message.notification?.body ?: "Tienes un nuevo mensaje"
        val senderUid = message.data["senderUid"] ?: ""

        mostrarNotificacion(titulo, cuerpo, senderUid)
    }
    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "MENSAJES",
                "Mensajes",
                NotificationManager.IMPORTANCE_HIGH
            )

            canal.description = "Notificaciones de mensajes"

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }
    private fun mostrarNotificacion(titulo: String, cuerpo: String, senderUid: String) {
        crearCanalNotificacion()
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("uid", senderUid)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "MENSAJES")
            .setSmallIcon(R.drawable.ic_notification) // 👈 AJUSTA ESTE ÍCONO
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }


}
