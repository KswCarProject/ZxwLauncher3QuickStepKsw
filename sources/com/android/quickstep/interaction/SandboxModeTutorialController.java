package com.android.quickstep.interaction;

import android.graphics.PointF;
import com.android.launcher3.R;
import com.android.quickstep.interaction.EdgeBackGestureHandler;
import com.android.quickstep.interaction.NavBarGestureHandler;
import com.android.quickstep.interaction.TutorialController;

public class SandboxModeTutorialController extends SwipeUpGestureTutorialController {
    public /* bridge */ /* synthetic */ int getHotseatIconLeft() {
        return super.getHotseatIconLeft();
    }

    public /* bridge */ /* synthetic */ int getHotseatIconTop() {
        return super.getHotseatIconTop();
    }

    public /* bridge */ /* synthetic */ int getIntroductionSubtitle() {
        return super.getIntroductionSubtitle();
    }

    public /* bridge */ /* synthetic */ int getIntroductionTitle() {
        return super.getIntroductionTitle();
    }

    public /* bridge */ /* synthetic */ int getMockAppIconResId() {
        return super.getMockAppIconResId();
    }

    public /* bridge */ /* synthetic */ int getMockWallpaperResId() {
        return super.getMockWallpaperResId();
    }

    public /* bridge */ /* synthetic */ int getSpokenIntroductionSubtitle() {
        return super.getSpokenIntroductionSubtitle();
    }

    public /* bridge */ /* synthetic */ int getSuccessFeedbackSubtitle() {
        return super.getSuccessFeedbackSubtitle();
    }

    public /* bridge */ /* synthetic */ boolean isGestureCompleted() {
        return super.isGestureCompleted();
    }

    public /* bridge */ /* synthetic */ void onMotionPaused(boolean z) {
        super.onMotionPaused(z);
    }

    public /* bridge */ /* synthetic */ void setNavBarGestureProgress(Float f) {
        super.setNavBarGestureProgress(f);
    }

    SandboxModeTutorialController(SandboxModeTutorialFragment sandboxModeTutorialFragment, TutorialController.TutorialType tutorialType) {
        super(sandboxModeTutorialFragment, tutorialType);
    }

    public void onBackGestureAttempted(EdgeBackGestureHandler.BackGestureResult backGestureResult) {
        int i = AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult[backGestureResult.ordinal()];
        if (i == 1 || i == 2) {
            showRippleEffect((Runnable) null);
            showFeedback(R.string.sandbox_mode_back_gesture_feedback_successful);
        } else if (i == 3 || i == 4) {
            showFeedback(R.string.back_gesture_feedback_cancelled);
        } else if (i == 5) {
            showFeedback(R.string.sandbox_mode_back_gesture_feedback_swipe_too_far_from_edge);
        }
    }

    /* renamed from: com.android.quickstep.interaction.SandboxModeTutorialController$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult;
        static final /* synthetic */ int[] $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult;

        /* JADX WARNING: Can't wrap try/catch for region: R(26:0|(2:1|2)|3|(2:5|6)|7|(2:9|10)|11|(2:13|14)|15|17|18|19|20|(2:21|22)|23|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Can't wrap try/catch for region: R(27:0|(2:1|2)|3|(2:5|6)|7|9|10|11|(2:13|14)|15|17|18|19|20|(2:21|22)|23|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Can't wrap try/catch for region: R(29:0|1|2|3|(2:5|6)|7|9|10|11|13|14|15|17|18|19|20|(2:21|22)|23|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Can't wrap try/catch for region: R(30:0|1|2|3|(2:5|6)|7|9|10|11|13|14|15|17|18|19|20|21|22|23|25|26|27|28|29|30|31|32|33|34|36) */
        /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x0065 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x006f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x0079 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x0083 */
        static {
            /*
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult[] r0 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult = r0
                r1 = 1
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r2 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_COMPLETED     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r3 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_GESTURE_COMPLETED     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r4 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.OVERVIEW_GESTURE_COMPLETED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                r3 = 4
                int[] r4 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_NOT_STARTED_WRONG_SWIPE_DIRECTION     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                r4 = 5
                int[] r5 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r6 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_CANCELLED     // Catch:{ NoSuchFieldError -> 0x003e }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r5[r6] = r4     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r5 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r6 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r7 = 6
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r5 = $SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r6 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r7 = 7
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult[] r5 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.values()
                int r5 = r5.length
                int[] r5 = new int[r5]
                $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult = r5
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r6 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_LEFT     // Catch:{ NoSuchFieldError -> 0x0065 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0065 }
                r5[r6] = r1     // Catch:{ NoSuchFieldError -> 0x0065 }
            L_0x0065:
                int[] r1 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x006f }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r5 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_COMPLETED_FROM_RIGHT     // Catch:{ NoSuchFieldError -> 0x006f }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x006f }
                r1[r5] = r0     // Catch:{ NoSuchFieldError -> 0x006f }
            L_0x006f:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x0079 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r1 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_LEFT     // Catch:{ NoSuchFieldError -> 0x0079 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0079 }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0079 }
            L_0x0079:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x0083 }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r1 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_CANCELLED_FROM_RIGHT     // Catch:{ NoSuchFieldError -> 0x0083 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0083 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x0083 }
            L_0x0083:
                int[] r0 = $SwitchMap$com$android$quickstep$interaction$EdgeBackGestureHandler$BackGestureResult     // Catch:{ NoSuchFieldError -> 0x008d }
                com.android.quickstep.interaction.EdgeBackGestureHandler$BackGestureResult r1 = com.android.quickstep.interaction.EdgeBackGestureHandler.BackGestureResult.BACK_NOT_STARTED_TOO_FAR_FROM_EDGE     // Catch:{ NoSuchFieldError -> 0x008d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x008d }
                r0[r1] = r4     // Catch:{ NoSuchFieldError -> 0x008d }
            L_0x008d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.interaction.SandboxModeTutorialController.AnonymousClass1.<clinit>():void");
        }
    }

    public void onNavBarGestureAttempted(NavBarGestureHandler.NavBarGestureResult navBarGestureResult, PointF pointF) {
        switch (AnonymousClass1.$SwitchMap$com$android$quickstep$interaction$NavBarGestureHandler$NavBarGestureResult[navBarGestureResult.ordinal()]) {
            case 1:
                showRippleEffect((Runnable) null);
                showFeedback(R.string.sandbox_mode_assistant_gesture_feedback_successful);
                return;
            case 2:
                animateFakeTaskViewHome(pointF, new Runnable() {
                    public final void run() {
                        SandboxModeTutorialController.this.lambda$onNavBarGestureAttempted$0$SandboxModeTutorialController();
                    }
                });
                return;
            case 3:
                fadeOutFakeTaskView(true, true, new Runnable() {
                    public final void run() {
                        SandboxModeTutorialController.this.lambda$onNavBarGestureAttempted$1$SandboxModeTutorialController();
                    }
                });
                return;
            case 4:
            case 5:
            case 6:
            case 7:
                showFeedback(R.string.home_gesture_feedback_swipe_too_far_from_edge);
                return;
            default:
                return;
        }
    }

    public /* synthetic */ void lambda$onNavBarGestureAttempted$0$SandboxModeTutorialController() {
        showFeedback(R.string.sandbox_mode_home_gesture_feedback_successful);
    }

    public /* synthetic */ void lambda$onNavBarGestureAttempted$1$SandboxModeTutorialController() {
        showFeedback(R.string.sandbox_mode_overview_gesture_feedback_successful);
    }
}
