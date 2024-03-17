package com.android.quickstep.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.android.quickstep.TouchInteractionService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class TISBindHelper implements ServiceConnection {
    private static final long BACKOFF_MILLIS = 1000;
    private static final long MAX_BACKOFF_MILLIS = 600000;
    private static final String TAG = "TISBindHelper";
    private short mConnectionAttempts;
    private final Consumer<TouchInteractionService.TISBinder> mConnectionCallback;
    private final Runnable mConnectionRunnable = new Runnable() {
        public final void run() {
            TISBindHelper.this.internalBindToTIS();
        }
    };
    private final Context mContext;
    private final Handler mHandler = new Handler();
    private boolean mIsConnected;
    private final ArrayList<Runnable> mPendingConnectedCallbacks = new ArrayList<>();
    private boolean mTisServiceBound;

    public void onServiceDisconnected(ComponentName componentName) {
    }

    public TISBindHelper(Context context, Consumer<TouchInteractionService.TISBinder> consumer) {
        this.mContext = context;
        this.mConnectionCallback = consumer;
        internalBindToTIS();
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (!(iBinder instanceof TouchInteractionService.TISBinder)) {
            internalUnbindToTIS();
            this.mHandler.postDelayed(this.mConnectionRunnable, BACKOFF_MILLIS);
            return;
        }
        Log.d(TAG, "TIS service connected");
        this.mIsConnected = true;
        this.mConnectionCallback.accept((TouchInteractionService.TISBinder) iBinder);
        Iterator<Runnable> it = this.mPendingConnectedCallbacks.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mPendingConnectedCallbacks.clear();
        resetServiceBindRetryState();
    }

    public void onBindingDied(ComponentName componentName) {
        Log.w(TAG, "TIS binding died");
        internalBindToTIS();
    }

    public void runOnBindToTouchInteractionService(Runnable runnable) {
        if (this.mIsConnected) {
            runnable.run();
        } else {
            this.mPendingConnectedCallbacks.add(runnable);
        }
    }

    /* access modifiers changed from: private */
    public void internalBindToTIS() {
        boolean bindService = this.mContext.bindService(new Intent(this.mContext, TouchInteractionService.class), this, 0);
        this.mTisServiceBound = bindService;
        if (bindService) {
            resetServiceBindRetryState();
            return;
        }
        Log.w(TAG, "Retrying TIS Binder connection attempt: " + this.mConnectionAttempts);
        this.mHandler.postDelayed(this.mConnectionRunnable, (long) Math.min(Math.scalb(1000.0f, this.mConnectionAttempts), 600000.0f));
        this.mConnectionAttempts = (short) (this.mConnectionAttempts + 1);
    }

    private void internalUnbindToTIS() {
        if (this.mTisServiceBound) {
            this.mContext.unbindService(this);
            this.mTisServiceBound = false;
        }
    }

    private void resetServiceBindRetryState() {
        if (this.mHandler.hasCallbacks(this.mConnectionRunnable)) {
            this.mHandler.removeCallbacks(this.mConnectionRunnable);
        }
        this.mConnectionAttempts = 0;
    }

    public void onDestroy() {
        internalUnbindToTIS();
        resetServiceBindRetryState();
        this.mIsConnected = false;
        this.mPendingConnectedCallbacks.clear();
    }
}
