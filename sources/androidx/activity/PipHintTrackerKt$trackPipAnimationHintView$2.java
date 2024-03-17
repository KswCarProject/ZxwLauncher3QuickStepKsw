package androidx.activity;

import android.app.Activity;
import android.graphics.Rect;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H@¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", "", "hint", "Landroid/graphics/Rect;", "emit", "(Landroid/graphics/Rect;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 6, 0}, xi = 48)
/* compiled from: PipHintTracker.kt */
final class PipHintTrackerKt$trackPipAnimationHintView$2<T> implements FlowCollector {
    final /* synthetic */ Activity $this_trackPipAnimationHintView;

    PipHintTrackerKt$trackPipAnimationHintView$2(Activity activity) {
        this.$this_trackPipAnimationHintView = activity;
    }

    public final Object emit(Rect rect, Continuation<? super Unit> continuation) {
        Api26Impl.INSTANCE.setPipParamsSourceRectHint(this.$this_trackPipAnimationHintView, rect);
        return Unit.INSTANCE;
    }
}
