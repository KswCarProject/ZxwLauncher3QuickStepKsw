package com.android.launcher3.taskbar;

import android.graphics.drawable.Drawable;
import com.android.launcher3.R;
import com.android.launcher3.dragndrop.DragView;

public class TaskbarDragView extends DragView<BaseTaskbarContext> {
    public TaskbarDragView(BaseTaskbarContext baseTaskbarContext, Drawable drawable, int i, int i2, float f, float f2, float f3) {
        super(baseTaskbarContext, drawable, i, i2, f, f2, f3);
    }

    public void animateTo(int i, int i2, Runnable runnable, int i3) {
        animate().translationX((float) (i - this.mRegistrationX)).translationY((float) (i2 - this.mRegistrationY)).scaleX(this.mScaleOnDrop).scaleY(this.mScaleOnDrop).withEndAction(new Runnable(runnable) {
            public final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TaskbarDragView.this.lambda$animateTo$0$TaskbarDragView(this.f$1);
            }
        }).setDuration((long) Math.max(i3, getResources().getInteger(R.integer.config_dropAnimMinDuration))).start();
    }

    public /* synthetic */ void lambda$animateTo$0$TaskbarDragView(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        ((BaseTaskbarContext) this.mActivity).getDragLayer().removeView(this);
    }
}
