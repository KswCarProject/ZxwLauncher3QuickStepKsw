package com.android.launcher3.taskbar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/* renamed from: com.android.launcher3.taskbar.LauncherTaskbarUIController-$$ExternalSynthetic1  reason: invalid class name */
public /* synthetic */ class LauncherTaskbarUIController$$ExternalSynthetic1 {
    public static /* synthetic */ Set m0(Object[] objArr) {
        HashSet hashSet = new HashSet(objArr.length);
        int length = objArr.length;
        int i = 0;
        while (i < length) {
            Object obj = objArr[i];
            if (hashSet.add(Objects.requireNonNull(obj))) {
                i++;
            } else {
                throw new IllegalArgumentException("duplicate element: " + obj);
            }
        }
        return Collections.unmodifiableSet(hashSet);
    }
}
