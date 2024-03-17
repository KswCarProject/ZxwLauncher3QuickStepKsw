package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityManager;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.IconProvider;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Preconditions;
import com.android.quickstep.util.CancellableTask;
import com.android.quickstep.util.TaskKeyLruCache;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.PackageManagerWrapper;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TaskIconCache implements DisplayController.DisplayInfoChangeListener {
    private final AccessibilityManager mAccessibilityManager;
    private final Executor mBgExecutor;
    private final Context mContext;
    private BitmapInfo mDefaultIconBase = null;
    private final SparseArray<BitmapInfo> mDefaultIcons = new SparseArray<>();
    private final TaskKeyLruCache<TaskCacheEntry> mIconCache;
    private BaseIconFactory mIconFactory;
    private final IconProvider mIconProvider;

    public TaskIconCache(Context context, Executor executor, IconProvider iconProvider) {
        this.mContext = context;
        this.mBgExecutor = executor;
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mIconProvider = iconProvider;
        this.mIconCache = new TaskKeyLruCache<>(context.getResources().getInteger(R.integer.recentsIconCacheSize));
        DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).addChangeListener(this);
    }

    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
        if ((i & 4) != 0) {
            clearCache();
        }
    }

    public CancellableTask updateIconInBackground(final Task task, final Consumer<Task> consumer) {
        Preconditions.assertUIThread();
        if (task.icon != null) {
            consumer.accept(task);
            return null;
        }
        AnonymousClass1 r0 = new CancellableTask<TaskCacheEntry>() {
            public TaskCacheEntry getResultOnBg() {
                return TaskIconCache.this.getCacheEntry(task);
            }

            public void handleResult(TaskCacheEntry taskCacheEntry) {
                task.icon = taskCacheEntry.icon;
                task.titleDescription = taskCacheEntry.contentDescription;
                consumer.accept(task);
            }
        };
        this.mBgExecutor.execute(r0);
        return r0;
    }

    public void clearCache() {
        this.mBgExecutor.execute(new Runnable() {
            public final void run() {
                TaskIconCache.this.resetFactory();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void onTaskRemoved(Task.TaskKey taskKey) {
        this.mIconCache.remove(taskKey);
    }

    /* access modifiers changed from: package-private */
    public void invalidateCacheEntries(String str, UserHandle userHandle) {
        this.mBgExecutor.execute(new Runnable(str, userHandle) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ UserHandle f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TaskIconCache.this.lambda$invalidateCacheEntries$1$TaskIconCache(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$invalidateCacheEntries$1$TaskIconCache(String str, UserHandle userHandle) {
        this.mIconCache.removeAll(new Predicate(str, userHandle) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ UserHandle f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return TaskIconCache.lambda$invalidateCacheEntries$0(this.f$0, this.f$1, (Task.TaskKey) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$invalidateCacheEntries$0(String str, UserHandle userHandle, Task.TaskKey taskKey) {
        return str.equals(taskKey.getPackageName()) && userHandle.getIdentifier() == taskKey.userId;
    }

    /* access modifiers changed from: private */
    public TaskCacheEntry getCacheEntry(Task task) {
        TaskCacheEntry andInvalidateIfModified = this.mIconCache.getAndInvalidateIfModified(task.key);
        if (andInvalidateIfModified != null) {
            return andInvalidateIfModified;
        }
        ActivityManager.TaskDescription taskDescription = task.taskDescription;
        Task.TaskKey taskKey = task.key;
        ActivityInfo activityInfo = null;
        TaskCacheEntry taskCacheEntry = new TaskCacheEntry();
        Bitmap icon = getIcon(taskDescription, taskKey.userId);
        if (icon != null) {
            taskCacheEntry.icon = getBitmapInfo(new BitmapDrawable(this.mContext.getResources(), icon), taskKey.userId, taskDescription.getPrimaryColor(), false).newIcon(this.mContext);
        } else {
            activityInfo = PackageManagerWrapper.getInstance().getActivityInfo(taskKey.getComponent(), taskKey.userId);
            if (activityInfo != null) {
                taskCacheEntry.icon = getBitmapInfo(this.mIconProvider.getIcon(activityInfo), taskKey.userId, taskDescription.getPrimaryColor(), activityInfo.applicationInfo.isInstantApp()).newIcon(this.mContext);
            } else {
                taskCacheEntry.icon = getDefaultIcon(taskKey.userId);
            }
        }
        if (this.mAccessibilityManager.isEnabled()) {
            if (activityInfo == null) {
                activityInfo = PackageManagerWrapper.getInstance().getActivityInfo(taskKey.getComponent(), taskKey.userId);
            }
            if (activityInfo != null) {
                taskCacheEntry.contentDescription = getBadgedContentDescription(activityInfo, task.key.userId, task.taskDescription);
            }
        }
        this.mIconCache.put(task.key, taskCacheEntry);
        return taskCacheEntry;
    }

    private Bitmap getIcon(ActivityManager.TaskDescription taskDescription, int i) {
        if (taskDescription.getInMemoryIcon() != null) {
            return taskDescription.getInMemoryIcon();
        }
        return ActivityManager.TaskDescription.loadTaskDescriptionIcon(taskDescription.getIconFilename(), i);
    }

    private String getBadgedContentDescription(ActivityInfo activityInfo, int i, ActivityManager.TaskDescription taskDescription) {
        String str;
        PackageManager packageManager = this.mContext.getPackageManager();
        if (taskDescription == null) {
            str = null;
        } else {
            str = Utilities.trim(taskDescription.getLabel());
        }
        if (TextUtils.isEmpty(str)) {
            str = Utilities.trim(activityInfo.loadLabel(packageManager));
        }
        String trim = Utilities.trim(activityInfo.applicationInfo.loadLabel(packageManager));
        String charSequence = i != UserHandle.myUserId() ? packageManager.getUserBadgedLabel(trim, UserHandle.of(i)).toString() : trim;
        return trim.equals(str) ? charSequence : charSequence + " " + str;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0067, code lost:
        return r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable getDefaultIcon(int r6) {
        /*
            r5 = this;
            android.util.SparseArray<com.android.launcher3.icons.BitmapInfo> r0 = r5.mDefaultIcons
            monitor-enter(r0)
            com.android.launcher3.icons.BitmapInfo r1 = r5.mDefaultIconBase     // Catch:{ all -> 0x0074 }
            if (r1 != 0) goto L_0x0023
            com.android.launcher3.icons.BaseIconFactory r1 = r5.getIconFactory()     // Catch:{ all -> 0x0074 }
            com.android.launcher3.icons.BitmapInfo r2 = r1.makeDefaultIcon()     // Catch:{ all -> 0x0017 }
            r5.mDefaultIconBase = r2     // Catch:{ all -> 0x0017 }
            if (r1 == 0) goto L_0x0023
            r1.close()     // Catch:{ all -> 0x0074 }
            goto L_0x0023
        L_0x0017:
            r6 = move-exception
            if (r1 == 0) goto L_0x0022
            r1.close()     // Catch:{ all -> 0x001e }
            goto L_0x0022
        L_0x001e:
            r1 = move-exception
            r6.addSuppressed(r1)     // Catch:{ all -> 0x0074 }
        L_0x0022:
            throw r6     // Catch:{ all -> 0x0074 }
        L_0x0023:
            android.util.SparseArray<com.android.launcher3.icons.BitmapInfo> r1 = r5.mDefaultIcons     // Catch:{ all -> 0x0074 }
            int r1 = r1.indexOfKey(r6)     // Catch:{ all -> 0x0074 }
            if (r1 < 0) goto L_0x003b
            android.util.SparseArray<com.android.launcher3.icons.BitmapInfo> r6 = r5.mDefaultIcons     // Catch:{ all -> 0x0074 }
            java.lang.Object r6 = r6.valueAt(r1)     // Catch:{ all -> 0x0074 }
            com.android.launcher3.icons.BitmapInfo r6 = (com.android.launcher3.icons.BitmapInfo) r6     // Catch:{ all -> 0x0074 }
            android.content.Context r1 = r5.mContext     // Catch:{ all -> 0x0074 }
            com.android.launcher3.icons.FastBitmapDrawable r6 = r6.newIcon(r1)     // Catch:{ all -> 0x0074 }
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            return r6
        L_0x003b:
            com.android.launcher3.icons.BaseIconFactory r1 = r5.getIconFactory()     // Catch:{ all -> 0x0074 }
            com.android.launcher3.icons.BitmapInfo r2 = r5.mDefaultIconBase     // Catch:{ all -> 0x0068 }
            com.android.launcher3.icons.BaseIconFactory$IconOptions r3 = new com.android.launcher3.icons.BaseIconFactory$IconOptions     // Catch:{ all -> 0x0068 }
            r3.<init>()     // Catch:{ all -> 0x0068 }
            android.os.UserHandle r4 = android.os.UserHandle.of(r6)     // Catch:{ all -> 0x0068 }
            com.android.launcher3.icons.BaseIconFactory$IconOptions r3 = r3.setUser(r4)     // Catch:{ all -> 0x0068 }
            com.android.launcher3.util.FlagOp r3 = r1.getBitmapFlagOp(r3)     // Catch:{ all -> 0x0068 }
            com.android.launcher3.icons.BitmapInfo r2 = r2.withFlags(r3)     // Catch:{ all -> 0x0068 }
            android.util.SparseArray<com.android.launcher3.icons.BitmapInfo> r3 = r5.mDefaultIcons     // Catch:{ all -> 0x0068 }
            r3.put(r6, r2)     // Catch:{ all -> 0x0068 }
            android.content.Context r6 = r5.mContext     // Catch:{ all -> 0x0068 }
            com.android.launcher3.icons.FastBitmapDrawable r6 = r2.newIcon(r6)     // Catch:{ all -> 0x0068 }
            if (r1 == 0) goto L_0x0066
            r1.close()     // Catch:{ all -> 0x0074 }
        L_0x0066:
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            return r6
        L_0x0068:
            r6 = move-exception
            if (r1 == 0) goto L_0x0073
            r1.close()     // Catch:{ all -> 0x006f }
            goto L_0x0073
        L_0x006f:
            r1 = move-exception
            r6.addSuppressed(r1)     // Catch:{ all -> 0x0074 }
        L_0x0073:
            throw r6     // Catch:{ all -> 0x0074 }
        L_0x0074:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.TaskIconCache.getDefaultIcon(int):android.graphics.drawable.Drawable");
    }

    private BitmapInfo getBitmapInfo(Drawable drawable, int i, int i2, boolean z) {
        BaseIconFactory iconFactory = getIconFactory();
        try {
            iconFactory.disableColorExtraction();
            iconFactory.setWrapperBackgroundColor(i2);
            BitmapInfo createBadgedIconBitmap = iconFactory.createBadgedIconBitmap(drawable, new BaseIconFactory.IconOptions().setUser(UserHandle.of(i)).setInstantApp(z));
            if (iconFactory != null) {
                iconFactory.close();
            }
            return createBadgedIconBitmap;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    private BaseIconFactory getIconFactory() {
        if (this.mIconFactory == null) {
            this.mIconFactory = new BaseIconFactory(this.mContext, DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getInfo().getDensityDpi(), this.mContext.getResources().getDimensionPixelSize(R.dimen.taskbar_icon_size));
        }
        return this.mIconFactory;
    }

    /* access modifiers changed from: private */
    public void resetFactory() {
        this.mIconFactory = null;
        this.mIconCache.evictAll();
    }

    private static class TaskCacheEntry {
        public String contentDescription;
        public Drawable icon;

        private TaskCacheEntry() {
            this.contentDescription = "";
        }
    }
}
