package com.android.launcher3.uioverrides.states;

import com.android.launcher3.Launcher;
import com.android.quickstep.views.RecentsView;

public class SplitScreenSelectState extends OverviewState {
    public int getVisibleElements(Launcher launcher) {
        return 64;
    }

    public SplitScreenSelectState(int i) {
        super(i);
    }

    public float getSplitSelectTranslation(Launcher launcher) {
        return ((RecentsView) launcher.getOverviewPanel()).getSplitSelectTranslation();
    }
}
