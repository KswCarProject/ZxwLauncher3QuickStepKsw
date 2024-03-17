package com.android.quickstep.util;

import android.graphics.HardwareRenderer;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.function.Consumer;

public class SurfaceTransactionApplier extends RemoteAnimationTargets.ReleaseCheck {
    private static final int MSG_UPDATE_SEQUENCE_NUMBER = 0;
    private final Handler mApplyHandler;
    private final SurfaceControl mBarrierSurfaceControl;
    private int mLastSequenceNumber = 0;
    private final ViewRootImpl mTargetViewRootImpl;

    public SurfaceTransactionApplier(View view) {
        ViewRootImpl viewRootImpl = view.getViewRootImpl();
        this.mTargetViewRootImpl = viewRootImpl;
        this.mBarrierSurfaceControl = viewRootImpl.getSurfaceControl();
        this.mApplyHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SurfaceTransactionApplier.this.onApplyMessage(message);
            }
        });
        setCanRelease(true);
    }

    /* access modifiers changed from: protected */
    public boolean onApplyMessage(Message message) {
        boolean z = false;
        if (message.what != 0) {
            return false;
        }
        if (message.arg1 == this.mLastSequenceNumber) {
            z = true;
        }
        setCanRelease(z);
        return true;
    }

    public void scheduleApply(SyncRtSurfaceTransactionApplierCompat.SurfaceParams... surfaceParamsArr) {
        View view = this.mTargetViewRootImpl.getView();
        if (view != null) {
            SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
            for (int length = surfaceParamsArr.length - 1; length >= 0; length--) {
                SyncRtSurfaceTransactionApplierCompat.SurfaceParams surfaceParams = surfaceParamsArr[length];
                if (surfaceParams.surface.isValid()) {
                    surfaceParams.applyTo(transaction);
                }
            }
            int i = this.mLastSequenceNumber + 1;
            this.mLastSequenceNumber = i;
            setCanRelease(false);
            this.mTargetViewRootImpl.registerRtFrameCallback(new HardwareRenderer.FrameDrawingCallback(i, transaction) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ SurfaceControl.Transaction f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onFrameDraw(long j) {
                    SurfaceTransactionApplier.this.lambda$scheduleApply$0$SurfaceTransactionApplier(this.f$1, this.f$2, j);
                }
            });
            view.invalidate();
        }
    }

    public /* synthetic */ void lambda$scheduleApply$0$SurfaceTransactionApplier(int i, SurfaceControl.Transaction transaction, long j) {
        SurfaceControl surfaceControl = this.mBarrierSurfaceControl;
        if (surfaceControl == null || !surfaceControl.isValid()) {
            Message.obtain(this.mApplyHandler, 0, i, 0).sendToTarget();
            return;
        }
        this.mTargetViewRootImpl.mergeWithNextTransaction(transaction, j);
        Message.obtain(this.mApplyHandler, 0, i, 0).sendToTarget();
    }

    public static void create(final View view, final Consumer<SurfaceTransactionApplier> consumer) {
        if (view == null) {
            consumer.accept((Object) null);
        } else if (view.isAttachedToWindow()) {
            consumer.accept(new SurfaceTransactionApplier(view));
        } else {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                public void onViewDetachedFromWindow(View view) {
                }

                public void onViewAttachedToWindow(View view) {
                    view.removeOnAttachStateChangeListener(this);
                    consumer.accept(new SurfaceTransactionApplier(view));
                }
            });
        }
    }
}
