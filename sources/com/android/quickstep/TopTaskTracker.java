package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.TraceHelper;
import com.android.quickstep.TopTaskTracker;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import com.android.wm.shell.splitscreen.ISplitScreenListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TopTaskTracker extends ISplitScreenListener.Stub implements TaskStackChangeListener {
    private static final int HISTORY_SIZE = 5;
    public static MainThreadInitializedObject<TopTaskTracker> INSTANCE = new MainThreadInitializedObject<>($$Lambda$TopTaskTracker$k8JRDra3b9FDVjg4CHizpkZXnc8.INSTANCE);
    private static final int SPLIT_SCREEN_STATUS = 0;
    private Context mContext;
    Handler mHandler;
    private final SplitConfigurationOptions.StagedSplitTaskPosition mMainStagePosition;
    private final LinkedList<ActivityManager.RunningTaskInfo> mOrderedTaskList = new LinkedList<>();
    private int mPinnedTaskId;
    private final SplitConfigurationOptions.StagedSplitTaskPosition mSideStagePosition;
    private boolean mSplitScreen;

    public static /* synthetic */ TopTaskTracker lambda$k8JRDra3b9FDVjg4CHizpkZXnc8(Context context) {
        return new TopTaskTracker(context);
    }

    private TopTaskTracker(Context context) {
        SplitConfigurationOptions.StagedSplitTaskPosition stagedSplitTaskPosition = new SplitConfigurationOptions.StagedSplitTaskPosition();
        this.mMainStagePosition = stagedSplitTaskPosition;
        SplitConfigurationOptions.StagedSplitTaskPosition stagedSplitTaskPosition2 = new SplitConfigurationOptions.StagedSplitTaskPosition();
        this.mSideStagePosition = stagedSplitTaskPosition2;
        this.mPinnedTaskId = -1;
        this.mSplitScreen = false;
        this.mHandler = new Handler() {
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 0) {
                    TopTaskTracker.this.sendSplitScreenStatus();
                    sendEmptyMessageDelayed(0, 5000);
                }
            }
        };
        this.mContext = context;
        stagedSplitTaskPosition.stageType = 0;
        stagedSplitTaskPosition2.stageType = 1;
        TaskStackChangeListeners.getInstance().registerTaskStackListener(this);
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).registerSplitScreenListener(this);
    }

    static /* synthetic */ boolean lambda$onTaskRemoved$0(int i, ActivityManager.RunningTaskInfo runningTaskInfo) {
        return runningTaskInfo.taskId == i;
    }

    public void onTaskRemoved(int i) {
        this.mOrderedTaskList.removeIf(new Predicate(i) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return TopTaskTracker.lambda$onTaskRemoved$0(this.f$0, (ActivityManager.RunningTaskInfo) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$onTaskMovedToFront$1(ActivityManager.RunningTaskInfo runningTaskInfo, ActivityManager.RunningTaskInfo runningTaskInfo2) {
        return runningTaskInfo2.taskId == runningTaskInfo.taskId;
    }

    public void onTaskMovedToFront(ActivityManager.RunningTaskInfo runningTaskInfo) {
        this.mOrderedTaskList.removeIf(new Predicate(runningTaskInfo) {
            public final /* synthetic */ ActivityManager.RunningTaskInfo f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return TopTaskTracker.lambda$onTaskMovedToFront$1(this.f$0, (ActivityManager.RunningTaskInfo) obj);
            }
        });
        this.mOrderedTaskList.addFirst(runningTaskInfo);
        if (this.mOrderedTaskList.size() >= 5) {
            Iterator<ActivityManager.RunningTaskInfo> descendingIterator = this.mOrderedTaskList.descendingIterator();
            while (descendingIterator.hasNext()) {
                ActivityManager.RunningTaskInfo next = descendingIterator.next();
                if (next.taskId != runningTaskInfo.taskId && next.taskId != this.mMainStagePosition.taskId && next.taskId != this.mSideStagePosition.taskId) {
                    descendingIterator.remove();
                    return;
                }
            }
        }
    }

    public void onStagePositionChanged(int i, int i2) {
        if (i == 0) {
            this.mMainStagePosition.stagePosition = i2;
        } else {
            this.mSideStagePosition.stagePosition = i2;
        }
    }

    public void onTaskStageChanged(int i, int i2, boolean z) {
        Log.d("TopTaskTracker", "visible = " + z + ", stage = " + i2);
        boolean z2 = i2 != -1;
        SystemProperties.set("zxw_in_split_screen", z2 ? "1" : "0");
        if (this.mSplitScreen != z2) {
            this.mSplitScreen = z2;
            this.mHandler.removeMessages(0);
            this.mHandler.sendEmptyMessage(0);
        }
        if (!z) {
            if (this.mMainStagePosition.taskId == i) {
                resetTaskId(this.mMainStagePosition);
            } else if (this.mSideStagePosition.taskId == i) {
                resetTaskId(this.mSideStagePosition);
            }
        } else if (i2 == -1) {
            resetTaskId(i == this.mMainStagePosition.taskId ? this.mMainStagePosition : this.mSideStagePosition);
        } else if (i2 == 0) {
            this.mMainStagePosition.taskId = i;
        } else {
            this.mSideStagePosition.taskId = i;
        }
    }

    public void onActivityPinned(String str, int i, int i2, int i3) {
        this.mPinnedTaskId = i2;
    }

    public void onActivityUnpinned() {
        this.mPinnedTaskId = -1;
    }

    private void resetTaskId(SplitConfigurationOptions.StagedSplitTaskPosition stagedSplitTaskPosition) {
        stagedSplitTaskPosition.taskId = -1;
    }

    public int[] getRunningSplitTaskIds() {
        if (this.mMainStagePosition.taskId == -1 || this.mSideStagePosition.taskId == -1) {
            return new int[0];
        }
        int[] iArr = new int[2];
        if (this.mMainStagePosition.stagePosition == 0) {
            iArr[0] = this.mMainStagePosition.taskId;
            iArr[1] = this.mSideStagePosition.taskId;
        } else {
            iArr[1] = this.mMainStagePosition.taskId;
            iArr[0] = this.mSideStagePosition.taskId;
        }
        return iArr;
    }

    public CachedTaskInfo getCachedTopTask(boolean z) {
        if (z) {
            return new CachedTaskInfo(Arrays.asList((ActivityManager.RunningTaskInfo[]) TraceHelper.allowIpcs("getCachedTopTask.true", $$Lambda$TopTaskTracker$ja5H4EjvNDFxWHB43kwiTOCubRE.INSTANCE)));
        }
        if (this.mOrderedTaskList.isEmpty()) {
            Collections.addAll(this.mOrderedTaskList, (ActivityManager.RunningTaskInfo[]) TraceHelper.allowIpcs("getCachedTopTask.false", $$Lambda$TopTaskTracker$z5NnLW2RfhjzLdBEqnd9OJIpl8.INSTANCE));
        }
        ArrayList arrayList = new ArrayList(this.mOrderedTaskList);
        arrayList.removeIf(new Predicate() {
            public final boolean test(Object obj) {
                return TopTaskTracker.this.lambda$getCachedTopTask$4$TopTaskTracker((ActivityManager.RunningTaskInfo) obj);
            }
        });
        return new CachedTaskInfo(arrayList);
    }

    public /* synthetic */ boolean lambda$getCachedTopTask$4$TopTaskTracker(ActivityManager.RunningTaskInfo runningTaskInfo) {
        return runningTaskInfo.taskId == this.mPinnedTaskId;
    }

    public static class CachedTaskInfo {
        private final List<ActivityManager.RunningTaskInfo> mAllCachedTasks;
        private final ActivityManager.RunningTaskInfo mTopTask;

        CachedTaskInfo(List<ActivityManager.RunningTaskInfo> list) {
            this.mAllCachedTasks = list;
            this.mTopTask = list.isEmpty() ? null : list.get(0);
        }

        public int getTaskId() {
            ActivityManager.RunningTaskInfo runningTaskInfo = this.mTopTask;
            if (runningTaskInfo == null) {
                return -1;
            }
            return runningTaskInfo.taskId;
        }

        public boolean isRootChooseActivity() {
            ActivityManager.RunningTaskInfo runningTaskInfo = this.mTopTask;
            return runningTaskInfo != null && "android.intent.action.CHOOSER".equals(runningTaskInfo.baseIntent.getAction());
        }

        public boolean isExcludedAssistant() {
            ActivityManager.RunningTaskInfo runningTaskInfo = this.mTopTask;
            return (runningTaskInfo == null || runningTaskInfo.configuration.windowConfiguration.getActivityType() != 4 || (this.mTopTask.baseIntent.getFlags() & 8388608) == 0) ? false : true;
        }

        public boolean isHomeTask() {
            ActivityManager.RunningTaskInfo runningTaskInfo = this.mTopTask;
            return runningTaskInfo != null && runningTaskInfo.configuration.windowConfiguration.getActivityType() == 2;
        }

        public Task[] getPlaceholderTasks() {
            if (this.mTopTask == null) {
                return new Task[0];
            }
            return new Task[]{Task.from(new Task.TaskKey(this.mTopTask), this.mTopTask, false)};
        }

        public Task[] getPlaceholderTasks(int[] iArr) {
            if (this.mTopTask == null) {
                return new Task[0];
            }
            Task[] taskArr = new Task[iArr.length];
            for (int i = 0; i < iArr.length; i++) {
                this.mAllCachedTasks.forEach(new Consumer(iArr[i], taskArr, i) {
                    public final /* synthetic */ int f$0;
                    public final /* synthetic */ Task[] f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void accept(Object obj) {
                        TopTaskTracker.CachedTaskInfo.lambda$getPlaceholderTasks$0(this.f$0, this.f$1, this.f$2, (ActivityManager.RunningTaskInfo) obj);
                    }
                });
            }
            return taskArr;
        }

        static /* synthetic */ void lambda$getPlaceholderTasks$0(int i, Task[] taskArr, int i2, ActivityManager.RunningTaskInfo runningTaskInfo) {
            if (runningTaskInfo.taskId == i) {
                taskArr[i2] = Task.from(new Task.TaskKey(runningTaskInfo), runningTaskInfo, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendSplitScreenStatus() {
        Intent intent;
        if (this.mSplitScreen) {
            intent = new Intent("com.android.launcher3.SPLIT_SCREEN");
            intent.putExtra(NotificationCompat.CATEGORY_STATUS, 1);
        } else {
            intent = new Intent("com.android.launcher3.SPLIT_SCREEN");
            intent.putExtra(NotificationCompat.CATEGORY_STATUS, 0);
        }
        Context context = this.mContext;
        if (context != null) {
            context.sendBroadcastAsUser(intent, UserHandle.ALL);
        }
    }
}
