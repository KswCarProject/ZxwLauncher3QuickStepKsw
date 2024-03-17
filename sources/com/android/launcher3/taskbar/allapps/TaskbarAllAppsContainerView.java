package com.android.launcher3.taskbar.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowInsets;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.AllAppsGridAdapter;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.allapps.BaseAdapterProvider;
import com.android.launcher3.allapps.BaseAllAppsAdapter;

public class TaskbarAllAppsContainerView extends ActivityAllAppsContainerView<TaskbarAllAppsContext> {
    public TaskbarAllAppsContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskbarAllAppsContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        setInsets(windowInsets.getInsets(WindowInsets.Type.systemBars()).toRect());
        return super.onApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: protected */
    public BaseAllAppsAdapter<TaskbarAllAppsContext> createAdapter(AlphabeticalAppsList<TaskbarAllAppsContext> alphabeticalAppsList, BaseAdapterProvider[] baseAdapterProviderArr) {
        return new AllAppsGridAdapter((TaskbarAllAppsContext) this.mActivityContext, getLayoutInflater(), alphabeticalAppsList, baseAdapterProviderArr);
    }
}
