package androidx.lifecycle;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;

public abstract class AbstractSavedStateViewModelFactory extends ViewModelProvider.OnRequeryFactory implements ViewModelProvider.Factory {
    static final String TAG_SAVED_STATE_HANDLE_CONTROLLER = "androidx.lifecycle.savedstate.vm.tag";
    private Bundle mDefaultArgs;
    private Lifecycle mLifecycle;
    private SavedStateRegistry mSavedStateRegistry;

    /* access modifiers changed from: protected */
    public abstract <T extends ViewModel> T create(String str, Class<T> cls, SavedStateHandle savedStateHandle);

    public AbstractSavedStateViewModelFactory() {
    }

    public AbstractSavedStateViewModelFactory(SavedStateRegistryOwner savedStateRegistryOwner, Bundle bundle) {
        this.mSavedStateRegistry = savedStateRegistryOwner.getSavedStateRegistry();
        this.mLifecycle = savedStateRegistryOwner.getLifecycle();
        this.mDefaultArgs = bundle;
    }

    public final <T extends ViewModel> T create(Class<T> cls, CreationExtras creationExtras) {
        String str = (String) creationExtras.get(ViewModelProvider.NewInstanceFactory.VIEW_MODEL_KEY);
        if (str == null) {
            throw new IllegalStateException("VIEW_MODEL_KEY must always be provided by ViewModelProvider");
        } else if (this.mSavedStateRegistry != null) {
            return create(str, cls);
        } else {
            return create(str, cls, SavedStateHandleSupport.createSavedStateHandle(creationExtras));
        }
    }

    private <T extends ViewModel> T create(String str, Class<T> cls) {
        SavedStateHandleController create = LegacySavedStateHandleController.create(this.mSavedStateRegistry, this.mLifecycle, str, this.mDefaultArgs);
        T create2 = create(str, cls, create.getHandle());
        create2.setTagIfAbsent(TAG_SAVED_STATE_HANDLE_CONTROLLER, create);
        return create2;
    }

    public final <T extends ViewModel> T create(Class<T> cls) {
        String canonicalName = cls.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
        } else if (this.mLifecycle != null) {
            return create(canonicalName, cls);
        } else {
            throw new UnsupportedOperationException("AbstractSavedStateViewModelFactory constructed with empty constructor supports only calls to create(modelClass: Class<T>, extras: CreationExtras).");
        }
    }

    public void onRequery(ViewModel viewModel) {
        SavedStateRegistry savedStateRegistry = this.mSavedStateRegistry;
        if (savedStateRegistry != null) {
            LegacySavedStateHandleController.attachHandleIfNeeded(viewModel, savedStateRegistry, this.mLifecycle);
        }
    }
}
