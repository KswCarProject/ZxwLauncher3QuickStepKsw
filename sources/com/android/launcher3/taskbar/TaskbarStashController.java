package com.android.launcher3.taskbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.SystemUiProxy;
import java.io.PrintWriter;
import java.util.StringJoiner;
import java.util.function.IntPredicate;

public class TaskbarStashController implements TaskbarControllers.LoggableTaskbarController {
    private static final boolean DEFAULT_STASHED_PREF = false;
    private static final int FLAGS_IN_APP = 257;
    private static final int FLAGS_REPORT_STASHED_INSETS_TO_APP = 30;
    private static final int FLAGS_STASHED_IN_APP = 190;
    private static final int FLAGS_STASHED_IN_APP_IGNORING_IME = 158;
    public static final int FLAG_IN_APP = 1;
    public static final int FLAG_IN_SETUP = 256;
    public static final int FLAG_IN_STASHED_LAUNCHER_STATE = 64;
    public static final int FLAG_STASHED_IN_APP_ALL_APPS = 128;
    public static final int FLAG_STASHED_IN_APP_EMPTY = 8;
    public static final int FLAG_STASHED_IN_APP_IME = 32;
    public static final int FLAG_STASHED_IN_APP_MANUAL = 2;
    public static final int FLAG_STASHED_IN_APP_PINNED = 4;
    public static final int FLAG_STASHED_IN_APP_SETUP = 16;
    private static final String SHARED_PREFS_STASHED_KEY = "taskbar_is_stashed";
    private static final float STASHED_TASKBAR_HINT_SCALE = 0.9f;
    private static final float STASHED_TASKBAR_SCALE = 0.5f;
    private static final long TASKBAR_HINT_STASH_DURATION = 400;
    public static final long TASKBAR_STASH_DURATION = 300;
    private static final long TASKBAR_STASH_DURATION_FOR_IME = 80;
    private static final float UNSTASHED_TASKBAR_HANDLE_HINT_SCALE = 1.1f;
    private final TaskbarActivityContext mActivity;
    /* access modifiers changed from: private */
    public AnimatorSet mAnimator;
    private TaskbarControllers mControllers;
    private boolean mEnableManualStashingForTests = false;
    private MultiValueAlpha.AlphaProperty mIconAlphaForStash;
    private AnimatedFloat mIconScaleForStash;
    private AnimatedFloat mIconTranslationYForStash;
    private boolean mIsImeShowing;
    /* access modifiers changed from: private */
    public boolean mIsStashed = false;
    private boolean mIsSystemGestureInProgress;
    private final SharedPreferences mPrefs;
    private final int mStashedHeight;
    private int mState;
    private final StatePropertyHolder mStatePropertyHolder = new StatePropertyHolder(new IntPredicate() {
        public final boolean test(int i) {
            return TaskbarStashController.this.lambda$new$0$TaskbarStashController(i);
        }
    });
    private final SystemUiProxy mSystemUiProxy;
    private AnimatedFloat mTaskbarBackgroundOffset;
    private AnimatedFloat mTaskbarImeBgAlpha;
    private MultiValueAlpha.AlphaProperty mTaskbarStashedHandleAlpha;
    private AnimatedFloat mTaskbarStashedHandleHintScale;
    private final int mUnstashedHeight;

    private boolean hasAnyFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    public /* synthetic */ boolean lambda$new$0$TaskbarStashController(int i) {
        boolean hasAnyFlag = hasAnyFlag(i, 257);
        return (hasAnyFlag && hasAnyFlag(i, FLAGS_STASHED_IN_APP)) || (!hasAnyFlag && hasAnyFlag(i, 64));
    }

    public TaskbarStashController(TaskbarActivityContext taskbarActivityContext) {
        this.mActivity = taskbarActivityContext;
        this.mPrefs = Utilities.getPrefs(taskbarActivityContext);
        this.mSystemUiProxy = SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(taskbarActivityContext);
        this.mUnstashedHeight = taskbarActivityContext.getDeviceProfile().taskbarSize;
        this.mStashedHeight = taskbarActivityContext.getDeviceProfile().stashedTaskbarSize;
    }

    public void init(TaskbarControllers taskbarControllers, boolean z) {
        this.mControllers = taskbarControllers;
        TaskbarDragLayerController taskbarDragLayerController = taskbarControllers.taskbarDragLayerController;
        this.mTaskbarBackgroundOffset = taskbarDragLayerController.getTaskbarBackgroundOffset();
        this.mTaskbarImeBgAlpha = taskbarDragLayerController.getImeBgTaskbar();
        TaskbarViewController taskbarViewController = taskbarControllers.taskbarViewController;
        this.mIconAlphaForStash = taskbarViewController.getTaskbarIconAlpha().getProperty(2);
        this.mIconScaleForStash = taskbarViewController.getTaskbarIconScaleForStash();
        this.mIconTranslationYForStash = taskbarViewController.getTaskbarIconTranslationYForStash();
        StashedHandleViewController stashedHandleViewController = taskbarControllers.stashedHandleViewController;
        this.mTaskbarStashedHandleAlpha = stashedHandleViewController.getStashedHandleAlpha().getProperty(0);
        this.mTaskbarStashedHandleHintScale = stashedHandleViewController.getStashedHandleHintScale();
        boolean z2 = true;
        boolean z3 = supportsManualStashing() && this.mPrefs.getBoolean(SHARED_PREFS_STASHED_KEY, false);
        if (this.mActivity.isUserSetupComplete() && !z) {
            z2 = false;
        }
        updateStateForFlag(2, z3);
        updateStateForFlag(16, z2);
        updateStateForFlag(256, z2);
        applyState();
        notifyStashChange(false, isStashedInApp());
    }

    public boolean supportsVisualStashing() {
        return this.mControllers.uiController.supportsVisualStashing();
    }

    /* access modifiers changed from: protected */
    public boolean supportsManualStashing() {
        return supportsVisualStashing() && (!Utilities.IS_RUNNING_IN_TEST_HARNESS || this.mEnableManualStashingForTests);
    }

    public void enableManualStashingForTests(boolean z) {
        this.mEnableManualStashingForTests = z;
    }

    /* access modifiers changed from: protected */
    public void setSetupUIVisible(boolean z) {
        boolean z2 = z || !this.mActivity.isUserSetupComplete();
        updateStateForFlag(256, z2);
        updateStateForFlag(16, z2);
        applyState(z2 ? 0 : 300);
    }

    public boolean isStashed() {
        return this.mIsStashed;
    }

    public boolean isStashedInApp() {
        return hasAnyFlag(FLAGS_STASHED_IN_APP);
    }

    public boolean isStashedInAppIgnoringIme() {
        return hasAnyFlag(FLAGS_STASHED_IN_APP_IGNORING_IME);
    }

    public boolean isInStashedLauncherState() {
        return hasAnyFlag(64) && supportsVisualStashing();
    }

    private boolean hasAnyFlag(int i) {
        return hasAnyFlag(this.mState, i);
    }

    public boolean isInAppAndNotStashed() {
        return !this.mIsStashed && isInApp();
    }

    public boolean isInApp() {
        return hasAnyFlag(257);
    }

    public int getContentHeightToReportToApps() {
        if (!supportsVisualStashing() || !hasAnyFlag(30)) {
            return this.mUnstashedHeight;
        }
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        if (hasAnyFlag(16) && deviceProfile.isTaskbarPresent && !deviceProfile.isLandscape) {
            return this.mUnstashedHeight;
        }
        AnimatorSet animatorSet = this.mAnimator;
        boolean z = animatorSet != null && animatorSet.isStarted();
        if (this.mControllers.stashedHandleViewController.isStashedHandleVisible() || !isInApp() || z) {
            return this.mStashedHeight;
        }
        return 0;
    }

    public int getTappableHeightToReportToApps() {
        int contentHeightToReportToApps = getContentHeightToReportToApps();
        if (contentHeightToReportToApps <= this.mStashedHeight) {
            return 0;
        }
        return contentHeightToReportToApps;
    }

    public int getStashedHeight() {
        return this.mStashedHeight;
    }

    public boolean onLongPressToUnstashTaskbar() {
        if (!isStashed() || !canCurrentlyManuallyUnstash() || !updateAndAnimateIsManuallyStashedInApp(false)) {
            return false;
        }
        this.mControllers.taskbarActivityContext.getDragLayer().performHapticFeedback(0);
        return true;
    }

    private boolean canCurrentlyManuallyUnstash() {
        return (this.mState & 191) == 3;
    }

    public boolean updateAndAnimateIsManuallyStashedInApp(boolean z) {
        if (!supportsManualStashing() || hasAnyFlag(2) == z) {
            return false;
        }
        this.mPrefs.edit().putBoolean(SHARED_PREFS_STASHED_KEY, z).apply();
        updateStateForFlag(2, z);
        applyState();
        return true;
    }

    /* access modifiers changed from: protected */
    public void addUnstashToHotseatAnimation(AnimatorSet animatorSet, int i) {
        createAnimToIsStashed(false, (long) i, 0, false);
        animatorSet.play(this.mAnimator);
    }

    /* access modifiers changed from: private */
    public void createAnimToIsStashed(boolean z, long j, long j2, boolean z2) {
        float f;
        final boolean z3 = z;
        long j3 = j;
        long j4 = j2;
        AnimatorSet animatorSet = this.mAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.mAnimator = new AnimatorSet();
        float f2 = 0.0f;
        if (!supportsVisualStashing()) {
            this.mAnimator.play(this.mIconAlphaForStash.animateToValue(z3 ? 0.0f : 1.0f).setDuration(j3));
            AnimatorSet animatorSet2 = this.mAnimator;
            AnimatedFloat animatedFloat = this.mTaskbarImeBgAlpha;
            if (!hasAnyFlag(32)) {
                f2 = 1.0f;
            }
            animatorSet2.play(animatedFloat.animateToValue(f2).setDuration(j3));
            this.mAnimator.setStartDelay(j4);
            this.mAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = TaskbarStashController.this.mAnimator = null;
                }
            });
            return;
        }
        AnimatorSet animatorSet3 = new AnimatorSet();
        AnimatorSet animatorSet4 = new AnimatorSet();
        AnimatorSet animatorSet5 = new AnimatorSet();
        float f3 = 0.5f;
        if (z3) {
            animatorSet3.play(this.mIconTranslationYForStash.animateToValue(((float) (this.mUnstashedHeight - this.mStashedHeight)) / 2.0f));
            if (z2) {
                animatorSet3.play(this.mTaskbarBackgroundOffset.animateToValue(1.0f));
            } else {
                animatorSet3.addListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
                    public final void run() {
                        TaskbarStashController.this.lambda$createAnimToIsStashed$1$TaskbarStashController();
                    }
                }));
            }
            animatorSet4.playTogether(new Animator[]{this.mIconAlphaForStash.animateToValue(0.0f), this.mIconScaleForStash.animateToValue(0.5f)});
            animatorSet5.playTogether(new Animator[]{this.mTaskbarStashedHandleAlpha.animateToValue(1.0f)});
            f = 0.75f;
        } else {
            animatorSet3.playTogether(new Animator[]{this.mIconScaleForStash.animateToValue(1.0f), this.mIconTranslationYForStash.animateToValue(0.0f)});
            if (z2) {
                animatorSet3.play(this.mTaskbarBackgroundOffset.animateToValue(0.0f));
            } else {
                animatorSet3.addListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
                    public final void run() {
                        TaskbarStashController.this.lambda$createAnimToIsStashed$2$TaskbarStashController();
                    }
                }));
            }
            animatorSet4.playTogether(new Animator[]{this.mTaskbarStashedHandleAlpha.animateToValue(0.0f)});
            animatorSet5.playTogether(new Animator[]{this.mIconAlphaForStash.animateToValue(1.0f)});
            f = 0.5f;
            f3 = 0.75f;
        }
        animatorSet3.play(this.mControllers.stashedHandleViewController.createRevealAnimToIsStashed(z3));
        animatorSet3.play(this.mTaskbarStashedHandleHintScale.animateToValue(1.0f));
        animatorSet3.setDuration(j3);
        float f4 = (float) j3;
        animatorSet4.setDuration((long) (f * f4));
        animatorSet5.setDuration((long) (f4 * f3));
        animatorSet5.setStartDelay((long) (f4 * (1.0f - f3)));
        this.mAnimator.playTogether(new Animator[]{animatorSet3, animatorSet4, animatorSet5});
        this.mAnimator.setStartDelay(j2);
        this.mAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                boolean unused = TaskbarStashController.this.mIsStashed = z3;
                TaskbarStashController taskbarStashController = TaskbarStashController.this;
                taskbarStashController.onIsStashedChanged(taskbarStashController.mIsStashed);
            }

            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = TaskbarStashController.this.mAnimator = null;
            }
        });
    }

    public /* synthetic */ void lambda$createAnimToIsStashed$1$TaskbarStashController() {
        this.mTaskbarBackgroundOffset.updateValue(1.0f);
    }

    public /* synthetic */ void lambda$createAnimToIsStashed$2$TaskbarStashController() {
        this.mTaskbarBackgroundOffset.updateValue(0.0f);
    }

    public void startStashHint(boolean z) {
        if (!isStashed() && supportsManualStashing()) {
            this.mIconScaleForStash.animateToValue(z ? STASHED_TASKBAR_HINT_SCALE : 1.0f).setDuration(TASKBAR_HINT_STASH_DURATION).start();
        }
    }

    public void startUnstashHint(boolean z) {
        if (isStashed() && canCurrentlyManuallyUnstash()) {
            this.mTaskbarStashedHandleHintScale.animateToValue(z ? UNSTASHED_TASKBAR_HANDLE_HINT_SCALE : 1.0f).setDuration(TASKBAR_HINT_STASH_DURATION).start();
        }
    }

    /* access modifiers changed from: private */
    public void onIsStashedChanged(boolean z) {
        this.mControllers.runAfterInit(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TaskbarStashController.this.lambda$onIsStashedChanged$3$TaskbarStashController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onIsStashedChanged$3$TaskbarStashController(boolean z) {
        this.mControllers.stashedHandleViewController.onIsStashedChanged(z);
        this.mControllers.taskbarInsetsController.onTaskbarWindowHeightOrInsetsChanged();
    }

    public void applyState() {
        applyState(hasAnyFlag(256) ? 0 : 300);
    }

    public void applyState(long j) {
        this.mStatePropertyHolder.setState(this.mState, j, true);
    }

    public void applyState(long j, long j2) {
        this.mStatePropertyHolder.setState(this.mState, j, j2, true);
    }

    public Animator applyStateWithoutStart() {
        return applyStateWithoutStart(300);
    }

    public Animator applyStateWithoutStart(long j) {
        return this.mStatePropertyHolder.setState(this.mState, j, false);
    }

    public void setSystemGestureInProgress(boolean z) {
        this.mIsSystemGestureInProgress = z;
        if (!z) {
            maybeResetStashedInAppAllApps(hasAnyFlag(32) == this.mIsImeShowing);
            boolean hasAnyFlag = hasAnyFlag(32);
            boolean z2 = this.mIsImeShowing;
            if (hasAnyFlag != z2) {
                updateStateForFlag(32, z2);
                applyState(TASKBAR_STASH_DURATION_FOR_IME, getTaskbarStashStartDelayForIme());
            }
        }
    }

    public void maybeResetStashedInAppAllApps() {
        maybeResetStashedInAppAllApps(true);
    }

    private void maybeResetStashedInAppAllApps(boolean z) {
        if (!this.mIsSystemGestureInProgress) {
            updateStateForFlag(128, false);
            if (z) {
                applyState((long) LauncherState.ALL_APPS.getTransitionDuration(this.mControllers.taskbarActivityContext, false));
            }
        }
    }

    private long getTaskbarStashStartDelayForIme() {
        if (this.mIsImeShowing) {
            return 0;
        }
        return ((long) this.mControllers.taskbarActivityContext.getResources().getInteger(17694720)) - TASKBAR_STASH_DURATION_FOR_IME;
    }

    public void updateStateForSysuiFlags(int i, boolean z) {
        long j;
        long j2;
        updateStateForFlag(4, hasAnyFlag(i, 1));
        boolean hasAnyFlag = hasAnyFlag(i, 262144);
        this.mIsImeShowing = hasAnyFlag;
        long j3 = 0;
        if (!this.mIsSystemGestureInProgress) {
            updateStateForFlag(32, hasAnyFlag);
            j2 = TASKBAR_STASH_DURATION_FOR_IME;
            j = getTaskbarStashStartDelayForIme();
        } else {
            j2 = 300;
            j = 0;
        }
        if (z) {
            j2 = 0;
        }
        if (!z) {
            j3 = j;
        }
        applyState(j2, j3);
    }

    public void updateStateForFlag(int i, boolean z) {
        if (i == 1 && TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.TASKBAR_IN_APP_STATE, String.format("setting flag FLAG_IN_APP to: %b", new Object[]{Boolean.valueOf(z)}), new Exception());
        }
        if (z) {
            this.mState = i | this.mState;
            return;
        }
        this.mState = (~i) & this.mState;
    }

    /* access modifiers changed from: private */
    public void onStateChangeApplied(int i) {
        if (hasAnyFlag(i, FLAGS_STASHED_IN_APP)) {
            this.mControllers.uiController.onStashedInAppChanged();
        }
        if (hasAnyFlag(i, 447)) {
            notifyStashChange(hasAnyFlag(257), isStashedInApp());
        }
        if (!hasAnyFlag(i, 2)) {
            return;
        }
        if (hasAnyFlag(2)) {
            this.mActivity.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_LONGPRESS_HIDE);
        } else {
            this.mActivity.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_TASKBAR_LONGPRESS_SHOW);
        }
    }

    private void notifyStashChange(boolean z, boolean z2) {
        this.mSystemUiProxy.notifyTaskbarStatus(z, z2);
        this.mControllers.taskbarActivityContext.updateInsetRoundedCornerFrame(z && !isStashedInAppIgnoringIme());
        this.mControllers.rotationButtonController.onTaskbarStateChange(z, z2);
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarStashController:");
        printWriter.println(String.format("%s\tmStashedHeight=%dpx", new Object[]{str, Integer.valueOf(this.mStashedHeight)}));
        printWriter.println(String.format("%s\tmUnstashedHeight=%dpx", new Object[]{str, Integer.valueOf(this.mUnstashedHeight)}));
        printWriter.println(String.format("%s\tmIsStashed=%b", new Object[]{str, Boolean.valueOf(this.mIsStashed)}));
        printWriter.println(String.format("%s\tappliedState=%s", new Object[]{str, getStateString(this.mStatePropertyHolder.mPrevFlags)}));
        printWriter.println(String.format("%s\tmState=%s", new Object[]{str, getStateString(this.mState)}));
        printWriter.println(String.format("%s\tmIsSystemGestureInProgress=%b", new Object[]{str, Boolean.valueOf(this.mIsSystemGestureInProgress)}));
        printWriter.println(String.format("%s\tmIsImeShowing=%b", new Object[]{str, Boolean.valueOf(this.mIsImeShowing)}));
    }

    private static String getStateString(int i) {
        StringJoiner stringJoiner = new StringJoiner("|");
        Utilities.appendFlag(stringJoiner, i, 257, "FLAG_IN_APP");
        Utilities.appendFlag(stringJoiner, i, 2, "FLAG_STASHED_IN_APP_MANUAL");
        Utilities.appendFlag(stringJoiner, i, 4, "FLAG_STASHED_IN_APP_PINNED");
        Utilities.appendFlag(stringJoiner, i, 8, "FLAG_STASHED_IN_APP_EMPTY");
        Utilities.appendFlag(stringJoiner, i, 16, "FLAG_STASHED_IN_APP_SETUP");
        Utilities.appendFlag(stringJoiner, i, 32, "FLAG_STASHED_IN_APP_IME");
        Utilities.appendFlag(stringJoiner, i, 64, "FLAG_IN_STASHED_LAUNCHER_STATE");
        Utilities.appendFlag(stringJoiner, i, 128, "FLAG_STASHED_IN_APP_ALL_APPS");
        Utilities.appendFlag(stringJoiner, i, 256, "FLAG_IN_SETUP");
        return stringJoiner.toString();
    }

    private class StatePropertyHolder {
        private boolean mIsStashed;
        /* access modifiers changed from: private */
        public int mPrevFlags;
        private final IntPredicate mStashCondition;

        StatePropertyHolder(IntPredicate intPredicate) {
            this.mStashCondition = intPredicate;
        }

        public Animator setState(int i, long j, boolean z) {
            return setState(i, j, 0, z);
        }

        public Animator setState(int i, long j, long j2, boolean z) {
            int i2 = this.mPrevFlags;
            int i3 = i2 ^ i;
            if (i2 != i) {
                TaskbarStashController.this.onStateChangeApplied(i3);
                this.mPrevFlags = i;
            }
            boolean test = this.mStashCondition.test(i);
            if (this.mIsStashed == test) {
                return null;
            }
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.TASKBAR_IN_APP_STATE, String.format("setState: mIsStashed=%b, isStashed=%b, duration=%d, start=:%b", new Object[]{Boolean.valueOf(this.mIsStashed), Boolean.valueOf(test), Long.valueOf(j), Boolean.valueOf(z)}));
            }
            this.mIsStashed = test;
            TaskbarStashController.this.createAnimToIsStashed(test, j, j2, true);
            if (z) {
                TaskbarStashController.this.mAnimator.start();
            }
            return TaskbarStashController.this.mAnimator;
        }
    }
}
