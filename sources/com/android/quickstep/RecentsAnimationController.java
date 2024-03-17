package com.android.quickstep;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.WindowManagerGlobal;
import android.window.PictureInPictureSurfaceTransaction;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.RunnableList;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.RecentsAnimationControllerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.Objects;
import java.util.function.Consumer;

public class RecentsAnimationController {
    private static final String TAG = "RecentsAnimationController";
    private final boolean mAllowMinimizeSplitScreen;
    private final RecentsAnimationControllerCompat mController;
    private boolean mFinishRequested = false;
    private boolean mFinishTargetIsLauncher;
    private final Consumer<RecentsAnimationController> mOnFinishedListener;
    private RunnableList mPendingFinishCallbacks = new RunnableList();
    private boolean mSplitScreenMinimized = false;
    private boolean mUseLauncherSysBarFlags = false;

    public RecentsAnimationController(RecentsAnimationControllerCompat recentsAnimationControllerCompat, boolean z, Consumer<RecentsAnimationController> consumer) {
        this.mController = recentsAnimationControllerCompat;
        this.mOnFinishedListener = consumer;
        this.mAllowMinimizeSplitScreen = z;
    }

    public ThumbnailData screenshotTask(int i) {
        return this.mController.screenshotTask(i);
    }

    public void setUseLauncherSystemBarFlags(boolean z) {
        if (this.mUseLauncherSysBarFlags != z) {
            this.mUseLauncherSysBarFlags = z;
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RecentsAnimationController.this.lambda$setUseLauncherSystemBarFlags$0$RecentsAnimationController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setUseLauncherSystemBarFlags$0$RecentsAnimationController(boolean z) {
        if (!TaskAnimationManager.ENABLE_SHELL_TRANSITIONS) {
            this.mController.setAnimationTargetsBehindSystemBars(!z);
            return;
        }
        try {
            WindowManagerGlobal.getWindowManagerService().setRecentsAppBehindSystemBars(z);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to reach window manager", e);
        }
    }

    public void setSplitScreenMinimized(Context context, boolean z) {
        if (this.mAllowMinimizeSplitScreen && this.mSplitScreenMinimized != z) {
            this.mSplitScreenMinimized = z;
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(context, z) {
                public final /* synthetic */ Context f$0;
                public final /* synthetic */ boolean f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.f$0).setSplitScreenMinimized(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$removeTaskTarget$2$RecentsAnimationController(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        this.mController.removeTask(remoteAnimationTargetCompat.taskId);
    }

    public void removeTaskTarget(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(remoteAnimationTargetCompat) {
            public final /* synthetic */ RemoteAnimationTargetCompat f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationController.this.lambda$removeTaskTarget$2$RecentsAnimationController(this.f$1);
            }
        });
    }

    public void finishAnimationToHome() {
        finishController(true, (Runnable) null, false);
    }

    public void finishAnimationToApp() {
        finishController(false, (Runnable) null, false);
    }

    public void finish(boolean z, Runnable runnable) {
        finish(z, runnable, false);
    }

    public void finish(boolean z, Runnable runnable, boolean z2) {
        Preconditions.assertUIThread();
        finishController(z, runnable, z2);
    }

    public void finishController(boolean z, Runnable runnable, boolean z2) {
        if (this.mFinishRequested) {
            this.mPendingFinishCallbacks.add(runnable);
            return;
        }
        this.mFinishRequested = true;
        this.mFinishTargetIsLauncher = z;
        this.mOnFinishedListener.accept(this);
        this.mPendingFinishCallbacks.add(runnable);
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(z, z2) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RecentsAnimationController.this.lambda$finishController$3$RecentsAnimationController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$finishController$3$RecentsAnimationController(boolean z, boolean z2) {
        this.mController.finish(z, z2);
        InteractionJankMonitorWrapper.end(11);
        InteractionJankMonitorWrapper.end(9);
        LooperExecutor looperExecutor = Executors.MAIN_EXECUTOR;
        RunnableList runnableList = this.mPendingFinishCallbacks;
        Objects.requireNonNull(runnableList);
        looperExecutor.execute(new Runnable() {
            public final void run() {
                RunnableList.this.executeAllAndDestroy();
            }
        });
    }

    public void cleanupScreenshot() {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() {
            public final void run() {
                RecentsAnimationController.this.lambda$cleanupScreenshot$4$RecentsAnimationController();
            }
        });
    }

    public /* synthetic */ void lambda$cleanupScreenshot$4$RecentsAnimationController() {
        this.mController.cleanupScreenshot();
    }

    public void detachNavigationBarFromApp(boolean z) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationController.this.lambda$detachNavigationBarFromApp$5$RecentsAnimationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$detachNavigationBarFromApp$5$RecentsAnimationController(boolean z) {
        this.mController.detachNavigationBarFromApp(z);
    }

    public void animateNavigationBarToApp(long j) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(j) {
            public final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationController.this.lambda$animateNavigationBarToApp$6$RecentsAnimationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$animateNavigationBarToApp$6$RecentsAnimationController(long j) {
        this.mController.animateNavigationBarToApp(j);
    }

    public /* synthetic */ void lambda$setWillFinishToHome$7$RecentsAnimationController(boolean z) {
        this.mController.setWillFinishToHome(z);
    }

    public void setWillFinishToHome(boolean z) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationController.this.lambda$setWillFinishToHome$7$RecentsAnimationController(this.f$1);
            }
        });
    }

    public void setFinishTaskTransaction(int i, PictureInPictureSurfaceTransaction pictureInPictureSurfaceTransaction, SurfaceControl surfaceControl) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(i, pictureInPictureSurfaceTransaction, surfaceControl) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ PictureInPictureSurfaceTransaction f$2;
            public final /* synthetic */ SurfaceControl f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                RecentsAnimationController.this.lambda$setFinishTaskTransaction$8$RecentsAnimationController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$setFinishTaskTransaction$8$RecentsAnimationController(int i, PictureInPictureSurfaceTransaction pictureInPictureSurfaceTransaction, SurfaceControl surfaceControl) {
        this.mController.setFinishTaskTransaction(i, pictureInPictureSurfaceTransaction, surfaceControl);
    }

    public void enableInputConsumer() {
        Executors.UI_HELPER_EXECUTOR.submit(new Runnable() {
            public final void run() {
                RecentsAnimationController.this.lambda$enableInputConsumer$9$RecentsAnimationController();
            }
        });
    }

    public /* synthetic */ void lambda$enableInputConsumer$9$RecentsAnimationController() {
        this.mController.hideCurrentInputMethod();
        this.mController.setInputConsumerEnabled(true);
    }

    public RecentsAnimationControllerCompat getController() {
        return this.mController;
    }

    public boolean getFinishTargetIsLauncher() {
        return this.mFinishTargetIsLauncher;
    }
}
