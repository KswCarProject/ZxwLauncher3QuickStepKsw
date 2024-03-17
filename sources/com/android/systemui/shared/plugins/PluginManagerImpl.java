package com.android.systemui.shared.plugins;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginActionManager;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.UncaughtExceptionPreHandlerManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.Thread;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PluginManagerImpl extends BroadcastReceiver implements PluginManager {
    static final String DISABLE_PLUGIN = "com.android.systemui.action.DISABLE_PLUGIN";
    private static final String TAG = "PluginManagerImpl";
    private final PluginActionManager.Factory mActionManagerFactory;
    private final Map<String, ClassLoader> mClassLoaders = new ArrayMap();
    private final Context mContext;
    private final boolean mIsDebuggable;
    private boolean mListening;
    private final PluginEnabler mPluginEnabler;
    /* access modifiers changed from: private */
    public final ArrayMap<PluginListener<?>, PluginActionManager<?>> mPluginMap = new ArrayMap<>();
    private final PluginPrefs mPluginPrefs;
    private final ArraySet<String> mPrivilegedPlugins;

    public PluginManagerImpl(Context context, PluginActionManager.Factory factory, boolean z, UncaughtExceptionPreHandlerManager uncaughtExceptionPreHandlerManager, PluginEnabler pluginEnabler, PluginPrefs pluginPrefs, List<String> list) {
        ArraySet<String> arraySet = new ArraySet<>();
        this.mPrivilegedPlugins = arraySet;
        this.mContext = context;
        this.mActionManagerFactory = factory;
        this.mIsDebuggable = z;
        arraySet.addAll(list);
        this.mPluginPrefs = pluginPrefs;
        this.mPluginEnabler = pluginEnabler;
        uncaughtExceptionPreHandlerManager.registerHandler(new PluginExceptionHandler());
    }

    public boolean isDebuggable() {
        return this.mIsDebuggable;
    }

    public String[] getPrivilegedPlugins() {
        return (String[]) this.mPrivilegedPlugins.toArray(new String[0]);
    }

    public <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<T> cls) {
        addPluginListener(pluginListener, cls, false);
    }

    public <T extends Plugin> void addPluginListener(PluginListener<T> pluginListener, Class<T> cls, boolean z) {
        addPluginListener(PluginManager.Helper.getAction(cls), pluginListener, cls, z);
    }

    public <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class<T> cls) {
        addPluginListener(str, pluginListener, cls, false);
    }

    public <T extends Plugin> void addPluginListener(String str, PluginListener<T> pluginListener, Class<T> cls, boolean z) {
        this.mPluginPrefs.addAction(str);
        PluginActionManager<T> create = this.mActionManagerFactory.create(str, pluginListener, cls, z, isDebuggable());
        create.loadAll();
        synchronized (this) {
            this.mPluginMap.put(pluginListener, create);
        }
        startListening();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0022, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removePluginListener(com.android.systemui.plugins.PluginListener<?> r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            android.util.ArrayMap<com.android.systemui.plugins.PluginListener<?>, com.android.systemui.shared.plugins.PluginActionManager<?>> r0 = r1.mPluginMap     // Catch:{ all -> 0x0023 }
            boolean r0 = r0.containsKey(r2)     // Catch:{ all -> 0x0023 }
            if (r0 != 0) goto L_0x000b
            monitor-exit(r1)     // Catch:{ all -> 0x0023 }
            return
        L_0x000b:
            android.util.ArrayMap<com.android.systemui.plugins.PluginListener<?>, com.android.systemui.shared.plugins.PluginActionManager<?>> r0 = r1.mPluginMap     // Catch:{ all -> 0x0023 }
            java.lang.Object r2 = r0.remove(r2)     // Catch:{ all -> 0x0023 }
            com.android.systemui.shared.plugins.PluginActionManager r2 = (com.android.systemui.shared.plugins.PluginActionManager) r2     // Catch:{ all -> 0x0023 }
            r2.destroy()     // Catch:{ all -> 0x0023 }
            android.util.ArrayMap<com.android.systemui.plugins.PluginListener<?>, com.android.systemui.shared.plugins.PluginActionManager<?>> r2 = r1.mPluginMap     // Catch:{ all -> 0x0023 }
            int r2 = r2.size()     // Catch:{ all -> 0x0023 }
            if (r2 != 0) goto L_0x0021
            r1.stopListening()     // Catch:{ all -> 0x0023 }
        L_0x0021:
            monitor-exit(r1)     // Catch:{ all -> 0x0023 }
            return
        L_0x0023:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0023 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.shared.plugins.PluginManagerImpl.removePluginListener(com.android.systemui.plugins.PluginListener):void");
    }

    private void startListening() {
        if (!this.mListening) {
            this.mListening = true;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addDataScheme("package");
            this.mContext.registerReceiver(this, intentFilter);
            intentFilter.addAction("com.android.systemui.action.PLUGIN_CHANGED");
            intentFilter.addAction(DISABLE_PLUGIN);
            intentFilter.addDataScheme("package");
            this.mContext.registerReceiver(this, intentFilter, PluginActionManager.PLUGIN_PERMISSION, (Handler) null, 2);
            this.mContext.registerReceiver(this, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        }
    }

    private void stopListening() {
        if (this.mListening) {
            this.mListening = false;
            this.mContext.unregisterReceiver(this);
        }
    }

    public void onReceive(Context context, Intent intent) {
        int disableReason;
        if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
            synchronized (this) {
                for (PluginActionManager<?> loadAll : this.mPluginMap.values()) {
                    loadAll.loadAll();
                }
            }
        } else if (DISABLE_PLUGIN.equals(intent.getAction())) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(intent.getData().toString().substring(10));
            if (!isPluginPrivileged(unflattenFromString)) {
                this.mPluginEnabler.setDisabled(unflattenFromString, 2);
                ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).cancel(unflattenFromString.getClassName(), 6);
            }
        } else {
            String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
            ComponentName unflattenFromString2 = ComponentName.unflattenFromString(encodedSchemeSpecificPart);
            if (clearClassLoader(encodedSchemeSpecificPart)) {
                if (Build.IS_ENG) {
                    Toast.makeText(this.mContext, "Reloading " + encodedSchemeSpecificPart, 1).show();
                } else {
                    Log.v(TAG, "Reloading " + encodedSchemeSpecificPart);
                }
            }
            if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction()) && unflattenFromString2 != null && ((disableReason = this.mPluginEnabler.getDisableReason(unflattenFromString2)) == 3 || disableReason == 4 || disableReason == 2)) {
                Log.i(TAG, "Re-enabling previously disabled plugin that has been updated: " + unflattenFromString2.flattenToShortString());
                this.mPluginEnabler.setEnabled(unflattenFromString2);
            }
            synchronized (this) {
                if (!"android.intent.action.PACKAGE_ADDED".equals(intent.getAction()) && !"android.intent.action.PACKAGE_CHANGED".equals(intent.getAction())) {
                    if (!"android.intent.action.PACKAGE_REPLACED".equals(intent.getAction())) {
                        for (PluginActionManager<?> onPackageRemoved : this.mPluginMap.values()) {
                            onPackageRemoved.onPackageRemoved(encodedSchemeSpecificPart);
                        }
                    }
                }
                for (PluginActionManager<?> reloadPackage : this.mPluginMap.values()) {
                    reloadPackage.reloadPackage(encodedSchemeSpecificPart);
                }
            }
        }
    }

    private boolean clearClassLoader(String str) {
        return this.mClassLoaders.remove(str) != null;
    }

    public <T> boolean dependsOn(Plugin plugin, Class<T> cls) {
        synchronized (this) {
            for (int i = 0; i < this.mPluginMap.size(); i++) {
                if (this.mPluginMap.valueAt(i).dependsOn(plugin, cls)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this) {
            printWriter.println(String.format("  plugin map (%d):", new Object[]{Integer.valueOf(this.mPluginMap.size())}));
            for (PluginListener next : this.mPluginMap.keySet()) {
                printWriter.println(String.format("    %s -> %s", new Object[]{next, this.mPluginMap.get(next)}));
            }
        }
    }

    private boolean isPluginPrivileged(ComponentName componentName) {
        Iterator<String> it = this.mPrivilegedPlugins.iterator();
        while (it.hasNext()) {
            String next = it.next();
            ComponentName unflattenFromString = ComponentName.unflattenFromString(next);
            if (unflattenFromString != null) {
                if (unflattenFromString.equals(componentName)) {
                    return true;
                }
            } else if (next.equals(componentName.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    static class ClassLoaderFilter extends ClassLoader {
        private final ClassLoader mBase;
        private final String mPackage;

        public ClassLoaderFilter(ClassLoader classLoader, String str) {
            super(ClassLoader.getSystemClassLoader());
            this.mBase = classLoader;
            this.mPackage = str;
        }

        /* access modifiers changed from: protected */
        public Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
            if (!str.startsWith(this.mPackage)) {
                super.loadClass(str, z);
            }
            return this.mBase.loadClass(str);
        }
    }

    private class PluginExceptionHandler implements Thread.UncaughtExceptionHandler {
        private PluginExceptionHandler() {
        }

        public void uncaughtException(Thread thread, Throwable th) {
            if (!SystemProperties.getBoolean("plugin.debugging", false)) {
                boolean checkStack = checkStack(th);
                if (!checkStack) {
                    synchronized (this) {
                        for (PluginActionManager disableAll : PluginManagerImpl.this.mPluginMap.values()) {
                            checkStack |= disableAll.disableAll();
                        }
                    }
                }
                if (checkStack) {
                    new CrashWhilePluginActiveException(th);
                }
            }
        }

        private boolean checkStack(Throwable th) {
            boolean z;
            if (th == null) {
                return false;
            }
            synchronized (this) {
                z = false;
                for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                    for (PluginActionManager checkAndDisable : PluginManagerImpl.this.mPluginMap.values()) {
                        z |= checkAndDisable.checkAndDisable(stackTraceElement.getClassName());
                    }
                }
            }
            return checkStack(th.getCause()) | z;
        }
    }

    public static class CrashWhilePluginActiveException extends RuntimeException {
        public CrashWhilePluginActiveException(Throwable th) {
            super(th);
        }
    }
}
