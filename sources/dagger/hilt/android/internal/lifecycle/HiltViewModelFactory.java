package dagger.hilt.android.internal.lifecycle;

import android.app.Activity;
import android.os.Bundle;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.savedstate.SavedStateRegistryOwner;
import dagger.Module;
import dagger.hilt.EntryPoints;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.multibindings.Multibinds;
import java.util.Map;
import java.util.Set;
import javax.inject.Provider;

public final class HiltViewModelFactory implements ViewModelProvider.Factory {
    private final ViewModelProvider.Factory delegateFactory;
    private final AbstractSavedStateViewModelFactory hiltViewModelFactory;
    private final Set<String> hiltViewModelKeys;

    interface ActivityCreatorEntryPoint {
        ViewModelComponentBuilder getViewModelComponentBuilder();

        Set<String> getViewModelKeys();
    }

    public interface ViewModelFactoriesEntryPoint {
        Map<String, Provider<ViewModel>> getHiltViewModelMap();
    }

    @Module
    interface ViewModelModule {
        @Multibinds
        Map<String, ViewModel> hiltViewModelMap();
    }

    public HiltViewModelFactory(SavedStateRegistryOwner savedStateRegistryOwner, Bundle bundle, Set<String> set, ViewModelProvider.Factory factory, final ViewModelComponentBuilder viewModelComponentBuilder) {
        this.hiltViewModelKeys = set;
        this.delegateFactory = factory;
        this.hiltViewModelFactory = new AbstractSavedStateViewModelFactory(savedStateRegistryOwner, bundle) {
            /* access modifiers changed from: protected */
            public <T extends ViewModel> T create(String str, Class<T> cls, SavedStateHandle savedStateHandle) {
                Provider provider = ((ViewModelFactoriesEntryPoint) EntryPoints.get(viewModelComponentBuilder.savedStateHandle(savedStateHandle).build(), ViewModelFactoriesEntryPoint.class)).getHiltViewModelMap().get(cls.getName());
                if (provider != null) {
                    return (ViewModel) provider.get();
                }
                throw new IllegalStateException("Expected the @HiltViewModel-annotated class '" + cls.getName() + "' to be available in the multi-binding of @HiltViewModelMap but none was found.");
            }
        };
    }

    public <T extends ViewModel> T create(Class<T> cls) {
        if (this.hiltViewModelKeys.contains(cls.getName())) {
            return this.hiltViewModelFactory.create(cls);
        }
        return this.delegateFactory.create(cls);
    }

    public static ViewModelProvider.Factory createInternal(Activity activity, SavedStateRegistryOwner savedStateRegistryOwner, Bundle bundle, ViewModelProvider.Factory factory) {
        ActivityCreatorEntryPoint activityCreatorEntryPoint = (ActivityCreatorEntryPoint) EntryPoints.get(activity, ActivityCreatorEntryPoint.class);
        return new HiltViewModelFactory(savedStateRegistryOwner, bundle, activityCreatorEntryPoint.getViewModelKeys(), factory, activityCreatorEntryPoint.getViewModelComponentBuilder());
    }
}
