package com.android.quickstep;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.icons.IconProvider;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.quickstep.util.GroupTask;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.KeyguardManagerCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RecentsModel implements IconProvider.IconChangeListener, TaskStackChangeListener {
    public static final MainThreadInitializedObject<RecentsModel> INSTANCE = new MainThreadInitializedObject<>($$Lambda$RecentsModel$P93tHa31NLuzhVvQ3PlsvxKGnOE.INSTANCE);
    private static final Executor RECENTS_MODEL_EXECUTOR = Executors.newSingleThreadExecutor(new Executors.SimpleThreadFactory("TaskThumbnailIconCache-", 10));
    private final Context mContext;
    private final TaskIconCache mIconCache;
    private final RecentTasksList mTaskList;
    private final TaskThumbnailCache mThumbnailCache;
    private final List<TaskVisualsChangeListener> mThumbnailChangeListeners = new ArrayList();

    public interface TaskVisualsChangeListener {
        void onTaskIconChanged(String str, UserHandle userHandle);

        Task onTaskThumbnailChanged(int i, ThumbnailData thumbnailData);
    }

    public static /* synthetic */ RecentsModel lambda$P93tHa31NLuzhVvQ3PlsvxKGnOE(Context context) {
        return new RecentsModel(context);
    }

    private RecentsModel(Context context) {
        this.mContext = context;
        this.mTaskList = new RecentTasksList(com.android.launcher3.util.Executors.MAIN_EXECUTOR, new KeyguardManagerCompat(context), SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context));
        IconProvider iconProvider = new IconProvider(context);
        Executor executor = RECENTS_MODEL_EXECUTOR;
        this.mIconCache = new TaskIconCache(context, executor, iconProvider);
        this.mThumbnailCache = new TaskThumbnailCache(context, executor);
        TaskStackChangeListeners.getInstance().registerTaskStackListener(this);
        iconProvider.registerIconChangeListener(this, com.android.launcher3.util.Executors.MAIN_EXECUTOR.getHandler());
    }

    public TaskIconCache getIconCache() {
        return this.mIconCache;
    }

    public TaskThumbnailCache getThumbnailCache() {
        return this.mThumbnailCache;
    }

    public int getTasks(Consumer<ArrayList<GroupTask>> consumer) {
        return this.mTaskList.getTasks(false, consumer);
    }

    public boolean isTaskListValid(int i) {
        return this.mTaskList.isTaskListValid(i);
    }

    public boolean isLoadingTasksInBackground() {
        return this.mTaskList.isLoadingTasksInBackground();
    }

    public void isTaskRemoved(int i, Consumer<Boolean> consumer) {
        this.mTaskList.getTasks(true, new Consumer(i, consumer) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ Consumer f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                RecentsModel.lambda$isTaskRemoved$0(this.f$0, this.f$1, (ArrayList) obj);
            }
        });
    }

    static /* synthetic */ void lambda$isTaskRemoved$0(int i, Consumer consumer, ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (((GroupTask) it.next()).containsTask(i)) {
                consumer.accept(false);
                return;
            }
        }
        consumer.accept(true);
    }

    public void onTaskStackChangedBackground() {
        if (this.mThumbnailCache.isPreloadingEnabled() && TaskUtils.checkCurrentOrManagedUserId(Process.myUserHandle().getIdentifier(), this.mContext)) {
            ActivityManager.RunningTaskInfo runningTask = ActivityManagerWrapper.getInstance().getRunningTask();
            this.mTaskList.getTaskKeys(this.mThumbnailCache.getCacheSize(), new Consumer(runningTask != null ? runningTask.id : -1) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    RecentsModel.this.lambda$onTaskStackChangedBackground$1$RecentsModel(this.f$1, (ArrayList) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onTaskStackChangedBackground$1$RecentsModel(int i, ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            GroupTask groupTask = (GroupTask) it.next();
            if (!groupTask.containsTask(i)) {
                this.mThumbnailCache.updateThumbnailInCache(groupTask.task1);
                this.mThumbnailCache.updateThumbnailInCache(groupTask.task2);
            }
        }
    }

    public boolean onTaskSnapshotChanged(int i, ThumbnailData thumbnailData) {
        this.mThumbnailCache.updateTaskSnapShot(i, thumbnailData);
        for (int size = this.mThumbnailChangeListeners.size() - 1; size >= 0; size--) {
            Task onTaskThumbnailChanged = this.mThumbnailChangeListeners.get(size).onTaskThumbnailChanged(i, thumbnailData);
            if (onTaskThumbnailChanged != null) {
                onTaskThumbnailChanged.thumbnail = thumbnailData;
            }
        }
        return true;
    }

    public void onTaskRemoved(int i) {
        Task.TaskKey taskKey = new Task.TaskKey(i, 0, new Intent(), (ComponentName) null, 0, 0);
        this.mThumbnailCache.remove(taskKey);
        this.mIconCache.onTaskRemoved(taskKey);
    }

    public void onTrimMemory(int i) {
        if (i == 20) {
            this.mThumbnailCache.getHighResLoadingState().setVisible(false);
        }
        if (i == 15) {
            this.mThumbnailCache.clear();
            this.mIconCache.clearCache();
        }
    }

    public void onAppIconChanged(String str, UserHandle userHandle) {
        this.mIconCache.invalidateCacheEntries(str, userHandle);
        for (int size = this.mThumbnailChangeListeners.size() - 1; size >= 0; size--) {
            this.mThumbnailChangeListeners.get(size).onTaskIconChanged(str, userHandle);
        }
    }

    public void onSystemIconStateChanged(String str) {
        this.mIconCache.clearCache();
    }

    public void addThumbnailChangeListener(TaskVisualsChangeListener taskVisualsChangeListener) {
        this.mThumbnailChangeListeners.add(taskVisualsChangeListener);
    }

    public void removeThumbnailChangeListener(TaskVisualsChangeListener taskVisualsChangeListener) {
        this.mThumbnailChangeListeners.remove(taskVisualsChangeListener);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "RecentsModel:");
        this.mTaskList.dump("  ", printWriter);
    }
}
