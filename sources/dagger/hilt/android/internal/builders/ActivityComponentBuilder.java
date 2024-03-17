package dagger.hilt.android.internal.builders;

import android.app.Activity;
import dagger.BindsInstance;
import dagger.hilt.android.components.ActivityComponent;

public interface ActivityComponentBuilder {
    ActivityComponentBuilder activity(@BindsInstance Activity activity);

    ActivityComponent build();
}
