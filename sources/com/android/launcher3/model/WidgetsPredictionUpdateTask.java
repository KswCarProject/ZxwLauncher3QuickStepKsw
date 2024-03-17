package com.android.launcher3.model;

import android.app.prediction.AppTarget;
import android.content.ComponentName;
import android.text.TextUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.QuickstepModelDelegate;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class WidgetsPredictionUpdateTask extends BaseModelUpdateTask {
    private final QuickstepModelDelegate.PredictorState mPredictorState;
    private final List<AppTarget> mTargets;

    static /* synthetic */ ComponentKey lambda$execute$2(WidgetItem widgetItem) {
        return widgetItem;
    }

    static /* synthetic */ WidgetItem lambda$execute$3(WidgetItem widgetItem) {
        return widgetItem;
    }

    WidgetsPredictionUpdateTask(QuickstepModelDelegate.PredictorState predictorState, List<AppTarget> list) {
        this.mPredictorState = predictorState;
        this.mTargets = list;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        Set set = (Set) bgDataModel.appWidgets.stream().map($$Lambda$WidgetsPredictionUpdateTask$dn_4OmeRS92CpaepT5P5REgvz3Q.INSTANCE).collect(Collectors.toSet());
        Map<PackageUserKey, List<WidgetItem>> allWidgetsWithoutShortcuts = bgDataModel.widgetsModel.getAllWidgetsWithoutShortcuts();
        BgDataModel.FixedContainerItems fixedContainerItems = new BgDataModel.FixedContainerItems(this.mPredictorState.containerId);
        if (FeatureFlags.ENABLE_LOCAL_RECOMMENDED_WIDGETS_FILTER.get()) {
            for (AppTarget next : this.mTargets) {
                PackageUserKey packageUserKey = new PackageUserKey(next.getPackageName(), next.getUser());
                if (allWidgetsWithoutShortcuts.containsKey(packageUserKey)) {
                    List list = (List) allWidgetsWithoutShortcuts.get(packageUserKey).stream().filter(new Predicate(set) {
                        public final /* synthetic */ Set f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final boolean test(Object obj) {
                            return WidgetsPredictionUpdateTask.lambda$execute$1(this.f$0, (WidgetItem) obj);
                        }
                    }).collect(Collectors.toList());
                    if (list.size() > 0) {
                        fixedContainerItems.items.add(new PendingAddWidgetInfo(((WidgetItem) list.get(0)).widgetInfo, LauncherSettings.Favorites.CONTAINER_WIDGETS_PREDICTION));
                    }
                }
            }
        } else {
            Map map = (Map) allWidgetsWithoutShortcuts.values().stream().flatMap($$Lambda$2uajARPg7zs3QXdN3ekutEAqAgM.INSTANCE).distinct().collect(Collectors.toMap($$Lambda$WidgetsPredictionUpdateTask$OwDR0GelRf9INXFPTiVYXyqZgfs.INSTANCE, $$Lambda$WidgetsPredictionUpdateTask$wPTV8kEA54bvw69xQys4KbtgNw.INSTANCE));
            for (AppTarget next2 : this.mTargets) {
                if (!TextUtils.isEmpty(next2.getClassName())) {
                    ComponentKey componentKey = new ComponentKey(new ComponentName(next2.getPackageName(), next2.getClassName()), next2.getUser());
                    if (map.containsKey(componentKey)) {
                        fixedContainerItems.items.add(new PendingAddWidgetInfo(((WidgetItem) map.get(componentKey)).widgetInfo, LauncherSettings.Favorites.CONTAINER_WIDGETS_PREDICTION));
                    }
                }
            }
        }
        bgDataModel.extraItems.put(this.mPredictorState.containerId, fixedContainerItems);
        bindExtraContainerItems(fixedContainerItems);
    }

    static /* synthetic */ ComponentKey lambda$execute$0(LauncherAppWidgetInfo launcherAppWidgetInfo) {
        return new ComponentKey(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
    }

    static /* synthetic */ boolean lambda$execute$1(Set set, WidgetItem widgetItem) {
        return !set.contains(new ComponentKey(widgetItem.componentName, widgetItem.user));
    }
}
