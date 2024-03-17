package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.icons.IconProvider;
import com.android.launcher3.icons.LauncherIconProvider;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.pm.InstallSessionHelper;
import com.android.launcher3.pm.InstallSessionTracker;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.SafeCloseable;
import com.android.launcher3.util.SettingsCache;
import com.android.launcher3.util.SimpleBroadcastReceiver;
import com.android.launcher3.util.Themes;
import com.android.launcher3.widget.custom.CustomWidgetManager;
import java.util.Objects;
import java.util.function.Consumer;

public class LauncherAppState implements SafeCloseable {
    public static final String ACTION_FORCE_ROLOAD = "force-reload-launcher";
    public static final MainThreadInitializedObject<LauncherAppState> INSTANCE = new MainThreadInitializedObject<>($$Lambda$3TAXPdFdOH48UabGt6bxpPq6E.INSTANCE);
    private static final String KEY_ICON_STATE = "pref_icon_shape_path";
    /* access modifiers changed from: private */
    public final Context mContext;
    private final IconCache mIconCache;
    /* access modifiers changed from: private */
    public final LauncherIconProvider mIconProvider;
    private final InvariantDeviceProfile mInvariantDeviceProfile;
    /* access modifiers changed from: private */
    public final LauncherModel mModel;
    private final RunnableList mOnTerminateCallback;

    public static LauncherAppState getInstance(Context context) {
        return INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
    }

    public static LauncherAppState getInstanceNoCreate() {
        return INSTANCE.getNoCreate();
    }

    public Context getContext() {
        return this.mContext;
    }

    public LauncherAppState(Context context) {
        this(context, LauncherFiles.APP_ICONS_DB);
        Log.v(Launcher.TAG, "LauncherAppState initiated zxw");
        Preconditions.assertUIThread();
        this.mInvariantDeviceProfile.addOnChangeListener(new InvariantDeviceProfile.OnIDPChangeListener() {
            public final void onIdpChanged(boolean z) {
                LauncherAppState.this.lambda$new$0$LauncherAppState(z);
            }
        });
        ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).registerCallback(this.mModel);
        LauncherModel launcherModel = this.mModel;
        Objects.requireNonNull(launcherModel);
        SimpleBroadcastReceiver simpleBroadcastReceiver = new SimpleBroadcastReceiver(new Consumer() {
            public final void accept(Object obj) {
                LauncherModel.this.onBroadcastIntent((Intent) obj);
            }
        });
        simpleBroadcastReceiver.register(this.mContext, "android.intent.action.LOCALE_CHANGED", "android.intent.action.MANAGED_PROFILE_AVAILABLE", "android.intent.action.MANAGED_PROFILE_UNAVAILABLE", "android.intent.action.MANAGED_PROFILE_UNLOCKED", "android.app.action.DEVICE_POLICY_RESOURCE_UPDATED");
        this.mOnTerminateCallback.add(new Runnable(simpleBroadcastReceiver) {
            public final /* synthetic */ SimpleBroadcastReceiver f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LauncherAppState.this.lambda$new$1$LauncherAppState(this.f$1);
            }
        });
        LauncherModel launcherModel2 = this.mModel;
        Objects.requireNonNull(launcherModel2);
        CustomWidgetManager.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).setWidgetRefreshCallback(new Consumer() {
            public final void accept(Object obj) {
                LauncherModel.this.refreshAndBindWidgetsAndShortcuts((PackageUserKey) obj);
            }
        });
        LauncherModel launcherModel3 = this.mModel;
        Objects.requireNonNull(launcherModel3);
        SafeCloseable addUserChangeListener = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).addUserChangeListener(new Runnable() {
            public final void run() {
                LauncherModel.this.forceReload();
            }
        });
        RunnableList runnableList = this.mOnTerminateCallback;
        Objects.requireNonNull(addUserChangeListener);
        runnableList.add(new Runnable() {
            public final void run() {
                SafeCloseable.this.close();
            }
        });
        IconObserver iconObserver = new IconObserver();
        SafeCloseable registerIconChangeListener = this.mIconProvider.registerIconChangeListener(iconObserver, Executors.MODEL_EXECUTOR.getHandler());
        RunnableList runnableList2 = this.mOnTerminateCallback;
        Objects.requireNonNull(registerIconChangeListener);
        runnableList2.add(new Runnable() {
            public final void run() {
                SafeCloseable.this.close();
            }
        });
        LooperExecutor looperExecutor = Executors.MODEL_EXECUTOR;
        Objects.requireNonNull(iconObserver);
        looperExecutor.execute(new Runnable() {
            public final void run() {
                LauncherAppState.IconObserver.this.verifyIconChanged();
            }
        });
        if (FeatureFlags.ENABLE_THEMED_ICONS.get()) {
            SharedPreferences prefs = Utilities.getPrefs(this.mContext);
            prefs.registerOnSharedPreferenceChangeListener(iconObserver);
            this.mOnTerminateCallback.add(new Runnable(prefs, iconObserver) {
                public final /* synthetic */ SharedPreferences f$0;
                public final /* synthetic */ LauncherAppState.IconObserver f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    this.f$0.unregisterOnSharedPreferenceChangeListener(this.f$1);
                }
            });
        }
        InstallSessionTracker registerInstallTracker = InstallSessionHelper.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).registerInstallTracker(this.mModel);
        RunnableList runnableList3 = this.mOnTerminateCallback;
        Objects.requireNonNull(registerInstallTracker);
        runnableList3.add(new Runnable() {
            public final void run() {
                InstallSessionTracker.this.unregister();
            }
        });
        SettingsCache settingsCache = SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext);
        $$Lambda$LauncherAppState$igD78ILRoBqqLUxNDL79OeU1_bE r0 = new SettingsCache.OnChangeListener() {
            public final void onSettingsChanged(boolean z) {
                LauncherAppState.this.onNotificationSettingsChanged(z);
            }
        };
        settingsCache.register(SettingsCache.NOTIFICATION_BADGING_URI, r0);
        onNotificationSettingsChanged(settingsCache.getValue(SettingsCache.NOTIFICATION_BADGING_URI));
        this.mOnTerminateCallback.add(new Runnable(r0) {
            public final /* synthetic */ SettingsCache.OnChangeListener f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SettingsCache.this.unregister(SettingsCache.NOTIFICATION_BADGING_URI, this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$LauncherAppState(boolean z) {
        if (z) {
            refreshAndReloadLauncher();
        }
    }

    public /* synthetic */ void lambda$new$1$LauncherAppState(SimpleBroadcastReceiver simpleBroadcastReceiver) {
        this.mContext.unregisterReceiver(simpleBroadcastReceiver);
    }

    public LauncherAppState(Context context, String str) {
        RunnableList runnableList = new RunnableList();
        this.mOnTerminateCallback = runnableList;
        this.mContext = context;
        InvariantDeviceProfile invariantDeviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        this.mInvariantDeviceProfile = invariantDeviceProfile;
        LauncherIconProvider launcherIconProvider = new LauncherIconProvider(context);
        this.mIconProvider = launcherIconProvider;
        IconCache iconCache = new IconCache(context, invariantDeviceProfile, str, launcherIconProvider);
        this.mIconCache = iconCache;
        this.mModel = new LauncherModel(context, this, iconCache, new AppFilter(context), str != null);
        Objects.requireNonNull(iconCache);
        runnableList.add(new Runnable() {
            public final void run() {
                IconCache.this.close();
            }
        });
    }

    /* access modifiers changed from: private */
    public void onNotificationSettingsChanged(boolean z) {
        if (z) {
            NotificationListener.requestRebind(new ComponentName(this.mContext, NotificationListener.class));
        }
    }

    /* access modifiers changed from: private */
    public void refreshAndReloadLauncher() {
        LauncherIcons.clearPool();
        this.mIconCache.updateIconParams(this.mInvariantDeviceProfile.fillResIconDpi, this.mInvariantDeviceProfile.iconBitmapSize);
        this.mModel.forceReload();
    }

    public void close() {
        this.mModel.destroy();
        ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).unregisterCallback(this.mModel);
        CustomWidgetManager.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).setWidgetRefreshCallback((Consumer<PackageUserKey>) null);
        this.mOnTerminateCallback.executeAllAndDestroy();
    }

    public IconProvider getIconProvider() {
        return this.mIconProvider;
    }

    public IconCache getIconCache() {
        return this.mIconCache;
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    public InvariantDeviceProfile getInvariantDeviceProfile() {
        return this.mInvariantDeviceProfile;
    }

    public static InvariantDeviceProfile getIDP(Context context) {
        return InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
    }

    private class IconObserver implements IconProvider.IconChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
        private IconObserver() {
        }

        public void onAppIconChanged(String str, UserHandle userHandle) {
            LauncherAppState.this.mModel.onAppIconChanged(str, userHandle);
        }

        public void onSystemIconStateChanged(String str) {
            IconShape.init(LauncherAppState.this.mContext);
            LauncherAppState.this.refreshAndReloadLauncher();
            Utilities.getDevicePrefs(LauncherAppState.this.mContext).edit().putString(LauncherAppState.KEY_ICON_STATE, str).apply();
        }

        /* access modifiers changed from: package-private */
        public void verifyIconChanged() {
            String systemIconState = LauncherAppState.this.mIconProvider.getSystemIconState();
            if (!systemIconState.equals(Utilities.getDevicePrefs(LauncherAppState.this.mContext).getString(LauncherAppState.KEY_ICON_STATE, ""))) {
                onSystemIconStateChanged(systemIconState);
            }
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            if (Themes.KEY_THEMED_ICONS.equals(str)) {
                LauncherAppState.this.mIconProvider.setIconThemeSupported(Themes.isThemedIconEnabled(LauncherAppState.this.mContext));
                verifyIconChanged();
            }
        }
    }
}
