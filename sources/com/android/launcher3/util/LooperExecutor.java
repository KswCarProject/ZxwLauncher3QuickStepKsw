package com.android.launcher3.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class LooperExecutor extends AbstractExecutorService {
    private final Handler mHandler;

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public LooperExecutor(Looper looper) {
        this.mHandler = new Handler(looper);
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void execute(Runnable runnable) {
        if (getHandler().getLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

    public void post(Runnable runnable) {
        getHandler().post(runnable);
    }

    @Deprecated
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean awaitTermination(long j, TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    public Thread getThread() {
        return getHandler().getLooper().getThread();
    }

    public Looper getLooper() {
        return getHandler().getLooper();
    }

    public void setThreadPriority(int i) {
        Process.setThreadPriority(((HandlerThread) getThread()).getThreadId(), i);
    }
}
