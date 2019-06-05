package com.atom.dashboardandroid.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import com.atom.dashboardandroid.Room.Entities.Task;
import com.atom.dashboardandroid.sevices.SchedulingService;
import com.atom.dashboardandroid.sevices.TaskNotificationReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AlarmUtil {
    public static final String TAG = "ALARM_UTIL";
    private static int INDEX = 1;

    public static void cancelAlarm(Context context, Task task) {
        Intent intent = new Intent(context, SchedulingService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(SchedulingService.INDEX, INDEX);
        bundle.putString(Task.TITLE, task.getTitle());
        bundle.putString(Task.CONTENT, task.getContent());
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void createService(Context context, Task task) {
        Intent intent = new Intent(context, SchedulingService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(SchedulingService.INDEX, INDEX);
        bundle.putString(Task.TITLE, task.getTitle());
        bundle.putString(Task.CONTENT, task.getContent());
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        INDEX++;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getDate());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,task.getDate().getTime(), pendingIntent);
    }
}
