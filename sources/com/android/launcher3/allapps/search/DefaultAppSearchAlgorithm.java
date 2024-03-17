package com.android.launcher3.allapps.search;

import android.content.Context;
import android.os.Handler;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.model.AllAppsList;
import com.android.launcher3.model.BaseModelUpdateTask;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.search.SearchAlgorithm;
import com.android.launcher3.search.SearchCallback;
import com.android.launcher3.search.StringMatcherUtility;
import com.android.launcher3.util.Executors;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppSearchAlgorithm implements SearchAlgorithm<BaseAllAppsAdapter.AdapterItem> {
    private static final int MAX_RESULTS_COUNT = 5;
    private final LauncherAppState mAppState;
    /* access modifiers changed from: private */
    public final Handler mResultHandler = new Handler(Executors.MAIN_EXECUTOR.getLooper());

    public DefaultAppSearchAlgorithm(Context context) {
        this.mAppState = LauncherAppState.getInstance(context);
    }

    public void cancel(boolean z) {
        if (z) {
            this.mResultHandler.removeCallbacksAndMessages((Object) null);
        }
    }

    public void doSearch(final String str, final SearchCallback<BaseAllAppsAdapter.AdapterItem> searchCallback) {
        this.mAppState.getModel().enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                DefaultAppSearchAlgorithm.this.mResultHandler.post(new Runnable(str, DefaultAppSearchAlgorithm.getTitleMatchResult(allAppsList.data, str)) {
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
        });
    }

    public static ArrayList<BaseAllAppsAdapter.AdapterItem> getTitleMatchResult(List<AppInfo> list, String str) {
        String lowerCase = str.toLowerCase();
        ArrayList<BaseAllAppsAdapter.AdapterItem> arrayList = new ArrayList<>();
        StringMatcherUtility.StringMatcher instance = StringMatcherUtility.StringMatcher.getInstance();
        int size = list.size();
        int i = 0;
        for (int i2 = 0; i2 < size && i < 5; i2++) {
            AppInfo appInfo = list.get(i2);
            if (StringMatcherUtility.matches(lowerCase, appInfo.title.toString(), instance)) {
                arrayList.add(BaseAllAppsAdapter.AdapterItem.asApp(appInfo));
                i++;
            }
        }
        return arrayList;
    }
}
