package com.android.launcher3.taskbar;

import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.util.TouchController;
import java.util.function.Supplier;

public class RecentsHitboxExtender implements TouchController {
    private static final int RECENTS_HITBOX_TIMEOUT_MS = 500;
    private boolean mAnimatingFromTaskbarToOverview;
    private DeviceProfile mDeviceProfile;
    private Handler mHandler;
    private float mLastIconAlignment;
    private Supplier<float[]> mParentCoordSupplier;
    private View mRecentsButton;
    private boolean mRecentsButtonClicked;
    private final Rect mRecentsHitBox = new Rect();
    private final Runnable mRecentsHitboxResetRunnable = new Runnable() {
        public final void run() {
            RecentsHitboxExtender.this.reset();
        }
    };
    private View mRecentsParent;
    private TouchDelegate mRecentsTouchDelegate;

    public void init(View view, View view2, DeviceProfile deviceProfile, Supplier<float[]> supplier, Handler handler) {
        this.mRecentsButton = view;
        this.mRecentsParent = view2;
        this.mDeviceProfile = deviceProfile;
        this.mParentCoordSupplier = supplier;
        this.mHandler = handler;
    }

    public void onRecentsButtonClicked() {
        this.mRecentsButtonClicked = true;
    }

    public void onAnimationProgressToOverview(float f) {
        int i = (f > 1.0f ? 1 : (f == 1.0f ? 0 : -1));
        if (i == 0 || f == 0.0f) {
            this.mLastIconAlignment = f;
            if (this.mAnimatingFromTaskbarToOverview) {
                if (i == 0) {
                    this.mHandler.postDelayed(this.mRecentsHitboxResetRunnable, 500);
                    return;
                } else {
                    this.mHandler.removeCallbacks(this.mRecentsHitboxResetRunnable);
                    reset();
                }
            }
        }
        if (!this.mAnimatingFromTaskbarToOverview && f > 0.0f && this.mLastIconAlignment == 0.0f && this.mRecentsButtonClicked) {
            this.mAnimatingFromTaskbarToOverview = true;
            float[] fArr = this.mParentCoordSupplier.get();
            int i2 = (int) fArr[0];
            int i3 = (int) fArr[1];
            this.mRecentsHitBox.set(i2, i3, this.mRecentsButton.getWidth() + i2, this.mRecentsButton.getHeight() + i3 + this.mDeviceProfile.getTaskbarOffsetY());
            TouchDelegate touchDelegate = new TouchDelegate(this.mRecentsHitBox, this.mRecentsButton);
            this.mRecentsTouchDelegate = touchDelegate;
            this.mRecentsParent.setTouchDelegate(touchDelegate);
        }
    }

    /* access modifiers changed from: private */
    public void reset() {
        this.mAnimatingFromTaskbarToOverview = false;
        this.mRecentsButton.setTouchDelegate((TouchDelegate) null);
        this.mRecentsHitBox.setEmpty();
        this.mRecentsButtonClicked = false;
    }

    public boolean extendedHitboxEnabled() {
        return this.mAnimatingFromTaskbarToOverview;
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        return this.mRecentsTouchDelegate.onTouchEvent(motionEvent);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mRecentsHitBox.contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }
}
