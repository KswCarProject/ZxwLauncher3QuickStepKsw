package com.android.launcher3.widget.custom;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Parcel;
import android.os.Process;
import android.util.SparseArray;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.SafeCloseable;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.systemui.plugins.CustomWidgetPlugin;
import com.android.systemui.plugins.PluginListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CustomWidgetManager implements PluginListener<CustomWidgetPlugin>, SafeCloseable {
    public static final MainThreadInitializedObject<CustomWidgetManager> INSTANCE = new MainThreadInitializedObject<>($$Lambda$CustomWidgetManager$KTvIvuJBygaYFI3nHoDo6nn7Fjg.INSTANCE);
    private int mAutoProviderId = 0;
    private final Context mContext;
    private final List<CustomAppWidgetProviderInfo> mCustomWidgets;
    private final SparseArray<CustomWidgetPlugin> mPlugins;
    private Consumer<PackageUserKey> mWidgetRefreshCallback;
    private final SparseArray<ComponentName> mWidgetsIdMap;

    public static /* synthetic */ CustomWidgetManager lambda$KTvIvuJBygaYFI3nHoDo6nn7Fjg(Context context) {
        return new CustomWidgetManager(context);
    }

    private CustomWidgetManager(Context context) {
        this.mContext = context;
        this.mPlugins = new SparseArray<>();
        this.mCustomWidgets = new ArrayList();
        this.mWidgetsIdMap = new SparseArray<>();
        PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).addPluginListener(this, CustomWidgetPlugin.class, true);
    }

    public void close() {
        PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).removePluginListener(this);
    }

    public void onPluginConnected(CustomWidgetPlugin customWidgetPlugin, Context context) {
        this.mPlugins.put(this.mAutoProviderId, customWidgetPlugin);
        List<AppWidgetProviderInfo> installedProvidersForProfile = AppWidgetManager.getInstance(context).getInstalledProvidersForProfile(Process.myUserHandle());
        if (!installedProvidersForProfile.isEmpty()) {
            Parcel obtain = Parcel.obtain();
            installedProvidersForProfile.get(0).writeToParcel(obtain, 0);
            obtain.setDataPosition(0);
            CustomAppWidgetProviderInfo newInfo = newInfo(this.mAutoProviderId, customWidgetPlugin, obtain, context);
            obtain.recycle();
            this.mCustomWidgets.add(newInfo);
            this.mWidgetsIdMap.put(this.mAutoProviderId, newInfo.provider);
            this.mWidgetRefreshCallback.accept((Object) null);
            this.mAutoProviderId++;
        }
    }

    public void onPluginDisconnected(CustomWidgetPlugin customWidgetPlugin) {
        int findProviderId = findProviderId(customWidgetPlugin);
        if (findProviderId != -1) {
            this.mPlugins.remove(findProviderId);
            this.mCustomWidgets.remove(getWidgetProvider(findProviderId));
            this.mWidgetsIdMap.remove(findProviderId);
        }
    }

    public void setWidgetRefreshCallback(Consumer<PackageUserKey> consumer) {
        this.mWidgetRefreshCallback = consumer;
    }

    public void onViewCreated(LauncherAppWidgetHostView launcherAppWidgetHostView) {
        CustomWidgetPlugin customWidgetPlugin = this.mPlugins.get(((CustomAppWidgetProviderInfo) launcherAppWidgetHostView.getAppWidgetInfo()).providerId);
        if (customWidgetPlugin != null) {
            customWidgetPlugin.onViewCreated(launcherAppWidgetHostView);
        }
    }

    public Stream<CustomAppWidgetProviderInfo> stream() {
        return this.mCustomWidgets.stream();
    }

    public int getWidgetIdForCustomProvider(ComponentName componentName) {
        int indexOfValue = this.mWidgetsIdMap.indexOfValue(componentName);
        if (indexOfValue >= 0) {
            return -100 - this.mWidgetsIdMap.keyAt(indexOfValue);
        }
        return 0;
    }

    public LauncherAppWidgetProviderInfo getWidgetProvider(int i) {
        ComponentName componentName = this.mWidgetsIdMap.get(-100 - i);
        for (LauncherAppWidgetProviderInfo next : this.mCustomWidgets) {
            if (next.provider.equals(componentName)) {
                return next;
            }
        }
        return null;
    }

    private static CustomAppWidgetProviderInfo newInfo(int i, CustomWidgetPlugin customWidgetPlugin, Parcel parcel, Context context) {
        CustomAppWidgetProviderInfo customAppWidgetProviderInfo = new CustomAppWidgetProviderInfo(parcel, false, i);
        customAppWidgetProviderInfo.provider = new ComponentName(context.getPackageName(), LauncherAppWidgetProviderInfo.CLS_CUSTOM_WIDGET_PREFIX + i);
        customAppWidgetProviderInfo.label = customWidgetPlugin.getLabel();
        customAppWidgetProviderInfo.resizeMode = customWidgetPlugin.getResizeMode();
        customAppWidgetProviderInfo.spanX = customWidgetPlugin.getSpanX();
        customAppWidgetProviderInfo.spanY = customWidgetPlugin.getSpanY();
        customAppWidgetProviderInfo.minSpanX = customWidgetPlugin.getMinSpanX();
        customAppWidgetProviderInfo.minSpanY = customWidgetPlugin.getMinSpanY();
        return customAppWidgetProviderInfo;
    }

    private int findProviderId(CustomWidgetPlugin customWidgetPlugin) {
        for (int i = 0; i < this.mPlugins.size(); i++) {
            int keyAt = this.mPlugins.keyAt(i);
            if (this.mPlugins.get(keyAt) == customWidgetPlugin) {
                return keyAt;
            }
        }
        return -1;
    }
}
