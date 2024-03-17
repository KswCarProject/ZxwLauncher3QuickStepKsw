package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Property;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;

public class TaskThumbnailView extends View {
    public static final Property<TaskThumbnailView, Float> DIM_ALPHA = new FloatProperty<TaskThumbnailView>("dimAlpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(TaskThumbnailView taskThumbnailView, float f) {
            taskThumbnailView.setDimAlpha(f);
        }

        public Float get(TaskThumbnailView taskThumbnailView) {
            return Float.valueOf(taskThumbnailView.mDimAlpha);
        }
    };
    private static final MainThreadInitializedObject<TaskView.FullscreenDrawParams> TEMP_PARAMS = new MainThreadInitializedObject<>($$Lambda$RhdQ5MP_gY2iW0rnGffucvTJ9M.INSTANCE);
    private final BaseActivity mActivity;
    private final Paint mBackgroundPaint;
    protected BitmapShader mBitmapShader;
    private final Paint mClearPaint;
    /* access modifiers changed from: private */
    public float mDimAlpha;
    private final int mDimColor;
    private final Paint mDimmingPaintAfterClearing;
    private TaskView.FullscreenDrawParams mFullscreenParams;
    private TaskOverlayFactory.TaskOverlay mOverlay;
    private boolean mOverlayEnabled;
    private final Paint mPaint;
    private final PreviewPositionHelper mPreviewPositionHelper;
    private final Rect mPreviewRect;
    private Task mTask;
    private ThumbnailData mThumbnailData;

    public TaskThumbnailView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TaskThumbnailView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskThumbnailView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Paint paint = new Paint(1);
        this.mPaint = paint;
        Paint paint2 = new Paint(1);
        this.mBackgroundPaint = paint2;
        Paint paint3 = new Paint();
        this.mClearPaint = paint3;
        Paint paint4 = new Paint();
        this.mDimmingPaintAfterClearing = paint4;
        this.mPreviewRect = new Rect();
        this.mPreviewPositionHelper = new PreviewPositionHelper();
        this.mDimAlpha = 0.0f;
        paint.setFilterBitmap(true);
        paint2.setColor(-1);
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mActivity = BaseActivity.fromContext(context);
        this.mFullscreenParams = TEMP_PARAMS.lambda$get$1$MainThreadInitializedObject(context);
        int foregroundScrimDimColor = RecentsView.getForegroundScrimDimColor(context);
        this.mDimColor = foregroundScrimDimColor;
        paint4.setColor(foregroundScrimDimColor);
    }

    public void bind(Task task) {
        getTaskOverlay().reset();
        this.mTask = task;
        int i = ViewCompat.MEASURED_STATE_MASK;
        if (task != null) {
            i = -16777216 | task.colorBackground;
        }
        this.mPaint.setColor(i);
        this.mBackgroundPaint.setColor(i);
    }

    public void setThumbnail(Task task, ThumbnailData thumbnailData, boolean z) {
        this.mTask = task;
        boolean z2 = true;
        boolean z3 = this.mThumbnailData == null;
        if (thumbnailData == null || thumbnailData.thumbnail == null) {
            thumbnailData = null;
        }
        this.mThumbnailData = thumbnailData;
        if (z) {
            if (!z3 || thumbnailData == null) {
                z2 = false;
            }
            refresh(z2);
        }
    }

    public void setThumbnail(Task task, ThumbnailData thumbnailData) {
        setThumbnail(task, thumbnailData, true);
    }

    public void refresh() {
        refresh(false);
    }

    private void refresh(boolean z) {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData == null || thumbnailData.thumbnail == null) {
            this.mBitmapShader = null;
            this.mThumbnailData = null;
            this.mPaint.setShader((Shader) null);
            getTaskOverlay().reset();
        } else {
            Bitmap bitmap = this.mThumbnailData.thumbnail;
            bitmap.prepareToDraw();
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.mBitmapShader = bitmapShader;
            this.mPaint.setShader(bitmapShader);
            updateThumbnailMatrix();
            if (z) {
                refreshOverlay();
            }
        }
        updateThumbnailPaintFilter();
    }

    public void setDimAlpha(float f) {
        this.mDimAlpha = f;
        updateThumbnailPaintFilter();
    }

    public TaskOverlayFactory.TaskOverlay getTaskOverlay() {
        if (this.mOverlay == null) {
            this.mOverlay = getTaskView().getRecentsView().getTaskOverlayFactory().createOverlay(this);
        }
        return this.mOverlay;
    }

    public float getDimAlpha() {
        return this.mDimAlpha;
    }

    public Insets getScaledInsets() {
        if (this.mThumbnailData == null) {
            return Insets.NONE;
        }
        RectF rectF = new RectF(0.0f, 0.0f, (float) this.mThumbnailData.thumbnail.getWidth(), (float) this.mThumbnailData.thumbnail.getHeight());
        RectF rectF2 = new RectF(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        Matrix matrix = new Matrix();
        this.mPreviewPositionHelper.getMatrix().invert(matrix);
        RectF rectF3 = new RectF();
        matrix.mapRect(rectF3, rectF2);
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        int i = 0;
        int round = TaskView.clipLeft(deviceProfile) ? Math.round(rectF3.left) : 0;
        int round2 = TaskView.clipTop(deviceProfile) ? Math.round(rectF3.top) : 0;
        int round3 = TaskView.clipRight(deviceProfile) ? Math.round(rectF.right - rectF3.right) : 0;
        if (TaskView.clipBottom(deviceProfile)) {
            i = Math.round(rectF.bottom - rectF3.bottom);
        }
        return Insets.of(round, round2, round3, i);
    }

    public int getSysUiStatusNavFlags() {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData == null) {
            return 0;
        }
        int i = 8;
        if ((thumbnailData.appearance & 8) != 0) {
            i = 4;
        }
        return i | 0 | ((this.mThumbnailData.appearance & 16) != 0 ? 1 : 2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        RectF rectF = this.mFullscreenParams.mCurrentDrawnInsets;
        canvas.save();
        canvas.scale(this.mFullscreenParams.mScale, this.mFullscreenParams.mScale);
        canvas.translate(rectF.left, rectF.top);
        drawOnCanvas(canvas, -rectF.left, -rectF.top, ((float) getMeasuredWidth()) + rectF.right, ((float) getMeasuredHeight()) + rectF.bottom, this.mFullscreenParams.mCurrentDrawnCornerRadius);
        canvas.restore();
    }

    public PreviewPositionHelper getPreviewPositionHelper() {
        return this.mPreviewPositionHelper;
    }

    public void setFullscreenParams(TaskView.FullscreenDrawParams fullscreenDrawParams) {
        this.mFullscreenParams = fullscreenDrawParams;
        getTaskOverlay().setFullscreenParams(fullscreenDrawParams);
        invalidate();
    }

    public void drawOnCanvas(Canvas canvas, float f, float f2, float f3, float f4, float f5) {
        if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || this.mTask == null || !getTaskView().isRunningTask() || getTaskView().showScreenshot()) {
            canvas.drawRoundRect(f, f2 + 1.0f, f3, f4 - 1.0f, f5, f5, this.mBackgroundPaint);
            Task task = this.mTask;
            if (!(task == null || task.isLocked || this.mBitmapShader == null || this.mThumbnailData == null)) {
                canvas.drawRoundRect(f, f2, f3, f4, f5, f5, this.mPaint);
                return;
            }
            return;
        }
        float f6 = f5;
        canvas.drawRoundRect(f, f2, f3, f4, f6, f5, this.mClearPaint);
        canvas.drawRoundRect(f, f2, f3, f4, f5, f6, this.mDimmingPaintAfterClearing);
    }

    public TaskView getTaskView() {
        return (TaskView) getParent();
    }

    public void setOverlayEnabled(boolean z) {
        if (this.mOverlayEnabled != z) {
            this.mOverlayEnabled = z;
            refreshOverlay();
        }
    }

    private void refreshOverlay() {
        if (this.mOverlayEnabled) {
            getTaskOverlay().initOverlay(this.mTask, this.mThumbnailData, this.mPreviewPositionHelper.mMatrix, this.mPreviewPositionHelper.mIsOrientationChanged);
        } else {
            getTaskOverlay().reset();
        }
    }

    private void updateThumbnailPaintFilter() {
        ColorFilter colorFilter = getColorFilter(this.mDimAlpha);
        this.mBackgroundPaint.setColorFilter(colorFilter);
        int i = (int) (this.mDimAlpha * 255.0f);
        this.mDimmingPaintAfterClearing.setAlpha(i);
        if (this.mBitmapShader != null) {
            this.mPaint.setColorFilter(colorFilter);
        } else {
            this.mPaint.setColorFilter((ColorFilter) null);
            this.mPaint.setColor(ColorUtils.blendARGB(ViewCompat.MEASURED_STATE_MASK, this.mDimColor, (float) i));
        }
        invalidate();
    }

    private void updateThumbnailMatrix() {
        ThumbnailData thumbnailData;
        boolean unused = this.mPreviewPositionHelper.mIsOrientationChanged = false;
        if (!(this.mBitmapShader == null || (thumbnailData = this.mThumbnailData) == null)) {
            this.mPreviewRect.set(0, 0, thumbnailData.thumbnail.getWidth(), this.mThumbnailData.thumbnail.getHeight());
            this.mPreviewPositionHelper.updateThumbnailMatrix(this.mPreviewRect, this.mThumbnailData, getMeasuredWidth(), getMeasuredHeight(), this.mActivity.getDeviceProfile(), getTaskView().getRecentsView().getPagedViewOrientedState().getRecentsActivityRotation(), getLayoutDirection() == 1);
            this.mBitmapShader.setLocalMatrix(this.mPreviewPositionHelper.mMatrix);
            this.mPaint.setShader(this.mBitmapShader);
        }
        getTaskView().updateCurrentFullscreenParams(this.mPreviewPositionHelper);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateThumbnailMatrix();
        refreshOverlay();
    }

    private ColorFilter getColorFilter(float f) {
        return Utilities.makeColorTintingColorFilter(this.mDimColor, f);
    }

    public Bitmap getThumbnail() {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData == null) {
            return null;
        }
        return thumbnailData.thumbnail;
    }

    public boolean isRealSnapshot() {
        ThumbnailData thumbnailData = this.mThumbnailData;
        if (thumbnailData != null && thumbnailData.isRealSnapshot && !this.mTask.isLocked) {
            return true;
        }
        return false;
    }

    public static class PreviewPositionHelper {
        private static final RectF EMPTY_RECT_F = new RectF();
        private final RectF mClippedInsets = new RectF();
        /* access modifiers changed from: private */
        public boolean mIsOrientationChanged;
        /* access modifiers changed from: private */
        public final Matrix mMatrix = new Matrix();

        private int getRotationDelta(int i, int i2) {
            int i3 = i2 - i;
            return i3 < 0 ? i3 + 4 : i3;
        }

        private boolean isOrientationChange(int i) {
            return i == 1 || i == 3;
        }

        public Matrix getMatrix() {
            return this.mMatrix;
        }

        public void updateThumbnailMatrix(Rect rect, ThumbnailData thumbnailData, int i, int i2, DeviceProfile deviceProfile, int i3, boolean z) {
            float f;
            boolean z2;
            boolean z3;
            int i4;
            float f2;
            float f3;
            float f4;
            float f5;
            ThumbnailData thumbnailData2 = thumbnailData;
            int i5 = i;
            int i6 = i2;
            DeviceProfile deviceProfile2 = deviceProfile;
            int rotationDelta = getRotationDelta(i3, thumbnailData2.rotation);
            RectF rectF = new RectF();
            if (TaskView.clipLeft(deviceProfile)) {
                rectF.left = (float) thumbnailData2.insets.left;
            }
            if (TaskView.clipRight(deviceProfile)) {
                rectF.right = (float) thumbnailData2.insets.right;
            }
            if (TaskView.clipTop(deviceProfile)) {
                rectF.top = (float) thumbnailData2.insets.top;
            }
            if (TaskView.clipBottom(deviceProfile)) {
                rectF.bottom = (float) thumbnailData2.insets.bottom;
            }
            float f6 = thumbnailData2.scale;
            boolean z4 = true;
            boolean z5 = !deviceProfile2.isMultiWindowMode && thumbnailData2.windowingMode == 1 && !deviceProfile2.isTablet;
            boolean z6 = isOrientationChange(rotationDelta) && z5;
            if (i5 == 0 || i6 == 0 || f6 == 0.0f) {
                z2 = z6;
                z3 = false;
                f = 0.0f;
            } else {
                if (rotationDelta <= 0 || !z5) {
                    z4 = false;
                }
                float width = ((float) rect.width()) / f6;
                float height = ((float) rect.height()) / f6;
                float f7 = width - (rectF.left + rectF.right);
                float f8 = height - (rectF.top + rectF.bottom);
                float f9 = (float) i5;
                float f10 = (float) i6;
                boolean z7 = z6;
                float f11 = f8;
                float f12 = f10;
                boolean isRelativePercentDifferenceGreaterThan = Utilities.isRelativePercentDifferenceGreaterThan(f9 / f10, z4 ? f8 / f7 : f7 / f8, 0.1f);
                if (z4 && isRelativePercentDifferenceGreaterThan) {
                    z4 = false;
                    z7 = false;
                }
                if (isRelativePercentDifferenceGreaterThan) {
                    if (!TaskView.clipLeft(deviceProfile)) {
                        rectF.left = (float) thumbnailData2.letterboxInsets.left;
                    }
                    if (!TaskView.clipRight(deviceProfile)) {
                        rectF.right = (float) thumbnailData2.letterboxInsets.right;
                    }
                    if (!TaskView.clipTop(deviceProfile)) {
                        rectF.top = (float) thumbnailData2.letterboxInsets.top;
                    }
                    if (!TaskView.clipBottom(deviceProfile)) {
                        rectF.bottom = (float) thumbnailData2.letterboxInsets.bottom;
                    }
                    f7 = width - (rectF.left + rectF.right);
                    f3 = height - (rectF.top + rectF.bottom);
                } else {
                    f3 = f11;
                }
                if (z7) {
                    f4 = f9;
                    f9 = f12;
                } else {
                    f4 = f12;
                }
                float f13 = f9 / f4;
                float f14 = f7 / f13;
                if (f14 > f3) {
                    if (f3 < f4) {
                        f14 = Math.min(f4, height);
                    } else {
                        f14 = f3;
                    }
                    float f15 = f14 * f13;
                    if (f15 > width) {
                        f14 = width / f13;
                    } else {
                        width = f15;
                    }
                } else {
                    width = f7;
                }
                if (z) {
                    rectF.left += f7 - width;
                    f5 = 0.0f;
                    if (rectF.right < 0.0f) {
                        rectF.left += rectF.right;
                        rectF.right = 0.0f;
                    }
                } else {
                    f5 = 0.0f;
                    rectF.right += f7 - width;
                    if (rectF.left < 0.0f) {
                        rectF.right += rectF.left;
                        rectF.left = 0.0f;
                    }
                }
                rectF.bottom += f3 - f14;
                if (rectF.top < f5) {
                    rectF.bottom += rectF.top;
                    rectF.top = f5;
                } else if (rectF.bottom < f5) {
                    rectF.top += rectF.bottom;
                    rectF.bottom = f5;
                }
                z3 = z4;
                f = f9 / (width * f6);
                z2 = z7;
            }
            Rect insets = deviceProfile.getInsets();
            if (!z3) {
                if (deviceProfile2.isMultiWindowMode) {
                    this.mClippedInsets.offsetTo(((float) insets.left) * f6, ((float) insets.top) * f6);
                } else {
                    this.mClippedInsets.offsetTo(rectF.left * f6, rectF.top * f6);
                }
                this.mMatrix.setTranslate((-rectF.left) * f6, (-rectF.top) * f6);
            } else {
                setThumbnailRotation(rotationDelta, rectF, f6, rect, deviceProfile);
            }
            if (z2) {
                f2 = ((float) rect.height()) * f;
                i4 = rect.width();
            } else {
                f2 = ((float) rect.width()) * f;
                i4 = rect.height();
            }
            float f16 = ((float) i4) * f;
            this.mClippedInsets.left *= f;
            this.mClippedInsets.top *= f;
            if (deviceProfile2.isMultiWindowMode) {
                this.mClippedInsets.right = ((float) insets.right) * f6 * f;
                this.mClippedInsets.bottom = ((float) insets.bottom) * f6 * f;
            } else {
                RectF rectF2 = this.mClippedInsets;
                rectF2.right = Math.max(0.0f, (f2 - rectF2.left) - ((float) i5));
                RectF rectF3 = this.mClippedInsets;
                rectF3.bottom = Math.max(0.0f, (f16 - rectF3.top) - ((float) i6));
            }
            this.mMatrix.postScale(f, f);
            this.mIsOrientationChanged = z2;
        }

        private void setThumbnailRotation(int i, RectF rectF, float f, Rect rect, DeviceProfile deviceProfile) {
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            this.mMatrix.setRotate((float) (i * 90));
            float f7 = 0.0f;
            if (i != 1) {
                if (i == 2) {
                    f7 = -rectF.top;
                    f5 = (float) rect.height();
                    f6 = (float) rect.width();
                    f3 = -rectF.left;
                } else if (i != 3) {
                    f4 = 0.0f;
                    f3 = 0.0f;
                    f2 = 0.0f;
                } else {
                    float f8 = rectF.top;
                    f3 = rectF.right;
                    f5 = (float) rect.width();
                    f6 = 0.0f;
                    f7 = f8;
                }
                f4 = f5;
                f2 = f6;
            } else {
                float f9 = rectF.bottom;
                f3 = rectF.left;
                f2 = (float) rect.height();
                f7 = f9;
                f4 = 0.0f;
            }
            this.mClippedInsets.offsetTo(f7 * f, f3 * f);
            this.mMatrix.postTranslate(f2, f4);
            if (TaskView.useFullThumbnail(deviceProfile)) {
                this.mMatrix.postTranslate(-this.mClippedInsets.left, -this.mClippedInsets.top);
            }
        }

        public RectF getInsetsToDrawInFullscreen(DeviceProfile deviceProfile) {
            return TaskView.useFullThumbnail(deviceProfile) ? this.mClippedInsets : EMPTY_RECT_F;
        }
    }
}
