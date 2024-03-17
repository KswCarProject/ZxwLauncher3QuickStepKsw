package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlinx.coroutines.CoroutineScope;

@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0005H@"}, d2 = {"<anonymous>", "", "T", "Lkotlinx/coroutines/CoroutineScope;", "downstream", "Lkotlinx/coroutines/flow/FlowCollector;"}, k = 3, mv = {1, 6, 0}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1", f = "Delay.kt", i = {0, 0, 0, 0, 1, 1, 1, 1}, l = {222, 355}, m = "invokeSuspend", n = {"downstream", "values", "lastValue", "timeoutMillis", "downstream", "values", "lastValue", "timeoutMillis"}, s = {"L$0", "L$1", "L$2", "L$3", "L$0", "L$1", "L$2", "L$3"})
/* compiled from: Delay.kt */
final class FlowKt__DelayKt$debounceInternal$1 extends SuspendLambda implements Function3<CoroutineScope, FlowCollector<? super T>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Flow<T> $this_debounceInternal;
    final /* synthetic */ Function1<T, Long> $timeoutMillisSelector;
    private /* synthetic */ Object L$0;
    /* synthetic */ Object L$1;
    Object L$2;
    Object L$3;
    int label;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    FlowKt__DelayKt$debounceInternal$1(Function1<? super T, Long> function1, Flow<? extends T> flow, Continuation<? super FlowKt__DelayKt$debounceInternal$1> continuation) {
        super(3, continuation);
        this.$timeoutMillisSelector = function1;
        this.$this_debounceInternal = flow;
    }

    public final Object invoke(CoroutineScope coroutineScope, FlowCollector<? super T> flowCollector, Continuation<? super Unit> continuation) {
        FlowKt__DelayKt$debounceInternal$1 flowKt__DelayKt$debounceInternal$1 = new FlowKt__DelayKt$debounceInternal$1(this.$timeoutMillisSelector, this.$this_debounceInternal, continuation);
        flowKt__DelayKt$debounceInternal$1.L$0 = coroutineScope;
        flowKt__DelayKt$debounceInternal$1.L$1 = flowCollector;
        return flowKt__DelayKt$debounceInternal$1.invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(14:7|29|30|33|(3:35|(1:40)(1:39)|(2:42|43))|44|45|46|(1:48)|49|52|(1:54)|(1:56)(2:57|58)|56) */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x012c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x012d, code lost:
        r14.handleBuilderException(r0);
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0111 A[Catch:{ all -> 0x012c }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0140  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object invokeSuspend(java.lang.Object r18) {
        /*
            r17 = this;
            r1 = r17
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r1.label
            r4 = 2
            r5 = 0
            r7 = 1
            r8 = 0
            if (r2 == 0) goto L_0x004b
            if (r2 == r7) goto L_0x0034
            if (r2 != r4) goto L_0x002c
            java.lang.Object r2 = r1.L$3
            kotlin.jvm.internal.Ref$LongRef r2 = (kotlin.jvm.internal.Ref.LongRef) r2
            java.lang.Object r2 = r1.L$2
            kotlin.jvm.internal.Ref$ObjectRef r2 = (kotlin.jvm.internal.Ref.ObjectRef) r2
            java.lang.Object r9 = r1.L$1
            kotlinx.coroutines.channels.ReceiveChannel r9 = (kotlinx.coroutines.channels.ReceiveChannel) r9
            java.lang.Object r10 = r1.L$0
            kotlinx.coroutines.flow.FlowCollector r10 = (kotlinx.coroutines.flow.FlowCollector) r10
            kotlin.ResultKt.throwOnFailure(r18)
            r11 = r10
            r10 = r9
            r9 = r2
            r2 = r1
            goto L_0x0144
        L_0x002c:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r2)
            throw r0
        L_0x0034:
            java.lang.Object r2 = r1.L$3
            kotlin.jvm.internal.Ref$LongRef r2 = (kotlin.jvm.internal.Ref.LongRef) r2
            java.lang.Object r9 = r1.L$2
            kotlin.jvm.internal.Ref$ObjectRef r9 = (kotlin.jvm.internal.Ref.ObjectRef) r9
            java.lang.Object r10 = r1.L$1
            kotlinx.coroutines.channels.ReceiveChannel r10 = (kotlinx.coroutines.channels.ReceiveChannel) r10
            java.lang.Object r11 = r1.L$0
            kotlinx.coroutines.flow.FlowCollector r11 = (kotlinx.coroutines.flow.FlowCollector) r11
            kotlin.ResultKt.throwOnFailure(r18)
            r12 = r2
            r2 = r1
            goto L_0x00c4
        L_0x004b:
            kotlin.ResultKt.throwOnFailure(r18)
            java.lang.Object r2 = r1.L$0
            r9 = r2
            kotlinx.coroutines.CoroutineScope r9 = (kotlinx.coroutines.CoroutineScope) r9
            java.lang.Object r2 = r1.L$1
            kotlinx.coroutines.flow.FlowCollector r2 = (kotlinx.coroutines.flow.FlowCollector) r2
            r10 = 0
            r11 = 0
            kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1$values$1 r12 = new kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1$values$1
            kotlinx.coroutines.flow.Flow<T> r13 = r1.$this_debounceInternal
            r12.<init>(r13, r8)
            kotlin.jvm.functions.Function2 r12 = (kotlin.jvm.functions.Function2) r12
            r13 = 3
            r14 = 0
            kotlinx.coroutines.channels.ReceiveChannel r9 = kotlinx.coroutines.channels.ProduceKt.produce$default(r9, r10, r11, r12, r13, r14)
            kotlin.jvm.internal.Ref$ObjectRef r10 = new kotlin.jvm.internal.Ref$ObjectRef
            r10.<init>()
            r11 = r2
            r2 = r1
            r16 = r10
            r10 = r9
            r9 = r16
        L_0x0074:
            T r12 = r9.element
            kotlinx.coroutines.internal.Symbol r13 = kotlinx.coroutines.flow.internal.NullSurrogateKt.DONE
            if (r12 == r13) goto L_0x0149
            kotlin.jvm.internal.Ref$LongRef r12 = new kotlin.jvm.internal.Ref$LongRef
            r12.<init>()
            T r13 = r9.element
            if (r13 == 0) goto L_0x00c6
            kotlin.jvm.functions.Function1<T, java.lang.Long> r13 = r2.$timeoutMillisSelector
            kotlinx.coroutines.internal.Symbol r14 = kotlinx.coroutines.flow.internal.NullSurrogateKt.NULL
            T r15 = r9.element
            if (r15 != r14) goto L_0x008c
            r15 = r8
        L_0x008c:
            java.lang.Object r13 = r13.invoke(r15)
            java.lang.Number r13 = (java.lang.Number) r13
            long r13 = r13.longValue()
            r12.element = r13
            long r13 = r12.element
            int r13 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r13 < 0) goto L_0x00a0
            r13 = r7
            goto L_0x00a1
        L_0x00a0:
            r13 = 0
        L_0x00a1:
            if (r13 == 0) goto L_0x00cf
            long r13 = r12.element
            int r13 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x00c6
            kotlinx.coroutines.internal.Symbol r13 = kotlinx.coroutines.flow.internal.NullSurrogateKt.NULL
            T r14 = r9.element
            if (r14 != r13) goto L_0x00b0
            r14 = r8
        L_0x00b0:
            r13 = r2
            kotlin.coroutines.Continuation r13 = (kotlin.coroutines.Continuation) r13
            r2.L$0 = r11
            r2.L$1 = r10
            r2.L$2 = r9
            r2.L$3 = r12
            r2.label = r7
            java.lang.Object r13 = r11.emit(r14, r13)
            if (r13 != r0) goto L_0x00c4
            return r0
        L_0x00c4:
            r9.element = r8
        L_0x00c6:
            r16 = r2
            r2 = r0
            r0 = r12
            r12 = r11
            r11 = r10
            r10 = r16
            goto L_0x00db
        L_0x00cf:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r2 = "Debounce timeout should not be negative"
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x00db:
            boolean r13 = kotlinx.coroutines.DebugKt.getASSERTIONS_ENABLED()
            if (r13 == 0) goto L_0x00f8
            T r13 = r9.element
            if (r13 == 0) goto L_0x00ee
            long r13 = r0.element
            int r13 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r13 <= 0) goto L_0x00ec
            goto L_0x00ee
        L_0x00ec:
            r13 = 0
            goto L_0x00ef
        L_0x00ee:
            r13 = r7
        L_0x00ef:
            if (r13 == 0) goto L_0x00f2
            goto L_0x00f8
        L_0x00f2:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x00f8:
            r10.L$0 = r12
            r10.L$1 = r11
            r10.L$2 = r9
            r10.L$3 = r0
            r10.label = r4
            r13 = r10
            kotlin.coroutines.Continuation r13 = (kotlin.coroutines.Continuation) r13
            kotlinx.coroutines.selects.SelectBuilderImpl r14 = new kotlinx.coroutines.selects.SelectBuilderImpl
            r14.<init>(r13)
            r15 = r14
            kotlinx.coroutines.selects.SelectBuilder r15 = (kotlinx.coroutines.selects.SelectBuilder) r15     // Catch:{ all -> 0x012c }
            T r3 = r9.element     // Catch:{ all -> 0x012c }
            if (r3 == 0) goto L_0x011d
            long r4 = r0.element     // Catch:{ all -> 0x012c }
            kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1$3$1 r0 = new kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1$3$1     // Catch:{ all -> 0x012c }
            r0.<init>(r12, r9, r8)     // Catch:{ all -> 0x012c }
            kotlin.jvm.functions.Function1 r0 = (kotlin.jvm.functions.Function1) r0     // Catch:{ all -> 0x012c }
            r15.onTimeout(r4, r0)     // Catch:{ all -> 0x012c }
        L_0x011d:
            kotlinx.coroutines.selects.SelectClause1 r0 = r11.getOnReceiveCatching()     // Catch:{ all -> 0x012c }
            kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1$3$2 r4 = new kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1$3$2     // Catch:{ all -> 0x012c }
            r4.<init>(r9, r12, r8)     // Catch:{ all -> 0x012c }
            kotlin.jvm.functions.Function2 r4 = (kotlin.jvm.functions.Function2) r4     // Catch:{ all -> 0x012c }
            r15.invoke(r0, r4)     // Catch:{ all -> 0x012c }
            goto L_0x0130
        L_0x012c:
            r0 = move-exception
            r14.handleBuilderException(r0)
        L_0x0130:
            java.lang.Object r0 = r14.getResult()
            java.lang.Object r4 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            if (r0 != r4) goto L_0x013d
            kotlin.coroutines.jvm.internal.DebugProbesKt.probeCoroutineSuspended(r13)
        L_0x013d:
            if (r0 != r2) goto L_0x0140
            return r2
        L_0x0140:
            r0 = r2
            r2 = r10
            r10 = r11
            r11 = r12
        L_0x0144:
            r4 = 2
            r5 = 0
            goto L_0x0074
        L_0x0149:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
