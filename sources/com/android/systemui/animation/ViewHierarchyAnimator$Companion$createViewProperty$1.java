package com.android.systemui.animation;

import android.util.IntProperty;
import android.view.View;
import com.android.systemui.animation.ViewHierarchyAnimator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u001f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0002H\u0002¢\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\u0004H\u0016¨\u0006\n"}, d2 = {"com/android/systemui/animation/ViewHierarchyAnimator$Companion$createViewProperty$1", "Landroid/util/IntProperty;", "Landroid/view/View;", "get", "", "view", "(Landroid/view/View;)Ljava/lang/Integer;", "setValue", "", "value", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ViewHierarchyAnimator.kt */
public final class ViewHierarchyAnimator$Companion$createViewProperty$1 extends IntProperty<View> {
    final /* synthetic */ ViewHierarchyAnimator.Bound $bound;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ViewHierarchyAnimator$Companion$createViewProperty$1(ViewHierarchyAnimator.Bound bound, String str) {
        super(str);
        this.$bound = bound;
    }

    public void setValue(View view, int i) {
        Intrinsics.checkNotNullParameter(view, "view");
        ViewHierarchyAnimator.Companion.setBound(view, this.$bound, i);
    }

    public Integer get(View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        Integer access$getBound = ViewHierarchyAnimator.Companion.getBound(view, this.$bound);
        return Integer.valueOf(access$getBound == null ? this.$bound.getValue(view) : access$getBound.intValue());
    }
}
