package com.example.admin.servicetimer.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.admin.servicetimer.MainActivity;

import com.example.admin.servicetimer.R;
import com.example.admin.servicetimer.SharedPref;

import java.util.concurrent.TimeUnit;

import static com.example.admin.servicetimer.service.Constants.IS_PAUSED;
import static com.example.admin.servicetimer.service.Constants.PAUSE_TIME;
import static com.example.admin.servicetimer.service.Constants.START_TIME;

public class TimerService extends Service {
    public static final String TIME_INFO = "time_info";

    private Counter timer;

    private String pauseVal = "Timer";

    private long pauseTime;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean isPaused = intent.getBooleanExtra(IS_PAUSED, false);
        Log.e("isPaused ", "" + isPaused);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        @SuppressLint("WrongConstant")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isPaused) {
            timer.cancel();
            SharedPref localSharedPreferences = SharedPref.getInstance(this);
            localSharedPreferences.savePauseData(PAUSE_TIME, pauseTime);
            localSharedPreferences.setTimerPaused(IS_PAUSED, true);

            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(pauseVal)
                    .setContentIntent(pendingIntent).build();

            startForeground(101, notification);

        } else {
            long startTime = intent.getLongExtra(START_TIME, 30000);

            Log.e("startTime ", "" + startTime);

            timer = new Counter(30000, 1000);
            timer.start();

            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("Timer is on")
                    .setContentIntent(pendingIntent).build();

            startForeground(101, notification);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();

        SharedPref localSharedPreferences = SharedPref.getInstance(this);
        localSharedPreferences.setTimerPaused(IS_PAUSED, false);

        Intent timerInfoIntent = new Intent(TIME_INFO);
        timerInfoIntent.putExtra("VALUE", "Stopped");
        LocalBroadcastManager.getInstance(TimerService.this).sendBroadcast(timerInfoIntent);
    }

    public class Counter extends CountDownTimer {

        public Counter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            pauseVal = hms;
            pauseTime = millis;
            Intent timerInfoIntent = new Intent(TIME_INFO);
            timerInfoIntent.putExtra("VALUE", hms);
            LocalBroadcastManager.getInstance(TimerService.this).sendBroadcast(timerInfoIntent);
        }

        @Override
        public void onFinish() {
            Intent timerInfoIntent = new Intent(TIME_INFO);
            timerInfoIntent.putExtra("VALUE", "Completed");
            LocalBroadcastManager.getInstance(TimerService.this).sendBroadcast(timerInfoIntent);
            //stopSelf();
        }
    }
}
