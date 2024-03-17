package com.android.launcher3.icons.cache;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.FlagOp;
import com.android.launcher3.util.SQLiteCacheHelper;
import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public abstract class BaseIconCache {
    private static final boolean DEBUG = false;
    public static final String EMPTY_CLASS_NAME = ".";
    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
    private static final String TAG = "BaseIconCache";
    private final Looper mBgLooper;
    private final Map<ComponentKey, CacheEntry> mCache;
    protected final Context mContext;
    private final String mDbFileName;
    private BitmapInfo mDefaultIcon;
    protected IconDB mIconDb;
    protected int mIconDpi;
    protected LocaleList mLocaleList = LocaleList.getEmptyLocaleList();
    protected final PackageManager mPackageManager;
    protected String mSystemState = "";
    private final SparseArray<FlagOp> mUserFlagOpMap = new SparseArray<>();
    protected final Handler mWorkerHandler;

    public static class CacheEntry {
        public BitmapInfo bitmap = BitmapInfo.LOW_RES_INFO;
        public CharSequence contentDescription = "";
        public CharSequence title = "";
    }

    public abstract BaseIconFactory getIconFactory();

    /* access modifiers changed from: protected */
    public abstract long getSerialNumberForUser(UserHandle userHandle);

    /* access modifiers changed from: protected */
    public abstract boolean isInstantApp(ApplicationInfo applicationInfo);

    public BaseIconCache(Context context, String str, Looper looper, int i, int i2, boolean z) {
        this.mContext = context;
        this.mDbFileName = str;
        this.mPackageManager = context.getPackageManager();
        this.mBgLooper = looper;
        this.mWorkerHandler = new Handler(looper);
        if (z) {
            this.mCache = new HashMap(50);
        } else {
            this.mCache = new AbstractMap<ComponentKey, CacheEntry>() {
                public CacheEntry put(ComponentKey componentKey, CacheEntry cacheEntry) {
                    return cacheEntry;
                }

                public Set<Map.Entry<ComponentKey, CacheEntry>> entrySet() {
                    return Collections.emptySet();
                }
            };
        }
        updateSystemState();
        this.mIconDpi = i;
        this.mIconDb = new IconDB(context, str, i2);
    }

    public void updateIconParams(int i, int i2) {
        this.mWorkerHandler.post(new Runnable(i, i2) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                BaseIconCache.this.lambda$updateIconParams$0$BaseIconCache(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: updateIconParamsBg */
    public synchronized void lambda$updateIconParams$0$BaseIconCache(int i, int i2) {
        this.mIconDpi = i;
        this.mDefaultIcon = null;
        this.mUserFlagOpMap.clear();
        this.mIconDb.clear();
        this.mIconDb.close();
        this.mIconDb = new IconDB(this.mContext, this.mDbFileName, i2);
        this.mCache.clear();
    }

    private Drawable getFullResIcon(Resources resources, int i) {
        if (!(resources == null || i == 0)) {
            try {
                return resources.getDrawableForDensity(i, this.mIconDpi);
            } catch (Resources.NotFoundException unused) {
            }
        }
        return BaseIconFactory.getFullResDefaultActivityIcon(this.mIconDpi);
    }

    public Drawable getFullResIcon(String str, int i) {
        try {
            return getFullResIcon(this.mPackageManager.getResourcesForApplication(str), i);
        } catch (PackageManager.NameNotFoundException unused) {
            return BaseIconFactory.getFullResDefaultActivityIcon(this.mIconDpi);
        }
    }

    public Drawable getFullResIcon(ActivityInfo activityInfo) {
        try {
            return getFullResIcon(this.mPackageManager.getResourcesForApplication(activityInfo.applicationInfo), activityInfo.getIconResource());
        } catch (PackageManager.NameNotFoundException unused) {
            return BaseIconFactory.getFullResDefaultActivityIcon(this.mIconDpi);
        }
    }

    public synchronized void remove(ComponentName componentName, UserHandle userHandle) {
        this.mCache.remove(new ComponentKey(componentName, userHandle));
    }

    private void removeFromMemCacheLocked(String str, UserHandle userHandle) {
        HashSet hashSet = new HashSet();
        for (ComponentKey next : this.mCache.keySet()) {
            if (next.componentName.getPackageName().equals(str) && next.user.equals(userHandle)) {
                hashSet.add(next);
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            this.mCache.remove((ComponentKey) it.next());
        }
    }

    public synchronized void removeIconsForPkg(String str, UserHandle userHandle) {
        removeFromMemCacheLocked(str, userHandle);
        long serialNumberForUser = getSerialNumberForUser(userHandle);
        this.mIconDb.delete("componentName LIKE ? AND profileId = ?", new String[]{str + "/%", Long.toString(serialNumberForUser)});
    }

    public IconCacheUpdateHandler getUpdateHandler() {
        updateSystemState();
        return new IconCacheUpdateHandler(this);
    }

    private void updateSystemState() {
        this.mLocaleList = this.mContext.getResources().getConfiguration().getLocales();
        this.mSystemState = this.mLocaleList.toLanguageTags() + "," + Build.VERSION.SDK_INT;
    }

    /* access modifiers changed from: protected */
    public String getIconSystemState(String str) {
        return this.mSystemState;
    }

    public synchronized <T> void addIconToDBAndMemCache(T t, CachingLogic<T> cachingLogic, PackageInfo packageInfo, long j, boolean z) {
        CacheEntry cacheEntry;
        UserHandle user = cachingLogic.getUser(t);
        ComponentName component = cachingLogic.getComponent(t);
        ComponentKey componentKey = new ComponentKey(component, user);
        CacheEntry cacheEntry2 = null;
        if (!z && (cacheEntry = this.mCache.get(componentKey)) != null) {
            if (!cacheEntry.bitmap.isNullOrLowRes()) {
                cacheEntry2 = cacheEntry;
            }
        }
        if (cacheEntry2 == null) {
            cacheEntry2 = new CacheEntry();
            cacheEntry2.bitmap = cachingLogic.loadIcon(this.mContext, t);
        }
        if (!cacheEntry2.bitmap.isNullOrLowRes()) {
            CharSequence label = cachingLogic.getLabel(t);
            if (label == null) {
                Log.d(TAG, "No label returned from caching logic instance: " + cachingLogic);
            }
            cacheEntry2.title = label;
            cacheEntry2.contentDescription = this.mPackageManager.getUserBadgedLabel(cacheEntry2.title, user);
            if (cachingLogic.addToMemCache()) {
                this.mCache.put(componentKey, cacheEntry2);
            }
            addIconToDB(newContentValues(cacheEntry2.bitmap, cacheEntry2.title.toString(), component.getPackageName(), cachingLogic.getKeywords(t, this.mLocaleList)), component, packageInfo, j, cachingLogic.getLastUpdatedTime(t, packageInfo));
        }
    }

    private void addIconToDB(ContentValues contentValues, ComponentName componentName, PackageInfo packageInfo, long j, long j2) {
        contentValues.put(IconDB.COLUMN_COMPONENT, componentName.flattenToString());
        contentValues.put("profileId", Long.valueOf(j));
        contentValues.put(IconDB.COLUMN_LAST_UPDATED, Long.valueOf(j2));
        contentValues.put(IconDB.COLUMN_VERSION, Integer.valueOf(packageInfo.versionCode));
        this.mIconDb.insertOrReplace(contentValues);
    }

    public synchronized BitmapInfo getDefaultIcon(UserHandle userHandle) {
        if (this.mDefaultIcon == null) {
            BaseIconFactory iconFactory = getIconFactory();
            try {
                this.mDefaultIcon = iconFactory.makeDefaultIcon();
                if (iconFactory != null) {
                    iconFactory.close();
                }
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        }
        return this.mDefaultIcon.withFlags(getUserFlagOpLocked(userHandle));
        throw th;
    }

    /* access modifiers changed from: protected */
    public FlagOp getUserFlagOpLocked(UserHandle userHandle) {
        int hashCode = userHandle.hashCode();
        int indexOfKey = this.mUserFlagOpMap.indexOfKey(hashCode);
        if (indexOfKey >= 0) {
            return this.mUserFlagOpMap.valueAt(indexOfKey);
        }
        BaseIconFactory iconFactory = getIconFactory();
        try {
            FlagOp bitmapFlagOp = iconFactory.getBitmapFlagOp(new BaseIconFactory.IconOptions().setUser(userHandle));
            this.mUserFlagOpMap.put(hashCode, bitmapFlagOp);
            if (iconFactory != null) {
                iconFactory.close();
            }
            return bitmapFlagOp;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public boolean isDefaultIcon(BitmapInfo bitmapInfo, UserHandle userHandle) {
        return getDefaultIcon(userHandle).icon == bitmapInfo.icon;
    }

    /* access modifiers changed from: protected */
    public <T> CacheEntry cacheLocked(ComponentName componentName, UserHandle userHandle, Supplier<T> supplier, CachingLogic<T> cachingLogic, boolean z, boolean z2) {
        return cacheLocked(componentName, userHandle, supplier, cachingLogic, (Cursor) null, z, z2);
    }

    /* access modifiers changed from: protected */
    public <T> CacheEntry cacheLocked(ComponentName componentName, UserHandle userHandle, Supplier<T> supplier, CachingLogic<T> cachingLogic, Cursor cursor, boolean z, boolean z2) {
        boolean z3;
        UserHandle userHandle2 = userHandle;
        Cursor cursor2 = cursor;
        boolean z4 = z2;
        assertWorkerThread();
        ComponentName componentName2 = componentName;
        ComponentKey componentKey = new ComponentKey(componentName, userHandle);
        CacheEntry cacheEntry = this.mCache.get(componentKey);
        if (cacheEntry != null && (!cacheEntry.bitmap.isLowRes() || z4)) {
            return cacheEntry;
        }
        CacheEntry cacheEntry2 = new CacheEntry();
        if (cachingLogic.addToMemCache()) {
            this.mCache.put(componentKey, cacheEntry2);
        }
        T t = null;
        boolean z5 = false;
        if (cursor2 == null) {
            z3 = getEntryFromDBLocked(componentKey, cacheEntry2, z4);
        } else {
            z3 = updateTitleAndIconLocked(componentKey, cacheEntry2, cursor2, z4);
        }
        if (!z3) {
            T t2 = supplier.get();
            loadFallbackIcon(t2, cacheEntry2, cachingLogic, z, true, componentName, userHandle);
            t = t2;
            z5 = true;
        }
        if (TextUtils.isEmpty(cacheEntry2.title)) {
            if (t == null && !z5) {
                t = supplier.get();
            }
            if (t != null) {
                loadFallbackTitle(t, cacheEntry2, cachingLogic, userHandle);
            }
        }
        return cacheEntry2;
    }

    /* access modifiers changed from: protected */
    public <T> void loadFallbackIcon(T t, CacheEntry cacheEntry, CachingLogic<T> cachingLogic, boolean z, boolean z2, ComponentName componentName, UserHandle userHandle) {
        CacheEntry entryForPackageLocked;
        if (t != null) {
            cacheEntry.bitmap = cachingLogic.loadIcon(this.mContext, t);
            return;
        }
        if (z && (entryForPackageLocked = getEntryForPackageLocked(componentName.getPackageName(), userHandle, false)) != null) {
            cacheEntry.bitmap = entryForPackageLocked.bitmap;
            cacheEntry.contentDescription = entryForPackageLocked.contentDescription;
            if (z2) {
                cacheEntry.title = entryForPackageLocked.title;
            }
        }
        if (cacheEntry.bitmap == null) {
            cacheEntry.bitmap = getDefaultIcon(userHandle);
        }
    }

    /* access modifiers changed from: protected */
    public <T> void loadFallbackTitle(T t, CacheEntry cacheEntry, CachingLogic<T> cachingLogic, UserHandle userHandle) {
        cacheEntry.title = cachingLogic.getLabel(t);
        cacheEntry.contentDescription = this.mPackageManager.getUserBadgedLabel(cachingLogic.getDescription(t, cacheEntry.title), userHandle);
    }

    public synchronized void clear() {
        assertWorkerThread();
        this.mIconDb.clear();
    }

    /* access modifiers changed from: protected */
    public synchronized void cachePackageInstallInfo(String str, UserHandle userHandle, Bitmap bitmap, CharSequence charSequence) {
        removeFromMemCacheLocked(str, userHandle);
        ComponentKey packageKey = getPackageKey(str, userHandle);
        CacheEntry cacheEntry = this.mCache.get(packageKey);
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry();
        }
        if (!TextUtils.isEmpty(charSequence)) {
            cacheEntry.title = charSequence;
        }
        if (bitmap != null) {
            BaseIconFactory iconFactory = getIconFactory();
            cacheEntry.bitmap = iconFactory.createShapedIconBitmap(bitmap, new BaseIconFactory.IconOptions().setUser(userHandle));
            iconFactory.close();
        }
        if (!TextUtils.isEmpty(charSequence) && cacheEntry.bitmap.icon != null) {
            this.mCache.put(packageKey, cacheEntry);
        }
    }

    private static ComponentKey getPackageKey(String str, UserHandle userHandle) {
        return new ComponentKey(new ComponentName(str, str + EMPTY_CLASS_NAME), userHandle);
    }

    /* access modifiers changed from: protected */
    public CacheEntry getEntryForPackageLocked(String str, UserHandle userHandle, boolean z) {
        String str2 = str;
        UserHandle userHandle2 = userHandle;
        boolean z2 = z;
        assertWorkerThread();
        ComponentKey packageKey = getPackageKey(str, userHandle);
        CacheEntry cacheEntry = this.mCache.get(packageKey);
        if (cacheEntry != null && (!cacheEntry.bitmap.isLowRes() || z2)) {
            return cacheEntry;
        }
        CacheEntry cacheEntry2 = new CacheEntry();
        boolean z3 = true;
        if (!getEntryFromDBLocked(packageKey, cacheEntry2, z2)) {
            try {
                PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str2, Process.myUserHandle().equals(userHandle2) ? 0 : 8192);
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if (applicationInfo != null) {
                    BaseIconFactory iconFactory = getIconFactory();
                    BitmapInfo createBadgedIconBitmap = iconFactory.createBadgedIconBitmap(applicationInfo.loadIcon(this.mPackageManager), new BaseIconFactory.IconOptions().setUser(userHandle2).setInstantApp(isInstantApp(applicationInfo)));
                    iconFactory.close();
                    cacheEntry2.title = applicationInfo.loadLabel(this.mPackageManager);
                    cacheEntry2.contentDescription = this.mPackageManager.getUserBadgedLabel(cacheEntry2.title, userHandle2);
                    cacheEntry2.bitmap = BitmapInfo.of(z2 ? BitmapInfo.LOW_RES_ICON : createBadgedIconBitmap.icon, createBadgedIconBitmap.color);
                    addIconToDB(newContentValues(createBadgedIconBitmap, cacheEntry2.title.toString(), str2, (String) null), packageKey.componentName, packageInfo, getSerialNumberForUser(userHandle2), packageInfo.lastUpdateTime);
                } else {
                    throw new PackageManager.NameNotFoundException("ApplicationInfo is null");
                }
            } catch (PackageManager.NameNotFoundException unused) {
                z3 = false;
            }
        }
        if (z3) {
            this.mCache.put(packageKey, cacheEntry2);
        }
        return cacheEntry2;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0041, code lost:
        if (r1 != null) goto L_0x0050;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004e, code lost:
        if (r1 == null) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0050, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0053, code lost:
        android.os.Trace.endSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0056, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean getEntryFromDBLocked(com.android.launcher3.util.ComponentKey r10, com.android.launcher3.icons.cache.BaseIconCache.CacheEntry r11, boolean r12) {
        /*
            r9 = this;
            java.lang.String r0 = "loadIconIndividually"
            android.os.Trace.beginSection(r0)
            r0 = 0
            r1 = 0
            com.android.launcher3.icons.cache.BaseIconCache$IconDB r2 = r9.mIconDb     // Catch:{ SQLiteException -> 0x0046 }
            if (r12 == 0) goto L_0x000e
            java.lang.String[] r3 = com.android.launcher3.icons.cache.BaseIconCache.IconDB.COLUMNS_LOW_RES     // Catch:{ SQLiteException -> 0x0046 }
            goto L_0x0010
        L_0x000e:
            java.lang.String[] r3 = com.android.launcher3.icons.cache.BaseIconCache.IconDB.COLUMNS_HIGH_RES     // Catch:{ SQLiteException -> 0x0046 }
        L_0x0010:
            java.lang.String r4 = "componentName = ? AND profileId = ?"
            r5 = 2
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ SQLiteException -> 0x0046 }
            android.content.ComponentName r6 = r10.componentName     // Catch:{ SQLiteException -> 0x0046 }
            java.lang.String r6 = r6.flattenToString()     // Catch:{ SQLiteException -> 0x0046 }
            r5[r0] = r6     // Catch:{ SQLiteException -> 0x0046 }
            r6 = 1
            android.os.UserHandle r7 = r10.user     // Catch:{ SQLiteException -> 0x0046 }
            long r7 = r9.getSerialNumberForUser(r7)     // Catch:{ SQLiteException -> 0x0046 }
            java.lang.String r7 = java.lang.Long.toString(r7)     // Catch:{ SQLiteException -> 0x0046 }
            r5[r6] = r7     // Catch:{ SQLiteException -> 0x0046 }
            android.database.Cursor r1 = r2.query(r3, r4, r5)     // Catch:{ SQLiteException -> 0x0046 }
            boolean r2 = r1.moveToNext()     // Catch:{ SQLiteException -> 0x0046 }
            if (r2 == 0) goto L_0x0041
            boolean r10 = r9.updateTitleAndIconLocked(r10, r11, r1, r12)     // Catch:{ SQLiteException -> 0x0046 }
            if (r1 == 0) goto L_0x003d
            r1.close()
        L_0x003d:
            android.os.Trace.endSection()
            return r10
        L_0x0041:
            if (r1 == 0) goto L_0x0053
            goto L_0x0050
        L_0x0044:
            r10 = move-exception
            goto L_0x0057
        L_0x0046:
            r10 = move-exception
            java.lang.String r11 = "BaseIconCache"
            java.lang.String r12 = "Error reading icon cache"
            android.util.Log.d(r11, r12, r10)     // Catch:{ all -> 0x0044 }
            if (r1 == 0) goto L_0x0053
        L_0x0050:
            r1.close()
        L_0x0053:
            android.os.Trace.endSection()
            return r0
        L_0x0057:
            if (r1 == 0) goto L_0x005c
            r1.close()
        L_0x005c:
            android.os.Trace.endSection()
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.cache.BaseIconCache.getEntryFromDBLocked(com.android.launcher3.util.ComponentKey, com.android.launcher3.icons.cache.BaseIconCache$CacheEntry, boolean):boolean");
    }

    private boolean updateTitleAndIconLocked(ComponentKey componentKey, CacheEntry cacheEntry, Cursor cursor, boolean z) {
        cacheEntry.bitmap = BitmapInfo.of(BitmapInfo.LOW_RES_ICON, GraphicsUtils.setColorAlphaBound(cursor.getInt(IconDB.INDEX_COLOR), 255));
        cacheEntry.title = cursor.getString(IconDB.INDEX_TITLE);
        if (cacheEntry.title == null) {
            cacheEntry.title = "";
            cacheEntry.contentDescription = "";
        } else {
            cacheEntry.contentDescription = this.mPackageManager.getUserBadgedLabel(cacheEntry.title, componentKey.user);
        }
        if (!z) {
            byte[] blob = cursor.getBlob(IconDB.INDEX_ICON);
            if (blob == null) {
                return false;
            }
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.HARDWARE;
                cacheEntry.bitmap = BitmapInfo.of((Bitmap) Objects.requireNonNull(BitmapFactory.decodeByteArray(blob, 0, blob.length, options)), cacheEntry.bitmap.color);
                byte[] blob2 = cursor.getBlob(IconDB.INDEX_MONO_ICON);
                Bitmap bitmap = cacheEntry.bitmap.icon;
                if (blob2 != null && blob2.length == bitmap.getHeight() * bitmap.getWidth()) {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ALPHA_8);
                    createBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(blob2));
                    Bitmap copy = createBitmap.copy(Bitmap.Config.HARDWARE, false);
                    if (copy != null) {
                        createBitmap.recycle();
                        createBitmap = copy;
                    }
                    BaseIconFactory iconFactory = getIconFactory();
                    try {
                        cacheEntry.bitmap.setMonoIcon(createBitmap, iconFactory);
                        if (iconFactory != null) {
                            iconFactory.close();
                        }
                    } catch (Throwable th) {
                        th.addSuppressed(th);
                    }
                }
            } catch (Exception unused) {
                return false;
            }
        }
        cacheEntry.bitmap.flags = cursor.getInt(IconDB.INDEX_FLAGS);
        cacheEntry.bitmap = cacheEntry.bitmap.withFlags(getUserFlagOpLocked(componentKey.user));
        if (cacheEntry.bitmap != null) {
            return true;
        }
        return false;
        throw th;
    }

    public synchronized Cursor queryCacheDb(String[] strArr, String str, String[] strArr2) {
        return this.mIconDb.query(strArr, str, strArr2);
    }

    public static final class IconDB extends SQLiteCacheHelper {
        public static final String[] COLUMNS_HIGH_RES;
        public static final String[] COLUMNS_LOW_RES;
        public static final String COLUMN_COMPONENT = "componentName";
        public static final String COLUMN_FLAGS = "flags";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_ICON_COLOR = "icon_color";
        public static final String COLUMN_KEYWORDS = "keywords";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_LAST_UPDATED = "lastUpdated";
        public static final String COLUMN_MONO_ICON = "mono_icon";
        public static final String COLUMN_ROWID = "rowid";
        public static final String COLUMN_SYSTEM_STATE = "system_state";
        public static final String COLUMN_USER = "profileId";
        public static final String COLUMN_VERSION = "version";
        /* access modifiers changed from: private */
        public static final int INDEX_COLOR;
        /* access modifiers changed from: private */
        public static final int INDEX_FLAGS;
        /* access modifiers changed from: private */
        public static final int INDEX_ICON;
        /* access modifiers changed from: private */
        public static final int INDEX_MONO_ICON;
        /* access modifiers changed from: private */
        public static final int INDEX_TITLE;
        private static final int RELEASE_VERSION = 34;
        public static final String TABLE_NAME = "icons";

        static {
            String[] strArr = {COLUMN_COMPONENT, COLUMN_LABEL, COLUMN_ICON_COLOR, "flags"};
            COLUMNS_LOW_RES = strArr;
            String[] strArr2 = (String[]) Arrays.copyOf(strArr, strArr.length + 2, String[].class);
            COLUMNS_HIGH_RES = strArr2;
            strArr2[strArr.length] = "icon";
            strArr2[strArr.length + 1] = COLUMN_MONO_ICON;
            INDEX_TITLE = Arrays.asList(strArr).indexOf(COLUMN_LABEL);
            INDEX_COLOR = Arrays.asList(strArr).indexOf(COLUMN_ICON_COLOR);
            INDEX_FLAGS = Arrays.asList(strArr).indexOf("flags");
            int length = strArr.length;
            INDEX_ICON = length;
            INDEX_MONO_ICON = length + 1;
        }

        public IconDB(Context context, String str, int i) {
            super(context, str, i + 2228224, TABLE_NAME);
        }

        /* access modifiers changed from: protected */
        public void onCreateTable(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS icons (componentName TEXT NOT NULL, profileId INTEGER NOT NULL, lastUpdated INTEGER NOT NULL DEFAULT 0, version INTEGER NOT NULL DEFAULT 0, icon BLOB, mono_icon BLOB, icon_color INTEGER NOT NULL DEFAULT 0, flags INTEGER NOT NULL DEFAULT 0, label TEXT, system_state TEXT, keywords TEXT, PRIMARY KEY (componentName, profileId) );");
        }
    }

    private ContentValues newContentValues(BitmapInfo bitmapInfo, String str, String str2, String str3) {
        ContentValues contentValues = new ContentValues();
        if (bitmapInfo.canPersist()) {
            contentValues.put("icon", GraphicsUtils.flattenBitmap(bitmapInfo.icon));
            Bitmap mono = bitmapInfo.getMono();
            if (mono != null && mono.getHeight() == bitmapInfo.icon.getHeight() && mono.getWidth() == bitmapInfo.icon.getWidth() && mono.getConfig() == Bitmap.Config.ALPHA_8) {
                byte[] bArr = new byte[(mono.getWidth() * mono.getHeight())];
                mono.copyPixelsToBuffer(ByteBuffer.wrap(bArr));
                contentValues.put(IconDB.COLUMN_MONO_ICON, bArr);
            } else {
                contentValues.put(IconDB.COLUMN_MONO_ICON, (byte[]) null);
            }
        } else {
            byte[] bArr2 = null;
            contentValues.put("icon", bArr2);
            contentValues.put(IconDB.COLUMN_MONO_ICON, bArr2);
        }
        contentValues.put(IconDB.COLUMN_ICON_COLOR, Integer.valueOf(bitmapInfo.color));
        contentValues.put("flags", Integer.valueOf(bitmapInfo.flags));
        contentValues.put(IconDB.COLUMN_LABEL, str);
        contentValues.put(IconDB.COLUMN_SYSTEM_STATE, getIconSystemState(str2));
        contentValues.put(IconDB.COLUMN_KEYWORDS, str3);
        return contentValues;
    }

    private void assertWorkerThread() {
        if (Looper.myLooper() != this.mBgLooper) {
            throw new IllegalStateException("Cache accessed on wrong thread " + Looper.myLooper());
        }
    }
}
