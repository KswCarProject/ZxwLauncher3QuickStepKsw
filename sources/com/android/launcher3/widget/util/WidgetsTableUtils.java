package com.android.launcher3.widget.util;

import com.android.launcher3.model.WidgetItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class WidgetsTableUtils {
    private static final Comparator<WidgetItem> WIDGET_SHORTCUT_COMPARATOR = $$Lambda$WidgetsTableUtils$nS21QI6oXLR5L4TABgCFKtFA3RE.INSTANCE;

    static /* synthetic */ int lambda$static$0(WidgetItem widgetItem, WidgetItem widgetItem2) {
        if (widgetItem.widgetInfo != null && widgetItem2.widgetInfo == null) {
            return -1;
        }
        if (widgetItem.widgetInfo == null && widgetItem2.widgetInfo != null) {
            return 1;
        }
        if (widgetItem.spanX == widgetItem2.spanX) {
            if (widgetItem.spanY == widgetItem2.spanY) {
                return 0;
            }
            if (widgetItem.spanY > widgetItem2.spanY) {
                return 1;
            }
            return -1;
        } else if (widgetItem.spanX > widgetItem2.spanX) {
            return 1;
        } else {
            return -1;
        }
    }

    public static List<ArrayList<WidgetItem>> groupWidgetItemsIntoTableWithReordering(List<WidgetItem> list, int i) {
        return groupWidgetItemsIntoTableWithoutReordering((List) list.stream().sorted(WIDGET_SHORTCUT_COMPARATOR).collect(Collectors.toList()), i);
    }

    public static List<ArrayList<WidgetItem>> groupWidgetItemsIntoTableWithoutReordering(List<WidgetItem> list, int i) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = null;
        for (WidgetItem next : list) {
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
                arrayList.add(arrayList2);
            }
            int size = arrayList2.size();
            int intValue = next.spanX + ((Integer) arrayList2.stream().map($$Lambda$WidgetsTableUtils$kPaMY2UVLQORvY9M1SnhM9Ek8Ds.INSTANCE).reduce(0, $$Lambda$WidgetsTableUtils$x5ZpzdiYf1RXAJlr3R79mbPdAY.INSTANCE)).intValue();
            if (size == 0) {
                arrayList2.add(next);
            } else if (intValue > i - 1 || !next.hasSameType((WidgetItem) arrayList2.get(size - 1))) {
                arrayList2 = new ArrayList();
                arrayList.add(arrayList2);
                arrayList2.add(next);
            } else {
                arrayList2.add(next);
            }
        }
        return arrayList;
    }
}
