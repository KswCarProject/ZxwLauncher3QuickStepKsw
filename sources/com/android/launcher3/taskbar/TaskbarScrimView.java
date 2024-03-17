package com.android.launcher3.taskbar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.views.ActivityContext;

public class TaskbarScrimView extends View {
    private final TaskbarBackgroundRenderer mRenderer;
    private boolean mShowScrim;

    public TaskbarScrimView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TaskbarScrimView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskbarScrimView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TaskbarScrimView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        TaskbarBackgroundRenderer taskbarBackgroundRenderer = new TaskbarBackgroundRenderer((TaskbarActivityContext) ActivityContext.lookupContext(context));
        this.mRenderer = taskbarBackgroundRenderer;
        taskbarBackgroundRenderer.getPaint().setColor(getResources().getColor(17170473));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mShowScrim) {
            this.mRenderer.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void setScrimAlpha(float f) {
        this.mShowScrim = f > 0.0f;
        this.mRenderer.getPaint().setAlpha((int) (f * 255.0f));
        invalidate();
    }
}
