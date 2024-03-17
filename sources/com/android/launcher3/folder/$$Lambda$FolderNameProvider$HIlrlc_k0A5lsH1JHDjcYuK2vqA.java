package com.android.launcher3.folder;

import android.content.ComponentName;
import java.util.function.Function;

/* renamed from: com.android.launcher3.folder.-$$Lambda$FolderNameProvider$HIlrlc_k0A5lsH1JHDjcYuK2vqA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FolderNameProvider$HIlrlc_k0A5lsH1JHDjcYuK2vqA implements Function {
    public static final /* synthetic */ $$Lambda$FolderNameProvider$HIlrlc_k0A5lsH1JHDjcYuK2vqA INSTANCE = new $$Lambda$FolderNameProvider$HIlrlc_k0A5lsH1JHDjcYuK2vqA();

    private /* synthetic */ $$Lambda$FolderNameProvider$HIlrlc_k0A5lsH1JHDjcYuK2vqA() {
    }

    public final Object apply(Object obj) {
        return ((ComponentName) obj).getPackageName();
    }
}
