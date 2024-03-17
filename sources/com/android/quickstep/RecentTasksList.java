package com.android.quickstep;

import android.app.ActivityManager;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.quickstep.RecentTasksList;
import com.android.quickstep.util.GroupTask;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.KeyguardManagerCompat;
import com.android.wm.shell.recents.IRecentTasksListener;
import com.android.wm.shell.util.GroupedRecentTaskInfo;
import com.android.wm.shell.util.StagedSplitBounds;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;

public class RecentTasksList {
    private static final TaskLoadResult INVALID_RESULT = new TaskLoadResult(-1, false, 0);
    private int mChangeId = 1;
    /* access modifiers changed from: private */
    public final KeyguardManagerCompat mKeyguardManager;
    private boolean mLoadingTasksInBackground;
    /* access modifiers changed from: private */
    public final LooperExecutor mMainThreadExecutor;
    private TaskLoadResult mResultsBg;
    private TaskLoadResult mResultsUi;
    private final SystemUiProxy mSysUiProxy;

    public RecentTasksList(LooperExecutor looperExecutor, KeyguardManagerCompat keyguardManagerCompat, SystemUiProxy systemUiProxy) {
        TaskLoadResult taskLoadResult = INVALID_RESULT;
        this.mResultsBg = taskLoadResult;
        this.mResultsUi = taskLoadResult;
        this.mMainThreadExecutor = looperExecutor;
        this.mKeyguardManager = keyguardManagerCompat;
        this.mSysUiProxy = systemUiProxy;
        systemUiProxy.registerRecentTasksListener(new IRecentTasksListener.Stub() {
            public void onRecentTasksChanged() throws RemoteException {
                Log.d("RecentTasksList", "onRecentTasksChanged");
                RecentTasksList.this.mMainThreadExecutor.execute(new Runnable() {
                    public final void run() {
                        RecentTasksList.this.onRecentTasksChanged();
                    }
                });
            }
        });
    }

    public boolean isLoadingTasksInBackground() {
        return this.mLoadingTasksInBackground;
    }

    public void getTaskKeys(int i, Consumer<ArrayList<GroupTask>> consumer) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(i, consumer) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ Consumer f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RecentTasksList.this.lambda$getTaskKeys$1$RecentTasksList(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$getTaskKeys$1$RecentTasksList(int i, Consumer consumer) {
        this.mMainThreadExecutor.execute(new Runnable(consumer, loadTasksInBackground(i, -1, true)) {
            public final /* synthetic */ Consumer f$0;
            public final /* synthetic */ ArrayList f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                this.f$0.accept(this.f$1);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001e, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int getTasks(boolean r4, java.util.function.Consumer<java.util.ArrayList<com.android.quickstep.util.GroupTask>> r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            int r0 = r3.mChangeId     // Catch:{ all -> 0x002e }
            com.android.quickstep.RecentTasksList$TaskLoadResult r1 = r3.mResultsUi     // Catch:{ all -> 0x002e }
            boolean r1 = r1.isValidForRequest(r0, r4)     // Catch:{ all -> 0x002e }
            if (r1 == 0) goto L_0x001f
            if (r5 == 0) goto L_0x001d
            com.android.quickstep.RecentTasksList$TaskLoadResult r4 = r3.mResultsUi     // Catch:{ all -> 0x002e }
            java.util.ArrayList r4 = r3.copyOf(r4)     // Catch:{ all -> 0x002e }
            com.android.launcher3.util.LooperExecutor r1 = r3.mMainThreadExecutor     // Catch:{ all -> 0x002e }
            com.android.quickstep.-$$Lambda$RecentTasksList$fLJ5feW3MV-reLgv2Kxw34qiEBA r2 = new com.android.quickstep.-$$Lambda$RecentTasksList$fLJ5feW3MV-reLgv2Kxw34qiEBA     // Catch:{ all -> 0x002e }
            r2.<init>(r5, r4)     // Catch:{ all -> 0x002e }
            r1.post(r2)     // Catch:{ all -> 0x002e }
        L_0x001d:
            monitor-exit(r3)
            return r0
        L_0x001f:
            r1 = 1
            r3.mLoadingTasksInBackground = r1     // Catch:{ all -> 0x002e }
            com.android.launcher3.util.LooperExecutor r1 = com.android.launcher3.util.Executors.UI_HELPER_EXECUTOR     // Catch:{ all -> 0x002e }
            com.android.quickstep.-$$Lambda$RecentTasksList$0rZOIGHjKo7aWwgK3nVVtia3YJc r2 = new com.android.quickstep.-$$Lambda$RecentTasksList$0rZOIGHjKo7aWwgK3nVVtia3YJc     // Catch:{ all -> 0x002e }
            r2.<init>(r0, r4, r5)     // Catch:{ all -> 0x002e }
            r1.execute(r2)     // Catch:{ all -> 0x002e }
            monitor-exit(r3)
            return r0
        L_0x002e:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.RecentTasksList.getTasks(boolean, java.util.function.Consumer):int");
    }

    public /* synthetic */ void lambda$getTasks$4$RecentTasksList(int i, boolean z, Consumer consumer) {
        if (!this.mResultsBg.isValidForRequest(i, z)) {
            this.mResultsBg = loadTasksInBackground(Integer.MAX_VALUE, i, z);
        }
        this.mMainThreadExecutor.execute(new Runnable(this.mResultsBg, consumer) {
            public final /* synthetic */ RecentTasksList.TaskLoadResult f$1;
            public final /* synthetic */ Consumer f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RecentTasksList.this.lambda$getTasks$3$RecentTasksList(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$getTasks$3$RecentTasksList(TaskLoadResult taskLoadResult, Consumer consumer) {
        this.mLoadingTasksInBackground = false;
        this.mResultsUi = taskLoadResult;
        if (consumer != null) {
            consumer.accept(copyOf(taskLoadResult));
        }
    }

    public synchronized boolean isTaskListValid(int i) {
        return this.mChangeId == i;
    }

    public void onRecentTasksChanged() {
        invalidateLoadedTasks();
    }

    private synchronized void invalidateLoadedTasks() {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable() {
            public final void run() {
                RecentTasksList.this.lambda$invalidateLoadedTasks$5$RecentTasksList();
            }
        });
        this.mResultsUi = INVALID_RESULT;
        this.mChangeId++;
    }

    public /* synthetic */ void lambda$invalidateLoadedTasks$5$RecentTasksList() {
        this.mResultsBg = INVALID_RESULT;
    }

    /* access modifiers changed from: package-private */
    public TaskLoadResult loadTasksInBackground(int i, int i2, boolean z) {
        Task task;
        ArrayList<GroupedRecentTaskInfo> recentTasks = this.mSysUiProxy.getRecentTasks(i, Process.myUserHandle().getIdentifier());
        Log.d("RecentTasksList", "loadTasksInBackground size = " + recentTasks.size());
        Collections.reverse(recentTasks);
        AnonymousClass2 r0 = new SparseBooleanArray() {
            public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            public boolean get(int i) {
                if (indexOfKey(i) < 0) {
                    put(i, RecentTasksList.this.mKeyguardManager.isDeviceLocked(i));
                }
                return super.get(i);
            }
        };
        TaskLoadResult taskLoadResult = new TaskLoadResult(i2, z, recentTasks.size());
        Iterator<GroupedRecentTaskInfo> it = recentTasks.iterator();
        while (it.hasNext()) {
            GroupedRecentTaskInfo next = it.next();
            ActivityManager.RecentTaskInfo recentTaskInfo = next.mTaskInfo1;
            ActivityManager.RecentTaskInfo recentTaskInfo2 = next.mTaskInfo2;
            Task.TaskKey taskKey = new Task.TaskKey(recentTaskInfo);
            if (z) {
                task = new Task(taskKey);
            } else {
                task = Task.from(taskKey, recentTaskInfo, r0.get(taskKey.userId));
            }
            task.setLastSnapshotData(recentTaskInfo);
            Task task2 = null;
            if (recentTaskInfo2 != null) {
                Task.TaskKey taskKey2 = new Task.TaskKey(recentTaskInfo2);
                if (z) {
                    task2 = new Task(taskKey2);
                } else {
                    task2 = Task.from(taskKey2, recentTaskInfo2, r0.get(taskKey2.userId));
                }
                task2.setLastSnapshotData(recentTaskInfo2);
            }
            taskLoadResult.add(new GroupTask(task, task2, convertSplitBounds(next.mStagedSplitBounds)));
        }
        return taskLoadResult;
    }

    private SplitConfigurationOptions.StagedSplitBounds convertSplitBounds(StagedSplitBounds stagedSplitBounds) {
        if (stagedSplitBounds == null) {
            return null;
        }
        return new SplitConfigurationOptions.StagedSplitBounds(stagedSplitBounds.leftTopBounds, stagedSplitBounds.rightBottomBounds, stagedSplitBounds.leftTopTaskId, stagedSplitBounds.rightBottomTaskId);
    }

    private ArrayList<GroupTask> copyOf(ArrayList<GroupTask> arrayList) {
        ArrayList<GroupTask> arrayList2 = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList2.add(new GroupTask(arrayList.get(i)));
        }
        return arrayList2;
    }

    public void dump(String str, PrintWriter printWriter) {
        Object obj;
        printWriter.println(str + "RecentTasksList:");
        printWriter.println(str + "  mChangeId=" + this.mChangeId);
        printWriter.println(str + "  mResultsUi=[id=" + this.mResultsUi.mRequestId + ", tasks=");
        Iterator it = this.mResultsUi.iterator();
        while (true) {
            obj = "-1";
            if (!it.hasNext()) {
                break;
            }
            GroupTask groupTask = (GroupTask) it.next();
            StringBuilder append = new StringBuilder().append(str).append("    t1=").append(groupTask.task1.key.id).append(" t2=");
            if (groupTask.hasMultipleTasks()) {
                obj = Integer.valueOf(groupTask.task2.key.id);
            }
            printWriter.println(append.append(obj).toString());
        }
        printWriter.println(str + "  ]");
        ArrayList<GroupedRecentTaskInfo> recentTasks = this.mSysUiProxy.getRecentTasks(Integer.MAX_VALUE, Process.myUserHandle().getIdentifier());
        printWriter.println(str + "  rawTasks=[");
        Iterator<GroupedRecentTaskInfo> it2 = recentTasks.iterator();
        while (it2.hasNext()) {
            GroupedRecentTaskInfo next = it2.next();
            printWriter.println(str + "    t1=" + next.mTaskInfo1.taskId + " t2=" + (next.mTaskInfo2 != null ? Integer.valueOf(next.mTaskInfo2.taskId) : obj));
        }
        printWriter.println(str + "  ]");
    }

    private static class TaskLoadResult extends ArrayList<GroupTask> {
        final boolean mKeysOnly;
        final int mRequestId;

        TaskLoadResult(int i, boolean z, int i2) {
            super(i2);
            this.mRequestId = i;
            this.mKeysOnly = z;
        }

        /* access modifiers changed from: package-private */
        public boolean isValidForRequest(int i, boolean z) {
            return this.mRequestId == i && (!this.mKeysOnly || z);
        }
    }
}
