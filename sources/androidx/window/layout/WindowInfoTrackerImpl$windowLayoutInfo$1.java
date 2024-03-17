package androidx.window.layout;

import android.app.Activity;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.channels.Channel;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/flow/FlowCollector;", "Landroidx/window/layout/WindowLayoutInfo;"}, k = 3, mv = {1, 6, 0}, xi = 48)
@DebugMetadata(c = "androidx.window.layout.WindowInfoTrackerImpl$windowLayoutInfo$1", f = "WindowInfoTrackerImpl.kt", i = {0, 0, 1, 1}, l = {54, 55}, m = "invokeSuspend", n = {"$this$flow", "listener", "$this$flow", "listener"}, s = {"L$0", "L$1", "L$0", "L$1"})
/* compiled from: WindowInfoTrackerImpl.kt */
final class WindowInfoTrackerImpl$windowLayoutInfo$1 extends SuspendLambda implements Function2<FlowCollector<? super WindowLayoutInfo>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Activity $activity;
    private /* synthetic */ Object L$0;
    Object L$1;
    Object L$2;
    int label;
    final /* synthetic */ WindowInfoTrackerImpl this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    WindowInfoTrackerImpl$windowLayoutInfo$1(WindowInfoTrackerImpl windowInfoTrackerImpl, Activity activity, Continuation<? super WindowInfoTrackerImpl$windowLayoutInfo$1> continuation) {
        super(2, continuation);
        this.this$0 = windowInfoTrackerImpl;
        this.$activity = activity;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        WindowInfoTrackerImpl$windowLayoutInfo$1 windowInfoTrackerImpl$windowLayoutInfo$1 = new WindowInfoTrackerImpl$windowLayoutInfo$1(this.this$0, this.$activity, continuation);
        windowInfoTrackerImpl$windowLayoutInfo$1.L$0 = obj;
        return windowInfoTrackerImpl$windowLayoutInfo$1;
    }

    public final Object invoke(FlowCollector<? super WindowLayoutInfo> flowCollector, Continuation<? super Unit> continuation) {
        return ((WindowInfoTrackerImpl$windowLayoutInfo$1) create(flowCollector, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0073 A[Catch:{ all -> 0x00a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x007e A[Catch:{ all -> 0x00a4 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object invokeSuspend(java.lang.Object r10) {
        /*
            r9 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r1 = r9.label
            r2 = 2
            r3 = 1
            if (r1 == 0) goto L_0x0039
            if (r1 == r3) goto L_0x0027
            if (r1 != r2) goto L_0x001f
            java.lang.Object r1 = r9.L$2
            kotlinx.coroutines.channels.ChannelIterator r1 = (kotlinx.coroutines.channels.ChannelIterator) r1
            java.lang.Object r4 = r9.L$1
            androidx.core.util.Consumer r4 = (androidx.core.util.Consumer) r4
            java.lang.Object r5 = r9.L$0
            kotlinx.coroutines.flow.FlowCollector r5 = (kotlinx.coroutines.flow.FlowCollector) r5
            kotlin.ResultKt.throwOnFailure(r10)     // Catch:{ all -> 0x00a6 }
            r10 = r5
            goto L_0x0060
        L_0x001f:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
            r10.<init>(r0)
            throw r10
        L_0x0027:
            java.lang.Object r1 = r9.L$2
            kotlinx.coroutines.channels.ChannelIterator r1 = (kotlinx.coroutines.channels.ChannelIterator) r1
            java.lang.Object r4 = r9.L$1
            androidx.core.util.Consumer r4 = (androidx.core.util.Consumer) r4
            java.lang.Object r5 = r9.L$0
            kotlinx.coroutines.flow.FlowCollector r5 = (kotlinx.coroutines.flow.FlowCollector) r5
            kotlin.ResultKt.throwOnFailure(r10)     // Catch:{ all -> 0x00a6 }
            r6 = r5
            r5 = r9
            goto L_0x0076
        L_0x0039:
            kotlin.ResultKt.throwOnFailure(r10)
            java.lang.Object r10 = r9.L$0
            kotlinx.coroutines.flow.FlowCollector r10 = (kotlinx.coroutines.flow.FlowCollector) r10
            r1 = 10
            kotlinx.coroutines.channels.BufferOverflow r4 = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
            r5 = 4
            r6 = 0
            kotlinx.coroutines.channels.Channel r1 = kotlinx.coroutines.channels.ChannelKt.Channel$default(r1, r4, r6, r5, r6)
            androidx.window.layout.-$$Lambda$WindowInfoTrackerImpl$windowLayoutInfo$1$LgDWJbk4b494d79uZZm3iJ0WM6A r4 = new androidx.window.layout.-$$Lambda$WindowInfoTrackerImpl$windowLayoutInfo$1$LgDWJbk4b494d79uZZm3iJ0WM6A
            r4.<init>()
            androidx.window.layout.WindowInfoTrackerImpl r5 = r9.this$0
            androidx.window.layout.WindowBackend r5 = r5.windowBackend
            android.app.Activity r6 = r9.$activity
            androidx.window.layout.-$$Lambda$PNiE7SuEFxRjAZH7pJpZIFOFjWg r7 = androidx.window.layout.$$Lambda$PNiE7SuEFxRjAZH7pJpZIFOFjWg.INSTANCE
            r5.registerLayoutChangeCallback(r6, r7, r4)
            kotlinx.coroutines.channels.ChannelIterator r1 = r1.iterator()     // Catch:{ all -> 0x00a6 }
        L_0x0060:
            r5 = r9
        L_0x0061:
            r6 = r5
            kotlin.coroutines.Continuation r6 = (kotlin.coroutines.Continuation) r6     // Catch:{ all -> 0x00a4 }
            r5.L$0 = r10     // Catch:{ all -> 0x00a4 }
            r5.L$1 = r4     // Catch:{ all -> 0x00a4 }
            r5.L$2 = r1     // Catch:{ all -> 0x00a4 }
            r5.label = r3     // Catch:{ all -> 0x00a4 }
            java.lang.Object r6 = r1.hasNext(r6)     // Catch:{ all -> 0x00a4 }
            if (r6 != r0) goto L_0x0073
            return r0
        L_0x0073:
            r8 = r6
            r6 = r10
            r10 = r8
        L_0x0076:
            java.lang.Boolean r10 = (java.lang.Boolean) r10     // Catch:{ all -> 0x00a4 }
            boolean r10 = r10.booleanValue()     // Catch:{ all -> 0x00a4 }
            if (r10 == 0) goto L_0x0098
            java.lang.Object r10 = r1.next()     // Catch:{ all -> 0x00a4 }
            androidx.window.layout.WindowLayoutInfo r10 = (androidx.window.layout.WindowLayoutInfo) r10     // Catch:{ all -> 0x00a4 }
            r7 = r5
            kotlin.coroutines.Continuation r7 = (kotlin.coroutines.Continuation) r7     // Catch:{ all -> 0x00a4 }
            r5.L$0 = r6     // Catch:{ all -> 0x00a4 }
            r5.L$1 = r4     // Catch:{ all -> 0x00a4 }
            r5.L$2 = r1     // Catch:{ all -> 0x00a4 }
            r5.label = r2     // Catch:{ all -> 0x00a4 }
            java.lang.Object r10 = r6.emit(r10, r7)     // Catch:{ all -> 0x00a4 }
            if (r10 != r0) goto L_0x0096
            return r0
        L_0x0096:
            r10 = r6
            goto L_0x0061
        L_0x0098:
            androidx.window.layout.WindowInfoTrackerImpl r10 = r5.this$0
            androidx.window.layout.WindowBackend r10 = r10.windowBackend
            r10.unregisterLayoutChangeCallback(r4)
            kotlin.Unit r10 = kotlin.Unit.INSTANCE
            return r10
        L_0x00a4:
            r10 = move-exception
            goto L_0x00a8
        L_0x00a6:
            r10 = move-exception
            r5 = r9
        L_0x00a8:
            androidx.window.layout.WindowInfoTrackerImpl r0 = r5.this$0
            androidx.window.layout.WindowBackend r0 = r0.windowBackend
            r0.unregisterLayoutChangeCallback(r4)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.layout.WindowInfoTrackerImpl$windowLayoutInfo$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }

    /* access modifiers changed from: private */
    /* renamed from: invokeSuspend$lambda-0  reason: not valid java name */
    public static final void m21invokeSuspend$lambda0(Channel channel, WindowLayoutInfo windowLayoutInfo) {
        Intrinsics.checkNotNullExpressionValue(windowLayoutInfo, "info");
        channel.m1599trySendJP2dKIU(windowLayoutInfo);
    }
}
