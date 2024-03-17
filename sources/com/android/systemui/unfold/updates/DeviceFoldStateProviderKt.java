package com.android.systemui.unfold.updates;

import kotlin.Metadata;

@Metadata(d1 = {"\u0000 \n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a\u0010\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\u0007H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000\"\u0016\u0010\u0002\u001a\u00020\u00038\u0006XT¢\u0006\b\n\u0000\u0012\u0004\b\u0004\u0010\u0005\"\u0016\u0010\u0006\u001a\u00020\u00078\u0006XT¢\u0006\b\n\u0000\u0012\u0004\b\b\u0010\u0005\"\u000e\u0010\t\u001a\u00020\nXT¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"DEBUG", "", "FULLY_OPEN_THRESHOLD_DEGREES", "", "getFULLY_OPEN_THRESHOLD_DEGREES$annotations", "()V", "START_CLOSING_ON_APPS_THRESHOLD_DEGREES", "", "getSTART_CLOSING_ON_APPS_THRESHOLD_DEGREES$annotations", "TAG", "", "stateToString", "update", "SharedLibWrapper_release"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* compiled from: DeviceFoldStateProvider.kt */
public final class DeviceFoldStateProviderKt {
    private static final boolean DEBUG = false;
    public static final float FULLY_OPEN_THRESHOLD_DEGREES = 15.0f;
    public static final int START_CLOSING_ON_APPS_THRESHOLD_DEGREES = 60;
    private static final String TAG = "DeviceFoldProvider";

    public static /* synthetic */ void getFULLY_OPEN_THRESHOLD_DEGREES$annotations() {
    }

    public static /* synthetic */ void getSTART_CLOSING_ON_APPS_THRESHOLD_DEGREES$annotations() {
    }

    /* access modifiers changed from: private */
    public static final String stateToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? "UNKNOWN" : "FINISH_CLOSED" : "FINISH_FULL_OPEN" : "FINISH_HALF_OPEN" : "UNFOLDED_SCREEN_AVAILABLE" : "START_CLOSING" : "START_OPENING";
    }
}
