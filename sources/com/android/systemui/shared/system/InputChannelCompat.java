package com.android.systemui.shared.system;

import android.graphics.Matrix;
import android.os.Looper;
import android.view.BatchedInputEventReceiver;
import android.view.Choreographer;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.MotionEvent;

public class InputChannelCompat {

    public interface InputEventListener {
        void onInputEvent(InputEvent inputEvent);
    }

    public static boolean mergeMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2) {
        return motionEvent2.addBatch(motionEvent);
    }

    public static Matrix createRotationMatrix(int i, int i2, int i3) {
        return MotionEvent.createRotateMatrix(i, i2, i3);
    }

    public static class InputEventReceiver {
        private final BatchedInputEventReceiver mReceiver;

        public InputEventReceiver(InputChannel inputChannel, Looper looper, Choreographer choreographer, InputEventListener inputEventListener) {
            final InputEventListener inputEventListener2 = inputEventListener;
            this.mReceiver = new BatchedInputEventReceiver(inputChannel, looper, choreographer) {
                public void onInputEvent(InputEvent inputEvent) {
                    inputEventListener2.onInputEvent(inputEvent);
                    finishInputEvent(inputEvent, true);
                }
            };
        }

        public void setBatchingEnabled(boolean z) {
            this.mReceiver.setBatchingEnabled(z);
        }

        public void dispose() {
            this.mReceiver.dispose();
        }
    }
}
