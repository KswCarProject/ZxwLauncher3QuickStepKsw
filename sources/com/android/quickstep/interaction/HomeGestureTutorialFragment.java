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

public class HomeGestureTutorialFragment extends TutorialFragment {
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
        return Integer.valueOf(R.drawable.gesture_tutorial_loop_home);
    }

    /* access modifiers changed from: protected */
    public Animator createGestureAnimation() {
        if (!(this.mTutorialController instanceof HomeGestureTutorialController)) {
            return null;
        }
        final float fullscreenHeight = ((float) this.mRootView.getFullscreenHeight()) / 2.0f;
        final HomeGestureTutorialController homeGestureTutorialController = (HomeGestureTutorialController) this.mTutorialController;
        AnimatorSet createFingerDotAppearanceAnimatorSet = homeGestureTutorialController.createFingerDotAppearanceAnimatorSet();
        createFingerDotAppearanceAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                HomeGestureTutorialFragment.this.mFingerDotView.setTranslationY(fullscreenHeight);
            }
        });
        Animator createAnimationPause = homeGestureTutorialController.createAnimationPause();
        createAnimationPause.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                homeGestureTutorialController.resetFakeTaskView(true);
            }
        });
        ArrayList arrayList = new ArrayList();
        arrayList.add(createFingerDotAppearanceAnimatorSet);
        arrayList.add(homeGestureTutorialController.createFingerDotHomeSwipeAnimator(fullscreenHeight));
        arrayList.add(homeGestureTutorialController.createFingerDotDisappearanceAnimatorSet());
        arrayList.add(createAnimationPause);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                homeGestureTutorialController.resetFakeTaskView(true);
            }
        });
        animatorSet.playSequentially(arrayList);
        return animatorSet;
    }

    /* access modifiers changed from: package-private */
    public TutorialController createController(TutorialController.TutorialType tutorialType) {
        return new HomeGestureTutorialController(this, tutorialType);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends TutorialController> getControllerClass() {
        return HomeGestureTutorialController.class;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        releaseFeedbackAnimation();
        return super.onTouch(view, motionEvent);
    }

    /* access modifiers changed from: package-private */
    public void logTutorialStepShown(StatsLogManager statsLogManager) {
        statsLogManager.logger().log(StatsLogManager.LauncherEvent.LAUNCHER_GESTURE_TUTORIAL_HOME_STEP_SHOWN);
    }

    /* access modifiers changed from: package-private */
    public void logTutorialStepCompleted(StatsLogManager statsLogManager) {
        statsLogManager.logger().log(StatsLogManager.LauncherEvent.LAUNCHER_GESTURE_TUTORIAL_HOME_STEP_COMPLETED);
    }
}
