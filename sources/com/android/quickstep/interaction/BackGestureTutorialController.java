package com.android.quickstep.interaction;

import android.graphics.PointF;
import com.android.launcher3.R;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import com.android.quickstep.interaction.TutorialController;

final class BackGestureTutorialController extends TutorialController {
    public int getIntroductionSubtitle() {
        return R.string.back_gesture_intro_subtitle;
    }

    public int getIntroductionTitle() {
        return R.string.back_gesture_intro_title;
    }

    public int getSpokenIntroductionSubtitle() {
        return R.string.back_gesture_spoken_intro_subtitle;
    }

    BackGestureTutorialController(BackGestureTutorialFragment backGestureTutorialFragment, TutorialController.TutorialType tutorialType) {
        super(backGestureTutorialFragment, tutorialType);
    }

    public int getSuccessFeedbackSubtitle() {
        return this.mTutorialFragment.isAtFinalStep() ? R.string.back_gesture_feedback_complete_without_follow_up : R.string.back_gesture_feedback_complete_with_overview_follow_up;
    }

    /* access modifiers changed from: protected */
    public int getMockAppTaskLayoutResId() {
        return getMockAppTaskCurrentPageLayoutResId();
    }

    /* access modifiers changed from: package-private */
    public int getMockAppTaskCurrentPageLayoutResId() {
        return this.mTutorialFragment.isLargeScreen() ? R.layout.gesture_tutorial_tablet_mock_conversation : R.layout.gesture_tutorial_mock_conversation;
    }

    /* access modifiers changed from: package-private */
    public int getMockAppTaskPreviousPageLayoutResId() {
        return this.mTutorialFragment.isLargeScreen() ? R.layout.gesture_tutorial_tablet_mock_conversation_list : R.layout.gesture_tutorial_mock_conversation_list;
    }

    public void onBackGestureAttempted(EdgeBackGestureHandler.BackGestureResult backGestureResult) {
        if (!isGestureCompleted()) {
            int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
            if (i == 1) {
                handleBackAttempt(backGestureResult);
            } else if (i == 2) {
                if (backGestureResult == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT || backGestureResult == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT) {
                    this.mTutorialFragment.closeTutorial();
                }
            }
        }
    }

    private void handleBackAttempt(EdgeBackGestureHandler.BackGestureResult backGestureResult) {
        switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[backGestureResult.ordinal()]) {
            case 1:
            case 2:
                this.mTutorialFragment.releaseFeedbackAnimation();
                updateFakeAppTaskViewLayout(getMockAppTaskPreviousPageLayoutResId());
                showSuccessFeedback();
                return;
            case 3:
            case 4:
                showFeedback(R.string.back_gesture_feedback_cancelled);
                return;
            case 5:
                showFeedback(R.string.back_gesture_feedback_swipe_too_far_from_edge);
                return;
            case 6:
                showFeedback(R.string.back_gesture_feedback_swipe_in_nav_bar);
                return;
            default:
                return;
        }
    }

    public void onNavBarGestureAttempted(NavBarGestureHandler.NavBarGestureResult navBarGestureResult, PointF pointF) {
        if (!isGestureCompleted()) {
            if (this.mTutorialType == TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE) {
                if (navBarGestureResult == NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED) {
                    this.mTutorialFragment.closeTutorial();
                }
            } else if (this.mTutorialType == TutorialController.TutorialType.BACK_NAVIGATION) {
                int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[navBarGestureResult.ordinal()];
                if (i == 1 || i == 2 || i == 3) {
                    showFeedback(R.string.back_gesture_feedback_swipe_too_far_from_edge);
                } else {
                    showFeedback(R.string.back_gesture_feedback_swipe_in_nav_bar);
                }
            }
        }
    }

    /* renamed from: com.android.quickstep.interaction.BackGestureTutorialController$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType;

        /* JADX WARNING: Can't wrap try/catch for region: R(39:0|(2:1|2)|3|(2:5|6)|7|9|10|11|(2:13|14)|15|(2:17|18)|19|21|22|23|24|25|26|27|28|29|31|32|33|34|35|36|37|38|39|40|41|42|43|45|46|47|48|50) */
        /* JADX WARNING: Can't wrap try/catch for region: R(40:0|(2:1|2)|3|(2:5|6)|7|9|10|11|(2:13|14)|15|17|18|19|21|22|23|24|25|26|27|28|29|31|32|33|34|35|36|37|38|39|40|41|42|43|45|46|47|48|50) */
        /* JADX WARNING: Can't wrap try/catch for region: R(41:0|(2:1|2)|3|5|6|7|9|10|11|(2:13|14)|15|17|18|19|21|22|23|24|25|26|27|28|29|31|32|33|34|35|36|37|38|39|40|41|42|43|45|46|47|48|50) */
        /* JADX WARNING: Can't wrap try/catch for region: R(42:0|1|2|3|5|6|7|9|10|11|(2:13|14)|15|17|18|19|21|22|23|24|25|26|27|28|29|31|32|33|34|35|36|37|38|39|40|41|42|43|45|46|47|48|50) */
        /* JADX WARNING: Can't wrap try/catch for region: R(43:0|1|2|3|5|6|7|9|10|11|13|14|15|17|18|19|21|22|23|24|25|26|27|28|29|31|32|33|34|35|36|37|38|39|40|41|42|43|45|46|47|48|50) */
        /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x007d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x0087 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:37:0x0091 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x009b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x00a5 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:47:0x00c0 */
        static {
            /*
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult[] r0 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult = r0
                r1 = 1
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r2 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r3 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r4 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_CANCELLED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                r3 = 4
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                r4 = 5
                int[] r5 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r6 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.OVERVIEW_GESTURE_COMPLETED     // Catch:{ NoSuchFieldError -> 0x003e }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r5[r6] = r4     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                r5 = 6
                int[] r6 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r7 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_NOT_STARTED_WRONG_SWIPE_DIRECTION     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r6[r7] = r5     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r6 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r7 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_COMPLETED     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r8 = 7
                r6[r7] = r8     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r6 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r7 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_BAD_ANGLE     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r8 = 8
                r6[r7] = r8     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r6 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r7 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_SWIPE_TOO_SHORT     // Catch:{ NoSuchFieldError -> 0x006c }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r8 = 9
                r6[r7] = r8     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult[] r6 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.values()
                int r6 = r6.length
                int[] r6 = new int[r6]
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult = r6
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r7 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT     // Catch:{ NoSuchFieldError -> 0x007d }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x007d }
                r6[r7] = r1     // Catch:{ NoSuchFieldError -> 0x007d }
            L_0x007d:
                int[] r6 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x0087 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r7 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT     // Catch:{ NoSuchFieldError -> 0x0087 }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x0087 }
                r6[r7] = r0     // Catch:{ NoSuchFieldError -> 0x0087 }
            L_0x0087:
                int[] r6 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x0091 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r7 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_LEFT     // Catch:{ NoSuchFieldError -> 0x0091 }
                int r7 = r7.ordinal()     // Catch:{ NoSuchFieldError -> 0x0091 }
                r6[r7] = r2     // Catch:{ NoSuchFieldError -> 0x0091 }
            L_0x0091:
                int[] r2 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x009b }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r6 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_RIGHT     // Catch:{ NoSuchFieldError -> 0x009b }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x009b }
                r2[r6] = r3     // Catch:{ NoSuchFieldError -> 0x009b }
            L_0x009b:
                int[] r2 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x00a5 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r3 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x00a5 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x00a5 }
                r2[r3] = r4     // Catch:{ NoSuchFieldError -> 0x00a5 }
            L_0x00a5:
                int[] r2 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x00af }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r3 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_NOT_STARTED_IN_NAV_BAR_REGION     // Catch:{ NoSuchFieldError -> 0x00af }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x00af }
                r2[r3] = r5     // Catch:{ NoSuchFieldError -> 0x00af }
            L_0x00af:
                com.android.quickstep.interaction.TutorialController$TutorialType[] r2 = com.android.quickstep.interaction.TutorialController.TutorialType.values()
                int r2 = r2.length
                int[] r2 = new int[r2]
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType = r2
                com.android.quickstep.interaction.TutorialController$TutorialType r3 = com.android.quickstep.interaction.TutorialController.TutorialType.BACK_NAVIGATION     // Catch:{ NoSuchFieldError -> 0x00c0 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x00c0 }
                r2[r3] = r1     // Catch:{ NoSuchFieldError -> 0x00c0 }
            L_0x00c0:
                int[] r1 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x00ca }
                com.android.quickstep.interaction.TutorialController$TutorialType r2 = com.android.quickstep.interaction.TutorialController.TutorialType.BACK_NAVIGATION_COMPLETE     // Catch:{ NoSuchFieldError -> 0x00ca }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x00ca }
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x00ca }
            L_0x00ca:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.interaction.BackGestureTutorialController.AnonymousClass1.<clinit>():void");
        }
    }
}
