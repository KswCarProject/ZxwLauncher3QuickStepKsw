package com.android.quickstep;

import android.content.Context;
import android.content.res.Resources;
import com.android.launcher3.R;
import com.android.launcher3.util.Preconditions;
import com.android.quickstep.util.CancellableTask;
import com.android.quickstep.util.TaskKeyLruCache;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class TaskThumbnailCache {
    private final Executor mBgExecutor;
    /* access modifiers changed from: private */
    public final TaskKeyLruCache<ThumbnailData> mCache;
    private final int mCacheSize;
    private final boolean mEnableTaskSnapshotPreloading;
    private final HighResLoadingState mHighResLoadingState;

    public static class HighResLoadingState {
        private ArrayList<HighResLoadingStateChangedCallback> mCallbacks;
        private boolean mFlingingFast;
        private boolean mForceHighResThumbnails;
        private boolean mHighResLoadingEnabled;
        /* access modifiers changed from: private */
        public boolean mVisible;

        public interface HighResLoadingStateChangedCallback {
            void onHighResLoadingStateChanged(boolean z);
        }

        private HighResLoadingState(Context context) {
            this.mCallbacks = new ArrayList<>();
            this.mForceHighResThumbnails = !TaskThumbnailCache.supportsLowResThumbnails();
        }

        public void addCallback(HighResLoadingStateChangedCallback highResLoadingStateChangedCallback) {
            this.mCallbacks.add(highResLoadingStateChangedCallback);
        }

        public void removeCallback(HighResLoadingStateChangedCallback highResLoadingStateChangedCallback) {
            this.mCallbacks.remove(highResLoadingStateChangedCallback);
        }

        public void setVisible(boolean z) {
            this.mVisible = z;
            updateState();
        }

        public void setFlingingFast(boolean z) {
            this.mFlingingFast = z;
            updateState();
        }

        public boolean isEnabled() {
            return this.mHighResLoadingEnabled;
        }

        private void updateState() {
            boolean z = this.mHighResLoadingEnabled;
            boolean z2 = this.mForceHighResThumbnails || (this.mVisible && !this.mFlingingFast);
            this.mHighResLoadingEnabled = z2;
            if (z != z2) {
                for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                    this.mCallbacks.get(size).onHighResLoadingStateChanged(this.mHighResLoadingEnabled);
                }
            }
        }
    }

    public TaskThumbnailCache(Context context, Executor executor) {
        this.mBgExecutor = executor;
        this.mHighResLoadingState = new HighResLoadingState(context);
        Resources resources = context.getResources();
        int integer = resources.getInteger(R.integer.recentsThumbnailCacheSize);
        this.mCacheSize = integer;
        this.mEnableTaskSnapshotPreloading = resources.getBoolean(R.bool.config_enableTaskSnapshotPreloading);
        this.mCache = new TaskKeyLruCache<>(integer);
    }

    public void updateThumbnailInCache(Task task) {
        if (task != null) {
            Preconditions.assertUIThread();
            if (task.thumbnail == null) {
                updateThumbnailInBackground(task.key, true, new Consumer() {
                    public final void accept(Object obj) {
                        Task.this.thumbnail = (ThumbnailData) obj;
                    }
                });
            }
        }
    }

    public void updateTaskSnapShot(int i, ThumbnailData thumbnailData) {
        Preconditions.assertUIThread();
        this.mCache.updateIfAlreadyInCache(i, thumbnailData);
    }

    public CancellableTask updateThumbnailInBackground(Task task, Consumer<ThumbnailData> consumer) {
        Preconditions.assertUIThread();
        boolean z = !this.mHighResLoadingState.isEnabled();
        if (task.thumbnail == null || task.thumbnail.thumbnail == null || (task.thumbnail.reducedResolution && !z)) {
            return updateThumbnailInBackground(task.key, !this.mHighResLoadingState.isEnabled(), new Consumer(consumer) {
                public final /* synthetic */ Consumer f$1;

                {
                    this.f$1 = r2;
                }

                public final void accept(Object obj) {
                    TaskThumbnailCache.lambda$updateThumbnailInBackground$1(Task.this, this.f$1, (ThumbnailData) obj);
                }
            });
        }
        consumer.accept(task.thumbnail);
        return null;
    }

    static /* synthetic */ void lambda$updateThumbnailInBackground$1(Task task, Consumer consumer, ThumbnailData thumbnailData) {
        task.thumbnail = thumbnailData;
        consumer.accept(thumbnailData);
    }

    private CancellableTask updateThumbnailInBackground(final Task.TaskKey taskKey, final boolean z, final Consumer<ThumbnailData> consumer) {
        Preconditions.assertUIThread();
        ThumbnailData andInvalidateIfModified = this.mCache.getAndInvalidateIfModified(taskKey);
        if (andInvalidateIfModified == null || andInvalidateIfModified.thumbnail == null || (andInvalidateIfModified.reducedResolution && !z)) {
            AnonymousClass1 r0 = new CancellableTask<ThumbnailData>() {
                public ThumbnailData getResultOnBg() {
                    return ActivityManagerWrapper.getInstance().getTaskThumbnail(taskKey.id, z);
                }

                public void handleResult(ThumbnailData thumbnailData) {
                    TaskThumbnailCache.this.mCache.put(taskKey, thumbnailData);
                    consumer.accept(thumbnailData);
                }
            };
            this.mBgExecutor.execute(r0);
            return r0;
        }
        consumer.accept(andInvalidateIfModified);
        return null;
    }

    public void clear() {
        this.mCache.evictAll();
    }

    public void remove(Task.TaskKey taskKey) {
        this.mCache.remove(taskKey);
    }

    public int getCacheSize() {
        return this.mCacheSize;
    }

    public HighResLoadingState getHighResLoadingState() {
        return this.mHighResLoadingState;
    }

    public boolean isPreloadingEnabled() {
        return this.mEnableTaskSnapshotPreloading && this.mHighResLoadingState.mVisible;
    }

    /* access modifiers changed from: private */
    public static boolean supportsLowResThumbnails() {
        Resources system = Resources.getSystem();
        int identifier = system.getIdentifier("config_lowResTaskSnapshotScale", "dimen", "android");
        if (identifier == 0 || 0.0f < system.getFloat(identifier)) {
            return true;
        }
        return false;
    }
}
