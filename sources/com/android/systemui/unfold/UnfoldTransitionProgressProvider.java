package com.android.systemui.unfold;

import com.android.systemui.statusbar.policy.CallbackController;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0005J\b\u0010\u0003\u001a\u00020\u0004H&¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "Lcom/android/systemui/statusbar/policy/CallbackController;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "destroy", "", "TransitionProgressListener", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: UnfoldTransitionProgressProvider.kt */
public interface UnfoldTransitionProgressProvider extends CallbackController<TransitionProgressListener> {

    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\u0003H\u0016¨\u0006\b"}, d2 = {"Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "", "onTransitionFinished", "", "onTransitionProgress", "progress", "", "onTransitionStarted", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldTransitionProgressProvider.kt */
    public interface TransitionProgressListener {

        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
        /* compiled from: UnfoldTransitionProgressProvider.kt */
        public static final class DefaultImpls {
            public static void onTransitionFinished(TransitionProgressListener transitionProgressListener) {
                Intrinsics.checkNotNullParameter(transitionProgressListener, "this");
            }

            public static void onTransitionProgress(TransitionProgressListener transitionProgressListener, float f) {
                Intrinsics.checkNotNullParameter(transitionProgressListener, "this");
            }

            public static void onTransitionStarted(TransitionProgressListener transitionProgressListener) {
                Intrinsics.checkNotNullParameter(transitionProgressListener, "this");
            }
        }

        void onTransitionFinished();

        void onTransitionProgress(float f);

        void onTransitionStarted();
    }

    void destroy();
}
