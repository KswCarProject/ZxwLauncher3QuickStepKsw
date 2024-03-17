package com.android.systemui.unfold.util;

import android.os.Trace;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import javax.inject.Inject;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0011\b\u0001\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0016J\b\u0010\u000b\u001a\u00020\u0007H\u0016R\u000e\u0010\u0005\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lcom/android/systemui/unfold/util/ATraceLoggerTransitionProgressListener;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "tracePrefix", "", "(Ljava/lang/String;)V", "traceName", "onTransitionFinished", "", "onTransitionProgress", "progress", "", "onTransitionStarted", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ATraceLoggerTransitionProgressListener.kt */
public final class ATraceLoggerTransitionProgressListener implements UnfoldTransitionProgressProvider.TransitionProgressListener {
    private final String traceName;

    @Inject
    public ATraceLoggerTransitionProgressListener(@UnfoldTransitionATracePrefix String str) {
        Intrinsics.checkNotNullParameter(str, "tracePrefix");
        this.traceName = Intrinsics.stringPlus(str, "#FoldUnfoldTransitionInProgress");
    }

    public void onTransitionStarted() {
        Trace.beginAsyncSection(this.traceName, 0);
    }

    public void onTransitionFinished() {
        Trace.endAsyncSection(this.traceName, 0);
    }

    public void onTransitionProgress(float f) {
        Trace.setCounter(this.traceName, (long) (f * ((float) 100)));
    }
}
