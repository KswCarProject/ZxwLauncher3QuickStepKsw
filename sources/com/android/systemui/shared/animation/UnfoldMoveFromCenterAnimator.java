package com.android.systemui.shared.animation;

import android.graphics.Point;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\u0018\u00002\u00020\u0001:\u0004!\"#$B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0002\u0010\nJ\u0006\u0010\u0014\u001a\u00020\u0015J\u0010\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\u0010\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u001a\u001a\u00020\u0011H\u0016J\u000e\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0018J\u0006\u0010\u001c\u001a\u00020\u0015J\u0006\u0010\u001d\u001a\u00020\u0015J\u0014\u0010\u001e\u001a\u00020\u0015*\u00020\r2\u0006\u0010\u001a\u001a\u00020\u0011H\u0002J\u0014\u0010\u001f\u001a\u00020\u0015*\u00020\r2\u0006\u0010\u001a\u001a\u00020\u0011H\u0002J\u0014\u0010 \u001a\u00020\r*\u00020\r2\u0006\u0010\u0017\u001a\u00020\u0018H\u0002R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006%"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "windowManager", "Landroid/view/WindowManager;", "translationApplier", "Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$TranslationApplier;", "viewCenterProvider", "Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$ViewCenterProvider;", "alphaProvider", "Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$AlphaProvider;", "(Landroid/view/WindowManager;Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$TranslationApplier;Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$ViewCenterProvider;Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$AlphaProvider;)V", "animatedViews", "", "Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$AnimatedView;", "isVerticalFold", "", "lastAnimationProgress", "", "screenSize", "Landroid/graphics/Point;", "clearRegisteredViews", "", "createAnimatedView", "view", "Landroid/view/View;", "onTransitionProgress", "progress", "registerViewForAnimation", "updateDisplayProperties", "updateViewPositions", "applyAlpha", "applyTransition", "updateAnimatedView", "AlphaProvider", "AnimatedView", "TranslationApplier", "ViewCenterProvider", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: UnfoldMoveFromCenterAnimator.kt */
public final class UnfoldMoveFromCenterAnimator implements UnfoldTransitionProgressProvider.TransitionProgressListener {
    private final AlphaProvider alphaProvider;
    private final List<AnimatedView> animatedViews;
    private boolean isVerticalFold;
    private float lastAnimationProgress;
    private final Point screenSize;
    private final TranslationApplier translationApplier;
    private final ViewCenterProvider viewCenterProvider;
    private final WindowManager windowManager;

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H&¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$AlphaProvider;", "", "getAlpha", "", "progress", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldMoveFromCenterAnimator.kt */
    public interface AlphaProvider {
        float getAlpha(float f);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public UnfoldMoveFromCenterAnimator(WindowManager windowManager2) {
        this(windowManager2, (TranslationApplier) null, (ViewCenterProvider) null, (AlphaProvider) null, 14, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(windowManager2, "windowManager");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public UnfoldMoveFromCenterAnimator(WindowManager windowManager2, TranslationApplier translationApplier2) {
        this(windowManager2, translationApplier2, (ViewCenterProvider) null, (AlphaProvider) null, 12, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(windowManager2, "windowManager");
        Intrinsics.checkNotNullParameter(translationApplier2, "translationApplier");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public UnfoldMoveFromCenterAnimator(WindowManager windowManager2, TranslationApplier translationApplier2, ViewCenterProvider viewCenterProvider2) {
        this(windowManager2, translationApplier2, viewCenterProvider2, (AlphaProvider) null, 8, (DefaultConstructorMarker) null);
        Intrinsics.checkNotNullParameter(windowManager2, "windowManager");
        Intrinsics.checkNotNullParameter(translationApplier2, "translationApplier");
        Intrinsics.checkNotNullParameter(viewCenterProvider2, "viewCenterProvider");
    }

    public UnfoldMoveFromCenterAnimator(WindowManager windowManager2, TranslationApplier translationApplier2, ViewCenterProvider viewCenterProvider2, AlphaProvider alphaProvider2) {
        Intrinsics.checkNotNullParameter(windowManager2, "windowManager");
        Intrinsics.checkNotNullParameter(translationApplier2, "translationApplier");
        Intrinsics.checkNotNullParameter(viewCenterProvider2, "viewCenterProvider");
        this.windowManager = windowManager2;
        this.translationApplier = translationApplier2;
        this.viewCenterProvider = viewCenterProvider2;
        this.alphaProvider = alphaProvider2;
        this.screenSize = new Point();
        this.animatedViews = new ArrayList();
        this.lastAnimationProgress = 1.0f;
    }

    public void onTransitionFinished() {
        UnfoldTransitionProgressProvider.TransitionProgressListener.DefaultImpls.onTransitionFinished(this);
    }

    public void onTransitionStarted() {
        UnfoldTransitionProgressProvider.TransitionProgressListener.DefaultImpls.onTransitionStarted(this);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ UnfoldMoveFromCenterAnimator(WindowManager windowManager2, TranslationApplier translationApplier2, ViewCenterProvider viewCenterProvider2, AlphaProvider alphaProvider2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(windowManager2, (i & 2) != 0 ? new TranslationApplier() {
            public void apply(View view, float f, float f2) {
                TranslationApplier.DefaultImpls.apply(this, view, f, f2);
            }
        } : translationApplier2, (i & 4) != 0 ? new ViewCenterProvider() {
            public void getViewCenter(View view, Point point) {
                ViewCenterProvider.DefaultImpls.getViewCenter(this, view, point);
            }
        } : viewCenterProvider2, (i & 8) != 0 ? null : alphaProvider2);
    }

    public final void updateDisplayProperties() {
        this.windowManager.getDefaultDisplay().getSize(this.screenSize);
        this.isVerticalFold = this.windowManager.getDefaultDisplay().getRotation() == 0 || this.windowManager.getDefaultDisplay().getRotation() == 2;
    }

    public final void updateViewPositions() {
        for (AnimatedView animatedView : this.animatedViews) {
            View view = (View) animatedView.getView().get();
            if (view != null) {
                updateAnimatedView(animatedView, view);
            }
        }
        onTransitionProgress(this.lastAnimationProgress);
    }

    public final void registerViewForAnimation(View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        this.animatedViews.add(createAnimatedView(view));
    }

    public final void clearRegisteredViews() {
        onTransitionProgress(1.0f);
        this.animatedViews.clear();
    }

    public void onTransitionProgress(float f) {
        for (AnimatedView animatedView : this.animatedViews) {
            applyTransition(animatedView, f);
            applyAlpha(animatedView, f);
        }
        this.lastAnimationProgress = f;
    }

    private final void applyTransition(AnimatedView animatedView, float f) {
        View view = (View) animatedView.getView().get();
        if (view != null) {
            float f2 = ((float) 1) - f;
            this.translationApplier.apply(view, animatedView.getStartTranslationX() * f2, animatedView.getStartTranslationY() * f2);
        }
    }

    private final void applyAlpha(AnimatedView animatedView, float f) {
        View view;
        if (this.alphaProvider != null && (view = (View) animatedView.getView().get()) != null) {
            view.setAlpha(this.alphaProvider.getAlpha(f));
        }
    }

    private final AnimatedView createAnimatedView(View view) {
        return updateAnimatedView(new AnimatedView(new WeakReference(view), 0.0f, 0.0f, 6, (DefaultConstructorMarker) null), view);
    }

    private final AnimatedView updateAnimatedView(AnimatedView animatedView, View view) {
        Point point = new Point();
        this.viewCenterProvider.getViewCenter(view, point);
        int i = point.x;
        int i2 = point.y;
        if (this.isVerticalFold) {
            animatedView.setStartTranslationX(((float) ((this.screenSize.x / 2) - i)) * 0.3f);
            animatedView.setStartTranslationY(0.0f);
        } else {
            animatedView.setStartTranslationX(0.0f);
            animatedView.setStartTranslationY(((float) ((this.screenSize.y / 2) - i2)) * 0.3f);
        }
        return animatedView;
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0016¨\u0006\t"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$TranslationApplier;", "", "apply", "", "view", "Landroid/view/View;", "x", "", "y", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldMoveFromCenterAnimator.kt */
    public interface TranslationApplier {
        void apply(View view, float f, float f2);

        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
        /* compiled from: UnfoldMoveFromCenterAnimator.kt */
        public static final class DefaultImpls {
            public static void apply(TranslationApplier translationApplier, View view, float f, float f2) {
                Intrinsics.checkNotNullParameter(translationApplier, "this");
                Intrinsics.checkNotNullParameter(view, "view");
                view.setTranslationX(f);
                view.setTranslationY(f2);
            }
        }
    }

    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$ViewCenterProvider;", "", "getViewCenter", "", "view", "Landroid/view/View;", "outPoint", "Landroid/graphics/Point;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldMoveFromCenterAnimator.kt */
    public interface ViewCenterProvider {
        void getViewCenter(View view, Point point);

        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
        /* compiled from: UnfoldMoveFromCenterAnimator.kt */
        public static final class DefaultImpls {
            public static void getViewCenter(ViewCenterProvider viewCenterProvider, View view, Point point) {
                Intrinsics.checkNotNullParameter(viewCenterProvider, "this");
                Intrinsics.checkNotNullParameter(view, "view");
                Intrinsics.checkNotNullParameter(point, "outPoint");
                int[] iArr = new int[2];
                view.getLocationOnScreen(iArr);
                int i = iArr[0];
                int i2 = iArr[1];
                point.x = i + (view.getWidth() / 2);
                point.y = i2 + (view.getHeight() / 2);
            }
        }
    }

    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u000b\b\u0002\u0018\u00002\u00020\u0001B'\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006¢\u0006\u0002\u0010\bR\u001a\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u0007\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\n\"\u0004\b\u000e\u0010\fR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0011"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldMoveFromCenterAnimator$AnimatedView;", "", "view", "Ljava/lang/ref/WeakReference;", "Landroid/view/View;", "startTranslationX", "", "startTranslationY", "(Ljava/lang/ref/WeakReference;FF)V", "getStartTranslationX", "()F", "setStartTranslationX", "(F)V", "getStartTranslationY", "setStartTranslationY", "getView", "()Ljava/lang/ref/WeakReference;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldMoveFromCenterAnimator.kt */
    private static final class AnimatedView {
        private float startTranslationX;
        private float startTranslationY;
        private final WeakReference<View> view;

        public AnimatedView(WeakReference<View> weakReference, float f, float f2) {
            Intrinsics.checkNotNullParameter(weakReference, "view");
            this.view = weakReference;
            this.startTranslationX = f;
            this.startTranslationY = f2;
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public /* synthetic */ AnimatedView(WeakReference weakReference, float f, float f2, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this(weakReference, (i & 2) != 0 ? 0.0f : f, (i & 4) != 0 ? 0.0f : f2);
        }

        public final WeakReference<View> getView() {
            return this.view;
        }

        public final float getStartTranslationX() {
            return this.startTranslationX;
        }

        public final void setStartTranslationX(float f) {
            this.startTranslationX = f;
        }

        public final float getStartTranslationY() {
            return this.startTranslationY;
        }

        public final void setStartTranslationY(float f) {
            this.startTranslationY = f;
        }
    }
}
