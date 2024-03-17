package com.android.launcher3.allapps;

import com.android.launcher3.allapps.BaseAllAppsAdapter;
import java.util.function.Predicate;

/* renamed from: com.android.launcher3.allapps.-$$Lambda$pXIiW0nt8H_wcXa7zeiDgGgH-pM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$pXIiW0nt8H_wcXa7zeiDgGgHpM implements Predicate {
    public static final /* synthetic */ $$Lambda$pXIiW0nt8H_wcXa7zeiDgGgHpM INSTANCE = new $$Lambda$pXIiW0nt8H_wcXa7zeiDgGgHpM();

    private /* synthetic */ $$Lambda$pXIiW0nt8H_wcXa7zeiDgGgHpM() {
    }

    public final boolean test(Object obj) {
        return ((BaseAllAppsAdapter.AdapterItem) obj).isCountedForAccessibility();
    }
}
