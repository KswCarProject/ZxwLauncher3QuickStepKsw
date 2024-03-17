package com.android.launcher3.taskbar.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.SearchUiManager;

public class TaskbarAllAppsFallbackSearchContainer extends View implements SearchUiManager {
    public ExtendedEditText getEditText() {
        return null;
    }

    public void initializeSearch(ActivityAllAppsContainerView<?> activityAllAppsContainerView) {
    }

    public void resetSearch() {
    }

    public TaskbarAllAppsFallbackSearchContainer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskbarAllAppsFallbackSearchContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}
