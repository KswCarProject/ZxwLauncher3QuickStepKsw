package com.android.systemui.flags;

import kotlin.Metadata;

@Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/android/systemui/flags/StringFlagSerializer;", "Lcom/android/systemui/flags/FlagSerializer;", "", "()V", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: FlagSerializer.kt */
public final class StringFlagSerializer extends FlagSerializer<String> {
    public static final StringFlagSerializer INSTANCE = new StringFlagSerializer();

    private StringFlagSerializer() {
        super("string", AnonymousClass1.INSTANCE, AnonymousClass2.INSTANCE);
    }
}
