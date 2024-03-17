package com.android.launcher3.model;

import android.util.Log;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.model.BaseLoaderResults;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.IntSparseArrayMap;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.LooperIdleLock;
import com.android.launcher3.util.RunnableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public abstract class BaseLoaderResults {
    protected static final int INVALID_SCREEN_ID = -1;
    private static final int ITEMS_CHUNK = 6;
    protected static final String TAG = "LoaderResults";
    protected final LauncherAppState mApp;
    private final AllAppsList mBgAllAppsList;
    protected final BgDataModel mBgDataModel;
    private final BgDataModel.Callbacks[] mCallbacksList;
    private int mMyBindingId;
    protected final LooperExecutor mUiExecutor;

    public abstract void bindDeepShortcuts();

    public abstract void bindWidgets();

    public BaseLoaderResults(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList, BgDataModel.Callbacks[] callbacksArr, LooperExecutor looperExecutor) {
        this.mUiExecutor = looperExecutor;
        this.mApp = launcherAppState;
        this.mBgDataModel = bgDataModel;
        this.mBgAllAppsList = allAppsList;
        this.mCallbacksList = callbacksArr;
    }

    public void bindWorkspace(boolean z) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        IntArray intArray = new IntArray();
        ArrayList arrayList3 = new ArrayList();
        synchronized (this.mBgDataModel) {
            arrayList.addAll(this.mBgDataModel.workspaceItems);
            arrayList2.addAll(this.mBgDataModel.appWidgets);
            intArray.addAll(this.mBgDataModel.collectWorkspaceScreens());
            IntSparseArrayMap<BgDataModel.FixedContainerItems> intSparseArrayMap = this.mBgDataModel.extraItems;
            Objects.requireNonNull(arrayList3);
            intSparseArrayMap.forEach(new Consumer(arrayList3) {
                public final /* synthetic */ ArrayList f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    boolean unused = this.f$0.add((BgDataModel.FixedContainerItems) obj);
                }
            });
            if (z) {
                this.mBgDataModel.lastBindId++;
            }
            this.mMyBindingId = this.mBgDataModel.lastBindId;
        }
        BgDataModel.Callbacks[] callbacksArr = this.mCallbacksList;
        int length = callbacksArr.length;
        int i = 0;
        while (i < length) {
            new WorkspaceBinder(callbacksArr[i], this.mUiExecutor, this.mApp, this.mBgDataModel, this.mMyBindingId, arrayList, arrayList2, arrayList3, intArray).bind();
            i++;
            length = length;
            callbacksArr = callbacksArr;
        }
    }

    public void bindAllApps() {
        executeCallbacksTask(new LauncherModel.CallbackTask(this.mBgAllAppsList.copyData(), this.mBgAllAppsList.getFlags()) {
            public final /* synthetic */ AppInfo[] f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void execute(BgDataModel.Callbacks callbacks) {
                callbacks.bindAllApplications(this.f$0, this.f$1);
            }
        }, this.mUiExecutor);
    }

    /* access modifiers changed from: protected */
    public void sortWorkspaceItemsSpatially(InvariantDeviceProfile invariantDeviceProfile, ArrayList<ItemInfo> arrayList) {
        Collections.sort(arrayList, new Comparator(invariantDeviceProfile.numColumns * invariantDeviceProfile.numRows, invariantDeviceProfile.numColumns) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final int compare(Object obj, Object obj2) {
                return BaseLoaderResults.lambda$sortWorkspaceItemsSpatially$1(this.f$0, this.f$1, (ItemInfo) obj, (ItemInfo) obj2);
            }
        });
    }

    static /* synthetic */ int lambda$sortWorkspaceItemsSpatially$1(int i, int i2, ItemInfo itemInfo, ItemInfo itemInfo2) {
        if (itemInfo.container != itemInfo2.container) {
            return Integer.compare(itemInfo.container, itemInfo2.container);
        }
        int i3 = itemInfo.container;
        if (i3 == -101) {
            return Integer.compare(itemInfo.screenId, itemInfo2.screenId);
        }
        if (i3 != -100) {
            return 0;
        }
        return Integer.compare((itemInfo.screenId * i) + (itemInfo.cellY * i2) + itemInfo.cellX, (itemInfo2.screenId * i) + (itemInfo2.cellY * i2) + itemInfo2.cellX);
    }

    /* access modifiers changed from: protected */
    public void executeCallbacksTask(LauncherModel.CallbackTask callbackTask, Executor executor) {
        executor.execute(new Runnable(callbackTask) {
            public final /* synthetic */ LauncherModel.CallbackTask f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BaseLoaderResults.this.lambda$executeCallbacksTask$2$BaseLoaderResults(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$executeCallbacksTask$2$BaseLoaderResults(LauncherModel.CallbackTask callbackTask) {
        if (this.mMyBindingId != this.mBgDataModel.lastBindId) {
            Log.d(TAG, "Too many consecutive reloads, skipping obsolete data-bind");
            return;
        }
        for (BgDataModel.Callbacks execute : this.mCallbacksList) {
            callbackTask.execute(execute);
        }
    }

    public LooperIdleLock newIdleLock(Object obj) {
        LooperIdleLock looperIdleLock = new LooperIdleLock(obj, this.mUiExecutor.getLooper());
        if (this.mUiExecutor.getLooper().getQueue().isIdle()) {
            looperIdleLock.queueIdle();
        }
        return looperIdleLock;
    }

    private class WorkspaceBinder {
        private final LauncherAppState mApp;
        private final ArrayList<LauncherAppWidgetInfo> mAppWidgets;
        private final BgDataModel mBgDataModel;
        private final BgDataModel.Callbacks mCallbacks;
        private final ArrayList<BgDataModel.FixedContainerItems> mExtraItems;
        private final int mMyBindingId;
        private final IntArray mOrderedScreenIds;
        private final Executor mUiExecutor;
        private final ArrayList<ItemInfo> mWorkspaceItems;

        WorkspaceBinder(BgDataModel.Callbacks callbacks, Executor executor, LauncherAppState launcherAppState, BgDataModel bgDataModel, int i, ArrayList<ItemInfo> arrayList, ArrayList<LauncherAppWidgetInfo> arrayList2, ArrayList<BgDataModel.FixedContainerItems> arrayList3, IntArray intArray) {
            this.mCallbacks = callbacks;
            this.mUiExecutor = executor;
            this.mApp = launcherAppState;
            this.mBgDataModel = bgDataModel;
            this.mMyBindingId = i;
            this.mWorkspaceItems = arrayList;
            this.mAppWidgets = arrayList2;
            this.mExtraItems = arrayList3;
            this.mOrderedScreenIds = intArray;
        }

        /* access modifiers changed from: private */
        public void bind() {
            IntSet pagesToBindSynchronously = this.mCallbacks.getPagesToBindSynchronously(this.mOrderedScreenIds);
            Objects.requireNonNull(pagesToBindSynchronously, "Null screen ids provided by " + this.mCallbacks);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.NULL_INT_SET, "bind (1) currentScreenIds: " + pagesToBindSynchronously + ", pointer: " + this.mCallbacks + ", name: " + this.mCallbacks.getClass().getName());
            }
            ModelUtils.filterCurrentWorkspaceItems(pagesToBindSynchronously, this.mWorkspaceItems, arrayList, arrayList2);
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.NULL_INT_SET, "bind (2) currentScreenIds: " + pagesToBindSynchronously);
            }
            ModelUtils.filterCurrentWorkspaceItems(pagesToBindSynchronously, this.mAppWidgets, arrayList3, arrayList4);
            InvariantDeviceProfile invariantDeviceProfile = this.mApp.getInvariantDeviceProfile();
            BaseLoaderResults.this.sortWorkspaceItemsSpatially(invariantDeviceProfile, arrayList);
            BaseLoaderResults.this.sortWorkspaceItemsSpatially(invariantDeviceProfile, arrayList2);
            executeCallbacksTask($$Lambda$BaseLoaderResults$WorkspaceBinder$cblznQmnnUA9QO03bWt0keFmw_c.INSTANCE, this.mUiExecutor);
            executeCallbacksTask(new LauncherModel.CallbackTask() {
                public final void execute(BgDataModel.Callbacks callbacks) {
                    BaseLoaderResults.WorkspaceBinder.this.lambda$bind$1$BaseLoaderResults$WorkspaceBinder(callbacks);
                }
            }, this.mUiExecutor);
            bindWorkspaceItems(arrayList, this.mUiExecutor);
            bindAppWidgets(arrayList3, this.mUiExecutor);
            this.mExtraItems.forEach(new Consumer() {
                public final void accept(Object obj) {
                    BaseLoaderResults.WorkspaceBinder.this.lambda$bind$3$BaseLoaderResults$WorkspaceBinder((BgDataModel.FixedContainerItems) obj);
                }
            });
            RunnableList runnableList = new RunnableList();
            Objects.requireNonNull(runnableList);
            $$Lambda$FMERNP2dSxJGuPpZQyt5SFs3Cqw r3 = new Executor() {
                public final void execute(Runnable runnable) {
                    RunnableList.this.add(runnable);
                }
            };
            bindWorkspaceItems(arrayList2, r3);
            bindAppWidgets(arrayList4, r3);
            executeCallbacksTask(new LauncherModel.CallbackTask() {
                public final void execute(BgDataModel.Callbacks callbacks) {
                    callbacks.finishBindingItems(IntSet.this);
                }
            }, r3);
            r3.execute(new Runnable() {
                public final void run() {
                    BaseLoaderResults.WorkspaceBinder.this.lambda$bind$5$BaseLoaderResults$WorkspaceBinder();
                }
            });
            executeCallbacksTask(new LauncherModel.CallbackTask(runnableList) {
                public final /* synthetic */ RunnableList f$1;

                {
                    this.f$1 = r2;
                }

                public final void execute(BgDataModel.Callbacks callbacks) {
                    BaseLoaderResults.WorkspaceBinder.lambda$bind$6(IntSet.this, this.f$1, callbacks);
                }
            }, this.mUiExecutor);
            this.mCallbacks.bindStringCache(this.mBgDataModel.stringCache.clone());
        }

        static /* synthetic */ void lambda$bind$0(BgDataModel.Callbacks callbacks) {
            callbacks.clearPendingBinds();
            callbacks.startBinding();
        }

        public /* synthetic */ void lambda$bind$1$BaseLoaderResults$WorkspaceBinder(BgDataModel.Callbacks callbacks) {
            callbacks.bindScreens(this.mOrderedScreenIds);
        }

        public /* synthetic */ void lambda$bind$3$BaseLoaderResults$WorkspaceBinder(BgDataModel.FixedContainerItems fixedContainerItems) {
            executeCallbacksTask(new LauncherModel.CallbackTask() {
                public final void execute(BgDataModel.Callbacks callbacks) {
                    callbacks.bindExtraContainerItems(BgDataModel.FixedContainerItems.this);
                }
            }, this.mUiExecutor);
        }

        public /* synthetic */ void lambda$bind$5$BaseLoaderResults$WorkspaceBinder() {
            Executors.MODEL_EXECUTOR.setThreadPriority(0);
            ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mApp.getContext()).resumeModelPush(2);
        }

        static /* synthetic */ void lambda$bind$6(IntSet intSet, RunnableList runnableList, BgDataModel.Callbacks callbacks) {
            Executors.MODEL_EXECUTOR.setThreadPriority(10);
            callbacks.onInitialBindComplete(intSet, runnableList);
        }

        private void bindWorkspaceItems(ArrayList<ItemInfo> arrayList, Executor executor) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                int i2 = i + 6;
                executeCallbacksTask(new LauncherModel.CallbackTask(arrayList, i, i2 <= size ? 6 : size - i) {
                    public final /* synthetic */ ArrayList f$0;
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void execute(BgDataModel.Callbacks callbacks) {
                        callbacks.bindItems(this.f$0.subList(this.f$1, this.f$2 + this.f$1), false);
                    }
                }, executor);
                i = i2;
            }
        }

        private void bindAppWidgets(List<LauncherAppWidgetInfo> list, Executor executor) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                executeCallbacksTask(new LauncherModel.CallbackTask() {
                    public final void execute(BgDataModel.Callbacks callbacks) {
                        callbacks.bindItems(Collections.singletonList(ItemInfo.this), false);
                    }
                }, executor);
            }
        }

        /* access modifiers changed from: protected */
        public void executeCallbacksTask(LauncherModel.CallbackTask callbackTask, Executor executor) {
            executor.execute(new Runnable(callbackTask) {
                public final /* synthetic */ LauncherModel.CallbackTask f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BaseLoaderResults.WorkspaceBinder.this.lambda$executeCallbacksTask$9$BaseLoaderResults$WorkspaceBinder(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$executeCallbacksTask$9$BaseLoaderResults$WorkspaceBinder(LauncherModel.CallbackTask callbackTask) {
            if (this.mMyBindingId != this.mBgDataModel.lastBindId) {
                Log.d(BaseLoaderResults.TAG, "Too many consecutive reloads, skipping obsolete data-bind");
            } else {
                callbackTask.execute(this.mCallbacks);
            }
        }
    }
}
