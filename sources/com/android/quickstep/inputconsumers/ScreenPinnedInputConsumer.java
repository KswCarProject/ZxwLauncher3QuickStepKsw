package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.view.MotionEvent;
import com.android.launcher3.R;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.MotionPauseDetector;

public class ScreenPinnedInputConsumer implements InputConsumer {
    private static final String TAG = "ScreenPinnedConsumer";
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private float mTouchDownY;

    public int getType() {
        return 64;
    }

    public ScreenPinnedInputConsumer(Context context, GestureState gestureState) {
        this.mMotionPauseMinDisplacement = context.getResources().getDimension(R.dimen.motion_pause_detector_min_displacement_from_app);
        MotionPauseDetector motionPauseDetector = new MotionPauseDetector(context, true);
        this.mMotionPauseDetector = motionPauseDetector;
        motionPauseDetector.setOnMotionPauseListener(new MotionPauseDetector.OnMotionPauseListener(context, gestureState) {
            public final /* synthetic */ Context f$1;
            public final /* synthetic */ GestureState f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onMotionPauseDetected() {
                ScreenPinnedInputConsumer.this.lambda$new$0$ScreenPinnedInputConsumer(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$ScreenPinnedInputConsumer(Context context, GestureState gestureState) {
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).stopScreenPinning();
        StatefulActivity createdActivity = gestureState.getActivityInterface().getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.getRootView().performHapticFeedback(0, 1);
        }
        this.mMotionPauseDetector.clear();
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action != 0) {
            boolean z = true;
            if (action != 1) {
                if (action == 2) {
                    float f = this.mTouchDownY - y;
                    MotionPauseDetector motionPauseDetector = this.mMotionPauseDetector;
                    if (f >= this.mMotionPauseMinDisplacement) {
                        z = false;
                    }
                    motionPauseDetector.setDisallowPause(z);
                    this.mMotionPauseDetector.addPosition(motionEvent);
                    return;
                } else if (action != 3) {
                    return;
                }
            }
            this.mMotionPauseDetector.clear();
            return;
        }
        this.mTouchDownY = y;
    }
}
