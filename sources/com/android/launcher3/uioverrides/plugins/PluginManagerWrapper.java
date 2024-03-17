package com.android.launcher3.uioverrides.plugins;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginActionManager;
import com.android.systemui.shared.plugins.PluginInstance;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.plugins.PluginManagerImpl;
import com.android.systemui.shared.plugins.PluginPrefs;
import com.android.systemui.shared.system.UncaughtExceptionPreHandlerManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PluginManagerWrapper {
    public static final MainThreadInitializedObject<PluginManagerWrapper> INSTANCE = new MainThreadInitializedObject<>($$Lambda$PluginManagerWrapper$wcrwVKrOKSKOcLjAzNnTL7QDkFk.INSTANCE);
    public static final String PLUGIN_CHANGED = "com.android.systemui.action.PLUGIN_CHANGED";
    private static final UncaughtExceptionPreHandlerManager UNCAUGHT_EXCEPTION_PRE_HANDLER_MANAGER = new UncaughtExceptionPreHandlerManager();
    private final Context mContext;
    private final PluginEnablerImpl mPluginEnabler;
    private final PluginManager mPluginManager;

    public static /* synthetic */ PluginManagerWrapper lambda$wcrwVKrOKSKOcLjAzNnTL7QDkFk(Context context) {
        return new PluginManagerWrapper(context);
    }

    private PluginManagerWrapper(Context context) {
        this.mContext = context;
        PluginEnablerImpl pluginEnablerImpl = new PluginEnablerImpl(context);
        this.mPluginEnabler = pluginEnablerImpl;
        List emptyList = Collections.emptyList();
        Context context2 = context;
        List list = emptyList;
        this.mPluginManager = new PluginManagerImpl(context2, new PluginActionManager.Factory(context2, context.getPackageManager(), context.getMainExecutor(), Executors.MODEL_EXECUTOR, (NotificationManager) context.getSystemService(NotificationManager.class), pluginEnablerImpl, list, new PluginInstance.Factory(getClass().getClassLoader(), new PluginInstance.InstanceFactory(), new PluginInstance.VersionChecker(), emptyList, Utilities.IS_DEBUG_DEVICE)), Utilities.IS_DEBUG_DEVICE, UNCAUGHT_EXCEPTION_PRE_HANDLER_MANAGER, pluginEnablerImpl, new PluginPrefs(context), list);
    }

    public PluginEnablerImpl getPluginEnabler() {
        return this.mPluginEnabler;
    }

    public <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<T> cls) {
        addPluginListener(pluginListener, cls, false);
    }

    public <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<T> cls, boolean z) {
        this.mPluginManager.addPluginListener(pluginListener, cls, z);
    }

    public void removePluginListener(PluginListener<? extends Plugin> pluginListener) {
        this.mPluginManager.removePluginListener(pluginListener);
    }

    public Set<String> getPluginActions() {
        return new PluginPrefs(this.mContext).getPluginList();
    }

    public static String pluginEnabledKey(ComponentName componentName) {
        return PluginEnablerImpl.pluginEnabledKey(componentName);
    }

    public static boolean hasPlugins(Context context) {
        return PluginPrefs.hasPlugins(context);
    }

    public void dump(PrintWriter printWriter) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (String intent : getPluginActions()) {
            for (ResolveInfo next : this.mContext.getPackageManager().queryIntentServices(new Intent(intent), 512)) {
                ComponentName componentName = new ComponentName(next.serviceInfo.packageName, next.serviceInfo.name);
                if (this.mPluginEnabler.isEnabled(componentName)) {
                    arrayList.add(componentName);
                } else {
                    arrayList2.add(componentName);
                }
            }
        }
        printWriter.println("PluginManager:");
        printWriter.println("  numEnabledPlugins=" + arrayList.size());
        printWriter.println("  numDisabledPlugins=" + arrayList2.size());
        printWriter.println("  enabledPlugins=" + arrayList);
        printWriter.println("  disabledPlugins=" + arrayList2);
    }
}
