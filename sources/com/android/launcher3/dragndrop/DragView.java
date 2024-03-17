package com.android.launcher3.dragndrop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import java.util.Objects;

public abstract class DragView<T extends Context & ActivityContext> extends FrameLayout {
    public static final int VIEW_ZOOM_DURATION = 150;
    protected final T mActivity;
    final ValueAnimator mAnim;
    /* access modifiers changed from: private */
    public boolean mAnimStarted;
    /* access modifiers changed from: private */
    public int mAnimatedShiftX;
    /* access modifiers changed from: private */
    public int mAnimatedShiftY;
    private Drawable mBadge;
    private Drawable mBgSpringDrawable;
    private final int mBlurSizeOutline;
    private final View mContent;
    private int mContentViewInParentViewIndex;
    private ViewGroup.LayoutParams mContentViewLayoutParams;
    private ViewGroup mContentViewParent;
    private final BaseDragLayer<T> mDragLayer;
    private Rect mDragRegion;
    private Point mDragVisualizeOffset;
    private Drawable mFgSpringDrawable;
    private boolean mHasDrawn;
    private final int mHeight;
    private final float mInitialScale;
    private int mLastTouchX;
    private int mLastTouchY;
    /* access modifiers changed from: private */
    public Runnable mOnAnimEndCallback;
    private final RunnableList mOnDragStartCallback;
    protected final int mRegistrationX;
    protected final int mRegistrationY;
    protected final float mScaleOnDrop;
    private Path mScaledMaskPath;
    protected final int[] mTempLoc;
    private SpringFloatValue mTranslateX;
    private SpringFloatValue mTranslateY;
    private final int mWidth;

    public abstract void animateTo(int i, int i2, Runnable runnable, int i3);

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public DragView(T t, Drawable drawable, int i, int i2, float f, float f2, float f3) {
        this(t, getViewFromDrawable(t, drawable), drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), i, i2, f, f2, f3);
    }

    public DragView(T t, View view, int i, int i2, int i3, int i4, float f, float f2, float f3) {
        super(t);
        this.mContentViewInParentViewIndex = -1;
        this.mTempLoc = new int[2];
        this.mOnDragStartCallback = new RunnableList();
        this.mDragVisualizeOffset = null;
        this.mDragRegion = null;
        this.mHasDrawn = false;
        this.mOnAnimEndCallback = null;
        this.mActivity = t;
        this.mDragLayer = ((ActivityContext) t).getDragLayer();
        this.mContent = view;
        this.mWidth = i;
        this.mHeight = i2;
        this.mContentViewLayoutParams = view.getLayoutParams();
        if (view.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            this.mContentViewParent = viewGroup;
            this.mContentViewInParentViewIndex = viewGroup.indexOfChild(view);
            this.mContentViewParent.removeView(view);
        }
        addView(view, new FrameLayout.LayoutParams(i, i2));
        if (!(view.getScaleX() == 1.0f && view.getScaleY() == 1.0f)) {
            setClipChildren(false);
            setClipToPadding(false);
        }
        float f4 = (float) i;
        float f5 = (f3 + f4) / f4;
        setScaleX(f);
        setScaleY(f);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mAnim = ofFloat;
        ofFloat.setDuration(150);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(f, f5) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DragView.this.lambda$new$0$DragView(this.f$1, this.f$2, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                boolean unused = DragView.this.mAnimStarted = true;
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (DragView.this.mOnAnimEndCallback != null) {
                    DragView.this.mOnAnimEndCallback.run();
                }
            }
        });
        setDragRegion(new Rect(0, 0, i, i2));
        this.mRegistrationX = i3;
        this.mRegistrationY = i4;
        this.mInitialScale = f;
        this.mScaleOnDrop = f2;
        measure(View.MeasureSpec.makeMeasureSpec(i, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(i2, BasicMeasure.EXACTLY));
        this.mBlurSizeOutline = getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        setElevation(getResources().getDimension(R.dimen.drag_elevation));
        setWillNotDraw(false);
    }

    public /* synthetic */ void lambda$new$0$DragView(float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = f + (((Float) valueAnimator.getAnimatedValue()).floatValue() * (f2 - f));
        setScaleX(floatValue);
        setScaleY(floatValue);
        if (!isAttachedToWindow()) {
            valueAnimator.cancel();
        }
    }

    public void setOnAnimationEndCallback(Runnable runnable) {
        this.mOnAnimEndCallback = runnable;
    }

    public void setItemInfo(ItemInfo itemInfo) {
        if (itemInfo.itemType == 0 || itemInfo.itemType == 7 || itemInfo.itemType == 6 || itemInfo.itemType == 2) {
            Executors.MODEL_EXECUTOR.getHandler().postAtFrontOfQueue(new Runnable(itemInfo) {
                public final /* synthetic */ ItemInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DragView.this.lambda$setItemInfo$3$DragView(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setItemInfo$3$DragView(ItemInfo itemInfo) {
        Drawable drawable;
        Object[] objArr = new Object[1];
        int i = this.mWidth;
        int i2 = this.mHeight;
        Drawable fullDrawable = Utilities.getFullDrawable(this.mActivity, itemInfo, i, i2, true, objArr);
        if (fullDrawable instanceof AdaptiveIconDrawable) {
            int dimension = ((int) this.mActivity.getResources().getDimension(R.dimen.blur_size_medium_outline)) / 2;
            Rect rect = new Rect(0, 0, i, i2);
            rect.inset(dimension, dimension);
            Drawable badge = Utilities.getBadge(this.mActivity, itemInfo, objArr[0]);
            this.mBadge = badge;
            FastBitmapDrawable.setBadgeBounds(badge, rect);
            boolean z = true ^ (fullDrawable instanceof FolderAdaptiveIcon);
            LauncherIcons obtain = LauncherIcons.obtain(this.mActivity);
            if (z) {
                drawable = fullDrawable;
            } else {
                try {
                    drawable = new AdaptiveIconDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK), (Drawable) null);
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
            Utilities.scaleRectAboutCenter(rect, obtain.getNormalizer().getScale(drawable, (RectF) null, (Path) null, (boolean[]) null));
            if (obtain != null) {
                obtain.close();
            }
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) fullDrawable;
            Rect rect2 = new Rect(rect);
            Utilities.scaleRectAboutCenter(rect2, 0.98f);
            adaptiveIconDrawable.setBounds(rect2);
            Path iconMask = adaptiveIconDrawable.getIconMask();
            this.mTranslateX = new SpringFloatValue(this, ((float) i) * AdaptiveIconDrawable.getExtraInsetFraction());
            this.mTranslateY = new SpringFloatValue(this, ((float) i2) * AdaptiveIconDrawable.getExtraInsetFraction());
            rect.inset((int) (((float) (-rect.width())) * AdaptiveIconDrawable.getExtraInsetFraction()), (int) (((float) (-rect.height())) * AdaptiveIconDrawable.getExtraInsetFraction()));
            Drawable background = adaptiveIconDrawable.getBackground();
            this.mBgSpringDrawable = background;
            if (background == null) {
                this.mBgSpringDrawable = new ColorDrawable(0);
            }
            this.mBgSpringDrawable.setBounds(rect);
            Drawable foreground = adaptiveIconDrawable.getForeground();
            this.mFgSpringDrawable = foreground;
            if (foreground == null) {
                this.mFgSpringDrawable = new ColorDrawable(0);
            }
            this.mFgSpringDrawable.setBounds(rect);
            new Handler(Looper.getMainLooper()).post(new Runnable(iconMask, itemInfo) {
                public final /* synthetic */ Path f$1;
                public final /* synthetic */ ItemInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DragView.this.lambda$setItemInfo$2$DragView(this.f$1, this.f$2);
                }
            });
            return;
        }
        return;
        throw th;
    }

    public /* synthetic */ void lambda$setItemInfo$2$DragView(Path path, ItemInfo itemInfo) {
        this.mOnDragStartCallback.add(new Runnable(path, itemInfo) {
            public final /* synthetic */ Path f$1;
            public final /* synthetic */ ItemInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DragView.this.lambda$setItemInfo$1$DragView(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$setItemInfo$1$DragView(Path path, ItemInfo itemInfo) {
        this.mScaledMaskPath = path;
        removeAllViewsInLayout();
        if (itemInfo.isDisabled()) {
            ColorFilter disabledColorFilter = FastBitmapDrawable.getDisabledColorFilter();
            this.mBgSpringDrawable.setColorFilter(disabledColorFilter);
            this.mFgSpringDrawable.setColorFilter(disabledColorFilter);
            this.mBadge.setColorFilter(disabledColorFilter);
        }
        invalidate();
    }

    public void onDragStart() {
        this.mOnDragStartCallback.executeAllAndDestroy();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.mWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.mHeight, BasicMeasure.EXACTLY));
    }

    public int getDragRegionWidth() {
        return this.mDragRegion.width();
    }

    public int getDragRegionHeight() {
        return this.mDragRegion.height();
    }

    public void setDragVisualizeOffset(Point point) {
        this.mDragVisualizeOffset = point;
    }

    public Point getDragVisualizeOffset() {
        return this.mDragVisualizeOffset;
    }

    public void setDragRegion(Rect rect) {
        this.mDragRegion = rect;
    }

    public Rect getDragRegion() {
        return this.mDragRegion;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.mHasDrawn = true;
        if (this.mScaledMaskPath != null) {
            int save = canvas.save();
            canvas.clipPath(this.mScaledMaskPath);
            this.mBgSpringDrawable.draw(canvas);
            canvas.translate(this.mTranslateX.mValue, this.mTranslateY.mValue);
            this.mFgSpringDrawable.draw(canvas);
            canvas.restoreToCount(save);
            this.mBadge.draw(canvas);
        }
    }

    public void crossFadeContent(Drawable drawable, int i) {
        if (this.mContent.getParent() != null) {
            View viewFromDrawable = getViewFromDrawable(getContext(), drawable);
            viewFromDrawable.measure(View.MeasureSpec.makeMeasureSpec(this.mWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.mHeight, BasicMeasure.EXACTLY));
            viewFromDrawable.layout(0, 0, this.mWidth, this.mHeight);
            addViewInLayout(viewFromDrawable, 0, new FrameLayout.LayoutParams(this.mWidth, this.mHeight));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(ObjectAnimator.ofFloat(viewFromDrawable, LauncherAnimUtils.VIEW_ALPHA, new float[]{0.0f, 1.0f}));
            animatorSet.play(ObjectAnimator.ofFloat(this.mContent, LauncherAnimUtils.VIEW_ALPHA, new float[]{0.0f}));
            animatorSet.setDuration((long) i).setInterpolator(Interpolators.DEACCEL_1_5);
            animatorSet.start();
        }
    }

    public boolean hasDrawn() {
        return this.mHasDrawn;
    }

    public void show(int i, int i2) {
        this.mDragLayer.addView(this);
        BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(this.mWidth, this.mHeight);
        layoutParams.customPosition = true;
        setLayoutParams(layoutParams);
        View view = this.mContent;
        if (view != null) {
            view.setVisibility(0);
        }
        move(i, i2);
        ValueAnimator valueAnimator = this.mAnim;
        Objects.requireNonNull(valueAnimator);
        post(new Runnable(valueAnimator) {
            public final /* synthetic */ ValueAnimator f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.start();
            }
        });
    }

    public void cancelAnimation() {
        ValueAnimator valueAnimator = this.mAnim;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mAnim.cancel();
        }
    }

    public boolean isAnimationFinished() {
        return this.mAnimStarted && !this.mAnim.isRunning();
    }

    public void move(int i, int i2) {
        int i3;
        if (i > 0 && i2 > 0 && (i3 = this.mLastTouchX) > 0 && this.mLastTouchY > 0 && this.mScaledMaskPath != null) {
            this.mTranslateX.animateToPos((float) (i3 - i));
            this.mTranslateY.animateToPos((float) (this.mLastTouchY - i2));
        }
        this.mLastTouchX = i;
        this.mLastTouchY = i2;
        applyTranslation();
    }

    public void animateShift(final int i, final int i2) {
        if (!this.mAnim.isStarted()) {
            this.mAnimatedShiftX = i;
            this.mAnimatedShiftY = i2;
            applyTranslation();
            this.mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = 1.0f - valueAnimator.getAnimatedFraction();
                    int unused = DragView.this.mAnimatedShiftX = (int) (((float) i) * animatedFraction);
                    int unused2 = DragView.this.mAnimatedShiftY = (int) (animatedFraction * ((float) i2));
                    DragView.this.applyTranslation();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void applyTranslation() {
        setTranslationX((float) ((this.mLastTouchX - this.mRegistrationX) + this.mAnimatedShiftX));
        setTranslationY((float) ((this.mLastTouchY - this.mRegistrationY) + this.mAnimatedShiftY));
    }

    public void detachContentView(boolean z) {
        if (this.mContent != null && this.mContentViewParent != null && this.mContentViewInParentViewIndex >= 0) {
            Picture picture = new Picture();
            this.mContent.draw(picture.beginRecording(this.mWidth, this.mHeight));
            picture.endRecording();
            View view = new View(this.mActivity);
            view.setBackground(new PictureDrawable(picture));
            view.measure(View.MeasureSpec.makeMeasureSpec(this.mWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.mHeight, BasicMeasure.EXACTLY));
            view.layout(this.mContent.getLeft(), this.mContent.getTop(), this.mContent.getRight(), this.mContent.getBottom());
            setClipToOutline(this.mContent.getClipToOutline());
            setOutlineProvider(this.mContent.getOutlineProvider());
            addViewInLayout(view, indexOfChild(this.mContent), this.mContent.getLayoutParams(), true);
            removeViewInLayout(this.mContent);
            this.mContent.setVisibility(4);
            this.mContent.setLayoutParams(this.mContentViewLayoutParams);
            if (z) {
                this.mContentViewParent.addView(this.mContent, this.mContentViewInParentViewIndex);
            }
            this.mContentViewParent = null;
            this.mContentViewInParentViewIndex = -1;
        }
    }

    public void remove() {
        if (getParent() != null) {
            this.mDragLayer.removeView(this);
        }
    }

    public int getBlurSizeOutline() {
        return this.mBlurSizeOutline;
    }

    public float getInitialScale() {
        return this.mInitialScale;
    }

    public View getContentView() {
        return this.mContent;
    }

    public ViewGroup getContentViewParent() {
        return this.mContentViewParent;
    }

    private static class SpringFloatValue {
        private static final float DAMPENING_RATIO = 1.0f;
        private static final int PARALLAX_MAX_IN_DP = 8;
        private static final int STIFFNESS = 4000;
        private static final FloatPropertyCompat<SpringFloatValue> VALUE = new FloatPropertyCompat<SpringFloatValue>("value") {
            public float getValue(SpringFloatValue springFloatValue) {
                return springFloatValue.mValue;
            }

            public void setValue(SpringFloatValue springFloatValue, float f) {
                float unused = springFloatValue.mValue = f;
                springFloatValue.mView.invalidate();
            }
        };
        private final float mDelta;
        private final SpringAnimation mSpring;
        /* access modifiers changed from: private */
        public float mValue;
        /* access modifiers changed from: private */
        public final View mView;

        public SpringFloatValue(View view, float f) {
            this.mView = view;
            this.mSpring = ((SpringAnimation) ((SpringAnimation) new SpringAnimation(this, VALUE, 0.0f).setMinValue(-f)).setMaxValue(f)).setSpring(new SpringForce(0.0f).setDampingRatio(1.0f).setStiffness(4000.0f));
            this.mDelta = view.getResources().getDisplayMetrics().density * 8.0f;
        }

        public void animateToPos(float f) {
            SpringAnimation springAnimation = this.mSpring;
            float f2 = this.mDelta;
            springAnimation.animateToFinalPosition(Utilities.boundToRange(f, -f2, f2));
        }
    }

    private static View getViewFromDrawable(Context context, Drawable drawable) {
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(drawable);
        return imageView;
    }

    public static void removeAllViews(ActivityContext activityContext) {
        BaseDragLayer dragLayer = activityContext.getDragLayer();
        for (int childCount = dragLayer.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = dragLayer.getChildAt(childCount);
            if (childAt instanceof DragView) {
                dragLayer.removeView(childAt);
            }
        }
    }
}
