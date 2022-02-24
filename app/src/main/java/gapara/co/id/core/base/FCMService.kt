package gapara.co.id.core.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import gapara.co.id.core.api.GSONManager
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.log
import gapara.co.id.core.api.response.LoginResponse
import gapara.co.id.core.model.Urls
import gapara.co.id.core.model.UserModel
import gapara.co.id.R
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.feature.splash.SplashActivity
import kotlinx.coroutines.launch
import kotlin.random.Random

class FCMService : FirebaseMessagingService(), CoroutineDeclare {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (token != BaseApplication.sessionManager?.fcmToken) {
            BaseApplication.sessionManager?.fcmToken = token
            reLogin(token)
        }
    }

    override fun handleIntent(intent: Intent?) {
        intent?.extras?.apply {
            val data = HashMap<String, String>()
            data["scheme"] = getString("scheme") ?: ""
            data["body"] = getString("body") ?: ""
            data["title"] = getString("title") ?: ""

            if (!getString("title").isNullOrBlank() && !getString("body").isNullOrBlank()) {
                BaseApplication.context?.apply {
                    showNotification(data, this)
                }
            }
        }
        super.handleIntent(intent)
    }

    private fun reLogin(fcmToken: String) {
        BaseApplication.sessionManager?.loginParams.takeIf { !it.isNullOrEmpty() }?.apply {
            val userModel = GSONManager.fromJson(this, UserModel::class.java)
            userModel.fcmToken = fcmToken
            val url = Urls.POST_LOGIN

            resetAccessToken()

            launch {
                val loginResponse = PostRequest<UserModel>(url).post<LoginResponse>(userModel)
                if (loginResponse?.isSuccess == true) {
                    BaseApplication.sessionManager?.accessToken = loginResponse.loginModel?.accessToken
                }
            }
        }
    }

    private fun resetAccessToken() {
        BaseApplication.sessionManager?.accessToken = ""
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        BaseApplication.context?.apply {
            showNotification(remoteMessage.data, this)
        }
    }

    private fun showNotification(data: Map<String, String>?, context: Context) {
        data?.let { scheme ->
            val finalScheme = scheme["scheme"]

            log("scheme $finalScheme")
            log("data $scheme")

            val defaultName = context.getString(R.string.default_notification_channel_id)
            val title = scheme["title"] ?: defaultName
            val message = scheme["body"] ?: "Notification from $defaultName"

            val notificationIntent = Intent()

            notificationIntent.setClass(context, SplashActivity::class.java)

            val ranInt = Random.nextInt()

            val intentPending = PendingIntent.getActivity(
                context,
                ranInt,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationBuilder = NotificationCompat.Builder(context, defaultName)
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setSound(soundUri, AudioManager.STREAM_ALARM)
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(intentPending)
                .setSmallIcon(R.drawable.ic_notifications)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            setVibrator(context)

            val notification = notificationBuilder.build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    defaultName,
                    defaultName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
                manager.notify(ranInt, notification)
            } else {
                val notificationManager = NotificationManagerCompat.from(this)
                notificationManager.notify(ranInt, notification)
            }

        } ?: run {
            log("FCM kosong")
        }
    }
}