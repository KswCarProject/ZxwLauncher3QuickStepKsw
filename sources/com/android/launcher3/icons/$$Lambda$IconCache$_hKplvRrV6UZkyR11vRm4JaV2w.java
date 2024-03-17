package com.android.launcher3.icons;

import androidx.core.util.Pair;
import com.android.launcher3.model.data.IconRequestInfo;
import java.util.function.Function;

/* renamed from: com.android.launcher3.icons.-$$Lambda$IconCache$_hKplvRrV6UZkyR11-vRm4JaV2w  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$IconCache$_hKplvRrV6UZkyR11vRm4JaV2w implements Function {
    public static final /* synthetic */ $$Lambda$IconCache$_hKplvRrV6UZkyR11vRm4JaV2w INSTANCE = new $$Lambda$IconCache$_hKplvRrV6UZkyR11vRm4JaV2w();

    private /* synthetic */ $$Lambda$IconCache$_hKplvRrV6UZkyR11vRm4JaV2w() {
    }

    public final Object apply(Object obj) {
        return Pair.create(((IconRequestInfo) obj).itemInfo.user, Boolean.valueOf(((IconRequestInfo) obj).useLowResIcon));
    }
}
