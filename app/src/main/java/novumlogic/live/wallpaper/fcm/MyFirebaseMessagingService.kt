package novumlogic.live.wallpaper.fcm

/**
 * Created by Priya Sindkar.
 */
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.base.SplashActivity

import java.util.concurrent.atomic.AtomicInteger

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val c = AtomicInteger(0)
    fun getID(): Int {
        return c.incrementAndGet()
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage != null && remoteMessage.data.isNotEmpty()) {
            sendNotification(remoteMessage.data)
            try {

            } catch (e: Exception) {
                Log.e("NOVUMLOGIC_AQUA", "error: ${e.message}")
            }
        }
    }

    override fun onNewToken(token: String?) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
    }

    private fun sendNotification(messageBody: MutableMap<String, String>) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = "novumlogiclivewp"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageBody["title"])
                .setContentText(messageBody["message"])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "novumlogiclivewp",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(messageBody["id"]!!.toInt(), notificationBuilder.build())
    }
}