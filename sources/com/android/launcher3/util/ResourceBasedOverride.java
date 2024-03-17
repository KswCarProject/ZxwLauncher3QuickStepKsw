package com.android.launcher3.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;

public interface ResourceBasedOverride {

    public static class Overrides {
        private static final String TAG = "Overrides";

        public static <T extends ResourceBasedOverride> T getObject(Class<T> cls, Context context, int i) {
            String string = context.getString(i);
            if (!TextUtils.isEmpty(string)) {
                try {
                    return (ResourceBasedOverride) Class.forName(string).getDeclaredConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
                } catch (ClassCastException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    Log.e(TAG, "Bad overriden class", e);
                }
            }
            try {
                return (ResourceBasedOverride) cls.newInstance();
            } catch (IllegalAccessException | InstantiationException e2) {
                throw new RuntimeException(e2);
            }
        }
    }
}
