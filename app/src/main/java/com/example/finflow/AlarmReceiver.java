package com.example.finflow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final String CHANNEL_NAME = "Important Reminders";
    private static final String CHANNEL_DESCRIPTION = "Channel for important reminder notifications";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Notification received");
        String reminderIdString = intent.getStringExtra(ReminderEditActivity.EXTRA_REMINDER_ID);
        if (reminderIdString != null) {
            int reminderId = Integer.parseInt(reminderIdString);
            Reminder reminder;
            try (ReminderDatabase rb = new ReminderDatabase(context)) {
                reminder = rb.getReminder(reminderId);
            }
            if (reminder != null) {
                showNotification(context, reminder);
            }
        }
    }

    private void showNotification(Context context, Reminder reminder) {
        Log.d(TAG, "showNotification: Creating notification");
        Intent editIntent = new Intent(context, ReminderEditActivity.class);
        editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, String.valueOf(reminder.getID()));
        PendingIntent clickPendingIntent = PendingIntent.getActivity(context, reminder.getID(), editIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_alarm_on_alarm_24dp)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setTicker(reminder.getTitle())
                .setContentText(reminder.getTitle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(clickPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
                if (channel != null && channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }
            }
            notificationManager.notify(reminder.getID(), builder.build());
            Log.d(TAG, "showNotification: Notification created and sent");
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setAlarm(Context context, Calendar calendar, int id) {
        createNotificationChannel(context);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, String.valueOf(id));
        mPendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long diffTime = calendar.getTimeInMillis() - System.currentTimeMillis();
        mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);
        enableBootReceiver(context);
        Log.d(TAG, "setAlarm: Alarm set for reminder ID " + id);
    }

    public void setRepeatAlarm(Context context, Calendar calendar, int id, long repeatTime) {
        createNotificationChannel(context);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, String.valueOf(id));
        mPendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long diffTime = calendar.getTimeInMillis() - System.currentTimeMillis();
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + diffTime,
                repeatTime, mPendingIntent);
        enableBootReceiver(context);
        Log.d(TAG, "setRepeatAlarm: Repeating alarm set for reminder ID " + id);
    }

    public void cancelAlarm(Context context, int id) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getBroadcast(context, id, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_IMMUTABLE);
        mAlarmManager.cancel(mPendingIntent);
        disableBootReceiver(context);
        Log.d(TAG, "cancelAlarm: Alarm cancelled for reminder ID " + id);
    }

    private void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
    }
}