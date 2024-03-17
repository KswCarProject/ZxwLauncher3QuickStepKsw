package com.android.launcher3.taskbar;

import android.util.SparseArray;
import android.view.View;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LauncherBindableItemsContainer;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class TaskbarModelCallbacks implements BgDataModel.Callbacks, LauncherBindableItemsContainer {
    private final TaskbarView mContainer;
    private final TaskbarActivityContext mContext;
    private TaskbarControllers mControllers;
    private final SparseArray<ItemInfo> mHotseatItems = new SparseArray<>();
    private List<ItemInfo> mPredictedItems = Collections.emptyList();

    public TaskbarModelCallbacks(TaskbarActivityContext taskbarActivityContext, TaskbarView taskbarView) {
        this.mContext = taskbarActivityContext;
        this.mContainer = taskbarView;
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
    }

    public void startBinding() {
        this.mContext.setBindingItems(true);
        this.mHotseatItems.clear();
        this.mPredictedItems = Collections.emptyList();
    }

    public void finishBindingItems(IntSet intSet) {
        this.mContext.setBindingItems(false);
        commitItemsToUI();
    }

    public void bindAppsAdded(IntArray intArray, ArrayList<ItemInfo> arrayList, ArrayList<ItemInfo> arrayList2) {
        boolean handleItemsAdded = handleItemsAdded(arrayList);
        boolean handleItemsAdded2 = handleItemsAdded(arrayList2);
        if (handleItemsAdded || handleItemsAdded2) {
            commitItemsToUI();
        }
    }

    public void bindItems(List<ItemInfo> list, boolean z) {
        if (handleItemsAdded(list)) {
            commitItemsToUI();
        }
    }

    private boolean handleItemsAdded(List<ItemInfo> list) {
        boolean z = false;
        for (ItemInfo next : list) {
            if (next.container == -101) {
                this.mHotseatItems.put(next.screenId, next);
                z = true;
            }
        }
        return z;
    }

    public void bindWorkspaceItemsChanged(List<WorkspaceItemInfo> list) {
        updateWorkspaceItems(list, this.mContext);
    }

    public void bindRestoreItemsChange(HashSet<ItemInfo> hashSet) {
        updateRestoreItems(hashSet, this.mContext);
    }

    public void mapOverItems(LauncherBindableItemsContainer.ItemOperator itemOperator) {
        int childCount = this.mContainer.getChildCount();
        int i = 0;
        while (i < childCount) {
            View childAt = this.mContainer.getChildAt(i);
            if (!itemOperator.evaluate((ItemInfo) childAt.getTag(), childAt)) {
                i++;
            } else {
                return;
            }
        }
    }

    public void bindWorkspaceComponentsRemoved(Predicate<ItemInfo> predicate) {
        if (handleItemsRemoved(predicate)) {
            commitItemsToUI();
        }
    }

    private boolean handleItemsRemoved(Predicate<ItemInfo> predicate) {
        boolean z = false;
        for (int size = this.mHotseatItems.size() - 1; size >= 0; size--) {
            if (predicate.test(this.mHotseatItems.valueAt(size))) {
                this.mHotseatItems.removeAt(size);
                z = true;
            }
        }
        return z;
    }

    public void bindItemsModified(List<ItemInfo> list) {
        boolean handleItemsRemoved = handleItemsRemoved(ItemInfoMatcher.ofItems(list));
        boolean handleItemsAdded = handleItemsAdded(list);
        if (handleItemsRemoved || handleItemsAdded) {
            commitItemsToUI();
        }
    }

    public void bindExtraContainerItems(BgDataModel.FixedContainerItems fixedContainerItems) {
        if (fixedContainerItems.containerId == -103) {
            this.mPredictedItems = fixedContainerItems.items;
            commitItemsToUI();
        } else if (fixedContainerItems.containerId == -102) {
            this.mControllers.taskbarAllAppsController.setPredictedApps(fixedContainerItems.items);
        }
    }

    private void commitItemsToUI() {
        if (!this.mContext.isBindingItems()) {
            int i = this.mContext.getDeviceProfile().numShownHotseatIcons;
            ItemInfo[] itemInfoArr = new ItemInfo[i];
            int size = this.mPredictedItems.size();
            boolean z = true;
            int i2 = 0;
            for (int i3 = 0; i3 < i; i3++) {
                itemInfoArr[i3] = this.mHotseatItems.get(i3);
                if (itemInfoArr[i3] == null && i2 < size) {
                    itemInfoArr[i3] = this.mPredictedItems.get(i2);
                    itemInfoArr[i3].screenId = i3;
                    i2++;
                }
                if (itemInfoArr[i3] != null) {
                    z = false;
                }
            }
            this.mContainer.updateHotseatItems(itemInfoArr);
            this.mControllers.runAfterInit(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TaskbarModelCallbacks.this.lambda$commitItemsToUI$0$TaskbarModelCallbacks(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$commitItemsToUI$0$TaskbarModelCallbacks(boolean z) {
        this.mControllers.taskbarStashController.updateStateForFlag(8, z);
        this.mControllers.taskbarStashController.applyState();
    }

    public void bindDeepShortcutMap(HashMap<ComponentKey, Integer> hashMap) {
        this.mControllers.taskbarPopupController.setDeepShortcutMap(hashMap);
    }

    public void bindAllApplications(AppInfo[] appInfoArr, int i) {
        this.mControllers.taskbarAllAppsController.setApps(appInfoArr, i);
    }

    /* access modifiers changed from: protected */
    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarModelCallbacks:");
        printWriter.println(String.format("%s\thotseat items count=%s", new Object[]{str, Integer.valueOf(this.mHotseatItems.size())}));
        List<ItemInfo> list = this.mPredictedItems;
        if (list != null) {
            printWriter.println(String.format("%s\tpredicted items count=%s", new Object[]{str, Integer.valueOf(list.size())}));
        }
    }
}
