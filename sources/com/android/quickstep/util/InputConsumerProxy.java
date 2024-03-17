package com.android.quickstep.util;

import android.content.Context;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.SimpleOrientationTouchTransformer;
import com.android.systemui.shared.system.InputConsumerController;
import java.util.function.Supplier;

public class InputConsumerProxy {
    private static final String TAG = "InputConsumerProxy";
    private Runnable mCallback;
    private Supplier<InputConsumer> mConsumerSupplier;
    private final Context mContext;
    private boolean mDestroyPending = false;
    private boolean mDestroyed = false;
    private InputConsumer mInputConsumer;
    private final InputConsumerController mInputConsumerController;
    private final Supplier<Integer> mRotationSupplier;
    private boolean mTouchInProgress = false;

    public InputConsumerProxy(Context context, Supplier<Integer> supplier, InputConsumerController inputConsumerController, Runnable runnable, Supplier<InputConsumer> supplier2) {
        this.mContext = context;
        this.mRotationSupplier = supplier;
        this.mInputConsumerController = inputConsumerController;
        this.mCallback = runnable;
        this.mConsumerSupplier = supplier2;
    }

    public void enable() {
        if (!this.mDestroyed) {
            this.mInputConsumerController.setInputListener(new InputConsumerController.InputListener() {
                public final boolean onInputEvent(InputEvent inputEvent) {
                    return InputConsumerProxy.this.onInputConsumerEvent(inputEvent);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public boolean onInputConsumerEvent(InputEvent inputEvent) {
        boolean z = true;
        if (inputEvent instanceof MotionEvent) {
            MotionEvent motionEvent = (MotionEvent) inputEvent;
            int actionMasked = motionEvent.getActionMasked();
            if (!(actionMasked == 9 || actionMasked == 7 || actionMasked == 10)) {
                z = false;
            }
            if (z) {
                onInputConsumerHoverEvent(motionEvent);
            } else {
                onInputConsumerMotionEvent(motionEvent);
            }
        } else if (inputEvent instanceof KeyEvent) {
            initInputConsumerIfNeeded();
            this.mInputConsumer.onKeyEvent((KeyEvent) inputEvent);
            return true;
        }
        return false;
    }

    private boolean onInputConsumerMotionEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        boolean z = this.mTouchInProgress;
        if (!z && action != 0) {
            Log.w(TAG, "Received non-down motion before down motion: " + action);
            return false;
        } else if (!z || action != 0) {
            if (action == 0) {
                this.mTouchInProgress = true;
                initInputConsumerIfNeeded();
            } else if (action == 3 || action == 1) {
                this.mTouchInProgress = false;
                if (this.mDestroyPending) {
                    destroy();
                }
            }
            if (this.mInputConsumer != null) {
                SimpleOrientationTouchTransformer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).transform(motionEvent, this.mRotationSupplier.get().intValue());
                this.mInputConsumer.onMotionEvent(motionEvent);
            }
            return true;
        } else {
            Log.w(TAG, "Received down motion while touch was already in progress");
            return false;
        }
    }

    private void onInputConsumerHoverEvent(MotionEvent motionEvent) {
        initInputConsumerIfNeeded();
        if (this.mInputConsumer != null) {
            SimpleOrientationTouchTransformer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).transform(motionEvent, this.mRotationSupplier.get().intValue());
            this.mInputConsumer.onHoverEvent(motionEvent);
        }
    }

    public void destroy() {
        if (this.mTouchInProgress) {
            this.mDestroyPending = true;
            return;
        }
        this.mDestroyPending = false;
        this.mDestroyed = true;
        this.mInputConsumerController.setInputListener((InputConsumerController.InputListener) null);
    }

    public void unregisterCallback() {
        this.mCallback = null;
    }

    private void initInputConsumerIfNeeded() {
        if (this.mInputConsumer == null) {
            Runnable runnable = this.mCallback;
            if (runnable != null) {
                runnable.run();
            }
            this.mInputConsumer = this.mConsumerSupplier.get();
            this.mConsumerSupplier = null;
        }
    }
}
