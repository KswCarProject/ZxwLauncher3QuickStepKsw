package com.android.quickstep;

import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.window.IOnBackInvokedCallback;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController;
import com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController;
import com.android.systemui.shared.system.smartspace.SmartspaceState;
import com.android.wm.shell.back.IBackAnimation;
import com.android.wm.shell.onehanded.IOneHanded;
import com.android.wm.shell.pip.IPip;
import com.android.wm.shell.pip.IPipAnimationListener;
import com.android.wm.shell.recents.IRecentTasks;
import com.android.wm.shell.recents.IRecentTasksListener;
import com.android.wm.shell.splitscreen.ISplitScreen;
import com.android.wm.shell.splitscreen.ISplitScreenListener;
import com.android.wm.shell.startingsurface.IStartingWindow;
import com.android.wm.shell.startingsurface.IStartingWindowListener;
import com.android.wm.shell.transition.IShellTransitions;
import com.android.wm.shell.util.GroupedRecentTaskInfo;
import java.util.ArrayList;
import java.util.Arrays;

public class SystemUiProxy implements ISystemUiProxy, DisplayController.DisplayInfoChangeListener {
    public static final MainThreadInitializedObject<SystemUiProxy> INSTANCE = new MainThreadInitializedObject<>($$Lambda$iAxkpMrj0WdrsxUoJw1biy3lxbw.INSTANCE);
    private static final String TAG = "SystemUiProxy";
    private IBackAnimation mBackAnimation;
    private IOnBackInvokedCallback mBackToLauncherCallback;
    private boolean mHasNavButtonAlphaBeenSet = false;
    private float mLastNavButtonAlpha;
    private boolean mLastNavButtonAnimate;
    private int mLastShelfHeight;
    private boolean mLastShelfVisible;
    private int mLastSystemUiStateFlags;
    private IOneHanded mOneHanded;
    private ILauncherUnlockAnimationController mPendingLauncherUnlockAnimationController;
    private Runnable mPendingSetNavButtonAlpha = null;
    private IPip mPip;
    private IPipAnimationListener mPipAnimationListener;
    private IRecentTasks mRecentTasks;
    private IRecentTasksListener mRecentTasksListener;
    private final ArrayList<RemoteTransitionCompat> mRemoteTransitions = new ArrayList<>();
    private IShellTransitions mShellTransitions;
    private ISplitScreen mSplitScreen;
    private ISplitScreenListener mSplitScreenListener;
    private IStartingWindow mStartingWindow;
    private IStartingWindowListener mStartingWindowListener;
    private ISystemUiProxy mSystemUiProxy;
    private final IBinder.DeathRecipient mSystemUiProxyDeathRecipient = new IBinder.DeathRecipient() {
        public final void binderDied() {
            SystemUiProxy.this.lambda$new$1$SystemUiProxy();
        }
    };
    private ISysuiUnlockAnimationController mSysuiUnlockAnimationController;

    public IBinder asBinder() {
        return null;
    }

    public /* synthetic */ void lambda$new$1$SystemUiProxy() {
        Executors.MAIN_EXECUTOR.execute(new Runnable() {
            public final void run() {
                SystemUiProxy.this.lambda$new$0$SystemUiProxy();
            }
        });
    }

    public SystemUiProxy(Context context) {
        DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).addChangeListener(this);
    }

    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
        if ((i & 16) != 0) {
            lambda$setNavBarButtonAlpha$2$SystemUiProxy(1.0f, false);
        }
    }

    public void onBackPressed() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onBackPressed();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onBackPressed", e);
            }
        }
    }

    public void onImeSwitcherPressed() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onImeSwitcherPressed();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onImeSwitcherPressed", e);
            }
        }
    }

    public void setHomeRotationEnabled(boolean z) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.setHomeRotationEnabled(z);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onBackPressed", e);
            }
        }
    }

    public void setProxy(ISystemUiProxy iSystemUiProxy, IPip iPip, ISplitScreen iSplitScreen, IOneHanded iOneHanded, IShellTransitions iShellTransitions, IStartingWindow iStartingWindow, IRecentTasks iRecentTasks, ISysuiUnlockAnimationController iSysuiUnlockAnimationController, IBackAnimation iBackAnimation) {
        IOnBackInvokedCallback iOnBackInvokedCallback;
        unlinkToDeath();
        this.mSystemUiProxy = iSystemUiProxy;
        this.mPip = iPip;
        this.mSplitScreen = iSplitScreen;
        this.mOneHanded = iOneHanded;
        this.mShellTransitions = iShellTransitions;
        this.mStartingWindow = iStartingWindow;
        this.mSysuiUnlockAnimationController = iSysuiUnlockAnimationController;
        this.mRecentTasks = iRecentTasks;
        this.mBackAnimation = iBackAnimation;
        linkToDeath();
        IPipAnimationListener iPipAnimationListener = this.mPipAnimationListener;
        if (!(iPipAnimationListener == null || this.mPip == null)) {
            setPinnedStackAnimationListener(iPipAnimationListener);
        }
        ISplitScreenListener iSplitScreenListener = this.mSplitScreenListener;
        if (!(iSplitScreenListener == null || this.mSplitScreen == null)) {
            registerSplitScreenListener(iSplitScreenListener);
        }
        IStartingWindowListener iStartingWindowListener = this.mStartingWindowListener;
        if (!(iStartingWindowListener == null || this.mStartingWindow == null)) {
            setStartingWindowListener(iStartingWindowListener);
        }
        ILauncherUnlockAnimationController iLauncherUnlockAnimationController = this.mPendingLauncherUnlockAnimationController;
        if (!(iLauncherUnlockAnimationController == null || this.mSysuiUnlockAnimationController == null)) {
            setLauncherUnlockAnimationController(iLauncherUnlockAnimationController);
            this.mPendingLauncherUnlockAnimationController = null;
        }
        for (int size = this.mRemoteTransitions.size() - 1; size >= 0; size--) {
            registerRemoteTransition(this.mRemoteTransitions.get(size));
        }
        IRecentTasksListener iRecentTasksListener = this.mRecentTasksListener;
        if (!(iRecentTasksListener == null || this.mRecentTasks == null)) {
            registerRecentTasksListener(iRecentTasksListener);
        }
        if (!(this.mBackAnimation == null || (iOnBackInvokedCallback = this.mBackToLauncherCallback) == null)) {
            setBackToLauncherCallback(iOnBackInvokedCallback);
        }
        Runnable runnable = this.mPendingSetNavButtonAlpha;
        if (runnable != null) {
            runnable.run();
            this.mPendingSetNavButtonAlpha = null;
        }
    }

    /* renamed from: clearProxy */
    public void lambda$new$0$SystemUiProxy() {
        setProxy((ISystemUiProxy) null, (IPip) null, (ISplitScreen) null, (IOneHanded) null, (IShellTransitions) null, (IStartingWindow) null, (IRecentTasks) null, (ISysuiUnlockAnimationController) null, (IBackAnimation) null);
    }

    public void setLastSystemUiStateFlags(int i) {
        this.mLastSystemUiStateFlags = i;
    }

    public int getLastSystemUiStateFlags() {
        return this.mLastSystemUiStateFlags;
    }

    public boolean isActive() {
        return this.mSystemUiProxy != null;
    }

    private void linkToDeath() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.asBinder().linkToDeath(this.mSystemUiProxyDeathRecipient, 0);
            } catch (RemoteException unused) {
                Log.e(TAG, "Failed to link sysui proxy death recipient");
            }
        }
    }

    private void unlinkToDeath() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            iSystemUiProxy.asBinder().unlinkToDeath(this.mSystemUiProxyDeathRecipient, 0);
        }
    }

    public void startScreenPinning(int i) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.startScreenPinning(i);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call startScreenPinning", e);
            }
        }
    }

    public void onOverviewShown(boolean z) {
        onOverviewShown(z, TAG);
    }

    public void onOverviewShown(boolean z, String str) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onOverviewShown(z);
            } catch (RemoteException e) {
                Log.w(str, "Failed call onOverviewShown from: " + (z ? "home" : "app"), e);
            }
        }
    }

    public Rect getNonMinimizedSplitScreenSecondaryBounds() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy == null) {
            return null;
        }
        try {
            return iSystemUiProxy.getNonMinimizedSplitScreenSecondaryBounds();
        } catch (RemoteException e) {
            Log.w(TAG, "Failed call getNonMinimizedSplitScreenSecondaryBounds", e);
            return null;
        }
    }

    public float getLastNavButtonAlpha() {
        return this.mLastNavButtonAlpha;
    }

    /* renamed from: setNavBarButtonAlpha */
    public void lambda$setNavBarButtonAlpha$2$SystemUiProxy(float f, boolean z) {
        if ((Float.compare(f, this.mLastNavButtonAlpha) == 0 && z == this.mLastNavButtonAnimate && this.mHasNavButtonAlphaBeenSet) ? false : true) {
            ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
            if (iSystemUiProxy == null) {
                this.mPendingSetNavButtonAlpha = new Runnable(f, z) {
                    public final /* synthetic */ float f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        SystemUiProxy.this.lambda$setNavBarButtonAlpha$2$SystemUiProxy(this.f$1, this.f$2);
                    }
                };
                return;
            }
            this.mLastNavButtonAlpha = f;
            this.mLastNavButtonAnimate = z;
            this.mHasNavButtonAlphaBeenSet = true;
            try {
                iSystemUiProxy.setNavBarButtonAlpha(f, z);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setNavBarButtonAlpha", e);
            }
        }
    }

    public void onStatusBarMotionEvent(MotionEvent motionEvent) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onStatusBarMotionEvent(motionEvent);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onStatusBarMotionEvent", e);
            }
        }
    }

    public void onAssistantProgress(float f) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onAssistantProgress(f);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onAssistantProgress with progress: " + f, e);
            }
        }
    }

    public void onAssistantGestureCompletion(float f) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.onAssistantGestureCompletion(f);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onAssistantGestureCompletion", e);
            }
        }
    }

    public void startAssistant(Bundle bundle) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.startAssistant(bundle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call startAssistant", e);
            }
        }
    }

    public void notifyAccessibilityButtonClicked(int i) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyAccessibilityButtonClicked(i);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyAccessibilityButtonClicked", e);
            }
        }
    }

    public void notifyAccessibilityButtonLongClicked() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyAccessibilityButtonLongClicked();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyAccessibilityButtonLongClicked", e);
            }
        }
    }

    public void stopScreenPinning() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.stopScreenPinning();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call stopScreenPinning", e);
            }
        }
    }

    public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.handleImageAsScreenshot(bitmap, rect, insets, i);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call handleImageAsScreenshot", e);
            }
        }
    }

    public void setSplitScreenMinimized(boolean z) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.setSplitScreenMinimized(z);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setSplitScreenMinimized", e);
            }
        }
    }

    public void notifySwipeUpGestureStarted() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifySwipeUpGestureStarted();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifySwipeUpGestureStarted", e);
            }
        }
    }

    public void notifySwipeToHomeFinished() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifySwipeToHomeFinished();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifySwipeToHomeFinished", e);
            }
        }
    }

    public void notifyPrioritizedRotation(int i) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyPrioritizedRotation(i);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyPrioritizedRotation with arg: " + i, e);
            }
        }
    }

    public void notifyTaskbarStatus(boolean z, boolean z2) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyTaskbarStatus(z, z2);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyTaskbarStatus with arg: " + z + ", " + z2, e);
            }
        }
    }

    public void notifyTaskbarAutohideSuspend(boolean z) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.notifyTaskbarAutohideSuspend(z);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifyTaskbarAutohideSuspend with arg: " + z, e);
            }
        }
    }

    public void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.handleImageBundleAsScreenshot(bundle, rect, insets, taskKey);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call handleImageBundleAsScreenshot");
            }
        }
    }

    public void expandNotificationPanel() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.expandNotificationPanel();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call expandNotificationPanel", e);
            }
        }
    }

    public void toggleNotificationPanel() {
        ISystemUiProxy iSystemUiProxy = this.mSystemUiProxy;
        if (iSystemUiProxy != null) {
            try {
                iSystemUiProxy.toggleNotificationPanel();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call toggleNotificationPanel", e);
            }
        }
    }

    public void setShelfHeight(boolean z, int i) {
        boolean z2 = (z == this.mLastShelfVisible && i == this.mLastShelfHeight) ? false : true;
        IPip iPip = this.mPip;
        if (iPip != null && z2) {
            this.mLastShelfVisible = z;
            this.mLastShelfHeight = i;
            try {
                iPip.setShelfHeight(z, i);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setShelfHeight visible: " + z + " height: " + i, e);
            }
        }
    }

    public void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) {
        IPip iPip = this.mPip;
        if (iPip != null) {
            try {
                iPip.setPinnedStackAnimationListener(iPipAnimationListener);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setPinnedStackAnimationListener", e);
            }
        }
        this.mPipAnimationListener = iPipAnimationListener;
    }

    public Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) {
        IPip iPip = this.mPip;
        if (iPip == null) {
            return null;
        }
        try {
            return iPip.startSwipePipToHome(componentName, activityInfo, pictureInPictureParams, i, i2);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed call startSwipePipToHome", e);
            return null;
        }
    }

    public void stopSwipePipToHome(int i, ComponentName componentName, Rect rect, SurfaceControl surfaceControl) {
        IPip iPip = this.mPip;
        if (iPip != null) {
            try {
                iPip.stopSwipePipToHome(i, componentName, rect, surfaceControl);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call stopSwipePipToHome");
            }
        }
    }

    public void registerSplitScreenListener(ISplitScreenListener iSplitScreenListener) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.registerSplitScreenListener(iSplitScreenListener);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call registerSplitScreenListener");
            }
        }
        this.mSplitScreenListener = iSplitScreenListener;
    }

    public void unregisterSplitScreenListener(ISplitScreenListener iSplitScreenListener) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.unregisterSplitScreenListener(iSplitScreenListener);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call unregisterSplitScreenListener");
            }
        }
        this.mSplitScreenListener = null;
    }

    public void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, float f, RemoteTransitionCompat remoteTransitionCompat) {
        if (this.mSystemUiProxy != null) {
            try {
                this.mSplitScreen.startTasks(i, bundle, i2, bundle2, i3, f, remoteTransitionCompat.getTransition());
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startTask");
            }
        }
    }

    public void startTasksWithLegacyTransition(int i, Bundle bundle, int i2, Bundle bundle2, int i3, float f, RemoteAnimationAdapter remoteAnimationAdapter) {
        if (this.mSystemUiProxy != null) {
            try {
                this.mSplitScreen.startTasksWithLegacyTransition(i, bundle, i2, bundle2, i3, f, remoteAnimationAdapter);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startTasksWithLegacyTransition");
            }
        }
    }

    public void startIntentAndTaskWithLegacyTransition(PendingIntent pendingIntent, Intent intent, int i, Bundle bundle, Bundle bundle2, int i2, float f, RemoteAnimationAdapter remoteAnimationAdapter) {
        if (this.mSystemUiProxy != null) {
            try {
                this.mSplitScreen.startIntentAndTaskWithLegacyTransition(pendingIntent, intent, i, bundle, bundle2, i2, f, remoteAnimationAdapter);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startTasksWithLegacyTransition");
            }
        }
    }

    public void startShortcut(String str, String str2, int i, Bundle bundle, UserHandle userHandle) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.startShortcut(str, str2, i, bundle, userHandle);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startShortcut");
            }
        }
    }

    public void startIntent(PendingIntent pendingIntent, Intent intent, int i, Bundle bundle) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.startIntent(pendingIntent, intent, i, bundle);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call startIntent");
            }
        }
    }

    public void removeFromSideStage(int i) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen != null) {
            try {
                iSplitScreen.removeFromSideStage(i);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call removeFromSideStage");
            }
        }
    }

    public RemoteAnimationTarget[] onGoingToRecentsLegacy(RemoteAnimationTarget[] remoteAnimationTargetArr) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen == null) {
            return null;
        }
        try {
            return iSplitScreen.onGoingToRecentsLegacy(remoteAnimationTargetArr);
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed call onGoingToRecentsLegacy");
            return null;
        }
    }

    public RemoteAnimationTarget[] onStartingSplitLegacy(RemoteAnimationTarget[] remoteAnimationTargetArr) {
        ISplitScreen iSplitScreen = this.mSplitScreen;
        if (iSplitScreen == null) {
            return null;
        }
        try {
            return iSplitScreen.onStartingSplitLegacy(remoteAnimationTargetArr);
        } catch (RemoteException unused) {
            Log.w(TAG, "Failed call onStartingSplitLegacy");
            return null;
        }
    }

    public void startOneHandedMode() {
        IOneHanded iOneHanded = this.mOneHanded;
        if (iOneHanded != null) {
            try {
                iOneHanded.startOneHanded();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call startOneHandedMode", e);
            }
        }
    }

    public void stopOneHandedMode() {
        IOneHanded iOneHanded = this.mOneHanded;
        if (iOneHanded != null) {
            try {
                iOneHanded.stopOneHanded();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call stopOneHandedMode", e);
            }
        }
    }

    public void registerRemoteTransition(RemoteTransitionCompat remoteTransitionCompat) {
        IShellTransitions iShellTransitions = this.mShellTransitions;
        if (iShellTransitions != null) {
            try {
                iShellTransitions.registerRemote(remoteTransitionCompat.getFilter(), remoteTransitionCompat.getTransition());
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call registerRemoteTransition");
            }
        }
        if (!this.mRemoteTransitions.contains(remoteTransitionCompat)) {
            this.mRemoteTransitions.add(remoteTransitionCompat);
        }
    }

    public void unregisterRemoteTransition(RemoteTransitionCompat remoteTransitionCompat) {
        IShellTransitions iShellTransitions = this.mShellTransitions;
        if (iShellTransitions != null) {
            try {
                iShellTransitions.unregisterRemote(remoteTransitionCompat.getTransition());
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call registerRemoteTransition");
            }
        }
        this.mRemoteTransitions.remove(remoteTransitionCompat);
    }

    public void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) {
        IStartingWindow iStartingWindow = this.mStartingWindow;
        if (iStartingWindow != null) {
            try {
                iStartingWindow.setStartingWindowListener(iStartingWindowListener);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setStartingWindowListener", e);
            }
        }
        this.mStartingWindowListener = iStartingWindowListener;
    }

    public void setLauncherUnlockAnimationController(ILauncherUnlockAnimationController iLauncherUnlockAnimationController) {
        ISysuiUnlockAnimationController iSysuiUnlockAnimationController = this.mSysuiUnlockAnimationController;
        if (iSysuiUnlockAnimationController != null) {
            try {
                iSysuiUnlockAnimationController.setLauncherUnlockController(iLauncherUnlockAnimationController);
                if (iLauncherUnlockAnimationController != null) {
                    iLauncherUnlockAnimationController.dispatchSmartspaceStateToSysui();
                }
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call setStartingWindowListener", e);
            }
        } else {
            this.mPendingLauncherUnlockAnimationController = iLauncherUnlockAnimationController;
        }
    }

    public void notifySysuiSmartspaceStateUpdated(SmartspaceState smartspaceState) {
        ISysuiUnlockAnimationController iSysuiUnlockAnimationController = this.mSysuiUnlockAnimationController;
        if (iSysuiUnlockAnimationController != null) {
            try {
                iSysuiUnlockAnimationController.onLauncherSmartspaceStateUpdated(smartspaceState);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call notifySysuiSmartspaceStateUpdated", e);
                e.printStackTrace();
            }
        }
    }

    public void registerRecentTasksListener(IRecentTasksListener iRecentTasksListener) {
        IRecentTasks iRecentTasks = this.mRecentTasks;
        if (iRecentTasks != null) {
            try {
                iRecentTasks.registerRecentTasksListener(iRecentTasksListener);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call registerRecentTasksListener", e);
            }
        }
        this.mRecentTasksListener = iRecentTasksListener;
    }

    public void unregisterRecentTasksListener(IRecentTasksListener iRecentTasksListener) {
        IRecentTasks iRecentTasks = this.mRecentTasks;
        if (iRecentTasks != null) {
            try {
                iRecentTasks.unregisterRecentTasksListener(iRecentTasksListener);
            } catch (RemoteException unused) {
                Log.w(TAG, "Failed call unregisterRecentTasksListener");
            }
        }
        this.mRecentTasksListener = null;
    }

    public void setBackToLauncherCallback(IOnBackInvokedCallback iOnBackInvokedCallback) {
        this.mBackToLauncherCallback = iOnBackInvokedCallback;
        IBackAnimation iBackAnimation = this.mBackAnimation;
        if (iBackAnimation != null) {
            try {
                iBackAnimation.setBackToLauncherCallback(iOnBackInvokedCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed call setBackToLauncherCallback", e);
            }
        }
    }

    public void clearBackToLauncherCallback(IOnBackInvokedCallback iOnBackInvokedCallback) {
        if (this.mBackToLauncherCallback == iOnBackInvokedCallback) {
            this.mBackToLauncherCallback = null;
            IBackAnimation iBackAnimation = this.mBackAnimation;
            if (iBackAnimation != null) {
                try {
                    iBackAnimation.clearBackToLauncherCallback();
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed call clearBackToLauncherCallback", e);
                }
            }
        }
    }

    public void onBackToLauncherAnimationFinished() {
        IBackAnimation iBackAnimation = this.mBackAnimation;
        if (iBackAnimation != null) {
            try {
                iBackAnimation.onBackToLauncherAnimationFinished();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call onBackAnimationFinished", e);
            }
        }
    }

    public ArrayList<GroupedRecentTaskInfo> getRecentTasks(int i, int i2) {
        IRecentTasks iRecentTasks = this.mRecentTasks;
        if (iRecentTasks != null) {
            try {
                GroupedRecentTaskInfo[] recentTasks = iRecentTasks.getRecentTasks(i, 2, i2);
                if (recentTasks == null) {
                    return new ArrayList<>();
                }
                return new ArrayList<>(Arrays.asList(recentTasks));
            } catch (RemoteException e) {
                Log.w(TAG, "Failed call getRecentTasks", e);
            }
        }
        return new ArrayList<>();
    }
}
