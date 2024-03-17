package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.util.Xml;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.qsb.QsbContainerView;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.PackageManagerHelper;
import java.io.IOException;
import java.util.Locale;
import java.util.function.Supplier;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AutoInstallsLayout {
    static final String ACTION_LAUNCHER_CUSTOMIZATION = "android.autoinstalls.config.action.PLAY_AUTO_INSTALL";
    private static final String ATTR_CLASS_NAME = "className";
    private static final String ATTR_CONTAINER = "container";
    private static final String ATTR_ICON = "icon";
    private static final String ATTR_KEY = "key";
    private static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_RANK = "rank";
    private static final String ATTR_SCREEN = "screen";
    private static final String ATTR_SPAN_X = "spanX";
    private static final String ATTR_SPAN_Y = "spanY";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_TITLE_TEXT = "titleText";
    private static final String ATTR_URL = "url";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_WORKSPACE = "workspace";
    private static final String ATTR_X = "x";
    private static final String ATTR_Y = "y";
    private static final String FORMATTED_LAYOUT_RES = "default_layout_%dx%d";
    private static final String FORMATTED_LAYOUT_RES_WITH_HOSTEAT = "default_layout_%dx%d_h%s";
    private static final String HOTSEAT_CONTAINER_NAME = LauncherSettings.Favorites.containerToString(LauncherSettings.Favorites.CONTAINER_HOTSEAT);
    private static final String LAYOUT_RES = "default_layout";
    private static final boolean LOGD = false;
    private static final String TAG = "AutoInstalls";
    private static final String TAG_APPWIDGET = "appwidget";
    private static final String TAG_APP_ICON = "appicon";
    private static final String TAG_AUTO_INSTALL = "autoinstall";
    private static final String TAG_EXTRA = "extra";
    private static final String TAG_FOLDER = "folder";
    private static final String TAG_INCLUDE = "include";
    protected static final String TAG_SEARCH_WIDGET = "searchwidget";
    private static final String TAG_SHORTCUT = "shortcut";
    public static final String TAG_WORKSPACE = "workspace";
    final AppWidgetHost mAppWidgetHost;
    protected final LayoutParserCallback mCallback;
    private final int mColumnCount;
    final Context mContext;
    protected SQLiteDatabase mDb;
    private final InvariantDeviceProfile mIdp;
    protected final Supplier<XmlPullParser> mInitialLayoutSupplier;
    protected final PackageManager mPackageManager;
    protected final String mRootTag;
    private final int mRowCount;
    protected final Resources mSourceRes;
    private final int[] mTemp;
    final ContentValues mValues;

    public interface LayoutParserCallback {
        int generateNewItemId();

        int insertAndCheck(SQLiteDatabase sQLiteDatabase, ContentValues contentValues);
    }

    protected interface TagParser {
        int parseAndAdd(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException;
    }

    static AutoInstallsLayout get(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback layoutParserCallback) {
        int i;
        Pair<String, Resources> findSystemApk = PackageManagerHelper.findSystemApk(ACTION_LAUNCHER_CUSTOMIZATION, context.getPackageManager());
        if (findSystemApk == null) {
            return null;
        }
        String str = (String) findSystemApk.first;
        Resources resources = (Resources) findSystemApk.second;
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        String format = String.format(Locale.ENGLISH, FORMATTED_LAYOUT_RES_WITH_HOSTEAT, new Object[]{Integer.valueOf(idp.numColumns), Integer.valueOf(idp.numRows), Integer.valueOf(idp.numDatabaseHotseatIcons)});
        int identifier = resources.getIdentifier(format, "xml", str);
        if (identifier == 0) {
            Log.d(TAG, "Formatted layout: " + format + " not found. Trying layout without hosteat");
            format = String.format(Locale.ENGLISH, FORMATTED_LAYOUT_RES, new Object[]{Integer.valueOf(idp.numColumns), Integer.valueOf(idp.numRows)});
            identifier = resources.getIdentifier(format, "xml", str);
        }
        if (identifier == 0) {
            Log.d(TAG, "Formatted layout: " + format + " not found. Trying the default layout");
            i = resources.getIdentifier(LAYOUT_RES, "xml", str);
        } else {
            i = identifier;
        }
        if (i != 0) {
            return new AutoInstallsLayout(context, appWidgetHost, layoutParserCallback, resources, i, "workspace");
        }
        Log.e(TAG, "Layout definition not found in package: " + str);
        return null;
    }

    public AutoInstallsLayout(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback layoutParserCallback, Resources resources, int i, String str) {
        this(context, appWidgetHost, layoutParserCallback, resources, (Supplier<XmlPullParser>) new Supplier(resources, i) {
            public final /* synthetic */ Resources f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get() {
                return this.f$0.getXml(this.f$1);
            }
        }, str);
    }

    public AutoInstallsLayout(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback layoutParserCallback, Resources resources, Supplier<XmlPullParser> supplier, String str) {
        this.mTemp = new int[2];
        this.mContext = context;
        this.mAppWidgetHost = appWidgetHost;
        this.mCallback = layoutParserCallback;
        this.mPackageManager = context.getPackageManager();
        this.mValues = new ContentValues();
        this.mRootTag = str;
        this.mSourceRes = resources;
        this.mInitialLayoutSupplier = supplier;
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        this.mIdp = idp;
        this.mRowCount = idp.numRows;
        this.mColumnCount = idp.numColumns;
    }

    public int loadLayout(SQLiteDatabase sQLiteDatabase, IntArray intArray) {
        this.mDb = sQLiteDatabase;
        try {
            return parseLayout(this.mInitialLayoutSupplier.get(), intArray);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing layout: ", e);
            return -1;
        }
    }

    /* access modifiers changed from: protected */
    public int parseLayout(XmlPullParser xmlPullParser, IntArray intArray) throws XmlPullParserException, IOException {
        beginDocument(xmlPullParser, this.mRootTag);
        int depth = xmlPullParser.getDepth();
        ArrayMap<String, TagParser> layoutElementsMap = getLayoutElementsMap();
        int i = 0;
        while (true) {
            int next = xmlPullParser.next();
            if ((next != 3 || xmlPullParser.getDepth() > depth) && next != 1) {
                if (next == 2) {
                    i += parseAndAddNode(xmlPullParser, layoutElementsMap, intArray);
                }
            }
        }
        return i;
    }

    /* access modifiers changed from: protected */
    public void parseContainerAndScreen(XmlPullParser xmlPullParser, int[] iArr) {
        if (HOTSEAT_CONTAINER_NAME.equals(getAttributeValue(xmlPullParser, "container"))) {
            iArr[0] = -101;
            iArr[1] = Integer.parseInt(getAttributeValue(xmlPullParser, "rank"));
            return;
        }
        iArr[0] = -100;
        iArr[1] = Integer.parseInt(getAttributeValue(xmlPullParser, "screen"));
    }

    /* access modifiers changed from: protected */
    public int parseAndAddNode(XmlPullParser xmlPullParser, ArrayMap<String, TagParser> arrayMap, IntArray intArray) throws XmlPullParserException, IOException {
        if (TAG_INCLUDE.equals(xmlPullParser.getName())) {
            int attributeResourceValue = getAttributeResourceValue(xmlPullParser, "workspace", 0);
            if (attributeResourceValue != 0) {
                return parseLayout(this.mSourceRes.getXml(attributeResourceValue), intArray);
            }
            return 0;
        }
        this.mValues.clear();
        parseContainerAndScreen(xmlPullParser, this.mTemp);
        int[] iArr = this.mTemp;
        int i = iArr[0];
        int i2 = iArr[1];
        this.mValues.put("container", Integer.valueOf(i));
        this.mValues.put("screen", Integer.valueOf(i2));
        this.mValues.put(LauncherSettings.Favorites.CELLX, convertToDistanceFromEnd(getAttributeValue(xmlPullParser, ATTR_X), this.mColumnCount));
        this.mValues.put(LauncherSettings.Favorites.CELLY, convertToDistanceFromEnd(getAttributeValue(xmlPullParser, ATTR_Y), this.mRowCount));
        TagParser tagParser = arrayMap.get(xmlPullParser.getName());
        if (tagParser == null || tagParser.parseAndAdd(xmlPullParser) < 0) {
            return 0;
        }
        if (!intArray.contains(i2) && i == -100) {
            intArray.add(i2);
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    public int addShortcut(String str, Intent intent, int i) {
        int generateNewItemId = this.mCallback.generateNewItemId();
        this.mValues.put(LauncherSettings.Favorites.INTENT, intent.toUri(0));
        this.mValues.put("title", str);
        this.mValues.put(LauncherSettings.Favorites.ITEM_TYPE, Integer.valueOf(i));
        this.mValues.put("spanX", 1);
        this.mValues.put("spanY", 1);
        this.mValues.put("_id", Integer.valueOf(generateNewItemId));
        if (this.mCallback.insertAndCheck(this.mDb, this.mValues) < 0) {
            return -1;
        }
        return generateNewItemId;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, TagParser> getFolderElementsMap() {
        ArrayMap<String, TagParser> arrayMap = new ArrayMap<>();
        arrayMap.put(TAG_APP_ICON, new AppShortcutParser());
        arrayMap.put(TAG_AUTO_INSTALL, new AutoInstallParser());
        arrayMap.put(TAG_SHORTCUT, new ShortcutParser(this.mSourceRes));
        return arrayMap;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, TagParser> getLayoutElementsMap() {
        ArrayMap<String, TagParser> arrayMap = new ArrayMap<>();
        arrayMap.put(TAG_APP_ICON, new AppShortcutParser());
        arrayMap.put(TAG_AUTO_INSTALL, new AutoInstallParser());
        arrayMap.put(TAG_FOLDER, new FolderParser(this));
        arrayMap.put(TAG_APPWIDGET, new PendingWidgetParser());
        arrayMap.put(TAG_SEARCH_WIDGET, new SearchWidgetParser());
        arrayMap.put(TAG_SHORTCUT, new ShortcutParser(this.mSourceRes));
        return arrayMap;
    }

    protected class AppShortcutParser implements TagParser {
        protected AppShortcutParser() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x006f, code lost:
            android.util.Log.e(com.android.launcher3.AutoInstallsLayout.TAG, "Favorite not found: " + r0 + "/" + r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0092, code lost:
            return -1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
            r3 = new android.content.ComponentName(r8.this$0.mPackageManager.currentToCanonicalPackageNames(new java.lang.String[]{r0})[0], r1);
            r7 = r3;
            r3 = r8.this$0.mPackageManager.getActivityInfo(r3, 0);
            r2 = r7;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0027 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int parseAndAdd(org.xmlpull.v1.XmlPullParser r9) {
            /*
                r8 = this;
                java.lang.String r0 = "packageName"
                java.lang.String r0 = com.android.launcher3.AutoInstallsLayout.getAttributeValue(r9, r0)
                java.lang.String r1 = "className"
                java.lang.String r1 = com.android.launcher3.AutoInstallsLayout.getAttributeValue(r9, r1)
                boolean r2 = android.text.TextUtils.isEmpty(r0)
                if (r2 != 0) goto L_0x0093
                boolean r2 = android.text.TextUtils.isEmpty(r1)
                if (r2 != 0) goto L_0x0093
                r9 = 0
                android.content.ComponentName r2 = new android.content.ComponentName     // Catch:{ NameNotFoundException -> 0x0027 }
                r2.<init>(r0, r1)     // Catch:{ NameNotFoundException -> 0x0027 }
                com.android.launcher3.AutoInstallsLayout r3 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x0027 }
                android.content.pm.PackageManager r3 = r3.mPackageManager     // Catch:{ NameNotFoundException -> 0x0027 }
                android.content.pm.ActivityInfo r3 = r3.getActivityInfo(r2, r9)     // Catch:{ NameNotFoundException -> 0x0027 }
                goto L_0x0046
            L_0x0027:
                com.android.launcher3.AutoInstallsLayout r2 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x006f }
                android.content.pm.PackageManager r2 = r2.mPackageManager     // Catch:{ NameNotFoundException -> 0x006f }
                r3 = 1
                java.lang.String[] r3 = new java.lang.String[r3]     // Catch:{ NameNotFoundException -> 0x006f }
                r3[r9] = r0     // Catch:{ NameNotFoundException -> 0x006f }
                java.lang.String[] r2 = r2.currentToCanonicalPackageNames(r3)     // Catch:{ NameNotFoundException -> 0x006f }
                android.content.ComponentName r3 = new android.content.ComponentName     // Catch:{ NameNotFoundException -> 0x006f }
                r2 = r2[r9]     // Catch:{ NameNotFoundException -> 0x006f }
                r3.<init>(r2, r1)     // Catch:{ NameNotFoundException -> 0x006f }
                com.android.launcher3.AutoInstallsLayout r2 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x006f }
                android.content.pm.PackageManager r2 = r2.mPackageManager     // Catch:{ NameNotFoundException -> 0x006f }
                android.content.pm.ActivityInfo r2 = r2.getActivityInfo(r3, r9)     // Catch:{ NameNotFoundException -> 0x006f }
                r7 = r3
                r3 = r2
                r2 = r7
            L_0x0046:
                android.content.Intent r4 = new android.content.Intent     // Catch:{ NameNotFoundException -> 0x006f }
                java.lang.String r5 = "android.intent.action.MAIN"
                r6 = 0
                r4.<init>(r5, r6)     // Catch:{ NameNotFoundException -> 0x006f }
                java.lang.String r5 = "android.intent.category.LAUNCHER"
                android.content.Intent r4 = r4.addCategory(r5)     // Catch:{ NameNotFoundException -> 0x006f }
                android.content.Intent r2 = r4.setComponent(r2)     // Catch:{ NameNotFoundException -> 0x006f }
                r4 = 270532608(0x10200000, float:3.1554436E-29)
                android.content.Intent r2 = r2.setFlags(r4)     // Catch:{ NameNotFoundException -> 0x006f }
                com.android.launcher3.AutoInstallsLayout r4 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x006f }
                android.content.pm.PackageManager r5 = r4.mPackageManager     // Catch:{ NameNotFoundException -> 0x006f }
                java.lang.CharSequence r3 = r3.loadLabel(r5)     // Catch:{ NameNotFoundException -> 0x006f }
                java.lang.String r3 = r3.toString()     // Catch:{ NameNotFoundException -> 0x006f }
                int r9 = r4.addShortcut(r3, r2, r9)     // Catch:{ NameNotFoundException -> 0x006f }
                return r9
            L_0x006f:
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r2 = "Favorite not found: "
                java.lang.StringBuilder r9 = r9.append(r2)
                java.lang.StringBuilder r9 = r9.append(r0)
                java.lang.String r0 = "/"
                java.lang.StringBuilder r9 = r9.append(r0)
                java.lang.StringBuilder r9 = r9.append(r1)
                java.lang.String r9 = r9.toString()
                java.lang.String r0 = "AutoInstalls"
                android.util.Log.e(r0, r9)
                r9 = -1
                return r9
            L_0x0093:
                int r9 = r8.invalidPackageOrClass(r9)
                return r9
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AutoInstallsLayout.AppShortcutParser.parseAndAdd(org.xmlpull.v1.XmlPullParser):int");
        }

        /* access modifiers changed from: protected */
        public int invalidPackageOrClass(XmlPullParser xmlPullParser) {
            Log.w(AutoInstallsLayout.TAG, "Skipping invalid <favorite> with no component");
            return -1;
        }
    }

    protected class AutoInstallParser implements TagParser {
        protected AutoInstallParser() {
        }

        public int parseAndAdd(XmlPullParser xmlPullParser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlPullParser, AutoInstallsLayout.ATTR_PACKAGE_NAME);
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(xmlPullParser, AutoInstallsLayout.ATTR_CLASS_NAME);
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                return -1;
            }
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.RESTORED, 2);
            Intent flags = new Intent("android.intent.action.MAIN", (Uri) null).addCategory("android.intent.category.LAUNCHER").setComponent(new ComponentName(attributeValue, attributeValue2)).setFlags(270532608);
            AutoInstallsLayout autoInstallsLayout = AutoInstallsLayout.this;
            return autoInstallsLayout.addShortcut(autoInstallsLayout.mContext.getString(R.string.package_state_unknown), flags, 0);
        }
    }

    protected class ShortcutParser implements TagParser {
        private final Resources mIconRes;

        public ShortcutParser(Resources resources) {
            this.mIconRes = resources;
        }

        public int parseAndAdd(XmlPullParser xmlPullParser) {
            Intent parseIntent;
            Drawable drawable;
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(xmlPullParser, "title", 0);
            int attributeResourceValue2 = AutoInstallsLayout.getAttributeResourceValue(xmlPullParser, "icon", 0);
            if (attributeResourceValue == 0 || attributeResourceValue2 == 0 || (parseIntent = parseIntent(xmlPullParser)) == null || (drawable = this.mIconRes.getDrawable(attributeResourceValue2)) == null) {
                return -1;
            }
            LauncherIcons obtain = LauncherIcons.obtain(AutoInstallsLayout.this.mContext);
            AutoInstallsLayout.this.mValues.put("icon", GraphicsUtils.flattenBitmap(obtain.createBadgedIconBitmap(drawable).icon));
            obtain.recycle();
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.ICON_PACKAGE, this.mIconRes.getResourcePackageName(attributeResourceValue2));
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.ICON_RESOURCE, this.mIconRes.getResourceName(attributeResourceValue2));
            parseIntent.setFlags(270532608);
            AutoInstallsLayout autoInstallsLayout = AutoInstallsLayout.this;
            return autoInstallsLayout.addShortcut(autoInstallsLayout.mSourceRes.getString(attributeResourceValue), parseIntent, 1);
        }

        /* access modifiers changed from: protected */
        public Intent parseIntent(XmlPullParser xmlPullParser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlPullParser, AutoInstallsLayout.ATTR_URL);
            if (TextUtils.isEmpty(attributeValue) || !Patterns.WEB_URL.matcher(attributeValue).matches()) {
                return null;
            }
            return new Intent("android.intent.action.VIEW", (Uri) null).setData(Uri.parse(attributeValue));
        }
    }

    protected class PendingWidgetParser implements TagParser {
        protected PendingWidgetParser() {
        }

        public ComponentName getComponentName(XmlPullParser xmlPullParser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlPullParser, AutoInstallsLayout.ATTR_PACKAGE_NAME);
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(xmlPullParser, AutoInstallsLayout.ATTR_CLASS_NAME);
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                return null;
            }
            return new ComponentName(attributeValue, attributeValue2);
        }

        public int parseAndAdd(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            ComponentName componentName = getComponentName(xmlPullParser);
            if (componentName == null) {
                return -1;
            }
            AutoInstallsLayout.this.mValues.put("spanX", AutoInstallsLayout.getAttributeValue(xmlPullParser, "spanX"));
            AutoInstallsLayout.this.mValues.put("spanY", AutoInstallsLayout.getAttributeValue(xmlPullParser, "spanY"));
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.ITEM_TYPE, 4);
            Bundle bundle = new Bundle();
            int depth = xmlPullParser.getDepth();
            while (true) {
                int next = xmlPullParser.next();
                if (next == 3 && xmlPullParser.getDepth() <= depth) {
                    return verifyAndInsert(componentName, bundle);
                }
                if (next == 2) {
                    if (AutoInstallsLayout.TAG_EXTRA.equals(xmlPullParser.getName())) {
                        String attributeValue = AutoInstallsLayout.getAttributeValue(xmlPullParser, AutoInstallsLayout.ATTR_KEY);
                        String attributeValue2 = AutoInstallsLayout.getAttributeValue(xmlPullParser, "value");
                        if (attributeValue != null && attributeValue2 != null) {
                            bundle.putString(attributeValue, attributeValue2);
                        }
                    } else {
                        throw new RuntimeException("Widgets can contain only extras");
                    }
                }
            }
            throw new RuntimeException("Widget extras must have a key and value");
        }

        /* access modifiers changed from: protected */
        public int verifyAndInsert(ComponentName componentName, Bundle bundle) {
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, componentName.flattenToString());
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.RESTORED, 35);
            AutoInstallsLayout.this.mValues.put("_id", Integer.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            if (!bundle.isEmpty()) {
                AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.INTENT, new Intent().putExtras(bundle).toUri(0));
            }
            int insertAndCheck = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (insertAndCheck < 0) {
                return -1;
            }
            return insertAndCheck;
        }
    }

    protected class SearchWidgetParser extends PendingWidgetParser {
        protected SearchWidgetParser() {
            super();
        }

        public ComponentName getComponentName(XmlPullParser xmlPullParser) {
            return QsbContainerView.getSearchComponentName(AutoInstallsLayout.this.mContext);
        }

        /* access modifiers changed from: protected */
        public int verifyAndInsert(ComponentName componentName, Bundle bundle) {
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.OPTIONS, 1);
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(AutoInstallsLayout.this.mValues.getAsInteger(LauncherSettings.Favorites.RESTORED).intValue() | 4));
            return super.verifyAndInsert(componentName, bundle);
        }
    }

    protected class FolderParser implements TagParser {
        private final ArrayMap<String, TagParser> mFolderElements;

        public FolderParser(AutoInstallsLayout autoInstallsLayout) {
            this(autoInstallsLayout.getFolderElementsMap());
        }

        public FolderParser(ArrayMap<String, TagParser> arrayMap) {
            this.mFolderElements = arrayMap;
        }

        public int parseAndAdd(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            String str;
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(xmlPullParser, "title", 0);
            if (attributeResourceValue != 0) {
                str = AutoInstallsLayout.this.mSourceRes.getString(attributeResourceValue);
            } else {
                str = AutoInstallsLayout.getAttributeValue(xmlPullParser, AutoInstallsLayout.ATTR_TITLE_TEXT);
                if (TextUtils.isEmpty(str)) {
                    str = "";
                }
            }
            AutoInstallsLayout.this.mValues.put("title", str);
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.ITEM_TYPE, 2);
            AutoInstallsLayout.this.mValues.put("spanX", 1);
            AutoInstallsLayout.this.mValues.put("spanY", 1);
            AutoInstallsLayout.this.mValues.put("_id", Integer.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            int insertAndCheck = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (insertAndCheck < 0) {
                return -1;
            }
            ContentValues contentValues = new ContentValues(AutoInstallsLayout.this.mValues);
            IntArray intArray = new IntArray();
            int depth = xmlPullParser.getDepth();
            int i = 0;
            while (true) {
                int next = xmlPullParser.next();
                if (next != 3 || xmlPullParser.getDepth() > depth) {
                    if (next == 2) {
                        AutoInstallsLayout.this.mValues.clear();
                        AutoInstallsLayout.this.mValues.put("container", Integer.valueOf(insertAndCheck));
                        AutoInstallsLayout.this.mValues.put("rank", Integer.valueOf(i));
                        TagParser tagParser = this.mFolderElements.get(xmlPullParser.getName());
                        if (tagParser != null) {
                            int parseAndAdd = tagParser.parseAndAdd(xmlPullParser);
                            if (parseAndAdd >= 0) {
                                intArray.add(parseAndAdd);
                                i++;
                            }
                        } else {
                            throw new RuntimeException("Invalid folder item " + xmlPullParser.getName());
                        }
                    }
                } else if (intArray.size() >= 2) {
                    return insertAndCheck;
                } else {
                    LauncherProvider.SqlArguments sqlArguments = new LauncherProvider.SqlArguments(LauncherSettings.Favorites.getContentUri(insertAndCheck), (String) null, (String[]) null);
                    AutoInstallsLayout.this.mDb.delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
                    if (intArray.size() != 1) {
                        return -1;
                    }
                    ContentValues contentValues2 = new ContentValues();
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, "container");
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, "screen");
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, LauncherSettings.Favorites.CELLX);
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, LauncherSettings.Favorites.CELLY);
                    int i2 = intArray.get(0);
                    AutoInstallsLayout.this.mDb.update(LauncherSettings.Favorites.TABLE_NAME, contentValues2, "_id=" + i2, (String[]) null);
                    return i2;
                }
            }
        }
    }

    /*  JADX ERROR: StackOverflow in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    public static void beginDocument(org.xmlpull.v1.XmlPullParser r3, java.lang.String r4) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        L_0x0000:
            int r0 = r3.next()
            r1 = 2
            if (r0 == r1) goto L_0x000b
            r2 = 1
            if (r0 == r2) goto L_0x000b
            goto L_0x0000
        L_0x000b:
            if (r0 != r1) goto L_0x003f
            java.lang.String r0 = r3.getName()
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x0018
            return
        L_0x0018:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unexpected start tag: found "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r3 = r3.getName()
            java.lang.StringBuilder r3 = r1.append(r3)
            java.lang.String r1 = ", expected "
            java.lang.StringBuilder r3 = r3.append(r1)
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.<init>(r3)
            throw r0
        L_0x003f:
            org.xmlpull.v1.XmlPullParserException r3 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r4 = "No start tag found"
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AutoInstallsLayout.beginDocument(org.xmlpull.v1.XmlPullParser, java.lang.String):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = java.lang.Integer.parseInt(r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String convertToDistanceFromEnd(java.lang.String r1, int r2) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r1)
            if (r0 != 0) goto L_0x0011
            int r0 = java.lang.Integer.parseInt(r1)
            if (r0 >= 0) goto L_0x0011
            int r2 = r2 + r0
            java.lang.String r1 = java.lang.Integer.toString(r2)
        L_0x0011:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AutoInstallsLayout.convertToDistanceFromEnd(java.lang.String, int):java.lang.String");
    }

    protected static String getAttributeValue(XmlPullParser xmlPullParser, String str) {
        String attributeValue = xmlPullParser.getAttributeValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", str);
        return attributeValue == null ? xmlPullParser.getAttributeValue((String) null, str) : attributeValue;
    }

    protected static int getAttributeResourceValue(XmlPullParser xmlPullParser, String str, int i) {
        AttributeSet asAttributeSet = Xml.asAttributeSet(xmlPullParser);
        int attributeResourceValue = asAttributeSet.getAttributeResourceValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", str, i);
        return attributeResourceValue == i ? asAttributeSet.getAttributeResourceValue((String) null, str, i) : attributeResourceValue;
    }

    static void copyInteger(ContentValues contentValues, ContentValues contentValues2, String str) {
        contentValues2.put(str, contentValues.getAsInteger(str));
    }
}
