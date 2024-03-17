package com.android.systemui.unfold.util;

import android.content.Context;
import android.os.RemoteException;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001\u0018B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0001¢\u0006\u0002\u0010\u0007J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u000fH\u0016J\u0006\u0010\u0013\u001a\u00020\u000fJ\u0010\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\n\u001a\u00060\u000bR\u00020\u0000X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"}, d2 = {"Lcom/android/systemui/unfold/util/NaturalRotationUnfoldProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "context", "Landroid/content/Context;", "windowManagerInterface", "Landroid/view/IWindowManager;", "unfoldTransitionProgressProvider", "(Landroid/content/Context;Landroid/view/IWindowManager;Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;)V", "isNaturalRotation", "", "rotationWatcher", "Lcom/android/systemui/unfold/util/NaturalRotationUnfoldProgressProvider$RotationWatcher;", "scopedUnfoldTransitionProgressProvider", "Lcom/android/systemui/unfold/util/ScopedUnfoldTransitionProgressProvider;", "addCallback", "", "listener", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "destroy", "init", "onRotationChanged", "rotation", "", "removeCallback", "RotationWatcher", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: NaturalRotationUnfoldProgressProvider.kt */
public final class NaturalRotationUnfoldProgressProvider implements UnfoldTransitionProgressProvider {
    private final Context context;
    private boolean isNaturalRotation;
    private final RotationWatcher rotationWatcher = new RotationWatcher(this);
    private final ScopedUnfoldTransitionProgressProvider scopedUnfoldTransitionProgressProvider;
    private final IWindowManager windowManagerInterface;

    public NaturalRotationUnfoldProgressProvider(Context context2, IWindowManager iWindowManager, UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(iWindowManager, "windowManagerInterface");
        Intrinsics.checkNotNullParameter(unfoldTransitionProgressProvider, "unfoldTransitionProgressProvider");
        this.context = context2;
        this.windowManagerInterface = iWindowManager;
        this.scopedUnfoldTransitionProgressProvider = new ScopedUnfoldTransitionProgressProvider(unfoldTransitionProgressProvider);
    }

    public final void init() {
        try {
            this.windowManagerInterface.watchRotation(this.rotationWatcher, this.context.getDisplay().getDisplayId());
            onRotationChanged(this.context.getDisplay().getRotation());
        } catch (RemoteException e) {
            RuntimeException rethrowFromSystemServer = e.rethrowFromSystemServer();
            Intrinsics.checkNotNullExpressionValue(rethrowFromSystemServer, "e.rethrowFromSystemServer()");
            throw rethrowFromSystemServer;
        }
    }

    /* access modifiers changed from: private */
    public final void onRotationChanged(int i) {
        boolean z = i == 0 || i == 2;
        if (this.isNaturalRotation != z) {
            this.isNaturalRotation = z;
            this.scopedUnfoldTransitionProgressProvider.setReadyToHandleTransition(z);
        }
    }

    public void destroy() {
        try {
            this.windowManagerInterface.removeRotationWatcher(this.rotationWatcher);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        this.scopedUnfoldTransitionProgressProvider.destroy();
    }

    public void addCallback(UnfoldTransitionProgressProvider.TransitionProgressListener transitionProgressListener) {
        Intrinsics.checkNotNullParameter(transitionProgressListener, "listener");
        this.scopedUnfoldTransitionProgressProvider.addCallback(transitionProgressListener);
    }

    public void removeCallback(UnfoldTransitionProgressProvider.TransitionProgressListener transitionProgressListener) {
        Intrinsics.checkNotNullParameter(transitionProgressListener, "listener");
        this.scopedUnfoldTransitionProgressProvider.removeCallback(transitionProgressListener);
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007"}, d2 = {"Lcom/android/systemui/unfold/util/NaturalRotationUnfoldProgressProvider$RotationWatcher;", "Landroid/view/IRotationWatcher$Stub;", "(Lcom/android/systemui/unfold/util/NaturalRotationUnfoldProgressProvider;)V", "onRotationChanged", "", "rotation", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: NaturalRotationUnfoldProgressProvider.kt */
    private final class RotationWatcher extends IRotationWatcher.Stub {
        final /* synthetic */ NaturalRotationUnfoldProgressProvider this$0;

        public RotationWatcher(NaturalRotationUnfoldProgressProvider naturalRotationUnfoldProgressProvider) {
            Intrinsics.checkNotNullParameter(naturalRotationUnfoldProgressProvider, "this$0");
            this.this$0 = naturalRotationUnfoldProgressProvider;
        }

        public void onRotationChanged(int i) {
            this.this$0.onRotationChanged(i);
        }
    }
}
