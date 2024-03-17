package com.android.systemui.shared.animation;

import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.unfold.UnfoldTransitionProgressProvider;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\f\u0018\u00002\u00020\u0001:\u0003\u0019\u001a\u001bB\u001b\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0016\u0010\u000f\u001a\u00020\u00102\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\u0011\u001a\u00020\u0010H\u0016J\u0010\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u000bH\u0016J\b\u0010\u0014\u001a\u00020\u0010H\u0016J\u001e\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\t2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0002J\u0010\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u000bH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX.¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u000e¢\u0006\u0002\n\u0000¨\u0006\u001c"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider$TransitionProgressListener;", "viewsIdToTranslate", "", "Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$ViewIdToTranslate;", "progressProvider", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "(Ljava/util/Set;Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;)V", "rootView", "Landroid/view/ViewGroup;", "translationMax", "", "viewsToTranslate", "", "Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$ViewToTranslate;", "init", "", "onTransitionFinished", "onTransitionProgress", "progress", "onTransitionStarted", "registerViewsForAnimation", "parent", "ids", "translateViews", "Direction", "ViewIdToTranslate", "ViewToTranslate", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: UnfoldConstantTranslateAnimator.kt */
public final class UnfoldConstantTranslateAnimator implements UnfoldTransitionProgressProvider.TransitionProgressListener {
    private final UnfoldTransitionProgressProvider progressProvider;
    private ViewGroup rootView;
    private float translationMax;
    private final Set<ViewIdToTranslate> viewsIdToTranslate;
    private List<ViewToTranslate> viewsToTranslate = CollectionsKt.emptyList();

    public UnfoldConstantTranslateAnimator(Set<ViewIdToTranslate> set, UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
        Intrinsics.checkNotNullParameter(set, "viewsIdToTranslate");
        Intrinsics.checkNotNullParameter(unfoldTransitionProgressProvider, "progressProvider");
        this.viewsIdToTranslate = set;
        this.progressProvider = unfoldTransitionProgressProvider;
    }

    public final void init(ViewGroup viewGroup, float f) {
        Intrinsics.checkNotNullParameter(viewGroup, "rootView");
        this.rootView = viewGroup;
        this.translationMax = f;
        this.progressProvider.addCallback(this);
    }

    public void onTransitionStarted() {
        ViewGroup viewGroup = this.rootView;
        if (viewGroup == null) {
            Intrinsics.throwUninitializedPropertyAccessException("rootView");
            viewGroup = null;
        }
        registerViewsForAnimation(viewGroup, this.viewsIdToTranslate);
    }

    public void onTransitionProgress(float f) {
        translateViews(f);
    }

    public void onTransitionFinished() {
        translateViews(1.0f);
    }

    private final void translateViews(float f) {
        View view;
        float f2 = (f - 1.0f) * this.translationMax;
        for (ViewToTranslate viewToTranslate : this.viewsToTranslate) {
            WeakReference<View> component1 = viewToTranslate.component1();
            Direction component2 = viewToTranslate.component2();
            if (viewToTranslate.component3().invoke().booleanValue() && (view = (View) component1.get()) != null) {
                view.setTranslationX(component2.getMultiplier() * f2);
            }
        }
    }

    private final void registerViewsForAnimation(ViewGroup viewGroup, Set<ViewIdToTranslate> set) {
        ViewToTranslate viewToTranslate;
        Collection arrayList = new ArrayList();
        for (ViewIdToTranslate viewIdToTranslate : set) {
            int component1 = viewIdToTranslate.component1();
            Direction component2 = viewIdToTranslate.component2();
            Function0<Boolean> component3 = viewIdToTranslate.component3();
            View findViewById = viewGroup.findViewById(component1);
            if (findViewById == null) {
                viewToTranslate = null;
            } else {
                viewToTranslate = new ViewToTranslate(new WeakReference(findViewById), component2, component3);
            }
            if (viewToTranslate != null) {
                arrayList.add(viewToTranslate);
            }
        }
        this.viewsToTranslate = (List) arrayList;
    }

    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\u000e\n\u0000\b\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0002\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J-\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0001J\u0013\u0010\u0014\u001a\u00020\b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÖ\u0001J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0019"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$ViewIdToTranslate;", "", "viewId", "", "direction", "Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$Direction;", "shouldBeAnimated", "Lkotlin/Function0;", "", "(ILcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$Direction;Lkotlin/jvm/functions/Function0;)V", "getDirection", "()Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$Direction;", "getShouldBeAnimated", "()Lkotlin/jvm/functions/Function0;", "getViewId", "()I", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "toString", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldConstantTranslateAnimator.kt */
    public static final class ViewIdToTranslate {
        private final Direction direction;
        private final Function0<Boolean> shouldBeAnimated;
        private final int viewId;

        public static /* synthetic */ ViewIdToTranslate copy$default(ViewIdToTranslate viewIdToTranslate, int i, Direction direction2, Function0<Boolean> function0, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                i = viewIdToTranslate.viewId;
            }
            if ((i2 & 2) != 0) {
                direction2 = viewIdToTranslate.direction;
            }
            if ((i2 & 4) != 0) {
                function0 = viewIdToTranslate.shouldBeAnimated;
            }
            return viewIdToTranslate.copy(i, direction2, function0);
        }

        public final int component1() {
            return this.viewId;
        }

        public final Direction component2() {
            return this.direction;
        }

        public final Function0<Boolean> component3() {
            return this.shouldBeAnimated;
        }

        public final ViewIdToTranslate copy(int i, Direction direction2, Function0<Boolean> function0) {
            Intrinsics.checkNotNullParameter(direction2, "direction");
            Intrinsics.checkNotNullParameter(function0, "shouldBeAnimated");
            return new ViewIdToTranslate(i, direction2, function0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ViewIdToTranslate)) {
                return false;
            }
            ViewIdToTranslate viewIdToTranslate = (ViewIdToTranslate) obj;
            return this.viewId == viewIdToTranslate.viewId && this.direction == viewIdToTranslate.direction && Intrinsics.areEqual((Object) this.shouldBeAnimated, (Object) viewIdToTranslate.shouldBeAnimated);
        }

        public int hashCode() {
            return (((Integer.hashCode(this.viewId) * 31) + this.direction.hashCode()) * 31) + this.shouldBeAnimated.hashCode();
        }

        public String toString() {
            return "ViewIdToTranslate(viewId=" + this.viewId + ", direction=" + this.direction + ", shouldBeAnimated=" + this.shouldBeAnimated + ')';
        }

        public ViewIdToTranslate(int i, Direction direction2, Function0<Boolean> function0) {
            Intrinsics.checkNotNullParameter(direction2, "direction");
            Intrinsics.checkNotNullParameter(function0, "shouldBeAnimated");
            this.viewId = i;
            this.direction = direction2;
            this.shouldBeAnimated = function0;
        }

        public final int getViewId() {
            return this.viewId;
        }

        public final Direction getDirection() {
            return this.direction;
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public /* synthetic */ ViewIdToTranslate(int i, Direction direction2, Function0 function0, int i2, DefaultConstructorMarker defaultConstructorMarker) {
            this(i, direction2, (i2 & 4) != 0 ? AnonymousClass1.INSTANCE : function0);
        }

        public final Function0<Boolean> getShouldBeAnimated() {
            return this.shouldBeAnimated;
        }
    }

    @Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\b\u0018\u00002\u00020\u0001B)\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0002\u0010\nJ\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0006HÆ\u0003J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\bHÆ\u0003J3\u0010\u0014\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bHÆ\u0001J\u0013\u0010\u0015\u001a\u00020\t2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001J\t\u0010\u0019\u001a\u00020\u001aHÖ\u0001R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001b"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$ViewToTranslate;", "", "view", "Ljava/lang/ref/WeakReference;", "Landroid/view/View;", "direction", "Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$Direction;", "shouldBeAnimated", "Lkotlin/Function0;", "", "(Ljava/lang/ref/WeakReference;Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$Direction;Lkotlin/jvm/functions/Function0;)V", "getDirection", "()Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$Direction;", "getShouldBeAnimated", "()Lkotlin/jvm/functions/Function0;", "getView", "()Ljava/lang/ref/WeakReference;", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldConstantTranslateAnimator.kt */
    private static final class ViewToTranslate {
        private final Direction direction;
        private final Function0<Boolean> shouldBeAnimated;
        private final WeakReference<View> view;

        public static /* synthetic */ ViewToTranslate copy$default(ViewToTranslate viewToTranslate, WeakReference<View> weakReference, Direction direction2, Function0<Boolean> function0, int i, Object obj) {
            if ((i & 1) != 0) {
                weakReference = viewToTranslate.view;
            }
            if ((i & 2) != 0) {
                direction2 = viewToTranslate.direction;
            }
            if ((i & 4) != 0) {
                function0 = viewToTranslate.shouldBeAnimated;
            }
            return viewToTranslate.copy(weakReference, direction2, function0);
        }

        public final WeakReference<View> component1() {
            return this.view;
        }

        public final Direction component2() {
            return this.direction;
        }

        public final Function0<Boolean> component3() {
            return this.shouldBeAnimated;
        }

        public final ViewToTranslate copy(WeakReference<View> weakReference, Direction direction2, Function0<Boolean> function0) {
            Intrinsics.checkNotNullParameter(weakReference, "view");
            Intrinsics.checkNotNullParameter(direction2, "direction");
            Intrinsics.checkNotNullParameter(function0, "shouldBeAnimated");
            return new ViewToTranslate(weakReference, direction2, function0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ViewToTranslate)) {
                return false;
            }
            ViewToTranslate viewToTranslate = (ViewToTranslate) obj;
            return Intrinsics.areEqual((Object) this.view, (Object) viewToTranslate.view) && this.direction == viewToTranslate.direction && Intrinsics.areEqual((Object) this.shouldBeAnimated, (Object) viewToTranslate.shouldBeAnimated);
        }

        public int hashCode() {
            return (((this.view.hashCode() * 31) + this.direction.hashCode()) * 31) + this.shouldBeAnimated.hashCode();
        }

        public String toString() {
            return "ViewToTranslate(view=" + this.view + ", direction=" + this.direction + ", shouldBeAnimated=" + this.shouldBeAnimated + ')';
        }

        public ViewToTranslate(WeakReference<View> weakReference, Direction direction2, Function0<Boolean> function0) {
            Intrinsics.checkNotNullParameter(weakReference, "view");
            Intrinsics.checkNotNullParameter(direction2, "direction");
            Intrinsics.checkNotNullParameter(function0, "shouldBeAnimated");
            this.view = weakReference;
            this.direction = direction2;
            this.shouldBeAnimated = function0;
        }

        public final WeakReference<View> getView() {
            return this.view;
        }

        public final Direction getDirection() {
            return this.direction;
        }

        public final Function0<Boolean> getShouldBeAnimated() {
            return this.shouldBeAnimated;
        }
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0006\b\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\b¨\u0006\t"}, d2 = {"Lcom/android/systemui/shared/animation/UnfoldConstantTranslateAnimator$Direction;", "", "multiplier", "", "(Ljava/lang/String;IF)V", "getMultiplier", "()F", "LEFT", "RIGHT", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldConstantTranslateAnimator.kt */
    public enum Direction {
        LEFT(-1.0f),
        RIGHT(1.0f);
        
        private final float multiplier;

        private Direction(float f) {
            this.multiplier = f;
        }

        public final float getMultiplier() {
            return this.multiplier;
        }
    }
}
