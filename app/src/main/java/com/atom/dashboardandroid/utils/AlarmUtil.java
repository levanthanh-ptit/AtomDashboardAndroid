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
        Log.d(TAG, "task date in milis: "+task.getDate().getTime());
        Date d = new Date(calendar.getTimeInMillis());
        Log.d(TAG, "createService: d:::"+d.toString());
        Log.d(TAG, "createService: "
                + calendar.get(Calendar.HOUR)
                + ":" + calendar.get(Calendar.MINUTE)
                + " " + calendar.get(Calendar.DAY_OF_MONTH)
                + ":" + calendar.get(Calendar.MONTH)
                + ":" + calendar.get(Calendar.YEAR));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "createService: task time: "+new Date().getTime());
        Log.d(TAG, "createService: system time: "+System.currentTimeMillis());

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,task.getDate().getTime(), pendingIntent);

    }

//    public static void createBroadcast(Context context, Task task) {
//        Intent intent = new Intent(context, TaskNotificationReceiver.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt(SchedulingService.INDEX, INDEX);
//        bundle.putString(Task.TITLE, task.getTitle());
//        bundle.putString(Task.CONTENT, task.getContent());
//        intent.putExtras(bundle);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                0,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        INDEX++;
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(task.getDate());
//        Date d = new Date(calendar.getTimeInMillis());
//        Log.d(TAG, "createBroadcast: d:::"+d.toString());
//        Log.d(TAG, "createBroadcast: "
//                + calendar.get(Calendar.HOUR)
//                + ":" + calendar.get(Calendar.MINUTE)
//                + ":" + calendar.get(Calendar.DAY_OF_MONTH)
//                + ":" + calendar.get(Calendar.MONTH)
//                + ":" + calendar.get(Calendar.YEAR));
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//
//    }
}
