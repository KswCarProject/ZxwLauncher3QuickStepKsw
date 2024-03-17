package com.android.quickstep.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Log;
import com.android.launcher3.anim.SpringAnimationBuilder;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.views.RecentsView;
import java.util.Arrays;

public class RecentsAtomicAnimationFactory<ACTIVITY_TYPE extends StatefulActivity, STATE_TYPE> extends StateManager.AtomicAnimationFactory<STATE_TYPE> {
    public static final int INDEX_RECENTS_FADE_ANIM = 0;
    public static final int INDEX_RECENTS_TRANSLATE_X_ANIM = 1;
    private static final int MY_ANIM_COUNT = 2;
    protected final ACTIVITY_TYPE mActivity;

    public RecentsAtomicAnimationFactory(ACTIVITY_TYPE activity_type) {
        super(2);
        this.mActivity = activity_type;
    }

    public Animator createStateElementAnimation(int i, float... fArr) {
        if (i == 0) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat((RecentsView) this.mActivity.getOverviewPanel(), RecentsView.CONTENT_ALPHA, fArr);
            Log.d(TestProtocol.BAD_STATE, "RAAF createStateElementAnimation alpha=" + Arrays.toString(fArr));
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    Log.d(TestProtocol.BAD_STATE, "RAAF createStateElementAnimation onStart");
                }

                public void onAnimationCancel(Animator animator) {
                    float f;
                    RecentsView recentsView = (RecentsView) RecentsAtomicAnimationFactory.this.mActivity.getOverviewPanel();
                    if (recentsView == null) {
                        f = -1.0f;
                    } else {
                        f = ((Float) RecentsView.CONTENT_ALPHA.get(recentsView)).floatValue();
                    }
                    Log.d(TestProtocol.BAD_STATE, "RAAF createStateElementAnimation onCancel, alpha=" + f);
                }

                public void onAnimationEnd(Animator animator) {
                    Log.d(TestProtocol.BAD_STATE, "RAAF createStateElementAnimation onEnd");
                }
            });
            return ofFloat;
        } else if (i != 1) {
            return super.createStateElementAnimation(i, fArr);
        } else {
            return new SpringAnimationBuilder(this.mActivity).setMinimumVisibleChange(0.002f).setDampingRatio(0.8f).setStiffness(250.0f).setValues(fArr).build((RecentsView) this.mActivity.getOverviewPanel(), RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET);
        }
    }
}
