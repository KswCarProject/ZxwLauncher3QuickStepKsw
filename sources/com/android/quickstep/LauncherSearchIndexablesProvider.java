package com.android.quickstep;

import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexablesContract;
import android.provider.SearchIndexablesProvider;
import android.util.Xml;
import com.android.launcher3.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class LauncherSearchIndexablesProvider extends SearchIndexablesProvider {
    public boolean onCreate() {
        return true;
    }

    public Cursor queryXmlResources(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
        matrixCursor.newRow().add("xmlResId", Integer.valueOf(R.xml.indexable_launcher_prefs)).add("intentAction", "android.intent.action.APPLICATION_PREFERENCES").add("intentTargetPackage", getContext().getPackageName()).add("intentTargetClass", getContext().getPackageManager().resolveActivity(new Intent("android.intent.action.APPLICATION_PREFERENCES").setPackage(getContext().getPackageName()), 0).activityInfo.name);
        return matrixCursor;
    }

    public Cursor queryRawData(String[] strArr) {
        return new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
    }

    public Cursor queryNonIndexableKeys(String[] strArr) {
        XmlResourceParser xml;
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
        if (!((LauncherApps) getContext().getSystemService(LauncherApps.class)).hasShortcutHostPermission()) {
            try {
                xml = getContext().getResources().getXml(R.xml.indexable_launcher_prefs);
                int depth = xml.getDepth();
                int[] iArr = {16843240};
                while (true) {
                    int next = xml.next();
                    if ((next != 3 || xml.getDepth() > depth) && next != 1) {
                        if (next == 2) {
                            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(Xml.asAttributeSet(xml), iArr);
                            matrixCursor.addRow(new String[]{obtainStyledAttributes.getString(0)});
                            obtainStyledAttributes.recycle();
                        }
                    }
                }
                if (xml != null) {
                    xml.close();
                }
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException(e);
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        }
        return matrixCursor;
        throw th;
    }
}
