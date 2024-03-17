package dagger.hilt.android.internal.managers;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.fragment.app.Fragment;
import dagger.hilt.EntryPoints;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.managers.ViewComponentManager;
import dagger.hilt.internal.GeneratedComponentManager;
import dagger.hilt.internal.Preconditions;

public class FragmentComponentManager implements GeneratedComponentManager<Object> {
    private volatile Object component;
    private final Object componentLock = new Object();
    private final Fragment fragment;

    public interface FragmentComponentBuilderEntryPoint {
        FragmentComponentBuilder fragmentComponentBuilder();
    }

    /* access modifiers changed from: protected */
    public void validate(Fragment fragment2) {
    }

    public FragmentComponentManager(Fragment fragment2) {
        this.fragment = fragment2;
    }

    public Object generatedComponent() {
        if (this.component == null) {
            synchronized (this.componentLock) {
                if (this.component == null) {
                    this.component = createComponent();
                }
            }
        }
        return this.component;
    }

    private Object createComponent() {
        Preconditions.checkNotNull(this.fragment.getHost(), "Hilt Fragments must be attached before creating the component.");
        Preconditions.checkState(this.fragment.getHost() instanceof GeneratedComponentManager, "Hilt Fragments must be attached to an @AndroidEntryPoint Activity. Found: %s", this.fragment.getHost().getClass());
        validate(this.fragment);
        return ((FragmentComponentBuilderEntryPoint) EntryPoints.get(this.fragment.getHost(), FragmentComponentBuilderEntryPoint.class)).fragmentComponentBuilder().fragment(this.fragment).build();
    }

    public static final void initializeArguments(Fragment fragment2) {
        Preconditions.checkNotNull(fragment2);
        if (fragment2.getArguments() == null) {
            fragment2.setArguments(new Bundle());
        }
    }

    public static final Context findActivity(Context context) {
        while ((context instanceof ContextWrapper) && !(context instanceof Activity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return context;
    }

    public static ContextWrapper createContextWrapper(Context context, Fragment fragment2) {
        return new ViewComponentManager.FragmentContextWrapper(context, fragment2);
    }

    public static ContextWrapper createContextWrapper(LayoutInflater layoutInflater, Fragment fragment2) {
        return new ViewComponentManager.FragmentContextWrapper(layoutInflater, fragment2);
    }
}
