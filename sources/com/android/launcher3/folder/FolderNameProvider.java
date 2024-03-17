package com.android.launcher3.folder;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.AllAppsList;
import com.android.launcher3.model.BaseModelUpdateTask;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.StringCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.IntSparseArrayMap;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.ResourceBasedOverride;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FolderNameProvider implements ResourceBasedOverride {
    private static final boolean DEBUG = false;
    public static final int SUGGEST_MAX = 4;
    private static final String TAG = "FolderNameProvider";
    protected List<AppInfo> mAppInfos;
    protected IntSparseArrayMap<FolderInfo> mFolderInfos;

    public static FolderNameProvider newInstance(Context context) {
        FolderNameProvider folderNameProvider = (FolderNameProvider) ResourceBasedOverride.Overrides.getObject(FolderNameProvider.class, context.getApplicationContext(), R.string.folder_name_provider_class);
        Preconditions.assertWorkerThread();
        folderNameProvider.load(context);
        return folderNameProvider;
    }

    public static FolderNameProvider newInstance(Context context, List<AppInfo> list, IntSparseArrayMap<FolderInfo> intSparseArrayMap) {
        Preconditions.assertWorkerThread();
        FolderNameProvider folderNameProvider = (FolderNameProvider) ResourceBasedOverride.Overrides.getObject(FolderNameProvider.class, context.getApplicationContext(), R.string.folder_name_provider_class);
        folderNameProvider.load(list, intSparseArrayMap);
        return folderNameProvider;
    }

    private void load(Context context) {
        LauncherAppState.getInstance(context).getModel().enqueueModelUpdateTask(new FolderNameWorker());
    }

    private void load(List<AppInfo> list, IntSparseArrayMap<FolderInfo> intSparseArrayMap) {
        this.mAppInfos = list;
        this.mFolderInfos = intSparseArrayMap;
    }

    public void getSuggestedFolderName(Context context, ArrayList<WorkspaceItemInfo> arrayList, FolderNameInfos folderNameInfos) {
        Preconditions.assertWorkerThread();
        Set set = (Set) arrayList.stream().map($$Lambda$FolderNameProvider$N_zc3TXlYHSSUXg98gTwRjVR1gc.INSTANCE).collect(Collectors.toSet());
        if (set.size() == 1 && !set.contains(Process.myUserHandle())) {
            setAsLastSuggestion(folderNameInfos, getWorkFolderName(context));
        }
        Set set2 = (Set) arrayList.stream().map($$Lambda$deOpjqzkbX1EEVTuYb4mQx6m2yo.INSTANCE).filter($$Lambda$FolderNameProvider$QykguMtrdAvt37qk5jJ34Yd4.INSTANCE).map($$Lambda$FolderNameProvider$HIlrlc_k0A5lsH1JHDjcYuK2vqA.INSTANCE).collect(Collectors.toSet());
        if (set2.size() == 1) {
            getAppInfoByPackageName((String) set2.iterator().next()).ifPresent(new Consumer(folderNameInfos) {
                public final /* synthetic */ FolderNameInfos f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    FolderNameProvider.this.lambda$getSuggestedFolderName$1$FolderNameProvider(this.f$1, (AppInfo) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$getSuggestedFolderName$1$FolderNameProvider(FolderNameInfos folderNameInfos, AppInfo appInfo) {
        setAsFirstSuggestion(folderNameInfos, appInfo.title == null ? "" : appInfo.title.toString());
    }

    private String getWorkFolderName(Context context) {
        if (!Utilities.ATLEAST_T) {
            return context.getString(R.string.work_folder_name);
        }
        return ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getResources().getString(StringCache.WORK_FOLDER_NAME, new Supplier(context) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final Object get() {
                return this.f$0.getString(R.string.work_folder_name);
            }
        });
    }

    private Optional<AppInfo> getAppInfoByPackageName(String str) {
        List<AppInfo> list = this.mAppInfos;
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return this.mAppInfos.stream().filter($$Lambda$FolderNameProvider$VRzWQSYxn6lrkBzBsQZgEe7b7dk.INSTANCE).filter(new Predicate(str) {
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((AppInfo) obj).componentName.getPackageName().equals(this.f$0);
            }
        }).findAny();
    }

    static /* synthetic */ boolean lambda$getAppInfoByPackageName$3(AppInfo appInfo) {
        return appInfo.componentName != null;
    }

    private void setAsFirstSuggestion(FolderNameInfos folderNameInfos, CharSequence charSequence) {
        if (folderNameInfos != null && !folderNameInfos.contains(charSequence)) {
            folderNameInfos.setStatus(2);
            folderNameInfos.setStatus(4);
            CharSequence[] labels = folderNameInfos.getLabels();
            Float[] scores = folderNameInfos.getScores();
            for (int length = labels.length - 1; length > 0; length--) {
                int i = length - 1;
                if (labels[i] != null && !TextUtils.isEmpty(labels[i])) {
                    folderNameInfos.setLabel(length, labels[i], scores[i]);
                }
            }
            folderNameInfos.setLabel(0, charSequence, Float.valueOf(1.0f));
        }
    }

    private void setAsLastSuggestion(FolderNameInfos folderNameInfos, CharSequence charSequence) {
        if (folderNameInfos != null && !folderNameInfos.contains(charSequence)) {
            folderNameInfos.setStatus(2);
            folderNameInfos.setStatus(4);
            CharSequence[] labels = folderNameInfos.getLabels();
            for (int i = 0; i < labels.length; i++) {
                if (labels[i] == null || TextUtils.isEmpty(labels[i])) {
                    folderNameInfos.setLabel(i, charSequence, Float.valueOf(1.0f));
                    return;
                }
            }
            folderNameInfos.setLabel(labels.length - 1, charSequence, Float.valueOf(1.0f));
        }
    }

    private class FolderNameWorker extends BaseModelUpdateTask {
        private FolderNameWorker() {
        }

        public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
            FolderNameProvider.this.mFolderInfos = bgDataModel.folders.clone();
            FolderNameProvider.this.mAppInfos = Arrays.asList(allAppsList.copyData());
        }
    }
}
