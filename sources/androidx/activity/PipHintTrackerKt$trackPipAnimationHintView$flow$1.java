package androidx.activity;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ProducerScope;

@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/channels/ProducerScope;", "Landroid/graphics/Rect;"}, k = 3, mv = {1, 6, 0}, xi = 48)
@DebugMetadata(c = "androidx.activity.PipHintTrackerKt$trackPipAnimationHintView$flow$1", f = "PipHintTracker.kt", i = {}, l = {87}, m = "invokeSuspend", n = {}, s = {})
/* compiled from: PipHintTracker.kt */
final class PipHintTrackerKt$trackPipAnimationHintView$flow$1 extends SuspendLambda implements Function2<ProducerScope<? super Rect>, Continuation<? super Unit>, Object> {
    final /* synthetic */ View $view;
    private /* synthetic */ Object L$0;
    int label;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    PipHintTrackerKt$trackPipAnimationHintView$flow$1(View view, Continuation<? super PipHintTrackerKt$trackPipAnimationHintView$flow$1> continuation) {
        super(2, continuation);
        this.$view = view;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        PipHintTrackerKt$trackPipAnimationHintView$flow$1 pipHintTrackerKt$trackPipAnimationHintView$flow$1 = new PipHintTrackerKt$trackPipAnimationHintView$flow$1(this.$view, continuation);
        pipHintTrackerKt$trackPipAnimationHintView$flow$1.L$0 = obj;
        return pipHintTrackerKt$trackPipAnimationHintView$flow$1;
    }

    public final Object invoke(ProducerScope<? super Rect> producerScope, Continuation<? super Unit> continuation) {
        return ((PipHintTrackerKt$trackPipAnimationHintView$flow$1) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    public final Object invokeSuspend(Object obj) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            ProducerScope producerScope = (ProducerScope) this.L$0;
            final $$Lambda$PipHintTrackerKt$trackPipAnimationHintView$flow$1$4ion0smgNNILo7u6DNVHbCOCSDk r1 = new View.OnLayoutChangeListener() {
                public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    PipHintTrackerKt$trackPipAnimationHintView$flow$1.m1invokeSuspend$lambda0(ProducerScope.this, view, i, i2, i3, i4, i5, i6, i7, i8);
                }
            };
            final $$Lambda$PipHintTrackerKt$trackPipAnimationHintView$flow$1$r95DZduMC1yZ2Ffp03M69mkmmB8 r4 = new ViewTreeObserver.OnScrollChangedListener(this.$view) {
                public final /* synthetic */ View f$1;

                {
                    this.f$1 = r2;
                }

                public final void onScrollChanged() {
                    PipHintTrackerKt$trackPipAnimationHintView$flow$1.m2invokeSuspend$lambda1(ProducerScope.this, this.f$1);
                }
            };
            final PipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1 pipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1 = new PipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1(producerScope, this.$view, r4, r1);
            if (Api19Impl.INSTANCE.isAttachedToWindow(this.$view)) {
                producerScope.m1599trySendJP2dKIU(PipHintTrackerKt.trackPipAnimationHintView$positionInWindow(this.$view));
                this.$view.getViewTreeObserver().addOnScrollChangedListener(r4);
                this.$view.addOnLayoutChangeListener(r1);
            }
            this.$view.addOnAttachStateChangeListener(pipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1);
            final View view = this.$view;
            this.label = 1;
            if (ProduceKt.awaitClose(producerScope, new Function0<Unit>() {
                public final void invoke() {
                    view.getViewTreeObserver().removeOnScrollChangedListener(r4);
                    view.removeOnLayoutChangeListener(r1);
                    view.removeOnAttachStateChangeListener(pipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1);
                }
            }, this) == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else if (i == 1) {
            ResultKt.throwOnFailure(obj);
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.INSTANCE;
    }

    /* access modifiers changed from: private */
    /* renamed from: invokeSuspend$lambda-0  reason: not valid java name */
    public static final void m1invokeSuspend$lambda0(ProducerScope producerScope, View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (i != i5 || i3 != i7 || i2 != i6 || i4 != i8) {
            Intrinsics.checkNotNullExpressionValue(view, "v");
            producerScope.m1599trySendJP2dKIU(PipHintTrackerKt.trackPipAnimationHintView$positionInWindow(view));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: invokeSuspend$lambda-1  reason: not valid java name */
    public static final void m2invokeSuspend$lambda1(ProducerScope producerScope, View view) {
        producerScope.m1599trySendJP2dKIU(PipHintTrackerKt.trackPipAnimationHintView$positionInWindow(view));
    }
}
