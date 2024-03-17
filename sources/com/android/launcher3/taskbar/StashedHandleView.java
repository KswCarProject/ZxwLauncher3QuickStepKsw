package com.android.launcher3.taskbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;

public class StashedHandleView extends View {
    private static final long COLOR_CHANGE_DURATION = 120;
    /* access modifiers changed from: private */
    public ObjectAnimator mColorChangeAnim;
    private final Rect mSampledRegion;
    private final int mStashedHandleDarkColor;
    private final int mStashedHandleLightColor;
    private final int[] mTmpArr;

    public StashedHandleView(Context context) {
        this(context, (AttributeSet) null);
    }

    public StashedHandleView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StashedHandleView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public StashedHandleView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mSampledRegion = new Rect();
        this.mTmpArr = new int[2];
        this.mStashedHandleLightColor = ContextCompat.getColor(context, R.color.taskbar_stashed_handle_light_color);
        this.mStashedHandleDarkColor = ContextCompat.getColor(context, R.color.taskbar_stashed_handle_dark_color);
    }

    public void updateSampledRegion(Rect rect) {
        getLocationOnScreen(this.mTmpArr);
        this.mSampledRegion.set(rect);
        Rect rect2 = this.mSampledRegion;
        int[] iArr = this.mTmpArr;
        rect2.offset(iArr[0], iArr[1]);
    }

    public Rect getSampledRegion() {
        return this.mSampledRegion;
    }

    public void updateHandleColor(boolean z, boolean z2) {
        int i = z ? this.mStashedHandleLightColor : this.mStashedHandleDarkColor;
        ObjectAnimator objectAnimator = this.mColorChangeAnim;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (z2) {
            ObjectAnimator ofArgb = ObjectAnimator.ofArgb(this, LauncherAnimUtils.VIEW_BACKGROUND_COLOR, new int[]{i});
            this.mColorChangeAnim = ofArgb;
            ofArgb.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ObjectAnimator unused = StashedHandleView.this.mColorChangeAnim = null;
                }
            });
            this.mColorChangeAnim.setDuration(120);
            this.mColorChangeAnim.start();
            return;
        }
        setBackgroundColor(i);
    }
}
