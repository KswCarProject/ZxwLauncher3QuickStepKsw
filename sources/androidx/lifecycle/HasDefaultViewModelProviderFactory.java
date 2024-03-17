package androidx.lifecycle;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

public interface HasDefaultViewModelProviderFactory {
    ViewModelProvider.Factory getDefaultViewModelProviderFactory();

    CreationExtras getDefaultViewModelCreationExtras() {
        return CreationExtras.Empty.INSTANCE;
    }
}
