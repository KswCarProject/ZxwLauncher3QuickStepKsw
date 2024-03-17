package com.android.launcher3.folder;

import com.android.launcher3.model.data.WorkspaceItemInfo;
import java.util.function.Function;

/* renamed from: com.android.launcher3.folder.-$$Lambda$FolderNameProvider$N_zc3TXlYHSSUXg98gTwRjVR1gc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FolderNameProvider$N_zc3TXlYHSSUXg98gTwRjVR1gc implements Function {
    public static final /* synthetic */ $$Lambda$FolderNameProvider$N_zc3TXlYHSSUXg98gTwRjVR1gc INSTANCE = new $$Lambda$FolderNameProvider$N_zc3TXlYHSSUXg98gTwRjVR1gc();

    private /* synthetic */ $$Lambda$FolderNameProvider$N_zc3TXlYHSSUXg98gTwRjVR1gc() {
    }

    public final Object apply(Object obj) {
        return ((WorkspaceItemInfo) obj).user;
    }
}
