package com.android.launcher3.anim;

import android.graphics.Path;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import com.android.launcher3.Utilities;

public class Interpolators {
    public static final Interpolator ACCEL = new AccelerateInterpolator();
    public static final Interpolator ACCELERATED_EASE = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator ACCEL_0_5 = new AccelerateInterpolator(0.5f);
    public static final Interpolator ACCEL_0_75 = new AccelerateInterpolator(0.75f);
    public static final Interpolator ACCEL_1_5 = new AccelerateInterpolator(1.5f);
    public static final Interpolator ACCEL_2 = new AccelerateInterpolator(2.0f);
    public static final Interpolator ACCEL_DEACCEL = new AccelerateDecelerateInterpolator();
    public static final Interpolator AGGRESSIVE_EASE = new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
    public static final Interpolator AGGRESSIVE_EASE_IN_OUT = new PathInterpolator(0.6f, 0.0f, 0.4f, 1.0f);
    public static final Interpolator DEACCEL = new DecelerateInterpolator();
    public static final Interpolator DEACCEL_1_5 = new DecelerateInterpolator(1.5f);
    public static final Interpolator DEACCEL_1_7 = new DecelerateInterpolator(1.7f);
    public static final Interpolator DEACCEL_2 = new DecelerateInterpolator(2.0f);
    public static final Interpolator DEACCEL_2_5 = new DecelerateInterpolator(2.5f);
    public static final Interpolator DEACCEL_3 = new DecelerateInterpolator(3.0f);
    public static final Interpolator DECELERATED_EASE = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator EMPHASIZED_ACCELERATE = new PathInterpolator(0.3f, 0.0f, 0.8f, 0.15f);
    public static final Interpolator EMPHASIZED_DECELERATE = new PathInterpolator(0.05f, 0.7f, 0.1f, 1.0f);
    public static final Interpolator EXAGGERATED_EASE;
    private static final float FAST_FLING_PX_MS = 10.0f;
    public static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator FINAL_FRAME = $$Lambda$Interpolators$BYh4UQN8RfS1ruklDaswjWbjWc.INSTANCE;
    public static final Interpolator INSTANT = $$Lambda$Interpolators$1MtJuxUJHSF4_AFPGh6NDb0oWHM.INSTANCE;
    public static final Interpolator LINEAR = new LinearInterpolator();
    public static final Interpolator OVERSHOOT_1_2 = new OvershootInterpolator(1.2f);
    public static final Interpolator OVERSHOOT_1_7 = new OvershootInterpolator(1.7f);
    public static final Interpolator SCROLL = new Interpolator() {
        public float getInterpolation(float f) {
            float f2 = f - 1.0f;
            return (f2 * f2 * f2 * f2 * f2) + 1.0f;
        }
    };
    public static final Interpolator SCROLL_CUBIC = new Interpolator() {
        public float getInterpolation(float f) {
            float f2 = f - 1.0f;
            return (f2 * f2 * f2) + 1.0f;
        }
    };
    public static final Interpolator TOUCH_RESPONSE_INTERPOLATOR = new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
    public static final Interpolator TOUCH_RESPONSE_INTERPOLATOR_ACCEL_DEACCEL = $$Lambda$Interpolators$EOVZRYL3WTZQ1fJuYRDvkggS3PY.INSTANCE;
    public static final Interpolator ZOOM_IN = new Interpolator() {
        public float getInterpolation(float f) {
            return Interpolators.DEACCEL_3.getInterpolation(1.0f - Interpolators.ZOOM_OUT.getInterpolation(1.0f - f));
        }
    };
    public static final Interpolator ZOOM_OUT = new Interpolator() {
        private static final float FOCAL_LENGTH = 0.35f;

        private float zInterpolate(float f) {
            return (1.0f - (FOCAL_LENGTH / (f + FOCAL_LENGTH))) / 0.7407408f;
        }

        public float getInterpolation(float f) {
            return zInterpolate(f);
        }
    };

    static /* synthetic */ float lambda$static$0(float f) {
        return 1.0f;
    }

    static /* synthetic */ float lambda$static$1(float f) {
        return f < 1.0f ? 0.0f : 1.0f;
    }

    static {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        Path path2 = path;
        path2.cubicTo(0.05f, 0.0f, 0.133333f, 0.08f, 0.166666f, 0.4f);
        path2.cubicTo(0.225f, 0.94f, 0.5f, 1.0f, 1.0f, 1.0f);
        EXAGGERATED_EASE = new PathInterpolator(path);
    }

    public static Interpolator scrollInterpolatorForVelocity(float f) {
        return Math.abs(f) > FAST_FLING_PX_MS ? SCROLL : SCROLL_CUBIC;
    }

    public static Interpolator overshootInterpolatorForVelocity(float f) {
        return new OvershootInterpolator(Math.min(Math.abs(f), 3.0f));
    }

    public static Interpolator clampToProgress(Interpolator interpolator, float f, float f2) {
        if (f2 >= f) {
            return new Interpolator(interpolator, f, f2) {
                public final /* synthetic */ Interpolator f$0;
                public final /* synthetic */ float f$1;
                public final /* synthetic */ float f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final float getInterpolation(float f) {
                    return Interpolators.clampToProgress(this.f$0, f, this.f$1, this.f$2);
                }
            };
        }
        throw new IllegalArgumentException(String.format("upperBound (%f) must be greater than lowerBound (%f)", new Object[]{Float.valueOf(f2), Float.valueOf(f)}));
    }

    public static float clampToProgress(Interpolator interpolator, float f, float f2, float f3) {
        if (f3 < f2) {
            throw new IllegalArgumentException(String.format("upperBound (%f) must be greater than lowerBound (%f)", new Object[]{Float.valueOf(f3), Float.valueOf(f2)}));
        } else if (f == f2 && f == f3) {
            return f == 0.0f ? 0.0f : 1.0f;
        } else {
            if (f < f2) {
                return 0.0f;
            }
            if (f > f3) {
                return 1.0f;
            }
            return interpolator.getInterpolation((f - f2) / (f3 - f2));
        }
    }

    public static float clampToProgress(float f, float f2, float f3) {
        return clampToProgress(LINEAR, f, f2, f3);
    }

    public static Interpolator mapToProgress(Interpolator interpolator, float f, float f2) {
        return new Interpolator(interpolator, f, f2) {
            public final /* synthetic */ Interpolator f$0;
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final float getInterpolation(float f) {
                return Utilities.mapRange(this.f$0.getInterpolation(f), this.f$1, this.f$2);
            }
        };
    }

    static /* synthetic */ float lambda$reverse$5(Interpolator interpolator, float f) {
        return 1.0f - interpolator.getInterpolation(1.0f - f);
    }

    public static Interpolator reverse(Interpolator interpolator) {
        return new Interpolator(interpolator) {
            public final /* synthetic */ Interpolator f$0;

            {
                this.f$0 = r1;
            }

            public final float getInterpolation(float f) {
                return Interpolators.lambda$reverse$5(this.f$0, f);
            }
        };
    }
}
