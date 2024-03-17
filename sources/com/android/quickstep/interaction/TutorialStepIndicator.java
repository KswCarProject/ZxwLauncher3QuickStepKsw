package com.android.quickstep.interaction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.content.res.AppCompatResources;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.GraphicsUtils;

public class TutorialStepIndicator extends LinearLayout {
    private static final String LOG_TAG = "TutorialStepIndicator";
    private int mCurrentStep = -1;
    private int mTotalSteps = -1;

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.generateLayoutParams(layoutParams);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public TutorialStepIndicator(Context context) {
        super(context);
    }

    public TutorialStepIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TutorialStepIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TutorialStepIndicator(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setTutorialProgress(int i, int i2) {
        if (i <= 0) {
            Log.w(LOG_TAG, "Current step number invalid: " + i + ". Assuming step 1.");
            i = 1;
        }
        if (i2 <= 0) {
            Log.w(LOG_TAG, "Total number of steps invalid: " + i2 + ". Assuming 1 step.");
            i2 = 1;
        }
        if (i > i2) {
            Log.w(LOG_TAG, "Current step number greater than the total number of steps. Assuming final step.");
            i = i2;
        }
        if (i2 < 2) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        this.mCurrentStep = i;
        this.mTotalSteps = i2;
        initializeStepIndicators();
    }

    private void initializeStepIndicators() {
        for (int i = this.mTotalSteps; i < getChildCount(); i++) {
            removeViewAt(i);
        }
        int attrColor = GraphicsUtils.getAttrColor(getContext(), 16842806);
        int attrColor2 = GraphicsUtils.getAttrColor(getContext(), 16842810);
        for (int i2 = 0; i2 < this.mTotalSteps; i2++) {
            Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.tutorial_step_indicator_pill);
            if (i2 >= getChildCount()) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageDrawable(drawable);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                layoutParams.setMarginStart(Utilities.dpToPx(3.0f));
                layoutParams.setMarginEnd(Utilities.dpToPx(3.0f));
                addView(imageView, layoutParams);
            }
            if (drawable != null) {
                if (i2 < this.mCurrentStep) {
                    drawable.setTint(attrColor);
                } else {
                    drawable.setTint(attrColor2);
                }
            }
        }
    }
}
