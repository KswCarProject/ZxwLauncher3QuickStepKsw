package com.android.quickstep.interaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.logging.StatsLogManager;
import com.android.quickstep.interaction.TutorialController;

public class AssistantGestureTutorialFragment extends TutorialFragment {
    /* access modifiers changed from: package-private */
    public void logTutorialStepCompleted(StatsLogManager statsLogManager) {
    }

    /* access modifiers changed from: package-private */
    public void logTutorialStepShown(StatsLogManager statsLogManager) {
    }

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
    public TutorialController createController(TutorialController.TutorialType tutorialType) {
        return new AssistantGestureTutorialController(this, tutorialType);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends TutorialController> getControllerClass() {
        return AssistantGestureTutorialController.class;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && this.mTutorialController != null) {
            this.mTutorialController.setRippleHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouch(view, motionEvent);
    }
}
