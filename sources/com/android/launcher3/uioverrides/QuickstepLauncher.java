package com.android.launcher3.uioverrides;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.QuickstepAccessibilityDelegate;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.appprediction.PredictionRowView;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.hybridhotseat.HotseatPredictionController;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.uioverrides.states.QuickstepAtomicAnimationFactory;
import com.android.launcher3.uioverrides.touchcontrollers.NavBarToHomeTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.NoButtonNavbarToOverviewTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.NoButtonQuickSwitchTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.QuickSwitchTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.StatusBarTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.TransposedQuickSwitchTouchController;
import com.android.launcher3.uioverrides.touchcontrollers.TwoButtonNavbarTouchController;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.widget.LauncherAppWidgetHost;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.util.QuickstepOnboardingPrefs;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class QuickstepLauncher extends BaseQuickstepLauncher {
    public static final boolean GO_LOW_RAM_RECENTS_ENABLED = false;
    public static final UiThreadHelper.AsyncCommand SET_SHELF_HEIGHT = $$Lambda$QuickstepLauncher$v3E47rlTMv8PsAbyA0BrjKhkzc.INSTANCE;
    private BgDataModel.FixedContainerItems mAllAppsPredictions;
    private HotseatPredictionController mHotseatPredictionController;

    static /* synthetic */ void lambda$static$0(Context context, int i, int i2) {
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).setShelfHeight(i != 0, i2);
    }

    /* access modifiers changed from: protected */
    public void setupViews() {
        super.setupViews();
        this.mHotseatPredictionController = new HotseatPredictionController(this);
    }

    public void logAppLaunch(StatsLogManager statsLogManager, ItemInfo itemInfo, InstanceId instanceId) {
        if (this.mAllAppsSessionLogId != null && LauncherState.ALL_APPS.equals(getStateManager().getCurrentStableState())) {
            instanceId = this.mAllAppsSessionLogId;
        }
        StatsLogManager.StatsLogger withInstanceId = statsLogManager.logger().withItemInfo(itemInfo).withInstanceId(instanceId);
        if (this.mAllAppsPredictions != null && (itemInfo.itemType == 0 || itemInfo.itemType == 1 || itemInfo.itemType == 6)) {
            int size = this.mAllAppsPredictions.items.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                ItemInfo itemInfo2 = this.mAllAppsPredictions.items.get(i);
                if (itemInfo2.itemType == itemInfo.itemType && itemInfo2.user.equals(itemInfo.user) && Objects.equals(itemInfo2.getIntent(), itemInfo.getIntent())) {
                    withInstanceId.withRank(i);
                    break;
                }
                i++;
            }
        }
        withInstanceId.log(StatsLogManager.LauncherEvent.LAUNCHER_APP_LAUNCH_TAP);
        this.mHotseatPredictionController.logLaunchedAppRankingInfo(itemInfo, instanceId);
    }

    /* access modifiers changed from: protected */
    public void completeAddShortcut(Intent intent, int i, int i2, int i3, int i4, PendingRequestArgs pendingRequestArgs) {
        if (i == -101) {
            this.mHotseatPredictionController.onDeferredDrop(i3, i4);
        }
        super.completeAddShortcut(intent, i, i2, i3, i4, pendingRequestArgs);
    }

    /* access modifiers changed from: protected */
    public LauncherAccessibilityDelegate createAccessibilityDelegate() {
        return new QuickstepAccessibilityDelegate(this);
    }

    public HotseatPredictionController getHotseatPredictionController() {
        return this.mHotseatPredictionController;
    }

    /* access modifiers changed from: protected */
    public QuickstepOnboardingPrefs createOnboardingPrefs(SharedPreferences sharedPreferences) {
        return new QuickstepOnboardingPrefs(this, sharedPreferences);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        onStateOrResumeChanging(false);
    }

    public boolean startActivitySafely(View view, Intent intent, ItemInfo itemInfo) {
        this.mHotseatPredictionController.setPauseUIUpdate(getTaskbarUIController() == null);
        boolean startActivitySafely = super.lambda$startActivitySafely$7$Launcher(view, intent, itemInfo);
        if (getTaskbarUIController() == null && !startActivitySafely) {
            this.mHotseatPredictionController.setPauseUIUpdate(false);
        }
        return startActivitySafely;
    }

    /* access modifiers changed from: protected */
    public void onActivityFlagsChanged(int i) {
        super.onActivityFlagsChanged(i);
        if ((i & 85) != 0) {
            onStateOrResumeChanging((getActivityFlags() & 64) == 0);
        }
        if ((i & 1) != 0 || (i & getActivityFlags() & 4) != 0) {
            this.mHotseatPredictionController.setPauseUIUpdate(false);
        }
    }

    /* access modifiers changed from: protected */
    public void showAllAppsFromIntent(boolean z) {
        TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_HOME_KEY);
        super.showAllAppsFromIntent(z);
    }

    public Stream<SystemShortcut.Factory> getSupportedShortcuts() {
        return Stream.concat(Stream.of(this.mHotseatPredictionController), super.getSupportedShortcuts());
    }

    private void onStateOrResumeChanging(boolean z) {
        LauncherState state = getStateManager().getState();
        int i = 1;
        if ((getActivityFlags() & 1) != 0) {
            DeviceProfile deviceProfile = getDeviceProfile();
            boolean z2 = (getActivityFlags() & 32) != 0;
            if (!(state == LauncherState.NORMAL || state == LauncherState.OVERVIEW) || ((!z2 && !isUserActive()) || deviceProfile.isVerticalBarLayout())) {
                i = 0;
            }
            UiThreadHelper.runAsyncCommand(this, SET_SHELF_HEIGHT, i, deviceProfile.hotseatBarSizePx);
        }
        if (state == LauncherState.NORMAL && !z) {
            ((RecentsView) getOverviewPanel()).setSwipeDownShouldLaunchApp(false);
        }
    }

    public void bindExtraContainerItems(BgDataModel.FixedContainerItems fixedContainerItems) {
        if (fixedContainerItems.containerId == -102) {
            this.mAllAppsPredictions = fixedContainerItems;
            ((PredictionRowView) getAppsView().getFloatingHeaderView().findFixedRowByType(PredictionRowView.class)).setPredictedApps(fixedContainerItems.items);
        } else if (fixedContainerItems.containerId == -103) {
            this.mHotseatPredictionController.setPredictedItems(fixedContainerItems);
        } else if (fixedContainerItems.containerId == -111) {
            getPopupDataProvider().setRecommendedWidgets(fixedContainerItems.items);
        }
    }

    public void bindWorkspaceComponentsRemoved(Predicate<ItemInfo> predicate) {
        super.bindWorkspaceComponentsRemoved(predicate);
        this.mHotseatPredictionController.onModelItemsRemoved(predicate);
    }

    public void onDestroy() {
        super.onDestroy();
        this.mHotseatPredictionController.destroy();
    }

    public void onStateSetEnd(LauncherState launcherState) {
        super.onStateSetEnd(launcherState);
        int i = launcherState.ordinal;
        if (i == 2) {
            RecentsView recentsView = (RecentsView) getOverviewPanel();
            AccessibilityManagerCompat.sendCustomAccessibilityEvent(recentsView.getPageAt(recentsView.getCurrentPage()), 8, (String) null);
        } else if (i == 4) {
            TaskView taskViewAt = ((RecentsView) getOverviewPanel()).getTaskViewAt(0);
            if (taskViewAt != null) {
                taskViewAt.launchTask(new Consumer() {
                    public final void accept(Object obj) {
                        QuickstepLauncher.this.lambda$onStateSetEnd$1$QuickstepLauncher((Boolean) obj);
                    }
                });
            } else {
                getStateManager().goToState(LauncherState.NORMAL);
            }
        } else if (i == 7) {
            Workspace<?> workspace = getWorkspace();
            getStateManager().goToState(LauncherState.NORMAL);
            if (workspace.getNextPage() != 0) {
                Objects.requireNonNull(workspace);
                workspace.post(new Runnable() {
                    public final void run() {
                        Workspace.this.moveToDefaultScreen();
                    }
                });
            }
        } else if (i == 8) {
            getStateManager().goToState(LauncherState.OVERVIEW);
            getDragLayer().performHapticFeedback(1);
        }
    }

    public /* synthetic */ void lambda$onStateSetEnd$1$QuickstepLauncher(Boolean bool) {
        if (!bool.booleanValue()) {
            getStateManager().goToState(LauncherState.OVERVIEW);
        } else {
            getStateManager().moveToRestState();
        }
    }

    public TouchController[] createTouchControllers() {
        Object obj;
        DisplayController.NavigationMode navigationMode = DisplayController.getNavigationMode(this);
        ArrayList arrayList = new ArrayList();
        arrayList.add(getDragController());
        int i = AnonymousClass1.$SwitchMap$com$android$launcher3$util$DisplayController$NavigationMode[navigationMode.ordinal()];
        if (i == 1) {
            arrayList.add(new NoButtonQuickSwitchTouchController(this));
            arrayList.add(new NavBarToHomeTouchController(this));
            arrayList.add(new NoButtonNavbarToOverviewTouchController(this));
        } else if (i != 2) {
            arrayList.add(new PortraitStatesTouchController(this));
        } else {
            arrayList.add(new TwoButtonNavbarTouchController(this));
            if (getDeviceProfile().isVerticalBarLayout()) {
                obj = new TransposedQuickSwitchTouchController(this);
            } else {
                obj = new QuickSwitchTouchController(this);
            }
            arrayList.add(obj);
            arrayList.add(new PortraitStatesTouchController(this));
        }
        if (!getDeviceProfile().isMultiWindowMode) {
            arrayList.add(new StatusBarTouchController(this));
        }
        arrayList.add(new LauncherTaskViewController(this));
        return (TouchController[]) arrayList.toArray(new TouchController[arrayList.size()]);
    }

    /* renamed from: com.android.launcher3.uioverrides.QuickstepLauncher$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$util$DisplayController$NavigationMode;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                com.android.launcher3.util.DisplayController$NavigationMode[] r0 = com.android.launcher3.util.DisplayController.NavigationMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$launcher3$util$DisplayController$NavigationMode = r0
                com.android.launcher3.util.DisplayController$NavigationMode r1 = com.android.launcher3.util.DisplayController.NavigationMode.NO_BUTTON     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$launcher3$util$DisplayController$NavigationMode     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.launcher3.util.DisplayController$NavigationMode r1 = com.android.launcher3.util.DisplayController.NavigationMode.TWO_BUTTONS     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$launcher3$util$DisplayController$NavigationMode     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.launcher3.util.DisplayController$NavigationMode r1 = com.android.launcher3.util.DisplayController.NavigationMode.THREE_BUTTONS     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.uioverrides.QuickstepLauncher.AnonymousClass1.<clinit>():void");
        }
    }

    public StateManager.AtomicAnimationFactory createAtomicAnimationFactory() {
        return new QuickstepAtomicAnimationFactory(this);
    }

    /* access modifiers changed from: protected */
    public LauncherAppWidgetHost createAppWidgetHost() {
        LauncherAppWidgetHost createAppWidgetHost = super.createAppWidgetHost();
        if (FeatureFlags.ENABLE_QUICKSTEP_WIDGET_APP_START.get()) {
            createAppWidgetHost.setInteractionHandler(new QuickstepInteractionHandler(this));
        }
        return createAppWidgetHost;
    }

    private static final class LauncherTaskViewController extends TaskViewTouchController<Launcher> {
        LauncherTaskViewController(Launcher launcher) {
            super(launcher);
        }

        /* access modifiers changed from: protected */
        public boolean isRecentsInteractive() {
            return ((Launcher) this.mActivity).isInState(LauncherState.OVERVIEW) || ((Launcher) this.mActivity).isInState(LauncherState.OVERVIEW_MODAL_TASK);
        }

        /* access modifiers changed from: protected */
        public boolean isRecentsModal() {
            return ((Launcher) this.mActivity).isInState(LauncherState.OVERVIEW_MODAL_TASK);
        }

        /* access modifiers changed from: protected */
        public void onUserControlledAnimationCreated(AnimatorPlaybackController animatorPlaybackController) {
            ((Launcher) this.mActivity).getStateManager().setCurrentUserControlledAnimation(animatorPlaybackController);
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Object obj;
        super.dump(str, fileDescriptor, printWriter, strArr);
        RecentsView recentsView = (RecentsView) getOverviewPanel();
        printWriter.println("\nQuickstepLauncher:");
        StringBuilder append = new StringBuilder().append(str).append("\tmOrientationState: ");
        if (recentsView == null) {
            obj = "recentsNull";
        } else {
            obj = recentsView.getPagedViewOrientedState();
        }
        printWriter.println(append.append(obj).toString());
    }
}
