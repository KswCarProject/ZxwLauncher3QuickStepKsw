package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.channels.ChannelResult;
import kotlinx.coroutines.flow.internal.NullSurrogateKt;

@Metadata(d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H@"}, d2 = {"<anonymous>", "", "T", "value", "Lkotlinx/coroutines/channels/ChannelResult;", ""}, k = 3, mv = {1, 6, 0}, xi = 48)
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__DelayKt$debounceInternal$1$3$2", f = "Delay.kt", i = {0}, l = {243}, m = "invokeSuspend", n = {"$this$onFailure$iv"}, s = {"L$0"})
/* compiled from: Delay.kt */
final class FlowKt__DelayKt$debounceInternal$1$3$2 extends SuspendLambda implements Function2<ChannelResult<? extends Object>, Continuation<? super Unit>, Object> {
    final /* synthetic */ FlowCollector<T> $downstream;
    final /* synthetic */ Ref.ObjectRef<Object> $lastValue;
    /* synthetic */ Object L$0;
    Object L$1;
    int label;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    FlowKt__DelayKt$debounceInternal$1$3$2(Ref.ObjectRef<Object> objectRef, FlowCollector<? super T> flowCollector, Continuation<? super FlowKt__DelayKt$debounceInternal$1$3$2> continuation) {
        super(2, continuation);
        this.$lastValue = objectRef;
        this.$downstream = flowCollector;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        FlowKt__DelayKt$debounceInternal$1$3$2 flowKt__DelayKt$debounceInternal$1$3$2 = new FlowKt__DelayKt$debounceInternal$1$3$2(this.$lastValue, this.$downstream, continuation);
        flowKt__DelayKt$debounceInternal$1$3$2.L$0 = obj;
        return flowKt__DelayKt$debounceInternal$1$3$2;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj, Object obj2) {
        return m1606invokeWpGqRn0(((ChannelResult) obj).m1589unboximpl(), (Continuation) obj2);
    }

    /* renamed from: invoke-WpGqRn0  reason: not valid java name */
    public final Object m1606invokeWpGqRn0(Object obj, Continuation<? super Unit> continuation) {
        return ((FlowKt__DelayKt$debounceInternal$1$3$2) create(ChannelResult.m1577boximpl(obj), continuation)).invokeSuspend(Unit.INSTANCE);
    }

    public final Object invokeSuspend(Object obj) {
        Ref.ObjectRef<Object> objectRef;
        Ref.ObjectRef<Object> objectRef2;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            T r7 = ((ChannelResult) this.L$0).m1589unboximpl();
            Ref.ObjectRef<Object> objectRef3 = this.$lastValue;
            boolean z = r7 instanceof ChannelResult.Failed;
            if (!z) {
                objectRef3.element = r7;
            }
            objectRef = this.$lastValue;
            FlowCollector<T> flowCollector = this.$downstream;
            if (z) {
                Throwable r3 = ChannelResult.m1581exceptionOrNullimpl(r7);
                if (r3 == null) {
                    if (objectRef.element != null) {
                        T t = NullSurrogateKt.NULL;
                        T t2 = objectRef.element;
                        if (t2 == t) {
                            t2 = null;
                        }
                        this.L$0 = r7;
                        this.L$1 = objectRef;
                        this.label = 1;
                        if (flowCollector.emit(t2, this) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        objectRef2 = objectRef;
                    }
                    objectRef.element = NullSurrogateKt.DONE;
                } else {
                    throw r3;
                }
            }
            return Unit.INSTANCE;
        } else if (i == 1) {
            objectRef2 = (Ref.ObjectRef) this.L$1;
            ResultKt.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        objectRef = objectRef2;
        objectRef.element = NullSurrogateKt.DONE;
        return Unit.INSTANCE;
    }
}
