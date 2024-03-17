package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.RemoteViews;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.RoundedCornerEnforcement;
import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;

final class FloatingWidgetBackgroundView extends View {
    private final DrawableProperties mBackgroundProperties = new DrawableProperties();
    private final ColorDrawable mFallbackDrawable = new ColorDrawable();
    private float mFinalRadius;
    private final DrawableProperties mForegroundProperties = new DrawableProperties();
    private float mInitialOutlineRadius;
    private boolean mIsUsingFallback;
    private Drawable mOriginalBackground;
    private Drawable mOriginalForeground;
    /* access modifiers changed from: private */
    public float mOutlineRadius;
    private View mSourceView;

    FloatingWidgetBackgroundView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), FloatingWidgetBackgroundView.this.mOutlineRadius);
            }
        });
        setClipToOutline(true);
    }

    /* access modifiers changed from: package-private */
    public void init(LauncherAppWidgetHostView launcherAppWidgetHostView, View view, float f, int i) {
        this.mFinalRadius = f;
        this.mSourceView = view;
        this.mInitialOutlineRadius = getOutlineRadius(launcherAppWidgetHostView, view);
        this.mIsUsingFallback = false;
        if (isSupportedDrawable(view.getForeground())) {
            Drawable foreground = view.getForeground();
            this.mOriginalForeground = foreground;
            this.mForegroundProperties.init(foreground.getConstantState().newDrawable().mutate());
            setForeground(this.mForegroundProperties.mDrawable);
            Drawable mutate = this.mOriginalForeground.getConstantState().newDrawable().mutate();
            mutate.setAlpha(0);
            this.mSourceView.setForeground(mutate);
        }
        if (isSupportedDrawable(view.getBackground())) {
            Drawable background = view.getBackground();
            this.mOriginalBackground = background;
            this.mBackgroundProperties.init(background.getConstantState().newDrawable().mutate());
            setBackground(this.mBackgroundProperties.mDrawable);
            Drawable mutate2 = this.mOriginalBackground.getConstantState().newDrawable().mutate();
            mutate2.setAlpha(0);
            this.mSourceView.setBackground(mutate2);
        } else if (this.mOriginalForeground == null) {
            this.mFallbackDrawable.setColor(i);
            setBackground(this.mFallbackDrawable);
            this.mIsUsingFallback = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void update(float f, float f2) {
        if (!isUninitialized()) {
            float f3 = this.mInitialOutlineRadius;
            float f4 = this.mFinalRadius;
            this.mOutlineRadius = f3 + ((f4 - f3) * f);
            this.mForegroundProperties.updateDrawable(f4, f);
            this.mBackgroundProperties.updateDrawable(this.mFinalRadius, f);
            if (!this.mIsUsingFallback) {
                f2 = 1.0f;
            }
            setAlpha(f2);
        }
    }

    /* access modifiers changed from: package-private */
    public void finish() {
        if (!isUninitialized()) {
            Drawable drawable = this.mOriginalForeground;
            if (drawable != null) {
                this.mSourceView.setForeground(drawable);
            }
            Drawable drawable2 = this.mOriginalBackground;
            if (drawable2 != null) {
                this.mSourceView.setBackground(drawable2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void recycle() {
        this.mSourceView = null;
        this.mOriginalForeground = null;
        this.mOriginalBackground = null;
        this.mOutlineRadius = 0.0f;
        this.mFinalRadius = 0.0f;
        setForeground((Drawable) null);
        setBackground((Drawable) null);
    }

    /* access modifiers changed from: package-private */
    public float getMaximumRadius() {
        if (isUninitialized()) {
            return 0.0f;
        }
        return Math.max(this.mInitialOutlineRadius, Math.max(getMaxRadius(this.mOriginalForeground), getMaxRadius(this.mOriginalBackground)));
    }

    private boolean isUninitialized() {
        return this.mSourceView == null;
    }

    private static float getMaxRadius(Drawable drawable) {
        if (!(drawable instanceof GradientDrawable)) {
            return 0.0f;
        }
        GradientDrawable gradientDrawable = (GradientDrawable) drawable;
        float[] cornerRadii = gradientDrawable.getCornerRadii();
        float cornerRadius = gradientDrawable.getCornerRadius();
        double d = 0.0d;
        if (cornerRadii != null) {
            d = IntStream.range(0, cornerRadii.length).mapToDouble(new IntToDoubleFunction(cornerRadii) {
                public final /* synthetic */ float[] f$0;

                {
                    this.f$0 = r1;
                }

                public final double applyAsDouble(int i) {
                    return FloatingWidgetBackgroundView.lambda$getMaxRadius$0(this.f$0, i);
                }
            }).max().orElse(0.0d);
        }
        return Math.max(cornerRadius, (float) d);
    }

    static /* synthetic */ double lambda$getMaxRadius$0(float[] fArr, int i) {
        return (double) fArr[i];
    }

    private static boolean isSupportedDrawable(Drawable drawable) {
        return (drawable instanceof ColorDrawable) || ((drawable instanceof GradientDrawable) && ((GradientDrawable) drawable).getShape() == 0);
    }

    private static float getOutlineRadius(LauncherAppWidgetHostView launcherAppWidgetHostView, View view) {
        if (RoundedCornerEnforcement.isRoundedCornerEnabled() && launcherAppWidgetHostView.hasEnforcedCornerRadius()) {
            return launcherAppWidgetHostView.getEnforcedCornerRadius();
        }
        if (!(view.getOutlineProvider() instanceof RemoteViews.RemoteViewOutlineProvider) || !view.getClipToOutline()) {
            return 0.0f;
        }
        return view.getOutlineProvider().getRadius();
    }

    private static class DrawableProperties {
        /* access modifiers changed from: private */
        public Drawable mDrawable;
        private float[] mOriginalRadii;
        private float mOriginalRadius;
        private final float[] mTmpRadii;

        private DrawableProperties() {
            this.mTmpRadii = new float[8];
        }

        /* access modifiers changed from: package-private */
        public void init(Drawable drawable) {
            this.mDrawable = drawable;
            if (drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                this.mOriginalRadius = gradientDrawable.getCornerRadius();
                this.mOriginalRadii = gradientDrawable.getCornerRadii();
            }
        }

        /* access modifiers changed from: package-private */
        public void updateDrawable(float f, float f2) {
            Drawable drawable = this.mDrawable;
            if (drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                if (this.mOriginalRadii != null) {
                    int i = 0;
                    while (true) {
                        float[] fArr = this.mOriginalRadii;
                        if (i < fArr.length) {
                            this.mTmpRadii[i] = fArr[i] + ((f - fArr[i]) * f2);
                            i++;
                        } else {
                            gradientDrawable.setCornerRadii(this.mTmpRadii);
                            return;
                        }
                    }
                } else {
                    float f3 = this.mOriginalRadius;
                    gradientDrawable.setCornerRadius(f3 + ((f - f3) * f2));
                }
            }
        }
    }
}
