package gapara.co.id.core.base.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import gapara.co.id.core.api.ApiExtensionKt;

public class OnClearFromRecentService extends Service {

    private int currentTime = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentTime = intent.getIntExtra(NotificationService.EXTRA_TIME_INTERVAL, 0);
        ApiExtensionKt.log("ClearFromRecentService "+"Service Started "+intent.getIntExtra(NotificationService.EXTRA_TIME_INTERVAL, 0));
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        ApiExtensionKt.log("ClearFromRecentService Service Destroyed");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        ApiExtensionKt.log("ClearFromRecentService "+"END "+currentTime);
        runBroadcast();
        stopSelf();
    }

    private void runBroadcast() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NotificationService.ACTION_RESTART_SERVICE);
        broadcastIntent.setClass(this, NotificationReceiver.class);
        broadcastIntent.putExtra(NotificationService.EXTRA_TIME_INTERVAL, currentTime);
        this.sendBroadcast(broadcastIntent);
    }
}