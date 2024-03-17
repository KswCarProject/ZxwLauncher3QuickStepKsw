package com.android.quickstep.util;

import java.io.File;
import java.io.FilenameFilter;

/* renamed from: com.android.quickstep.util.-$$Lambda$ImageActionUtils$VUkVC-FVMOkMKcJgnCajKCuGDnc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ImageActionUtils$VUkVCFVMOkMKcJgnCajKCuGDnc implements FilenameFilter {
    public static final /* synthetic */ $$Lambda$ImageActionUtils$VUkVCFVMOkMKcJgnCajKCuGDnc INSTANCE = new $$Lambda$ImageActionUtils$VUkVCFVMOkMKcJgnCajKCuGDnc();

    private /* synthetic */ $$Lambda$ImageActionUtils$VUkVCFVMOkMKcJgnCajKCuGDnc() {
    }

    public final boolean accept(File file, String str) {
        return str.startsWith(ImageActionUtils.BASE_NAME);
    }
}
