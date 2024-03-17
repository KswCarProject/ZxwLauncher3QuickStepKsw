package com.android.launcher3.util;

import android.content.ComponentName;
import android.os.UserHandle;
import java.util.Arrays;

public class ComponentKey {
    public final ComponentName componentName;
    private final int mHashCode;
    public final UserHandle user;

    public ComponentKey(ComponentName componentName2, UserHandle userHandle) {
        if (componentName2 == null || userHandle == null) {
            throw null;
        }
        this.componentName = componentName2;
        this.user = userHandle;
        this.mHashCode = Arrays.hashCode(new Object[]{componentName2, userHandle});
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object obj) {
        ComponentKey componentKey = (ComponentKey) obj;
        return componentKey.componentName.equals(this.componentName) && componentKey.user.equals(this.user);
    }

    public String toString() {
        return this.componentName.flattenToString() + "#" + this.user.hashCode();
    }

    public static ComponentKey fromString(String str) {
        int i;
        ComponentName unflattenFromString;
        int indexOf = str.indexOf(35);
        if (indexOf < 0 || (i = indexOf + 1) >= str.length() || (unflattenFromString = ComponentName.unflattenFromString(str.substring(0, indexOf))) == null) {
            return null;
        }
        try {
            return new ComponentKey(unflattenFromString, UserHandle.getUserHandleForUid(Integer.parseInt(str.substring(i))));
        } catch (NumberFormatException unused) {
            return null;
        }
    }
}
