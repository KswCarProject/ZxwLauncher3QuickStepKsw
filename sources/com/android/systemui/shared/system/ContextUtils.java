package com.android.systemui.shared.system;

import android.content.Context;

public class ContextUtils {
    public static int getUserId(Context context) {
        return context.getUserId();
    }
}
