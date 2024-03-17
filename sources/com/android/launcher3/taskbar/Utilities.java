package com.android.launcher3.taskbar;

import java.util.StringJoiner;

public final class Utilities {
    private Utilities() {
    }

    static void appendFlag(StringJoiner stringJoiner, int i, int i2, String str) {
        if ((i & i2) != 0) {
            stringJoiner.add(str);
        }
    }
}
