package com.android.launcher3.graphics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.Pair;
import android.util.Property;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.util.Themes;
import java.lang.ref.WeakReference;

public class PreloadIconDrawable extends FastBitmapDrawable {
    private static final int COLOR_SHADOW = 1426063360;
    private static final int COLOR_TRACK = 2012147438;
    private static final float COMPLETE_ANIM_FRACTION = 0.3f;
    private static final int DEFAULT_PATH_SIZE = 100;
    private static final long DURATION_SCALE = 500;
    private static final Property<PreloadIconDrawable, Float> INTERNAL_STATE = new Property<PreloadIconDrawable, Float>(Float.TYPE, "internalStateProgress") {
        public Float get(PreloadIconDrawable preloadIconDrawable) {
            return Float.valueOf(preloadIconDrawable.mInternalStateProgress);
        }

        public void set(PreloadIconDrawable preloadIconDrawable, Float f) {
            preloadIconDrawable.setInternalProgress(f.floatValue());
        }
    };
    private static final int MAX_PAINT_ALPHA = 255;
    private static final int PRELOAD_ACCENT_COLOR_INDEX = 0;
    private static final int PRELOAD_BACKGROUND_COLOR_INDEX = 1;
    private static final float PROGRESS_GAP = 2.0f;
    private static final float PROGRESS_WIDTH = 7.0f;
    private static final float SMALL_SCALE = 0.6f;
    private static final SparseArray<WeakReference<Pair<Path, Bitmap>>> sShadowCache = new SparseArray<>();
    private ObjectAnimator mCurrentAnim;
    private float mIconScale;
    private final int mIndicatorColor;
    /* access modifiers changed from: private */
    public float mInternalStateProgress;
    private final boolean mIsDarkMode;
    private boolean mIsStartable;
    private final ItemInfoWithIcon mItem;
    private final PathMeasure mPathMeasure;
    private final Paint mProgressPaint;
    /* access modifiers changed from: private */
    public boolean mRanFinishAnimation;
    private final Path mScaledProgressPath;
    private final Path mScaledTrackPath;
    private Bitmap mShadowBitmap;
    private final Path mShapePath;
    private final int mSystemAccentColor;
    private final int mSystemBackgroundColor;
    private final Matrix mTmpMatrix;
    private int mTrackAlpha;
    private float mTrackLength;

    public PreloadIconDrawable(ItemInfoWithIcon itemInfoWithIcon, Context context) {
        this(itemInfoWithIcon, IconPalette.getPreloadProgressColor(context, itemInfoWithIcon.bitmap.color), getPreloadColors(context), Utilities.isDarkTheme(context));
    }

    public PreloadIconDrawable(ItemInfoWithIcon itemInfoWithIcon, int i, int[] iArr, boolean z) {
        super(itemInfoWithIcon.bitmap);
        this.mTmpMatrix = new Matrix();
        this.mPathMeasure = new PathMeasure();
        this.mItem = itemInfoWithIcon;
        this.mShapePath = GraphicsUtils.getShapePath(100);
        this.mScaledTrackPath = new Path();
        this.mScaledProgressPath = new Path();
        Paint paint = new Paint(3);
        this.mProgressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        this.mIndicatorColor = i;
        this.mSystemAccentColor = iArr[0];
        this.mSystemBackgroundColor = iArr[1];
        this.mIsDarkMode = z;
        setLevel(itemInfoWithIcon.getProgressLevel());
        setIsStartable(itemInfoWithIcon.isAppStartable());
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mTmpMatrix.setScale(((((float) rect.width()) - 14.0f) - 4.0f) / 100.0f, ((((float) rect.height()) - 14.0f) - 4.0f) / 100.0f);
        this.mTmpMatrix.postTranslate(((float) rect.left) + PROGRESS_WIDTH + 2.0f, ((float) rect.top) + PROGRESS_WIDTH + 2.0f);
        this.mShapePath.transform(this.mTmpMatrix, this.mScaledTrackPath);
        float width = (float) (rect.width() / 100);
        this.mProgressPaint.setStrokeWidth(PROGRESS_WIDTH * width);
        this.mShadowBitmap = getShadowBitmap(rect.width(), rect.height(), width * 2.0f);
        this.mPathMeasure.setPath(this.mScaledTrackPath, true);
        this.mTrackLength = this.mPathMeasure.getLength();
        setInternalProgress(this.mInternalStateProgress);
    }

    private Bitmap getShadowBitmap(int i, int i2, float f) {
        int i3;
        int i4 = ((i << 16) | i2) * (this.mIsDarkMode ? -1 : 1);
        SparseArray<WeakReference<Pair<Path, Bitmap>>> sparseArray = sShadowCache;
        WeakReference weakReference = sparseArray.get(i4);
        Pair pair = weakReference != null ? (Pair) weakReference.get() : null;
        Bitmap bitmap = (pair == null || !((Path) pair.first).equals(this.mShapePath)) ? null : (Bitmap) pair.second;
        if (bitmap != null) {
            return bitmap;
        }
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = this.mProgressPaint;
        if (this.mIsStartable) {
            i3 = COLOR_SHADOW;
        } else {
            i3 = this.mSystemAccentColor;
        }
        paint.setShadowLayer(f, 0.0f, 0.0f, i3);
        this.mProgressPaint.setColor(this.mIsStartable ? COLOR_TRACK : this.mSystemBackgroundColor);
        this.mProgressPaint.setAlpha(255);
        canvas.drawPath(this.mScaledTrackPath, this.mProgressPaint);
        this.mProgressPaint.clearShadowLayer();
        canvas.setBitmap((Bitmap) null);
        sparseArray.put(i4, new WeakReference(Pair.create(this.mShapePath, createBitmap)));
        return createBitmap;
    }

    public void drawInternal(Canvas canvas, Rect rect) {
        if (this.mRanFinishAnimation) {
            super.drawInternal(canvas, rect);
            return;
        }
        this.mProgressPaint.setColor(this.mIsStartable ? this.mIndicatorColor : this.mSystemAccentColor);
        this.mProgressPaint.setAlpha(this.mTrackAlpha);
        Bitmap bitmap = this.mShadowBitmap;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, (float) rect.left, (float) rect.top, this.mProgressPaint);
        }
        canvas.drawPath(this.mScaledProgressPath, this.mProgressPaint);
        int save = canvas.save();
        float f = this.mIconScale;
        canvas.scale(f, f, rect.exactCenterX(), rect.exactCenterY());
        super.drawInternal(canvas, rect);
        canvas.restoreToCount(save);
    }

    /* access modifiers changed from: protected */
    public boolean onLevelChange(int i) {
        updateInternalState(((float) i) * 0.01f, getBounds().width() > 0, false);
        return true;
    }

    public void maybePerformFinishedAnimation() {
        if (this.mInternalStateProgress == 0.0f) {
            this.mInternalStateProgress = 1.0f;
        }
        updateInternalState(1.3f, true, true);
    }

    public boolean hasNotCompleted() {
        return !this.mRanFinishAnimation;
    }

    public void setIsStartable(boolean z) {
        if (this.mIsStartable != z) {
            this.mIsStartable = z;
            setIsDisabled(!z);
        }
    }

    private void updateInternalState(float f, boolean z, boolean z2) {
        ObjectAnimator objectAnimator = this.mCurrentAnim;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mCurrentAnim = null;
        }
        if (Float.compare(f, this.mInternalStateProgress) != 0) {
            if (f < this.mInternalStateProgress) {
                z = false;
            }
            if (!z || this.mRanFinishAnimation) {
                setInternalProgress(f);
                return;
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, INTERNAL_STATE, new float[]{f});
            this.mCurrentAnim = ofFloat;
            ofFloat.setDuration((long) ((f - this.mInternalStateProgress) * 500.0f));
            this.mCurrentAnim.setInterpolator(Interpolators.LINEAR);
            if (z2) {
                this.mCurrentAnim.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = PreloadIconDrawable.this.mRanFinishAnimation = true;
                    }
                });
            }
            this.mCurrentAnim.start();
        }
    }

    /* access modifiers changed from: private */
    public void setInternalProgress(float f) {
        this.mInternalStateProgress = f;
        if (f <= 0.0f) {
            this.mIconScale = 0.6f;
            this.mScaledTrackPath.reset();
            this.mTrackAlpha = 255;
        }
        if (f < 1.0f && f > 0.0f) {
            this.mPathMeasure.getSegment(0.0f, f * this.mTrackLength, this.mScaledProgressPath, true);
            this.mIconScale = 0.6f;
            this.mTrackAlpha = 255;
        } else if (f >= 1.0f) {
            setIsDisabled(this.mItem.isDisabled());
            this.mScaledTrackPath.set(this.mScaledProgressPath);
            float f2 = (f - 1.0f) / 0.3f;
            if (f2 >= 1.0f) {
                this.mIconScale = 1.0f;
                this.mTrackAlpha = 0;
            } else {
                this.mTrackAlpha = Math.round((1.0f - f2) * 255.0f);
                this.mIconScale = (f2 * 0.39999998f) + 0.6f;
            }
        }
        invalidateSelf();
    }

    private static int[] getPreloadColors(Context context) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, 16974563);
        return new int[]{Themes.getColorAccent(contextThemeWrapper), Themes.getColorBackgroundFloating(contextThemeWrapper)};
    }

    public static PreloadIconDrawable newPendingIcon(Context context, ItemInfoWithIcon itemInfoWithIcon) {
        return new PreloadIconDrawable(itemInfoWithIcon, context);
    }

    public FastBitmapDrawable.FastBitmapConstantState newConstantState() {
        return new PreloadIconConstantState(this.mBitmap, this.mIconColor, this.mItem, this.mIndicatorColor, new int[]{this.mSystemAccentColor, this.mSystemBackgroundColor}, this.mIsDarkMode);
    }

    protected static class PreloadIconConstantState extends FastBitmapDrawable.FastBitmapConstantState {
        protected final int mIndicatorColor;
        protected final ItemInfoWithIcon mInfo;
        protected final boolean mIsDarkMode;
        protected final int mLevel;
        protected final int[] mPreloadColors;

        public PreloadIconConstantState(Bitmap bitmap, int i, ItemInfoWithIcon itemInfoWithIcon, int i2, int[] iArr, boolean z) {
            super(bitmap, i);
            this.mInfo = itemInfoWithIcon;
            this.mIndicatorColor = i2;
            this.mPreloadColors = iArr;
            this.mIsDarkMode = z;
            this.mLevel = itemInfoWithIcon.getProgressLevel();
        }

        public PreloadIconDrawable createDrawable() {
            return new PreloadIconDrawable(this.mInfo, this.mIndicatorColor, this.mPreloadColors, this.mIsDarkMode);
        }
    }
}
