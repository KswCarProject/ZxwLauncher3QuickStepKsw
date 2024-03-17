package androidx.activity.contextaware;

import android.content.Context;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancellableContinuation;

@Metadata(d1 = {"\u0000\u0017\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0006"}, d2 = {"androidx/activity/contextaware/ContextAwareKt$withContextAvailable$2$listener$1", "Landroidx/activity/contextaware/OnContextAvailableListener;", "onContextAvailable", "", "context", "Landroid/content/Context;", "activity-ktx_release"}, k = 1, mv = {1, 6, 0}, xi = 176)
/* compiled from: ContextAware.kt */
public final class ContextAwareKt$withContextAvailable$2$listener$1 implements OnContextAvailableListener {
    final /* synthetic */ CancellableContinuation<R> $co;
    final /* synthetic */ Function1<Context, R> $onContextAvailable;

    public ContextAwareKt$withContextAvailable$2$listener$1(CancellableContinuation<? super R> cancellableContinuation, Function1<? super Context, ? extends R> function1) {
        this.$co = cancellableContinuation;
        this.$onContextAvailable = function1;
    }

    public void onContextAvailable(Context context) {
        Object obj;
        Intrinsics.checkNotNullParameter(context, "context");
        CancellableContinuation<R> cancellableContinuation = this.$co;
        Function1<Context, R> function1 = this.$onContextAvailable;
        try {
            Result.Companion companion = Result.Companion;
            ContextAwareKt$withContextAvailable$2$listener$1 contextAwareKt$withContextAvailable$2$listener$1 = this;
            obj = Result.m132constructorimpl(function1.invoke(context));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.Companion;
            obj = Result.m132constructorimpl(ResultKt.createFailure(th));
        }
        cancellableContinuation.resumeWith(obj);
    }
}
