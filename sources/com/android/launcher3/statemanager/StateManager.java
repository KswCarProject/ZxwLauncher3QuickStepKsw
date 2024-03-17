package com.android.launcher3.statemanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.states.StateAnimationConfig;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StateManager<STATE_TYPE extends BaseState<STATE_TYPE>> {
    public static final String TAG = "StateManager";
    private final StatefulActivity<STATE_TYPE> mActivity;
    /* access modifiers changed from: private */
    public final AtomicAnimationFactory mAtomicAnimationFactory;
    private final STATE_TYPE mBaseState;
    /* access modifiers changed from: private */
    public final AnimationState mConfig = new AnimationState();
    private STATE_TYPE mCurrentStableState;
    private STATE_TYPE mLastStableState;
    private final ArrayList<StateListener<STATE_TYPE>> mListeners = new ArrayList<>();
    private STATE_TYPE mRestState;
    private STATE_TYPE mState;
    private StateHandler<STATE_TYPE>[] mStateHandlers;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());

    public interface StateHandler<STATE_TYPE> {
        void setState(STATE_TYPE state_type);

        void setStateWithAnimation(STATE_TYPE state_type, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation);
    }

    public interface StateListener<STATE_TYPE> {
        void onStateTransitionComplete(STATE_TYPE state_type) {
        }

        void onStateTransitionStart(STATE_TYPE state_type) {
        }
    }

    public StateManager(StatefulActivity<STATE_TYPE> statefulActivity, STATE_TYPE state_type) {
        this.mActivity = statefulActivity;
        this.mBaseState = state_type;
        this.mCurrentStableState = state_type;
        this.mLastStableState = state_type;
        this.mState = state_type;
        this.mAtomicAnimationFactory = statefulActivity.createAtomicAnimationFactory();
    }

    public STATE_TYPE getState() {
        return this.mState;
    }

    public STATE_TYPE getCurrentStableState() {
        return this.mCurrentStableState;
    }

    public String toString() {
        return " StateManager(mLastStableState:" + this.mLastStableState + ", mCurrentStableState:" + this.mCurrentStableState + ", mState:" + this.mState + ", mRestState:" + this.mRestState + ", isInTransition:" + isInTransition() + ")";
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "StateManager:");
        printWriter.println(str + "\tmLastStableState:" + this.mLastStableState);
        printWriter.println(str + "\tmCurrentStableState:" + this.mCurrentStableState);
        printWriter.println(str + "\tmState:" + this.mState);
        printWriter.println(str + "\tmRestState:" + this.mRestState);
        printWriter.println(str + "\tisInTransition:" + isInTransition());
    }

    public StateHandler[] getStateHandlers() {
        if (this.mStateHandlers == null) {
            ArrayList arrayList = new ArrayList();
            this.mActivity.collectStateHandlers(arrayList);
            this.mStateHandlers = (StateHandler[]) arrayList.toArray(new StateHandler[arrayList.size()]);
        }
        return this.mStateHandlers;
    }

    public void addStateListener(StateListener stateListener) {
        this.mListeners.add(stateListener);
    }

    public void removeStateListener(StateListener stateListener) {
        this.mListeners.remove(stateListener);
    }

    public boolean shouldAnimateStateChange() {
        return !this.mActivity.isForceInvisible() && this.mActivity.isStarted();
    }

    public boolean isInStableState(STATE_TYPE state_type) {
        return this.mState == state_type && this.mCurrentStableState == state_type && (this.mConfig.targetState == null || this.mConfig.targetState == state_type);
    }

    public boolean isInTransition() {
        return this.mConfig.currentAnimation != null;
    }

    public void goToState(STATE_TYPE state_type) {
        goToState(state_type, shouldAnimateStateChange());
    }

    public void goToState(STATE_TYPE state_type, boolean z) {
        goToState(state_type, z, 0, (Animator.AnimatorListener) null);
    }

    public void goToState(STATE_TYPE state_type, boolean z, Animator.AnimatorListener animatorListener) {
        goToState(state_type, z, 0, animatorListener);
    }

    public void goToState(STATE_TYPE state_type, long j, Animator.AnimatorListener animatorListener) {
        goToState(state_type, true, j, animatorListener);
    }

    public void goToState(STATE_TYPE state_type, long j) {
        goToState(state_type, true, j, (Animator.AnimatorListener) null);
    }

    public void reapplyState() {
        reapplyState(false);
    }

    public void reapplyState(boolean z) {
        boolean z2 = this.mConfig.currentAnimation != null;
        if (z) {
            this.mAtomicAnimationFactory.cancelAllStateElementAnimation();
            cancelAnimation();
        }
        if (this.mConfig.currentAnimation == null) {
            for (StateHandler state : getStateHandlers()) {
                state.setState(this.mState);
            }
            if (z2) {
                onStateTransitionEnd(this.mState);
            }
        }
    }

    private void goToState(STATE_TYPE state_type, boolean z, long j, Animator.AnimatorListener animatorListener) {
        boolean areAnimatorsEnabled = z & ValueAnimator.areAnimatorsEnabled();
        if (this.mActivity.isInState(state_type)) {
            if (this.mConfig.currentAnimation == null) {
                if (animatorListener != null) {
                    animatorListener.onAnimationEnd((Animator) null);
                    return;
                }
                return;
            } else if (!this.mConfig.userControlled && areAnimatorsEnabled && this.mConfig.targetState == state_type) {
                if (animatorListener != null) {
                    this.mConfig.currentAnimation.addListener(animatorListener);
                    return;
                }
                return;
            }
        }
        STATE_TYPE state_type2 = this.mState;
        cancelAnimation();
        if (!areAnimatorsEnabled) {
            this.mAtomicAnimationFactory.cancelAllStateElementAnimation();
            onStateTransitionStart(state_type);
            for (StateHandler state : getStateHandlers()) {
                state.setState(state_type);
            }
            onStateTransitionEnd(state_type);
            if (animatorListener != null) {
                animatorListener.onAnimationEnd((Animator) null);
            }
        } else if (j > 0) {
            this.mUiHandler.postDelayed(new Runnable(this.mConfig.changeId, state_type, state_type2, animatorListener) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ BaseState f$2;
                public final /* synthetic */ BaseState f$3;
                public final /* synthetic */ Animator.AnimatorListener f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    StateManager.this.lambda$goToState$0$StateManager(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            }, j);
        } else {
            goToStateAnimated(state_type, state_type2, animatorListener);
        }
    }

    public /* synthetic */ void lambda$goToState$0$StateManager(int i, BaseState baseState, BaseState baseState2, Animator.AnimatorListener animatorListener) {
        if (this.mConfig.changeId == i) {
            goToStateAnimated(baseState, baseState2, animatorListener);
        }
    }

    private void goToStateAnimated(STATE_TYPE state_type, STATE_TYPE state_type2, Animator.AnimatorListener animatorListener) {
        int i;
        AnimationState animationState = this.mConfig;
        if (state_type == this.mBaseState) {
            i = state_type2.getTransitionDuration(this.mActivity, false);
        } else {
            i = state_type.getTransitionDuration(this.mActivity, true);
        }
        animationState.duration = (long) i;
        prepareForAtomicAnimation(state_type2, state_type, this.mConfig);
        AnimatorSet buildAnim = createAnimationToNewWorkspaceInternal(state_type).buildAnim();
        if (animatorListener != null) {
            buildAnim.addListener(animatorListener);
        }
        this.mUiHandler.post(new StartAnimRunnable(buildAnim));
    }

    public void prepareForAtomicAnimation(STATE_TYPE state_type, STATE_TYPE state_type2, StateAnimationConfig stateAnimationConfig) {
        this.mAtomicAnimationFactory.prepareForAtomicAnimation(state_type, state_type2, stateAnimationConfig);
    }

    public AnimatorSet createAtomicAnimation(STATE_TYPE state_type, STATE_TYPE state_type2, StateAnimationConfig stateAnimationConfig) {
        PendingAnimation pendingAnimation = new PendingAnimation(stateAnimationConfig.duration);
        prepareForAtomicAnimation(state_type, state_type2, stateAnimationConfig);
        for (StateHandler stateWithAnimation : this.mActivity.getStateManager().getStateHandlers()) {
            stateWithAnimation.setStateWithAnimation(state_type2, stateAnimationConfig, pendingAnimation);
        }
        return pendingAnimation.buildAnim();
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(STATE_TYPE state_type, long j) {
        return createAnimationToNewWorkspace(state_type, j, 0);
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(STATE_TYPE state_type, long j, int i) {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        stateAnimationConfig.duration = j;
        stateAnimationConfig.animFlags = i;
        return createAnimationToNewWorkspace(state_type, stateAnimationConfig);
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(STATE_TYPE state_type, StateAnimationConfig stateAnimationConfig) {
        stateAnimationConfig.userControlled = true;
        cancelAnimation();
        stateAnimationConfig.copyTo(this.mConfig);
        this.mConfig.playbackController = createAnimationToNewWorkspaceInternal(state_type).createPlaybackController();
        return this.mConfig.playbackController;
    }

    private PendingAnimation createAnimationToNewWorkspaceInternal(STATE_TYPE state_type) {
        PendingAnimation pendingAnimation = new PendingAnimation(this.mConfig.duration);
        if (!this.mConfig.hasAnimationFlag(1)) {
            for (StateHandler stateWithAnimation : getStateHandlers()) {
                stateWithAnimation.setStateWithAnimation(state_type, this.mConfig, pendingAnimation);
            }
        }
        pendingAnimation.addListener(createStateAnimationListener(state_type));
        this.mConfig.setAnimation(pendingAnimation.buildAnim(), state_type);
        return pendingAnimation;
    }

    private Animator.AnimatorListener createStateAnimationListener(final STATE_TYPE state_type) {
        return new AnimationSuccessListener() {
            public void onAnimationStart(Animator animator) {
                StateManager.this.onStateTransitionStart(state_type);
            }

            public void onAnimationSuccess(Animator animator) {
                StateManager.this.onStateTransitionEnd(state_type);
            }
        };
    }

    /* access modifiers changed from: private */
    public void onStateTransitionStart(STATE_TYPE state_type) {
        this.mState = state_type;
        this.mActivity.onStateSetStart(state_type);
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            this.mListeners.get(size).onStateTransitionStart(state_type);
        }
    }

    /* access modifiers changed from: private */
    public void onStateTransitionEnd(STATE_TYPE state_type) {
        STATE_TYPE state_type2 = this.mCurrentStableState;
        if (state_type != state_type2) {
            this.mLastStableState = state_type.getHistoryForState(state_type2);
            this.mCurrentStableState = state_type;
        }
        this.mActivity.onStateSetEnd(state_type);
        if (state_type == this.mBaseState) {
            setRestState((BaseState) null);
        }
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            this.mListeners.get(size).onStateTransitionComplete(state_type);
        }
    }

    public STATE_TYPE getLastState() {
        return this.mLastStableState;
    }

    public void moveToRestState() {
        if ((this.mConfig.currentAnimation == null || !this.mConfig.userControlled) && this.mState.shouldDisableRestore()) {
            goToState(getRestState());
            this.mLastStableState = this.mBaseState;
        }
    }

    public STATE_TYPE getRestState() {
        STATE_TYPE state_type = this.mRestState;
        return state_type == null ? this.mBaseState : state_type;
    }

    public void setRestState(STATE_TYPE state_type) {
        this.mRestState = state_type;
    }

    public void cancelAnimation() {
        this.mConfig.reset();
        while (true) {
            if (this.mConfig.currentAnimation != null || this.mConfig.playbackController != null) {
                this.mConfig.reset();
            } else {
                return;
            }
        }
    }

    public void setCurrentUserControlledAnimation(AnimatorPlaybackController animatorPlaybackController) {
        clearCurrentAnimation();
        setCurrentAnimation(animatorPlaybackController.getTarget(), new Animator[0]);
        this.mConfig.userControlled = true;
        this.mConfig.playbackController = animatorPlaybackController;
    }

    public void setCurrentAnimation(AnimatorSet animatorSet, STATE_TYPE state_type) {
        cancelAnimation();
        setCurrentAnimation(animatorSet, new Animator[0]);
        animatorSet.addListener(createStateAnimationListener(state_type));
    }

    public void setCurrentAnimation(AnimatorSet animatorSet, Animator... animatorArr) {
        int length = animatorArr.length;
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            AnimatorSet animatorSet2 = animatorArr[i];
            if (animatorSet2 != null) {
                if (this.mConfig.playbackController != null && this.mConfig.playbackController.getTarget() == animatorSet2) {
                    clearCurrentAnimation();
                    break;
                } else if (this.mConfig.currentAnimation == animatorSet2) {
                    clearCurrentAnimation();
                    break;
                }
            }
            i++;
        }
        if (this.mConfig.currentAnimation != null) {
            z = true;
        }
        cancelAnimation();
        if (z) {
            reapplyState();
            onStateTransitionEnd(this.mState);
        }
        this.mConfig.setAnimation(animatorSet, null);
    }

    public void cancelStateElementAnimation(int i) {
        if (this.mAtomicAnimationFactory.mStateElementAnimators[i] != null) {
            this.mAtomicAnimationFactory.mStateElementAnimators[i].cancel();
        }
    }

    public Animator createStateElementAnimation(final int i, float... fArr) {
        cancelStateElementAnimation(i);
        Animator createStateElementAnimation = this.mAtomicAnimationFactory.createStateElementAnimation(i, fArr);
        this.mAtomicAnimationFactory.mStateElementAnimators[i] = createStateElementAnimation;
        createStateElementAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                StateManager.this.mAtomicAnimationFactory.mStateElementAnimators[i] = null;
            }
        });
        return createStateElementAnimation;
    }

    private void clearCurrentAnimation() {
        if (this.mConfig.currentAnimation != null) {
            this.mConfig.currentAnimation.removeListener(this.mConfig);
            this.mConfig.currentAnimation = null;
        }
        this.mConfig.playbackController = null;
    }

    private class StartAnimRunnable implements Runnable {
        private final AnimatorSet mAnim;

        public StartAnimRunnable(AnimatorSet animatorSet) {
            this.mAnim = animatorSet;
        }

        public void run() {
            AnimatorSet animatorSet = StateManager.this.mConfig.currentAnimation;
            AnimatorSet animatorSet2 = this.mAnim;
            if (animatorSet == animatorSet2) {
                animatorSet2.start();
            }
        }
    }

    private static class AnimationState<STATE_TYPE> extends StateAnimationConfig implements Animator.AnimatorListener {
        private static final StateAnimationConfig DEFAULT = new StateAnimationConfig();
        public int changeId;
        public AnimatorSet currentAnimation;
        public AnimatorPlaybackController playbackController;
        public STATE_TYPE targetState;

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }

        private AnimationState() {
            this.changeId = 0;
        }

        public void reset() {
            AnimatorSet animatorSet = this.currentAnimation;
            AnimatorPlaybackController animatorPlaybackController = this.playbackController;
            DEFAULT.copyTo(this);
            this.targetState = null;
            this.currentAnimation = null;
            this.playbackController = null;
            this.changeId++;
            if (animatorPlaybackController != null) {
                animatorPlaybackController.getAnimationPlayer().cancel();
                animatorPlaybackController.dispatchOnCancel().dispatchOnEnd();
            } else if (animatorSet != null) {
                animatorSet.setDuration(0);
                if (!animatorSet.isStarted()) {
                    AnimatorPlaybackController.callListenerCommandRecursively(animatorSet, $$Lambda$ERUW54S9iwj8TJ01RKFFfhN9pl0.INSTANCE);
                    AnimatorPlaybackController.callListenerCommandRecursively(animatorSet, $$Lambda$G9BCccKCnF7M65F4KmuNOitA8.INSTANCE);
                }
                animatorSet.cancel();
            }
        }

        public void onAnimationEnd(Animator animator) {
            AnimatorPlaybackController animatorPlaybackController = this.playbackController;
            if (animatorPlaybackController != null && animatorPlaybackController.getTarget() == animator) {
                this.playbackController = null;
            }
            if (this.currentAnimation == animator) {
                this.currentAnimation = null;
            }
        }

        public void setAnimation(AnimatorSet animatorSet, STATE_TYPE state_type) {
            this.currentAnimation = animatorSet;
            this.targetState = state_type;
            animatorSet.addListener(this);
        }
    }

    public static class AtomicAnimationFactory<STATE_TYPE> {
        protected static final int NEXT_INDEX = 0;
        /* access modifiers changed from: private */
        public final Animator[] mStateElementAnimators;

        public void prepareForAtomicAnimation(STATE_TYPE state_type, STATE_TYPE state_type2, StateAnimationConfig stateAnimationConfig) {
        }

        public AtomicAnimationFactory(int i) {
            this.mStateElementAnimators = new Animator[i];
        }

        /* access modifiers changed from: package-private */
        public void cancelAllStateElementAnimation() {
            for (Animator animator : this.mStateElementAnimators) {
                if (animator != null) {
                    animator.cancel();
                }
            }
        }

        public Animator createStateElementAnimation(int i, float... fArr) {
            throw new RuntimeException("Unknown gesture animation " + i);
        }
    }
}
