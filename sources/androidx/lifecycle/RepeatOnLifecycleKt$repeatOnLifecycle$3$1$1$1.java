package androidx.lifecycle;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.sync.Mutex;

@Metadata(d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\n¢\u0006\u0002\b\u0006"}, d2 = {"<anonymous>", "", "<anonymous parameter 0>", "Landroidx/lifecycle/LifecycleOwner;", "event", "Landroidx/lifecycle/Lifecycle$Event;", "onStateChanged"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: RepeatOnLifecycle.kt */
final class RepeatOnLifecycleKt$repeatOnLifecycle$3$1$1$1 implements LifecycleEventObserver {
    final /* synthetic */ CoroutineScope $$this$coroutineScope;
    final /* synthetic */ Function2<CoroutineScope, Continuation<? super Unit>, Object> $block;
    final /* synthetic */ Lifecycle.Event $cancelWorkEvent;
    final /* synthetic */ CancellableContinuation<Unit> $cont;
    final /* synthetic */ Ref.ObjectRef<Job> $launchedJob;
    final /* synthetic */ Mutex $mutex;
    final /* synthetic */ Lifecycle.Event $startWorkEvent;

    RepeatOnLifecycleKt$repeatOnLifecycle$3$1$1$1(Lifecycle.Event event, Ref.ObjectRef<Job> objectRef, CoroutineScope coroutineScope, Lifecycle.Event event2, CancellableContinuation<? super Unit> cancellableContinuation, Mutex mutex, Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object> function2) {
        this.$startWorkEvent = event;
        this.$launchedJob = objectRef;
        this.$$this$coroutineScope = coroutineScope;
        this.$cancelWorkEvent = event2;
        this.$cont = cancellableContinuation;
        this.$mutex = mutex;
        this.$block = function2;
    }

    public final void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        Intrinsics.checkNotNullParameter(lifecycleOwner, "<anonymous parameter 0>");
        Intrinsics.checkNotNullParameter(event, NotificationCompat.CATEGORY_EVENT);
        if (event == this.$startWorkEvent) {
            Ref.ObjectRef<Job> objectRef = this.$launchedJob;
            CoroutineScope coroutineScope = this.$$this$coroutineScope;
            final Mutex mutex = this.$mutex;
            final Function2<CoroutineScope, Continuation<? super Unit>, Object> function2 = this.$block;
            objectRef.element = BuildersKt__Builders_commonKt.launch$default(coroutineScope, (CoroutineContext) null, (CoroutineStart) null, new AnonymousClass1((Continuation<? super AnonymousClass1>) null), 3, (Object) null);
            return;
        }
        if (event == this.$cancelWorkEvent) {
            Job job = (Job) this.$launchedJob.element;
            if (job != null) {
                Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
            }
            this.$launchedJob.element = null;
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            Result.Companion companion = Result.Companion;
            this.$cont.resumeWith(Result.m132constructorimpl(Unit.INSTANCE));
        }
    }

    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {1, 6, 0}, xi = 48)
    @DebugMetadata(c = "androidx.lifecycle.RepeatOnLifecycleKt$repeatOnLifecycle$3$1$1$1$1", f = "RepeatOnLifecycle.kt", i = {0, 1}, l = {171, 110}, m = "invokeSuspend", n = {"$this$withLock_u24default$iv", "$this$withLock_u24default$iv"}, s = {"L$0", "L$0"})
    /* renamed from: androidx.lifecycle.RepeatOnLifecycleKt$repeatOnLifecycle$3$1$1$1$1  reason: invalid class name */
    /* compiled from: RepeatOnLifecycle.kt */
    static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        Object L$0;
        Object L$1;
        int label;

        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new AnonymousClass1(mutex, function2, continuation);
        }

        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        public final Object invokeSuspend(Object obj) {
            Throwable th;
            Mutex mutex;
            Mutex mutex2;
            Function2<CoroutineScope, Continuation<? super Unit>, Object> function2;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                mutex2 = mutex;
                function2 = function2;
                this.L$0 = mutex2;
                this.L$1 = function2;
                this.label = 1;
                if (mutex2.lock((Object) null, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else if (i == 1) {
                function2 = (Function2) this.L$1;
                ResultKt.throwOnFailure(obj);
                mutex2 = (Mutex) this.L$0;
            } else if (i == 2) {
                mutex = (Mutex) this.L$0;
                try {
                    ResultKt.throwOnFailure(obj);
                    Unit unit = Unit.INSTANCE;
                    mutex.unlock((Object) null);
                    return Unit.INSTANCE;
                } catch (Throwable th2) {
                    th = th2;
                }
            } else {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            try {
                this.L$0 = mutex2;
                this.L$1 = null;
                this.label = 2;
                if (CoroutineScopeKt.coroutineScope(new RepeatOnLifecycleKt$repeatOnLifecycle$3$1$1$1$1$1$1(function2, (Continuation<? super RepeatOnLifecycleKt$repeatOnLifecycle$3$1$1$1$1$1$1>) null), this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                mutex = mutex2;
                Unit unit2 = Unit.INSTANCE;
                mutex.unlock((Object) null);
                return Unit.INSTANCE;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                mutex = mutex2;
                th = th4;
                mutex.unlock((Object) null);
                throw th;
            }
        }
    }
}
