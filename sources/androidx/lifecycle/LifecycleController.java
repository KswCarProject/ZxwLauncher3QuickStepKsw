package androidx.lifecycle;

import androidx.lifecycle.Lifecycle;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.Job;

@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\b\u0010\r\u001a\u00020\u000eH\u0007J\u0011\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00020\tH\bR\u000e\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Landroidx/lifecycle/LifecycleController;", "", "lifecycle", "Landroidx/lifecycle/Lifecycle;", "minState", "Landroidx/lifecycle/Lifecycle$State;", "dispatchQueue", "Landroidx/lifecycle/DispatchQueue;", "parentJob", "Lkotlinx/coroutines/Job;", "(Landroidx/lifecycle/Lifecycle;Landroidx/lifecycle/Lifecycle$State;Landroidx/lifecycle/DispatchQueue;Lkotlinx/coroutines/Job;)V", "observer", "Landroidx/lifecycle/LifecycleEventObserver;", "finish", "", "handleDestroy", "lifecycle-runtime-ktx_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: LifecycleController.kt */
public final class LifecycleController {
    private final DispatchQueue dispatchQueue;
    private final Lifecycle lifecycle;
    private final Lifecycle.State minState;
    private final LifecycleEventObserver observer;

    public LifecycleController(Lifecycle lifecycle2, Lifecycle.State state, DispatchQueue dispatchQueue2, Job job) {
        Intrinsics.checkNotNullParameter(lifecycle2, "lifecycle");
        Intrinsics.checkNotNullParameter(state, "minState");
        Intrinsics.checkNotNullParameter(dispatchQueue2, "dispatchQueue");
        Intrinsics.checkNotNullParameter(job, "parentJob");
        this.lifecycle = lifecycle2;
        this.minState = state;
        this.dispatchQueue = dispatchQueue2;
        $$Lambda$LifecycleController$7XqClT_TFDHmqX3ARP07WvbP2Qg r3 = new LifecycleEventObserver(job) {
            public final /* synthetic */ Job f$1;

            {
                this.f$1 = r2;
            }

            public final void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                LifecycleController.m7observer$lambda0(LifecycleController.this, this.f$1, lifecycleOwner, event);
            }
        };
        this.observer = r3;
        if (lifecycle2.getCurrentState() == Lifecycle.State.DESTROYED) {
            Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
            finish();
            return;
        }
        lifecycle2.addObserver(r3);
    }

    /* access modifiers changed from: private */
    /* renamed from: observer$lambda-0  reason: not valid java name */
    public static final void m7observer$lambda0(LifecycleController lifecycleController, Job job, LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        Intrinsics.checkNotNullParameter(lifecycleController, "this$0");
        Intrinsics.checkNotNullParameter(job, "$parentJob");
        Intrinsics.checkNotNullParameter(lifecycleOwner, "source");
        Intrinsics.checkNotNullParameter(event, "<anonymous parameter 1>");
        if (lifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
            lifecycleController.finish();
        } else if (lifecycleOwner.getLifecycle().getCurrentState().compareTo(lifecycleController.minState) < 0) {
            lifecycleController.dispatchQueue.pause();
        } else {
            lifecycleController.dispatchQueue.resume();
        }
    }

    private final void handleDestroy(Job job) {
        Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
        finish();
    }

    public final void finish() {
        this.lifecycle.removeObserver(this.observer);
        this.dispatchQueue.finish();
    }
}
