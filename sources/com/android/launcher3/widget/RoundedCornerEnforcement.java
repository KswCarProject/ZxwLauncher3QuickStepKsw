package com.android.launcher3.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import java.util.ArrayList;
import java.util.List;

public class RoundedCornerEnforcement {
    private RoundedCornerEnforcement() {
    }

    public static View findBackground(View view) {
        List<View> findViewsWithId = findViewsWithId(view, 16908288);
        if (findViewsWithId.size() == 1) {
            return findViewsWithId.get(0);
        }
        if (!(view instanceof ViewGroup)) {
            return view;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        return viewGroup.getChildCount() > 0 ? findUndefinedBackground(viewGroup.getChildAt(0)) : view;
    }

    public static boolean hasAppWidgetOptedOut(View view, View view2) {
        return view2.getId() == 16908288 && view2.getClipToOutline();
    }

    public static boolean isRoundedCornerEnabled() {
        return Utilities.ATLEAST_S && FeatureFlags.ENABLE_ENFORCED_ROUNDED_CORNERS.get();
    }

    public static void computeRoundedRectangle(View view, View view2, Rect rect) {
        rect.left = 0;
        rect.right = view2.getWidth();
        rect.top = 0;
        rect.bottom = view2.getHeight();
        while (view2 != view) {
            rect.offset(view2.getLeft(), view2.getTop());
            view2 = (View) view2.getParent();
        }
    }

    public static float computeEnforcedRadius(Context context) {
        if (!Utilities.ATLEAST_S) {
            return 0.0f;
        }
        Resources resources = context.getResources();
        return Math.min(resources.getDimension(R.dimen.enforced_rounded_corner_max_radius), resources.getDimension(17104904));
    }

    private static List<View> findViewsWithId(View view, int i) {
        ArrayList arrayList = new ArrayList();
        accumulateViewsWithId(view, i, arrayList);
        return arrayList;
    }

    private static void accumulateViewsWithId(View view, int i, List<View> list) {
        if (view.getId() == i) {
            list.add(view);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                accumulateViewsWithId(viewGroup.getChildAt(i2), i, list);
            }
        }
    }

    private static boolean isViewVisible(View view) {
        if (view.getVisibility() != 0) {
            return false;
        }
        if (view.willNotDraw() && view.getForeground() == null && view.getBackground() == null) {
            return false;
        }
        return true;
    }

    private static View findUndefinedBackground(View view) {
        View view2 = null;
        if (view.getVisibility() != 0) {
            return null;
        }
        if (isViewVisible(view)) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View findUndefinedBackground = findUndefinedBackground(viewGroup.getChildAt(i));
                if (findUndefinedBackground != null) {
                    if (view2 != null) {
                        return view;
                    }
                    view2 = findUndefinedBackground;
                }
            }
        }
        return view2;
    }
}
