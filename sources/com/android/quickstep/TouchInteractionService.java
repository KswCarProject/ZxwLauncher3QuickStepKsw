package com.android.quickstep;

import android.app.PendingIntent;
import android.app.RemoteAction;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Choreographer;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.launcher3.R;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.taskbar.TaskbarActivityContext;
import com.android.launcher3.taskbar.TaskbarManager;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.tracing.LauncherTraceProto;
import com.android.launcher3.tracing.TouchInteractionServiceProto;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.WindowBounds;
import com.android.quickstep.AbsSwipeUpHandler;
import com.android.quickstep.TouchInteractionService;
import com.android.quickstep.inputconsumers.AccessibilityInputConsumer;
import com.android.quickstep.inputconsumers.AssistantInputConsumer;
import com.android.quickstep.inputconsumers.DeviceLockedInputConsumer;
import com.android.quickstep.inputconsumers.OneHandedModeInputConsumer;
import com.android.quickstep.inputconsumers.OtherActivityInputConsumer;
import com.android.quickstep.inputconsumers.OverviewInputConsumer;
import com.android.quickstep.inputconsumers.OverviewWithoutFocusInputConsumer;
import com.android.quickstep.inputconsumers.ProgressDelegateInputConsumer;
import com.android.quickstep.inputconsumers.ResetGestureInputConsumer;
import com.android.quickstep.inputconsumers.ScreenPinnedInputConsumer;
import com.android.quickstep.inputconsumers.SysUiOverlayInputConsumer;
import com.android.quickstep.inputconsumers.TaskbarStashInputConsumer;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.ProtoTracer;
import com.android.quickstep.util.ProxyScreenStatusProvider;
import com.android.quickstep.util.SplitScreenBounds;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputChannelCompat;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.android.wm.shell.back.IBackAnimation;
import com.android.wm.shell.onehanded.IOneHanded;
import com.android.wm.shell.pip.IPip;
import com.android.wm.shell.recents.IRecentTasks;
import com.android.wm.shell.splitscreen.ISplitScreen;
import com.android.wm.shell.startingsurface.IStartingWindow;
import com.android.wm.shell.transition.IShellTransitions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class TouchInteractionService extends Service implements ProtoTraceable<LauncherTraceProto.Builder> {
    private static final String HAS_ENABLED_QUICKSTEP_ONCE = "launcher.has_enabled_quickstep_once";
    private static final String KEY_BACK_NOTIFICATION_COUNT = "backNotificationCount";
    private static final int MAX_BACK_NOTIFICATION_COUNT = 3;
    private static final String NOTIFY_ACTION_BACK = "com.android.quickstep.action.BACK_GESTURE";
    private static final int SYSTEM_ACTION_ID_ALL_APPS = 14;
    private static final String TAG = "TouchInteractionService";
    private static boolean sConnected = false;
    /* access modifiers changed from: private */
    public static boolean sIsInitialized = false;
    private ActivityManagerWrapper mAM;
    private int mBackGestureNotificationCounter = -1;
    private InputConsumer mConsumer = InputConsumer.NO_OP;
    /* access modifiers changed from: private */
    public RecentsAnimationDeviceState mDeviceState;
    private final AbsSwipeUpHandler.Factory mFallbackSwipeHandlerFactory = new AbsSwipeUpHandler.Factory() {
        public final AbsSwipeUpHandler newHandler(GestureState gestureState, long j) {
            return TouchInteractionService.this.createFallbackSwipeHandler(gestureState, j);
        }
    };
    private GestureState mGestureState = GestureState.DEFAULT_STATE;
    private InputConsumerController mInputConsumer;
    private InputChannelCompat.InputEventReceiver mInputEventReceiver;
    private InputMonitorCompat mInputMonitorCompat;
    private final AbsSwipeUpHandler.Factory mLauncherSwipeHandlerFactory = new AbsSwipeUpHandler.Factory() {
        public final AbsSwipeUpHandler newHandler(GestureState gestureState, long j) {
            return TouchInteractionService.this.createLauncherSwipeHandler(gestureState, j);
        }
    };
    private Choreographer mMainChoreographer;
    /* access modifiers changed from: private */
    public OverviewCommandHelper mOverviewCommandHelper;
    private OverviewComponentObserver mOverviewComponentObserver;
    private ResetGestureInputConsumer mResetGestureInputConsumer;
    private RotationTouchHelper mRotationTouchHelper;
    /* access modifiers changed from: private */
    public Function<GestureState, AnimatedFloat> mSwipeUpProxyProvider = $$Lambda$TouchInteractionService$sS1wCGZjpSGERlfGVKkK8cxWvg.INSTANCE;
    private final TISBinder mTISBinder = new TISBinder();
    private TaskAnimationManager mTaskAnimationManager;
    /* access modifiers changed from: private */
    public TaskbarManager mTaskbarManager;
    private InputConsumer mUncheckedConsumer = InputConsumer.NO_OP;

    static /* synthetic */ AnimatedFloat lambda$new$0(GestureState gestureState) {
        return null;
    }

    public class TISBinder extends IOverviewProxy.Stub {
        private Runnable mOnOverviewTargetChangeListener = null;

        static /* synthetic */ AnimatedFloat lambda$setSwipeUpProxy$11(GestureState gestureState) {
            return null;
        }

        public void onTip(int i, int i2) {
        }

        public TISBinder() {
        }

        public void onInitialize(Bundle bundle) {
            ISystemUiProxy asInterface = ISystemUiProxy.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SYSUI_PROXY));
            IPip asInterface2 = IPip.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_PIP));
            ISplitScreen asInterface3 = ISplitScreen.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_SPLIT_SCREEN));
            IOneHanded asInterface4 = IOneHanded.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_ONE_HANDED));
            IShellTransitions asInterface5 = IShellTransitions.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_SHELL_TRANSITIONS));
            IStartingWindow asInterface6 = IStartingWindow.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_STARTING_WINDOW));
            ISysuiUnlockAnimationController asInterface7 = ISysuiUnlockAnimationController.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_UNLOCK_ANIMATION_CONTROLLER));
            Executors.MAIN_EXECUTOR.execute(new Runnable(asInterface, asInterface2, asInterface3, asInterface4, asInterface5, asInterface6, IRecentTasks.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_RECENT_TASKS)), asInterface7, IBackAnimation.Stub.asInterface(bundle.getBinder(QuickStepContract.KEY_EXTRA_SHELL_BACK_ANIMATION))) {
                public final /* synthetic */ ISystemUiProxy f$1;
                public final /* synthetic */ IPip f$2;
                public final /* synthetic */ ISplitScreen f$3;
                public final /* synthetic */ IOneHanded f$4;
                public final /* synthetic */ IShellTransitions f$5;
                public final /* synthetic */ IStartingWindow f$6;
                public final /* synthetic */ IRecentTasks f$7;
                public final /* synthetic */ ISysuiUnlockAnimationController f$8;
                public final /* synthetic */ IBackAnimation f$9;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                    this.f$9 = r10;
                }

                public final void run() {
                    TouchInteractionService.TISBinder.this.lambda$onInitialize$0$TouchInteractionService$TISBinder(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
                }
            });
            boolean unused = TouchInteractionService.sIsInitialized = true;
        }

        public /* synthetic */ void lambda$onInitialize$0$TouchInteractionService$TISBinder(ISystemUiProxy iSystemUiProxy, IPip iPip, ISplitScreen iSplitScreen, IOneHanded iOneHanded, IShellTransitions iShellTransitions, IStartingWindow iStartingWindow, IRecentTasks iRecentTasks, ISysuiUnlockAnimationController iSysuiUnlockAnimationController, IBackAnimation iBackAnimation) {
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(TouchInteractionService.this).setProxy(iSystemUiProxy, iPip, iSplitScreen, iOneHanded, iShellTransitions, iStartingWindow, iRecentTasks, iSysuiUnlockAnimationController, iBackAnimation);
            TouchInteractionService.this.initInputMonitor("TISBinder#onInitialize()");
            TouchInteractionService.this.preloadOverview(true);
        }

        public void onOverviewToggle() {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "onOverviewToggle");
            if (!TouchInteractionService.this.mDeviceState.isScreenPinningActive()) {
                TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
                TouchInteractionService.this.mOverviewCommandHelper.addCommand(4);
            }
        }

        public void onOverviewShown(boolean z) {
            if (z) {
                TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
                TouchInteractionService.this.mOverviewCommandHelper.addCommand(2);
                return;
            }
            TouchInteractionService.this.mOverviewCommandHelper.addCommand(1);
        }

        public void onOverviewHidden(boolean z, boolean z2) {
            if (z && !z2) {
                TouchInteractionService.this.mOverviewCommandHelper.addCommand(3);
            }
        }

        public void onAssistantAvailable(boolean z) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TouchInteractionService.TISBinder.this.lambda$onAssistantAvailable$1$TouchInteractionService$TISBinder(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onAssistantAvailable$1$TouchInteractionService$TISBinder(boolean z) {
            TouchInteractionService.this.mDeviceState.setAssistantAvailable(z);
            TouchInteractionService.this.onAssistantVisibilityChanged();
        }

        public void onAssistantVisibilityChanged(float f) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(f) {
                public final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TouchInteractionService.TISBinder.this.lambda$onAssistantVisibilityChanged$2$TouchInteractionService$TISBinder(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onAssistantVisibilityChanged$2$TouchInteractionService$TISBinder(float f) {
            TouchInteractionService.this.mDeviceState.setAssistantVisibility(f);
            TouchInteractionService.this.onAssistantVisibilityChanged();
        }

        public void onBackAction(boolean z, int i, int i2, boolean z2, boolean z3) {
            Log.d(TouchInteractionService.TAG, "onBackAction");
        }

        public void onSystemUiStateChanged(int i) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TouchInteractionService.TISBinder.this.lambda$onSystemUiStateChanged$3$TouchInteractionService$TISBinder(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onSystemUiStateChanged$3$TouchInteractionService$TISBinder(int i) {
            int systemUiStateFlags = TouchInteractionService.this.mDeviceState.getSystemUiStateFlags();
            TouchInteractionService.this.mDeviceState.setSystemUiFlags(i);
            TouchInteractionService.this.onSystemUiFlagsChanged(systemUiStateFlags);
        }

        public /* synthetic */ void lambda$onActiveNavBarRegionChanges$4$TouchInteractionService$TISBinder(Region region) {
            TouchInteractionService.this.mDeviceState.setDeferredGestureRegion(region);
        }

        public void onActiveNavBarRegionChanges(Region region) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(region) {
                public final /* synthetic */ Region f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TouchInteractionService.TISBinder.this.lambda$onActiveNavBarRegionChanges$4$TouchInteractionService$TISBinder(this.f$1);
                }
            });
        }

        public void onSplitScreenSecondaryBoundsChanged(Rect rect, Rect rect2) {
            Executors.MAIN_EXECUTOR.execute(new Runnable() {
                public final void run() {
                    SplitScreenBounds.INSTANCE.setSecondaryWindowBounds(WindowBounds.this);
                }
            });
        }

        public void onScreenTurnedOn() {
            LooperExecutor looperExecutor = Executors.MAIN_EXECUTOR;
            ProxyScreenStatusProvider proxyScreenStatusProvider = ProxyScreenStatusProvider.INSTANCE;
            Objects.requireNonNull(proxyScreenStatusProvider);
            looperExecutor.execute(new Runnable() {
                public final void run() {
                    ProxyScreenStatusProvider.this.onScreenTurnedOn();
                }
            });
        }

        public void preloadOverviewForSUWAllSet() {
            TouchInteractionService.this.preloadOverview(false, true);
        }

        public void onRotationProposal(int i, boolean z) {
            if (TouchInteractionService.this.mTaskbarManager != null) {
                executeForTaskbarManager(new Runnable(i, z) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        TouchInteractionService.TISBinder.this.lambda$onRotationProposal$6$TouchInteractionService$TISBinder(this.f$1, this.f$2);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onRotationProposal$6$TouchInteractionService$TISBinder(int i, boolean z) {
            TouchInteractionService.this.mTaskbarManager.onRotationProposal(i, z);
        }

        public void disable(int i, int i2, int i3, boolean z) {
            if (TouchInteractionService.this.mTaskbarManager != null) {
                executeForTaskbarManager(new Runnable(i, i2, i3, z) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ int f$2;
                    public final /* synthetic */ int f$3;
                    public final /* synthetic */ boolean f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        TouchInteractionService.TISBinder.this.lambda$disable$7$TouchInteractionService$TISBinder(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$disable$7$TouchInteractionService$TISBinder(int i, int i2, int i3, boolean z) {
            TouchInteractionService.this.mTaskbarManager.disableNavBarElements(i, i2, i3, z);
        }

        public void onSystemBarAttributesChanged(int i, int i2) {
            if (TouchInteractionService.this.mTaskbarManager != null) {
                executeForTaskbarManager(new Runnable(i, i2) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        TouchInteractionService.TISBinder.this.lambda$onSystemBarAttributesChanged$8$TouchInteractionService$TISBinder(this.f$1, this.f$2);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onSystemBarAttributesChanged$8$TouchInteractionService$TISBinder(int i, int i2) {
            TouchInteractionService.this.mTaskbarManager.onSystemBarAttributesChanged(i, i2);
        }

        public void onNavButtonsDarkIntensityChanged(float f) {
            if (TouchInteractionService.this.mTaskbarManager != null) {
                executeForTaskbarManager(new Runnable(f) {
                    public final /* synthetic */ float f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TouchInteractionService.TISBinder.this.lambda$onNavButtonsDarkIntensityChanged$9$TouchInteractionService$TISBinder(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onNavButtonsDarkIntensityChanged$9$TouchInteractionService$TISBinder(float f) {
            TouchInteractionService.this.mTaskbarManager.onNavButtonsDarkIntensityChanged(f);
        }

        private void executeForTaskbarManager(Runnable runnable) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TouchInteractionService.TISBinder.this.lambda$executeForTaskbarManager$10$TouchInteractionService$TISBinder(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$executeForTaskbarManager$10$TouchInteractionService$TISBinder(Runnable runnable) {
            if (TouchInteractionService.this.mTaskbarManager != null) {
                runnable.run();
            }
        }

        public TaskbarManager getTaskbarManager() {
            return TouchInteractionService.this.mTaskbarManager;
        }

        public OverviewCommandHelper getOverviewCommandHelper() {
            return TouchInteractionService.this.mOverviewCommandHelper;
        }

        public void setSwipeUpProxy(Function<GestureState, AnimatedFloat> function) {
            TouchInteractionService touchInteractionService = TouchInteractionService.this;
            if (function == null) {
                function = $$Lambda$TouchInteractionService$TISBinder$lKj2YN6qFv0I5CbWs2AlicPZ88k.INSTANCE;
            }
            Function unused = touchInteractionService.mSwipeUpProxyProvider = function;
        }

        public void setGestureBlockedTaskId(int i) {
            TouchInteractionService.this.mDeviceState.setGestureBlockingTaskId(i);
        }

        public void setOverviewTargetChangeListener(Runnable runnable) {
            this.mOnOverviewTargetChangeListener = runnable;
        }

        /* access modifiers changed from: protected */
        public void onOverviewTargetChange() {
            Runnable runnable = this.mOnOverviewTargetChangeListener;
            if (runnable != null) {
                runnable.run();
                this.mOnOverviewTargetChangeListener = null;
            }
        }
    }

    public static boolean isConnected() {
        return sConnected;
    }

    public static boolean isInitialized() {
        return sIsInitialized;
    }

    public void onCreate() {
        super.onCreate();
        this.mMainChoreographer = Choreographer.getInstance();
        this.mAM = ActivityManagerWrapper.getInstance();
        RecentsAnimationDeviceState recentsAnimationDeviceState = new RecentsAnimationDeviceState(this, true);
        this.mDeviceState = recentsAnimationDeviceState;
        this.mRotationTouchHelper = recentsAnimationDeviceState.getRotationTouchHelper();
        this.mDeviceState.runOnUserUnlocked(new Runnable() {
            public final void run() {
                TouchInteractionService.this.onUserUnlocked();
            }
        });
        TaskbarManager taskbarManager = this.mTaskbarManager;
        if (taskbarManager != null) {
            RecentsAnimationDeviceState recentsAnimationDeviceState2 = this.mDeviceState;
            Objects.requireNonNull(taskbarManager);
            recentsAnimationDeviceState2.runOnUserUnlocked(new Runnable() {
                public final void run() {
                    TaskbarManager.this.onUserUnlocked();
                }
            });
        }
        this.mDeviceState.addNavigationModeChangedCallback(new Runnable() {
            public final void run() {
                TouchInteractionService.this.onNavigationModeChanged();
            }
        });
        ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).add(this);
        sConnected = true;
    }

    private void disposeEventHandlers(String str) {
        Log.d(TAG, "disposeEventHandlers: Reason: " + str);
        InputChannelCompat.InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        InputMonitorCompat inputMonitorCompat = this.mInputMonitorCompat;
        if (inputMonitorCompat != null) {
            inputMonitorCompat.dispose();
            this.mInputMonitorCompat = null;
        }
    }

    /* access modifiers changed from: private */
    public void initInputMonitor(String str) {
        disposeEventHandlers("Initializing input monitor due to: " + str);
        if (!this.mDeviceState.isButtonNavMode()) {
            InputMonitorCompat inputMonitorCompat = new InputMonitorCompat("swipe-up", this.mDeviceState.getDisplayId());
            this.mInputMonitorCompat = inputMonitorCompat;
            this.mInputEventReceiver = inputMonitorCompat.getInputReceiver(Looper.getMainLooper(), this.mMainChoreographer, new InputChannelCompat.InputEventListener() {
                public final void onInputEvent(InputEvent inputEvent) {
                    TouchInteractionService.this.onInputEvent(inputEvent);
                }
            });
            this.mRotationTouchHelper.updateGestureTouchRegions();
        }
    }

    /* access modifiers changed from: private */
    public void onNavigationModeChanged() {
        initInputMonitor("onNavigationModeChanged()");
        resetHomeBounceSeenOnQuickstepEnabledFirstTime();
    }

    public void onUserUnlocked() {
        this.mTaskAnimationManager = new TaskAnimationManager(this);
        this.mOverviewComponentObserver = new OverviewComponentObserver(this, this.mDeviceState);
        this.mOverviewCommandHelper = new OverviewCommandHelper(this, this.mOverviewComponentObserver, this.mTaskAnimationManager);
        this.mResetGestureInputConsumer = new ResetGestureInputConsumer(this.mTaskAnimationManager);
        InputConsumerController recentsAnimationInputConsumer = InputConsumerController.getRecentsAnimationInputConsumer();
        this.mInputConsumer = recentsAnimationInputConsumer;
        recentsAnimationInputConsumer.registerInputConsumer();
        onSystemUiFlagsChanged(this.mDeviceState.getSystemUiStateFlags());
        onAssistantVisibilityChanged();
        TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(this);
        this.mBackGestureNotificationCounter = Math.max(0, Utilities.getDevicePrefs(this).getInt(KEY_BACK_NOTIFICATION_COUNT, 3));
        resetHomeBounceSeenOnQuickstepEnabledFirstTime();
        this.mOverviewComponentObserver.setOverviewChangeListener(new Consumer() {
            public final void accept(Object obj) {
                TouchInteractionService.this.onOverviewTargetChange(((Boolean) obj).booleanValue());
            }
        });
        onOverviewTargetChange(this.mOverviewComponentObserver.isHomeAndOverviewSame());
    }

    public OverviewCommandHelper getOverviewCommandHelper() {
        return this.mOverviewCommandHelper;
    }

    private void resetHomeBounceSeenOnQuickstepEnabledFirstTime() {
        if (this.mDeviceState.isUserUnlocked() && !this.mDeviceState.isButtonNavMode()) {
            SharedPreferences prefs = Utilities.getPrefs(this);
            if (!prefs.getBoolean(HAS_ENABLED_QUICKSTEP_ONCE, true)) {
                prefs.edit().putBoolean(HAS_ENABLED_QUICKSTEP_ONCE, true).putBoolean(OnboardingPrefs.HOME_BOUNCE_SEEN, false).apply();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onOverviewTargetChange(boolean z) {
        TaskbarManager taskbarManager;
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(AccessibilityManager.class);
        if (z) {
            accessibilityManager.registerSystemAction(new RemoteAction(Icon.createWithResource(this, R.drawable.ic_apps), getString(R.string.all_apps_label), getString(R.string.all_apps_label), PendingIntent.getActivity(this, 14, new Intent(this.mOverviewComponentObserver.getHomeIntent()).setAction("android.intent.action.ALL_APPS"), 201326592)), 14);
        } else {
            accessibilityManager.unregisterSystemAction(14);
        }
        StatefulActivity createdActivity = this.mOverviewComponentObserver.getActivityInterface().getCreatedActivity();
        if (!(createdActivity == null || (taskbarManager = this.mTaskbarManager) == null)) {
            taskbarManager.setActivity(createdActivity);
        }
        this.mTISBinder.onOverviewTargetChange();
    }

    /* access modifiers changed from: private */
    public void onSystemUiFlagsChanged(int i) {
        if (this.mDeviceState.isUserUnlocked()) {
            int systemUiStateFlags = this.mDeviceState.getSystemUiStateFlags();
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).setLastSystemUiStateFlags(systemUiStateFlags);
            this.mOverviewComponentObserver.onSystemUiStateChanged();
            TaskbarManager taskbarManager = this.mTaskbarManager;
            if (taskbarManager != null) {
                taskbarManager.onSystemUiFlagsChanged(systemUiStateFlags);
            }
            boolean z = true;
            boolean z2 = (i & 4) != 0;
            if ((systemUiStateFlags & 4) == 0) {
                z = false;
            }
            if (z2 != z && z) {
                this.mTaskAnimationManager.endLiveTile();
            }
            int i2 = systemUiStateFlags & 4096;
            if ((i & 4096) == i2) {
                return;
            }
            if (i2 != 0) {
                Log.d(TAG, "Starting tracing.");
                ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).start();
                return;
            }
            Log.d(TAG, "Stopping tracing. Dumping to file=" + ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getTraceFile());
            ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).stop();
        }
    }

    /* access modifiers changed from: private */
    public void onAssistantVisibilityChanged() {
        if (this.mDeviceState.isUserUnlocked()) {
            this.mOverviewComponentObserver.getActivityInterface().onAssistantVisibilityChanged(this.mDeviceState.getAssistantVisibility());
        }
    }

    public void onDestroy() {
        Log.d(TAG, "Touch service destroyed: user=" + getUserId());
        sIsInitialized = false;
        if (this.mDeviceState.isUserUnlocked()) {
            this.mInputConsumer.unregisterInputConsumer();
            this.mOverviewComponentObserver.onDestroy();
        }
        disposeEventHandlers("TouchInteractionService onDestroy()");
        this.mDeviceState.destroy();
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).lambda$new$0$SystemUiProxy();
        ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).stop();
        ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).remove(this);
        ((AccessibilityManager) getSystemService(AccessibilityManager.class)).unregisterSystemAction(14);
        TaskbarManager taskbarManager = this.mTaskbarManager;
        if (taskbarManager != null) {
            taskbarManager.destroy();
        }
        sConnected = false;
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Touch service connected: user=" + getUserId());
        return this.mTISBinder;
    }

    /* access modifiers changed from: private */
    public void onInputEvent(InputEvent inputEvent) {
        InputConsumer inputConsumer;
        if (!(inputEvent instanceof MotionEvent)) {
            Log.e(TAG, "Unknown event " + inputEvent);
            return;
        }
        MotionEvent motionEvent = (MotionEvent) inputEvent;
        TestLogging.recordMotionEvent(TestProtocol.SEQUENCE_TIS, "TouchInteractionService.onInputEvent", motionEvent);
        if (this.mDeviceState.isUserUnlocked()) {
            boolean z = true;
            Object beginFlagsOverride = TraceHelper.INSTANCE.beginFlagsOverride(1);
            int action = motionEvent.getAction();
            if (action == 0) {
                this.mRotationTouchHelper.setOrientationTransformIfNeeded(motionEvent);
                if (!this.mDeviceState.isOneHandedModeActive() && this.mRotationTouchHelper.isInSwipeUpTouchRegion(motionEvent)) {
                    GestureState gestureState = new GestureState(this.mGestureState);
                    GestureState createGestureState = createGestureState(this.mGestureState);
                    createGestureState.setSwipeUpStartTimeMs(SystemClock.uptimeMillis());
                    this.mConsumer.onConsumerAboutToBeSwitched();
                    this.mGestureState = createGestureState;
                    this.mConsumer = newConsumer(gestureState, createGestureState, motionEvent);
                    ActiveGestureLog.INSTANCE.addLog("setInputConsumer: " + this.mConsumer.getName());
                    this.mUncheckedConsumer = this.mConsumer;
                } else if (this.mDeviceState.isUserUnlocked() && this.mDeviceState.isFullyGesturalNavMode() && this.mDeviceState.canTriggerAssistantAction(motionEvent)) {
                    this.mGestureState = createGestureState(this.mGestureState);
                    this.mUncheckedConsumer = tryCreateAssistantInputConsumer(InputConsumer.NO_OP, this.mGestureState, motionEvent);
                } else if (this.mDeviceState.canTriggerOneHandedAction(motionEvent)) {
                    this.mUncheckedConsumer = new OneHandedModeInputConsumer(this, this.mDeviceState, InputConsumer.NO_OP, this.mInputMonitorCompat);
                } else {
                    this.mUncheckedConsumer = InputConsumer.NO_OP;
                }
            } else if (this.mUncheckedConsumer != InputConsumer.NO_OP) {
                this.mRotationTouchHelper.setOrientationTransformIfNeeded(motionEvent);
            }
            if (this.mUncheckedConsumer != InputConsumer.NO_OP) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0 || actionMasked == 1) {
                    ActiveGestureLog.INSTANCE.addLog("onMotionEvent(" + ((int) motionEvent.getRawX()) + ", " + ((int) motionEvent.getRawY()) + ")", motionEvent.getActionMasked());
                } else {
                    ActiveGestureLog.INSTANCE.addLog("onMotionEvent", motionEvent.getActionMasked());
                }
            }
            boolean z2 = this.mGestureState.getActivityInterface() != null && this.mGestureState.getActivityInterface().shouldCancelCurrentGesture();
            if (!(action == 1 || action == 3 || z2) || (inputConsumer = this.mConsumer) == null || inputConsumer.getActiveConsumerInHierarchy().isConsumerDetachedFromGesture()) {
                z = false;
            }
            if (z2) {
                motionEvent.setAction(3);
            }
            this.mUncheckedConsumer.onMotionEvent(motionEvent);
            if (z) {
                reset();
            }
            TraceHelper.INSTANCE.endFlagsOverride(beginFlagsOverride);
            ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).scheduleFrameUpdate();
        }
    }

    private InputConsumer tryCreateAssistantInputConsumer(InputConsumer inputConsumer, GestureState gestureState, MotionEvent motionEvent) {
        if (this.mDeviceState.isGestureBlockedTask(gestureState.getRunningTask())) {
            return inputConsumer;
        }
        return new AssistantInputConsumer(this, gestureState, inputConsumer, this.mInputMonitorCompat, this.mDeviceState, motionEvent);
    }

    public GestureState createGestureState(GestureState gestureState) {
        GestureState gestureState2 = new GestureState(this.mOverviewComponentObserver, ActiveGestureLog.INSTANCE.generateAndSetLogId());
        if (this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            gestureState2.updateRunningTask(gestureState.getRunningTask());
            gestureState2.updateLastStartedTaskId(gestureState.getLastStartedTaskId());
            gestureState2.updatePreviouslyAppearedTaskIds(gestureState.getPreviouslyAppearedTaskIds());
        } else {
            gestureState2.updateRunningTask(TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getCachedTopTask(false));
        }
        return gestureState2;
    }

    private InputConsumer newConsumer(GestureState gestureState, GestureState gestureState2, MotionEvent motionEvent) {
        OneHandedModeInputConsumer oneHandedModeInputConsumer;
        InputConsumer inputConsumer;
        TaskbarActivityContext currentActivityContext;
        AnimatedFloat apply = this.mSwipeUpProxyProvider.apply(this.mGestureState);
        if (apply != null) {
            return new ProgressDelegateInputConsumer(this, this.mTaskAnimationManager, this.mGestureState, this.mInputMonitorCompat, apply);
        }
        boolean canStartSystemGesture = this.mDeviceState.canStartSystemGesture();
        if (this.mDeviceState.isUserUnlocked()) {
            if (canStartSystemGesture || gestureState.isRecentsAnimationRunning()) {
                oneHandedModeInputConsumer = newBaseConsumer(gestureState, gestureState2, motionEvent);
            } else {
                oneHandedModeInputConsumer = getDefaultInputConsumer();
            }
            if (this.mDeviceState.isGesturalNavMode()) {
                handleOrientationSetup(oneHandedModeInputConsumer);
            }
            if (this.mDeviceState.isFullyGesturalNavMode()) {
                if (this.mDeviceState.canTriggerAssistantAction(motionEvent)) {
                    oneHandedModeInputConsumer = tryCreateAssistantInputConsumer(oneHandedModeInputConsumer, gestureState2, motionEvent);
                }
                TaskbarManager taskbarManager = this.mTaskbarManager;
                if (!(taskbarManager == null || (currentActivityContext = taskbarManager.getCurrentActivityContext()) == null)) {
                    oneHandedModeInputConsumer = new TaskbarStashInputConsumer(this, oneHandedModeInputConsumer, this.mInputMonitorCompat, currentActivityContext);
                }
                if ((this.mDeviceState.isBubblesExpanded() && !this.mDeviceState.isNotificationPanelExpanded()) || this.mDeviceState.isSystemUiDialogShowing()) {
                    oneHandedModeInputConsumer = new SysUiOverlayInputConsumer(getBaseContext(), this.mDeviceState, this.mInputMonitorCompat);
                }
                if (this.mDeviceState.isScreenPinningActive()) {
                    oneHandedModeInputConsumer = new ScreenPinnedInputConsumer(this, gestureState2);
                }
                if (this.mDeviceState.canTriggerOneHandedAction(motionEvent)) {
                    oneHandedModeInputConsumer = new OneHandedModeInputConsumer(this, this.mDeviceState, oneHandedModeInputConsumer, this.mInputMonitorCompat);
                }
                if (!this.mDeviceState.isAccessibilityMenuAvailable()) {
                    return oneHandedModeInputConsumer;
                }
                inputConsumer = new AccessibilityInputConsumer(this, this.mDeviceState, oneHandedModeInputConsumer, this.mInputMonitorCompat);
            } else {
                if (this.mDeviceState.isScreenPinningActive()) {
                    oneHandedModeInputConsumer = getDefaultInputConsumer();
                }
                if (!this.mDeviceState.canTriggerOneHandedAction(motionEvent)) {
                    return oneHandedModeInputConsumer;
                }
                inputConsumer = new OneHandedModeInputConsumer(this, this.mDeviceState, oneHandedModeInputConsumer, this.mInputMonitorCompat);
            }
            return inputConsumer;
        } else if (canStartSystemGesture) {
            return createDeviceLockedInputConsumer(gestureState2);
        } else {
            return getDefaultInputConsumer();
        }
    }

    private void handleOrientationSetup(InputConsumer inputConsumer) {
        inputConsumer.notifyOrientationSetup();
    }

    private InputConsumer newBaseConsumer(GestureState gestureState, GestureState gestureState2, MotionEvent motionEvent) {
        if (this.mDeviceState.isKeyguardShowingOccluded()) {
            return createDeviceLockedInputConsumer(gestureState2);
        }
        boolean z = gestureState2.getActivityInterface().isStarted() && gestureState2.getRunningTask() != null && gestureState2.getRunningTask().isRootChooseActivity();
        if (gestureState2.getRunningTask() != null && gestureState2.getRunningTask().isExcludedAssistant()) {
            gestureState2.updateRunningTask(TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getCachedTopTask(true));
            z = gestureState2.getRunningTask().isHomeTask();
        }
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && gestureState2.getActivityInterface().isInLiveTileMode()) {
            return createOverviewInputConsumer(gestureState, gestureState2, motionEvent, z);
        }
        if (gestureState2.getRunningTask() == null) {
            return getDefaultInputConsumer();
        }
        if (gestureState.isRunningAnimationToLauncher() || ((gestureState2.getActivityInterface().isResumed() && !gestureState.isRecentsAnimationRunning()) || z)) {
            return createOverviewInputConsumer(gestureState, gestureState2, motionEvent, z);
        }
        if (this.mDeviceState.isGestureBlockedTask(gestureState2.getRunningTask())) {
            return getDefaultInputConsumer();
        }
        return createOtherActivityInputConsumer(gestureState2, motionEvent);
    }

    public AbsSwipeUpHandler.Factory getSwipeUpHandlerFactory() {
        return !this.mOverviewComponentObserver.isHomeAndOverviewSame() ? this.mFallbackSwipeHandlerFactory : this.mLauncherSwipeHandlerFactory;
    }

    private InputConsumer createOtherActivityInputConsumer(GestureState gestureState, MotionEvent motionEvent) {
        return new OtherActivityInputConsumer(this, this.mDeviceState, this.mTaskAnimationManager, gestureState, !this.mOverviewComponentObserver.isHomeAndOverviewSame() || gestureState.getActivityInterface().deferStartingActivity(this.mDeviceState, motionEvent), new Consumer() {
            public final void accept(Object obj) {
                TouchInteractionService.this.onConsumerInactive((OtherActivityInputConsumer) obj);
            }
        }, this.mInputMonitorCompat, this.mInputEventReceiver, this.mDeviceState.isInExclusionRegion(motionEvent), getSwipeUpHandlerFactory());
    }

    private InputConsumer createDeviceLockedInputConsumer(GestureState gestureState) {
        if (!this.mDeviceState.isFullyGesturalNavMode() || gestureState.getRunningTask() == null) {
            return getDefaultInputConsumer();
        }
        return new DeviceLockedInputConsumer(this, this.mDeviceState, this.mTaskAnimationManager, gestureState, this.mInputMonitorCompat);
    }

    public InputConsumer createOverviewInputConsumer(GestureState gestureState, GestureState gestureState2, MotionEvent motionEvent, boolean z) {
        StatefulActivity createdActivity = gestureState2.getActivityInterface().getCreatedActivity();
        if (createdActivity == null) {
            return getDefaultInputConsumer();
        }
        if (createdActivity.getRootView().hasWindowFocus() || gestureState.isRunningAnimationToLauncher() || ((FeatureFlags.ASSISTANT_GIVES_LAUNCHER_FOCUS.get() && z) || (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && gestureState2.getActivityInterface().isInLiveTileMode()))) {
            return new OverviewInputConsumer(gestureState2, createdActivity, this.mInputMonitorCompat, false);
        }
        return new OverviewWithoutFocusInputConsumer(createdActivity, this.mDeviceState, gestureState2, this.mInputMonitorCompat, this.mDeviceState.isInExclusionRegion(motionEvent));
    }

    /* access modifiers changed from: private */
    public void onConsumerInactive(InputConsumer inputConsumer) {
        InputConsumer inputConsumer2 = this.mConsumer;
        if (inputConsumer2 != null && inputConsumer2.getActiveConsumerInHierarchy() == inputConsumer) {
            reset();
        }
    }

    private void reset() {
        InputConsumer defaultInputConsumer = getDefaultInputConsumer();
        this.mUncheckedConsumer = defaultInputConsumer;
        this.mConsumer = defaultInputConsumer;
        this.mGestureState = GestureState.DEFAULT_STATE;
        InputChannelCompat.InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.setBatchingEnabled(true);
        }
    }

    private InputConsumer getDefaultInputConsumer() {
        ResetGestureInputConsumer resetGestureInputConsumer = this.mResetGestureInputConsumer;
        if (resetGestureInputConsumer != null) {
            return resetGestureInputConsumer;
        }
        return InputConsumer.NO_OP;
    }

    /* access modifiers changed from: private */
    public void preloadOverview(boolean z) {
        preloadOverview(z, false);
    }

    /* access modifiers changed from: private */
    public void preloadOverview(boolean z, boolean z2) {
        if (this.mDeviceState.isUserUnlocked()) {
            if (this.mDeviceState.isButtonNavMode() && !this.mOverviewComponentObserver.isHomeAndOverviewSame()) {
                return;
            }
            if ((!RestoreDbTask.isPending(this) || z2) && this.mDeviceState.isUserSetupComplete()) {
                BaseActivityInterface activityInterface = this.mOverviewComponentObserver.getActivityInterface();
                Intent intent = new Intent(this.mOverviewComponentObserver.getOverviewIntentIgnoreSysUiState());
                if (activityInterface.getCreatedActivity() == null || !z) {
                    this.mTaskAnimationManager.preloadRecentsAnimation(intent);
                }
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        StatefulActivity createdActivity;
        if (!this.mDeviceState.isUserUnlocked() || (createdActivity = this.mOverviewComponentObserver.getActivityInterface().getCreatedActivity()) == null || createdActivity.isStarted()) {
            return;
        }
        if (this.mOverviewComponentObserver.canHandleConfigChanges(createdActivity.getComponentName(), createdActivity.getResources().getConfiguration().diff(configuration))) {
            this.mDeviceState.onOneHandedModeChanged(ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, getApplicationContext().getResources()));
            return;
        }
        preloadOverview(false);
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StatefulActivity statefulActivity;
        if (strArr.length <= 0 || !Utilities.IS_DEBUG_DEVICE) {
            FeatureFlags.dump(printWriter);
            if (this.mDeviceState.isUserUnlocked()) {
                PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(getBaseContext()).dump(printWriter);
            }
            this.mDeviceState.dump(printWriter);
            OverviewComponentObserver overviewComponentObserver = this.mOverviewComponentObserver;
            if (overviewComponentObserver != null) {
                overviewComponentObserver.dump(printWriter);
            }
            OverviewCommandHelper overviewCommandHelper = this.mOverviewCommandHelper;
            if (overviewCommandHelper != null) {
                overviewCommandHelper.dump(printWriter);
            }
            GestureState gestureState = this.mGestureState;
            if (gestureState != null) {
                gestureState.dump(printWriter);
            }
            printWriter.println("Input state:");
            printWriter.println("  mInputMonitorCompat=" + this.mInputMonitorCompat);
            printWriter.println("  mInputEventReceiver=" + this.mInputEventReceiver);
            DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).dump(printWriter);
            printWriter.println("TouchState:");
            OverviewComponentObserver overviewComponentObserver2 = this.mOverviewComponentObserver;
            if (overviewComponentObserver2 == null) {
                statefulActivity = null;
            } else {
                statefulActivity = overviewComponentObserver2.getActivityInterface().getCreatedActivity();
            }
            OverviewComponentObserver overviewComponentObserver3 = this.mOverviewComponentObserver;
            boolean z = overviewComponentObserver3 != null && overviewComponentObserver3.getActivityInterface().isResumed();
            printWriter.println("  createdOverviewActivity=" + statefulActivity);
            printWriter.println("  resumed=" + z);
            printWriter.println("  mConsumer=" + this.mConsumer.getName());
            ActiveGestureLog.INSTANCE.dump("", printWriter);
            RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).dump("", printWriter);
            printWriter.println("ProtoTrace:");
            printWriter.println("  file=" + ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getTraceFile());
            if (statefulActivity != null) {
                statefulActivity.getDeviceProfile().dump("", printWriter);
            }
            this.mTaskbarManager.dumpLogs("", printWriter);
            return;
        }
        LinkedList linkedList = new LinkedList(Arrays.asList(strArr));
        String str = (String) linkedList.pollFirst();
        str.hashCode();
        if (str.equals("cmd")) {
            if (linkedList.peekFirst() == null) {
                printAvailableCommands(printWriter);
            } else {
                onCommand(printWriter, linkedList);
            }
        }
    }

    private void printAvailableCommands(PrintWriter printWriter) {
        printWriter.println("Available commands:");
        printWriter.println("  clear-touch-log: Clears the touch interaction log");
    }

    private void onCommand(PrintWriter printWriter, LinkedList<String> linkedList) {
        String pollFirst = linkedList.pollFirst();
        pollFirst.hashCode();
        if (pollFirst.equals("clear-touch-log")) {
            ActiveGestureLog.INSTANCE.clear();
        }
    }

    /* access modifiers changed from: private */
    public AbsSwipeUpHandler createLauncherSwipeHandler(GestureState gestureState, long j) {
        RecentsAnimationDeviceState recentsAnimationDeviceState = this.mDeviceState;
        TaskAnimationManager taskAnimationManager = this.mTaskAnimationManager;
        return new LauncherSwipeHandlerV2(this, recentsAnimationDeviceState, taskAnimationManager, gestureState, j, taskAnimationManager.isRecentsAnimationRunning(), this.mInputConsumer);
    }

    /* access modifiers changed from: private */
    public AbsSwipeUpHandler createFallbackSwipeHandler(GestureState gestureState, long j) {
        RecentsAnimationDeviceState recentsAnimationDeviceState = this.mDeviceState;
        TaskAnimationManager taskAnimationManager = this.mTaskAnimationManager;
        return new FallbackSwipeHandler(this, recentsAnimationDeviceState, taskAnimationManager, gestureState, j, taskAnimationManager.isRecentsAnimationRunning(), this.mInputConsumer);
    }

    public void writeToProto(LauncherTraceProto.Builder builder) {
        TouchInteractionServiceProto.Builder newBuilder = TouchInteractionServiceProto.newBuilder();
        newBuilder.setServiceConnected(true);
        OverviewComponentObserver overviewComponentObserver = this.mOverviewComponentObserver;
        if (overviewComponentObserver != null) {
            overviewComponentObserver.writeToProto(newBuilder);
        }
        this.mConsumer.writeToProto(newBuilder);
        builder.setTouchInteractionService(newBuilder);
    }
}
