package com.android.systemui.animation;

import android.graphics.Path;
import android.util.MathUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;

public class Interpolators {
    public static final Interpolator ACCELERATE = new AccelerateInterpolator();
    public static final Interpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    public static final Interpolator ALPHA_IN = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator ALPHA_OUT = new PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f);
    public static final Interpolator BOUNCE = new BounceInterpolator();
    public static final Interpolator CONTROL_STATE = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator CUSTOM_40_40 = new PathInterpolator(0.4f, 0.0f, 0.6f, 1.0f);
    public static final Interpolator DECELERATE_QUINT = new DecelerateInterpolator(2.5f);
    public static final Interpolator EMPHASIZED = createEmphasizedInterpolator();
    public static final Interpolator EMPHASIZED_ACCELERATE = new PathInterpolator(0.3f, 0.0f, 0.8f, 0.15f);
    public static final Interpolator EMPHASIZED_DECELERATE = new PathInterpolator(0.05f, 0.7f, 0.1f, 1.0f);
    public static final Interpolator FAST_OUT_LINEAR_IN;
    public static final Interpolator FAST_OUT_SLOW_IN;
    public static final Interpolator FAST_OUT_SLOW_IN_REVERSE = new PathInterpolator(0.8f, 0.0f, 0.6f, 1.0f);
    public static final Interpolator ICON_OVERSHOT = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.4f);
    public static final Interpolator ICON_OVERSHOT_LESS = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.1f);
    public static final Interpolator LEGACY;
    public static final Interpolator LEGACY_ACCELERATE;
    public static final Interpolator LEGACY_DECELERATE;
    public static final Interpolator LINEAR = new LinearInterpolator();
    public static final Interpolator LINEAR_OUT_SLOW_IN;
    public static final Interpolator PANEL_CLOSE_ACCELERATED = new PathInterpolator(0.3f, 0.0f, 0.5f, 1.0f);
    public static final Interpolator SLOW_OUT_LINEAR_IN = new PathInterpolator(0.8f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator STANDARD = new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
    public static final Interpolator STANDARD_ACCELERATE = new PathInterpolator(0.3f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator STANDARD_DECELERATE = new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Interpolator TOUCH_RESPONSE = new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
    public static final Interpolator TOUCH_RESPONSE_REVERSE = new PathInterpolator(0.9f, 0.0f, 0.7f, 1.0f);

    static {
        PathInterpolator pathInterpolator = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
        LEGACY = pathInterpolator;
        PathInterpolator pathInterpolator2 = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
        LEGACY_ACCELERATE = pathInterpolator2;
        PathInterpolator pathInterpolator3 = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
        LEGACY_DECELERATE = pathInterpolator3;
        FAST_OUT_SLOW_IN = pathInterpolator;
        FAST_OUT_LINEAR_IN = pathInterpolator2;
        LINEAR_OUT_SLOW_IN = pathInterpolator3;
    }

    public static float getOvershootInterpolation(float f, float f2, float f3) {
        if (f2 == 0.0f || f3 == 0.0f) {
            throw new IllegalArgumentException("Invalid values for overshoot");
        }
        float f4 = 1.0f + f2;
        return MathUtils.max(0.0f, ((float) (1.0d - Math.exp((double) ((-(MathUtils.log(f4 / f2) / f3)) * f)))) * f4);
    }

    public static float getOvershootInterpolation(float f) {
        return MathUtils.max(0.0f, (float) (1.0d - Math.exp((double) (f * -4.0f))));
    }

    private static PathInterpolator createEmphasizedInterpolator() {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        Path path2 = path;
        path2.cubicTo(0.05f, 0.0f, 0.133333f, 0.06f, 0.166666f, 0.4f);
        path2.cubicTo(0.208333f, 0.82f, 0.25f, 1.0f, 1.0f, 1.0f);
        return new PathInterpolator(path);
    }
}
