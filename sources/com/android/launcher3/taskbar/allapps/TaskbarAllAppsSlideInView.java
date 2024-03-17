package com.android.launcher3.taskbar.allapps;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.views.AbstractSlideInView;
import java.util.Optional;

public class TaskbarAllAppsSlideInView extends AbstractSlideInView<TaskbarAllAppsContext> implements Insettable, DeviceProfile.OnDeviceProfileChangeListener {
    private TaskbarAllAppsContainerView mAppsView;
    private AbstractSlideInView.OnCloseListener mOnCloseBeginListener;
    private float mShiftRange;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 131072) != 0;
    }

    public TaskbarAllAppsSlideInView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskbarAllAppsSlideInView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: package-private */
    public void show(boolean z) {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mIsOpen = true;
            attachToContainer();
            if (z) {
                this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
                this.mOpenCloseAnimator.setInterpolator(Interpolators.AGGRESSIVE_EASE);
                this.mOpenCloseAnimator.setDuration((long) LauncherState.ALL_APPS.getTransitionDuration((TaskbarAllAppsContext) this.mActivityContext, true)).start();
                return;
            }
            this.mTranslationShift = 0.0f;
        }
    }

    /* access modifiers changed from: package-private */
    public TaskbarAllAppsContainerView getAppsView() {
        return this.mAppsView;
    }

    /* access modifiers changed from: package-private */
    public void setOnCloseBeginListener(AbstractSlideInView.OnCloseListener onCloseListener) {
        this.mOnCloseBeginListener = onCloseListener;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        Optional.ofNullable(this.mOnCloseBeginListener).ifPresent($$Lambda$DZmqG_xiGvF3FuaigAeeojgrqBU.INSTANCE);
        handleClose(z, (long) LauncherState.ALL_APPS.getTransitionDuration((TaskbarAllAppsContext) this.mActivityContext, false));
    }

    /* access modifiers changed from: protected */
    public Interpolator getIdleInterpolator() {
        return com.android.systemui.animation.Interpolators.EMPHASIZED_ACCELERATE;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        TaskbarAllAppsContainerView taskbarAllAppsContainerView = (TaskbarAllAppsContainerView) findViewById(R.id.apps_view);
        this.mAppsView = taskbarAllAppsContainerView;
        this.mContent = taskbarAllAppsContainerView;
        setShiftRange((float) ((TaskbarAllAppsContext) this.mActivityContext).getDeviceProfile().allAppsShiftRange);
        ((TaskbarAllAppsContext) this.mActivityContext).addOnDeviceProfileChangeListener(this);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setTranslationShift(this.mTranslationShift);
    }

    /* access modifiers changed from: protected */
    public int getScrimColor(Context context) {
        return context.getColor(R.color.widgets_picker_scrim);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mNoIntercept = !this.mAppsView.shouldContainerScroll(motionEvent);
        }
        return super.onControllerInterceptTouchEvent(motionEvent);
    }

    public void setInsets(Rect rect) {
        this.mAppsView.setInsets(rect);
    }

    public void onDeviceProfileChanged(DeviceProfile deviceProfile) {
        setShiftRange((float) deviceProfile.allAppsShiftRange);
        setTranslationShift(0.0f);
    }

    private void setShiftRange(float f) {
        this.mShiftRange = f;
    }

    /* access modifiers changed from: protected */
    public float getShiftRange() {
        return this.mShiftRange;
    }

    /* access modifiers changed from: protected */
    public boolean isEventOverContent(MotionEvent motionEvent) {
        return getPopupContainer().isEventOverView(this.mAppsView.getVisibleContainerView(), motionEvent);
    }
}
