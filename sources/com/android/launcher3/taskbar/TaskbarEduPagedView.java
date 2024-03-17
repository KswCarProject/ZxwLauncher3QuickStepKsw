package com.android.launcher3.taskbar;

import android.content.Context;
import android.util.AttributeSet;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.PagedView;
import com.android.launcher3.R;
import com.android.launcher3.pageindicators.PageIndicatorDots;
import com.android.launcher3.taskbar.TaskbarEduController;
import com.android.launcher3.views.ActivityContext;

public class TaskbarEduPagedView extends PagedView<PageIndicatorDots> {
    private TaskbarEduController.TaskbarEduCallbacks mControllerCallbacks;
    private TaskbarEduView mTaskbarEduView;

    public TaskbarEduPagedView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setImportantForAccessibility(1);
    }

    /* access modifiers changed from: package-private */
    public void setTaskbarEduView(TaskbarEduView taskbarEduView) {
        this.mTaskbarEduView = taskbarEduView;
        this.mPageIndicator = taskbarEduView.findViewById(R.id.content_page_indicator);
        initParentViews(taskbarEduView);
    }

    /* access modifiers changed from: package-private */
    public void setControllerCallbacks(TaskbarEduController.TaskbarEduCallbacks taskbarEduCallbacks) {
        this.mControllerCallbacks = taskbarEduCallbacks;
        taskbarEduCallbacks.onPageChanged(getCurrentPage(), getPageCount());
    }

    /* access modifiers changed from: protected */
    public int getChildGap(int i, int i2) {
        return this.mTaskbarEduView.getPaddingLeft() + this.mTaskbarEduView.getPaddingRight();
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (this.mMaxScroll > 0) {
            ((PageIndicatorDots) this.mPageIndicator).setScroll(i, this.mMaxScroll);
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int i) {
        super.notifyPageSwitchListener(i);
        this.mControllerCallbacks.onPageChanged(getCurrentPage(), getPageCount());
    }

    /* access modifiers changed from: protected */
    public boolean canScroll(float f, float f2) {
        return AbstractFloatingView.getTopOpenViewWithType((ActivityContext) ActivityContext.lookupContext(getContext()), 458751) == null;
    }
}
