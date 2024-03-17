package com.android.launcher3;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.hardware.SensorManager;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.model.WellbeingModel;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.QuickstepSystemShortcut;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.proxy.ProxyActivityStarter;
import com.android.launcher3.proxy.StartActivityParams;
import com.android.launcher3.statehandlers.BackButtonAlphaHandler;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.taskbar.LauncherTaskbarUIController;
import com.android.launcher3.taskbar.TaskbarManager;
import com.android.launcher3.uioverrides.DeviceFlag;
import com.android.launcher3.uioverrides.RecentsViewStateController;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.ObjectWrapper;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.UiThreadHelper;
import com.android.quickstep.OverviewCommandHelper;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.TouchInteractionService;
import com.android.quickstep.util.LauncherUnfoldAnimationController;
import com.android.quickstep.util.ProxyScreenStatusProvider;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.quickstep.util.RemoteFadeOutAnimationListener;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.util.TISBindHelper;
import com.android.quickstep.views.OverviewActionsView;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.unfold.UnfoldTransitionFactory;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import com.android.systemui.unfold.config.UnfoldTransitionConfig;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class BaseQuickstepLauncher extends Launcher {
    public static final UiThreadHelper.AsyncCommand SET_BACK_BUTTON_ALPHA = $$Lambda$BaseQuickstepLauncher$f_kzozjMzRC_NNQmaGpAVmGp8.INSTANCE;
    private OverviewActionsView mActionsView;
    private QuickstepTransitionManager mAppTransitionManager;
    private DepthController mDepthController = new DepthController(this);
    private LauncherUnfoldAnimationController mLauncherUnfoldAnimationController;
    private DragOptions mNextWorkspaceDragOptions = null;
    private OverviewCommandHelper mOverviewCommandHelper;
    private TISBindHelper mTISBindHelper;
    private TaskbarManager mTaskbarManager;
    private LauncherTaskbarUIController mTaskbarUIController;
    private UnfoldTransitionProgressProvider mUnfoldTransitionProgressProvider;

    static /* synthetic */ void lambda$static$0(Context context, int i, int i2) {
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).lambda$setNavBarButtonAlpha$2$SystemUiProxy(Float.intBitsToFloat(i), i2 != 0);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addMultiWindowModeChangedListener(this.mDepthController);
        initUnfoldTransitionProgressProvider();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        LauncherUnfoldAnimationController launcherUnfoldAnimationController = this.mLauncherUnfoldAnimationController;
        if (launcherUnfoldAnimationController != null) {
            launcherUnfoldAnimationController.onResume();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        LauncherUnfoldAnimationController launcherUnfoldAnimationController = this.mLauncherUnfoldAnimationController;
        if (launcherUnfoldAnimationController != null) {
            launcherUnfoldAnimationController.onPause();
        }
        super.onPause();
    }

    public void onDestroy() {
        this.mAppTransitionManager.onActivityDestroyed();
        UnfoldTransitionProgressProvider unfoldTransitionProgressProvider = this.mUnfoldTransitionProgressProvider;
        if (unfoldTransitionProgressProvider != null) {
            unfoldTransitionProgressProvider.destroy();
        }
        this.mTISBindHelper.onDestroy();
        TaskbarManager taskbarManager = this.mTaskbarManager;
        if (taskbarManager != null) {
            taskbarManager.clearActivity(this);
        }
        LauncherUnfoldAnimationController launcherUnfoldAnimationController = this.mLauncherUnfoldAnimationController;
        if (launcherUnfoldAnimationController != null) {
            launcherUnfoldAnimationController.onDestroy();
        }
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        OverviewCommandHelper overviewCommandHelper = this.mOverviewCommandHelper;
        if (overviewCommandHelper != null) {
            overviewCommandHelper.clearPendingCommands();
        }
    }

    public QuickstepTransitionManager getAppTransitionManager() {
        return this.mAppTransitionManager;
    }

    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getThumbnailCache().getHighResLoadingState().setVisible(true);
    }

    /* access modifiers changed from: protected */
    public void handleGestureContract(Intent intent) {
        if (FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get()) {
            super.handleGestureContract(intent);
        }
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).onTrimMemory(i);
    }

    public void onUiChangedWhileSleeping() {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() {
            public final void run() {
                BaseQuickstepLauncher.this.lambda$onUiChangedWhileSleeping$1$BaseQuickstepLauncher();
            }
        });
    }

    public /* synthetic */ void lambda$onUiChangedWhileSleeping$1$BaseQuickstepLauncher() {
        ActivityManagerWrapper.getInstance().invalidateHomeTaskSnapshot(this);
    }

    /* access modifiers changed from: protected */
    public void onScreenOff() {
        super.onScreenOff();
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            ((RecentsView) getOverviewPanel()).finishRecentsAnimation(true, (Runnable) null);
        }
    }

    public void onScrollChanged(float f) {
        super.onScrollChanged(f);
        this.mDepthController.onOverlayScrollChanged(f);
        onTaskbarInAppDisplayProgressUpdate(f, 0);
    }

    public void onAllAppsTransition(float f) {
        super.onAllAppsTransition(f);
        onTaskbarInAppDisplayProgressUpdate(f, 1);
    }

    public void onWidgetsTransition(float f) {
        super.onWidgetsTransition(f);
        onTaskbarInAppDisplayProgressUpdate(f, 2);
    }

    private void onTaskbarInAppDisplayProgressUpdate(float f, int i) {
        LauncherTaskbarUIController launcherTaskbarUIController;
        TaskbarManager taskbarManager = this.mTaskbarManager;
        if (taskbarManager != null && taskbarManager.getCurrentActivityContext() != null && (launcherTaskbarUIController = this.mTaskbarUIController) != null) {
            launcherTaskbarUIController.onTaskbarInAppDisplayProgressUpdate(f, i);
        }
    }

    public void startIntentSenderForResult(IntentSender intentSender, int i, Intent intent, int i2, int i3, int i4, Bundle bundle) {
        if (i != -1) {
            this.mPendingActivityRequestCode = i;
            StartActivityParams startActivityParams = new StartActivityParams((Activity) this, i);
            startActivityParams.intentSender = intentSender;
            startActivityParams.fillInIntent = intent;
            startActivityParams.flagsMask = i2;
            startActivityParams.flagsValues = i3;
            startActivityParams.extraFlags = i4;
            startActivityParams.options = bundle;
            startActivity(ProxyActivityStarter.getLaunchIntent(this, startActivityParams));
            return;
        }
        super.startIntentSenderForResult(intentSender, i, intent, i2, i3, i4, bundle);
    }

    public void startActivityForResult(Intent intent, int i, Bundle bundle) {
        if (i != -1) {
            this.mPendingActivityRequestCode = i;
            StartActivityParams startActivityParams = new StartActivityParams((Activity) this, i);
            startActivityParams.intent = intent;
            startActivityParams.options = bundle;
            startActivity(ProxyActivityStarter.getLaunchIntent(this, startActivityParams));
            return;
        }
        super.startActivityForResult(intent, i, bundle);
    }

    /* access modifiers changed from: protected */
    public void onDeferredResumed() {
        super.onDeferredResumed();
        handlePendingActivityRequest();
    }

    public void onStateSetEnd(LauncherState launcherState) {
        super.onStateSetEnd(launcherState);
        handlePendingActivityRequest();
    }

    private void handlePendingActivityRequest() {
        if (this.mPendingActivityRequestCode != -1 && isInState(LauncherState.NORMAL) && (getActivityFlags() & 4) != 0) {
            onActivityResult(this.mPendingActivityRequestCode, 0, (Intent) null);
            startActivity(ProxyActivityStarter.getLaunchIntent(this, (StartActivityParams) null));
        }
    }

    /* access modifiers changed from: protected */
    public void setupViews() {
        super.setupViews();
        this.mActionsView = (OverviewActionsView) findViewById(R.id.overview_actions_view);
        RecentsView recentsView = (RecentsView) getOverviewPanel();
        recentsView.init(this.mActionsView, new SplitSelectStateController(this, this.mHandler, getStateManager(), getDepthController()), findViewById(R.id.overview_clear_panel));
        this.mActionsView.updateDimension(getDeviceProfile(), recentsView.getLastComputedTaskSize());
        this.mActionsView.updateVerticalMargin(DisplayController.getNavigationMode(this));
        QuickstepTransitionManager quickstepTransitionManager = new QuickstepTransitionManager(this);
        this.mAppTransitionManager = quickstepTransitionManager;
        quickstepTransitionManager.registerRemoteAnimations();
        this.mAppTransitionManager.registerRemoteTransitions();
        this.mTISBindHelper = new TISBindHelper(this, new Consumer() {
            public final void accept(Object obj) {
                BaseQuickstepLauncher.this.onTISConnected((TouchInteractionService.TISBinder) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    public void onTISConnected(TouchInteractionService.TISBinder tISBinder) {
        TaskbarManager taskbarManager = tISBinder.getTaskbarManager();
        this.mTaskbarManager = taskbarManager;
        if (taskbarManager != null) {
            taskbarManager.setActivity(this);
        }
        this.mOverviewCommandHelper = tISBinder.getOverviewCommandHelper();
    }

    public void runOnBindToTouchInteractionService(Runnable runnable) {
        this.mTISBindHelper.runOnBindToTouchInteractionService(runnable);
    }

    private void initUnfoldTransitionProgressProvider() {
        UnfoldTransitionConfig createConfig = UnfoldTransitionFactory.createConfig(this);
        if (createConfig.isEnabled()) {
            this.mUnfoldTransitionProgressProvider = UnfoldTransitionFactory.createUnfoldTransitionProgressProvider(this, createConfig, ProxyScreenStatusProvider.INSTANCE, (DeviceStateManager) getSystemService(DeviceStateManager.class), (ActivityManager) getSystemService(ActivityManager.class), (SensorManager) getSystemService(SensorManager.class), getMainThreadHandler(), getMainExecutor(), Executors.THREAD_POOL_EXECUTOR, DeviceFlag.NAMESPACE_LAUNCHER);
            this.mLauncherUnfoldAnimationController = new LauncherUnfoldAnimationController(this, getWindowManager(), this.mUnfoldTransitionProgressProvider);
        }
    }

    public void setTaskbarUIController(LauncherTaskbarUIController launcherTaskbarUIController) {
        this.mTaskbarUIController = launcherTaskbarUIController;
    }

    public LauncherTaskbarUIController getTaskbarUIController() {
        return this.mTaskbarUIController;
    }

    public <T extends OverviewActionsView> T getActionsView() {
        return this.mActionsView;
    }

    /* access modifiers changed from: protected */
    public void closeOpenViews(boolean z) {
        super.closeOpenViews(z);
        TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_HOME_KEY);
    }

    /* access modifiers changed from: protected */
    public void collectStateHandlers(List<StateManager.StateHandler> list) {
        super.collectStateHandlers(list);
        list.add(getDepthController());
        list.add(new RecentsViewStateController(this));
        list.add(new BackButtonAlphaHandler(this));
    }

    public DepthController getDepthController() {
        return this.mDepthController;
    }

    public UnfoldTransitionProgressProvider getUnfoldTransitionProgressProvider() {
        return this.mUnfoldTransitionProgressProvider;
    }

    public boolean supportsAdaptiveIconAnimation(View view) {
        return this.mAppTransitionManager.hasControlRemoteAppTransitionPermission();
    }

    public DragOptions getDefaultWorkspaceDragOptions() {
        DragOptions dragOptions = this.mNextWorkspaceDragOptions;
        if (dragOptions == null) {
            return super.getDefaultWorkspaceDragOptions();
        }
        this.mNextWorkspaceDragOptions = null;
        return dragOptions;
    }

    public void setNextWorkspaceDragOptions(DragOptions dragOptions) {
        this.mNextWorkspaceDragOptions = dragOptions;
    }

    public void useFadeOutAnimationForLauncherStart(final CancellationSignal cancellationSignal) {
        getAppTransitionManager().setRemoteAnimationProvider(new RemoteAnimationProvider() {
            public AnimatorSet createWindowAnimation(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2) {
                cancellationSignal.cancel();
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                ofFloat.addUpdateListener(new RemoteFadeOutAnimationListener(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2));
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(ofFloat);
                return animatorSet;
            }
        }, cancellationSignal);
    }

    public float[] getNormalOverviewScaleAndOffset() {
        if (DisplayController.getNavigationMode(this).hasGestures) {
            return new float[]{1.0f, 1.0f};
        }
        return new float[]{1.1f, 0.0f};
    }

    public void onDragLayerHierarchyChanged() {
        onLauncherStateOrFocusChanged();
    }

    /* access modifiers changed from: protected */
    public void onActivityFlagsChanged(int i) {
        LauncherTaskbarUIController launcherTaskbarUIController;
        if ((i & 72) != 0) {
            onLauncherStateOrFocusChanged();
        }
        if ((i & 1) != 0) {
            this.mDepthController.setActivityStarted(isStarted());
        }
        if (!((i & 2) == 0 || (launcherTaskbarUIController = this.mTaskbarUIController) == null)) {
            launcherTaskbarUIController.onLauncherResumedOrPaused(hasBeenResumed());
        }
        super.onActivityFlagsChanged(i);
    }

    public boolean shouldBackButtonBeHidden(LauncherState launcherState) {
        boolean z = true;
        boolean z2 = DisplayController.getNavigationMode(this).hasGestures && launcherState.hasFlag(LauncherState.FLAG_HIDE_BACK_BUTTON) && hasWindowFocus() && (getActivityFlags() & 64) == 0;
        if (!z2) {
            return z2;
        }
        if (AbstractFloatingView.getTopOpenViewWithType(this, 523799) != null) {
            z = false;
        }
        return z;
    }

    private void onLauncherStateOrFocusChanged() {
        boolean shouldBackButtonBeHidden = shouldBackButtonBeHidden(getStateManager().getState());
        if (DisplayController.getNavigationMode(this) == DisplayController.NavigationMode.TWO_BUTTONS) {
            UiThreadHelper.setBackButtonAlphaAsync(this, SET_BACK_BUTTON_ALPHA, shouldBackButtonBeHidden ? 0.0f : 1.0f, true);
        }
        if (getDragLayer() != null) {
            getRootView().setDisallowBackGesture(shouldBackButtonBeHidden);
        }
    }

    public void finishBindingItems(IntSet intSet) {
        super.finishBindingItems(intSet);
        WellbeingModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(this);
    }

    public void onInitialBindComplete(IntSet intSet, RunnableList runnableList) {
        runnableList.add(new Runnable() {
            public final void run() {
                BaseQuickstepLauncher.this.lambda$onInitialBindComplete$2$BaseQuickstepLauncher();
            }
        });
        super.onInitialBindComplete(intSet, runnableList);
    }

    public /* synthetic */ void lambda$onInitialBindComplete$2$BaseQuickstepLauncher() {
        LauncherUnfoldAnimationController launcherUnfoldAnimationController = this.mLauncherUnfoldAnimationController;
        if (launcherUnfoldAnimationController != null) {
            launcherUnfoldAnimationController.updateRegisteredViewsIfNeeded();
        }
    }

    public Stream<SystemShortcut.Factory> getSupportedShortcuts() {
        Log.d("BaseLauncher", "getSupportedShortcuts");
        Stream<BaseDraggingActivity> of = Stream.of(WellbeingModel.SHORTCUT_FACTORY);
        if (FeatureFlags.ENABLE_SPLIT_FROM_WORKSPACE.get()) {
            List<SplitConfigurationOptions.SplitPositionOption> splitPositionOptions = ((RecentsView) getOverviewPanel()).getPagedOrientationHandler().getSplitPositionOptions(this.mDeviceProfile);
            ArrayList arrayList = new ArrayList();
            for (SplitConfigurationOptions.SplitPositionOption splitSelectShortcutByPosition : splitPositionOptions) {
                arrayList.add(QuickstepSystemShortcut.getSplitSelectShortcutByPosition(splitSelectShortcutByPosition));
            }
            of = Stream.concat(of, arrayList.stream());
        }
        return Stream.concat(of, super.getSupportedShortcuts());
    }

    public ActivityOptionsWrapper getActivityLaunchOptions(View view, ItemInfo itemInfo) {
        ActivityOptionsWrapper activityOptionsWrapper;
        if (this.mAppTransitionManager.hasControlRemoteAppTransitionPermission()) {
            activityOptionsWrapper = this.mAppTransitionManager.getActivityLaunchOptions(view);
        } else {
            activityOptionsWrapper = super.getActivityLaunchOptions(view, itemInfo);
        }
        if (this.mLastTouchUpTime > 0) {
            ActivityOptionsCompat.setLauncherSourceInfo(activityOptionsWrapper.options, this.mLastTouchUpTime);
        }
        activityOptionsWrapper.options.setSplashScreenStyle(1);
        activityOptionsWrapper.options.setLaunchDisplayId((view == null || view.getDisplay() == null) ? 0 : view.getDisplay().getDisplayId());
        addLaunchCookie(itemInfo, activityOptionsWrapper.options);
        return activityOptionsWrapper;
    }

    public void addLaunchCookie(ItemInfo itemInfo, ActivityOptions activityOptions) {
        IBinder launchCookie = getLaunchCookie(itemInfo);
        if (launchCookie != null) {
            activityOptions.setLaunchCookie(launchCookie);
        }
    }

    public IBinder getLaunchCookie(ItemInfo itemInfo) {
        if (itemInfo == null) {
            return null;
        }
        int i = itemInfo.container;
        if (i != -101 && i != -100 && itemInfo.container < 0) {
            return ObjectWrapper.wrap(Integer.MIN_VALUE);
        }
        int i2 = itemInfo.itemType;
        if (i2 == 0 || i2 == 1 || i2 == 4 || i2 == 6) {
            return ObjectWrapper.wrap(new Integer(itemInfo.id));
        }
        return ObjectWrapper.wrap(Integer.MIN_VALUE);
    }

    public void setHintUserWillBeActive() {
        addActivityFlags(32);
    }

    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
        super.onDisplayInfoChanged(context, info, i);
        if ((i & 1) != 0) {
            getStateManager().moveToRestState();
        }
        if ((i & 16) != 0) {
            getDragLayer().recreateControllers();
            OverviewActionsView overviewActionsView = this.mActionsView;
            if (overviewActionsView != null) {
                overviewActionsView.updateVerticalMargin(info.navigationMode);
            }
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        DepthController depthController = this.mDepthController;
        if (depthController != null) {
            depthController.dump(str, printWriter);
        }
    }
}
