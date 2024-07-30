package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data[action]?.let {
            try {
            when (Action.valueOf(it)) {
                Action.LIKE -> handleLike(gson.fromJson(message.data[content], Like::class.java))
                Action.NEWPOST -> handleNewPost(gson.fromJson(message.data[content], NewPost::class.java))
            }
        } catch (e: IllegalArgumentException) {
            handleException()
        }
        }
        textMessageReceived(message)
    }

    private fun textMessageReceived(message: RemoteMessage) {
        val userId = AppAuth.getInstance().state.value?.id.toString()

        val messageData = gson.fromJson(
            message.data[content],
            TextNotification::class.java
        )

        when (messageData.recipientId) {
            null,
            userId -> handleTextNotification(messageData.content)
            else -> AppAuth.getInstance().sendPushToken()
        }
    }



    override fun onNewToken(token: String) {
        AppAuth.getInstance().sendPushToken(token)
    }

    private fun handleTextNotification(content: String?) {
        makeNotification(
            getString(
                R.string.notification_new,
                content
            )
        )
    }

    private fun handleException() {
        makeNotification("Some unknown type of notification received")
    }

    private fun handleNewPost(content: NewPost) {
        makeNotificationWithBigText(
            getString(
                R.string.notification_new_post,
                content.userName,
                // content.postText.take(15)+"..."

            ), content.postText
        )
    }

    private fun handleLike(content: Like) {
        makeNotification(
            getString(
                R.string.notification_user_liked,
                content.userName,
                content.postAuthor
            )
        )
    }

    private fun makeNotification(content: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(Random.nextInt(100_000), notification)
    }

    private fun makeNotificationWithBigText(title: String, postText: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(postText.take(20) + "...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(postText)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(Random.nextInt(100_000), notification)
    }
}

enum class Action {
    LIKE,
    NEWPOST,
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class TextNotification(
    val recipientId: String?,
    val content: String
)

data class NewPost(
    val userId: Long,
    val userName: String,
    val postText: String
)