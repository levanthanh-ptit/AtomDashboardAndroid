package com.atom.dashboardandroid.sevices;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.atom.dashboardandroid.AddTaskActivity;
import com.atom.dashboardandroid.R;
import com.atom.dashboardandroid.Room.Entities.Task;


public class SchedulingService extends IntentService {
    public static final String TAG = "SCHEDULING_SERVICE";
    public static final String TASK = "ATOM_TASK";
    public static final String INDEX = "INDEX";

    public SchedulingService() {
        super(SchedulingService.class.getSimpleName());
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Task Channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Todo");
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "onHandleIntent: system time: "+System.currentTimeMillis());
            Bundle bundle = intent.getExtras();
            int index = bundle.getInt(INDEX, -1);
            String title = bundle.getString(Task.TITLE,"");
            String content = bundle.getString(Task.CONTENT,"");
            Intent notificationIntent = new Intent(this, AddTaskActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            int requestId = (int) System.currentTimeMillis();
            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            initChannels(this);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, "default")
                            .setSmallIcon(R.drawable.ic_tasks_solid)
                            .setContentTitle(title)
                            .setContentText(content)
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(false)
                            .setPriority(6)
                            .setVibrate(new long[]{200, 1000})
                            .setContentIntent(contentIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(index, builder.build());
        }
    }
}
