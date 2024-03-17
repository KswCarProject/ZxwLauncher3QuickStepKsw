package com.android.launcher3.model.data;

import android.content.Intent;
import java.util.function.Function;

/* renamed from: com.android.launcher3.model.data.-$$Lambda$ItemInfo$oQmwRQ06JULBwJpOr3rLrhHcXKM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ItemInfo$oQmwRQ06JULBwJpOr3rLrhHcXKM implements Function {
    public static final /* synthetic */ $$Lambda$ItemInfo$oQmwRQ06JULBwJpOr3rLrhHcXKM INSTANCE = new $$Lambda$ItemInfo$oQmwRQ06JULBwJpOr3rLrhHcXKM();

    private /* synthetic */ $$Lambda$ItemInfo$oQmwRQ06JULBwJpOr3rLrhHcXKM() {
    }

    public final Object apply(Object obj) {
        return ((Intent) obj).getStringExtra("shortcut_id");
    }
}
