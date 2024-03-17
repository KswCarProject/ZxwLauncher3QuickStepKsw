package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.animation.Interpolator;
import android.widget.Toast;
import android.window.PictureInPictureSurfaceTransaction;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.tracing.InputConsumerProto;
import com.android.launcher3.tracing.SwipeHandlerProto;
import com.android.launcher3.util.ActivityLifecycleCallbacksAdapter;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.WindowBounds;
import com.android.launcher3.util.window.RefreshRateTracker;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.AbsSwipeUpHandler;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.ActivityInitListener;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.util.GroupTask;
import com.android.quickstep.util.InputConsumerProxy;
import com.android.quickstep.util.InputProxyHandlerFactory;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.quickstep.util.ProtoTracer;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.util.SwipePipToHomeAnimator;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.util.VibratorWrapper;
import com.android.quickstep.util.WorkspaceRevealAnim;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.LatencyTrackerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbsSwipeUpHandler<T extends StatefulActivity<S>, Q extends RecentsView, S extends BaseState<S>> extends SwipeUpAnimationLogic implements View.OnApplyWindowInsetsListener, RecentsAnimationCallbacks.RecentsAnimationListener {
    public static final long HOME_DURATION = 250;
    private static final int LAUNCHER_UI_STATES;
    private static final int LOG_NO_OP_PAGE_INDEX = -1;
    private static final float MAX_QUICK_SWITCH_RECENTS_SCALE_PROGRESS = 0.07f;
    public static final long MAX_SWIPE_DURATION = 350;
    public static final float MIN_PROGRESS_FOR_OVERVIEW = 0.7f;
    public static final long RECENTS_ATTACH_DURATION = 300;
    private static final String SCREENSHOT_CAPTURED_EVT = "ScreenshotCaptured";
    private static final int STATE_APP_CONTROLLER_RECEIVED = getFlagForIndex(4, "STATE_APP_CONTROLLER_RECEIVED");
    private static final int STATE_CAPTURE_SCREENSHOT = getFlagForIndex(11, "STATE_CAPTURE_SCREENSHOT");
    private static final int STATE_CURRENT_TASK_FINISHED = getFlagForIndex(16, "STATE_CURRENT_TASK_FINISHED");
    private static final int STATE_FINISH_WITH_NO_END = getFlagForIndex(17, "STATE_FINISH_WITH_NO_END");
    private static final int STATE_GESTURE_CANCELLED = getFlagForIndex(9, "STATE_GESTURE_CANCELLED");
    private static final int STATE_GESTURE_COMPLETED = getFlagForIndex(10, "STATE_GESTURE_COMPLETED");
    private static final int STATE_GESTURE_STARTED = getFlagForIndex(8, "STATE_GESTURE_STARTED");
    protected static final int STATE_HANDLER_INVALIDATED = getFlagForIndex(7, "STATE_HANDLER_INVALIDATED");
    protected static final int STATE_LAUNCHER_BIND_TO_SERVICE;
    protected static final int STATE_LAUNCHER_DRAWN;
    protected static final int STATE_LAUNCHER_PRESENT;
    protected static final int STATE_LAUNCHER_STARTED;
    private static final String[] STATE_NAMES = null;
    private static final int STATE_RESUME_LAST_TASK = getFlagForIndex(14, "STATE_RESUME_LAST_TASK");
    private static final int STATE_SCALED_CONTROLLER_HOME = getFlagForIndex(5, "STATE_SCALED_CONTROLLER_HOME");
    private static final int STATE_SCALED_CONTROLLER_RECENTS = getFlagForIndex(6, "STATE_SCALED_CONTROLLER_RECENTS");
    protected static final int STATE_SCREENSHOT_CAPTURED = getFlagForIndex(12, "STATE_SCREENSHOT_CAPTURED");
    private static final int STATE_SCREENSHOT_VIEW_SHOWN = getFlagForIndex(13, "STATE_SCREENSHOT_VIEW_SHOWN");
    private static final int STATE_START_NEW_TASK = getFlagForIndex(15, "STATE_START_NEW_TASK");
    private static final float SWIPE_DURATION_MULTIPLIER = Math.min(1.4285715f, 3.3333333f);
    private static final String TAG = "AbsSwipeUpHandler";
    protected T mActivity;
    protected final ActivityInitListener mActivityInitListener;
    protected final BaseActivityInterface<S, T> mActivityInterface;
    /* access modifiers changed from: private */
    public final TaskStackChangeListener mActivityRestartListener = new TaskStackChangeListener() {
        public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo runningTaskInfo, boolean z, boolean z2, boolean z3) {
            if (runningTaskInfo.taskId == AbsSwipeUpHandler.this.mGestureState.getRunningTaskId() && runningTaskInfo.configuration.windowConfiguration.getActivityType() != 2) {
                AbsSwipeUpHandler.this.endRunningWindowAnim(true);
                TaskStackChangeListeners.getInstance().unregisterTaskStackListener(AbsSwipeUpHandler.this.mActivityRestartListener);
                ActivityManagerWrapper.getInstance().startActivityFromRecents(runningTaskInfo.taskId, (ActivityOptions) null);
            }
        }
    };
    private BaseActivityInterface.AnimationFactory mAnimationFactory = $$Lambda$AbsSwipeUpHandler$KPIpeK6N2SJdrVKKyqbx3soM0.INSTANCE;
    protected boolean mCanceled;
    private boolean mContinuingLastGesture;
    protected RecentsAnimationController mDeferredCleanupRecentsAnimationController;
    private ValueAnimator mDividerAnimator;
    private PointF mDownPos;
    protected Runnable mGestureEndCallback;
    private boolean mGestureStarted;
    private boolean mHasEndedLauncherTransition;
    /* access modifiers changed from: private */
    public boolean mHasMotionEverBeenPaused;
    protected final InputConsumerProxy mInputConsumerProxy;
    private boolean mIsLikelyToStartNewTask;
    /* access modifiers changed from: private */
    public boolean mIsMotionPaused;
    protected boolean mIsSwipingPipToHome;
    private long mLauncherFrameDrawnTime;
    private AnimatorControllerWithResistance mLauncherTransitionController;
    private final ActivityLifecycleCallbacksAdapter mLifecycleCallbacks = new ActivityLifecycleCallbacksAdapter() {
        public void onActivityDestroyed(Activity activity) {
            if (AbsSwipeUpHandler.this.mActivity == activity) {
                AbsSwipeUpHandler.this.mRecentsView = null;
                AbsSwipeUpHandler.this.mActivity = null;
            }
        }
    };
    private boolean mLogDirectionUpOrLeft = true;
    private final Runnable mOnDeferredActivityLaunch = new Runnable() {
        public final void run() {
            AbsSwipeUpHandler.this.onDeferredActivityLaunch();
        }
    };
    private final ViewTreeObserver.OnScrollChangedListener mOnRecentsScrollListener = new ViewTreeObserver.OnScrollChangedListener() {
        public final void onScrollChanged() {
            AbsSwipeUpHandler.this.onRecentsViewScroll();
        }
    };
    /* access modifiers changed from: private */
    public Animator mParallelRunningAnim;
    private boolean mPassedOverviewThreshold;
    private final float mQuickSwitchScaleScrollThreshold;
    protected RecentsAnimationController mRecentsAnimationController;
    private final ArrayList<Runnable> mRecentsAnimationStartCallbacks = new ArrayList<>();
    protected RecentsAnimationTargets mRecentsAnimationTargets;
    protected Q mRecentsView;
    private boolean mRecentsViewScrollLinked = false;
    private SwipeUpAnimationLogic.RunningWindowAnim[] mRunningWindowAnim;
    protected MultiStateCallback mStateCallback;
    private SwipePipToHomeAnimator mSwipePipToHomeAnimator;
    private final SwipePipToHomeAnimator[] mSwipePipToHomeAnimators = new SwipePipToHomeAnimator[2];
    protected final TaskAnimationManager mTaskAnimationManager;
    private ThumbnailData mTaskSnapshot;
    private final long mTouchTimeMs;
    private boolean mWasLauncherAlreadyVisible;

    public interface Factory {
        AbsSwipeUpHandler newHandler(GestureState gestureState, long j);
    }

    private static int getFlagForIndex(int i, String str) {
        return 1 << i;
    }

    static /* synthetic */ void lambda$new$0(long j) {
    }

    /* access modifiers changed from: protected */
    public abstract SwipeUpAnimationLogic.HomeAnimationFactory createHomeAnimationFactory(ArrayList<IBinder> arrayList, long j, boolean z, boolean z2, RemoteAnimationTargetCompat remoteAnimationTargetCompat);

    /* access modifiers changed from: protected */
    public abstract void finishRecentsControllerToHome(Runnable runnable);

    static {
        int flagForIndex = getFlagForIndex(0, "STATE_LAUNCHER_PRESENT");
        STATE_LAUNCHER_PRESENT = flagForIndex;
        int flagForIndex2 = getFlagForIndex(1, "STATE_LAUNCHER_STARTED");
        STATE_LAUNCHER_STARTED = flagForIndex2;
        int flagForIndex3 = getFlagForIndex(2, "STATE_LAUNCHER_DRAWN");
        STATE_LAUNCHER_DRAWN = flagForIndex3;
        int flagForIndex4 = getFlagForIndex(3, "STATE_LAUNCHER_BIND_TO_SERVICE");
        STATE_LAUNCHER_BIND_TO_SERVICE = flagForIndex4;
        LAUNCHER_UI_STATES = flagForIndex | flagForIndex3 | flagForIndex2 | flagForIndex4;
    }

    public AbsSwipeUpHandler(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, long j, boolean z, InputConsumerController inputConsumerController) {
        super(context, recentsAnimationDeviceState, gestureState);
        BaseActivityInterface<S, T> activityInterface = gestureState.getActivityInterface();
        this.mActivityInterface = activityInterface;
        this.mActivityInitListener = activityInterface.createActivityInitListener(new Predicate() {
            public final boolean test(Object obj) {
                return AbsSwipeUpHandler.this.onActivityInit((Boolean) obj);
            }
        });
        this.mInputConsumerProxy = new InputConsumerProxy(context, new Supplier() {
            public final Object get() {
                return AbsSwipeUpHandler.this.lambda$new$1$AbsSwipeUpHandler();
            }
        }, inputConsumerController, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.lambda$new$2$AbsSwipeUpHandler();
            }
        }, new InputProxyHandlerFactory(activityInterface, this.mGestureState));
        this.mTaskAnimationManager = taskAnimationManager;
        this.mTouchTimeMs = j;
        this.mContinuingLastGesture = z;
        this.mQuickSwitchScaleScrollThreshold = context.getResources().getDimension(R.dimen.quick_switch_scaling_scroll_threshold);
        initAfterSubclassConstructor();
        initStateCallbacks();
    }

    public /* synthetic */ Integer lambda$new$1$AbsSwipeUpHandler() {
        return Integer.valueOf(this.mRecentsView.getPagedViewOrientedState().getRecentsActivityRotation());
    }

    public /* synthetic */ void lambda$new$2$AbsSwipeUpHandler() {
        endRunningWindowAnim(this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME);
        endLauncherTransitionController();
    }

    private void initStateCallbacks() {
        MultiStateCallback multiStateCallback = new MultiStateCallback(STATE_NAMES);
        this.mStateCallback = multiStateCallback;
        int i = STATE_LAUNCHER_PRESENT;
        int i2 = STATE_GESTURE_STARTED;
        multiStateCallback.runOnceAtState(i | i2, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.onLauncherPresentAndGestureStarted();
            }
        });
        MultiStateCallback multiStateCallback2 = this.mStateCallback;
        int i3 = STATE_LAUNCHER_DRAWN;
        multiStateCallback2.runOnceAtState(i3 | i2, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.initializeLauncherAnimationController();
            }
        });
        this.mStateCallback.runOnceAtState(i | i3, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.launcherFrameDrawn();
            }
        });
        this.mStateCallback.runOnceAtState(STATE_LAUNCHER_STARTED | i | STATE_GESTURE_CANCELLED, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.resetStateForAnimationCancel();
            }
        });
        MultiStateCallback multiStateCallback3 = this.mStateCallback;
        int i4 = STATE_RESUME_LAST_TASK;
        int i5 = STATE_APP_CONTROLLER_RECEIVED;
        multiStateCallback3.runOnceAtState(i4 | i5, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.resumeLastTask();
            }
        });
        MultiStateCallback multiStateCallback4 = this.mStateCallback;
        int i6 = STATE_START_NEW_TASK;
        int i7 = STATE_SCREENSHOT_CAPTURED;
        multiStateCallback4.runOnceAtState(i6 | i7, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.startNewTask();
            }
        });
        MultiStateCallback multiStateCallback5 = this.mStateCallback;
        int i8 = STATE_CAPTURE_SCREENSHOT;
        multiStateCallback5.runOnceAtState(i | i5 | i3 | i8, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.switchToScreenshot();
            }
        });
        MultiStateCallback multiStateCallback6 = this.mStateCallback;
        int i9 = STATE_GESTURE_COMPLETED;
        int i10 = STATE_SCALED_CONTROLLER_RECENTS;
        multiStateCallback6.runOnceAtState(i7 | i9 | i10, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.finishCurrentTransitionToRecents();
            }
        });
        MultiStateCallback multiStateCallback7 = this.mStateCallback;
        int i11 = STATE_SCALED_CONTROLLER_HOME;
        multiStateCallback7.runOnceAtState(i7 | i9 | i11, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.finishCurrentTransitionToHome();
            }
        });
        MultiStateCallback multiStateCallback8 = this.mStateCallback;
        int i12 = STATE_CURRENT_TASK_FINISHED;
        multiStateCallback8.runOnceAtState(i11 | i12, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.reset();
            }
        });
        this.mStateCallback.runOnceAtState(i2 | i3 | i | i5 | i10 | i12 | i9, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.setupLauncherUiAfterSwipeUpToRecentsAnimation();
            }
        });
        this.mGestureState.runOnceAtState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.continueComputingRecentsScrollIfNecessary();
            }
        });
        this.mGestureState.runOnceAtState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED | GestureState.STATE_RECENTS_SCROLLING_FINISHED, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.onSettledOnEndTarget();
            }
        });
        MultiStateCallback multiStateCallback9 = this.mStateCallback;
        int i13 = STATE_HANDLER_INVALIDATED;
        multiStateCallback9.runOnceAtState(i13, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.invalidateHandler();
            }
        });
        this.mStateCallback.runOnceAtState(i | i13, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.invalidateHandlerWithLauncher();
            }
        });
        this.mStateCallback.runOnceAtState(i13 | i4, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.resetStateForAnimationCancel();
            }
        });
        this.mStateCallback.runOnceAtState(i13 | STATE_FINISH_WITH_NO_END, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.resetStateForAnimationCancel();
            }
        });
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mStateCallback.addChangeListener(i | i5 | STATE_SCREENSHOT_VIEW_SHOWN | i8, new Consumer() {
                public final void accept(Object obj) {
                    AbsSwipeUpHandler.this.lambda$initStateCallbacks$3$AbsSwipeUpHandler((Boolean) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$initStateCallbacks$3$AbsSwipeUpHandler(Boolean bool) {
        this.mRecentsView.setRunningTaskHidden(!bool.booleanValue());
    }

    /* access modifiers changed from: protected */
    public boolean onActivityInit(Boolean bool) {
        if (this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED)) {
            return false;
        }
        T createdActivity = this.mActivityInterface.getCreatedActivity();
        if (createdActivity != null) {
            initTransitionEndpoints(createdActivity.getDeviceProfile());
        }
        T createdActivity2 = this.mActivityInterface.getCreatedActivity();
        T t = this.mActivity;
        if (t == createdActivity2) {
            return true;
        }
        if (t != null) {
            if (this.mStateCallback.hasStates(STATE_GESTURE_COMPLETED)) {
                this.mGestureState.setState(GestureState.STATE_RECENTS_SCROLLING_FINISHED);
                return true;
            }
            int state = this.mStateCallback.getState() & (~LAUNCHER_UI_STATES);
            initStateCallbacks();
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(state);
        }
        this.mWasLauncherAlreadyVisible = bool.booleanValue();
        this.mActivity = createdActivity2;
        if (bool.booleanValue()) {
            this.mActivity.clearForceInvisibleFlag(9);
        } else {
            this.mActivity.addForceInvisibleFlag(9);
        }
        Q q = (RecentsView) createdActivity2.getOverviewPanel();
        this.mRecentsView = q;
        q.setOnPageTransitionEndCallback((Runnable) null);
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_LAUNCHER_PRESENT);
        if (bool.booleanValue()) {
            onLauncherStart();
        } else {
            createdActivity2.runOnceOnStart(new Runnable() {
                public final void run() {
                    AbsSwipeUpHandler.this.onLauncherStart();
                }
            });
        }
        this.mGestureState.runOnceAtState(GestureState.STATE_RECENTS_ANIMATION_CANCELED, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.lambda$onActivityInit$5$AbsSwipeUpHandler();
            }
        });
        setupRecentsViewUi();
        linkRecentsViewScroll();
        createdActivity2.runOnBindToTouchInteractionService(new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.onLauncherBindToService();
            }
        });
        this.mActivity.registerActivityLifecycleCallbacks(this.mLifecycleCallbacks);
        return true;
    }

    public /* synthetic */ void lambda$onActivityInit$5$AbsSwipeUpHandler() {
        HashMap<Integer, ThumbnailData> consumeRecentsAnimationCanceledSnapshot = this.mGestureState.consumeRecentsAnimationCanceledSnapshot();
        if (consumeRecentsAnimationCanceledSnapshot != null) {
            this.mRecentsView.switchToScreenshot(consumeRecentsAnimationCanceledSnapshot, new Runnable() {
                public final void run() {
                    AbsSwipeUpHandler.this.lambda$onActivityInit$4$AbsSwipeUpHandler();
                }
            });
            this.mRecentsView.onRecentsAnimationComplete();
        }
    }

    public /* synthetic */ void lambda$onActivityInit$4$AbsSwipeUpHandler() {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.cleanupScreenshot();
            return;
        }
        RecentsAnimationController recentsAnimationController2 = this.mDeferredCleanupRecentsAnimationController;
        if (recentsAnimationController2 != null) {
            recentsAnimationController2.cleanupScreenshot();
            this.mDeferredCleanupRecentsAnimationController = null;
        }
    }

    /* access modifiers changed from: protected */
    public boolean moveWindowWithRecentsScroll() {
        return this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME;
    }

    /* access modifiers changed from: private */
    public void onLauncherStart() {
        final T createdActivity = this.mActivityInterface.getCreatedActivity();
        if (this.mActivity == createdActivity && !this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED)) {
            this.mRecentsView.updateRecentsRotation();
            runActionOnRemoteHandles(new Consumer() {
                public final void accept(Object obj) {
                    AbsSwipeUpHandler.this.lambda$onLauncherStart$6$AbsSwipeUpHandler((RemoteTargetGluer.RemoteTargetHandle) obj);
                }
            });
            if (this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME) {
                $$Lambda$AbsSwipeUpHandler$itbi64vjBVEjTnDOd3PmJ7VXzI r1 = new Runnable() {
                    public final void run() {
                        AbsSwipeUpHandler.this.lambda$onLauncherStart$7$AbsSwipeUpHandler();
                    }
                };
                if (this.mWasLauncherAlreadyVisible) {
                    this.mStateCallback.runOnceAtState(STATE_GESTURE_STARTED, r1);
                } else {
                    r1.run();
                }
            }
            AbstractFloatingView.closeAllOpenViewsExcept(createdActivity, this.mWasLauncherAlreadyVisible, 256);
            if (this.mWasLauncherAlreadyVisible) {
                this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_LAUNCHER_DRAWN);
            } else {
                final Object beginSection = TraceHelper.INSTANCE.beginSection("WTS-init");
                final BaseDragLayer dragLayer = createdActivity.getDragLayer();
                dragLayer.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                    boolean mHandled = false;

                    public void onDraw() {
                        if (!this.mHandled) {
                            this.mHandled = true;
                            TraceHelper.INSTANCE.endSection(beginSection);
                            View view = dragLayer;
                            view.post(new Runnable(view) {
                                public final /* synthetic */ View f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    AbsSwipeUpHandler.AnonymousClass2.this.lambda$onDraw$0$AbsSwipeUpHandler$2(this.f$1);
                                }
                            });
                            if (createdActivity == AbsSwipeUpHandler.this.mActivity) {
                                AbsSwipeUpHandler.this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(AbsSwipeUpHandler.STATE_LAUNCHER_DRAWN);
                            }
                        }
                    }

                    public /* synthetic */ void lambda$onDraw$0$AbsSwipeUpHandler$2(View view) {
                        view.getViewTreeObserver().removeOnDrawListener(this);
                    }
                });
            }
            createdActivity.getRootView().setOnApplyWindowInsetsListener(this);
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_LAUNCHER_STARTED);
        }
    }

    public /* synthetic */ void lambda$onLauncherStart$6$AbsSwipeUpHandler(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTaskViewSimulator().setOrientationState(this.mRecentsView.getPagedViewOrientedState());
    }

    public /* synthetic */ void lambda$onLauncherStart$7$AbsSwipeUpHandler() {
        this.mAnimationFactory = this.mActivityInterface.prepareRecentsUI(this.mDeviceState, this.mWasLauncherAlreadyVisible, new Consumer() {
            public final void accept(Object obj) {
                AbsSwipeUpHandler.this.onAnimatorPlaybackControllerCreated((AnimatorControllerWithResistance) obj);
            }
        });
        maybeUpdateRecentsAttachedState(false);
        if (this.mGestureState.getEndTarget() != null) {
            this.mAnimationFactory.setEndTarget(this.mGestureState.getEndTarget());
        }
    }

    /* access modifiers changed from: private */
    public void onLauncherBindToService() {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_LAUNCHER_BIND_TO_SERVICE);
        flushOnRecentsAnimationAndLauncherBound();
    }

    /* access modifiers changed from: private */
    public void onLauncherPresentAndGestureStarted() {
        setupRecentsViewUi();
        this.mGestureState.getActivityInterface().setOnDeferredActivityLaunchCallback(this.mOnDeferredActivityLaunch);
        this.mGestureState.runOnceAtState(GestureState.STATE_END_TARGET_SET, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.lambda$onLauncherPresentAndGestureStarted$8$AbsSwipeUpHandler();
            }
        });
        notifyGestureStartedAsync();
    }

    public /* synthetic */ void lambda$onLauncherPresentAndGestureStarted$8$AbsSwipeUpHandler() {
        this.mDeviceState.getRotationTouchHelper().onEndTargetCalculated(this.mGestureState.getEndTarget(), this.mActivityInterface);
    }

    /* access modifiers changed from: private */
    public void onDeferredActivityLaunch() {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mActivityInterface.switchRunningTaskViewToScreenshot((HashMap<Integer, ThumbnailData>) null, new Runnable() {
                public final void run() {
                    AbsSwipeUpHandler.this.lambda$onDeferredActivityLaunch$9$AbsSwipeUpHandler();
                }
            });
        } else {
            this.mTaskAnimationManager.finishRunningRecentsAnimation(true);
        }
    }

    public /* synthetic */ void lambda$onDeferredActivityLaunch$9$AbsSwipeUpHandler() {
        this.mTaskAnimationManager.finishRunningRecentsAnimation(true);
    }

    private void setupRecentsViewUi() {
        if (this.mContinuingLastGesture) {
            updateSysUiFlags(this.mCurrentShift.value);
        } else {
            notifyGestureAnimationStartToRecents();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyGestureAnimationStartToRecents() {
        Task[] taskArr;
        if (this.mIsSwipeForStagedSplit) {
            taskArr = this.mGestureState.getRunningTask().getPlaceholderTasks(TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getRunningSplitTaskIds());
        } else {
            taskArr = this.mGestureState.getRunningTask().getPlaceholderTasks();
        }
        this.mRecentsView.onGestureAnimationStart(taskArr, this.mDeviceState.getRotationTouchHelper());
    }

    /* access modifiers changed from: private */
    public void launcherFrameDrawn() {
        this.mLauncherFrameDrawnTime = SystemClock.uptimeMillis();
    }

    /* access modifiers changed from: private */
    public void initializeLauncherAnimationController() {
        buildAnimationController();
        Object beginSection = TraceHelper.INSTANCE.beginSection("logToggleRecents", 2);
        LatencyTrackerCompat.logToggleRecents(this.mContext, (int) (this.mLauncherFrameDrawnTime - this.mTouchTimeMs));
        TraceHelper.INSTANCE.endSection(beginSection);
        RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getThumbnailCache().getHighResLoadingState().setVisible(true);
    }

    public MotionPauseDetector.OnMotionPauseListener getMotionPauseListener() {
        return new MotionPauseDetector.OnMotionPauseListener() {
            public void onMotionPauseDetected() {
                boolean unused = AbsSwipeUpHandler.this.mHasMotionEverBeenPaused = true;
                AbsSwipeUpHandler.this.maybeUpdateRecentsAttachedState(true, true);
                AbsSwipeUpHandler.this.performHapticFeedback();
            }

            public void onMotionPauseChanged(boolean z) {
                boolean unused = AbsSwipeUpHandler.this.mIsMotionPaused = z;
            }
        };
    }

    private void maybeUpdateRecentsAttachedState() {
        maybeUpdateRecentsAttachedState(true);
    }

    /* access modifiers changed from: private */
    public void maybeUpdateRecentsAttachedState(boolean z) {
        maybeUpdateRecentsAttachedState(z, false);
    }

    /* access modifiers changed from: private */
    public void maybeUpdateRecentsAttachedState(boolean z, boolean z2) {
        if (this.mDeviceState.isFullyGesturalNavMode() && this.mRecentsView != null) {
            RecentsAnimationTargets recentsAnimationTargets = this.mRecentsAnimationTargets;
            RemoteAnimationTargetCompat findTask = recentsAnimationTargets != null ? recentsAnimationTargets.findTask(this.mGestureState.getRunningTaskId()) : null;
            boolean z3 = true;
            if (this.mGestureState.getEndTarget() != null) {
                z3 = this.mGestureState.getEndTarget().recentsAttachedToAppWindow;
            } else if ((!this.mContinuingLastGesture || this.mRecentsView.getRunningTaskIndex() == this.mRecentsView.getNextPage()) && ((findTask == null || !isNotInRecents(findTask)) && !this.mHasMotionEverBeenPaused && !this.mIsLikelyToStartNewTask)) {
                z3 = false;
            }
            if (z2 && !this.mAnimationFactory.hasRecentsEverAttachedToAppWindow() && z3) {
                this.mRecentsView.moveFocusedTaskToFront();
            }
            this.mAnimationFactory.setRecentsAttachedToAppWindow(z3, z);
            if (z) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        AbsSwipeUpHandler.this.lambda$maybeUpdateRecentsAttachedState$10$AbsSwipeUpHandler(valueAnimator);
                    }
                });
                ofFloat.setDuration(300).start();
                MultiStateCallback multiStateCallback = this.mStateCallback;
                int i = STATE_HANDLER_INVALIDATED;
                Objects.requireNonNull(ofFloat);
                multiStateCallback.runOnceAtState(i, new Runnable(ofFloat) {
                    public final /* synthetic */ ValueAnimator f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.cancel();
                    }
                });
                return;
            }
            applyScrollAndTransform();
        }
    }

    public /* synthetic */ void lambda$maybeUpdateRecentsAttachedState$10$AbsSwipeUpHandler(ValueAnimator valueAnimator) {
        SwipeUpAnimationLogic.RunningWindowAnim[] runningWindowAnimArr = this.mRunningWindowAnim;
        if (runningWindowAnimArr == null || runningWindowAnimArr.length == 0) {
            applyScrollAndTransform();
        }
    }

    public void setIsLikelyToStartNewTask(boolean z) {
        setIsLikelyToStartNewTask(z, true);
    }

    private void setIsLikelyToStartNewTask(boolean z, boolean z2) {
        if (this.mIsLikelyToStartNewTask != z) {
            this.mIsLikelyToStartNewTask = z;
            maybeUpdateRecentsAttachedState(z2);
        }
    }

    private void buildAnimationController() {
        if (canCreateNewOrUpdateExistingLauncherTransitionController()) {
            initTransitionEndpoints(this.mActivity.getDeviceProfile());
            this.mAnimationFactory.createActivityInterface((long) this.mTransitionDragLength);
        }
    }

    private boolean canCreateNewOrUpdateExistingLauncherTransitionController() {
        return this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.HOME && !this.mHasEndedLauncherTransition;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        WindowInsets onApplyWindowInsets = view.onApplyWindowInsets(windowInsets);
        buildAnimationController();
        updateFinalShift();
        return onApplyWindowInsets;
    }

    /* access modifiers changed from: private */
    public void onAnimatorPlaybackControllerCreated(AnimatorControllerWithResistance animatorControllerWithResistance) {
        boolean z = this.mLauncherTransitionController == null;
        this.mLauncherTransitionController = animatorControllerWithResistance;
        if (z) {
            this.mStateCallback.runOnceAtState(STATE_GESTURE_STARTED, new Runnable() {
                public final void run() {
                    AbsSwipeUpHandler.this.lambda$onAnimatorPlaybackControllerCreated$11$AbsSwipeUpHandler();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onAnimatorPlaybackControllerCreated$11$AbsSwipeUpHandler() {
        this.mLauncherTransitionController.getNormalController().dispatchOnStart();
        updateLauncherTransitionProgress();
    }

    public Intent getLaunchIntent() {
        return this.mGestureState.getOverviewIntent();
    }

    public void updateFinalShift() {
        boolean z = this.mCurrentShift.value >= 0.7f;
        if (z != this.mPassedOverviewThreshold) {
            this.mPassedOverviewThreshold = z;
            if (this.mDeviceState.isTwoButtonNavMode() && !this.mGestureState.isHandlingAtomicEvent()) {
                performHapticFeedback();
            }
        }
        updateSysUiFlags(this.mCurrentShift.value);
        applyScrollAndTransform();
        updateLauncherTransitionProgress();
    }

    private void updateLauncherTransitionProgress() {
        if (this.mLauncherTransitionController != null && canCreateNewOrUpdateExistingLauncherTransitionController()) {
            this.mLauncherTransitionController.setProgress(Math.max(this.mCurrentShift.value, getScaleProgressDueToScroll()), this.mDragLengthFactor);
        }
    }

    private void updateSysUiFlags(float f) {
        Q q;
        int i;
        if (this.mRecentsAnimationController != null && (q = this.mRecentsView) != null) {
            TaskView runningTaskView = q.getRunningTaskView();
            TaskView taskViewNearestToCenterOfScreen = this.mRecentsView.getTaskViewNearestToCenterOfScreen();
            if (taskViewNearestToCenterOfScreen == null) {
                i = 0;
            } else {
                i = taskViewNearestToCenterOfScreen.getThumbnail().getSysUiStatusNavFlags();
            }
            boolean z = true;
            boolean z2 = f > 0.14999998f;
            boolean z3 = taskViewNearestToCenterOfScreen != runningTaskView;
            RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
            if (!z2 && (!z3 || i == 0)) {
                z = false;
            }
            recentsAnimationController.setUseLauncherSystemBarFlags(z);
            this.mRecentsAnimationController.setSplitScreenMinimized(this.mContext, z2);
            this.mRecentsAnimationController.setWillFinishToHome(z2);
            if (z2) {
                this.mActivity.getSystemUiController().updateUiState(3, 0);
            } else {
                this.mActivity.getSystemUiController().updateUiState(3, i);
            }
        }
    }

    public void onRecentsAnimationStart(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
        DeviceProfile deviceProfile;
        super.onRecentsAnimationStart(recentsAnimationController, recentsAnimationTargets);
        ActiveGestureLog.INSTANCE.addLog("startRecentsAnimationCallback", recentsAnimationTargets.apps.length);
        this.mRemoteTargetHandles = this.mTargetGluer.assignTargetsForSplitScreen(this.mContext, (RemoteAnimationTargets) recentsAnimationTargets);
        this.mRecentsAnimationController = recentsAnimationController;
        this.mRecentsAnimationTargets = recentsAnimationTargets;
        if (this.mActivity == null) {
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = recentsAnimationTargets.apps[0];
            RecentsOrientedState orientationState = this.mRemoteTargetHandles[0].getTaskViewSimulator().getOrientationState();
            DeviceProfile launcherDeviceProfile = orientationState.getLauncherDeviceProfile();
            if (recentsAnimationTargets.minimizedHomeBounds == null || remoteAnimationTargetCompat == null) {
                deviceProfile = launcherDeviceProfile.copy(this.mContext);
            } else {
                deviceProfile = launcherDeviceProfile.getMultiWindowProfile(this.mContext, new WindowBounds(this.mActivityInterface.getOverviewWindowBounds(recentsAnimationTargets.minimizedHomeBounds, remoteAnimationTargetCompat), recentsAnimationTargets.homeContentInsets));
            }
            deviceProfile.updateInsets(recentsAnimationTargets.homeContentInsets);
            deviceProfile.updateIsSeascape(this.mContext);
            initTransitionEndpoints(deviceProfile);
            orientationState.setMultiWindowMode(deviceProfile.isMultiWindowMode);
        }
        flushOnRecentsAnimationAndLauncherBound();
        MultiStateCallback multiStateCallback = this.mStateCallback;
        int i = STATE_APP_CONTROLLER_RECEIVED;
        multiStateCallback.runOnceAtState(STATE_GESTURE_STARTED | i, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.startInterceptingTouchesForGesture();
            }
        });
        this.mStateCallback.setStateOnUiThread(i);
        this.mPassedOverviewThreshold = false;
    }

    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
        ActiveGestureLog.INSTANCE.addLog("cancelRecentsAnimation");
        this.mActivityInitListener.unregister();
        this.mDeferredCleanupRecentsAnimationController = this.mRecentsAnimationController;
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_CANCELLED | STATE_HANDLER_INVALIDATED);
        if (this.mRecentsAnimationTargets != null) {
            setDividerShown(true, false);
        }
        this.mRecentsAnimationController = null;
        this.mRecentsAnimationTargets = null;
        Q q = this.mRecentsView;
        if (q != null) {
            q.setRecentsAnimationTargets((RecentsAnimationController) null, (RecentsAnimationTargets) null);
        }
    }

    public void onGestureStarted(boolean z) {
        this.mActivityInterface.closeOverlay();
        TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
        Q q = this.mRecentsView;
        if (q != null) {
            q.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                boolean mHandled = false;

                public void onDraw() {
                    if (!this.mHandled) {
                        this.mHandled = true;
                        InteractionJankMonitorWrapper.begin((View) AbsSwipeUpHandler.this.mRecentsView, 11, 2000);
                        InteractionJankMonitorWrapper.begin(AbsSwipeUpHandler.this.mRecentsView, 9);
                        AbsSwipeUpHandler.this.mRecentsView.post(new Runnable() {
                            public final void run() {
                                AbsSwipeUpHandler.AnonymousClass4.this.lambda$onDraw$0$AbsSwipeUpHandler$4();
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$onDraw$0$AbsSwipeUpHandler$4() {
                    AbsSwipeUpHandler.this.mRecentsView.getViewTreeObserver().removeOnDrawListener(this);
                }
            });
        }
        notifyGestureStartedAsync();
        setIsLikelyToStartNewTask(z, false);
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_STARTED);
        this.mGestureStarted = true;
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).notifySwipeUpGestureStarted();
    }

    private void notifyGestureStartedAsync() {
        T t = this.mActivity;
        if (t != null) {
            t.clearForceInvisibleFlag(9);
        }
    }

    public void onGestureCancelled() {
        updateDisplacement(0.0f);
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_COMPLETED);
        handleNormalGestureEnd(0.0f, false, new PointF(), true);
    }

    public void onGestureEnded(float f, PointF pointF, PointF pointF2) {
        boolean z = true;
        boolean z2 = this.mGestureStarted && !this.mIsMotionPaused && Math.abs(f) > this.mContext.getResources().getDimension(R.dimen.quickstep_fling_threshold_speed);
        this.mStateCallback.setStateOnUiThread(STATE_GESTURE_COMPLETED);
        if (Math.abs(pointF.y) > Math.abs(pointF.x)) {
            if (pointF.y >= 0.0f) {
                z = false;
            }
            this.mLogDirectionUpOrLeft = z;
        } else {
            if (pointF.x >= 0.0f) {
                z = false;
            }
            this.mLogDirectionUpOrLeft = z;
        }
        this.mDownPos = pointF2;
        $$Lambda$AbsSwipeUpHandler$UYzl2tE9JNRdbqyYC8zC9y0tXc r8 = new Runnable(f, z2, pointF) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ PointF f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                AbsSwipeUpHandler.this.lambda$onGestureEnded$12$AbsSwipeUpHandler(this.f$1, this.f$2, this.f$3);
            }
        };
        Q q = this.mRecentsView;
        if (q != null) {
            q.runOnPageScrollsInitialized(r8);
        } else {
            r8.run();
        }
    }

    public /* synthetic */ void lambda$onGestureEnded$12$AbsSwipeUpHandler(float f, boolean z, PointF pointF) {
        handleNormalGestureEnd(f, z, pointF, false);
    }

    /* access modifiers changed from: private */
    public void endRunningWindowAnim(boolean z) {
        SwipeUpAnimationLogic.RunningWindowAnim[] runningWindowAnimArr = this.mRunningWindowAnim;
        if (runningWindowAnimArr != null) {
            int i = 0;
            if (z) {
                int length = runningWindowAnimArr.length;
                while (i < length) {
                    SwipeUpAnimationLogic.RunningWindowAnim runningWindowAnim = runningWindowAnimArr[i];
                    if (runningWindowAnim != null) {
                        runningWindowAnim.cancel();
                    }
                    i++;
                }
            } else {
                int length2 = runningWindowAnimArr.length;
                while (i < length2) {
                    SwipeUpAnimationLogic.RunningWindowAnim runningWindowAnim2 = runningWindowAnimArr[i];
                    if (runningWindowAnim2 != null) {
                        runningWindowAnim2.end();
                    }
                    i++;
                }
            }
        }
        Animator animator = this.mParallelRunningAnim;
        if (animator != null) {
            animator.end();
        }
    }

    /* access modifiers changed from: private */
    public void onSettledOnEndTarget() {
        maybeUpdateRecentsAttachedState(false);
        GestureState.GestureEndTarget endTarget = this.mGestureState.getEndTarget();
        View onSettledOnEndTarget = this.mActivityInterface.onSettledOnEndTarget(endTarget);
        if (endTarget != GestureState.GestureEndTarget.NEW_TASK) {
            InteractionJankMonitorWrapper.cancel(11);
        }
        if (endTarget != GestureState.GestureEndTarget.HOME) {
            InteractionJankMonitorWrapper.cancel(9);
        }
        int i = AnonymousClass11.$SwitchMap$com$android$quickstep$GestureState$GestureEndTarget[endTarget.ordinal()];
        if (i == 1) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_SCALED_CONTROLLER_HOME | STATE_CAPTURE_SCREENSHOT);
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).notifySwipeToHomeFinished();
        } else if (i == 2) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_SCALED_CONTROLLER_RECENTS | STATE_CAPTURE_SCREENSHOT | STATE_SCREENSHOT_VIEW_SHOWN);
        } else if (i == 3) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_START_NEW_TASK | STATE_CAPTURE_SCREENSHOT);
        } else if (i == 4) {
            if (onSettledOnEndTarget != null) {
                ViewUtils.postFrameDrawn(onSettledOnEndTarget, new Runnable() {
                    public final void run() {
                        AbsSwipeUpHandler.this.lambda$onSettledOnEndTarget$13$AbsSwipeUpHandler();
                    }
                });
            } else {
                this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RESUME_LAST_TASK);
            }
            if (this.mRecentsAnimationTargets != null) {
                setDividerShown(true, true);
            }
        }
        ActiveGestureLog.INSTANCE.addLog("onSettledOnEndTarget " + endTarget);
    }

    /* renamed from: com.android.quickstep.AbsSwipeUpHandler$11  reason: invalid class name */
    static /* synthetic */ class AnonymousClass11 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                com.android.quickstep.GestureState$GestureEndTarget[] r0 = com.android.quickstep.GestureState.GestureEndTarget.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget = r0
                com.android.quickstep.GestureState$GestureEndTarget r1 = com.android.quickstep.GestureState.GestureEndTarget.HOME     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.quickstep.GestureState$GestureEndTarget r1 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.quickstep.GestureState$GestureEndTarget r1 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$quickstep$GestureState$GestureEndTarget     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.quickstep.GestureState$GestureEndTarget r1 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.AbsSwipeUpHandler.AnonymousClass11.<clinit>():void");
        }
    }

    public /* synthetic */ void lambda$onSettledOnEndTarget$13$AbsSwipeUpHandler() {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RESUME_LAST_TASK);
    }

    /* access modifiers changed from: protected */
    public boolean handleTaskAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        if (this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED)) {
            return false;
        }
        boolean anyMatch = Arrays.stream(remoteAnimationTargetCompatArr).anyMatch(new Predicate() {
            public final boolean test(Object obj) {
                return AbsSwipeUpHandler.this.lambda$handleTaskAppeared$14$AbsSwipeUpHandler((RemoteAnimationTargetCompat) obj);
            }
        });
        if (!this.mStateCallback.hasStates(STATE_START_NEW_TASK) || !anyMatch) {
            return false;
        }
        reset();
        return true;
    }

    public /* synthetic */ boolean lambda$handleTaskAppeared$14$AbsSwipeUpHandler(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return remoteAnimationTargetCompat.taskId == this.mGestureState.getLastStartedTaskId();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0027, code lost:
        if (r3 != r0) goto L_0x0017;
     */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:77:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.quickstep.GestureState.GestureEndTarget calculateEndTarget(android.graphics.PointF r8, float r9, boolean r10, boolean r11) {
        /*
            r7 = this;
            com.android.quickstep.GestureState r0 = r7.mGestureState
            boolean r0 = r0.isHandlingAtomicEvent()
            if (r0 == 0) goto L_0x000b
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            return r8
        L_0x000b:
            Q r0 = r7.mRecentsView
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x002a
            boolean r0 = r7.hasTargets()
            if (r0 != 0) goto L_0x0019
        L_0x0017:
            r0 = r1
            goto L_0x002b
        L_0x0019:
            Q r0 = r7.mRecentsView
            int r0 = r0.getRunningTaskIndex()
            Q r3 = r7.mRecentsView
            int r3 = r3.getNextPage()
            if (r0 < 0) goto L_0x002a
            if (r3 == r0) goto L_0x002a
            goto L_0x0017
        L_0x002a:
            r0 = r2
        L_0x002b:
            com.android.quickstep.AnimatedFloat r3 = r7.mCurrentShift
            float r3 = r3.value
            r4 = 1060320051(0x3f333333, float:0.7)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x0038
            r3 = r1
            goto L_0x0039
        L_0x0038:
            r3 = r2
        L_0x0039:
            float r4 = r8.x
            float r4 = java.lang.Math.abs(r4)
            android.content.Context r5 = r7.mContext
            android.content.res.Resources r5 = r5.getResources()
            r6 = 2131165905(0x7f0702d1, float:1.794604E38)
            float r5 = r5.getDimension(r6)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x0052
            r4 = r1
            goto L_0x0053
        L_0x0052:
            r4 = r2
        L_0x0053:
            if (r10 != 0) goto L_0x0094
            if (r11 == 0) goto L_0x005b
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            goto L_0x00dd
        L_0x005b:
            com.android.quickstep.RecentsAnimationDeviceState r8 = r7.mDeviceState
            boolean r8 = r8.isFullyGesturalNavMode()
            if (r8 == 0) goto L_0x0083
            if (r0 == 0) goto L_0x006b
            if (r4 == 0) goto L_0x006b
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto L_0x00dd
        L_0x006b:
            boolean r8 = r7.mIsMotionPaused
            if (r8 == 0) goto L_0x0073
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            goto L_0x00dd
        L_0x0073:
            if (r0 == 0) goto L_0x0079
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto L_0x00dd
        L_0x0079:
            if (r3 != 0) goto L_0x007f
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            goto L_0x00dd
        L_0x007f:
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.HOME
            goto L_0x00dd
        L_0x0083:
            if (r3 == 0) goto L_0x008c
            boolean r8 = r7.mGestureStarted
            if (r8 == 0) goto L_0x008c
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            goto L_0x00dd
        L_0x008c:
            if (r0 == 0) goto L_0x0091
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto L_0x00dd
        L_0x0091:
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            goto L_0x00dd
        L_0x0094:
            r10 = 0
            int r10 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x009b
            r10 = r1
            goto L_0x009c
        L_0x009b:
            r10 = r2
        L_0x009c:
            if (r0 == 0) goto L_0x00ad
            float r8 = r8.x
            float r8 = java.lang.Math.abs(r8)
            float r9 = java.lang.Math.abs(r9)
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 <= 0) goto L_0x00ad
            goto L_0x00ae
        L_0x00ad:
            r1 = r2
        L_0x00ae:
            com.android.quickstep.RecentsAnimationDeviceState r8 = r7.mDeviceState
            boolean r8 = r8.isFullyGesturalNavMode()
            if (r8 == 0) goto L_0x00bd
            if (r10 == 0) goto L_0x00bd
            if (r1 != 0) goto L_0x00bd
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.HOME
            goto L_0x00dd
        L_0x00bd:
            com.android.quickstep.RecentsAnimationDeviceState r8 = r7.mDeviceState
            boolean r8 = r8.isFullyGesturalNavMode()
            if (r8 == 0) goto L_0x00ca
            if (r10 == 0) goto L_0x00ca
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto L_0x00dd
        L_0x00ca:
            if (r10 == 0) goto L_0x00d6
            if (r3 != 0) goto L_0x00d3
            if (r1 == 0) goto L_0x00d3
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto L_0x00dd
        L_0x00d3:
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            goto L_0x00dd
        L_0x00d6:
            if (r0 == 0) goto L_0x00db
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.NEW_TASK
            goto L_0x00dd
        L_0x00db:
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
        L_0x00dd:
            com.android.quickstep.RecentsAnimationDeviceState r9 = r7.mDeviceState
            boolean r9 = r9.isOverviewDisabled()
            if (r9 == 0) goto L_0x00ef
            com.android.quickstep.GestureState$GestureEndTarget r9 = com.android.quickstep.GestureState.GestureEndTarget.RECENTS
            if (r8 == r9) goto L_0x00ed
            com.android.quickstep.GestureState$GestureEndTarget r9 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            if (r8 != r9) goto L_0x00ef
        L_0x00ed:
            com.android.quickstep.GestureState$GestureEndTarget r8 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
        L_0x00ef:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.AbsSwipeUpHandler.calculateEndTarget(android.graphics.PointF, float, boolean, boolean):com.android.quickstep.GestureState$GestureEndTarget");
    }

    private void handleNormalGestureEnd(float f, boolean z, PointF pointF, boolean z2) {
        long j;
        float f2;
        Interpolator interpolator;
        Q q;
        float f3 = this.mCurrentShift.value;
        GestureState.GestureEndTarget calculateEndTarget = calculateEndTarget(pointF, f, z, z2);
        boolean z3 = false;
        this.mGestureState.setEndTarget(calculateEndTarget, false);
        this.mAnimationFactory.setEndTarget(calculateEndTarget);
        float f4 = calculateEndTarget.isLauncher ? 1.0f : 0.0f;
        if (!z) {
            j = Math.min(350, (long) Math.abs(Math.round((f4 - f3) * 350.0f * SWIPE_DURATION_MULTIPLIER)));
            f2 = f3;
        } else {
            float boundToRange = Utilities.boundToRange(f3 - ((pointF.y * ((float) RefreshRateTracker.getSingleFrameMs(this.mContext))) / ((float) this.mTransitionDragLength)), 0.0f, this.mDragLengthFactor);
            if (this.mTransitionDragLength > 0) {
                f2 = boundToRange;
                j = Math.min(350, ((long) Math.round(Math.abs(((f4 - f3) * ((float) this.mTransitionDragLength)) / pointF.y))) * 2);
            } else {
                f2 = boundToRange;
                j = 350;
            }
        }
        if (this.mActivityInterface.stateFromGestureEndTarget(calculateEndTarget).displayOverviewTasksAsGrid(this.mDp)) {
            interpolator = Interpolators.ACCEL_DEACCEL;
        } else if (calculateEndTarget == GestureState.GestureEndTarget.RECENTS) {
            interpolator = Interpolators.OVERSHOOT_1_2;
        } else {
            interpolator = Interpolators.DEACCEL;
        }
        Interpolator interpolator2 = interpolator;
        if (calculateEndTarget.isLauncher) {
            this.mInputConsumerProxy.enable();
        }
        boolean z4 = true;
        if (calculateEndTarget == GestureState.GestureEndTarget.HOME) {
            j = 250;
            RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
            if (recentsAnimationController != null) {
                recentsAnimationController.detachNavigationBarFromApp(true);
            }
        } else if (calculateEndTarget == GestureState.GestureEndTarget.RECENTS && (q = this.mRecentsView) != null) {
            int destinationPage = q.getDestinationPage();
            if (destinationPage == -1) {
                Log.e(TAG, "RecentsView destination page is invalid", new IllegalStateException());
            }
            if (this.mRecentsView.getNextPage() != destinationPage) {
                this.mRecentsView.snapToPage(destinationPage, Math.toIntExact(j));
                z3 = true;
            }
            if (((long) this.mRecentsView.getScroller().getDuration()) > 350) {
                Q q2 = this.mRecentsView;
                q2.snapToPage(q2.getNextPage(), WorkspaceRevealAnim.DURATION_MS);
            } else {
                z4 = z3;
            }
            if (!this.mGestureState.isHandlingAtomicEvent() || z4) {
                j = Math.max(j, (long) this.mRecentsView.getScroller().getDuration());
            }
        }
        long j2 = j;
        Q q3 = this.mRecentsView;
        if (q3 != null) {
            q3.setOnPageTransitionEndCallback(new Runnable() {
                public final void run() {
                    AbsSwipeUpHandler.this.lambda$handleNormalGestureEnd$15$AbsSwipeUpHandler();
                }
            });
        } else {
            this.mGestureState.setState(GestureState.STATE_RECENTS_SCROLLING_FINISHED);
        }
        animateToProgress(f2, f4, j2, interpolator2, calculateEndTarget, pointF);
    }

    public /* synthetic */ void lambda$handleNormalGestureEnd$15$AbsSwipeUpHandler() {
        this.mGestureState.setState(GestureState.STATE_RECENTS_SCROLLING_FINISHED);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0056, code lost:
        r4 = r3.mRecentsView;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doLogGesture(com.android.quickstep.GestureState.GestureEndTarget r4, com.android.quickstep.views.TaskView r5) {
        /*
            r3 = this;
            com.android.launcher3.DeviceProfile r0 = r3.mDp
            if (r0 == 0) goto L_0x0067
            com.android.launcher3.DeviceProfile r0 = r3.mDp
            boolean r0 = r0.isGestureMode
            if (r0 == 0) goto L_0x0067
            android.graphics.PointF r0 = r3.mDownPos
            if (r0 != 0) goto L_0x000f
            goto L_0x0067
        L_0x000f:
            int[] r0 = com.android.quickstep.AbsSwipeUpHandler.AnonymousClass11.$SwitchMap$com$android$quickstep$GestureState$GestureEndTarget
            int r1 = r4.ordinal()
            r0 = r0[r1]
            r1 = 1
            if (r0 == r1) goto L_0x0033
            r2 = 2
            if (r0 == r2) goto L_0x0030
            r2 = 3
            if (r0 == r2) goto L_0x0026
            r2 = 4
            if (r0 == r2) goto L_0x0026
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r0 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.IGNORE
            goto L_0x0035
        L_0x0026:
            boolean r0 = r3.mLogDirectionUpOrLeft
            if (r0 == 0) goto L_0x002d
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r0 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_QUICKSWITCH_LEFT
            goto L_0x0035
        L_0x002d:
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r0 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_QUICKSWITCH_RIGHT
            goto L_0x0035
        L_0x0030:
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r0 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_OVERVIEW_GESTURE
            goto L_0x0035
        L_0x0033:
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r0 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_HOME_GESTURE
        L_0x0035:
            android.content.Context r2 = r3.mContext
            com.android.launcher3.logging.StatsLogManager r2 = com.android.launcher3.logging.StatsLogManager.newInstance(r2)
            com.android.launcher3.logging.StatsLogManager$StatsLogger r2 = r2.logger()
            com.android.launcher3.logging.StatsLogManager$StatsLogger r1 = r2.withSrcState(r1)
            int r2 = r4.containerType
            com.android.launcher3.logging.StatsLogManager$StatsLogger r1 = r1.withDstState(r2)
            if (r5 == 0) goto L_0x0052
            com.android.launcher3.model.data.WorkspaceItemInfo r5 = r5.getItemInfo()
            r1.withItemInfo(r5)
        L_0x0052:
            com.android.quickstep.GestureState$GestureEndTarget r5 = com.android.quickstep.GestureState.GestureEndTarget.LAST_TASK
            if (r4 == r5) goto L_0x0060
            Q r4 = r3.mRecentsView
            if (r4 != 0) goto L_0x005b
            goto L_0x0060
        L_0x005b:
            int r4 = r4.getNextPage()
            goto L_0x0061
        L_0x0060:
            r4 = -1
        L_0x0061:
            r1.withRank(r4)
            r1.log(r0)
        L_0x0067:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.AbsSwipeUpHandler.doLogGesture(com.android.quickstep.GestureState$GestureEndTarget, com.android.quickstep.views.TaskView):void");
    }

    private void animateToProgress(float f, float f2, long j, Interpolator interpolator, GestureState.GestureEndTarget gestureEndTarget, PointF pointF) {
        runOnRecentsAnimationAndLauncherBound(new Runnable(f, f2, j, interpolator, gestureEndTarget, pointF) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ long f$3;
            public final /* synthetic */ Interpolator f$4;
            public final /* synthetic */ GestureState.GestureEndTarget f$5;
            public final /* synthetic */ PointF f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                AbsSwipeUpHandler.this.lambda$animateToProgress$16$AbsSwipeUpHandler(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: animateToProgressInternal */
    public void lambda$animateToProgress$16$AbsSwipeUpHandler(float f, float f2, long j, Interpolator interpolator, GestureState.GestureEndTarget gestureEndTarget, PointF pointF) {
        ArrayList arrayList;
        RectFSpringAnim[] rectFSpringAnimArr;
        float f3 = f;
        long j2 = j;
        PointF pointF2 = pointF;
        maybeUpdateRecentsAttachedState();
        if (this.mGestureState.getEndTarget().isLauncher) {
            TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mActivityRestartListener);
            Animator parallelAnimationToLauncher = this.mActivityInterface.getParallelAnimationToLauncher(this.mGestureState.getEndTarget(), j2, this.mTaskAnimationManager.getCurrentCallbacks());
            this.mParallelRunningAnim = parallelAnimationToLauncher;
            if (parallelAnimationToLauncher != null) {
                parallelAnimationToLauncher.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        Animator unused = AbsSwipeUpHandler.this.mParallelRunningAnim = null;
                    }
                });
                this.mParallelRunningAnim.start();
            }
        }
        boolean z = true;
        if (this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME) {
            getOrientationHandler().adjustFloatingIconStartVelocity(pointF2);
            RecentsAnimationTargets recentsAnimationTargets = this.mRecentsAnimationTargets;
            RemoteAnimationTargetCompat findTask = recentsAnimationTargets != null ? recentsAnimationTargets.findTask(this.mGestureState.getRunningTaskId()) : null;
            if (findTask != null) {
                arrayList = findTask.taskInfo.launchCookies;
            } else {
                arrayList = new ArrayList();
            }
            ArrayList arrayList2 = arrayList;
            boolean z2 = findTask != null && findTask.isTranslucent;
            boolean z3 = !this.mDeviceState.isPipActive() && findTask != null && findTask.allowEnterPip && findTask.taskInfo.pictureInPictureParams != null && findTask.taskInfo.pictureInPictureParams.isAutoEnterEnabled();
            SwipeUpAnimationLogic.HomeAnimationFactory createHomeAnimationFactory = createHomeAnimationFactory(arrayList2, j, z2, z3, findTask);
            if (this.mIsSwipeForStagedSplit || !z3) {
                z = false;
            }
            this.mIsSwipingPipToHome = z;
            if (z) {
                SwipePipToHomeAnimator createWindowAnimationToPip = createWindowAnimationToPip(createHomeAnimationFactory, findTask, f3);
                this.mSwipePipToHomeAnimator = createWindowAnimationToPip;
                rectFSpringAnimArr = this.mSwipePipToHomeAnimators;
                rectFSpringAnimArr[0] = createWindowAnimationToPip;
            } else {
                this.mSwipePipToHomeAnimator = null;
                rectFSpringAnimArr = createWindowAnimationToHome(f3, createHomeAnimationFactory);
                rectFSpringAnimArr[0].addAnimatorListener(new AnimationSuccessListener() {
                    public void onAnimationSuccess(Animator animator) {
                        if (AbsSwipeUpHandler.this.mRecentsAnimationController != null) {
                            AbsSwipeUpHandler.this.mGestureState.setState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED);
                        }
                    }
                });
            }
            this.mRunningWindowAnim = new SwipeUpAnimationLogic.RunningWindowAnim[rectFSpringAnimArr.length];
            int length = rectFSpringAnimArr.length;
            for (int i = 0; i < length; i++) {
                RectFSpringAnim rectFSpringAnim = rectFSpringAnimArr[i];
                if (rectFSpringAnim != null) {
                    rectFSpringAnim.start(this.mContext, pointF2);
                    this.mRunningWindowAnim[i] = SwipeUpAnimationLogic.RunningWindowAnim.wrap(rectFSpringAnim);
                }
            }
            createHomeAnimationFactory.setSwipeVelocity(pointF2.y);
            createHomeAnimationFactory.playAtomicAnimation(pointF2.y);
            this.mLauncherTransitionController = null;
            Q q = this.mRecentsView;
            if (q != null) {
                q.onPrepareGestureEndAnimation((AnimatorSet) null, this.mGestureState.getEndTarget(), getRemoteTaskViewSimulators());
                return;
            }
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animateToValue = this.mCurrentShift.animateToValue(f3, f2);
        animateToValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AbsSwipeUpHandler.this.lambda$animateToProgressInternal$17$AbsSwipeUpHandler(valueAnimator);
            }
        });
        final GestureState.GestureEndTarget gestureEndTarget2 = gestureEndTarget;
        animateToValue.addListener(new AnimationSuccessListener() {
            public void onAnimationSuccess(Animator animator) {
                if (AbsSwipeUpHandler.this.mRecentsAnimationController != null) {
                    if (AbsSwipeUpHandler.this.mRecentsView != null) {
                        int nextPage = AbsSwipeUpHandler.this.mRecentsView.getNextPage();
                        int lastAppearedTaskIndex = AbsSwipeUpHandler.this.getLastAppearedTaskIndex();
                        boolean hasStartedNewTask = AbsSwipeUpHandler.this.hasStartedNewTask();
                        if (gestureEndTarget2 == GestureState.GestureEndTarget.NEW_TASK && nextPage == lastAppearedTaskIndex && !hasStartedNewTask) {
                            AbsSwipeUpHandler.this.mGestureState.setEndTarget(GestureState.GestureEndTarget.LAST_TASK);
                        } else if (gestureEndTarget2 == GestureState.GestureEndTarget.LAST_TASK && hasStartedNewTask) {
                            AbsSwipeUpHandler.this.mGestureState.setEndTarget(GestureState.GestureEndTarget.NEW_TASK);
                        }
                    }
                    AbsSwipeUpHandler.this.mGestureState.setState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED);
                }
            }
        });
        animatorSet.play(animateToValue);
        Q q2 = this.mRecentsView;
        if (q2 != null) {
            q2.onPrepareGestureEndAnimation(animatorSet, this.mGestureState.getEndTarget(), getRemoteTaskViewSimulators());
        }
        animatorSet.setDuration(j2).setInterpolator(interpolator);
        animatorSet.start();
        this.mRunningWindowAnim = new SwipeUpAnimationLogic.RunningWindowAnim[]{SwipeUpAnimationLogic.RunningWindowAnim.wrap((Animator) animatorSet)};
    }

    public /* synthetic */ void lambda$animateToProgressInternal$17$AbsSwipeUpHandler(ValueAnimator valueAnimator) {
        computeRecentsScrollIfInvisible();
    }

    private int calculateWindowRotation(RemoteAnimationTargetCompat remoteAnimationTargetCompat, RecentsOrientedState recentsOrientedState) {
        if (remoteAnimationTargetCompat.rotationChange == 0 || !TaskAnimationManager.ENABLE_SHELL_TRANSITIONS) {
            return recentsOrientedState.getDisplayRotation();
        }
        return Math.abs(remoteAnimationTargetCompat.rotationChange) == 1 ? 3 : 1;
    }

    private SwipePipToHomeAnimator createWindowAnimationToPip(SwipeUpAnimationLogic.HomeAnimationFactory homeAnimationFactory, RemoteAnimationTargetCompat remoteAnimationTargetCompat, float f) {
        ActivityManager.RunningTaskInfo runningTaskInfo = remoteAnimationTargetCompat.taskInfo;
        RecentsOrientedState orientationState = this.mRemoteTargetHandles[0].getTaskViewSimulator().getOrientationState();
        int calculateWindowRotation = calculateWindowRotation(remoteAnimationTargetCompat, orientationState);
        int recentsActivityRotation = orientationState.getRecentsActivityRotation();
        Matrix[] matrixArr = new Matrix[this.mRemoteTargetHandles.length];
        RectF rectF = updateProgressForStartRect(matrixArr, f)[0];
        Matrix matrix = matrixArr[0];
        Matrix matrix2 = new Matrix();
        matrix.invert(matrix2);
        matrix2.mapRect(rectF);
        SwipePipToHomeAnimator.Builder attachedView = new SwipePipToHomeAnimator.Builder().setContext(this.mContext).setTaskId(remoteAnimationTargetCompat.taskId).setComponentName(runningTaskInfo.topActivity).setLeash(remoteAnimationTargetCompat.leash).setSourceRectHint(remoteAnimationTargetCompat.taskInfo.pictureInPictureParams.getSourceRectHint()).setAppBounds(runningTaskInfo.configuration.windowConfiguration.getBounds()).setHomeToWindowPositionMap(matrix).setStartBounds(rectF).setDestinationBounds(SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).startSwipePipToHome(runningTaskInfo.topActivity, runningTaskInfo.topActivityInfo, remoteAnimationTargetCompat.taskInfo.pictureInPictureParams, recentsActivityRotation, this.mDp.hotseatBarSizePx)).setCornerRadius(this.mRecentsView.getPipCornerRadius()).setShadowRadius(this.mRecentsView.getPipShadowRadius()).setAttachedView(this.mRecentsView);
        if (recentsActivityRotation == 0 && (calculateWindowRotation == 1 || calculateWindowRotation == 3)) {
            attachedView.setFromRotation(this.mRemoteTargetHandles[0].getTaskViewSimulator(), calculateWindowRotation, runningTaskInfo.displayCutoutInsets);
        }
        SwipePipToHomeAnimator build = attachedView.build();
        final AnimatorPlaybackController createActivityAnimationToHome = homeAnimationFactory.createActivityAnimationToHome();
        build.addAnimatorListener(new AnimatorListenerAdapter() {
            private boolean mHasAnimationEnded;

            public void onAnimationStart(Animator animator) {
                if (!this.mHasAnimationEnded) {
                    createActivityAnimationToHome.dispatchOnStart();
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (!this.mHasAnimationEnded) {
                    this.mHasAnimationEnded = true;
                    createActivityAnimationToHome.getAnimationPlayer().end();
                    if (AbsSwipeUpHandler.this.mRecentsAnimationController != null) {
                        AbsSwipeUpHandler.this.mGestureState.setState(GestureState.STATE_END_TARGET_ANIMATION_FINISHED);
                    }
                }
            }
        });
        setupWindowAnimation(new RectFSpringAnim[]{build});
        return build;
    }

    /* access modifiers changed from: private */
    public void startInterceptingTouchesForGesture() {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.enableInputConsumer();
            setDividerShown(false, true);
        }
    }

    private void computeRecentsScrollIfInvisible() {
        Q q = this.mRecentsView;
        if (q != null && q.getVisibility() != 0) {
            this.mRecentsView.computeScroll();
        }
    }

    /* access modifiers changed from: private */
    public void continueComputingRecentsScrollIfNecessary() {
        if (!this.mGestureState.hasState(GestureState.STATE_RECENTS_SCROLLING_FINISHED) && !this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED) && !this.mCanceled) {
            computeRecentsScrollIfInvisible();
            this.mRecentsView.postOnAnimation(new Runnable() {
                public final void run() {
                    AbsSwipeUpHandler.this.continueComputingRecentsScrollIfNecessary();
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public RectFSpringAnim[] createWindowAnimationToHome(float f, SwipeUpAnimationLogic.HomeAnimationFactory homeAnimationFactory) {
        RectFSpringAnim[] createWindowAnimationToHome = super.createWindowAnimationToHome(f, homeAnimationFactory);
        setupWindowAnimation(createWindowAnimationToHome);
        return createWindowAnimationToHome;
    }

    private void setupWindowAnimation(RectFSpringAnim[] rectFSpringAnimArr) {
        rectFSpringAnimArr[0].addOnUpdateListener(new RectFSpringAnim.OnUpdateListener() {
            public final void onUpdate(RectF rectF, float f) {
                AbsSwipeUpHandler.this.lambda$setupWindowAnimation$18$AbsSwipeUpHandler(rectF, f);
            }
        });
        rectFSpringAnimArr[0].addAnimatorListener(new AnimationSuccessListener() {
            public void onAnimationSuccess(Animator animator) {
                if (AbsSwipeUpHandler.this.mRecentsView != null) {
                    Q q = AbsSwipeUpHandler.this.mRecentsView;
                    Q q2 = AbsSwipeUpHandler.this.mRecentsView;
                    Objects.requireNonNull(q2);
                    q.post(new Runnable() {
                        public final void run() {
                            RecentsView.this.resetTaskVisuals();
                        }
                    });
                }
                AbsSwipeUpHandler.this.maybeUpdateRecentsAttachedState(false);
                AbsSwipeUpHandler.this.mActivityInterface.onSwipeUpToHomeComplete(AbsSwipeUpHandler.this.mDeviceState);
            }
        });
        RecentsAnimationTargets recentsAnimationTargets = this.mRecentsAnimationTargets;
        if (recentsAnimationTargets != null) {
            recentsAnimationTargets.addReleaseCheck(rectFSpringAnimArr[0]);
        }
    }

    public /* synthetic */ void lambda$setupWindowAnimation$18$AbsSwipeUpHandler(RectF rectF, float f) {
        updateSysUiFlags(Math.max(f, this.mCurrentShift.value));
    }

    public void onConsumerAboutToBeSwitched() {
        T t = this.mActivity;
        if (t != null) {
            t.clearRunOnceOnStartCallback();
            resetLauncherListeners();
        }
        if (!this.mGestureState.isRecentsAnimationRunning() || this.mGestureState.getEndTarget() == null || this.mGestureState.getEndTarget().isLauncher) {
            this.mStateCallback.setStateOnUiThread(STATE_FINISH_WITH_NO_END);
            reset();
            return;
        }
        cancelCurrentAnimation();
    }

    public boolean isCanceled() {
        return this.mCanceled;
    }

    /* access modifiers changed from: private */
    public void resumeLastTask() {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.finish(false, (Runnable) null);
            ActiveGestureLog.INSTANCE.addLog("finishRecentsAnimation", false);
        }
        doLogGesture(GestureState.GestureEndTarget.LAST_TASK, (TaskView) null);
        reset();
    }

    /* access modifiers changed from: private */
    public void startNewTask() {
        Q q = this.mRecentsView;
        startNewTask(new Consumer(q == null ? null : q.getNextPageTaskView()) {
            public final /* synthetic */ TaskView f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                AbsSwipeUpHandler.this.lambda$startNewTask$19$AbsSwipeUpHandler(this.f$1, (Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$startNewTask$19$AbsSwipeUpHandler(TaskView taskView, Boolean bool) {
        if (!bool.booleanValue()) {
            reset();
            endLauncherTransitionController();
            updateSysUiFlags(1.0f);
        }
        doLogGesture(GestureState.GestureEndTarget.NEW_TASK, taskView);
    }

    /* access modifiers changed from: protected */
    public void onRestartPreviouslyAppearedTask() {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.finish(false, (Runnable) null);
        }
        reset();
    }

    /* access modifiers changed from: private */
    public void reset() {
        this.mStateCallback.setStateOnUiThread(STATE_HANDLER_INVALIDATED);
        T t = this.mActivity;
        if (t != null) {
            t.unregisterActivityLifecycleCallbacks(this.mLifecycleCallbacks);
        }
    }

    private void cancelCurrentAnimation() {
        this.mCanceled = true;
        this.mCurrentShift.cancelAnimation();
        this.mInputConsumerProxy.unregisterCallback();
        this.mActivityInitListener.unregister();
        ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mActivityRestartListener);
        this.mTaskSnapshot = null;
    }

    /* access modifiers changed from: private */
    public void invalidateHandler() {
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || !this.mActivityInterface.isInLiveTileMode() || this.mGestureState.getEndTarget() != GestureState.GestureEndTarget.RECENTS) {
            this.mInputConsumerProxy.destroy();
            this.mTaskAnimationManager.setLiveTileCleanUpHandler((Runnable) null);
        }
        this.mInputConsumerProxy.unregisterCallback();
        endRunningWindowAnim(false);
        Runnable runnable = this.mGestureEndCallback;
        if (runnable != null) {
            runnable.run();
        }
        this.mActivityInitListener.unregister();
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mActivityRestartListener);
        this.mTaskSnapshot = null;
    }

    /* access modifiers changed from: private */
    public void invalidateHandlerWithLauncher() {
        endLauncherTransitionController();
        this.mRecentsView.onGestureAnimationEnd();
        resetLauncherListeners();
    }

    private void endLauncherTransitionController() {
        this.mHasEndedLauncherTransition = true;
        AnimatorControllerWithResistance animatorControllerWithResistance = this.mLauncherTransitionController;
        if (animatorControllerWithResistance != null) {
            animatorControllerWithResistance.getNormalController().dispatchSetInterpolator(new TimeInterpolator() {
                public final float getInterpolation(float f) {
                    return AbsSwipeUpHandler.this.lambda$endLauncherTransitionController$20$AbsSwipeUpHandler(f);
                }
            });
            this.mLauncherTransitionController.getNormalController().getAnimationPlayer().end();
            this.mLauncherTransitionController = null;
        }
        Q q = this.mRecentsView;
        if (q != null) {
            q.abortScrollerAnimation();
        }
    }

    public /* synthetic */ float lambda$endLauncherTransitionController$20$AbsSwipeUpHandler(float f) {
        return Utilities.boundToRange(this.mCurrentShift.value, 0.0f, 1.0f);
    }

    private void resetLauncherListeners() {
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mActivityInterface.setOnDeferredActivityLaunchCallback((Runnable) null);
        }
        this.mActivity.getRootView().setOnApplyWindowInsetsListener((View.OnApplyWindowInsetsListener) null);
        this.mRecentsView.removeOnScrollChangedListener(this.mOnRecentsScrollListener);
    }

    /* access modifiers changed from: private */
    public void resetStateForAnimationCancel() {
        this.mActivityInterface.onTransitionCancelled(this.mWasLauncherAlreadyVisible || this.mGestureStarted, this.mGestureState.getEndTarget());
        if (this.mRecentsAnimationTargets != null) {
            setDividerShown(true, true);
        }
        T t = this.mActivity;
        if (t != null) {
            t.clearForceInvisibleFlag(1);
        }
    }

    /* access modifiers changed from: protected */
    public void switchToScreenshot() {
        if (!hasTargets()) {
            this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
            return;
        }
        int runningTaskId = this.mGestureState.getRunningTaskId();
        boolean z = !FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get();
        boolean z2 = false;
        if (this.mRecentsAnimationController != null) {
            if (this.mTaskSnapshot == null) {
                Executors.UI_HELPER_EXECUTOR.execute(new Runnable(runningTaskId, z) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        AbsSwipeUpHandler.this.lambda$switchToScreenshot$22$AbsSwipeUpHandler(this.f$1, this.f$2);
                    }
                });
                return;
            }
            z2 = updateThumbnail(runningTaskId, z);
        }
        if (!z2) {
            setScreenshotCapturedState();
        }
    }

    public /* synthetic */ void lambda$switchToScreenshot$22$AbsSwipeUpHandler(int i, boolean z) {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(recentsAnimationController.screenshotTask(i), i, z) {
                public final /* synthetic */ ThumbnailData f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    AbsSwipeUpHandler.this.lambda$switchToScreenshot$21$AbsSwipeUpHandler(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$switchToScreenshot$21$AbsSwipeUpHandler(ThumbnailData thumbnailData, int i, boolean z) {
        this.mTaskSnapshot = thumbnailData;
        if (!updateThumbnail(i, z)) {
            setScreenshotCapturedState();
        }
    }

    private boolean updateThumbnail(int i, boolean z) {
        TaskView updateThumbnail = (this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME || this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.NEW_TASK) ? null : this.mRecentsView.updateThumbnail(i, this.mTaskSnapshot, z);
        if (updateThumbnail == null || !z || this.mCanceled) {
            return false;
        }
        return ViewUtils.postFrameDrawn(updateThumbnail, new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.lambda$updateThumbnail$23$AbsSwipeUpHandler();
            }
        }, new BooleanSupplier() {
            public final boolean getAsBoolean() {
                return AbsSwipeUpHandler.this.isCanceled();
            }
        });
    }

    public /* synthetic */ void lambda$updateThumbnail$23$AbsSwipeUpHandler() {
        this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
    }

    private void setScreenshotCapturedState() {
        Object beginSection = TraceHelper.INSTANCE.beginSection(SCREENSHOT_CAPTURED_EVT, 4);
        this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
        TraceHelper.INSTANCE.endSection(beginSection);
    }

    /* access modifiers changed from: private */
    public void finishCurrentTransitionToRecents() {
        RecentsAnimationController recentsAnimationController;
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
            RecentsAnimationController recentsAnimationController2 = this.mRecentsAnimationController;
            if (recentsAnimationController2 != null) {
                recentsAnimationController2.detachNavigationBarFromApp(true);
            }
        } else if (!hasTargets() || (recentsAnimationController = this.mRecentsAnimationController) == null) {
            this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
        } else {
            recentsAnimationController.finish(true, new Runnable() {
                public final void run() {
                    AbsSwipeUpHandler.this.lambda$finishCurrentTransitionToRecents$24$AbsSwipeUpHandler();
                }
            });
        }
        ActiveGestureLog.INSTANCE.addLog("finishRecentsAnimation", true);
    }

    public /* synthetic */ void lambda$finishCurrentTransitionToRecents$24$AbsSwipeUpHandler() {
        this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
    }

    /* access modifiers changed from: private */
    public void finishCurrentTransitionToHome() {
        if (!hasTargets() || this.mRecentsAnimationController == null) {
            this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
        } else {
            maybeFinishSwipeToHome();
            finishRecentsControllerToHome(new Runnable() {
                public final void run() {
                    AbsSwipeUpHandler.this.lambda$finishCurrentTransitionToHome$25$AbsSwipeUpHandler();
                }
            });
        }
        ActiveGestureLog.INSTANCE.addLog("finishRecentsAnimation", true);
        GestureState.GestureEndTarget gestureEndTarget = GestureState.GestureEndTarget.HOME;
        Q q = this.mRecentsView;
        doLogGesture(gestureEndTarget, q == null ? null : q.getCurrentPageTaskView());
    }

    public /* synthetic */ void lambda$finishCurrentTransitionToHome$25$AbsSwipeUpHandler() {
        this.mStateCallback.setStateOnUiThread(STATE_CURRENT_TASK_FINISHED);
    }

    private void maybeFinishSwipeToHome() {
        if (this.mIsSwipingPipToHome && this.mSwipePipToHomeAnimators[0] != null) {
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).stopSwipePipToHome(this.mSwipePipToHomeAnimator.getTaskId(), this.mSwipePipToHomeAnimator.getComponentName(), this.mSwipePipToHomeAnimator.getDestinationBounds(), this.mSwipePipToHomeAnimator.getContentOverlay());
            this.mRecentsAnimationController.setFinishTaskTransaction(this.mSwipePipToHomeAnimator.getTaskId(), this.mSwipePipToHomeAnimator.getFinishTransaction(), this.mSwipePipToHomeAnimator.getContentOverlay());
            this.mIsSwipingPipToHome = false;
        } else if (this.mIsSwipeForStagedSplit) {
            PictureInPictureSurfaceTransaction build = new PictureInPictureSurfaceTransaction.Builder().setAlpha(0.0f).build();
            for (int finishTaskTransaction : TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getRunningSplitTaskIds()) {
                this.mRecentsAnimationController.setFinishTaskTransaction(finishTaskTransaction, build, (SurfaceControl) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setupLauncherUiAfterSwipeUpToRecentsAnimation() {
        if (!this.mStateCallback.hasStates(STATE_HANDLER_INVALIDATED)) {
            endLauncherTransitionController();
            this.mRecentsView.onSwipeUpAnimationSuccess();
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
                this.mTaskAnimationManager.setLiveTileCleanUpHandler(new Runnable() {
                    public final void run() {
                        AbsSwipeUpHandler.this.lambda$setupLauncherUiAfterSwipeUpToRecentsAnimation$26$AbsSwipeUpHandler();
                    }
                });
                this.mTaskAnimationManager.enableLiveTileRestartListener();
            }
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).onOverviewShown(false, TAG);
            doLogGesture(GestureState.GestureEndTarget.RECENTS, this.mRecentsView.getCurrentPageTaskView());
            reset();
        }
    }

    public /* synthetic */ void lambda$setupLauncherUiAfterSwipeUpToRecentsAnimation$26$AbsSwipeUpHandler() {
        this.mRecentsView.cleanupRemoteTargets();
        this.mInputConsumerProxy.destroy();
    }

    private static boolean isNotInRecents(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        return remoteAnimationTargetCompat.isNotInRecents || remoteAnimationTargetCompat.activityType == 2;
    }

    /* access modifiers changed from: protected */
    public void initAfterSubclassConstructor() {
        initTransitionEndpoints(this.mRemoteTargetHandles[0].getTaskViewSimulator().getOrientationState().getLauncherDeviceProfile());
    }

    /* access modifiers changed from: protected */
    public void performHapticFeedback() {
        VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).vibrate(VibratorWrapper.OVERVIEW_HAPTIC);
    }

    public Consumer<MotionEvent> getRecentsViewDispatcher(float f) {
        Q q = this.mRecentsView;
        if (q != null) {
            return q.getEventDispatcher(f);
        }
        return null;
    }

    public void setGestureEndCallback(Runnable runnable) {
        this.mGestureEndCallback = runnable;
    }

    /* access modifiers changed from: protected */
    public void linkRecentsViewScroll() {
        SurfaceTransactionApplier.create(this.mRecentsView, new Consumer() {
            public final void accept(Object obj) {
                AbsSwipeUpHandler.this.lambda$linkRecentsViewScroll$29$AbsSwipeUpHandler((SurfaceTransactionApplier) obj);
            }
        });
        this.mRecentsView.addOnScrollChangedListener(this.mOnRecentsScrollListener);
        runOnRecentsAnimationAndLauncherBound(new Runnable() {
            public final void run() {
                AbsSwipeUpHandler.this.lambda$linkRecentsViewScroll$30$AbsSwipeUpHandler();
            }
        });
        this.mRecentsViewScrollLinked = true;
    }

    public /* synthetic */ void lambda$linkRecentsViewScroll$29$AbsSwipeUpHandler(SurfaceTransactionApplier surfaceTransactionApplier) {
        runActionOnRemoteHandles(new Consumer() {
            public final void accept(Object obj) {
                ((RemoteTargetGluer.RemoteTargetHandle) obj).getTransformParams().setSyncTransactionApplier(SurfaceTransactionApplier.this);
            }
        });
        runOnRecentsAnimationAndLauncherBound(new Runnable(surfaceTransactionApplier) {
            public final /* synthetic */ SurfaceTransactionApplier f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                AbsSwipeUpHandler.this.lambda$linkRecentsViewScroll$28$AbsSwipeUpHandler(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$linkRecentsViewScroll$28$AbsSwipeUpHandler(SurfaceTransactionApplier surfaceTransactionApplier) {
        this.mRecentsAnimationTargets.addReleaseCheck(surfaceTransactionApplier);
    }

    public /* synthetic */ void lambda$linkRecentsViewScroll$30$AbsSwipeUpHandler() {
        this.mRecentsView.setRecentsAnimationTargets(this.mRecentsAnimationController, this.mRecentsAnimationTargets);
    }

    /* access modifiers changed from: private */
    public void onRecentsViewScroll() {
        if (moveWindowWithRecentsScroll()) {
            updateFinalShift();
        }
    }

    /* access modifiers changed from: protected */
    public void startNewTask(Consumer<Boolean> consumer) {
        if (!this.mCanceled) {
            TaskView nextPageTaskView = this.mRecentsView.getNextPageTaskView();
            if (nextPageTaskView != null) {
                int i = nextPageTaskView.getTask().key.id;
                this.mGestureState.updateLastStartedTaskId(i);
                nextPageTaskView.launchTask(new Consumer(consumer, this.mGestureState.getPreviouslyAppearedTaskIds().contains(Integer.valueOf(i))) {
                    public final /* synthetic */ Consumer f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void accept(Object obj) {
                        AbsSwipeUpHandler.this.lambda$startNewTask$31$AbsSwipeUpHandler(this.f$1, this.f$2, (Boolean) obj);
                    }
                }, true);
            } else {
                this.mActivityInterface.onLaunchTaskFailed();
                Toast.makeText(this.mContext, R.string.activity_not_available, 0).show();
                RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
                if (recentsAnimationController != null) {
                    recentsAnimationController.finish(true, (Runnable) null);
                }
            }
        }
        this.mCanceled = false;
    }

    public /* synthetic */ void lambda$startNewTask$31$AbsSwipeUpHandler(Consumer consumer, boolean z, Boolean bool) {
        consumer.accept(bool);
        if (!bool.booleanValue()) {
            this.mActivityInterface.onLaunchTaskFailed();
            RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
            if (recentsAnimationController != null) {
                recentsAnimationController.finish(true, (Runnable) null);
            }
        } else if (z) {
            onRestartPreviouslyAppearedTask();
        }
    }

    private void runOnRecentsAnimationAndLauncherBound(Runnable runnable) {
        this.mRecentsAnimationStartCallbacks.add(runnable);
        flushOnRecentsAnimationAndLauncherBound();
    }

    private void flushOnRecentsAnimationAndLauncherBound() {
        if (this.mRecentsAnimationTargets != null && this.mStateCallback.hasStates(STATE_LAUNCHER_BIND_TO_SERVICE) && !this.mRecentsAnimationStartCallbacks.isEmpty()) {
            Iterator it = new ArrayList(this.mRecentsAnimationStartCallbacks).iterator();
            while (it.hasNext()) {
                ((Runnable) it.next()).run();
            }
            this.mRecentsAnimationStartCallbacks.clear();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasTargets() {
        RecentsAnimationTargets recentsAnimationTargets = this.mRecentsAnimationTargets;
        return recentsAnimationTargets != null && recentsAnimationTargets.hasTargets();
    }

    public void onRecentsAnimationFinished(RecentsAnimationController recentsAnimationController) {
        if (!recentsAnimationController.getFinishTargetIsLauncher()) {
            setDividerShown(true, false);
        }
        this.mRecentsAnimationController = null;
        this.mRecentsAnimationTargets = null;
        Q q = this.mRecentsView;
        if (q != null) {
            q.setRecentsAnimationTargets((RecentsAnimationController) null, (RecentsAnimationTargets) null);
        }
    }

    public void onTasksAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        if (this.mRecentsAnimationController != null && handleTaskAppeared(remoteAnimationTargetCompatArr)) {
            this.mRecentsAnimationController.finish(false, (Runnable) null);
            this.mActivityInterface.onLaunchTaskSuccess();
            ActiveGestureLog.INSTANCE.addLog("finishRecentsAnimation", false);
        }
    }

    /* access modifiers changed from: protected */
    public int getLastAppearedTaskIndex() {
        if (this.mGestureState.getLastAppearedTaskId() != -1) {
            return this.mRecentsView.getTaskIndexForId(this.mGestureState.getLastAppearedTaskId());
        }
        return this.mRecentsView.getRunningTaskIndex();
    }

    /* access modifiers changed from: protected */
    public boolean hasStartedNewTask() {
        return this.mGestureState.getLastStartedTaskId() != -1;
    }

    public void initWhenReady() {
        RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getTasks((Consumer<ArrayList<GroupTask>>) null);
        this.mActivityInitListener.register();
    }

    /* access modifiers changed from: protected */
    public void applyScrollAndTransform() {
        boolean z = true;
        boolean z2 = (this.mRecentsAnimationTargets == null || this.mGestureState.getEndTarget() == GestureState.GestureEndTarget.HOME) ? false : true;
        if (!this.mRecentsViewScrollLinked || this.mRecentsView == null) {
            z = false;
        }
        for (RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle : this.mRemoteTargetHandles) {
            AnimatorControllerWithResistance playbackController = remoteTargetHandle.getPlaybackController();
            if (playbackController != null) {
                playbackController.setProgress(Math.max(this.mCurrentShift.value, getScaleProgressDueToScroll()), this.mDragLengthFactor);
            }
            if (z2) {
                TaskViewSimulator taskViewSimulator = remoteTargetHandle.getTaskViewSimulator();
                if (z) {
                    taskViewSimulator.setScroll((float) this.mRecentsView.getScrollOffset());
                }
                taskViewSimulator.apply(remoteTargetHandle.getTransformParams());
            }
        }
        ProtoTracer.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).scheduleFrameUpdate();
    }

    private float getScaleProgressDueToScroll() {
        Q q;
        T t = this.mActivity;
        if (t == null || !t.getDeviceProfile().isTablet || (q = this.mRecentsView) == null || !this.mRecentsViewScrollLinked) {
            return 0.0f;
        }
        float abs = (float) Math.abs(q.getScrollOffset(q.getCurrentPage()));
        int primaryValue = this.mRecentsView.getPagedOrientationHandler().getPrimaryValue(this.mRecentsView.getLastComputedTaskSize().width(), this.mRecentsView.getLastComputedTaskSize().height()) + this.mRecentsView.getPageSpacing();
        float maxScaleForFullScreen = this.mRecentsView.getMaxScaleForFullScreen() * 0.07f;
        float f = this.mQuickSwitchScaleScrollThreshold;
        if (abs < f) {
            return Utilities.mapToRange(abs, 0.0f, f, 0.0f, maxScaleForFullScreen, Interpolators.ACCEL_DEACCEL);
        }
        float f2 = (float) primaryValue;
        return abs > f2 - f ? Utilities.mapToRange(abs, f2 - f, f2, maxScaleForFullScreen, 0.0f, Interpolators.ACCEL_DEACCEL) : maxScaleForFullScreen;
    }

    private void setDividerShown(boolean z, boolean z2) {
        ValueAnimator valueAnimator = this.mDividerAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mDividerAnimator = TaskViewUtils.createSplitAuxiliarySurfacesAnimator(this.mRecentsAnimationTargets.nonApps, z, new Consumer(z2) {
            public final /* synthetic */ boolean f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                AbsSwipeUpHandler.lambda$setDividerShown$32(this.f$0, (ValueAnimator) obj);
            }
        });
    }

    static /* synthetic */ void lambda$setDividerShown$32(boolean z, ValueAnimator valueAnimator) {
        valueAnimator.start();
        if (z) {
            valueAnimator.end();
        }
    }

    public void writeToProto(InputConsumerProto.Builder builder) {
        int i;
        SwipeHandlerProto.Builder newBuilder = SwipeHandlerProto.newBuilder();
        this.mGestureState.writeToProto(newBuilder);
        newBuilder.setIsRecentsAttachedToAppWindow(this.mAnimationFactory.isRecentsAttachedToAppWindow());
        Q q = this.mRecentsView;
        if (q == null) {
            i = 0;
        } else {
            i = q.getScrollOffset();
        }
        newBuilder.setScrollOffset(i);
        newBuilder.setAppToOverviewProgress(this.mCurrentShift.value);
        builder.setSwipeHandler(newBuilder);
    }
}
