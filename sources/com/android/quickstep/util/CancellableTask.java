package com.android.quickstep.util;

import com.android.launcher3.util.Executors;

public abstract class CancellableTask<T> implements Runnable {
    private boolean mCancelled = false;

    public abstract T getResultOnBg();

    public abstract void handleResult(T t);

    public final void run() {
        if (!this.mCancelled) {
            Object resultOnBg = getResultOnBg();
            if (!this.mCancelled) {
                Executors.MAIN_EXECUTOR.execute(new Runnable(resultOnBg) {
                    public final /* synthetic */ Object f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        CancellableTask.this.lambda$run$0$CancellableTask(this.f$1);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$run$0$CancellableTask(Object obj) {
        if (!this.mCancelled) {
            handleResult(obj);
        }
    }

    public void cancel() {
        this.mCancelled = true;
    }
}
