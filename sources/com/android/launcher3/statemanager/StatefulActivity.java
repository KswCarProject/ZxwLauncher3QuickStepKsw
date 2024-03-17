package com.android.launcher3.statemanager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.LauncherRootView;
import com.android.launcher3.Utilities;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.views.BaseDragLayer;
import java.util.List;

public abstract class StatefulActivity<STATE_TYPE extends BaseState<STATE_TYPE>> extends BaseDraggingActivity {
    private boolean mDeferredResumePending;
    private final Runnable mHandleDeferredResume = new Runnable() {
        public final void run() {
            StatefulActivity.this.handleDeferredResume();
        }
    };
    public final Handler mHandler = new Handler();
    private LauncherRootView mRootView;

    /* access modifiers changed from: protected */
    public abstract void collectStateHandlers(List<StateManager.StateHandler> list);

    public abstract StateManager<STATE_TYPE> getStateManager();

    /* access modifiers changed from: protected */
    public void onDeferredResumed() {
    }

    public void onStateSetEnd(STATE_TYPE state_type) {
    }

    public void onUiChangedWhileSleeping() {
    }

    public boolean isInState(STATE_TYPE state_type) {
        return getStateManager().getState() == state_type;
    }

    /* access modifiers changed from: protected */
    public void inflateRootView(int i) {
        LauncherRootView launcherRootView = (LauncherRootView) LayoutInflater.from(this).inflate(i, (ViewGroup) null);
        this.mRootView = launcherRootView;
        launcherRootView.setSystemUiVisibility(1792);
    }

    public final LauncherRootView getRootView() {
        return this.mRootView;
    }

    public <T extends View> T findViewById(int i) {
        return this.mRootView.findViewById(i);
    }

    public void onStateSetStart(STATE_TYPE state_type) {
        if (this.mDeferredResumePending) {
            handleDeferredResume();
        }
    }

    public StateManager.AtomicAnimationFactory<STATE_TYPE> createAtomicAnimationFactory() {
        return new StateManager.AtomicAnimationFactory<>(0);
    }

    public void reapplyUi() {
        reapplyUi(true);
    }

    public void reapplyUi(boolean z) {
        getRootView().dispatchInsets();
        getStateManager().reapplyState(z);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        BaseDragLayer dragLayer = getDragLayer();
        boolean isUserActive = isUserActive();
        BaseState state = getStateManager().getState();
        int childCount = dragLayer.getChildCount();
        super.onStop();
        if (!isChangingConfigurations()) {
            getStateManager().moveToRestState();
        }
        onTrimMemory(20);
        if (isUserActive) {
            dragLayer.post(new Runnable(state, dragLayer, childCount) {
                public final /* synthetic */ BaseState f$1;
                public final /* synthetic */ BaseDragLayer f$2;
                public final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    StatefulActivity.this.lambda$onStop$0$StatefulActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onStop$0$StatefulActivity(BaseState baseState, BaseDragLayer baseDragLayer, int i) {
        if (!getStateManager().isInStableState(baseState) || baseDragLayer.getAlpha() < 1.0f || baseDragLayer.getChildCount() != i) {
            onUiChangedWhileSleeping();
        }
    }

    /* access modifiers changed from: private */
    public void handleDeferredResume() {
        if (!hasBeenResumed() || getStateManager().getState().hasFlag(1)) {
            this.mDeferredResumePending = true;
            return;
        }
        addActivityFlags(4);
        onDeferredResumed();
        this.mDeferredResumePending = false;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mHandler.removeCallbacks(this.mHandleDeferredResume);
        Utilities.postAsyncCallback(this.mHandler, this.mHandleDeferredResume);
    }

    public void runOnBindToTouchInteractionService(Runnable runnable) {
        runnable.run();
    }
}
