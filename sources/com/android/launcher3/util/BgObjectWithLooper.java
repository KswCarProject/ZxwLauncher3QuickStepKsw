package com.android.launcher3.util;

import android.os.Looper;

public abstract class BgObjectWithLooper {
    /* access modifiers changed from: protected */
    public abstract void onInitialized(Looper looper);

    public final void initializeInBackground(String str) {
        new Thread(new Runnable() {
            public final void run() {
                BgObjectWithLooper.this.runOnThread();
            }
        }, str).start();
    }

    /* access modifiers changed from: private */
    public void runOnThread() {
        Looper.prepare();
        onInitialized(Looper.myLooper());
        Looper.loop();
    }
}
