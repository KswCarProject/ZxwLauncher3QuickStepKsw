package com.android.launcher3.widget.model;

import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.widget.WidgetItemComparator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.stream.Collectors;

public abstract class WidgetsListBaseEntry {
    public static final int RANK_WIDGETS_LIST_CONTENT = 4;
    public static final int RANK_WIDGETS_LIST_HEADER = 2;
    public static final int RANK_WIDGETS_LIST_SEARCH_HEADER = 3;
    public static final int RANK_WIDGETS_TOP_SPACE = 1;
    public final PackageItemInfo mPkgItem;
    public final String mTitleSectionName;
    public final List<WidgetItem> mWidgets;

    public interface Header<T extends WidgetsListBaseEntry & Header<T>> {
        boolean isWidgetListShown();

        T withWidgetListShown();
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Rank {
    }

    public abstract int getRank();

    public WidgetsListBaseEntry(PackageItemInfo packageItemInfo, String str, List<WidgetItem> list) {
        this.mPkgItem = packageItemInfo;
        this.mTitleSectionName = str;
        this.mWidgets = (List) list.stream().sorted(new WidgetItemComparator()).collect(Collectors.toList());
    }
}
