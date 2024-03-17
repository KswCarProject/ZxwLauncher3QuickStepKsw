package com.android.launcher3.hybridhotseat;

import android.app.prediction.AppTarget;
import android.content.Context;
import android.os.Bundle;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.PredictionHelper;
import com.android.launcher3.model.data.ItemInfo;
import java.util.ArrayList;
import java.util.Iterator;

public class HotseatPredictionModel {
    private static final String BUNDLE_KEY_CURRENT_ITEMS = "current_items";
    private static final String BUNDLE_KEY_PIN_EVENTS = "pin_events";

    public static Bundle convertDataModelToAppTargetBundle(Context context, BgDataModel bgDataModel) {
        Bundle bundle = new Bundle();
        ArrayList arrayList = new ArrayList();
        Iterator<ItemInfo> it = bgDataModel.getAllWorkspaceItems().iterator();
        while (it.hasNext()) {
            ItemInfo next = it.next();
            AppTarget appTargetFromItemInfo = PredictionHelper.getAppTargetFromItemInfo(context, next);
            if (appTargetFromItemInfo == null || PredictionHelper.isTrackedForHotseatPrediction(next)) {
                arrayList.add(PredictionHelper.wrapAppTargetWithItemLocation(appTargetFromItemInfo, 3, next));
            }
        }
        ArrayList arrayList2 = new ArrayList();
        BgDataModel.FixedContainerItems fixedContainerItems = (BgDataModel.FixedContainerItems) bgDataModel.extraItems.get(LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
        if (fixedContainerItems != null) {
            for (ItemInfo appTargetFromItemInfo2 : fixedContainerItems.items) {
                AppTarget appTargetFromItemInfo3 = PredictionHelper.getAppTargetFromItemInfo(context, appTargetFromItemInfo2);
                if (appTargetFromItemInfo3 != null) {
                    arrayList2.add(appTargetFromItemInfo3);
                }
            }
        }
        bundle.putParcelableArrayList(BUNDLE_KEY_PIN_EVENTS, arrayList);
        bundle.putParcelableArrayList(BUNDLE_KEY_CURRENT_ITEMS, arrayList2);
        return bundle;
    }
}
