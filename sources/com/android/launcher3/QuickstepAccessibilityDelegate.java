package com.android.launcher3;

import android.view.View;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.uioverrides.PredictedAppIcon;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import java.util.List;

public class QuickstepAccessibilityDelegate extends LauncherAccessibilityDelegate {
    public QuickstepAccessibilityDelegate(QuickstepLauncher quickstepLauncher) {
        super(quickstepLauncher);
        this.mActions.put(R.id.action_pin_prediction, new BaseAccessibilityDelegate.LauncherAction(R.id.action_pin_prediction, R.string.pin_prediction, 44));
    }

    /* access modifiers changed from: protected */
    public void getSupportedActions(View view, ItemInfo itemInfo, List<BaseAccessibilityDelegate<Launcher>.LauncherAction> list) {
        if ((view instanceof PredictedAppIcon) && !((PredictedAppIcon) view).isPinned()) {
            list.add(new BaseAccessibilityDelegate.LauncherAction(R.id.action_pin_prediction, R.string.pin_prediction, 44));
        }
        super.getSupportedActions(view, itemInfo, list);
    }

    /* access modifiers changed from: protected */
    public boolean performAction(View view, ItemInfo itemInfo, int i, boolean z) {
        QuickstepLauncher quickstepLauncher = (QuickstepLauncher) this.mContext;
        if (i != R.id.action_pin_prediction) {
            return super.performAction(view, itemInfo, i, z);
        }
        if (quickstepLauncher.getHotseatPredictionController() == null) {
            return false;
        }
        quickstepLauncher.getHotseatPredictionController().pinPrediction(itemInfo);
        return true;
    }
}
