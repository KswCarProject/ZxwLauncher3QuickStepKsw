package com.android.launcher3.icons.cache;

import android.os.Handler;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HandlerRunnable<T> implements Runnable {
    private final Consumer<T> mCallback;
    private final Executor mCallbackExecutor;
    private boolean mCanceled;
    private final Runnable mEndRunnable;
    private boolean mEnded;
    private final Supplier<T> mTask;
    private final Handler mWorkerHandler;

    static /* synthetic */ void lambda$new$0() {
    }

    public HandlerRunnable(Handler handler, Supplier<T> supplier, Executor executor, Consumer<T> consumer) {
        this(handler, supplier, executor, consumer, $$Lambda$HandlerRunnable$ocDo_3GlQlGxLLEvO0m1JeQtA.INSTANCE);
    }

    public HandlerRunnable(Handler handler, Supplier<T> supplier, Executor executor, Consumer<T> consumer, Runnable runnable) {
        this.mEnded = false;
        this.mCanceled = false;
        this.mWorkerHandler = handler;
        this.mTask = supplier;
        this.mCallbackExecutor = executor;
        this.mCallback = consumer;
        this.mEndRunnable = runnable;
    }

    public void cancel() {
        this.mWorkerHandler.removeCallbacks(this);
        this.mCanceled = true;
        this.mCallbackExecutor.execute(new Runnable() {
            public final void run() {
                HandlerRunnable.this.onEnd();
            }
        });
    }

    public void run() {
        this.mCallbackExecutor.execute(new Runnable(this.mTask.get()) {
            public final /* synthetic */ Object f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                HandlerRunnable.this.lambda$run$1$HandlerRunnable(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$run$1$HandlerRunnable(Object obj) {
        if (!this.mCanceled) {
            this.mCallback.accept(obj);
        }
        onEnd();
    }

    /* access modifiers changed from: private */
    public void onEnd() {
        if (!this.mEnded) {
            this.mEnded = true;
            this.mEndRunnable.run();
        }
    }
}
