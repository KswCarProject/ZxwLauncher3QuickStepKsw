package com.android.systemui.flags;

import kotlin.Metadata;

@Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00060\u0001j\u0002`\u0002B\u0005¢\u0006\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/android/systemui/flags/NoFlagResultsException;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "()V", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: FlagManager.kt */
public final class NoFlagResultsException extends Exception {
    public NoFlagResultsException() {
        super("SystemUI failed to communicate its flags back successfully");
    }
}
