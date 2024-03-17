package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.PointF;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import com.android.quickstep.interaction.TutorialController;
import java.util.ArrayList;

final class OverviewGestureTutorialController extends SwipeUpGestureTutorialController {
    public int getIntroductionSubtitle() {
        return R.string.overview_gesture_intro_subtitle;
    }

    public int getIntroductionTitle() {
        return R.string.overview_gesture_intro_title;
    }

    public int getSpokenIntroductionSubtitle() {
        return R.string.overview_gesture_spoken_intro_subtitle;
    }

    OverviewGestureTutorialController(OverviewGestureTutorialFragment overviewGestureTutorialFragment, TutorialController.TutorialType tutorialType) {
        super(overviewGestureTutorialFragment, tutorialType);
    }

    public int getSuccessFeedbackSubtitle() {
        return (this.mTutorialFragment.getNumSteps() <= 1 || !this.mTutorialFragment.isAtFinalStep()) ? R.string.overview_gesture_feedback_complete_without_follow_up : R.string.overview_gesture_feedback_complete_with_follow_up;
    }

    /* access modifiers changed from: protected */
    public int getMockAppTaskLayoutResId() {
        return this.mTutorialFragment.isLargeScreen() ? R.layout.gesture_tutorial_tablet_mock_conversation_list : R.layout.gesture_tutorial_mock_conversation_list;
    }

    public void onBackGestureAttempted(EdgeBackGestureHandler.BackGestureResult backGestureResult) {
        if (!isGestureCompleted()) {
            int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
            if (i == 1) {
                int i2 = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[backGestureResult.ordinal()];
                if (i2 == 1 || i2 == 2 || i2 == 3 || i2 == 4) {
                    showFeedback(R.string.overview_gesture_feedback_swipe_too_far_from_edge);
                }
            } else if (i == 2) {
                if (backGestureResult == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT || backGestureResult == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT) {
                    this.mTutorialFragment.closeTutorial();
                }
            }
        }
    }

    public void onNavBarGestureAttempted(NavBarGestureHandler.NavBarGestureResult navBarGestureResult, PointF pointF) {
        if (!isGestureCompleted()) {
            int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
            if (i == 1) {
                switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[navBarGestureResult.ordinal()]) {
                    case 1:
                        animateFakeTaskViewHome(pointF, new Runnable() {
                            public final void run() {
                                OverviewGestureTutorialController.this.lambda$onNavBarGestureAttempted$0$OverviewGestureTutorialController();
                            }
                        });
                        return;
                    case 2:
                    case 3:
                        showFeedback(R.string.overview_gesture_feedback_swipe_too_far_from_edge);
                        return;
                    case 4:
                        this.mTutorialFragment.releaseFeedbackAnimation();
                        animateTaskViewToOverview();
                        onMotionPaused(true);
                        showSuccessFeedback();
                        return;
                    case 5:
                    case 6:
                        fadeOutFakeTaskView(false, true, (Runnable) null);
                        showFeedback(R.string.overview_gesture_feedback_wrong_swipe_direction);
                        return;
                    default:
                        return;
                }
            } else if (i == 2 && navBarGestureResult == NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED) {
                this.mTutorialFragment.closeTutorial();
            }
        }
    }

    /* renamed from: com.android.quickstep.interaction.OverviewGestureTutorialController$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType;

        /* JADX WARNING: Can't wrap try/catch for region: R(24:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|13|14|15|16|(2:17|18)|19|21|22|(2:23|24)|25|27|28|29|30|31|32|(3:33|34|36)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(30:0|1|2|3|(2:5|6)|7|9|10|11|13|14|15|16|17|18|19|21|22|23|24|25|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0033 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x005a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x0075 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x007f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x0089 */
        static {
            /*
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult[] r0 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult = r0
                r1 = 1
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r2 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r3 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r4 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                r3 = 4
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.OVERVIEW_GESTURE_COMPLETED     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_NOT_STARTED_WRONG_SWIPE_DIRECTION     // Catch:{ NoSuchFieldError -> 0x003e }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r6 = 5
                r4[r5] = r6     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_CANCELLED     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r6 = 6
                r4[r5] = r6     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                com.android.quickstep.interaction.TutorialController$TutorialType[] r4 = com.android.quickstep.interaction.TutorialController.TutorialType.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType = r4
                com.android.quickstep.interaction.TutorialController$TutorialType r5 = com.android.quickstep.interaction.TutorialController.TutorialType.OVERVIEW_NAVIGATION     // Catch:{ NoSuchFieldError -> 0x005a }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x005a }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x005a }
            L_0x005a:
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x0064 }
                com.android.quickstep.interaction.TutorialController$TutorialType r5 = com.android.quickstep.interaction.TutorialController.TutorialType.OVERVIEW_NAVIGATION_COMPLETE     // Catch:{ NoSuchFieldError -> 0x0064 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0064 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0064 }
            L_0x0064:
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult[] r4 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult = r4
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r5 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT     // Catch:{ NoSuchFieldError -> 0x0075 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0075 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0075 }
            L_0x0075:
                int[] r1 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x007f }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r4 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT     // Catch:{ NoSuchFieldError -> 0x007f }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x007f }
                r1[r4] = r0     // Catch:{ NoSuchFieldError -> 0x007f }
            L_0x007f:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x0089 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r1 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_LEFT     // Catch:{ NoSuchFieldError -> 0x0089 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0089 }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0089 }
            L_0x0089:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x0093 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r1 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_RIGHT     // Catch:{ NoSuchFieldError -> 0x0093 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0093 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x0093 }
            L_0x0093:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.interaction.OverviewGestureTutorialController.AnonymousClass1.<clinit>():void");
        }
    }

    public /* synthetic */ void lambda$onNavBarGestureAttempted$0$OverviewGestureTutorialController() {
        showFeedback(R.string.overview_gesture_feedback_home_detected);
        resetFakeTaskView(true);
    }

    public void animateTaskViewToOverview() {
        AnimatorSet createAnimationToMultiRowLayout;
        PendingAnimation pendingAnimation = new PendingAnimation(300);
        pendingAnimation.setFloat(this.mTaskViewSwipeUpAnimation.getCurrentShift(), AnimatedFloat.VALUE, 1.0f, Interpolators.ACCEL);
        ArrayList arrayList = new ArrayList();
        if (this.mTutorialFragment.isLargeScreen() && (createAnimationToMultiRowLayout = this.mFakePreviousTaskView.createAnimationToMultiRowLayout()) != null) {
            createAnimationToMultiRowLayout.setDuration(300);
            arrayList.add(createAnimationToMultiRowLayout);
        }
        arrayList.add(pendingAnimation.buildAnim());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        hideFakeTaskbar(false);
        animatorSet.start();
        this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap((Animator) animatorSet);
    }
}
