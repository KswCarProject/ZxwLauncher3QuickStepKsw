package com.android.launcher3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.view.WindowInsets;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.graphics.SysUiScrim;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.util.window.WindowManagerProxy;
import java.util.Collections;
import java.util.List;

public class LauncherRootView extends InsettableFrameLayout {
    @ViewDebug.ExportedProperty(category = "launcher")
    private static final List<Rect> SYSTEM_GESTURE_EXCLUSION_RECT = Collections.singletonList(new Rect());
    private final StatefulActivity mActivity;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mDisallowBackGesture;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mForceHideBackArrow;
    private final SysUiScrim mSysUiScrim;
    private final Rect mTempRect = new Rect();
    private WindowStateListener mWindowStateListener;

    public interface WindowStateListener {
        void onWindowFocusChanged(boolean z);

        void onWindowVisibilityChanged(int i);
    }

    public LauncherRootView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mActivity = (StatefulActivity) StatefulActivity.fromContext(context);
        this.mSysUiScrim = new SysUiScrim(this);
    }

    private void handleSystemWindowInsets(Rect rect) {
        this.mActivity.getDeviceProfile().updateInsets(rect);
        boolean z = !rect.equals(this.mInsets);
        setInsets(rect);
        if (z) {
            this.mActivity.getStateManager().reapplyState(true);
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        WindowInsets normalizeWindowInsets = WindowManagerProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).normalizeWindowInsets(getContext(), windowInsets, this.mTempRect);
        handleSystemWindowInsets(this.mTempRect);
        return normalizeWindowInsets;
    }

    public void setInsets(Rect rect) {
        if (!rect.equals(this.mInsets)) {
            super.setInsets(rect);
            this.mSysUiScrim.onInsetsChanged(rect);
        }
    }

    public void dispatchInsets() {
        this.mActivity.getDeviceProfile().updateInsets(this.mInsets);
        super.setInsets(this.mInsets);
    }

    public void setWindowStateListener(WindowStateListener windowStateListener) {
        this.mWindowStateListener = windowStateListener;
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        WindowStateListener windowStateListener = this.mWindowStateListener;
        if (windowStateListener != null) {
            windowStateListener.onWindowFocusChanged(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        WindowStateListener windowStateListener = this.mWindowStateListener;
        if (windowStateListener != null) {
            windowStateListener.onWindowVisibilityChanged(i);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.mSysUiScrim.draw(canvas);
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        SYSTEM_GESTURE_EXCLUSION_RECT.get(0).set(i, i2, i3, i4);
        setDisallowBackGesture(this.mDisallowBackGesture);
        this.mSysUiScrim.setSize(i3 - i, i4 - i2);
    }

    public void setForceHideBackArrow(boolean z) {
        this.mForceHideBackArrow = z;
        setDisallowBackGesture(this.mDisallowBackGesture);
    }

    public void setDisallowBackGesture(boolean z) {
        List<Rect> list;
        if (Utilities.ATLEAST_Q && !FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get()) {
            this.mDisallowBackGesture = z;
            if (this.mForceHideBackArrow || z) {
                list = SYSTEM_GESTURE_EXCLUSION_RECT;
            } else {
                list = Collections.emptyList();
            }
            setSystemGestureExclusionRects(list);
        }
    }

    public SysUiScrim getSysUiScrim() {
        return this.mSysUiScrim;
    }
}
