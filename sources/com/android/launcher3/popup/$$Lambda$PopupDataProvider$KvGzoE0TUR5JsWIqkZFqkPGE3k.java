package com.android.launcher3.popup;

import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.model.WidgetsListContentEntry;
import java.util.function.Function;

/* renamed from: com.android.launcher3.popup.-$$Lambda$PopupDataProvider$KvGzoE0TU-R5JsWIqkZFqkPGE3k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PopupDataProvider$KvGzoE0TUR5JsWIqkZFqkPGE3k implements Function {
    public static final /* synthetic */ $$Lambda$PopupDataProvider$KvGzoE0TUR5JsWIqkZFqkPGE3k INSTANCE = new $$Lambda$PopupDataProvider$KvGzoE0TUR5JsWIqkZFqkPGE3k();

    private /* synthetic */ $$Lambda$PopupDataProvider$KvGzoE0TUR5JsWIqkZFqkPGE3k() {
    }

    public final Object apply(Object obj) {
        return ((WidgetsListContentEntry) ((WidgetsListBaseEntry) obj)).mWidgets.stream();
    }
}
