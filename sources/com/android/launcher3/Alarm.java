package com.android.launcher3;

import android.os.Handler;
import android.os.SystemClock;

public class Alarm implements Runnable {
    private OnAlarmListener mAlarmListener;
    private boolean mAlarmPending = false;
    private long mAlarmTriggerTime;
    private Handler mHandler = new Handler();
    private boolean mWaitingForCallback;

    public void setOnAlarmListener(OnAlarmListener onAlarmListener) {
        this.mAlarmListener = onAlarmListener;
    }

    public void setAlarm(long j) {
        long uptimeMillis = SystemClock.uptimeMillis();
        this.mAlarmPending = true;
        long j2 = this.mAlarmTriggerTime;
        long j3 = j + uptimeMillis;
        this.mAlarmTriggerTime = j3;
        if (this.mWaitingForCallback && j2 > j3) {
            this.mHandler.removeCallbacks(this);
            this.mWaitingForCallback = false;
        }
        if (!this.mWaitingForCallback) {
            this.mHandler.postDelayed(this, this.mAlarmTriggerTime - uptimeMillis);
            this.mWaitingForCallback = true;
        }
    }

    public void cancelAlarm() {
        this.mAlarmPending = false;
    }

    public void run() {
        this.mWaitingForCallback = false;
        if (this.mAlarmPending) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = this.mAlarmTriggerTime;
            if (j > uptimeMillis) {
                this.mHandler.postDelayed(this, Math.max(0, j - uptimeMillis));
                this.mWaitingForCallback = true;
                return;
            }
            this.mAlarmPending = false;
            OnAlarmListener onAlarmListener = this.mAlarmListener;
            if (onAlarmListener != null) {
                onAlarmListener.onAlarm(this);
            }
        }
    }

    public boolean alarmPending() {
        return this.mAlarmPending;
    }
}
