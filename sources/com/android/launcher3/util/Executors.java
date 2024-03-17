package com.android.launcher3.util;

import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import com.android.launcher3.util.Executors;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Executors {
    private static final int KEEP_ALIVE = 1;
    public static final LooperExecutor MAIN_EXECUTOR = new LooperExecutor(Looper.getMainLooper());
    public static final LooperExecutor MODEL_EXECUTOR = new LooperExecutor(createAndStartNewLooper("launcher-loader"));
    private static final Map<String, LooperExecutor> PACKAGE_EXECUTORS = new ConcurrentHashMap();
    private static final int POOL_SIZE;
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;
    public static final LooperExecutor UI_HELPER_EXECUTOR = new LooperExecutor(createAndStartNewLooper("UiThreadHelper", -2));

    static {
        int max = Math.max(Runtime.getRuntime().availableProcessors(), 2);
        POOL_SIZE = max;
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(max, max, 1, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }

    public static Looper createAndStartNewLooper(String str) {
        return createAndStartNewLooper(str, 0);
    }

    public static Looper createAndStartNewLooper(String str, int i) {
        HandlerThread handlerThread = new HandlerThread(str, i);
        handlerThread.start();
        return handlerThread.getLooper();
    }

    public static LooperExecutor getPackageExecutor(String str) {
        return PACKAGE_EXECUTORS.computeIfAbsent(str, $$Lambda$Executors$KVuVtJkCJcvaxBKjXjZKHMnXIQ.INSTANCE);
    }

    static /* synthetic */ LooperExecutor lambda$getPackageExecutor$0(String str) {
        return new LooperExecutor(createAndStartNewLooper(str, 0));
    }

    public static class SimpleThreadFactory implements ThreadFactory {
        private final AtomicInteger mCount = new AtomicInteger(0);
        private final String mNamePrefix;
        private final int mPriority;

        public SimpleThreadFactory(String str, int i) {
            this.mNamePrefix = str;
            this.mPriority = i;
        }

        public Thread newThread(Runnable runnable) {
            return new Thread(new Runnable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    Executors.SimpleThreadFactory.this.lambda$newThread$0$Executors$SimpleThreadFactory(this.f$1);
                }
            }, this.mNamePrefix + this.mCount.incrementAndGet());
        }

        public /* synthetic */ void lambda$newThread$0$Executors$SimpleThreadFactory(Runnable runnable) {
            Process.setThreadPriority(this.mPriority);
            runnable.run();
        }
    }
}
