package androidx.activity;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.channels.ProducerScope;

@Metadata(d1 = {"\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0007"}, d2 = {"androidx/activity/PipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1", "Landroid/view/View$OnAttachStateChangeListener;", "onViewAttachedToWindow", "", "v", "Landroid/view/View;", "onViewDetachedFromWindow", "activity-ktx_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: PipHintTracker.kt */
public final class PipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1 implements View.OnAttachStateChangeListener {
    final /* synthetic */ ProducerScope<Rect> $$this$callbackFlow;
    final /* synthetic */ View.OnLayoutChangeListener $layoutChangeListener;
    final /* synthetic */ ViewTreeObserver.OnScrollChangedListener $scrollChangeListener;
    final /* synthetic */ View $view;

    PipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1(ProducerScope<? super Rect> producerScope, View view, ViewTreeObserver.OnScrollChangedListener onScrollChangedListener, View.OnLayoutChangeListener onLayoutChangeListener) {
        this.$$this$callbackFlow = producerScope;
        this.$view = view;
        this.$scrollChangeListener = onScrollChangedListener;
        this.$layoutChangeListener = onLayoutChangeListener;
    }

    public void onViewAttachedToWindow(View view) {
        Intrinsics.checkNotNullParameter(view, "v");
        this.$$this$callbackFlow.m1599trySendJP2dKIU(PipHintTrackerKt.trackPipAnimationHintView$positionInWindow(this.$view));
        this.$view.getViewTreeObserver().addOnScrollChangedListener(this.$scrollChangeListener);
        this.$view.addOnLayoutChangeListener(this.$layoutChangeListener);
    }

    public void onViewDetachedFromWindow(View view) {
        Intrinsics.checkNotNullParameter(view, "v");
        view.getViewTreeObserver().removeOnScrollChangedListener(this.$scrollChangeListener);
        view.removeOnLayoutChangeListener(this.$layoutChangeListener);
    }
}
