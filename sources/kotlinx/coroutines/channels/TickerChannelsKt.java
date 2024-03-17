package kotlinx.coroutines.channels;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;

@Metadata(d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a/\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H@ø\u0001\u0000¢\u0006\u0002\u0010\u0007\u001a/\u0010\b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H@ø\u0001\u0000¢\u0006\u0002\u0010\u0007\u001a4\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u0002\u0004\n\u0002\b\u0019¨\u0006\u000f"}, d2 = {"fixedDelayTicker", "", "delayMillis", "", "initialDelayMillis", "channel", "Lkotlinx/coroutines/channels/SendChannel;", "(JJLkotlinx/coroutines/channels/SendChannel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fixedPeriodTicker", "ticker", "Lkotlinx/coroutines/channels/ReceiveChannel;", "context", "Lkotlin/coroutines/CoroutineContext;", "mode", "Lkotlinx/coroutines/channels/TickerMode;", "kotlinx-coroutines-core"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* compiled from: TickerChannels.kt */
public final class TickerChannelsKt {
    public static /* synthetic */ ReceiveChannel ticker$default(long j, long j2, CoroutineContext coroutineContext, TickerMode tickerMode, int i, Object obj) {
        if ((i & 2) != 0) {
            j2 = j;
        }
        if ((i & 4) != 0) {
            coroutineContext = EmptyCoroutineContext.INSTANCE;
        }
        if ((i & 8) != 0) {
            tickerMode = TickerMode.FIXED_PERIOD;
        }
        return ticker(j, j2, coroutineContext, tickerMode);
    }

    public static final ReceiveChannel<Unit> ticker(long j, long j2, CoroutineContext coroutineContext, TickerMode tickerMode) {
        long j3 = j2;
        boolean z = true;
        if (j >= 0) {
            if (j3 < 0) {
                z = false;
            }
            if (z) {
                CoroutineContext coroutineContext2 = coroutineContext;
                return ProduceKt.produce(GlobalScope.INSTANCE, Dispatchers.getUnconfined().plus(coroutineContext), 0, new TickerChannelsKt$ticker$3(tickerMode, j, j2, (Continuation<? super TickerChannelsKt$ticker$3>) null));
            }
            throw new IllegalArgumentException(("Expected non-negative initial delay, but has " + j2 + " ms").toString());
        }
        throw new IllegalArgumentException(("Expected non-negative delay, but has " + j + " ms").toString());
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00c6 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f8  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0128 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0029  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.Object fixedPeriodTicker(long r19, long r21, kotlinx.coroutines.channels.SendChannel<? super kotlin.Unit> r23, kotlin.coroutines.Continuation<? super kotlin.Unit> r24) {
        /*
            r0 = r24
            boolean r1 = r0 instanceof kotlinx.coroutines.channels.TickerChannelsKt$fixedPeriodTicker$1
            if (r1 == 0) goto L_0x0016
            r1 = r0
            kotlinx.coroutines.channels.TickerChannelsKt$fixedPeriodTicker$1 r1 = (kotlinx.coroutines.channels.TickerChannelsKt$fixedPeriodTicker$1) r1
            int r2 = r1.label
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            r2 = r2 & r3
            if (r2 == 0) goto L_0x0016
            int r0 = r1.label
            int r0 = r0 - r3
            r1.label = r0
            goto L_0x001b
        L_0x0016:
            kotlinx.coroutines.channels.TickerChannelsKt$fixedPeriodTicker$1 r1 = new kotlinx.coroutines.channels.TickerChannelsKt$fixedPeriodTicker$1
            r1.<init>(r0)
        L_0x001b:
            java.lang.Object r0 = r1.result
            java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r3 = r1.label
            r5 = 4
            r6 = 3
            r7 = 2
            r8 = 1
            if (r3 == 0) goto L_0x0077
            if (r3 == r8) goto L_0x006a
            if (r3 == r7) goto L_0x005e
            if (r3 == r6) goto L_0x004c
            if (r3 != r5) goto L_0x0044
            long r8 = r1.J$1
            long r10 = r1.J$0
            java.lang.Object r3 = r1.L$0
            kotlinx.coroutines.channels.SendChannel r3 = (kotlinx.coroutines.channels.SendChannel) r3
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r5
        L_0x003d:
            r17 = r8
            r8 = r10
            r10 = r17
            goto L_0x0129
        L_0x0044:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L_0x004c:
            long r8 = r1.J$1
            long r10 = r1.J$0
            java.lang.Object r3 = r1.L$0
            kotlinx.coroutines.channels.SendChannel r3 = (kotlinx.coroutines.channels.SendChannel) r3
            kotlin.ResultKt.throwOnFailure(r0)
        L_0x0057:
            r17 = r8
            r8 = r10
            r10 = r17
            goto L_0x0112
        L_0x005e:
            long r8 = r1.J$1
            long r10 = r1.J$0
            java.lang.Object r3 = r1.L$0
            kotlinx.coroutines.channels.SendChannel r3 = (kotlinx.coroutines.channels.SendChannel) r3
            kotlin.ResultKt.throwOnFailure(r0)
            goto L_0x00cd
        L_0x006a:
            long r8 = r1.J$1
            long r10 = r1.J$0
            java.lang.Object r3 = r1.L$0
            kotlinx.coroutines.channels.SendChannel r3 = (kotlinx.coroutines.channels.SendChannel) r3
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r3
            goto L_0x00b1
        L_0x0077:
            kotlin.ResultKt.throwOnFailure(r0)
            kotlinx.coroutines.AbstractTimeSource r0 = kotlinx.coroutines.AbstractTimeSourceKt.getTimeSource()
            if (r0 != 0) goto L_0x0082
            r0 = 0
            goto L_0x008a
        L_0x0082:
            long r9 = r0.nanoTime()
            java.lang.Long r0 = kotlin.coroutines.jvm.internal.Boxing.boxLong(r9)
        L_0x008a:
            if (r0 != 0) goto L_0x0091
            long r9 = java.lang.System.nanoTime()
            goto L_0x0095
        L_0x0091:
            long r9 = r0.longValue()
        L_0x0095:
            long r11 = kotlinx.coroutines.EventLoop_commonKt.delayToNanos(r21)
            long r9 = r9 + r11
            r0 = r23
            r1.L$0 = r0
            r11 = r19
            r1.J$0 = r11
            r1.J$1 = r9
            r1.label = r8
            r13 = r21
            java.lang.Object r3 = kotlinx.coroutines.DelayKt.delay(r13, r1)
            if (r3 != r2) goto L_0x00af
            return r2
        L_0x00af:
            r8 = r9
            r10 = r11
        L_0x00b1:
            long r10 = kotlinx.coroutines.EventLoop_commonKt.delayToNanos(r10)
        L_0x00b5:
            long r8 = r8 + r10
            kotlin.Unit r3 = kotlin.Unit.INSTANCE
            r1.L$0 = r0
            r1.J$0 = r8
            r1.J$1 = r10
            r1.label = r7
            java.lang.Object r3 = r0.send(r3, r1)
            if (r3 != r2) goto L_0x00c7
            return r2
        L_0x00c7:
            r3 = r0
            r17 = r8
            r8 = r10
            r10 = r17
        L_0x00cd:
            kotlinx.coroutines.AbstractTimeSource r0 = kotlinx.coroutines.AbstractTimeSourceKt.getTimeSource()
            if (r0 != 0) goto L_0x00d5
            r0 = 0
            goto L_0x00dd
        L_0x00d5:
            long r12 = r0.nanoTime()
            java.lang.Long r0 = kotlin.coroutines.jvm.internal.Boxing.boxLong(r12)
        L_0x00dd:
            if (r0 != 0) goto L_0x00e4
            long r12 = java.lang.System.nanoTime()
            goto L_0x00e8
        L_0x00e4:
            long r12 = r0.longValue()
        L_0x00e8:
            long r14 = r10 - r12
            r4 = 0
            long r14 = kotlin.ranges.RangesKt.coerceAtLeast((long) r14, (long) r4)
            int r16 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r16 != 0) goto L_0x0115
            int r4 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r4 == 0) goto L_0x0115
            long r4 = r12 - r10
            long r4 = r4 % r8
            long r4 = r8 - r4
            long r10 = r12 + r4
            long r4 = kotlinx.coroutines.EventLoop_commonKt.delayNanosToMillis(r4)
            r1.L$0 = r3
            r1.J$0 = r10
            r1.J$1 = r8
            r1.label = r6
            java.lang.Object r4 = kotlinx.coroutines.DelayKt.delay(r4, r1)
            if (r4 != r2) goto L_0x0057
            return r2
        L_0x0112:
            r0 = r3
            r5 = 4
            goto L_0x00b5
        L_0x0115:
            long r4 = kotlinx.coroutines.EventLoop_commonKt.delayNanosToMillis(r14)
            r1.L$0 = r3
            r1.J$0 = r10
            r1.J$1 = r8
            r0 = 4
            r1.label = r0
            java.lang.Object r4 = kotlinx.coroutines.DelayKt.delay(r4, r1)
            if (r4 != r2) goto L_0x003d
            return r2
        L_0x0129:
            r5 = r0
            r0 = r3
            goto L_0x00b5
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.TickerChannelsKt.fixedPeriodTicker(long, long, kotlinx.coroutines.channels.SendChannel, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: kotlinx.coroutines.channels.SendChannel<? super kotlin.Unit>} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0072 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0080 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0026  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.Object fixedDelayTicker(long r6, long r8, kotlinx.coroutines.channels.SendChannel<? super kotlin.Unit> r10, kotlin.coroutines.Continuation<? super kotlin.Unit> r11) {
        /*
            boolean r0 = r11 instanceof kotlinx.coroutines.channels.TickerChannelsKt$fixedDelayTicker$1
            if (r0 == 0) goto L_0x0014
            r0 = r11
            kotlinx.coroutines.channels.TickerChannelsKt$fixedDelayTicker$1 r0 = (kotlinx.coroutines.channels.TickerChannelsKt$fixedDelayTicker$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L_0x0014
            int r11 = r0.label
            int r11 = r11 - r2
            r0.label = r11
            goto L_0x0019
        L_0x0014:
            kotlinx.coroutines.channels.TickerChannelsKt$fixedDelayTicker$1 r0 = new kotlinx.coroutines.channels.TickerChannelsKt$fixedDelayTicker$1
            r0.<init>(r11)
        L_0x0019:
            java.lang.Object r11 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            r3 = 3
            r4 = 2
            r5 = 1
            if (r2 == 0) goto L_0x0054
            if (r2 == r5) goto L_0x0049
            if (r2 == r4) goto L_0x003f
            if (r2 != r3) goto L_0x0037
            long r6 = r0.J$0
            java.lang.Object r8 = r0.L$0
            kotlinx.coroutines.channels.SendChannel r8 = (kotlinx.coroutines.channels.SendChannel) r8
            kotlin.ResultKt.throwOnFailure(r11)
        L_0x0035:
            r10 = r8
            goto L_0x0064
        L_0x0037:
            java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
            java.lang.String r7 = "call to 'resume' before 'invoke' with coroutine"
            r6.<init>(r7)
            throw r6
        L_0x003f:
            long r6 = r0.J$0
            java.lang.Object r8 = r0.L$0
            kotlinx.coroutines.channels.SendChannel r8 = (kotlinx.coroutines.channels.SendChannel) r8
            kotlin.ResultKt.throwOnFailure(r11)
            goto L_0x0074
        L_0x0049:
            long r6 = r0.J$0
            java.lang.Object r8 = r0.L$0
            r10 = r8
            kotlinx.coroutines.channels.SendChannel r10 = (kotlinx.coroutines.channels.SendChannel) r10
            kotlin.ResultKt.throwOnFailure(r11)
            goto L_0x0064
        L_0x0054:
            kotlin.ResultKt.throwOnFailure(r11)
            r0.L$0 = r10
            r0.J$0 = r6
            r0.label = r5
            java.lang.Object r8 = kotlinx.coroutines.DelayKt.delay(r8, r0)
            if (r8 != r1) goto L_0x0064
            return r1
        L_0x0064:
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            r0.L$0 = r10
            r0.J$0 = r6
            r0.label = r4
            java.lang.Object r8 = r10.send(r8, r0)
            if (r8 != r1) goto L_0x0073
            return r1
        L_0x0073:
            r8 = r10
        L_0x0074:
            r0.L$0 = r8
            r0.J$0 = r6
            r0.label = r3
            java.lang.Object r9 = kotlinx.coroutines.DelayKt.delay(r6, r0)
            if (r9 != r1) goto L_0x0035
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.TickerChannelsKt.fixedDelayTicker(long, long, kotlinx.coroutines.channels.SendChannel, kotlin.coroutines.Continuation):java.lang.Object");
    }
}
