package com.itcs.aihome;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.widget.Toast;

import java.net.UnknownServiceException;
import java.security.Provider;
import java.util.List;
import java.util.Map;

public class BootDeviceReceiver extends BroadcastReceiver {
    public static final String TAG = BootDeviceReceiver.class.getSimpleName();
    private static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String message = "BootDeviceReceiver onReceive, action is " + action;
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        showNotification("AI HOME SERVICE", "Ai Home service started.", context);
        Log.d(TAG_BOOT_BROADCAST_RECEIVER, message);
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            startServiceByAlarm(context);
        }
    }

    private void startServiceDirectly(Context context) {
        try {
            while (true) {
                String message = "BootDeviceReceiver onReceive start service directly.";
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                Log.d(TAG_BOOT_BROADCAST_RECEIVER, message);
                Intent startServiceIntent = new Intent(context, RunAfterBootService.class);
                context.startService(startServiceIntent);
                Thread.sleep(2000);
            }
        }catch(InterruptedException ex)
        {
            Log.e(TAG_BOOT_BROADCAST_RECEIVER, ex.getMessage(), ex);
        }
    }

    private void startServiceByAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RunAfterBootService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long startTime = System.currentTimeMillis();
        long intervalTime = 60 * 1000;
        String message = "Start service use repeat alarm";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG_BOOT_BROADCAST_RECEIVER, message);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, pendingIntent);
    }

    private void showNotification(String title, String content, Context context) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.aihome)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
}