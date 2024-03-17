package androidx.concurrent.futures;

import com.google.common.util.concurrent.ListenableFuture;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004H\n¢\u0006\u0002\b\u0005¨\u0006\u0006"}, d2 = {"<anonymous>", "", "T", "it", "", "invoke", "androidx/concurrent/futures/ListenableFutureKt$await$2$1"}, k = 3, mv = {1, 1, 16})
/* compiled from: ListenableFuture.kt */
final class ListenableFutureKt$await$$inlined$suspendCancellableCoroutine$lambda$1 extends Lambda implements Function1<Throwable, Unit> {
    final /* synthetic */ ListenableFuture $this_await$inlined;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ListenableFutureKt$await$$inlined$suspendCancellableCoroutine$lambda$1(ListenableFuture listenableFuture) {
        super(1);
        this.$this_await$inlined = listenableFuture;
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Throwable) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(Throwable th) {
        this.$this_await$inlined.cancel(false);
    }
}
