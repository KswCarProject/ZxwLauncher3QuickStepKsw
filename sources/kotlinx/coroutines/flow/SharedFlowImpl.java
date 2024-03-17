package kotlinx.coroutines.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationKt;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.DisposableHandle;
import kotlinx.coroutines.channels.BufferOverflow;
import kotlinx.coroutines.flow.internal.AbstractSharedFlow;
import kotlinx.coroutines.flow.internal.AbstractSharedFlowKt;
import kotlinx.coroutines.flow.internal.AbstractSharedFlowSlot;
import kotlinx.coroutines.flow.internal.FusibleFlow;

@Metadata(d1 = {"\u0000\u0001\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010 \n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0001\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0012\b\u0010\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u00020\u00030\u00022\b\u0012\u0004\u0012\u0002H\u00010\u00042\b\u0012\u0004\u0012\u0002H\u00010\u00052\b\u0012\u0004\u0012\u0002H\u00010\u0006:\u0001hB\u001d\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\u0019\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0003H@ø\u0001\u0000¢\u0006\u0002\u0010.J\u0010\u0010/\u001a\u00020,2\u0006\u00100\u001a\u000201H\u0002J\b\u00102\u001a\u00020,H\u0002J\u001f\u00103\u001a\u0002042\f\u00105\u001a\b\u0012\u0004\u0012\u00028\u000006H@ø\u0001\u0000¢\u0006\u0002\u00107J\u0010\u00108\u001a\u00020,2\u0006\u00109\u001a\u00020\u0012H\u0002J\b\u0010:\u001a\u00020\u0003H\u0014J\u001d\u0010;\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u000e2\u0006\u0010<\u001a\u00020\bH\u0014¢\u0006\u0002\u0010=J\b\u0010>\u001a\u00020,H\u0002J\u0019\u0010?\u001a\u00020,2\u0006\u0010@\u001a\u00028\u0000H@ø\u0001\u0000¢\u0006\u0002\u0010AJ\u0019\u0010B\u001a\u00020,2\u0006\u0010@\u001a\u00028\u0000H@ø\u0001\u0000¢\u0006\u0002\u0010AJ\u0012\u0010C\u001a\u00020,2\b\u0010D\u001a\u0004\u0018\u00010\u000fH\u0002J1\u0010E\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020,\u0018\u00010F0\u000e2\u0014\u0010G\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020,\u0018\u00010F0\u000eH\u0002¢\u0006\u0002\u0010HJ&\u0010I\u001a\b\u0012\u0004\u0012\u00028\u00000J2\u0006\u0010K\u001a\u00020L2\u0006\u0010M\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0012\u0010N\u001a\u0004\u0018\u00010\u000f2\u0006\u0010O\u001a\u00020\u0012H\u0002J7\u0010P\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u000e2\u0010\u0010Q\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u000f\u0018\u00010\u000e2\u0006\u0010R\u001a\u00020\b2\u0006\u0010S\u001a\u00020\bH\u0002¢\u0006\u0002\u0010TJ\b\u0010U\u001a\u00020,H\u0016J\u0015\u0010V\u001a\u00020W2\u0006\u0010@\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010XJ\u0015\u0010Y\u001a\u00020W2\u0006\u0010@\u001a\u00028\u0000H\u0002¢\u0006\u0002\u0010XJ\u0015\u0010Z\u001a\u00020W2\u0006\u0010@\u001a\u00028\u0000H\u0002¢\u0006\u0002\u0010XJ\u0010\u0010[\u001a\u00020\u00122\u0006\u0010-\u001a\u00020\u0003H\u0002J\u0012\u0010\\\u001a\u0004\u0018\u00010\u000f2\u0006\u0010-\u001a\u00020\u0003H\u0002J(\u0010]\u001a\u00020,2\u0006\u0010^\u001a\u00020\u00122\u0006\u0010_\u001a\u00020\u00122\u0006\u0010`\u001a\u00020\u00122\u0006\u0010a\u001a\u00020\u0012H\u0002J%\u0010b\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020,\u0018\u00010F0\u000e2\u0006\u0010c\u001a\u00020\u0012H\u0000¢\u0006\u0004\bd\u0010eJ\r\u0010f\u001a\u00020\u0012H\u0000¢\u0006\u0002\bgR\u001a\u0010\r\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u000f\u0018\u00010\u000eX\u000e¢\u0006\u0004\n\u0002\u0010\u0010R\u000e\u0010\t\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\u00020\u00128BX\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0015\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\u00020\u00128BX\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0014R\u001a\u0010\u0018\u001a\u00028\u00008DX\u0004¢\u0006\f\u0012\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001d\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\u00020\u00128BX\u0004¢\u0006\u0006\u001a\u0004\b\u001f\u0010\u0014R\u000e\u0010 \u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000R\u001a\u0010!\u001a\b\u0012\u0004\u0012\u00028\u00000\"8VX\u0004¢\u0006\u0006\u001a\u0004\b#\u0010$R\u000e\u0010%\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u0014\u0010&\u001a\u00020\b8BX\u0004¢\u0006\u0006\u001a\u0004\b'\u0010(R\u0014\u0010)\u001a\u00020\b8BX\u0004¢\u0006\u0006\u001a\u0004\b*\u0010(\u0002\u0004\n\u0002\b\u0019¨\u0006i"}, d2 = {"Lkotlinx/coroutines/flow/SharedFlowImpl;", "T", "Lkotlinx/coroutines/flow/internal/AbstractSharedFlow;", "Lkotlinx/coroutines/flow/SharedFlowSlot;", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lkotlinx/coroutines/flow/CancellableFlow;", "Lkotlinx/coroutines/flow/internal/FusibleFlow;", "replay", "", "bufferCapacity", "onBufferOverflow", "Lkotlinx/coroutines/channels/BufferOverflow;", "(IILkotlinx/coroutines/channels/BufferOverflow;)V", "buffer", "", "", "[Ljava/lang/Object;", "bufferEndIndex", "", "getBufferEndIndex", "()J", "bufferSize", "head", "getHead", "lastReplayedLocked", "getLastReplayedLocked$annotations", "()V", "getLastReplayedLocked", "()Ljava/lang/Object;", "minCollectorIndex", "queueEndIndex", "getQueueEndIndex", "queueSize", "replayCache", "", "getReplayCache", "()Ljava/util/List;", "replayIndex", "replaySize", "getReplaySize", "()I", "totalSize", "getTotalSize", "awaitValue", "", "slot", "(Lkotlinx/coroutines/flow/SharedFlowSlot;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cancelEmitter", "emitter", "Lkotlinx/coroutines/flow/SharedFlowImpl$Emitter;", "cleanupTailLocked", "collect", "", "collector", "Lkotlinx/coroutines/flow/FlowCollector;", "(Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "correctCollectorIndexesOnDropOldest", "newHead", "createSlot", "createSlotArray", "size", "(I)[Lkotlinx/coroutines/flow/SharedFlowSlot;", "dropOldestLocked", "emit", "value", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "emitSuspend", "enqueueLocked", "item", "findSlotsToResumeLocked", "Lkotlin/coroutines/Continuation;", "resumesIn", "([Lkotlin/coroutines/Continuation;)[Lkotlin/coroutines/Continuation;", "fuse", "Lkotlinx/coroutines/flow/Flow;", "context", "Lkotlin/coroutines/CoroutineContext;", "capacity", "getPeekedValueLockedAt", "index", "growBuffer", "curBuffer", "curSize", "newSize", "([Ljava/lang/Object;II)[Ljava/lang/Object;", "resetReplayCache", "tryEmit", "", "(Ljava/lang/Object;)Z", "tryEmitLocked", "tryEmitNoCollectorsLocked", "tryPeekLocked", "tryTakeValue", "updateBufferLocked", "newReplayIndex", "newMinCollectorIndex", "newBufferEndIndex", "newQueueEndIndex", "updateCollectorIndexLocked", "oldIndex", "updateCollectorIndexLocked$kotlinx_coroutines_core", "(J)[Lkotlin/coroutines/Continuation;", "updateNewCollectorIndexLocked", "updateNewCollectorIndexLocked$kotlinx_coroutines_core", "Emitter", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: SharedFlow.kt */
public class SharedFlowImpl<T> extends AbstractSharedFlow<SharedFlowSlot> implements MutableSharedFlow<T>, CancellableFlow<T>, FusibleFlow<T> {
    private Object[] buffer;
    /* access modifiers changed from: private */
    public final int bufferCapacity;
    private int bufferSize;
    private long minCollectorIndex;
    private final BufferOverflow onBufferOverflow;
    /* access modifiers changed from: private */
    public int queueSize;
    private final int replay;
    private long replayIndex;

    @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
    /* compiled from: SharedFlow.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[BufferOverflow.values().length];
            iArr[BufferOverflow.SUSPEND.ordinal()] = 1;
            iArr[BufferOverflow.DROP_LATEST.ordinal()] = 2;
            iArr[BufferOverflow.DROP_OLDEST.ordinal()] = 3;
            $EnumSwitchMapping$0 = iArr;
        }
    }

    protected static /* synthetic */ void getLastReplayedLocked$annotations() {
    }

    public Object collect(FlowCollector<? super T> flowCollector, Continuation<?> continuation) {
        return collect$suspendImpl(this, flowCollector, continuation);
    }

    public Object emit(T t, Continuation<? super Unit> continuation) {
        return emit$suspendImpl(this, t, continuation);
    }

    public SharedFlowImpl(int i, int i2, BufferOverflow bufferOverflow) {
        this.replay = i;
        this.bufferCapacity = i2;
        this.onBufferOverflow = bufferOverflow;
    }

    /* access modifiers changed from: private */
    public final long getHead() {
        return Math.min(this.minCollectorIndex, this.replayIndex);
    }

    private final int getReplaySize() {
        return (int) ((getHead() + ((long) this.bufferSize)) - this.replayIndex);
    }

    /* access modifiers changed from: private */
    public final int getTotalSize() {
        return this.bufferSize + this.queueSize;
    }

    private final long getBufferEndIndex() {
        return getHead() + ((long) this.bufferSize);
    }

    private final long getQueueEndIndex() {
        return getHead() + ((long) this.bufferSize) + ((long) this.queueSize);
    }

    /* access modifiers changed from: protected */
    public final T getLastReplayedLocked() {
        Object[] objArr = this.buffer;
        Intrinsics.checkNotNull(objArr);
        return SharedFlowKt.getBufferAt(objArr, (this.replayIndex + ((long) getReplaySize())) - 1);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: kotlinx.coroutines.flow.SharedFlowSlot} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00ae A[Catch:{ all -> 0x00d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c5 A[Catch:{ all -> 0x00d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0026  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ java.lang.Object collect$suspendImpl(kotlinx.coroutines.flow.SharedFlowImpl r8, kotlinx.coroutines.flow.FlowCollector r9, kotlin.coroutines.Continuation r10) {
        /*
            boolean r0 = r10 instanceof kotlinx.coroutines.flow.SharedFlowImpl$collect$1
            if (r0 == 0) goto L_0x0014
            r0 = r10
            kotlinx.coroutines.flow.SharedFlowImpl$collect$1 r0 = (kotlinx.coroutines.flow.SharedFlowImpl$collect$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L_0x0014
            int r10 = r0.label
            int r10 = r10 - r2
            r0.label = r10
            goto L_0x0019
        L_0x0014:
            kotlinx.coroutines.flow.SharedFlowImpl$collect$1 r0 = new kotlinx.coroutines.flow.SharedFlowImpl$collect$1
            r0.<init>(r8, r10)
        L_0x0019:
            java.lang.Object r10 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            r3 = 3
            r4 = 2
            r5 = 1
            if (r2 == 0) goto L_0x0076
            if (r2 == r5) goto L_0x005f
            if (r2 == r4) goto L_0x004b
            if (r2 != r3) goto L_0x0043
            java.lang.Object r8 = r0.L$3
            kotlinx.coroutines.Job r8 = (kotlinx.coroutines.Job) r8
            java.lang.Object r9 = r0.L$2
            kotlinx.coroutines.flow.SharedFlowSlot r9 = (kotlinx.coroutines.flow.SharedFlowSlot) r9
            java.lang.Object r2 = r0.L$1
            kotlinx.coroutines.flow.FlowCollector r2 = (kotlinx.coroutines.flow.FlowCollector) r2
            java.lang.Object r5 = r0.L$0
            kotlinx.coroutines.flow.SharedFlowImpl r5 = (kotlinx.coroutines.flow.SharedFlowImpl) r5
        L_0x003c:
            kotlin.ResultKt.throwOnFailure(r10)     // Catch:{ all -> 0x005c }
            r10 = r2
            r2 = r8
            r8 = r5
            goto L_0x00a6
        L_0x0043:
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.String r9 = "call to 'resume' before 'invoke' with coroutine"
            r8.<init>(r9)
            throw r8
        L_0x004b:
            java.lang.Object r8 = r0.L$3
            kotlinx.coroutines.Job r8 = (kotlinx.coroutines.Job) r8
            java.lang.Object r9 = r0.L$2
            kotlinx.coroutines.flow.SharedFlowSlot r9 = (kotlinx.coroutines.flow.SharedFlowSlot) r9
            java.lang.Object r2 = r0.L$1
            kotlinx.coroutines.flow.FlowCollector r2 = (kotlinx.coroutines.flow.FlowCollector) r2
            java.lang.Object r5 = r0.L$0
            kotlinx.coroutines.flow.SharedFlowImpl r5 = (kotlinx.coroutines.flow.SharedFlowImpl) r5
            goto L_0x003c
        L_0x005c:
            r8 = move-exception
            goto L_0x00de
        L_0x005f:
            java.lang.Object r8 = r0.L$2
            r9 = r8
            kotlinx.coroutines.flow.SharedFlowSlot r9 = (kotlinx.coroutines.flow.SharedFlowSlot) r9
            java.lang.Object r8 = r0.L$1
            kotlinx.coroutines.flow.FlowCollector r8 = (kotlinx.coroutines.flow.FlowCollector) r8
            java.lang.Object r2 = r0.L$0
            kotlinx.coroutines.flow.SharedFlowImpl r2 = (kotlinx.coroutines.flow.SharedFlowImpl) r2
            kotlin.ResultKt.throwOnFailure(r10)     // Catch:{ all -> 0x0072 }
            r10 = r8
            r8 = r2
            goto L_0x0098
        L_0x0072:
            r8 = move-exception
            r5 = r2
            goto L_0x00de
        L_0x0076:
            kotlin.ResultKt.throwOnFailure(r10)
            kotlinx.coroutines.flow.internal.AbstractSharedFlowSlot r10 = r8.allocateSlot()
            kotlinx.coroutines.flow.SharedFlowSlot r10 = (kotlinx.coroutines.flow.SharedFlowSlot) r10
            boolean r2 = r9 instanceof kotlinx.coroutines.flow.SubscribedFlowCollector     // Catch:{ all -> 0x00da }
            if (r2 == 0) goto L_0x0095
            r2 = r9
            kotlinx.coroutines.flow.SubscribedFlowCollector r2 = (kotlinx.coroutines.flow.SubscribedFlowCollector) r2     // Catch:{ all -> 0x00da }
            r0.L$0 = r8     // Catch:{ all -> 0x00da }
            r0.L$1 = r9     // Catch:{ all -> 0x00da }
            r0.L$2 = r10     // Catch:{ all -> 0x00da }
            r0.label = r5     // Catch:{ all -> 0x00da }
            java.lang.Object r2 = r2.onSubscription(r0)     // Catch:{ all -> 0x00da }
            if (r2 != r1) goto L_0x0095
            return r1
        L_0x0095:
            r7 = r10
            r10 = r9
            r9 = r7
        L_0x0098:
            kotlin.coroutines.CoroutineContext r2 = r0.getContext()     // Catch:{ all -> 0x00d6 }
            kotlinx.coroutines.Job$Key r5 = kotlinx.coroutines.Job.Key     // Catch:{ all -> 0x00d6 }
            kotlin.coroutines.CoroutineContext$Key r5 = (kotlin.coroutines.CoroutineContext.Key) r5     // Catch:{ all -> 0x00d6 }
            kotlin.coroutines.CoroutineContext$Element r2 = r2.get(r5)     // Catch:{ all -> 0x00d6 }
            kotlinx.coroutines.Job r2 = (kotlinx.coroutines.Job) r2     // Catch:{ all -> 0x00d6 }
        L_0x00a6:
            java.lang.Object r5 = r8.tryTakeValue(r9)     // Catch:{ all -> 0x00d6 }
            kotlinx.coroutines.internal.Symbol r6 = kotlinx.coroutines.flow.SharedFlowKt.NO_VALUE     // Catch:{ all -> 0x00d6 }
            if (r5 == r6) goto L_0x00c5
            if (r2 != 0) goto L_0x00b1
            goto L_0x00b4
        L_0x00b1:
            kotlinx.coroutines.JobKt.ensureActive((kotlinx.coroutines.Job) r2)     // Catch:{ all -> 0x00d6 }
        L_0x00b4:
            r0.L$0 = r8     // Catch:{ all -> 0x00d6 }
            r0.L$1 = r10     // Catch:{ all -> 0x00d6 }
            r0.L$2 = r9     // Catch:{ all -> 0x00d6 }
            r0.L$3 = r2     // Catch:{ all -> 0x00d6 }
            r0.label = r3     // Catch:{ all -> 0x00d6 }
            java.lang.Object r5 = r10.emit(r5, r0)     // Catch:{ all -> 0x00d6 }
            if (r5 != r1) goto L_0x00a6
            return r1
        L_0x00c5:
            r0.L$0 = r8     // Catch:{ all -> 0x00d6 }
            r0.L$1 = r10     // Catch:{ all -> 0x00d6 }
            r0.L$2 = r9     // Catch:{ all -> 0x00d6 }
            r0.L$3 = r2     // Catch:{ all -> 0x00d6 }
            r0.label = r4     // Catch:{ all -> 0x00d6 }
            java.lang.Object r5 = r8.awaitValue(r9, r0)     // Catch:{ all -> 0x00d6 }
            if (r5 != r1) goto L_0x00a6
            return r1
        L_0x00d6:
            r10 = move-exception
            r5 = r8
            r8 = r10
            goto L_0x00de
        L_0x00da:
            r9 = move-exception
            r5 = r8
            r8 = r9
            r9 = r10
        L_0x00de:
            kotlinx.coroutines.flow.internal.AbstractSharedFlowSlot r9 = (kotlinx.coroutines.flow.internal.AbstractSharedFlowSlot) r9
            r5.freeSlot(r9)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.SharedFlowImpl.collect$suspendImpl(kotlinx.coroutines.flow.SharedFlowImpl, kotlinx.coroutines.flow.FlowCollector, kotlin.coroutines.Continuation):java.lang.Object");
    }

    public boolean tryEmit(T t) {
        int i;
        boolean z;
        Continuation<Unit>[] continuationArr = AbstractSharedFlowKt.EMPTY_RESUMES;
        synchronized (this) {
            i = 0;
            if (tryEmitLocked(t)) {
                continuationArr = findSlotsToResumeLocked(continuationArr);
                z = true;
            } else {
                z = false;
            }
        }
        int length = continuationArr.length;
        while (i < length) {
            Continuation<Unit> continuation = continuationArr[i];
            i++;
            if (continuation != null) {
                Result.Companion companion = Result.Companion;
                continuation.resumeWith(Result.m132constructorimpl(Unit.INSTANCE));
            }
        }
        return z;
    }

    static /* synthetic */ Object emit$suspendImpl(SharedFlowImpl sharedFlowImpl, Object obj, Continuation continuation) {
        if (sharedFlowImpl.tryEmit(obj)) {
            return Unit.INSTANCE;
        }
        Object emitSuspend = sharedFlowImpl.emitSuspend(obj, continuation);
        return emitSuspend == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? emitSuspend : Unit.INSTANCE;
    }

    /* access modifiers changed from: private */
    public final boolean tryEmitLocked(T t) {
        if (getNCollectors() == 0) {
            return tryEmitNoCollectorsLocked(t);
        }
        if (this.bufferSize >= this.bufferCapacity && this.minCollectorIndex <= this.replayIndex) {
            int i = WhenMappings.$EnumSwitchMapping$0[this.onBufferOverflow.ordinal()];
            if (i == 1) {
                return false;
            }
            if (i == 2) {
                return true;
            }
        }
        enqueueLocked(t);
        int i2 = this.bufferSize + 1;
        this.bufferSize = i2;
        if (i2 > this.bufferCapacity) {
            dropOldestLocked();
        }
        if (getReplaySize() > this.replay) {
            updateBufferLocked(this.replayIndex + 1, this.minCollectorIndex, getBufferEndIndex(), getQueueEndIndex());
        }
        return true;
    }

    private final boolean tryEmitNoCollectorsLocked(T t) {
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(getNCollectors() == 0)) {
                throw new AssertionError();
            }
        }
        if (this.replay == 0) {
            return true;
        }
        enqueueLocked(t);
        int i = this.bufferSize + 1;
        this.bufferSize = i;
        if (i > this.replay) {
            dropOldestLocked();
        }
        this.minCollectorIndex = getHead() + ((long) this.bufferSize);
        return true;
    }

    private final void dropOldestLocked() {
        Object[] objArr = this.buffer;
        Intrinsics.checkNotNull(objArr);
        SharedFlowKt.setBufferAt(objArr, getHead(), (Object) null);
        this.bufferSize--;
        long head = getHead() + 1;
        if (this.replayIndex < head) {
            this.replayIndex = head;
        }
        if (this.minCollectorIndex < head) {
            correctCollectorIndexesOnDropOldest(head);
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(getHead() == head)) {
                throw new AssertionError();
            }
        }
    }

    private final void correctCollectorIndexesOnDropOldest(long j) {
        AbstractSharedFlowSlot[] access$getSlots;
        AbstractSharedFlow abstractSharedFlow = this;
        if (!(abstractSharedFlow.nCollectors == 0 || (access$getSlots = abstractSharedFlow.slots) == null)) {
            int i = 0;
            int length = access$getSlots.length;
            while (i < length) {
                AbstractSharedFlowSlot abstractSharedFlowSlot = access$getSlots[i];
                i++;
                if (abstractSharedFlowSlot != null) {
                    SharedFlowSlot sharedFlowSlot = (SharedFlowSlot) abstractSharedFlowSlot;
                    if (sharedFlowSlot.index >= 0 && sharedFlowSlot.index < j) {
                        sharedFlowSlot.index = j;
                    }
                }
            }
        }
        this.minCollectorIndex = j;
    }

    /* access modifiers changed from: private */
    public final void enqueueLocked(Object obj) {
        int totalSize = getTotalSize();
        Object[] objArr = this.buffer;
        if (objArr == null) {
            objArr = growBuffer((Object[]) null, 0, 2);
        } else if (totalSize >= objArr.length) {
            objArr = growBuffer(objArr, totalSize, objArr.length * 2);
        }
        SharedFlowKt.setBufferAt(objArr, getHead() + ((long) totalSize), obj);
    }

    private final Object[] growBuffer(Object[] objArr, int i, int i2) {
        if (i2 > 0) {
            Object[] objArr2 = new Object[i2];
            this.buffer = objArr2;
            if (objArr == null) {
                return objArr2;
            }
            long head = getHead();
            for (int i3 = 0; i3 < i; i3++) {
                long j = ((long) i3) + head;
                SharedFlowKt.setBufferAt(objArr2, j, SharedFlowKt.getBufferAt(objArr, j));
            }
            return objArr2;
        }
        throw new IllegalStateException("Buffer size overflow".toString());
    }

    public final long updateNewCollectorIndexLocked$kotlinx_coroutines_core() {
        long j = this.replayIndex;
        if (j < this.minCollectorIndex) {
            this.minCollectorIndex = j;
        }
        return j;
    }

    public final Continuation<Unit>[] updateCollectorIndexLocked$kotlinx_coroutines_core(long j) {
        int i;
        long j2;
        AbstractSharedFlowSlot[] access$getSlots;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(j >= this.minCollectorIndex)) {
                throw new AssertionError();
            }
        }
        if (j > this.minCollectorIndex) {
            return AbstractSharedFlowKt.EMPTY_RESUMES;
        }
        long head = getHead();
        long j3 = ((long) this.bufferSize) + head;
        long j4 = 1;
        if (this.bufferCapacity == 0 && this.queueSize > 0) {
            j3++;
        }
        AbstractSharedFlow abstractSharedFlow = this;
        if (!(abstractSharedFlow.nCollectors == 0 || (access$getSlots = abstractSharedFlow.slots) == null)) {
            int length = access$getSlots.length;
            int i2 = 0;
            while (i2 < length) {
                AbstractSharedFlowSlot abstractSharedFlowSlot = access$getSlots[i2];
                i2++;
                if (abstractSharedFlowSlot != null) {
                    SharedFlowSlot sharedFlowSlot = (SharedFlowSlot) abstractSharedFlowSlot;
                    if (sharedFlowSlot.index >= 0 && sharedFlowSlot.index < j3) {
                        j3 = sharedFlowSlot.index;
                    }
                }
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(j3 >= this.minCollectorIndex)) {
                throw new AssertionError();
            }
        }
        if (j3 <= this.minCollectorIndex) {
            return AbstractSharedFlowKt.EMPTY_RESUMES;
        }
        long bufferEndIndex = getBufferEndIndex();
        if (getNCollectors() > 0) {
            i = Math.min(this.queueSize, this.bufferCapacity - ((int) (bufferEndIndex - j3)));
        } else {
            i = this.queueSize;
        }
        Continuation<Unit>[] continuationArr = AbstractSharedFlowKt.EMPTY_RESUMES;
        long j5 = ((long) this.queueSize) + bufferEndIndex;
        if (i > 0) {
            continuationArr = new Continuation[i];
            Object[] objArr = this.buffer;
            Intrinsics.checkNotNull(objArr);
            long j6 = bufferEndIndex;
            int i3 = 0;
            while (true) {
                if (bufferEndIndex >= j5) {
                    j2 = j3;
                    break;
                }
                long j7 = bufferEndIndex + j4;
                Object access$getBufferAt = SharedFlowKt.getBufferAt(objArr, bufferEndIndex);
                if (access$getBufferAt != SharedFlowKt.NO_VALUE) {
                    Objects.requireNonNull(access$getBufferAt, "null cannot be cast to non-null type kotlinx.coroutines.flow.SharedFlowImpl.Emitter");
                    Emitter emitter = (Emitter) access$getBufferAt;
                    int i4 = i3 + 1;
                    j2 = j3;
                    continuationArr[i3] = emitter.cont;
                    SharedFlowKt.setBufferAt(objArr, bufferEndIndex, SharedFlowKt.NO_VALUE);
                    SharedFlowKt.setBufferAt(objArr, j6, emitter.value);
                    j6++;
                    if (i4 >= i) {
                        break;
                    }
                    i3 = i4;
                    bufferEndIndex = j7;
                    j3 = j2;
                } else {
                    bufferEndIndex = j7;
                }
                j4 = 1;
            }
            bufferEndIndex = j6;
        } else {
            j2 = j3;
        }
        int i5 = (int) (bufferEndIndex - head);
        long j8 = getNCollectors() == 0 ? bufferEndIndex : j2;
        long max = Math.max(this.replayIndex, bufferEndIndex - ((long) Math.min(this.replay, i5)));
        if (this.bufferCapacity == 0 && max < j5) {
            Object[] objArr2 = this.buffer;
            Intrinsics.checkNotNull(objArr2);
            if (Intrinsics.areEqual(SharedFlowKt.getBufferAt(objArr2, max), (Object) SharedFlowKt.NO_VALUE)) {
                bufferEndIndex++;
                max++;
            }
        }
        updateBufferLocked(max, j8, bufferEndIndex, j5);
        cleanupTailLocked();
        return true ^ (continuationArr.length == 0) ? findSlotsToResumeLocked(continuationArr) : continuationArr;
    }

    private final void updateBufferLocked(long j, long j2, long j3, long j4) {
        long j5 = j;
        long j6 = j2;
        long min = Math.min(j6, j5);
        boolean z = true;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(min >= getHead())) {
                throw new AssertionError();
            }
        }
        for (long head = getHead(); head < min; head = 1 + head) {
            Object[] objArr = this.buffer;
            Intrinsics.checkNotNull(objArr);
            SharedFlowKt.setBufferAt(objArr, head, (Object) null);
        }
        this.replayIndex = j5;
        this.minCollectorIndex = j6;
        this.bufferSize = (int) (j3 - min);
        this.queueSize = (int) (j4 - j3);
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(this.bufferSize >= 0)) {
                throw new AssertionError();
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(this.queueSize >= 0)) {
                throw new AssertionError();
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (this.replayIndex > getHead() + ((long) this.bufferSize)) {
                z = false;
            }
            if (!z) {
                throw new AssertionError();
            }
        }
    }

    private final void cleanupTailLocked() {
        if (this.bufferCapacity != 0 || this.queueSize > 1) {
            Object[] objArr = this.buffer;
            Intrinsics.checkNotNull(objArr);
            while (this.queueSize > 0 && SharedFlowKt.getBufferAt(objArr, (getHead() + ((long) getTotalSize())) - 1) == SharedFlowKt.NO_VALUE) {
                this.queueSize--;
                SharedFlowKt.setBufferAt(objArr, getHead() + ((long) getTotalSize()), (Object) null);
            }
        }
    }

    private final Object tryTakeValue(SharedFlowSlot sharedFlowSlot) {
        Object obj;
        Continuation<Unit>[] continuationArr = AbstractSharedFlowKt.EMPTY_RESUMES;
        synchronized (this) {
            long tryPeekLocked = tryPeekLocked(sharedFlowSlot);
            if (tryPeekLocked < 0) {
                obj = SharedFlowKt.NO_VALUE;
            } else {
                long j = sharedFlowSlot.index;
                Object peekedValueLockedAt = getPeekedValueLockedAt(tryPeekLocked);
                sharedFlowSlot.index = tryPeekLocked + 1;
                Object obj2 = peekedValueLockedAt;
                continuationArr = updateCollectorIndexLocked$kotlinx_coroutines_core(j);
                obj = obj2;
            }
        }
        int i = 0;
        int length = continuationArr.length;
        while (i < length) {
            Continuation<Unit> continuation = continuationArr[i];
            i++;
            if (continuation != null) {
                Result.Companion companion = Result.Companion;
                continuation.resumeWith(Result.m132constructorimpl(Unit.INSTANCE));
            }
        }
        return obj;
    }

    /* access modifiers changed from: private */
    public final long tryPeekLocked(SharedFlowSlot sharedFlowSlot) {
        long j = sharedFlowSlot.index;
        if (j < getBufferEndIndex()) {
            return j;
        }
        if (this.bufferCapacity <= 0 && j <= getHead() && this.queueSize != 0) {
            return j;
        }
        return -1;
    }

    private final Object getPeekedValueLockedAt(long j) {
        Object[] objArr = this.buffer;
        Intrinsics.checkNotNull(objArr);
        Object access$getBufferAt = SharedFlowKt.getBufferAt(objArr, j);
        return access$getBufferAt instanceof Emitter ? ((Emitter) access$getBufferAt).value : access$getBufferAt;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: kotlin.coroutines.Continuation<kotlin.Unit>[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0014, code lost:
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0014, code lost:
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001c, code lost:
        r4 = (kotlinx.coroutines.flow.SharedFlowSlot) r4;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=kotlin.coroutines.Continuation<kotlin.Unit>[], code=kotlin.coroutines.Continuation[], for r11v0, types: [kotlin.coroutines.Continuation<kotlin.Unit>[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final kotlin.coroutines.Continuation<kotlin.Unit>[] findSlotsToResumeLocked(kotlin.coroutines.Continuation[] r11) {
        /*
            r10 = this;
            int r0 = r11.length
            r1 = r10
            kotlinx.coroutines.flow.internal.AbstractSharedFlow r1 = (kotlinx.coroutines.flow.internal.AbstractSharedFlow) r1
            int r2 = r1.nCollectors
            if (r2 != 0) goto L_0x000b
            goto L_0x0050
        L_0x000b:
            kotlinx.coroutines.flow.internal.AbstractSharedFlowSlot[] r1 = r1.slots
            if (r1 != 0) goto L_0x0012
            goto L_0x0050
        L_0x0012:
            r2 = 0
            int r3 = r1.length
        L_0x0014:
            if (r2 >= r3) goto L_0x0050
            r4 = r1[r2]
            int r2 = r2 + 1
            if (r4 == 0) goto L_0x0014
            kotlinx.coroutines.flow.SharedFlowSlot r4 = (kotlinx.coroutines.flow.SharedFlowSlot) r4
            kotlin.coroutines.Continuation<? super kotlin.Unit> r5 = r4.cont
            if (r5 != 0) goto L_0x0023
            goto L_0x0014
        L_0x0023:
            long r6 = r10.tryPeekLocked(r4)
            r8 = 0
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 >= 0) goto L_0x002e
            goto L_0x0014
        L_0x002e:
            r6 = r11
            java.lang.Object[] r6 = (java.lang.Object[]) r6
            int r7 = r6.length
            if (r0 < r7) goto L_0x0044
            int r11 = r6.length
            r7 = 2
            int r11 = r11 * r7
            int r11 = java.lang.Math.max(r7, r11)
            java.lang.Object[] r11 = java.util.Arrays.copyOf(r6, r11)
            java.lang.String r6 = "copyOf(this, newSize)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r11, r6)
        L_0x0044:
            r6 = r11
            kotlin.coroutines.Continuation[] r6 = (kotlin.coroutines.Continuation[]) r6
            int r7 = r0 + 1
            r6[r0] = r5
            r0 = 0
            r4.cont = r0
            r0 = r7
            goto L_0x0014
        L_0x0050:
            kotlin.coroutines.Continuation[] r11 = (kotlin.coroutines.Continuation[]) r11
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.SharedFlowImpl.findSlotsToResumeLocked(kotlin.coroutines.Continuation[]):kotlin.coroutines.Continuation[]");
    }

    /* access modifiers changed from: protected */
    public SharedFlowSlot createSlot() {
        return new SharedFlowSlot();
    }

    /* access modifiers changed from: protected */
    public SharedFlowSlot[] createSlotArray(int i) {
        return new SharedFlowSlot[i];
    }

    public Flow<T> fuse(CoroutineContext coroutineContext, int i, BufferOverflow bufferOverflow) {
        return SharedFlowKt.fuseSharedFlow(this, coroutineContext, i, bufferOverflow);
    }

    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B1\u0012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0002\u0010\u000bJ\b\u0010\f\u001a\u00020\nH\u0016R\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t8\u0006X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u00038\u0006X\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lkotlinx/coroutines/flow/SharedFlowImpl$Emitter;", "Lkotlinx/coroutines/DisposableHandle;", "flow", "Lkotlinx/coroutines/flow/SharedFlowImpl;", "index", "", "value", "", "cont", "Lkotlin/coroutines/Continuation;", "", "(Lkotlinx/coroutines/flow/SharedFlowImpl;JLjava/lang/Object;Lkotlin/coroutines/Continuation;)V", "dispose", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: SharedFlow.kt */
    private static final class Emitter implements DisposableHandle {
        public final Continuation<Unit> cont;
        public final SharedFlowImpl<?> flow;
        public long index;
        public final Object value;

        public Emitter(SharedFlowImpl<?> sharedFlowImpl, long j, Object obj, Continuation<? super Unit> continuation) {
            this.flow = sharedFlowImpl;
            this.index = j;
            this.value = obj;
            this.cont = continuation;
        }

        public void dispose() {
            this.flow.cancelEmitter(this);
        }
    }

    public List<T> getReplayCache() {
        synchronized (this) {
            int replaySize = getReplaySize();
            if (replaySize == 0) {
                List<T> emptyList = CollectionsKt.emptyList();
                return emptyList;
            }
            ArrayList arrayList = new ArrayList(replaySize);
            Object[] objArr = this.buffer;
            Intrinsics.checkNotNull(objArr);
            int i = 0;
            while (i < replaySize) {
                int i2 = i + 1;
                arrayList.add(SharedFlowKt.getBufferAt(objArr, this.replayIndex + ((long) i)));
                i = i2;
            }
            return arrayList;
        }
    }

    /* access modifiers changed from: private */
    public final Object emitSuspend(T t, Continuation<? super Unit> continuation) {
        Continuation<Unit>[] continuationArr;
        Emitter emitter;
        CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 1);
        cancellableContinuationImpl.initCancellability();
        CancellableContinuation cancellableContinuation = cancellableContinuationImpl;
        Continuation<Unit>[] continuationArr2 = AbstractSharedFlowKt.EMPTY_RESUMES;
        synchronized (this) {
            if (tryEmitLocked(t)) {
                Result.Companion companion = Result.Companion;
                cancellableContinuation.resumeWith(Result.m132constructorimpl(Unit.INSTANCE));
                continuationArr = findSlotsToResumeLocked(continuationArr2);
                emitter = null;
            } else {
                Emitter emitter2 = new Emitter(this, ((long) getTotalSize()) + getHead(), t, cancellableContinuation);
                enqueueLocked(emitter2);
                this.queueSize = this.queueSize + 1;
                if (this.bufferCapacity == 0) {
                    continuationArr2 = findSlotsToResumeLocked(continuationArr2);
                }
                continuationArr = continuationArr2;
                emitter = emitter2;
            }
        }
        if (emitter != null) {
            CancellableContinuationKt.disposeOnCancellation(cancellableContinuation, emitter);
        }
        int i = 0;
        int length = continuationArr.length;
        while (i < length) {
            Continuation<Unit> continuation2 = continuationArr[i];
            i++;
            if (continuation2 != null) {
                Result.Companion companion2 = Result.Companion;
                continuation2.resumeWith(Result.m132constructorimpl(Unit.INSTANCE));
            }
        }
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    /* access modifiers changed from: private */
    public final void cancelEmitter(Emitter emitter) {
        synchronized (this) {
            if (emitter.index >= getHead()) {
                Object[] objArr = this.buffer;
                Intrinsics.checkNotNull(objArr);
                if (SharedFlowKt.getBufferAt(objArr, emitter.index) == emitter) {
                    SharedFlowKt.setBufferAt(objArr, emitter.index, SharedFlowKt.NO_VALUE);
                    cleanupTailLocked();
                    Unit unit = Unit.INSTANCE;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public final Object awaitValue(SharedFlowSlot sharedFlowSlot, Continuation<? super Unit> continuation) {
        CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 1);
        cancellableContinuationImpl.initCancellability();
        CancellableContinuation cancellableContinuation = cancellableContinuationImpl;
        synchronized (this) {
            if (tryPeekLocked(sharedFlowSlot) < 0) {
                sharedFlowSlot.cont = cancellableContinuation;
                sharedFlowSlot.cont = cancellableContinuation;
            } else {
                Result.Companion companion = Result.Companion;
                cancellableContinuation.resumeWith(Result.m132constructorimpl(Unit.INSTANCE));
            }
            Unit unit = Unit.INSTANCE;
        }
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    public void resetReplayCache() {
        synchronized (this) {
            updateBufferLocked(getBufferEndIndex(), this.minCollectorIndex, getBufferEndIndex(), getQueueEndIndex());
            Unit unit = Unit.INSTANCE;
        }
    }
}
