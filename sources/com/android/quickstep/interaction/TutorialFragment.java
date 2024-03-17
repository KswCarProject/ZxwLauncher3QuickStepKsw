package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Insets;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.logging.StatsLogManager;
import com.android.quickstep.interaction.TutorialController;
import java.util.Objects;

abstract class TutorialFragment extends Fragment implements View.OnTouchListener {
    private static final String COMPLETED_TUTORIAL_STEPS_PREFERENCE_KEY = "pref_completedTutorialSteps";
    static final String KEY_GESTURE_COMPLETE = "gesture_complete";
    static final String KEY_TUTORIAL_TYPE = "tutorial_type";
    private static final String LOG_TAG = "TutorialFragment";
    private static final String TUTORIAL_SKIPPED_PREFERENCE_KEY = "pref_gestureTutorialSkipped";
    private DeviceProfile mDeviceProfile;
    /* access modifiers changed from: private */
    public AnimatedVectorDrawable mEdgeAnimation = null;
    EdgeBackGestureHandler mEdgeBackGestureHandler;
    private ImageView mEdgeGestureVideoView;
    View mFakePreviousTaskView;
    View mFingerDotView;
    private boolean mFragmentStopped = false;
    private Animator mGestureAnimation = null;
    boolean mGestureComplete = false;
    private boolean mIntroductionShown = false;
    private boolean mIsFoldable;
    private boolean mIsLargeScreen;
    NavBarGestureHandler mNavBarGestureHandler;
    RootSandboxLayout mRootView;
    TutorialController mTutorialController = null;
    TutorialController.TutorialType mTutorialType;

    /* access modifiers changed from: package-private */
    public abstract TutorialController createController(TutorialController.TutorialType tutorialType);

    /* access modifiers changed from: protected */
    public Animator createGestureAnimation() {
        return null;
    }

    /* access modifiers changed from: package-private */
    public abstract Class<? extends TutorialController> getControllerClass();

    /* access modifiers changed from: package-private */
    public Integer getEdgeAnimationResId() {
        return null;
    }

    /* access modifiers changed from: package-private */
    public abstract void logTutorialStepCompleted(StatsLogManager statsLogManager);

    /* access modifiers changed from: package-private */
    public abstract void logTutorialStepShown(StatsLogManager statsLogManager);

    TutorialFragment() {
    }

    public static TutorialFragment newInstance(TutorialController.TutorialType tutorialType, boolean z) {
        TutorialFragment fragmentForTutorialType = getFragmentForTutorialType(tutorialType);
        if (fragmentForTutorialType == null) {
            fragmentForTutorialType = new BackGestureTutorialFragment();
            tutorialType = TutorialController.TutorialType.BACK_NAVIGATION;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TUTORIAL_TYPE, tutorialType);
        bundle.putBoolean(KEY_GESTURE_COMPLETE, z);
        fragmentForTutorialType.setArguments(bundle);
        return fragmentForTutorialType;
    }

    /* renamed from: com.android.quickstep.interaction.TutorialFragment$4  reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType;

        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|(3:17|18|20)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.quickstep.interaction.TutorialController$TutorialType[] r0 = com.android.quickstep.interaction.TutorialController.TutorialType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType = r0
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.BACK_NAVIGATION     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.HOME_NAVIGATION     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.HOME_NAVIGATION_COMPLETE     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.OVERVIEW_NAVIGATION     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.OVERVIEW_NAVIGATION_COMPLETE     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.ASSISTANT     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.ASSISTANT_COMPLETE     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.quickstep.interaction.TutorialController$TutorialType r1 = com.android.quickstep.interaction.TutorialController.TutorialType.SANDBOX_MODE     // Catch:{ NoSuchFieldError -> 0x006c }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.interaction.TutorialFragment.AnonymousClass4.<clinit>():void");
        }
    }

    private static TutorialFragment getFragmentForTutorialType(TutorialController.TutorialType tutorialType) {
        switch (AnonymousClass4.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[tutorialType.ordinal()]) {
            case 1:
            case 2:
                return new BackGestureTutorialFragment();
            case 3:
            case 4:
                return new HomeGestureTutorialFragment();
            case 5:
            case 6:
                return new OverviewGestureTutorialFragment();
            case 7:
            case 8:
                return new AssistantGestureTutorialFragment();
            case 9:
                return new SandboxModeTutorialFragment();
            default:
                Log.e(LOG_TAG, "Failed to find an appropriate fragment for " + tutorialType.name());
                return null;
        }
    }

    /* access modifiers changed from: package-private */
    public Animator getGestureAnimation() {
        return this.mGestureAnimation;
    }

    /* access modifiers changed from: package-private */
    public AnimatedVectorDrawable getEdgeAnimation() {
        return this.mEdgeAnimation;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            bundle = getArguments();
        }
        this.mTutorialType = (TutorialController.TutorialType) bundle.getSerializable(KEY_TUTORIAL_TYPE);
        this.mGestureComplete = bundle.getBoolean(KEY_GESTURE_COMPLETE, false);
        this.mEdgeBackGestureHandler = new EdgeBackGestureHandler(getContext());
        this.mNavBarGestureHandler = new NavBarGestureHandler(getContext());
        DeviceProfile deviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).getDeviceProfile(getContext());
        this.mDeviceProfile = deviceProfile;
        this.mIsLargeScreen = deviceProfile.isTablet;
        this.mIsFoldable = this.mDeviceProfile.isTwoPanels;
    }

    public boolean isLargeScreen() {
        return this.mIsLargeScreen;
    }

    public boolean isFoldable() {
        return this.mIsFoldable;
    }

    /* access modifiers changed from: package-private */
    public DeviceProfile getDeviceProfile() {
        return this.mDeviceProfile;
    }

    public void onDestroy() {
        super.onDestroy();
        this.mEdgeBackGestureHandler.unregisterBackGestureAttemptCallback();
        this.mNavBarGestureHandler.unregisterNavBarGestureAttemptCallback();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        RootSandboxLayout rootSandboxLayout = (RootSandboxLayout) layoutInflater.inflate(R.layout.gesture_tutorial_fragment, viewGroup, false);
        this.mRootView = rootSandboxLayout;
        rootSandboxLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return TutorialFragment.this.lambda$onCreateView$0$TutorialFragment(view, windowInsets);
            }
        });
        this.mRootView.setOnTouchListener(this);
        this.mEdgeGestureVideoView = (ImageView) this.mRootView.findViewById(R.id.gesture_tutorial_edge_gesture_video);
        this.mFingerDotView = this.mRootView.findViewById(R.id.gesture_tutorial_finger_dot);
        this.mFakePreviousTaskView = this.mRootView.findViewById(R.id.gesture_tutorial_fake_previous_task_view);
        return this.mRootView;
    }

    public /* synthetic */ WindowInsets lambda$onCreateView$0$TutorialFragment(View view, WindowInsets windowInsets) {
        Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
        this.mEdgeBackGestureHandler.setInsets(insets.left, insets.right);
        return windowInsets;
    }

    public void onStop() {
        super.onStop();
        releaseFeedbackAnimation();
        this.mFragmentStopped = true;
    }

    /* access modifiers changed from: package-private */
    public void initializeFeedbackVideoView() {
        if (updateFeedbackAnimation() && this.mTutorialController != null) {
            if (isGestureComplete()) {
                this.mTutorialController.showSuccessFeedback();
            } else if (!this.mIntroductionShown) {
                int introductionTitle = this.mTutorialController.getIntroductionTitle();
                int introductionSubtitle = this.mTutorialController.getIntroductionSubtitle();
                if (introductionTitle == -1) {
                    Log.e(LOG_TAG, "Cannot show introduction feedback for tutorial step: " + this.mTutorialType + ", no introduction feedback title", new IllegalStateException());
                }
                if (introductionTitle == -1) {
                    Log.e(LOG_TAG, "Cannot show introduction feedback for tutorial step: " + this.mTutorialType + ", no introduction feedback subtitle", new IllegalStateException());
                }
                TutorialController tutorialController = this.mTutorialController;
                tutorialController.showFeedback(introductionTitle, introductionSubtitle, tutorialController.getSpokenIntroductionSubtitle(), false, true);
                this.mIntroductionShown = true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateFeedbackAnimation() {
        if (!updateEdgeAnimation()) {
            return false;
        }
        Animator createGestureAnimation = createGestureAnimation();
        this.mGestureAnimation = createGestureAnimation;
        if (createGestureAnimation != null) {
            createGestureAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    TutorialFragment.this.mFingerDotView.setVisibility(0);
                }

                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    TutorialFragment.this.mFingerDotView.setVisibility(8);
                }

                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    TutorialFragment.this.mFingerDotView.setVisibility(8);
                }
            });
        }
        if (this.mGestureAnimation != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean updateEdgeAnimation() {
        Integer edgeAnimationResId = getEdgeAnimationResId();
        if (edgeAnimationResId == null || getContext() == null) {
            return false;
        }
        AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) getContext().getDrawable(edgeAnimationResId.intValue());
        this.mEdgeAnimation = animatedVectorDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    TutorialFragment.this.mEdgeAnimation.start();
                }
            });
        }
        this.mEdgeGestureVideoView.setImageDrawable(this.mEdgeAnimation);
        if (this.mEdgeAnimation != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void releaseFeedbackAnimation() {
        TutorialController tutorialController = this.mTutorialController;
        if (tutorialController != null && !tutorialController.isGestureCompleted()) {
            this.mTutorialController.cancelQueuedGestureAnimation();
        }
        Animator animator = this.mGestureAnimation;
        if (animator != null && animator.isRunning()) {
            this.mGestureAnimation.cancel();
        }
        AnimatedVectorDrawable animatedVectorDrawable = this.mEdgeAnimation;
        if (animatedVectorDrawable != null && animatedVectorDrawable.isRunning()) {
            this.mEdgeAnimation.stop();
        }
        this.mEdgeGestureVideoView.setVisibility(8);
    }

    public void onResume() {
        TutorialController tutorialController;
        super.onResume();
        releaseFeedbackAnimation();
        if (!this.mFragmentStopped || (tutorialController = this.mTutorialController) == null) {
            this.mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    TutorialFragment tutorialFragment = TutorialFragment.this;
                    tutorialFragment.changeController(tutorialFragment.mTutorialType);
                    TutorialFragment.this.mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            return;
        }
        tutorialController.showFeedback();
        this.mFragmentStopped = false;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (this.mTutorialController != null && !isGestureComplete()) {
            this.mTutorialController.hideFeedback();
        }
        return this.mNavBarGestureHandler.onTouch(view, motionEvent) | this.mEdgeBackGestureHandler.onTouch(view, motionEvent);
    }

    /* access modifiers changed from: package-private */
    public boolean onInterceptTouch(MotionEvent motionEvent) {
        return this.mNavBarGestureHandler.onInterceptTouch(motionEvent) | this.mEdgeBackGestureHandler.onInterceptTouch(motionEvent);
    }

    /* access modifiers changed from: package-private */
    public void onAttachedToWindow() {
        StatsLogManager statsLogManager = getStatsLogManager();
        if (statsLogManager != null) {
            logTutorialStepShown(statsLogManager);
        }
        this.mEdgeBackGestureHandler.setViewGroupParent(getRootView());
    }

    /* access modifiers changed from: package-private */
    public void onDetachedFromWindow() {
        this.mEdgeBackGestureHandler.setViewGroupParent((ViewGroup) null);
    }

    /* access modifiers changed from: package-private */
    public void changeController(TutorialController.TutorialType tutorialType) {
        if (getControllerClass().isInstance(this.mTutorialController)) {
            this.mTutorialController.setTutorialType(tutorialType);
            TutorialController tutorialController = this.mTutorialController;
            Objects.requireNonNull(tutorialController);
            tutorialController.fadeTaskViewAndRun(new Runnable() {
                public final void run() {
                    TutorialController.this.transitToController();
                }
            });
        } else {
            TutorialController createController = createController(tutorialType);
            this.mTutorialController = createController;
            createController.transitToController();
        }
        this.mEdgeBackGestureHandler.registerBackGestureAttemptCallback(this.mTutorialController);
        this.mNavBarGestureHandler.registerNavBarGestureAttemptCallback(this.mTutorialController);
        this.mTutorialType = tutorialType;
        initializeFeedbackVideoView();
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(KEY_TUTORIAL_TYPE, this.mTutorialType);
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: package-private */
    public RootSandboxLayout getRootView() {
        return this.mRootView;
    }

    /* access modifiers changed from: package-private */
    public void continueTutorial() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences != null) {
            ArraySet arraySet = new ArraySet(sharedPreferences.getStringSet(COMPLETED_TUTORIAL_STEPS_PREFERENCE_KEY, new ArraySet()));
            arraySet.add(this.mTutorialType.toString());
            sharedPreferences.edit().putStringSet(COMPLETED_TUTORIAL_STEPS_PREFERENCE_KEY, arraySet).apply();
        }
        StatsLogManager statsLogManager = getStatsLogManager();
        if (statsLogManager != null) {
            logTutorialStepCompleted(statsLogManager);
        }
        GestureSandboxActivity gestureSandboxActivity = getGestureSandboxActivity();
        if (gestureSandboxActivity == null) {
            closeTutorial();
        } else {
            gestureSandboxActivity.continueTutorial();
        }
    }

    /* access modifiers changed from: package-private */
    public void closeTutorial() {
        closeTutorial(false);
    }

    /* access modifiers changed from: package-private */
    public void closeTutorial(boolean z) {
        if (z) {
            SharedPreferences sharedPreferences = getSharedPreferences();
            if (sharedPreferences != null) {
                sharedPreferences.edit().putBoolean(TUTORIAL_SKIPPED_PREFERENCE_KEY, true).apply();
            }
            StatsLogManager statsLogManager = getStatsLogManager();
            if (statsLogManager != null) {
                statsLogManager.logger().log(StatsLogManager.LauncherEvent.LAUNCHER_GESTURE_TUTORIAL_SKIPPED);
            }
        }
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setResult(-1);
            activity.finish();
        }
    }

    /* access modifiers changed from: package-private */
    public void startSystemNavigationSetting() {
        startActivity(new Intent("com.android.settings.GESTURE_NAVIGATION_SETTINGS"));
    }

    /* access modifiers changed from: package-private */
    public int getCurrentStep() {
        GestureSandboxActivity gestureSandboxActivity = getGestureSandboxActivity();
        if (gestureSandboxActivity == null) {
            return -1;
        }
        return gestureSandboxActivity.getCurrentStep();
    }

    /* access modifiers changed from: package-private */
    public int getNumSteps() {
        GestureSandboxActivity gestureSandboxActivity = getGestureSandboxActivity();
        if (gestureSandboxActivity == null) {
            return -1;
        }
        return gestureSandboxActivity.getNumSteps();
    }

    /* access modifiers changed from: package-private */
    public boolean isAtFinalStep() {
        return getCurrentStep() == getNumSteps();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.mTutorialController;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isGestureComplete() {
        /*
            r1 = this;
            boolean r0 = r1.mGestureComplete
            if (r0 != 0) goto L_0x0011
            com.android.quickstep.interaction.TutorialController r0 = r1.mTutorialController
            if (r0 == 0) goto L_0x000f
            boolean r0 = r0.isGestureCompleted()
            if (r0 == 0) goto L_0x000f
            goto L_0x0011
        L_0x000f:
            r0 = 0
            goto L_0x0012
        L_0x0011:
            r0 = 1
        L_0x0012:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.interaction.TutorialFragment.isGestureComplete():boolean");
    }

    private GestureSandboxActivity getGestureSandboxActivity() {
        Context context = getContext();
        if (context instanceof GestureSandboxActivity) {
            return (GestureSandboxActivity) context;
        }
        return null;
    }

    private StatsLogManager getStatsLogManager() {
        GestureSandboxActivity gestureSandboxActivity = getGestureSandboxActivity();
        if (gestureSandboxActivity != null) {
            return gestureSandboxActivity.getStatsLogManager();
        }
        return null;
    }

    private SharedPreferences getSharedPreferences() {
        GestureSandboxActivity gestureSandboxActivity = getGestureSandboxActivity();
        if (gestureSandboxActivity != null) {
            return gestureSandboxActivity.getSharedPrefs();
        }
        return null;
    }
}
