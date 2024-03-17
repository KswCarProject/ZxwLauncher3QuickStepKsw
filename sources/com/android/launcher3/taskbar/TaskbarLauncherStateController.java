package com.android.launcher3.taskbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.Hotseat;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RecentsAnimationController;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.animation.ViewRootSync;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TaskbarLauncherStateController {
    private static final int FLAGS_ALL = -1;
    public static final int FLAG_RECENTS_ANIMATION_RUNNING = 2;
    public static final int FLAG_RESUMED = 1;
    public static final int FLAG_TRANSITION_STATE_RUNNING = 4;
    private boolean mCanSyncViews;
    /* access modifiers changed from: private */
    public TaskbarControllers mControllers;
    private final AnimatedFloat mIconAlignmentForGestureState = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarLauncherStateController.this.onIconAlignmentRatioChangedForAppAndHomeTransition();
        }
    });
    private final AnimatedFloat mIconAlignmentForLauncherState = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarLauncherStateController.this.onIconAlignmentRatioChangedForStateTransition();
        }
    });
    /* access modifiers changed from: private */
    public final AnimatedFloat mIconAlignmentForResumedState = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarLauncherStateController.this.onIconAlignmentRatioChangedForAppAndHomeTransition();
        }
    });
    /* access modifiers changed from: private */
    public MultiValueAlpha.AlphaProperty mIconAlphaForHome;
    /* access modifiers changed from: private */
    public boolean mIsAnimatingToLauncherViaGesture;
    /* access modifiers changed from: private */
    public boolean mIsAnimatingToLauncherViaResume;
    /* access modifiers changed from: private */
    public BaseQuickstepLauncher mLauncher;
    /* access modifiers changed from: private */
    public LauncherState mLauncherState = LauncherState.NORMAL;
    /* access modifiers changed from: private */
    public Integer mPrevState;
    /* access modifiers changed from: private */
    public boolean mShouldDelayLauncherStateAnim;
    private int mState;
    private final StateManager.StateListener<LauncherState> mStateListener = new StateManager.StateListener<LauncherState>() {
        public void onStateTransitionStart(LauncherState launcherState) {
            if (launcherState != TaskbarLauncherStateController.this.mLauncherState) {
                TaskbarLauncherStateController taskbarLauncherStateController = TaskbarLauncherStateController.this;
                Integer unused = taskbarLauncherStateController.mPrevState = Integer.valueOf(taskbarLauncherStateController.mPrevState.intValue() & -5);
                LauncherState unused2 = TaskbarLauncherStateController.this.mLauncherState = launcherState;
            }
            TaskbarLauncherStateController.this.updateStateForFlag(4, true);
            if (!TaskbarLauncherStateController.this.mShouldDelayLauncherStateAnim) {
                TaskbarLauncherStateController.this.applyState();
            }
        }

        public void onStateTransitionComplete(LauncherState launcherState) {
            LauncherState unused = TaskbarLauncherStateController.this.mLauncherState = launcherState;
            TaskbarLauncherStateController.this.updateStateForFlag(4, false);
            TaskbarLauncherStateController.this.applyState();
        }
    };
    /* access modifiers changed from: private */
    public TaskBarRecentsAnimationListener mTaskBarRecentsAnimationListener;
    private AnimatedFloat mTaskbarBackgroundAlpha;

    private boolean hasAnyFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    static /* synthetic */ void lambda$onIconAlignmentRatioChanged$2() {
    }

    public void init(TaskbarControllers taskbarControllers, BaseQuickstepLauncher baseQuickstepLauncher) {
        this.mCanSyncViews = false;
        this.mControllers = taskbarControllers;
        this.mLauncher = baseQuickstepLauncher;
        this.mTaskbarBackgroundAlpha = taskbarControllers.taskbarDragLayerController.getTaskbarBackgroundAlpha();
        MultiValueAlpha.AlphaProperty property = this.mControllers.taskbarViewController.getTaskbarIconAlpha().getProperty(0);
        this.mIconAlphaForHome = property;
        property.setConsumer(new Consumer() {
            public final void accept(Object obj) {
                TaskbarLauncherStateController.this.lambda$init$0$TaskbarLauncherStateController((Float) obj);
            }
        });
        this.mIconAlignmentForResumedState.finishAnimation();
        onIconAlignmentRatioChangedForAppAndHomeTransition();
        this.mLauncher.getStateManager().addStateListener(this.mStateListener);
        updateStateForFlag(1, baseQuickstepLauncher.hasBeenResumed());
        this.mLauncherState = baseQuickstepLauncher.getStateManager().getState();
        applyState(0);
        this.mCanSyncViews = true;
    }

    public /* synthetic */ void lambda$init$0$TaskbarLauncherStateController(Float f) {
        Hotseat hotseat = this.mLauncher.getHotseat();
        float f2 = 0.0f;
        if (f.floatValue() <= 0.0f) {
            f2 = 1.0f;
        }
        hotseat.setIconsAlpha(f2);
    }

    public void onDestroy() {
        this.mCanSyncViews = false;
        this.mIconAlignmentForResumedState.finishAnimation();
        this.mIconAlignmentForGestureState.finishAnimation();
        this.mIconAlignmentForLauncherState.finishAnimation();
        this.mIconAlphaForHome.setConsumer((Consumer<Float>) null);
        this.mLauncher.getHotseat().setIconsAlpha(1.0f);
        this.mLauncher.getStateManager().removeStateListener(this.mStateListener);
        this.mCanSyncViews = true;
    }

    public Animator createAnimToLauncher(LauncherState launcherState, RecentsAnimationCallbacks recentsAnimationCallbacks, long j) {
        AnimatorSet animatorSet = new AnimatorSet();
        TaskbarStashController taskbarStashController = this.mControllers.taskbarStashController;
        taskbarStashController.updateStateForFlag(64, launcherState.isTaskbarStashed(this.mLauncher));
        taskbarStashController.updateStateForFlag(1, false);
        updateStateForFlag(2, true);
        animatorSet.play(taskbarStashController.applyStateWithoutStart(j));
        animatorSet.play(applyState(j, false));
        TaskBarRecentsAnimationListener taskBarRecentsAnimationListener = new TaskBarRecentsAnimationListener(recentsAnimationCallbacks);
        this.mTaskBarRecentsAnimationListener = taskBarRecentsAnimationListener;
        recentsAnimationCallbacks.addListener(taskBarRecentsAnimationListener);
        ((RecentsView) this.mLauncher.getOverviewPanel()).setTaskLaunchListener(new RecentsView.TaskLaunchListener() {
            public final void onTaskLaunched() {
                TaskbarLauncherStateController.this.lambda$createAnimToLauncher$1$TaskbarLauncherStateController();
            }
        });
        return animatorSet;
    }

    public /* synthetic */ void lambda$createAnimToLauncher$1$TaskbarLauncherStateController() {
        this.mTaskBarRecentsAnimationListener.endGestureStateOverride(true);
    }

    public boolean isAnimatingToLauncher() {
        return this.mIsAnimatingToLauncherViaResume || this.mIsAnimatingToLauncherViaGesture;
    }

    public void setShouldDelayLauncherStateAnim(boolean z) {
        if (!z && this.mShouldDelayLauncherStateAnim) {
            applyState();
        }
        this.mShouldDelayLauncherStateAnim = z;
    }

    public void updateStateForFlag(int i, boolean z) {
        if (z) {
            this.mState = i | this.mState;
            return;
        }
        this.mState = (~i) & this.mState;
    }

    private boolean hasAnyFlag(int i) {
        return hasAnyFlag(this.mState, i);
    }

    public void applyState() {
        applyState(300);
    }

    public void applyState(long j) {
        applyState(j, true);
    }

    public Animator applyState(boolean z) {
        return applyState(300, z);
    }

    public Animator applyState(long j, boolean z) {
        Integer num = this.mPrevState;
        if (num != null && num.intValue() == this.mState) {
            return null;
        }
        Integer num2 = this.mPrevState;
        int intValue = num2 == null ? -1 : num2.intValue() ^ this.mState;
        this.mPrevState = Integer.valueOf(this.mState);
        return onStateChangeApplied(intValue, j, z);
    }

    private Animator onStateChangeApplied(int i, final long j, boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        boolean goingToUnstashedLauncherState = goingToUnstashedLauncherState();
        boolean z2 = false;
        if (hasAnyFlag(i, 4)) {
            boolean z3 = !hasAnyFlag(4);
            playStateTransitionAnim(animatorSet, j, z3);
            if (z3 && this.mLauncherState == LauncherState.QUICK_SWITCH) {
                updateStateForFlag(1, false);
                applyState(0);
            }
        }
        boolean z4 = goingToUnstashedLauncherState != goingToUnstashedLauncherState();
        boolean z5 = this.mIconAlignmentForResumedState.isAnimating() && z4;
        float f = 1.0f;
        if (hasAnyFlag(i, 1) || z5) {
            final boolean isResumed = isResumed();
            float f2 = (!isResumed || (!goingToUnstashedLauncherState() && z4)) ? 0.0f : 1.0f;
            if (!this.mIconAlignmentForResumedState.isAnimatingToValue(f2)) {
                ObjectAnimator duration = this.mIconAlignmentForResumedState.animateToValue(f2).setDuration(j);
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = TaskbarLauncherStateController.this.mIsAnimatingToLauncherViaResume = false;
                    }

                    public void onAnimationStart(Animator animator) {
                        boolean unused = TaskbarLauncherStateController.this.mIsAnimatingToLauncherViaResume = isResumed;
                        TaskbarStashController taskbarStashController = TaskbarLauncherStateController.this.mControllers.taskbarStashController;
                        taskbarStashController.updateStateForFlag(1, !isResumed);
                        taskbarStashController.applyState(j);
                    }
                });
                animatorSet.play(duration);
            }
        }
        if (this.mIconAlignmentForGestureState.isAnimating() && z4) {
            z2 = true;
        }
        if (hasAnyFlag(i, 2) || z2) {
            boolean isRecentsAnimationRunning = isRecentsAnimationRunning();
            float f3 = (!isRecentsAnimationRunning || !goingToUnstashedLauncherState()) ? 0.0f : 1.0f;
            if (!this.mIconAlignmentForGestureState.isAnimatingToValue(f3)) {
                ObjectAnimator animateToValue = this.mIconAlignmentForGestureState.animateToValue(f3);
                if (isRecentsAnimationRunning) {
                    animateToValue.setDuration(j);
                }
                animateToValue.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = TaskbarLauncherStateController.this.mIsAnimatingToLauncherViaGesture = false;
                    }

                    public void onAnimationStart(Animator animator) {
                        TaskbarLauncherStateController taskbarLauncherStateController = TaskbarLauncherStateController.this;
                        boolean unused = taskbarLauncherStateController.mIsAnimatingToLauncherViaGesture = taskbarLauncherStateController.isRecentsAnimationRunning();
                    }
                });
                animatorSet.play(animateToValue);
            }
        }
        if (hasAnyFlag(i, 3)) {
            boolean hasAnyFlag = hasAnyFlag(3);
            if (hasAnyFlag) {
                AbstractFloatingView.closeAllOpenViews(this.mControllers.taskbarActivityContext);
            }
            AnimatedFloat animatedFloat = this.mTaskbarBackgroundAlpha;
            if (hasAnyFlag) {
                f = 0.0f;
            }
            animatorSet.play(animatedFloat.animateToValue(f).setDuration(j));
        }
        if (z) {
            animatorSet.start();
        }
        return animatorSet;
    }

    private boolean goingToUnstashedLauncherState() {
        return !this.mControllers.taskbarStashController.isInStashedLauncherState();
    }

    private void playStateTransitionAnim(AnimatorSet animatorSet, long j, final boolean z) {
        final boolean isTaskbarStashed = this.mLauncherState.isTaskbarStashed(this.mLauncher);
        float f = this.mLauncherState.isTaskbarAlignedWithHotseat(this.mLauncher) ? 1.0f : 0.0f;
        TaskbarStashController taskbarStashController = this.mControllers.taskbarStashController;
        taskbarStashController.updateStateForFlag(64, isTaskbarStashed);
        Animator applyStateWithoutStart = taskbarStashController.applyStateWithoutStart(j);
        if (applyStateWithoutStart != null) {
            applyStateWithoutStart.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (isTaskbarStashed && z) {
                        TaskbarLauncherStateController.this.mLauncher.getHotseat().setIconsAlpha(1.0f);
                    }
                }

                public void onAnimationStart(Animator animator) {
                    if (TaskbarLauncherStateController.this.mLauncher.getHotseat().getIconsAlpha() > 0.0f) {
                        TaskbarLauncherStateController.this.mIconAlphaForHome.setValue(TaskbarLauncherStateController.this.mLauncher.getHotseat().getIconsAlpha());
                    }
                }
            });
            animatorSet.play(applyStateWithoutStart);
        }
        if (!this.mIconAlignmentForLauncherState.isAnimatingToValue(f)) {
            this.mIconAlignmentForLauncherState.finishAnimation();
            animatorSet.play(this.mIconAlignmentForLauncherState.animateToValue(f).setDuration(j));
        }
    }

    private boolean isResumed() {
        return (this.mState & 1) != 0;
    }

    /* access modifiers changed from: private */
    public boolean isRecentsAnimationRunning() {
        return (this.mState & 2) != 0;
    }

    /* access modifiers changed from: private */
    public void onIconAlignmentRatioChangedForStateTransition() {
        if (isResumed() || this.mTaskBarRecentsAnimationListener != null) {
            onIconAlignmentRatioChanged(new Supplier() {
                public final Object get() {
                    return Float.valueOf(TaskbarLauncherStateController.this.getCurrentIconAlignmentRatioForLauncherState());
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onIconAlignmentRatioChangedForAppAndHomeTransition() {
        onIconAlignmentRatioChanged(new Supplier() {
            public final Object get() {
                return Float.valueOf(TaskbarLauncherStateController.this.getCurrentIconAlignmentRatioBetweenAppAndHome());
            }
        });
    }

    private void onIconAlignmentRatioChanged(Supplier<Float> supplier) {
        if (this.mControllers != null) {
            float floatValue = supplier.get().floatValue();
            float value = this.mIconAlphaForHome.getValue();
            boolean z = true;
            boolean z2 = floatValue < 1.0f;
            if ((!z2 || Float.compare(value, 1.0f) == 0) && (z2 || Float.compare(value, 0.0f) == 0)) {
                z = false;
            }
            updateIconAlignment(floatValue);
            if (z && this.mCanSyncViews && !Utilities.IS_RUNNING_IN_TEST_HARNESS) {
                ViewRootSync.synchronizeNextDraw((View) this.mLauncher.getHotseat(), (View) this.mControllers.taskbarActivityContext.getDragLayer(), (Runnable) $$Lambda$TaskbarLauncherStateController$1x9CXHyqJ42WclFLMNc8b1KVEU.INSTANCE);
            }
        }
    }

    private void updateIconAlignment(float f) {
        this.mControllers.taskbarViewController.setLauncherIconAlignment(f, this.mLauncher.getDeviceProfile());
        setTaskbarViewVisible(f < 1.0f);
        this.mControllers.navbarButtonsViewController.updateTaskbarAlignment(f);
    }

    /* access modifiers changed from: private */
    public float getCurrentIconAlignmentRatioBetweenAppAndHome() {
        return Math.max(this.mIconAlignmentForResumedState.value, this.mIconAlignmentForGestureState.value);
    }

    /* access modifiers changed from: private */
    public float getCurrentIconAlignmentRatioForLauncherState() {
        return this.mIconAlignmentForLauncherState.value;
    }

    private void setTaskbarViewVisible(boolean z) {
        this.mIconAlphaForHome.setValue(z ? 1.0f : 0.0f);
    }

    private final class TaskBarRecentsAnimationListener implements RecentsAnimationCallbacks.RecentsAnimationListener {
        private final RecentsAnimationCallbacks mCallbacks;

        TaskBarRecentsAnimationListener(RecentsAnimationCallbacks recentsAnimationCallbacks) {
            this.mCallbacks = recentsAnimationCallbacks;
        }

        public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
            endGestureStateOverride(!TaskbarLauncherStateController.this.mLauncher.isInState(LauncherState.OVERVIEW));
        }

        public void onRecentsAnimationFinished(RecentsAnimationController recentsAnimationController) {
            endGestureStateOverride(!recentsAnimationController.getFinishTargetIsLauncher());
        }

        /* access modifiers changed from: private */
        public void endGestureStateOverride(boolean z) {
            this.mCallbacks.removeListener(this);
            TaskBarRecentsAnimationListener unused = TaskbarLauncherStateController.this.mTaskBarRecentsAnimationListener = null;
            boolean z2 = !z;
            TaskbarLauncherStateController.this.updateStateForFlag(2, false);
            TaskbarLauncherStateController.this.updateStateForFlag(1, z2);
            TaskbarLauncherStateController.this.applyState();
            TaskbarLauncherStateController.this.mIconAlignmentForResumedState.cancelAnimation();
            TaskbarLauncherStateController.this.mIconAlignmentForResumedState.updateValue(z2 ? 1.0f : 0.0f);
            TaskbarStashController taskbarStashController = TaskbarLauncherStateController.this.mControllers.taskbarStashController;
            taskbarStashController.updateStateForFlag(1, z);
            taskbarStashController.applyState();
        }
    }

    private static String getStateString(int i) {
        StringJoiner stringJoiner = new StringJoiner("|");
        String str = "";
        stringJoiner.add((i & 1) != 0 ? "FLAG_RESUMED" : str);
        stringJoiner.add((i & 2) != 0 ? "FLAG_RECENTS_ANIMATION_RUNNING" : str);
        if ((i & 4) != 0) {
            str = "FLAG_TRANSITION_STATE_RUNNING";
        }
        stringJoiner.add(str);
        return stringJoiner.toString();
    }

    /* access modifiers changed from: protected */
    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarLauncherStateController:");
        printWriter.println(String.format("%s\tmIconAlignmentForResumedState=%.2f", new Object[]{str, Float.valueOf(this.mIconAlignmentForResumedState.value)}));
        printWriter.println(String.format("%s\tmIconAlignmentForGestureState=%.2f", new Object[]{str, Float.valueOf(this.mIconAlignmentForGestureState.value)}));
        printWriter.println(String.format("%s\tmIconAlignmentForLauncherState=%.2f", new Object[]{str, Float.valueOf(this.mIconAlignmentForLauncherState.value)}));
        printWriter.println(String.format("%s\tmTaskbarBackgroundAlpha=%.2f", new Object[]{str, Float.valueOf(this.mTaskbarBackgroundAlpha.value)}));
        printWriter.println(String.format("%s\tmIconAlphaForHome=%.2f", new Object[]{str, Float.valueOf(this.mIconAlphaForHome.getValue())}));
        printWriter.println(String.format("%s\tmPrevState=%s", new Object[]{str, getStateString(this.mPrevState.intValue())}));
        printWriter.println(String.format("%s\tmState=%s", new Object[]{str, getStateString(this.mState)}));
        printWriter.println(String.format("%s\tmLauncherState=%s", new Object[]{str, this.mLauncherState}));
        printWriter.println(String.format("%s\tmIsAnimatingToLauncherViaGesture=%b", new Object[]{str, Boolean.valueOf(this.mIsAnimatingToLauncherViaGesture)}));
        printWriter.println(String.format("%s\tmIsAnimatingToLauncherViaResume=%b", new Object[]{str, Boolean.valueOf(this.mIsAnimatingToLauncherViaResume)}));
        printWriter.println(String.format("%s\tmShouldDelayLauncherStateAnim=%b", new Object[]{str, Boolean.valueOf(this.mShouldDelayLauncherStateAnim)}));
    }
}
