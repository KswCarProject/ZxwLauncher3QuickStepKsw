package com.android.launcher3.workprofile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.launcher3.PagedView;

public class PersonalWorkPagedView extends PagedView<PersonalWorkSlidingTabStrip> {
    static final float MAX_SWIPE_ANGLE = 1.0471976f;
    static final float START_DAMPING_TOUCH_SLOP_ANGLE = 0.5235988f;
    static final float TOUCH_SLOP_DAMPING_FACTOR = 4.0f;

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return "";
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public PersonalWorkPagedView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PersonalWorkPagedView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PersonalWorkPagedView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        ((PersonalWorkSlidingTabStrip) this.mPageIndicator).setScroll(i, this.mMaxScroll);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent) {
        float abs = Math.abs(motionEvent.getX() - getDownMotionX());
        float abs2 = Math.abs(motionEvent.getY() - getDownMotionY());
        if (Float.compare(abs, 0.0f) != 0) {
            float atan = (float) Math.atan((double) (abs2 / abs));
            if (abs > ((float) this.mTouchSlop) || abs2 > ((float) this.mTouchSlop)) {
                cancelCurrentPageLongPress();
            }
            if (atan <= MAX_SWIPE_ANGLE) {
                if (atan > START_DAMPING_TOUCH_SLOP_ANGLE) {
                    super.determineScrollingStart(motionEvent, (((float) Math.sqrt((double) ((atan - START_DAMPING_TOUCH_SLOP_ANGLE) / START_DAMPING_TOUCH_SLOP_ANGLE))) * TOUCH_SLOP_DAMPING_FACTOR) + 1.0f);
                } else {
                    super.determineScrollingStart(motionEvent);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean canScroll(float f, float f2) {
        return f2 > f && super.canScroll(f, f2);
    }
}
