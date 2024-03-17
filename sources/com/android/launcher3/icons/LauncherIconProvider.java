package com.android.launcher3.icons;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.launcher3.R;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.IconProvider;
import com.android.launcher3.util.Themes;
import java.util.Collections;
import java.util.Map;

public class LauncherIconProvider extends IconProvider {
    private static final String ATTR_DRAWABLE = "drawable";
    private static final String ATTR_PACKAGE = "package";
    private static final Map<String, IconProvider.ThemeData> DISABLED_MAP = Collections.emptyMap();
    private static final String TAG = "LIconProvider";
    private static final String TAG_ICON = "icon";
    private boolean mSupportsIconTheme;
    private Map<String, IconProvider.ThemeData> mThemedIconMap;

    public LauncherIconProvider(Context context) {
        super(context);
        setIconThemeSupported(Themes.isThemedIconEnabled(context));
    }

    public void setIconThemeSupported(boolean z) {
        Map<String, IconProvider.ThemeData> map;
        this.mSupportsIconTheme = z;
        if (!z || !FeatureFlags.USE_LOCAL_ICON_OVERRIDES.get()) {
            map = DISABLED_MAP;
        } else {
            map = null;
        }
        this.mThemedIconMap = map;
    }

    /* access modifiers changed from: protected */
    public IconProvider.ThemeData getThemeDataForPackage(String str) {
        return getThemedIconMap().get(str);
    }

    public String getSystemIconState() {
        return super.getSystemIconState() + (this.mSupportsIconTheme ? ",with-theme" : ",no-theme");
    }

    private Map<String, IconProvider.ThemeData> getThemedIconMap() {
        XmlResourceParser xml;
        Map<String, IconProvider.ThemeData> map = this.mThemedIconMap;
        if (map != null) {
            return map;
        }
        ArrayMap arrayMap = new ArrayMap();
        Resources resources = this.mContext.getResources();
        try {
            xml = resources.getXml(R.xml.grayscale_icon_map);
            int depth = xml.getDepth();
            while (true) {
                int next = xml.next();
                if (next == 2 || next == 1) {
                }
            }
            while (true) {
                int next2 = xml.next();
                if ((next2 != 3 || xml.getDepth() > depth) && next2 != 1) {
                    if (next2 == 2) {
                        if ("icon".equals(xml.getName())) {
                            String attributeValue = xml.getAttributeValue((String) null, ATTR_PACKAGE);
                            int attributeResourceValue = xml.getAttributeResourceValue((String) null, ATTR_DRAWABLE, 0);
                            if (attributeResourceValue != 0 && !TextUtils.isEmpty(attributeValue)) {
                                arrayMap.put(attributeValue, new IconProvider.ThemeData(resources, attributeResourceValue));
                            }
                        }
                    }
                }
            }
            if (xml != null) {
                xml.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to parse icon map", e);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        this.mThemedIconMap = arrayMap;
        return arrayMap;
        throw th;
    }
}
