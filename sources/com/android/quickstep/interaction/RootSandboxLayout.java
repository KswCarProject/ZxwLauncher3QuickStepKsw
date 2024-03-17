package com.android.quickstep.interaction;

import android.content.Context;
import android.graphics.Insets;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.WindowInsets;
import android.widget.RelativeLayout;
import androidx.fragment.app.FragmentManager;

public class RootSandboxLayout extends RelativeLayout {
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public RootSandboxLayout(Context context) {
        super(context);
    }

    public RootSandboxLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RootSandboxLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return ((TutorialFragment) FragmentManager.findFragment(this)).onInterceptTouch(motionEvent);
    }

    public int getFullscreenHeight() {
        Insets insets = getRootWindowInsets().getInsets(WindowInsets.Type.systemBars());
        return getHeight() + insets.top + insets.bottom;
    }
}
