package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AppFilter {
    private final Set<ComponentName> mFilteredComponents;

    public AppFilter(Context context) {
        this.mFilteredComponents = (Set) Arrays.stream(context.getResources().getStringArray(R.array.filtered_components)).map($$Lambda$AppFilter$mVB2YB61TxO4cQ_5vWZ3Rq7CXhk.INSTANCE).collect(Collectors.toSet());
    }

    public boolean shouldShowApp(ComponentName componentName) {
        return !this.mFilteredComponents.contains(componentName);
    }
}
