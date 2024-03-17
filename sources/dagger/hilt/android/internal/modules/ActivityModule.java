package dagger.hilt.android.internal.modules;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
abstract class ActivityModule {
    /* access modifiers changed from: package-private */
    @Binds
    public abstract Context provideContext(Activity activity);

    @Reusable
    @Provides
    static FragmentActivity provideFragmentActivity(Activity activity) {
        try {
            return (FragmentActivity) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Expected activity to be a FragmentActivity: " + activity, e);
        }
    }

    private ActivityModule() {
    }
}
