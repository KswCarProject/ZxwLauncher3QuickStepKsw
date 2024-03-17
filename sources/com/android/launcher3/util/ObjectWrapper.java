package com.android.launcher3.util;

import android.os.Binder;
import android.os.IBinder;

public class ObjectWrapper<T> extends Binder {
    private T mObject;

    public ObjectWrapper(T t) {
        this.mObject = t;
    }

    public T get() {
        return this.mObject;
    }

    public void clear() {
        this.mObject = null;
    }

    public static IBinder wrap(Object obj) {
        return new ObjectWrapper(obj);
    }

    public static <T> T unwrap(IBinder iBinder) {
        if (iBinder instanceof ObjectWrapper) {
            return ((ObjectWrapper) iBinder).get();
        }
        return null;
    }
}
