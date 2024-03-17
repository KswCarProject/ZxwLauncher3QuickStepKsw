package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.views.ActivityContext;

public class WorkPausedCard extends LinearLayout implements View.OnClickListener {
    private final ActivityContext mActivityContext;
    private Button mBtn;

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.generateLayoutParams(layoutParams);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public WorkPausedCard(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public WorkPausedCard(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WorkPausedCard(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mActivityContext = (ActivityContext) ActivityContext.lookupContext(getContext());
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        Button button = (Button) findViewById(R.id.enable_work_apps);
        this.mBtn = button;
        button.setOnClickListener(this);
    }

    public void onClick(View view) {
        if (Utilities.ATLEAST_P) {
            setEnabled(false);
            this.mActivityContext.getAppsView().getWorkManager().setWorkProfileEnabled(true);
            this.mActivityContext.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_TURN_ON_WORK_APPS_TAP);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = getResources().getConfiguration().orientation;
        getLayoutParams().height = i5 == 1 ? -1 : -2;
        super.onLayout(z, i, i2, i3, i4);
    }
}
