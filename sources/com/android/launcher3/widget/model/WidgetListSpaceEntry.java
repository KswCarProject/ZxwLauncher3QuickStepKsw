package com.android.launcher3.widget.model;

import android.os.Process;
import com.android.launcher3.model.data.PackageItemInfo;
import java.util.Collections;

public class WidgetListSpaceEntry extends WidgetsListBaseEntry {
    public int getRank() {
        return 1;
    }

    public WidgetListSpaceEntry() {
        super(new PackageItemInfo("", Process.myUserHandle()), "", Collections.EMPTY_LIST);
        this.mPkgItem.title = "";
    }
}
