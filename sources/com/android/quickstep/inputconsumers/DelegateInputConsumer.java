package com.android.quickstep.inputconsumers;

import android.view.MotionEvent;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.tracing.InputConsumerProto;
import com.android.quickstep.InputConsumer;
import com.android.systemui.shared.system.InputMonitorCompat;

public abstract class DelegateInputConsumer implements InputConsumer {
    protected static final int STATE_ACTIVE = 1;
    protected static final int STATE_DELEGATE_ACTIVE = 2;
    protected static final int STATE_INACTIVE = 0;
    protected final InputConsumer mDelegate;
    protected final InputMonitorCompat mInputMonitor;
    protected int mState = 0;

    public DelegateInputConsumer(InputConsumer inputConsumer, InputMonitorCompat inputMonitorCompat) {
        this.mDelegate = inputConsumer;
        this.mInputMonitor = inputMonitorCompat;
    }

    public InputConsumer getActiveConsumerInHierarchy() {
        if (this.mState == 1) {
            return this;
        }
        return this.mDelegate.getActiveConsumerInHierarchy();
    }

    public boolean allowInterceptByParent() {
        return this.mDelegate.allowInterceptByParent() && this.mState != 1;
    }

    public void onConsumerAboutToBeSwitched() {
        this.mDelegate.onConsumerAboutToBeSwitched();
    }

    /* access modifiers changed from: protected */
    public void setActive(MotionEvent motionEvent) {
        this.mState = 1;
        TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
        this.mInputMonitor.pilferPointers();
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        obtain.setAction(3);
        this.mDelegate.onMotionEvent(obtain);
        obtain.recycle();
    }

    public void writeToProtoInternal(InputConsumerProto.Builder builder) {
        this.mDelegate.writeToProtoInternal(builder);
    }
}
