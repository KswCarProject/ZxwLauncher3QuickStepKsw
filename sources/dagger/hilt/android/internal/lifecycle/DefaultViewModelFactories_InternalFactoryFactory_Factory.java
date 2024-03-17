package dagger.hilt.android.internal.lifecycle;

import android.app.Application;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.internal.Factory;
import java.util.Set;
import javax.inject.Provider;

public final class DefaultViewModelFactories_InternalFactoryFactory_Factory implements Factory<DefaultViewModelFactories.InternalFactoryFactory> {
    private final Provider<Application> applicationProvider;
    private final Provider<Set<String>> keySetProvider;
    private final Provider<ViewModelComponentBuilder> viewModelComponentBuilderProvider;

    public DefaultViewModelFactories_InternalFactoryFactory_Factory(Provider<Application> provider, Provider<Set<String>> provider2, Provider<ViewModelComponentBuilder> provider3) {
        this.applicationProvider = provider;
        this.keySetProvider = provider2;
        this.viewModelComponentBuilderProvider = provider3;
    }

    public DefaultViewModelFactories.InternalFactoryFactory get() {
        return newInstance(this.applicationProvider.get(), this.keySetProvider.get(), this.viewModelComponentBuilderProvider.get());
    }

    public static DefaultViewModelFactories_InternalFactoryFactory_Factory create(Provider<Application> provider, Provider<Set<String>> provider2, Provider<ViewModelComponentBuilder> provider3) {
        return new DefaultViewModelFactories_InternalFactoryFactory_Factory(provider, provider2, provider3);
    }

    public static DefaultViewModelFactories.InternalFactoryFactory newInstance(Application application, Set<String> set, ViewModelComponentBuilder viewModelComponentBuilder) {
        return new DefaultViewModelFactories.InternalFactoryFactory(application, set, viewModelComponentBuilder);
    }
}
