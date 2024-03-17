package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.LocusId;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Pair;
import android.util.Property;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.Toast;
import android.window.PictureInPictureSurfaceTransaction;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.slice.core.SliceHints;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.PagedView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.SpringProperty;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.cache.HandlerRunnable;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.popup.QuickstepSystemShortcut;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.DynamicResource;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.ResourceBasedOverride;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.TranslateEdgeEffect;
import com.android.launcher3.util.ViewPool;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.RecentsAnimationController;
import com.android.quickstep.RecentsAnimationTargets;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.RotationTouchHelper;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.TaskThumbnailCache;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.TaskViewUtils;
import com.android.quickstep.TopTaskTracker;
import com.android.quickstep.ViewUtils;
import com.android.quickstep.util.GroupTask;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.quickstep.util.SplitScreenBounds;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.util.TransformParams;
import com.android.quickstep.util.VibratorWrapper;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.plugins.ResourceProvider;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import com.android.wm.shell.pip.IPipAnimationListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public abstract class RecentsView<ACTIVITY_TYPE extends StatefulActivity<STATE_TYPE>, STATE_TYPE extends BaseState<STATE_TYPE>> extends PagedView implements Insettable, TaskThumbnailCache.HighResLoadingState.HighResLoadingStateChangedCallback, RecentsModel.TaskVisualsChangeListener, SplitScreenBounds.OnChangeListener {
    private static final float ADDITIONAL_DISMISS_TRANSLATION_INTERPOLATION_OFFSET = 0.05f;
    private static final int ADDITION_TASK_DURATION = 200;
    public static final FloatProperty<RecentsView> ADJACENT_PAGE_HORIZONTAL_OFFSET = new FloatProperty<RecentsView>("adjacentPageHorizontalOffset") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            if (recentsView.mAdjacentPageHorizontalOffset != f) {
                float unused = recentsView.mAdjacentPageHorizontalOffset = f;
                recentsView.updatePageOffsets();
            }
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mAdjacentPageHorizontalOffset);
        }
    };
    private static final float ANIMATION_DISMISS_PROGRESS_MIDPOINT = 0.5f;
    private static final FloatProperty<RecentsView> COLOR_TINT = new FloatProperty<RecentsView>("colorTint") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            recentsView.setColorTint(f);
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.getColorTint());
        }
    };
    public static final FloatProperty<RecentsView> CONTENT_ALPHA = new FloatProperty<RecentsView>("contentAlpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            recentsView.setContentAlpha(f);
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.getContentAlpha());
        }
    };
    private static final boolean DEBUG = false;
    private static final int DEFAULT_ACTIONS_VIEW_ALPHA_ANIMATION_DURATION = 300;
    private static final int DISMISS_TASK_DURATION = 300;
    private static final float END_DISMISS_TRANSLATION_INTERPOLATION_OFFSET = 0.75f;
    public static final FloatProperty<RecentsView> FULLSCREEN_PROGRESS = new FloatProperty<RecentsView>("fullscreenProgress") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            recentsView.setFullscreenProgress(f);
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mFullscreenProgress);
        }
    };
    private static final float INITIAL_DISMISS_TRANSLATION_INTERPOLATION_OFFSET = 0.55f;
    private static final int OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION = 270;
    public static final FloatProperty<RecentsView> RECENTS_GRID_PROGRESS = new FloatProperty<RecentsView>("recentsGrid") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            recentsView.setGridProgress(f);
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mGridProgress);
        }
    };
    public static final FloatProperty<RecentsView> RECENTS_SCALE_PROPERTY = new FloatProperty<RecentsView>("recentsScale") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, final float f) {
            recentsView.setScaleX(f);
            recentsView.setScaleY(f);
            recentsView.mLastComputedTaskStartPushOutDistance = null;
            recentsView.mLastComputedTaskEndPushOutDistance = null;
            recentsView.runActionOnRemoteHandles(new Consumer<RemoteTargetGluer.RemoteTargetHandle>() {
                public void accept(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
                    remoteTargetHandle.getTaskViewSimulator().recentsViewScale.value = f;
                }
            });
            recentsView.setTaskViewsResistanceTranslation(recentsView.mTaskViewsSecondaryTranslation);
            recentsView.updateTaskViewsSnapshotRadius();
            recentsView.updatePageOffsets();
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.getScaleX());
        }
    };
    private static final int REMOVE_TASK_WAIT_FOR_APP_STOP_MS = 300;
    public static final VibrationEffect SCROLL_VIBRATION_FALLBACK = VibratorWrapper.EFFECT_TEXTURE_TICK;
    public static final int SCROLL_VIBRATION_PRIMITIVE = (Utilities.ATLEAST_S ? 8 : -1);
    public static final float SCROLL_VIBRATION_PRIMITIVE_SCALE = 0.6f;
    private static final float SIGNIFICANT_MOVE_SCREEN_WIDTH_PERCENTAGE = 0.15f;
    private static final String TAG = "RecentsView";
    public static final FloatProperty<RecentsView> TASK_MODALNESS = new FloatProperty<RecentsView>("taskModalness") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            recentsView.setTaskModalness(f);
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mTaskModalness);
        }
    };
    public static final FloatProperty<RecentsView> TASK_PRIMARY_SPLIT_TRANSLATION = new FloatProperty<RecentsView>("taskPrimarySplitTranslation") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            recentsView.setTaskViewsPrimarySplitTranslation(f);
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mTaskViewsPrimarySplitTranslation);
        }
    };
    public static final FloatProperty<RecentsView> TASK_SECONDARY_SPLIT_TRANSLATION = new FloatProperty<RecentsView>("taskSecondarySplitTranslation") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            recentsView.setTaskViewsSecondarySplitTranslation(f);
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mTaskViewsSecondarySplitTranslation);
        }
    };
    public static final FloatProperty<RecentsView> TASK_SECONDARY_TRANSLATION = new FloatProperty<RecentsView>("taskSecondaryTranslation") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(RecentsView recentsView, float f) {
            recentsView.setTaskViewsResistanceTranslation(f);
        }

        public Float get(RecentsView recentsView) {
            return Float.valueOf(recentsView.mTaskViewsSecondaryTranslation);
        }
    };
    public static final float UPDATE_SYSUI_FLAGS_THRESHOLD = 0.85f;
    private final int[] INVALID_TASK_IDS = {-1, -1};
    /* access modifiers changed from: private */
    public OverviewActionsView mActionsView;
    private ObjectAnimator mActionsViewAlphaAnimator;
    private float mActionsViewAlphaAnimatorFinalValue;
    protected final ACTIVITY_TYPE mActivity;
    private ActivityManager mActivityManager;
    /* access modifiers changed from: private */
    public float mAdjacentPageHorizontalOffset = 0.0f;
    /* access modifiers changed from: private */
    public final ClearAllButton mClearAllButton;
    private final Rect mClearAllButtonDeadZoneRect = new Rect();
    private float mColorTint;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected float mContentAlpha = 1.0f;
    protected GestureState.GestureEndTarget mCurrentGestureEndTarget;
    protected boolean mDisallowScrollToClearAll;
    private int mDownX;
    private int mDownY;
    private final Drawable mEmptyIcon;
    private final CharSequence mEmptyMessage;
    private final int mEmptyMessagePadding;
    private final TextPaint mEmptyMessagePaint;
    private Layout mEmptyTextLayout;
    protected boolean mEnableDrawingLiveTile = false;
    private final float mFastFlingVelocity;
    private FloatingTaskView mFirstFloatingTaskView;
    protected int mFocusedTaskViewId = -1;
    protected boolean mFreezeViewVisibility;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected float mFullscreenProgress = 0.0f;
    private boolean mGestureActive;
    /* access modifiers changed from: private */
    public float mGridProgress = 0.0f;
    private final ViewPool<GroupedTaskView> mGroupedTaskViewPool;
    /* access modifiers changed from: private */
    public boolean mHandleTaskStackChanges;
    private final SparseBooleanArray mHasVisibleTaskData = new SparseBooleanArray();
    private final PinnedStackAnimationListener mIPipAnimationListener = new PinnedStackAnimationListener();
    private final InvariantDeviceProfile mIdp;
    private int mIgnoreResetTaskId = -1;
    protected final Rect mLastComputedGridSize = new Rect();
    protected final Rect mLastComputedGridTaskSize = new Rect();
    protected Float mLastComputedTaskEndPushOutDistance = null;
    protected final Rect mLastComputedTaskSize = new Rect();
    protected Float mLastComputedTaskStartPushOutDistance = null;
    private final Point mLastMeasureSize = new Point();
    private LayoutTransition mLayoutTransition;
    protected boolean mLoadPlanEverApplied;
    /* access modifiers changed from: private */
    public final RecentsModel mModel;
    private TaskView mMovingTaskView;
    private BaseActivity.MultiWindowModeChangedListener mMultiWindowModeChangedListener = new BaseActivity.MultiWindowModeChangedListener() {
        public void onMultiWindowModeChanged(boolean z) {
            RecentsView.this.mOrientationState.setMultiWindowMode(z);
            RecentsView recentsView = RecentsView.this;
            recentsView.setLayoutRotation(recentsView.mOrientationState.getTouchRotation(), RecentsView.this.mOrientationState.getDisplayRotation());
            RecentsView.this.updateChildTaskOrientations();
            if (!z && RecentsView.this.mOverviewStateEnabled) {
                RecentsView.this.reloadIfNeeded();
            }
        }
    };
    private OnEmptyMessageUpdatedListener mOnEmptyMessageUpdatedListener;
    protected final RecentsOrientedState mOrientationState;
    private int mOverScrollShift = 0;
    private boolean mOverlayEnabled;
    private boolean mOverviewFullscreenEnabled;
    private boolean mOverviewGridEnabled;
    private boolean mOverviewSelectEnabled;
    /* access modifiers changed from: private */
    public boolean mOverviewStateEnabled;
    /* access modifiers changed from: private */
    public PendingAnimation mPendingAnimation;
    /* access modifiers changed from: private */
    public int mPipCornerRadius;
    /* access modifiers changed from: private */
    public int mPipShadowRadius;
    protected RecentsAnimationController mRecentsAnimationController;
    protected RemoteTargetGluer.RemoteTargetHandle[] mRemoteTargetHandles;
    private boolean mRunningTaskShowScreenshot = false;
    protected boolean mRunningTaskTileHidden;
    protected int mRunningTaskViewId = -1;
    private final int mScrollHapticMinGapMillis;
    private long mScrollLastHapticTimestamp;
    private final List<ViewTreeObserver.OnScrollChangedListener> mScrollListeners = new ArrayList();
    private FloatingTaskView mSecondFloatingTaskView;
    private View mSecondSplitHiddenView;
    private boolean mShowAsGridLastOnLayout = false;
    private boolean mShowEmptyMessage;
    private RunnableList mSideTaskLaunchCallback;
    protected final BaseActivityInterface<STATE_TYPE, ACTIVITY_TYPE> mSizeStrategy;
    private SplitConfigurationOptions.StagedSplitBounds mSplitBoundsConfig;
    private TaskView mSplitHiddenTaskView;
    private int mSplitHiddenTaskViewIndex = -1;
    private final int mSplitPlaceholderInset;
    /* access modifiers changed from: private */
    public final int mSplitPlaceholderSize;
    private QuickstepSystemShortcut.SplitSelectSource mSplitSelectSource;
    private SplitSelectStateController mSplitSelectStateController;
    private final Toast mSplitToast = Toast.makeText(getContext(), R.string.toast_split_select_app, 0);
    private final Toast mSplitUnsupportedToast = Toast.makeText(getContext(), R.string.toast_split_app_unsupported, 0);
    private final float mSquaredTouchSlop;
    private boolean mSwipeDownShouldLaunchApp;
    protected SurfaceTransactionApplier mSyncTransactionApplier;
    private float mTaskGridVerticalDiff;
    protected int mTaskHeight;
    private boolean mTaskIconScaledDown = false;
    private TaskLaunchListener mTaskLaunchListener;
    private int mTaskListChangeId = -1;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected float mTaskModalness = 0.0f;
    private final TaskOverlayFactory mTaskOverlayFactory;
    private final TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() {
        public void onActivityPinned(String str, int i, int i2, int i3) {
            TaskView taskViewByTaskId;
            if (RecentsView.this.mHandleTaskStackChanges && TaskUtils.checkCurrentOrManagedUserId(i, RecentsView.this.getContext()) && (taskViewByTaskId = RecentsView.this.getTaskViewByTaskId(i2)) != null) {
                RecentsView.this.removeView(taskViewByTaskId);
            }
        }

        public void onActivityUnpinned() {
            if (RecentsView.this.mHandleTaskStackChanges) {
                RecentsView.this.reloadIfNeeded();
                RecentsView.this.enableLayoutTransitions();
            }
        }

        public void onTaskRemoved(int i) {
            TaskView taskViewByTaskId;
            if (RecentsView.this.mHandleTaskStackChanges && (taskViewByTaskId = RecentsView.this.getTaskViewByTaskId(i)) != null) {
                Task.TaskKey taskKey = taskViewByTaskId.getTask().key;
                Executors.UI_HELPER_EXECUTOR.execute(new HandlerRunnable(Executors.UI_HELPER_EXECUTOR.getHandler(), new Supplier() {
                    public final Object get() {
                        return RecentsView.AnonymousClass11.lambda$onTaskRemoved$0(Task.TaskKey.this);
                    }
                }, Executors.MAIN_EXECUTOR, new Consumer(i, taskKey) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ Task.TaskKey f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void accept(Object obj) {
                        RecentsView.AnonymousClass11.this.lambda$onTaskRemoved$2$RecentsView$11(this.f$1, this.f$2, (Boolean) obj);
                    }
                }));
            }
        }

        static /* synthetic */ Boolean lambda$onTaskRemoved$0(Task.TaskKey taskKey) {
            return Boolean.valueOf(PackageManagerWrapper.getInstance().getActivityInfo(taskKey.getComponent(), taskKey.userId) == null);
        }

        public /* synthetic */ void lambda$onTaskRemoved$2$RecentsView$11(int i, Task.TaskKey taskKey, Boolean bool) {
            if (bool.booleanValue()) {
                RecentsView.this.dismissTask(i);
            } else {
                RecentsView.this.mModel.isTaskRemoved(taskKey.id, new Consumer(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        RecentsView.AnonymousClass11.this.lambda$onTaskRemoved$1$RecentsView$11(this.f$1, (Boolean) obj);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onTaskRemoved$1$RecentsView$11(int i, Boolean bool) {
            if (bool.booleanValue()) {
                RecentsView.this.dismissTask(i);
            }
        }
    };
    private final Rect mTaskViewDeadZoneRect = new Rect();
    private int mTaskViewIdCount;
    private final ViewPool<TaskView> mTaskViewPool;
    protected float mTaskViewsPrimarySplitTranslation = 0.0f;
    protected float mTaskViewsSecondarySplitTranslation = 0.0f;
    protected float mTaskViewsSecondaryTranslation = 0.0f;
    protected int mTaskWidth;
    private final float[] mTempFloat = new float[1];
    private final Matrix mTempMatrix = new Matrix();
    private final PointF mTempPointF = new PointF();
    protected final Rect mTempRect = new Rect();
    protected final RectF mTempRectF = new RectF();
    private ObjectAnimator mTintingAnimator;
    private final int mTintingColor;
    private Task[] mTmpRunningTasks;
    private float mTopBottomRowHeightDiff;
    /* access modifiers changed from: private */
    public final IntSet mTopRowIdSet = new IntSet();
    private boolean mTouchDownToStartHome;
    private View zxwClearAll = null;
    private ViewGroup zxwClearPane = null;

    public interface OnEmptyMessageUpdatedListener {
        void onEmptyMessageUpdated(boolean z);
    }

    public interface TaskLaunchListener {
        void onTaskLaunched();
    }

    /* access modifiers changed from: protected */
    public boolean canLaunchFullscreenTask() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void drawEdgeEffect(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return "";
    }

    /* access modifiers changed from: protected */
    public DepthController getDepthController() {
        return null;
    }

    /* access modifiers changed from: protected */
    public TaskView getHomeTaskView() {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isPageOrderFlipped() {
        return true;
    }

    public void setModalStateEnabled(boolean z) {
    }

    public abstract void startHome();

    static /* synthetic */ int access$2612(RecentsView recentsView, int i) {
        int i2 = recentsView.mCurrentPageScrollDiff + i;
        recentsView.mCurrentPageScrollDiff = i2;
        return i2;
    }

    static /* synthetic */ int access$2912(RecentsView recentsView, int i) {
        int i2 = recentsView.mCurrentPageScrollDiff + i;
        recentsView.mCurrentPageScrollDiff = i2;
        return i2;
    }

    static /* synthetic */ int access$4412(RecentsView recentsView, int i) {
        int i2 = recentsView.mCurrentPageScrollDiff + i;
        recentsView.mCurrentPageScrollDiff = i2;
        return i2;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public RecentsView(Context context, AttributeSet attributeSet, int i, BaseActivityInterface baseActivityInterface) {
        super(context, attributeSet, i);
        Context context2 = context;
        BaseActivityInterface baseActivityInterface2 = baseActivityInterface;
        this.mActivityManager = (ActivityManager) context.getSystemService(SliceHints.HINT_ACTIVITY);
        setEnableFreeScroll(true);
        this.mSizeStrategy = baseActivityInterface2;
        ACTIVITY_TYPE activity_type = (StatefulActivity) BaseActivity.fromContext(context);
        this.mActivity = activity_type;
        RecentsOrientedState recentsOrientedState = new RecentsOrientedState(context, baseActivityInterface2, new IntConsumer() {
            public final void accept(int i) {
                RecentsView.this.animateRecentsRotationInPlace(i);
            }
        });
        this.mOrientationState = recentsOrientedState;
        recentsOrientedState.setRecentsRotation(activity_type.getDisplay().getRotation());
        this.mScrollHapticMinGapMillis = getResources().getInteger(R.integer.recentsScrollHapticMinGapMillis);
        this.mFastFlingVelocity = (float) getResources().getDimensionPixelSize(R.dimen.recents_fast_fling_velocity);
        this.mModel = RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        this.mIdp = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        ClearAllButton clearAllButton = (ClearAllButton) LayoutInflater.from(context).inflate(R.layout.overview_clear_all_button, this, false);
        this.mClearAllButton = clearAllButton;
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RecentsView.this.dismissAllTasks(view);
            }
        });
        clearAllButton.setVisibility(4);
        Context context3 = context;
        this.mTaskViewPool = new ViewPool(context3, this, R.layout.task, 20, 10);
        this.mGroupedTaskViewPool = new ViewPool(context3, this, R.layout.task_grouped, 20, 10);
        this.mIsRtl = this.mOrientationHandler.getRecentsRtlSetting(getResources());
        setLayoutDirection(this.mIsRtl ? 1 : 0);
        this.mSplitPlaceholderSize = getResources().getDimensionPixelSize(R.dimen.split_placeholder_size);
        this.mSplitPlaceholderInset = getResources().getDimensionPixelSize(R.dimen.split_placeholder_inset);
        this.mSquaredTouchSlop = Utilities.squaredTouchSlop(context);
        Drawable drawable = context.getDrawable(R.drawable.ic_empty_recents);
        this.mEmptyIcon = drawable;
        drawable.setCallback(this);
        this.mEmptyMessage = context.getText(R.string.recents_empty_message);
        TextPaint textPaint = new TextPaint();
        this.mEmptyMessagePaint = textPaint;
        textPaint.setColor(Themes.getAttrColor(context, 16842806));
        textPaint.setTextSize(getResources().getDimension(R.dimen.recents_empty_message_text_size));
        textPaint.setTypeface(Typeface.create(Themes.getDefaultBodyFont(context), 0));
        textPaint.setAntiAlias(true);
        this.mEmptyMessagePadding = getResources().getDimensionPixelSize(R.dimen.recents_empty_message_text_padding);
        setWillNotDraw(false);
        updateEmptyMessage();
        this.mOrientationHandler = recentsOrientedState.getOrientationHandler();
        this.mTaskOverlayFactory = (TaskOverlayFactory) ResourceBasedOverride.Overrides.getObject(TaskOverlayFactory.class, context.getApplicationContext(), R.string.task_overlay_factory_class);
        activity_type.getViewCache().setCacheSize(R.layout.digital_wellbeing_toast, 5);
        this.mTintingColor = getForegroundScrimDimColor(context);
    }

    public OverScroller getScroller() {
        return this.mScroller;
    }

    public boolean isRtl() {
        return this.mIsRtl;
    }

    /* access modifiers changed from: protected */
    public void initEdgeEffect() {
        this.mEdgeGlowLeft = new TranslateEdgeEffect(getContext());
        this.mEdgeGlowRight = new TranslateEdgeEffect(getContext());
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (!this.mAllowOverScroll || (this.mEdgeGlowRight.isFinished() && this.mEdgeGlowLeft.isFinished())) {
            if (this.mOverScrollShift != 0) {
                this.mOverScrollShift = 0;
                dispatchScrollChanged();
            }
            super.dispatchDraw(canvas);
        } else {
            int save = canvas.save();
            int dampedScroll = OverScroll.dampedScroll(getUndampedOverScrollShift(), this.mOrientationHandler.getPrimaryValue(getWidth(), getHeight()));
            this.mOrientationHandler.setPrimary(canvas, PagedOrientationHandler.CANVAS_TRANSLATE, (float) dampedScroll);
            if (this.mOverScrollShift != dampedScroll) {
                this.mOverScrollShift = dampedScroll;
                dispatchScrollChanged();
            }
            super.dispatchDraw(canvas);
            canvas.restoreToCount(save);
        }
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && this.mEnableDrawingLiveTile && this.mRemoteTargetHandles != null) {
            redrawLiveTile();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x003f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private float getUndampedOverScrollShift() {
        /*
            r5 = this;
            int r0 = r5.getWidth()
            int r1 = r5.getHeight()
            com.android.launcher3.touch.PagedOrientationHandler r2 = r5.mOrientationHandler
            int r2 = r2.getPrimaryValue((int) r0, (int) r1)
            com.android.launcher3.touch.PagedOrientationHandler r3 = r5.mOrientationHandler
            int r0 = r3.getSecondaryValue((int) r0, (int) r1)
            com.android.launcher3.util.EdgeEffectCompat r1 = r5.mEdgeGlowLeft
            boolean r1 = r1.isFinished()
            r3 = 0
            if (r1 != 0) goto L_0x0036
            com.android.launcher3.util.EdgeEffectCompat r1 = r5.mEdgeGlowLeft
            r1.setSize(r0, r2)
            com.android.launcher3.util.EdgeEffectCompat r1 = r5.mEdgeGlowLeft
            com.android.launcher3.util.TranslateEdgeEffect r1 = (com.android.launcher3.util.TranslateEdgeEffect) r1
            float[] r4 = r5.mTempFloat
            boolean r1 = r1.getTranslationShift(r4)
            if (r1 == 0) goto L_0x0036
            float[] r1 = r5.mTempFloat
            r1 = r1[r3]
            r5.postInvalidateOnAnimation()
            goto L_0x0037
        L_0x0036:
            r1 = 0
        L_0x0037:
            com.android.launcher3.util.EdgeEffectCompat r4 = r5.mEdgeGlowRight
            boolean r4 = r4.isFinished()
            if (r4 != 0) goto L_0x0058
            com.android.launcher3.util.EdgeEffectCompat r4 = r5.mEdgeGlowRight
            r4.setSize(r0, r2)
            com.android.launcher3.util.EdgeEffectCompat r0 = r5.mEdgeGlowRight
            com.android.launcher3.util.TranslateEdgeEffect r0 = (com.android.launcher3.util.TranslateEdgeEffect) r0
            float[] r4 = r5.mTempFloat
            boolean r0 = r0.getTranslationShift(r4)
            if (r0 == 0) goto L_0x0058
            float[] r0 = r5.mTempFloat
            r0 = r0[r3]
            float r1 = r1 - r0
            r5.postInvalidateOnAnimation()
        L_0x0058:
            float r0 = (float) r2
            float r1 = r1 * r0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.RecentsView.getUndampedOverScrollShift():float");
    }

    public int getOverScrollShift() {
        return this.mOverScrollShift;
    }

    public Task onTaskThumbnailChanged(int i, ThumbnailData thumbnailData) {
        TaskView taskViewByTaskId;
        if (!this.mHandleTaskStackChanges || (taskViewByTaskId = getTaskViewByTaskId(i)) == null) {
            return null;
        }
        for (TaskView.TaskIdAttributeContainer taskIdAttributeContainer : taskViewByTaskId.getTaskIdAttributeContainers()) {
            if (taskIdAttributeContainer != null && i == taskIdAttributeContainer.getTask().key.id) {
                taskIdAttributeContainer.getThumbnailView().setThumbnail(taskIdAttributeContainer.getTask(), thumbnailData);
            }
        }
        return null;
    }

    public void onTaskIconChanged(String str, UserHandle userHandle) {
        for (int i = 0; i < getTaskViewCount(); i++) {
            TaskView requireTaskViewAt = requireTaskViewAt(i);
            Task task = requireTaskViewAt.getTask();
            if (task != null && task.key != null && str.equals(task.key.getPackageName()) && task.key.userId == userHandle.getIdentifier()) {
                task.icon = null;
                if (requireTaskViewAt.getIconView().getDrawable() != null) {
                    requireTaskViewAt.onTaskListVisibilityChanged(true);
                }
            }
        }
    }

    public TaskView updateThumbnail(int i, ThumbnailData thumbnailData, boolean z) {
        TaskView taskViewByTaskId = getTaskViewByTaskId(i);
        if (taskViewByTaskId != null) {
            taskViewByTaskId.getThumbnail().setThumbnail(taskViewByTaskId.getTask(), thumbnailData, z);
        }
        return taskViewByTaskId;
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        updateTaskStackListenerState();
    }

    public void init(OverviewActionsView overviewActionsView, SplitSelectStateController splitSelectStateController, View view) {
        this.mActionsView = overviewActionsView;
        overviewActionsView.updateHiddenFlags(2, getTaskViewCount() == 0);
        this.mSplitSelectStateController = splitSelectStateController;
        this.zxwClearPane = (ViewGroup) view;
    }

    public SplitSelectStateController getSplitPlaceholder() {
        return this.mSplitSelectStateController;
    }

    public boolean isSplitSelectionActive() {
        return this.mSplitSelectStateController.isSplitSelectActive();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateTaskStackListenerState();
        this.mModel.getThumbnailCache().getHighResLoadingState().addCallback(this);
        this.mActivity.addMultiWindowModeChangedListener(this.mMultiWindowModeChangedListener);
        TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mTaskStackListener);
        this.mSyncTransactionApplier = new SurfaceTransactionApplier(this);
        runActionOnRemoteHandles(new Consumer() {
            public final void accept(Object obj) {
                RecentsView.this.lambda$onAttachedToWindow$0$RecentsView((RemoteTargetGluer.RemoteTargetHandle) obj);
            }
        });
        RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).addThumbnailChangeListener(this);
        this.mIPipAnimationListener.setActivityAndRecentsView(this.mActivity, this);
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).setPinnedStackAnimationListener(this.mIPipAnimationListener);
        this.mOrientationState.initListeners();
        SplitScreenBounds.INSTANCE.addOnChangeListener(this);
        this.mTaskOverlayFactory.initListeners();
    }

    public /* synthetic */ void lambda$onAttachedToWindow$0$RecentsView(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTransformParams().setSyncTransactionApplier(this.mSyncTransactionApplier);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        updateTaskStackListenerState();
        this.mModel.getThumbnailCache().getHighResLoadingState().removeCallback(this);
        this.mActivity.removeMultiWindowModeChangedListener(this.mMultiWindowModeChangedListener);
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
        this.mSyncTransactionApplier = null;
        runActionOnRemoteHandles($$Lambda$RecentsView$A0C7VGCnyP4UflVFvg_BtuCFZcw.INSTANCE);
        executeSideTaskLaunchCallback();
        RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).removeThumbnailChangeListener(this);
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).setPinnedStackAnimationListener((IPipAnimationListener) null);
        SplitScreenBounds.INSTANCE.removeOnChangeListener(this);
        this.mIPipAnimationListener.setActivityAndRecentsView(null, (RecentsView) null);
        this.mOrientationState.destroyListeners();
        this.mTaskOverlayFactory.removeListeners();
    }

    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if ((view instanceof TaskView) && view != this.mSplitHiddenTaskView && view != this.mMovingTaskView) {
            TaskView taskView = (TaskView) view;
            boolean z = false;
            for (int delete : taskView.getTaskIds()) {
                this.mHasVisibleTaskData.delete(delete);
            }
            if (view instanceof GroupedTaskView) {
                this.mGroupedTaskViewPool.recycle((GroupedTaskView) taskView);
            } else {
                this.mTaskViewPool.recycle(taskView);
            }
            taskView.setTaskViewId(-1);
            OverviewActionsView overviewActionsView = this.mActionsView;
            if (getTaskViewCount() == 0) {
                z = true;
            }
            overviewActionsView.updateHiddenFlags(2, z);
        }
    }

    public void onViewAdded(View view) {
        super.onViewAdded(view);
        view.setAlpha(this.mContentAlpha);
        view.setLayoutDirection(this.mIsRtl ^ true ? 1 : 0);
        this.mActionsView.updateHiddenFlags(2, false);
        updateEmptyMessage();
    }

    public void draw(Canvas canvas) {
        maybeDrawEmptyMessage(canvas);
        super.draw(canvas);
    }

    public void addSideTaskLaunchCallback(RunnableList runnableList) {
        if (this.mSideTaskLaunchCallback == null) {
            this.mSideTaskLaunchCallback = new RunnableList();
        }
        RunnableList runnableList2 = this.mSideTaskLaunchCallback;
        Objects.requireNonNull(runnableList);
        runnableList2.add(new Runnable() {
            public final void run() {
                RunnableList.this.executeAllAndDestroy();
            }
        });
    }

    public void setTaskLaunchListener(TaskLaunchListener taskLaunchListener) {
        this.mTaskLaunchListener = taskLaunchListener;
    }

    public void onTaskLaunchedInLiveTileMode() {
        TaskLaunchListener taskLaunchListener = this.mTaskLaunchListener;
        if (taskLaunchListener != null) {
            taskLaunchListener.onTaskLaunched();
            this.mTaskLaunchListener = null;
        }
    }

    private void executeSideTaskLaunchCallback() {
        RunnableList runnableList = this.mSideTaskLaunchCallback;
        if (runnableList != null) {
            runnableList.executeAllAndDestroy();
            this.mSideTaskLaunchCallback = null;
        }
    }

    public void launchSideTaskInLiveTileModeForRestartedApp(int i) {
        RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr;
        RemoteAnimationTargets targetSet;
        int taskViewIdFromTaskId = getTaskViewIdFromTaskId(i);
        int i2 = this.mRunningTaskViewId;
        if (i2 != -1 && i2 == taskViewIdFromTaskId && (remoteTargetHandleArr = this.mRemoteTargetHandles) != null && (targetSet = remoteTargetHandleArr[0].getTransformParams().getTargetSet()) != null && targetSet.findTask(i) != null) {
            launchSideTaskInLiveTileMode(i, targetSet.apps, targetSet.wallpapers, targetSet.nonApps);
        }
    }

    public void launchSideTaskInLiveTileMode(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3) {
        AnimatorSet animatorSet = new AnimatorSet();
        TaskView taskViewByTaskId = getTaskViewByTaskId(i);
        if (taskViewByTaskId == null || !isTaskViewVisible(taskViewByTaskId)) {
            SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(this.mActivity.getDragLayer());
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.setDuration(336);
            ofFloat.setInterpolator(Interpolators.ACCEL_DEACCEL);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(remoteAnimationTargetCompatArr, surfaceTransactionApplier) {
                public final /* synthetic */ RemoteAnimationTargetCompat[] f$1;
                public final /* synthetic */ SurfaceTransactionApplier f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    RecentsView.this.lambda$launchSideTaskInLiveTileMode$2$RecentsView(this.f$1, this.f$2, valueAnimator);
                }
            });
            animatorSet.play(ofFloat);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    RecentsView.this.finishRecentsAnimation(false, (Runnable) null);
                }
            });
        } else {
            TaskViewUtils.composeRecentsLaunchAnimator(animatorSet, taskViewByTaskId, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, true, this.mActivity.getStateManager(), this, getDepthController());
        }
        animatorSet.start();
    }

    public /* synthetic */ void lambda$launchSideTaskInLiveTileMode$2$RecentsView(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, SurfaceTransactionApplier surfaceTransactionApplier, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompatArr[remoteAnimationTargetCompatArr.length - 1].leash);
        Matrix matrix = new Matrix();
        matrix.postScale(animatedFraction, animatedFraction);
        float f = 1.0f - animatedFraction;
        matrix.postTranslate((((float) this.mActivity.getDeviceProfile().widthPx) * f) / 2.0f, (((float) this.mActivity.getDeviceProfile().heightPx) * f) / 2.0f);
        builder.withAlpha(animatedFraction).withMatrix(matrix);
        surfaceTransactionApplier.scheduleApply(builder.build());
    }

    public boolean isTaskViewVisible(TaskView taskView) {
        if (!showAsGrid()) {
            return Math.abs(indexOfChild(taskView) - getNextPage()) <= 1;
        }
        int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
        return isTaskViewWithinBounds(taskView, primaryScroll, this.mOrientationHandler.getMeasuredSize(this) + primaryScroll);
    }

    public boolean isTaskViewFullyVisible(TaskView taskView) {
        if (!showAsGrid()) {
            return indexOfChild(taskView) == getNextPage();
        }
        int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
        return isTaskViewFullyWithinBounds(taskView, primaryScroll, this.mOrientationHandler.getMeasuredSize(this) + primaryScroll);
    }

    private TaskView getLastGridTaskView() {
        return getLastGridTaskView(getTopRowIdArray(), getBottomRowIdArray());
    }

    /* access modifiers changed from: private */
    public TaskView getLastGridTaskView(IntArray intArray, IntArray intArray2) {
        int i;
        if (intArray.isEmpty() && intArray2.isEmpty()) {
            return null;
        }
        if (intArray.size() >= intArray2.size()) {
            i = intArray.get(intArray.size() - 1);
        } else {
            i = intArray2.get(intArray2.size() - 1);
        }
        return getTaskViewFromTaskViewId(i);
    }

    private int getSnapToLastTaskScrollDiff() {
        return this.mOrientationHandler.getPrimaryScroll(this) - getLastTaskScroll(getScrollForPage(indexOfChild(this.mClearAllButton)), this.mOrientationHandler.getPrimarySize((View) this.mClearAllButton));
    }

    private int getLastTaskScroll(int i, int i2) {
        int clearAllExtraPageSpacing = i2 + getClearAllExtraPageSpacing();
        if (!this.mIsRtl) {
            clearAllExtraPageSpacing = -clearAllExtraPageSpacing;
        }
        return i + clearAllExtraPageSpacing;
    }

    private int getSnapToFocusedTaskScrollDiff(boolean z) {
        int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
        int scrollForPage = getScrollForPage(indexOfChild(getFocusedTaskView()));
        if (!z) {
            int primarySize = (this.mLastComputedTaskSize.right - this.mLastComputedGridSize.right) - this.mOrientationHandler.getPrimarySize((View) this.mClearAllButton);
            if (!this.mIsRtl) {
                primarySize = -primarySize;
            }
            scrollForPage += primarySize;
        }
        return primaryScroll - scrollForPage;
    }

    private boolean isTaskViewWithinBounds(TaskView taskView, int i, int i2) {
        int childStart = this.mOrientationHandler.getChildStart(taskView) + ((int) taskView.getOffsetAdjustment(showAsFullscreen(), showAsGrid()));
        int measuredSize = ((int) (((float) this.mOrientationHandler.getMeasuredSize(taskView)) * taskView.getSizeAdjustment(showAsFullscreen()))) + childStart;
        return (childStart >= i && childStart <= i2) || (measuredSize >= i && measuredSize <= i2);
    }

    private boolean isTaskViewFullyWithinBounds(TaskView taskView, int i, int i2) {
        int childStart = this.mOrientationHandler.getChildStart(taskView) + ((int) taskView.getOffsetAdjustment(showAsFullscreen(), showAsGrid()));
        return childStart >= i && ((int) (((float) this.mOrientationHandler.getMeasuredSize(taskView)) * taskView.getSizeAdjustment(showAsFullscreen()))) + childStart <= i2;
    }

    public boolean isTaskInExpectedScrollPosition(int i) {
        return getScrollForPage(i) == getPagedOrientationHandler().getPrimaryScroll(this);
    }

    private boolean isFocusedTaskInExpectedScrollPosition() {
        TaskView focusedTaskView = getFocusedTaskView();
        return focusedTaskView != null && isTaskInExpectedScrollPosition(indexOfChild(focusedTaskView));
    }

    public TaskView getTaskViewByTaskId(int i) {
        if (i == -1) {
            return null;
        }
        for (int i2 = 0; i2 < getTaskViewCount(); i2++) {
            TaskView requireTaskViewAt = requireTaskViewAt(i2);
            int[] taskIds = requireTaskViewAt.getTaskIds();
            if (taskIds[0] == i || taskIds[1] == i) {
                return requireTaskViewAt;
            }
        }
        return null;
    }

    public void setOverviewStateEnabled(boolean z) {
        int i = 0;
        if (z && this.zxwClearAll == null) {
            View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.zxw_clear_all_panel, this.zxwClearPane, false);
            this.zxwClearAll = inflate;
            this.zxwClearPane.addView(inflate);
        }
        ViewGroup viewGroup = this.zxwClearPane;
        if (viewGroup != null) {
            viewGroup.setVisibility(z ? 0 : 8);
        }
        View view = this.zxwClearAll;
        if (view != null) {
            if (!z) {
                i = 8;
            }
            view.setVisibility(i);
            this.zxwClearAll.setOnClickListener(z ? new View.OnClickListener() {
                public final void onClick(View view) {
                    RecentsView.this.lambda$setOverviewStateEnabled$3$RecentsView(view);
                }
            } : null);
        }
        this.mOverviewStateEnabled = z;
        updateTaskStackListenerState();
        this.mOrientationState.setRotationWatcherEnabled(z);
        if (!z) {
            this.mTmpRunningTasks = null;
            this.mSplitBoundsConfig = null;
        }
        updateLocusId();
    }

    public /* synthetic */ void lambda$setOverviewStateEnabled$3$RecentsView(View view) {
        this.mClearAllButton.callOnClick();
    }

    public boolean isClearAllHidden() {
        return this.mClearAllButton.getAlpha() != 1.0f;
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
        super.onPageBeginTransition();
        if (!this.mActivity.getDeviceProfile().isTablet) {
            this.mActionsView.updateDisabledFlags(1, true);
        }
    }

    /* access modifiers changed from: protected */
    public void onPageEndTransition() {
        super.onPageEndTransition();
        if (isClearAllHidden() && !this.mActivity.getDeviceProfile().isTablet) {
            this.mActionsView.updateDisabledFlags(1, false);
        }
        if (getNextPage() > 0) {
            setSwipeDownShouldLaunchApp(true);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isSignificantMove(float f, int i) {
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        if (!deviceProfile.isTablet) {
            return super.isSignificantMove(f, i);
        }
        return f > ((float) deviceProfile.availableWidthPx) * SIGNIFICANT_MOVE_SCREEN_WIDTH_PERCENTAGE;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        boolean z = false;
        if (showAsGrid()) {
            int taskViewCount = getTaskViewCount();
            for (int i = 0; i < taskViewCount; i++) {
                TaskView requireTaskViewAt = requireTaskViewAt(i);
                if (isTaskViewVisible(requireTaskViewAt) && requireTaskViewAt.offerTouchToChildren(motionEvent)) {
                    return true;
                }
            }
        } else {
            TaskView currentPageTaskView = getCurrentPageTaskView();
            if (currentPageTaskView != null && currentPageTaskView.offerTouchToChildren(motionEvent)) {
                return true;
            }
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            if (!isHandlingTouch() && !isModal()) {
                if (this.mShowEmptyMessage) {
                    this.mTouchDownToStartHome = true;
                } else {
                    updateDeadZoneRects();
                    boolean z2 = this.mClearAllButton.getAlpha() == 1.0f && this.mClearAllButtonDeadZoneRect.contains(x, y);
                    if ((motionEvent.getEdgeFlags() & 256) != 0) {
                        z = true;
                    }
                    if (!z2 && !z && !this.mTaskViewDeadZoneRect.contains(getScrollX() + x, y)) {
                        this.mTouchDownToStartHome = true;
                    }
                }
            }
            this.mDownX = x;
            this.mDownY = y;
        } else if (action == 1) {
            if (this.mTouchDownToStartHome) {
                startHome();
            }
            this.mTouchDownToStartHome = false;
        } else if (action != 2) {
            if (action == 3) {
                this.mTouchDownToStartHome = false;
            }
        } else if (this.mTouchDownToStartHome && (isHandlingTouch() || Utilities.squaredHypot((float) (this.mDownX - x), (float) (this.mDownY - y)) > this.mSquaredTouchSlop)) {
            this.mTouchDownToStartHome = false;
        }
        return isHandlingTouch();
    }

    /* access modifiers changed from: protected */
    public void onNotSnappingToPageInFreeScroll() {
        int i;
        int finalX = this.mScroller.getFinalX();
        if (finalX > this.mMinScroll && finalX < this.mMaxScroll) {
            boolean z = false;
            int scrollForPage = getScrollForPage(!this.mIsRtl ? 0 : getPageCount() - 1);
            int scrollForPage2 = getScrollForPage(!this.mIsRtl ? getPageCount() - 1 : 0);
            if (finalX < (scrollForPage + this.mMinScroll) / 2) {
                i = this.mMinScroll;
            } else if (finalX > (scrollForPage2 + this.mMaxScroll) / 2) {
                i = this.mMaxScroll;
            } else {
                i = getScrollForPage(this.mNextPage);
            }
            if (showAsGrid()) {
                if (!isSplitSelectionActive()) {
                    TaskView taskViewAt = getTaskViewAt(this.mNextPage);
                    boolean z2 = taskViewAt != null && taskViewAt.isFocusedTask() && isTaskViewFullyVisible(taskViewAt);
                    if (this.mNextPage == indexOfChild(this.mClearAllButton)) {
                        z = true;
                    }
                    if (!z2 && !z) {
                        return;
                    }
                } else {
                    return;
                }
            }
            this.mScroller.setFinalX(i);
            int duration = 270 - this.mScroller.getDuration();
            if (duration > 0) {
                this.mScroller.extendDuration(duration);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onEdgeAbsorbingScroll() {
        vibrateForScroll();
    }

    /* access modifiers changed from: protected */
    public void onScrollOverPageChanged() {
        vibrateForScroll();
    }

    private void vibrateForScroll() {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - this.mScrollLastHapticTimestamp > ((long) this.mScrollHapticMinGapMillis)) {
            this.mScrollLastHapticTimestamp = uptimeMillis;
            VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).vibrate(SCROLL_VIBRATION_PRIMITIVE, 0.6f, SCROLL_VIBRATION_FALLBACK);
        }
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent, float f) {
        if (!isModal()) {
            super.determineScrollingStart(motionEvent, f);
        }
    }

    public void moveFocusedTaskToFront() {
        TaskView focusedTaskView;
        if (this.mActivity.getDeviceProfile().isTablet && (focusedTaskView = getFocusedTaskView()) != null && indexOfChild(focusedTaskView) == this.mCurrentPage && this.mCurrentPage != 0) {
            this.mCurrentPageScrollDiff = this.mOrientationHandler.getPrimaryScroll(this) - getScrollForPage(this.mCurrentPage);
            this.mMovingTaskView = focusedTaskView;
            removeView(focusedTaskView);
            this.mMovingTaskView = null;
            focusedTaskView.resetPersistentViewTransforms();
            addView(focusedTaskView, 0);
            setCurrentPage(0);
            updateGridProperties();
        }
    }

    /* access modifiers changed from: protected */
    public void applyLoadPlan(ArrayList<GroupTask> arrayList) {
        int i;
        TaskView taskViewByTaskId;
        ArrayList<GroupTask> arrayList2 = arrayList;
        PendingAnimation pendingAnimation = this.mPendingAnimation;
        if (pendingAnimation != null) {
            pendingAnimation.addEndListener(new Consumer(arrayList2) {
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    RecentsView.this.lambda$applyLoadPlan$4$RecentsView(this.f$1, (Boolean) obj);
                }
            });
            return;
        }
        boolean z = true;
        this.mLoadPlanEverApplied = true;
        if (arrayList2 == null || arrayList.isEmpty()) {
            removeTasksViewsAndClearAllButton();
            onTaskStackUpdated();
            resetTouchState();
            return;
        }
        TaskView taskViewAt = getTaskViewAt(this.mCurrentPage);
        int i2 = taskViewAt != null ? taskViewAt.getTask().key.id : -1;
        unloadVisibleTaskData(3);
        int i3 = this.mIgnoreResetTaskId;
        TaskView taskView = null;
        TaskView taskViewByTaskId2 = i3 == -1 ? null : getTaskViewByTaskId(i3);
        int i4 = getTaskIdsForTaskViewId(this.mRunningTaskViewId)[0];
        int i5 = getTaskIdsForTaskViewId(this.mFocusedTaskViewId)[0];
        int i6 = this.mCurrentPage;
        removeAllViews();
        int size = arrayList.size() - 1;
        while (size >= 0) {
            GroupTask groupTask = arrayList2.get(size);
            boolean hasMultipleTasks = groupTask.hasMultipleTasks();
            TaskView taskViewFromPool = getTaskViewFromPool(hasMultipleTasks);
            addView(taskViewFromPool);
            if (hasMultipleTasks) {
                boolean z2 = groupTask.mStagedSplitBounds.leftTopTaskId == groupTask.task1.key.id ? z : false;
                ((GroupedTaskView) taskViewFromPool).bind(z2 ? groupTask.task1 : groupTask.task2, z2 ? groupTask.task2 : groupTask.task1, this.mOrientationState, groupTask.mStagedSplitBounds);
            } else {
                taskViewFromPool.bind(groupTask.task1, this.mOrientationState);
            }
            size--;
            z = true;
        }
        if (!arrayList.isEmpty()) {
            addView(this.mClearAllButton);
            this.mClearAllButton.setVisibility(4);
        }
        boolean z3 = this.mNextPage != -1;
        if (z3) {
            this.mCurrentPage = i6;
        } else {
            setCurrentPage(i6);
        }
        TaskView taskViewByTaskId3 = getTaskViewByTaskId(i5);
        if (taskViewByTaskId3 == null && getTaskViewCount() > 0) {
            taskViewByTaskId3 = getTaskViewAt(0);
        }
        this.mFocusedTaskViewId = taskViewByTaskId3 != null ? taskViewByTaskId3.getTaskViewId() : -1;
        updateTaskSize();
        updateChildTaskOrientations();
        if (i4 != -1) {
            taskView = getTaskViewByTaskId(i4);
            if (taskView != null) {
                this.mRunningTaskViewId = taskView.getTaskViewId();
            } else {
                this.mRunningTaskViewId = -1;
            }
        }
        if (!z3) {
            if (i4 != -1) {
                i = indexOfChild(taskView);
            } else if (getTaskViewCount() > 0) {
                i = indexOfChild(requireTaskViewAt(0));
            }
            if (!(i == -1 || this.mCurrentPage == i)) {
                setCurrentPage(i);
            }
            int i7 = this.mIgnoreResetTaskId;
            if (!(i7 == -1 || getTaskViewByTaskId(i7) == taskViewByTaskId2)) {
                this.mIgnoreResetTaskId = -1;
            }
            resetTaskVisuals();
            onTaskStackUpdated();
            updateEnabledOverlays();
        } else if (!(i2 == -1 || (taskViewByTaskId = getTaskViewByTaskId(i2)) == null)) {
            i = indexOfChild(taskViewByTaskId);
            setCurrentPage(i);
            int i72 = this.mIgnoreResetTaskId;
            this.mIgnoreResetTaskId = -1;
            resetTaskVisuals();
            onTaskStackUpdated();
            updateEnabledOverlays();
        }
        i = -1;
        setCurrentPage(i);
        int i722 = this.mIgnoreResetTaskId;
        this.mIgnoreResetTaskId = -1;
        resetTaskVisuals();
        onTaskStackUpdated();
        updateEnabledOverlays();
    }

    public /* synthetic */ void lambda$applyLoadPlan$4$RecentsView(ArrayList arrayList, Boolean bool) {
        applyLoadPlan(arrayList);
    }

    private boolean isModal() {
        return this.mTaskModalness > 0.0f;
    }

    public boolean isLoadingTasks() {
        return this.mModel.isLoadingTasksInBackground();
    }

    private void removeTasksViewsAndClearAllButton() {
        for (int taskViewCount = getTaskViewCount() - 1; taskViewCount >= 0; taskViewCount--) {
            removeView(requireTaskViewAt(taskViewCount));
        }
        if (indexOfChild(this.mClearAllButton) != -1) {
            removeView(this.mClearAllButton);
        }
    }

    public int getTaskViewCount() {
        int childCount = getChildCount();
        return indexOfChild(this.mClearAllButton) != -1 ? childCount - 1 : childCount;
    }

    public int getGroupedTaskViewCount() {
        int i = 0;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof GroupedTaskView) {
                i++;
            }
        }
        return i;
    }

    public int getTopRowTaskCountForTablet() {
        return this.mTopRowIdSet.size();
    }

    public int getBottomRowTaskCountForTablet() {
        return (getTaskViewCount() - this.mTopRowIdSet.size()) - 1;
    }

    /* access modifiers changed from: protected */
    public void onTaskStackUpdated() {
        updateEmptyMessage();
    }

    public void resetTaskVisuals() {
        int taskViewCount = getTaskViewCount();
        while (true) {
            taskViewCount--;
            float f = 0.0f;
            if (taskViewCount < 0) {
                break;
            }
            TaskView requireTaskViewAt = requireTaskViewAt(taskViewCount);
            if (this.mIgnoreResetTaskId != requireTaskViewAt.getTaskIds()[0]) {
                requireTaskViewAt.resetViewTransforms();
                if (!this.mTaskIconScaledDown) {
                    f = 1.0f;
                }
                requireTaskViewAt.setIconScaleAndDim(f);
                requireTaskViewAt.setStableAlpha(this.mContentAlpha);
                requireTaskViewAt.setFullscreenProgress(this.mFullscreenProgress);
                requireTaskViewAt.setModalness(this.mTaskModalness);
            }
        }
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            runActionOnRemoteHandles($$Lambda$RecentsView$VwP94hmtlWXt6kLPY5rkmvuW1GM.INSTANCE);
            boolean z = this.mRunningTaskShowScreenshot;
            if (!z) {
                setRunningTaskViewShowScreenshot(z);
            }
        }
        boolean z2 = this.mRunningTaskTileHidden;
        if (z2) {
            setRunningTaskHidden(z2);
        }
        updateCurveProperties();
        loadVisibleTaskData(3);
        setTaskModalness(0.0f);
        setColorTint(0.0f);
    }

    static /* synthetic */ void lambda$resetTaskVisuals$5(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        TaskViewSimulator taskViewSimulator = remoteTargetHandle.getTaskViewSimulator();
        taskViewSimulator.taskPrimaryTranslation.value = 0.0f;
        taskViewSimulator.taskSecondaryTranslation.value = 0.0f;
        taskViewSimulator.fullScreenProgress.value = 0.0f;
        taskViewSimulator.recentsViewScale.value = 1.0f;
    }

    public void setFullscreenProgress(float f) {
        this.mFullscreenProgress = f;
        int taskViewCount = getTaskViewCount();
        for (int i = 0; i < taskViewCount; i++) {
            requireTaskViewAt(i).setFullscreenProgress(this.mFullscreenProgress);
        }
        this.mClearAllButton.setFullscreenProgress(f);
        this.mActionsView.getFullscreenAlpha().setValue(Utilities.mapToRange(f, 0.0f, 0.1f, 1.0f, 0.0f, Interpolators.LINEAR));
    }

    private void updateTaskStackListenerState() {
        boolean z = this.mOverviewStateEnabled && isAttachedToWindow() && getWindowVisibility() == 0;
        if (z != this.mHandleTaskStackChanges) {
            this.mHandleTaskStackChanges = z;
            if (z) {
                reloadIfNeeded();
            }
        }
    }

    public void setInsets(Rect rect) {
        this.mInsets.set(rect);
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        setOverviewGridEnabled(this.mActivity.getStateManager().getState().displayOverviewTasksAsGrid(deviceProfile));
        setPageSpacing(deviceProfile.overviewPageSpacing);
        runActionOnRemoteHandles(new Consumer() {
            public final void accept(Object obj) {
                ((RemoteTargetGluer.RemoteTargetHandle) obj).getTaskViewSimulator().setDp(DeviceProfile.this);
            }
        });
        this.mOrientationState.setDeviceProfile(deviceProfile);
        updateOrientationHandler();
        this.mActionsView.updateDimension(deviceProfile, this.mLastComputedTaskSize);
    }

    private void updateOrientationHandler() {
        updateOrientationHandler(true);
    }

    private void updateOrientationHandler(boolean z) {
        PagedOrientationHandler pagedOrientationHandler = this.mOrientationHandler;
        this.mOrientationHandler = this.mOrientationState.getOrientationHandler();
        this.mIsRtl = this.mOrientationHandler.getRecentsRtlSetting(getResources());
        setLayoutDirection(this.mIsRtl ? 1 : 0);
        this.mClearAllButton.setLayoutDirection(this.mIsRtl ^ true ? 1 : 0);
        this.mClearAllButton.setRotation(this.mOrientationHandler.getDegreesRotated());
        if (z || !this.mOrientationHandler.equals(pagedOrientationHandler)) {
            this.mActivity.getDragLayer().recreateControllers();
            onOrientationChanged();
        }
        boolean z2 = false;
        boolean z3 = (this.mOrientationState.getTouchRotation() == 0 && this.mOrientationState.getRecentsActivityRotation() == 0) ? false : true;
        OverviewActionsView overviewActionsView = this.mActionsView;
        if (!this.mOrientationState.isRecentsActivityRotationAllowed() && z3) {
            z2 = true;
        }
        overviewActionsView.updateHiddenFlags(1, z2);
        updateChildTaskOrientations();
        updateSizeAndPadding();
        requestLayout();
        setCurrentPage(this.mCurrentPage);
    }

    private void onOrientationChanged() {
        setModalStateEnabled(false);
        if (isSplitSelectionActive()) {
            onRotateInSplitSelectionState();
        }
    }

    private void updateSizeAndPadding() {
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        getTaskSize(this.mTempRect);
        this.mTaskWidth = this.mTempRect.width();
        this.mTaskHeight = this.mTempRect.height();
        this.mTempRect.top -= deviceProfile.overviewTaskThumbnailTopMarginPx;
        setPadding(this.mTempRect.left - this.mInsets.left, this.mTempRect.top - this.mInsets.top, (deviceProfile.widthPx - this.mInsets.right) - this.mTempRect.right, (deviceProfile.heightPx - this.mInsets.bottom) - this.mTempRect.bottom);
        this.mSizeStrategy.calculateGridSize(this.mActivity.getDeviceProfile(), this.mLastComputedGridSize);
        BaseActivityInterface<STATE_TYPE, ACTIVITY_TYPE> baseActivityInterface = this.mSizeStrategy;
        ACTIVITY_TYPE activity_type = this.mActivity;
        baseActivityInterface.calculateGridTaskSize(activity_type, activity_type.getDeviceProfile(), this.mLastComputedGridTaskSize, this.mOrientationHandler);
        this.mTaskGridVerticalDiff = (float) (this.mLastComputedGridTaskSize.top - this.mLastComputedTaskSize.top);
        this.mTopBottomRowHeightDiff = (float) (this.mLastComputedGridTaskSize.height() + deviceProfile.overviewTaskThumbnailTopMarginPx + deviceProfile.overviewRowSpacing);
        updateTaskSize();
    }

    private void updateTaskSize() {
        updateTaskSize(false);
    }

    /* access modifiers changed from: private */
    public void updateTaskSize(boolean z) {
        int taskViewCount = getTaskViewCount();
        if (taskViewCount != 0) {
            float f = 0.0f;
            for (int i = 0; i < taskViewCount; i++) {
                TaskView requireTaskViewAt = requireTaskViewAt(i);
                requireTaskViewAt.updateTaskSize();
                requireTaskViewAt.getPrimaryNonGridTranslationProperty().set(requireTaskViewAt, Float.valueOf(f));
                requireTaskViewAt.getSecondaryNonGridTranslationProperty().set(requireTaskViewAt, Float.valueOf(0.0f));
                float nonGridScale = ((float) requireTaskViewAt.getLayoutParams().width) * (1.0f - requireTaskViewAt.getNonGridScale());
                if (!this.mIsRtl) {
                    nonGridScale = -nonGridScale;
                }
                f += nonGridScale;
            }
            this.mClearAllButton.setFullscreenTranslationPrimary(f);
            updateGridProperties(z);
        }
    }

    public void getTaskSize(Rect rect) {
        BaseActivityInterface<STATE_TYPE, ACTIVITY_TYPE> baseActivityInterface = this.mSizeStrategy;
        ACTIVITY_TYPE activity_type = this.mActivity;
        baseActivityInterface.calculateTaskSize(activity_type, activity_type.getDeviceProfile(), rect);
        this.mLastComputedTaskSize.set(rect);
    }

    public Point getSelectedTaskSize() {
        BaseActivityInterface<STATE_TYPE, ACTIVITY_TYPE> baseActivityInterface = this.mSizeStrategy;
        ACTIVITY_TYPE activity_type = this.mActivity;
        baseActivityInterface.calculateTaskSize(activity_type, activity_type.getDeviceProfile(), this.mTempRect);
        return new Point(this.mTempRect.width(), this.mTempRect.height());
    }

    public Rect getLastComputedTaskSize() {
        return this.mLastComputedTaskSize;
    }

    public Rect getLastComputedGridTaskSize() {
        return this.mLastComputedGridTaskSize;
    }

    public void getModalTaskSize(Rect rect) {
        BaseActivityInterface<STATE_TYPE, ACTIVITY_TYPE> baseActivityInterface = this.mSizeStrategy;
        ACTIVITY_TYPE activity_type = this.mActivity;
        baseActivityInterface.calculateModalTaskSize(activity_type, activity_type.getDeviceProfile(), rect);
    }

    /* access modifiers changed from: protected */
    public boolean computeScrollHelper() {
        boolean computeScrollHelper = super.computeScrollHelper();
        updateCurveProperties();
        boolean z = false;
        if (computeScrollHelper || isHandlingTouch()) {
            if (computeScrollHelper && this.mScroller.getCurrVelocity() > this.mFastFlingVelocity) {
                z = true;
            }
            loadVisibleTaskData(3);
        }
        updateActionsViewFocusedScroll();
        this.mModel.getThumbnailCache().getHighResLoadingState().setFlingingFast(z);
        return computeScrollHelper;
    }

    /* access modifiers changed from: private */
    public void updateActionsViewFocusedScroll() {
        if (showAsGrid()) {
            float f = isFocusedTaskInExpectedScrollPosition() ? 1.0f : 0.0f;
            ObjectAnimator objectAnimator = this.mActionsViewAlphaAnimator;
            if (objectAnimator == null || !objectAnimator.isStarted() || (this.mActionsViewAlphaAnimator.isStarted() && this.mActionsViewAlphaAnimatorFinalValue != f)) {
                animateActionsViewAlpha(f, 300);
            }
        }
    }

    private void animateActionsViewAlpha(float f, long j) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mActionsView.getVisibilityAlpha(), MultiValueAlpha.VALUE, new float[]{f});
        this.mActionsViewAlphaAnimator = ofFloat;
        this.mActionsViewAlphaAnimatorFinalValue = f;
        ofFloat.setDuration(j);
        this.mActionsViewAlphaAnimator.start();
    }

    public void updateCurveProperties() {
        if (getPageCount() != 0 && getPageAt(0).getMeasuredWidth() != 0) {
            this.mClearAllButton.onRecentsViewScroll(this.mOrientationHandler.getPrimaryScroll(this), this.mOverviewGridEnabled);
        }
    }

    /* access modifiers changed from: protected */
    public int getDestinationPage(int i) {
        if (!this.mActivity.getDeviceProfile().isTablet) {
            return super.getDestinationPage(i);
        }
        int i2 = -1;
        if (!pageScrollsInitialized()) {
            Log.e(TAG, "Cannot get destination page: RecentsView not properly initialized", new IllegalStateException());
            return -1;
        }
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < getChildCount(); i4++) {
            int abs = Math.abs(this.mPageScrolls[i4] - i);
            if (abs < i3) {
                i2 = i4;
                i3 = abs;
            }
        }
        return i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c8 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadVisibleTaskData(int r15) {
        /*
            r14 = this;
            boolean r0 = r14.mOverviewStateEnabled
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x0010
            android.widget.OverScroller r0 = r14.mScroller
            boolean r0 = r0.isFinished()
            if (r0 == 0) goto L_0x0010
            r0 = r1
            goto L_0x0011
        L_0x0010:
            r0 = r2
        L_0x0011:
            if (r0 != 0) goto L_0x00cb
            int r0 = r14.mTaskListChangeId
            r3 = -1
            if (r0 != r3) goto L_0x001a
            goto L_0x00cb
        L_0x001a:
            boolean r0 = r14.showAsGrid()
            if (r0 == 0) goto L_0x0036
            com.android.launcher3.touch.PagedOrientationHandler r0 = r14.mOrientationHandler
            int r0 = r0.getPrimaryScroll(r14)
            com.android.launcher3.touch.PagedOrientationHandler r3 = r14.mOrientationHandler
            int r3 = r3.getMeasuredSize(r14)
            int r4 = r3 / 2
            int r5 = r0 - r4
            int r0 = r0 + r3
            int r0 = r0 + r4
            r3 = r0
            r0 = r2
            r4 = r0
            goto L_0x004d
        L_0x0036:
            int r0 = r14.getPageNearestToCenterOfScreen()
            int r3 = r14.getChildCount()
            int r4 = r0 + -2
            int r4 = java.lang.Math.max(r2, r4)
            int r0 = r0 + 2
            int r3 = r3 - r1
            int r0 = java.lang.Math.min(r0, r3)
            r3 = r2
            r5 = r3
        L_0x004d:
            r6 = r2
        L_0x004e:
            int r7 = r14.getTaskViewCount()
            if (r6 >= r7) goto L_0x00cb
            com.android.quickstep.views.TaskView r7 = r14.requireTaskViewAt(r6)
            com.android.systemui.shared.recents.model.Task r8 = r7.getTask()
            int r9 = r14.indexOfChild(r7)
            boolean r10 = r14.showAsGrid()
            if (r10 == 0) goto L_0x006b
            boolean r9 = r14.isTaskViewWithinBounds(r7, r5, r3)
            goto L_0x0072
        L_0x006b:
            if (r4 > r9) goto L_0x0071
            if (r9 > r0) goto L_0x0071
            r9 = r1
            goto L_0x0072
        L_0x0071:
            r9 = r2
        L_0x0072:
            if (r9 == 0) goto L_0x00b0
            com.android.systemui.shared.recents.model.Task[] r10 = r14.mTmpRunningTasks
            if (r10 == 0) goto L_0x0085
            int r11 = r10.length
            r12 = r2
        L_0x007a:
            if (r12 >= r11) goto L_0x0085
            r13 = r10[r12]
            if (r8 != r13) goto L_0x0082
            r10 = r1
            goto L_0x0086
        L_0x0082:
            int r12 = r12 + 1
            goto L_0x007a
        L_0x0085:
            r10 = r2
        L_0x0086:
            if (r10 == 0) goto L_0x0089
            goto L_0x00c8
        L_0x0089:
            android.util.SparseBooleanArray r10 = r14.mHasVisibleTaskData
            com.android.systemui.shared.recents.model.Task$TaskKey r11 = r8.key
            int r11 = r11.id
            boolean r10 = r10.get(r11)
            if (r10 != 0) goto L_0x00a6
            com.android.quickstep.views.TaskView r10 = r14.getRunningTaskView()
            if (r7 != r10) goto L_0x00a2
            boolean r10 = r14.mGestureActive
            if (r10 == 0) goto L_0x00a2
            r10 = r15 & -3
            goto L_0x00a3
        L_0x00a2:
            r10 = r15
        L_0x00a3:
            r7.onTaskListVisibilityChanged(r1, r10)
        L_0x00a6:
            android.util.SparseBooleanArray r7 = r14.mHasVisibleTaskData
            com.android.systemui.shared.recents.model.Task$TaskKey r8 = r8.key
            int r8 = r8.id
            r7.put(r8, r9)
            goto L_0x00c8
        L_0x00b0:
            android.util.SparseBooleanArray r9 = r14.mHasVisibleTaskData
            com.android.systemui.shared.recents.model.Task$TaskKey r10 = r8.key
            int r10 = r10.id
            boolean r9 = r9.get(r10)
            if (r9 == 0) goto L_0x00bf
            r7.onTaskListVisibilityChanged(r2, r15)
        L_0x00bf:
            android.util.SparseBooleanArray r7 = r14.mHasVisibleTaskData
            com.android.systemui.shared.recents.model.Task$TaskKey r8 = r8.key
            int r8 = r8.id
            r7.delete(r8)
        L_0x00c8:
            int r6 = r6 + 1
            goto L_0x004e
        L_0x00cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.RecentsView.loadVisibleTaskData(int):void");
    }

    private void unloadVisibleTaskData(int i) {
        TaskView taskViewByTaskId;
        for (int i2 = 0; i2 < this.mHasVisibleTaskData.size(); i2++) {
            if (this.mHasVisibleTaskData.valueAt(i2) && (taskViewByTaskId = getTaskViewByTaskId(this.mHasVisibleTaskData.keyAt(i2))) != null) {
                taskViewByTaskId.onTaskListVisibilityChanged(false, i);
            }
        }
        this.mHasVisibleTaskData.clear();
    }

    public void onHighResLoadingStateChanged(boolean z) {
        TaskView taskViewByTaskId;
        for (int i = 0; i < this.mHasVisibleTaskData.size(); i++) {
            if (this.mHasVisibleTaskData.valueAt(i) && (taskViewByTaskId = getTaskViewByTaskId(this.mHasVisibleTaskData.keyAt(i))) != null) {
                taskViewByTaskId.onTaskListVisibilityChanged(true);
            }
        }
    }

    public void reset() {
        setCurrentTask(-1);
        this.mCurrentPageScrollDiff = 0;
        this.mIgnoreResetTaskId = -1;
        this.mTaskListChangeId = -1;
        this.mFocusedTaskViewId = -1;
        if (this.mRecentsAnimationController != null) {
            if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || !this.mEnableDrawingLiveTile) {
                this.mRecentsAnimationController = null;
            } else {
                finishRecentsAnimation(true, (Runnable) null);
            }
        }
        setEnableDrawingLiveTile(false);
        runActionOnRemoteHandles($$Lambda$RecentsView$kUFsNUqv4boKiozjgpn5njUR1Fk.INSTANCE);
        resetFromSplitSelectionState();
        this.mSplitSelectStateController.resetState();
        post(new Runnable() {
            public final void run() {
                RecentsView.this.lambda$reset$8$RecentsView();
            }
        });
    }

    static /* synthetic */ void lambda$reset$7(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTransformParams().setTargetSet((RemoteAnimationTargets) null);
        remoteTargetHandle.getTaskViewSimulator().setDrawsBelowRecents(false);
    }

    public /* synthetic */ void lambda$reset$8$RecentsView() {
        unloadVisibleTaskData(3);
        setCurrentPage(0);
        LayoutUtils.setViewEnabled(this.mActionsView, true);
        if (this.mOrientationState.setGestureActive(false)) {
            updateOrientationHandler(false);
        }
    }

    public int getRunningTaskViewId() {
        return this.mRunningTaskViewId;
    }

    /* access modifiers changed from: protected */
    public int[] getTaskIdsForRunningTaskView() {
        return getTaskIdsForTaskViewId(this.mRunningTaskViewId);
    }

    private int[] getTaskIdsForTaskViewId(int i) {
        TaskView taskViewFromTaskViewId = getTaskViewFromTaskViewId(i);
        if (taskViewFromTaskViewId == null) {
            return this.INVALID_TASK_IDS;
        }
        return taskViewFromTaskViewId.getTaskIds();
    }

    public TaskView getRunningTaskView() {
        return getTaskViewFromTaskViewId(this.mRunningTaskViewId);
    }

    public TaskView getFocusedTaskView() {
        return getTaskViewFromTaskViewId(this.mFocusedTaskViewId);
    }

    /* access modifiers changed from: private */
    public TaskView getTaskViewFromTaskViewId(int i) {
        if (i == -1) {
            return null;
        }
        for (int i2 = 0; i2 < getTaskViewCount(); i2++) {
            TaskView requireTaskViewAt = requireTaskViewAt(i2);
            if (requireTaskViewAt.getTaskViewId() == i) {
                return requireTaskViewAt;
            }
        }
        return null;
    }

    public int getRunningTaskIndex() {
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView == null) {
            return -1;
        }
        return indexOfChild(runningTaskView);
    }

    private <T extends TaskView> T getTaskViewFromPool(boolean z) {
        T t;
        if (z) {
            t = (TaskView) this.mGroupedTaskViewPool.getView();
        } else {
            t = (TaskView) this.mTaskViewPool.getView();
        }
        t.setTaskViewId(this.mTaskViewIdCount);
        int i = this.mTaskViewIdCount;
        if (i == Integer.MAX_VALUE) {
            this.mTaskViewIdCount = 0;
        } else {
            this.mTaskViewIdCount = i + 1;
        }
        return t;
    }

    public int getTaskIndexForId(int i) {
        TaskView taskViewByTaskId = getTaskViewByTaskId(i);
        if (taskViewByTaskId == null) {
            return -1;
        }
        return indexOfChild(taskViewByTaskId);
    }

    public void reloadIfNeeded() {
        if (!this.mModel.isTaskListValid(this.mTaskListChangeId)) {
            this.mTaskListChangeId = this.mModel.getTasks(new Consumer() {
                public final void accept(Object obj) {
                    RecentsView.this.applyLoadPlan((ArrayList) obj);
                }
            });
        }
    }

    public void onGestureAnimationStart(Task[] taskArr, RotationTouchHelper rotationTouchHelper) {
        this.mGestureActive = true;
        if (this.mOrientationState.setGestureActive(true)) {
            setLayoutRotation(rotationTouchHelper.getCurrentActiveRotation(), rotationTouchHelper.getDisplayRotation());
            updateSizeAndPadding();
        }
        showCurrentTask(taskArr);
        setEnableFreeScroll(false);
        setEnableDrawingLiveTile(false);
        setRunningTaskHidden(true);
        setTaskIconScaledDown(true);
    }

    public void onSwipeUpAnimationSuccess() {
        animateUpTaskIconScale();
        setSwipeDownShouldLaunchApp(true);
    }

    /* access modifiers changed from: private */
    public void animateRecentsRotationInPlace(int i) {
        if (!this.mOrientationState.isRecentsActivityRotationAllowed()) {
            AnimatorSet recentsChangedOrientation = setRecentsChangedOrientation(true);
            recentsChangedOrientation.addListener(AnimatorListeners.forSuccessCallback(new Runnable(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RecentsView.this.lambda$animateRecentsRotationInPlace$9$RecentsView(this.f$1);
                }
            }));
            recentsChangedOrientation.start();
        }
    }

    public /* synthetic */ void lambda$animateRecentsRotationInPlace$9$RecentsView(int i) {
        setLayoutRotation(i, this.mOrientationState.getDisplayRotation());
        this.mActivity.getDragLayer().recreateControllers();
        setRecentsChangedOrientation(false).start();
    }

    public AnimatorSet setRecentsChangedOrientation(boolean z) {
        getRunningTaskIndex();
        int currentPage = getCurrentPage();
        AnimatorSet animatorSet = new AnimatorSet();
        for (int i = 0; i < getTaskViewCount(); i++) {
            TaskView requireTaskViewAt = requireTaskViewAt(i);
            float f = 0.0f;
            if (currentPage != i || requireTaskViewAt.getAlpha() == 0.0f) {
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (!z) {
                    f = 1.0f;
                }
                fArr[0] = f;
                animatorSet.play(ObjectAnimator.ofFloat(requireTaskViewAt, property, fArr));
            }
        }
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public void updateChildTaskOrientations() {
        for (int i = 0; i < getTaskViewCount(); i++) {
            requireTaskViewAt(i).setOrientationState(this.mOrientationState);
        }
        TaskMenuView taskMenuView = (TaskMenuView) AbstractFloatingView.getTopOpenViewWithType(this.mActivity, 2048);
        if (taskMenuView != null) {
            taskMenuView.onRotationChanged();
        }
    }

    public void onPrepareGestureEndAnimation(AnimatorSet animatorSet, GestureState.GestureEndTarget gestureEndTarget, TaskViewSimulator[] taskViewSimulatorArr) {
        this.mCurrentGestureEndTarget = gestureEndTarget;
        if (gestureEndTarget == GestureState.GestureEndTarget.RECENTS) {
            updateGridProperties();
        }
        if (this.mSizeStrategy.stateFromGestureEndTarget(gestureEndTarget).displayOverviewTasksAsGrid(this.mActivity.getDeviceProfile())) {
            TaskView runningTaskView = getRunningTaskView();
            float f = 0.0f;
            if (runningTaskView != null) {
                f = this.mOrientationHandler.getPrimaryValue(runningTaskView.getGridTranslationX(), runningTaskView.getGridTranslationY()) - ((Float) runningTaskView.getPrimaryNonGridTranslationProperty().get(runningTaskView)).floatValue();
            }
            for (TaskViewSimulator taskViewSimulator : taskViewSimulatorArr) {
                if (animatorSet == null) {
                    setGridProgress(1.0f);
                    taskViewSimulator.taskPrimaryTranslation.value = f;
                } else {
                    animatorSet.play(ObjectAnimator.ofFloat(this, RECENTS_GRID_PROGRESS, new float[]{1.0f}));
                    animatorSet.play(taskViewSimulator.taskPrimaryTranslation.animateToValue(f));
                }
            }
        }
    }

    public void onGestureAnimationEnd() {
        this.mGestureActive = false;
        if (this.mOrientationState.setGestureActive(false)) {
            updateOrientationHandler(false);
        }
        setEnableFreeScroll(true);
        setEnableDrawingLiveTile(this.mCurrentGestureEndTarget == GestureState.GestureEndTarget.RECENTS);
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            setRunningTaskViewShowScreenshot(true);
        }
        setRunningTaskHidden(false);
        animateUpTaskIconScale();
        animateActionsViewIn();
        this.mCurrentGestureEndTarget = null;
    }

    /* access modifiers changed from: protected */
    public boolean shouldAddStubTaskView(Task[] taskArr) {
        int i;
        int i2;
        if (taskArr.length > 1) {
            TaskView taskViewByTaskId = getTaskViewByTaskId(taskArr[0].key.id);
            TaskView taskViewByTaskId2 = getTaskViewByTaskId(taskArr[1].key.id);
            if (taskViewByTaskId == null) {
                i = -1;
            } else {
                i = taskViewByTaskId.getTaskViewId();
            }
            if (taskViewByTaskId2 == null) {
                i2 = -1;
            } else {
                i2 = taskViewByTaskId2.getTaskViewId();
            }
            if (i != i2 || i == -1) {
                return true;
            }
            return false;
        }
        Task task = taskArr[0];
        if (task == null || getTaskViewByTaskId(task.key.id) != null) {
            return false;
        }
        return true;
    }

    private void showCurrentTask(Task[] taskArr) {
        TaskView taskView;
        if (taskArr.length != 0) {
            int i = -1;
            boolean z = taskArr.length > 1;
            if (shouldAddStubTaskView(taskArr)) {
                boolean z2 = getChildCount() == 0;
                if (z) {
                    taskView = getTaskViewFromPool(true);
                    this.mTmpRunningTasks = new Task[]{taskArr[0], taskArr[1]};
                    addView(taskView, 0);
                    Task[] taskArr2 = this.mTmpRunningTasks;
                    ((GroupedTaskView) taskView).bind(taskArr2[0], taskArr2[1], this.mOrientationState, this.mSplitBoundsConfig);
                } else {
                    taskView = getTaskViewFromPool(false);
                    addView(taskView, 0);
                    Task[] taskArr3 = {taskArr[0]};
                    this.mTmpRunningTasks = taskArr3;
                    taskView.bind(taskArr3[0], this.mOrientationState);
                }
                int taskViewId = taskView.getTaskViewId();
                if (z2) {
                    addView(this.mClearAllButton);
                    this.mClearAllButton.setVisibility(4);
                }
                measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), BasicMeasure.EXACTLY));
                layout(getLeft(), getTop(), getRight(), getBottom());
                i = taskViewId;
            } else if (getTaskViewByTaskId(taskArr[0].key.id) != null) {
                i = getTaskViewByTaskId(taskArr[0].key.id).getTaskViewId();
            }
            boolean z3 = this.mRunningTaskTileHidden;
            setCurrentTask(i);
            this.mFocusedTaskViewId = i;
            setCurrentPage(getRunningTaskIndex());
            setRunningTaskViewShowScreenshot(false);
            setRunningTaskHidden(z3);
            updateTaskSize();
            updateChildTaskOrientations();
            reloadIfNeeded();
        }
    }

    public void setCurrentTask(int i) {
        int i2 = this.mRunningTaskViewId;
        if (i2 != i) {
            if (i2 != -1) {
                setTaskIconScaledDown(false);
                setRunningTaskViewShowScreenshot(true);
                setRunningTaskHidden(false);
            }
            this.mRunningTaskViewId = i;
        }
    }

    private int getTaskViewIdFromTaskId(int i) {
        TaskView taskViewByTaskId = getTaskViewByTaskId(i);
        if (taskViewByTaskId != null) {
            return taskViewByTaskId.getTaskViewId();
        }
        return -1;
    }

    public void setRunningTaskHidden(boolean z) {
        float f;
        this.mRunningTaskTileHidden = z;
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView != null) {
            if (z) {
                f = 0.0f;
            } else {
                f = this.mContentAlpha;
            }
            runningTaskView.setStableAlpha(f);
            if (!z) {
                AccessibilityManagerCompat.sendCustomAccessibilityEvent(runningTaskView, 8, (String) null);
            }
        }
    }

    private void setRunningTaskViewShowScreenshot(boolean z) {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mRunningTaskShowScreenshot = z;
            TaskView runningTaskView = getRunningTaskView();
            if (runningTaskView != null) {
                runningTaskView.setShowScreenshot(this.mRunningTaskShowScreenshot);
            }
        }
    }

    public void setTaskIconScaledDown(boolean z) {
        if (this.mTaskIconScaledDown != z) {
            this.mTaskIconScaledDown = z;
            int taskViewCount = getTaskViewCount();
            for (int i = 0; i < taskViewCount; i++) {
                requireTaskViewAt(i).setIconScaleAndDim(this.mTaskIconScaledDown ? 0.0f : 1.0f);
            }
        }
    }

    private void animateActionsViewIn() {
        if (!showAsGrid() || isFocusedTaskInExpectedScrollPosition()) {
            animateActionsViewAlpha(1.0f, 120);
        }
    }

    public void animateUpTaskIconScale() {
        this.mTaskIconScaledDown = false;
        int taskViewCount = getTaskViewCount();
        for (int i = 0; i < taskViewCount; i++) {
            TaskView requireTaskViewAt = requireTaskViewAt(i);
            requireTaskViewAt.setIconScaleAnimStartProgress(0.0f);
            requireTaskViewAt.animateIconScaleAndDimIntoView();
        }
    }

    private void updateGridProperties() {
        updateGridProperties(false, Integer.MAX_VALUE);
    }

    private void updateGridProperties(boolean z) {
        updateGridProperties(z, Integer.MAX_VALUE);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00bd, code lost:
        if (r14 <= r15) goto L_0x00bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00c2, code lost:
        r20 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00cc, code lost:
        if (r14 <= r15) goto L_0x00bf;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0209  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0211 A[LOOP:3: B:115:0x020f->B:116:0x0211, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x022c  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0235  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x016f A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d1  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01af  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01bc  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01d1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateGridProperties(boolean r26, int r27) {
        /*
            r25 = this;
            r0 = r25
            int r1 = r25.getTaskViewCount()
            if (r1 != 0) goto L_0x0009
            return
        L_0x0009:
            ACTIVITY_TYPE r2 = r0.mActivity
            com.android.launcher3.DeviceProfile r2 = r2.getDeviceProfile()
            int r2 = r2.overviewTaskThumbnailTopMarginPx
            com.android.launcher3.util.IntSet r3 = new com.android.launcher3.util.IntSet
            r3.<init>()
            com.android.launcher3.util.IntSet r4 = new com.android.launcher3.util.IntSet
            r4.<init>()
            float[] r5 = new float[r1]
            r6 = 2147483647(0x7fffffff, float:NaN)
            int r7 = r25.getNextPage()
            com.android.quickstep.views.TaskView r8 = r0.getTaskViewAt(r7)
            com.android.quickstep.views.TaskView r9 = r25.getHomeTaskView()
            r10 = 0
            if (r26 != 0) goto L_0x0034
            com.android.launcher3.util.IntSet r11 = r0.mTopRowIdSet
            r11.clear()
        L_0x0034:
            r19 = r7
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
        L_0x0041:
            if (r13 >= r1) goto L_0x017a
            com.android.quickstep.views.TaskView r7 = r0.requireTaskViewAt(r13)
            r21 = r1
            android.view.ViewGroup$LayoutParams r1 = r7.getLayoutParams()
            int r1 = r1.width
            r22 = r4
            int r4 = r0.mPageSpacing
            int r1 = r1 + r4
            boolean r4 = r7.isFocusedTask()
            if (r4 == 0) goto L_0x0092
            int r14 = r14 + r1
            int r15 = r15 + r1
            r4 = r5[r13]
            float r6 = (float) r11
            float r4 = r4 + r6
            r5[r13] = r4
            r4 = r5[r13]
            boolean r6 = r0.mIsRtl
            if (r6 == 0) goto L_0x006a
            float r6 = (float) r1
            goto L_0x006c
        L_0x006a:
            int r6 = -r1
            float r6 = (float) r6
        L_0x006c:
            float r4 = r4 + r6
            r5[r13] = r4
            android.graphics.Rect r4 = r0.mLastComputedTaskSize
            int r4 = r4.height()
            int r4 = r4 + r2
            android.view.ViewGroup$LayoutParams r6 = r7.getLayoutParams()
            int r6 = r6.height
            int r4 = r4 - r6
            float r4 = (float) r4
            r6 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r6
            r7.setGridTranslationY(r4)
            r12 = r1
            if (r7 != r8) goto L_0x0089
            r18 = r12
        L_0x0089:
            r23 = r2
            r24 = r9
            r6 = r13
            r1 = r22
            goto L_0x016f
        L_0x0092:
            if (r13 <= r6) goto L_0x00a4
            r4 = r5[r13]
            r23 = r2
            boolean r2 = r0.mIsRtl
            if (r2 == 0) goto L_0x009e
            float r2 = (float) r12
            goto L_0x00a0
        L_0x009e:
            int r2 = -r12
            float r2 = (float) r2
        L_0x00a0:
            float r4 = r4 + r2
            r5[r13] = r4
            goto L_0x00ae
        L_0x00a4:
            r23 = r2
            boolean r2 = r0.mIsRtl
            if (r2 == 0) goto L_0x00ac
            r2 = r1
            goto L_0x00ad
        L_0x00ac:
            int r2 = -r1
        L_0x00ad:
            int r11 = r11 + r2
        L_0x00ae:
            int r2 = r7.getTaskViewId()
            if (r26 == 0) goto L_0x00cc
            r4 = r27
            if (r13 <= r4) goto L_0x00c5
            com.android.launcher3.util.IntSet r4 = r0.mTopRowIdSet
            r4.remove(r2)
            if (r14 > r15) goto L_0x00c2
        L_0x00bf:
            r20 = 1
            goto L_0x00cf
        L_0x00c2:
            r20 = 0
            goto L_0x00cf
        L_0x00c5:
            com.android.launcher3.util.IntSet r4 = r0.mTopRowIdSet
            boolean r20 = r4.contains(r2)
            goto L_0x00cf
        L_0x00cc:
            if (r14 > r15) goto L_0x00c2
            goto L_0x00bf
        L_0x00cf:
            if (r20 == 0) goto L_0x011e
            if (r9 == 0) goto L_0x00d7
            if (r10 != 0) goto L_0x00d7
            r10 = r7
            goto L_0x00d8
        L_0x00d7:
            int r14 = r14 + r1
        L_0x00d8:
            r3.add(r13)
            com.android.launcher3.util.IntSet r1 = r0.mTopRowIdSet
            r1.add(r2)
            float r1 = r0.mTaskGridVerticalDiff
            r7.setGridTranslationY(r1)
            int r1 = r13 + -1
            r2 = 0
        L_0x00e8:
            boolean r4 = r3.contains(r1)
            if (r4 != 0) goto L_0x010b
            if (r1 < 0) goto L_0x010b
            if (r1 != r6) goto L_0x00f5
            r24 = r9
            goto L_0x0106
        L_0x00f5:
            com.android.quickstep.views.TaskView r4 = r0.requireTaskViewAt(r1)
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            int r4 = r4.width
            r24 = r9
            int r9 = r0.mPageSpacing
            int r4 = r4 + r9
            float r4 = (float) r4
            float r2 = r2 + r4
        L_0x0106:
            int r1 = r1 + -1
            r9 = r24
            goto L_0x00e8
        L_0x010b:
            r24 = r9
            boolean r1 = r0.mIsRtl
            if (r1 == 0) goto L_0x0112
            goto L_0x0113
        L_0x0112:
            float r2 = -r2
        L_0x0113:
            r1 = r5[r13]
            float r17 = r17 + r2
            float r1 = r1 + r17
            r5[r13] = r1
            r1 = r22
            goto L_0x0166
        L_0x011e:
            r24 = r9
            int r15 = r15 + r1
            r1 = r22
            r1.add(r13)
            float r2 = r0.mTopBottomRowHeightDiff
            float r4 = r0.mTaskGridVerticalDiff
            float r2 = r2 + r4
            r7.setGridTranslationY(r2)
            int r2 = r13 + -1
            r4 = 0
        L_0x0131:
            boolean r9 = r1.contains(r2)
            if (r9 != 0) goto L_0x0154
            if (r2 < 0) goto L_0x0154
            if (r2 != r6) goto L_0x013e
            r22 = r10
            goto L_0x014f
        L_0x013e:
            com.android.quickstep.views.TaskView r9 = r0.requireTaskViewAt(r2)
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            int r9 = r9.width
            r22 = r10
            int r10 = r0.mPageSpacing
            int r9 = r9 + r10
            float r9 = (float) r9
            float r4 = r4 + r9
        L_0x014f:
            int r2 = r2 + -1
            r10 = r22
            goto L_0x0131
        L_0x0154:
            r22 = r10
            boolean r2 = r0.mIsRtl
            if (r2 == 0) goto L_0x015b
            goto L_0x015c
        L_0x015b:
            float r4 = -r4
        L_0x015c:
            r2 = r5[r13]
            float r16 = r16 + r4
            float r2 = r2 + r16
            r5[r13] = r2
            r10 = r22
        L_0x0166:
            if (r7 != r8) goto L_0x016f
            if (r20 == 0) goto L_0x016d
            r18 = r14
            goto L_0x016f
        L_0x016d:
            r18 = r15
        L_0x016f:
            int r13 = r13 + 1
            r4 = r1
            r1 = r21
            r2 = r23
            r9 = r24
            goto L_0x0041
        L_0x017a:
            r21 = r1
            r1 = r4
            if (r8 == 0) goto L_0x0188
            r2 = 1
            r4 = 0
            float r2 = r8.getScrollAdjustment(r2, r4)
            r7 = r5[r19]
            goto L_0x018b
        L_0x0188:
            r4 = 0
            r2 = 0
            r7 = 0
        L_0x018b:
            int r9 = r21 + -1
            boolean r10 = r3.contains(r9)
            if (r10 == 0) goto L_0x0195
            r16 = r17
        L_0x0195:
            if (r14 > r15) goto L_0x01a0
            boolean r1 = r3.contains(r9)
            if (r1 == 0) goto L_0x01aa
            int r1 = r15 - r14
            goto L_0x01a8
        L_0x01a0:
            boolean r1 = r1.contains(r9)
            if (r1 == 0) goto L_0x01aa
            int r1 = r14 - r15
        L_0x01a8:
            float r1 = (float) r1
            goto L_0x01ab
        L_0x01aa:
            r1 = 0
        L_0x01ab:
            boolean r3 = r0.mIsRtl
            if (r3 == 0) goto L_0x01b0
            float r1 = -r1
        L_0x01b0:
            int r3 = java.lang.Math.max(r14, r15)
            android.graphics.Rect r9 = r0.mLastComputedGridSize
            int r9 = r9.width()
            if (r3 >= r9) goto L_0x01d1
            android.graphics.Rect r9 = r0.mLastComputedGridSize
            int r9 = r9.width()
            int r9 = r9 - r3
            float r3 = (float) r9
            boolean r9 = r0.mIsRtl
            if (r9 == 0) goto L_0x01c9
            float r3 = -r3
        L_0x01c9:
            r11 = r3
            android.graphics.Rect r3 = r0.mLastComputedGridSize
            int r3 = r3.width()
            goto L_0x01d2
        L_0x01d1:
            r11 = 0
        L_0x01d2:
            float r16 = r16 + r1
            float r16 = r16 + r11
            float r16 = r16 + r2
            r1 = r21
            if (r6 >= r1) goto L_0x01e6
            boolean r6 = r0.mIsRtl
            if (r6 == 0) goto L_0x01e2
            float r6 = (float) r12
            goto L_0x01e4
        L_0x01e2:
            int r6 = -r12
            float r6 = (float) r6
        L_0x01e4:
            float r16 = r16 + r6
        L_0x01e6:
            if (r8 == 0) goto L_0x020e
            int r3 = r3 - r18
            int r6 = r0.mPageSpacing
            int r3 = r3 + r6
            int r6 = r0.mTaskWidth
            android.view.ViewGroup$LayoutParams r8 = r8.getLayoutParams()
            int r8 = r8.width
            int r6 = r6 - r8
            android.graphics.Rect r8 = r0.mLastComputedGridSize
            int r8 = r8.width()
            int r9 = r0.mTaskWidth
            int r8 = r8 - r9
            int r8 = r8 / 2
            int r6 = r6 + r8
            if (r3 >= r6) goto L_0x020e
            int r6 = r6 - r3
            boolean r3 = r0.mIsRtl
            if (r3 == 0) goto L_0x020b
            float r3 = (float) r6
            goto L_0x020d
        L_0x020b:
            int r3 = -r6
            float r3 = (float) r3
        L_0x020d:
            float r7 = r7 + r3
        L_0x020e:
            r12 = r4
        L_0x020f:
            if (r12 >= r1) goto L_0x021f
            com.android.quickstep.views.TaskView r3 = r0.requireTaskViewAt(r12)
            r4 = r5[r12]
            float r4 = r4 - r7
            float r4 = r4 + r2
            r3.setGridTranslationX(r4)
            int r12 = r12 + 1
            goto L_0x020f
        L_0x021f:
            com.android.quickstep.views.ClearAllButton r1 = r0.mClearAllButton
            float r2 = r16 - r7
            r1.setGridTranslationPrimary(r2)
            com.android.quickstep.views.ClearAllButton r1 = r0.mClearAllButton
            boolean r2 = r0.mIsRtl
            if (r2 == 0) goto L_0x0235
            android.graphics.Rect r2 = r0.mLastComputedTaskSize
            int r2 = r2.left
            android.graphics.Rect r3 = r0.mLastComputedGridSize
            int r3 = r3.left
            goto L_0x023d
        L_0x0235:
            android.graphics.Rect r2 = r0.mLastComputedTaskSize
            int r2 = r2.right
            android.graphics.Rect r3 = r0.mLastComputedGridSize
            int r3 = r3.right
        L_0x023d:
            int r2 = r2 - r3
            float r2 = (float) r2
            r1.setGridScrollOffset(r2)
            float r1 = r0.mGridProgress
            r0.setGridProgress(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.RecentsView.updateGridProperties(boolean, int):void");
    }

    private boolean isSameGridRow(TaskView taskView, TaskView taskView2) {
        if (taskView == null || taskView2 == null) {
            return false;
        }
        int taskViewId = taskView.getTaskViewId();
        int taskViewId2 = taskView2.getTaskViewId();
        int i = this.mFocusedTaskViewId;
        if (taskViewId == i || taskViewId2 == i) {
            return false;
        }
        if ((!this.mTopRowIdSet.contains(taskViewId) || !this.mTopRowIdSet.contains(taskViewId2)) && (this.mTopRowIdSet.contains(taskViewId) || this.mTopRowIdSet.contains(taskViewId2))) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void setGridProgress(float f) {
        int taskViewCount = getTaskViewCount();
        if (taskViewCount != 0) {
            this.mGridProgress = f;
            for (int i = 0; i < taskViewCount; i++) {
                requireTaskViewAt(i).setGridProgress(f);
            }
            this.mClearAllButton.setGridProgress(f);
        }
    }

    /* access modifiers changed from: private */
    public void enableLayoutTransitions() {
        if (this.mLayoutTransition == null) {
            LayoutTransition layoutTransition = new LayoutTransition();
            this.mLayoutTransition = layoutTransition;
            layoutTransition.enableTransitionType(2);
            this.mLayoutTransition.setDuration(200);
            this.mLayoutTransition.setStartDelay(2, 0);
            this.mLayoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
                public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                }

                public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                    if (view instanceof TaskView) {
                        RecentsView.this.snapToPage(0);
                        RecentsView.this.setLayoutTransition((LayoutTransition) null);
                    }
                }
            });
        }
        setLayoutTransition(this.mLayoutTransition);
    }

    public void setSwipeDownShouldLaunchApp(boolean z) {
        this.mSwipeDownShouldLaunchApp = z;
    }

    public boolean shouldSwipeDownLaunchApp() {
        return this.mSwipeDownShouldLaunchApp;
    }

    public void setIgnoreResetTask(int i) {
        this.mIgnoreResetTaskId = i;
    }

    public void clearIgnoreResetTask(int i) {
        if (this.mIgnoreResetTaskId == i) {
            this.mIgnoreResetTaskId = -1;
        }
    }

    private void addDismissedTaskAnimations(TaskView taskView, long j, PendingAnimation pendingAnimation) {
        pendingAnimation.setFloat(taskView, LauncherAnimUtils.VIEW_ALPHA, 0.0f, Interpolators.clampToProgress(isOnGridBottomRow(taskView) ? Interpolators.ACCEL : Interpolators.FINAL_FRAME, 0.0f, 0.5f));
        FloatProperty<TaskView> secondaryDissmissTranslationProperty = taskView.getSecondaryDissmissTranslationProperty();
        int secondaryDimension = this.mOrientationHandler.getSecondaryDimension(taskView);
        int secondaryTranslationDirectionFactor = this.mOrientationHandler.getSecondaryTranslationDirectionFactor();
        ResourceProvider provider = DynamicResource.provider(this.mActivity);
        pendingAnimation.add(ObjectAnimator.ofFloat(taskView, secondaryDissmissTranslationProperty, new float[]{(float) (secondaryTranslationDirectionFactor * secondaryDimension * 2)}).setDuration(j), Interpolators.LINEAR, new SpringProperty(2).setDampingRatio(provider.getFloat(R.dimen.dismiss_task_trans_y_damping_ratio)).setStiffness(provider.getFloat(R.dimen.dismiss_task_trans_y_stiffness)));
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && this.mEnableDrawingLiveTile && taskView.isRunningTask()) {
            pendingAnimation.addOnFrameCallback(new Runnable(taskView) {
                public final /* synthetic */ TaskView f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RecentsView.this.lambda$addDismissedTaskAnimations$11$RecentsView(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$addDismissedTaskAnimations$11$RecentsView(TaskView taskView) {
        runActionOnRemoteHandles(new Consumer(taskView) {
            public final /* synthetic */ TaskView f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                RecentsView.this.lambda$addDismissedTaskAnimations$10$RecentsView(this.f$1, (RemoteTargetGluer.RemoteTargetHandle) obj);
            }
        });
        redrawLiveTile();
    }

    public /* synthetic */ void lambda$addDismissedTaskAnimations$10$RecentsView(TaskView taskView, RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTaskViewSimulator().taskSecondaryTranslation.value = this.mOrientationHandler.getSecondaryValue(taskView.getTranslationX(), taskView.getTranslationY());
    }

    private void createInitialSplitSelectAnimation(PendingAnimation pendingAnimation) {
        this.mOrientationHandler.getInitialSplitPlaceholderBounds(this.mSplitPlaceholderSize, this.mSplitPlaceholderInset, this.mActivity.getDeviceProfile(), this.mSplitSelectStateController.getActiveSplitStagePosition(), this.mTempRect);
        RectF rectF = new RectF();
        TaskView taskView = this.mSplitHiddenTaskView;
        if (taskView != null) {
            taskView.setVisibility(4);
            FloatingTaskView floatingTaskView = FloatingTaskView.getFloatingTaskView(this.mActivity, this.mSplitHiddenTaskView.getThumbnail(), this.mSplitHiddenTaskView.getThumbnail().getThumbnail(), this.mSplitHiddenTaskView.getIconView().getDrawable(), rectF);
            this.mFirstFloatingTaskView = floatingTaskView;
            floatingTaskView.setAlpha(1.0f);
            this.mFirstFloatingTaskView.addAnimation(pendingAnimation, rectF, this.mTempRect, true, true);
        } else {
            FloatingTaskView floatingTaskView2 = FloatingTaskView.getFloatingTaskView(this.mActivity, this.mSplitSelectSource.view, (Bitmap) null, this.mSplitSelectSource.drawable, rectF);
            this.mFirstFloatingTaskView = floatingTaskView2;
            floatingTaskView2.setAlpha(1.0f);
            this.mFirstFloatingTaskView.addAnimation(pendingAnimation, rectF, this.mTempRect, false, true);
        }
        InteractionJankMonitorWrapper.begin((View) this, 49, "First tile selected");
        pendingAnimation.addEndListener(new Consumer() {
            public final void accept(Object obj) {
                RecentsView.this.lambda$createInitialSplitSelectAnimation$12$RecentsView((Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$createInitialSplitSelectAnimation$12$RecentsView(Boolean bool) {
        if (bool.booleanValue()) {
            this.mSplitToast.show();
            InteractionJankMonitorWrapper.end(49);
            return;
        }
        InteractionJankMonitorWrapper.cancel(49);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0382, code lost:
        if (isSameGridRow(r13, r8) == false) goto L_0x035c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x038d, code lost:
        if (isSameGridRow(r13, r2) != false) goto L_0x0390;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01e5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01f1  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x026b  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0286  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0458  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0462  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.launcher3.anim.PendingAnimation createTaskDismissAnimation(com.android.quickstep.views.TaskView r41, boolean r42, boolean r43, long r44, boolean r46) {
        /*
            r40 = this;
            r15 = r40
            r2 = r41
            r0 = r44
            java.lang.String r3 = "RecentsView"
            java.lang.String r4 = "createTaskDismissAnimation"
            android.util.Log.d(r3, r4)
            com.android.launcher3.anim.PendingAnimation r3 = r15.mPendingAnimation
            if (r3 == 0) goto L_0x001c
            com.android.launcher3.anim.AnimatorPlaybackController r3 = r3.createPlaybackController()
            com.android.launcher3.anim.AnimatorPlaybackController r3 = r3.dispatchOnCancel()
            r3.dispatchOnEnd()
        L_0x001c:
            com.android.launcher3.anim.PendingAnimation r14 = new com.android.launcher3.anim.PendingAnimation
            r14.<init>(r0)
            int r3 = r40.getPageCount()
            if (r3 != 0) goto L_0x0028
            return r14
        L_0x0028:
            boolean r5 = r40.showAsGrid()
            int r8 = r40.getTaskViewCount()
            int r13 = r40.indexOfChild(r41)
            int r4 = r41.getTaskViewId()
            int[] r6 = new int[r3]
            int[] r7 = new int[r3]
            if (r5 == 0) goto L_0x00d3
            android.view.ViewGroup$LayoutParams r9 = r41.getLayoutParams()
            int r9 = r9.width
            int r10 = r15.mPageSpacing
            int r9 = r9 + r10
            float r9 = (float) r9
            int r10 = r15.mFocusedTaskViewId
            if (r4 != r10) goto L_0x004e
            r10 = 1
            goto L_0x004f
        L_0x004e:
            r10 = 0
        L_0x004f:
            if (r10 == 0) goto L_0x00c5
            boolean r18 = r40.isSplitSelectionActive()
            if (r18 != 0) goto L_0x00c5
            com.android.launcher3.util.IntSet r12 = r15.mTopRowIdSet
            int r12 = r12.size()
            if (r12 <= 0) goto L_0x0073
            com.android.launcher3.util.IntSet r12 = r15.mTopRowIdSet
            int r12 = r12.size()
            float r12 = (float) r12
            int r11 = r8 + -1
            float r11 = (float) r11
            r20 = 1073741824(0x40000000, float:2.0)
            float r11 = r11 / r20
            int r11 = (r12 > r11 ? 1 : (r12 == r11 ? 0 : -1))
            if (r11 < 0) goto L_0x0073
            r11 = 1
            goto L_0x0074
        L_0x0073:
            r11 = 0
        L_0x0074:
            r12 = 0
        L_0x0075:
            r20 = r9
            if (r12 >= r8) goto L_0x00a4
            com.android.quickstep.views.TaskView r9 = r15.requireTaskViewAt(r12)
            if (r9 != r2) goto L_0x0084
            r21 = r10
            r22 = r13
            goto L_0x009b
        L_0x0084:
            r21 = r10
            com.android.launcher3.util.IntSet r10 = r15.mTopRowIdSet
            r22 = r13
            int r13 = r9.getTaskViewId()
            boolean r10 = r10.contains(r13)
            if (r11 == 0) goto L_0x0096
            if (r10 != 0) goto L_0x00a9
        L_0x0096:
            if (r11 != 0) goto L_0x009b
            if (r10 != 0) goto L_0x009b
            goto L_0x00a9
        L_0x009b:
            int r12 = r12 + 1
            r9 = r20
            r10 = r21
            r13 = r22
            goto L_0x0075
        L_0x00a4:
            r21 = r10
            r22 = r13
            r9 = 0
        L_0x00a9:
            if (r9 == 0) goto L_0x00bd
            android.view.ViewGroup$LayoutParams r10 = r9.getLayoutParams()
            int r10 = r10.width
            int r12 = r15.mPageSpacing
            int r10 = r10 + r12
            float r10 = (float) r10
            r13 = r9
            r12 = r10
            r19 = r11
            r9 = r20
            r10 = 0
            goto L_0x00fb
        L_0x00bd:
            r13 = r9
            r19 = r11
            r9 = r20
            r10 = 0
            r12 = 0
            goto L_0x00fb
        L_0x00c5:
            r20 = r9
            r21 = r10
            r22 = r13
            r9 = r20
            r10 = 0
            r12 = 0
            r13 = 0
            r19 = 0
            goto L_0x00fb
        L_0x00d3:
            r22 = r13
            com.android.launcher3.PagedView$ComputePageScrollsLogic r9 = SIMPLE_SCROLL_LOGIC
            r11 = 0
            r15.getPageScrolls(r6, r11, r9)
            com.android.quickstep.views.-$$Lambda$RecentsView$zZRSKYeVvvLn12Vm4vrz-ZWR1DQ r9 = new com.android.quickstep.views.-$$Lambda$RecentsView$zZRSKYeVvvLn12Vm4vrz-ZWR1DQ
            r9.<init>()
            r15.getPageScrolls(r7, r11, r9)
            r9 = 1
            if (r3 <= r9) goto L_0x00f3
            r10 = r6[r9]
            r9 = r6[r11]
            int r10 = r10 - r9
            int r9 = java.lang.Math.abs(r10)
            r10 = r9
            r19 = r11
            goto L_0x00f6
        L_0x00f3:
            r10 = r11
            r19 = r10
        L_0x00f6:
            r21 = r19
            r9 = 0
            r12 = 0
            r13 = 0
        L_0x00fb:
            android.content.res.Resources r11 = r40.getResources()
            r23 = r12
            r12 = 2131755346(0x7f100152, float:1.9141569E38)
            java.lang.String r11 = r11.getString(r12)
            r15.announceForAccessibility(r11)
            boolean r11 = r40.isClearAllHidden()
            ACTIVITY_TYPE r12 = r15.mActivity
            com.android.launcher3.DeviceProfile r12 = r12.getDeviceProfile()
            boolean r12 = r12.isLandscape
            if (r12 == 0) goto L_0x0121
            boolean r12 = r40.isSplitSelectionActive()
            if (r12 == 0) goto L_0x0121
            r12 = 1
            goto L_0x0122
        L_0x0121:
            r12 = 0
        L_0x0122:
            boolean r24 = r40.isSplitPlaceholderFirstInGrid()
            boolean r25 = r40.isSplitPlaceholderLastInGrid()
            if (r5 == 0) goto L_0x0137
            com.android.quickstep.views.TaskView r16 = r40.getLastGridTaskView()
            r26 = r9
            r9 = r16
            r16 = r13
            goto L_0x013c
        L_0x0137:
            r26 = r9
            r16 = r13
            r9 = 0
        L_0x013c:
            int r13 = r15.mCurrentPage
            int r13 = r15.getScrollForPage(r13)
            r27 = r6
            int r6 = r15.indexOfChild(r9)
            int r6 = r15.getScrollForPage(r6)
            if (r13 != r6) goto L_0x0151
            r28 = 1
            goto L_0x0153
        L_0x0151:
            r28 = 0
        L_0x0153:
            if (r9 == 0) goto L_0x0277
            boolean r9 = r9.isVisibleToUser()
            if (r9 == 0) goto L_0x0277
            com.android.launcher3.util.IntSet r9 = r15.mTopRowIdSet
            int r9 = r9.size()
            com.android.launcher3.util.IntSet r13 = r15.mTopRowIdSet
            int r13 = r13.size()
            int r13 = r8 - r13
            r18 = 1
            int r13 = r13 + -1
            if (r9 <= r13) goto L_0x0172
            r29 = 1
            goto L_0x0174
        L_0x0172:
            r29 = 0
        L_0x0174:
            if (r13 <= r9) goto L_0x0178
            r9 = 1
            goto L_0x0179
        L_0x0178:
            r9 = 0
        L_0x0179:
            com.android.launcher3.util.IntSet r13 = r15.mTopRowIdSet
            boolean r13 = r13.contains(r4)
            if (r13 != 0) goto L_0x0186
            if (r21 != 0) goto L_0x0186
            r30 = 1
            goto L_0x0188
        L_0x0186:
            r30 = 0
        L_0x0188:
            if (r29 == 0) goto L_0x018c
            if (r13 != 0) goto L_0x0190
        L_0x018c:
            if (r9 == 0) goto L_0x0195
            if (r30 == 0) goto L_0x0195
        L_0x0190:
            r9 = r26
        L_0x0192:
            r17 = 0
            goto L_0x01a2
        L_0x0195:
            if (r29 == 0) goto L_0x0199
            if (r19 != 0) goto L_0x019d
        L_0x0199:
            if (r9 == 0) goto L_0x01a0
            if (r19 != 0) goto L_0x01a0
        L_0x019d:
            r9 = r23
            goto L_0x0192
        L_0x01a0:
            r9 = 0
            goto L_0x0192
        L_0x01a2:
            int r13 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r13 <= 0) goto L_0x01be
            r13 = 2
            if (r8 <= r13) goto L_0x01b5
            boolean r13 = r15.mIsRtl
            if (r13 == 0) goto L_0x01ae
            float r9 = -r9
        L_0x01ae:
            float r9 = r9 + r17
            r13 = r9
            if (r11 == 0) goto L_0x01c0
            r9 = 1
            goto L_0x01c1
        L_0x01b5:
            int r9 = r15.getSnapToFocusedTaskScrollDiff(r11)
            float r9 = (float) r9
            float r9 = r9 + r17
            r13 = r9
            goto L_0x01c0
        L_0x01be:
            r13 = r17
        L_0x01c0:
            r9 = 0
        L_0x01c1:
            com.android.quickstep.views.ClearAllButton r6 = r15.mClearAllButton
            float r6 = r6.getAlpha()
            int r6 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r6 == 0) goto L_0x01ce
            if (r12 == 0) goto L_0x01ce
            r9 = 1
        L_0x01ce:
            if (r9 == 0) goto L_0x01e5
            int r6 = r40.getSnapToLastTaskScrollDiff()
            float r6 = (float) r6
            float r13 = r13 + r6
            if (r25 == 0) goto L_0x01ec
            boolean r6 = r15.mIsRtl
            if (r6 == 0) goto L_0x01df
            int r6 = r15.mSplitPlaceholderSize
            goto L_0x01e2
        L_0x01df:
            int r6 = r15.mSplitPlaceholderSize
            int r6 = -r6
        L_0x01e2:
            float r6 = (float) r6
            float r13 = r13 + r6
            goto L_0x01ec
        L_0x01e5:
            if (r12 == 0) goto L_0x01ec
            if (r28 == 0) goto L_0x01ec
            r6 = 0
            r9 = 1
            goto L_0x01ed
        L_0x01ec:
            r6 = 0
        L_0x01ed:
            int r12 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r12 == 0) goto L_0x026b
            r6 = 1020054733(0x3ccccccd, float:0.025)
            int r12 = r8 + -1
            float r12 = (float) r12
            float r12 = r12 * r6
            r6 = 1061158912(0x3f400000, float:0.75)
            float r12 = r12 + r6
            r31 = r9
            r9 = 1065353216(0x3f800000, float:1.0)
            float r12 = com.android.launcher3.Utilities.boundToRange((float) r12, (float) r6, (float) r9)
            r6 = 0
        L_0x0204:
            if (r6 >= r8) goto L_0x024c
            com.android.quickstep.views.TaskView r9 = r15.requireTaskViewAt(r6)
            r33 = r4
            android.util.FloatProperty<com.android.quickstep.views.TaskView> r4 = com.android.quickstep.views.TaskView.GRID_END_TRANSLATION_X
            r34 = r7
            android.view.animation.Interpolator r7 = com.android.launcher3.anim.Interpolators.LINEAR
            r35 = r10
            r10 = 1065353216(0x3f800000, float:1.0)
            android.view.animation.Interpolator r7 = com.android.launcher3.anim.Interpolators.clampToProgress((android.view.animation.Interpolator) r7, (float) r12, (float) r10)
            r14.setFloat(r9, r4, r13, r7)
            r4 = 1020054733(0x3ccccccd, float:0.025)
            float r12 = r12 - r4
            r7 = 1061158912(0x3f400000, float:0.75)
            float r12 = com.android.launcher3.Utilities.boundToRange((float) r12, (float) r7, (float) r10)
            com.android.launcher3.config.FeatureFlags$BooleanFlag r10 = com.android.launcher3.config.FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE
            boolean r10 = r10.get()
            if (r10 == 0) goto L_0x0241
            boolean r10 = r15.mEnableDrawingLiveTile
            if (r10 == 0) goto L_0x0241
            boolean r10 = r9.isRunningTask()
            if (r10 == 0) goto L_0x0241
            com.android.quickstep.views.-$$Lambda$RecentsView$O_BrVeoxLkrZZo3VJ59xk2lXk8M r10 = new com.android.quickstep.views.-$$Lambda$RecentsView$O_BrVeoxLkrZZo3VJ59xk2lXk8M
            r10.<init>(r9)
            r14.addOnFrameCallback(r10)
        L_0x0241:
            int r6 = r6 + 1
            r4 = r33
            r7 = r34
            r10 = r35
            r9 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0204
        L_0x024c:
            r33 = r4
            r34 = r7
            r35 = r10
            if (r11 == 0) goto L_0x0266
            com.android.quickstep.views.ClearAllButton r4 = r15.mClearAllButton
            android.util.FloatProperty<com.android.quickstep.views.ClearAllButton> r6 = com.android.quickstep.views.ClearAllButton.DISMISS_ALPHA
            android.view.animation.Interpolator r7 = com.android.launcher3.anim.Interpolators.LINEAR
            r9 = 0
            r14.setFloat(r4, r6, r9, r7)
            com.android.quickstep.views.RecentsView$15 r4 = new com.android.quickstep.views.RecentsView$15
            r4.<init>()
            r14.addListener(r4)
        L_0x0266:
            r9 = r12
            r7 = r31
            r6 = 1
            goto L_0x0281
        L_0x026b:
            r33 = r4
            r34 = r7
            r31 = r9
            r35 = r10
            r7 = r31
            r6 = 0
            goto L_0x027f
        L_0x0277:
            r33 = r4
            r34 = r7
            r35 = r10
            r6 = 0
            r7 = 0
        L_0x027f:
            r9 = 1065353216(0x3f800000, float:1.0)
        L_0x0281:
            r4 = 0
            r10 = 0
            r12 = 0
        L_0x0284:
            if (r10 >= r3) goto L_0x044a
            android.view.View r13 = r15.getChildAt(r10)
            if (r13 != r2) goto L_0x02a9
            if (r42 == 0) goto L_0x0297
            if (r46 == 0) goto L_0x0294
            r15.createInitialSplitSelectAnimation(r14)
            goto L_0x0297
        L_0x0294:
            r15.addDismissedTaskAnimations(r2, r0, r14)
        L_0x0297:
            r29 = r6
            r37 = r7
            r32 = r11
            r11 = r22
            r36 = r35
            r18 = 1
            r22 = r3
            r35 = r8
            goto L_0x035a
        L_0x02a9:
            r30 = 1028443341(0x3d4ccccd, float:0.05)
            r31 = 1057803469(0x3f0ccccd, float:0.55)
            if (r5 != 0) goto L_0x0360
            boolean r0 = r15.mIsRtl
            if (r0 == 0) goto L_0x02b8
            r0 = r35
            goto L_0x02b9
        L_0x02b8:
            r0 = 0
        L_0x02b9:
            int r1 = r15.mCurrentPage
            r32 = r11
            r11 = r22
            if (r1 != r11) goto L_0x02db
            int r1 = r8 + -1
            r22 = r3
            int r3 = r15.mCurrentPage
            if (r3 != r1) goto L_0x02d6
            boolean r1 = r15.mIsRtl
            if (r1 == 0) goto L_0x02d1
            r1 = r35
            int r3 = -r1
            goto L_0x02d4
        L_0x02d1:
            r1 = r35
            r3 = r1
        L_0x02d4:
            int r0 = r0 + r3
            goto L_0x02d8
        L_0x02d6:
            r1 = r35
        L_0x02d8:
            r18 = 1
            goto L_0x02ef
        L_0x02db:
            r22 = r3
            r1 = r35
            int r3 = r15.mCurrentPage
            r18 = 1
            int r3 = r3 + -1
            if (r11 != r3) goto L_0x02ef
            boolean r3 = r15.mIsRtl
            if (r3 == 0) goto L_0x02ed
            int r3 = -r1
            goto L_0x02ee
        L_0x02ed:
            r3 = r1
        L_0x02ee:
            int r0 = r0 + r3
        L_0x02ef:
            r3 = r34[r10]
            r35 = r27[r10]
            int r3 = r3 - r35
            int r3 = r3 + r0
            if (r3 == 0) goto L_0x0350
            boolean r0 = r13 instanceof com.android.quickstep.views.TaskView
            if (r0 == 0) goto L_0x0304
            r4 = r13
            com.android.quickstep.views.TaskView r4 = (com.android.quickstep.views.TaskView) r4
            android.util.FloatProperty r4 = r4.getPrimaryDismissTranslationProperty()
            goto L_0x030a
        L_0x0304:
            com.android.launcher3.touch.PagedOrientationHandler r4 = r15.mOrientationHandler
            android.util.FloatProperty r4 = r4.getPrimaryViewTranslate()
        L_0x030a:
            int r35 = r10 - r11
            r36 = r1
            int r1 = java.lang.Math.abs(r35)
            float r1 = (float) r1
            float r1 = r1 * r30
            float r3 = (float) r3
            r35 = r8
            android.view.animation.Interpolator r8 = com.android.launcher3.anim.Interpolators.LINEAR
            float r1 = r1 + r31
            r29 = r6
            r37 = r7
            r6 = 0
            r7 = 1065353216(0x3f800000, float:1.0)
            float r1 = com.android.launcher3.Utilities.boundToRange((float) r1, (float) r6, (float) r7)
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.clampToProgress((android.view.animation.Interpolator) r8, (float) r1, (float) r7)
            r14.setFloat(r13, r4, r3, r1)
            com.android.launcher3.config.FeatureFlags$BooleanFlag r1 = com.android.launcher3.config.FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE
            boolean r1 = r1.get()
            if (r1 == 0) goto L_0x034d
            boolean r1 = r15.mEnableDrawingLiveTile
            if (r1 == 0) goto L_0x034d
            if (r0 == 0) goto L_0x034d
            r0 = r13
            com.android.quickstep.views.TaskView r0 = (com.android.quickstep.views.TaskView) r0
            boolean r0 = r0.isRunningTask()
            if (r0 == 0) goto L_0x034d
            com.android.quickstep.views.-$$Lambda$RecentsView$-OrxTeW_u2fsC5-HVVF_kDJTrfs r0 = new com.android.quickstep.views.-$$Lambda$RecentsView$-OrxTeW_u2fsC5-HVVF_kDJTrfs
            r0.<init>(r13)
            r14.addOnFrameCallback(r0)
        L_0x034d:
            r4 = r18
            goto L_0x035a
        L_0x0350:
            r36 = r1
            r29 = r6
            r37 = r7
            r35 = r8
            r7 = 1065353216(0x3f800000, float:1.0)
        L_0x035a:
            r8 = r16
        L_0x035c:
            r3 = r26
            goto L_0x0432
        L_0x0360:
            r29 = r6
            r37 = r7
            r32 = r11
            r11 = r22
            r36 = r35
            r7 = 1065353216(0x3f800000, float:1.0)
            r18 = 1
            r22 = r3
            r35 = r8
            boolean r0 = r13 instanceof com.android.quickstep.views.TaskView
            if (r0 == 0) goto L_0x035a
            com.android.quickstep.views.TaskView r13 = (com.android.quickstep.views.TaskView) r13
            if (r21 == 0) goto L_0x0385
            r8 = r16
            if (r16 == 0) goto L_0x0390
            boolean r0 = r15.isSameGridRow(r13, r8)
            if (r0 != 0) goto L_0x0390
            goto L_0x035c
        L_0x0385:
            r8 = r16
            if (r10 < r11) goto L_0x035c
            boolean r0 = r15.isSameGridRow(r13, r2)
            if (r0 != 0) goto L_0x0390
            goto L_0x035c
        L_0x0390:
            int r12 = r12 + 1
            float r0 = (float) r12
            float r0 = r0 * r30
            float r0 = r0 + r31
            r1 = 0
            float r0 = com.android.launcher3.Utilities.boundToRange((float) r0, (float) r1, (float) r9)
            if (r13 != r8) goto L_0x03f0
            int r1 = r15.mTaskWidth
            float r1 = (float) r1
            android.graphics.Rect r3 = r15.mLastComputedGridTaskSize
            int r3 = r3.width()
            float r3 = (float) r3
            float r1 = r1 / r3
            android.util.FloatProperty<com.android.quickstep.views.TaskView> r3 = com.android.quickstep.views.TaskView.SNAPSHOT_SCALE
            android.view.animation.Interpolator r6 = com.android.launcher3.anim.Interpolators.LINEAR
            android.view.animation.Interpolator r6 = com.android.launcher3.anim.Interpolators.clampToProgress((android.view.animation.Interpolator) r6, (float) r0, (float) r9)
            r14.setFloat(r13, r3, r1, r6)
            android.util.FloatProperty r1 = r13.getPrimaryDismissTranslationProperty()
            boolean r3 = r15.mIsRtl
            if (r3 == 0) goto L_0x03c0
            r3 = r26
            r6 = r3
            goto L_0x03c3
        L_0x03c0:
            r3 = r26
            float r6 = -r3
        L_0x03c3:
            android.view.animation.Interpolator r7 = com.android.launcher3.anim.Interpolators.LINEAR
            android.view.animation.Interpolator r7 = com.android.launcher3.anim.Interpolators.clampToProgress((android.view.animation.Interpolator) r7, (float) r0, (float) r9)
            r14.setFloat(r13, r1, r6, r7)
            float r1 = r15.mTaskGridVerticalDiff
            float r1 = -r1
            if (r19 != 0) goto L_0x03d4
            float r6 = r15.mTopBottomRowHeightDiff
            float r1 = r1 - r6
        L_0x03d4:
            android.util.FloatProperty r6 = r13.getSecondaryDissmissTranslationProperty()
            android.view.animation.Interpolator r7 = com.android.launcher3.anim.Interpolators.LINEAR
            android.view.animation.Interpolator r0 = com.android.launcher3.anim.Interpolators.clampToProgress((android.view.animation.Interpolator) r7, (float) r0, (float) r9)
            r14.setFloat(r13, r6, r1, r0)
            android.util.FloatProperty<com.android.quickstep.views.TaskView> r0 = com.android.quickstep.views.TaskView.FOCUS_TRANSITION
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.LINEAR
            r6 = 1056964608(0x3f000000, float:0.5)
            r7 = 0
            android.view.animation.Interpolator r1 = com.android.launcher3.anim.Interpolators.clampToProgress((android.view.animation.Interpolator) r1, (float) r7, (float) r6)
            r14.setFloat(r13, r0, r7, r1)
            goto L_0x0432
        L_0x03f0:
            r3 = r26
            r7 = 0
            if (r8 == 0) goto L_0x03f8
            r1 = r23
            goto L_0x03f9
        L_0x03f8:
            r1 = r3
        L_0x03f9:
            if (r21 == 0) goto L_0x041f
            if (r8 != 0) goto L_0x041f
            int r6 = r15.getScrollForPage(r11)
            com.android.launcher3.touch.PagedOrientationHandler r7 = r15.mOrientationHandler
            int r7 = r7.getPrimaryScroll(r15)
            int r7 = r7 - r6
            boolean r6 = r15.mIsRtl
            if (r6 == 0) goto L_0x040e
            float r6 = (float) r7
            goto L_0x0410
        L_0x040e:
            int r6 = -r7
            float r6 = (float) r6
        L_0x0410:
            float r1 = r1 + r6
            if (r24 == 0) goto L_0x041f
            boolean r6 = r15.mIsRtl
            if (r6 == 0) goto L_0x041b
            int r6 = r15.mSplitPlaceholderSize
            int r6 = -r6
            goto L_0x041d
        L_0x041b:
            int r6 = r15.mSplitPlaceholderSize
        L_0x041d:
            float r6 = (float) r6
            float r1 = r1 + r6
        L_0x041f:
            android.util.FloatProperty r6 = r13.getPrimaryDismissTranslationProperty()
            boolean r7 = r15.mIsRtl
            if (r7 == 0) goto L_0x0428
            goto L_0x0429
        L_0x0428:
            float r1 = -r1
        L_0x0429:
            android.view.animation.Interpolator r7 = com.android.launcher3.anim.Interpolators.LINEAR
            android.view.animation.Interpolator r0 = com.android.launcher3.anim.Interpolators.clampToProgress((android.view.animation.Interpolator) r7, (float) r0, (float) r9)
            r14.setFloat(r13, r6, r1, r0)
        L_0x0432:
            int r10 = r10 + 1
            r0 = r44
            r26 = r3
            r16 = r8
            r3 = r22
            r6 = r29
            r8 = r35
            r35 = r36
            r7 = r37
            r22 = r11
            r11 = r32
            goto L_0x0284
        L_0x044a:
            r29 = r6
            r37 = r7
            r35 = r8
            r32 = r11
            r8 = r16
            r11 = r22
            if (r4 == 0) goto L_0x0460
            com.android.quickstep.views.-$$Lambda$CvnoLYYyGI-oRJ631yBnR0gTTn0 r0 = new com.android.quickstep.views.-$$Lambda$CvnoLYYyGI-oRJ631yBnR0gTTn0
            r0.<init>()
            r14.addOnFrameCallback(r0)
        L_0x0460:
            if (r42 == 0) goto L_0x0468
            r0 = 1036831949(0x3dcccccd, float:0.1)
            r2.setTranslationZ(r0)
        L_0x0468:
            r15.mPendingAnimation = r14
            com.android.quickstep.views.RecentsView$16 r13 = new com.android.quickstep.views.RecentsView$16
            r0 = r13
            r1 = r40
            r2 = r41
            r3 = r43
            r4 = r33
            r6 = r29
            r7 = r37
            r16 = r8
            r8 = r35
            r9 = r32
            r10 = r16
            r16 = r11
            r11 = r24
            r12 = r25
            r38 = r13
            r13 = r16
            r39 = r14
            r14 = r21
            r15 = r28
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            r1 = r38
            r0 = r39
            r0.addEndListener(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.RecentsView.createTaskDismissAnimation(com.android.quickstep.views.TaskView, boolean, boolean, long, boolean):com.android.launcher3.anim.PendingAnimation");
    }

    static /* synthetic */ boolean lambda$createTaskDismissAnimation$13(TaskView taskView, View view) {
        return (view.getVisibility() == 8 || view == taskView) ? false : true;
    }

    public /* synthetic */ void lambda$createTaskDismissAnimation$15$RecentsView(TaskView taskView) {
        runActionOnRemoteHandles(new Consumer() {
            public final void accept(Object obj) {
                ((RemoteTargetGluer.RemoteTargetHandle) obj).getTaskViewSimulator().taskPrimaryTranslation.value = ((Float) TaskView.GRID_END_TRANSLATION_X.get(TaskView.this)).floatValue();
            }
        });
        redrawLiveTile();
    }

    public /* synthetic */ void lambda$createTaskDismissAnimation$17$RecentsView(View view) {
        runActionOnRemoteHandles(new Consumer(view) {
            public final /* synthetic */ View f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                RecentsView.this.lambda$createTaskDismissAnimation$16$RecentsView(this.f$1, (RemoteTargetGluer.RemoteTargetHandle) obj);
            }
        });
        redrawLiveTile();
    }

    public /* synthetic */ void lambda$createTaskDismissAnimation$16$RecentsView(View view, RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTaskViewSimulator().taskPrimaryTranslation.value = this.mOrientationHandler.getPrimaryValue(view.getTranslationX(), view.getTranslationY());
    }

    /* access modifiers changed from: private */
    public void updateCurrentTaskActionsVisibility() {
        boolean z = getCurrentPageTaskView() instanceof GroupedTaskView;
        this.mActionsView.updateHiddenFlags(8, z);
        if (!z) {
            OverviewActionsView overviewActionsView = this.mActionsView;
            boolean z2 = true;
            if (!this.mActivity.getDeviceProfile().isTablet || getTaskViewCount() <= 1) {
                z2 = false;
            }
            overviewActionsView.setSplitButtonVisible(z2);
        }
    }

    /* access modifiers changed from: private */
    public IntArray getTopRowIdArray() {
        if (this.mTopRowIdSet.isEmpty()) {
            return new IntArray(0);
        }
        IntArray intArray = new IntArray(this.mTopRowIdSet.size());
        int taskViewCount = getTaskViewCount();
        for (int i = 0; i < taskViewCount; i++) {
            int taskViewId = requireTaskViewAt(i).getTaskViewId();
            if (this.mTopRowIdSet.contains(taskViewId)) {
                intArray.add(taskViewId);
            }
        }
        return intArray;
    }

    /* access modifiers changed from: private */
    public IntArray getBottomRowIdArray() {
        int bottomRowTaskCountForTablet = getBottomRowTaskCountForTablet();
        if (bottomRowTaskCountForTablet <= 0) {
            return new IntArray(0);
        }
        IntArray intArray = new IntArray(bottomRowTaskCountForTablet);
        int taskViewCount = getTaskViewCount();
        for (int i = 0; i < taskViewCount; i++) {
            int taskViewId = requireTaskViewAt(i).getTaskViewId();
            if (!this.mTopRowIdSet.contains(taskViewId) && taskViewId != this.mFocusedTaskViewId) {
                intArray.add(taskViewId);
            }
        }
        return intArray;
    }

    /* access modifiers changed from: private */
    public int getHighestVisibleTaskIndex() {
        if (this.mTopRowIdSet.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        IntArray topRowIdArray = getTopRowIdArray();
        IntArray bottomRowIdArray = getBottomRowIdArray();
        int min = Math.min(bottomRowIdArray.size(), topRowIdArray.size());
        int i = Integer.MAX_VALUE;
        for (int i2 = 0; i2 < min; i2++) {
            TaskView taskViewFromTaskViewId = getTaskViewFromTaskViewId(topRowIdArray.get(i2));
            if (isTaskViewVisible(taskViewFromTaskViewId)) {
                i = Math.max(indexOfChild(taskViewFromTaskViewId), indexOfChild(getTaskViewFromTaskViewId(bottomRowIdArray.get(i2))));
            } else if (i < Integer.MAX_VALUE) {
                break;
            }
        }
        return i;
    }

    /* access modifiers changed from: private */
    public void removeTaskInternal(TaskView taskView, int i) {
        if (!(taskView == null || taskView.getTask() == null)) {
            String packageName = taskView.getTask().key.getPackageName();
            if ("com.szchoiceway.btsuite".equals(packageName)) {
                this.mContext.sendBroadcast(new Intent("ZXW_ACTION_KILL_BACKGROUND_BT"));
            } else {
                Intent intent = new Intent("ZXW_LAUNCHER_ACTION_KILL_PROGRESS");
                intent.putExtra("packageName", packageName);
                this.mContext.sendBroadcast(intent);
            }
        }
        Executors.UI_HELPER_EXECUTOR.getHandler().postDelayed(new Runnable(i, taskView) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ TaskView f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RecentsView.this.lambda$removeTaskInternal$18$RecentsView(this.f$1, this.f$2);
            }
        }, 300);
    }

    public /* synthetic */ void lambda$removeTaskInternal$18$RecentsView(int i, TaskView taskView) {
        int[] taskIdsForTaskViewId = getTaskIdsForTaskViewId(i);
        int i2 = taskIdsForTaskViewId[0];
        int i3 = taskIdsForTaskViewId[1];
        Log.d(TAG, "removeTaskInternal primaryTaskId = " + i2 + ", secondaryTaskId = " + i3 + ", dismissedTaskViewId = " + i);
        if (i2 > -1) {
            ActivityManagerWrapper.getInstance().removeTask(i2);
        }
        if (i3 != -1) {
            ActivityManagerWrapper.getInstance().removeTask(i3);
        }
        if (taskView != null && taskView.getTask() != null) {
            Log.d(TAG, "dismissedTaskView key id = " + taskView.getTask().key.id);
            ActivityManagerWrapper.getInstance().removeTask(taskView.getTask().key.id);
            String packageName = taskView.getTask().key.getPackageName();
            Log.i(TAG, "removeTaskInternal " + packageName);
            List<String> hicarPackageNames = getHicarPackageNames();
            if (hicarPackageNames.contains(packageName)) {
                for (String next : hicarPackageNames) {
                    Log.d(TAG, "killProcess packageName = " + next);
                    killProcess(next);
                    this.mActivityManager.forceStopPackage(next);
                }
                return;
            }
            Log.d(TAG, "killProcess packageName = " + packageName);
            if (!"com.ivicar.avm".equals(packageName)) {
                killProcess(packageName);
                this.mActivityManager.forceStopPackage(packageName);
            }
        }
    }

    public boolean shouldShiftThumbnailsForSplitSelect() {
        return !this.mActivity.getDeviceProfile().isTablet || !this.mActivity.getDeviceProfile().isLandscape;
    }

    /* access modifiers changed from: protected */
    public void onDismissAnimationEnds() {
        AccessibilityManagerCompat.sendDismissAnimationEndsEventToTest(getContext());
    }

    public PendingAnimation createAllTasksDismissAnimation(long j) {
        for (int taskViewCount = getTaskViewCount() - 1; taskViewCount >= 0; taskViewCount--) {
            TaskView taskViewAt = getTaskViewAt(taskViewCount);
            if (!(taskViewAt == null || taskViewAt.getTask() == null)) {
                String packageName = taskViewAt.getTask().key.getPackageName();
                if ("com.szchoiceway.btsuite".equals(packageName)) {
                    this.mContext.sendBroadcast(new Intent("ZXW_ACTION_KILL_BACKGROUND_BT"));
                } else {
                    Intent intent = new Intent("ZXW_LAUNCHER_ACTION_KILL_PROGRESS");
                    intent.putExtra("packageName", packageName);
                    this.mContext.sendBroadcast(intent);
                }
            }
        }
        PendingAnimation pendingAnimation = new PendingAnimation(j);
        int taskViewCount2 = getTaskViewCount();
        for (int i = 0; i < taskViewCount2; i++) {
            addDismissedTaskAnimations(getTaskViewAt(i), j, pendingAnimation);
        }
        this.mPendingAnimation = pendingAnimation;
        pendingAnimation.addEndListener(new Consumer() {
            public final void accept(Object obj) {
                RecentsView.this.lambda$createAllTasksDismissAnimation$20$RecentsView((Boolean) obj);
            }
        });
        return pendingAnimation;
    }

    public /* synthetic */ void lambda$createAllTasksDismissAnimation$20$RecentsView(Boolean bool) {
        if (bool.booleanValue()) {
            finishRecentsAnimation(true, false, new Runnable() {
                public final void run() {
                    RecentsView.this.lambda$createAllTasksDismissAnimation$19$RecentsView();
                }
            });
        }
        this.mPendingAnimation = null;
    }

    public /* synthetic */ void lambda$createAllTasksDismissAnimation$19$RecentsView() {
        Handler handler = Executors.UI_HELPER_EXECUTOR.getHandler();
        ActivityManagerWrapper instance = ActivityManagerWrapper.getInstance();
        Objects.requireNonNull(instance);
        handler.postDelayed(new Runnable() {
            public final void run() {
                ActivityManagerWrapper.this.removeAllRecentTasks();
            }
        }, 300);
        for (int taskViewCount = getTaskViewCount() - 1; taskViewCount >= 0; taskViewCount--) {
            TaskView taskViewAt = getTaskViewAt(taskViewCount);
            if (!(taskViewAt == null || taskViewAt.getTask() == null)) {
                String packageName = taskViewAt.getTask().key.getPackageName();
                Log.i(TAG, "createAllTasksDismissAnimation " + packageName + ", id = ");
                List<String> hicarPackageNames = getHicarPackageNames();
                if (hicarPackageNames.contains(packageName)) {
                    for (String next : hicarPackageNames) {
                        Log.d(TAG, "killprogress packageName = " + packageName);
                        killProcess(next);
                        this.mActivityManager.forceStopPackage(next);
                    }
                } else {
                    Log.d(TAG, "killprogress packageName = " + packageName);
                    if (!"com.ivicar.avm".equals(packageName)) {
                        killProcess(packageName);
                        this.mActivityManager.forceStopPackage(packageName);
                    }
                }
            }
        }
        removeTasksViewsAndClearAllButton();
        startHome();
    }

    private boolean snapToPageRelative(int i, int i2, boolean z) {
        if (i == 0) {
            return false;
        }
        int nextPage = getNextPage() + i2;
        if (!z && (nextPage < 0 || nextPage >= i)) {
            return false;
        }
        snapToPage((nextPage + i) % i);
        getChildAt(getNextPage()).requestFocus();
        return true;
    }

    private void runDismissAnimation(PendingAnimation pendingAnimation) {
        AnimatorPlaybackController createPlaybackController = pendingAnimation.createPlaybackController();
        createPlaybackController.dispatchOnStart();
        createPlaybackController.getAnimationPlayer().setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        createPlaybackController.start();
    }

    /* access modifiers changed from: private */
    public void dismissTask(int i) {
        TaskView taskViewByTaskId = getTaskViewByTaskId(i);
        if (taskViewByTaskId != null) {
            dismissTask(taskViewByTaskId, true, false);
        }
    }

    public void dismissTask(TaskView taskView, boolean z, boolean z2) {
        runDismissAnimation(createTaskDismissAnimation(taskView, z, z2, 300, false));
    }

    /* access modifiers changed from: private */
    public void dismissAllTasks(View view) {
        runDismissAnimation(createAllTasksDismissAnimation(300));
        this.mActivity.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_TASK_CLEAR_ALL);
    }

    private void dismissCurrentTask() {
        TaskView nextPageTaskView = getNextPageTaskView();
        if (nextPageTaskView != null) {
            dismissTask(nextPageTaskView, true, true);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            int i = -1;
            if (keyCode == 21) {
                int pageCount = getPageCount();
                if (this.mIsRtl) {
                    i = 1;
                }
                return snapToPageRelative(pageCount, i, false);
            } else if (keyCode == 22) {
                int pageCount2 = getPageCount();
                if (!this.mIsRtl) {
                    i = 1;
                }
                return snapToPageRelative(pageCount2, i, false);
            } else if (keyCode == 61) {
                int taskViewCount = getTaskViewCount();
                if (!keyEvent.isShiftPressed()) {
                    i = 1;
                }
                return snapToPageRelative(taskViewCount, i, keyEvent.isAltPressed());
            } else if (keyCode == 67 || keyCode == 112) {
                dismissCurrentTask();
                return true;
            } else if (keyCode == 158 && keyEvent.isAltPressed()) {
                dismissCurrentTask();
                return true;
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (z && getChildCount() > 0) {
            if (i != 1) {
                if (i == 2) {
                    setCurrentPage(0);
                    return;
                } else if (!(i == 17 || i == 66)) {
                    return;
                }
            }
            setCurrentPage(getChildCount() - 1);
        }
    }

    public float getContentAlpha() {
        return this.mContentAlpha;
    }

    public void setContentAlpha(float f) {
        if (f != this.mContentAlpha) {
            float boundToRange = Utilities.boundToRange(f, 0.0f, 1.0f);
            this.mContentAlpha = boundToRange;
            int i = getTaskIdsForRunningTaskView()[0];
            for (int taskViewCount = getTaskViewCount() - 1; taskViewCount >= 0; taskViewCount--) {
                TaskView requireTaskViewAt = requireTaskViewAt(taskViewCount);
                int[] taskIds = requireTaskViewAt.getTaskIds();
                if (!this.mRunningTaskTileHidden || !(taskIds[0] == i || taskIds[1] == i)) {
                    requireTaskViewAt.setStableAlpha(boundToRange);
                }
            }
            this.mClearAllButton.setContentAlpha(this.mContentAlpha);
            int round = Math.round(255.0f * boundToRange);
            this.mEmptyMessagePaint.setAlpha(round);
            this.mEmptyIcon.setAlpha(round);
            this.mActionsView.getContentAlpha().setValue(this.mContentAlpha);
            if (boundToRange > 0.0f) {
                setVisibility(0);
            } else if (!this.mFreezeViewVisibility) {
                setVisibility(4);
            }
        }
    }

    public void setFreezeViewVisibility(boolean z) {
        if (this.mFreezeViewVisibility != z) {
            this.mFreezeViewVisibility = z;
            if (!z) {
                setVisibility(this.mContentAlpha > 0.0f ? 0 : 4);
            }
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        OverviewActionsView overviewActionsView = this.mActionsView;
        if (overviewActionsView != null) {
            overviewActionsView.updateHiddenFlags(4, i != 0);
            if (i != 0) {
                this.mActionsView.updateDisabledFlags(1, false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateRecentsRotation();
        onOrientationChanged();
    }

    public void updateRecentsRotation() {
        this.mOrientationState.setRecentsRotation(this.mActivity.getDisplay().getRotation());
    }

    public void setLayoutRotation(int i, int i2) {
        if (this.mOrientationState.update(i, i2)) {
            updateOrientationHandler();
        }
    }

    public RecentsOrientedState getPagedViewOrientedState() {
        return this.mOrientationState;
    }

    public PagedOrientationHandler getPagedOrientationHandler() {
        return this.mOrientationHandler;
    }

    public TaskView getNextTaskView() {
        return getTaskViewAt(getRunningTaskIndex() + 1);
    }

    public TaskView getCurrentPageTaskView() {
        return getTaskViewAt(getCurrentPage());
    }

    public TaskView getNextPageTaskView() {
        return getTaskViewAt(getNextPage());
    }

    public TaskView getTaskViewNearestToCenterOfScreen() {
        return getTaskViewAt(getPageNearestToCenterOfScreen());
    }

    public TaskView getTaskViewAt(int i) {
        View childAt = getChildAt(i);
        if (childAt instanceof TaskView) {
            return (TaskView) childAt;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public TaskView requireTaskViewAt(int i) {
        return (TaskView) Objects.requireNonNull(getTaskViewAt(i));
    }

    public void setOnEmptyMessageUpdatedListener(OnEmptyMessageUpdatedListener onEmptyMessageUpdatedListener) {
        this.mOnEmptyMessageUpdatedListener = onEmptyMessageUpdatedListener;
    }

    public void updateEmptyMessage() {
        boolean z = true;
        boolean z2 = getTaskViewCount() == 0;
        if (this.mLastMeasureSize.x == getWidth() && this.mLastMeasureSize.y == getHeight()) {
            z = false;
        }
        if (z2 != this.mShowEmptyMessage || z) {
            setContentDescription(z2 ? this.mEmptyMessage : "");
            this.mShowEmptyMessage = z2;
            updateEmptyStateUi(z);
            invalidate();
            OnEmptyMessageUpdatedListener onEmptyMessageUpdatedListener = this.mOnEmptyMessageUpdatedListener;
            if (onEmptyMessageUpdatedListener != null) {
                onEmptyMessageUpdatedListener.onEmptyMessageUpdated(this.mShowEmptyMessage);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mOverviewStateEnabled || this.mFirstLayout) {
            this.mShowAsGridLastOnLayout = showAsGrid();
            super.onLayout(z, i, i2, i3, i4);
            updateEmptyStateUi(z);
            getTaskSize(this.mTempRect);
            updatePivots();
            setTaskModalness(this.mTaskModalness);
            this.mLastComputedTaskStartPushOutDistance = null;
            this.mLastComputedTaskEndPushOutDistance = null;
            updatePageOffsets();
            runActionOnRemoteHandles(new Consumer() {
                public final void accept(Object obj) {
                    RecentsView.this.lambda$onLayout$21$RecentsView((RemoteTargetGluer.RemoteTargetHandle) obj);
                }
            });
            setImportantForAccessibility(isModal() ? 2 : 0);
        }
    }

    public /* synthetic */ void lambda$onLayout$21$RecentsView(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTaskViewSimulator().setScroll((float) getScrollOffset());
    }

    private void updatePivots() {
        if (this.mOverviewSelectEnabled) {
            setPivotX((float) this.mLastComputedTaskSize.centerX());
            setPivotY((float) this.mLastComputedTaskSize.bottom);
            return;
        }
        getPagedViewOrientedState().getFullScreenScaleAndPivot(this.mTempRect, this.mActivity.getDeviceProfile(), this.mTempPointF);
        setPivotX(this.mTempPointF.x);
        setPivotY(this.mTempPointF.y);
    }

    /* access modifiers changed from: private */
    public void updatePageOffsets() {
        float f;
        float f2;
        float f3;
        FloatProperty floatProperty;
        float f4 = this.mAdjacentPageHorizontalOffset;
        float interpolation = Interpolators.ACCEL_0_75.getInterpolation(this.mTaskModalness);
        int childCount = getChildCount();
        int i = -1;
        TaskView runningTaskView = (this.mRunningTaskViewId == -1 || !this.mRunningTaskTileHidden) ? null : getRunningTaskView();
        if (runningTaskView != null) {
            i = indexOfChild(runningTaskView);
        }
        int currentPage = getCurrentPage();
        int i2 = i - 1;
        float horizontalOffsetSize = i2 >= 0 ? getHorizontalOffsetSize(i2, i, f4) : 0.0f;
        int i3 = i + 1;
        float horizontalOffsetSize2 = i3 < childCount ? getHorizontalOffsetSize(i3, i, f4) : 0.0f;
        boolean showAsGrid = showAsGrid();
        int i4 = 0;
        if (showAsGrid) {
            int i5 = currentPage == 0 ? 1 : 0;
            f2 = i5 < childCount ? getHorizontalOffsetSize(i5, currentPage, interpolation) : 0.0f;
            f3 = 0.0f;
            f = 0.0f;
        } else {
            int i6 = currentPage - 1;
            float horizontalOffsetSize3 = i6 >= 0 ? getHorizontalOffsetSize(i6, currentPage, interpolation) : 0.0f;
            int i7 = currentPage + 1;
            if (i7 < childCount) {
                f3 = getHorizontalOffsetSize(i7, currentPage, interpolation);
            } else {
                f3 = 0.0f;
            }
            f = horizontalOffsetSize3;
            f2 = 0.0f;
        }
        while (i4 < childCount) {
            float f5 = (i4 == i ? 0.0f : i4 < i ? horizontalOffsetSize : horizontalOffsetSize2) + (i4 == currentPage ? 0.0f : showAsGrid ? f2 : i4 < currentPage ? f : f3);
            View childAt = getChildAt(i4);
            if (childAt instanceof TaskView) {
                floatProperty = ((TaskView) childAt).getPrimaryTaskOffsetTranslationProperty();
            } else {
                floatProperty = this.mOrientationHandler.getPrimaryViewTranslate();
            }
            floatProperty.set(childAt, Float.valueOf(f5));
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && this.mEnableDrawingLiveTile && i4 == getRunningTaskIndex()) {
                runActionOnRemoteHandles(new Consumer(f5) {
                    public final /* synthetic */ float f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        ((RemoteTargetGluer.RemoteTargetHandle) obj).getTaskViewSimulator().taskPrimaryTranslation.value = this.f$0;
                    }
                });
                redrawLiveTile();
            }
            i4++;
        }
        updateCurveProperties();
    }

    private void getPersistentChildPosition(int i, int i2, RectF rectF) {
        View childAt = getChildAt(i);
        rectF.set((float) childAt.getLeft(), (float) childAt.getTop(), (float) childAt.getRight(), (float) childAt.getBottom());
        if (childAt instanceof TaskView) {
            TaskView taskView = (TaskView) childAt;
            rectF.offset(taskView.getPersistentTranslationX(), taskView.getPersistentTranslationY());
            rectF.top += (float) this.mActivity.getDeviceProfile().overviewTaskThumbnailTopMarginPx;
            this.mTempMatrix.reset();
            float persistentScale = taskView.getPersistentScale();
            this.mTempMatrix.postScale(persistentScale, persistentScale, this.mIsRtl ? rectF.right : rectF.left, rectF.top);
            this.mTempMatrix.mapRect(rectF);
        }
        int i3 = -i2;
        rectF.offset((float) this.mOrientationHandler.getPrimaryValue(i3, 0), (float) this.mOrientationHandler.getSecondaryValue(i3, 0));
    }

    private float getHorizontalOffsetSize(int i, int i2, float f) {
        boolean z;
        float f2;
        float f3;
        if (f == 0.0f) {
            return 0.0f;
        }
        RectF rectF = this.mTempRectF;
        if (i2 > -1) {
            int scrollForPage = (getScrollForPage(i2) + this.mOrientationHandler.getPrimaryScroll(this)) - getScrollForPage(this.mCurrentPage);
            getPersistentChildPosition(i2, scrollForPage, rectF);
            float start = this.mOrientationHandler.getStart(rectF);
            getPersistentChildPosition(i, scrollForPage, rectF);
            z = this.mOrientationHandler.getStart(rectF) < start;
        } else {
            getPersistentChildPosition(i, getScrollForPage(i), rectF);
            z = this.mIsRtl;
        }
        if (z) {
            float f4 = -this.mOrientationHandler.getPrimarySize(rectF);
            f2 = -this.mOrientationHandler.getEnd(rectF);
            if (this.mLastComputedTaskStartPushOutDistance == null) {
                rectF.offsetTo(this.mOrientationHandler.getPrimaryValue(f4, 0.0f), this.mOrientationHandler.getSecondaryValue(f4, 0.0f));
                getMatrix().mapRect(rectF);
                this.mLastComputedTaskStartPushOutDistance = Float.valueOf(this.mOrientationHandler.getEnd(rectF) / this.mOrientationHandler.getPrimaryScale(this));
            }
            f3 = this.mLastComputedTaskStartPushOutDistance.floatValue();
        } else {
            float primarySize = (float) this.mOrientationHandler.getPrimarySize((View) this);
            f2 = primarySize - this.mOrientationHandler.getStart(rectF);
            if (this.mLastComputedTaskEndPushOutDistance == null) {
                rectF.offsetTo(this.mOrientationHandler.getPrimaryValue(primarySize, 0.0f), this.mOrientationHandler.getSecondaryValue(primarySize, 0.0f));
                getMatrix().mapRect(rectF);
                this.mLastComputedTaskEndPushOutDistance = Float.valueOf((this.mOrientationHandler.getStart(rectF) - primarySize) / this.mOrientationHandler.getPrimaryScale(this));
            }
            f3 = this.mLastComputedTaskEndPushOutDistance.floatValue();
        }
        return (f2 - f3) * f;
    }

    /* access modifiers changed from: protected */
    public void setTaskViewsResistanceTranslation(float f) {
        this.mTaskViewsSecondaryTranslation = f;
        for (int i = 0; i < getTaskViewCount(); i++) {
            TaskView requireTaskViewAt = requireTaskViewAt(i);
            requireTaskViewAt.getTaskResistanceTranslationProperty().set(requireTaskViewAt, Float.valueOf(f / getScaleY()));
        }
        runActionOnRemoteHandles(new Consumer(f) {
            public final /* synthetic */ float f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((RemoteTargetGluer.RemoteTargetHandle) obj).getTaskViewSimulator().recentsViewSecondaryTranslation.value = this.f$0;
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateTaskViewsSnapshotRadius() {
        for (int i = 0; i < getTaskViewCount(); i++) {
            requireTaskViewAt(i).updateSnapshotRadius();
        }
    }

    /* access modifiers changed from: protected */
    public void setTaskViewsPrimarySplitTranslation(float f) {
        this.mTaskViewsPrimarySplitTranslation = f;
        for (int i = 0; i < getTaskViewCount(); i++) {
            TaskView requireTaskViewAt = requireTaskViewAt(i);
            requireTaskViewAt.getPrimarySplitTranslationProperty().set(requireTaskViewAt, Float.valueOf(f));
        }
    }

    /* access modifiers changed from: protected */
    public void setTaskViewsSecondarySplitTranslation(float f) {
        this.mTaskViewsSecondarySplitTranslation = f;
        for (int i = 0; i < getTaskViewCount(); i++) {
            TaskView requireTaskViewAt = requireTaskViewAt(i);
            if (requireTaskViewAt != this.mSplitHiddenTaskView) {
                requireTaskViewAt.getSecondarySplitTranslationProperty().set(requireTaskViewAt, Float.valueOf(f));
            }
        }
    }

    public void applySplitPrimaryScrollOffset() {
        float f;
        float f2 = 0.0f;
        if (isSplitPlaceholderFirstInGrid()) {
            f2 = (float) (this.mIsRtl ? this.mSplitPlaceholderSize : -this.mSplitPlaceholderSize);
            f = 0.0f;
        } else if (isSplitPlaceholderLastInGrid()) {
            f = (float) (this.mIsRtl ? -this.mSplitPlaceholderSize : this.mSplitPlaceholderSize);
        } else {
            f = 0.0f;
        }
        for (int i = 0; i < getTaskViewCount(); i++) {
            requireTaskViewAt(i).setSplitScrollOffsetPrimary(f2);
        }
        this.mClearAllButton.setSplitSelectScrollOffsetPrimary(f);
    }

    private boolean isSplitPlaceholderFirstInGrid() {
        if (!this.mActivity.getDeviceProfile().isLandscape || !showAsGrid() || !isSplitSelectionActive()) {
            return false;
        }
        int activeSplitStagePosition = this.mSplitSelectStateController.getActiveSplitStagePosition();
        if (this.mIsRtl) {
            if (activeSplitStagePosition != 1) {
                return false;
            }
        } else if (activeSplitStagePosition != 0) {
            return false;
        }
        return true;
    }

    private boolean isSplitPlaceholderLastInGrid() {
        if (!this.mActivity.getDeviceProfile().isLandscape || !showAsGrid() || !isSplitSelectionActive()) {
            return false;
        }
        int activeSplitStagePosition = this.mSplitSelectStateController.getActiveSplitStagePosition();
        if (this.mIsRtl) {
            if (activeSplitStagePosition != 0) {
                return false;
            }
        } else if (activeSplitStagePosition != 1) {
            return false;
        }
        return true;
    }

    public void resetSplitPrimaryScrollOffset() {
        for (int i = 0; i < getTaskViewCount(); i++) {
            requireTaskViewAt(i).setSplitScrollOffsetPrimary(0.0f);
        }
        this.mClearAllButton.setSplitSelectScrollOffsetPrimary(0.0f);
    }

    public void resetModalVisuals() {
        TaskView currentPageTaskView = getCurrentPageTaskView();
        if (currentPageTaskView != null) {
            currentPageTaskView.getThumbnail().getTaskOverlay().resetModalVisuals();
        }
    }

    public void initiateSplitSelect(TaskView taskView) {
        initiateSplitSelect(taskView, this.mOrientationHandler.getDefaultSplitPosition(this.mActivity.getDeviceProfile()));
    }

    public void initiateSplitSelect(TaskView taskView, int i) {
        this.mSplitHiddenTaskView = taskView;
        this.mSplitSelectStateController.setInitialTaskSelect(taskView.getTask().key.id, i);
        this.mSplitHiddenTaskViewIndex = indexOfChild(taskView);
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            finishRecentsAnimation(true, (Runnable) null);
        }
    }

    public void initiateSplitSelect(QuickstepSystemShortcut.SplitSelectSource splitSelectSource) {
        this.mSplitSelectSource = splitSelectSource;
        this.mSplitSelectStateController.setInitialTaskSelect(splitSelectSource.intent, splitSelectSource.position.stagePosition);
    }

    public PendingAnimation createSplitSelectInitAnimation(int i) {
        TaskView taskView = this.mSplitHiddenTaskView;
        if (taskView != null) {
            return createTaskDismissAnimation(taskView, true, false, (long) i, true);
        }
        PendingAnimation pendingAnimation = new PendingAnimation((long) i);
        createInitialSplitSelectAnimation(pendingAnimation);
        return pendingAnimation;
    }

    public boolean confirmSplitSelect(TaskView taskView, Task task, IconView iconView, TaskThumbnailView taskThumbnailView) {
        Task task2 = task;
        TaskThumbnailView taskThumbnailView2 = taskThumbnailView;
        if (canLaunchFullscreenTask()) {
            return false;
        }
        if (this.mSplitSelectStateController.isBothSplitAppsConfirmed()) {
            return true;
        }
        this.mSplitToast.cancel();
        if (!task2.isDockable) {
            this.mSplitUnsupportedToast.show();
            return true;
        }
        this.mSplitSelectStateController.setSecondTask(task2);
        RectF rectF = new RectF();
        Rect rect = new Rect();
        Rect rect2 = new Rect();
        Rect rect3 = this.mTempRect;
        PendingAnimation pendingAnimation = new PendingAnimation((long) this.mActivity.getStateManager().getState().getTransitionDuration(this.mActivity, false));
        this.mOrientationHandler.getFinalSplitPlaceholderBounds(getResources().getDimensionPixelSize(R.dimen.multi_window_task_divider_size) / 2, this.mActivity.getDeviceProfile(), this.mSplitSelectStateController.getActiveSplitStagePosition(), rect3, rect);
        this.mFirstFloatingTaskView.getBoundsOnScreen(rect2);
        this.mFirstFloatingTaskView.addAnimation(pendingAnimation, new RectF(rect2), rect3, false, true);
        FloatingTaskView floatingTaskView = FloatingTaskView.getFloatingTaskView(this.mActivity, taskThumbnailView2, taskThumbnailView.getThumbnail(), iconView.getDrawable(), rectF);
        this.mSecondFloatingTaskView = floatingTaskView;
        floatingTaskView.setAlpha(1.0f);
        this.mSecondFloatingTaskView.addAnimation(pendingAnimation, rectF, rect, true, false);
        pendingAnimation.addEndListener(new Consumer() {
            public final void accept(Object obj) {
                RecentsView.this.lambda$confirmSplitSelect$25$RecentsView((Boolean) obj);
            }
        });
        if (taskView.containsMultipleTasks()) {
            this.mSecondSplitHiddenView = taskThumbnailView2;
        } else {
            this.mSecondSplitHiddenView = taskView;
        }
        this.mSecondSplitHiddenView.setVisibility(4);
        InteractionJankMonitorWrapper.begin((View) this, 49, "Second tile selected");
        pendingAnimation.buildAnim().start();
        return true;
    }

    public /* synthetic */ void lambda$confirmSplitSelect$25$RecentsView(Boolean bool) {
        this.mSplitSelectStateController.launchSplitTasks(new Consumer() {
            public final void accept(Object obj) {
                RecentsView.this.lambda$confirmSplitSelect$24$RecentsView((Boolean) obj);
            }
        });
        InteractionJankMonitorWrapper.end(49);
    }

    public /* synthetic */ void lambda$confirmSplitSelect$24$RecentsView(Boolean bool) {
        resetFromSplitSelectionState();
    }

    /* access modifiers changed from: protected */
    public void resetFromSplitSelectionState() {
        if (!(this.mSplitSelectSource == null && this.mSplitHiddenTaskViewIndex == -1)) {
            if (this.mFirstFloatingTaskView != null) {
                this.mActivity.getRootView().removeView(this.mFirstFloatingTaskView);
                this.mFirstFloatingTaskView = null;
            }
            if (this.mSecondFloatingTaskView != null) {
                this.mActivity.getRootView().removeView(this.mSecondFloatingTaskView);
                this.mSecondFloatingTaskView = null;
                this.mSecondSplitHiddenView.setVisibility(0);
                this.mSecondSplitHiddenView = null;
            }
            this.mSplitSelectSource = null;
        }
        if (this.mSplitHiddenTaskViewIndex != -1) {
            if (!this.mActivity.getDeviceProfile().isTablet) {
                int i = this.mCurrentPage;
                int i2 = this.mSplitHiddenTaskViewIndex;
                if (i2 <= i) {
                    i2 = i + 1;
                }
                snapToPageImmediately(i2);
            }
            onLayout(false, getLeft(), getTop(), getRight(), getBottom());
            resetTaskVisuals();
            this.mSplitHiddenTaskViewIndex = -1;
            TaskView taskView = this.mSplitHiddenTaskView;
            if (taskView != null) {
                taskView.setVisibility(0);
                this.mSplitHiddenTaskView = null;
            }
        }
    }

    public float getSplitSelectTranslation() {
        int activeSplitStagePosition = getSplitPlaceholder().getActiveSplitStagePosition();
        if (!shouldShiftThumbnailsForSplitSelect()) {
            return 0.0f;
        }
        return this.mActivity.getResources().getDimension(R.dimen.split_placeholder_size) * ((float) getPagedOrientationHandler().getSplitTranslationDirectionFactor(activeSplitStagePosition, this.mActivity.getDeviceProfile()));
    }

    /* access modifiers changed from: protected */
    public void onRotateInSplitSelectionState() {
        this.mOrientationHandler.getInitialSplitPlaceholderBounds(this.mSplitPlaceholderSize, this.mSplitPlaceholderInset, this.mActivity.getDeviceProfile(), this.mSplitSelectStateController.getActiveSplitStagePosition(), this.mTempRect);
        this.mTempRectF.set(this.mTempRect);
        this.mFirstFloatingTaskView.updateOrientationHandler(this.mOrientationHandler);
        this.mFirstFloatingTaskView.update(this.mTempRectF, 1.0f);
        Pair<FloatProperty, FloatProperty> splitSelectTaskOffset = getPagedOrientationHandler().getSplitSelectTaskOffset(TASK_PRIMARY_SPLIT_TRANSLATION, TASK_SECONDARY_SPLIT_TRANSLATION, this.mActivity.getDeviceProfile());
        ((FloatProperty) splitSelectTaskOffset.first).set(this, Float.valueOf(getSplitSelectTranslation()));
        ((FloatProperty) splitSelectTaskOffset.second).set(this, Float.valueOf(0.0f));
        applySplitPrimaryScrollOffset();
    }

    private void updateDeadZoneRects() {
        this.mClearAllButtonDeadZoneRect.setEmpty();
        if (this.mClearAllButton.getWidth() > 0) {
            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.recents_clear_all_deadzone_vertical_margin);
            this.mClearAllButton.getHitRect(this.mClearAllButtonDeadZoneRect);
            this.mClearAllButtonDeadZoneRect.inset((-getPaddingRight()) / 2, -dimensionPixelSize);
        }
        this.mTaskViewDeadZoneRect.setEmpty();
        int taskViewCount = getTaskViewCount();
        if (taskViewCount > 0) {
            TaskView requireTaskViewAt = requireTaskViewAt(0);
            requireTaskViewAt(taskViewCount - 1).getHitRect(this.mTaskViewDeadZoneRect);
            this.mTaskViewDeadZoneRect.union(requireTaskViewAt.getLeft(), requireTaskViewAt.getTop(), requireTaskViewAt.getRight(), requireTaskViewAt.getBottom());
        }
    }

    private void updateEmptyStateUi(boolean z) {
        boolean z2 = getWidth() > 0 && getHeight() > 0;
        if (z && z2) {
            this.mEmptyTextLayout = null;
            this.mLastMeasureSize.set(getWidth(), getHeight());
        }
        ViewGroup viewGroup = this.zxwClearPane;
        if (viewGroup != null) {
            viewGroup.setVisibility(this.mShowEmptyMessage ? 8 : 0);
        }
        if (this.mShowEmptyMessage && z2 && this.mEmptyTextLayout == null) {
            int i = this.mLastMeasureSize.x;
            int i2 = this.mEmptyMessagePadding;
            int i3 = (i - i2) - i2;
            CharSequence charSequence = this.mEmptyMessage;
            StaticLayout build = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), this.mEmptyMessagePaint, i3).setAlignment(Layout.Alignment.ALIGN_CENTER).build();
            this.mEmptyTextLayout = build;
            int height = (this.mLastMeasureSize.y - ((build.getHeight() + this.mEmptyMessagePadding) + this.mEmptyIcon.getIntrinsicHeight())) / 2;
            int intrinsicWidth = (this.mLastMeasureSize.x - this.mEmptyIcon.getIntrinsicWidth()) / 2;
            Drawable drawable = this.mEmptyIcon;
            drawable.setBounds(intrinsicWidth, height, drawable.getIntrinsicWidth() + intrinsicWidth, this.mEmptyIcon.getIntrinsicHeight() + height);
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || (this.mShowEmptyMessage && drawable == this.mEmptyIcon);
    }

    /* access modifiers changed from: protected */
    public void maybeDrawEmptyMessage(Canvas canvas) {
        if (this.mShowEmptyMessage && this.mEmptyTextLayout != null) {
            this.mTempRect.set(this.mInsets.left + getPaddingLeft(), this.mInsets.top + getPaddingTop(), this.mInsets.right + getPaddingRight(), this.mInsets.bottom + getPaddingBottom());
            canvas.save();
            canvas.translate((float) (getScrollX() + ((this.mTempRect.left - this.mTempRect.right) / 2)), (float) ((this.mTempRect.top - this.mTempRect.bottom) / 2));
            this.mEmptyIcon.draw(canvas);
            canvas.translate((float) this.mEmptyMessagePadding, (float) (this.mEmptyIcon.getBounds().bottom + this.mEmptyMessagePadding));
            this.mEmptyTextLayout.draw(canvas);
            canvas.restore();
            ViewGroup viewGroup = this.zxwClearPane;
            if (viewGroup != null) {
                viewGroup.setVisibility(8);
            }
        }
    }

    public AnimatorSet createAdjacentPageAnimForTaskLaunch(TaskView taskView) {
        AnimatorSet animatorSet = new AnimatorSet();
        int indexOfChild = indexOfChild(taskView);
        int currentPage = getCurrentPage();
        boolean z = indexOfChild == currentPage;
        float maxScaleForFullScreen = getMaxScaleForFullScreen();
        RecentsView recentsView = taskView.getRecentsView();
        if (z) {
            animatorSet.play(ObjectAnimator.ofFloat(recentsView, RECENTS_SCALE_PROPERTY, new float[]{maxScaleForFullScreen}));
            animatorSet.play(ObjectAnimator.ofFloat(recentsView, FULLSCREEN_PROGRESS, new float[]{1.0f}));
        } else {
            float width = ((float) taskView.getWidth()) * (maxScaleForFullScreen - 1.0f);
            if (this.mIsRtl) {
                width = -width;
            }
            animatorSet.play(ObjectAnimator.ofFloat(getPageAt(currentPage), this.mOrientationHandler.getPrimaryViewTranslate(), new float[]{width}));
            int runningTaskIndex = recentsView.getRunningTaskIndex();
            if (!(!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || runningTaskIndex == -1 || runningTaskIndex == indexOfChild || recentsView.getRemoteTargetHandles() == null)) {
                for (RemoteTargetGluer.RemoteTargetHandle taskViewSimulator : recentsView.getRemoteTargetHandles()) {
                    animatorSet.play(ObjectAnimator.ofFloat(taskViewSimulator.getTaskViewSimulator().taskPrimaryTranslation, AnimatedFloat.VALUE, new float[]{width}));
                }
            }
            int i = currentPage + (currentPage - indexOfChild);
            if (i >= 0 && i < getPageCount()) {
                animatorSet.play(ObjectAnimator.ofPropertyValuesHolder(getPageAt(i), new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(this.mOrientationHandler.getPrimaryViewTranslate(), new float[]{width}), PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{1.0f}), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{1.0f})}));
            }
        }
        return animatorSet;
    }

    public float getMaxScaleForFullScreen() {
        getTaskSize(this.mTempRect);
        return getPagedViewOrientedState().getFullScreenScaleAndPivot(this.mTempRect, this.mActivity.getDeviceProfile(), this.mTempPointF);
    }

    public PendingAnimation createTaskLaunchAnimation(TaskView taskView, long j, Interpolator interpolator) {
        if (getTaskViewCount() == 0) {
            return new PendingAnimation(j);
        }
        updateGridProperties();
        updateScrollSynchronously();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(taskView.getThumbnail().getSysUiStatusNavFlags(), new boolean[]{false}) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ boolean[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RecentsView.this.lambda$createTaskLaunchAnimation$26$RecentsView(this.f$1, this.f$2, valueAnimator);
            }
        });
        AnimatorSet createAdjacentPageAnimForTaskLaunch = createAdjacentPageAnimForTaskLaunch(taskView);
        DepthController depthController = getDepthController();
        if (depthController != null) {
            createAdjacentPageAnimForTaskLaunch.play(ObjectAnimator.ofFloat(depthController, DepthController.DEPTH, new float[]{LauncherState.BACKGROUND_APP.getDepth(this.mActivity)}));
        }
        createAdjacentPageAnimForTaskLaunch.play(ofFloat);
        createAdjacentPageAnimForTaskLaunch.setInterpolator(interpolator);
        PendingAnimation pendingAnimation = new PendingAnimation(j);
        this.mPendingAnimation = pendingAnimation;
        pendingAnimation.add(createAdjacentPageAnimForTaskLaunch);
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            runActionOnRemoteHandles(new Consumer(interpolator) {
                public final /* synthetic */ Interpolator f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    RecentsView.this.lambda$createTaskLaunchAnimation$27$RecentsView(this.f$1, (RemoteTargetGluer.RemoteTargetHandle) obj);
                }
            });
            this.mPendingAnimation.addOnFrameCallback(new Runnable() {
                public final void run() {
                    RecentsView.this.redrawLiveTile();
                }
            });
        }
        this.mPendingAnimation.addEndListener(new Consumer(taskView) {
            public final /* synthetic */ TaskView f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                RecentsView.this.lambda$createTaskLaunchAnimation$29$RecentsView(this.f$1, (Boolean) obj);
            }
        });
        return this.mPendingAnimation;
    }

    public /* synthetic */ void lambda$createTaskLaunchAnimation$26$RecentsView(int i, boolean[] zArr, ValueAnimator valueAnimator) {
        if (valueAnimator.getAnimatedFraction() > 0.85f) {
            this.mActivity.getSystemUiController().updateUiState(3, i);
        } else {
            this.mActivity.getSystemUiController().updateUiState(3, 0);
        }
        boolean z = valueAnimator.getAnimatedFraction() >= 0.5f;
        if (z != zArr[0]) {
            zArr[0] = z;
            performHapticFeedback(1, 1);
        }
    }

    public /* synthetic */ void lambda$createTaskLaunchAnimation$27$RecentsView(Interpolator interpolator, RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTaskViewSimulator().addOverviewToAppAnim(this.mPendingAnimation, interpolator);
    }

    public /* synthetic */ void lambda$createTaskLaunchAnimation$29$RecentsView(TaskView taskView, Boolean bool) {
        RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr;
        if (bool.booleanValue()) {
            if (!(taskView.getTaskIds()[1] == -1 || (remoteTargetHandleArr = this.mRemoteTargetHandles) == null)) {
                TaskViewUtils.createSplitAuxiliarySurfacesAnimator(remoteTargetHandleArr[0].getTransformParams().getTargetSet().nonApps, true, $$Lambda$RecentsView$kEJfEWKrT_C9UuOp_2lK9aaNtvc.INSTANCE);
            }
            if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || !taskView.isRunningTask()) {
                taskView.launchTask(new Consumer() {
                    public final void accept(Object obj) {
                        RecentsView.this.onTaskLaunchAnimationEnd(((Boolean) obj).booleanValue());
                    }
                });
            } else {
                finishRecentsAnimation(false, (Runnable) null);
                onTaskLaunchAnimationEnd(true);
            }
            if (taskView.getTask() != null) {
                this.mActivity.getStatsLogManager().logger().withItemInfo(taskView.getItemInfo()).log(StatsLogManager.LauncherEvent.LAUNCHER_TASK_LAUNCH_SWIPE_DOWN);
            }
        } else {
            onTaskLaunchAnimationEnd(false);
        }
        this.mPendingAnimation = null;
    }

    static /* synthetic */ void lambda$createTaskLaunchAnimation$28(ValueAnimator valueAnimator) {
        valueAnimator.start();
        valueAnimator.end();
    }

    /* access modifiers changed from: protected */
    public void onTaskLaunchAnimationEnd(boolean z) {
        if (z) {
            resetTaskVisuals();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int i) {
        super.notifyPageSwitchListener(i);
        updateCurrentTaskActionsVisibility();
        loadVisibleTaskData(3);
        updateEnabledOverlays();
    }

    public void addChildrenForAccessibility(ArrayList<View> arrayList) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            arrayList.add(getChildAt(childCount));
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(1, getTaskViewCount(), false, 0));
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        int taskViewCount = getTaskViewCount();
        accessibilityEvent.setScrollable(taskViewCount > 0);
        if (accessibilityEvent.getEventType() == 4096) {
            int[] visibleChildrenRange = getVisibleChildrenRange();
            accessibilityEvent.setFromIndex(taskViewCount - visibleChildrenRange[1]);
            accessibilityEvent.setToIndex(taskViewCount - visibleChildrenRange[0]);
            accessibilityEvent.setItemCount(taskViewCount);
        }
    }

    public CharSequence getAccessibilityClassName() {
        return ListView.class.getName();
    }

    public void setEnableDrawingLiveTile(boolean z) {
        this.mEnableDrawingLiveTile = z;
    }

    public void redrawLiveTile() {
        runActionOnRemoteHandles($$Lambda$RecentsView$dDvi8WWYcGf1U6mMlye8D_Hdd8c.INSTANCE);
    }

    static /* synthetic */ void lambda$redrawLiveTile$30(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        TransformParams transformParams = remoteTargetHandle.getTransformParams();
        if (transformParams.getTargetSet() != null) {
            remoteTargetHandle.getTaskViewSimulator().apply(transformParams);
        }
    }

    public RemoteTargetGluer.RemoteTargetHandle[] getRemoteTargetHandles() {
        return this.mRemoteTargetHandles;
    }

    public void setRecentsAnimationTargets(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
        this.mRecentsAnimationController = recentsAnimationController;
        this.mSplitSelectStateController.setRecentsAnimationRunning(true);
        if (recentsAnimationTargets != null && recentsAnimationTargets.apps.length != 0) {
            RemoteTargetGluer remoteTargetGluer = new RemoteTargetGluer(getContext(), getSizeStrategy());
            this.mRemoteTargetHandles = remoteTargetGluer.assignTargetsForSplitScreen(getContext(), (RemoteAnimationTargets) recentsAnimationTargets);
            this.mSplitBoundsConfig = remoteTargetGluer.getStagedSplitBounds();
            runActionOnRemoteHandles(new Consumer() {
                public final void accept(Object obj) {
                    RecentsView.this.lambda$setRecentsAnimationTargets$31$RecentsView((RemoteTargetGluer.RemoteTargetHandle) obj);
                }
            });
            TaskView runningTaskView = getRunningTaskView();
            if (runningTaskView instanceof GroupedTaskView) {
                ((GroupedTaskView) runningTaskView).updateSplitBoundsConfig(this.mSplitBoundsConfig);
            }
        }
    }

    public /* synthetic */ void lambda$setRecentsAnimationTargets$31$RecentsView(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        TransformParams transformParams = remoteTargetHandle.getTransformParams();
        SurfaceTransactionApplier surfaceTransactionApplier = this.mSyncTransactionApplier;
        if (surfaceTransactionApplier != null) {
            transformParams.setSyncTransactionApplier(surfaceTransactionApplier);
            transformParams.getTargetSet().addReleaseCheck(this.mSyncTransactionApplier);
        }
        TaskViewSimulator taskViewSimulator = remoteTargetHandle.getTaskViewSimulator();
        taskViewSimulator.setOrientationState(this.mOrientationState);
        taskViewSimulator.setDp(this.mActivity.getDeviceProfile());
        taskViewSimulator.recentsViewScale.value = 1.0f;
    }

    public void runActionOnRemoteHandles(Consumer<RemoteTargetGluer.RemoteTargetHandle> consumer) {
        RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr = this.mRemoteTargetHandles;
        if (remoteTargetHandleArr != null) {
            for (RemoteTargetGluer.RemoteTargetHandle accept : remoteTargetHandleArr) {
                consumer.accept(accept);
            }
        }
    }

    public void finishRecentsAnimation(boolean z, Runnable runnable) {
        finishRecentsAnimation(z, true, runnable);
    }

    public void finishRecentsAnimation(boolean z, boolean z2, Runnable runnable) {
        cleanupRemoteTargets();
        if (!z && FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).setSplitScreenMinimized(false);
        }
        if (this.mRecentsAnimationController != null) {
            boolean z3 = z && z2;
            if (z3) {
                SystemUiProxy systemUiProxy = SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext());
                systemUiProxy.notifySwipeToHomeFinished();
                systemUiProxy.setShelfHeight(true, this.mActivity.getDeviceProfile().hotseatBarSizePx);
                PictureInPictureSurfaceTransaction build = new PictureInPictureSurfaceTransaction.Builder().setAlpha(0.0f).build();
                for (int finishTaskTransaction : TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).getRunningSplitTaskIds()) {
                    this.mRecentsAnimationController.setFinishTaskTransaction(finishTaskTransaction, build, (SurfaceControl) null);
                }
            }
            this.mRecentsAnimationController.finish(z, new Runnable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RecentsView.this.lambda$finishRecentsAnimation$32$RecentsView(this.f$1);
                }
            }, z3);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$finishRecentsAnimation$32$RecentsView(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        onRecentsAnimationComplete();
    }

    public void onRecentsAnimationComplete() {
        setRunningTaskViewShowScreenshot(true);
        setCurrentTask(-1);
        this.mRecentsAnimationController = null;
        this.mSplitSelectStateController.setRecentsAnimationRunning(false);
        executeSideTaskLaunchCallback();
    }

    public void setDisallowScrollToClearAll(boolean z) {
        if (this.mDisallowScrollToClearAll != z) {
            this.mDisallowScrollToClearAll = z;
            updateMinAndMaxScrollX();
        }
    }

    public void updateScrollSynchronously() {
        onMeasure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), BasicMeasure.EXACTLY));
        onLayout(false, getLeft(), getTop(), getRight(), getBottom());
        updateMinAndMaxScrollX();
    }

    /* access modifiers changed from: protected */
    public int getChildGap(int i, int i2) {
        int indexOfChild = indexOfChild(this.mClearAllButton);
        if (i == indexOfChild || i2 == indexOfChild) {
            return getClearAllExtraPageSpacing();
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getClearAllExtraPageSpacing() {
        if (showAsGrid()) {
            return Math.max(this.mActivity.getDeviceProfile().overviewGridSideMargin - this.mPageSpacing, 0);
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void updateMinAndMaxScrollX() {
        super.updateMinAndMaxScrollX();
    }

    /* access modifiers changed from: protected */
    public int computeMinScroll() {
        if (getTaskViewCount() <= 0) {
            return super.computeMinScroll();
        }
        return getScrollForPage(this.mIsRtl ? getLastViewIndex() : getFirstViewIndex());
    }

    /* access modifiers changed from: protected */
    public int computeMaxScroll() {
        if (getTaskViewCount() <= 0) {
            return super.computeMaxScroll();
        }
        return getScrollForPage(this.mIsRtl ? getFirstViewIndex() : getLastViewIndex());
    }

    private int getFirstViewIndex() {
        TaskView focusedTaskView = this.mShowAsGridLastOnLayout ? getFocusedTaskView() : null;
        if (focusedTaskView != null) {
            return indexOfChild(focusedTaskView);
        }
        return 0;
    }

    private int getLastViewIndex() {
        if (!this.mDisallowScrollToClearAll) {
            return indexOfChild(this.mClearAllButton);
        }
        if (this.mShowAsGridLastOnLayout) {
            return indexOfChild(getLastGridTaskView());
        }
        return getTaskViewCount() - 1;
    }

    public int getClearAllScroll() {
        return getScrollForPage(indexOfChild(this.mClearAllButton));
    }

    /* access modifiers changed from: protected */
    public boolean getPageScrolls(int[] iArr, boolean z, PagedView.ComputePageScrollsLogic computePageScrollsLogic) {
        boolean z2;
        int i;
        int[] iArr2 = new int[iArr.length];
        super.getPageScrolls(iArr2, z, computePageScrollsLogic);
        boolean showAsFullscreen = showAsFullscreen();
        boolean showAsGrid = showAsGrid();
        if (z) {
            int primaryValue = this.mOrientationHandler.getPrimaryValue(this.mTaskWidth, this.mTaskHeight) - this.mOrientationHandler.getPrimarySize((View) this.mClearAllButton);
            ClearAllButton clearAllButton = this.mClearAllButton;
            if (!this.mIsRtl) {
                primaryValue = -primaryValue;
            }
            clearAllButton.setScrollOffsetPrimary((float) primaryValue);
        }
        int indexOfChild = indexOfChild(this.mClearAllButton);
        int primarySize = this.mOrientationHandler.getPrimarySize((View) this.mClearAllButton);
        if (indexOfChild == -1 || indexOfChild >= iArr.length) {
            z2 = false;
            i = 0;
        } else {
            i = iArr2[indexOfChild] + ((int) this.mClearAllButton.getScrollAdjustment(showAsFullscreen, showAsGrid));
            if (iArr[indexOfChild] != i) {
                iArr[indexOfChild] = i;
                z2 = true;
            } else {
                z2 = false;
            }
        }
        int taskViewCount = getTaskViewCount();
        for (int i2 = 0; i2 < taskViewCount; i2++) {
            int scrollAdjustment = iArr2[i2] + ((int) requireTaskViewAt(i2).getScrollAdjustment(showAsFullscreen, showAsGrid));
            int lastTaskScroll = getLastTaskScroll(i, primarySize);
            if ((this.mIsRtl && scrollAdjustment < lastTaskScroll) || (!this.mIsRtl && scrollAdjustment > lastTaskScroll)) {
                scrollAdjustment = lastTaskScroll;
            }
            if (iArr[i2] != scrollAdjustment) {
                iArr[i2] = scrollAdjustment;
                z2 = true;
            }
        }
        return z2;
    }

    /* access modifiers changed from: protected */
    public int getChildOffset(int i) {
        float f;
        float offsetAdjustment;
        int childOffset = super.getChildOffset(i);
        View childAt = getChildAt(i);
        if (childAt instanceof TaskView) {
            f = (float) childOffset;
            offsetAdjustment = ((TaskView) childAt).getOffsetAdjustment(showAsFullscreen(), showAsGrid());
        } else if (!(childAt instanceof ClearAllButton)) {
            return childOffset;
        } else {
            f = (float) childOffset;
            offsetAdjustment = ((ClearAllButton) childAt).getOffsetAdjustment(this.mOverviewFullscreenEnabled, showAsGrid());
        }
        return (int) (f + offsetAdjustment);
    }

    /* access modifiers changed from: protected */
    public int getChildVisibleSize(int i) {
        TaskView taskViewAt = getTaskViewAt(i);
        if (taskViewAt == null) {
            return super.getChildVisibleSize(i);
        }
        return (int) (((float) super.getChildVisibleSize(i)) * taskViewAt.getSizeAdjustment(showAsFullscreen()));
    }

    public ClearAllButton getClearAllButton() {
        return this.mClearAllButton;
    }

    public int getScrollOffset() {
        return getScrollOffset(getRunningTaskIndex());
    }

    public int getScrollOffset(int i) {
        if (i == -1) {
            return 0;
        }
        int overScrollShift = getOverScrollShift();
        float f = this.mAdjacentPageHorizontalOffset;
        if (f > 0.0f) {
            overScrollShift = (int) Utilities.mapRange(f, (float) overScrollShift, getUndampedOverScrollShift());
        }
        return (getScrollForPage(i) - this.mOrientationHandler.getPrimaryScroll(this)) + overScrollShift + getOffsetFromScrollPosition(i);
    }

    private int getOffsetFromScrollPosition(int i) {
        return getOffsetFromScrollPosition(i, getTopRowIdArray(), getBottomRowIdArray());
    }

    /* access modifiers changed from: private */
    public int getOffsetFromScrollPosition(int i, IntArray intArray, IntArray intArray2) {
        TaskView taskViewAt;
        TaskView lastGridTaskView;
        int i2;
        int i3;
        if (!showAsGrid() || (taskViewAt = getTaskViewAt(i)) == null || (lastGridTaskView = getLastGridTaskView(intArray, intArray2)) == null || getScrollForPage(i) != getScrollForPage(indexOfChild(lastGridTaskView))) {
            return 0;
        }
        int width = (this.mLastComputedGridTaskSize.width() + this.mPageSpacing) * (getPositionInRow(lastGridTaskView, intArray, intArray2) - getPositionInRow(taskViewAt, intArray, intArray2));
        if (this.mIsRtl) {
            i2 = this.mLastComputedGridSize.left;
        } else {
            i2 = this.mLastComputedGridSize.right;
        }
        int i4 = i2 + (this.mIsRtl ? this.mPageSpacing : -this.mPageSpacing);
        if (!this.mIsRtl) {
            width = -width;
        }
        int i5 = i4 + width;
        if (this.mIsRtl) {
            i3 = this.mLastComputedGridTaskSize.left;
        } else {
            i3 = this.mLastComputedGridTaskSize.right;
        }
        return i5 - i3;
    }

    private int getPositionInRow(TaskView taskView, IntArray intArray, IntArray intArray2) {
        int indexOf = intArray.indexOf(taskView.getTaskViewId());
        return indexOf != -1 ? indexOf : intArray2.indexOf(taskView.getTaskViewId());
    }

    public boolean isOnGridBottomRow(TaskView taskView) {
        return showAsGrid() && !this.mTopRowIdSet.contains(taskView.getTaskViewId()) && taskView.getTaskViewId() != this.mFocusedTaskViewId;
    }

    public Consumer<MotionEvent> getEventDispatcher(float f) {
        float degreesRotated = f == 0.0f ? this.mOrientationHandler.getDegreesRotated() : -f;
        if (degreesRotated == 0.0f) {
            return new Consumer() {
                public final void accept(Object obj) {
                    RecentsView.this.lambda$getEventDispatcher$33$RecentsView((MotionEvent) obj);
                }
            };
        }
        return new Consumer(f, degreesRotated) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                RecentsView.this.lambda$getEventDispatcher$34$RecentsView(this.f$1, this.f$2, (MotionEvent) obj);
            }
        };
    }

    public /* synthetic */ void lambda$getEventDispatcher$33$RecentsView(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
    }

    public /* synthetic */ void lambda$getEventDispatcher$34$RecentsView(float f, float f2, MotionEvent motionEvent) {
        if (f == 0.0f || !this.mOrientationState.isMultipleOrientationSupportedByDevice() || this.mOrientationState.getOrientationHandler().isLayoutNaturalToLauncher()) {
            float f3 = -f2;
            this.mOrientationState.transformEvent(f3, motionEvent, true);
            super.onTouchEvent(motionEvent);
            this.mOrientationState.transformEvent(f3, motionEvent, false);
            return;
        }
        this.mOrientationState.flipVertical(motionEvent);
        super.onTouchEvent(motionEvent);
        this.mOrientationState.flipVertical(motionEvent);
    }

    private void updateEnabledOverlays() {
        int nextPage = this.mOverlayEnabled ? getNextPage() : -1;
        int taskViewCount = getTaskViewCount();
        int i = 0;
        while (i < taskViewCount) {
            requireTaskViewAt(i).setOverlayEnabled(i == nextPage);
            i++;
        }
    }

    public void setOverlayEnabled(boolean z) {
        if (this.mOverlayEnabled != z) {
            this.mOverlayEnabled = z;
            updateEnabledOverlays();
        }
    }

    public void setOverviewGridEnabled(boolean z) {
        if (this.mOverviewGridEnabled != z) {
            this.mOverviewGridEnabled = z;
            updateActionsViewFocusedScroll();
            requestLayout();
        }
    }

    public void setOverviewFullscreenEnabled(boolean z) {
        if (this.mOverviewFullscreenEnabled != z) {
            this.mOverviewFullscreenEnabled = z;
            requestLayout();
        }
    }

    public void setOverviewSelectEnabled(boolean z) {
        if (this.mOverviewSelectEnabled != z) {
            this.mOverviewSelectEnabled = z;
            updatePivots();
        }
    }

    public void switchToScreenshot(Runnable runnable) {
        if (this.mRecentsAnimationController != null) {
            switchToScreenshotInternal(runnable);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    private void switchToScreenshotInternal(Runnable runnable) {
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView == null) {
            runnable.run();
            return;
        }
        runningTaskView.setShowScreenshot(true);
        for (TaskView.TaskIdAttributeContainer taskIdAttributeContainer : runningTaskView.getTaskIdAttributeContainers()) {
            if (taskIdAttributeContainer != null) {
                ThumbnailData screenshotTask = this.mRecentsAnimationController.screenshotTask(taskIdAttributeContainer.getTask().key.id);
                TaskThumbnailView thumbnailView = taskIdAttributeContainer.getThumbnailView();
                if (screenshotTask != null) {
                    thumbnailView.setThumbnail(taskIdAttributeContainer.getTask(), screenshotTask);
                } else {
                    thumbnailView.refresh();
                }
            }
        }
        ViewUtils.postFrameDrawn(runningTaskView, runnable);
    }

    public void switchToScreenshot(HashMap<Integer, ThumbnailData> hashMap, Runnable runnable) {
        TaskView runningTaskView = getRunningTaskView();
        if (runningTaskView != null) {
            runningTaskView.setShowScreenshot(true);
            runningTaskView.refreshThumbnails(hashMap);
            ViewUtils.postFrameDrawn(runningTaskView, runnable);
            return;
        }
        runnable.run();
    }

    /* access modifiers changed from: private */
    public void setTaskModalness(float f) {
        this.mTaskModalness = f;
        updatePageOffsets();
        if (getCurrentPageTaskView() != null) {
            getCurrentPageTaskView().setModalness(f);
        }
        boolean z = false;
        boolean z2 = !this.mOrientationState.isRecentsActivityRotationAllowed() && this.mOrientationState.getTouchRotation() != 0;
        OverviewActionsView overviewActionsView = this.mActionsView;
        if (f < 1.0f && z2) {
            z = true;
        }
        overviewActionsView.updateHiddenFlags(1, z);
    }

    public void onSecondaryWindowBoundsChanged() {
        setInsets(this.mInsets);
    }

    public TaskOverlayFactory getTaskOverlayFactory() {
        return this.mTaskOverlayFactory;
    }

    public BaseActivityInterface getSizeStrategy() {
        return this.mSizeStrategy;
    }

    public void showForegroundScrim(boolean z) {
        float f = 0.0f;
        if (z || this.mColorTint != 0.0f) {
            FloatProperty<RecentsView> floatProperty = COLOR_TINT;
            float[] fArr = new float[1];
            if (z) {
                f = 0.5f;
            }
            fArr[0] = f;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, floatProperty, fArr);
            this.mTintingAnimator = ofFloat;
            ofFloat.setAutoCancel(true);
            this.mTintingAnimator.start();
            return;
        }
        ObjectAnimator objectAnimator = this.mTintingAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mTintingAnimator = null;
        }
    }

    /* access modifiers changed from: private */
    public void setColorTint(float f) {
        this.mColorTint = f;
        for (int i = 0; i < getTaskViewCount(); i++) {
            requireTaskViewAt(i).setColorTint(this.mColorTint, this.mTintingColor);
        }
        Drawable background = this.mActivity.getScrimView().getBackground();
        if (background == null) {
            return;
        }
        if (f == 0.0f) {
            background.setTintList((ColorStateList) null);
            return;
        }
        background.setTintBlendMode(BlendMode.SRC_OVER);
        background.setTint(ColorUtils.setAlphaComponent(this.mTintingColor, (int) (f * 255.0f)));
    }

    /* access modifiers changed from: private */
    public float getColorTint() {
        return this.mColorTint;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r2.mCurrentGestureEndTarget;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showAsGrid() {
        /*
            r2 = this;
            boolean r0 = r2.mOverviewGridEnabled
            if (r0 != 0) goto L_0x001d
            com.android.quickstep.GestureState$GestureEndTarget r0 = r2.mCurrentGestureEndTarget
            if (r0 == 0) goto L_0x001b
            com.android.quickstep.BaseActivityInterface<STATE_TYPE, ACTIVITY_TYPE> r1 = r2.mSizeStrategy
            com.android.launcher3.statemanager.BaseState r0 = r1.stateFromGestureEndTarget(r0)
            ACTIVITY_TYPE r1 = r2.mActivity
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            boolean r0 = r0.displayOverviewTasksAsGrid(r1)
            if (r0 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r0 = 0
            goto L_0x001e
        L_0x001d:
            r0 = 1
        L_0x001e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.views.RecentsView.showAsGrid():boolean");
    }

    private boolean showAsFullscreen() {
        return this.mOverviewFullscreenEnabled && this.mCurrentGestureEndTarget != GestureState.GestureEndTarget.RECENTS;
    }

    public void cleanupRemoteTargets() {
        this.mRemoteTargetHandles = null;
    }

    public void addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener onScrollChangedListener) {
        this.mScrollListeners.add(onScrollChangedListener);
    }

    public void removeOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener onScrollChangedListener) {
        this.mScrollListeners.remove(onScrollChangedListener);
    }

    public int getPipCornerRadius() {
        return this.mPipCornerRadius;
    }

    public int getPipShadowRadius() {
        return this.mPipShadowRadius;
    }

    public boolean scrollLeft() {
        int i;
        int i2;
        if (!showAsGrid()) {
            return super.scrollLeft();
        }
        int nextPage = getNextPage();
        if (nextPage < 0) {
            return this.mAllowOverScroll;
        }
        TaskView taskViewAt = getTaskViewAt(nextPage);
        while (true) {
            if ((taskViewAt == null || isTaskViewFullyVisible(taskViewAt)) && nextPage - 1 >= 0) {
                nextPage--;
                taskViewAt = getTaskViewAt(nextPage);
            }
        }
        if (this.mIsRtl) {
            i = this.mLastComputedGridSize.left;
        } else {
            i = this.mLastComputedGridSize.right;
        }
        int i3 = i + (this.mIsRtl ? this.mPageSpacing : -this.mPageSpacing);
        if (this.mIsRtl) {
            i2 = this.mLastComputedGridTaskSize.left;
        } else {
            i2 = this.mLastComputedGridTaskSize.right;
        }
        int scrollForPage = (getScrollForPage(nextPage) + i2) - i3;
        while (true) {
            int i4 = nextPage - 1;
            if (i4 < 0) {
                break;
            }
            if (!this.mIsRtl) {
                if (getScrollForPage(i4) <= scrollForPage) {
                    break;
                }
            } else if (getScrollForPage(i4) >= scrollForPage) {
                break;
            }
            nextPage--;
        }
        snapToPage(nextPage);
        return true;
    }

    public boolean scrollRight() {
        if (!showAsGrid()) {
            return super.scrollRight();
        }
        int nextPage = getNextPage();
        if (nextPage >= getChildCount()) {
            return this.mAllowOverScroll;
        }
        TaskView taskViewAt = getTaskViewAt(nextPage);
        while (taskViewAt != null && isTaskViewFullyVisible(taskViewAt)) {
            int i = nextPage + 1;
            if (i >= getChildCount()) {
                break;
            }
            int i2 = i;
            taskViewAt = getTaskViewAt(i);
            nextPage = i2;
        }
        snapToPage(nextPage);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        dispatchScrollChanged();
    }

    /* access modifiers changed from: private */
    public void dispatchScrollChanged() {
        runActionOnRemoteHandles(new Consumer() {
            public final void accept(Object obj) {
                RecentsView.this.lambda$dispatchScrollChanged$35$RecentsView((RemoteTargetGluer.RemoteTargetHandle) obj);
            }
        });
        for (int size = this.mScrollListeners.size() - 1; size >= 0; size--) {
            this.mScrollListeners.get(size).onScrollChanged();
        }
    }

    public /* synthetic */ void lambda$dispatchScrollChanged$35$RecentsView(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTaskViewSimulator().setScroll((float) getScrollOffset());
    }

    private static class PinnedStackAnimationListener<T extends BaseActivity> extends IPipAnimationListener.Stub {
        private T mActivity;
        private RecentsView mRecentsView;

        private PinnedStackAnimationListener() {
        }

        public void setActivityAndRecentsView(T t, RecentsView recentsView) {
            this.mActivity = t;
            this.mRecentsView = recentsView;
        }

        public void onPipAnimationStarted() {
            Executors.MAIN_EXECUTOR.execute(new Runnable() {
                public final void run() {
                    RecentsView.PinnedStackAnimationListener.this.lambda$onPipAnimationStarted$0$RecentsView$PinnedStackAnimationListener();
                }
            });
        }

        public /* synthetic */ void lambda$onPipAnimationStarted$0$RecentsView$PinnedStackAnimationListener() {
            T t = this.mActivity;
            if (t != null) {
                t.clearForceInvisibleFlag(9);
            }
        }

        public void onPipResourceDimensionsChanged(int i, int i2) {
            RecentsView recentsView = this.mRecentsView;
            if (recentsView != null) {
                int unused = recentsView.mPipCornerRadius = i;
                int unused2 = this.mRecentsView.mPipShadowRadius = i2;
            }
        }

        public void onExpandPip() {
            Executors.MAIN_EXECUTOR.execute(new Runnable() {
                public final void run() {
                    RecentsView.PinnedStackAnimationListener.this.lambda$onExpandPip$1$RecentsView$PinnedStackAnimationListener();
                }
            });
        }

        public /* synthetic */ void lambda$onExpandPip$1$RecentsView$PinnedStackAnimationListener() {
            RecentsView recentsView = this.mRecentsView;
            if (recentsView != null && recentsView.mSizeStrategy.getTaskbarController() != null) {
                this.mRecentsView.mSizeStrategy.getTaskbarController().onExpandPip();
            }
        }
    }

    public static int getForegroundScrimDimColor(Context context) {
        return ColorUtils.blendARGB(ViewCompat.MEASURED_STATE_MASK, Themes.getAttrColor(context, R.attr.overviewScrimColor), 0.25f);
    }

    public RecentsAnimationController getRecentsAnimationController() {
        return this.mRecentsAnimationController;
    }

    public void updateLocusId() {
        String str;
        if (!this.mOverviewStateEnabled || !this.mActivity.isStarted()) {
            str = "Overview" + "|DISABLED";
        } else {
            str = "Overview" + "|ENABLED";
        }
        Executors.UI_HELPER_EXECUTOR.post(new Runnable(new LocusId(str)) {
            public final /* synthetic */ LocusId f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsView.this.lambda$updateLocusId$36$RecentsView(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$updateLocusId$36$RecentsView(LocusId locusId) {
        this.mActivity.setLocusContext(locusId, Bundle.EMPTY);
    }

    private List<String> getHicarPackageNames() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("com.huawei.hicar");
        arrayList.add("com.huiwei.dmsdpdevice");
        arrayList.add("com.huiwei.hisight");
        arrayList.add("com.huiwei.nearby");
        return arrayList;
    }

    public void enterSplitScreen() {
        Task task;
        SplitConfigurationOptions.SplitPositionOption splitPositionOption = new SplitConfigurationOptions.SplitPositionOption(R.drawable.ic_split_right, R.string.split_screen_position_right, 1, 0);
        TaskView taskViewAt = getTaskViewAt(0);
        if (taskViewAt != null && taskViewAt.getTask() != null && (task = taskViewAt.getTask()) != null) {
            if (task.isDockable) {
                Log.d(TAG, "enterSplitScreen packageName = " + taskViewAt.getTask().key.getPackageName());
                taskViewAt.initiateSplitSelect(splitPositionOption);
                return;
            }
            Toast.makeText(this.mContext, R.string.toast_split_app_unsupported, 1).show();
        }
    }

    public void killProcess(String str) {
        try {
            Runtime.getRuntime().exec("am force-stop " + str + " \n");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "killProcess: e = " + e.toString());
        }
    }
}
