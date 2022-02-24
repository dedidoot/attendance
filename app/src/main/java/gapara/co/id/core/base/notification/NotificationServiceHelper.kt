package gapara.co.id.core.base.notification

import android.app.ActivityManager
import android.content.Context
import android.content.Intent

open class NotificationServiceHelper(val context: Context) {

    private var currentTimer: Int = 0

    fun setCurrentTimer(timer: Int): NotificationServiceHelper {
        this.currentTimer = timer
        return this
    }

    fun run(): NotificationServiceHelper {
        val notificationService = NotificationService()
        if (isServiceRunning(notificationService::class.java)) {
            context.stopService(Intent(context, notificationService::class.java))
        }

        val serviceIntent = Intent(context, OnClearFromRecentService::class.java)
        serviceIntent.putExtra(NotificationService.EXTRA_TIME_INTERVAL, currentTimer)
        context.startService(serviceIntent)

        return this
    }

    private fun isServiceRunning(classObject: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (classObject.name == service.service.className) {
                return true
            }
        }
        return false
    }

}