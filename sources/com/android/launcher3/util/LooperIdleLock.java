package com.android.launcher3.util;

import android.os.Looper;
import android.os.MessageQueue;

public class LooperIdleLock implements MessageQueue.IdleHandler {
    private boolean mIsLocked = true;
    private final Object mLock;
    private Looper mLooper;

    public LooperIdleLock(Object obj, Looper looper) {
        this.mLock = obj;
        this.mLooper = looper;
        looper.getQueue().addIdleHandler(this);
    }

    public boolean queueIdle() {
        synchronized (this.mLock) {
            this.mIsLocked = false;
            this.mLock.notify();
        }
        this.mLooper.getQueue().removeIdleHandler(this);
        return false;
    }

    public boolean awaitLocked(long j) {
        if (this.mIsLocked) {
            try {
                this.mLock.wait(j);
            } catch (InterruptedException unused) {
            }
        }
        return this.mIsLocked;
    }
}
