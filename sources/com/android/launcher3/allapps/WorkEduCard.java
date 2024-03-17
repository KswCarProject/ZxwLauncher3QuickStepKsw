package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.BaseAllAppsContainerView;
import com.android.launcher3.views.ActivityContext;

public class WorkEduCard extends FrameLayout implements View.OnClickListener, Animation.AnimationListener {
    private final ActivityContext mActivityContext;
    Animation mDismissAnim;
    private int mPosition;

    public void onAnimationRepeat(Animation animation) {
    }

    public void onAnimationStart(Animation animation) {
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public WorkEduCard(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public WorkEduCard(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WorkEduCard(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPosition = -1;
        this.mActivityContext = (ActivityContext) ActivityContext.lookupContext(getContext());
        Animation loadAnimation = AnimationUtils.loadAnimation(context, 17432577);
        this.mDismissAnim = loadAnimation;
        loadAnimation.setDuration(500);
        this.mDismissAnim.setAnimationListener(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mDismissAnim.reset();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mDismissAnim.cancel();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.action_btn).setOnClickListener(this);
        ((ViewGroup.MarginLayoutParams) findViewById(R.id.wrapper).getLayoutParams()).width = this.mActivityContext.getAppsView().getFloatingHeaderView().getTabWidth();
    }

    public void onClick(View view) {
        startAnimation(this.mDismissAnim);
        Utilities.getPrefs(getContext()).edit().putInt(WorkAdapterProvider.KEY_WORK_EDU_STEP, 1).apply();
    }

    public void onAnimationEnd(Animation animation) {
        removeCard();
    }

    private void removeCard() {
        if (this.mPosition != -1) {
            AllAppsRecyclerView allAppsRecyclerView = ((BaseAllAppsContainerView.AdapterHolder) this.mActivityContext.getAppsView().mAH.get(1)).mRecyclerView;
            allAppsRecyclerView.getApps().getAdapterItems().remove(this.mPosition);
            allAppsRecyclerView.getAdapter().notifyItemRemoved(this.mPosition);
        } else if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
    }

    public void setPosition(int i) {
        this.mPosition = i;
    }
}
