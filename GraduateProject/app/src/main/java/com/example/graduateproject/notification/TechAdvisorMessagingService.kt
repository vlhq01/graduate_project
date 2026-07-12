package com.example.graduateproject.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.graduateproject.MainActivity
import com.example.graduateproject.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class TechAdvisorMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val title = data["title"] ?: "TechAdvisor"
        val body = data["body"] ?: "Bạn có gợi ý sản phẩm mới."
        val productId = data["productId"]
        val imageUrl = data["imageUrl"]

        Log.d("image", "onMessageReceived: $imageUrl")

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = imageUrl
                ?.takeIf { it.isNotBlank() }
                ?.let { loadBitmap(it) }

            showRichNotification(
                title = title,
                body = body,
                productId = productId,
                image = bitmap
            )
        }
    }

    private fun showRichNotification(
        title: String,
        body: String,
        productId: String?,
        image: Bitmap?
    ) {
        val channelId = "techadvisor_recommendations"
        createChannel(channelId)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("productId", productId)
            putExtra("notificationType", "AI_PICK")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            productId?.hashCode() ?: 0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(0xFF56D6A0.toInt())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.searchicon,
                "Xem ngay",
                pendingIntent
            )

        if (image != null) {
            builder
                .setLargeIcon(image)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon(null as Bitmap?)
                        .setSummaryText(body)
                )
        } else {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
        }

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun createChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "AI recommendations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Personalized product recommendations and deals"
            }

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun loadBitmap(url: String): Bitmap? {
        return runCatching {
            URL(url).openStream().use { BitmapFactory.decodeStream(it) }
        }.getOrNull()
    }
}