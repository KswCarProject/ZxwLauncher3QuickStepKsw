package com.android.launcher3.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.icons.FastBitmapDrawable;

public class AllAppsButton extends BubbleTextView {
    public AllAppsButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public AllAppsButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AllAppsButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setIcon(new FastBitmapDrawable(LauncherAppState.getInstance(context).getIconCache().getIconFactory().createScaledBitmapWithShadow(new ContextThemeWrapper(context, R.style.AllAppsButtonTheme).getDrawable(R.drawable.ic_all_apps_button))));
    }
}
