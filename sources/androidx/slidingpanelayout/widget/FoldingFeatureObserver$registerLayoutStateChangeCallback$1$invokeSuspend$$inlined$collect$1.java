package androidx.slidingpanelayout.widget;

import androidx.slidingpanelayout.widget.FoldingFeatureObserver;
import androidx.window.layout.FoldingFeature;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(d1 = {"\u0000\u0013\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00028\u00000\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00028\u0000H@ø\u0001\u0000¢\u0006\u0002\u0010\u0005\u0002\u0004\n\u0002\b\u0019¨\u0006\u0006¸\u0006\u0000"}, d2 = {"kotlinx/coroutines/flow/FlowKt__CollectKt$collect$3", "Lkotlinx/coroutines/flow/FlowCollector;", "emit", "", "value", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: Collect.kt */
public final class FoldingFeatureObserver$registerLayoutStateChangeCallback$1$invokeSuspend$$inlined$collect$1 implements FlowCollector<FoldingFeature> {
    final /* synthetic */ FoldingFeatureObserver this$0;

    public FoldingFeatureObserver$registerLayoutStateChangeCallback$1$invokeSuspend$$inlined$collect$1(FoldingFeatureObserver foldingFeatureObserver) {
        this.this$0 = foldingFeatureObserver;
    }

    public Object emit(FoldingFeature foldingFeature, Continuation<? super Unit> continuation) {
        Unit unit;
        FoldingFeature foldingFeature2 = foldingFeature;
        FoldingFeatureObserver.OnFoldingFeatureChangeListener access$getOnFoldingFeatureChangeListener$p = this.this$0.onFoldingFeatureChangeListener;
        if (access$getOnFoldingFeatureChangeListener$p == null) {
            unit = null;
        } else {
            access$getOnFoldingFeatureChangeListener$p.onFoldingFeatureChange(foldingFeature2);
            unit = Unit.INSTANCE;
        }
        if (unit == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            return unit;
        }
        return Unit.INSTANCE;
    }
}
