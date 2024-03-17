package com.android.systemui.unfold.util;

import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0010\u0002\n\u0002\b\f\b\u0016\u0018\u0000 \u00182\u00020\u00012\u00020\u0002:\u0001\u0018B\u0013\b\u0007\u0012\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u0001¢\u0006\u0002\u0010\u0004J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0002H\u0016J\b\u0010\u000f\u001a\u00020\rH\u0016J\b\u0010\u0010\u001a\u00020\rH\u0016J\u0010\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\tH\u0016J\b\u0010\u0013\u001a\u00020\rH\u0016J\u0010\u0010\u0014\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0002H\u0016J\u000e\u0010\u0015\u001a\u00020\r2\u0006\u0010\u0005\u001a\u00020\u0006J\u0010\u0010\u0016\u001a\u00020\r2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001R\u000e\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\u000bX\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0001X\u000e¢\u0006\u0002\n\u0000¨\u0006\u0019"}, d2 = {"Lcom/android/systemui/unfold/util/ScopedUnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "source", "(Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;)V", "isReadyToHandleTransition", "", "isTransitionRunning", "lastTransitionProgress", "", "listeners", "", "addCallback", "", "listener", "destroy", "onTransitionFinished", "onTransitionProgress", "progress", "onTransitionStarted", "removeCallback", "setReadyToHandleTransition", "setSourceProvider", "provider", "Companion", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ScopedUnfoldTransitionProgressProvider.kt */
public class ScopedUnfoldTransitionProgressProvider implements UnfoldTransitionProgressProvider, UnfoldTransitionProgressProvider.TransitionProgressListener {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private static final float PROGRESS_UNSET = -1.0f;
    private boolean isReadyToHandleTransition;
    private boolean isTransitionRunning;
    private float lastTransitionProgress;
    private final List<UnfoldTransitionProgressProvider.TransitionProgressListener> listeners;
    private UnfoldTransitionProgressProvider source;

    public ScopedUnfoldTransitionProgressProvider() {
        this((UnfoldTransitionProgressProvider) null, 1, (DefaultConstructorMarker) null);
    }

    public ScopedUnfoldTransitionProgressProvider(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        this.listeners = new ArrayList();
        this.lastTransitionProgress = PROGRESS_UNSET;
        setSourceProvider(unfoldTransitionProgressProvider);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ ScopedUnfoldTransitionProgressProvider(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : unfoldTransitionProgressProvider);
    }

    public final void setSourceProvider(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        UnfoldTransitionProgressProvider unfoldTransitionProgressProvider2 = this.source;
        if (unfoldTransitionProgressProvider2 != null) {
            unfoldTransitionProgressProvider2.removeCallback(this);
        }
        if (unfoldTransitionProgressProvider != null) {
            this.source = unfoldTransitionProgressProvider;
            unfoldTransitionProgressProvider.addCallback(this);
            return;
        }
        this.source = null;
    }

    public final void setReadyToHandleTransition(boolean z) {
        if (this.isTransitionRunning) {
            boolean z2 = false;
            if (z) {
                for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionStarted : this.listeners) {
                    onTransitionStarted.onTransitionStarted();
                }
                if (this.lastTransitionProgress == PROGRESS_UNSET) {
                    z2 = true;
                }
                if (!z2) {
                    for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionProgress : this.listeners) {
                        onTransitionProgress.onTransitionProgress(this.lastTransitionProgress);
                    }
                }
            } else {
                this.isTransitionRunning = false;
                for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionFinished : this.listeners) {
                    onTransitionFinished.onTransitionFinished();
                }
            }
        }
        this.isReadyToHandleTransition = z;
    }

    public void addCallback(UnfoldTransitionProgressProvider.TransitionProgressListener transitionProgressListener) {
        Intrinsics.checkNotNullParameter(transitionProgressListener, "listener");
        this.listeners.add(transitionProgressListener);
    }

    public void removeCallback(UnfoldTransitionProgressProvider.TransitionProgressListener transitionProgressListener) {
        Intrinsics.checkNotNullParameter(transitionProgressListener, "listener");
        this.listeners.remove(transitionProgressListener);
    }

    public void destroy() {
        UnfoldTransitionProgressProvider unfoldTransitionProgressProvider = this.source;
        if (unfoldTransitionProgressProvider != null) {
            unfoldTransitionProgressProvider.removeCallback(this);
        }
        UnfoldTransitionProgressProvider unfoldTransitionProgressProvider2 = this.source;
        if (unfoldTransitionProgressProvider2 != null) {
            unfoldTransitionProgressProvider2.destroy();
        }
    }

    public void onTransitionStarted() {
        this.isTransitionRunning = true;
        if (this.isReadyToHandleTransition) {
            for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionStarted : this.listeners) {
                onTransitionStarted.onTransitionStarted();
            }
        }
    }

    public void onTransitionProgress(float f) {
        if (this.isReadyToHandleTransition) {
            for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionProgress : this.listeners) {
                onTransitionProgress.onTransitionProgress(f);
            }
        }
        this.lastTransitionProgress = f;
    }

    public void onTransitionFinished() {
        if (this.isReadyToHandleTransition) {
            for (UnfoldTransitionProgressProvider.TransitionProgressListener onTransitionFinished : this.listeners) {
                onTransitionFinished.onTransitionFinished();
            }
        }
        this.isTransitionRunning = false;
        this.lastTransitionProgress = PROGRESS_UNSET;
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/unfold/util/ScopedUnfoldTransitionProgressProvider$Companion;", "", "()V", "PROGRESS_UNSET", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ScopedUnfoldTransitionProgressProvider.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
