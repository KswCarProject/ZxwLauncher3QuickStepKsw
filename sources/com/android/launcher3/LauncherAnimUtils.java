package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.util.MultiScalePropertyFactory;

public class LauncherAnimUtils {
    public static final IntProperty<Drawable> DRAWABLE_ALPHA = new IntProperty<Drawable>("drawableAlpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Integer) obj2);
        }

        public Integer get(Drawable drawable) {
            return Integer.valueOf(drawable.getAlpha());
        }

        public void setValue(Drawable drawable, int i) {
            drawable.setAlpha(i);
        }
    };
    public static final MultiScalePropertyFactory<Hotseat> HOTSEAT_SCALE_PROPERTY_FACTORY = new MultiScalePropertyFactory<>("hotseat_scale_property");
    public static final IntProperty<ViewGroup.LayoutParams> LAYOUT_HEIGHT = new IntProperty<ViewGroup.LayoutParams>("height") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Integer) obj2);
        }

        public Integer get(ViewGroup.LayoutParams layoutParams) {
            return Integer.valueOf(layoutParams.height);
        }

        public void setValue(ViewGroup.LayoutParams layoutParams, int i) {
            layoutParams.height = i;
        }
    };
    public static final IntProperty<ViewGroup.LayoutParams> LAYOUT_WIDTH = new IntProperty<ViewGroup.LayoutParams>("width") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Integer) obj2);
        }

        public Integer get(ViewGroup.LayoutParams layoutParams) {
            return Integer.valueOf(layoutParams.width);
        }

        public void setValue(ViewGroup.LayoutParams layoutParams, int i) {
            layoutParams.width = i;
        }
    };
    public static final int SCALE_INDEX_REVEAL_ANIM = 4;
    public static final int SCALE_INDEX_UNFOLD_ANIMATION = 1;
    public static final int SCALE_INDEX_UNLOCK_ANIMATION = 2;
    public static final int SCALE_INDEX_WORKSPACE_STATE = 3;
    public static final FloatProperty<View> SCALE_PROPERTY = new FloatProperty<View>("scale") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(View view) {
            return Float.valueOf(view.getScaleX());
        }

        public void setValue(View view, float f) {
            view.setScaleX(f);
            view.setScaleY(f);
        }
    };
    public static final int SPRING_LOADED_EXIT_DELAY = 500;
    public static final float SUCCESS_TRANSITION_PROGRESS = 0.5f;
    public static final float TABLET_BOTTOM_SHEET_SUCCESS_TRANSITION_PROGRESS = 0.3f;
    public static final FloatProperty<View> VIEW_ALPHA;
    public static final IntProperty<View> VIEW_BACKGROUND_COLOR = new IntProperty<View>("backgroundColor") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Integer) obj2);
        }

        public void setValue(View view, int i) {
            view.setBackgroundColor(i);
        }

        public Integer get(View view) {
            if (!(view.getBackground() instanceof ColorDrawable)) {
                return 0;
            }
            return Integer.valueOf(((ColorDrawable) view.getBackground()).getColor());
        }
    };
    public static final FloatProperty<View> VIEW_TRANSLATE_X;
    public static final FloatProperty<View> VIEW_TRANSLATE_Y;
    public static final MultiScalePropertyFactory<Workspace<?>> WORKSPACE_SCALE_PROPERTY_FACTORY = new MultiScalePropertyFactory<>("workspace_scale_property");

    static {
        FloatProperty<View> floatProperty;
        FloatProperty<View> floatProperty2;
        FloatProperty<View> floatProperty3;
        if (View.TRANSLATION_X instanceof FloatProperty) {
            floatProperty = (FloatProperty) View.TRANSLATION_X;
        } else {
            floatProperty = new FloatProperty<View>("translateX") {
                public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
                    super.set(obj, (Float) obj2);
                }

                public void setValue(View view, float f) {
                    view.setTranslationX(f);
                }

                public Float get(View view) {
                    return Float.valueOf(view.getTranslationX());
                }
            };
        }
        VIEW_TRANSLATE_X = floatProperty;
        if (View.TRANSLATION_Y instanceof FloatProperty) {
            floatProperty2 = (FloatProperty) View.TRANSLATION_Y;
        } else {
            floatProperty2 = new FloatProperty<View>("translateY") {
                public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
                    super.set(obj, (Float) obj2);
                }

                public void setValue(View view, float f) {
                    view.setTranslationY(f);
                }

                public Float get(View view) {
                    return Float.valueOf(view.getTranslationY());
                }
            };
        }
        VIEW_TRANSLATE_Y = floatProperty2;
        if (View.ALPHA instanceof FloatProperty) {
            floatProperty3 = (FloatProperty) View.ALPHA;
        } else {
            floatProperty3 = new FloatProperty<View>("alpha") {
                public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
                    super.set(obj, (Float) obj2);
                }

                public void setValue(View view, float f) {
                    view.setAlpha(f);
                }

                public Float get(View view) {
                    return Float.valueOf(view.getAlpha());
                }
            };
        }
        VIEW_ALPHA = floatProperty3;
    }

    public static int blockedFlingDurationFactor(float f) {
        return (int) Utilities.boundToRange(Math.abs(f) / 2.0f, 2.0f, 6.0f);
    }

    public static Animator.AnimatorListener newCancelListener(final Runnable runnable) {
        return new AnimatorListenerAdapter() {
            boolean mDispatched = false;

            public void onAnimationCancel(Animator animator) {
                if (!this.mDispatched) {
                    this.mDispatched = true;
                    runnable.run();
                }
            }
        };
    }
}
