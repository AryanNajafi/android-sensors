package com.github.sensorsreport.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LifecycleService;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;

import com.github.sensorsreport.MainActivity;
import com.github.sensorsreport.R;
import com.github.sensorsreport.data.SensorLiveData;
import com.github.sensorsreport.data.db.AppDatabase;
import com.github.sensorsreport.data.db.SensorDataDao;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ReportService extends LifecycleService {

    public static final String REQUESTING_REPORT = "requesting_report";

    private static final int NOTIFICATION_ID = 1002;
    private static final String CHANNEL_ID = "channel_01";

    private SensorDataDao sensorDataDao;

    private Executor diskExecutor;

    public ReportService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        diskExecutor = Executors.newSingleThreadExecutor();

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(REQUESTING_REPORT, true)
                .apply();

        sensorDataDao = AppDatabase.getDatabase(this).sensorDataDao();

        SensorLiveData.getInstance(this).observe(this, sensorData -> {
            if (sensorData == null) {
                return;
            }

            diskExecutor.execute(() -> sensorDataDao.insert(sensorData));
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                            getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        startForeground(NOTIFICATION_ID, makeNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(REQUESTING_REPORT, false)
                .apply();

        super.onDestroy();
    }

    private Notification makeNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notification_title))
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID);
        }
        return notificationBuilder.build();
    }
}
