package com.android.launcher3.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/* renamed from: com.android.launcher3.util.OnboardingPrefs-$$ExternalSynthetic1  reason: invalid class name */
public /* synthetic */ class OnboardingPrefs$$ExternalSynthetic1 {
    public static /* synthetic */ Map m0(Map.Entry[] entryArr) {
        HashMap hashMap = new HashMap(entryArr.length);
        int length = entryArr.length;
        int i = 0;
        while (i < length) {
            Map.Entry entry = entryArr[i];
            Object requireNonNull = Objects.requireNonNull(entry.getKey());
            if (hashMap.put(requireNonNull, Objects.requireNonNull(entry.getValue())) == null) {
                i++;
            } else {
                throw new IllegalArgumentException("duplicate key: " + requireNonNull);
            }
        }
        return Collections.unmodifiableMap(hashMap);
    }
}
