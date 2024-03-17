package dagger.hilt.android.internal.managers;

import android.content.Context;
import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.EntryPoints;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.internal.ThreadUtil;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.internal.GeneratedComponentManager;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

final class ActivityRetainedComponentManager implements GeneratedComponentManager<ActivityRetainedComponent> {
    private volatile ActivityRetainedComponent component;
    private final Object componentLock = new Object();
    private final ViewModelProvider viewModelProvider;

    public interface ActivityRetainedComponentBuilderEntryPoint {
        ActivityRetainedComponentBuilder retainedComponentBuilder();
    }

    public interface ActivityRetainedLifecycleEntryPoint {
        ActivityRetainedLifecycle getActivityRetainedLifecycle();
    }

    static final class ActivityRetainedComponentViewModel extends ViewModel {
        private final ActivityRetainedComponent component;

        ActivityRetainedComponentViewModel(ActivityRetainedComponent activityRetainedComponent) {
            this.component = activityRetainedComponent;
        }

        /* access modifiers changed from: package-private */
        public ActivityRetainedComponent getComponent() {
            return this.component;
        }

        /* access modifiers changed from: protected */
        public void onCleared() {
            super.onCleared();
            ((Lifecycle) ((ActivityRetainedLifecycleEntryPoint) EntryPoints.get(this.component, ActivityRetainedLifecycleEntryPoint.class)).getActivityRetainedLifecycle()).dispatchOnCleared();
        }
    }

    ActivityRetainedComponentManager(ComponentActivity componentActivity) {
        this.viewModelProvider = getViewModelProvider(componentActivity, componentActivity);
    }

    private ViewModelProvider getViewModelProvider(ViewModelStoreOwner viewModelStoreOwner, final Context context) {
        return new ViewModelProvider(viewModelStoreOwner, (ViewModelProvider.Factory) new ViewModelProvider.Factory() {
            public <T extends ViewModel> T create(Class<T> cls) {
                return new ActivityRetainedComponentViewModel(((ActivityRetainedComponentBuilderEntryPoint) EntryPointAccessors.fromApplication(context, ActivityRetainedComponentBuilderEntryPoint.class)).retainedComponentBuilder().build());
            }
        });
    }

    public ActivityRetainedComponent generatedComponent() {
        if (this.component == null) {
            synchronized (this.componentLock) {
                if (this.component == null) {
                    this.component = createComponent();
                }
            }
        }
        return this.component;
    }

    private ActivityRetainedComponent createComponent() {
        return ((ActivityRetainedComponentViewModel) this.viewModelProvider.get(ActivityRetainedComponentViewModel.class)).getComponent();
    }

    static final class Lifecycle implements ActivityRetainedLifecycle {
        private final Set<ActivityRetainedLifecycle.OnClearedListener> listeners = new HashSet();
        private boolean onClearedDispatched = false;

        @Inject
        Lifecycle() {
        }

        public void addOnClearedListener(ActivityRetainedLifecycle.OnClearedListener onClearedListener) {
            ThreadUtil.ensureMainThread();
            throwIfOnClearedDispatched();
            this.listeners.add(onClearedListener);
        }

        public void removeOnClearedListener(ActivityRetainedLifecycle.OnClearedListener onClearedListener) {
            ThreadUtil.ensureMainThread();
            throwIfOnClearedDispatched();
            this.listeners.remove(onClearedListener);
        }

        /* access modifiers changed from: package-private */
        public void dispatchOnCleared() {
            ThreadUtil.ensureMainThread();
            this.onClearedDispatched = true;
            for (ActivityRetainedLifecycle.OnClearedListener onCleared : this.listeners) {
                onCleared.onCleared();
            }
        }

        private void throwIfOnClearedDispatched() {
            if (this.onClearedDispatched) {
                throw new IllegalStateException("There was a race between the call to add/remove an OnClearedListener and onCleared(). This can happen when posting to the Main thread from a background thread, which is not supported.");
            }
        }
    }

    @Module
    static abstract class LifecycleModule {
        /* access modifiers changed from: package-private */
        @Binds
        public abstract ActivityRetainedLifecycle bind(Lifecycle lifecycle);

        LifecycleModule() {
        }
    }
}
