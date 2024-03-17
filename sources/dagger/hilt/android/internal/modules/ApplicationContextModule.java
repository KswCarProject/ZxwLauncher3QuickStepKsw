package dagger.hilt.android.internal.modules;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.internal.Contexts;

@Module
public final class ApplicationContextModule {
    private final Context applicationContext;

    public ApplicationContextModule(Context context) {
        this.applicationContext = context;
    }

    /* access modifiers changed from: package-private */
    @Provides
    public Context provideContext() {
        return this.applicationContext;
    }

    /* access modifiers changed from: package-private */
    @Provides
    public Application provideApplication() {
        return Contexts.getApplication(this.applicationContext);
    }
}
