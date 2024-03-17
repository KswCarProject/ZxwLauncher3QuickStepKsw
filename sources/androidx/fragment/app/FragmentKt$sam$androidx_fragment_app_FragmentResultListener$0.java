package androidx.fragment.app;

import android.os.Bundle;
import kotlin.Metadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, k = 3, mv = {1, 4, 1})
/* compiled from: Fragment.kt */
final class FragmentKt$sam$androidx_fragment_app_FragmentResultListener$0 implements FragmentResultListener {
    private final /* synthetic */ Function2 function;

    FragmentKt$sam$androidx_fragment_app_FragmentResultListener$0(Function2 function2) {
        this.function = function2;
    }

    public final /* synthetic */ void onFragmentResult(String str, Bundle bundle) {
        Intrinsics.checkNotNullParameter(str, "p0");
        Intrinsics.checkNotNullParameter(bundle, "p1");
        Intrinsics.checkNotNullExpressionValue(this.function.invoke(str, bundle), "invoke(...)");
    }
}
