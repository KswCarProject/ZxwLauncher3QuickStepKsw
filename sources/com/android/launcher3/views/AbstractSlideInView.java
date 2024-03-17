package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.touch.BaseSwipeDetector;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSlideInView<T extends Context & ActivityContext> extends AbstractFloatingView implements SingleAxisSwipeDetector.Listener {
    protected static final Property<AbstractSlideInView, Float> TRANSLATION_SHIFT = new Property<AbstractSlideInView, Float>(Float.class, "translationShift") {
        public Float get(AbstractSlideInView abstractSlideInView) {
            return Float.valueOf(abstractSlideInView.mTranslationShift);
        }

        public void set(AbstractSlideInView abstractSlideInView, Float f) {
            abstractSlideInView.setTranslationShift(f.floatValue());
        }
    };
    protected static final float TRANSLATION_SHIFT_CLOSED = 1.0f;
    protected static final float TRANSLATION_SHIFT_OPENED = 0.0f;
    protected final T mActivityContext;
    protected final View mColorScrim;
    /* access modifiers changed from: protected */
    public ViewGroup mContent;
    protected boolean mNoIntercept;
    protected List<OnCloseListener> mOnCloseListeners = new ArrayList();
    /* access modifiers changed from: protected */
    public final ObjectAnimator mOpenCloseAnimator;
    protected Interpolator mScrollInterpolator;
    protected final SingleAxisSwipeDetector mSwipeDetector;
    protected float mTranslationShift = 1.0f;

    public interface OnCloseListener {
        void onSlideInViewClosed();
    }

    /* access modifiers changed from: protected */
    public int getScrimColor(Context context) {
        return -1;
    }

    public void onDragStart(boolean z, float f) {
    }

    public AbstractSlideInView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mActivityContext = ActivityContext.lookupContext(context);
        this.mScrollInterpolator = Interpolators.SCROLL_CUBIC;
        this.mSwipeDetector = new SingleAxisSwipeDetector(context, this, SingleAxisSwipeDetector.VERTICAL);
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[0]);
        this.mOpenCloseAnimator = ofPropertyValuesHolder;
        ofPropertyValuesHolder.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AbstractSlideInView.this.mSwipeDetector.finishedScrolling();
                AbstractSlideInView.this.announceAccessibilityChanges();
            }
        });
        int scrimColor = getScrimColor(context);
        this.mColorScrim = scrimColor != -1 ? createColorScrim(context, scrimColor) : null;
    }

    /* access modifiers changed from: protected */
    public void attachToContainer() {
        if (this.mColorScrim != null) {
            getPopupContainer().addView(this.mColorScrim);
        }
        getPopupContainer().addView(this);
    }

    /* access modifiers changed from: protected */
    public float getShiftRange() {
        return (float) this.mContent.getHeight();
    }

    /* access modifiers changed from: protected */
    public void setTranslationShift(float f) {
        this.mTranslationShift = f;
        this.mContent.setTranslationY(f * getShiftRange());
        View view = this.mColorScrim;
        if (view != null) {
            view.setAlpha(1.0f - this.mTranslationShift);
        }
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mNoIntercept) {
            return false;
        }
        this.mSwipeDetector.setDetectableScrollConditions(this.mSwipeDetector.isIdleState() ? 2 : 0, false);
        this.mSwipeDetector.onTouchEvent(motionEvent);
        if (this.mSwipeDetector.isDraggingOrSettling() || !isEventOverContent(motionEvent)) {
            return true;
        }
        return false;
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        this.mSwipeDetector.onTouchEvent(motionEvent);
        if (motionEvent.getAction() == 1 && this.mSwipeDetector.isIdleState() && !isOpeningAnimationRunning() && !isEventOverContent(motionEvent)) {
            close(true);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isEventOverContent(MotionEvent motionEvent) {
        return getPopupContainer().isEventOverView(this.mContent, motionEvent);
    }

    private boolean isOpeningAnimationRunning() {
        return this.mIsOpen && this.mOpenCloseAnimator.isRunning();
    }

    public boolean onDrag(float f) {
        float shiftRange = getShiftRange();
        setTranslationShift(Utilities.boundToRange(f, 0.0f, shiftRange) / shiftRange);
        return true;
    }

    public void onDragEnd(float f) {
        float f2 = ((ActivityContext) this.mActivityContext).getDeviceProfile().isTablet ? 0.3f : 0.5f;
        if ((!this.mSwipeDetector.isFling(f) || f <= 0.0f) && this.mTranslationShift <= f2) {
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setDuration(BaseSwipeDetector.calculateDuration(f, this.mTranslationShift)).setInterpolator(Interpolators.DEACCEL);
            this.mOpenCloseAnimator.start();
            return;
        }
        this.mScrollInterpolator = Interpolators.scrollInterpolatorForVelocity(f);
        this.mOpenCloseAnimator.setDuration(BaseSwipeDetector.calculateDuration(f, 1.0f - this.mTranslationShift));
        close(true);
    }

    public void addOnCloseListener(OnCloseListener onCloseListener) {
        this.mOnCloseListeners.add(onCloseListener);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z, long j) {
        if (this.mIsOpen) {
            if (!z) {
                this.mOpenCloseAnimator.cancel();
                setTranslationShift(1.0f);
                onCloseComplete();
                return;
            }
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{1.0f})});
            this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AbstractSlideInView.this.mOpenCloseAnimator.removeListener(this);
                    AbstractSlideInView.this.onCloseComplete();
                }
            });
            if (this.mSwipeDetector.isIdleState()) {
                this.mOpenCloseAnimator.setDuration(j).setInterpolator(getIdleInterpolator());
            } else {
                this.mOpenCloseAnimator.setInterpolator(this.mScrollInterpolator);
            }
            this.mOpenCloseAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public Interpolator getIdleInterpolator() {
        return Interpolators.ACCEL;
    }

    /* access modifiers changed from: protected */
    public void onCloseComplete() {
        this.mIsOpen = false;
        getPopupContainer().removeView(this);
        if (this.mColorScrim != null) {
            getPopupContainer().removeView(this.mColorScrim);
        }
        this.mOnCloseListeners.forEach($$Lambda$DZmqG_xiGvF3FuaigAeeojgrqBU.INSTANCE);
    }

    /* access modifiers changed from: protected */
    public BaseDragLayer getPopupContainer() {
        return ((ActivityContext) this.mActivityContext).getDragLayer();
    }

    /* access modifiers changed from: protected */
    public View createColorScrim(Context context, int i) {
        View view = new View(context);
        view.forceHasOverlappingRendering(false);
        view.setBackgroundColor(i);
        BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(-1, -1);
        layoutParams.ignoreInsets = true;
        view.setLayoutParams(layoutParams);
        return view;
    }
}
