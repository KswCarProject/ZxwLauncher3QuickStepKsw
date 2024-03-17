package com.android.launcher3.util;

import android.os.Trace;
import java.util.function.Supplier;

public class TraceHelper {
    public static final int FLAG_ALLOW_BINDER_TRACKING = 1;
    public static final int FLAG_CHECK_FOR_RACE_CONDITIONS = 4;
    public static final int FLAG_IGNORE_BINDERS = 2;
    public static final int FLAG_UI_EVENT = 5;
    public static TraceHelper INSTANCE = new TraceHelper();

    public Object beginFlagsOverride(int i) {
        return null;
    }

    public void endFlagsOverride(Object obj) {
    }

    public Object beginSection(String str) {
        return beginSection(str, 0);
    }

    public Object beginSection(String str, int i) {
        Trace.beginSection(str);
        return null;
    }

    public void endSection(Object obj) {
        Trace.endSection();
    }

    public static <T> T allowIpcs(String str, Supplier<T> supplier) {
        Object beginSection = INSTANCE.beginSection(str, 2);
        try {
            return supplier.get();
        } finally {
            INSTANCE.endSection(beginSection);
        }
    }
}
