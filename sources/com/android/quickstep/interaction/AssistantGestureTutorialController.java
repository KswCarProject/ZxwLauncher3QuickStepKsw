package com.android.quickstep.interaction;

import android.graphics.PointF;
import com.android.launcher3.R;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import com.android.quickstep.interaction.TutorialController;

final class AssistantGestureTutorialController extends TutorialController {
    public void setAssistantProgress(float f) {
    }

    AssistantGestureTutorialController(AssistantGestureTutorialFragment assistantGestureTutorialFragment, TutorialController.TutorialType tutorialType) {
        super(assistantGestureTutorialFragment, tutorialType);
    }

    public void onBackGestureAttempted(EdgeBackGestureHandler.BackGestureResult backGestureResult) {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            int i2 = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[backGestureResult.ordinal()];
            if (i2 == 1 || i2 == 2 || i2 == 3 || i2 == 4) {
                showFeedback(R.string.assistant_gesture_feedback_swipe_too_far_from_corner);
            }
        } else if (i == 2) {
            if (backGestureResult == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT || backGestureResult == EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT) {
                this.mTutorialFragment.closeTutorial();
            }
        }
    }

    public void onNavBarGestureAttempted(NavBarGestureHandler.NavBarGestureResult navBarGestureResult, PointF pointF) {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType[this.mTutorialType.ordinal()];
        if (i == 1) {
            switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[navBarGestureResult.ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    showFeedback(R.string.assistant_gesture_feedback_swipe_too_far_from_corner);
                    return;
                case 7:
                    showRippleEffect((Runnable) null);
                    showFeedback(R.string.assistant_gesture_tutorial_playground_subtitle);
                    return;
                case 8:
                    showFeedback(R.string.assistant_gesture_feedback_swipe_not_diagonal);
                    return;
                case 9:
                    showFeedback(R.string.assistant_gesture_feedback_swipe_not_long_enough);
                    return;
                default:
                    return;
            }
        } else if (i == 2 && navBarGestureResult == NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED) {
            this.mTutorialFragment.closeTutorial();
        }
    }

    /* renamed from: com.android.quickstep.interaction.AssistantGestureTutorialController$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType;

        /* JADX WARNING: Can't wrap try/catch for region: R(32:0|(2:1|2)|3|(2:5|6)|7|9|10|11|13|14|15|16|17|18|19|20|21|22|23|24|25|27|28|(2:29|30)|31|33|34|35|36|37|38|(3:39|40|42)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(36:0|1|2|3|(2:5|6)|7|9|10|11|13|14|15|16|17|18|19|20|21|22|23|24|25|27|28|29|30|31|33|34|35|36|37|38|39|40|42) */
        /* JADX WARNING: Can't wrap try/catch for region: R(37:0|1|2|3|5|6|7|9|10|11|13|14|15|16|17|18|19|20|21|22|23|24|25|27|28|29|30|31|33|34|35|36|37|38|39|40|42) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0033 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x007d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:35:0x0098 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:37:0x00a2 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x00ac */
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
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r3 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.OVERVIEW_GESTURE_COMPLETED     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r4 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                r3 = 4
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x0033 }
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
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_COMPLETED     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r6 = 7
                r4[r5] = r6     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_BAD_ANGLE     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r6 = 8
                r4[r5] = r6     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_SWIPE_TOO_SHORT     // Catch:{ NoSuchFieldError -> 0x006c }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r6 = 9
                r4[r5] = r6     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                com.android.quickstep.interaction.TutorialController$TutorialType[] r4 = com.android.quickstep.interaction.TutorialController.TutorialType.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType = r4
                com.android.quickstep.interaction.TutorialController$TutorialType r5 = com.android.quickstep.interaction.TutorialController.TutorialType.ASSISTANT     // Catch:{ NoSuchFieldError -> 0x007d }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x007d }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x007d }
            L_0x007d:
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$TutorialController$TutorialType     // Catch:{ NoSuchFieldError -> 0x0087 }
                com.android.quickstep.interaction.TutorialController$TutorialType r5 = com.android.quickstep.interaction.TutorialController.TutorialType.ASSISTANT_COMPLETE     // Catch:{ NoSuchFieldError -> 0x0087 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0087 }
                r4[r5] = r0     // Catch:{ NoSuchFieldError -> 0x0087 }
            L_0x0087:
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult[] r4 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.values()
                int r4 = r4.length
                int[] r4 = new int[r4]
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult = r4
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r5 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT     // Catch:{ NoSuchFieldError -> 0x0098 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0098 }
                r4[r5] = r1     // Catch:{ NoSuchFieldError -> 0x0098 }
            L_0x0098:
                int[] r1 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x00a2 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r4 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT     // Catch:{ NoSuchFieldError -> 0x00a2 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x00a2 }
                r1[r4] = r0     // Catch:{ NoSuchFieldError -> 0x00a2 }
            L_0x00a2:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x00ac }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r1 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_LEFT     // Catch:{ NoSuchFieldError -> 0x00ac }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00ac }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00ac }
            L_0x00ac:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x00b6 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r1 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_RIGHT     // Catch:{ NoSuchFieldError -> 0x00b6 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00b6 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x00b6 }
            L_0x00b6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.interaction.AssistantGestureTutorialController.AnonymousClass1.<clinit>():void");
        }
    }
}
