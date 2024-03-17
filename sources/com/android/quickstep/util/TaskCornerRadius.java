package com.android.quickstep.util;

import android.content.Context;
import android.content.res.Resources;
import com.android.launcher3.R;
import com.android.launcher3.util.Themes;
import com.android.systemui.shared.system.QuickStepContract;

public class TaskCornerRadius {
    public static float get(Context context) {
        Resources resources = context.getResources();
        if (!QuickStepContract.supportsRoundedCornersOnWindows(resources)) {
            return resources.getDimension(R.dimen.task_corner_radius_small);
        }
        float dimension = resources.getDimension(R.dimen.task_corner_radius_override);
        return dimension > 0.0f ? dimension : Themes.getDialogCornerRadius(context);
    }
}
