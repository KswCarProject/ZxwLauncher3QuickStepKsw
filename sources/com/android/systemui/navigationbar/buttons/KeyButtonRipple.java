package com.android.systemui.navigationbar.buttons;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Trace;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.HashSet;

public class KeyButtonRipple extends Drawable {
    private static final Interpolator ALPHA_OUT_INTERPOLATOR = new PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f);
    private static final int ANIMATION_DURATION_FADE = 450;
    private static final int ANIMATION_DURATION_SCALE = 350;
    private static final float GLOW_MAX_ALPHA = 0.2f;
    private static final float GLOW_MAX_ALPHA_DARK = 0.1f;
    private static final float GLOW_MAX_SCALE_FACTOR = 1.35f;
    private final AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            KeyButtonRipple.this.mRunningAnimations.remove(animator);
            if (KeyButtonRipple.this.mRunningAnimations.isEmpty() && !KeyButtonRipple.this.mPressed) {
                boolean unused = KeyButtonRipple.this.mVisible = false;
                boolean unused2 = KeyButtonRipple.this.mDrawingHardwareGlow = false;
                KeyButtonRipple.this.invalidateSelf();
            }
        }
    };
    private CanvasProperty<Float> mBottomProp;
    private boolean mDark;
    private boolean mDelayTouchFeedback;
    /* access modifiers changed from: private */
    public boolean mDrawingHardwareGlow;
    private final TraceAnimatorListener mEnterHwTraceAnimator = new TraceAnimatorListener("enterHardware");
    private final TraceAnimatorListener mExitHwTraceAnimator = new TraceAnimatorListener("exitHardware");
    private float mGlowAlpha = 0.0f;
    private float mGlowScale = 1.0f;
    private final Handler mHandler = new Handler();
    private final Interpolator mInterpolator = new LogInterpolator();
    private boolean mLastDark;
    private CanvasProperty<Float> mLeftProp;
    private int mMaxWidth;
    private final int mMaxWidthResource;
    private CanvasProperty<Paint> mPaintProp;
    /* access modifiers changed from: private */
    public boolean mPressed;
    private CanvasProperty<Float> mRightProp;
    private Paint mRipplePaint;
    /* access modifiers changed from: private */
    public final HashSet<Animator> mRunningAnimations = new HashSet<>();
    private CanvasProperty<Float> mRxProp;
    private CanvasProperty<Float> mRyProp;
    private boolean mSupportHardware;
    private final View mTargetView;
    private final ArrayList<Animator> mTmpArray = new ArrayList<>();
    private CanvasProperty<Float> mTopProp;
    private Type mType = Type.ROUNDED_RECT;
    /* access modifiers changed from: private */
    public boolean mVisible;

    public enum Type {
        OVAL,
        ROUNDED_RECT
    }

    public int getOpacity() {
        return -3;
    }

    public boolean hasFocusStateSpecified() {
        return true;
    }

    public boolean isStateful() {
        return true;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public KeyButtonRipple(Context context, View view, int i) {
        this.mMaxWidthResource = i;
        this.mMaxWidth = context.getResources().getDimensionPixelSize(i);
        this.mTargetView = view;
    }

    public void updateResources() {
        this.mMaxWidth = this.mTargetView.getContext().getResources().getDimensionPixelSize(this.mMaxWidthResource);
        invalidateSelf();
    }

    public void setDarkIntensity(float f) {
        this.mDark = f >= 0.5f;
    }

    public void setDelayTouchFeedback(boolean z) {
        this.mDelayTouchFeedback = z;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    private Paint getRipplePaint() {
        if (this.mRipplePaint == null) {
            Paint paint = new Paint();
            this.mRipplePaint = paint;
            paint.setAntiAlias(true);
            this.mRipplePaint.setColor(this.mLastDark ? ViewCompat.MEASURED_STATE_MASK : -1);
        }
        return this.mRipplePaint;
    }

    private void drawSoftware(Canvas canvas) {
        if (this.mGlowAlpha > 0.0f) {
            Paint ripplePaint = getRipplePaint();
            ripplePaint.setAlpha((int) (this.mGlowAlpha * 255.0f));
            float width = (float) getBounds().width();
            float height = (float) getBounds().height();
            boolean z = width > height;
            float rippleSize = ((float) getRippleSize()) * this.mGlowScale * 0.5f;
            float f = width * 0.5f;
            float f2 = height * 0.5f;
            float f3 = z ? rippleSize : f;
            if (z) {
                rippleSize = f2;
            }
            float f4 = z ? f2 : f;
            if (this.mType == Type.ROUNDED_RECT) {
                canvas.drawRoundRect(f - f3, f2 - rippleSize, f3 + f, f2 + rippleSize, f4, f4, ripplePaint);
                return;
            }
            canvas.save();
            canvas.translate(f, f2);
            float min = Math.min(f3, rippleSize);
            float f5 = -min;
            canvas.drawOval(f5, f5, min, min, ripplePaint);
            canvas.restore();
        }
    }

    public void draw(Canvas canvas) {
        boolean isHardwareAccelerated = canvas.isHardwareAccelerated();
        this.mSupportHardware = isHardwareAccelerated;
        if (isHardwareAccelerated) {
            drawHardware((RecordingCanvas) canvas);
        } else {
            drawSoftware(canvas);
        }
    }

    private boolean isHorizontal() {
        return getBounds().width() > getBounds().height();
    }

    private void drawHardware(RecordingCanvas recordingCanvas) {
        if (!this.mDrawingHardwareGlow) {
            return;
        }
        if (this.mType == Type.ROUNDED_RECT) {
            recordingCanvas.drawRoundRect(this.mLeftProp, this.mTopProp, this.mRightProp, this.mBottomProp, this.mRxProp, this.mRyProp, this.mPaintProp);
            return;
        }
        recordingCanvas.drawCircle(CanvasProperty.createFloat((float) (getBounds().width() / 2)), CanvasProperty.createFloat((float) (getBounds().height() / 2)), CanvasProperty.createFloat((((float) Math.min(getBounds().width(), getBounds().height())) * 1.0f) / 2.0f), this.mPaintProp);
    }

    public float getGlowAlpha() {
        return this.mGlowAlpha;
    }

    public void setGlowAlpha(float f) {
        this.mGlowAlpha = f;
        invalidateSelf();
    }

    public float getGlowScale() {
        return this.mGlowScale;
    }

    public void setGlowScale(float f) {
        this.mGlowScale = f;
        invalidateSelf();
    }

    private float getMaxGlowAlpha() {
        return this.mLastDark ? 0.1f : 0.2f;
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        boolean z;
        int i = 0;
        while (true) {
            if (i >= iArr.length) {
                z = false;
                break;
            } else if (iArr[i] == 16842919) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (z == this.mPressed) {
            return false;
        }
        setPressed(z);
        this.mPressed = z;
        return true;
    }

    public boolean setVisible(boolean z, boolean z2) {
        boolean visible = super.setVisible(z, z2);
        if (visible) {
            jumpToCurrentState();
        }
        return visible;
    }

    public void jumpToCurrentState() {
        endAnimations("jumpToCurrentState", false);
    }

    public void setPressed(boolean z) {
        boolean z2 = this.mDark;
        if (z2 != this.mLastDark && z) {
            this.mRipplePaint = null;
            this.mLastDark = z2;
        }
        if (this.mSupportHardware) {
            setPressedHardware(z);
        } else {
            setPressedSoftware(z);
        }
    }

    public void abortDelayedRipple() {
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    private void endAnimations(String str, boolean z) {
        Trace.beginSection("KeyButtonRipple.endAnim: reason=" + str + " cancel=" + z);
        Trace.endSection();
        this.mVisible = false;
        this.mTmpArray.addAll(this.mRunningAnimations);
        int size = this.mTmpArray.size();
        for (int i = 0; i < size; i++) {
            Animator animator = this.mTmpArray.get(i);
            if (z) {
                animator.cancel();
            } else {
                animator.end();
            }
        }
        this.mTmpArray.clear();
        this.mRunningAnimations.clear();
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    private void setPressedSoftware(boolean z) {
        if (!z) {
            exitSoftware();
        } else if (!this.mDelayTouchFeedback) {
            enterSoftware();
        } else if (this.mRunningAnimations.isEmpty()) {
            this.mHandler.removeCallbacksAndMessages((Object) null);
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    KeyButtonRipple.this.enterSoftware();
                }
            }, (long) ViewConfiguration.getTapTimeout());
        } else if (this.mVisible) {
            enterSoftware();
        }
    }

    /* access modifiers changed from: private */
    public void enterSoftware() {
        endAnimations("enterSoftware", true);
        this.mVisible = true;
        this.mGlowAlpha = getMaxGlowAlpha();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "glowScale", new float[]{0.0f, GLOW_MAX_SCALE_FACTOR});
        ofFloat.setInterpolator(this.mInterpolator);
        ofFloat.setDuration(350);
        ofFloat.addListener(this.mAnimatorListener);
        ofFloat.start();
        this.mRunningAnimations.add(ofFloat);
        if (this.mDelayTouchFeedback && !this.mPressed) {
            exitSoftware();
        }
    }

    private void exitSoftware() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "glowAlpha", new float[]{this.mGlowAlpha, 0.0f});
        ofFloat.setInterpolator(ALPHA_OUT_INTERPOLATOR);
        ofFloat.setDuration(450);
        ofFloat.addListener(this.mAnimatorListener);
        ofFloat.start();
        this.mRunningAnimations.add(ofFloat);
    }

    private void setPressedHardware(boolean z) {
        if (!z) {
            exitHardware();
        } else if (!this.mDelayTouchFeedback) {
            enterHardware();
        } else if (this.mRunningAnimations.isEmpty()) {
            this.mHandler.removeCallbacksAndMessages((Object) null);
            this.mHandler.postDelayed(new Runnable() {
                public final void run() {
                    KeyButtonRipple.this.enterHardware();
                }
            }, (long) ViewConfiguration.getTapTimeout());
        } else if (this.mVisible) {
            enterHardware();
        }
    }

    private void setExtendStart(CanvasProperty<Float> canvasProperty) {
        if (isHorizontal()) {
            this.mLeftProp = canvasProperty;
        } else {
            this.mTopProp = canvasProperty;
        }
    }

    private CanvasProperty<Float> getExtendStart() {
        return isHorizontal() ? this.mLeftProp : this.mTopProp;
    }

    private void setExtendEnd(CanvasProperty<Float> canvasProperty) {
        if (isHorizontal()) {
            this.mRightProp = canvasProperty;
        } else {
            this.mBottomProp = canvasProperty;
        }
    }

    private CanvasProperty<Float> getExtendEnd() {
        return isHorizontal() ? this.mRightProp : this.mBottomProp;
    }

    private int getExtendSize() {
        return isHorizontal() ? getBounds().width() : getBounds().height();
    }

    private int getRippleSize() {
        return Math.min(isHorizontal() ? getBounds().width() : getBounds().height(), this.mMaxWidth);
    }

    /* access modifiers changed from: private */
    public void enterHardware() {
        endAnimations("enterHardware", true);
        this.mVisible = true;
        this.mDrawingHardwareGlow = true;
        setExtendStart(CanvasProperty.createFloat((float) (getExtendSize() / 2)));
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(getExtendStart(), ((float) (getExtendSize() / 2)) - ((((float) getRippleSize()) * GLOW_MAX_SCALE_FACTOR) / 2.0f));
        renderNodeAnimator.setDuration(350);
        renderNodeAnimator.setInterpolator(this.mInterpolator);
        renderNodeAnimator.addListener(this.mAnimatorListener);
        renderNodeAnimator.setTarget(this.mTargetView);
        setExtendEnd(CanvasProperty.createFloat((float) (getExtendSize() / 2)));
        RenderNodeAnimator renderNodeAnimator2 = new RenderNodeAnimator(getExtendEnd(), ((float) (getExtendSize() / 2)) + ((((float) getRippleSize()) * GLOW_MAX_SCALE_FACTOR) / 2.0f));
        renderNodeAnimator2.setDuration(350);
        renderNodeAnimator2.setInterpolator(this.mInterpolator);
        renderNodeAnimator2.addListener(this.mAnimatorListener);
        renderNodeAnimator2.addListener(this.mEnterHwTraceAnimator);
        renderNodeAnimator2.setTarget(this.mTargetView);
        if (isHorizontal()) {
            this.mTopProp = CanvasProperty.createFloat(0.0f);
            this.mBottomProp = CanvasProperty.createFloat((float) getBounds().height());
            this.mRxProp = CanvasProperty.createFloat((float) (getBounds().height() / 2));
            this.mRyProp = CanvasProperty.createFloat((float) (getBounds().height() / 2));
        } else {
            this.mLeftProp = CanvasProperty.createFloat(0.0f);
            this.mRightProp = CanvasProperty.createFloat((float) getBounds().width());
            this.mRxProp = CanvasProperty.createFloat((float) (getBounds().width() / 2));
            this.mRyProp = CanvasProperty.createFloat((float) (getBounds().width() / 2));
        }
        this.mGlowScale = GLOW_MAX_SCALE_FACTOR;
        this.mGlowAlpha = getMaxGlowAlpha();
        Paint ripplePaint = getRipplePaint();
        this.mRipplePaint = ripplePaint;
        ripplePaint.setAlpha((int) (this.mGlowAlpha * 255.0f));
        this.mPaintProp = CanvasProperty.createPaint(this.mRipplePaint);
        renderNodeAnimator.start();
        renderNodeAnimator2.start();
        this.mRunningAnimations.add(renderNodeAnimator);
        this.mRunningAnimations.add(renderNodeAnimator2);
        invalidateSelf();
        if (this.mDelayTouchFeedback && !this.mPressed) {
            exitHardware();
        }
    }

    private void exitHardware() {
        this.mPaintProp = CanvasProperty.createPaint(getRipplePaint());
        RenderNodeAnimator renderNodeAnimator = new RenderNodeAnimator(this.mPaintProp, 1, 0.0f);
        renderNodeAnimator.setDuration(450);
        renderNodeAnimator.setInterpolator(ALPHA_OUT_INTERPOLATOR);
        renderNodeAnimator.addListener(this.mAnimatorListener);
        renderNodeAnimator.addListener(this.mExitHwTraceAnimator);
        renderNodeAnimator.setTarget(this.mTargetView);
        renderNodeAnimator.start();
        this.mRunningAnimations.add(renderNodeAnimator);
        invalidateSelf();
    }

    private static final class TraceAnimatorListener extends AnimatorListenerAdapter {
        private final String mName;

        TraceAnimatorListener(String str) {
            this.mName = str;
        }

        public void onAnimationStart(Animator animator) {
            Trace.beginSection("KeyButtonRipple.start." + this.mName);
            Trace.endSection();
        }

        public void onAnimationCancel(Animator animator) {
            Trace.beginSection("KeyButtonRipple.cancel." + this.mName);
            Trace.endSection();
        }

        public void onAnimationEnd(Animator animator) {
            Trace.beginSection("KeyButtonRipple.end." + this.mName);
            Trace.endSection();
        }
    }

    private static final class LogInterpolator implements Interpolator {
        private LogInterpolator() {
        }

        public float getInterpolation(float f) {
            return 1.0f - ((float) Math.pow(400.0d, ((double) (-f)) * 1.4d));
        }
    }
}
