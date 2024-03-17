package kotlinx.coroutines.sync;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationKt;

@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\b\u0004\n\u0002\u0018\u0002\b\u0002\u0018\u00002\u00020\u001eB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0001¢\u0006\u0004\b\u0004\u0010\u0005J\u0013\u0010\u0007\u001a\u00020\u0006H@ø\u0001\u0000¢\u0006\u0004\b\u0007\u0010\bJ\u0013\u0010\t\u001a\u00020\u0006H@ø\u0001\u0000¢\u0006\u0004\b\t\u0010\bJ\u001d\u0010\r\u001a\u00020\f2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\nH\u0002¢\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u000f\u001a\u00020\u0006H\u0016¢\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0011\u001a\u00020\fH\u0016¢\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\fH\u0002¢\u0006\u0004\b\u0013\u0010\u0012J\u0019\u0010\u0014\u001a\u00020\f*\b\u0012\u0004\u0012\u00020\u00060\nH\u0002¢\u0006\u0004\b\u0014\u0010\u000eR\u0014\u0010\u0017\u001a\u00020\u00018VX\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R \u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00060\u00188\u0002X\u0004¢\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0014\u0010\u0002\u001a\u00020\u00018\u0002X\u0004¢\u0006\u0006\n\u0004\b\u0002\u0010\u001c\u0002\u0004\n\u0002\b\u0019¨\u0006\u001d"}, d2 = {"Lkotlinx/coroutines/sync/SemaphoreImpl;", "", "permits", "acquiredPermits", "<init>", "(II)V", "", "acquire", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "acquireSlowPath", "Lkotlinx/coroutines/CancellableContinuation;", "cont", "", "addAcquireToQueue", "(Lkotlinx/coroutines/CancellableContinuation;)Z", "release", "()V", "tryAcquire", "()Z", "tryResumeNextFromQueue", "tryResumeAcquire", "getAvailablePermits", "()I", "availablePermits", "Lkotlin/Function1;", "", "onCancellationRelease", "Lkotlin/jvm/functions/Function1;", "I", "kotlinx-coroutines-core", "Lkotlinx/coroutines/sync/Semaphore;"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Semaphore.kt */
final class SemaphoreImpl implements Semaphore {
    static final /* synthetic */ AtomicIntegerFieldUpdater _availablePermits$FU;
    private static final /* synthetic */ AtomicLongFieldUpdater deqIdx$FU;
    private static final /* synthetic */ AtomicLongFieldUpdater enqIdx$FU;
    private static final /* synthetic */ AtomicReferenceFieldUpdater head$FU;
    private static final /* synthetic */ AtomicReferenceFieldUpdater tail$FU;
    volatile /* synthetic */ int _availablePermits;
    private volatile /* synthetic */ long deqIdx = 0;
    private volatile /* synthetic */ long enqIdx = 0;
    private volatile /* synthetic */ Object head;
    /* access modifiers changed from: private */
    public final Function1<Throwable, Unit> onCancellationRelease;
    private final int permits;
    private volatile /* synthetic */ Object tail;

    static {
        Class<SemaphoreImpl> cls = SemaphoreImpl.class;
        head$FU = AtomicReferenceFieldUpdater.newUpdater(cls, Object.class, "head");
        deqIdx$FU = AtomicLongFieldUpdater.newUpdater(cls, "deqIdx");
        tail$FU = AtomicReferenceFieldUpdater.newUpdater(cls, Object.class, "tail");
        enqIdx$FU = AtomicLongFieldUpdater.newUpdater(cls, "enqIdx");
        _availablePermits$FU = AtomicIntegerFieldUpdater.newUpdater(cls, "_availablePermits");
    }

    public SemaphoreImpl(int i, int i2) {
        this.permits = i;
        boolean z = true;
        if (i > 0) {
            if ((i2 < 0 || i2 > i) ? false : z) {
                SemaphoreSegment semaphoreSegment = new SemaphoreSegment(0, (SemaphoreSegment) null, 2);
                this.head = semaphoreSegment;
                this.tail = semaphoreSegment;
                this._availablePermits = i - i2;
                this.onCancellationRelease = new SemaphoreImpl$onCancellationRelease$1(this);
                return;
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("The number of acquired permits should be in 0..", Integer.valueOf(i)).toString());
        }
        throw new IllegalArgumentException(Intrinsics.stringPlus("Semaphore should have at least 1 permit, but had ", Integer.valueOf(i)).toString());
    }

    public int getAvailablePermits() {
        return Math.max(this._availablePermits, 0);
    }

    public Object acquire(Continuation<? super Unit> continuation) {
        if (_availablePermits$FU.getAndDecrement(this) > 0) {
            return Unit.INSTANCE;
        }
        Object acquireSlowPath = acquireSlowPath(continuation);
        return acquireSlowPath == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? acquireSlowPath : Unit.INSTANCE;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0059, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0075, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean addAcquireToQueue(kotlinx.coroutines.CancellableContinuation<? super kotlin.Unit> r15) {
        /*
            r14 = this;
            java.lang.Object r0 = r14.tail
            kotlinx.coroutines.sync.SemaphoreSegment r0 = (kotlinx.coroutines.sync.SemaphoreSegment) r0
            java.util.concurrent.atomic.AtomicLongFieldUpdater r1 = enqIdx$FU
            long r1 = r1.getAndIncrement(r14)
            int r3 = kotlinx.coroutines.sync.SemaphoreKt.SEGMENT_SIZE
            long r3 = (long) r3
            long r3 = r1 / r3
        L_0x0011:
            r5 = r0
            kotlinx.coroutines.internal.Segment r5 = (kotlinx.coroutines.internal.Segment) r5
        L_0x0014:
            long r6 = r5.getId()
            int r6 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r6 < 0) goto L_0x0028
            boolean r6 = r5.getRemoved()
            if (r6 == 0) goto L_0x0023
            goto L_0x0028
        L_0x0023:
            java.lang.Object r5 = kotlinx.coroutines.internal.SegmentOrClosed.m1623constructorimpl(r5)
            goto L_0x003d
        L_0x0028:
            r6 = r5
            kotlinx.coroutines.internal.ConcurrentLinkedListNode r6 = (kotlinx.coroutines.internal.ConcurrentLinkedListNode) r6
            java.lang.Object r6 = r6.getNextOrClosed()
            kotlinx.coroutines.internal.Symbol r7 = kotlinx.coroutines.internal.ConcurrentLinkedListKt.CLOSED
            if (r6 != r7) goto L_0x00db
            kotlinx.coroutines.internal.Symbol r5 = kotlinx.coroutines.internal.ConcurrentLinkedListKt.CLOSED
            java.lang.Object r5 = kotlinx.coroutines.internal.SegmentOrClosed.m1623constructorimpl(r5)
        L_0x003d:
            boolean r6 = kotlinx.coroutines.internal.SegmentOrClosed.m1628isClosedimpl(r5)
            r7 = 0
            r8 = 1
            if (r6 != 0) goto L_0x0082
            kotlinx.coroutines.internal.Segment r6 = kotlinx.coroutines.internal.SegmentOrClosed.m1626getSegmentimpl(r5)
        L_0x0049:
            java.lang.Object r9 = r14.tail
            kotlinx.coroutines.internal.Segment r9 = (kotlinx.coroutines.internal.Segment) r9
            long r10 = r9.getId()
            long r12 = r6.getId()
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 < 0) goto L_0x005b
        L_0x0059:
            r6 = r8
            goto L_0x0075
        L_0x005b:
            boolean r10 = r6.tryIncPointers$kotlinx_coroutines_core()
            if (r10 != 0) goto L_0x0063
            r6 = r7
            goto L_0x0075
        L_0x0063:
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r10 = tail$FU
            boolean r10 = r10.compareAndSet(r14, r9, r6)
            if (r10 == 0) goto L_0x0078
            boolean r6 = r9.decPointers$kotlinx_coroutines_core()
            if (r6 == 0) goto L_0x0059
            r9.remove()
            goto L_0x0059
        L_0x0075:
            if (r6 == 0) goto L_0x0011
            goto L_0x0082
        L_0x0078:
            boolean r9 = r6.decPointers$kotlinx_coroutines_core()
            if (r9 == 0) goto L_0x0049
            r6.remove()
            goto L_0x0049
        L_0x0082:
            kotlinx.coroutines.internal.Segment r0 = kotlinx.coroutines.internal.SegmentOrClosed.m1626getSegmentimpl(r5)
            kotlinx.coroutines.sync.SemaphoreSegment r0 = (kotlinx.coroutines.sync.SemaphoreSegment) r0
            int r3 = kotlinx.coroutines.sync.SemaphoreKt.SEGMENT_SIZE
            long r3 = (long) r3
            long r1 = r1 % r3
            int r1 = (int) r1
            r2 = 0
            java.util.concurrent.atomic.AtomicReferenceArray r3 = r0.acquirers
            boolean r2 = r3.compareAndSet(r1, r2, r15)
            if (r2 == 0) goto L_0x00a5
            kotlinx.coroutines.sync.CancelSemaphoreAcquisitionHandler r2 = new kotlinx.coroutines.sync.CancelSemaphoreAcquisitionHandler
            r2.<init>(r0, r1)
            kotlinx.coroutines.CancelHandlerBase r2 = (kotlinx.coroutines.CancelHandlerBase) r2
            kotlin.jvm.functions.Function1 r2 = (kotlin.jvm.functions.Function1) r2
            r15.invokeOnCancellation(r2)
            return r8
        L_0x00a5:
            kotlinx.coroutines.internal.Symbol r2 = kotlinx.coroutines.sync.SemaphoreKt.PERMIT
            kotlinx.coroutines.internal.Symbol r3 = kotlinx.coroutines.sync.SemaphoreKt.TAKEN
            java.util.concurrent.atomic.AtomicReferenceArray r4 = r0.acquirers
            boolean r2 = r4.compareAndSet(r1, r2, r3)
            if (r2 == 0) goto L_0x00bd
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            kotlin.jvm.functions.Function1<java.lang.Throwable, kotlin.Unit> r1 = r14.onCancellationRelease
            r15.resume(r0, r1)
            return r8
        L_0x00bd:
            boolean r15 = kotlinx.coroutines.DebugKt.getASSERTIONS_ENABLED()
            if (r15 == 0) goto L_0x00da
            java.util.concurrent.atomic.AtomicReferenceArray r15 = r0.acquirers
            java.lang.Object r15 = r15.get(r1)
            kotlinx.coroutines.internal.Symbol r0 = kotlinx.coroutines.sync.SemaphoreKt.BROKEN
            if (r15 != r0) goto L_0x00d0
            goto L_0x00d1
        L_0x00d0:
            r8 = r7
        L_0x00d1:
            if (r8 == 0) goto L_0x00d4
            goto L_0x00da
        L_0x00d4:
            java.lang.AssertionError r15 = new java.lang.AssertionError
            r15.<init>()
            throw r15
        L_0x00da:
            return r7
        L_0x00db:
            kotlinx.coroutines.internal.ConcurrentLinkedListNode r6 = (kotlinx.coroutines.internal.ConcurrentLinkedListNode) r6
            kotlinx.coroutines.internal.Segment r6 = (kotlinx.coroutines.internal.Segment) r6
            if (r6 == 0) goto L_0x00e4
        L_0x00e1:
            r5 = r6
            goto L_0x0014
        L_0x00e4:
            long r6 = r5.getId()
            r8 = 1
            long r6 = r6 + r8
            r8 = r5
            kotlinx.coroutines.sync.SemaphoreSegment r8 = (kotlinx.coroutines.sync.SemaphoreSegment) r8
            kotlinx.coroutines.sync.SemaphoreSegment r6 = kotlinx.coroutines.sync.SemaphoreKt.createSegment(r6, r8)
            kotlinx.coroutines.internal.Segment r6 = (kotlinx.coroutines.internal.Segment) r6
            r7 = r6
            kotlinx.coroutines.internal.ConcurrentLinkedListNode r7 = (kotlinx.coroutines.internal.ConcurrentLinkedListNode) r7
            boolean r7 = r5.trySetNext(r7)
            if (r7 == 0) goto L_0x0014
            boolean r7 = r5.getRemoved()
            if (r7 == 0) goto L_0x00e1
            r5.remove()
            goto L_0x00e1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.sync.SemaphoreImpl.addAcquireToQueue(kotlinx.coroutines.CancellableContinuation):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0059, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0075, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean tryResumeNextFromQueue() {
        /*
            r14 = this;
            java.lang.Object r0 = r14.head
            kotlinx.coroutines.sync.SemaphoreSegment r0 = (kotlinx.coroutines.sync.SemaphoreSegment) r0
            java.util.concurrent.atomic.AtomicLongFieldUpdater r1 = deqIdx$FU
            long r1 = r1.getAndIncrement(r14)
            int r3 = kotlinx.coroutines.sync.SemaphoreKt.SEGMENT_SIZE
            long r3 = (long) r3
            long r3 = r1 / r3
        L_0x0011:
            r5 = r0
            kotlinx.coroutines.internal.Segment r5 = (kotlinx.coroutines.internal.Segment) r5
        L_0x0014:
            long r6 = r5.getId()
            int r6 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r6 < 0) goto L_0x0028
            boolean r6 = r5.getRemoved()
            if (r6 == 0) goto L_0x0023
            goto L_0x0028
        L_0x0023:
            java.lang.Object r5 = kotlinx.coroutines.internal.SegmentOrClosed.m1623constructorimpl(r5)
            goto L_0x003d
        L_0x0028:
            r6 = r5
            kotlinx.coroutines.internal.ConcurrentLinkedListNode r6 = (kotlinx.coroutines.internal.ConcurrentLinkedListNode) r6
            java.lang.Object r6 = r6.getNextOrClosed()
            kotlinx.coroutines.internal.Symbol r7 = kotlinx.coroutines.internal.ConcurrentLinkedListKt.CLOSED
            if (r6 != r7) goto L_0x00da
            kotlinx.coroutines.internal.Symbol r5 = kotlinx.coroutines.internal.ConcurrentLinkedListKt.CLOSED
            java.lang.Object r5 = kotlinx.coroutines.internal.SegmentOrClosed.m1623constructorimpl(r5)
        L_0x003d:
            boolean r6 = kotlinx.coroutines.internal.SegmentOrClosed.m1628isClosedimpl(r5)
            r7 = 0
            r8 = 1
            if (r6 != 0) goto L_0x0082
            kotlinx.coroutines.internal.Segment r6 = kotlinx.coroutines.internal.SegmentOrClosed.m1626getSegmentimpl(r5)
        L_0x0049:
            java.lang.Object r9 = r14.head
            kotlinx.coroutines.internal.Segment r9 = (kotlinx.coroutines.internal.Segment) r9
            long r10 = r9.getId()
            long r12 = r6.getId()
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 < 0) goto L_0x005b
        L_0x0059:
            r6 = r8
            goto L_0x0075
        L_0x005b:
            boolean r10 = r6.tryIncPointers$kotlinx_coroutines_core()
            if (r10 != 0) goto L_0x0063
            r6 = r7
            goto L_0x0075
        L_0x0063:
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r10 = head$FU
            boolean r10 = r10.compareAndSet(r14, r9, r6)
            if (r10 == 0) goto L_0x0078
            boolean r6 = r9.decPointers$kotlinx_coroutines_core()
            if (r6 == 0) goto L_0x0059
            r9.remove()
            goto L_0x0059
        L_0x0075:
            if (r6 == 0) goto L_0x0011
            goto L_0x0082
        L_0x0078:
            boolean r9 = r6.decPointers$kotlinx_coroutines_core()
            if (r9 == 0) goto L_0x0049
            r6.remove()
            goto L_0x0049
        L_0x0082:
            kotlinx.coroutines.internal.Segment r0 = kotlinx.coroutines.internal.SegmentOrClosed.m1626getSegmentimpl(r5)
            kotlinx.coroutines.sync.SemaphoreSegment r0 = (kotlinx.coroutines.sync.SemaphoreSegment) r0
            r0.cleanPrev()
            long r5 = r0.getId()
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0094
            return r7
        L_0x0094:
            int r3 = kotlinx.coroutines.sync.SemaphoreKt.SEGMENT_SIZE
            long r3 = (long) r3
            long r1 = r1 % r3
            int r1 = (int) r1
            kotlinx.coroutines.internal.Symbol r2 = kotlinx.coroutines.sync.SemaphoreKt.PERMIT
            java.util.concurrent.atomic.AtomicReferenceArray r3 = r0.acquirers
            java.lang.Object r2 = r3.getAndSet(r1, r2)
            if (r2 != 0) goto L_0x00cc
            int r2 = kotlinx.coroutines.sync.SemaphoreKt.MAX_SPIN_CYCLES
        L_0x00ab:
            if (r7 >= r2) goto L_0x00bc
            int r7 = r7 + 1
            java.util.concurrent.atomic.AtomicReferenceArray r3 = r0.acquirers
            java.lang.Object r3 = r3.get(r1)
            kotlinx.coroutines.internal.Symbol r4 = kotlinx.coroutines.sync.SemaphoreKt.TAKEN
            if (r3 != r4) goto L_0x00ab
            return r8
        L_0x00bc:
            kotlinx.coroutines.internal.Symbol r2 = kotlinx.coroutines.sync.SemaphoreKt.PERMIT
            kotlinx.coroutines.internal.Symbol r3 = kotlinx.coroutines.sync.SemaphoreKt.BROKEN
            java.util.concurrent.atomic.AtomicReferenceArray r0 = r0.acquirers
            boolean r0 = r0.compareAndSet(r1, r2, r3)
            r0 = r0 ^ r8
            return r0
        L_0x00cc:
            kotlinx.coroutines.internal.Symbol r0 = kotlinx.coroutines.sync.SemaphoreKt.CANCELLED
            if (r2 != r0) goto L_0x00d3
            return r7
        L_0x00d3:
            kotlinx.coroutines.CancellableContinuation r2 = (kotlinx.coroutines.CancellableContinuation) r2
            boolean r0 = r14.tryResumeAcquire(r2)
            return r0
        L_0x00da:
            kotlinx.coroutines.internal.ConcurrentLinkedListNode r6 = (kotlinx.coroutines.internal.ConcurrentLinkedListNode) r6
            kotlinx.coroutines.internal.Segment r6 = (kotlinx.coroutines.internal.Segment) r6
            if (r6 == 0) goto L_0x00e3
        L_0x00e0:
            r5 = r6
            goto L_0x0014
        L_0x00e3:
            long r6 = r5.getId()
            r8 = 1
            long r6 = r6 + r8
            r8 = r5
            kotlinx.coroutines.sync.SemaphoreSegment r8 = (kotlinx.coroutines.sync.SemaphoreSegment) r8
            kotlinx.coroutines.sync.SemaphoreSegment r6 = kotlinx.coroutines.sync.SemaphoreKt.createSegment(r6, r8)
            kotlinx.coroutines.internal.Segment r6 = (kotlinx.coroutines.internal.Segment) r6
            r7 = r6
            kotlinx.coroutines.internal.ConcurrentLinkedListNode r7 = (kotlinx.coroutines.internal.ConcurrentLinkedListNode) r7
            boolean r7 = r5.trySetNext(r7)
            if (r7 == 0) goto L_0x0014
            boolean r7 = r5.getRemoved()
            if (r7 == 0) goto L_0x00e0
            r5.remove()
            goto L_0x00e0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.sync.SemaphoreImpl.tryResumeNextFromQueue():boolean");
    }

    private final boolean tryResumeAcquire(CancellableContinuation<? super Unit> cancellableContinuation) {
        Object tryResume = cancellableContinuation.tryResume(Unit.INSTANCE, (Object) null, this.onCancellationRelease);
        if (tryResume == null) {
            return false;
        }
        cancellableContinuation.completeResume(tryResume);
        return true;
    }

    public boolean tryAcquire() {
        int i;
        do {
            i = this._availablePermits;
            if (i <= 0) {
                return false;
            }
        } while (!_availablePermits$FU.compareAndSet(this, i, i - 1));
        return true;
    }

    /* access modifiers changed from: private */
    public final Object acquireSlowPath(Continuation<? super Unit> continuation) {
        CancellableContinuationImpl<? super Unit> orCreateCancellableContinuation = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(continuation));
        CancellableContinuation cancellableContinuation = orCreateCancellableContinuation;
        while (true) {
            if (!addAcquireToQueue(cancellableContinuation)) {
                if (_availablePermits$FU.getAndDecrement(this) > 0) {
                    cancellableContinuation.resume(Unit.INSTANCE, this.onCancellationRelease);
                    break;
                }
            } else {
                break;
            }
        }
        Object result = orCreateCancellableContinuation.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
    }

    public void release() {
        while (true) {
            int i = this._availablePermits;
            int i2 = this.permits;
            if (i < i2) {
                if (_availablePermits$FU.compareAndSet(this, i, i + 1) && (i >= 0 || tryResumeNextFromQueue())) {
                    return;
                }
            } else {
                throw new IllegalStateException(Intrinsics.stringPlus("The number of released permits cannot be greater than ", Integer.valueOf(i2)).toString());
            }
        }
    }
}
