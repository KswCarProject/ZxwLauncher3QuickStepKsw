package com.android.quickstep.util;

import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class BinderTracker {
    private static final String TAG = "BinderTracker";

    public static void start() {
        Log.wtf(TAG, "Accessing tracker in released code.", new Exception());
    }

    public static void stop() {
        Log.wtf(TAG, "Accessing tracker in released code.", new Exception());
    }

    private static class Tracker implements Binder.ProxyTransactListener {
        public void onTransactEnded(Object obj) {
        }

        private Tracker() {
        }

        public Object onTransactStarted(IBinder iBinder, int i) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                return null;
            }
            Log.e(BinderTracker.TAG, "Binder call on ui thread", new Exception());
            return null;
        }
    }
}
