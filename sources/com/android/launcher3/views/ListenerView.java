package com.android.launcher3.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import com.android.launcher3.AbstractFloatingView;

public class ListenerView extends AbstractFloatingView {
    private Runnable mCloseListener;

    public boolean canInterceptEventsInSystemGestureRegion() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 256) != 0;
    }

    public ListenerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setVisibility(8);
    }

    public void setListener(Runnable runnable) {
        this.mCloseListener = runnable;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsOpen = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsOpen = false;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        if (this.mIsOpen) {
            Runnable runnable = this.mCloseListener;
            if (runnable != null) {
                runnable.run();
            } else if (getParent() instanceof ViewGroup) {
                ((ViewGroup) getParent()).removeView(this);
            }
        }
        this.mIsOpen = false;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            handleClose(false);
        }
        return false;
    }
}
