package com.android.launcher3;

import android.content.ComponentName;
import com.android.launcher3.model.data.ItemInfo;
import java.util.Optional;

public class PendingAddItemInfo extends ItemInfo {
    public ComponentName componentName;

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return super.dumpProperties() + " componentName=" + this.componentName;
    }

    public ItemInfo makeShallowCopy() {
        PendingAddItemInfo pendingAddItemInfo = new PendingAddItemInfo();
        pendingAddItemInfo.copyFrom(this);
        pendingAddItemInfo.componentName = this.componentName;
        return pendingAddItemInfo;
    }

    public ComponentName getTargetComponent() {
        return (ComponentName) Optional.ofNullable(super.getTargetComponent()).orElse(this.componentName);
    }
}
