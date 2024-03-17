package androidx.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowKt;

@Metadata(d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001d\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H@ø\u0001\u0000¢\u0006\u0002\u0010\u0005\u0002\u0004\n\u0002\b\u0019¨\u0006\u0006"}, d2 = {"trackPipAnimationHintView", "", "Landroid/app/Activity;", "view", "Landroid/view/View;", "(Landroid/app/Activity;Landroid/view/View;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "activity-ktx_release"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* compiled from: PipHintTracker.kt */
public final class PipHintTrackerKt {
    /* access modifiers changed from: private */
    public static final Rect trackPipAnimationHintView$positionInWindow(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect;
    }

    public static final Object trackPipAnimationHintView(Activity activity, View view, Continuation<? super Unit> continuation) {
        Object collect = FlowKt.callbackFlow(new PipHintTrackerKt$trackPipAnimationHintView$flow$1(view, (Continuation<? super PipHintTrackerKt$trackPipAnimationHintView$flow$1>) null)).collect(new PipHintTrackerKt$trackPipAnimationHintView$2(activity), continuation);
        return collect == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? collect : Unit.INSTANCE;
    }
}
