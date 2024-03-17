package com.android.launcher3.widget.picker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import com.android.launcher3.R;
import com.android.launcher3.util.Themes;

final class WidgetsListDrawableFactory {
    private final float mMiddleCornerRadius;
    private final ColorStateList mRippleColor;
    private final ColorStateList mSurfaceColor;
    private final float mTopBottomCornerRadius;

    WidgetsListDrawableFactory(Context context) {
        Resources resources = context.getResources();
        this.mTopBottomCornerRadius = resources.getDimension(R.dimen.widget_list_top_bottom_corner_radius);
        this.mMiddleCornerRadius = resources.getDimension(R.dimen.widget_list_content_corner_radius);
        this.mSurfaceColor = context.getColorStateList(R.color.surface);
        this.mRippleColor = ColorStateList.valueOf(Themes.getAttrColor(context, 16843820));
    }

    /* access modifiers changed from: package-private */
    public Drawable createHeaderBackgroundDrawable() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        int[] iArr = WidgetsListDrawableState.SINGLE.mStateSet;
        float f = this.mTopBottomCornerRadius;
        stateListDrawable.addState(iArr, createRoundedRectDrawable(f, f));
        stateListDrawable.addState(WidgetsListDrawableState.FIRST_EXPANDED.mStateSet, createRoundedRectDrawable(this.mTopBottomCornerRadius, 0.0f));
        stateListDrawable.addState(WidgetsListDrawableState.FIRST.mStateSet, createRoundedRectDrawable(this.mTopBottomCornerRadius, this.mMiddleCornerRadius));
        stateListDrawable.addState(WidgetsListDrawableState.MIDDLE_EXPANDED.mStateSet, createRoundedRectDrawable(this.mMiddleCornerRadius, 0.0f));
        int[] iArr2 = WidgetsListDrawableState.MIDDLE.mStateSet;
        float f2 = this.mMiddleCornerRadius;
        stateListDrawable.addState(iArr2, createRoundedRectDrawable(f2, f2));
        stateListDrawable.addState(WidgetsListDrawableState.LAST.mStateSet, createRoundedRectDrawable(this.mMiddleCornerRadius, this.mTopBottomCornerRadius));
        return new RippleDrawable(this.mRippleColor, stateListDrawable, stateListDrawable);
    }

    /* access modifiers changed from: package-private */
    public Drawable createContentBackgroundDrawable() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(WidgetsListDrawableState.MIDDLE.mStateSet, createRoundedRectDrawable(0.0f, this.mMiddleCornerRadius));
        stateListDrawable.addState(WidgetsListDrawableState.LAST.mStateSet, createRoundedRectDrawable(0.0f, this.mTopBottomCornerRadius));
        return new RippleDrawable(this.mRippleColor, stateListDrawable, stateListDrawable);
    }

    private Drawable createRoundedRectDrawable(float f, float f2) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(this.mSurfaceColor);
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadii(new float[]{f, f, f, f, f2, f2, f2, f2});
        return gradientDrawable;
    }
}
