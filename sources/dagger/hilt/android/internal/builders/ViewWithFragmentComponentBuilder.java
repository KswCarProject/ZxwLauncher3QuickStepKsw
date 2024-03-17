package dagger.hilt.android.internal.builders;

import android.view.View;
import dagger.BindsInstance;
import dagger.hilt.android.components.ViewWithFragmentComponent;

public interface ViewWithFragmentComponentBuilder {
    ViewWithFragmentComponent build();

    ViewWithFragmentComponentBuilder view(@BindsInstance View view);
}
