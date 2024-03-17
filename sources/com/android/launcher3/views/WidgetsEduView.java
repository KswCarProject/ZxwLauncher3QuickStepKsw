package com.android.launcher3.views;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;

public class WidgetsEduView extends AbstractSlideInView<Launcher> implements Insettable {
    private static final int DEFAULT_CLOSE_DURATION = 200;
    private Rect mInsets;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 32768) != 0;
    }

    public WidgetsEduView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetsEduView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mInsets = new Rect();
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        handleClose(true, 200);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (ViewGroup) findViewById(R.id.edu_view);
        findViewById(R.id.edu_close_button).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WidgetsEduView.this.lambda$onFinishInflate$0$WidgetsEduView(view);
            }
        });
    }

    public /* synthetic */ void lambda$onFinishInflate$0$WidgetsEduView(View view) {
        close(true);
    }

    public void setInsets(Rect rect) {
        this.mInsets.set(rect);
        this.mContent.setPadding(this.mContent.getPaddingStart(), this.mContent.getPaddingTop(), this.mContent.getPaddingEnd(), rect.bottom);
    }

    private void show() {
        attachToContainer();
        animateOpen();
    }

    /* access modifiers changed from: protected */
    public int getScrimColor(Context context) {
        return context.getResources().getColor(R.color.widgets_picker_scrim);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int measuredWidth = this.mContent.getMeasuredWidth();
        int i6 = (((((i3 - i) - measuredWidth) - this.mInsets.left) - this.mInsets.right) / 2) + this.mInsets.left;
        this.mContent.layout(i6, i5 - this.mContent.getMeasuredHeight(), measuredWidth + i6, i5);
        setTranslationShift(this.mTranslationShift);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        DeviceProfile deviceProfile = ((Launcher) this.mActivityContext).getDeviceProfile();
        if (this.mInsets.bottom > 0) {
            i3 = this.mInsets.left + this.mInsets.right;
        } else {
            Rect rect = deviceProfile.workspacePadding;
            i3 = Math.max(rect.left + rect.right, (this.mInsets.left + this.mInsets.right) * 2);
        }
        measureChildWithMargins(this.mContent, i, i3, i2, this.mInsets.top + deviceProfile.edgeMarginPx);
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
    }

    private void animateOpen() {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mIsOpen = true;
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            this.mOpenCloseAnimator.start();
        }
    }

    public static WidgetsEduView showEducationDialog(Launcher launcher) {
        WidgetsEduView widgetsEduView = (WidgetsEduView) LayoutInflater.from(launcher).inflate(R.layout.widgets_edu, launcher.getDragLayer(), false);
        widgetsEduView.show();
        return widgetsEduView;
    }
}
