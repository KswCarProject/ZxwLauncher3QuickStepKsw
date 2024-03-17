package com.android.launcher3.graphics;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.BitmapCreationCheck;
import com.android.launcher3.icons.GraphicsUtils;
import java.util.Objects;

public class BitmapCreationCheck {
    public static final boolean ENABLED = false;
    private static final String TAG = "BitmapCreationCheck";

    public static void startTracking(Context context) {
        MyTracker myTracker = new MyTracker();
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(myTracker);
        Objects.requireNonNull(myTracker);
        GraphicsUtils.sOnNewBitmapRunnable = new Runnable() {
            public final void run() {
                BitmapCreationCheck.MyTracker.this.onBitmapCreated();
            }
        };
    }

    private static class MyTracker implements Application.ActivityLifecycleCallbacks, View.OnAttachStateChangeListener {
        /* access modifiers changed from: private */
        public final ThreadLocal<Boolean> mCurrentThreadDrawing;

        public void onActivityDestroyed(Activity activity) {
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }

        public void onViewDetachedFromWindow(View view) {
        }

        private MyTracker() {
            this.mCurrentThreadDrawing = ThreadLocal.withInitial($$Lambda$BitmapCreationCheck$MyTracker$tZe3Dl8Kp85hFTC2ALBfsccp4K8.INSTANCE);
        }

        static /* synthetic */ Boolean lambda$new$0() {
            return false;
        }

        public void onActivityCreated(Activity activity, Bundle bundle) {
            activity.getWindow().getDecorView().addOnAttachStateChangeListener(this);
        }

        public void onViewAttachedToWindow(View view) {
            view.getViewTreeObserver().addOnDrawListener(new MyViewDrawListener(view.getHandler()));
        }

        private class MyViewDrawListener implements ViewTreeObserver.OnDrawListener, Runnable {
            private final Handler mHandler;

            MyViewDrawListener(Handler handler) {
                this.mHandler = handler;
            }

            public void onDraw() {
                MyTracker.this.mCurrentThreadDrawing.set(true);
                Utilities.postAsyncCallback(this.mHandler, this);
            }

            public void run() {
                MyTracker.this.mCurrentThreadDrawing.set(false);
            }
        }

        /* access modifiers changed from: private */
        public void onBitmapCreated() {
            if (this.mCurrentThreadDrawing.get().booleanValue()) {
                Log.e(BitmapCreationCheck.TAG, "Bitmap created during draw pass", new Exception());
            }
        }
    }
}
