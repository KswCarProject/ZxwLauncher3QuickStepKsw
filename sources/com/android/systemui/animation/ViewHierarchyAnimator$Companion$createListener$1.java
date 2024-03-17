package com.android.systemui.animation;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.systemui.R;
import com.android.systemui.animation.ViewHierarchyAnimator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;

@Metadata(d1 = {"\u0000\u001f\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\b*\u0001\u0000\b\n\u0018\u00002\u00020\u0001JR\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u0007H\u0016¨\u0006\u000f"}, d2 = {"com/android/systemui/animation/ViewHierarchyAnimator$Companion$createListener$1", "Landroid/view/View$OnLayoutChangeListener;", "onLayoutChange", "", "view", "Landroid/view/View;", "left", "", "top", "right", "bottom", "previousLeft", "previousTop", "previousRight", "previousBottom", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ViewHierarchyAnimator.kt */
public final class ViewHierarchyAnimator$Companion$createListener$1 implements View.OnLayoutChangeListener {
    final /* synthetic */ long $duration;
    final /* synthetic */ boolean $ephemeral;
    final /* synthetic */ boolean $ignorePreviousValues;
    final /* synthetic */ Interpolator $interpolator;
    final /* synthetic */ ViewHierarchyAnimator.Hotspot $origin;

    ViewHierarchyAnimator$Companion$createListener$1(ViewHierarchyAnimator.Hotspot hotspot, boolean z, Interpolator interpolator, long j, boolean z2) {
        this.$origin = hotspot;
        this.$ignorePreviousValues = z;
        this.$interpolator = interpolator;
        this.$duration = j;
        this.$ephemeral = z2;
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        View view2 = view;
        int i9 = i;
        int i10 = i2;
        int i11 = i3;
        int i12 = i4;
        if (view2 != null) {
            Integer access$getBound = ViewHierarchyAnimator.Companion.getBound(view2, ViewHierarchyAnimator.Bound.LEFT);
            int intValue = access$getBound == null ? i5 : access$getBound.intValue();
            Integer access$getBound2 = ViewHierarchyAnimator.Companion.getBound(view2, ViewHierarchyAnimator.Bound.TOP);
            int intValue2 = access$getBound2 == null ? i6 : access$getBound2.intValue();
            Integer access$getBound3 = ViewHierarchyAnimator.Companion.getBound(view2, ViewHierarchyAnimator.Bound.RIGHT);
            int intValue3 = access$getBound3 == null ? i7 : access$getBound3.intValue();
            Integer access$getBound4 = ViewHierarchyAnimator.Companion.getBound(view2, ViewHierarchyAnimator.Bound.BOTTOM);
            int intValue4 = access$getBound4 == null ? i8 : access$getBound4.intValue();
            Object tag = view2.getTag(R.id.tag_animator);
            ObjectAnimator objectAnimator = tag instanceof ObjectAnimator ? (ObjectAnimator) tag : null;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (!ViewHierarchyAnimator.Companion.isVisible(view.getVisibility(), i, i2, i3, i4)) {
                ViewHierarchyAnimator.Companion.setBound(view2, ViewHierarchyAnimator.Bound.LEFT, i9);
                ViewHierarchyAnimator.Companion.setBound(view2, ViewHierarchyAnimator.Bound.TOP, i10);
                ViewHierarchyAnimator.Companion.setBound(view2, ViewHierarchyAnimator.Bound.RIGHT, i11);
                ViewHierarchyAnimator.Companion.setBound(view2, ViewHierarchyAnimator.Bound.BOTTOM, i12);
                return;
            }
            int i13 = i12;
            Map access$processStartValues = ViewHierarchyAnimator.Companion.processStartValues(this.$origin, i, i2, i3, i4, intValue, intValue2, intValue3, intValue4, this.$ignorePreviousValues);
            Map mapOf = MapsKt.mapOf(TuplesKt.to(ViewHierarchyAnimator.Bound.LEFT, Integer.valueOf(i)), TuplesKt.to(ViewHierarchyAnimator.Bound.TOP, Integer.valueOf(i2)), TuplesKt.to(ViewHierarchyAnimator.Bound.RIGHT, Integer.valueOf(i3)), TuplesKt.to(ViewHierarchyAnimator.Bound.BOTTOM, Integer.valueOf(i4)));
            Set linkedHashSet = new LinkedHashSet();
            if (((Number) MapsKt.getValue(access$processStartValues, ViewHierarchyAnimator.Bound.LEFT)).intValue() != i9) {
                linkedHashSet.add(ViewHierarchyAnimator.Bound.LEFT);
            }
            if (((Number) MapsKt.getValue(access$processStartValues, ViewHierarchyAnimator.Bound.TOP)).intValue() != i10) {
                linkedHashSet.add(ViewHierarchyAnimator.Bound.TOP);
            }
            if (((Number) MapsKt.getValue(access$processStartValues, ViewHierarchyAnimator.Bound.RIGHT)).intValue() != i11) {
                linkedHashSet.add(ViewHierarchyAnimator.Bound.RIGHT);
            }
            if (((Number) MapsKt.getValue(access$processStartValues, ViewHierarchyAnimator.Bound.BOTTOM)).intValue() != i13) {
                linkedHashSet.add(ViewHierarchyAnimator.Bound.BOTTOM);
            }
            if (!linkedHashSet.isEmpty()) {
                ViewHierarchyAnimator.Companion.startAnimation(view, linkedHashSet, access$processStartValues, mapOf, this.$interpolator, this.$duration, this.$ephemeral);
            }
        }
    }
}
