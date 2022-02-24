package gapara.co.id.core.base.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder

import java.util.Timer
import java.util.TimerTask
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import gapara.co.id.core.api.log
import gapara.co.id.R

class NotificationService: Service() {

    private var timer: Timer? = null

    var counter = 0
    var timeInterval = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            startForeground(ID_DEFAULT_NOTIFICATION, createNotification())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val notificationChannelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_id)
        val notificationChannel = NotificationChannel(notificationChannelId, channelName, NotificationManager.IMPORTANCE_NONE)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)

        startForeground(ID_OREO_NOTIFICATION, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationBuilder(this)
                .initialize()
                .setIsOnGoing(true)
                .setTitle("${getString(R.string.default_notification_channel_id)} standby")
                .setNotificationCategory(NotificationCompat.CATEGORY_SERVICE)
                .setNotificationPriority(NotificationManagerCompat.IMPORTANCE_NONE)
                .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        timeInterval = intent?.getIntExtra(EXTRA_TIME_INTERVAL, 0) ?: 0
        log("NotificationService onStartCommand: $timeInterval")
        startTimerTask()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimerTask()
        log("NotificationService onDestroy: Run service after destroy Count $counter")
        if (timeInterval != 0 && counter < timeInterval) {
            val broadcastIntent = Intent()
            broadcastIntent.action = ACTION_RESTART_SERVICE
            broadcastIntent.setClass(this, NotificationReceiver::class.java)
            this.sendBroadcast(broadcastIntent)
        }
    }

    private fun startTimerTask() {
        if (timeInterval != 0) {
            this.timer = Timer()
            timer?.schedule(createTimerTask(), ONE_SECOND, ONE_SECOND)
        }
    }

    private fun createTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                log("NotificationService Count: "+"=========  " + counter++)
                stopTickingTimer(this)
            }
        }
    }

    private fun stopTickingTimer(currentTimerTask: TimerTask) {
        if (counter == timeInterval) {
            log("NotificationService Count: run: STOP !!!")
            stopServiceNotif()
            currentTimerTask.cancel()
        }
    }

    private fun stopServiceNotif() {
        val broadcastIntent = Intent()
        broadcastIntent.action = ACTION_RESTART_SERVICE
        broadcastIntent.putExtra(EXTRA_STOP_SERVICE, true)
        broadcastIntent.setClass(this, NotificationReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private fun stopTimerTask() {
        timer?.cancel()
        timer = null
    }

    companion object {
        const val EXTRA_TIME_INTERVAL = "time_interval"
        const val EXTRA_STOP_SERVICE = "stop_service"
        const val ACTION_RESTART_SERVICE = "restartservice"

        private const val ID_DEFAULT_NOTIFICATION = 1
        private const val ID_OREO_NOTIFICATION = 2

        private const val ONE_SECOND = 1000L
    }
}
