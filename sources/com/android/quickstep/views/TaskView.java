package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewOverlay;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.TransformingTouchDelegate;
import com.android.launcher3.util.ViewPool;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskIconCache;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.TaskThumbnailCache;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.TaskViewUtils;
import com.android.quickstep.util.CancellableTask;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.quickstep.util.TaskCornerRadius;
import com.android.quickstep.util.TransformParams;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import kotlinx.coroutines.internal.LockFreeTaskQueueCore;

public class TaskView extends FrameLayout implements ViewPool.Reusable {
    private static final boolean DEBUG = false;
    private static final long DIM_ANIM_DURATION = 700;
    private static final FloatProperty<TaskView> DISMISS_TRANSLATION_X = new FloatProperty<TaskView>("dismissTranslationX") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setDismissTranslationX(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mDismissTranslationX);
        }
    };
    private static final FloatProperty<TaskView> DISMISS_TRANSLATION_Y = new FloatProperty<TaskView>("dismissTranslationY") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setDismissTranslationY(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mDismissTranslationY);
        }
    };
    private static final float EDGE_SCALE_DOWN_FACTOR_CAROUSEL = 0.03f;
    private static final float EDGE_SCALE_DOWN_FACTOR_GRID = 0.0f;
    public static final int FLAG_UPDATE_ALL = 3;
    public static final int FLAG_UPDATE_ICON = 1;
    public static final int FLAG_UPDATE_THUMBNAIL = 2;
    public static final FloatProperty<TaskView> FOCUS_TRANSITION = new FloatProperty<TaskView>("focusTransition") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setIconAndDimTransitionProgress(f, false);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mFocusTransitionProgress);
        }
    };
    public static final FloatProperty<TaskView> GRID_END_TRANSLATION_X = new FloatProperty<TaskView>("gridEndTranslationX") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setGridEndTranslationX(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mGridEndTranslationX);
        }
    };
    private static final Interpolator GRID_INTERPOLATOR = Interpolators.ACCEL_DEACCEL;
    public static final float MAX_PAGE_SCRIM_ALPHA = 0.4f;
    private static final FloatProperty<TaskView> NON_GRID_TRANSLATION_X = new FloatProperty<TaskView>("nonGridTranslationX") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setNonGridTranslationX(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mNonGridTranslationX);
        }
    };
    private static final FloatProperty<TaskView> NON_GRID_TRANSLATION_Y = new FloatProperty<TaskView>("nonGridTranslationY") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setNonGridTranslationY(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mNonGridTranslationY);
        }
    };
    public static final long SCALE_ICON_DURATION = 120;
    public static final FloatProperty<TaskView> SNAPSHOT_SCALE = new FloatProperty<TaskView>("snapshotScale") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setSnapshotScale(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mSnapshotView.getScaleX());
        }
    };
    private static final FloatProperty<TaskView> SPLIT_SELECT_TRANSLATION_X = new FloatProperty<TaskView>("splitSelectTranslationX") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setSplitSelectTranslationX(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mSplitSelectTranslationX);
        }
    };
    private static final FloatProperty<TaskView> SPLIT_SELECT_TRANSLATION_Y = new FloatProperty<TaskView>("splitSelectTranslationY") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setSplitSelectTranslationY(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mSplitSelectTranslationY);
        }
    };
    private static final List<Rect> SYSTEM_GESTURE_EXCLUSION_RECT = Collections.singletonList(new Rect());
    private static final String TAG = "TaskView";
    private static final FloatProperty<TaskView> TASK_OFFSET_TRANSLATION_X = new FloatProperty<TaskView>("taskOffsetTranslationX") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setTaskOffsetTranslationX(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mTaskOffsetTranslationX);
        }
    };
    private static final FloatProperty<TaskView> TASK_OFFSET_TRANSLATION_Y = new FloatProperty<TaskView>("taskOffsetTranslationY") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setTaskOffsetTranslationY(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mTaskOffsetTranslationY);
        }
    };
    private static final FloatProperty<TaskView> TASK_RESISTANCE_TRANSLATION_X = new FloatProperty<TaskView>("taskResistanceTranslationX") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setTaskResistanceTranslationX(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mTaskResistanceTranslationX);
        }
    };
    private static final FloatProperty<TaskView> TASK_RESISTANCE_TRANSLATION_Y = new FloatProperty<TaskView>("taskResistanceTranslationY") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskView taskView, float f) {
            taskView.setTaskResistanceTranslationY(f);
        }

        public Float get(TaskView taskView) {
            return Float.valueOf(taskView.mTaskResistanceTranslationY);
        }
    };
    protected final StatefulActivity mActivity;
    private float mBoxTranslationY;
    protected final FullscreenDrawParams mCurrentFullscreenParams;
    protected final DigitalWellBeingToast mDigitalWellBeingToast;
    private float mDismissScale;
    /* access modifiers changed from: private */
    public float mDismissTranslationX;
    /* access modifiers changed from: private */
    public float mDismissTranslationY;
    private boolean mEndQuickswitchCuj;
    /* access modifiers changed from: private */
    public float mFocusTransitionProgress;
    private float mFullscreenProgress;
    /* access modifiers changed from: private */
    public float mGridEndTranslationX;
    private float mGridProgress;
    private float mGridTranslationX;
    private float mGridTranslationY;
    /* access modifiers changed from: private */
    public ObjectAnimator mIconAndDimAnimator;
    private final float[] mIconCenterCoords;
    private CancellableTask mIconLoadRequest;
    private float mIconScaleAnimStartProgress;
    private TransformingTouchDelegate mIconTouchDelegate;
    protected IconView mIconView;
    /* access modifiers changed from: private */
    public boolean mIsClickableAsLiveTile;
    private final PointF mLastTouchDownPosition;
    private float mModalness;
    private float mNonGridScale;
    /* access modifiers changed from: private */
    public float mNonGridTranslationX;
    /* access modifiers changed from: private */
    public float mNonGridTranslationY;
    private final TaskOutlineProvider mOutlineProvider;
    private boolean mShowScreenshot;
    protected TaskThumbnailView mSnapshotView;
    private float mSplitSelectScrollOffsetPrimary;
    /* access modifiers changed from: private */
    public float mSplitSelectTranslationX;
    /* access modifiers changed from: private */
    public float mSplitSelectTranslationY;
    private float mStableAlpha;
    protected Task mTask;
    protected final TaskIdAttributeContainer[] mTaskIdAttributeContainer;
    protected final int[] mTaskIdContainer;
    /* access modifiers changed from: private */
    public float mTaskOffsetTranslationX;
    /* access modifiers changed from: private */
    public float mTaskOffsetTranslationY;
    /* access modifiers changed from: private */
    public float mTaskResistanceTranslationX;
    /* access modifiers changed from: private */
    public float mTaskResistanceTranslationY;
    private int mTaskViewId;
    private CancellableTask mThumbnailLoadRequest;

    @Retention(RetentionPolicy.SOURCE)
    public @interface TaskDataChanges {
    }

    public static boolean clipLeft(DeviceProfile deviceProfile) {
        return false;
    }

    public static boolean clipRight(DeviceProfile deviceProfile) {
        return false;
    }

    public static boolean clipTop(DeviceProfile deviceProfile) {
        return false;
    }

    /* access modifiers changed from: protected */
    public int getChildTaskIndexAtPosition(PointF pointF) {
        return 0;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean needsUpdate(int i, int i2) {
        return (i & i2) == i2;
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public static boolean clipBottom(DeviceProfile deviceProfile) {
        return deviceProfile.isTablet;
    }

    public static boolean useFullThumbnail(DeviceProfile deviceProfile) {
        return deviceProfile.isTablet && !deviceProfile.isTaskbarPresentInApps;
    }

    public TaskView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TaskView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNonGridScale = 1.0f;
        this.mDismissScale = 1.0f;
        this.mIconScaleAnimStartProgress = 0.0f;
        this.mFocusTransitionProgress = 1.0f;
        this.mModalness = 0.0f;
        this.mStableAlpha = 1.0f;
        this.mTaskViewId = -1;
        this.mTaskIdContainer = new int[]{-1, -1};
        this.mTaskIdAttributeContainer = new TaskIdAttributeContainer[2];
        this.mIconCenterCoords = new float[2];
        this.mLastTouchDownPosition = new PointF();
        this.mIsClickableAsLiveTile = true;
        StatefulActivity statefulActivity = (StatefulActivity) StatefulActivity.fromContext(context);
        this.mActivity = statefulActivity;
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TaskView.this.onClick(view);
            }
        });
        FullscreenDrawParams fullscreenDrawParams = new FullscreenDrawParams(context);
        this.mCurrentFullscreenParams = fullscreenDrawParams;
        this.mDigitalWellBeingToast = new DigitalWellBeingToast(statefulActivity, this);
        TaskOutlineProvider taskOutlineProvider = new TaskOutlineProvider(getContext(), fullscreenDrawParams, statefulActivity.getDeviceProfile().overviewTaskThumbnailTopMarginPx);
        this.mOutlineProvider = taskOutlineProvider;
        setOutlineProvider(taskOutlineProvider);
    }

    public void setTaskViewId(int i) {
        this.mTaskViewId = i;
    }

    public int getTaskViewId() {
        return this.mTaskViewId;
    }

    public WorkspaceItemInfo getItemInfo() {
        return getItemInfo(this.mTask);
    }

    /* access modifiers changed from: protected */
    public WorkspaceItemInfo getItemInfo(Task task) {
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
        workspaceItemInfo.itemType = 7;
        workspaceItemInfo.container = LauncherSettings.Favorites.CONTAINER_TASKSWITCHER;
        if (task == null) {
            return workspaceItemInfo;
        }
        ComponentKey launchComponentKeyForTask = TaskUtils.getLaunchComponentKeyForTask(task.key);
        workspaceItemInfo.user = launchComponentKeyForTask.user;
        workspaceItemInfo.intent = new Intent().setComponent(launchComponentKeyForTask.componentName);
        workspaceItemInfo.title = task.title;
        if (getRecentsView() != null) {
            workspaceItemInfo.screenId = getRecentsView().indexOfChild(this);
        }
        return workspaceItemInfo;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mSnapshotView = (TaskThumbnailView) findViewById(R.id.snapshot);
        this.mIconView = (IconView) findViewById(R.id.icon);
        this.mIconTouchDelegate = new TransformingTouchDelegate(this.mIconView);
    }

    public boolean offerTouchToChildren(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            computeAndSetIconTouchDelegate(this.mIconView, this.mIconCenterCoords, this.mIconTouchDelegate);
        }
        TransformingTouchDelegate transformingTouchDelegate = this.mIconTouchDelegate;
        return transformingTouchDelegate != null && transformingTouchDelegate.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void computeAndSetIconTouchDelegate(IconView iconView, float[] fArr, TransformingTouchDelegate transformingTouchDelegate) {
        float width = ((float) iconView.getWidth()) / 2.0f;
        fArr[1] = width;
        fArr[0] = width;
        Utilities.getDescendantCoordRelativeToAncestor(iconView, this.mActivity.getDragLayer(), fArr, false);
        transformingTouchDelegate.setBounds((int) (fArr[0] - width), (int) (fArr[1] - width), (int) (fArr[0] + width), (int) (fArr[1] + width));
    }

    public void setModalness(float f) {
        if (this.mModalness != f) {
            this.mModalness = f;
            this.mIconView.setAlpha(Utilities.comp(f));
            this.mDigitalWellBeingToast.updateBannerOffset(f, this.mCurrentFullscreenParams.mCurrentDrawnInsets.top + this.mCurrentFullscreenParams.mCurrentDrawnInsets.bottom);
        }
    }

    public DigitalWellBeingToast getDigitalWellBeingToast() {
        return this.mDigitalWellBeingToast;
    }

    public void bind(Task task, RecentsOrientedState recentsOrientedState) {
        cancelPendingLoadTasks();
        this.mTask = task;
        this.mTaskIdContainer[0] = task.key.id;
        this.mTaskIdAttributeContainer[0] = new TaskIdAttributeContainer(task, this.mSnapshotView, this.mIconView, -1);
        this.mSnapshotView.bind(task);
        setOrientationState(recentsOrientedState);
    }

    public TaskIdAttributeContainer[] getTaskIdAttributeContainers() {
        return this.mTaskIdAttributeContainer;
    }

    public Task getTask() {
        return this.mTask;
    }

    public int[] getTaskIds() {
        return this.mTaskIdContainer;
    }

    public boolean containsMultipleTasks() {
        return this.mTaskIdContainer[1] != -1;
    }

    public TaskThumbnailView getThumbnail() {
        return this.mSnapshotView;
    }

    /* access modifiers changed from: package-private */
    public void refreshThumbnails(HashMap<Integer, ThumbnailData> hashMap) {
        ThumbnailData thumbnailData;
        Task task = this.mTask;
        if (task == null || hashMap == null || (thumbnailData = hashMap.get(Integer.valueOf(task.key.id))) == null) {
            this.mSnapshotView.refresh();
        } else {
            this.mSnapshotView.setThumbnail(this.mTask, thumbnailData);
        }
    }

    public TaskThumbnailView[] getThumbnails() {
        return new TaskThumbnailView[]{this.mSnapshotView};
    }

    public IconView getIconView() {
        return this.mIconView;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mLastTouchDownPosition.set(motionEvent.getX(), motionEvent.getY());
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* access modifiers changed from: private */
    public void onClick(View view) {
        if (getTask() != null && !confirmSecondSplitSelectApp()) {
            launchTasks();
            this.mActivity.getStatsLogManager().logger().withItemInfo(getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_TASK_LAUNCH_TAP);
        }
    }

    private boolean confirmSecondSplitSelectApp() {
        TaskIdAttributeContainer taskIdAttributeContainer = this.mTaskIdAttributeContainer[getChildTaskIndexAtPosition(this.mLastTouchDownPosition)];
        return getRecentsView().confirmSplitSelect(this, taskIdAttributeContainer.getTask(), taskIdAttributeContainer.getIconView(), taskIdAttributeContainer.getThumbnailView());
    }

    public RunnableList launchTaskAnimated() {
        Task task = this.mTask;
        if (task != null) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "startActivityFromRecentsAsync", task);
            ActivityOptionsWrapper activityLaunchOptions = this.mActivity.getActivityLaunchOptions(this, (ItemInfo) null);
            activityLaunchOptions.options.setLaunchDisplayId(getDisplay() == null ? 0 : getDisplay().getDisplayId());
            if (ActivityManagerWrapper.getInstance().startActivityFromRecents(this.mTask.key, activityLaunchOptions.options)) {
                RecentsView recentsView = getRecentsView();
                if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || recentsView.getRunningTaskViewId() == -1) {
                    return activityLaunchOptions.onEndCallback;
                }
                recentsView.onTaskLaunchedInLiveTileMode();
                RunnableList runnableList = new RunnableList();
                recentsView.addSideTaskLaunchCallback(runnableList);
                return runnableList;
            }
            notifyTaskLaunchFailed(TAG);
        }
        return null;
    }

    public void launchTask(Consumer<Boolean> consumer) {
        launchTask(consumer, false);
    }

    public void launchTask(Consumer<Boolean> consumer, boolean z) {
        Task task = this.mTask;
        int i = 0;
        if (task != null) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "startActivityFromRecentsAsync", task);
            ActivityOptions makeCustomAnimation = ActivityOptionsCompat.makeCustomAnimation(getContext(), 0, 0, new Runnable(consumer) {
                public final /* synthetic */ Consumer f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    this.f$0.accept(true);
                }
            }, Executors.MAIN_EXECUTOR.getHandler());
            if (getDisplay() != null) {
                i = getDisplay().getDisplayId();
            }
            makeCustomAnimation.setLaunchDisplayId(i);
            if (z) {
                ActivityOptionsCompat.setFreezeRecentTasksList(makeCustomAnimation);
            }
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(this.mTask.key, makeCustomAnimation, consumer) {
                public final /* synthetic */ Task.TaskKey f$1;
                public final /* synthetic */ ActivityOptions f$2;
                public final /* synthetic */ Consumer f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    TaskView.this.lambda$launchTask$2$TaskView(this.f$1, this.f$2, this.f$3);
                }
            });
            return;
        }
        consumer.accept(false);
    }

    public /* synthetic */ void lambda$launchTask$2$TaskView(Task.TaskKey taskKey, ActivityOptions activityOptions, Consumer consumer) {
        if (!ActivityManagerWrapper.getInstance().startActivityFromRecents(taskKey, activityOptions)) {
            Executors.MAIN_EXECUTOR.post(new Runnable(consumer) {
                public final /* synthetic */ Consumer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TaskView.this.lambda$launchTask$1$TaskView(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$launchTask$1$TaskView(Consumer consumer) {
        notifyTaskLaunchFailed(TAG);
        consumer.accept(false);
    }

    public void launchTasks() {
        RemoteAnimationTargets remoteAnimationTargets;
        final RecentsView recentsView = getRecentsView();
        RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr = recentsView.mRemoteTargetHandles;
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || !isRunningTask() || remoteTargetHandleArr == null) {
            launchTaskAnimated();
        } else if (this.mIsClickableAsLiveTile) {
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).setSplitScreenMinimized(false);
            this.mIsClickableAsLiveTile = false;
            if (remoteTargetHandleArr.length == 1) {
                remoteAnimationTargets = remoteTargetHandleArr[0].getTransformParams().getTargetSet();
            } else {
                TransformParams transformParams = remoteTargetHandleArr[0].getTransformParams();
                TransformParams transformParams2 = remoteTargetHandleArr[1].getTransformParams();
                remoteAnimationTargets = new RemoteAnimationTargets((RemoteAnimationTargetCompat[]) Stream.concat(Arrays.stream(transformParams.getTargetSet().apps), Arrays.stream(transformParams2.getTargetSet().apps)).toArray($$Lambda$TaskView$pDz0gntGLGScCmn66j2ZMTxHeno.INSTANCE), (RemoteAnimationTargetCompat[]) Stream.concat(Arrays.stream(transformParams.getTargetSet().wallpapers), Arrays.stream(transformParams2.getTargetSet().wallpapers)).toArray($$Lambda$TaskView$NJxkFhv4cFxvMKYX0LkmuVC2Dk.INSTANCE), transformParams.getTargetSet().nonApps, transformParams.getTargetSet().targetMode);
            }
            if (remoteAnimationTargets == null) {
                launchTaskAnimated();
                this.mIsClickableAsLiveTile = true;
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            TaskViewUtils.composeRecentsLaunchAnimator(animatorSet, this, remoteAnimationTargets.apps, remoteAnimationTargets.wallpapers, remoteAnimationTargets.nonApps, true, this.mActivity.getStateManager(), recentsView, recentsView.getDepthController());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    recentsView.runActionOnRemoteHandles($$Lambda$TaskView$14$dxgzjfTuOVzFok0jieQptbwKWD4.INSTANCE);
                }

                public void onAnimationEnd(Animator animator) {
                    if (!(TaskView.this.mTask == null || TaskView.this.mTask.key.displayId == TaskView.this.getRootViewDisplayId())) {
                        TaskView.this.launchTaskAnimated();
                    }
                    boolean unused = TaskView.this.mIsClickableAsLiveTile = true;
                }
            });
            animatorSet.start();
            recentsView.onTaskLaunchedInLiveTileMode();
        }
    }

    static /* synthetic */ RemoteAnimationTargetCompat[] lambda$launchTasks$3(int i) {
        return new RemoteAnimationTargetCompat[i];
    }

    static /* synthetic */ RemoteAnimationTargetCompat[] lambda$launchTasks$4(int i) {
        return new RemoteAnimationTargetCompat[i];
    }

    public void onTaskListVisibilityChanged(boolean z) {
        onTaskListVisibilityChanged(z, 3);
    }

    public void onTaskListVisibilityChanged(boolean z, int i) {
        if (this.mTask != null) {
            cancelPendingLoadTasks();
            if (z) {
                RecentsModel recentsModel = RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext());
                TaskThumbnailCache thumbnailCache = recentsModel.getThumbnailCache();
                TaskIconCache iconCache = recentsModel.getIconCache();
                if (needsUpdate(i, 2)) {
                    this.mThumbnailLoadRequest = thumbnailCache.updateThumbnailInBackground(this.mTask, new Consumer() {
                        public final void accept(Object obj) {
                            TaskView.this.lambda$onTaskListVisibilityChanged$5$TaskView((ThumbnailData) obj);
                        }
                    });
                }
                if (needsUpdate(i, 1)) {
                    this.mIconLoadRequest = iconCache.updateIconInBackground(this.mTask, new Consumer() {
                        public final void accept(Object obj) {
                            TaskView.this.lambda$onTaskListVisibilityChanged$6$TaskView((Task) obj);
                        }
                    });
                    return;
                }
                return;
            }
            if (needsUpdate(i, 2)) {
                this.mSnapshotView.setThumbnail((Task) null, (ThumbnailData) null);
                this.mTask.thumbnail = null;
            }
            if (needsUpdate(i, 1)) {
                setIcon(this.mIconView, (Drawable) null);
            }
        }
    }

    public /* synthetic */ void lambda$onTaskListVisibilityChanged$5$TaskView(ThumbnailData thumbnailData) {
        this.mSnapshotView.setThumbnail(this.mTask, thumbnailData);
    }

    public /* synthetic */ void lambda$onTaskListVisibilityChanged$6$TaskView(Task task) {
        setIcon(this.mIconView, task.icon);
        this.mDigitalWellBeingToast.initialize(this.mTask);
    }

    /* access modifiers changed from: protected */
    public void cancelPendingLoadTasks() {
        CancellableTask cancellableTask = this.mThumbnailLoadRequest;
        if (cancellableTask != null) {
            cancellableTask.cancel();
            this.mThumbnailLoadRequest = null;
        }
        CancellableTask cancellableTask2 = this.mIconLoadRequest;
        if (cancellableTask2 != null) {
            cancellableTask2.cancel();
            this.mIconLoadRequest = null;
        }
    }

    private boolean showTaskMenu(IconView iconView) {
        if (!getRecentsView().canLaunchFullscreenTask()) {
            return true;
        }
        if (this.mActivity.getDeviceProfile().isTablet || getRecentsView().isClearAllHidden()) {
            this.mActivity.getStatsLogManager().logger().withItemInfo(getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_TASK_ICON_TAP_OR_LONGPRESS);
            return showTaskMenuWithContainer(iconView);
        }
        getRecentsView().snapToPage(getRecentsView().indexOfChild(this));
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean showTaskMenuWithContainer(IconView iconView) {
        boolean z = false;
        TaskIdAttributeContainer taskIdAttributeContainer = this.mTaskIdAttributeContainer[iconView == this.mIconView ? (char) 0 : 1];
        if (!this.mActivity.getDeviceProfile().isTablet) {
            return TaskMenuView.showForTask(taskIdAttributeContainer);
        }
        if (getRecentsView().isOnGridBottomRow(taskIdAttributeContainer.getTaskView()) && this.mActivity.getDeviceProfile().isLandscape) {
            z = true;
        }
        return TaskMenuViewWithArrow.Companion.showForTask(taskIdAttributeContainer, z);
    }

    /* access modifiers changed from: protected */
    public void setIcon(IconView iconView, Drawable drawable) {
        if (drawable != null) {
            iconView.setDrawable(drawable);
            iconView.setOnClickListener(new View.OnClickListener(iconView) {
                public final /* synthetic */ IconView f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    TaskView.this.lambda$setIcon$7$TaskView(this.f$1, view);
                }
            });
            iconView.setOnLongClickListener(new View.OnLongClickListener(iconView) {
                public final /* synthetic */ IconView f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean onLongClick(View view) {
                    return TaskView.this.lambda$setIcon$8$TaskView(this.f$1, view);
                }
            });
            return;
        }
        iconView.setDrawable((Drawable) null);
        iconView.setOnClickListener((View.OnClickListener) null);
        iconView.setOnLongClickListener((View.OnLongClickListener) null);
    }

    public /* synthetic */ void lambda$setIcon$7$TaskView(IconView iconView, View view) {
        if (!confirmSecondSplitSelectApp()) {
            showTaskMenu(iconView);
        }
    }

    public /* synthetic */ boolean lambda$setIcon$8$TaskView(IconView iconView, View view) {
        requestDisallowInterceptTouchEvent(true);
        return showTaskMenu(iconView);
    }

    public void setOrientationState(RecentsOrientedState recentsOrientedState) {
        int i;
        int i2;
        PagedOrientationHandler orientationHandler = recentsOrientedState.getOrientationHandler();
        boolean z = getLayoutDirection() == 1;
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        boolean isGridTask = isGridTask();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mIconView.getLayoutParams();
        int i3 = deviceProfile.overviewTaskThumbnailTopMarginPx;
        int i4 = deviceProfile.overviewTaskIconSizePx;
        if (isGridTask) {
            i = deviceProfile.overviewTaskMarginGridPx;
        } else {
            i = deviceProfile.overviewTaskMarginPx;
        }
        orientationHandler.setTaskIconParams(layoutParams, (i3 - i4) - i, i4, i3, z);
        layoutParams.height = i4;
        layoutParams.width = i4;
        this.mIconView.setLayoutParams(layoutParams);
        this.mIconView.setRotation(orientationHandler.getDegreesRotated());
        if (isGridTask) {
            i2 = deviceProfile.overviewTaskIconDrawableSizeGridPx;
        } else {
            i2 = deviceProfile.overviewTaskIconDrawableSizePx;
        }
        this.mIconView.setDrawableSize(i2, i2);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mSnapshotView.getLayoutParams();
        layoutParams2.topMargin = i3;
        this.mSnapshotView.setLayoutParams(layoutParams2);
        this.mSnapshotView.getTaskOverlay().updateOrientationState(recentsOrientedState);
        this.mDigitalWellBeingToast.initialize(this.mTask);
    }

    public boolean isGridTask() {
        return this.mActivity.getDeviceProfile().isTablet && !isFocusedTask();
    }

    /* access modifiers changed from: protected */
    public void setIconAndDimTransitionProgress(float f, boolean z) {
        if (z) {
            f = 1.0f - f;
        }
        this.mFocusTransitionProgress = f;
        float f2 = 0.17142858f;
        float f3 = z ? 0.82857144f : 0.0f;
        if (z) {
            f2 = 1.0f;
        }
        float interpolation = Interpolators.clampToProgress(Interpolators.FAST_OUT_SLOW_IN, f3, f2).getInterpolation(f);
        this.mIconView.setAlpha(interpolation);
        this.mDigitalWellBeingToast.updateBannerOffset(1.0f - interpolation, this.mCurrentFullscreenParams.mCurrentDrawnInsets.top + this.mCurrentFullscreenParams.mCurrentDrawnInsets.bottom);
    }

    public void setIconScaleAnimStartProgress(float f) {
        this.mIconScaleAnimStartProgress = f;
    }

    public void animateIconScaleAndDimIntoView() {
        ObjectAnimator objectAnimator = this.mIconAndDimAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, FOCUS_TRANSITION, new float[]{1.0f});
        this.mIconAndDimAnimator = ofFloat;
        ofFloat.setCurrentFraction(this.mIconScaleAnimStartProgress);
        this.mIconAndDimAnimator.setDuration(DIM_ANIM_DURATION).setInterpolator(Interpolators.LINEAR);
        this.mIconAndDimAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator unused = TaskView.this.mIconAndDimAnimator = null;
            }
        });
        this.mIconAndDimAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void setIconScaleAndDim(float f) {
        setIconScaleAndDim(f, false);
    }

    private void setIconScaleAndDim(float f, boolean z) {
        ObjectAnimator objectAnimator = this.mIconAndDimAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        setIconAndDimTransitionProgress(f, z);
    }

    /* access modifiers changed from: protected */
    public void resetPersistentViewTransforms() {
        this.mBoxTranslationY = 0.0f;
        this.mGridTranslationY = 0.0f;
        this.mGridTranslationX = 0.0f;
        this.mNonGridTranslationY = 0.0f;
        this.mNonGridTranslationX = 0.0f;
        resetViewTransforms();
    }

    /* access modifiers changed from: protected */
    public void resetViewTransforms() {
        this.mGridEndTranslationX = 0.0f;
        this.mSplitSelectTranslationX = 0.0f;
        this.mTaskResistanceTranslationX = 0.0f;
        this.mTaskOffsetTranslationX = 0.0f;
        this.mDismissTranslationX = 0.0f;
        this.mTaskResistanceTranslationY = 0.0f;
        this.mTaskOffsetTranslationY = 0.0f;
        this.mDismissTranslationY = 0.0f;
        if (getRecentsView() == null || !getRecentsView().isSplitSelectionActive()) {
            this.mSplitSelectTranslationY = 0.0f;
        }
        setSnapshotScale(1.0f);
        applyTranslationX();
        applyTranslationY();
        setTranslationZ(0.0f);
        setAlpha(this.mStableAlpha);
        setIconScaleAndDim(1.0f);
        setColorTint(0.0f, 0);
    }

    public void setStableAlpha(float f) {
        this.mStableAlpha = f;
        setAlpha(f);
    }

    public void onRecycle() {
        resetPersistentViewTransforms();
        this.mSnapshotView.setThumbnail(this.mTask, (ThumbnailData) null);
        setOverlayEnabled(false);
        onTaskListVisibilityChanged(false);
    }

    public float getTaskCornerRadius() {
        return this.mCurrentFullscreenParams.mCornerRadius;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mActivity.getDeviceProfile().isTablet) {
            setPivotX(getLayoutDirection() == 1 ? 0.0f : (float) (i3 - i));
            setPivotY((float) this.mSnapshotView.getTop());
        } else {
            setPivotX(((float) (i3 - i)) * 0.5f);
            setPivotY(((float) this.mSnapshotView.getTop()) + (((float) this.mSnapshotView.getHeight()) * 0.5f));
        }
        if (Utilities.ATLEAST_Q) {
            List<Rect> list = SYSTEM_GESTURE_EXCLUSION_RECT;
            list.get(0).set(0, 0, getWidth(), getHeight());
            setSystemGestureExclusionRects(list);
        }
    }

    public static float getEdgeScaleDownFactor(DeviceProfile deviceProfile) {
        if (deviceProfile.isTablet) {
            return 0.0f;
        }
        return EDGE_SCALE_DOWN_FACTOR_CAROUSEL;
    }

    private void setNonGridScale(float f) {
        this.mNonGridScale = f;
        applyScale();
    }

    public float getNonGridScale() {
        return this.mNonGridScale;
    }

    /* access modifiers changed from: private */
    public void setSnapshotScale(float f) {
        this.mDismissScale = f;
        applyScale();
    }

    public void setGridProgress(float f) {
        this.mGridProgress = f;
        applyTranslationX();
        applyTranslationY();
        applyScale();
    }

    private void applyScale() {
        float persistentScale = getPersistentScale() * 1.0f * this.mDismissScale;
        setScaleX(persistentScale);
        setScaleY(persistentScale);
        updateSnapshotRadius();
    }

    public float getPersistentScale() {
        return Utilities.mapRange(GRID_INTERPOLATOR.getInterpolation(this.mGridProgress), this.mNonGridScale, 1.0f) * 1.0f;
    }

    /* access modifiers changed from: private */
    public void setSplitSelectTranslationX(float f) {
        this.mSplitSelectTranslationX = f;
        applyTranslationX();
    }

    /* access modifiers changed from: private */
    public void setSplitSelectTranslationY(float f) {
        this.mSplitSelectTranslationY = f;
        applyTranslationY();
    }

    public void setSplitScrollOffsetPrimary(float f) {
        this.mSplitSelectScrollOffsetPrimary = f;
    }

    /* access modifiers changed from: private */
    public void setDismissTranslationX(float f) {
        this.mDismissTranslationX = f;
        applyTranslationX();
    }

    /* access modifiers changed from: private */
    public void setDismissTranslationY(float f) {
        this.mDismissTranslationY = f;
        applyTranslationY();
    }

    /* access modifiers changed from: private */
    public void setTaskOffsetTranslationX(float f) {
        this.mTaskOffsetTranslationX = f;
        applyTranslationX();
    }

    /* access modifiers changed from: private */
    public void setTaskOffsetTranslationY(float f) {
        this.mTaskOffsetTranslationY = f;
        applyTranslationY();
    }

    /* access modifiers changed from: private */
    public void setTaskResistanceTranslationX(float f) {
        this.mTaskResistanceTranslationX = f;
        applyTranslationX();
    }

    /* access modifiers changed from: private */
    public void setTaskResistanceTranslationY(float f) {
        this.mTaskResistanceTranslationY = f;
        applyTranslationY();
    }

    /* access modifiers changed from: private */
    public void setNonGridTranslationX(float f) {
        this.mNonGridTranslationX = f;
        applyTranslationX();
    }

    /* access modifiers changed from: private */
    public void setNonGridTranslationY(float f) {
        this.mNonGridTranslationY = f;
        applyTranslationY();
    }

    public void setGridTranslationX(float f) {
        this.mGridTranslationX = f;
        applyTranslationX();
    }

    public float getGridTranslationX() {
        return this.mGridTranslationX;
    }

    public void setGridTranslationY(float f) {
        this.mGridTranslationY = f;
        applyTranslationY();
    }

    public float getGridTranslationY() {
        return this.mGridTranslationY;
    }

    /* access modifiers changed from: private */
    public void setGridEndTranslationX(float f) {
        this.mGridEndTranslationX = f;
        applyTranslationX();
    }

    public float getScrollAdjustment(boolean z, boolean z2) {
        float f;
        if (z2) {
            f = this.mGridTranslationX;
        } else {
            f = ((Float) getPrimaryNonGridTranslationProperty().get(this)).floatValue();
        }
        return f + 0.0f + this.mSplitSelectScrollOffsetPrimary;
    }

    public float getOffsetAdjustment(boolean z, boolean z2) {
        return getScrollAdjustment(z, z2);
    }

    public float getSizeAdjustment(boolean z) {
        if (z) {
            return 1.0f * this.mNonGridScale;
        }
        return 1.0f;
    }

    private void setBoxTranslationY(float f) {
        this.mBoxTranslationY = f;
        applyTranslationY();
    }

    private void applyTranslationX() {
        setTranslationX(this.mDismissTranslationX + this.mTaskOffsetTranslationX + this.mTaskResistanceTranslationX + this.mSplitSelectTranslationX + this.mGridEndTranslationX + getPersistentTranslationX());
    }

    private void applyTranslationY() {
        setTranslationY(this.mDismissTranslationY + this.mTaskOffsetTranslationY + this.mTaskResistanceTranslationY + this.mSplitSelectTranslationY + getPersistentTranslationY());
    }

    public float getPersistentTranslationX() {
        return getNonGridTrans(this.mNonGridTranslationX) + getGridTrans(this.mGridTranslationX);
    }

    public float getPersistentTranslationY() {
        return this.mBoxTranslationY + getNonGridTrans(this.mNonGridTranslationY) + getGridTrans(this.mGridTranslationY);
    }

    public FloatProperty<TaskView> getPrimarySplitTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getPrimaryValue(SPLIT_SELECT_TRANSLATION_X, SPLIT_SELECT_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getSecondarySplitTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getSecondaryValue(SPLIT_SELECT_TRANSLATION_X, SPLIT_SELECT_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getPrimaryDismissTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getPrimaryValue(DISMISS_TRANSLATION_X, DISMISS_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getSecondaryDissmissTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getSecondaryValue(DISMISS_TRANSLATION_X, DISMISS_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getPrimaryTaskOffsetTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getPrimaryValue(TASK_OFFSET_TRANSLATION_X, TASK_OFFSET_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getTaskResistanceTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getSecondaryValue(TASK_RESISTANCE_TRANSLATION_X, TASK_RESISTANCE_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getPrimaryNonGridTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getPrimaryValue(NON_GRID_TRANSLATION_X, NON_GRID_TRANSLATION_Y);
    }

    public FloatProperty<TaskView> getSecondaryNonGridTranslationProperty() {
        return (FloatProperty) getPagedOrientationHandler().getSecondaryValue(NON_GRID_TRANSLATION_X, NON_GRID_TRANSLATION_Y);
    }

    public boolean isEndQuickswitchCuj() {
        return this.mEndQuickswitchCuj;
    }

    public void setEndQuickswitchCuj(boolean z) {
        this.mEndQuickswitchCuj = z;
    }

    private static final class TaskOutlineProvider extends ViewOutlineProvider {
        private FullscreenDrawParams mFullscreenParams;
        private int mMarginTop;

        TaskOutlineProvider(Context context, FullscreenDrawParams fullscreenDrawParams, int i) {
            this.mMarginTop = i;
            this.mFullscreenParams = fullscreenDrawParams;
        }

        public void updateParams(FullscreenDrawParams fullscreenDrawParams, int i) {
            this.mFullscreenParams = fullscreenDrawParams;
            this.mMarginTop = i;
        }

        public void getOutline(View view, Outline outline) {
            RectF rectF = this.mFullscreenParams.mCurrentDrawnInsets;
            float f = this.mFullscreenParams.mScale;
            outline.setRoundRect(0, (int) (((float) this.mMarginTop) * f), (int) ((rectF.left + ((float) view.getWidth()) + rectF.right) * f), (int) ((rectF.top + ((float) view.getHeight()) + rectF.bottom) * f), this.mFullscreenParams.mCurrentDrawnCornerRadius);
        }
    }

    private int getExpectedViewHeight(View view) {
        int i = view.getLayoutParams().height;
        if (i > 0) {
            return i;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(LockFreeTaskQueueCore.MAX_CAPACITY_MASK, Integer.MIN_VALUE);
        view.measure(makeMeasureSpec, makeMeasureSpec);
        return view.getMeasuredHeight();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.string.accessibility_close, getContext().getText(R.string.accessibility_close)));
        Context context = getContext();
        for (TaskIdAttributeContainer taskIdAttributeContainer : this.mTaskIdAttributeContainer) {
            if (taskIdAttributeContainer != null) {
                for (SystemShortcut createAccessibilityAction : TaskOverlayFactory.getEnabledShortcuts(this, this.mActivity.getDeviceProfile(), taskIdAttributeContainer)) {
                    accessibilityNodeInfo.addAction(createAccessibilityAction.createAccessibilityAction(context));
                }
            }
        }
        if (this.mDigitalWellBeingToast.hasLimit()) {
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(R.string.accessibility_app_usage_settings, getContext().getText(R.string.accessibility_app_usage_settings)));
        }
        RecentsView recentsView = getRecentsView();
        accessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(0, 1, (recentsView.getTaskViewCount() - recentsView.indexOfChild(this)) - 1, 1, false));
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (i == R.string.accessibility_close) {
            getRecentsView().dismissTask(this, true, true);
            return true;
        } else if (i == R.string.accessibility_app_usage_settings) {
            this.mDigitalWellBeingToast.openAppUsageSettings(this);
            return true;
        } else {
            for (TaskIdAttributeContainer taskIdAttributeContainer : this.mTaskIdAttributeContainer) {
                if (taskIdAttributeContainer != null) {
                    for (SystemShortcut next : TaskOverlayFactory.getEnabledShortcuts(this, this.mActivity.getDeviceProfile(), taskIdAttributeContainer)) {
                        if (next.hasHandlerForAction(i)) {
                            next.onClick(this);
                            return true;
                        }
                    }
                    continue;
                }
            }
            return super.performAccessibilityAction(i, bundle);
        }
    }

    public RecentsView getRecentsView() {
        return (RecentsView) getParent();
    }

    /* access modifiers changed from: package-private */
    public PagedOrientationHandler getPagedOrientationHandler() {
        return getRecentsView().mOrientationState.getOrientationHandler();
    }

    private void notifyTaskLaunchFailed(String str) {
        String str2 = "Failed to launch task";
        if (this.mTask != null) {
            str2 = str2 + " (task=" + this.mTask.key.baseIntent + " userId=" + this.mTask.key.userId + ")";
        }
        Log.w(str, str2);
        Toast.makeText(getContext(), R.string.activity_not_available, 0).show();
    }

    public void setFullscreenProgress(float f) {
        float boundToRange = Utilities.boundToRange(f, 0.0f, 1.0f);
        this.mFullscreenProgress = boundToRange;
        this.mIconView.setVisibility(boundToRange < 1.0f ? 0 : 4);
        this.mSnapshotView.getTaskOverlay().setFullscreenProgress(boundToRange);
        updateSnapshotRadius();
        this.mOutlineProvider.updateParams(this.mCurrentFullscreenParams, this.mActivity.getDeviceProfile().overviewTaskThumbnailTopMarginPx);
        invalidateOutline();
    }

    /* access modifiers changed from: protected */
    public void updateSnapshotRadius() {
        updateCurrentFullscreenParams(this.mSnapshotView.getPreviewPositionHelper());
        this.mSnapshotView.setFullscreenParams(this.mCurrentFullscreenParams);
    }

    /* access modifiers changed from: package-private */
    public void updateCurrentFullscreenParams(TaskThumbnailView.PreviewPositionHelper previewPositionHelper) {
        if (getRecentsView() != null) {
            this.mCurrentFullscreenParams.setProgress(this.mFullscreenProgress, getRecentsView().getScaleX(), getScaleX(), getWidth(), this.mActivity.getDeviceProfile(), previewPositionHelper);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateTaskSize() {
        int i;
        int i2;
        float f;
        float f2;
        int i3;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        if (deviceProfile.isTablet) {
            int i4 = deviceProfile.overviewTaskThumbnailTopMarginPx;
            Rect lastComputedTaskSize = getRecentsView().getLastComputedTaskSize();
            int width = lastComputedTaskSize.width();
            int height = lastComputedTaskSize.height();
            if (isFocusedTask()) {
                i3 = height;
                i = width;
            } else {
                Rect lastComputedGridTaskSize = getRecentsView().getLastComputedGridTaskSize();
                i = lastComputedGridTaskSize.width();
                i3 = lastComputedGridTaskSize.height();
            }
            i2 = i3 + i4;
            f = ((float) width) / ((float) i);
            f2 = ((float) ((i2 - i4) - height)) / 2.0f;
        } else {
            i2 = -1;
            i = -1;
            f = 1.0f;
            f2 = 0.0f;
        }
        setNonGridScale(f);
        setBoxTranslationY(f2);
        if (layoutParams.width != i || layoutParams.height != i2) {
            layoutParams.width = i;
            layoutParams.height = i2;
            setLayoutParams(layoutParams);
        }
    }

    private float getGridTrans(float f) {
        return Utilities.mapRange(GRID_INTERPOLATOR.getInterpolation(this.mGridProgress), 0.0f, f);
    }

    private float getNonGridTrans(float f) {
        return f - getGridTrans(f);
    }

    public boolean isRunningTask() {
        if (getRecentsView() != null && this == getRecentsView().getRunningTaskView()) {
            return true;
        }
        return false;
    }

    public boolean isFocusedTask() {
        if (getRecentsView() != null && this == getRecentsView().getFocusedTaskView()) {
            return true;
        }
        return false;
    }

    public void setShowScreenshot(boolean z) {
        this.mShowScreenshot = z;
    }

    public boolean showScreenshot() {
        if (!isRunningTask()) {
            return true;
        }
        return this.mShowScreenshot;
    }

    public void setOverlayEnabled(boolean z) {
        this.mSnapshotView.setOverlayEnabled(z);
    }

    public void initiateSplitSelect(SplitConfigurationOptions.SplitPositionOption splitPositionOption) {
        AbstractFloatingView.closeOpenViews(this.mActivity, false, 2048);
        getRecentsView().initiateSplitSelect(this, splitPositionOption.stagePosition);
    }

    public void setColorTint(float f, int i) {
        this.mSnapshotView.setDimAlpha(f);
        this.mIconView.setIconColorTint(i, f);
        this.mDigitalWellBeingToast.setBannerColorTint(i, f);
    }

    /* access modifiers changed from: private */
    public int getRootViewDisplayId() {
        Display display = getRootView().getDisplay();
        if (display != null) {
            return display.getDisplayId();
        }
        return 0;
    }

    public static class FullscreenDrawParams {
        /* access modifiers changed from: private */
        public final float mCornerRadius;
        public float mCurrentDrawnCornerRadius;
        public RectF mCurrentDrawnInsets = new RectF();
        public float mScale = 1.0f;
        private final float mWindowCornerRadius;

        public FullscreenDrawParams(Context context) {
            float f = TaskCornerRadius.get(context);
            this.mCornerRadius = f;
            this.mWindowCornerRadius = QuickStepContract.getWindowCornerRadius(context);
            this.mCurrentDrawnCornerRadius = f;
        }

        public void setProgress(float f, float f2, float f3, int i, DeviceProfile deviceProfile, TaskThumbnailView.PreviewPositionHelper previewPositionHelper) {
            RectF insetsToDrawInFullscreen = previewPositionHelper.getInsetsToDrawInFullscreen(deviceProfile);
            float f4 = insetsToDrawInFullscreen.left * f;
            float f5 = insetsToDrawInFullscreen.right * f;
            float f6 = insetsToDrawInFullscreen.bottom;
            float f7 = 0.0f;
            if (deviceProfile.isTaskbarPresentInApps) {
                f6 = Math.max(0.0f, f6 - ((float) deviceProfile.taskbarSize));
            }
            this.mCurrentDrawnInsets.set(f4, insetsToDrawInFullscreen.top * f, f5, f6 * f);
            if (!deviceProfile.isMultiWindowMode) {
                f7 = this.mWindowCornerRadius;
            }
            this.mCurrentDrawnCornerRadius = (Utilities.mapRange(f, this.mCornerRadius, f7) / f2) / f3;
            if (i > 0) {
                float f8 = (float) i;
                this.mScale = f8 / ((f4 + f8) + f5);
            }
        }
    }

    public class TaskIdAttributeContainer {
        private final int mA11yNodeId;
        private final IconView mIconView;
        private int mStagePosition;
        private final Task mTask;
        private final TaskThumbnailView mThumbnailView;

        public TaskIdAttributeContainer(Task task, TaskThumbnailView taskThumbnailView, IconView iconView, int i) {
            this.mTask = task;
            this.mThumbnailView = taskThumbnailView;
            this.mIconView = iconView;
            this.mStagePosition = i;
            this.mA11yNodeId = i == 1 ? R.id.split_bottomRight_appInfo : R.id.split_topLeft_appInfo;
        }

        public TaskThumbnailView getThumbnailView() {
            return this.mThumbnailView;
        }

        public Task getTask() {
            return this.mTask;
        }

        public WorkspaceItemInfo getItemInfo() {
            return TaskView.this.getItemInfo(this.mTask);
        }

        public TaskView getTaskView() {
            return TaskView.this;
        }

        public IconView getIconView() {
            return this.mIconView;
        }

        public int getStagePosition() {
            return this.mStagePosition;
        }

        /* access modifiers changed from: package-private */
        public void setStagePosition(int i) {
            this.mStagePosition = i;
        }

        public int getA11yNodeId() {
            return this.mA11yNodeId;
        }
    }
}
