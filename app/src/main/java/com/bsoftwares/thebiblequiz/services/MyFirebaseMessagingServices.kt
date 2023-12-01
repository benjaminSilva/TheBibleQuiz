package com.bsoftwares.thebiblequiz.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.bsoftwares.thebiblequiz.MainActivity
import com.bsoftwares.thebiblequiz.R
import com.bsoftwares.thebiblequiz.data.repositories.BaseRepository
import com.bsoftwares.thebiblequiz.ui.navigation.MY_URI
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class MyFirebaseMessagingServices : FirebaseMessagingService() {

    @Inject
    lateinit var repo: BaseRepository


    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("Token Test", token)
        scope.launch {
            repo.updateToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        sendNotification(remoteMessage)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        val notificationType = remoteMessage.data["notificationType"]

        /*val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_IMMUTABLE
        )*/

        val channelId = when (notificationType) {
            "Reminders" -> {
                this.getString(R.string.reminder_channel)
            }

            else -> {
                "The Bible Quiz"
            }
        }

        remoteMessage.notification?.apply {

            val intent = if (title?.contains("friend") == true) {
                Intent(Intent.ACTION_VIEW, MY_URI.toUri(), this@MyFirebaseMessagingServices, MainActivity::class.java)
            } else {
                Intent(this@MyFirebaseMessagingServices, MainActivity::class.java).apply {
                    addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                }
            }

            val pendingIntent = if (title?.contains("friend") == true) {
                TaskStackBuilder.create(this@MyFirebaseMessagingServices).run {
                    addNextIntentWithParentStack(intent)
                    getPendingIntent(1, FLAG_IMMUTABLE)
                }
            } else {
                PendingIntent.getActivity(
                    this@MyFirebaseMessagingServices, 0, intent, FLAG_IMMUTABLE
                )
            }


            val notificationBuilder = NotificationCompat.Builder(this@MyFirebaseMessagingServices, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.baseline_star_rate_24)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    notificationType ?: "The Bible Quiz",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                manager.createNotificationChannel(channel)
            }

            manager.notify(Random.nextInt(), notificationBuilder.build())
        }
    }
}