package dagger.hilt.android.internal.modules;

import android.app.Activity;
import androidx.fragment.app.FragmentActivity;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class ActivityModule_ProvideFragmentActivityFactory implements Factory<FragmentActivity> {
    private final Provider<Activity> activityProvider;

    public ActivityModule_ProvideFragmentActivityFactory(Provider<Activity> provider) {
        this.activityProvider = provider;
    }

    public FragmentActivity get() {
        return provideFragmentActivity(this.activityProvider.get());
    }

    public static ActivityModule_ProvideFragmentActivityFactory create(Provider<Activity> provider) {
        return new ActivityModule_ProvideFragmentActivityFactory(provider);
    }

    public static FragmentActivity provideFragmentActivity(Activity activity) {
        return (FragmentActivity) Preconditions.checkNotNullFromProvides(ActivityModule.provideFragmentActivity(activity));
    }
}
