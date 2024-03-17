package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAnimationRunner;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.graphics.SysUiScrim;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.taskbar.FallbackTaskbarUIController;
import com.android.launcher3.taskbar.TaskbarManager;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.ActivityTracker;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.views.ScrimView;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.TouchInteractionService;
import com.android.quickstep.fallback.FallbackRecentsStateController;
import com.android.quickstep.fallback.FallbackRecentsView;
import com.android.quickstep.fallback.RecentsDragLayer;
import com.android.quickstep.fallback.RecentsState;
import com.android.quickstep.util.RecentsAtomicAnimationFactory;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.util.TISBindHelper;
import com.android.quickstep.views.OverviewActionsView;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class RecentsActivity extends StatefulActivity<RecentsState> {
    public static final ActivityTracker<RecentsActivity> ACTIVITY_TRACKER = new ActivityTracker<>();
    private static final long HOME_APPEAR_DURATION = 250;
    private static final long RECENTS_ANIMATION_TIMEOUT = 1000;
    private OverviewActionsView mActionsView;
    private LauncherAnimationRunner.RemoteAnimationFactory mActivityLaunchAnimationRunner;
    /* access modifiers changed from: private */
    public final Runnable mAnimationStartTimeoutRunnable = new Runnable() {
        public final void run() {
            RecentsActivity.this.onAnimationStartTimeout();
        }
    };
    private final LauncherAnimationRunner.RemoteAnimationFactory mAnimationToHomeFactory = new LauncherAnimationRunner.RemoteAnimationFactory() {
        public void onCreateAnimation(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
            AnimatorPlaybackController createAnimationToNewWorkspace = RecentsActivity.this.getStateManager().createAnimationToNewWorkspace(RecentsState.BG_LAUNCHER, 250);
            createAnimationToNewWorkspace.dispatchOnStart();
            for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : new RemoteAnimationTargets(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, 0).apps) {
                new SurfaceControl.Transaction().setAlpha(remoteAnimationTargetCompat.leash, 1.0f).apply();
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(createAnimationToNewWorkspace.getAnimationPlayer());
            animatorSet.setDuration(250);
            animationResult.setAnimation(animatorSet, RecentsActivity.this, new Runnable() {
                public final void run() {
                    RecentsActivity.AnonymousClass2.this.lambda$onCreateAnimation$0$RecentsActivity$2();
                }
            }, true);
        }

        public /* synthetic */ void lambda$onCreateAnimation$0$RecentsActivity$2() {
            RecentsActivity.this.getStateManager().goToState(RecentsState.HOME, false);
        }
    };
    private RecentsDragLayer mDragLayer;
    /* access modifiers changed from: private */
    public FallbackRecentsView mFallbackRecentsView;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private Configuration mOldConfig;
    private ScrimView mScrimView;
    /* access modifiers changed from: private */
    public StateManager<RecentsState> mStateManager;
    private TISBindHelper mTISBindHelper;
    private TaskbarManager mTaskbarManager;
    private FallbackTaskbarUIController mTaskbarUIController;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    /* access modifiers changed from: protected */
    public void setupViews() {
        inflateRootView(R.layout.fallback_recents_activity);
        setContentView(getRootView());
        this.mDragLayer = (RecentsDragLayer) findViewById(R.id.drag_layer);
        this.mScrimView = (ScrimView) findViewById(R.id.scrim_view);
        this.mFallbackRecentsView = (FallbackRecentsView) findViewById(R.id.overview_panel);
        this.mActionsView = (OverviewActionsView) findViewById(R.id.overview_actions_view);
        SysUiScrim.SYSUI_PROGRESS.set(getRootView().getSysUiScrim(), Float.valueOf(0.0f));
        SplitSelectStateController splitSelectStateController = new SplitSelectStateController(this, this.mHandler, getStateManager(), (DepthController) null);
        View findViewById = findViewById(R.id.overview_clear_panel);
        this.mDragLayer.recreateControllers();
        this.mFallbackRecentsView.init(this.mActionsView, splitSelectStateController, findViewById);
        this.mTISBindHelper = new TISBindHelper(this, new Consumer() {
            public final void accept(Object obj) {
                RecentsActivity.this.onTISConnected((TouchInteractionService.TISBinder) obj);
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
    }

    public void runOnBindToTouchInteractionService(Runnable runnable) {
        this.mTISBindHelper.runOnBindToTouchInteractionService(runnable);
    }

    public void setTaskbarUIController(FallbackTaskbarUIController fallbackTaskbarUIController) {
        this.mTaskbarUIController = fallbackTaskbarUIController;
    }

    public FallbackTaskbarUIController getTaskbarUIController() {
        return this.mTaskbarUIController;
    }

    public void onMultiWindowModeChanged(boolean z, Configuration configuration) {
        onHandleConfigChanged();
        super.onMultiWindowModeChanged(z, configuration);
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ACTIVITY_TRACKER.handleNewIntent(this);
    }

    /* access modifiers changed from: protected */
    public void onHandleConfigChanged() {
        initDeviceProfile();
        AbstractFloatingView.closeOpenViews(this, true, 23947);
        dispatchDeviceProfileChanged();
        reapplyUi();
        this.mDragLayer.recreateControllers();
    }

    /* access modifiers changed from: protected */
    public DeviceProfile createDeviceProfile() {
        DeviceProfile deviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getDeviceProfile(this);
        if (this.mDragLayer == null || !isInMultiWindowMode()) {
            return deviceProfile.copy(this);
        }
        return deviceProfile.getMultiWindowProfile(this, getMultiWindowDisplaySize());
    }

    public BaseDragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public ScrimView getScrimView() {
        return this.mScrimView;
    }

    public <T extends View> T getOverviewPanel() {
        return this.mFallbackRecentsView;
    }

    public OverviewActionsView getActionsView() {
        return this.mActionsView;
    }

    public void returnToHomescreen() {
        super.returnToHomescreen();
    }

    /* access modifiers changed from: private */
    public void onAnimationStartTimeout() {
        LauncherAnimationRunner.RemoteAnimationFactory remoteAnimationFactory = this.mActivityLaunchAnimationRunner;
        if (remoteAnimationFactory != null) {
            remoteAnimationFactory.onAnimationCancelled();
        }
    }

    public ActivityOptionsWrapper getActivityLaunchOptions(View view, ItemInfo itemInfo) {
        if (!(view instanceof TaskView)) {
            return super.getActivityLaunchOptions(view, itemInfo);
        }
        final TaskView taskView = (TaskView) view;
        final RunnableList runnableList = new RunnableList();
        this.mActivityLaunchAnimationRunner = new LauncherAnimationRunner.RemoteAnimationFactory() {
            public void onCreateAnimation(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, LauncherAnimationRunner.AnimationResult animationResult) {
                RecentsActivity.this.mHandler.removeCallbacks(RecentsActivity.this.mAnimationStartTimeoutRunnable);
                AnimatorSet access$200 = RecentsActivity.this.composeRecentsLaunchAnimator(taskView, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3);
                access$200.addListener(RecentsActivity.this.resetStateListener());
                RecentsActivity recentsActivity = RecentsActivity.this;
                RunnableList runnableList = runnableList;
                Objects.requireNonNull(runnableList);
                animationResult.setAnimation(access$200, recentsActivity, new Runnable() {
                    public final void run() {
                        RunnableList.this.executeAllAndDestroy();
                    }
                }, true);
            }

            public void onAnimationCancelled() {
                RecentsActivity.this.mHandler.removeCallbacks(RecentsActivity.this.mAnimationStartTimeoutRunnable);
                runnableList.executeAllAndDestroy();
            }
        };
        ActivityOptionsWrapper activityOptionsWrapper = new ActivityOptionsWrapper(ActivityOptionsCompat.makeRemoteAnimation(new RemoteAnimationAdapterCompat(new LauncherAnimationRunner(this.mUiHandler, this.mActivityLaunchAnimationRunner, true), 336, 120, getIApplicationThread())), runnableList);
        activityOptionsWrapper.options.setSplashScreenStyle(1);
        activityOptionsWrapper.options.setLaunchDisplayId((view == null || view.getDisplay() == null) ? 0 : view.getDisplay().getDisplayId());
        this.mHandler.postDelayed(this.mAnimationStartTimeoutRunnable, RECENTS_ANIMATION_TIMEOUT);
        return activityOptionsWrapper;
    }

    /* access modifiers changed from: private */
    public AnimatorSet composeRecentsLaunchAnimator(TaskView taskView, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3) {
        AnimatorSet animatorSet = new AnimatorSet();
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr4 = remoteAnimationTargetCompatArr;
        boolean taskIsATargetWithMode = TaskUtils.taskIsATargetWithMode(remoteAnimationTargetCompatArr, getTaskId(), 1);
        PendingAnimation pendingAnimation = new PendingAnimation(336);
        TaskViewUtils.createRecentsWindowAnimator(taskView, !taskIsATargetWithMode, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, (DepthController) null, pendingAnimation);
        animatorSet.play(pendingAnimation.buildAnim());
        if (taskIsATargetWithMode) {
            TaskView taskView2 = taskView;
            AnimatorSet createAdjacentPageAnimForTaskLaunch = this.mFallbackRecentsView.createAdjacentPageAnimForTaskLaunch(taskView);
            createAdjacentPageAnimForTaskLaunch.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            createAdjacentPageAnimForTaskLaunch.setDuration(336);
            createAdjacentPageAnimForTaskLaunch.addListener(resetStateListener());
            animatorSet.play(createAdjacentPageAnimForTaskLaunch);
        }
        return animatorSet;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        Log.d(TestProtocol.BAD_STATE, "RecentsActivity onStart mFallbackRecentsView.setContentAlpha(1)");
        this.mFallbackRecentsView.setContentAlpha(1.0f);
        super.onStart();
        this.mFallbackRecentsView.updateLocusId();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        onTrimMemory(20);
        this.mFallbackRecentsView.updateLocusId();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        AccessibilityManagerCompat.sendStateEventToTest(getBaseContext(), 2);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mStateManager = new StateManager<>(this, RecentsState.BG_LAUNCHER);
        this.mOldConfig = new Configuration(getResources().getConfiguration());
        initDeviceProfile();
        setupViews();
        getSystemUiController().updateUiState(0, Themes.getAttrBoolean(this, R.attr.isWorkspaceDarkText));
        ACTIVITY_TRACKER.handleCreate(this);
    }

    public void onConfigurationChanged(Configuration configuration) {
        if ((configuration.diff(this.mOldConfig) & 1152) != 0) {
            onHandleConfigChanged();
        }
        this.mOldConfig.setTo(configuration);
        super.onConfigurationChanged(configuration);
    }

    public void onStateSetEnd(RecentsState recentsState) {
        super.onStateSetEnd(recentsState);
        if (recentsState == RecentsState.DEFAULT) {
            AccessibilityManagerCompat.sendStateEventToTest(getBaseContext(), 2);
        }
    }

    private void initDeviceProfile() {
        this.mDeviceProfile = createDeviceProfile();
        onDeviceProfileInitiated();
    }

    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).getThumbnailCache().getHighResLoadingState().setVisible(true);
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(this).onTrimMemory(i);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        ACTIVITY_TRACKER.onActivityDestroyed(this);
        this.mActivityLaunchAnimationRunner = null;
        this.mTISBindHelper.onDestroy();
        TaskbarManager taskbarManager = this.mTaskbarManager;
        if (taskbarManager != null) {
            taskbarManager.clearActivity(this);
        }
    }

    public void onBackPressed() {
        startHome();
    }

    public void startHome() {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            RecentsView recentsView = (RecentsView) getOverviewPanel();
            recentsView.switchToScreenshot(new Runnable(recentsView) {
                public final /* synthetic */ RecentsView f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RecentsActivity.this.lambda$startHome$0$RecentsActivity(this.f$1);
                }
            });
            return;
        }
        startHomeInternal();
    }

    public /* synthetic */ void lambda$startHome$0$RecentsActivity(RecentsView recentsView) {
        recentsView.finishRecentsAnimation(true, new Runnable() {
            public final void run() {
                RecentsActivity.this.startHomeInternal();
            }
        });
    }

    /* access modifiers changed from: private */
    public void startHomeInternal() {
        startActivity(Utilities.createHomeIntent(), ActivityOptionsCompat.makeRemoteAnimation(new RemoteAnimationAdapterCompat(new LauncherAnimationRunner(getMainThreadHandler(), this.mAnimationToHomeFactory, true), 250, 0, getIApplicationThread())).toBundle());
    }

    /* access modifiers changed from: protected */
    public void collectStateHandlers(List<StateManager.StateHandler> list) {
        list.add(new FallbackRecentsStateController(this));
    }

    public StateManager<RecentsState> getStateManager() {
        return this.mStateManager;
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        printWriter.println(str + "Misc:");
        dumpMisc(str + "\t", printWriter);
    }

    public StateManager.AtomicAnimationFactory<RecentsState> createAtomicAnimationFactory() {
        return new RecentsAtomicAnimationFactory(this);
    }

    /* access modifiers changed from: private */
    public AnimatorListenerAdapter resetStateListener() {
        return new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                RecentsActivity.this.mFallbackRecentsView.resetTaskVisuals();
                RecentsActivity.this.mStateManager.reapplyState();
            }
        };
    }
}
