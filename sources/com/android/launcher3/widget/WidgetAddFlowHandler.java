package com.android.launcher3.widget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.launcher3.Launcher;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.util.PendingRequestArgs;

public class WidgetAddFlowHandler implements Parcelable {
    public static final Parcelable.Creator<WidgetAddFlowHandler> CREATOR = new Parcelable.Creator<WidgetAddFlowHandler>() {
        public WidgetAddFlowHandler createFromParcel(Parcel parcel) {
            return new WidgetAddFlowHandler(parcel);
        }

        public WidgetAddFlowHandler[] newArray(int i) {
            return new WidgetAddFlowHandler[i];
        }
    };
    private final AppWidgetProviderInfo mProviderInfo;

    public int describeContents() {
        return 0;
    }

    public WidgetAddFlowHandler(AppWidgetProviderInfo appWidgetProviderInfo) {
        this.mProviderInfo = appWidgetProviderInfo;
    }

    protected WidgetAddFlowHandler(Parcel parcel) {
        this.mProviderInfo = (AppWidgetProviderInfo) AppWidgetProviderInfo.CREATOR.createFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        this.mProviderInfo.writeToParcel(parcel, i);
    }

    public void startBindFlow(Launcher launcher, int i, ItemInfo itemInfo, int i2) {
        launcher.setWaitingForResult(PendingRequestArgs.forWidgetInfo(i, this, itemInfo));
        launcher.getAppWidgetHost().startBindFlow(launcher, i, this.mProviderInfo, i2);
    }

    public boolean startConfigActivity(Launcher launcher, LauncherAppWidgetInfo launcherAppWidgetInfo, int i) {
        return startConfigActivity(launcher, launcherAppWidgetInfo.appWidgetId, launcherAppWidgetInfo, i);
    }

    public boolean startConfigActivity(Launcher launcher, int i, ItemInfo itemInfo, int i2) {
        if (!needsConfigure()) {
            return false;
        }
        launcher.setWaitingForResult(PendingRequestArgs.forWidgetInfo(i, this, itemInfo));
        launcher.getAppWidgetHost().startConfigActivity(launcher, i, i2);
        return true;
    }

    public boolean needsConfigure() {
        int i = this.mProviderInfo.widgetFeatures;
        boolean z = ((i & 4) == 0 || (i & 1) == 0) ? false : true;
        if (this.mProviderInfo.configure == null || z) {
            return false;
        }
        return true;
    }

    public LauncherAppWidgetProviderInfo getProviderInfo(Context context) {
        return LauncherAppWidgetProviderInfo.fromProviderInfo(context, this.mProviderInfo);
    }
}
