package com.android.launcher3.dragndrop;

import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.LauncherApps;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.widget.WidgetAddFlowHandler;

public class PinWidgetFlowHandler extends WidgetAddFlowHandler implements Parcelable {
    public static final Parcelable.Creator<PinWidgetFlowHandler> CREATOR = new Parcelable.Creator<PinWidgetFlowHandler>() {
        public PinWidgetFlowHandler createFromParcel(Parcel parcel) {
            return new PinWidgetFlowHandler(parcel);
        }

        public PinWidgetFlowHandler[] newArray(int i) {
            return new PinWidgetFlowHandler[i];
        }
    };
    private final LauncherApps.PinItemRequest mRequest;

    public boolean needsConfigure() {
        return false;
    }

    public PinWidgetFlowHandler(AppWidgetProviderInfo appWidgetProviderInfo, LauncherApps.PinItemRequest pinItemRequest) {
        super(appWidgetProviderInfo);
        this.mRequest = pinItemRequest;
    }

    protected PinWidgetFlowHandler(Parcel parcel) {
        super(parcel);
        this.mRequest = (LauncherApps.PinItemRequest) LauncherApps.PinItemRequest.CREATOR.createFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        this.mRequest.writeToParcel(parcel, i);
    }

    public boolean startConfigActivity(Launcher launcher, int i, ItemInfo itemInfo, int i2) {
        Bundle bundle = new Bundle();
        bundle.putInt(LauncherSettings.Favorites.APPWIDGET_ID, i);
        this.mRequest.accept(bundle);
        return false;
    }
}
