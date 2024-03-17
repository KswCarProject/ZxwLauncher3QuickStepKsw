package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.R;
import com.android.launcher3.logging.StatsLogManager;
import com.android.quickstep.interaction.TutorialController;
import java.util.ArrayList;

public class OverviewGestureTutorialFragment extends TutorialFragment {
    public /* bridge */ /* synthetic */ boolean isFoldable() {
        return super.isFoldable();
    }

    public /* bridge */ /* synthetic */ boolean isLargeScreen() {
        return super.isLargeScreen();
    }

    public /* bridge */ /* synthetic */ void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public /* bridge */ /* synthetic */ View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public /* bridge */ /* synthetic */ void onDestroy() {
        super.onDestroy();
    }

    public /* bridge */ /* synthetic */ void onResume() {
        super.onResume();
    }

    public /* bridge */ /* synthetic */ void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    public /* bridge */ /* synthetic */ void onStop() {
        super.onStop();
    }

    /* access modifiers changed from: package-private */
    public Integer getEdgeAnimationResId() {
        return Integer.valueOf(R.drawable.gesture_tutorial_loop_overview);
    }

    /* access modifiers changed from: protected */
    public Animator createGestureAnimation() {
        if (!(this.mTutorialController instanceof OverviewGestureTutorialController)) {
            return null;
        }
        final float fullscreenHeight = ((float) this.mRootView.getFullscreenHeight()) / 2.0f;
        final OverviewGestureTutorialController overviewGestureTutorialController = (OverviewGestureTutorialController) this.mTutorialController;
        AnimatorSet createFingerDotAppearanceAnimatorSet = overviewGestureTutorialController.createFingerDotAppearanceAnimatorSet();
        createFingerDotAppearanceAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                OverviewGestureTutorialFragment.this.mFingerDotView.setTranslationY(fullscreenHeight);
            }
        });
        AnimatorSet createFingerDotDisappearanceAnimatorSet = overviewGestureTutorialController.createFingerDotDisappearanceAnimatorSet();
        createFingerDotDisappearanceAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                overviewGestureTutorialController.animateTaskViewToOverview();
            }
        });
        Animator createAnimationPause = overviewGestureTutorialController.createAnimationPause();
        createAnimationPause.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                overviewGestureTutorialController.resetFakeTaskView(false);
            }
        });
        ArrayList arrayList = new ArrayList();
        arrayList.add(createFingerDotAppearanceAnimatorSet);
        arrayList.add(overviewGestureTutorialController.createFingerDotOverviewSwipeAnimator(fullscreenHeight));
        arrayList.add(overviewGestureTutorialController.createAnimationPause());
        arrayList.add(createFingerDotDisappearanceAnimatorSet);
        arrayList.add(createAnimationPause);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                overviewGestureTutorialController.resetFakeTaskView(false);
            }
        });
        animatorSet.playSequentially(arrayList);
        return animatorSet;
    }

    /* access modifiers changed from: package-private */
    public TutorialController createController(TutorialController.TutorialType tutorialType) {
        return new OverviewGestureTutorialController(this, tutorialType);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends TutorialController> getControllerClass() {
        return OverviewGestureTutorialController.class;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        releaseFeedbackAnimation();
        return super.onTouch(view, motionEvent);
    }

    /* access modifiers changed from: package-private */
    public void logTutorialStepShown(StatsLogManager statsLogManager) {
        statsLogManager.logger().log(StatsLogManager.LauncherEvent.LAUNCHER_GESTURE_TUTORIAL_OVERVIEW_STEP_SHOWN);
    }

    /* access modifiers changed from: package-private */
    public void logTutorialStepCompleted(StatsLogManager statsLogManager) {
        statsLogManager.logger().log(StatsLogManager.LauncherEvent.LAUNCHER_GESTURE_TUTORIAL_OVERVIEW_STEP_COMPLETED);
    }
}
