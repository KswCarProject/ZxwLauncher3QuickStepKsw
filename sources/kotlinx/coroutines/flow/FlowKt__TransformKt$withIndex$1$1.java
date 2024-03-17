package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.collections.IndexedValue;
import kotlin.coroutines.jvm.internal.SuspendFunction;
import kotlin.jvm.internal.Ref;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u0002H@¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", "", "T", "value", "emit", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: Transform.kt */
final class FlowKt__TransformKt$withIndex$1$1<T> implements FlowCollector, SuspendFunction {
    final /* synthetic */ Ref.IntRef $index;
    final /* synthetic */ FlowCollector<IndexedValue<? extends T>> $this_unsafeFlow;

    FlowKt__TransformKt$withIndex$1$1(FlowCollector<? super IndexedValue<? extends T>> flowCollector, Ref.IntRef intRef) {
        this.$this_unsafeFlow = flowCollector;
        this.$index = intRef;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0032  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0024  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object emit(T r8, kotlin.coroutines.Continuation<? super kotlin.Unit> r9) {
        /*
            r7 = this;
            boolean r0 = r9 instanceof kotlinx.coroutines.flow.FlowKt__TransformKt$withIndex$1$1$emit$1
            if (r0 == 0) goto L_0x0014
            r0 = r9
            kotlinx.coroutines.flow.FlowKt__TransformKt$withIndex$1$1$emit$1 r0 = (kotlinx.coroutines.flow.FlowKt__TransformKt$withIndex$1$1$emit$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L_0x0014
            int r9 = r0.label
            int r9 = r9 - r2
            r0.label = r9
            goto L_0x0019
        L_0x0014:
            kotlinx.coroutines.flow.FlowKt__TransformKt$withIndex$1$1$emit$1 r0 = new kotlinx.coroutines.flow.FlowKt__TransformKt$withIndex$1$1$emit$1
            r0.<init>(r7, r9)
        L_0x0019:
            java.lang.Object r9 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            r3 = 1
            if (r2 == 0) goto L_0x0032
            if (r2 != r3) goto L_0x002a
            kotlin.ResultKt.throwOnFailure(r9)
            goto L_0x0051
        L_0x002a:
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.String r9 = "call to 'resume' before 'invoke' with coroutine"
            r8.<init>(r9)
            throw r8
        L_0x0032:
            kotlin.ResultKt.throwOnFailure(r9)
            kotlinx.coroutines.flow.FlowCollector<kotlin.collections.IndexedValue<? extends T>> r9 = r7.$this_unsafeFlow
            kotlin.collections.IndexedValue r2 = new kotlin.collections.IndexedValue
            kotlin.jvm.internal.Ref$IntRef r4 = r7.$index
            int r4 = r4.element
            kotlin.jvm.internal.Ref$IntRef r5 = r7.$index
            int r6 = r4 + 1
            r5.element = r6
            if (r4 < 0) goto L_0x0054
            r2.<init>(r4, r8)
            r0.label = r3
            java.lang.Object r8 = r9.emit(r2, r0)
            if (r8 != r1) goto L_0x0051
            return r1
        L_0x0051:
            kotlin.Unit r8 = kotlin.Unit.INSTANCE
            return r8
        L_0x0054:
            java.lang.ArithmeticException r8 = new java.lang.ArithmeticException
            java.lang.String r9 = "Index overflow has happened"
            r8.<init>(r9)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.FlowKt__TransformKt$withIndex$1$1.emit(java.lang.Object, kotlin.coroutines.Continuation):java.lang.Object");
    }
}
