package com.android.launcher3.graphics;

import android.app.Fragment;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextClock;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Hotseat;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.WorkspaceLayoutManager;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.ModelUtils;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.InstallSessionHelper;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.uioverrides.PredictedAppIconInflater;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.window.WindowManagerProxy;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.BaseLauncherAppWidgetHostView;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.LocalColorExtractor;
import com.android.launcher3.widget.NavigableAppWidgetHostView;
import com.android.launcher3.widget.custom.CustomWidgetManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LauncherPreviewRenderer extends ContextWrapper implements ActivityContext, WorkspaceLayoutManager, LayoutInflater.Factory2 {
    private final AppWidgetHost mAppWidgetHost;
    private final Context mContext;
    private final DeviceProfile mDp;
    private final LayoutInflater mHomeElementInflater;
    private final Hotseat mHotseat;
    private final InvariantDeviceProfile mIdp;
    private final Rect mInsets;
    private final InsettableFrameLayout mRootView;
    /* access modifiers changed from: private */
    public final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private final SparseIntArray mWallpaperColorResources;
    private final WorkspaceItemInfo mWorkspaceItemInfo;
    private final Map<Integer, CellLayout> mWorkspaceScreens;

    public static class PreviewContext extends MainThreadInitializedObject.SandboxContext {
        /* access modifiers changed from: private */
        public final ConcurrentLinkedQueue<LauncherIconsForPreview> mIconPool = new ConcurrentLinkedQueue<>();
        private final InvariantDeviceProfile mIdp;

        public PreviewContext(Context context, InvariantDeviceProfile invariantDeviceProfile) {
            super(context, UserCache.INSTANCE, InstallSessionHelper.INSTANCE, LauncherAppState.INSTANCE, InvariantDeviceProfile.INSTANCE, CustomWidgetManager.INSTANCE, PluginManagerWrapper.INSTANCE, WindowManagerProxy.INSTANCE, DisplayController.INSTANCE);
            this.mIdp = invariantDeviceProfile;
            this.mObjectMap.put(InvariantDeviceProfile.INSTANCE, invariantDeviceProfile);
            this.mObjectMap.put(LauncherAppState.INSTANCE, new LauncherAppState(this, (String) null));
        }

        public LauncherIcons newLauncherIcons(Context context) {
            LauncherIconsForPreview poll = this.mIconPool.poll();
            if (poll != null) {
                return poll;
            }
            return new LauncherIconsForPreview(context, this.mIdp.fillResIconDpi, this.mIdp.iconBitmapSize, -1);
        }

        private final class LauncherIconsForPreview extends LauncherIcons {
            private LauncherIconsForPreview(Context context, int i, int i2, int i3) {
                super(context, i, i2, i3);
            }

            public void recycle() {
                clear();
                PreviewContext.this.mIconPool.offer(this);
            }
        }
    }

    public LauncherPreviewRenderer(Context context, InvariantDeviceProfile invariantDeviceProfile, WallpaperColors wallpaperColors) {
        super(context);
        int i;
        HashMap hashMap = new HashMap();
        this.mWorkspaceScreens = hashMap;
        this.mContext = context;
        this.mIdp = invariantDeviceProfile;
        DeviceProfile copy = invariantDeviceProfile.getDeviceProfile(context).copy(context);
        this.mDp = copy;
        WindowInsets windowInsets = ((WindowManager) context.getSystemService(WindowManager.class)).getCurrentWindowMetrics().getWindowInsets();
        Rect rect = new Rect(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), copy.isTaskbarPresent ? 0 : windowInsets.getSystemWindowInsetBottom());
        this.mInsets = rect;
        copy.updateInsets(rect);
        BitmapInfo createBadgedIconBitmap = new BaseIconFactory(context, invariantDeviceProfile.fillResIconDpi, invariantDeviceProfile.iconBitmapSize) {
        }.createBadgedIconBitmap(new AdaptiveIconDrawable(new ColorDrawable(-1), new ColorDrawable(-1)));
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
        this.mWorkspaceItemInfo = workspaceItemInfo;
        workspaceItemInfo.bitmap = createBadgedIconBitmap;
        workspaceItemInfo.intent = new Intent();
        String string = context.getString(R.string.label_application);
        workspaceItemInfo.title = string;
        workspaceItemInfo.contentDescription = string;
        LayoutInflater from = LayoutInflater.from(new ContextThemeWrapper(this, R.style.HomeScreenElementTheme));
        this.mHomeElementInflater = from;
        from.setFactory2(this);
        LauncherPreviewAppWidgetHost launcherPreviewAppWidgetHost = null;
        InsettableFrameLayout insettableFrameLayout = (InsettableFrameLayout) from.inflate(copy.isTwoPanels ? R.layout.launcher_preview_two_panel_layout : R.layout.launcher_preview_layout, (ViewGroup) null, false);
        this.mRootView = insettableFrameLayout;
        insettableFrameLayout.setInsets(rect);
        measureView(insettableFrameLayout, copy.widthPx, copy.heightPx);
        Hotseat hotseat = (Hotseat) insettableFrameLayout.findViewById(R.id.hotseat);
        this.mHotseat = hotseat;
        hotseat.resetLayout(false);
        CellLayout cellLayout = (CellLayout) insettableFrameLayout.findViewById(R.id.workspace);
        int i2 = copy.workspacePadding.left + copy.cellLayoutPaddingPx.left;
        int i3 = copy.workspacePadding.top + copy.cellLayoutPaddingPx.top;
        if (copy.isTwoPanels) {
            i = copy.cellLayoutBorderSpacePx.x / 2;
        } else {
            i = copy.workspacePadding.right;
        }
        cellLayout.setPadding(i2, i3, i + copy.cellLayoutPaddingPx.right, copy.workspacePadding.bottom + copy.cellLayoutPaddingPx.bottom);
        hashMap.put(0, cellLayout);
        if (copy.isTwoPanels) {
            CellLayout cellLayout2 = (CellLayout) insettableFrameLayout.findViewById(R.id.workspace_right);
            cellLayout2.setPadding((copy.cellLayoutBorderSpacePx.x / 2) + copy.cellLayoutPaddingPx.left, copy.workspacePadding.top + copy.cellLayoutPaddingPx.top, copy.workspacePadding.right + copy.cellLayoutPaddingPx.right, copy.workspacePadding.bottom + copy.cellLayoutPaddingPx.bottom);
            hashMap.put(1, cellLayout2);
        }
        if (Utilities.ATLEAST_S) {
            wallpaperColors = wallpaperColors == null ? WallpaperManager.getInstance(context).getWallpaperColors(1) : wallpaperColors;
            this.mWallpaperColorResources = wallpaperColors != null ? LocalColorExtractor.newInstance(context).generateColorsOverride(wallpaperColors) : null;
        } else {
            this.mWallpaperColorResources = null;
        }
        this.mAppWidgetHost = FeatureFlags.WIDGETS_IN_LAUNCHER_PREVIEW.get() ? new LauncherPreviewAppWidgetHost(context) : launcherPreviewAppWidgetHost;
    }

    public View getRenderedView(BgDataModel bgDataModel, Map<ComponentKey, AppWidgetProviderInfo> map) {
        populate(bgDataModel, map);
        return this.mRootView;
    }

    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        if ("TextClock".equals(str)) {
            return new TextClock(context, attributeSet) {
                public Handler getHandler() {
                    return LauncherPreviewRenderer.this.mUiHandler;
                }
            };
        }
        if (!"fragment".equals(str)) {
            return null;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PreviewFragment);
        FragmentWithPreview fragmentWithPreview = (FragmentWithPreview) Fragment.instantiate(context, obtainStyledAttributes.getString(0));
        fragmentWithPreview.enterPreviewMode(context);
        fragmentWithPreview.onInit((Bundle) null);
        View onCreateView = fragmentWithPreview.onCreateView(LayoutInflater.from(context), (ViewGroup) view, (Bundle) null);
        onCreateView.setId(obtainStyledAttributes.getInt(1, -1));
        return onCreateView;
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return onCreateView((View) null, str, context, attributeSet);
    }

    public BaseDragLayer getDragLayer() {
        throw new UnsupportedOperationException();
    }

    public DeviceProfile getDeviceProfile() {
        return this.mDp;
    }

    public Hotseat getHotseat() {
        return this.mHotseat;
    }

    public CellLayout getScreenWithId(int i) {
        return this.mWorkspaceScreens.get(Integer.valueOf(i));
    }

    private void inflateAndAddIcon(WorkspaceItemInfo workspaceItemInfo) {
        BubbleTextView bubbleTextView = (BubbleTextView) this.mHomeElementInflater.inflate(R.layout.app_icon, this.mWorkspaceScreens.get(Integer.valueOf(workspaceItemInfo.screenId)), false);
        bubbleTextView.applyFromWorkspaceItem(workspaceItemInfo);
        addInScreenFromBind(bubbleTextView, workspaceItemInfo);
    }

    private void inflateAndAddFolder(FolderInfo folderInfo) {
        ViewGroup viewGroup;
        if (folderInfo.container == -100) {
            viewGroup = this.mWorkspaceScreens.get(Integer.valueOf(folderInfo.screenId));
        } else {
            viewGroup = this.mHotseat;
        }
        addInScreenFromBind(FolderIcon.inflateIcon(R.layout.folder_icon, this, viewGroup, folderInfo), folderInfo);
    }

    private void inflateAndAddWidgets(LauncherAppWidgetInfo launcherAppWidgetInfo, Map<ComponentKey, AppWidgetProviderInfo> map) {
        AppWidgetProviderInfo appWidgetProviderInfo;
        if (map != null && (appWidgetProviderInfo = map.get(new ComponentKey(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user))) != null) {
            inflateAndAddWidgets(launcherAppWidgetInfo, LauncherAppWidgetProviderInfo.fromProviderInfo(getApplicationContext(), appWidgetProviderInfo));
        }
    }

    private void inflateAndAddWidgets(LauncherAppWidgetInfo launcherAppWidgetInfo, WidgetsModel widgetsModel) {
        WidgetItem widgetProviderInfoByProviderName = widgetsModel.getWidgetProviderInfoByProviderName(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
        if (widgetProviderInfoByProviderName != null) {
            inflateAndAddWidgets(launcherAppWidgetInfo, widgetProviderInfoByProviderName.widgetInfo);
        }
    }

    private void inflateAndAddWidgets(LauncherAppWidgetInfo launcherAppWidgetInfo, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo) {
        AppWidgetHostView appWidgetHostView;
        if (FeatureFlags.WIDGETS_IN_LAUNCHER_PREVIEW.get()) {
            appWidgetHostView = this.mAppWidgetHost.createView(this.mContext, launcherAppWidgetInfo.appWidgetId, launcherAppWidgetProviderInfo);
        } else {
            AnonymousClass3 r0 = new NavigableAppWidgetHostView(this) {
                /* access modifiers changed from: protected */
                public boolean shouldAllowDirectClick() {
                    return false;
                }
            };
            r0.setAppWidget(-1, launcherAppWidgetProviderInfo);
            r0.updateAppWidget((RemoteViews) null);
            appWidgetHostView = r0;
        }
        SparseIntArray sparseIntArray = this.mWallpaperColorResources;
        if (sparseIntArray != null) {
            appWidgetHostView.setColorResources(sparseIntArray);
        }
        appWidgetHostView.setTag(launcherAppWidgetInfo);
        addInScreenFromBind(appWidgetHostView, launcherAppWidgetInfo);
    }

    private void inflateAndAddPredictedIcon(WorkspaceItemInfo workspaceItemInfo) {
        View inflate = PredictedAppIconInflater.inflate(this.mHomeElementInflater, this.mWorkspaceScreens.get(Integer.valueOf(workspaceItemInfo.screenId)), workspaceItemInfo);
        if (inflate != null) {
            addInScreenFromBind(inflate, workspaceItemInfo);
        }
    }

    private void dispatchVisibilityAggregated(View view, boolean z) {
        boolean z2 = true;
        boolean z3 = view.getVisibility() == 0;
        if (z3 || !z) {
            view.onVisibilityAggregated(z);
        }
        if (view instanceof ViewGroup) {
            if (!z3 || !z) {
                z2 = false;
            }
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                dispatchVisibilityAggregated(viewGroup.getChildAt(i), z2);
            }
        }
    }

    private void populate(BgDataModel bgDataModel, Map<ComponentKey, AppWidgetProviderInfo> map) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        IntSet wrap = IntSet.wrap((Iterable<Integer>) this.mWorkspaceScreens.keySet());
        ModelUtils.filterCurrentWorkspaceItems(wrap, bgDataModel.workspaceItems, arrayList, arrayList2);
        ModelUtils.filterCurrentWorkspaceItems(wrap, bgDataModel.appWidgets, arrayList3, arrayList4);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ItemInfo itemInfo = (ItemInfo) it.next();
            int i = itemInfo.itemType;
            if (!(i == 0 || i == 1)) {
                if (i == 2) {
                    inflateAndAddFolder((FolderInfo) itemInfo);
                } else if (i != 6) {
                }
            }
            inflateAndAddIcon((WorkspaceItemInfo) itemInfo);
        }
        Iterator it2 = arrayList3.iterator();
        while (it2.hasNext()) {
            ItemInfo itemInfo2 = (ItemInfo) it2.next();
            int i2 = itemInfo2.itemType;
            if (i2 == 4 || i2 == 5) {
                if (map != null) {
                    inflateAndAddWidgets((LauncherAppWidgetInfo) itemInfo2, map);
                } else {
                    inflateAndAddWidgets((LauncherAppWidgetInfo) itemInfo2, bgDataModel.widgetsModel);
                }
            }
        }
        IntArray missingHotseatRanks = ModelUtils.getMissingHotseatRanks(arrayList, this.mDp.numShownHotseatIcons);
        BgDataModel.FixedContainerItems fixedContainerItems = (BgDataModel.FixedContainerItems) bgDataModel.extraItems.get(LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
        List<ItemInfo> emptyList = fixedContainerItems == null ? Collections.emptyList() : fixedContainerItems.items;
        int min = Math.min(missingHotseatRanks.size(), emptyList.size());
        for (int i3 = 0; i3 < min; i3++) {
            int i4 = missingHotseatRanks.get(i3);
            WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo((WorkspaceItemInfo) emptyList.get(i3));
            workspaceItemInfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION;
            workspaceItemInfo.rank = i4;
            workspaceItemInfo.cellX = this.mHotseat.getCellXFromOrder(i4);
            workspaceItemInfo.cellY = this.mHotseat.getCellYFromOrder(i4);
            workspaceItemInfo.screenId = i4;
            inflateAndAddPredictedIcon(workspaceItemInfo);
        }
        measureView(this.mRootView, this.mDp.widthPx, this.mDp.heightPx);
        dispatchVisibilityAggregated(this.mRootView, true);
        measureView(this.mRootView, this.mDp.widthPx, this.mDp.heightPx);
        measureView(this.mRootView, this.mDp.widthPx, this.mDp.heightPx);
    }

    private static void measureView(View view, int i, int i2) {
        view.measure(View.MeasureSpec.makeMeasureSpec(i, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(i2, BasicMeasure.EXACTLY));
        view.layout(0, 0, i, i2);
    }

    private class LauncherPreviewAppWidgetHost extends AppWidgetHost {
        private LauncherPreviewAppWidgetHost(Context context) {
            super(context, 1024);
        }

        /* access modifiers changed from: protected */
        public AppWidgetHostView onCreateView(Context context, int i, AppWidgetProviderInfo appWidgetProviderInfo) {
            return new LauncherPreviewAppWidgetHostView(LauncherPreviewRenderer.this);
        }
    }

    private static class LauncherPreviewAppWidgetHostView extends BaseLauncherAppWidgetHostView {
        /* access modifiers changed from: protected */
        public boolean shouldAllowDirectClick() {
            return false;
        }

        private LauncherPreviewAppWidgetHostView(Context context) {
            super(context);
        }
    }

    public static class LauncherPreviewLayout extends InsettableFrameLayout {
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return true;
        }

        public LauncherPreviewLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }
    }
}
