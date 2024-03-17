package com.android.launcher3.dragndrop;

import android.content.pm.LauncherApps;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.DragEvent;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.PendingItemDragHelper;
import com.android.launcher3.widget.WidgetAddFlowHandler;

public class PinItemDragListener extends BaseItemDragListener {
    private final CancellationSignal mCancelSignal;
    private final float mPreviewScale;
    private final LauncherApps.PinItemRequest mRequest;

    public PinItemDragListener(LauncherApps.PinItemRequest pinItemRequest, Rect rect, int i, int i2) {
        this(pinItemRequest, rect, i, i2, 1.0f);
    }

    public PinItemDragListener(LauncherApps.PinItemRequest pinItemRequest, Rect rect, int i, int i2, float f) {
        super(rect, i, i2);
        this.mRequest = pinItemRequest;
        this.mCancelSignal = new CancellationSignal();
        this.mPreviewScale = f;
    }

    /* access modifiers changed from: protected */
    public boolean onDragStart(DragEvent dragEvent) {
        if (!this.mRequest.isValid()) {
            return false;
        }
        return super.onDragStart(dragEvent);
    }

    public boolean init(Launcher launcher, boolean z) {
        super.init(launcher, z);
        if (z) {
            return false;
        }
        launcher.useFadeOutAnimationForLauncherStart(this.mCancelSignal);
        return false;
    }

    /* access modifiers changed from: protected */
    public PendingItemDragHelper createDragHelper() {
        Object obj;
        if (this.mRequest.getRequestType() == 1) {
            obj = new PendingAddShortcutInfo(new PinShortcutRequestActivityInfo(this.mRequest, this.mLauncher));
        } else {
            LauncherAppWidgetProviderInfo fromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mLauncher, this.mRequest.getAppWidgetProviderInfo(this.mLauncher));
            final PinWidgetFlowHandler pinWidgetFlowHandler = new PinWidgetFlowHandler(fromProviderInfo, this.mRequest);
            obj = new PendingAddWidgetInfo(fromProviderInfo, LauncherSettings.Favorites.CONTAINER_PIN_WIDGETS) {
                public WidgetAddFlowHandler getHandler() {
                    return pinWidgetFlowHandler;
                }
            };
        }
        View view = new View(this.mLauncher);
        view.setTag(obj);
        PendingItemDragHelper pendingItemDragHelper = new PendingItemDragHelper(view);
        if (this.mRequest.getRequestType() == 2) {
            pendingItemDragHelper.setRemoteViewsPreview(getPreview(this.mRequest), this.mPreviewScale);
        }
        return pendingItemDragHelper;
    }

    /* access modifiers changed from: protected */
    public void postCleanup() {
        super.postCleanup();
        this.mCancelSignal.cancel();
    }

    public static RemoteViews getPreview(LauncherApps.PinItemRequest pinItemRequest) {
        Bundle extras = pinItemRequest.getExtras();
        if (extras == null || !(extras.get("appWidgetPreview") instanceof RemoteViews)) {
            return null;
        }
        return (RemoteViews) extras.get("appWidgetPreview");
    }
}
