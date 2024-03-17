package com.android.systemui.shared.navigationbar;

import android.graphics.Rect;
import android.os.Handler;
import android.view.CompositionSamplingListener;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.Executor;

public class RegionSamplingHelper implements View.OnAttachStateChangeListener, View.OnLayoutChangeListener {
    private static final float NAVIGATION_LUMINANCE_CHANGE_THRESHOLD = 0.05f;
    private static final float NAVIGATION_LUMINANCE_THRESHOLD = 0.5f;
    private final Executor mBackgroundExecutor;
    private final SamplingCallback mCallback;
    private final SysuiCompositionSamplingListener mCompositionSamplingListener;
    private float mCurrentMedianLuma;
    private boolean mFirstSamplingAfterStart;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private boolean mIsDestroyed;
    private float mLastMedianLuma;
    private final Rect mRegisteredSamplingBounds;
    private SurfaceControl mRegisteredStopLayer;
    /* access modifiers changed from: private */
    public Runnable mRemoveDrawRunnable;
    /* access modifiers changed from: private */
    public final View mSampledView;
    /* access modifiers changed from: private */
    public boolean mSamplingEnabled;
    private final CompositionSamplingListener mSamplingListener;
    private boolean mSamplingListenerRegistered;
    private final Rect mSamplingRequestBounds;
    /* access modifiers changed from: private */
    public ViewTreeObserver.OnDrawListener mUpdateOnDraw;
    private boolean mWaitingOnDraw;
    private boolean mWindowHasBlurs;
    private boolean mWindowVisible;
    private SurfaceControl mWrappedStopLayer;

    public interface SamplingCallback {
        Rect getSampledRegion(View view);

        boolean isSamplingEnabled() {
            return true;
        }

        void onRegionDarknessChanged(boolean z);
    }

    public RegionSamplingHelper(View view, SamplingCallback samplingCallback, Executor executor) {
        this(view, samplingCallback, view.getContext().getMainExecutor(), executor);
    }

    public RegionSamplingHelper(View view, SamplingCallback samplingCallback, Executor executor, Executor executor2) {
        this(view, samplingCallback, executor, executor2, new SysuiCompositionSamplingListener());
    }

    RegionSamplingHelper(View view, SamplingCallback samplingCallback, Executor executor, Executor executor2, SysuiCompositionSamplingListener sysuiCompositionSamplingListener) {
        this.mHandler = new Handler();
        this.mSamplingRequestBounds = new Rect();
        this.mRegisteredSamplingBounds = new Rect();
        this.mSamplingEnabled = false;
        this.mSamplingListenerRegistered = false;
        this.mRegisteredStopLayer = null;
        this.mWrappedStopLayer = null;
        this.mUpdateOnDraw = new ViewTreeObserver.OnDrawListener() {
            public void onDraw() {
                RegionSamplingHelper.this.mHandler.post(RegionSamplingHelper.this.mRemoveDrawRunnable);
                RegionSamplingHelper.this.onDraw();
            }
        };
        this.mRemoveDrawRunnable = new Runnable() {
            public void run() {
                RegionSamplingHelper.this.mSampledView.getViewTreeObserver().removeOnDrawListener(RegionSamplingHelper.this.mUpdateOnDraw);
            }
        };
        this.mBackgroundExecutor = executor2;
        this.mCompositionSamplingListener = sysuiCompositionSamplingListener;
        this.mSamplingListener = new CompositionSamplingListener(executor) {
            public void onSampleCollected(float f) {
                if (RegionSamplingHelper.this.mSamplingEnabled) {
                    RegionSamplingHelper.this.updateMediaLuma(f);
                }
            }
        };
        this.mSampledView = view;
        view.addOnAttachStateChangeListener(this);
        view.addOnLayoutChangeListener(this);
        this.mCallback = samplingCallback;
    }

    /* access modifiers changed from: private */
    public void onDraw() {
        if (this.mWaitingOnDraw) {
            this.mWaitingOnDraw = false;
            updateSamplingListener();
        }
    }

    public void start(Rect rect) {
        if (this.mCallback.isSamplingEnabled()) {
            if (rect != null) {
                this.mSamplingRequestBounds.set(rect);
            }
            this.mSamplingEnabled = true;
            this.mLastMedianLuma = -1.0f;
            this.mFirstSamplingAfterStart = true;
            updateSamplingListener();
        }
    }

    public void stop() {
        this.mSamplingEnabled = false;
        updateSamplingListener();
    }

    public void stopAndDestroy() {
        stop();
        Executor executor = this.mBackgroundExecutor;
        CompositionSamplingListener compositionSamplingListener = this.mSamplingListener;
        Objects.requireNonNull(compositionSamplingListener);
        executor.execute(new Runnable(compositionSamplingListener) {
            public final /* synthetic */ CompositionSamplingListener f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.destroy();
            }
        });
        this.mIsDestroyed = true;
    }

    public void onViewAttachedToWindow(View view) {
        updateSamplingListener();
    }

    public void onViewDetachedFromWindow(View view) {
        stopAndDestroy();
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateSamplingRect();
    }

    private void updateSamplingListener() {
        if (this.mSamplingEnabled && !this.mSamplingRequestBounds.isEmpty() && this.mWindowVisible && !this.mWindowHasBlurs && (this.mSampledView.isAttachedToWindow() || this.mFirstSamplingAfterStart)) {
            ViewRootImpl viewRootImpl = this.mSampledView.getViewRootImpl();
            SurfaceControl surfaceControl = null;
            SurfaceControl surfaceControl2 = viewRootImpl != null ? viewRootImpl.getSurfaceControl() : null;
            if (surfaceControl2 != null && surfaceControl2.isValid()) {
                surfaceControl = surfaceControl2;
            } else if (!this.mWaitingOnDraw) {
                this.mWaitingOnDraw = true;
                if (this.mHandler.hasCallbacks(this.mRemoveDrawRunnable)) {
                    this.mHandler.removeCallbacks(this.mRemoveDrawRunnable);
                } else {
                    this.mSampledView.getViewTreeObserver().addOnDrawListener(this.mUpdateOnDraw);
                }
            }
            if (!this.mSamplingRequestBounds.equals(this.mRegisteredSamplingBounds) || this.mRegisteredStopLayer != surfaceControl) {
                unregisterSamplingListener();
                this.mSamplingListenerRegistered = true;
                SurfaceControl wrap = wrap(surfaceControl);
                this.mBackgroundExecutor.execute(new Runnable(wrap) {
                    public final /* synthetic */ SurfaceControl f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        RegionSamplingHelper.this.lambda$updateSamplingListener$0$RegionSamplingHelper(this.f$1);
                    }
                });
                this.mRegisteredSamplingBounds.set(this.mSamplingRequestBounds);
                this.mRegisteredStopLayer = surfaceControl;
                this.mWrappedStopLayer = wrap;
            }
            this.mFirstSamplingAfterStart = false;
            return;
        }
        unregisterSamplingListener();
    }

    public /* synthetic */ void lambda$updateSamplingListener$0$RegionSamplingHelper(SurfaceControl surfaceControl) {
        if (surfaceControl == null || surfaceControl.isValid()) {
            this.mCompositionSamplingListener.register(this.mSamplingListener, 0, surfaceControl, this.mSamplingRequestBounds);
        }
    }

    /* access modifiers changed from: protected */
    public SurfaceControl wrap(SurfaceControl surfaceControl) {
        if (surfaceControl == null) {
            return null;
        }
        return new SurfaceControl(surfaceControl, "regionSampling");
    }

    private void unregisterSamplingListener() {
        if (this.mSamplingListenerRegistered) {
            this.mSamplingListenerRegistered = false;
            SurfaceControl surfaceControl = this.mWrappedStopLayer;
            this.mRegisteredStopLayer = null;
            this.mWrappedStopLayer = null;
            this.mRegisteredSamplingBounds.setEmpty();
            this.mBackgroundExecutor.execute(new Runnable(surfaceControl) {
                public final /* synthetic */ SurfaceControl f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RegionSamplingHelper.this.lambda$unregisterSamplingListener$1$RegionSamplingHelper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$unregisterSamplingListener$1$RegionSamplingHelper(SurfaceControl surfaceControl) {
        this.mCompositionSamplingListener.unregister(this.mSamplingListener);
        if (surfaceControl != null && surfaceControl.isValid()) {
            surfaceControl.release();
        }
    }

    /* access modifiers changed from: private */
    public void updateMediaLuma(float f) {
        this.mCurrentMedianLuma = f;
        if (Math.abs(f - this.mLastMedianLuma) > NAVIGATION_LUMINANCE_CHANGE_THRESHOLD) {
            this.mCallback.onRegionDarknessChanged(f < 0.5f);
            this.mLastMedianLuma = f;
        }
    }

    public void updateSamplingRect() {
        Rect sampledRegion = this.mCallback.getSampledRegion(this.mSampledView);
        if (!this.mSamplingRequestBounds.equals(sampledRegion)) {
            this.mSamplingRequestBounds.set(sampledRegion);
            updateSamplingListener();
        }
    }

    public void setWindowVisible(boolean z) {
        this.mWindowVisible = z;
        updateSamplingListener();
    }

    public void setWindowHasBlurs(boolean z) {
        this.mWindowHasBlurs = z;
        updateSamplingListener();
    }

    public void dump(PrintWriter printWriter) {
        dump("", printWriter);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "RegionSamplingHelper:");
        printWriter.println(str + "\tsampleView isAttached: " + this.mSampledView.isAttachedToWindow());
        printWriter.println(str + "\tsampleView isScValid: " + (this.mSampledView.isAttachedToWindow() ? Boolean.valueOf(this.mSampledView.getViewRootImpl().getSurfaceControl().isValid()) : "notAttached"));
        printWriter.println(str + "\tmSamplingEnabled: " + this.mSamplingEnabled);
        printWriter.println(str + "\tmSamplingListenerRegistered: " + this.mSamplingListenerRegistered);
        printWriter.println(str + "\tmSamplingRequestBounds: " + this.mSamplingRequestBounds);
        printWriter.println(str + "\tmRegisteredSamplingBounds: " + this.mRegisteredSamplingBounds);
        printWriter.println(str + "\tmLastMedianLuma: " + this.mLastMedianLuma);
        printWriter.println(str + "\tmCurrentMedianLuma: " + this.mCurrentMedianLuma);
        printWriter.println(str + "\tmWindowVisible: " + this.mWindowVisible);
        printWriter.println(str + "\tmWindowHasBlurs: " + this.mWindowHasBlurs);
        printWriter.println(str + "\tmWaitingOnDraw: " + this.mWaitingOnDraw);
        printWriter.println(str + "\tmRegisteredStopLayer: " + this.mRegisteredStopLayer);
        printWriter.println(str + "\tmWrappedStopLayer: " + this.mWrappedStopLayer);
        printWriter.println(str + "\tmIsDestroyed: " + this.mIsDestroyed);
    }

    public static class SysuiCompositionSamplingListener {
        public void register(CompositionSamplingListener compositionSamplingListener, int i, SurfaceControl surfaceControl, Rect rect) {
            CompositionSamplingListener.register(compositionSamplingListener, i, surfaceControl, rect);
        }

        public void unregister(CompositionSamplingListener compositionSamplingListener) {
            CompositionSamplingListener.unregister(compositionSamplingListener);
        }
    }
}
