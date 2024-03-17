package com.android.launcher3.taskbar;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.taskbar.unfold.NonDestroyableScopedUnfoldTransitionProgressProvider;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.SettingsCache;
import com.android.launcher3.util.SimpleBroadcastReceiver;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TouchInteractionService;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.util.ScopedUnfoldTransitionProgressProvider;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class TaskbarManager {
    private static final int CHANGE_FLAGS = 20;
    private static final Uri NAV_BAR_KIDS_MODE = Settings.Secure.getUriFor("nav_bar_kids_mode");
    private static final Uri USER_SETUP_COMPLETE_URI = Settings.Secure.getUriFor("user_setup_complete");
    private StatefulActivity mActivity;
    private final ComponentCallbacks mComponentCallbacks;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final DisplayController.DisplayInfoChangeListener mDispInfoChangeListener;
    private final DisplayController mDisplayController;
    private final SettingsCache.OnChangeListener mNavBarKidsModeListener;
    private final TaskbarNavButtonController mNavButtonController;
    private final TaskbarSharedState mSharedState = new TaskbarSharedState();
    private final SimpleBroadcastReceiver mShutdownReceiver;
    /* access modifiers changed from: private */
    public TaskbarActivityContext mTaskbarActivityContext;
    private final ScopedUnfoldTransitionProgressProvider mUnfoldProgressProvider = new NonDestroyableScopedUnfoldTransitionProgressProvider();
    private final SettingsCache.OnChangeListener mUserSetupCompleteListener;
    /* access modifiers changed from: private */
    public boolean mUserUnlocked = false;

    public TaskbarManager(TouchInteractionService touchInteractionService) {
        DisplayController displayController = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(touchInteractionService);
        this.mDisplayController = displayController;
        Context createWindowContext = touchInteractionService.createWindowContext(((DisplayManager) touchInteractionService.getSystemService(DisplayManager.class)).getDisplay(0), 2024, (Bundle) null);
        this.mContext = createWindowContext;
        this.mNavButtonController = new TaskbarNavButtonController(touchInteractionService, SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(createWindowContext), new Handler());
        $$Lambda$TaskbarManager$rg_s90KLWhaQFq72ZNl7UFtb8 r7 = new SettingsCache.OnChangeListener() {
            public final void onSettingsChanged(boolean z) {
                TaskbarManager.this.lambda$new$0$TaskbarManager(z);
            }
        };
        this.mUserSetupCompleteListener = r7;
        $$Lambda$TaskbarManager$s6BVSBeIb2jh4iF1TkC7rNmtpq8 r2 = new SettingsCache.OnChangeListener() {
            public final void onSettingsChanged(boolean z) {
                TaskbarManager.this.lambda$new$1$TaskbarManager(z);
            }
        };
        this.mNavBarKidsModeListener = r2;
        AnonymousClass1 r3 = new ComponentCallbacks() {
            private Configuration mOldConfig;

            public void onLowMemory() {
            }

            {
                this.mOldConfig = TaskbarManager.this.mContext.getResources().getConfiguration();
            }

            public void onConfigurationChanged(Configuration configuration) {
                DeviceProfile deviceProfile = TaskbarManager.this.mUserUnlocked ? LauncherAppState.getIDP(TaskbarManager.this.mContext).getDeviceProfile(TaskbarManager.this.mContext) : null;
                int diff = this.mOldConfig.diff(configuration);
                boolean z = true;
                boolean z2 = (diff & -2147473920) != 0;
                if (!((diff & 1024) == 0 || TaskbarManager.this.mTaskbarActivityContext == null || deviceProfile == null)) {
                    DeviceProfile deviceProfile2 = TaskbarManager.this.mTaskbarActivityContext.getDeviceProfile();
                    boolean z3 = (diff & 128) != 0;
                    int i = z3 ? deviceProfile2.heightPx : deviceProfile2.widthPx;
                    int i2 = z3 ? deviceProfile2.widthPx : deviceProfile2.heightPx;
                    if (deviceProfile.widthPx == i && deviceProfile.heightPx == i2) {
                        diff &= -1025;
                        if ((-2147473920 & diff) == 0) {
                            z = false;
                        }
                        z2 = z;
                    }
                }
                if (z2) {
                    TaskbarManager.this.recreateTaskbar();
                } else if (TaskbarManager.this.mTaskbarActivityContext != null) {
                    if (deviceProfile != null && deviceProfile.isTaskbarPresent) {
                        TaskbarManager.this.mTaskbarActivityContext.updateDeviceProfile(deviceProfile);
                    }
                    TaskbarManager.this.mTaskbarActivityContext.onConfigurationChanged(diff);
                }
                this.mOldConfig = configuration;
            }
        };
        this.mComponentCallbacks = r3;
        SimpleBroadcastReceiver simpleBroadcastReceiver = new SimpleBroadcastReceiver(new Consumer() {
            public final void accept(Object obj) {
                TaskbarManager.this.lambda$new$2$TaskbarManager((Intent) obj);
            }
        });
        this.mShutdownReceiver = simpleBroadcastReceiver;
        $$Lambda$TaskbarManager$c8pEucu7KJYp7wwiH0K6S7ETn8Q r5 = new DisplayController.DisplayInfoChangeListener() {
            public final void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
                TaskbarManager.this.lambda$new$3$TaskbarManager(context, info, i);
            }
        };
        this.mDispInfoChangeListener = r5;
        displayController.addChangeListener(r5);
        SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(createWindowContext).register(USER_SETUP_COMPLETE_URI, r7);
        SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(createWindowContext).register(NAV_BAR_KIDS_MODE, r2);
        createWindowContext.registerComponentCallbacks(r3);
        simpleBroadcastReceiver.register(createWindowContext, "android.intent.action.ACTION_SHUTDOWN");
        recreateTaskbar();
    }

    public /* synthetic */ void lambda$new$0$TaskbarManager(boolean z) {
        recreateTaskbar();
    }

    public /* synthetic */ void lambda$new$1$TaskbarManager(boolean z) {
        recreateTaskbar();
    }

    public /* synthetic */ void lambda$new$2$TaskbarManager(Intent intent) {
        destroyExistingTaskbar();
    }

    public /* synthetic */ void lambda$new$3$TaskbarManager(Context context, DisplayController.Info info, int i) {
        if ((i & 20) != 0) {
            recreateTaskbar();
        }
    }

    private void destroyExistingTaskbar() {
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext != null) {
            taskbarActivityContext.onDestroy();
            this.mTaskbarActivityContext = null;
        }
    }

    public AnimatorPlaybackController createLauncherStartFromSuwAnim(int i) {
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext == null) {
            return null;
        }
        return taskbarActivityContext.createLauncherStartFromSuwAnim(i);
    }

    public void onUserUnlocked() {
        this.mUserUnlocked = true;
        recreateTaskbar();
    }

    public void setActivity(StatefulActivity statefulActivity) {
        if (this.mActivity != statefulActivity) {
            this.mActivity = statefulActivity;
            this.mUnfoldProgressProvider.setSourceProvider(getUnfoldTransitionProgressProviderForActivity(statefulActivity));
            TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
            if (taskbarActivityContext != null) {
                taskbarActivityContext.setUIController(createTaskbarUIControllerForActivity(this.mActivity));
            }
        }
    }

    private UnfoldTransitionProgressProvider getUnfoldTransitionProgressProviderForActivity(StatefulActivity statefulActivity) {
        if (statefulActivity instanceof BaseQuickstepLauncher) {
            return ((BaseQuickstepLauncher) statefulActivity).getUnfoldTransitionProgressProvider();
        }
        return null;
    }

    private TaskbarUIController createTaskbarUIControllerForActivity(StatefulActivity statefulActivity) {
        if (statefulActivity instanceof BaseQuickstepLauncher) {
            if (this.mTaskbarActivityContext.getPackageManager().hasSystemFeature("android.hardware.type.pc")) {
                return new DesktopTaskbarUIController((BaseQuickstepLauncher) statefulActivity);
            }
            return new LauncherTaskbarUIController((BaseQuickstepLauncher) statefulActivity);
        } else if (statefulActivity instanceof RecentsActivity) {
            return new FallbackTaskbarUIController((RecentsActivity) statefulActivity);
        } else {
            return TaskbarUIController.DEFAULT;
        }
    }

    public void clearActivity(StatefulActivity statefulActivity) {
        if (this.mActivity == statefulActivity) {
            this.mActivity = null;
            TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
            if (taskbarActivityContext != null) {
                taskbarActivityContext.setUIController(TaskbarUIController.DEFAULT);
            }
            this.mUnfoldProgressProvider.setSourceProvider((UnfoldTransitionProgressProvider) null);
        }
    }

    /* access modifiers changed from: private */
    public void recreateTaskbar() {
        destroyExistingTaskbar();
        DeviceProfile deviceProfile = this.mUserUnlocked ? LauncherAppState.getIDP(this.mContext).getDeviceProfile(this.mContext) : null;
        if (!(deviceProfile != null && deviceProfile.isTaskbarPresent)) {
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).notifyTaskbarStatus(false, false);
            return;
        }
        TaskbarActivityContext taskbarActivityContext = new TaskbarActivityContext(this.mContext, deviceProfile, this.mNavButtonController, this.mUnfoldProgressProvider);
        this.mTaskbarActivityContext = taskbarActivityContext;
        taskbarActivityContext.init(this.mSharedState);
        StatefulActivity statefulActivity = this.mActivity;
        if (statefulActivity != null) {
            this.mTaskbarActivityContext.setUIController(createTaskbarUIControllerForActivity(statefulActivity));
        }
    }

    public void onSystemUiFlagsChanged(int i) {
        this.mSharedState.sysuiStateFlags = i;
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext != null) {
            taskbarActivityContext.updateSysuiStateFlags(i, false);
        }
    }

    public void setSetupUIVisible(boolean z) {
        this.mSharedState.setupUIVisible = z;
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext != null) {
            taskbarActivityContext.setSetupUIVisible(z);
        }
    }

    public void onRotationProposal(int i, boolean z) {
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext != null) {
            taskbarActivityContext.onRotationProposal(i, z);
        }
    }

    public void disableNavBarElements(int i, int i2, int i3, boolean z) {
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext != null) {
            taskbarActivityContext.disableNavBarElements(i, i2, i3, z);
        }
    }

    public void onSystemBarAttributesChanged(int i, int i2) {
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext != null) {
            taskbarActivityContext.onSystemBarAttributesChanged(i, i2);
        }
    }

    public void onNavButtonsDarkIntensityChanged(float f) {
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext != null) {
            taskbarActivityContext.onNavButtonsDarkIntensityChanged(f);
        }
    }

    public void destroy() {
        destroyExistingTaskbar();
        this.mDisplayController.removeChangeListener(this.mDispInfoChangeListener);
        SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).unregister(USER_SETUP_COMPLETE_URI, this.mUserSetupCompleteListener);
        SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).unregister(NAV_BAR_KIDS_MODE, this.mNavBarKidsModeListener);
        this.mContext.unregisterComponentCallbacks(this.mComponentCallbacks);
        this.mContext.unregisterReceiver(this.mShutdownReceiver);
    }

    public TaskbarActivityContext getCurrentActivityContext() {
        return this.mTaskbarActivityContext;
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarManager:");
        TaskbarActivityContext taskbarActivityContext = this.mTaskbarActivityContext;
        if (taskbarActivityContext == null) {
            printWriter.println(str + "\tTaskbarActivityContext: null");
        } else {
            taskbarActivityContext.dumpLogs(str + "\t", printWriter);
        }
    }
}
