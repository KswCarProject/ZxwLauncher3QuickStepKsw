package com.android.launcher3.taskbar;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.taskbar.allapps.TaskbarAllAppsController;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.LauncherBindableItemsContainer;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.SettingsCache;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.ViewCache;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.rotation.RotationButtonController;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.unfold.util.ScopedUnfoldTransitionProgressProvider;
import java.io.PrintWriter;
import java.util.function.Supplier;

public class TaskbarActivityContext extends BaseTaskbarContext {
    private static final boolean ENABLE_THREE_BUTTON_TASKBAR = SystemProperties.getBoolean("persist.debug.taskbar_three_button", false);
    private static final String IME_DRAWS_IME_NAV_BAR_RES_NAME = "config_imeDrawsImeNavBar";
    private static final String TAG = "TaskbarActivityContext";
    private static final String WINDOW_TITLE = "Taskbar";
    private final TaskbarShortcutMenuAccessibilityDelegate mAccessibilityDelegate;
    private boolean mBindingItems = false;
    private final TaskbarControllers mControllers;
    private DeviceProfile mDeviceProfile;
    private final TaskbarDragLayer mDragLayer;
    private final boolean mImeDrawsImeNavBar;
    private boolean mIsDestroyed = false;
    private boolean mIsExcludeFromMagnificationRegion = false;
    private boolean mIsFullscreen;
    private final boolean mIsNavBarForceVisible;
    private final boolean mIsNavBarKidsMode;
    private final boolean mIsSafeModeEnabled;
    private final boolean mIsUserSetupComplete;
    private int mLastRequestedNonFullscreenHeight;
    private final RoundedCorner mLeftCorner;
    private final DisplayController.NavigationMode mNavMode;
    private final RoundedCorner mRightCorner;
    private final ViewCache mViewCache = new ViewCache();
    private WindowManager.LayoutParams mWindowLayoutParams;
    private final WindowManager mWindowManager;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TaskbarActivityContext(Context context, DeviceProfile deviceProfile, TaskbarNavButtonController taskbarNavButtonController, ScopedUnfoldTransitionProgressProvider scopedUnfoldTransitionProgressProvider) {
        super(context);
        Context context2;
        NavbarButtonsViewController navbarButtonsViewController;
        TaskbarControllers taskbarControllers;
        DeviceProfile deviceProfile2 = deviceProfile;
        this.mDeviceProfile = deviceProfile2.copy(this);
        Resources resources = getResources();
        this.mNavMode = DisplayController.getNavigationMode(context);
        this.mImeDrawsImeNavBar = ResourceUtils.getBoolByName(IME_DRAWS_IME_NAV_BAR_RES_NAME, resources, false);
        this.mIsSafeModeEnabled = ((Boolean) TraceHelper.allowIpcs("isSafeMode", new Supplier() {
            public final Object get() {
                return TaskbarActivityContext.this.lambda$new$0$TaskbarActivityContext();
            }
        })).booleanValue();
        this.mIsUserSetupComplete = SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getValue(Settings.Secure.getUriFor("user_setup_complete"), 0);
        this.mIsNavBarForceVisible = SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getValue(Settings.Secure.getUriFor("nav_bar_kids_mode"), 0);
        this.mIsNavBarKidsMode = SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getValue(Settings.Secure.getUriFor("nav_bar_kids_mode"), 0);
        updateIconSize(resources);
        Display display = context.getDisplay();
        if (display.getDisplayId() == 0) {
            context2 = context.getApplicationContext();
        } else {
            context2 = context.getApplicationContext().createDisplayContext(display);
        }
        WindowManager windowManager = (WindowManager) context2.getSystemService(WindowManager.class);
        this.mWindowManager = windowManager;
        this.mLeftCorner = display.getRoundedCorner(3);
        this.mRightCorner = display.getRoundedCorner(2);
        TaskbarDragLayer taskbarDragLayer = (TaskbarDragLayer) this.mLayoutInflater.inflate(R.layout.taskbar, (ViewGroup) null, false);
        this.mDragLayer = taskbarDragLayer;
        TaskbarView taskbarView = (TaskbarView) taskbarDragLayer.findViewById(R.id.taskbar_view);
        TaskbarScrimView taskbarScrimView = (TaskbarScrimView) taskbarDragLayer.findViewById(R.id.taskbar_scrim);
        FrameLayout frameLayout = (FrameLayout) taskbarDragLayer.findViewById(R.id.navbuttons_view);
        StashedHandleView stashedHandleView = (StashedHandleView) taskbarDragLayer.findViewById(R.id.stashed_handle);
        this.mAccessibilityDelegate = new TaskbarShortcutMenuAccessibilityDelegate(this);
        TaskbarDragController taskbarDragController = new TaskbarDragController(this);
        if (getPackageManager().hasSystemFeature("android.hardware.type.pc")) {
            navbarButtonsViewController = new DesktopNavbarButtonsViewController(this, frameLayout);
        } else {
            navbarButtonsViewController = new NavbarButtonsViewController(this, frameLayout);
        }
        NavbarButtonsViewController navbarButtonsViewController2 = navbarButtonsViewController;
        RotationButtonController rotationButtonController = r0;
        RotationButtonController rotationButtonController2 = new RotationButtonController(this, context2.getColor(R.color.taskbar_nav_icon_light_color), context2.getColor(R.color.taskbar_nav_icon_dark_color), R.drawable.ic_sysbar_rotate_button_ccw_start_0, R.drawable.ic_sysbar_rotate_button_ccw_start_90, R.drawable.ic_sysbar_rotate_button_cw_start_0, R.drawable.ic_sysbar_rotate_button_cw_start_90, new Supplier() {
            public final Object get() {
                return TaskbarActivityContext.this.lambda$new$1$TaskbarActivityContext();
            }
        });
        TaskbarScrimView taskbarScrimView2 = taskbarScrimView;
        TaskbarDragLayerController taskbarDragLayerController = r0;
        TaskbarDragLayerController taskbarDragLayerController2 = new TaskbarDragLayerController(this, taskbarDragLayer);
        TaskbarViewController taskbarViewController = r0;
        TaskbarViewController taskbarViewController2 = new TaskbarViewController(this, taskbarView);
        TaskbarScrimViewController taskbarScrimViewController = r0;
        TaskbarScrimViewController taskbarScrimViewController2 = new TaskbarScrimViewController(this, taskbarScrimView2);
        TaskbarUnfoldAnimationController taskbarUnfoldAnimationController = r0;
        TaskbarUnfoldAnimationController taskbarUnfoldAnimationController2 = new TaskbarUnfoldAnimationController(this, scopedUnfoldTransitionProgressProvider, windowManager, WindowManagerGlobal.getWindowManagerService());
        TaskbarKeyguardController taskbarKeyguardController = r0;
        TaskbarKeyguardController taskbarKeyguardController2 = new TaskbarKeyguardController(this);
        StashedHandleViewController stashedHandleViewController = r0;
        StashedHandleViewController stashedHandleViewController2 = new StashedHandleViewController(this, stashedHandleView);
        TaskbarStashController taskbarStashController = r0;
        TaskbarStashController taskbarStashController2 = new TaskbarStashController(this);
        TaskbarEduController taskbarEduController = r0;
        TaskbarEduController taskbarEduController2 = new TaskbarEduController(this);
        TaskbarAutohideSuspendController taskbarAutohideSuspendController = r0;
        TaskbarAutohideSuspendController taskbarAutohideSuspendController2 = new TaskbarAutohideSuspendController(this);
        TaskbarPopupController taskbarPopupController = r0;
        TaskbarPopupController taskbarPopupController2 = new TaskbarPopupController(this);
        TaskbarForceVisibleImmersiveController taskbarForceVisibleImmersiveController = r0;
        TaskbarForceVisibleImmersiveController taskbarForceVisibleImmersiveController2 = new TaskbarForceVisibleImmersiveController(this);
        TaskbarAllAppsController taskbarAllAppsController = r0;
        TaskbarAllAppsController taskbarAllAppsController2 = new TaskbarAllAppsController(this, deviceProfile2);
        TaskbarInsetsController taskbarInsetsController = r0;
        TaskbarInsetsController taskbarInsetsController2 = new TaskbarInsetsController(this);
        new TaskbarControllers(this, taskbarDragController, taskbarNavButtonController, navbarButtonsViewController2, rotationButtonController, taskbarDragLayerController, taskbarViewController, taskbarScrimViewController, taskbarUnfoldAnimationController, taskbarKeyguardController, stashedHandleViewController, taskbarStashController, taskbarEduController, taskbarAutohideSuspendController, taskbarPopupController, taskbarForceVisibleImmersiveController, taskbarAllAppsController, taskbarInsetsController);
        this.mControllers = taskbarControllers;
    }

    public /* synthetic */ Boolean lambda$new$0$TaskbarActivityContext() {
        return Boolean.valueOf(getPackageManager().isSafeMode());
    }

    public /* synthetic */ Integer lambda$new$1$TaskbarActivityContext() {
        return Integer.valueOf(getDisplay().getRotation());
    }

    public void init(TaskbarSharedState taskbarSharedState) {
        this.mLastRequestedNonFullscreenHeight = getDefaultTaskbarWindowHeight();
        this.mWindowLayoutParams = createDefaultWindowLayoutParams();
        this.mControllers.init(taskbarSharedState);
        updateSysuiStateFlags(taskbarSharedState.sysuiStateFlags, true);
        this.mWindowManager.addView(this.mDragLayer, this.mWindowLayoutParams);
    }

    public DeviceProfile getDeviceProfile() {
        return this.mDeviceProfile;
    }

    public void updateDeviceProfile(DeviceProfile deviceProfile) {
        this.mControllers.taskbarAllAppsController.updateDeviceProfile(deviceProfile);
        this.mDeviceProfile = deviceProfile.copy(this);
        updateIconSize(getResources());
        AbstractFloatingView.closeAllOpenViewsExcept(this, false, AbstractFloatingView.TYPE_REBIND_SAFE);
        setTaskbarWindowFullscreen(this.mIsFullscreen);
        dispatchDeviceProfileChanged();
    }

    private void updateIconSize(Resources resources) {
        float dimension = resources.getDimension(R.dimen.taskbar_icon_size);
        this.mDeviceProfile.updateIconSize(1.0f, resources);
        this.mDeviceProfile.updateIconSize(dimension / ((float) this.mDeviceProfile.iconSizePx), resources);
    }

    public StatsLogManager getStatsLogManager() {
        return super.getStatsLogManager();
    }

    public WindowManager.LayoutParams createDefaultWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, this.mLastRequestedNonFullscreenHeight, 2024, 536870920, -3);
        layoutParams.setTitle(WINDOW_TITLE);
        layoutParams.packageName = getPackageName();
        layoutParams.gravity = 80;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.receiveInsetsIgnoringZOrder = true;
        layoutParams.softInputMode = 48;
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.privateFlags = 64;
        return layoutParams;
    }

    public void onConfigurationChanged(int i) {
        this.mControllers.onConfigurationChanged(i);
    }

    public boolean isThreeButtonNav() {
        return this.mNavMode == DisplayController.NavigationMode.THREE_BUTTONS;
    }

    public boolean isGestureNav() {
        return this.mNavMode == DisplayController.NavigationMode.NO_BUTTON;
    }

    public boolean imeDrawsImeNavBar() {
        return this.mImeDrawsImeNavBar;
    }

    public int getLeftCornerRadius() {
        RoundedCorner roundedCorner = this.mLeftCorner;
        if (roundedCorner == null) {
            return 0;
        }
        return roundedCorner.getRadius();
    }

    public int getRightCornerRadius() {
        RoundedCorner roundedCorner = this.mRightCorner;
        if (roundedCorner == null) {
            return 0;
        }
        return roundedCorner.getRadius();
    }

    public WindowManager.LayoutParams getWindowLayoutParams() {
        return this.mWindowLayoutParams;
    }

    public TaskbarDragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public Rect getFolderBoundingBox() {
        return this.mControllers.taskbarDragLayerController.getFolderBoundingBox();
    }

    public TaskbarDragController getDragController() {
        return this.mControllers.taskbarDragController;
    }

    public ViewCache getViewCache() {
        return this.mViewCache;
    }

    public View.OnClickListener getItemOnClickListener() {
        return new View.OnClickListener() {
            public final void onClick(View view) {
                TaskbarActivityContext.this.onTaskbarIconClicked(view);
            }
        };
    }

    public void applyOverwritesToLogItem(LauncherAtom.ItemInfo.Builder builder) {
        if (builder.hasContainerInfo()) {
            LauncherAtom.ContainerInfo containerInfo = builder.getContainerInfo();
            if (containerInfo.hasPredictedHotseatContainer()) {
                LauncherAtom.PredictedHotseatContainer predictedHotseatContainer = containerInfo.getPredictedHotseatContainer();
                LauncherAtom.TaskBarContainer.Builder newBuilder = LauncherAtom.TaskBarContainer.newBuilder();
                if (predictedHotseatContainer.hasIndex()) {
                    newBuilder.setIndex(predictedHotseatContainer.getIndex());
                }
                if (predictedHotseatContainer.hasCardinality()) {
                    newBuilder.setCardinality(predictedHotseatContainer.getCardinality());
                }
                builder.setContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setTaskBarContainer(newBuilder));
            } else if (containerInfo.hasHotseat()) {
                LauncherAtom.HotseatContainer hotseat = containerInfo.getHotseat();
                LauncherAtom.TaskBarContainer.Builder newBuilder2 = LauncherAtom.TaskBarContainer.newBuilder();
                if (hotseat.hasIndex()) {
                    newBuilder2.setIndex(hotseat.getIndex());
                }
                builder.setContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setTaskBarContainer(newBuilder2));
            } else if (containerInfo.hasFolder() && containerInfo.getFolder().hasHotseat()) {
                LauncherAtom.FolderContainer.Builder builder2 = (LauncherAtom.FolderContainer.Builder) containerInfo.getFolder().toBuilder();
                LauncherAtom.HotseatContainer hotseat2 = builder2.getHotseat();
                LauncherAtom.TaskBarContainer.Builder newBuilder3 = LauncherAtom.TaskBarContainer.newBuilder();
                if (hotseat2.hasIndex()) {
                    newBuilder3.setIndex(hotseat2.getIndex());
                }
                builder2.setTaskbar(newBuilder3);
                builder2.clearHotseat();
                builder.setContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setFolder(builder2));
            } else if (containerInfo.hasAllAppsContainer()) {
                builder.setContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setAllAppsContainer(((LauncherAtom.AllAppsContainer.Builder) containerInfo.getAllAppsContainer().toBuilder()).setTaskbarContainer(LauncherAtom.TaskBarContainer.newBuilder())));
            } else if (containerInfo.hasPredictionContainer()) {
                builder.setContainerInfo(LauncherAtom.ContainerInfo.newBuilder().setPredictionContainer(((LauncherAtom.PredictionContainer.Builder) containerInfo.getPredictionContainer().toBuilder()).setTaskbarContainer(LauncherAtom.TaskBarContainer.newBuilder())));
            }
        }
    }

    public DotInfo getDotInfoForItem(ItemInfo itemInfo) {
        return getPopupDataProvider().getDotInfoForItem(itemInfo);
    }

    public PopupDataProvider getPopupDataProvider() {
        return this.mControllers.taskbarPopupController.getPopupDataProvider();
    }

    public View.AccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public boolean isBindingItems() {
        return this.mBindingItems;
    }

    public void setBindingItems(boolean z) {
        this.mBindingItems = z;
    }

    public void onDragStart() {
        setTaskbarWindowFullscreen(true);
    }

    public void onDragEnd() {
        maybeSetTaskbarWindowNotFullscreen();
    }

    public void onPopupVisibilityChanged(boolean z) {
        setTaskbarWindowFocusable(z);
    }

    public void setUIController(TaskbarUIController taskbarUIController) {
        this.mControllers.uiController.onDestroy();
        this.mControllers.uiController = taskbarUIController;
        this.mControllers.uiController.init(this.mControllers);
    }

    public void setSetupUIVisible(boolean z) {
        this.mControllers.taskbarStashController.setSetupUIVisible(z);
    }

    public void onDestroy() {
        this.mIsDestroyed = true;
        setUIController(TaskbarUIController.DEFAULT);
        this.mControllers.onDestroy();
        this.mWindowManager.removeViewImmediate(this.mDragLayer);
    }

    public void updateSysuiStateFlags(int i, boolean z) {
        this.mControllers.navbarButtonsViewController.updateStateForSysuiFlags(i, z);
        this.mControllers.taskbarViewController.setImeIsVisible(this.mControllers.navbarButtonsViewController.isImeVisible());
        boolean z2 = true;
        onNotificationShadeExpandChanged((i & 2052) != 0, z);
        this.mControllers.taskbarViewController.setRecentsButtonDisabled(this.mControllers.navbarButtonsViewController.isRecentsDisabled() || isNavBarKidsModeActive());
        this.mControllers.stashedHandleViewController.setIsHomeButtonDisabled(this.mControllers.navbarButtonsViewController.isHomeDisabled());
        this.mControllers.taskbarKeyguardController.updateStateForSysuiFlags(i);
        TaskbarStashController taskbarStashController = this.mControllers.taskbarStashController;
        if (!z && isUserSetupComplete()) {
            z2 = false;
        }
        taskbarStashController.updateStateForSysuiFlags(i, z2);
        this.mControllers.taskbarScrimViewController.updateStateForSysuiFlags(i, z);
        this.mControllers.navButtonController.updateSysuiFlags(i);
        this.mControllers.taskbarForceVisibleImmersiveController.updateSysuiFlags(i);
    }

    private void onNotificationShadeExpandChanged(boolean z, boolean z2) {
        float f = z ? 0.0f : 1.0f;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(this.mControllers.taskbarViewController.getTaskbarIconAlpha().getProperty(4).animateToValue(f));
        if (!isThreeButtonNav()) {
            animatorSet.play(this.mControllers.taskbarDragLayerController.getNotificationShadeBgTaskbar().animateToValue(f));
        }
        animatorSet.start();
        if (z2) {
            animatorSet.end();
        }
    }

    public void onRotationProposal(int i, boolean z) {
        this.mControllers.rotationButtonController.onRotationProposal(i, z);
    }

    public void disableNavBarElements(int i, int i2, int i3, boolean z) {
        if (i == getDisplayId()) {
            this.mControllers.rotationButtonController.onDisable2FlagChanged(i3);
        }
    }

    public void onSystemBarAttributesChanged(int i, int i2) {
        this.mControllers.rotationButtonController.onBehaviorChanged(i, i2);
    }

    public void onNavButtonsDarkIntensityChanged(float f) {
        if (isUserSetupComplete()) {
            this.mControllers.navbarButtonsViewController.getTaskbarNavButtonDarkIntensity().updateValue(f);
        }
    }

    public void setTaskbarWindowFullscreen(boolean z) {
        int i;
        this.mControllers.taskbarAutohideSuspendController.updateFlag(1, z);
        this.mIsFullscreen = z;
        if (z) {
            i = -1;
        } else {
            i = this.mLastRequestedNonFullscreenHeight;
        }
        setTaskbarWindowHeight(i);
    }

    /* access modifiers changed from: package-private */
    public void maybeSetTaskbarWindowNotFullscreen() {
        if (AbstractFloatingView.getAnyView(this, AbstractFloatingView.TYPE_ALL) == null && !this.mControllers.taskbarDragController.isSystemDragInProgress()) {
            setTaskbarWindowFullscreen(false);
        }
    }

    public boolean isTaskbarWindowFullscreen() {
        return this.mIsFullscreen;
    }

    public void updateInsetRoundedCornerFrame(boolean z) {
        if (this.mDragLayer.isAttachedToWindow() && this.mWindowLayoutParams.insetsRoundedCornerFrame != z) {
            this.mWindowLayoutParams.insetsRoundedCornerFrame = z;
            this.mWindowManager.updateViewLayout(this.mDragLayer, this.mWindowLayoutParams);
        }
    }

    public void setTaskbarWindowHeight(int i) {
        if (this.mWindowLayoutParams.height != i && !this.mIsDestroyed) {
            if (i == -1) {
                i = this.mDeviceProfile.heightPx;
            } else {
                this.mLastRequestedNonFullscreenHeight = i;
                if (this.mIsFullscreen) {
                    return;
                }
            }
            this.mWindowLayoutParams.height = i;
            this.mControllers.taskbarInsetsController.onTaskbarWindowHeightOrInsetsChanged();
            this.mWindowManager.updateViewLayout(this.mDragLayer, this.mWindowLayoutParams);
        }
    }

    public int getDefaultTaskbarWindowHeight() {
        return this.mDeviceProfile.taskbarSize + Math.max(getLeftCornerRadius(), getRightCornerRadius());
    }

    public void setTaskbarWindowFocusable(boolean z) {
        if (z) {
            this.mWindowLayoutParams.flags &= -9;
        } else {
            this.mWindowLayoutParams.flags |= 8;
        }
        this.mWindowManager.updateViewLayout(this.mDragLayer, this.mWindowLayoutParams);
    }

    public void setTaskbarWindowFocusableForIme(boolean z) {
        if (z) {
            this.mControllers.navbarButtonsViewController.moveNavButtonsToNewWindow();
        } else {
            this.mControllers.navbarButtonsViewController.moveNavButtonsBackToTaskbarWindow();
        }
        setTaskbarWindowFocusable(z);
    }

    public void addWindowView(View view, WindowManager.LayoutParams layoutParams) {
        this.mWindowManager.addView(view, layoutParams);
    }

    public void removeWindowView(View view) {
        this.mWindowManager.removeViewImmediate(view);
    }

    /* access modifiers changed from: protected */
    public void onTaskbarIconClicked(View view) {
        Object tag = view.getTag();
        if (tag instanceof Task) {
            ActivityManagerWrapper.getInstance().startActivityFromRecents(((Task) tag).key, ActivityOptions.makeBasic());
        } else if (tag instanceof FolderInfo) {
            Folder folder = ((FolderIcon) view).getFolder();
            folder.setOnFolderStateChangedListener(new Folder.OnFolderStateChangedListener(folder) {
                public final /* synthetic */ Folder f$1;

                {
                    this.f$1 = r2;
                }

                public final void onFolderStateChanged(int i) {
                    TaskbarActivityContext.this.lambda$onTaskbarIconClicked$3$TaskbarActivityContext(this.f$1, i);
                }
            });
            setTaskbarWindowFullscreen(true);
            getDragLayer().post(new Runnable(folder) {
                public final /* synthetic */ Folder f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TaskbarActivityContext.this.lambda$onTaskbarIconClicked$5$TaskbarActivityContext(this.f$1);
                }
            });
        } else if (tag instanceof WorkspaceItemInfo) {
            WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) tag;
            if (workspaceItemInfo.isDisabled()) {
                ItemClickHandler.handleDisabledItemClicked(workspaceItemInfo, this);
            } else {
                Intent addFlags = new Intent(workspaceItemInfo.getIntent()).addFlags(268435456);
                try {
                    if (this.mIsSafeModeEnabled && !PackageManagerHelper.isSystemApp(this, addFlags)) {
                        Toast.makeText(this, R.string.safemode_shortcut_error, 0).show();
                    } else if (workspaceItemInfo.isPromise()) {
                        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "start: taskbarPromiseIcon");
                        startActivity(new PackageManagerHelper(this).getMarketIntent(workspaceItemInfo.getTargetPackage()).addFlags(268435456));
                    } else if (workspaceItemInfo.itemType == 6) {
                        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "start: taskbarDeepShortcut");
                        String deepShortcutId = workspaceItemInfo.getDeepShortcutId();
                        ((LauncherApps) getSystemService(LauncherApps.class)).startShortcut(addFlags.getPackage(), deepShortcutId, (Rect) null, (Bundle) null, workspaceItemInfo.user);
                    } else {
                        startItemInfoActivity(workspaceItemInfo);
                    }
                    this.mControllers.uiController.onTaskbarIconLaunched(workspaceItemInfo);
                } catch (ActivityNotFoundException | NullPointerException | SecurityException e) {
                    Toast.makeText(this, R.string.activity_not_found, 0).show();
                    Log.e(TAG, "Unable to launch. tag=" + workspaceItemInfo + " intent=" + addFlags, e);
                }
            }
        } else if (tag instanceof AppInfo) {
            AppInfo appInfo = (AppInfo) tag;
            startItemInfoActivity(appInfo);
            this.mControllers.uiController.onTaskbarIconLaunched(appInfo);
        } else {
            Log.e(TAG, "Unknown type clicked: " + tag);
        }
        AbstractFloatingView.closeAllOpenViews(this);
    }

    public /* synthetic */ void lambda$onTaskbarIconClicked$3$TaskbarActivityContext(Folder folder, int i) {
        if (i == 2) {
            setTaskbarWindowFocusableForIme(true);
        } else if (i == 0) {
            getDragLayer().post(new Runnable() {
                public final void run() {
                    TaskbarActivityContext.this.lambda$onTaskbarIconClicked$2$TaskbarActivityContext();
                }
            });
            folder.setOnFolderStateChangedListener((Folder.OnFolderStateChangedListener) null);
        }
    }

    public /* synthetic */ void lambda$onTaskbarIconClicked$2$TaskbarActivityContext() {
        setTaskbarWindowFocusableForIme(false);
    }

    public /* synthetic */ void lambda$onTaskbarIconClicked$5$TaskbarActivityContext(Folder folder) {
        folder.animateOpen();
        getStatsLogManager().logger().withItemInfo(folder.mInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_OPEN);
        folder.iterateOverItems(new LauncherBindableItemsContainer.ItemOperator() {
            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return TaskbarActivityContext.this.lambda$onTaskbarIconClicked$4$TaskbarActivityContext(itemInfo, view);
            }
        });
    }

    public /* synthetic */ boolean lambda$onTaskbarIconClicked$4$TaskbarActivityContext(ItemInfo itemInfo, View view) {
        this.mControllers.taskbarViewController.setClickAndLongClickListenersForIcon(view);
        view.setHapticFeedbackEnabled(true);
        return false;
    }

    private void startItemInfoActivity(ItemInfo itemInfo) {
        Intent addFlags = new Intent(itemInfo.getIntent()).addFlags(268435456);
        try {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "start: taskbarAppIcon");
            if (itemInfo.user.equals(Process.myUserHandle())) {
                startActivity(addFlags);
            } else {
                ((LauncherApps) getSystemService(LauncherApps.class)).startMainActivity(addFlags.getComponent(), itemInfo.user, addFlags.getSourceBounds(), (Bundle) null);
            }
        } catch (ActivityNotFoundException | NullPointerException | SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, 0).show();
            Log.e(TAG, "Unable to launch. tag=" + itemInfo + " intent=" + addFlags, e);
        }
    }

    public boolean onLongPressToUnstashTaskbar() {
        return this.mControllers.taskbarStashController.onLongPressToUnstashTaskbar();
    }

    public void startTaskbarUnstashHint(boolean z) {
        this.mControllers.taskbarStashController.startUnstashHint(z);
    }

    /* access modifiers changed from: protected */
    public boolean isUserSetupComplete() {
        return this.mIsUserSetupComplete;
    }

    /* access modifiers changed from: protected */
    public boolean isNavBarKidsModeActive() {
        return this.mIsNavBarKidsMode && isThreeButtonNav();
    }

    /* access modifiers changed from: protected */
    public boolean isNavBarForceVisible() {
        return this.mIsNavBarForceVisible;
    }

    /* access modifiers changed from: protected */
    public AnimatorPlaybackController createLauncherStartFromSuwAnim(int i) {
        AnimatorSet animatorSet = new AnimatorSet();
        long j = (long) i;
        animatorSet.setDuration(j);
        TaskbarUIController taskbarUIController = this.mControllers.uiController;
        if (taskbarUIController instanceof LauncherTaskbarUIController) {
            ((LauncherTaskbarUIController) taskbarUIController).addLauncherResumeAnimation(animatorSet, i);
        }
        this.mControllers.taskbarStashController.addUnstashToHotseatAnimation(animatorSet, i);
        if (!FeatureFlags.ENABLE_ALL_APPS_BUTTON_IN_HOTSEAT.get()) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.setDuration(j);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TaskbarActivityContext.this.lambda$createLauncherStartFromSuwAnim$6$TaskbarActivityContext(valueAnimator);
                }
            });
            animatorSet.play(ofFloat);
        }
        return AnimatorPlaybackController.wrap(animatorSet, j);
    }

    public /* synthetic */ void lambda$createLauncherStartFromSuwAnim$6$TaskbarActivityContext(ValueAnimator valueAnimator) {
        this.mControllers.taskbarViewController.getAllAppsButtonView().setAlpha(0.0f);
    }

    public void excludeFromMagnificationRegion(boolean z) {
        if (this.mIsExcludeFromMagnificationRegion != z) {
            this.mIsExcludeFromMagnificationRegion = z;
            if (z) {
                this.mWindowLayoutParams.privateFlags |= 2097152;
            } else {
                this.mWindowLayoutParams.privateFlags &= -2097153;
            }
            this.mWindowManager.updateViewLayout(this.mDragLayer, this.mWindowLayoutParams);
        }
    }

    public void showPopupMenuForIcon(BubbleTextView bubbleTextView) {
        setTaskbarWindowFullscreen(true);
        bubbleTextView.post(new Runnable(bubbleTextView) {
            public final /* synthetic */ BubbleTextView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TaskbarActivityContext.this.lambda$showPopupMenuForIcon$7$TaskbarActivityContext(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$showPopupMenuForIcon$7$TaskbarActivityContext(BubbleTextView bubbleTextView) {
        this.mControllers.taskbarPopupController.showForIcon(bubbleTextView);
    }

    /* access modifiers changed from: protected */
    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarActivityContext:");
        printWriter.println(String.format("%s\tmNavMode=%s", new Object[]{str, this.mNavMode}));
        printWriter.println(String.format("%s\tmImeDrawsImeNavBar=%b", new Object[]{str, Boolean.valueOf(this.mImeDrawsImeNavBar)}));
        printWriter.println(String.format("%s\tmIsUserSetupComplete=%b", new Object[]{str, Boolean.valueOf(this.mIsUserSetupComplete)}));
        printWriter.println(String.format("%s\tmWindowLayoutParams.height=%dpx", new Object[]{str, Integer.valueOf(this.mWindowLayoutParams.height)}));
        printWriter.println(String.format("%s\tmBindInProgress=%b", new Object[]{str, Boolean.valueOf(this.mBindingItems)}));
        this.mControllers.dumpLogs(str + "\t", printWriter);
        this.mDeviceProfile.dump(str, printWriter);
    }
}
