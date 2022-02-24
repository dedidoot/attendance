package gapara.co.id.core.base.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import gapara.co.id.R
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class NotificationBuilder(val context: Context) : NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id)) {

    private var notificationBuilder: NotificationCompat.Builder? = null

    private var notificationId = 1

    private var channelId = ""
    private var title: String? = null
    private var message: String? = null

    init {
        channelId = context.getString(R.string.default_notification_channel_id)
    }

    fun initialize() : NotificationBuilder {
        notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            notificationBuilder?.setSmallIcon(R.drawable.ic_app)
            notificationBuilder?.color = ContextCompat.getColor(context, R.color.white)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder?.setSound(defaultSoundUri)
        return this
    }

    fun setTitle(title: String?): NotificationBuilder {
        this.title = title
        notificationBuilder?.setContentTitle(title)
        return this
    }

    fun setMessage(message: String?): NotificationBuilder {
        this.message = message
        notificationBuilder?.setContentText(message)
        notificationBuilder?.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        return this
    }

    fun setNotifId(notifId: Int): NotificationBuilder {
        this.notificationId = notifId
        return this
    }

    fun setNotificationIntent(pendingIntent: PendingIntent): NotificationBuilder {
        notificationBuilder?.setContentIntent(pendingIntent)
        return this
    }

    fun setLargeIcon(photo: String?): NotificationBuilder {
        getBitmapFromURL(photo)?.let { bitmap ->
            notificationBuilder?.setLargeIcon(bitmap)
        }
        return this
    }

    fun setBigPhoto(bigPhoto: String?): NotificationBuilder {
        if (!bigPhoto.isNullOrBlank()) {
            getBitmapFromURL(bigPhoto)?.let { bitmap ->
                notificationBuilder?.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .setSummaryText(message)
                        .setBigContentTitle(title))
            }
        }
        return this
    }

    fun setNotificationCategory(category: String): NotificationBuilder {
        notificationBuilder?.setCategory(category)
        return this
    }

    fun setNotificationPriority(
            priority: Int = NotificationCompat.PRIORITY_DEFAULT): NotificationBuilder {
        notificationBuilder?.priority = priority
        return this
    }

    fun setIsOnGoing(isOnGoing: Boolean): NotificationBuilder {
        notificationBuilder?.setOngoing(isOnGoing)
        return this
    }

    fun show(): NotificationBuilder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        val notification = notificationBuilder?.build()
        notification?.let {
            notificationManager.notify(notificationId, notification)
        }
        return this
    }

    private fun getBitmapFromURL(photoUrl: String?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val url = URL(photoUrl)
            bitmap = BitmapFactory.decodeStream(url.openStream())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }
}
