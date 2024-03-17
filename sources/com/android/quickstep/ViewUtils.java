package com.android.quickstep;

import android.graphics.HardwareRenderer;
import android.os.Handler;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.launcher3.Utilities;
import com.android.quickstep.ViewUtils;
import java.util.function.BooleanSupplier;

public class ViewUtils {
    static /* synthetic */ boolean lambda$postFrameDrawn$0() {
        return false;
    }

    public static boolean postFrameDrawn(View view, Runnable runnable) {
        return postFrameDrawn(view, runnable, $$Lambda$ViewUtils$4dj86orGtS8qmkpomZDMEi3mL1k.INSTANCE);
    }

    public static boolean postFrameDrawn(View view, Runnable runnable, BooleanSupplier booleanSupplier) {
        return new FrameHandler(view, runnable, booleanSupplier).schedule();
    }

    private static class FrameHandler implements HardwareRenderer.FrameDrawingCallback {
        final BooleanSupplier mCancelled;
        int mDeferFrameCount = 1;
        final Runnable mFinishCallback;
        final Handler mHandler;
        final ViewRootImpl mViewRoot;

        FrameHandler(View view, Runnable runnable, BooleanSupplier booleanSupplier) {
            this.mViewRoot = view.getViewRootImpl();
            this.mFinishCallback = runnable;
            this.mCancelled = booleanSupplier;
            this.mHandler = new Handler();
        }

        public void onFrameDraw(long j) {
            Utilities.postAsyncCallback(this.mHandler, new Runnable() {
                public final void run() {
                    ViewUtils.FrameHandler.this.onFrame();
                }
            });
        }

        /* access modifiers changed from: private */
        public void onFrame() {
            if (!this.mCancelled.getAsBoolean()) {
                int i = this.mDeferFrameCount;
                if (i > 0) {
                    this.mDeferFrameCount = i - 1;
                    schedule();
                    return;
                }
                Runnable runnable = this.mFinishCallback;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }

        /* access modifiers changed from: private */
        public boolean schedule() {
            ViewRootImpl viewRootImpl = this.mViewRoot;
            if (viewRootImpl == null || viewRootImpl.getView() == null) {
                return false;
            }
            this.mViewRoot.registerRtFrameCallback(this);
            this.mViewRoot.getView().invalidate();
            return true;
        }
    }
}
