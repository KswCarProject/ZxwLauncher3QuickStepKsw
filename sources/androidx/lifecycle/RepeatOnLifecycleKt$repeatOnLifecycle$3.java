package androidx.lifecycle;

import androidx.lifecycle.Lifecycle;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 6, 0}, xi = 48)
@DebugMetadata(c = "androidx.lifecycle.RepeatOnLifecycleKt$repeatOnLifecycle$3", f = "RepeatOnLifecycle.kt", i = {}, l = {84}, m = "invokeSuspend", n = {}, s = {})
/* compiled from: RepeatOnLifecycle.kt */
final class RepeatOnLifecycleKt$repeatOnLifecycle$3 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ Function2<CoroutineScope, Continuation<? super Unit>, Object> $block;
    final /* synthetic */ Lifecycle.State $state;
    final /* synthetic */ Lifecycle $this_repeatOnLifecycle;
    private /* synthetic */ Object L$0;
    int label;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    RepeatOnLifecycleKt$repeatOnLifecycle$3(Lifecycle lifecycle, Lifecycle.State state, Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object> function2, Continuation<? super RepeatOnLifecycleKt$repeatOnLifecycle$3> continuation) {
        super(2, continuation);
        this.$this_repeatOnLifecycle = lifecycle;
        this.$state = state;
        this.$block = function2;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        RepeatOnLifecycleKt$repeatOnLifecycle$3 repeatOnLifecycleKt$repeatOnLifecycle$3 = new RepeatOnLifecycleKt$repeatOnLifecycle$3(this.$this_repeatOnLifecycle, this.$state, this.$block, continuation);
        repeatOnLifecycleKt$repeatOnLifecycle$3.L$0 = obj;
        return repeatOnLifecycleKt$repeatOnLifecycle$3;
    }

    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((RepeatOnLifecycleKt$repeatOnLifecycle$3) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 6, 0}, xi = 48)
    @DebugMetadata(c = "androidx.lifecycle.RepeatOnLifecycleKt$repeatOnLifecycle$3$1", f = "RepeatOnLifecycle.kt", i = {0, 0}, l = {166}, m = "invokeSuspend", n = {"launchedJob", "observer"}, s = {"L$0", "L$1"})
    /* renamed from: androidx.lifecycle.RepeatOnLifecycleKt$repeatOnLifecycle$3$1  reason: invalid class name */
    /* compiled from: RepeatOnLifecycle.kt */
    static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        Object L$5;
        int label;

        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new AnonymousClass1(lifecycle, state, coroutineScope, function2, continuation);
        }

        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: kotlin.jvm.internal.Ref$ObjectRef} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v21, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: kotlin.jvm.internal.Ref$ObjectRef} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final java.lang.Object invokeSuspend(java.lang.Object r17) {
            /*
                r16 = this;
                r1 = r16
                java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r2 = r1.label
                r3 = 0
                r4 = 1
                if (r2 == 0) goto L_0x0038
                if (r2 != r4) goto L_0x0030
                java.lang.Object r0 = r1.L$5
                kotlin.jvm.functions.Function2 r0 = (kotlin.jvm.functions.Function2) r0
                java.lang.Object r0 = r1.L$4
                kotlinx.coroutines.CoroutineScope r0 = (kotlinx.coroutines.CoroutineScope) r0
                java.lang.Object r0 = r1.L$3
                androidx.lifecycle.Lifecycle r0 = (androidx.lifecycle.Lifecycle) r0
                java.lang.Object r0 = r1.L$2
                androidx.lifecycle.Lifecycle$State r0 = (androidx.lifecycle.Lifecycle.State) r0
                java.lang.Object r0 = r1.L$1
                r2 = r0
                kotlin.jvm.internal.Ref$ObjectRef r2 = (kotlin.jvm.internal.Ref.ObjectRef) r2
                java.lang.Object r0 = r1.L$0
                r5 = r0
                kotlin.jvm.internal.Ref$ObjectRef r5 = (kotlin.jvm.internal.Ref.ObjectRef) r5
                kotlin.ResultKt.throwOnFailure(r17)     // Catch:{ all -> 0x002d }
                goto L_0x00b1
            L_0x002d:
                r0 = move-exception
                goto L_0x00d6
            L_0x0030:
                java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
                java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
                r0.<init>(r2)
                throw r0
            L_0x0038:
                kotlin.ResultKt.throwOnFailure(r17)
                androidx.lifecycle.Lifecycle r2 = r4
                androidx.lifecycle.Lifecycle$State r2 = r2.getCurrentState()
                androidx.lifecycle.Lifecycle$State r5 = androidx.lifecycle.Lifecycle.State.DESTROYED
                if (r2 != r5) goto L_0x0048
                kotlin.Unit r0 = kotlin.Unit.INSTANCE
                return r0
            L_0x0048:
                kotlin.jvm.internal.Ref$ObjectRef r2 = new kotlin.jvm.internal.Ref$ObjectRef
                r2.<init>()
                kotlin.jvm.internal.Ref$ObjectRef r13 = new kotlin.jvm.internal.Ref$ObjectRef
                r13.<init>()
                androidx.lifecycle.Lifecycle$State r5 = r5     // Catch:{ all -> 0x00d3 }
                androidx.lifecycle.Lifecycle r14 = r4     // Catch:{ all -> 0x00d3 }
                kotlinx.coroutines.CoroutineScope r8 = r6     // Catch:{ all -> 0x00d3 }
                kotlin.jvm.functions.Function2<kotlinx.coroutines.CoroutineScope, kotlin.coroutines.Continuation<? super kotlin.Unit>, java.lang.Object> r12 = r7     // Catch:{ all -> 0x00d3 }
                r1.L$0 = r2     // Catch:{ all -> 0x00d3 }
                r1.L$1 = r13     // Catch:{ all -> 0x00d3 }
                r1.L$2 = r5     // Catch:{ all -> 0x00d3 }
                r1.L$3 = r14     // Catch:{ all -> 0x00d3 }
                r1.L$4 = r8     // Catch:{ all -> 0x00d3 }
                r1.L$5 = r12     // Catch:{ all -> 0x00d3 }
                r1.label = r4     // Catch:{ all -> 0x00d3 }
                r6 = r1
                kotlin.coroutines.Continuation r6 = (kotlin.coroutines.Continuation) r6     // Catch:{ all -> 0x00d3 }
                kotlinx.coroutines.CancellableContinuationImpl r15 = new kotlinx.coroutines.CancellableContinuationImpl     // Catch:{ all -> 0x00d3 }
                kotlin.coroutines.Continuation r6 = kotlin.coroutines.intrinsics.IntrinsicsKt.intercepted(r6)     // Catch:{ all -> 0x00d3 }
                r15.<init>(r6, r4)     // Catch:{ all -> 0x00d3 }
                r15.initCancellability()     // Catch:{ all -> 0x00d3 }
                r10 = r15
                kotlinx.coroutines.CancellableContinuation r10 = (kotlinx.coroutines.CancellableContinuation) r10     // Catch:{ all -> 0x00d3 }
                androidx.lifecycle.Lifecycle$Event r6 = androidx.lifecycle.Lifecycle.Event.upTo(r5)     // Catch:{ all -> 0x00d3 }
                androidx.lifecycle.Lifecycle$Event r9 = androidx.lifecycle.Lifecycle.Event.downFrom(r5)     // Catch:{ all -> 0x00d3 }
                r5 = 0
                kotlinx.coroutines.sync.Mutex r11 = kotlinx.coroutines.sync.MutexKt.Mutex$default(r5, r4, r3)     // Catch:{ all -> 0x00d3 }
                androidx.lifecycle.RepeatOnLifecycleKt$repeatOnLifecycle$3$1$1$1 r7 = new androidx.lifecycle.RepeatOnLifecycleKt$repeatOnLifecycle$3$1$1$1     // Catch:{ all -> 0x00d3 }
                r5 = r7
                r3 = r7
                r7 = r2
                r5.<init>(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x00d3 }
                r13.element = r3     // Catch:{ all -> 0x00d3 }
                T r3 = r13.element     // Catch:{ all -> 0x00d3 }
                if (r3 == 0) goto L_0x00cb
                androidx.lifecycle.LifecycleEventObserver r3 = (androidx.lifecycle.LifecycleEventObserver) r3     // Catch:{ all -> 0x00d3 }
                androidx.lifecycle.LifecycleObserver r3 = (androidx.lifecycle.LifecycleObserver) r3     // Catch:{ all -> 0x00d3 }
                r14.addObserver(r3)     // Catch:{ all -> 0x00d3 }
                java.lang.Object r3 = r15.getResult()     // Catch:{ all -> 0x00d3 }
                java.lang.Object r5 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()     // Catch:{ all -> 0x00d3 }
                if (r3 != r5) goto L_0x00ac
                r5 = r1
                kotlin.coroutines.Continuation r5 = (kotlin.coroutines.Continuation) r5     // Catch:{ all -> 0x00d3 }
                kotlin.coroutines.jvm.internal.DebugProbesKt.probeCoroutineSuspended(r5)     // Catch:{ all -> 0x00d3 }
            L_0x00ac:
                if (r3 != r0) goto L_0x00af
                return r0
            L_0x00af:
                r5 = r2
                r2 = r13
            L_0x00b1:
                T r0 = r5.element
                kotlinx.coroutines.Job r0 = (kotlinx.coroutines.Job) r0
                if (r0 == 0) goto L_0x00bb
                r3 = 0
                kotlinx.coroutines.Job.DefaultImpls.cancel$default((kotlinx.coroutines.Job) r0, (java.util.concurrent.CancellationException) r3, (int) r4, (java.lang.Object) r3)
            L_0x00bb:
                T r0 = r2.element
                androidx.lifecycle.LifecycleEventObserver r0 = (androidx.lifecycle.LifecycleEventObserver) r0
                if (r0 == 0) goto L_0x00c8
                androidx.lifecycle.Lifecycle r2 = r4
                androidx.lifecycle.LifecycleObserver r0 = (androidx.lifecycle.LifecycleObserver) r0
                r2.removeObserver(r0)
            L_0x00c8:
                kotlin.Unit r0 = kotlin.Unit.INSTANCE
                return r0
            L_0x00cb:
                java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x00d3 }
                java.lang.String r3 = "null cannot be cast to non-null type androidx.lifecycle.LifecycleEventObserver"
                r0.<init>(r3)     // Catch:{ all -> 0x00d3 }
                throw r0     // Catch:{ all -> 0x00d3 }
            L_0x00d3:
                r0 = move-exception
                r5 = r2
                r2 = r13
            L_0x00d6:
                T r3 = r5.element
                kotlinx.coroutines.Job r3 = (kotlinx.coroutines.Job) r3
                if (r3 == 0) goto L_0x00e0
                r5 = 0
                kotlinx.coroutines.Job.DefaultImpls.cancel$default((kotlinx.coroutines.Job) r3, (java.util.concurrent.CancellationException) r5, (int) r4, (java.lang.Object) r5)
            L_0x00e0:
                T r2 = r2.element
                androidx.lifecycle.LifecycleEventObserver r2 = (androidx.lifecycle.LifecycleEventObserver) r2
                if (r2 == 0) goto L_0x00ed
                androidx.lifecycle.Lifecycle r3 = r4
                androidx.lifecycle.LifecycleObserver r2 = (androidx.lifecycle.LifecycleObserver) r2
                r3.removeObserver(r2)
            L_0x00ed:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.lifecycle.RepeatOnLifecycleKt$repeatOnLifecycle$3.AnonymousClass1.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public final Object invokeSuspend(Object obj) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            final CoroutineScope coroutineScope = (CoroutineScope) this.L$0;
            final Lifecycle lifecycle = this.$this_repeatOnLifecycle;
            final Lifecycle.State state = this.$state;
            final Function2<CoroutineScope, Continuation<? super Unit>, Object> function2 = this.$block;
            this.label = 1;
            if (BuildersKt.withContext(Dispatchers.getMain().getImmediate(), new AnonymousClass1((Continuation<? super AnonymousClass1>) null), this) == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else if (i == 1) {
            ResultKt.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.INSTANCE;
    }
}
