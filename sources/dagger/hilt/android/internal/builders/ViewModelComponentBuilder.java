package dagger.hilt.android.internal.builders;

import androidx.lifecycle.SavedStateHandle;
import dagger.BindsInstance;
import dagger.hilt.android.components.ViewModelComponent;

public interface ViewModelComponentBuilder {
    ViewModelComponent build();

    ViewModelComponentBuilder savedStateHandle(@BindsInstance SavedStateHandle savedStateHandle);
}
