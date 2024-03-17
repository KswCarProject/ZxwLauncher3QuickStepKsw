package com.android.quickstep;

import com.android.quickstep.RemoteAnimationTargets;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class RemoteAnimationTargets {
    public final RemoteAnimationTargetCompat[] apps;
    public final boolean hasRecents;
    private final CopyOnWriteArrayList<ReleaseCheck> mReleaseChecks = new CopyOnWriteArrayList<>();
    private boolean mReleased;
    public final RemoteAnimationTargetCompat[] nonApps;
    public final int targetMode;
    public final RemoteAnimationTargetCompat[] unfilteredApps;
    public final RemoteAnimationTargetCompat[] wallpapers;

    public RemoteAnimationTargets(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, int i) {
        boolean z = false;
        this.mReleased = false;
        ArrayList arrayList = new ArrayList();
        if (remoteAnimationTargetCompatArr != null) {
            boolean z2 = false;
            for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
                if (remoteAnimationTargetCompat.mode == i) {
                    arrayList.add(remoteAnimationTargetCompat);
                }
                z2 |= remoteAnimationTargetCompat.activityType == 3;
            }
            z = z2;
        }
        this.unfilteredApps = remoteAnimationTargetCompatArr;
        this.apps = (RemoteAnimationTargetCompat[]) arrayList.toArray(new RemoteAnimationTargetCompat[arrayList.size()]);
        this.wallpapers = remoteAnimationTargetCompatArr2;
        this.targetMode = i;
        this.hasRecents = z;
        this.nonApps = remoteAnimationTargetCompatArr3;
    }

    public RemoteAnimationTargetCompat findTask(int i) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : this.apps) {
            if (remoteAnimationTargetCompat.taskId == i) {
                return remoteAnimationTargetCompat;
            }
        }
        return null;
    }

    public RemoteAnimationTargetCompat getNavBarRemoteAnimationTarget() {
        return getNonAppTargetOfType(2019);
    }

    public RemoteAnimationTargetCompat getNonAppTargetOfType(int i) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : this.nonApps) {
            if (remoteAnimationTargetCompat.windowType == i) {
                return remoteAnimationTargetCompat;
            }
        }
        return null;
    }

    public RemoteAnimationTargetCompat getFirstAppTarget() {
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = this.apps;
        if (remoteAnimationTargetCompatArr.length > 0) {
            return remoteAnimationTargetCompatArr[0];
        }
        return null;
    }

    public int getFirstAppTargetTaskId() {
        RemoteAnimationTargetCompat firstAppTarget = getFirstAppTarget();
        if (firstAppTarget == null) {
            return -1;
        }
        return firstAppTarget.taskId;
    }

    public boolean isAnimatingHome() {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : this.unfilteredApps) {
            if (remoteAnimationTargetCompat.activityType == 2) {
                return true;
            }
        }
        return false;
    }

    public void addReleaseCheck(ReleaseCheck releaseCheck) {
        this.mReleaseChecks.add(releaseCheck);
    }

    public void release() {
        if (TaskAnimationManager.ENABLE_SHELL_TRANSITIONS) {
            this.mReleaseChecks.clear();
        } else if (!this.mReleased) {
            Iterator<ReleaseCheck> it = this.mReleaseChecks.iterator();
            while (it.hasNext()) {
                ReleaseCheck next = it.next();
                if (!next.mCanRelease) {
                    next.addOnSafeToReleaseCallback(new Runnable() {
                        public final void run() {
                            RemoteAnimationTargets.this.release();
                        }
                    });
                    return;
                }
            }
            this.mReleaseChecks.clear();
            this.mReleased = true;
            for (RemoteAnimationTargetCompat release : this.unfilteredApps) {
                release.release();
            }
            for (RemoteAnimationTargetCompat release2 : this.wallpapers) {
                release2.release();
            }
            for (RemoteAnimationTargetCompat release3 : this.nonApps) {
                release3.release();
            }
        }
    }

    public static class ReleaseCheck {
        private Runnable mAfterApplyCallback;
        boolean mCanRelease = false;

        /* access modifiers changed from: protected */
        public void setCanRelease(boolean z) {
            Runnable runnable;
            this.mCanRelease = z;
            if (z && (runnable = this.mAfterApplyCallback) != null) {
                this.mAfterApplyCallback = null;
                runnable.run();
            }
        }

        /* access modifiers changed from: package-private */
        public void addOnSafeToReleaseCallback(Runnable runnable) {
            if (this.mCanRelease) {
                runnable.run();
                return;
            }
            Runnable runnable2 = this.mAfterApplyCallback;
            if (runnable2 == null) {
                this.mAfterApplyCallback = runnable;
            } else {
                this.mAfterApplyCallback = new Runnable(runnable, runnable2) {
                    public final /* synthetic */ Runnable f$0;
                    public final /* synthetic */ Runnable f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        RemoteAnimationTargets.ReleaseCheck.lambda$addOnSafeToReleaseCallback$0(this.f$0, this.f$1);
                    }
                };
            }
        }

        static /* synthetic */ void lambda$addOnSafeToReleaseCallback$0(Runnable runnable, Runnable runnable2) {
            runnable.run();
            runnable2.run();
        }
    }
}
