package com.android.quickstep;

import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Region;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.view.MotionEvent;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.SettingsCache;
import com.android.quickstep.TopTaskTracker;
import com.android.quickstep.util.NavBarPosition;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SystemGestureExclusionListenerCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class RecentsAnimationDeviceState implements DisplayController.DisplayInfoChangeListener {
    static final String SUPPORT_ONE_HANDED_MODE = "ro.support_one_handed_mode";
    private boolean mAssistantAvailable;
    private float mAssistantVisibility;
    private final boolean mCanImeRenderGesturalNavButtons;
    private final Context mContext;
    private final Region mDeferredGestureRegion;
    private final DisplayController mDisplayController;
    private final int mDisplayId;
    private SystemGestureExclusionListenerCompat mExclusionListener;
    /* access modifiers changed from: private */
    public Region mExclusionRegion;
    private int mGestureBlockingTaskId;
    private boolean mIsOneHandedModeEnabled;
    private final boolean mIsOneHandedModeSupported;
    private boolean mIsSwipeToNotificationEnabled;
    private boolean mIsUserSetupComplete;
    /* access modifiers changed from: private */
    public boolean mIsUserUnlocked;
    private DisplayController.NavigationMode mMode;
    private NavBarPosition mNavBarPosition;
    private final ArrayList<Runnable> mOnDestroyActions;
    /* access modifiers changed from: private */
    public boolean mPipIsActive;
    private final TaskStackChangeListener mPipListener;
    private final RotationTouchHelper mRotationTouchHelper;
    private int mSystemUiStateFlags;
    private final ArrayList<Runnable> mUserUnlockedActions;
    private final BroadcastReceiver mUserUnlockedReceiver;

    public RecentsAnimationDeviceState(Context context) {
        this(context, false);
    }

    public RecentsAnimationDeviceState(Context context, boolean z) {
        this.mCanImeRenderGesturalNavButtons = InputMethodService.canImeRenderGesturalNavButtons();
        this.mOnDestroyActions = new ArrayList<>();
        this.mMode = DisplayController.NavigationMode.THREE_BUTTONS;
        this.mDeferredGestureRegion = new Region();
        this.mUserUnlockedActions = new ArrayList<>();
        AnonymousClass1 r0 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                    boolean unused = RecentsAnimationDeviceState.this.mIsUserUnlocked = true;
                    RecentsAnimationDeviceState.this.notifyUserUnlocked();
                }
            }
        };
        this.mUserUnlockedReceiver = r0;
        this.mGestureBlockingTaskId = -1;
        this.mContext = context;
        DisplayController displayController = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        this.mDisplayController = displayController;
        boolean z2 = false;
        this.mDisplayId = 0;
        boolean z3 = SystemProperties.getBoolean(SUPPORT_ONE_HANDED_MODE, false);
        this.mIsOneHandedModeSupported = z3;
        RotationTouchHelper rotationTouchHelper = RotationTouchHelper.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        this.mRotationTouchHelper = rotationTouchHelper;
        if (z) {
            rotationTouchHelper.init();
            Objects.requireNonNull(rotationTouchHelper);
            runOnDestroy(new Runnable() {
                public final void run() {
                    RotationTouchHelper.this.destroy();
                }
            });
        }
        boolean isUserUnlocked = ((UserManager) context.getSystemService(UserManager.class)).isUserUnlocked(Process.myUserHandle());
        this.mIsUserUnlocked = isUserUnlocked;
        if (!isUserUnlocked) {
            context.registerReceiver(r0, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        }
        runOnDestroy(new Runnable() {
            public final void run() {
                RecentsAnimationDeviceState.this.lambda$new$0$RecentsAnimationDeviceState();
            }
        });
        AnonymousClass2 r7 = new SystemGestureExclusionListenerCompat(0) {
            public void onExclusionChanged(Region region) {
                Region unused = RecentsAnimationDeviceState.this.mExclusionRegion = region;
            }
        };
        this.mExclusionListener = r7;
        Objects.requireNonNull(r7);
        runOnDestroy(new Runnable() {
            public final void run() {
                SystemGestureExclusionListenerCompat.this.unregister();
            }
        });
        displayController.addChangeListener(this);
        onDisplayInfoChanged(context, displayController.getInfo(), 31);
        runOnDestroy(new Runnable() {
            public final void run() {
                RecentsAnimationDeviceState.this.lambda$new$1$RecentsAnimationDeviceState();
            }
        });
        SettingsCache settingsCache = SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        if (z3) {
            Uri uriFor = Settings.Secure.getUriFor(SettingsCache.ONE_HANDED_ENABLED);
            $$Lambda$RecentsAnimationDeviceState$aiRcC9W6xdYVWXYxbAjlNzslJdI r02 = new SettingsCache.OnChangeListener() {
                public final void onSettingsChanged(boolean z) {
                    RecentsAnimationDeviceState.this.lambda$new$2$RecentsAnimationDeviceState(z);
                }
            };
            settingsCache.register(uriFor, r02);
            this.mIsOneHandedModeEnabled = settingsCache.getValue(uriFor);
            runOnDestroy(new Runnable(uriFor, r02) {
                public final /* synthetic */ Uri f$1;
                public final /* synthetic */ SettingsCache.OnChangeListener f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SettingsCache.this.unregister(this.f$1, this.f$2);
                }
            });
        } else {
            this.mIsOneHandedModeEnabled = false;
        }
        Uri uriFor2 = Settings.Secure.getUriFor(SettingsCache.ONE_HANDED_SWIPE_BOTTOM_TO_NOTIFICATION_ENABLED);
        $$Lambda$RecentsAnimationDeviceState$fC7k00KNAUvRt20TpYKqamAS6M r03 = new SettingsCache.OnChangeListener() {
            public final void onSettingsChanged(boolean z) {
                RecentsAnimationDeviceState.this.lambda$new$4$RecentsAnimationDeviceState(z);
            }
        };
        settingsCache.register(uriFor2, r03);
        this.mIsSwipeToNotificationEnabled = settingsCache.getValue(uriFor2);
        runOnDestroy(new Runnable(uriFor2, r03) {
            public final /* synthetic */ Uri f$1;
            public final /* synthetic */ SettingsCache.OnChangeListener f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SettingsCache.this.unregister(this.f$1, this.f$2);
            }
        });
        Uri uriFor3 = Settings.Secure.getUriFor("user_setup_complete");
        boolean value = settingsCache.getValue(uriFor3, 0);
        this.mIsUserSetupComplete = value;
        if (!value) {
            $$Lambda$RecentsAnimationDeviceState$zBd3sO7c27aYInGMe0Zliyba7aA r04 = new SettingsCache.OnChangeListener() {
                public final void onSettingsChanged(boolean z) {
                    RecentsAnimationDeviceState.this.lambda$new$6$RecentsAnimationDeviceState(z);
                }
            };
            settingsCache.register(uriFor3, r04);
            runOnDestroy(new Runnable(uriFor3, r04) {
                public final /* synthetic */ Uri f$1;
                public final /* synthetic */ SettingsCache.OnChangeListener f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SettingsCache.this.unregister(this.f$1, this.f$2);
                }
            });
        }
        try {
            this.mPipIsActive = ActivityTaskManager.getService().getRootTaskInfo(2, 0) != null ? true : z2;
        } catch (RemoteException unused) {
        }
        AnonymousClass3 r6 = new TaskStackChangeListener() {
            public void onActivityPinned(String str, int i, int i2, int i3) {
                boolean unused = RecentsAnimationDeviceState.this.mPipIsActive = true;
            }

            public void onActivityUnpinned() {
                boolean unused = RecentsAnimationDeviceState.this.mPipIsActive = false;
            }
        };
        this.mPipListener = r6;
        TaskStackChangeListeners.getInstance().registerTaskStackListener(r6);
        runOnDestroy(new Runnable() {
            public final void run() {
                RecentsAnimationDeviceState.this.lambda$new$8$RecentsAnimationDeviceState();
            }
        });
    }

    public /* synthetic */ void lambda$new$0$RecentsAnimationDeviceState() {
        Utilities.unregisterReceiverSafely(this.mContext, this.mUserUnlockedReceiver);
    }

    public /* synthetic */ void lambda$new$1$RecentsAnimationDeviceState() {
        this.mDisplayController.removeChangeListener(this);
    }

    public /* synthetic */ void lambda$new$2$RecentsAnimationDeviceState(boolean z) {
        this.mIsOneHandedModeEnabled = z;
    }

    public /* synthetic */ void lambda$new$4$RecentsAnimationDeviceState(boolean z) {
        this.mIsSwipeToNotificationEnabled = z;
    }

    public /* synthetic */ void lambda$new$6$RecentsAnimationDeviceState(boolean z) {
        this.mIsUserSetupComplete = z;
    }

    public /* synthetic */ void lambda$new$8$RecentsAnimationDeviceState() {
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mPipListener);
    }

    private void runOnDestroy(Runnable runnable) {
        this.mOnDestroyActions.add(runnable);
    }

    public void destroy() {
        Iterator<Runnable> it = this.mOnDestroyActions.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
    }

    public void addNavigationModeChangedCallback(Runnable runnable) {
        $$Lambda$RecentsAnimationDeviceState$NoQmcrHAvTEPSvv2GwcFSalEqlQ r0 = new DisplayController.DisplayInfoChangeListener(runnable) {
            public final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
                RecentsAnimationDeviceState.lambda$addNavigationModeChangedCallback$9(this.f$0, context, info, i);
            }
        };
        this.mDisplayController.addChangeListener(r0);
        runnable.run();
        runOnDestroy(new Runnable(r0) {
            public final /* synthetic */ DisplayController.DisplayInfoChangeListener f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsAnimationDeviceState.this.lambda$addNavigationModeChangedCallback$10$RecentsAnimationDeviceState(this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$addNavigationModeChangedCallback$9(Runnable runnable, Context context, DisplayController.Info info, int i) {
        if ((i & 16) != 0) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$addNavigationModeChangedCallback$10$RecentsAnimationDeviceState(DisplayController.DisplayInfoChangeListener displayInfoChangeListener) {
        this.mDisplayController.removeChangeListener(displayInfoChangeListener);
    }

    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
        if ((i & 18) != 0) {
            this.mMode = info.navigationMode;
            this.mNavBarPosition = new NavBarPosition(this.mMode, info);
            if (this.mMode == DisplayController.NavigationMode.NO_BUTTON) {
                this.mExclusionListener.register();
            } else {
                this.mExclusionListener.unregister();
            }
        }
    }

    public void onOneHandedModeChanged(int i) {
        this.mRotationTouchHelper.setGesturalHeight(i);
    }

    public NavBarPosition getNavBarPosition() {
        return this.mNavBarPosition;
    }

    public boolean isFullyGesturalNavMode() {
        return this.mMode == DisplayController.NavigationMode.NO_BUTTON;
    }

    public boolean isGesturalNavMode() {
        return this.mMode.hasGestures;
    }

    public boolean isTwoButtonNavMode() {
        return this.mMode == DisplayController.NavigationMode.TWO_BUTTONS;
    }

    public boolean isButtonNavMode() {
        return this.mMode == DisplayController.NavigationMode.THREE_BUTTONS;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public void runOnUserUnlocked(Runnable runnable) {
        if (this.mIsUserUnlocked) {
            runnable.run();
        } else {
            this.mUserUnlockedActions.add(runnable);
        }
    }

    public boolean isUserUnlocked() {
        return this.mIsUserUnlocked;
    }

    public boolean isUserSetupComplete() {
        return this.mIsUserSetupComplete;
    }

    /* access modifiers changed from: private */
    public void notifyUserUnlocked() {
        Iterator<Runnable> it = this.mUserUnlockedActions.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mUserUnlockedActions.clear();
        Utilities.unregisterReceiverSafely(this.mContext, this.mUserUnlockedReceiver);
    }

    public void setGestureBlockingTaskId(int i) {
        this.mGestureBlockingTaskId = i;
    }

    public boolean isGestureBlockedTask(TopTaskTracker.CachedTaskInfo cachedTaskInfo) {
        return cachedTaskInfo != null && cachedTaskInfo.getTaskId() == this.mGestureBlockingTaskId;
    }

    public void setSystemUiFlags(int i) {
        this.mSystemUiStateFlags = i;
    }

    public int getSystemUiStateFlags() {
        return this.mSystemUiStateFlags;
    }

    public boolean canStartSystemGesture() {
        int i = this.mSystemUiStateFlags;
        if (!((i & 2) == 0 || (i & 131072) != 0 || this.mRotationTouchHelper.isTaskListFrozen())) {
            return false;
        }
        int i2 = this.mSystemUiStateFlags;
        if ((i2 & 4) != 0 || (i2 & 2048) != 0 || (524288 & i2) != 0) {
            return false;
        }
        if ((i2 & 256) == 0 || (i2 & 128) == 0) {
            return true;
        }
        return false;
    }

    public boolean isKeyguardShowingOccluded() {
        return (this.mSystemUiStateFlags & 512) != 0;
    }

    public boolean isScreenPinningActive() {
        return (this.mSystemUiStateFlags & 1) != 0;
    }

    public boolean isAssistantGestureIsConstrained() {
        return (this.mSystemUiStateFlags & 8192) != 0;
    }

    public boolean isBubblesExpanded() {
        return (this.mSystemUiStateFlags & 16384) != 0;
    }

    public boolean isNotificationPanelExpanded() {
        return (this.mSystemUiStateFlags & 4) != 0;
    }

    public boolean isSystemUiDialogShowing() {
        return (this.mSystemUiStateFlags & 32768) != 0;
    }

    public boolean isLockToAppActive() {
        return ActivityManagerWrapper.getInstance().isLockToAppActive();
    }

    public boolean isAccessibilityMenuAvailable() {
        return (this.mSystemUiStateFlags & 16) != 0;
    }

    public boolean isAccessibilityMenuShortcutAvailable() {
        return (this.mSystemUiStateFlags & 32) != 0;
    }

    public boolean isHomeDisabled() {
        return (this.mSystemUiStateFlags & 256) != 0;
    }

    public boolean isOverviewDisabled() {
        return (this.mSystemUiStateFlags & 128) != 0;
    }

    public boolean isOneHandedModeActive() {
        return (this.mSystemUiStateFlags & 65536) != 0;
    }

    public void setDeferredGestureRegion(Region region) {
        this.mDeferredGestureRegion.set(region);
    }

    public boolean isInDeferredGestureRegion(MotionEvent motionEvent) {
        return this.mDeferredGestureRegion.contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    public boolean isInExclusionRegion(MotionEvent motionEvent) {
        Region region = this.mExclusionRegion;
        return this.mMode == DisplayController.NavigationMode.NO_BUTTON && region != null && region.contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    public void setAssistantAvailable(boolean z) {
        this.mAssistantAvailable = z;
    }

    public void setAssistantVisibility(float f) {
        this.mAssistantVisibility = f;
    }

    public float getAssistantVisibility() {
        return this.mAssistantVisibility;
    }

    public boolean canTriggerAssistantAction(MotionEvent motionEvent) {
        return this.mAssistantAvailable && !QuickStepContract.isAssistantGestureDisabled(this.mSystemUiStateFlags) && this.mRotationTouchHelper.touchInAssistantRegion(motionEvent) && !isLockToAppActive();
    }

    public boolean canTriggerOneHandedAction(MotionEvent motionEvent) {
        if (!this.mIsOneHandedModeSupported || !this.mIsOneHandedModeEnabled) {
            return false;
        }
        DisplayController.Info info = this.mDisplayController.getInfo();
        if (!this.mRotationTouchHelper.touchInOneHandedModeRegion(motionEvent) || info.currentSize.x >= info.currentSize.y) {
            return false;
        }
        return true;
    }

    public boolean isOneHandedModeEnabled() {
        return this.mIsOneHandedModeEnabled;
    }

    public boolean isSwipeToNotificationEnabled() {
        return this.mIsSwipeToNotificationEnabled;
    }

    public boolean isPipActive() {
        return this.mPipIsActive;
    }

    public RotationTouchHelper getRotationTouchHelper() {
        return this.mRotationTouchHelper;
    }

    public boolean isImeRenderingNavButtons() {
        return this.mCanImeRenderGesturalNavButtons && this.mMode == DisplayController.NavigationMode.NO_BUTTON && (this.mSystemUiStateFlags & 262144) != 0;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("DeviceState:");
        printWriter.println("  canStartSystemGesture=" + canStartSystemGesture());
        printWriter.println("  systemUiFlags=" + this.mSystemUiStateFlags);
        printWriter.println("  systemUiFlagsDesc=" + QuickStepContract.getSystemUiStateString(this.mSystemUiStateFlags));
        printWriter.println("  assistantAvailable=" + this.mAssistantAvailable);
        printWriter.println("  assistantDisabled=" + QuickStepContract.isAssistantGestureDisabled(this.mSystemUiStateFlags));
        printWriter.println("  isUserUnlocked=" + this.mIsUserUnlocked);
        printWriter.println("  isOneHandedModeEnabled=" + this.mIsOneHandedModeEnabled);
        printWriter.println("  isSwipeToNotificationEnabled=" + this.mIsSwipeToNotificationEnabled);
        printWriter.println("  deferredGestureRegion=" + this.mDeferredGestureRegion);
        printWriter.println("  pipIsActive=" + this.mPipIsActive);
        this.mRotationTouchHelper.dump(printWriter);
    }
}
