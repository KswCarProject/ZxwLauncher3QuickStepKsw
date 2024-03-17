package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.views.ClipIconView;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import java.util.ArrayList;
import java.util.Objects;

abstract class TutorialController implements EdgeBackGestureHandler.BackGestureAttemptCallback, NavBarGestureHandler.NavBarGestureAttemptCallback {
    private static final int ADVANCE_TUTORIAL_TIMEOUT_MS = 2000;
    private static final CharSequence DEFAULT_PIXEL_TIPS_APP_NAME = "Pixel Tips";
    private static final int FEEDBACK_ANIMATION_MS = 133;
    private static final int FINGER_DOT_ANIMATION_DURATION_MILLIS = 500;
    private static final float FINGER_DOT_SMALL_SCALE = 0.7f;
    private static final float FINGER_DOT_VISIBLE_ALPHA = 0.7f;
    private static final int GESTURE_ANIMATION_DELAY_MS = 1500;
    private static final long GESTURE_ANIMATION_PAUSE_DURATION_MILLIS = 1000;
    private static final String LOG_TAG = "TutorialController";
    private static final String PIXEL_TIPS_APP_PACKAGE_NAME = "com.google.android.apps.tips";
    private static final int RIPPLE_VISIBLE_MS = 300;
    final Context mContext;
    final Button mDoneButton;
    final ImageView mEdgeGestureVideoView;
    final FrameLayout mFakeHotseatView;
    final ClipIconView mFakeIconView;
    final RelativeLayout mFakeLauncherView;
    final AnimatedTaskView mFakePreviousTaskView;
    final FrameLayout mFakeTaskView;
    private Runnable mFakeTaskViewCallback;
    final AnimatedTaskbarView mFakeTaskbarView;
    private Runnable mFakeTaskbarViewCallback;
    final TextView mFeedbackTitleView;
    final ViewGroup mFeedbackView;
    private Runnable mFeedbackViewCallback;
    final ImageView mFingerDotView;
    private boolean mGestureCompleted = false;
    View mHotseatIconView;
    final RippleDrawable mRippleDrawable;
    final View mRippleView;
    private final Runnable mShowFeedbackRunnable;
    final TextView mSkipButton;
    private final AlertDialog mSkipTutorialDialog;
    private final Runnable mTitleViewCallback;
    final TutorialFragment mTutorialFragment;
    final TutorialStepIndicator mTutorialStepView;
    TutorialType mTutorialType;

    enum TutorialType {
        BACK_NAVIGATION,
        BACK_NAVIGATION_COMPLETE,
        HOME_NAVIGATION,
        HOME_NAVIGATION_COMPLETE,
        OVERVIEW_NAVIGATION,
        OVERVIEW_NAVIGATION_COMPLETE,
        ASSISTANT,
        ASSISTANT_COMPLETE,
        SANDBOX_MODE
    }

    public int getIntroductionSubtitle() {
        return -1;
    }

    public int getIntroductionTitle() {
        return -1;
    }

    public int getMockAppIconResId() {
        return R.drawable.default_sandbox_app_icon;
    }

    /* access modifiers changed from: protected */
    public int getMockAppTaskLayoutResId() {
        return -1;
    }

    /* access modifiers changed from: protected */
    public int getMockPreviousAppTaskThumbnailColorResId() {
        return R.color.gesture_tutorial_fake_previous_task_view_color;
    }

    public int getMockWallpaperResId() {
        return R.drawable.default_sandbox_wallpaper;
    }

    public int getSpokenIntroductionSubtitle() {
        return -1;
    }

    public int getSuccessFeedbackSubtitle() {
        return -1;
    }

    TutorialController(TutorialFragment tutorialFragment, TutorialType tutorialType) {
        this.mTutorialFragment = tutorialFragment;
        this.mTutorialType = tutorialType;
        this.mContext = tutorialFragment.getContext();
        RootSandboxLayout rootView = tutorialFragment.getRootView();
        TextView textView = (TextView) rootView.findViewById(R.id.gesture_tutorial_fragment_close_button);
        this.mSkipButton = textView;
        textView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TutorialController.this.lambda$new$0$TutorialController(view);
            }
        });
        ViewGroup viewGroup = (ViewGroup) rootView.findViewById(R.id.gesture_tutorial_fragment_feedback_view);
        this.mFeedbackView = viewGroup;
        this.mFeedbackTitleView = (TextView) viewGroup.findViewById(R.id.gesture_tutorial_fragment_feedback_title);
        this.mEdgeGestureVideoView = (ImageView) rootView.findViewById(R.id.gesture_tutorial_edge_gesture_video);
        this.mFakeLauncherView = (RelativeLayout) rootView.findViewById(R.id.gesture_tutorial_fake_launcher_view);
        this.mFakeHotseatView = (FrameLayout) rootView.findViewById(R.id.gesture_tutorial_fake_hotseat_view);
        this.mFakeIconView = (ClipIconView) rootView.findViewById(R.id.gesture_tutorial_fake_icon_view);
        this.mFakeTaskView = (FrameLayout) rootView.findViewById(R.id.gesture_tutorial_fake_task_view);
        this.mFakeTaskbarView = (AnimatedTaskbarView) rootView.findViewById(R.id.gesture_tutorial_fake_taskbar_view);
        this.mFakePreviousTaskView = (AnimatedTaskView) rootView.findViewById(R.id.gesture_tutorial_fake_previous_task_view);
        View findViewById = rootView.findViewById(R.id.gesture_tutorial_ripple_view);
        this.mRippleView = findViewById;
        this.mRippleDrawable = (RippleDrawable) findViewById.getBackground();
        this.mDoneButton = (Button) rootView.findViewById(R.id.gesture_tutorial_fragment_action_button);
        this.mTutorialStepView = (TutorialStepIndicator) rootView.findViewById(R.id.gesture_tutorial_fragment_feedback_tutorial_step);
        this.mFingerDotView = (ImageView) rootView.findViewById(R.id.gesture_tutorial_finger_dot);
        this.mSkipTutorialDialog = createSkipTutorialDialog();
        this.mTitleViewCallback = new Runnable() {
            public final void run() {
                TutorialController.this.lambda$new$1$TutorialController();
            }
        };
        this.mShowFeedbackRunnable = new Runnable() {
            public final void run() {
                TutorialController.this.lambda$new$3$TutorialController();
            }
        };
    }

    public /* synthetic */ void lambda$new$0$TutorialController(View view) {
        showSkipTutorialDialog();
    }

    public /* synthetic */ void lambda$new$1$TutorialController() {
        this.mFeedbackTitleView.sendAccessibilityEvent(8);
    }

    public /* synthetic */ void lambda$new$3$TutorialController() {
        this.mFeedbackView.setAlpha(0.0f);
        this.mFeedbackView.setScaleX(0.95f);
        this.mFeedbackView.setScaleY(0.95f);
        this.mFeedbackView.setVisibility(0);
        this.mFeedbackView.animate().setDuration(133).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).withEndAction(new Runnable() {
            public final void run() {
                TutorialController.this.lambda$new$2$TutorialController();
            }
        }).start();
        this.mFeedbackTitleView.postDelayed(this.mTitleViewCallback, 133);
    }

    public /* synthetic */ void lambda$new$2$TutorialController() {
        if (this.mGestureCompleted && !this.mTutorialFragment.isAtFinalStep()) {
            Runnable runnable = this.mFeedbackViewCallback;
            if (runnable != null) {
                this.mFeedbackView.removeCallbacks(runnable);
            }
            TutorialFragment tutorialFragment = this.mTutorialFragment;
            Objects.requireNonNull(tutorialFragment);
            $$Lambda$t4Ai1_ZEyN3Vpx4CY1gAN3Ew4U r1 = new Runnable() {
                public final void run() {
                    TutorialFragment.this.continueTutorial();
                }
            };
            this.mFeedbackViewCallback = r1;
            this.mFeedbackView.postDelayed(r1, 2000);
        }
    }

    private void showSkipTutorialDialog() {
        AlertDialog alertDialog = this.mSkipTutorialDialog;
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    public int getHotseatIconTop() {
        if (this.mHotseatIconView == null) {
            return 0;
        }
        return this.mFakeHotseatView.getTop() + this.mHotseatIconView.getTop();
    }

    public int getHotseatIconLeft() {
        if (this.mHotseatIconView == null) {
            return 0;
        }
        return this.mFakeHotseatView.getLeft() + this.mHotseatIconView.getLeft();
    }

    /* access modifiers changed from: package-private */
    public void setTutorialType(TutorialType tutorialType) {
        this.mTutorialType = tutorialType;
    }

    /* access modifiers changed from: protected */
    public int getMockHotseatResId() {
        if (this.mTutorialFragment.isLargeScreen()) {
            return this.mTutorialFragment.isFoldable() ? R.layout.gesture_tutorial_foldable_mock_hotseat : R.layout.gesture_tutorial_tablet_mock_hotseat;
        }
        return R.layout.gesture_tutorial_mock_hotseat;
    }

    /* access modifiers changed from: package-private */
    public void fadeTaskViewAndRun(Runnable runnable) {
        this.mFakeTaskView.animate().alpha(0.0f).setListener(AnimatorListeners.forSuccessCallback(runnable));
    }

    /* access modifiers changed from: package-private */
    public void showFeedback() {
        if (this.mGestureCompleted) {
            this.mFeedbackView.setTranslationY(0.0f);
            return;
        }
        Animator gestureAnimation = this.mTutorialFragment.getGestureAnimation();
        AnimatedVectorDrawable edgeAnimation = this.mTutorialFragment.getEdgeAnimation();
        if (gestureAnimation != null && edgeAnimation != null) {
            playFeedbackAnimation(gestureAnimation, edgeAnimation, this.mShowFeedbackRunnable, true);
        }
    }

    /* access modifiers changed from: package-private */
    public void showSuccessFeedback() {
        int successFeedbackSubtitle = getSuccessFeedbackSubtitle();
        if (successFeedbackSubtitle == -1) {
            Log.e(LOG_TAG, "Cannot show success feedback for tutorial step: " + this.mTutorialType + ", no success feedback subtitle", new IllegalStateException());
        }
        showFeedback(successFeedbackSubtitle, true);
    }

    /* access modifiers changed from: package-private */
    public void showFeedback(int i) {
        showFeedback(i, false);
    }

    /* access modifiers changed from: package-private */
    public void showFeedback(int i, boolean z) {
        showFeedback(z ? R.string.gesture_tutorial_nice : R.string.gesture_tutorial_try_again, i, -1, z, false);
    }

    /* access modifiers changed from: package-private */
    public void showFeedback(int i, int i2, int i3, boolean z, boolean z2) {
        CharSequence charSequence;
        this.mFeedbackTitleView.removeCallbacks(this.mTitleViewCallback);
        Runnable runnable = this.mFeedbackViewCallback;
        if (runnable != null) {
            this.mFeedbackView.removeCallbacks(runnable);
            this.mFeedbackViewCallback = null;
        }
        this.mFeedbackTitleView.setText(i);
        TextView textView = (TextView) this.mFeedbackView.findViewById(R.id.gesture_tutorial_fragment_feedback_subtitle);
        if (i3 == -1) {
            charSequence = this.mContext.getText(i2);
        } else {
            charSequence = Utilities.wrapForTts(this.mContext.getText(i2), this.mContext.getString(i3));
        }
        textView.setText(charSequence);
        if (z) {
            if (this.mTutorialFragment.isAtFinalStep()) {
                showActionButton();
            }
            Runnable runnable2 = this.mFakeTaskViewCallback;
            if (runnable2 != null) {
                this.mFakeTaskView.removeCallbacks(runnable2);
                this.mFakeTaskViewCallback = null;
            }
        }
        this.mGestureCompleted = z;
        Animator gestureAnimation = this.mTutorialFragment.getGestureAnimation();
        AnimatedVectorDrawable edgeAnimation = this.mTutorialFragment.getEdgeAnimation();
        if (z || gestureAnimation == null || edgeAnimation == null) {
            this.mTutorialFragment.releaseFeedbackAnimation();
            Runnable runnable3 = this.mShowFeedbackRunnable;
            this.mFeedbackViewCallback = runnable3;
            this.mFeedbackView.post(runnable3);
            return;
        }
        playFeedbackAnimation(gestureAnimation, edgeAnimation, this.mShowFeedbackRunnable, z2);
    }

    public boolean isGestureCompleted() {
        return this.mGestureCompleted;
    }

    /* access modifiers changed from: package-private */
    public void hideFeedback() {
        if (this.mFeedbackView.getVisibility() == 0) {
            cancelQueuedGestureAnimation();
            this.mFeedbackView.clearAnimation();
            this.mFeedbackView.setVisibility(4);
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelQueuedGestureAnimation() {
        Runnable runnable = this.mFeedbackViewCallback;
        if (runnable != null) {
            this.mFeedbackView.removeCallbacks(runnable);
            this.mFeedbackViewCallback = null;
        }
        Runnable runnable2 = this.mFakeTaskViewCallback;
        if (runnable2 != null) {
            this.mFakeTaskView.removeCallbacks(runnable2);
            this.mFakeTaskViewCallback = null;
        }
        Runnable runnable3 = this.mFakeTaskbarViewCallback;
        if (runnable3 != null) {
            this.mFakeTaskbarView.removeCallbacks(runnable3);
            this.mFakeTaskbarViewCallback = null;
        }
        this.mFeedbackTitleView.removeCallbacks(this.mTitleViewCallback);
    }

    private void playFeedbackAnimation(Animator animator, AnimatedVectorDrawable animatedVectorDrawable, Runnable runnable, boolean z) {
        if (animator.isRunning()) {
            animator.cancel();
        }
        if (animatedVectorDrawable.isRunning()) {
            animatedVectorDrawable.reset();
        }
        final AnimatedVectorDrawable animatedVectorDrawable2 = animatedVectorDrawable;
        final boolean z2 = z;
        final Runnable runnable2 = runnable;
        final Animator animator2 = animator;
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                TutorialController.this.mEdgeGestureVideoView.setVisibility(8);
                if (animatedVectorDrawable2.isRunning()) {
                    animatedVectorDrawable2.stop();
                }
                if (!z2) {
                    runnable2.run();
                }
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                TutorialController.this.mEdgeGestureVideoView.setVisibility(0);
                animatedVectorDrawable2.start();
                animator2.removeListener(this);
            }
        });
        cancelQueuedGestureAnimation();
        if (z) {
            this.mFeedbackViewCallback = runnable;
            Objects.requireNonNull(animator);
            this.mFakeTaskViewCallback = new Runnable(animator) {
                public final /* synthetic */ Animator f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    this.f$0.start();
                }
            };
            this.mFeedbackView.post(this.mFeedbackViewCallback);
            this.mFakeTaskView.postDelayed(this.mFakeTaskViewCallback, 1500);
            return;
        }
        animator.start();
    }

    /* access modifiers changed from: package-private */
    public void setRippleHotspot(float f, float f2) {
        this.mRippleDrawable.setHotspot(f, f2);
    }

    /* access modifiers changed from: package-private */
    public void showRippleEffect(Runnable runnable) {
        this.mRippleDrawable.setState(new int[]{16842919, 16842910});
        this.mRippleView.postDelayed(new Runnable(runnable) {
            public final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TutorialController.this.lambda$showRippleEffect$4$TutorialController(this.f$1);
            }
        }, 300);
    }

    public /* synthetic */ void lambda$showRippleEffect$4$TutorialController(Runnable runnable) {
        this.mRippleDrawable.setState(new int[0]);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: package-private */
    public void onActionButtonClicked(View view) {
        this.mTutorialFragment.continueTutorial();
    }

    /* access modifiers changed from: package-private */
    public void transitToController() {
        hideFeedback();
        hideActionButton();
        updateCloseButton();
        updateSubtext();
        updateDrawables();
        updateLayout();
        this.mGestureCompleted = false;
        FrameLayout frameLayout = this.mFakeHotseatView;
        if (frameLayout != null) {
            frameLayout.setVisibility(4);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateCloseButton() {
        this.mSkipButton.setTextAppearance(Utilities.isDarkTheme(this.mContext) ? R.style.TextAppearance_GestureTutorial_Feedback_Subtext : R.style.TextAppearance_GestureTutorial_Feedback_Subtext_Dark);
    }

    /* access modifiers changed from: package-private */
    public void hideActionButton() {
        this.mSkipButton.setVisibility(0);
        this.mDoneButton.setVisibility(4);
        this.mDoneButton.setOnClickListener((View.OnClickListener) null);
    }

    /* access modifiers changed from: package-private */
    public void showActionButton() {
        this.mSkipButton.setVisibility(8);
        this.mDoneButton.setVisibility(0);
        this.mDoneButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TutorialController.this.onActionButtonClicked(view);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void hideFakeTaskbar(boolean z) {
        if (this.mTutorialFragment.isLargeScreen()) {
            Runnable runnable = this.mFakeTaskbarViewCallback;
            if (runnable != null) {
                this.mFakeTaskbarView.removeCallbacks(runnable);
            }
            if (z) {
                this.mFakeTaskbarViewCallback = new Runnable() {
                    public final void run() {
                        TutorialController.this.lambda$hideFakeTaskbar$5$TutorialController();
                    }
                };
            } else {
                AnimatedTaskbarView animatedTaskbarView = this.mFakeTaskbarView;
                Objects.requireNonNull(animatedTaskbarView);
                this.mFakeTaskbarViewCallback = new Runnable() {
                    public final void run() {
                        AnimatedTaskbarView.this.animateDisappearanceToBottom();
                    }
                };
            }
            this.mFakeTaskbarView.post(this.mFakeTaskbarViewCallback);
        }
    }

    public /* synthetic */ void lambda$hideFakeTaskbar$5$TutorialController() {
        this.mFakeTaskbarView.animateDisappearanceToHotseat(this.mFakeHotseatView);
    }

    /* access modifiers changed from: package-private */
    public void showFakeTaskbar(boolean z) {
        if (this.mTutorialFragment.isLargeScreen()) {
            Runnable runnable = this.mFakeTaskbarViewCallback;
            if (runnable != null) {
                this.mFakeTaskbarView.removeCallbacks(runnable);
            }
            if (z) {
                this.mFakeTaskbarViewCallback = new Runnable() {
                    public final void run() {
                        TutorialController.this.lambda$showFakeTaskbar$6$TutorialController();
                    }
                };
            } else {
                AnimatedTaskbarView animatedTaskbarView = this.mFakeTaskbarView;
                Objects.requireNonNull(animatedTaskbarView);
                this.mFakeTaskbarViewCallback = new Runnable() {
                    public final void run() {
                        AnimatedTaskbarView.this.animateAppearanceFromBottom();
                    }
                };
            }
            this.mFakeTaskbarView.post(this.mFakeTaskbarViewCallback);
        }
    }

    public /* synthetic */ void lambda$showFakeTaskbar$6$TutorialController() {
        this.mFakeTaskbarView.animateAppearanceFromHotseat(this.mFakeHotseatView);
    }

    /* access modifiers changed from: package-private */
    public void updateFakeAppTaskViewLayout(int i) {
        updateFakeViewLayout(this.mFakeTaskView, i);
    }

    /* access modifiers changed from: package-private */
    public void updateFakeViewLayout(ViewGroup viewGroup, int i) {
        viewGroup.removeAllViews();
        if (i != -1) {
            viewGroup.addView(View.inflate(this.mContext, i, (ViewGroup) null), new FrameLayout.LayoutParams(-1, -1));
        }
    }

    private void updateSubtext() {
        this.mTutorialStepView.setTutorialProgress(this.mTutorialFragment.getCurrentStep(), this.mTutorialFragment.getNumSteps());
    }

    private void updateDrawables() {
        if (this.mContext != null) {
            this.mTutorialFragment.getRootView().setBackground(AppCompatResources.getDrawable(this.mContext, getMockWallpaperResId()));
            this.mTutorialFragment.updateFeedbackAnimation();
            this.mFakeLauncherView.setBackgroundColor(this.mContext.getColor(R.color.gesture_tutorial_fake_wallpaper_color));
            updateFakeViewLayout(this.mFakeHotseatView, getMockHotseatResId());
            this.mHotseatIconView = this.mFakeHotseatView.findViewById(R.id.hotseat_icon_1);
            updateFakeViewLayout(this.mFakeTaskView, getMockAppTaskLayoutResId());
            this.mFakeTaskView.animate().alpha(1.0f).setListener(AnimatorListeners.forSuccessCallback(new Runnable() {
                public final void run() {
                    TutorialController.this.lambda$updateDrawables$7$TutorialController();
                }
            }));
            this.mFakePreviousTaskView.setFakeTaskViewFillColor(this.mContext.getResources().getColor(getMockPreviousAppTaskThumbnailColorResId()));
            this.mFakeIconView.setBackground(AppCompatResources.getDrawable(this.mContext, getMockAppIconResId()));
        }
    }

    public /* synthetic */ void lambda$updateDrawables$7$TutorialController() {
        this.mFakeTaskView.animate().cancel();
    }

    private void updateLayout() {
        if (this.mContext != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mFeedbackView.getLayoutParams();
            Resources resources = this.mContext.getResources();
            boolean isLargeScreen = this.mTutorialFragment.isLargeScreen();
            int i = R.dimen.gesture_tutorial_tablet_feedback_margin_start_end;
            layoutParams.setMarginStart(resources.getDimensionPixelSize(isLargeScreen ? R.dimen.gesture_tutorial_tablet_feedback_margin_start_end : R.dimen.gesture_tutorial_feedback_margin_start_end));
            Resources resources2 = this.mContext.getResources();
            if (!this.mTutorialFragment.isLargeScreen()) {
                i = R.dimen.gesture_tutorial_feedback_margin_start_end;
            }
            layoutParams.setMarginEnd(resources2.getDimensionPixelSize(i));
            layoutParams.topMargin = this.mContext.getResources().getDimensionPixelSize(this.mTutorialFragment.isLargeScreen() ? R.dimen.gesture_tutorial_tablet_feedback_margin_top : R.dimen.gesture_tutorial_feedback_margin_top);
            this.mFakeTaskbarView.setVisibility(this.mTutorialFragment.isLargeScreen() ? 0 : 8);
            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mFakeHotseatView.getLayoutParams();
            int i2 = 21;
            if (!this.mTutorialFragment.isLargeScreen()) {
                DeviceProfile deviceProfile = this.mTutorialFragment.getDeviceProfile();
                deviceProfile.updateIsSeascape(this.mContext);
                if (!deviceProfile.isLandscape) {
                    i2 = 12;
                } else if (deviceProfile.isSeascape()) {
                    i2 = 20;
                }
                layoutParams2.addRule(i2);
            } else {
                layoutParams2.width = -1;
                layoutParams2.height = -2;
                layoutParams2.addRule(12);
                layoutParams2.removeRule(20);
                layoutParams2.removeRule(21);
            }
            this.mFakeHotseatView.setLayoutParams(layoutParams2);
        }
    }

    private AlertDialog createSkipTutorialDialog() {
        Context context = this.mContext;
        AlertDialog alertDialog = null;
        if (context instanceof GestureSandboxActivity) {
            GestureSandboxActivity gestureSandboxActivity = (GestureSandboxActivity) context;
            View inflate = View.inflate(gestureSandboxActivity, R.layout.gesture_tutorial_dialog, (ViewGroup) null);
            alertDialog = new AlertDialog.Builder(gestureSandboxActivity, 2131821050).setView(inflate).create();
            PackageManager packageManager = this.mContext.getPackageManager();
            CharSequence charSequence = DEFAULT_PIXEL_TIPS_APP_NAME;
            try {
                charSequence = packageManager.getApplicationLabel(packageManager.getApplicationInfo(PIXEL_TIPS_APP_PACKAGE_NAME, 128));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(LOG_TAG, "Could not find app label for package name: com.google.android.apps.tips. Defaulting to 'Pixel Tips.'", e);
            }
            TextView textView = (TextView) inflate.findViewById(R.id.gesture_tutorial_dialog_subtitle);
            if (textView != null) {
                textView.setText(this.mContext.getString(R.string.skip_tutorial_dialog_subtitle, new Object[]{charSequence}));
            } else {
                Log.w(LOG_TAG, "No subtitle view in the skip tutorial dialog to update.");
            }
            Button button = (Button) inflate.findViewById(R.id.gesture_tutorial_dialog_cancel_button);
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        AlertDialog.this.dismiss();
                    }
                });
            } else {
                Log.w(LOG_TAG, "No cancel button in the skip tutorial dialog to update.");
            }
            Button button2 = (Button) inflate.findViewById(R.id.gesture_tutorial_dialog_confirm_button);
            if (button2 != null) {
                button2.setOnClickListener(new View.OnClickListener(alertDialog) {
                    public final /* synthetic */ AlertDialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        TutorialController.this.lambda$createSkipTutorialDialog$9$TutorialController(this.f$1, view);
                    }
                });
            } else {
                Log.w(LOG_TAG, "No confirm button in the skip tutorial dialog to update.");
            }
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(gestureSandboxActivity.getColor(17170445)));
        }
        return alertDialog;
    }

    public /* synthetic */ void lambda$createSkipTutorialDialog$9$TutorialController(AlertDialog alertDialog, View view) {
        this.mTutorialFragment.closeTutorial(true);
        alertDialog.dismiss();
    }

    /* access modifiers changed from: protected */
    public AnimatorSet createFingerDotAppearanceAnimatorSet() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mFingerDotView, View.ALPHA, new float[]{0.0f, 0.7f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mFingerDotView, View.SCALE_Y, new float[]{0.7f, 1.0f});
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.mFingerDotView, View.SCALE_X, new float[]{0.7f, 1.0f});
        ArrayList arrayList = new ArrayList();
        arrayList.add(ofFloat);
        arrayList.add(ofFloat3);
        arrayList.add(ofFloat2);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(500);
        return animatorSet;
    }

    /* access modifiers changed from: protected */
    public AnimatorSet createFingerDotDisappearanceAnimatorSet() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mFingerDotView, View.ALPHA, new float[]{0.7f, 0.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mFingerDotView, View.SCALE_Y, new float[]{1.0f, 0.7f});
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.mFingerDotView, View.SCALE_X, new float[]{1.0f, 0.7f});
        ArrayList arrayList = new ArrayList();
        arrayList.add(ofFloat);
        arrayList.add(ofFloat3);
        arrayList.add(ofFloat2);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(500);
        return animatorSet;
    }

    /* access modifiers changed from: protected */
    public Animator createAnimationPause() {
        return ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(GESTURE_ANIMATION_PAUSE_DURATION_MILLIS);
    }
}
