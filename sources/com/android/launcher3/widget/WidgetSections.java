package com.android.launcher3.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.Xml;
import com.android.launcher3.R;
import com.android.launcher3.util.IntSet;
import java.io.IOException;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

public final class WidgetSections {
    public static final int NO_CATEGORY = -1;
    private static final String TAG_SECTION_NAME = "section";
    private static final String TAG_WIDGET_NAME = "widget";
    private static SparseArray<WidgetSection> sWidgetSections;
    private static Map<ComponentName, IntSet> sWidgetsToCategories;

    public static synchronized SparseArray<WidgetSection> getWidgetSections(Context context) {
        synchronized (WidgetSections.class) {
            SparseArray<WidgetSection> sparseArray = sWidgetSections;
            if (sparseArray != null) {
                return sparseArray;
            }
            parseWidgetSectionsXml(context);
            SparseArray<WidgetSection> sparseArray2 = sWidgetSections;
            return sparseArray2;
        }
    }

    public static synchronized Map<ComponentName, IntSet> getWidgetsToCategory(Context context) {
        synchronized (WidgetSections.class) {
            Map<ComponentName, IntSet> map = sWidgetsToCategories;
            if (map != null) {
                return map;
            }
            parseWidgetSectionsXml(context);
            Map<ComponentName, IntSet> map2 = sWidgetsToCategories;
            return map2;
        }
    }

    private static synchronized void parseWidgetSectionsXml(Context context) {
        XmlResourceParser xml;
        IntSet intSet;
        synchronized (WidgetSections.class) {
            SparseArray<WidgetSection> sparseArray = new SparseArray<>();
            ArrayMap arrayMap = new ArrayMap();
            try {
                xml = context.getResources().getXml(R.xml.widget_sections);
                int depth = xml.getDepth();
                while (true) {
                    int next = xml.next();
                    if ((next != 3 || xml.getDepth() > depth) && next != 1) {
                        if (next == 2 && TAG_SECTION_NAME.equals(xml.getName())) {
                            WidgetSection widgetSection = new WidgetSection(context, Xml.asAttributeSet(xml));
                            int depth2 = xml.getDepth();
                            while (true) {
                                int next2 = xml.next();
                                if ((next2 != 3 || xml.getDepth() > depth2) && next2 != 1) {
                                    if (next2 == 2 && TAG_WIDGET_NAME.equals(xml.getName())) {
                                        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xml), R.styleable.WidgetSections);
                                        ComponentName unflattenFromString = ComponentName.unflattenFromString(obtainStyledAttributes.getString(2));
                                        boolean z = obtainStyledAttributes.getBoolean(0, false);
                                        if (arrayMap.containsKey(unflattenFromString)) {
                                            intSet = (IntSet) arrayMap.get(unflattenFromString);
                                        } else {
                                            IntSet intSet2 = new IntSet();
                                            arrayMap.put(unflattenFromString, intSet2);
                                            intSet = intSet2;
                                        }
                                        if (z) {
                                            intSet.add(-1);
                                        }
                                        intSet.add(widgetSection.mCategory);
                                    }
                                }
                            }
                            sparseArray.put(widgetSection.mCategory, widgetSection);
                        }
                    }
                }
                sWidgetSections = sparseArray;
                sWidgetsToCategories = arrayMap;
                if (xml != null) {
                    xml.close();
                }
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException(e);
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        }
        return;
        throw th;
    }

    public static final class WidgetSection {
        public final int mCategory;
        public final int mSectionDrawable;
        public final int mSectionTitle;

        public WidgetSection(Context context, AttributeSet attributeSet) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.WidgetSections);
            this.mCategory = obtainStyledAttributes.getInt(1, -1);
            this.mSectionTitle = obtainStyledAttributes.getResourceId(4, 0);
            this.mSectionDrawable = obtainStyledAttributes.getResourceId(3, 0);
        }
    }
}
