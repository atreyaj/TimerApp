package com.example.admin.servicetimer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.servicetimer.service.Constants;
import com.example.admin.servicetimer.service.TimerService;

import java.util.concurrent.TimeUnit;

import static com.example.admin.servicetimer.service.Constants.IS_PAUSED;
import static com.example.admin.servicetimer.service.Constants.PAUSE_TIME;
import static com.example.admin.servicetimer.service.Constants.START_TIME;

public class MainActivity extends Activity {
    TextView textViewTime;
    private TimerStatusReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTime = (TextView) findViewById(R.id.status_tv);

        SharedPref localSharedPreferences = SharedPref.getInstance(this);

        boolean isTimerPaused = localSharedPreferences.isTimerPaused(IS_PAUSED);

        if(isTimerPaused)
        {
            long remainingTime = localSharedPreferences.getPauseData(PAUSE_TIME);
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(remainingTime),
                    TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)),
                    TimeUnit.MILLISECONDS.toSeconds(remainingTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime)));
            textViewTime.setText(hms);
        }

        receiver = new TimerStatusReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(TimerService.TIME_INFO));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public void startTimerService(View view) {
        Intent intent = new Intent(this, TimerService.class);
        intent.putExtra(IS_PAUSED,false);
        intent.putExtra(START_TIME,30000);
        startService(intent);
    }

    public void stopTimerService(View view) {
        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);
    }

    public void pauseTimerService(View view)
    {
        Intent intent = new Intent(this, TimerService.class);
        intent.putExtra(IS_PAUSED,true);
        startService(intent);
    }

    public void resetTimerService(View view)
    {
        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);

        textViewTime.setText("00:00:30");
    }


    private class TimerStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(TimerService.TIME_INFO)) {
                if (intent.hasExtra("VALUE")) {
                    textViewTime.setText(intent.getStringExtra("VALUE"));
                }
            }
        }
    }
}
