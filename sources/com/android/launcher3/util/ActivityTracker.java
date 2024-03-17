package com.android.launcher3.util;

import com.android.launcher3.BaseActivity;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ActivityTracker<T extends BaseActivity> {
    private CopyOnWriteArrayList<SchedulerCallback<T>> mCallbacks = new CopyOnWriteArrayList<>();
    private WeakReference<T> mCurrentActivity = new WeakReference<>((Object) null);

    public interface SchedulerCallback<T extends BaseActivity> {
        boolean init(T t, boolean z);
    }

    public <R extends T> R getCreatedActivity() {
        return (BaseActivity) this.mCurrentActivity.get();
    }

    public void onActivityDestroyed(T t) {
        if (this.mCurrentActivity.get() == t) {
            this.mCurrentActivity.clear();
        }
    }

    public void registerCallback(SchedulerCallback<T> schedulerCallback) {
        BaseActivity baseActivity = (BaseActivity) this.mCurrentActivity.get();
        this.mCallbacks.add(schedulerCallback);
        if (baseActivity != null && !schedulerCallback.init(baseActivity, baseActivity.isStarted())) {
            unregisterCallback(schedulerCallback);
        }
    }

    public void unregisterCallback(SchedulerCallback<T> schedulerCallback) {
        this.mCallbacks.remove(schedulerCallback);
    }

    public boolean handleCreate(T t) {
        this.mCurrentActivity = new WeakReference<>(t);
        return handleIntent(t, false);
    }

    public boolean handleNewIntent(T t) {
        return handleIntent(t, t.isStarted());
    }

    private boolean handleIntent(T t, boolean z) {
        Iterator<SchedulerCallback<T>> it = this.mCallbacks.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            SchedulerCallback next = it.next();
            if (!next.init(t, z)) {
                unregisterCallback(next);
            }
            z2 = true;
        }
        return z2;
    }
}
