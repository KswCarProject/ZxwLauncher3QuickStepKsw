package dagger.hilt.android.internal.managers;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import dagger.hilt.EntryPoints;
import dagger.hilt.android.internal.Contexts;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.internal.GeneratedComponentManager;
import dagger.hilt.internal.Preconditions;

public final class ViewComponentManager implements GeneratedComponentManager<Object> {
    private volatile Object component;
    private final Object componentLock = new Object();
    private final boolean hasFragmentBindings;
    private final View view;

    public interface ViewComponentBuilderEntryPoint {
        ViewComponentBuilder viewComponentBuilder();
    }

    public interface ViewWithFragmentComponentBuilderEntryPoint {
        ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder();
    }

    public ViewComponentManager(View view2, boolean z) {
        this.view = view2;
        this.hasFragmentBindings = z;
    }

    public Object generatedComponent() {
        if (this.component == null) {
            synchronized (this.componentLock) {
                if (this.component == null) {
                    this.component = createComponent();
                }
            }
        }
        return this.component;
    }

    private Object createComponent() {
        GeneratedComponentManager<?> parentComponentManager = getParentComponentManager(false);
        if (this.hasFragmentBindings) {
            return ((ViewWithFragmentComponentBuilderEntryPoint) EntryPoints.get(parentComponentManager, ViewWithFragmentComponentBuilderEntryPoint.class)).viewWithFragmentComponentBuilder().view(this.view).build();
        }
        return ((ViewComponentBuilderEntryPoint) EntryPoints.get(parentComponentManager, ViewComponentBuilderEntryPoint.class)).viewComponentBuilder().view(this.view).build();
    }

    public GeneratedComponentManager<?> maybeGetParentComponentManager() {
        return getParentComponentManager(true);
    }

    private GeneratedComponentManager<?> getParentComponentManager(boolean z) {
        if (this.hasFragmentBindings) {
            Context parentContext = getParentContext(FragmentContextWrapper.class, z);
            if (parentContext instanceof FragmentContextWrapper) {
                return (GeneratedComponentManager) ((FragmentContextWrapper) parentContext).getFragment();
            }
            if (z) {
                return null;
            }
            Context parentContext2 = getParentContext(GeneratedComponentManager.class, z);
            Preconditions.checkState(!(parentContext2 instanceof GeneratedComponentManager), "%s, @WithFragmentBindings Hilt view must be attached to an @AndroidEntryPoint Fragment. Was attached to context %s", this.view.getClass(), parentContext2.getClass().getName());
        } else {
            Context parentContext3 = getParentContext(GeneratedComponentManager.class, z);
            if (parentContext3 instanceof GeneratedComponentManager) {
                return (GeneratedComponentManager) parentContext3;
            }
            if (z) {
                return null;
            }
        }
        throw new IllegalStateException(String.format("%s, Hilt view must be attached to an @AndroidEntryPoint Fragment or Activity.", new Object[]{this.view.getClass()}));
    }

    private Context getParentContext(Class<?> cls, boolean z) {
        Context unwrap = unwrap(this.view.getContext(), cls);
        if (unwrap != Contexts.getApplication(unwrap.getApplicationContext())) {
            return unwrap;
        }
        Preconditions.checkState(z, "%s, Hilt view cannot be created using the application context. Use a Hilt Fragment or Activity context.", this.view.getClass());
        return null;
    }

    private static Context unwrap(Context context, Class<?> cls) {
        while ((context instanceof ContextWrapper) && !cls.isInstance(context)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return context;
    }

    public static final class FragmentContextWrapper extends ContextWrapper {
        /* access modifiers changed from: private */
        public LayoutInflater baseInflater;
        /* access modifiers changed from: private */
        public Fragment fragment;
        private final LifecycleEventObserver fragmentLifecycleObserver;
        /* access modifiers changed from: private */
        public LayoutInflater inflater;

        FragmentContextWrapper(Context context, Fragment fragment2) {
            super((Context) Preconditions.checkNotNull(context));
            AnonymousClass1 r2 = new LifecycleEventObserver() {
                public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        Fragment unused = FragmentContextWrapper.this.fragment = null;
                        LayoutInflater unused2 = FragmentContextWrapper.this.baseInflater = null;
                        LayoutInflater unused3 = FragmentContextWrapper.this.inflater = null;
                    }
                }
            };
            this.fragmentLifecycleObserver = r2;
            this.baseInflater = null;
            Fragment fragment3 = (Fragment) Preconditions.checkNotNull(fragment2);
            this.fragment = fragment3;
            fragment3.getLifecycle().addObserver(r2);
        }

        FragmentContextWrapper(LayoutInflater layoutInflater, Fragment fragment2) {
            super((Context) Preconditions.checkNotNull(((LayoutInflater) Preconditions.checkNotNull(layoutInflater)).getContext()));
            AnonymousClass1 r0 = new LifecycleEventObserver() {
                public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        Fragment unused = FragmentContextWrapper.this.fragment = null;
                        LayoutInflater unused2 = FragmentContextWrapper.this.baseInflater = null;
                        LayoutInflater unused3 = FragmentContextWrapper.this.inflater = null;
                    }
                }
            };
            this.fragmentLifecycleObserver = r0;
            this.baseInflater = layoutInflater;
            Fragment fragment3 = (Fragment) Preconditions.checkNotNull(fragment2);
            this.fragment = fragment3;
            fragment3.getLifecycle().addObserver(r0);
        }

        /* access modifiers changed from: package-private */
        public Fragment getFragment() {
            Preconditions.checkNotNull(this.fragment, "The fragment has already been destroyed.");
            return this.fragment;
        }

        public Object getSystemService(String str) {
            if (!"layout_inflater".equals(str)) {
                return getBaseContext().getSystemService(str);
            }
            if (this.inflater == null) {
                if (this.baseInflater == null) {
                    this.baseInflater = (LayoutInflater) getBaseContext().getSystemService("layout_inflater");
                }
                this.inflater = this.baseInflater.cloneInContext(this);
            }
            return this.inflater;
        }
    }
}
