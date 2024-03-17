package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.UserHandle;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.ComponentWithLabelAndIcon;
import com.android.launcher3.icons.IconCache;

public class LauncherAppWidgetProviderInfo extends AppWidgetProviderInfo implements ComponentWithLabelAndIcon {
    public static final String CLS_CUSTOM_WIDGET_PREFIX = "#custom-widget-";
    private boolean mIsMinSizeFulfilled;
    public int maxSpanX;
    public int maxSpanY;
    public int minSpanX;
    public int minSpanY;
    public int spanX;
    public int spanY;

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static LauncherAppWidgetProviderInfo fromProviderInfo(Context context, AppWidgetProviderInfo appWidgetProviderInfo) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo;
        if (appWidgetProviderInfo instanceof LauncherAppWidgetProviderInfo) {
            launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) appWidgetProviderInfo;
        } else {
            Parcel obtain = Parcel.obtain();
            appWidgetProviderInfo.writeToParcel(obtain, 0);
            obtain.setDataPosition(0);
            launcherAppWidgetProviderInfo = new LauncherAppWidgetProviderInfo(obtain);
            obtain.recycle();
        }
        launcherAppWidgetProviderInfo.initSpans(context, LauncherAppState.getIDP(context));
        return launcherAppWidgetProviderInfo;
    }

    protected LauncherAppWidgetProviderInfo() {
    }

    protected LauncherAppWidgetProviderInfo(Parcel parcel) {
        super(parcel);
    }

    public void initSpans(Context context, InvariantDeviceProfile invariantDeviceProfile) {
        InvariantDeviceProfile invariantDeviceProfile2 = invariantDeviceProfile;
        int i = invariantDeviceProfile2.numColumns;
        int i2 = invariantDeviceProfile2.numRows;
        Rect rect = new Rect();
        Rect rect2 = new Rect();
        AppWidgetHostView.getDefaultPaddingForWidget(context, this.provider, rect);
        Point point = new Point();
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        for (DeviceProfile next : invariantDeviceProfile2.supportedProfiles) {
            next.getCellSize(point);
            if (next.shouldInsetWidgets()) {
                rect2.setEmpty();
            } else {
                rect2.set(rect);
            }
            i3 = Math.max(i3, getSpanX(rect2, this.minResizeWidth, next.cellLayoutBorderSpacePx.x, (float) point.x));
            i4 = Math.max(i4, getSpanY(rect2, this.minResizeHeight, next.cellLayoutBorderSpacePx.y, (float) point.y));
            if (Utilities.ATLEAST_S) {
                if (this.maxResizeWidth > 0) {
                    i = Math.min(i, getSpanX(rect2, this.maxResizeWidth, next.cellLayoutBorderSpacePx.x, (float) point.x));
                }
                if (this.maxResizeHeight > 0) {
                    i2 = Math.min(i2, getSpanY(rect2, this.maxResizeHeight, next.cellLayoutBorderSpacePx.y, (float) point.y));
                }
            }
            i5 = Math.max(i5, getSpanX(rect2, this.minWidth, next.cellLayoutBorderSpacePx.x, (float) point.x));
            i6 = Math.max(i6, getSpanY(rect2, this.minHeight, next.cellLayoutBorderSpacePx.y, (float) point.y));
        }
        if (Utilities.ATLEAST_S) {
            i = Math.max(i, i3);
            i2 = Math.max(i2, i4);
            if (this.targetCellWidth >= i3 && this.targetCellWidth <= i && this.targetCellHeight >= i4 && this.targetCellHeight <= i2) {
                i5 = this.targetCellWidth;
                i6 = this.targetCellHeight;
            }
        }
        this.minSpanX = Math.min(i5, i3);
        this.minSpanY = Math.min(i6, i4);
        this.maxSpanX = i;
        this.maxSpanY = i2;
        this.mIsMinSizeFulfilled = Math.min(i5, i3) <= invariantDeviceProfile2.numColumns && Math.min(i6, i4) <= invariantDeviceProfile2.numRows;
        this.spanX = Math.min(i5, invariantDeviceProfile2.numColumns);
        this.spanY = Math.min(i6, invariantDeviceProfile2.numRows);
    }

    public boolean isMinSizeFulfilled() {
        return this.mIsMinSizeFulfilled;
    }

    private int getSpanX(Rect rect, int i, int i2, float f) {
        return Math.max(1, (int) Math.ceil((double) (((float) (((i + rect.left) + rect.right) + i2)) / (f + ((float) i2)))));
    }

    private int getSpanY(Rect rect, int i, int i2, float f) {
        return Math.max(1, (int) Math.ceil((double) (((float) (((i + rect.top) + rect.bottom) + i2)) / (f + ((float) i2)))));
    }

    public String getLabel(PackageManager packageManager) {
        return super.loadLabel(packageManager);
    }

    public Point getMinSpans() {
        int i = -1;
        int i2 = (this.resizeMode & 1) != 0 ? this.minSpanX : -1;
        if ((this.resizeMode & 2) != 0) {
            i = this.minSpanY;
        }
        return new Point(i2, i);
    }

    public boolean isCustomWidget() {
        return this.provider.getClassName().startsWith(CLS_CUSTOM_WIDGET_PREFIX);
    }

    public int getWidgetFeatures() {
        if (Utilities.ATLEAST_P) {
            return this.widgetFeatures;
        }
        return 0;
    }

    public boolean isReconfigurable() {
        return (this.configure == null || (getWidgetFeatures() & 1) == 0) ? false : true;
    }

    public boolean isConfigurationOptional() {
        return Utilities.ATLEAST_S && isReconfigurable() && (getWidgetFeatures() & 4) != 0;
    }

    public final ComponentName getComponent() {
        return this.provider;
    }

    public final UserHandle getUser() {
        return getProfile();
    }

    public Drawable getFullResIcon(IconCache iconCache) {
        return iconCache.getFullResIcon(this.provider.getPackageName(), this.icon);
    }
}
