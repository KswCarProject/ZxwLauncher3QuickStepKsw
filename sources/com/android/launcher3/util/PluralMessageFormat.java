package com.android.launcher3.util;

import android.content.Context;
import android.icu.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;

public class PluralMessageFormat {
    public static final String getIcuPluralString(Context context, int i, int i2) {
        MessageFormat messageFormat = new MessageFormat(context.getResources().getString(i), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put("count", Integer.valueOf(i2));
        return messageFormat.format(hashMap);
    }
}
