package com.android.quickstep;

import android.content.Context;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.ArrayList;

public class RemoteTargetGluer {
    private RemoteTargetHandle[] mRemoteTargetHandles;
    private SplitConfigurationOptions.StagedSplitBounds mStagedSplitBounds;

    public RemoteTargetGluer(Context context, BaseActivityInterface baseActivityInterface, RemoteAnimationTargets remoteAnimationTargets) {
        this.mRemoteTargetHandles = createHandles(context, baseActivityInterface, remoteAnimationTargets.apps.length);
    }

    public RemoteTargetGluer(Context context, BaseActivityInterface baseActivityInterface) {
        this.mRemoteTargetHandles = createHandles(context, baseActivityInterface, TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getRunningSplitTaskIds().length != 2 ? 1 : 2);
    }

    private RemoteTargetHandle[] createHandles(Context context, BaseActivityInterface baseActivityInterface, int i) {
        RemoteTargetHandle[] remoteTargetHandleArr = new RemoteTargetHandle[i];
        for (int i2 = 0; i2 < i; i2++) {
            remoteTargetHandleArr[i2] = new RemoteTargetHandle(new TaskViewSimulator(context, baseActivityInterface), new TransformParams());
        }
        return remoteTargetHandleArr;
    }

    public RemoteTargetHandle[] assignTargets(RemoteAnimationTargets remoteAnimationTargets) {
        int i = 0;
        while (true) {
            RemoteTargetHandle[] remoteTargetHandleArr = this.mRemoteTargetHandles;
            if (i >= remoteTargetHandleArr.length) {
                return remoteTargetHandleArr;
            }
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargets.apps[i];
            this.mRemoteTargetHandles[i].mTransformParams.setTargetSet(createRemoteAnimationTargetsForTarget(remoteAnimationTargets, (RemoteAnimationTargetCompat) null));
            this.mRemoteTargetHandles[i].mTaskViewSimulator.setPreview(remoteAnimationTargetCompat, (SplitConfigurationOptions.StagedSplitBounds) null);
            i++;
        }
    }

    public RemoteTargetHandle[] assignTargetsForSplitScreen(Context context, RemoteAnimationTargets remoteAnimationTargets) {
        return assignTargetsForSplitScreen(remoteAnimationTargets, TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getRunningSplitTaskIds());
    }

    public RemoteTargetHandle[] assignTargetsForSplitScreen(RemoteAnimationTargets remoteAnimationTargets, int[] iArr) {
        RemoteTargetHandle[] remoteTargetHandleArr = this.mRemoteTargetHandles;
        if (remoteTargetHandleArr.length == 1) {
            remoteTargetHandleArr[0].mTransformParams.setTargetSet(remoteAnimationTargets);
            if (remoteAnimationTargets.apps.length > 0) {
                this.mRemoteTargetHandles[0].mTaskViewSimulator.setPreview(remoteAnimationTargets.apps[0], (SplitConfigurationOptions.StagedSplitBounds) null);
            }
        } else {
            RemoteAnimationTargetCompat findTask = remoteAnimationTargets.findTask(iArr[0]);
            RemoteAnimationTargetCompat findTask2 = remoteAnimationTargets.findTask(iArr[1]);
            this.mStagedSplitBounds = new SplitConfigurationOptions.StagedSplitBounds(findTask.startScreenSpaceBounds, findTask2.startScreenSpaceBounds, iArr[0], iArr[1]);
            this.mRemoteTargetHandles[0].mTransformParams.setTargetSet(createRemoteAnimationTargetsForTarget(remoteAnimationTargets, findTask2));
            this.mRemoteTargetHandles[0].mTaskViewSimulator.setPreview(findTask, this.mStagedSplitBounds);
            this.mRemoteTargetHandles[1].mTransformParams.setTargetSet(createRemoteAnimationTargetsForTarget(remoteAnimationTargets, findTask));
            this.mRemoteTargetHandles[1].mTaskViewSimulator.setPreview(findTask2, this.mStagedSplitBounds);
        }
        return this.mRemoteTargetHandles;
    }

    private RemoteAnimationTargets createRemoteAnimationTargetsForTarget(RemoteAnimationTargets remoteAnimationTargets, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        ArrayList arrayList = new ArrayList();
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat2 : remoteAnimationTargets.unfilteredApps) {
            if (remoteAnimationTargetCompat2 != remoteAnimationTargetCompat && (remoteAnimationTargetCompat == null || remoteAnimationTargetCompat.taskInfo == null || remoteAnimationTargetCompat2.taskInfo == null || remoteAnimationTargetCompat.taskInfo.parentTaskId != remoteAnimationTargetCompat2.taskInfo.taskId)) {
                arrayList.add(remoteAnimationTargetCompat2);
            }
        }
        return new RemoteAnimationTargets((RemoteAnimationTargetCompat[]) arrayList.toArray(new RemoteAnimationTargetCompat[arrayList.size()]), remoteAnimationTargets.wallpapers, remoteAnimationTargets.nonApps, remoteAnimationTargets.targetMode);
    }

    public RemoteTargetHandle[] getRemoteTargetHandles() {
        return this.mRemoteTargetHandles;
    }

    public SplitConfigurationOptions.StagedSplitBounds getStagedSplitBounds() {
        return this.mStagedSplitBounds;
    }

    public static class RemoteTargetHandle {
        private AnimatorControllerWithResistance mPlaybackController;
        /* access modifiers changed from: private */
        public final TaskViewSimulator mTaskViewSimulator;
        /* access modifiers changed from: private */
        public final TransformParams mTransformParams;

        public RemoteTargetHandle(TaskViewSimulator taskViewSimulator, TransformParams transformParams) {
            this.mTransformParams = transformParams;
            this.mTaskViewSimulator = taskViewSimulator;
        }

        public TaskViewSimulator getTaskViewSimulator() {
            return this.mTaskViewSimulator;
        }

        public TransformParams getTransformParams() {
            return this.mTransformParams;
        }

        public AnimatorControllerWithResistance getPlaybackController() {
            return this.mPlaybackController;
        }

        public void setPlaybackController(AnimatorControllerWithResistance animatorControllerWithResistance) {
            this.mPlaybackController = animatorControllerWithResistance;
        }
    }
}
