package androidx.core.view;

import android.view.View;
import android.view.ViewGroup;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequenceScope;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H@¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", "", "Lkotlin/sequences/SequenceScope;", "Landroid/view/View;", "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"}, k = 3, mv = {1, 4, 2})
@DebugMetadata(c = "androidx.core.view.ViewGroupKt$descendants$1", f = "ViewGroup.kt", i = {0, 0, 0, 0, 1, 1, 1}, l = {97, 99}, m = "invokeSuspend", n = {"$this$sequence", "$this$forEach$iv", "child", "index$iv", "$this$sequence", "$this$forEach$iv", "index$iv"}, s = {"L$0", "L$1", "L$2", "I$0", "L$0", "L$1", "I$0"})
/* compiled from: ViewGroup.kt */
final class ViewGroupKt$descendants$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super View>, Continuation<? super Unit>, Object> {
    final /* synthetic */ ViewGroup $this_descendants;
    int I$0;
    int I$1;
    private /* synthetic */ Object L$0;
    Object L$1;
    Object L$2;
    int label;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ViewGroupKt$descendants$1(ViewGroup viewGroup, Continuation continuation) {
        super(2, continuation);
        this.$this_descendants = viewGroup;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        Intrinsics.checkNotNullParameter(continuation, "completion");
        ViewGroupKt$descendants$1 viewGroupKt$descendants$1 = new ViewGroupKt$descendants$1(this.$this_descendants, continuation);
        viewGroupKt$descendants$1.L$0 = obj;
        return viewGroupKt$descendants$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((ViewGroupKt$descendants$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x008e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object invokeSuspend(java.lang.Object r12) {
        /*
            r11 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r1 = r11.label
            r2 = 2
            r3 = 1
            if (r1 == 0) goto L_0x003d
            if (r1 == r3) goto L_0x0028
            if (r1 != r2) goto L_0x0020
            int r1 = r11.I$1
            int r4 = r11.I$0
            java.lang.Object r5 = r11.L$1
            android.view.ViewGroup r5 = (android.view.ViewGroup) r5
            java.lang.Object r6 = r11.L$0
            kotlin.sequences.SequenceScope r6 = (kotlin.sequences.SequenceScope) r6
            kotlin.ResultKt.throwOnFailure(r12)
            r12 = r11
            goto L_0x0090
        L_0x0020:
            java.lang.IllegalStateException r12 = new java.lang.IllegalStateException
            java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
            r12.<init>(r0)
            throw r12
        L_0x0028:
            int r1 = r11.I$1
            int r4 = r11.I$0
            java.lang.Object r5 = r11.L$2
            android.view.View r5 = (android.view.View) r5
            java.lang.Object r6 = r11.L$1
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            java.lang.Object r7 = r11.L$0
            kotlin.sequences.SequenceScope r7 = (kotlin.sequences.SequenceScope) r7
            kotlin.ResultKt.throwOnFailure(r12)
            r12 = r11
            goto L_0x0070
        L_0x003d:
            kotlin.ResultKt.throwOnFailure(r12)
            java.lang.Object r12 = r11.L$0
            kotlin.sequences.SequenceScope r12 = (kotlin.sequences.SequenceScope) r12
            android.view.ViewGroup r1 = r11.$this_descendants
            r4 = 0
            int r5 = r1.getChildCount()
            r6 = r11
        L_0x004c:
            if (r4 >= r5) goto L_0x009d
            android.view.View r7 = r1.getChildAt(r4)
            java.lang.String r8 = "getChildAt(index)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r7, r8)
            r6.L$0 = r12
            r6.L$1 = r1
            r6.L$2 = r7
            r6.I$0 = r4
            r6.I$1 = r5
            r6.label = r3
            java.lang.Object r8 = r12.yield(r7, r6)
            if (r8 != r0) goto L_0x006a
            return r0
        L_0x006a:
            r9 = r7
            r7 = r12
            r12 = r6
            r6 = r1
            r1 = r5
            r5 = r9
        L_0x0070:
            boolean r8 = r5 instanceof android.view.ViewGroup
            if (r8 == 0) goto L_0x0097
            android.view.ViewGroup r5 = (android.view.ViewGroup) r5
            kotlin.sequences.Sequence r5 = androidx.core.view.ViewGroupKt.getDescendants(r5)
            r12.L$0 = r7
            r12.L$1 = r6
            r8 = 0
            r12.L$2 = r8
            r12.I$0 = r4
            r12.I$1 = r1
            r12.label = r2
            java.lang.Object r5 = r7.yieldAll(r5, (kotlin.coroutines.Continuation<? super kotlin.Unit>) r12)
            if (r5 != r0) goto L_0x008e
            return r0
        L_0x008e:
            r5 = r6
            r6 = r7
        L_0x0090:
            r9 = r6
            r6 = r12
            r12 = r9
            r10 = r5
            r5 = r1
            r1 = r10
            goto L_0x009b
        L_0x0097:
            r5 = r1
            r1 = r6
            r6 = r12
            r12 = r7
        L_0x009b:
            int r4 = r4 + r3
            goto L_0x004c
        L_0x009d:
            kotlin.Unit r12 = kotlin.Unit.INSTANCE
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.view.ViewGroupKt$descendants$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
