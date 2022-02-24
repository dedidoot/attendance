package gapara.co.id.core.base.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getBooleanExtra(NotificationService.EXTRA_STOP_SERVICE, false)) {
            val stopIntent = Intent(context, NotificationService::class.java)
            stopIntent.putExtra(NotificationService.EXTRA_STOP_SERVICE, true)
            context.stopService(stopIntent)
        } else {
            val serviceIntent = Intent(context, NotificationService::class.java)
            serviceIntent.putExtra(NotificationService.EXTRA_TIME_INTERVAL, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
