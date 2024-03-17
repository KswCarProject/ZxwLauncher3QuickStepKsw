package com.android.launcher3.model;

import android.app.RemoteAction;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherApps;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.BgObjectWithLooper;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.SimpleBroadcastReceiver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class WellbeingModel extends BgObjectWithLooper {
    private static final String DB_NAME_MINIMAL_DEVICE = "minimal.db";
    private static final boolean DEBUG = false;
    private static final String EXTRA_ACTION = "action";
    private static final String EXTRA_ACTIONS = "actions";
    private static final String EXTRA_MAX_NUM_ACTIONS_SHOWN = "max_num_actions_shown";
    private static final String EXTRA_MINIMAL_DEVICE_STATE = "minimal_device_state";
    private static final String EXTRA_PACKAGES = "packages";
    private static final String EXTRA_SUCCESS = "success";
    public static final MainThreadInitializedObject<WellbeingModel> INSTANCE = new MainThreadInitializedObject<>($$Lambda$WellbeingModel$NamGAZkV_nPfIPATzPfJ7OZ7BRk.INSTANCE);
    private static final int IN_MINIMAL_DEVICE = 2;
    private static final String METHOD_GET_ACTIONS = "get_actions";
    private static final String METHOD_GET_MINIMAL_DEVICE_CONFIG = "get_minimal_device_config";
    private static final String PATH_ACTIONS = "actions";
    private static final String PATH_MINIMAL_DEVICE = "minimal_device";
    private static final int[] RETRY_TIMES_MS = {5000, 15000, 30000};
    public static final SystemShortcut.Factory<BaseDraggingActivity> SHORTCUT_FACTORY = $$Lambda$WellbeingModel$eyFStYSW0FEHleKvzx4LxFaof6s.INSTANCE;
    private static final String TAG = "WellbeingModel";
    private static final int UNKNOWN_MINIMAL_DEVICE_STATE = 0;
    private final Map<String, RemoteAction> mActionIdMap = new ArrayMap();
    private ContentObserver mContentObserver;
    private final Context mContext;
    private boolean mIsInTest;
    private final Object mModelLock = new Object();
    private final Map<String, String> mPackageToActionId = new HashMap();
    private final String mWellbeingProviderPkg;
    private Handler mWorkerHandler;

    public static /* synthetic */ WellbeingModel lambda$NamGAZkV_nPfIPATzPfJ7OZ7BRk(Context context) {
        return new WellbeingModel(context);
    }

    private WellbeingModel(Context context) {
        this.mContext = context;
        this.mWellbeingProviderPkg = context.getString(R.string.wellbeing_provider_pkg);
        initializeInBackground("WellbeingHandler");
    }

    /* access modifiers changed from: protected */
    public void onInitialized(Looper looper) {
        Handler handler = new Handler(looper);
        this.mWorkerHandler = handler;
        this.mContentObserver = Utilities.newContentObserver(handler, new Consumer() {
            public final void accept(Object obj) {
                WellbeingModel.this.onWellbeingUriChanged((Uri) obj);
            }
        });
        if (!TextUtils.isEmpty(this.mWellbeingProviderPkg)) {
            this.mContext.registerReceiver(new SimpleBroadcastReceiver(new Consumer() {
                public final void accept(Object obj) {
                    WellbeingModel.this.lambda$onInitialized$0$WellbeingModel((Intent) obj);
                }
            }), PackageManagerHelper.getPackageFilter(this.mWellbeingProviderPkg, "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED", "android.intent.action.PACKAGE_DATA_CLEARED", "android.intent.action.PACKAGE_RESTARTED"), (String) null, this.mWorkerHandler);
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addDataScheme("package");
            this.mContext.registerReceiver(new SimpleBroadcastReceiver(new Consumer() {
                public final void accept(Object obj) {
                    WellbeingModel.this.onAppPackageChanged((Intent) obj);
                }
            }), intentFilter, (String) null, this.mWorkerHandler);
            restartObserver();
        }
    }

    public /* synthetic */ void lambda$onInitialized$0$WellbeingModel(Intent intent) {
        restartObserver();
    }

    /* access modifiers changed from: private */
    public void onWellbeingUriChanged(Uri uri) {
        Preconditions.assertNonUiThread();
        if (this.mIsInTest) {
            Log.d(TAG, "ContentObserver.onChange() called with: uri = [" + uri + "]");
        }
        if (uri.getPath().contains("actions")) {
            updateAllPackages();
        } else if (uri.getPath().contains(PATH_MINIMAL_DEVICE) && FeatureFlags.ENABLE_MINIMAL_DEVICE.get()) {
            Context context = this.mContext;
            String str = DB_NAME_MINIMAL_DEVICE;
            context.deleteDatabase(str);
            Bundle bundle = new Bundle();
            if (isInMinimalDeviceMode()) {
                bundle.putString(LauncherProvider.KEY_LAYOUT_PROVIDER_AUTHORITY, this.mWellbeingProviderPkg + ".api");
            } else {
                str = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).dbFile;
            }
            LauncherSettings.Settings.call(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_SWITCH_DATABASE, str, bundle);
        }
    }

    public void setInTest(boolean z) {
        this.mIsInTest = z;
    }

    private void restartObserver() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.unregisterContentObserver(this.mContentObserver);
        Uri build = apiBuilder().path("actions").build();
        Uri build2 = apiBuilder().path(PATH_MINIMAL_DEVICE).build();
        try {
            contentResolver.registerContentObserver(build, true, this.mContentObserver);
            contentResolver.registerContentObserver(build2, true, this.mContentObserver);
        } catch (Exception e) {
            Log.e(TAG, "Failed to register content observer for " + build + ": " + e);
            if (this.mIsInTest) {
                throw new RuntimeException(e);
            }
        }
        updateAllPackages();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0069, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.launcher3.popup.SystemShortcut getShortcutForApp(java.lang.String r5, int r6, com.android.launcher3.BaseDraggingActivity r7, com.android.launcher3.model.data.ItemInfo r8, android.view.View r9) {
        /*
            r4 = this;
            com.android.launcher3.util.Preconditions.assertUIThread()
            int r0 = android.os.UserHandle.myUserId()
            r1 = 0
            if (r6 == r0) goto L_0x002d
            boolean r6 = r4.mIsInTest
            if (r6 == 0) goto L_0x002c
            java.lang.String r6 = "WellbeingModel"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "getShortcutForApp ["
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r5 = r7.append(r5)
            java.lang.String r7 = "]: not current user"
            java.lang.StringBuilder r5 = r5.append(r7)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r6, r5)
        L_0x002c:
            return r1
        L_0x002d:
            java.lang.Object r6 = r4.mModelLock
            monitor-enter(r6)
            java.util.Map<java.lang.String, java.lang.String> r0 = r4.mPackageToActionId     // Catch:{ all -> 0x00a1 }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ all -> 0x00a1 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x00a1 }
            if (r0 == 0) goto L_0x0043
            java.util.Map<java.lang.String, android.app.RemoteAction> r2 = r4.mActionIdMap     // Catch:{ all -> 0x00a1 }
            java.lang.Object r0 = r2.get(r0)     // Catch:{ all -> 0x00a1 }
            android.app.RemoteAction r0 = (android.app.RemoteAction) r0     // Catch:{ all -> 0x00a1 }
            goto L_0x0044
        L_0x0043:
            r0 = r1
        L_0x0044:
            if (r0 != 0) goto L_0x006a
            boolean r7 = r4.mIsInTest     // Catch:{ all -> 0x00a1 }
            if (r7 == 0) goto L_0x0068
            java.lang.String r7 = "WellbeingModel"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a1 }
            r8.<init>()     // Catch:{ all -> 0x00a1 }
            java.lang.String r9 = "getShortcutForApp ["
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x00a1 }
            java.lang.StringBuilder r5 = r8.append(r5)     // Catch:{ all -> 0x00a1 }
            java.lang.String r8 = "]: no action"
            java.lang.StringBuilder r5 = r5.append(r8)     // Catch:{ all -> 0x00a1 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00a1 }
            android.util.Log.d(r7, r5)     // Catch:{ all -> 0x00a1 }
        L_0x0068:
            monitor-exit(r6)     // Catch:{ all -> 0x00a1 }
            return r1
        L_0x006a:
            boolean r1 = r4.mIsInTest     // Catch:{ all -> 0x00a1 }
            if (r1 == 0) goto L_0x009a
            java.lang.String r1 = "WellbeingModel"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a1 }
            r2.<init>()     // Catch:{ all -> 0x00a1 }
            java.lang.String r3 = "getShortcutForApp ["
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ all -> 0x00a1 }
            java.lang.StringBuilder r5 = r2.append(r5)     // Catch:{ all -> 0x00a1 }
            java.lang.String r2 = "]: action: '"
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch:{ all -> 0x00a1 }
            java.lang.CharSequence r2 = r0.getTitle()     // Catch:{ all -> 0x00a1 }
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch:{ all -> 0x00a1 }
            java.lang.String r2 = "'"
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch:{ all -> 0x00a1 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00a1 }
            android.util.Log.d(r1, r5)     // Catch:{ all -> 0x00a1 }
        L_0x009a:
            com.android.launcher3.popup.RemoteActionShortcut r5 = new com.android.launcher3.popup.RemoteActionShortcut     // Catch:{ all -> 0x00a1 }
            r5.<init>(r0, r7, r8, r9)     // Catch:{ all -> 0x00a1 }
            monitor-exit(r6)     // Catch:{ all -> 0x00a1 }
            return r5
        L_0x00a1:
            r5 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x00a1 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.WellbeingModel.getShortcutForApp(java.lang.String, int, com.android.launcher3.BaseDraggingActivity, com.android.launcher3.model.data.ItemInfo, android.view.View):com.android.launcher3.popup.SystemShortcut");
    }

    private Uri.Builder apiBuilder() {
        return new Uri.Builder().scheme("content").authority(this.mWellbeingProviderPkg + ".api");
    }

    private boolean isInMinimalDeviceMode() {
        ContentProviderClient acquireUnstableContentProviderClient;
        if (!FeatureFlags.ENABLE_MINIMAL_DEVICE.get()) {
            return false;
        }
        if (this.mIsInTest) {
            Log.d(TAG, "isInMinimalDeviceMode() called");
        }
        Preconditions.assertNonUiThread();
        Uri build = apiBuilder().build();
        try {
            acquireUnstableContentProviderClient = this.mContext.getContentResolver().acquireUnstableContentProviderClient(build);
            Bundle bundle = null;
            if (acquireUnstableContentProviderClient != null) {
                bundle = acquireUnstableContentProviderClient.call(METHOD_GET_MINIMAL_DEVICE_CONFIG, (String) null, (Bundle) null);
            }
            boolean z = bundle != null && bundle.getInt(EXTRA_MINIMAL_DEVICE_STATE, 0) == 2;
            if (acquireUnstableContentProviderClient != null) {
                acquireUnstableContentProviderClient.close();
            }
            return z;
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve data from " + build + ": " + e);
            boolean z2 = this.mIsInTest;
            if (!z2) {
                if (z2) {
                    Log.i(TAG, "isInMinimalDeviceMode(): finished");
                }
                return false;
            }
            throw new RuntimeException(e);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    private boolean updateActions(String[] strArr) {
        if (strArr.length == 0) {
            return true;
        }
        if (this.mIsInTest) {
            Log.d(TAG, "retrieveActions() called with: packageNames = [" + String.join(", ", strArr) + "]");
        }
        Preconditions.assertNonUiThread();
        Uri build = apiBuilder().build();
        try {
            ContentProviderClient acquireUnstableContentProviderClient = this.mContext.getContentResolver().acquireUnstableContentProviderClient(build);
            if (acquireUnstableContentProviderClient == null) {
                try {
                    if (this.mIsInTest) {
                        Log.i(TAG, "retrieveActions(): null provider");
                    }
                    if (acquireUnstableContentProviderClient != null) {
                        acquireUnstableContentProviderClient.close();
                    }
                    return false;
                } catch (Throwable th) {
                    if (acquireUnstableContentProviderClient != null) {
                        acquireUnstableContentProviderClient.close();
                    }
                    throw th;
                }
            } else {
                Bundle bundle = new Bundle();
                bundle.putStringArray(EXTRA_PACKAGES, strArr);
                bundle.putInt(EXTRA_MAX_NUM_ACTIONS_SHOWN, 1);
                Bundle call = acquireUnstableContentProviderClient.call(METHOD_GET_ACTIONS, (String) null, bundle);
                if (!call.getBoolean(EXTRA_SUCCESS, true)) {
                    if (acquireUnstableContentProviderClient != null) {
                        acquireUnstableContentProviderClient.close();
                    }
                    return false;
                }
                synchronized (this.mModelLock) {
                    Stream stream = Arrays.stream(strArr);
                    Map<String, String> map = this.mPackageToActionId;
                    Objects.requireNonNull(map);
                    stream.forEach(new Consumer(map) {
                        public final /* synthetic */ Map f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void accept(Object obj) {
                            this.f$0.remove((String) obj);
                        }
                    });
                    for (String str : call.getStringArray("actions")) {
                        Bundle bundle2 = call.getBundle(str);
                        this.mActionIdMap.put(str, (RemoteAction) bundle2.getParcelable(EXTRA_ACTION));
                        String[] stringArray = bundle2.getStringArray(EXTRA_PACKAGES);
                        if (this.mIsInTest) {
                            Log.d(TAG, "....actionId: " + str + ", packages: " + String.join(", ", stringArray));
                        }
                        for (String put : stringArray) {
                            this.mPackageToActionId.put(put, str);
                        }
                    }
                }
                if (acquireUnstableContentProviderClient != null) {
                    acquireUnstableContentProviderClient.close();
                }
                if (this.mIsInTest) {
                    Log.i(TAG, "retrieveActions(): finished");
                }
                return true;
            }
        } catch (DeadObjectException unused) {
            Log.i(TAG, "retrieveActions(): DeadObjectException");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve data from " + build + ": " + e);
            if (!this.mIsInTest) {
                return true;
            }
            throw new RuntimeException(e);
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    private void updateActionsWithRetry(int i, String str) {
        if (this.mIsInTest) {
            Log.i(TAG, "updateActionsWithRetry(); retryCount: " + i + ", package: " + str);
        }
        String[] strArr = TextUtils.isEmpty(str) ? (String[]) ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).getActivityList((String) null, Process.myUserHandle()).stream().map($$Lambda$WellbeingModel$MsLKn8S_jAivxx6MSj2OMZAgi5k.INSTANCE).distinct().toArray($$Lambda$WellbeingModel$DSdSyZ9gs6sA_iPYSzJxwjnhLLs.INSTANCE) : new String[]{str};
        this.mWorkerHandler.removeCallbacksAndMessages(str);
        if (!updateActions(strArr)) {
            int[] iArr = RETRY_TIMES_MS;
            if (i < iArr.length) {
                this.mWorkerHandler.postDelayed(new Runnable(i, str) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        WellbeingModel.this.lambda$updateActionsWithRetry$3$WellbeingModel(this.f$1, this.f$2);
                    }
                }, str, (long) iArr[i]);
            }
        }
    }

    static /* synthetic */ String[] lambda$updateActionsWithRetry$2(int i) {
        return new String[i];
    }

    public /* synthetic */ void lambda$updateActionsWithRetry$3$WellbeingModel(int i, String str) {
        if (this.mIsInTest) {
            Log.i(TAG, "Retrying; attempt " + (i + 1));
        }
        updateActionsWithRetry(i + 1, str);
    }

    private void updateAllPackages() {
        if (this.mIsInTest) {
            Log.i(TAG, "updateAllPackages");
        }
        updateActionsWithRetry(0, (String) null);
    }

    /* access modifiers changed from: private */
    public void onAppPackageChanged(Intent intent) {
        if (this.mIsInTest) {
            Log.d(TAG, "Changes in apps: intent = [" + intent + "]");
        }
        Preconditions.assertNonUiThread();
        String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
        if (schemeSpecificPart != null && schemeSpecificPart.length() != 0) {
            String action = intent.getAction();
            if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                this.mWorkerHandler.removeCallbacksAndMessages(schemeSpecificPart);
                synchronized (this.mModelLock) {
                    this.mPackageToActionId.remove(schemeSpecificPart);
                }
            } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                updateActionsWithRetry(0, schemeSpecificPart);
            }
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$4(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo, View view) {
        if (itemInfo.getTargetComponent() == null) {
            return null;
        }
        return INSTANCE.lambda$get$1$MainThreadInitializedObject(baseDraggingActivity).getShortcutForApp(itemInfo.getTargetComponent().getPackageName(), itemInfo.user.getIdentifier(), baseDraggingActivity, itemInfo, view);
    }
}
