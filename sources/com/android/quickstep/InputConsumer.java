package com.android.quickstep;

import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.launcher3.tracing.InputConsumerProto;
import com.android.launcher3.tracing.TouchInteractionServiceProto;

public interface InputConsumer {
    public static final String[] NAMES = {"TYPE_NO_OP", "TYPE_OVERVIEW", "TYPE_OTHER_ACTIVITY", "TYPE_ASSISTANT", "TYPE_DEVICE_LOCKED", "TYPE_ACCESSIBILITY", "TYPE_SCREEN_PINNED", "TYPE_OVERVIEW_WITHOUT_FOCUS", "TYPE_RESET_GESTURE", "TYPE_PROGRESS_DELEGATE", "TYPE_SYSUI_OVERLAY", "TYPE_ONE_HANDED", "TYPE_TASKBAR_STASH"};
    public static final InputConsumer NO_OP = $$Lambda$InputConsumer$cayMJybMMyHXvJ76yEWjWeqQUo.INSTANCE;
    public static final int TYPE_ACCESSIBILITY = 32;
    public static final int TYPE_ASSISTANT = 8;
    public static final int TYPE_DEVICE_LOCKED = 16;
    public static final int TYPE_NO_OP = 1;
    public static final int TYPE_ONE_HANDED = 2048;
    public static final int TYPE_OTHER_ACTIVITY = 4;
    public static final int TYPE_OVERVIEW = 2;
    public static final int TYPE_OVERVIEW_WITHOUT_FOCUS = 128;
    public static final int TYPE_PROGRESS_DELEGATE = 512;
    public static final int TYPE_RESET_GESTURE = 256;
    public static final int TYPE_SCREEN_PINNED = 64;
    public static final int TYPE_SYSUI_OVERLAY = 1024;
    public static final int TYPE_TASKBAR_STASH = 4096;

    static /* synthetic */ int lambda$static$0() {
        return 1;
    }

    boolean allowInterceptByParent() {
        return true;
    }

    InputConsumer getActiveConsumerInHierarchy() {
        return this;
    }

    int getType();

    boolean isConsumerDetachedFromGesture() {
        return false;
    }

    void notifyOrientationSetup() {
    }

    void onConsumerAboutToBeSwitched() {
    }

    void onHoverEvent(MotionEvent motionEvent) {
    }

    void onKeyEvent(KeyEvent keyEvent) {
    }

    void onMotionEvent(MotionEvent motionEvent) {
    }

    void writeToProtoInternal(InputConsumerProto.Builder builder) {
    }

    void onInputEvent(InputEvent inputEvent) {
        if (inputEvent instanceof MotionEvent) {
            onMotionEvent((MotionEvent) inputEvent);
        } else {
            onKeyEvent((KeyEvent) inputEvent);
        }
    }

    String getName() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            String[] strArr = NAMES;
            if (i >= strArr.length) {
                return sb.toString();
            }
            if ((getType() & (1 << i)) != 0) {
                if (sb.length() > 0) {
                    sb.append(":");
                }
                sb.append(strArr[i]);
            }
            i++;
        }
    }

    void writeToProto(TouchInteractionServiceProto.Builder builder) {
        InputConsumerProto.Builder newBuilder = InputConsumerProto.newBuilder();
        newBuilder.setName(getName());
        writeToProtoInternal(newBuilder);
        builder.setInputConsumer(newBuilder);
    }
}
