package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.workprofile.PersonalWorkPagedView;

public class AllAppsPagedView extends PersonalWorkPagedView {
    public AllAppsPagedView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AllAppsPagedView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AllAppsPagedView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public boolean snapToPageWithVelocity(int i, int i2) {
        StatsLogManager.LauncherEvent launcherEvent;
        boolean snapToPageWithVelocity = super.snapToPageWithVelocity(i, i2);
        if (snapToPageWithVelocity && i != this.mCurrentPage) {
            StatsLogManager.StatsLogger logger = ((ActivityContext) ActivityContext.lookupContext(getContext())).getStatsLogManager().logger();
            if (this.mCurrentPage < i) {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_SWIPE_TO_WORK_TAB;
            } else {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_SWIPE_TO_PERSONAL_TAB;
            }
            logger.log(launcherEvent);
        }
        return snapToPageWithVelocity;
    }
}
