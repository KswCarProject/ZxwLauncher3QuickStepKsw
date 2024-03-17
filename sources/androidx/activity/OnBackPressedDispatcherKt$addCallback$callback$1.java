package androidx.activity;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@Metadata(d1 = {"\u0000\u0011\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016¨\u0006\u0004"}, d2 = {"androidx/activity/OnBackPressedDispatcherKt$addCallback$callback$1", "Landroidx/activity/OnBackPressedCallback;", "handleOnBackPressed", "", "activity-ktx_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: OnBackPressedDispatcher.kt */
public final class OnBackPressedDispatcherKt$addCallback$callback$1 extends OnBackPressedCallback {
    final /* synthetic */ Function1<OnBackPressedCallback, Unit> $onBackPressed;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    OnBackPressedDispatcherKt$addCallback$callback$1(boolean z, Function1<? super OnBackPressedCallback, Unit> function1) {
        super(z);
        this.$onBackPressed = function1;
    }

    public void handleOnBackPressed() {
        this.$onBackPressed.invoke(this);
    }
}
