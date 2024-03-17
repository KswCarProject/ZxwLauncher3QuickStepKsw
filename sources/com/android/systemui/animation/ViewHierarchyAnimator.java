package com.android.systemui.animation;

import android.animation.ValueAnimator;
import android.util.IntProperty;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import com.android.launcher3.systemui.R;
import com.android.systemui.animation.ViewHierarchyAnimator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\u0018\u0000 \u00042\u00020\u0001:\u0003\u0003\u0004\u0005B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/animation/ViewHierarchyAnimator;", "", "()V", "Bound", "Companion", "Hotspot", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ViewHierarchyAnimator.kt */
public final class ViewHierarchyAnimator {
    public static final Companion Companion;
    /* access modifiers changed from: private */
    public static final Interpolator DEFAULT_ADDITION_INTERPOLATOR = Interpolators.STANDARD_DECELERATE;
    private static final long DEFAULT_DURATION = 500;
    /* access modifiers changed from: private */
    public static final Interpolator DEFAULT_INTERPOLATOR = Interpolators.STANDARD;
    /* access modifiers changed from: private */
    public static final Interpolator DEFAULT_REMOVAL_INTERPOLATOR = Interpolators.STANDARD_ACCELERATE;
    /* access modifiers changed from: private */
    public static final Map<Bound, IntProperty<View>> PROPERTIES;

    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000b\b\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b¨\u0006\f"}, d2 = {"Lcom/android/systemui/animation/ViewHierarchyAnimator$Hotspot;", "", "(Ljava/lang/String;I)V", "CENTER", "LEFT", "TOP_LEFT", "TOP", "TOP_RIGHT", "RIGHT", "BOTTOM_RIGHT", "BOTTOM", "BOTTOM_LEFT", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ViewHierarchyAnimator.kt */
    public enum Hotspot {
        CENTER,
        LEFT,
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        RIGHT,
        BOTTOM_RIGHT,
        BOTTOM,
        BOTTOM_LEFT
    }

    @Metadata(d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\"\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u0015H\u0002J$\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u000e2\b\b\u0002\u0010\u0018\u001a\u00020\u00042\b\b\u0002\u0010\u0019\u001a\u00020\u0007H\u0007J(\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u0015H\u0002J8\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u000e2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\b\b\u0002\u0010\u0018\u001a\u00020\u00042\b\b\u0002\u0010\u0019\u001a\u00020\u00072\b\b\u0002\u0010\u001e\u001a\u00020\u0015H\u0007J$\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u000e2\b\b\u0002\u0010\u0018\u001a\u00020\u00042\b\b\u0002\u0010\u0019\u001a\u00020\u0007H\u0007J.\u0010 \u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u000e2\b\b\u0002\u0010!\u001a\u00020\u001d2\b\b\u0002\u0010\u0018\u001a\u00020\u00042\b\b\u0002\u0010\u0019\u001a\u00020\u0007H\u0007J(\u0010\"\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u0015H\u0002J6\u0010$\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u00152\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\b\b\u0002\u0010#\u001a\u00020\u0015H\u0002J \u0010%\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u0015H\u0002J\u0016\u0010&\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010'\u001a\u00020\fH\u0002J\u001f\u0010(\u001a\u0004\u0018\u00010)2\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010'\u001a\u00020\fH\u0002¢\u0006\u0002\u0010*J0\u0010+\u001a\u00020\u00152\u0006\u0010,\u001a\u00020)2\u0006\u0010-\u001a\u00020)2\u0006\u0010.\u001a\u00020)2\u0006\u0010/\u001a\u00020)2\u0006\u00100\u001a\u00020)H\u0002JL\u00101\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020)0\u000b2\u0006\u0010!\u001a\u00020\u001d2\u0006\u0010-\u001a\u00020)2\u0006\u0010.\u001a\u00020)2\u0006\u0010/\u001a\u00020)2\u0006\u00100\u001a\u00020)2\u0006\u00102\u001a\u00020)2\u0006\u00103\u001a\u00020)H\u0002J<\u00104\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020)0\u000b2\u0006\u0010!\u001a\u00020\u001d2\u0006\u0010-\u001a\u00020)2\u0006\u0010.\u001a\u00020)2\u0006\u0010/\u001a\u00020)2\u0006\u00100\u001a\u00020)H\u0002Jf\u00105\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020)0\u000b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\u0006\u00106\u001a\u00020)2\u0006\u00107\u001a\u00020)2\u0006\u00108\u001a\u00020)2\u0006\u00109\u001a\u00020)2\u0006\u0010:\u001a\u00020)2\u0006\u0010;\u001a\u00020)2\u0006\u0010<\u001a\u00020)2\u0006\u0010=\u001a\u00020)2\u0006\u0010#\u001a\u00020\u0015H\u0002J\u0010\u0010>\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000eH\u0002J \u0010?\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010'\u001a\u00020\f2\u0006\u0010@\u001a\u00020)H\u0002J<\u0010A\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020B2\u0006\u0010!\u001a\u00020\u001d2\u0012\u0010C\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020)0\u000b2\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u0007H\u0002J^\u0010D\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000e2\f\u0010E\u001a\b\u0012\u0004\u0012\u00020\f0F2\u0012\u0010G\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020)0\u000b2\u0012\u0010C\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020)0\u000b2\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u0015H\u0002J\u000e\u0010H\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u000eR\u0016\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007XT¢\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\u0002\n\u0000R \u0010\n\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\u000bX\u0004¢\u0006\u0002\n\u0000¨\u0006I"}, d2 = {"Lcom/android/systemui/animation/ViewHierarchyAnimator$Companion;", "", "()V", "DEFAULT_ADDITION_INTERPOLATOR", "Landroid/view/animation/Interpolator;", "kotlin.jvm.PlatformType", "DEFAULT_DURATION", "", "DEFAULT_INTERPOLATOR", "DEFAULT_REMOVAL_INTERPOLATOR", "PROPERTIES", "", "Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound;", "Landroid/util/IntProperty;", "Landroid/view/View;", "addListener", "", "view", "listener", "Landroid/view/View$OnLayoutChangeListener;", "recursive", "", "animate", "rootView", "interpolator", "duration", "ephemeral", "animateAddition", "origin", "Lcom/android/systemui/animation/ViewHierarchyAnimator$Hotspot;", "includeMargins", "animateNextUpdate", "animateRemoval", "destination", "createAdditionListener", "ignorePreviousValues", "createListener", "createUpdateListener", "createViewProperty", "bound", "getBound", "", "(Landroid/view/View;Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound;)Ljava/lang/Integer;", "isVisible", "visibility", "left", "top", "right", "bottom", "processChildEndValuesForRemoval", "parentWidth", "parentHeight", "processEndValuesForRemoval", "processStartValues", "newLeft", "newTop", "newRight", "newBottom", "previousLeft", "previousTop", "previousRight", "previousBottom", "recursivelyRemoveListener", "setBound", "value", "shiftChildrenForRemoval", "Landroid/view/ViewGroup;", "endValues", "startAnimation", "bounds", "", "startValues", "stopAnimating", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ViewHierarchyAnimator.kt */
    public static final class Companion {

        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
        /* compiled from: ViewHierarchyAnimator.kt */
        public /* synthetic */ class WhenMappings {
            public static final /* synthetic */ int[] $EnumSwitchMapping$0;

            static {
                int[] iArr = new int[Hotspot.values().length];
                iArr[Hotspot.CENTER.ordinal()] = 1;
                iArr[Hotspot.BOTTOM_LEFT.ordinal()] = 2;
                iArr[Hotspot.LEFT.ordinal()] = 3;
                iArr[Hotspot.TOP_LEFT.ordinal()] = 4;
                iArr[Hotspot.TOP.ordinal()] = 5;
                iArr[Hotspot.BOTTOM.ordinal()] = 6;
                iArr[Hotspot.TOP_RIGHT.ordinal()] = 7;
                iArr[Hotspot.RIGHT.ordinal()] = 8;
                iArr[Hotspot.BOTTOM_RIGHT.ordinal()] = 9;
                $EnumSwitchMapping$0 = iArr;
            }
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: private */
        public final boolean isVisible(int i, int i2, int i3, int i4, int i5) {
            return (i != 0 || i2 == i4 || i3 == i5) ? false : true;
        }

        public final boolean animate(View view) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            return animate$default(this, view, (Interpolator) null, 0, 6, (Object) null);
        }

        public final boolean animate(View view, Interpolator interpolator) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(interpolator, "interpolator");
            return animate$default(this, view, interpolator, 0, 4, (Object) null);
        }

        public final boolean animateAddition(View view) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            return animateAddition$default(this, view, (Hotspot) null, (Interpolator) null, 0, false, 30, (Object) null);
        }

        public final boolean animateAddition(View view, Hotspot hotspot) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(hotspot, "origin");
            return animateAddition$default(this, view, hotspot, (Interpolator) null, 0, false, 28, (Object) null);
        }

        public final boolean animateAddition(View view, Hotspot hotspot, Interpolator interpolator) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(hotspot, "origin");
            Intrinsics.checkNotNullParameter(interpolator, "interpolator");
            return animateAddition$default(this, view, hotspot, interpolator, 0, false, 24, (Object) null);
        }

        public final boolean animateAddition(View view, Hotspot hotspot, Interpolator interpolator, long j) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(hotspot, "origin");
            Intrinsics.checkNotNullParameter(interpolator, "interpolator");
            return animateAddition$default(this, view, hotspot, interpolator, j, false, 16, (Object) null);
        }

        public final boolean animateNextUpdate(View view) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            return animateNextUpdate$default(this, view, (Interpolator) null, 0, 6, (Object) null);
        }

        public final boolean animateNextUpdate(View view, Interpolator interpolator) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(interpolator, "interpolator");
            return animateNextUpdate$default(this, view, interpolator, 0, 4, (Object) null);
        }

        public final boolean animateRemoval(View view) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            return animateRemoval$default(this, view, (Hotspot) null, (Interpolator) null, 0, 14, (Object) null);
        }

        public final boolean animateRemoval(View view, Hotspot hotspot) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(hotspot, "destination");
            return animateRemoval$default(this, view, hotspot, (Interpolator) null, 0, 12, (Object) null);
        }

        public final boolean animateRemoval(View view, Hotspot hotspot, Interpolator interpolator) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(hotspot, "destination");
            Intrinsics.checkNotNullParameter(interpolator, "interpolator");
            return animateRemoval$default(this, view, hotspot, interpolator, 0, 8, (Object) null);
        }

        private Companion() {
        }

        /* access modifiers changed from: private */
        public final IntProperty<View> createViewProperty(Bound bound) {
            return new ViewHierarchyAnimator$Companion$createViewProperty$1(bound, bound.getLabel());
        }

        public static /* synthetic */ boolean animate$default(Companion companion, View view, Interpolator interpolator, long j, int i, Object obj) {
            if ((i & 2) != 0) {
                interpolator = ViewHierarchyAnimator.DEFAULT_INTERPOLATOR;
                Intrinsics.checkNotNullExpressionValue(interpolator, "DEFAULT_INTERPOLATOR");
            }
            if ((i & 4) != 0) {
                j = ViewHierarchyAnimator.DEFAULT_DURATION;
            }
            return companion.animate(view, interpolator, j);
        }

        public final boolean animate(View view, Interpolator interpolator, long j) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(interpolator, "interpolator");
            return animate(view, interpolator, j, false);
        }

        public static /* synthetic */ boolean animateNextUpdate$default(Companion companion, View view, Interpolator interpolator, long j, int i, Object obj) {
            if ((i & 2) != 0) {
                interpolator = ViewHierarchyAnimator.DEFAULT_INTERPOLATOR;
                Intrinsics.checkNotNullExpressionValue(interpolator, "DEFAULT_INTERPOLATOR");
            }
            if ((i & 4) != 0) {
                j = ViewHierarchyAnimator.DEFAULT_DURATION;
            }
            return companion.animateNextUpdate(view, interpolator, j);
        }

        public final boolean animateNextUpdate(View view, Interpolator interpolator, long j) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(interpolator, "interpolator");
            return animate(view, interpolator, j, true);
        }

        private final boolean animate(View view, Interpolator interpolator, long j, boolean z) {
            if (!isVisible(view.getVisibility(), view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                return false;
            }
            addListener(view, createUpdateListener(interpolator, j, z), true);
            return true;
        }

        private final View.OnLayoutChangeListener createUpdateListener(Interpolator interpolator, long j, boolean z) {
            return createListener$default(this, interpolator, j, z, (Hotspot) null, false, 24, (Object) null);
        }

        public final void stopAnimating(View view) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            recursivelyRemoveListener(view);
        }

        public static /* synthetic */ boolean animateAddition$default(Companion companion, View view, Hotspot hotspot, Interpolator interpolator, long j, boolean z, int i, Object obj) {
            if ((i & 2) != 0) {
                hotspot = Hotspot.CENTER;
            }
            Hotspot hotspot2 = hotspot;
            if ((i & 4) != 0) {
                interpolator = ViewHierarchyAnimator.DEFAULT_ADDITION_INTERPOLATOR;
                Intrinsics.checkNotNullExpressionValue(interpolator, "DEFAULT_ADDITION_INTERPOLATOR");
            }
            Interpolator interpolator2 = interpolator;
            if ((i & 8) != 0) {
                j = ViewHierarchyAnimator.DEFAULT_DURATION;
            }
            long j2 = j;
            if ((i & 16) != 0) {
                z = false;
            }
            return companion.animateAddition(view, hotspot2, interpolator2, j2, z);
        }

        public final boolean animateAddition(View view, Hotspot hotspot, Interpolator interpolator, long j, boolean z) {
            Intrinsics.checkNotNullParameter(view, "rootView");
            Intrinsics.checkNotNullParameter(hotspot, "origin");
            Intrinsics.checkNotNullParameter(interpolator, "interpolator");
            if (isVisible(view.getVisibility(), view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                return false;
            }
            addListener(view, createAdditionListener(hotspot, interpolator, j, !z), true);
            return true;
        }

        private final View.OnLayoutChangeListener createAdditionListener(Hotspot hotspot, Interpolator interpolator, long j, boolean z) {
            return createListener(interpolator, j, true, hotspot, z);
        }

        static /* synthetic */ View.OnLayoutChangeListener createListener$default(Companion companion, Interpolator interpolator, long j, boolean z, Hotspot hotspot, boolean z2, int i, Object obj) {
            if ((i & 8) != 0) {
                hotspot = null;
            }
            Hotspot hotspot2 = hotspot;
            if ((i & 16) != 0) {
                z2 = false;
            }
            return companion.createListener(interpolator, j, z, hotspot2, z2);
        }

        private final View.OnLayoutChangeListener createListener(Interpolator interpolator, long j, boolean z, Hotspot hotspot, boolean z2) {
            return new ViewHierarchyAnimator$Companion$createListener$1(hotspot, z2, interpolator, j, z);
        }

        public static /* synthetic */ boolean animateRemoval$default(Companion companion, View view, Hotspot hotspot, Interpolator interpolator, long j, int i, Object obj) {
            if ((i & 2) != 0) {
                hotspot = Hotspot.CENTER;
            }
            Hotspot hotspot2 = hotspot;
            if ((i & 4) != 0) {
                interpolator = ViewHierarchyAnimator.DEFAULT_REMOVAL_INTERPOLATOR;
                Intrinsics.checkNotNullExpressionValue(interpolator, "DEFAULT_REMOVAL_INTERPOLATOR");
            }
            Interpolator interpolator2 = interpolator;
            if ((i & 8) != 0) {
                j = ViewHierarchyAnimator.DEFAULT_DURATION;
            }
            return companion.animateRemoval(view, hotspot2, interpolator2, j);
        }

        public final boolean animateRemoval(View view, Hotspot hotspot, Interpolator interpolator, long j) {
            View view2 = view;
            Interpolator interpolator2 = interpolator;
            long j2 = j;
            Intrinsics.checkNotNullParameter(view2, "rootView");
            Intrinsics.checkNotNullParameter(hotspot, "destination");
            Intrinsics.checkNotNullParameter(interpolator2, "interpolator");
            if (!isVisible(view.getVisibility(), view.getLeft(), view.getTop(), view.getRight(), view.getBottom())) {
                return false;
            }
            ViewParent parent = view.getParent();
            Objects.requireNonNull(parent, "null cannot be cast to non-null type android.view.ViewGroup");
            ViewGroup viewGroup = (ViewGroup) parent;
            View.OnLayoutChangeListener createUpdateListener = createUpdateListener(interpolator2, j2, true);
            int childCount = viewGroup.getChildCount();
            int i = 0;
            while (i < childCount) {
                int i2 = i + 1;
                View childAt = viewGroup.getChildAt(i);
                if (!Intrinsics.areEqual((Object) childAt, (Object) view2)) {
                    Intrinsics.checkNotNullExpressionValue(childAt, "child");
                    addListener(childAt, createUpdateListener, false);
                }
                i = i2;
            }
            viewGroup.removeView(view2);
            viewGroup.getOverlay().add(view2);
            Map mapOf = MapsKt.mapOf(TuplesKt.to(Bound.LEFT, Integer.valueOf(view.getLeft())), TuplesKt.to(Bound.TOP, Integer.valueOf(view.getTop())), TuplesKt.to(Bound.RIGHT, Integer.valueOf(view.getRight())), TuplesKt.to(Bound.BOTTOM, Integer.valueOf(view.getBottom())));
            Map processEndValuesForRemoval = processEndValuesForRemoval(hotspot, view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            Set linkedHashSet = new LinkedHashSet();
            if (view.getLeft() != ((Number) MapsKt.getValue(processEndValuesForRemoval, Bound.LEFT)).intValue()) {
                linkedHashSet.add(Bound.LEFT);
            }
            if (view.getTop() != ((Number) MapsKt.getValue(processEndValuesForRemoval, Bound.TOP)).intValue()) {
                linkedHashSet.add(Bound.TOP);
            }
            if (view.getRight() != ((Number) MapsKt.getValue(processEndValuesForRemoval, Bound.RIGHT)).intValue()) {
                linkedHashSet.add(Bound.RIGHT);
            }
            if (view.getBottom() != ((Number) MapsKt.getValue(processEndValuesForRemoval, Bound.BOTTOM)).intValue()) {
                linkedHashSet.add(Bound.BOTTOM);
            }
            Map map = processEndValuesForRemoval;
            ViewGroup viewGroup2 = viewGroup;
            startAnimation(view, linkedHashSet, mapOf, processEndValuesForRemoval, interpolator, j, true);
            if (view2 instanceof ViewGroup) {
                ViewGroup viewGroup3 = (ViewGroup) view2;
                shiftChildrenForRemoval(viewGroup3, hotspot, map, interpolator, j);
                float[] fArr = new float[viewGroup3.getChildCount()];
                int childCount2 = viewGroup3.getChildCount();
                for (int i3 = 0; i3 < childCount2; i3++) {
                    fArr[i3] = viewGroup3.getChildAt(i3).getAlpha();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                ofFloat.setInterpolator(Interpolators.ALPHA_OUT);
                ofFloat.setDuration(j2 / ((long) 2));
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(view2, fArr) {
                    public final /* synthetic */ View f$0;
                    public final /* synthetic */ float[] f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ViewHierarchyAnimator.Companion.m112animateRemoval$lambda0(this.f$0, this.f$1, valueAnimator);
                    }
                });
                ofFloat.addListener(new ViewHierarchyAnimator$Companion$animateRemoval$2(view2, j2, viewGroup2));
                ofFloat.start();
            } else {
                long j3 = j2 / ((long) 2);
                view.animate().alpha(0.0f).setInterpolator(Interpolators.ALPHA_OUT).setDuration(j3).setStartDelay(j3).withEndAction(new Runnable(viewGroup2, view2) {
                    public final /* synthetic */ ViewGroup f$0;
                    public final /* synthetic */ View f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ViewHierarchyAnimator.Companion.m113animateRemoval$lambda1(this.f$0, this.f$1);
                    }
                }).start();
            }
            return true;
        }

        /* access modifiers changed from: private */
        /* renamed from: animateRemoval$lambda-0  reason: not valid java name */
        public static final void m112animateRemoval$lambda0(View view, float[] fArr, ValueAnimator valueAnimator) {
            Intrinsics.checkNotNullParameter(view, "$rootView");
            Intrinsics.checkNotNullParameter(fArr, "$startAlphas");
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                Object animatedValue = valueAnimator.getAnimatedValue();
                Objects.requireNonNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
                childAt.setAlpha(((Float) animatedValue).floatValue() * fArr[i]);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: animateRemoval$lambda-1  reason: not valid java name */
        public static final void m113animateRemoval$lambda1(ViewGroup viewGroup, View view) {
            Intrinsics.checkNotNullParameter(viewGroup, "$parent");
            Intrinsics.checkNotNullParameter(view, "$rootView");
            viewGroup.getOverlay().remove(view);
        }

        private final void shiftChildrenForRemoval(ViewGroup viewGroup, Hotspot hotspot, Map<Bound, Integer> map, Interpolator interpolator, long j) {
            Map<Bound, Integer> map2 = map;
            int childCount = viewGroup.getChildCount();
            int i = 0;
            while (i < childCount) {
                int i2 = i + 1;
                View childAt = viewGroup.getChildAt(i);
                Map mapOf = MapsKt.mapOf(TuplesKt.to(Bound.LEFT, Integer.valueOf(childAt.getLeft())), TuplesKt.to(Bound.TOP, Integer.valueOf(childAt.getTop())), TuplesKt.to(Bound.RIGHT, Integer.valueOf(childAt.getRight())), TuplesKt.to(Bound.BOTTOM, Integer.valueOf(childAt.getBottom())));
                Map<Bound, Integer> processChildEndValuesForRemoval = processChildEndValuesForRemoval(hotspot, childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom(), ((Number) MapsKt.getValue(map2, Bound.RIGHT)).intValue() - ((Number) MapsKt.getValue(map2, Bound.LEFT)).intValue(), ((Number) MapsKt.getValue(map2, Bound.BOTTOM)).intValue() - ((Number) MapsKt.getValue(map2, Bound.TOP)).intValue());
                Set linkedHashSet = new LinkedHashSet();
                if (childAt.getLeft() != ((Number) MapsKt.getValue(map2, Bound.LEFT)).intValue()) {
                    linkedHashSet.add(Bound.LEFT);
                }
                if (childAt.getTop() != ((Number) MapsKt.getValue(map2, Bound.TOP)).intValue()) {
                    linkedHashSet.add(Bound.TOP);
                }
                if (childAt.getRight() != ((Number) MapsKt.getValue(map2, Bound.RIGHT)).intValue()) {
                    linkedHashSet.add(Bound.RIGHT);
                }
                if (childAt.getBottom() != ((Number) MapsKt.getValue(map2, Bound.BOTTOM)).intValue()) {
                    linkedHashSet.add(Bound.BOTTOM);
                }
                Intrinsics.checkNotNullExpressionValue(childAt, "child");
                startAnimation(childAt, linkedHashSet, mapOf, processChildEndValuesForRemoval, interpolator, j, true);
                i = i2;
            }
        }

        /* access modifiers changed from: private */
        public final Map<Bound, Integer> processStartValues(Hotspot hotspot, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
            int i9;
            int i10;
            if (z) {
                i5 = i;
            }
            if (z) {
                i6 = i2;
            }
            if (z) {
                i7 = i3;
            }
            if (z) {
                i8 = i4;
            }
            if (hotspot != null) {
                switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                    case 1:
                        i9 = (i + i3) / 2;
                        break;
                    case 2:
                    case 3:
                    case 4:
                        i9 = Math.min(i5, i);
                        break;
                    case 5:
                    case 6:
                        i9 = i;
                        break;
                    case 7:
                    case 8:
                    case 9:
                        i9 = Math.max(i7, i3);
                        break;
                    default:
                        throw new NoWhenBranchMatchedException();
                }
                switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                    case 1:
                        i10 = (i2 + i4) / 2;
                        break;
                    case 2:
                    case 6:
                    case 9:
                        i10 = Math.max(i8, i4);
                        break;
                    case 3:
                    case 8:
                        i10 = i2;
                        break;
                    case 4:
                    case 5:
                    case 7:
                        i10 = Math.min(i6, i2);
                        break;
                    default:
                        throw new NoWhenBranchMatchedException();
                }
                switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                    case 1:
                        i3 = (i + i3) / 2;
                        break;
                    case 2:
                    case 3:
                    case 4:
                        i3 = Math.min(i5, i);
                        break;
                    case 5:
                    case 6:
                        break;
                    case 7:
                    case 8:
                    case 9:
                        i3 = Math.max(i7, i3);
                        break;
                    default:
                        throw new NoWhenBranchMatchedException();
                }
                switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                    case 1:
                        i4 = (i2 + i4) / 2;
                        break;
                    case 2:
                    case 6:
                    case 9:
                        i4 = Math.max(i8, i4);
                        break;
                    case 3:
                    case 8:
                        break;
                    case 4:
                    case 5:
                    case 7:
                        i4 = Math.min(i6, i2);
                        break;
                    default:
                        throw new NoWhenBranchMatchedException();
                }
                i7 = i3;
                i8 = i4;
                i5 = i9;
                i6 = i10;
            }
            return MapsKt.mapOf(TuplesKt.to(Bound.LEFT, Integer.valueOf(i5)), TuplesKt.to(Bound.TOP, Integer.valueOf(i6)), TuplesKt.to(Bound.RIGHT, Integer.valueOf(i7)), TuplesKt.to(Bound.BOTTOM, Integer.valueOf(i8)));
        }

        private final Map<Bound, Integer> processEndValuesForRemoval(Hotspot hotspot, int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                case 1:
                    i5 = (i + i3) / 2;
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    i5 = i;
                    break;
                case 7:
                case 8:
                case 9:
                    i5 = i3;
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                case 1:
                    i6 = (i2 + i4) / 2;
                    break;
                case 2:
                case 6:
                case 9:
                    i6 = i4;
                    break;
                case 3:
                case 4:
                case 5:
                case 7:
                case 8:
                    i6 = i2;
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                case 1:
                    i = (i + i3) / 2;
                    break;
                case 2:
                case 3:
                case 4:
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    i = i3;
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                case 1:
                    i2 = (i2 + i4) / 2;
                    break;
                case 2:
                case 3:
                case 6:
                case 8:
                case 9:
                    i2 = i4;
                    break;
                case 4:
                case 5:
                case 7:
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            return MapsKt.mapOf(TuplesKt.to(Bound.LEFT, Integer.valueOf(i5)), TuplesKt.to(Bound.TOP, Integer.valueOf(i6)), TuplesKt.to(Bound.RIGHT, Integer.valueOf(i)), TuplesKt.to(Bound.BOTTOM, Integer.valueOf(i2)));
        }

        private final Map<Bound, Integer> processChildEndValuesForRemoval(Hotspot hotspot, int i, int i2, int i3, int i4, int i5, int i6) {
            int i7 = (i3 - i) / 2;
            int i8 = (i4 - i2) / 2;
            switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                case 1:
                    i = (i5 / 2) - i7;
                    break;
                case 2:
                case 3:
                case 4:
                    i = -i7;
                    break;
                case 5:
                case 6:
                    break;
                case 7:
                case 8:
                case 9:
                    i = i5 - i7;
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                case 1:
                    i2 = (i6 / 2) - i8;
                    break;
                case 2:
                case 6:
                case 9:
                    i2 = i6 - i8;
                    break;
                case 3:
                case 8:
                    break;
                case 4:
                case 5:
                case 7:
                    i2 = -i8;
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                case 1:
                    i5 /= 2;
                    break;
                case 2:
                case 3:
                case 4:
                    i3 = i7;
                    break;
                case 5:
                case 6:
                    break;
                case 7:
                case 8:
                case 9:
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            i3 = i5 + i7;
            switch (WhenMappings.$EnumSwitchMapping$0[hotspot.ordinal()]) {
                case 1:
                    i6 /= 2;
                    break;
                case 2:
                case 6:
                case 9:
                    break;
                case 3:
                case 8:
                    break;
                case 4:
                case 5:
                case 7:
                    i4 = i8;
                    break;
                default:
                    throw new NoWhenBranchMatchedException();
            }
            i4 = i6 + i8;
            return MapsKt.mapOf(TuplesKt.to(Bound.LEFT, Integer.valueOf(i)), TuplesKt.to(Bound.TOP, Integer.valueOf(i2)), TuplesKt.to(Bound.RIGHT, Integer.valueOf(i3)), TuplesKt.to(Bound.BOTTOM, Integer.valueOf(i4)));
        }

        static /* synthetic */ void addListener$default(Companion companion, View view, View.OnLayoutChangeListener onLayoutChangeListener, boolean z, int i, Object obj) {
            if ((i & 4) != 0) {
                z = false;
            }
            companion.addListener(view, onLayoutChangeListener, z);
        }

        private final void addListener(View view, View.OnLayoutChangeListener onLayoutChangeListener, boolean z) {
            Object tag = view.getTag(R.id.tag_layout_listener);
            if (tag != null && (tag instanceof View.OnLayoutChangeListener)) {
                view.removeOnLayoutChangeListener((View.OnLayoutChangeListener) tag);
            }
            view.addOnLayoutChangeListener(onLayoutChangeListener);
            view.setTag(R.id.tag_layout_listener, onLayoutChangeListener);
            if ((view instanceof ViewGroup) && z) {
                int i = 0;
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                while (i < childCount) {
                    int i2 = i + 1;
                    View childAt = viewGroup.getChildAt(i);
                    Intrinsics.checkNotNullExpressionValue(childAt, "view.getChildAt(i)");
                    addListener(childAt, onLayoutChangeListener, true);
                    i = i2;
                }
            }
        }

        /* access modifiers changed from: private */
        public final void recursivelyRemoveListener(View view) {
            Object tag = view.getTag(R.id.tag_layout_listener);
            if (tag != null && (tag instanceof View.OnLayoutChangeListener)) {
                view.setTag(R.id.tag_layout_listener, (Object) null);
                view.removeOnLayoutChangeListener((View.OnLayoutChangeListener) tag);
            }
            if (view instanceof ViewGroup) {
                int i = 0;
                ViewGroup viewGroup = (ViewGroup) view;
                int childCount = viewGroup.getChildCount();
                while (i < childCount) {
                    int i2 = i + 1;
                    View childAt = viewGroup.getChildAt(i);
                    Intrinsics.checkNotNullExpressionValue(childAt, "view.getChildAt(i)");
                    recursivelyRemoveListener(childAt);
                    i = i2;
                }
            }
        }

        /* access modifiers changed from: private */
        public final Integer getBound(View view, Bound bound) {
            Object tag = view.getTag(bound.getOverrideTag());
            if (tag instanceof Integer) {
                return (Integer) tag;
            }
            return null;
        }

        /* access modifiers changed from: private */
        public final void setBound(View view, Bound bound, int i) {
            view.setTag(bound.getOverrideTag(), Integer.valueOf(i));
            bound.setValue(view, i);
        }

        /* JADX WARNING: type inference failed for: r14v0, types: [java.util.Map, java.util.Map<com.android.systemui.animation.ViewHierarchyAnimator$Bound, java.lang.Integer>] */
        /* JADX WARNING: type inference failed for: r15v0, types: [java.util.Map, java.util.Map<com.android.systemui.animation.ViewHierarchyAnimator$Bound, java.lang.Integer>] */
        /* access modifiers changed from: private */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void startAnimation(android.view.View r12, java.util.Set<? extends com.android.systemui.animation.ViewHierarchyAnimator.Bound> r13, java.util.Map<com.android.systemui.animation.ViewHierarchyAnimator.Bound, java.lang.Integer> r14, java.util.Map<com.android.systemui.animation.ViewHierarchyAnimator.Bound, java.lang.Integer> r15, android.view.animation.Interpolator r16, long r17, boolean r19) {
            /*
                r11 = this;
                r0 = r12
                r1 = r13
                r2 = r14
                java.util.List r3 = kotlin.collections.CollectionsKt.createListBuilder()
                r4 = r1
                java.lang.Iterable r4 = (java.lang.Iterable) r4
                java.util.Iterator r5 = r4.iterator()
            L_0x000e:
                boolean r6 = r5.hasNext()
                r7 = 0
                if (r6 == 0) goto L_0x004a
                java.lang.Object r6 = r5.next()
                com.android.systemui.animation.ViewHierarchyAnimator$Bound r6 = (com.android.systemui.animation.ViewHierarchyAnimator.Bound) r6
                java.util.Map r8 = com.android.systemui.animation.ViewHierarchyAnimator.PROPERTIES
                java.lang.Object r8 = r8.get(r6)
                android.util.Property r8 = (android.util.Property) r8
                r9 = 2
                int[] r9 = new int[r9]
                java.lang.Object r10 = kotlin.collections.MapsKt.getValue(r14, r6)
                java.lang.Number r10 = (java.lang.Number) r10
                int r10 = r10.intValue()
                r9[r7] = r10
                r7 = 1
                r10 = r15
                java.lang.Object r6 = kotlin.collections.MapsKt.getValue(r15, r6)
                java.lang.Number r6 = (java.lang.Number) r6
                int r6 = r6.intValue()
                r9[r7] = r6
                android.animation.PropertyValuesHolder r6 = android.animation.PropertyValuesHolder.ofInt(r8, r9)
                r3.add(r6)
                goto L_0x000e
            L_0x004a:
                java.util.List r3 = kotlin.collections.CollectionsKt.build(r3)
                java.util.Collection r3 = (java.util.Collection) r3
                android.animation.PropertyValuesHolder[] r5 = new android.animation.PropertyValuesHolder[r7]
                java.lang.Object[] r3 = r3.toArray(r5)
                java.lang.String r5 = "null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>"
                java.util.Objects.requireNonNull(r3, r5)
                android.animation.PropertyValuesHolder[] r3 = (android.animation.PropertyValuesHolder[]) r3
                int r5 = com.android.launcher3.systemui.R.id.tag_animator
                java.lang.Object r5 = r12.getTag(r5)
                boolean r6 = r5 instanceof android.animation.ObjectAnimator
                if (r6 == 0) goto L_0x006a
                android.animation.ObjectAnimator r5 = (android.animation.ObjectAnimator) r5
                goto L_0x006b
            L_0x006a:
                r5 = 0
            L_0x006b:
                if (r5 != 0) goto L_0x006e
                goto L_0x0071
            L_0x006e:
                r5.cancel()
            L_0x0071:
                int r5 = r3.length
                java.lang.Object[] r3 = java.util.Arrays.copyOf(r3, r5)
                android.animation.PropertyValuesHolder[] r3 = (android.animation.PropertyValuesHolder[]) r3
                android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofPropertyValuesHolder(r12, r3)
                r5 = r16
                android.animation.TimeInterpolator r5 = (android.animation.TimeInterpolator) r5
                r3.setInterpolator(r5)
                r5 = r17
                r3.setDuration(r5)
                com.android.systemui.animation.ViewHierarchyAnimator$Companion$startAnimation$1 r5 = new com.android.systemui.animation.ViewHierarchyAnimator$Companion$startAnimation$1
                r6 = r19
                r5.<init>(r12, r13, r6)
                android.animation.Animator$AnimatorListener r5 = (android.animation.Animator.AnimatorListener) r5
                r3.addListener(r5)
                java.util.Iterator r1 = r4.iterator()
            L_0x0098:
                boolean r4 = r1.hasNext()
                if (r4 == 0) goto L_0x00b4
                java.lang.Object r4 = r1.next()
                com.android.systemui.animation.ViewHierarchyAnimator$Bound r4 = (com.android.systemui.animation.ViewHierarchyAnimator.Bound) r4
                com.android.systemui.animation.ViewHierarchyAnimator$Companion r5 = com.android.systemui.animation.ViewHierarchyAnimator.Companion
                java.lang.Object r6 = kotlin.collections.MapsKt.getValue(r14, r4)
                java.lang.Number r6 = (java.lang.Number) r6
                int r6 = r6.intValue()
                r5.setBound(r12, r4, r6)
                goto L_0x0098
            L_0x00b4:
                int r1 = com.android.launcher3.systemui.R.id.tag_animator
                r12.setTag(r1, r3)
                r3.start()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.animation.ViewHierarchyAnimator.Companion.startAnimation(android.view.View, java.util.Set, java.util.Map, java.util.Map, android.view.animation.Interpolator, long, boolean):void");
        }
    }

    static {
        Companion companion = new Companion((DefaultConstructorMarker) null);
        Companion = companion;
        PROPERTIES = MapsKt.mapOf(TuplesKt.to(Bound.LEFT, companion.createViewProperty(Bound.LEFT)), TuplesKt.to(Bound.TOP, companion.createViewProperty(Bound.TOP)), TuplesKt.to(Bound.RIGHT, companion.createViewProperty(Bound.RIGHT)), TuplesKt.to(Bound.BOTTOM, companion.createViewProperty(Bound.BOTTOM)));
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\rH&J\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0005H&R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nj\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014¨\u0006\u0015"}, d2 = {"Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound;", "", "label", "", "overrideTag", "", "(Ljava/lang/String;ILjava/lang/String;I)V", "getLabel", "()Ljava/lang/String;", "getOverrideTag", "()I", "getValue", "view", "Landroid/view/View;", "setValue", "", "value", "LEFT", "TOP", "RIGHT", "BOTTOM", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ViewHierarchyAnimator.kt */
    private enum Bound {
        ;
        
        private final String label;
        private final int overrideTag;

        public abstract int getValue(View view);

        public abstract void setValue(View view, int i);

        @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bÆ\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0016¨\u0006\t"}, d2 = {"Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound$LEFT;", "Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound;", "getValue", "", "view", "Landroid/view/View;", "setValue", "", "value", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* compiled from: ViewHierarchyAnimator.kt */
        static final class LEFT extends Bound {
            LEFT(String str, int i) {
                super(str, i, "left", R.id.tag_override_left, (DefaultConstructorMarker) null);
            }

            public void setValue(View view, int i) {
                Intrinsics.checkNotNullParameter(view, "view");
                view.setLeft(i);
            }

            public int getValue(View view) {
                Intrinsics.checkNotNullParameter(view, "view");
                return view.getLeft();
            }
        }

        private Bound(String str, int i) {
            this.label = str;
            this.overrideTag = i;
        }

        public final String getLabel() {
            return this.label;
        }

        public final int getOverrideTag() {
            return this.overrideTag;
        }

        @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bÆ\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0016¨\u0006\t"}, d2 = {"Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound$TOP;", "Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound;", "getValue", "", "view", "Landroid/view/View;", "setValue", "", "value", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* compiled from: ViewHierarchyAnimator.kt */
        static final class TOP extends Bound {
            TOP(String str, int i) {
                super(str, i, "top", R.id.tag_override_top, (DefaultConstructorMarker) null);
            }

            public void setValue(View view, int i) {
                Intrinsics.checkNotNullParameter(view, "view");
                view.setTop(i);
            }

            public int getValue(View view) {
                Intrinsics.checkNotNullParameter(view, "view");
                return view.getTop();
            }
        }

        @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bÆ\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0016¨\u0006\t"}, d2 = {"Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound$RIGHT;", "Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound;", "getValue", "", "view", "Landroid/view/View;", "setValue", "", "value", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* compiled from: ViewHierarchyAnimator.kt */
        static final class RIGHT extends Bound {
            RIGHT(String str, int i) {
                super(str, i, "right", R.id.tag_override_right, (DefaultConstructorMarker) null);
            }

            public void setValue(View view, int i) {
                Intrinsics.checkNotNullParameter(view, "view");
                view.setRight(i);
            }

            public int getValue(View view) {
                Intrinsics.checkNotNullParameter(view, "view");
                return view.getRight();
            }
        }

        @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bÆ\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0016¨\u0006\t"}, d2 = {"Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound$BOTTOM;", "Lcom/android/systemui/animation/ViewHierarchyAnimator$Bound;", "getValue", "", "view", "Landroid/view/View;", "setValue", "", "value", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* compiled from: ViewHierarchyAnimator.kt */
        static final class BOTTOM extends Bound {
            BOTTOM(String str, int i) {
                super(str, i, "bottom", R.id.tag_override_bottom, (DefaultConstructorMarker) null);
            }

            public void setValue(View view, int i) {
                Intrinsics.checkNotNullParameter(view, "view");
                view.setBottom(i);
            }

            public int getValue(View view) {
                Intrinsics.checkNotNullParameter(view, "view");
                return view.getBottom();
            }
        }
    }
}
