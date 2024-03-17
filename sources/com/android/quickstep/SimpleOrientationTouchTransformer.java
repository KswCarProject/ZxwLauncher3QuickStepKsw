package com.android.quickstep;

import android.content.Context;
import android.view.MotionEvent;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.MainThreadInitializedObject;

public class SimpleOrientationTouchTransformer implements DisplayController.DisplayInfoChangeListener {
    public static final MainThreadInitializedObject<SimpleOrientationTouchTransformer> INSTANCE = new MainThreadInitializedObject<>($$Lambda$BFHk0_dcwlOR5ZBx4PMAIQQs2Jk.INSTANCE);
    private OrientationRectF mOrientationRectF;

    public SimpleOrientationTouchTransformer(Context context) {
        DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).addChangeListener(this);
        onDisplayInfoChanged(context, DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo(), 31);
    }

    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
        if ((i & 3) != 0) {
            this.mOrientationRectF = new OrientationRectF(0.0f, 0.0f, (float) info.currentSize.y, (float) info.currentSize.x, info.rotation);
        }
    }

    public void transform(MotionEvent motionEvent, int i) {
        this.mOrientationRectF.applyTransformToRotation(motionEvent, i, true);
    }
}
