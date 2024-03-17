package com.android.quickstep.util;

import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.systemui.shared.recents.model.Task;

public class GroupTask {
    public SplitConfigurationOptions.StagedSplitBounds mStagedSplitBounds;
    public Task task1;
    public Task task2;

    public GroupTask(Task task, Task task3, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds) {
        this.task1 = task;
        this.task2 = task3;
        this.mStagedSplitBounds = stagedSplitBounds;
    }

    public GroupTask(GroupTask groupTask) {
        this.task1 = new Task(groupTask.task1);
        this.task2 = groupTask.task2 != null ? new Task(groupTask.task2) : null;
        this.mStagedSplitBounds = groupTask.mStagedSplitBounds;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r1.task2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean containsTask(int r2) {
        /*
            r1 = this;
            com.android.systemui.shared.recents.model.Task r0 = r1.task1
            com.android.systemui.shared.recents.model.Task$TaskKey r0 = r0.key
            int r0 = r0.id
            if (r0 == r2) goto L_0x0015
            com.android.systemui.shared.recents.model.Task r0 = r1.task2
            if (r0 == 0) goto L_0x0013
            com.android.systemui.shared.recents.model.Task$TaskKey r0 = r0.key
            int r0 = r0.id
            if (r0 != r2) goto L_0x0013
            goto L_0x0015
        L_0x0013:
            r2 = 0
            goto L_0x0016
        L_0x0015:
            r2 = 1
        L_0x0016:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.util.GroupTask.containsTask(int):boolean");
    }

    public boolean hasMultipleTasks() {
        return this.task2 != null;
    }
}
