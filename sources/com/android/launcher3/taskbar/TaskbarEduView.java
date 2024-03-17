package com.android.launcher3.taskbar;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.launcher3.Insettable;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.taskbar.TaskbarEduController;
import com.android.launcher3.views.AbstractSlideInView;

public class TaskbarEduView extends AbstractSlideInView<TaskbarActivityContext> implements Insettable {
    private static final int DEFAULT_CLOSE_DURATION = 200;
    private static final int DEFAULT_OPEN_DURATION = 500;
    private Button mEndButton;
    private final Rect mInsets;
    private TaskbarEduPagedView mPagedView;
    private Button mStartButton;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 65536) != 0;
    }

    public TaskbarEduView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskbarEduView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mInsets = new Rect();
    }

    /* access modifiers changed from: protected */
    public void init(TaskbarEduController.TaskbarEduCallbacks taskbarEduCallbacks) {
        TaskbarEduPagedView taskbarEduPagedView = this.mPagedView;
        if (taskbarEduPagedView != null) {
            taskbarEduPagedView.setControllerCallbacks(taskbarEduCallbacks);
        }
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        handleClose(z, 200);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (ViewGroup) findViewById(R.id.edu_view);
        this.mStartButton = (Button) findViewById(R.id.edu_start_button);
        this.mEndButton = (Button) findViewById(R.id.edu_end_button);
        TaskbarEduPagedView taskbarEduPagedView = (TaskbarEduPagedView) findViewById(R.id.content);
        this.mPagedView = taskbarEduPagedView;
        taskbarEduPagedView.setTaskbarEduView(this);
    }

    public void setInsets(Rect rect) {
        this.mInsets.set(rect);
        this.mContent.setPadding(this.mContent.getPaddingStart(), this.mContent.getPaddingTop(), this.mContent.getPaddingEnd(), rect.bottom);
    }

    /* access modifiers changed from: protected */
    public void attachToContainer() {
        if (this.mColorScrim != null) {
            getPopupContainer().addView(this.mColorScrim, 0);
        }
        getPopupContainer().addView(this, 1);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Settings.Secure.putInt(this.mContext.getContentResolver(), "launcher_taskbar_education_showing", 0);
    }

    public void show() {
        attachToContainer();
        animateOpen();
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        String str;
        ViewGroup viewGroup = this.mContent;
        if (this.mIsOpen) {
            str = getContext().getString(R.string.taskbar_edu_opened);
        } else {
            str = getContext().getString(R.string.taskbar_edu_closed);
        }
        return Pair.create(viewGroup, str);
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

    private void animateOpen() {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mIsOpen = true;
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setInterpolator(Interpolators.AGGRESSIVE_EASE);
            this.mOpenCloseAnimator.setDuration(500).start();
        }
    }

    /* access modifiers changed from: package-private */
    public void snapToPage(int i) {
        this.mPagedView.snapToPage(i);
    }

    /* access modifiers changed from: package-private */
    public void updateStartButton(int i, View.OnClickListener onClickListener) {
        this.mStartButton.setText(i);
        this.mStartButton.setOnClickListener(onClickListener);
    }

    /* access modifiers changed from: package-private */
    public void updateEndButton(int i, View.OnClickListener onClickListener) {
        this.mEndButton.setText(i);
        this.mEndButton.setOnClickListener(onClickListener);
    }
}
