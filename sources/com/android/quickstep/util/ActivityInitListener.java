package com.android.quickstep.util;

import com.android.launcher3.BaseActivity;
import com.android.launcher3.util.ActivityTracker;
import java.util.function.BiPredicate;

public class ActivityInitListener<T extends BaseActivity> implements ActivityTracker.SchedulerCallback<T> {
    private final ActivityTracker<T> mActivityTracker;
    private boolean mIsRegistered = false;
    private BiPredicate<T, Boolean> mOnInitListener;

    public ActivityInitListener(BiPredicate<T, Boolean> biPredicate, ActivityTracker<T> activityTracker) {
        this.mOnInitListener = biPredicate;
        this.mActivityTracker = activityTracker;
    }

    public final boolean init(T t, boolean z) {
        if (!this.mIsRegistered) {
            return false;
        }
        return handleInit(t, z);
    }

    /* access modifiers changed from: protected */
    public boolean handleInit(T t, boolean z) {
        return this.mOnInitListener.test(t, Boolean.valueOf(z));
    }

    public void register() {
        this.mIsRegistered = true;
        this.mActivityTracker.registerCallback(this);
    }

    public void unregister() {
        this.mActivityTracker.unregisterCallback(this);
        this.mIsRegistered = false;
        this.mOnInitListener = null;
    }
}
