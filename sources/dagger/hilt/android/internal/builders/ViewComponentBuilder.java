package dagger.hilt.android.internal.builders;

import android.view.View;
import dagger.BindsInstance;
import dagger.hilt.android.components.ViewComponent;

public interface ViewComponentBuilder {
    ViewComponent build();

    ViewComponentBuilder view(@BindsInstance View view);
}
