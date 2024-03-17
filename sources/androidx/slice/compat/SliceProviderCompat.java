package androidx.slice.compat;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.core.util.Preconditions;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.SliceSpec;
import androidx.slice.core.SliceHints;
import androidx.versionedparcelable.ParcelUtils;
import com.android.launcher3.LauncherSettings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SliceProviderCompat {
    private static final String ALL_FILES = "slice_data_all_slice_files";
    public static final String ARG_SUPPORTS_VERSIONED_PARCELABLE = "supports_versioned_parcelable";
    private static final String DATA_PREFIX = "slice_data_";
    public static final String EXTRA_BIND_URI = "slice_uri";
    public static final String EXTRA_INTENT = "slice_intent";
    public static final String EXTRA_PID = "pid";
    public static final String EXTRA_PKG = "pkg";
    public static final String EXTRA_PROVIDER_PKG = "provider_pkg";
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_SLICE = "slice";
    public static final String EXTRA_SLICE_DESCENDANTS = "slice_descendants";
    public static final String EXTRA_SUPPORTED_SPECS = "specs";
    public static final String EXTRA_SUPPORTED_SPECS_REVS = "revs";
    public static final String EXTRA_UID = "uid";
    public static final String METHOD_CHECK_PERMISSION = "check_perms";
    public static final String METHOD_GET_DESCENDANTS = "get_descendants";
    public static final String METHOD_GET_PINNED_SPECS = "get_specs";
    public static final String METHOD_GRANT_PERMISSION = "grant_perms";
    public static final String METHOD_MAP_INTENT = "map_slice";
    public static final String METHOD_MAP_ONLY_INTENT = "map_only";
    public static final String METHOD_PIN = "pin_slice";
    public static final String METHOD_REVOKE_PERMISSION = "revoke_perms";
    public static final String METHOD_SLICE = "bind_slice";
    public static final String METHOD_UNPIN = "unpin_slice";
    public static final String PERMS_PREFIX = "slice_perms_";
    private static final long SLICE_BIND_ANR = 2000;
    private static final String TAG = "SliceProviderCompat";
    private final Runnable mAnr = new Runnable() {
        public void run() {
            Process.sendSignal(Process.myPid(), 3);
            Log.wtf(SliceProviderCompat.TAG, "Timed out while handling slice callback " + SliceProviderCompat.this.mCallback);
        }
    };
    String mCallback;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private CompatPermissionManager mPermissionManager;
    private CompatPinnedList mPinnedList;
    private final SliceProvider mProvider;

    public SliceProviderCompat(SliceProvider sliceProvider, CompatPermissionManager compatPermissionManager, Context context) {
        this.mProvider = sliceProvider;
        this.mContext = context;
        String str = DATA_PREFIX + getClass().getName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(ALL_FILES, 0);
        Set<String> stringSet = sharedPreferences.getStringSet(ALL_FILES, Collections.emptySet());
        if (!stringSet.contains(str)) {
            ArraySet arraySet = new ArraySet(stringSet);
            arraySet.add(str);
            sharedPreferences.edit().putStringSet(ALL_FILES, arraySet).commit();
        }
        this.mPinnedList = new CompatPinnedList(context, str);
        this.mPermissionManager = compatPermissionManager;
    }

    private Context getContext() {
        return this.mContext;
    }

    public String getCallingPackage() {
        return this.mProvider.getCallingPackage();
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Parcelable parcelable = null;
        if (str.equals(METHOD_SLICE)) {
            Slice handleBindSlice = handleBindSlice((Uri) bundle.getParcelable(EXTRA_BIND_URI), getSpecs(bundle), getCallingPackage());
            Bundle bundle2 = new Bundle();
            if (ARG_SUPPORTS_VERSIONED_PARCELABLE.equals(str2)) {
                if (handleBindSlice != null) {
                    parcelable = ParcelUtils.toParcelable(handleBindSlice);
                }
                bundle2.putParcelable(EXTRA_SLICE, parcelable);
            } else {
                if (handleBindSlice != null) {
                    parcelable = handleBindSlice.toBundle();
                }
                bundle2.putParcelable(EXTRA_SLICE, parcelable);
            }
            return bundle2;
        } else if (str.equals(METHOD_MAP_INTENT)) {
            Uri onMapIntentToUri = this.mProvider.onMapIntentToUri((Intent) bundle.getParcelable(EXTRA_INTENT));
            Bundle bundle3 = new Bundle();
            if (onMapIntentToUri != null) {
                Slice handleBindSlice2 = handleBindSlice(onMapIntentToUri, getSpecs(bundle), getCallingPackage());
                if (ARG_SUPPORTS_VERSIONED_PARCELABLE.equals(str2)) {
                    if (handleBindSlice2 != null) {
                        parcelable = ParcelUtils.toParcelable(handleBindSlice2);
                    }
                    bundle3.putParcelable(EXTRA_SLICE, parcelable);
                } else {
                    if (handleBindSlice2 != null) {
                        parcelable = handleBindSlice2.toBundle();
                    }
                    bundle3.putParcelable(EXTRA_SLICE, parcelable);
                }
            } else {
                bundle3.putParcelable(EXTRA_SLICE, (Parcelable) null);
            }
            return bundle3;
        } else if (str.equals(METHOD_MAP_ONLY_INTENT)) {
            Uri onMapIntentToUri2 = this.mProvider.onMapIntentToUri((Intent) bundle.getParcelable(EXTRA_INTENT));
            Bundle bundle4 = new Bundle();
            bundle4.putParcelable(EXTRA_SLICE, onMapIntentToUri2);
            return bundle4;
        } else if (str.equals(METHOD_PIN)) {
            Uri uri = (Uri) bundle.getParcelable(EXTRA_BIND_URI);
            Set<SliceSpec> specs = getSpecs(bundle);
            if (this.mPinnedList.addPin(uri, bundle.getString(EXTRA_PKG), specs)) {
                handleSlicePinned(uri);
            }
            return null;
        } else if (str.equals(METHOD_UNPIN)) {
            Uri uri2 = (Uri) bundle.getParcelable(EXTRA_BIND_URI);
            if (this.mPinnedList.removePin(uri2, bundle.getString(EXTRA_PKG))) {
                handleSliceUnpinned(uri2);
            }
            return null;
        } else if (str.equals(METHOD_GET_PINNED_SPECS)) {
            Uri uri3 = (Uri) bundle.getParcelable(EXTRA_BIND_URI);
            Bundle bundle5 = new Bundle();
            ArraySet<SliceSpec> specs2 = this.mPinnedList.getSpecs(uri3);
            if (specs2.size() != 0) {
                addSpecs(bundle5, specs2);
                return bundle5;
            }
            throw new IllegalStateException(uri3 + " is not pinned");
        } else if (str.equals(METHOD_GET_DESCENDANTS)) {
            Bundle bundle6 = new Bundle();
            bundle6.putParcelableArrayList(EXTRA_SLICE_DESCENDANTS, new ArrayList(handleGetDescendants((Uri) bundle.getParcelable(EXTRA_BIND_URI))));
            return bundle6;
        } else if (str.equals(METHOD_CHECK_PERMISSION)) {
            bundle.getString(EXTRA_PKG);
            int i = bundle.getInt("pid");
            int i2 = bundle.getInt(EXTRA_UID);
            Bundle bundle7 = new Bundle();
            bundle7.putInt(EXTRA_RESULT, this.mPermissionManager.checkSlicePermission((Uri) bundle.getParcelable(EXTRA_BIND_URI), i, i2));
            return bundle7;
        } else {
            if (str.equals(METHOD_GRANT_PERMISSION)) {
                Uri uri4 = (Uri) bundle.getParcelable(EXTRA_BIND_URI);
                String string = bundle.getString(EXTRA_PKG);
                if (Binder.getCallingUid() == Process.myUid()) {
                    this.mPermissionManager.grantSlicePermission(uri4, string);
                } else {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
            } else if (str.equals(METHOD_REVOKE_PERMISSION)) {
                Uri uri5 = (Uri) bundle.getParcelable(EXTRA_BIND_URI);
                String string2 = bundle.getString(EXTRA_PKG);
                if (Binder.getCallingUid() == Process.myUid()) {
                    this.mPermissionManager.revokeSlicePermission(uri5, string2);
                } else {
                    throw new SecurityException("Only the owning process can manage slice permissions");
                }
            }
            return null;
        }
    }

    private Collection<Uri> handleGetDescendants(Uri uri) {
        this.mCallback = "onGetSliceDescendants";
        return this.mProvider.onGetSliceDescendants(uri);
    }

    private void handleSlicePinned(Uri uri) {
        this.mCallback = "onSlicePinned";
        this.mHandler.postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            this.mProvider.onSlicePinned(uri);
            this.mProvider.handleSlicePinned(uri);
        } finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }

    private void handleSliceUnpinned(Uri uri) {
        this.mCallback = "onSliceUnpinned";
        this.mHandler.postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            this.mProvider.onSliceUnpinned(uri);
            this.mProvider.handleSliceUnpinned(uri);
        } finally {
            this.mHandler.removeCallbacks(this.mAnr);
        }
    }

    private Slice handleBindSlice(Uri uri, Set<SliceSpec> set, String str) {
        if (str == null) {
            str = getContext().getPackageManager().getNameForUid(Binder.getCallingUid());
        }
        if (this.mPermissionManager.checkSlicePermission(uri, Binder.getCallingPid(), Binder.getCallingUid()) != 0) {
            return SliceProvider.createPermissionSlice(getContext(), uri, str);
        }
        return onBindSliceStrict(uri, set);
    }

    private Slice onBindSliceStrict(Uri uri, Set<SliceSpec> set) {
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        this.mCallback = "onBindSlice";
        this.mHandler.postDelayed(this.mAnr, SLICE_BIND_ANR);
        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDeath().build());
            SliceProvider.setSpecs(set);
            try {
                Slice onBindSlice = this.mProvider.onBindSlice(uri);
                SliceProvider.setSpecs((Set<SliceSpec>) null);
                this.mHandler.removeCallbacks(this.mAnr);
                StrictMode.setThreadPolicy(threadPolicy);
                return onBindSlice;
            } catch (Exception e) {
                Log.wtf(TAG, "Slice with URI " + uri.toString() + " is invalid.", e);
                SliceProvider.setSpecs((Set<SliceSpec>) null);
                this.mHandler.removeCallbacks(this.mAnr);
                StrictMode.setThreadPolicy(threadPolicy);
                return null;
            }
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(threadPolicy);
            throw th;
        }
    }

    public static Slice bindSlice(Context context, Uri uri, Set<SliceSpec> set) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRA_BIND_URI, uri);
                addSpecs(bundle, set);
                Bundle call = acquireClient.mProvider.call(METHOD_SLICE, ARG_SUPPORTS_VERSIONED_PARCELABLE, bundle);
                if (call == null) {
                    return null;
                }
                call.setClassLoader(SliceProviderCompat.class.getClassLoader());
                Parcelable parcelable = call.getParcelable(EXTRA_SLICE);
                if (parcelable == null) {
                    return null;
                }
                if (parcelable instanceof Bundle) {
                    return new Slice((Bundle) parcelable);
                }
                return (Slice) ParcelUtils.fromParcelable(parcelable);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to bind slice", e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public static void addSpecs(Bundle bundle, Set<SliceSpec> set) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (SliceSpec next : set) {
            arrayList.add(next.getType());
            arrayList2.add(Integer.valueOf(next.getRevision()));
        }
        bundle.putStringArrayList(EXTRA_SUPPORTED_SPECS, arrayList);
        bundle.putIntegerArrayList(EXTRA_SUPPORTED_SPECS_REVS, arrayList2);
    }

    public static Set<SliceSpec> getSpecs(Bundle bundle) {
        ArraySet arraySet = new ArraySet();
        ArrayList<String> stringArrayList = bundle.getStringArrayList(EXTRA_SUPPORTED_SPECS);
        ArrayList<Integer> integerArrayList = bundle.getIntegerArrayList(EXTRA_SUPPORTED_SPECS_REVS);
        if (!(stringArrayList == null || integerArrayList == null)) {
            for (int i = 0; i < stringArrayList.size(); i++) {
                arraySet.add(new SliceSpec(stringArrayList.get(i), integerArrayList.get(i).intValue()));
            }
        }
        return arraySet;
    }

    public static Slice bindSlice(Context context, Intent intent, Set<SliceSpec> set) {
        Preconditions.checkNotNull(intent, LauncherSettings.Favorites.INTENT);
        Preconditions.checkArgument((intent.getComponent() == null && intent.getPackage() == null && intent.getData() == null) ? false : true, String.format("Slice intent must be explicit %s", new Object[]{intent}));
        ContentResolver contentResolver = context.getContentResolver();
        Uri data = intent.getData();
        if (data != null && "vnd.android.slice".equals(contentResolver.getType(data))) {
            return bindSlice(context, data, set);
        }
        Intent intent2 = new Intent(intent);
        if (!intent2.hasCategory("android.app.slice.category.SLICE")) {
            intent2.addCategory("android.app.slice.category.SLICE");
        }
        List<ResolveInfo> queryIntentContentProviders = context.getPackageManager().queryIntentContentProviders(intent2, 0);
        if (queryIntentContentProviders == null || queryIntentContentProviders.isEmpty()) {
            ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 128);
            if (resolveActivity == null || resolveActivity.activityInfo == null || resolveActivity.activityInfo.metaData == null || !resolveActivity.activityInfo.metaData.containsKey(SliceHints.SLICE_METADATA_KEY)) {
                return null;
            }
            return bindSlice(context, Uri.parse(resolveActivity.activityInfo.metaData.getString(SliceHints.SLICE_METADATA_KEY)), set);
        }
        Uri build = new Uri.Builder().scheme("content").authority(queryIntentContentProviders.get(0).providerInfo.authority).build();
        ProviderHolder acquireClient = acquireClient(contentResolver, build);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRA_INTENT, intent);
                addSpecs(bundle, set);
                Bundle call = acquireClient.mProvider.call(METHOD_MAP_INTENT, ARG_SUPPORTS_VERSIONED_PARCELABLE, bundle);
                if (call == null) {
                    return null;
                }
                call.setClassLoader(SliceProviderCompat.class.getClassLoader());
                Parcelable parcelable = call.getParcelable(EXTRA_SLICE);
                if (parcelable == null) {
                    return null;
                }
                if (parcelable instanceof Bundle) {
                    return new Slice((Bundle) parcelable);
                }
                return (Slice) ParcelUtils.fromParcelable(parcelable);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to bind slice", e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + build);
        }
    }

    public static void pinSlice(Context context, Uri uri, Set<SliceSpec> set) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRA_BIND_URI, uri);
                bundle.putString(EXTRA_PKG, context.getPackageName());
                addSpecs(bundle, set);
                acquireClient.mProvider.call(METHOD_PIN, ARG_SUPPORTS_VERSIONED_PARCELABLE, bundle);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to pin slice", e);
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public static void unpinSlice(Context context, Uri uri, Set<SliceSpec> set) {
        if (getPinnedSlices(context).contains(uri)) {
            ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
            if (acquireClient.mProvider != null) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(EXTRA_BIND_URI, uri);
                    bundle.putString(EXTRA_PKG, context.getPackageName());
                    addSpecs(bundle, set);
                    acquireClient.mProvider.call(METHOD_UNPIN, ARG_SUPPORTS_VERSIONED_PARCELABLE, bundle);
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to unpin slice", e);
                }
            } else {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }

    public static Set<SliceSpec> getPinnedSpecs(Context context, Uri uri) {
        ProviderHolder acquireClient = acquireClient(context.getContentResolver(), uri);
        if (acquireClient.mProvider != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRA_BIND_URI, uri);
                Bundle call = acquireClient.mProvider.call(METHOD_GET_PINNED_SPECS, ARG_SUPPORTS_VERSIONED_PARCELABLE, bundle);
                if (call != null) {
                    return getSpecs(call);
                }
                return null;
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to get pinned specs", e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00ce, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00cf, code lost:
        if (r0 != null) goto L_0x00d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00d1, code lost:
        if (r5 != null) goto L_0x00d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00dc, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00df, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.net.Uri mapIntentToUri(android.content.Context r5, android.content.Intent r6) {
        /*
            java.lang.String r0 = "intent"
            androidx.core.util.Preconditions.checkNotNull(r6, r0)
            android.content.ComponentName r0 = r6.getComponent()
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x001c
            java.lang.String r0 = r6.getPackage()
            if (r0 != 0) goto L_0x001c
            android.net.Uri r0 = r6.getData()
            if (r0 == 0) goto L_0x001a
            goto L_0x001c
        L_0x001a:
            r0 = r2
            goto L_0x001d
        L_0x001c:
            r0 = r1
        L_0x001d:
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r6
            java.lang.String r3 = "Slice intent must be explicit %s"
            java.lang.String r1 = java.lang.String.format(r3, r1)
            androidx.core.util.Preconditions.checkArgument(r0, r1)
            android.content.ContentResolver r0 = r5.getContentResolver()
            android.net.Uri r1 = r6.getData()
            if (r1 == 0) goto L_0x0041
            java.lang.String r3 = r0.getType(r1)
            java.lang.String r4 = "vnd.android.slice"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0041
            return r1
        L_0x0041:
            android.content.Intent r1 = new android.content.Intent
            r1.<init>(r6)
            java.lang.String r3 = "android.app.slice.category.SLICE"
            boolean r4 = r1.hasCategory(r3)
            if (r4 != 0) goto L_0x0051
            r1.addCategory(r3)
        L_0x0051:
            android.content.pm.PackageManager r3 = r5.getPackageManager()
            java.util.List r1 = r3.queryIntentContentProviders(r1, r2)
            r3 = 0
            if (r1 == 0) goto L_0x00e9
            boolean r4 = r1.isEmpty()
            if (r4 == 0) goto L_0x0064
            goto L_0x00e9
        L_0x0064:
            java.lang.Object r5 = r1.get(r2)
            android.content.pm.ResolveInfo r5 = (android.content.pm.ResolveInfo) r5
            android.content.pm.ProviderInfo r5 = r5.providerInfo
            java.lang.String r5 = r5.authority
            android.net.Uri$Builder r1 = new android.net.Uri$Builder
            r1.<init>()
            java.lang.String r2 = "content"
            android.net.Uri$Builder r1 = r1.scheme(r2)
            android.net.Uri$Builder r5 = r1.authority(r5)
            android.net.Uri r5 = r5.build()
            androidx.slice.compat.SliceProviderCompat$ProviderHolder r0 = acquireClient(r0, r5)     // Catch:{ RemoteException -> 0x00e0 }
            android.content.ContentProviderClient r1 = r0.mProvider     // Catch:{ all -> 0x00cc }
            if (r1 == 0) goto L_0x00b3
            android.os.Bundle r5 = new android.os.Bundle     // Catch:{ all -> 0x00cc }
            r5.<init>()     // Catch:{ all -> 0x00cc }
            java.lang.String r1 = "slice_intent"
            r5.putParcelable(r1, r6)     // Catch:{ all -> 0x00cc }
            android.content.ContentProviderClient r6 = r0.mProvider     // Catch:{ all -> 0x00cc }
            java.lang.String r1 = "map_only"
            java.lang.String r2 = "supports_versioned_parcelable"
            android.os.Bundle r5 = r6.call(r1, r2, r5)     // Catch:{ all -> 0x00cc }
            if (r5 == 0) goto L_0x00ad
            java.lang.String r6 = "slice"
            android.os.Parcelable r5 = r5.getParcelable(r6)     // Catch:{ all -> 0x00cc }
            android.net.Uri r5 = (android.net.Uri) r5     // Catch:{ all -> 0x00cc }
            if (r0 == 0) goto L_0x00ac
            r0.close()     // Catch:{ RemoteException -> 0x00e0 }
        L_0x00ac:
            return r5
        L_0x00ad:
            if (r0 == 0) goto L_0x00e8
            r0.close()     // Catch:{ RemoteException -> 0x00e0 }
            goto L_0x00e8
        L_0x00b3:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x00cc }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00cc }
            r1.<init>()     // Catch:{ all -> 0x00cc }
            java.lang.String r2 = "Unknown URI "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x00cc }
            java.lang.StringBuilder r5 = r1.append(r5)     // Catch:{ all -> 0x00cc }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00cc }
            r6.<init>(r5)     // Catch:{ all -> 0x00cc }
            throw r6     // Catch:{ all -> 0x00cc }
        L_0x00cc:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x00ce }
        L_0x00ce:
            r6 = move-exception
            if (r0 == 0) goto L_0x00df
            if (r5 == 0) goto L_0x00dc
            r0.close()     // Catch:{ all -> 0x00d7 }
            goto L_0x00df
        L_0x00d7:
            r0 = move-exception
            r5.addSuppressed(r0)     // Catch:{ RemoteException -> 0x00e0 }
            goto L_0x00df
        L_0x00dc:
            r0.close()     // Catch:{ RemoteException -> 0x00e0 }
        L_0x00df:
            throw r6     // Catch:{ RemoteException -> 0x00e0 }
        L_0x00e0:
            r5 = move-exception
            java.lang.String r6 = "SliceProviderCompat"
            java.lang.String r0 = "Unable to map slice"
            android.util.Log.e(r6, r0, r5)
        L_0x00e8:
            return r3
        L_0x00e9:
            android.content.pm.PackageManager r5 = r5.getPackageManager()
            r0 = 128(0x80, float:1.794E-43)
            android.content.pm.ResolveInfo r5 = r5.resolveActivity(r6, r0)
            if (r5 == 0) goto L_0x0118
            android.content.pm.ActivityInfo r6 = r5.activityInfo
            if (r6 == 0) goto L_0x0118
            android.content.pm.ActivityInfo r6 = r5.activityInfo
            android.os.Bundle r6 = r6.metaData
            if (r6 == 0) goto L_0x0118
            android.content.pm.ActivityInfo r6 = r5.activityInfo
            android.os.Bundle r6 = r6.metaData
            java.lang.String r0 = "android.metadata.SLICE_URI"
            boolean r6 = r6.containsKey(r0)
            if (r6 == 0) goto L_0x0118
            android.content.pm.ActivityInfo r5 = r5.activityInfo
            android.os.Bundle r5 = r5.metaData
            java.lang.String r5 = r5.getString(r0)
            android.net.Uri r5 = android.net.Uri.parse(r5)
            return r5
        L_0x0118:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.SliceProviderCompat.mapIntentToUri(android.content.Context, android.content.Intent):android.net.Uri");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0032, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0033, code lost:
        if (r3 != null) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0035, code lost:
        if (r4 != null) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0040, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0043, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Collection<android.net.Uri> getSliceDescendants(android.content.Context r3, android.net.Uri r4) {
        /*
            android.content.ContentResolver r3 = r3.getContentResolver()
            androidx.slice.compat.SliceProviderCompat$ProviderHolder r3 = acquireClient(r3, r4)     // Catch:{ RemoteException -> 0x0044 }
            android.os.Bundle r0 = new android.os.Bundle     // Catch:{ all -> 0x0030 }
            r0.<init>()     // Catch:{ all -> 0x0030 }
            java.lang.String r1 = "slice_uri"
            r0.putParcelable(r1, r4)     // Catch:{ all -> 0x0030 }
            android.content.ContentProviderClient r4 = r3.mProvider     // Catch:{ all -> 0x0030 }
            java.lang.String r1 = "get_descendants"
            java.lang.String r2 = "supports_versioned_parcelable"
            android.os.Bundle r4 = r4.call(r1, r2, r0)     // Catch:{ all -> 0x0030 }
            if (r4 == 0) goto L_0x002a
            java.lang.String r0 = "slice_descendants"
            java.util.ArrayList r4 = r4.getParcelableArrayList(r0)     // Catch:{ all -> 0x0030 }
            if (r3 == 0) goto L_0x0029
            r3.close()     // Catch:{ RemoteException -> 0x0044 }
        L_0x0029:
            return r4
        L_0x002a:
            if (r3 == 0) goto L_0x004c
            r3.close()     // Catch:{ RemoteException -> 0x0044 }
            goto L_0x004c
        L_0x0030:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0032 }
        L_0x0032:
            r0 = move-exception
            if (r3 == 0) goto L_0x0043
            if (r4 == 0) goto L_0x0040
            r3.close()     // Catch:{ all -> 0x003b }
            goto L_0x0043
        L_0x003b:
            r3 = move-exception
            r4.addSuppressed(r3)     // Catch:{ RemoteException -> 0x0044 }
            goto L_0x0043
        L_0x0040:
            r3.close()     // Catch:{ RemoteException -> 0x0044 }
        L_0x0043:
            throw r0     // Catch:{ RemoteException -> 0x0044 }
        L_0x0044:
            r3 = move-exception
            java.lang.String r4 = "SliceProviderCompat"
            java.lang.String r0 = "Unable to get slice descendants"
            android.util.Log.e(r4, r0, r3)
        L_0x004c:
            java.util.List r3 = java.util.Collections.emptyList()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.SliceProviderCompat.getSliceDescendants(android.content.Context, android.net.Uri):java.util.Collection");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0041, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0042, code lost:
        if (r2 != null) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
        if (r3 != null) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004f, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0052, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int checkSlicePermission(android.content.Context r2, java.lang.String r3, android.net.Uri r4, int r5, int r6) {
        /*
            android.content.ContentResolver r2 = r2.getContentResolver()
            androidx.slice.compat.SliceProviderCompat$ProviderHolder r2 = acquireClient(r2, r4)     // Catch:{ RemoteException -> 0x0053 }
            android.os.Bundle r0 = new android.os.Bundle     // Catch:{ all -> 0x003f }
            r0.<init>()     // Catch:{ all -> 0x003f }
            java.lang.String r1 = "slice_uri"
            r0.putParcelable(r1, r4)     // Catch:{ all -> 0x003f }
            java.lang.String r4 = "pkg"
            r0.putString(r4, r3)     // Catch:{ all -> 0x003f }
            java.lang.String r3 = "pid"
            r0.putInt(r3, r5)     // Catch:{ all -> 0x003f }
            java.lang.String r3 = "uid"
            r0.putInt(r3, r6)     // Catch:{ all -> 0x003f }
            android.content.ContentProviderClient r3 = r2.mProvider     // Catch:{ all -> 0x003f }
            java.lang.String r4 = "check_perms"
            java.lang.String r5 = "supports_versioned_parcelable"
            android.os.Bundle r3 = r3.call(r4, r5, r0)     // Catch:{ all -> 0x003f }
            if (r3 == 0) goto L_0x0039
            java.lang.String r4 = "result"
            int r3 = r3.getInt(r4)     // Catch:{ all -> 0x003f }
            if (r2 == 0) goto L_0x0038
            r2.close()     // Catch:{ RemoteException -> 0x0053 }
        L_0x0038:
            return r3
        L_0x0039:
            if (r2 == 0) goto L_0x005b
            r2.close()     // Catch:{ RemoteException -> 0x0053 }
            goto L_0x005b
        L_0x003f:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0041 }
        L_0x0041:
            r4 = move-exception
            if (r2 == 0) goto L_0x0052
            if (r3 == 0) goto L_0x004f
            r2.close()     // Catch:{ all -> 0x004a }
            goto L_0x0052
        L_0x004a:
            r2 = move-exception
            r3.addSuppressed(r2)     // Catch:{ RemoteException -> 0x0053 }
            goto L_0x0052
        L_0x004f:
            r2.close()     // Catch:{ RemoteException -> 0x0053 }
        L_0x0052:
            throw r4     // Catch:{ RemoteException -> 0x0053 }
        L_0x0053:
            r2 = move-exception
            java.lang.String r3 = "SliceProviderCompat"
            java.lang.String r4 = "Unable to check slice permission"
            android.util.Log.e(r3, r4, r2)
        L_0x005b:
            r2 = -1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.SliceProviderCompat.checkSlicePermission(android.content.Context, java.lang.String, android.net.Uri, int, int):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002d, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
        if (r2 != null) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0030, code lost:
        if (r3 != null) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003b, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003e, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void grantSlicePermission(android.content.Context r2, java.lang.String r3, java.lang.String r4, android.net.Uri r5) {
        /*
            android.content.ContentResolver r2 = r2.getContentResolver()
            androidx.slice.compat.SliceProviderCompat$ProviderHolder r2 = acquireClient(r2, r5)     // Catch:{ RemoteException -> 0x003f }
            android.os.Bundle r0 = new android.os.Bundle     // Catch:{ all -> 0x002b }
            r0.<init>()     // Catch:{ all -> 0x002b }
            java.lang.String r1 = "slice_uri"
            r0.putParcelable(r1, r5)     // Catch:{ all -> 0x002b }
            java.lang.String r5 = "provider_pkg"
            r0.putString(r5, r3)     // Catch:{ all -> 0x002b }
            java.lang.String r3 = "pkg"
            r0.putString(r3, r4)     // Catch:{ all -> 0x002b }
            android.content.ContentProviderClient r3 = r2.mProvider     // Catch:{ all -> 0x002b }
            java.lang.String r4 = "grant_perms"
            java.lang.String r5 = "supports_versioned_parcelable"
            r3.call(r4, r5, r0)     // Catch:{ all -> 0x002b }
            if (r2 == 0) goto L_0x0047
            r2.close()     // Catch:{ RemoteException -> 0x003f }
            goto L_0x0047
        L_0x002b:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x002d }
        L_0x002d:
            r4 = move-exception
            if (r2 == 0) goto L_0x003e
            if (r3 == 0) goto L_0x003b
            r2.close()     // Catch:{ all -> 0x0036 }
            goto L_0x003e
        L_0x0036:
            r2 = move-exception
            r3.addSuppressed(r2)     // Catch:{ RemoteException -> 0x003f }
            goto L_0x003e
        L_0x003b:
            r2.close()     // Catch:{ RemoteException -> 0x003f }
        L_0x003e:
            throw r4     // Catch:{ RemoteException -> 0x003f }
        L_0x003f:
            r2 = move-exception
            java.lang.String r3 = "SliceProviderCompat"
            java.lang.String r4 = "Unable to get slice descendants"
            android.util.Log.e(r3, r4, r2)
        L_0x0047:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.SliceProviderCompat.grantSlicePermission(android.content.Context, java.lang.String, java.lang.String, android.net.Uri):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002d, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
        if (r2 != null) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0030, code lost:
        if (r3 != null) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003b, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003e, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void revokeSlicePermission(android.content.Context r2, java.lang.String r3, java.lang.String r4, android.net.Uri r5) {
        /*
            android.content.ContentResolver r2 = r2.getContentResolver()
            androidx.slice.compat.SliceProviderCompat$ProviderHolder r2 = acquireClient(r2, r5)     // Catch:{ RemoteException -> 0x003f }
            android.os.Bundle r0 = new android.os.Bundle     // Catch:{ all -> 0x002b }
            r0.<init>()     // Catch:{ all -> 0x002b }
            java.lang.String r1 = "slice_uri"
            r0.putParcelable(r1, r5)     // Catch:{ all -> 0x002b }
            java.lang.String r5 = "provider_pkg"
            r0.putString(r5, r3)     // Catch:{ all -> 0x002b }
            java.lang.String r3 = "pkg"
            r0.putString(r3, r4)     // Catch:{ all -> 0x002b }
            android.content.ContentProviderClient r3 = r2.mProvider     // Catch:{ all -> 0x002b }
            java.lang.String r4 = "revoke_perms"
            java.lang.String r5 = "supports_versioned_parcelable"
            r3.call(r4, r5, r0)     // Catch:{ all -> 0x002b }
            if (r2 == 0) goto L_0x0047
            r2.close()     // Catch:{ RemoteException -> 0x003f }
            goto L_0x0047
        L_0x002b:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x002d }
        L_0x002d:
            r4 = move-exception
            if (r2 == 0) goto L_0x003e
            if (r3 == 0) goto L_0x003b
            r2.close()     // Catch:{ all -> 0x0036 }
            goto L_0x003e
        L_0x0036:
            r2 = move-exception
            r3.addSuppressed(r2)     // Catch:{ RemoteException -> 0x003f }
            goto L_0x003e
        L_0x003b:
            r2.close()     // Catch:{ RemoteException -> 0x003f }
        L_0x003e:
            throw r4     // Catch:{ RemoteException -> 0x003f }
        L_0x003f:
            r2 = move-exception
            java.lang.String r3 = "SliceProviderCompat"
            java.lang.String r4 = "Unable to get slice descendants"
            android.util.Log.e(r3, r4, r2)
        L_0x0047:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.compat.SliceProviderCompat.revokeSlicePermission(android.content.Context, java.lang.String, java.lang.String, android.net.Uri):void");
    }

    public static List<Uri> getPinnedSlices(Context context) {
        ArrayList arrayList = new ArrayList();
        for (String compatPinnedList : context.getSharedPreferences(ALL_FILES, 0).getStringSet(ALL_FILES, Collections.emptySet())) {
            arrayList.addAll(new CompatPinnedList(context, compatPinnedList).getPinnedSlices());
        }
        return arrayList;
    }

    private static ProviderHolder acquireClient(ContentResolver contentResolver, Uri uri) {
        ContentProviderClient acquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(uri);
        if (acquireUnstableContentProviderClient != null) {
            return new ProviderHolder(acquireUnstableContentProviderClient);
        }
        throw new IllegalArgumentException("No provider found for " + uri);
    }

    private static class ProviderHolder implements AutoCloseable {
        final ContentProviderClient mProvider;

        ProviderHolder(ContentProviderClient contentProviderClient) {
            this.mProvider = contentProviderClient;
        }

        public void close() {
            if (this.mProvider != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    this.mProvider.close();
                } else {
                    this.mProvider.release();
                }
            }
        }
    }
}
