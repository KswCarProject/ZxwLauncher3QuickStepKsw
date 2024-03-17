package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.dragndrop.FolderAdaptiveIcon;
import com.android.launcher3.graphics.IconShape;

public class ClipIconView extends View implements ClipPathView {
    private static final int FG_TRANS_X_FACTOR = 60;
    private static final int FG_TRANS_Y_FACTOR = 75;
    private static final FloatPropertyCompat<ClipIconView> mFgTransXProperty = new FloatPropertyCompat<ClipIconView>("ClipIconViewFgTransX") {
        public float getValue(ClipIconView clipIconView) {
            return clipIconView.mFgTransX;
        }

        public void setValue(ClipIconView clipIconView, float f) {
            float unused = clipIconView.mFgTransX = f;
            clipIconView.invalidate();
        }
    };
    private static final FloatPropertyCompat<ClipIconView> mFgTransYProperty = new FloatPropertyCompat<ClipIconView>("ClipIconViewFgTransY") {
        public float getValue(ClipIconView clipIconView) {
            return clipIconView.mFgTransY;
        }

        public void setValue(ClipIconView clipIconView, float f) {
            float unused = clipIconView.mFgTransY = f;
            clipIconView.invalidate();
        }
    };
    private static final Rect sTmpRect = new Rect();
    private Drawable mBackground;
    private final int mBlurSizeOutline;
    private Path mClipPath;
    private final Rect mEndRevealRect;
    private final SpringAnimation mFgSpringX;
    private final SpringAnimation mFgSpringY;
    /* access modifiers changed from: private */
    public float mFgTransX;
    /* access modifiers changed from: private */
    public float mFgTransY;
    private final Rect mFinalDrawableBounds;
    private Drawable mForeground;
    private boolean mIsAdaptiveIcon;
    private final boolean mIsRtl;
    /* access modifiers changed from: private */
    public final Rect mOutline;
    /* access modifiers changed from: private */
    public ValueAnimator mRevealAnimator;
    private final Rect mStartRevealRect;
    /* access modifiers changed from: private */
    public float mTaskCornerRadius;

    public ClipIconView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ClipIconView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ClipIconView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsAdaptiveIcon = false;
        this.mStartRevealRect = new Rect();
        this.mEndRevealRect = new Rect();
        this.mOutline = new Rect();
        this.mFinalDrawableBounds = new Rect();
        this.mBlurSizeOutline = getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        this.mIsRtl = Utilities.isRtl(getResources());
        this.mFgSpringX = new SpringAnimation(this, mFgTransXProperty).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(200.0f));
        this.mFgSpringY = new SpringAnimation(this, mFgTransYProperty).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(200.0f));
    }

    public void update(RectF rectF, float f, float f2, float f3, int i, boolean z, View view, DeviceProfile deviceProfile) {
        int i2;
        float f4;
        RectF rectF2 = rectF;
        View view2 = view;
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (this.mIsRtl) {
            f4 = rectF2.left;
            i2 = (deviceProfile.widthPx - marginLayoutParams.getMarginStart()) - marginLayoutParams.width;
        } else {
            DeviceProfile deviceProfile2 = deviceProfile;
            f4 = rectF2.left;
            i2 = marginLayoutParams.getMarginStart();
        }
        view2.setTranslationX(f4 - ((float) i2));
        view2.setTranslationY(rectF2.top - ((float) marginLayoutParams.topMargin));
        float min = (float) Math.min(marginLayoutParams.width, marginLayoutParams.height);
        float max = Math.max(1.0f, Math.min(rectF.width() / min, rectF.height() / min));
        if (!Float.isNaN(max)) {
            update(rectF, f, f2, f3, i, z, max, min, marginLayoutParams, deviceProfile);
            view2.setPivotX(0.0f);
            view2.setPivotY(0.0f);
            view2.setScaleX(max);
            view2.setScaleY(max);
            view.invalidate();
        }
    }

    private void update(RectF rectF, float f, float f2, float f3, int i, boolean z, float f4, float f5, ViewGroup.MarginLayoutParams marginLayoutParams, DeviceProfile deviceProfile) {
        int i2;
        float f6;
        int i3;
        RectF rectF2 = rectF;
        float f7 = f;
        float f8 = f2;
        ViewGroup.MarginLayoutParams marginLayoutParams2 = marginLayoutParams;
        DeviceProfile deviceProfile2 = deviceProfile;
        if (this.mIsRtl) {
            f6 = rectF2.left;
            i2 = (deviceProfile2.widthPx - marginLayoutParams.getMarginStart()) - marginLayoutParams2.width;
        } else {
            f6 = rectF2.left;
            i2 = marginLayoutParams.getMarginStart();
        }
        float f9 = f6 - ((float) i2);
        float f10 = rectF2.top - ((float) marginLayoutParams2.topMargin);
        float boundToRange = Utilities.boundToRange(Utilities.mapToRange(Math.max(f8, f7), f2, 1.0f, 0.0f, z ? 10.0f : 1.0f, Interpolators.LINEAR), 0.0f, 1.0f);
        if (deviceProfile2.isLandscape) {
            this.mOutline.right = (int) (rectF.width() / f4);
        } else {
            this.mOutline.bottom = (int) (rectF.height() / f4);
        }
        this.mTaskCornerRadius = f3 / f4;
        if (this.mIsAdaptiveIcon) {
            if (!z && f7 >= f8) {
                if (this.mRevealAnimator == null) {
                    ValueAnimator valueAnimator = (ValueAnimator) IconShape.getShape().createRevealAnimator(this, this.mStartRevealRect, this.mOutline, this.mTaskCornerRadius, !z);
                    this.mRevealAnimator = valueAnimator;
                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ValueAnimator unused = ClipIconView.this.mRevealAnimator = null;
                        }
                    });
                    this.mRevealAnimator.start();
                    this.mRevealAnimator.pause();
                }
                this.mRevealAnimator.setCurrentFraction(boundToRange);
            }
            float width = ((float) (deviceProfile2.isLandscape ? this.mOutline.width() : this.mOutline.height())) / f5;
            setBackgroundDrawableBounds(width, deviceProfile2.isLandscape);
            if (z) {
                int height = this.mFinalDrawableBounds.height();
                int width2 = this.mFinalDrawableBounds.width();
                int i4 = 0;
                if (deviceProfile2.isLandscape) {
                    i3 = 0;
                } else {
                    float f11 = (float) height;
                    i3 = (int) (((f11 * width) - f11) / 2.0f);
                }
                if (deviceProfile2.isLandscape) {
                    float f12 = (float) width2;
                    i4 = (int) (((width * f12) - f12) / 2.0f);
                }
                Rect rect = sTmpRect;
                rect.set(this.mFinalDrawableBounds);
                rect.offset(i4, i3);
                this.mForeground.setBounds(rect);
            } else {
                this.mForeground.setAlpha(i);
                this.mFgSpringX.animateToFinalPosition((float) ((int) ((f9 / ((float) deviceProfile2.availableWidthPx)) * 60.0f)));
                this.mFgSpringY.animateToFinalPosition((float) ((int) ((f10 / ((float) deviceProfile2.availableHeightPx)) * 75.0f)));
            }
        }
        invalidate();
        invalidateOutline();
    }

    private void setBackgroundDrawableBounds(float f, boolean z) {
        Rect rect = sTmpRect;
        rect.set(this.mFinalDrawableBounds);
        Utilities.scaleRectAboutCenter(rect, f);
        if (z) {
            rect.offsetTo((int) (((float) this.mFinalDrawableBounds.left) * f), rect.top);
        } else {
            rect.offsetTo(rect.left, (int) (((float) this.mFinalDrawableBounds.top) * f));
        }
        this.mBackground.setBounds(rect);
    }

    /* access modifiers changed from: protected */
    public void endReveal() {
        ValueAnimator valueAnimator = this.mRevealAnimator;
        if (valueAnimator != null) {
            valueAnimator.end();
        }
    }

    public void setIcon(Drawable drawable, int i, ViewGroup.MarginLayoutParams marginLayoutParams, boolean z, DeviceProfile deviceProfile) {
        int i2;
        boolean z2 = drawable instanceof AdaptiveIconDrawable;
        this.mIsAdaptiveIcon = z2;
        if (z2) {
            boolean z3 = drawable instanceof FolderAdaptiveIcon;
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) drawable;
            Drawable background = adaptiveIconDrawable.getBackground();
            if (background == null) {
                background = new ColorDrawable(0);
            }
            this.mBackground = background;
            Drawable foreground = adaptiveIconDrawable.getForeground();
            if (foreground == null) {
                foreground = new ColorDrawable(0);
            }
            this.mForeground = foreground;
            int i3 = marginLayoutParams.height;
            int i4 = marginLayoutParams.width;
            int i5 = this.mBlurSizeOutline / 2;
            this.mFinalDrawableBounds.set(0, 0, i4, i3);
            if (!z3) {
                int i6 = i - i5;
                this.mFinalDrawableBounds.inset(i6, i6);
            }
            this.mForeground.setBounds(this.mFinalDrawableBounds);
            this.mBackground.setBounds(this.mFinalDrawableBounds);
            this.mStartRevealRect.set(0, 0, i4, i3);
            if (!z3) {
                Utilities.scaleRectAboutCenter(this.mStartRevealRect, IconShape.getNormalizationScale());
            }
            if (deviceProfile.isLandscape) {
                marginLayoutParams.width = (int) Math.max((float) marginLayoutParams.width, ((float) marginLayoutParams.height) * deviceProfile.aspectRatio);
            } else {
                marginLayoutParams.height = (int) Math.max((float) marginLayoutParams.height, ((float) marginLayoutParams.width) * deviceProfile.aspectRatio);
            }
            if (this.mIsRtl) {
                i2 = (deviceProfile.widthPx - marginLayoutParams.getMarginStart()) - marginLayoutParams.width;
            } else {
                i2 = marginLayoutParams.leftMargin;
            }
            layout(i2, marginLayoutParams.topMargin, marginLayoutParams.width + i2, marginLayoutParams.topMargin + marginLayoutParams.height);
            float max = Math.max(((float) marginLayoutParams.height) / ((float) i3), ((float) marginLayoutParams.width) / ((float) i4));
            if (z) {
                max = 1.0f;
                this.mOutline.set(0, 0, i4, i3);
            } else {
                this.mOutline.set(0, 0, marginLayoutParams.width, marginLayoutParams.height);
            }
            setBackgroundDrawableBounds(max, deviceProfile.isLandscape);
            this.mEndRevealRect.set(0, 0, marginLayoutParams.width, marginLayoutParams.height);
            setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(ClipIconView.this.mOutline, ClipIconView.this.mTaskCornerRadius);
                }
            });
            setClipToOutline(true);
        } else {
            setBackground(drawable);
            setClipToOutline(false);
        }
        invalidate();
        invalidateOutline();
    }

    public void setClipPath(Path path) {
        this.mClipPath = path;
        invalidate();
    }

    public void draw(Canvas canvas) {
        int save = canvas.save();
        Path path = this.mClipPath;
        if (path != null) {
            canvas.clipPath(path);
        }
        super.draw(canvas);
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        if (this.mForeground != null) {
            int save2 = canvas.save();
            canvas.translate(this.mFgTransX, this.mFgTransY);
            this.mForeground.draw(canvas);
            canvas.restoreToCount(save2);
        }
        canvas.restoreToCount(save);
    }

    /* access modifiers changed from: package-private */
    public void recycle() {
        setBackground((Drawable) null);
        this.mIsAdaptiveIcon = false;
        this.mForeground = null;
        this.mBackground = null;
        this.mClipPath = null;
        this.mFinalDrawableBounds.setEmpty();
        ValueAnimator valueAnimator = this.mRevealAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mRevealAnimator = null;
        this.mTaskCornerRadius = 0.0f;
        this.mOutline.setEmpty();
        this.mFgTransY = 0.0f;
        this.mFgSpringX.cancel();
        this.mFgTransX = 0.0f;
        this.mFgSpringY.cancel();
    }
}
