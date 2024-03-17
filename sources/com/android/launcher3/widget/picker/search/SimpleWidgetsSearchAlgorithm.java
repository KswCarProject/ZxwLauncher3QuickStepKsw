package com.android.launcher3.widget.picker.search;

import android.os.Handler;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.search.SearchAlgorithm;
import com.android.launcher3.search.SearchCallback;
import com.android.launcher3.search.StringMatcherUtility;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.model.WidgetsListContentEntry;
import com.android.launcher3.widget.model.WidgetsListHeaderEntry;
import com.android.launcher3.widget.model.WidgetsListSearchHeaderEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SimpleWidgetsSearchAlgorithm implements SearchAlgorithm<WidgetsListBaseEntry> {
    private final PopupDataProvider mDataProvider;
    private final Handler mResultHandler = new Handler();

    public SimpleWidgetsSearchAlgorithm(PopupDataProvider popupDataProvider) {
        this.mDataProvider = popupDataProvider;
    }

    public void doSearch(String str, SearchCallback<WidgetsListBaseEntry> searchCallback) {
        this.mResultHandler.post(new Runnable(str, getFilteredWidgets(this.mDataProvider, str)) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SearchCallback.this.onSearchResult(this.f$1, this.f$2);
            }
        });
    }

    public void cancel(boolean z) {
        if (z) {
            this.mResultHandler.removeCallbacksAndMessages((Object) null);
        }
    }

    public static ArrayList<WidgetsListBaseEntry> getFilteredWidgets(PopupDataProvider popupDataProvider, String str) {
        ArrayList<WidgetsListBaseEntry> arrayList = new ArrayList<>();
        popupDataProvider.getAllWidgets().stream().filter($$Lambda$SimpleWidgetsSearchAlgorithm$SMiUBmwD42OZpgJpj4fvNarNxM.INSTANCE).forEach(new Consumer(str, arrayList) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ ArrayList f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                SimpleWidgetsSearchAlgorithm.lambda$getFilteredWidgets$2(this.f$0, this.f$1, (WidgetsListBaseEntry) obj);
            }
        });
        return arrayList;
    }

    static /* synthetic */ boolean lambda$getFilteredWidgets$1(WidgetsListBaseEntry widgetsListBaseEntry) {
        return widgetsListBaseEntry instanceof WidgetsListHeaderEntry;
    }

    static /* synthetic */ void lambda$getFilteredWidgets$2(String str, ArrayList arrayList, WidgetsListBaseEntry widgetsListBaseEntry) {
        List<WidgetItem> filterWidgetItems = filterWidgetItems(str, widgetsListBaseEntry.mPkgItem.title.toString(), widgetsListBaseEntry.mWidgets);
        if (filterWidgetItems.size() > 0) {
            arrayList.add(new WidgetsListSearchHeaderEntry(widgetsListBaseEntry.mPkgItem, widgetsListBaseEntry.mTitleSectionName, filterWidgetItems));
            arrayList.add(new WidgetsListContentEntry(widgetsListBaseEntry.mPkgItem, widgetsListBaseEntry.mTitleSectionName, filterWidgetItems));
        }
    }

    private static List<WidgetItem> filterWidgetItems(String str, String str2, List<WidgetItem> list) {
        StringMatcherUtility.StringMatcher instance = StringMatcherUtility.StringMatcher.getInstance();
        if (StringMatcherUtility.matches(str, str2, instance)) {
            return list;
        }
        return (List) list.stream().filter(new Predicate(str, instance) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ StringMatcherUtility.StringMatcher f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return StringMatcherUtility.matches(this.f$0, ((WidgetItem) obj).label, this.f$1);
            }
        }).collect(Collectors.toList());
    }
}
