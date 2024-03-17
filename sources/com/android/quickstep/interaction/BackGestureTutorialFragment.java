package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.R;
import com.android.launcher3.logging.StatsLogManager;
import com.android.quickstep.interaction.TutorialController;
import java.util.ArrayList;

public class BackGestureTutorialFragment extends TutorialFragment {
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
        return Integer.valueOf(R.drawable.gesture_tutorial_loop_back);
    }

    /* access modifiers changed from: protected */
    public Animator createGestureAnimation() {
        if (!(this.mTutorialController instanceof BackGestureTutorialController)) {
            return null;
        }
        final BackGestureTutorialController backGestureTutorialController = (BackGestureTutorialController) this.mTutorialController;
        final float f = (float) (-(this.mRootView.getWidth() / 2));
        AnimatorSet createFingerDotAppearanceAnimatorSet = backGestureTutorialController.createFingerDotAppearanceAnimatorSet();
        createFingerDotAppearanceAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                BackGestureTutorialFragment.this.mFingerDotView.setTranslationX(f);
            }
        });
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mFingerDotView, View.TRANSLATION_X, new float[]{f, 0.0f});
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                BackGestureTutorialController backGestureTutorialController = backGestureTutorialController;
                backGestureTutorialController.updateFakeAppTaskViewLayout(backGestureTutorialController.getMockAppTaskPreviousPageLayoutResId());
            }
        });
        ofFloat.setDuration(1000);
        Animator createAnimationPause = backGestureTutorialController.createAnimationPause();
        createAnimationPause.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                BackGestureTutorialController backGestureTutorialController = backGestureTutorialController;
                backGestureTutorialController.updateFakeAppTaskViewLayout(backGestureTutorialController.getMockAppTaskCurrentPageLayoutResId());
            }
        });
        ArrayList arrayList = new ArrayList();
        arrayList.add(createFingerDotAppearanceAnimatorSet);
        arrayList.add(ofFloat);
        arrayList.add(backGestureTutorialController.createFingerDotDisappearanceAnimatorSet());
        arrayList.add(createAnimationPause);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                BackGestureTutorialController backGestureTutorialController = backGestureTutorialController;
                backGestureTutorialController.updateFakeAppTaskViewLayout(backGestureTutorialController.getMockAppTaskCurrentPageLayoutResId());
            }
        });
        animatorSet.playSequentially(arrayList);
        return animatorSet;
    }

    /* access modifiers changed from: package-private */
    public TutorialController createController(TutorialController.TutorialType tutorialType) {
        return new BackGestureTutorialController(this, tutorialType);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends TutorialController> getControllerClass() {
        return BackGestureTutorialController.class;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        releaseFeedbackAnimation();
        if (motionEvent.getAction() == 0 && this.mTutorialController != null) {
            this.mTutorialController.setRippleHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouch(view, motionEvent);
    }

    /* access modifiers changed from: package-private */
    public void logTutorialStepShown(StatsLogManager statsLogManager) {
        statsLogManager.logger().log(StatsLogManager.LauncherEvent.LAUNCHER_GESTURE_TUTORIAL_BACK_STEP_SHOWN);
    }

    /* access modifiers changed from: package-private */
    public void logTutorialStepCompleted(StatsLogManager statsLogManager) {
        statsLogManager.logger().log(StatsLogManager.LauncherEvent.LAUNCHER_GESTURE_TUTORIAL_BACK_STEP_COMPLETED);
    }
}
