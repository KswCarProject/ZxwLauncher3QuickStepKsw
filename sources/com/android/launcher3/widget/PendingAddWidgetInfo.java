package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.Bundle;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.widget.util.WidgetSizes;

public class PendingAddWidgetInfo extends PendingAddItemInfo {
    public Bundle bindOptions = null;
    public AppWidgetHostView boundWidget;
    public int icon;
    public LauncherAppWidgetProviderInfo info;
    public int previewImage;
    public int sourceContainer;

    public PendingAddWidgetInfo(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, int i) {
        if (launcherAppWidgetProviderInfo.isCustomWidget()) {
            this.itemType = 5;
        } else {
            this.itemType = 4;
        }
        this.info = launcherAppWidgetProviderInfo;
        this.user = launcherAppWidgetProviderInfo.getProfile();
        this.componentName = launcherAppWidgetProviderInfo.provider;
        this.previewImage = launcherAppWidgetProviderInfo.previewImage;
        this.icon = launcherAppWidgetProviderInfo.icon;
        this.spanX = launcherAppWidgetProviderInfo.spanX;
        this.spanY = launcherAppWidgetProviderInfo.spanY;
        this.minSpanX = launcherAppWidgetProviderInfo.minSpanX;
        this.minSpanY = launcherAppWidgetProviderInfo.minSpanY;
        this.container = i;
        this.sourceContainer = i;
    }

    public WidgetAddFlowHandler getHandler() {
        return new WidgetAddFlowHandler((AppWidgetProviderInfo) this.info);
    }

    public Bundle getDefaultSizeOptions(Context context) {
        return WidgetSizes.getWidgetSizeOptions(context, this.componentName, this.spanX, this.spanY);
    }

    public LauncherAtom.ItemInfo buildProto(FolderInfo folderInfo) {
        return (LauncherAtom.ItemInfo) ((LauncherAtom.ItemInfo.Builder) super.buildProto(folderInfo).toBuilder()).addItemAttributes(LauncherAppWidgetInfo.getAttribute(this.sourceContainer)).build();
    }
}
