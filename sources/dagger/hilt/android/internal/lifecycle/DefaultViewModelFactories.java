package dagger.hilt.android.internal.lifecycle;

import android.app.Application;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.savedstate.SavedStateRegistryOwner;
import dagger.Module;
import dagger.hilt.EntryPoints;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.multibindings.Multibinds;
import java.util.Set;
import javax.inject.Inject;

public final class DefaultViewModelFactories {

    public interface ActivityEntryPoint {
        InternalFactoryFactory getHiltInternalFactoryFactory();
    }

    @Module
    interface ActivityModule {
        @Multibinds
        Set<String> viewModelKeys();
    }

    public interface FragmentEntryPoint {
        InternalFactoryFactory getHiltInternalFactoryFactory();
    }

    public static ViewModelProvider.Factory getActivityFactory(ComponentActivity componentActivity, ViewModelProvider.Factory factory) {
        return ((ActivityEntryPoint) EntryPoints.get(componentActivity, ActivityEntryPoint.class)).getHiltInternalFactoryFactory().fromActivity(componentActivity, factory);
    }

    public static ViewModelProvider.Factory getFragmentFactory(Fragment fragment, ViewModelProvider.Factory factory) {
        return ((FragmentEntryPoint) EntryPoints.get(fragment, FragmentEntryPoint.class)).getHiltInternalFactoryFactory().fromFragment(fragment, factory);
    }

    public static final class InternalFactoryFactory {
        private final Application application;
        private final Set<String> keySet;
        private final ViewModelComponentBuilder viewModelComponentBuilder;

        @Inject
        InternalFactoryFactory(Application application2, Set<String> set, ViewModelComponentBuilder viewModelComponentBuilder2) {
            this.application = application2;
            this.keySet = set;
            this.viewModelComponentBuilder = viewModelComponentBuilder2;
        }

        /* access modifiers changed from: package-private */
        public ViewModelProvider.Factory fromActivity(ComponentActivity componentActivity, ViewModelProvider.Factory factory) {
            return getHiltViewModelFactory(componentActivity, componentActivity.getIntent() != null ? componentActivity.getIntent().getExtras() : null, factory);
        }

        /* access modifiers changed from: package-private */
        public ViewModelProvider.Factory fromFragment(Fragment fragment, ViewModelProvider.Factory factory) {
            return getHiltViewModelFactory(fragment, fragment.getArguments(), factory);
        }

        private ViewModelProvider.Factory getHiltViewModelFactory(SavedStateRegistryOwner savedStateRegistryOwner, Bundle bundle, ViewModelProvider.Factory factory) {
            if (factory == null) {
                factory = new SavedStateViewModelFactory(this.application, savedStateRegistryOwner, bundle);
            }
            return new HiltViewModelFactory(savedStateRegistryOwner, bundle, this.keySet, factory, this.viewModelComponentBuilder);
        }
    }

    private DefaultViewModelFactories() {
    }
}
