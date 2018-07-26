package com.example.admin.servicetimer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by apple on 26/07/18.
 */

public class SharedPref
{
    private static SharedPref localSharedPreferences;

    private SharedPreferences timerSharedPref;

    private String prefKey = "TIMER_SHARED_PREF";

    public static SharedPref getInstance(Context context) {
        if (localSharedPreferences == null) {
            localSharedPreferences = new SharedPref(context);
        }
        return localSharedPreferences;
    }

    private SharedPref(Context context) {
        timerSharedPref = context.getSharedPreferences(prefKey,Context.MODE_PRIVATE);
    }

    public void savePauseData(String key,long value) {
        SharedPreferences.Editor prefsEditor = timerSharedPref.edit();
        prefsEditor.putLong(key, value);
        prefsEditor.commit();
    }

    public long getPauseData(String key) {
        if (timerSharedPref!= null) {
            return timerSharedPref.getLong(key, 0);
        }
        return 0;
    }

    public void setTimerPaused(String key,boolean value) {
        SharedPreferences.Editor prefsEditor = timerSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public boolean isTimerPaused(String key) {
        if (timerSharedPref!= null) {
            return timerSharedPref.getBoolean(key, false);
        }
        return false;
    }
}
