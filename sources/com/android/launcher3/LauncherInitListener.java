package com.android.launcher3;

import android.animation.AnimatorSet;
import android.os.CancellationSignal;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.function.BiPredicate;

public class LauncherInitListener extends ActivityInitListener<Launcher> {
    /* access modifiers changed from: private */
    public RemoteAnimationProvider mRemoteAnimationProvider;

    public LauncherInitListener(BiPredicate<Launcher, Boolean> biPredicate) {
        super(biPredicate, Launcher.ACTIVITY_TRACKER);
    }

    public boolean handleInit(final Launcher launcher, boolean z) {
        if (this.mRemoteAnimationProvider != null) {
            QuickstepTransitionManager appTransitionManager = ((BaseQuickstepLauncher) launcher).getAppTransitionManager();
            final CancellationSignal cancellationSignal = new CancellationSignal();
            appTransitionManager.setRemoteAnimationProvider(new RemoteAnimationProvider() {
                public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
                    cancellationSignal.cancel();
                    RemoteAnimationProvider access$000 = LauncherInitListener.this.mRemoteAnimationProvider;
                    RemoteAnimationProvider unused = LauncherInitListener.this.mRemoteAnimationProvider = null;
                    if (access$000 == null || !launcher.getStateManager().getState().overviewUi) {
                        return null;
                    }
                    return access$000.createWindowAnimation(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2);
                }
            }, cancellationSignal);
        }
        launcher.deferOverlayCallbacksUntilNextResumeOrStop();
        return super.handleInit(launcher, z);
    }

    public void unregister() {
        this.mRemoteAnimationProvider = null;
        super.unregister();
    }
}
