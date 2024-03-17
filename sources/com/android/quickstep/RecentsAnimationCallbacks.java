package com.android.quickstep;

import android.graphics.Rect;
import android.os.Handler;
import android.util.ArraySet;
import android.view.RemoteAnimationTarget;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.Preconditions;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RecentsAnimationControllerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class RecentsAnimationCallbacks implements com.android.systemui.shared.system.RecentsAnimationListener {
    private final boolean mAllowMinimizeSplitScreen;
    private boolean mCancelled;
    private RecentsAnimationController mController;
    private final Set<RecentsAnimationListener> mListeners = new ArraySet();
    private final SystemUiProxy mSystemUiProxy;

    public interface RecentsAnimationListener {
        void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
        }

        void onRecentsAnimationFinished(RecentsAnimationController recentsAnimationController) {
        }

        void onRecentsAnimationStart(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
        }

        boolean onSwitchToScreenshot(Runnable runnable) {
            return false;
        }

        void onTasksAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        }
    }

    public RecentsAnimationCallbacks(SystemUiProxy systemUiProxy, boolean z) {
        this.mSystemUiProxy = systemUiProxy;
        this.mAllowMinimizeSplitScreen = z;
    }

    public void addListener(RecentsAnimationListener recentsAnimationListener) {
        Preconditions.assertUIThread();
        this.mListeners.add(recentsAnimationListener);
    }

    public void removeListener(RecentsAnimationListener recentsAnimationListener) {
        Preconditions.assertUIThread();
        this.mListeners.remove(recentsAnimationListener);
    }

    public void removeAllListeners() {
        Preconditions.assertUIThread();
        this.mListeners.clear();
    }

    public void notifyAnimationCanceled() {
        this.mCancelled = true;
        onAnimationCanceled(new HashMap());
    }

    @Deprecated
    public final void onAnimationStart(RecentsAnimationControllerCompat recentsAnimationControllerCompat, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, Rect rect, Rect rect2) {
        onAnimationStart(recentsAnimationControllerCompat, remoteAnimationTargetCompatArr, new RemoteAnimationTargetCompat[0], rect, rect2);
    }

    public final void onAnimationStart(RecentsAnimationControllerCompat recentsAnimationControllerCompat, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, Rect rect, Rect rect2) {
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3 = remoteAnimationTargetCompatArr;
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr4 = remoteAnimationTargetCompatArr2;
        RecentsAnimationTargets recentsAnimationTargets = new RecentsAnimationTargets(remoteAnimationTargetCompatArr3, remoteAnimationTargetCompatArr4, RemoteAnimationTargetCompat.wrap(this.mSystemUiProxy.onGoingToRecentsLegacy((RemoteAnimationTarget[]) Arrays.stream(remoteAnimationTargetCompatArr).filter($$Lambda$RecentsAnimationCallbacks$REuHXvcNqLxk8w_4Zsf25kobF8.INSTANCE).map($$Lambda$RecentsAnimationCallbacks$tAyCrZ2lAkHHAGjbVm8XNYk3trc.INSTANCE).toArray($$Lambda$RecentsAnimationCallbacks$pSvoROGUC4Wje4RnmgtEZBG0DLU.INSTANCE))), rect, rect2);
        this.mController = new RecentsAnimationController(recentsAnimationControllerCompat, this.mAllowMinimizeSplitScreen, new Consumer() {
            public final void accept(Object obj) {
                RecentsAnimationCallbacks.this.onAnimationFinished((RecentsAnimationController) obj);
            }
        });
        if (this.mCancelled) {
            Handler handler = Executors.MAIN_EXECUTOR.getHandler();
            RecentsAnimationController recentsAnimationController = this.mController;
            Objects.requireNonNull(recentsAnimationController);
            Utilities.postAsyncCallback(handler, new Runnable() {
                public final void run() {
                    RecentsAnimationController.this.finishAnimationToApp();
                }
            });
            return;
        }
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable(recentsAnimationTargets) {
            public final /* synthetic */ RecentsAnimationTargets f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationCallbacks.this.lambda$onAnimationStart$2$RecentsAnimationCallbacks(this.f$1);
            }
        });
    }

    static /* synthetic */ boolean lambda$onAnimationStart$0(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return remoteAnimationTargetCompat.activityType != 2;
    }

    static /* synthetic */ RemoteAnimationTarget[] lambda$onAnimationStart$1(int i) {
        return new RemoteAnimationTarget[i];
    }

    public /* synthetic */ void lambda$onAnimationStart$2$RecentsAnimationCallbacks(RecentsAnimationTargets recentsAnimationTargets) {
        for (RecentsAnimationListener onRecentsAnimationStart : getListeners()) {
            onRecentsAnimationStart.onRecentsAnimationStart(this.mController, recentsAnimationTargets);
        }
    }

    public final void onAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable(hashMap) {
            public final /* synthetic */ HashMap f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationCallbacks.this.lambda$onAnimationCanceled$3$RecentsAnimationCallbacks(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onAnimationCanceled$3$RecentsAnimationCallbacks(HashMap hashMap) {
        for (RecentsAnimationListener onRecentsAnimationCanceled : getListeners()) {
            onRecentsAnimationCanceled.onRecentsAnimationCanceled(hashMap);
        }
    }

    public void onTasksAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable(remoteAnimationTargetCompatArr) {
            public final /* synthetic */ RemoteAnimationTargetCompat[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationCallbacks.this.lambda$onTasksAppeared$4$RecentsAnimationCallbacks(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onTasksAppeared$4$RecentsAnimationCallbacks(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        for (RecentsAnimationListener onTasksAppeared : getListeners()) {
            onTasksAppeared.onTasksAppeared(remoteAnimationTargetCompatArr);
        }
    }

    public boolean onSwitchToScreenshot(Runnable runnable) {
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable(runnable) {
            public final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationCallbacks.this.lambda$onSwitchToScreenshot$5$RecentsAnimationCallbacks(this.f$1);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onSwitchToScreenshot$5$RecentsAnimationCallbacks(Runnable runnable) {
        RecentsAnimationListener[] listeners = getListeners();
        int length = listeners.length;
        int i = 0;
        while (i < length) {
            if (!listeners[i].onSwitchToScreenshot(runnable)) {
                i++;
            } else {
                return;
            }
        }
        runnable.run();
    }

    /* access modifiers changed from: private */
    public final void onAnimationFinished(RecentsAnimationController recentsAnimationController) {
        Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable(recentsAnimationController) {
            public final /* synthetic */ RecentsAnimationController f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationCallbacks.this.lambda$onAnimationFinished$6$RecentsAnimationCallbacks(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onAnimationFinished$6$RecentsAnimationCallbacks(RecentsAnimationController recentsAnimationController) {
        for (RecentsAnimationListener onRecentsAnimationFinished : getListeners()) {
            onRecentsAnimationFinished.onRecentsAnimationFinished(recentsAnimationController);
        }
    }

    private RecentsAnimationListener[] getListeners() {
        Set<RecentsAnimationListener> set = this.mListeners;
        return (RecentsAnimationListener[]) set.toArray(new RecentsAnimationListener[set.size()]);
    }
}
