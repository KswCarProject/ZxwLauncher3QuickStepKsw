package dagger.hilt.android.internal.managers;

import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager;
import dagger.internal.Factory;

public final class ActivityRetainedComponentManager_Lifecycle_Factory implements Factory<ActivityRetainedComponentManager.Lifecycle> {
    public ActivityRetainedComponentManager.Lifecycle get() {
        return newInstance();
    }

    public static ActivityRetainedComponentManager_Lifecycle_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static ActivityRetainedComponentManager.Lifecycle newInstance() {
        return new ActivityRetainedComponentManager.Lifecycle();
    }

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final ActivityRetainedComponentManager_Lifecycle_Factory INSTANCE = new ActivityRetainedComponentManager_Lifecycle_Factory();

        private InstanceHolder() {
        }
    }
}
