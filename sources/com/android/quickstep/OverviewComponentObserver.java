package com.android.quickstep;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.SparseIntArray;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.tracing.OverviewComponentObserverProto;
import com.android.launcher3.tracing.TouchInteractionServiceProto;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.SimpleBroadcastReceiver;
import com.android.systemui.shared.system.PackageManagerWrapper;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public final class OverviewComponentObserver {
    private BaseActivityInterface mActivityInterface;
    private final SparseIntArray mConfigChangesMap;
    private final Context mContext;
    private final Intent mCurrentHomeIntent;
    private final RecentsAnimationDeviceState mDeviceState;
    private final Intent mFallbackIntent;
    private boolean mIsDefaultHome;
    private boolean mIsHomeAndOverviewSame;
    private boolean mIsHomeDisabled;
    private final Intent mMyHomeIntent;
    private final BroadcastReceiver mOtherHomeAppUpdateReceiver = new SimpleBroadcastReceiver(new Consumer() {
        public final void accept(Object obj) {
            OverviewComponentObserver.this.updateOverviewTargets((Intent) obj);
        }
    });
    private Consumer<Boolean> mOverviewChangeListener;
    private Intent mOverviewIntent;
    private String mUpdateRegisteredPackage;
    private final BroadcastReceiver mUserPreferenceChangeReceiver = new SimpleBroadcastReceiver(new Consumer() {
        public final void accept(Object obj) {
            OverviewComponentObserver.this.updateOverviewTargets((Intent) obj);
        }
    });

    static /* synthetic */ void lambda$new$0(Boolean bool) {
    }

    public OverviewComponentObserver(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState) {
        SparseIntArray sparseIntArray = new SparseIntArray();
        this.mConfigChangesMap = sparseIntArray;
        this.mOverviewChangeListener = $$Lambda$OverviewComponentObserver$hExlPGd0GMpQayNe2n8V6Ff14.INSTANCE;
        this.mContext = context;
        this.mDeviceState = recentsAnimationDeviceState;
        Intent createHomeIntent = Utilities.createHomeIntent();
        this.mCurrentHomeIntent = createHomeIntent;
        Intent intent = new Intent(createHomeIntent).setPackage(context.getPackageName());
        this.mMyHomeIntent = intent;
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 0);
        ComponentName componentName = new ComponentName(context.getPackageName(), resolveActivity.activityInfo.name);
        intent.setComponent(componentName);
        sparseIntArray.append(componentName.hashCode(), resolveActivity.activityInfo.configChanges);
        ComponentName componentName2 = new ComponentName(context, RecentsActivity.class);
        Intent flags = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.DEFAULT").setComponent(componentName2).setFlags(268435456);
        this.mFallbackIntent = flags;
        try {
            sparseIntArray.append(componentName2.hashCode(), context.getPackageManager().getActivityInfo(flags.getComponent(), 0).configChanges);
        } catch (PackageManager.NameNotFoundException unused) {
        }
        this.mContext.registerReceiver(this.mUserPreferenceChangeReceiver, new IntentFilter(PackageManagerWrapper.ACTION_PREFERRED_ACTIVITY_CHANGED));
        updateOverviewTargets();
    }

    public void setOverviewChangeListener(Consumer<Boolean> consumer) {
        this.mOverviewChangeListener = consumer;
    }

    public void onSystemUiStateChanged() {
        if (this.mDeviceState.isHomeDisabled() != this.mIsHomeDisabled) {
            updateOverviewTargets();
        }
        if (this.mDeviceState.isOneHandedModeEnabled()) {
            this.mActivityInterface.onOneHandedModeStateChanged(this.mDeviceState.isOneHandedModeActive());
        }
    }

    /* access modifiers changed from: private */
    public void updateOverviewTargets(Intent intent) {
        updateOverviewTargets();
    }

    private void updateOverviewTargets() {
        ComponentName homeActivities = PackageManagerWrapper.getInstance().getHomeActivities(new ArrayList());
        this.mIsHomeDisabled = this.mDeviceState.isHomeDisabled();
        this.mIsDefaultHome = Objects.equals(this.mMyHomeIntent.getComponent(), homeActivities);
        BaseActivityInterface baseActivityInterface = this.mActivityInterface;
        if (baseActivityInterface != null) {
            baseActivityInterface.onAssistantVisibilityChanged(0.0f);
        }
        if (FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get()) {
            this.mIsDefaultHome = false;
            if (homeActivities == null) {
                homeActivities = this.mMyHomeIntent.getComponent();
            }
        }
        if (this.mDeviceState.isHomeDisabled() || (homeActivities != null && !this.mIsDefaultHome)) {
            this.mActivityInterface = FallbackActivityInterface.INSTANCE;
            this.mIsHomeAndOverviewSame = false;
            this.mOverviewIntent = this.mFallbackIntent;
            this.mCurrentHomeIntent.setComponent(homeActivities);
            if (homeActivities == null) {
                unregisterOtherHomeAppUpdateReceiver();
            } else if (!homeActivities.getPackageName().equals(this.mUpdateRegisteredPackage)) {
                unregisterOtherHomeAppUpdateReceiver();
                String packageName = homeActivities.getPackageName();
                this.mUpdateRegisteredPackage = packageName;
                this.mContext.registerReceiver(this.mOtherHomeAppUpdateReceiver, PackageManagerHelper.getPackageFilter(packageName, "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED"));
            }
        } else {
            this.mActivityInterface = LauncherActivityInterface.INSTANCE;
            this.mIsHomeAndOverviewSame = true;
            Intent intent = this.mMyHomeIntent;
            this.mOverviewIntent = intent;
            this.mCurrentHomeIntent.setComponent(intent.getComponent());
            unregisterOtherHomeAppUpdateReceiver();
        }
        this.mOverviewChangeListener.accept(Boolean.valueOf(this.mIsHomeAndOverviewSame));
    }

    public void onDestroy() {
        this.mContext.unregisterReceiver(this.mUserPreferenceChangeReceiver);
        unregisterOtherHomeAppUpdateReceiver();
    }

    private void unregisterOtherHomeAppUpdateReceiver() {
        if (this.mUpdateRegisteredPackage != null) {
            this.mContext.unregisterReceiver(this.mOtherHomeAppUpdateReceiver);
            this.mUpdateRegisteredPackage = null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean canHandleConfigChanges(ComponentName componentName, int i) {
        if ((i & 1152) == 1152) {
            return true;
        }
        int i2 = this.mConfigChangesMap.get(componentName.hashCode());
        return i2 != 0 && ((~i2) & i) == 0;
    }

    /* access modifiers changed from: package-private */
    public Intent getOverviewIntentIgnoreSysUiState() {
        return this.mIsDefaultHome ? this.mMyHomeIntent : this.mOverviewIntent;
    }

    public Intent getOverviewIntent() {
        return this.mOverviewIntent;
    }

    public Intent getHomeIntent() {
        return this.mCurrentHomeIntent;
    }

    public boolean isHomeAndOverviewSame() {
        return this.mIsHomeAndOverviewSame;
    }

    public BaseActivityInterface getActivityInterface() {
        return this.mActivityInterface;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("OverviewComponentObserver:");
        printWriter.println("  isDefaultHome=" + this.mIsDefaultHome);
        printWriter.println("  isHomeDisabled=" + this.mIsHomeDisabled);
        printWriter.println("  homeAndOverviewSame=" + this.mIsHomeAndOverviewSame);
        printWriter.println("  overviewIntent=" + this.mOverviewIntent);
        printWriter.println("  homeIntent=" + this.mCurrentHomeIntent);
    }

    public void writeToProto(TouchInteractionServiceProto.Builder builder) {
        OverviewComponentObserverProto.Builder newBuilder = OverviewComponentObserverProto.newBuilder();
        newBuilder.setOverviewActivityStarted(this.mActivityInterface.isStarted());
        newBuilder.setOverviewActivityResumed(this.mActivityInterface.isResumed());
        builder.setOverviewComponentObvserver(newBuilder);
    }
}
