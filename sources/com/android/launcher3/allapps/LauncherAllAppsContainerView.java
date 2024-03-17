package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowInsets;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;

public class LauncherAllAppsContainerView extends ActivityAllAppsContainerView<Launcher> {
    public LauncherAllAppsContainerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public LauncherAllAppsContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LauncherAllAppsContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (((Launcher) this.mActivityContext).isInState(LauncherState.ALL_APPS)) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        this.mTouchHandler = null;
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!((Launcher) this.mActivityContext).isInState(LauncherState.ALL_APPS)) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public int getNavBarScrimHeight(WindowInsets windowInsets) {
        if (Utilities.ATLEAST_Q) {
            return windowInsets.getTappableElementInsets().bottom;
        }
        return windowInsets.getStableInsetBottom();
    }
}
