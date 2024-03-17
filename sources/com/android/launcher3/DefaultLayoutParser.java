package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.shortcuts.ShortcutKey;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DefaultLayoutParser extends AutoInstallsLayout {
    private static final String ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE = "com.android.launcher.action.APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE";
    private static final String ATTR_CONTAINER = "container";
    private static final String ATTR_FOLDER_ITEMS = "folderItems";
    private static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_SCREEN = "screen";
    private static final String ATTR_SHORTCUT_ID = "shortcutId";
    protected static final String ATTR_URI = "uri";
    private static final String TAG = "DefaultLayoutParser";
    private static final String TAG_APPWIDGET = "appwidget";
    protected static final String TAG_FAVORITE = "favorite";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_FOLDER = "folder";
    private static final String TAG_PARTNER_FOLDER = "partner-folder";
    protected static final String TAG_RESOLVE = "resolve";
    protected static final String TAG_SHORTCUT = "shortcut";

    public DefaultLayoutParser(Context context, AppWidgetHost appWidgetHost, AutoInstallsLayout.LayoutParserCallback layoutParserCallback, Resources resources, int i) {
        super(context, appWidgetHost, layoutParserCallback, resources, i, "favorites");
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, AutoInstallsLayout.TagParser> getFolderElementsMap() {
        return getFolderElementsMap(this.mSourceRes);
    }

    /* access modifiers changed from: package-private */
    public ArrayMap<String, AutoInstallsLayout.TagParser> getFolderElementsMap(Resources resources) {
        ArrayMap<String, AutoInstallsLayout.TagParser> arrayMap = new ArrayMap<>();
        arrayMap.put(TAG_FAVORITE, new AppShortcutWithUriParser());
        arrayMap.put(TAG_SHORTCUT, new UriShortcutParser(resources));
        return arrayMap;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, AutoInstallsLayout.TagParser> getLayoutElementsMap() {
        ArrayMap<String, AutoInstallsLayout.TagParser> arrayMap = new ArrayMap<>();
        arrayMap.put(TAG_FAVORITE, new AppShortcutWithUriParser());
        arrayMap.put(TAG_APPWIDGET, new AppWidgetParser());
        arrayMap.put("searchwidget", new AutoInstallsLayout.SearchWidgetParser());
        arrayMap.put(TAG_SHORTCUT, new UriShortcutParser(this.mSourceRes));
        arrayMap.put(TAG_RESOLVE, new ResolveParser());
        arrayMap.put(TAG_FOLDER, new MyFolderParser());
        arrayMap.put(TAG_PARTNER_FOLDER, new PartnerFolderParser());
        return arrayMap;
    }

    /* access modifiers changed from: protected */
    public void parseContainerAndScreen(XmlPullParser xmlPullParser, int[] iArr) {
        iArr[0] = -100;
        String attributeValue = getAttributeValue(xmlPullParser, "container");
        if (attributeValue != null) {
            iArr[0] = Integer.parseInt(attributeValue);
        }
        iArr[1] = Integer.parseInt(getAttributeValue(xmlPullParser, "screen"));
    }

    public class AppShortcutWithUriParser extends AutoInstallsLayout.AppShortcutParser {
        public AppShortcutWithUriParser() {
            super();
        }

        public /* bridge */ /* synthetic */ int parseAndAdd(XmlPullParser xmlPullParser) {
            return super.parseAndAdd(xmlPullParser);
        }

        /* access modifiers changed from: protected */
        public int invalidPackageOrClass(XmlPullParser xmlPullParser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlPullParser, DefaultLayoutParser.ATTR_URI);
            if (TextUtils.isEmpty(attributeValue)) {
                Log.e(DefaultLayoutParser.TAG, "Skipping invalid <favorite> with no component or uri");
                return -1;
            }
            try {
                Intent parseUri = Intent.parseUri(attributeValue, 0);
                ResolveInfo resolveActivity = DefaultLayoutParser.this.mPackageManager.resolveActivity(parseUri, 65536);
                List<ResolveInfo> queryIntentActivities = DefaultLayoutParser.this.mPackageManager.queryIntentActivities(parseUri, 65536);
                if (!wouldLaunchResolverActivity(resolveActivity, queryIntentActivities) || (resolveActivity = getSingleSystemActivity(queryIntentActivities)) != null) {
                    ActivityInfo activityInfo = resolveActivity.activityInfo;
                    Intent launchIntentForPackage = DefaultLayoutParser.this.mPackageManager.getLaunchIntentForPackage(activityInfo.packageName);
                    if (launchIntentForPackage == null) {
                        return -1;
                    }
                    launchIntentForPackage.setFlags(270532608);
                    DefaultLayoutParser defaultLayoutParser = DefaultLayoutParser.this;
                    return defaultLayoutParser.addShortcut(activityInfo.loadLabel(defaultLayoutParser.mPackageManager).toString(), launchIntentForPackage, 0);
                }
                Log.w(DefaultLayoutParser.TAG, "No preference or single system activity found for " + parseUri.toString());
                return -1;
            } catch (URISyntaxException e) {
                Log.e(DefaultLayoutParser.TAG, "Unable to add meta-favorite: " + attributeValue, e);
                return -1;
            }
        }

        private ResolveInfo getSingleSystemActivity(List<ResolveInfo> list) {
            int size = list.size();
            int i = 0;
            ResolveInfo resolveInfo = null;
            while (i < size) {
                try {
                    if ((DefaultLayoutParser.this.mPackageManager.getApplicationInfo(list.get(i).activityInfo.packageName, 0).flags & 1) != 0) {
                        if (resolveInfo != null) {
                            return null;
                        }
                        resolveInfo = list.get(i);
                    }
                    i++;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(DefaultLayoutParser.TAG, "Unable to get info about resolve results", e);
                    return null;
                }
            }
            return resolveInfo;
        }

        private boolean wouldLaunchResolverActivity(ResolveInfo resolveInfo, List<ResolveInfo> list) {
            for (int i = 0; i < list.size(); i++) {
                ResolveInfo resolveInfo2 = list.get(i);
                if (resolveInfo2.activityInfo.name.equals(resolveInfo.activityInfo.name) && resolveInfo2.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName)) {
                    return false;
                }
            }
            return true;
        }
    }

    public class UriShortcutParser extends AutoInstallsLayout.ShortcutParser {
        public UriShortcutParser(Resources resources) {
            super(resources);
        }

        public int parseAndAdd(XmlPullParser xmlPullParser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlPullParser, DefaultLayoutParser.ATTR_PACKAGE_NAME);
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(xmlPullParser, DefaultLayoutParser.ATTR_SHORTCUT_ID);
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                return super.parseAndAdd(xmlPullParser);
            }
            return parseAndAddDeepShortcut(attributeValue2, attributeValue);
        }

        private int parseAndAddDeepShortcut(String str, String str2) {
            try {
                ((LauncherApps) DefaultLayoutParser.this.mContext.getSystemService(LauncherApps.class)).pinShortcuts(str2, Collections.singletonList(str), Process.myUserHandle());
                Intent makeIntent = ShortcutKey.makeIntent(str, str2);
                DefaultLayoutParser.this.mValues.put(LauncherSettings.Favorites.RESTORED, 1);
                return DefaultLayoutParser.this.addShortcut((String) null, makeIntent, 6);
            } catch (Exception unused) {
                Log.e(DefaultLayoutParser.TAG, "Unable to pin the shortcut for shortcut id = " + str + " and package name = " + str2);
                return -1;
            }
        }

        /* access modifiers changed from: protected */
        public Intent parseIntent(XmlPullParser xmlPullParser) {
            String str;
            try {
                str = AutoInstallsLayout.getAttributeValue(xmlPullParser, DefaultLayoutParser.ATTR_URI);
                try {
                    return Intent.parseUri(str, 0);
                } catch (URISyntaxException unused) {
                    Log.w(DefaultLayoutParser.TAG, "Shortcut has malformed uri: " + str);
                    return null;
                }
            } catch (URISyntaxException unused2) {
                str = null;
                Log.w(DefaultLayoutParser.TAG, "Shortcut has malformed uri: " + str);
                return null;
            }
        }
    }

    public class ResolveParser implements AutoInstallsLayout.TagParser {
        private final AppShortcutWithUriParser mChildParser;

        public ResolveParser() {
            this.mChildParser = new AppShortcutWithUriParser();
        }

        public int parseAndAdd(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            int depth = xmlPullParser.getDepth();
            int i = -1;
            while (true) {
                int next = xmlPullParser.next();
                if (next == 3 && xmlPullParser.getDepth() <= depth) {
                    return i;
                }
                if (next == 2 && i <= -1) {
                    String name = xmlPullParser.getName();
                    if (DefaultLayoutParser.TAG_FAVORITE.equals(name)) {
                        i = this.mChildParser.parseAndAdd(xmlPullParser);
                    } else {
                        Log.e(DefaultLayoutParser.TAG, "Fallback groups can contain only favorites, found " + name);
                    }
                }
            }
        }
    }

    class PartnerFolderParser implements AutoInstallsLayout.TagParser {
        PartnerFolderParser() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
            r0 = r4.getResources();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int parseAndAdd(org.xmlpull.v1.XmlPullParser r4) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
            /*
                r3 = this;
                com.android.launcher3.DefaultLayoutParser r4 = com.android.launcher3.DefaultLayoutParser.this
                android.content.pm.PackageManager r4 = r4.mPackageManager
                com.android.launcher3.Partner r4 = com.android.launcher3.Partner.get(r4)
                if (r4 == 0) goto L_0x0036
                android.content.res.Resources r0 = r4.getResources()
                java.lang.String r4 = r4.getPackageName()
                java.lang.String r1 = "partner_folder"
                java.lang.String r2 = "xml"
                int r4 = r0.getIdentifier(r1, r2, r4)
                if (r4 == 0) goto L_0x0036
                android.content.res.XmlResourceParser r4 = r0.getXml(r4)
                java.lang.String r1 = "folder"
                com.android.launcher3.AutoInstallsLayout.beginDocument(r4, r1)
                com.android.launcher3.AutoInstallsLayout$FolderParser r1 = new com.android.launcher3.AutoInstallsLayout$FolderParser
                com.android.launcher3.DefaultLayoutParser r2 = com.android.launcher3.DefaultLayoutParser.this
                android.util.ArrayMap r0 = r2.getFolderElementsMap(r0)
                r1.<init>(r0)
                int r4 = r1.parseAndAdd(r4)
                return r4
            L_0x0036:
                r4 = -1
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.DefaultLayoutParser.PartnerFolderParser.parseAndAdd(org.xmlpull.v1.XmlPullParser):int");
        }
    }

    class MyFolderParser extends AutoInstallsLayout.FolderParser {
        MyFolderParser() {
            super(DefaultLayoutParser.this);
        }

        public int parseAndAdd(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(xmlPullParser, DefaultLayoutParser.ATTR_FOLDER_ITEMS, 0);
            if (attributeResourceValue != 0) {
                xmlPullParser = DefaultLayoutParser.this.mSourceRes.getXml(attributeResourceValue);
                AutoInstallsLayout.beginDocument(xmlPullParser, DefaultLayoutParser.TAG_FOLDER);
            }
            return super.parseAndAdd(xmlPullParser);
        }
    }

    protected class AppWidgetParser extends AutoInstallsLayout.PendingWidgetParser {
        protected AppWidgetParser() {
            super();
        }

        /* access modifiers changed from: protected */
        public int verifyAndInsert(ComponentName componentName, Bundle bundle) {
            int i = -1;
            try {
                DefaultLayoutParser.this.mPackageManager.getReceiverInfo(componentName, 0);
            } catch (Exception unused) {
                ComponentName componentName2 = new ComponentName(DefaultLayoutParser.this.mPackageManager.currentToCanonicalPackageNames(new String[]{componentName.getPackageName()})[0], componentName.getClassName());
                try {
                    DefaultLayoutParser.this.mPackageManager.getReceiverInfo(componentName2, 0);
                    componentName = componentName2;
                } catch (Exception unused2) {
                    Log.d(DefaultLayoutParser.TAG, "Can't find widget provider: " + componentName2.getClassName());
                    return -1;
                }
            }
            AppWidgetManager instance = AppWidgetManager.getInstance(DefaultLayoutParser.this.mContext);
            try {
                int allocateAppWidgetId = DefaultLayoutParser.this.mAppWidgetHost.allocateAppWidgetId();
                if (!instance.bindAppWidgetIdIfAllowed(allocateAppWidgetId, componentName)) {
                    Log.e(DefaultLayoutParser.TAG, "Unable to bind app widget id " + componentName);
                    DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(allocateAppWidgetId);
                    return -1;
                }
                DefaultLayoutParser.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_ID, Integer.valueOf(allocateAppWidgetId));
                DefaultLayoutParser.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, componentName.flattenToString());
                DefaultLayoutParser.this.mValues.put("_id", Integer.valueOf(DefaultLayoutParser.this.mCallback.generateNewItemId()));
                i = DefaultLayoutParser.this.mCallback.insertAndCheck(DefaultLayoutParser.this.mDb, DefaultLayoutParser.this.mValues);
                if (i < 0) {
                    DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(allocateAppWidgetId);
                    return i;
                }
                if (!bundle.isEmpty()) {
                    Intent intent = new Intent(DefaultLayoutParser.ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE);
                    intent.setComponent(componentName);
                    intent.putExtras(bundle);
                    intent.putExtra(LauncherSettings.Favorites.APPWIDGET_ID, allocateAppWidgetId);
                    DefaultLayoutParser.this.mContext.sendBroadcast(intent);
                }
                return i;
            } catch (RuntimeException e) {
                Log.e(DefaultLayoutParser.TAG, "Problem allocating appWidgetId", e);
            }
        }
    }
}
