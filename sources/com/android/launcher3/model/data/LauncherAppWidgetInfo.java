package com.android.launcher3.model.data;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Process;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.util.WidgetSizes;

public class LauncherAppWidgetInfo extends ItemInfo {
    public static final int CUSTOM_WIDGET_ID = -100;
    public static final int FEATURE_MAX_SIZE = 32;
    public static final int FEATURE_MIN_SIZE = 16;
    public static final int FEATURE_OPTIONAL_CONFIGURATION = 2;
    public static final int FEATURE_PREVIEW_LAYOUT = 4;
    public static final int FEATURE_RECONFIGURABLE = 1;
    public static final int FEATURE_ROUNDED_CORNERS = 64;
    public static final int FEATURE_TARGET_CELL_SIZE = 8;
    public static final int FLAG_DIRECT_CONFIG = 32;
    public static final int FLAG_ID_ALLOCATED = 16;
    public static final int FLAG_ID_NOT_VALID = 1;
    public static final int FLAG_PROVIDER_NOT_READY = 2;
    public static final int FLAG_RESTORE_STARTED = 8;
    public static final int FLAG_UI_NOT_READY = 4;
    public static final int NO_ID = -1;
    public static final int OPTION_SEARCH_WIDGET = 1;
    public static final int RESTORE_COMPLETED = 0;
    public int appWidgetId;
    public Intent bindOptions;
    public int installProgress;
    private boolean mHasNotifiedInitialWidgetSizeChanged;
    public int options;
    public PackageItemInfo pendingItemInfo;
    public ComponentName providerName;
    public int restoreStatus;
    public int sourceContainer;
    private int widgetFeatures;

    public LauncherAppWidgetInfo(int i, ComponentName componentName) {
        this.appWidgetId = -1;
        this.installProgress = -1;
        this.sourceContainer = -1;
        this.appWidgetId = i;
        this.providerName = componentName;
        if (isCustomWidget()) {
            this.itemType = 5;
        } else {
            this.itemType = 4;
        }
        this.spanX = -1;
        this.spanY = -1;
        this.widgetFeatures = -1;
        this.user = Process.myUserHandle();
        this.restoreStatus = 0;
    }

    public LauncherAppWidgetInfo(int i, ComponentName componentName, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, AppWidgetHostView appWidgetHostView) {
        this(i, componentName);
        this.widgetFeatures = computeWidgetFeatures(launcherAppWidgetProviderInfo, appWidgetHostView);
    }

    public LauncherAppWidgetInfo() {
        this.appWidgetId = -1;
        this.installProgress = -1;
        this.sourceContainer = -1;
        this.itemType = 4;
    }

    public boolean isCustomWidget() {
        return this.appWidgetId <= -100;
    }

    public ComponentName getTargetComponent() {
        return this.providerName;
    }

    public void onAddToDatabase(ContentWriter contentWriter) {
        super.onAddToDatabase(contentWriter);
        contentWriter.put(LauncherSettings.Favorites.APPWIDGET_ID, Integer.valueOf(this.appWidgetId)).put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, this.providerName.flattenToString()).put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(this.restoreStatus)).put(LauncherSettings.Favorites.OPTIONS, Integer.valueOf(this.options)).put(LauncherSettings.Favorites.INTENT, this.bindOptions).put(LauncherSettings.Favorites.APPWIDGET_SOURCE, Integer.valueOf(this.sourceContainer));
    }

    public void onBindAppWidget(Launcher launcher, AppWidgetHostView appWidgetHostView) {
        if (!this.mHasNotifiedInitialWidgetSizeChanged) {
            WidgetSizes.updateWidgetSizeRanges(appWidgetHostView, launcher, this.spanX, this.spanY);
            this.mHasNotifiedInitialWidgetSizeChanged = true;
        }
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return super.dumpProperties() + " providerName=" + this.providerName + " appWidgetId=" + this.appWidgetId;
    }

    public final boolean isWidgetIdAllocated() {
        int i = this.restoreStatus;
        return (i & 1) == 0 || (i & 16) == 16;
    }

    public final boolean hasRestoreFlag(int i) {
        return (this.restoreStatus & i) == i;
    }

    public final boolean hasOptionFlag(int i) {
        return (i & this.options) != 0;
    }

    private static int computeWidgetFeatures(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, AppWidgetHostView appWidgetHostView) {
        int i = launcherAppWidgetProviderInfo.isReconfigurable() ? 1 : 0;
        if (launcherAppWidgetProviderInfo.isConfigurationOptional()) {
            i |= 2;
        }
        if (Utilities.ATLEAST_S && launcherAppWidgetProviderInfo.previewLayout != 0) {
            i |= 4;
        }
        if ((Utilities.ATLEAST_S && launcherAppWidgetProviderInfo.targetCellWidth > 0) || launcherAppWidgetProviderInfo.targetCellHeight > 0) {
            i |= 8;
        }
        if (launcherAppWidgetProviderInfo.minResizeWidth > 0 || launcherAppWidgetProviderInfo.minResizeHeight > 0) {
            i |= 16;
        }
        if ((Utilities.ATLEAST_S && launcherAppWidgetProviderInfo.maxResizeWidth > 0) || launcherAppWidgetProviderInfo.maxResizeHeight > 0) {
            i |= 32;
        }
        return (!(appWidgetHostView instanceof LauncherAppWidgetHostView) || !((LauncherAppWidgetHostView) appWidgetHostView).hasEnforcedCornerRadius()) ? i : i | 64;
    }

    public static LauncherAtom.Attribute getAttribute(int i) {
        if (i == -105) {
            return LauncherAtom.Attribute.WIDGETS;
        }
        if (i == -104) {
            return LauncherAtom.Attribute.ALL_APPS_SEARCH_RESULT_WIDGETS;
        }
        switch (i) {
            case LauncherSettings.Favorites.CONTAINER_PIN_WIDGETS:
                return LauncherAtom.Attribute.PINITEM;
            case LauncherSettings.Favorites.CONTAINER_BOTTOM_WIDGETS_TRAY:
                return LauncherAtom.Attribute.WIDGETS_BOTTOM_TRAY;
            case LauncherSettings.Favorites.CONTAINER_WIDGETS_PREDICTION:
                return LauncherAtom.Attribute.WIDGETS_TRAY_PREDICTION;
            default:
                return LauncherAtom.Attribute.UNKNOWN;
        }
    }

    public LauncherAtom.ItemInfo buildProto(FolderInfo folderInfo) {
        LauncherAtom.ItemInfo buildProto = super.buildProto(folderInfo);
        return (LauncherAtom.ItemInfo) ((LauncherAtom.ItemInfo.Builder) buildProto.toBuilder()).setWidget(((LauncherAtom.Widget.Builder) buildProto.getWidget().toBuilder()).setWidgetFeatures(this.widgetFeatures)).addItemAttributes(getAttribute(this.sourceContainer)).build();
    }
}
