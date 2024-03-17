package com.android.launcher3.widget.util;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Size;
import android.util.SizeF;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.model.WidgetItem;
import java.util.ArrayList;
import java.util.List;

public final class WidgetSizes {
    public static ArrayList<SizeF> getWidgetPaddedSizes(Context context, ComponentName componentName, int i, int i2) {
        Rect defaultPaddingForWidget = AppWidgetHostView.getDefaultPaddingForWidget(context, componentName, (Rect) null);
        ArrayList<SizeF> arrayList = new ArrayList<>(2);
        float f = context.getResources().getDisplayMetrics().density;
        Point point = new Point();
        for (DeviceProfile next : LauncherAppState.getIDP(context).supportedProfiles) {
            Size widgetSizePx = getWidgetSizePx(next, i, i2, point);
            if (!next.shouldInsetWidgets()) {
                widgetSizePx = new Size((widgetSizePx.getWidth() - defaultPaddingForWidget.left) - defaultPaddingForWidget.right, (widgetSizePx.getHeight() - defaultPaddingForWidget.top) - defaultPaddingForWidget.bottom);
            }
            arrayList.add(new SizeF(((float) widgetSizePx.getWidth()) / f, ((float) widgetSizePx.getHeight()) / f));
        }
        return arrayList;
    }

    public static Size getWidgetSizePx(DeviceProfile deviceProfile, int i, int i2) {
        return getWidgetSizePx(deviceProfile, i, i2, (Point) null);
    }

    public static Size getWidgetPaddedSizePx(Context context, ComponentName componentName, DeviceProfile deviceProfile, int i, int i2) {
        Size widgetSizePx = getWidgetSizePx(deviceProfile, i, i2);
        if (deviceProfile.shouldInsetWidgets()) {
            return widgetSizePx;
        }
        Rect defaultPaddingForWidget = AppWidgetHostView.getDefaultPaddingForWidget(context, componentName, (Rect) null);
        return new Size((widgetSizePx.getWidth() - defaultPaddingForWidget.left) - defaultPaddingForWidget.right, (widgetSizePx.getHeight() - defaultPaddingForWidget.top) - defaultPaddingForWidget.bottom);
    }

    public static Size getWidgetItemSizePx(Context context, DeviceProfile deviceProfile, WidgetItem widgetItem) {
        if (widgetItem.isShortcut()) {
            int dimensionPixelSize = deviceProfile.allAppsIconSizePx + (context.getResources().getDimensionPixelSize(R.dimen.widget_preview_shortcut_padding) * 2);
            return new Size(dimensionPixelSize, dimensionPixelSize);
        }
        Size widgetSizePx = getWidgetSizePx(deviceProfile, widgetItem.spanX, widgetItem.spanY, (Point) null);
        if (!deviceProfile.shouldInsetWidgets()) {
            return widgetSizePx;
        }
        Rect rect = new Rect();
        AppWidgetHostView.getDefaultPaddingForWidget(context, widgetItem.componentName, rect);
        return new Size(widgetSizePx.getWidth() + rect.left + rect.right, widgetSizePx.getHeight() + rect.top + rect.bottom);
    }

    private static Size getWidgetSizePx(DeviceProfile deviceProfile, int i, int i2, Point point) {
        int i3 = (i - 1) * deviceProfile.cellLayoutBorderSpacePx.x;
        int i4 = (i2 - 1) * deviceProfile.cellLayoutBorderSpacePx.y;
        if (point == null) {
            point = new Point();
        }
        deviceProfile.getCellSize(point);
        return new Size((i * point.x) + i3, (i2 * point.y) + i4);
    }

    public static void updateWidgetSizeRanges(AppWidgetHostView appWidgetHostView, Context context, int i, int i2) {
        AppWidgetManager instance = AppWidgetManager.getInstance(context);
        int appWidgetId = appWidgetHostView.getAppWidgetId();
        if (appWidgetId > 0) {
            Bundle widgetSizeOptions = getWidgetSizeOptions(context, appWidgetHostView.getAppWidgetInfo().provider, i, i2);
            if (!widgetSizeOptions.getParcelableArrayList("appWidgetSizes").equals(instance.getAppWidgetOptions(appWidgetId).getParcelableArrayList("appWidgetSizes"))) {
                instance.updateAppWidgetOptions(appWidgetId, widgetSizeOptions);
            }
        }
    }

    public static Bundle getWidgetSizeOptions(Context context, ComponentName componentName, int i, int i2) {
        ArrayList<SizeF> widgetPaddedSizes = getWidgetPaddedSizes(context, componentName, i, i2);
        Rect minMaxSizes = getMinMaxSizes(widgetPaddedSizes);
        Bundle bundle = new Bundle();
        bundle.putInt("appWidgetMinWidth", minMaxSizes.left);
        bundle.putInt("appWidgetMinHeight", minMaxSizes.top);
        bundle.putInt("appWidgetMaxWidth", minMaxSizes.right);
        bundle.putInt("appWidgetMaxHeight", minMaxSizes.bottom);
        bundle.putParcelableArrayList("appWidgetSizes", widgetPaddedSizes);
        return bundle;
    }

    private static Rect getMinMaxSizes(List<SizeF> list) {
        if (list.isEmpty()) {
            return new Rect();
        }
        SizeF sizeF = list.get(0);
        Rect rect = new Rect((int) sizeF.getWidth(), (int) sizeF.getHeight(), (int) sizeF.getWidth(), (int) sizeF.getHeight());
        for (int i = 1; i < list.size(); i++) {
            rect.union((int) list.get(i).getWidth(), (int) list.get(i).getHeight());
        }
        return rect;
    }
}
