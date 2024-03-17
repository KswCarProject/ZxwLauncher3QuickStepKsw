package com.android.launcher3.model;

import android.app.prediction.AppTarget;
import android.app.prediction.AppTargetEvent;
import android.app.prediction.AppTargetId;
import android.content.ComponentName;
import android.content.Context;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import java.util.Locale;

public final class PredictionHelper {
    private static final String APP_LOCATION_HOTSEAT = "hotseat";
    private static final String APP_LOCATION_WORKSPACE = "workspace";

    public static AppTarget getAppTargetFromItemInfo(Context context, ItemInfo itemInfo) {
        if (itemInfo == null) {
            return null;
        }
        if (itemInfo.itemType == 4 && (itemInfo instanceof LauncherAppWidgetInfo)) {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
            if (launcherAppWidgetInfo.providerName != null) {
                ComponentName componentName = launcherAppWidgetInfo.providerName;
                return new AppTarget.Builder(new AppTargetId("widget:" + componentName.getPackageName()), componentName.getPackageName(), itemInfo.user).setClassName(componentName.getClassName()).build();
            }
        }
        if (itemInfo.itemType == 0 && itemInfo.getTargetComponent() != null) {
            ComponentName targetComponent = itemInfo.getTargetComponent();
            return new AppTarget.Builder(new AppTargetId("app:" + targetComponent.getPackageName()), targetComponent.getPackageName(), itemInfo.user).setClassName(targetComponent.getClassName()).build();
        } else if (itemInfo.itemType == 6 && (itemInfo instanceof WorkspaceItemInfo)) {
            ShortcutKey fromItemInfo = ShortcutKey.fromItemInfo(itemInfo);
            return new AppTarget.Builder(new AppTargetId("shortcut:" + fromItemInfo.getId()), fromItemInfo.componentName.getPackageName(), fromItemInfo.user).build();
        } else if (itemInfo.itemType == 2) {
            return new AppTarget.Builder(new AppTargetId("folder:" + itemInfo.id), context.getPackageName(), itemInfo.user).build();
        } else {
            return null;
        }
    }

    public static AppTargetEvent wrapAppTargetWithItemLocation(AppTarget appTarget, int i, ItemInfo itemInfo) {
        Locale locale = Locale.ENGLISH;
        Object[] objArr = new Object[6];
        objArr[0] = itemInfo.container == -101 ? APP_LOCATION_HOTSEAT : "workspace";
        objArr[1] = Integer.valueOf(itemInfo.screenId);
        objArr[2] = Integer.valueOf(itemInfo.cellX);
        objArr[3] = Integer.valueOf(itemInfo.cellY);
        objArr[4] = Integer.valueOf(itemInfo.spanX);
        objArr[5] = Integer.valueOf(itemInfo.spanY);
        return new AppTargetEvent.Builder(appTarget, i).setLaunchLocation(String.format(locale, "%s/%d/[%d,%d]/[%d,%d]", objArr)).build();
    }

    public static boolean isTrackedForHotseatPrediction(ItemInfo itemInfo) {
        return itemInfo.container == -101 || (itemInfo.container == -100 && itemInfo.screenId == 0);
    }

    /* renamed from: com.android.launcher3.model.PredictionHelper$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        static {
            /*
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase[] r0 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase = r0
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r1 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.HOTSEAT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r1 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.WORKSPACE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.PredictionHelper.AnonymousClass1.<clinit>():void");
        }
    }

    public static boolean isTrackedForHotseatPrediction(LauncherAtom.ItemInfo itemInfo) {
        LauncherAtom.ContainerInfo containerInfo = itemInfo.getContainerInfo();
        int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase[containerInfo.getContainerCase().ordinal()];
        if (i == 1) {
            return true;
        }
        if (i != 2) {
            return false;
        }
        if (containerInfo.getWorkspace().getPageIndex() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isTrackedForWidgetPrediction(ItemInfo itemInfo) {
        return itemInfo.itemType == 4 && itemInfo.container == -100;
    }

    public static boolean isTrackedForWidgetPrediction(LauncherAtom.ItemInfo itemInfo) {
        return itemInfo.getItemCase() == LauncherAtom.ItemInfo.ItemCase.WIDGET && itemInfo.getContainerInfo().getContainerCase() == LauncherAtom.ContainerInfo.ContainerCase.WORKSPACE;
    }
}
