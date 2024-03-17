package com.android.quickstep.util;

import android.content.Context;
import android.view.WindowManager;
import com.android.launcher3.R;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.WindowBounds;
import java.util.ArrayList;
import java.util.Iterator;

public class SplitScreenBounds {
    public static final SplitScreenBounds INSTANCE = new SplitScreenBounds();
    private WindowBounds mBounds;
    private final ArrayList<OnChangeListener> mListeners = new ArrayList<>();

    public interface OnChangeListener {
        void onSecondaryWindowBoundsChanged();
    }

    private SplitScreenBounds() {
    }

    public void setSecondaryWindowBounds(WindowBounds windowBounds) {
        if (!windowBounds.equals(this.mBounds)) {
            this.mBounds = windowBounds;
            Iterator<OnChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onSecondaryWindowBoundsChanged();
            }
        }
    }

    public WindowBounds getSecondaryWindowBounds(Context context) {
        if (this.mBounds == null) {
            this.mBounds = createDefaultWindowBounds(context);
        }
        return this.mBounds;
    }

    private static WindowBounds createDefaultWindowBounds(Context context) {
        WindowBounds fromWindowMetrics = WindowBounds.fromWindowMetrics(((WindowManager) context.getSystemService(WindowManager.class)).getMaximumWindowMetrics());
        int i = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo().rotation;
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.multi_window_task_divider_size) / 2;
        if (i == 0 || i == 2) {
            fromWindowMetrics.bounds.top = fromWindowMetrics.insets.top + (fromWindowMetrics.availableSize.y / 2) + dimensionPixelSize;
            fromWindowMetrics.insets.top = 0;
        } else {
            fromWindowMetrics.bounds.left = fromWindowMetrics.insets.left + (fromWindowMetrics.availableSize.x / 2) + dimensionPixelSize;
            fromWindowMetrics.insets.left = 0;
        }
        return new WindowBounds(fromWindowMetrics.bounds, fromWindowMetrics.insets);
    }

    public void addOnChangeListener(OnChangeListener onChangeListener) {
        this.mListeners.add(onChangeListener);
    }

    public void removeOnChangeListener(OnChangeListener onChangeListener) {
        this.mListeners.remove(onChangeListener);
    }
}
