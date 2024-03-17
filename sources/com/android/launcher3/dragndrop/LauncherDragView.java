package com.android.launcher3.dragndrop;

import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.statemanager.StateManager;

public class LauncherDragView extends DragView<Launcher> implements StateManager.StateListener<LauncherState> {
    public LauncherDragView(Launcher launcher, Drawable drawable, int i, int i2, float f, float f2, float f3) {
        super(launcher, drawable, i, i2, f, f2, f3);
    }

    public LauncherDragView(Launcher launcher, View view, int i, int i2, int i3, int i4, float f, float f2, float f3) {
        super(launcher, view, i, i2, i3, i4, f, f2, f3);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((Launcher) this.mActivity).getStateManager().addStateListener(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((Launcher) this.mActivity).getStateManager().removeStateListener(this);
    }

    public void onStateTransitionComplete(LauncherState launcherState) {
        setVisibility((launcherState == LauncherState.NORMAL || launcherState == LauncherState.SPRING_LOADED) ? 0 : 4);
    }

    public void animateTo(int i, int i2, Runnable runnable, int i3) {
        this.mTempLoc[0] = i - this.mRegistrationX;
        this.mTempLoc[1] = i2 - this.mRegistrationY;
        ((Launcher) this.mActivity).getDragLayer().animateViewIntoPosition(this, this.mTempLoc, 1.0f, this.mScaleOnDrop, this.mScaleOnDrop, 0, runnable, i3);
    }
}
