package dagger.hilt.android.internal.builders;

import android.app.Service;
import dagger.BindsInstance;
import dagger.hilt.android.components.ServiceComponent;

public interface ServiceComponentBuilder {
    ServiceComponent build();

    ServiceComponentBuilder service(@BindsInstance Service service);
}
