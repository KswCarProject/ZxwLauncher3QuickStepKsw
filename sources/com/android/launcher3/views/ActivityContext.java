package com.android.launcher3.views;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.search.SearchAdapterProvider;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.StringCache;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.util.ViewCache;

public interface ActivityContext {
    static /* synthetic */ void lambda$getItemOnClickListener$0(View view) {
    }

    void applyOverwritesToLogItem(LauncherAtom.ItemInfo.Builder builder) {
    }

    SearchAdapterProvider<?> createSearchAdapterProvider(ActivityAllAppsContainerView<?> activityAllAppsContainerView) {
        return null;
    }

    FolderIcon findFolderIcon(int i) {
        return null;
    }

    boolean finishAutoCancelActionMode() {
        return false;
    }

    View.AccessibilityDelegate getAccessibilityDelegate() {
        return null;
    }

    ActivityAllAppsContainerView<?> getAppsView() {
        return null;
    }

    DeviceProfile getDeviceProfile();

    DotInfo getDotInfoForItem(ItemInfo itemInfo) {
        return null;
    }

    <T extends DragController> T getDragController() {
        return null;
    }

    BaseDragLayer getDragLayer();

    OnboardingPrefs<?> getOnboardingPrefs() {
        return null;
    }

    PopupDataProvider getPopupDataProvider() {
        return null;
    }

    StringCache getStringCache() {
        return null;
    }

    void invalidateParent(ItemInfo itemInfo) {
    }

    boolean isBindingItems() {
        return false;
    }

    boolean shouldUseColorExtractionForPopup() {
        return true;
    }

    void updateOpenFolderPosition(int[] iArr, Rect rect, int i, int i2) {
    }

    Rect getFolderBoundingBox() {
        return getDeviceProfile().getAbsoluteOpenFolderBounds();
    }

    LayoutInflater getLayoutInflater() {
        if (!(this instanceof Context)) {
            return null;
        }
        Context context = (Context) this;
        return LayoutInflater.from(context).cloneInContext(context);
    }

    ViewCache getViewCache() {
        return new ViewCache();
    }

    StatsLogManager getStatsLogManager() {
        return StatsLogManager.newInstance((Context) this);
    }

    static <T extends Context & ActivityContext> T lookupContext(Context context) {
        T lookupContextNoThrow = lookupContextNoThrow(context);
        if (lookupContextNoThrow != null) {
            return lookupContextNoThrow;
        }
        throw new IllegalArgumentException("Cannot find ActivityContext in parent tree");
    }

    static <T extends Context & ActivityContext> T lookupContextNoThrow(Context context) {
        if (context instanceof ActivityContext) {
            return context;
        }
        if (context instanceof ContextWrapper) {
            return lookupContextNoThrow(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    View.OnClickListener getItemOnClickListener() {
        return $$Lambda$ActivityContext$NNvQejUrewzNnNa3uiJpQcYaOM.INSTANCE;
    }
}
