package com.android.launcher3.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.GestureNavContract;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.window.RefreshRateTracker;

public class FloatingSurfaceView extends AbstractFloatingView implements ViewTreeObserver.OnGlobalLayoutListener, Insettable, SurfaceHolder.Callback2 {
    private GestureNavContract mContract;
    private View mIcon;
    private final Rect mIconBounds;
    private final RectF mIconPosition;
    private final Launcher mLauncher;
    private final Picture mPicture;
    private final Runnable mRemoveViewRunnable;
    private final SurfaceView mSurfaceView;
    private final RectF mTmpPosition;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 8192) != 0;
    }

    public void setInsets(Rect rect) {
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    public FloatingSurfaceView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FloatingSurfaceView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingSurfaceView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTmpPosition = new RectF();
        this.mIconPosition = new RectF();
        this.mIconBounds = new Rect();
        this.mPicture = new Picture();
        this.mRemoveViewRunnable = new Runnable() {
            public final void run() {
                FloatingSurfaceView.this.removeViewFromParent();
            }
        };
        this.mLauncher = Launcher.getLauncher(context);
        SurfaceView surfaceView = new SurfaceView(context);
        this.mSurfaceView = surfaceView;
        surfaceView.setZOrderOnTop(true);
        surfaceView.getHolder().setFormat(-3);
        surfaceView.getHolder().addCallback(this);
        this.mIsOpen = true;
        addView(surfaceView);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        setCurrentIconVisible(true);
        this.mLauncher.getViewCache().recycleView(R.layout.floating_surface_view, this);
        this.mContract = null;
        this.mIcon = null;
        this.mIsOpen = false;
        Executors.MAIN_EXECUTOR.getHandler().postDelayed(this.mRemoveViewRunnable, (long) RefreshRateTracker.getSingleFrameMs(this.mLauncher));
    }

    /* access modifiers changed from: private */
    public void removeViewFromParent() {
        this.mPicture.beginRecording(1, 1);
        this.mPicture.endRecording();
        this.mLauncher.getDragLayer().removeViewInLayout(this);
    }

    private void removeViewImmediate() {
        Executors.MAIN_EXECUTOR.getHandler().removeCallbacks(this.mRemoveViewRunnable);
        removeViewFromParent();
    }

    public static void show(Launcher launcher, GestureNavContract gestureNavContract) {
        FloatingSurfaceView floatingSurfaceView = (FloatingSurfaceView) launcher.getViewCache().getView(R.layout.floating_surface_view, launcher, launcher.getDragLayer());
        floatingSurfaceView.mContract = gestureNavContract;
        floatingSurfaceView.mIsOpen = true;
        floatingSurfaceView.removeViewImmediate();
        launcher.getDragLayer().addView(floatingSurfaceView);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        close(false);
        removeViewImmediate();
        return false;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
        updateIconLocation();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        setCurrentIconVisible(true);
    }

    public void onGlobalLayout() {
        updateIconLocation();
    }

    private void updateIconLocation() {
        GestureNavContract gestureNavContract = this.mContract;
        if (gestureNavContract != null) {
            View firstMatchForAppClose = this.mLauncher.getFirstMatchForAppClose(-1, gestureNavContract.componentName.getPackageName(), this.mContract.user, false);
            boolean z = this.mIcon != firstMatchForAppClose;
            if (z) {
                setCurrentIconVisible(true);
                this.mIcon = firstMatchForAppClose;
                setCurrentIconVisible(false);
            }
            if (firstMatchForAppClose != null && firstMatchForAppClose.isAttachedToWindow()) {
                FloatingIconView.getLocationBoundsForView(this.mLauncher, firstMatchForAppClose, false, this.mTmpPosition, this.mIconBounds);
                if (!this.mTmpPosition.equals(this.mIconPosition)) {
                    this.mIconPosition.set(this.mTmpPosition);
                    sendIconInfo();
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mSurfaceView.getLayoutParams();
                    layoutParams.width = Math.round(this.mIconPosition.width());
                    layoutParams.height = Math.round(this.mIconPosition.height());
                    layoutParams.leftMargin = Math.round(this.mIconPosition.left);
                    layoutParams.topMargin = Math.round(this.mIconPosition.top);
                }
            }
            if (this.mIcon != null && z && !this.mIconBounds.isEmpty()) {
                setCurrentIconVisible(true);
                Canvas beginRecording = this.mPicture.beginRecording(this.mIconBounds.width(), this.mIconBounds.height());
                beginRecording.translate((float) (-this.mIconBounds.left), (float) (-this.mIconBounds.top));
                this.mIcon.draw(beginRecording);
                this.mPicture.endRecording();
                setCurrentIconVisible(false);
                drawOnSurface();
            }
        }
    }

    private void sendIconInfo() {
        if (this.mContract != null && !this.mIconPosition.isEmpty()) {
            this.mContract.sendEndPosition(this.mIconPosition, this.mLauncher, this.mSurfaceView.getSurfaceControl());
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawOnSurface();
        sendIconInfo();
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        drawOnSurface();
    }

    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
        drawOnSurface();
    }

    private void drawOnSurface() {
        SurfaceHolder holder = this.mSurfaceView.getHolder();
        Canvas lockHardwareCanvas = holder.lockHardwareCanvas();
        if (lockHardwareCanvas != null) {
            this.mPicture.draw(lockHardwareCanvas);
            holder.unlockCanvasAndPost(lockHardwareCanvas);
        }
    }

    private void setCurrentIconVisible(boolean z) {
        View view = this.mIcon;
        if (view != null) {
            IconLabelDotView.setIconAndDotVisible(view, z);
        }
    }
}
