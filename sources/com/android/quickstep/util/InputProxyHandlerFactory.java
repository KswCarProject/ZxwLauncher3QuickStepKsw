package com.android.quickstep.util;

import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.inputconsumers.OverviewInputConsumer;
import com.android.systemui.shared.system.InputMonitorCompat;
import java.util.function.Supplier;

public class InputProxyHandlerFactory implements Supplier<InputConsumer> {
    private final BaseActivityInterface mActivityInterface;
    private final GestureState mGestureState;

    public InputProxyHandlerFactory(BaseActivityInterface baseActivityInterface, GestureState gestureState) {
        this.mActivityInterface = baseActivityInterface;
        this.mGestureState = gestureState;
    }

    public InputConsumer get() {
        StatefulActivity createdActivity = this.mActivityInterface.getCreatedActivity();
        if (createdActivity == null) {
            return InputConsumer.NO_OP;
        }
        return new OverviewInputConsumer(this.mGestureState, createdActivity, (InputMonitorCompat) null, true);
    }
}
