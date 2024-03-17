package com.android.quickstep.util;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.TraceHelper;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.util.TransformParams;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.function.Supplier;

public class TaskViewSimulator implements TransformParams.BuilderProxy {
    private static final boolean DEBUG = false;
    private static final String TAG = "TaskViewSimulator";
    public final AnimatedFloat fullScreenProgress = new AnimatedFloat();
    private final Context mContext;
    private final TaskView.FullscreenDrawParams mCurrentFullscreenParams;
    private DeviceProfile mDp;
    private boolean mDrawsBelowRecents;
    private final Matrix mInversePositionMatrix = new Matrix();
    private boolean mIsGridTask;
    private final boolean mIsRecentsRtl;
    private boolean mLayoutValid = false;
    private final Matrix mMatrix = new Matrix();
    private final Matrix mMatrixTmp = new Matrix();
    private RecentsOrientedState mOrientationState;
    private int mOrientationStateId;
    private final PointF mPivot = new PointF();
    private final TaskThumbnailView.PreviewPositionHelper mPositionHelper = new TaskThumbnailView.PreviewPositionHelper();
    private final BaseActivityInterface mSizeStrategy;
    private int mStagePosition = -1;
    private SplitConfigurationOptions.StagedSplitBounds mStagedSplitBounds;
    private final Rect mTaskRect = new Rect();
    private int mTaskRectTranslationX;
    private int mTaskRectTranslationY;
    private final float[] mTempPoint = new float[2];
    private final RectF mTempRectF = new RectF();
    private final ThumbnailData mThumbnailData = new ThumbnailData();
    private final Rect mThumbnailPosition = new Rect();
    private final Rect mTmpCropRect = new Rect();
    public final AnimatedFloat recentsViewPrimaryTranslation = new AnimatedFloat();
    public final AnimatedFloat recentsViewScale = new AnimatedFloat();
    public final AnimatedFloat recentsViewScroll = new AnimatedFloat();
    public final AnimatedFloat recentsViewSecondaryTranslation = new AnimatedFloat();
    public final AnimatedFloat taskPrimaryTranslation = new AnimatedFloat();
    public final AnimatedFloat taskSecondaryTranslation = new AnimatedFloat();

    static /* synthetic */ void lambda$new$0(int i) {
    }

    public TaskViewSimulator(Context context, BaseActivityInterface baseActivityInterface) {
        this.mContext = context;
        this.mSizeStrategy = baseActivityInterface;
        RecentsOrientedState recentsOrientedState = (RecentsOrientedState) TraceHelper.allowIpcs("", new Supplier(context, baseActivityInterface) {
            public final /* synthetic */ Context f$0;
            public final /* synthetic */ BaseActivityInterface f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get() {
                return TaskViewSimulator.lambda$new$1(this.f$0, this.f$1);
            }
        });
        this.mOrientationState = recentsOrientedState;
        recentsOrientedState.setGestureActive(true);
        this.mCurrentFullscreenParams = new TaskView.FullscreenDrawParams(context);
        this.mOrientationStateId = this.mOrientationState.getStateId();
        this.mIsRecentsRtl = this.mOrientationState.getOrientationHandler().getRecentsRtlSetting(context.getResources());
    }

    static /* synthetic */ RecentsOrientedState lambda$new$1(Context context, BaseActivityInterface baseActivityInterface) {
        return new RecentsOrientedState(context, baseActivityInterface, $$Lambda$TaskViewSimulator$cgnMqiFiMk2ykDJXOYOfiJ0QId0.INSTANCE);
    }

    public void setDp(DeviceProfile deviceProfile) {
        this.mDp = deviceProfile;
        this.mLayoutValid = false;
        this.mOrientationState.setDeviceProfile(deviceProfile);
    }

    public void setOrientationState(RecentsOrientedState recentsOrientedState) {
        this.mOrientationState = recentsOrientedState;
        this.mLayoutValid = false;
    }

    public float getFullScreenScale() {
        Rect rect;
        DeviceProfile deviceProfile = this.mDp;
        if (deviceProfile == null) {
            return 1.0f;
        }
        if (this.mIsGridTask) {
            this.mSizeStrategy.calculateGridTaskSize(this.mContext, deviceProfile, this.mTaskRect, this.mOrientationState.getOrientationHandler());
        } else {
            this.mSizeStrategy.calculateTaskSize(this.mContext, deviceProfile, this.mTaskRect);
        }
        if (this.mStagedSplitBounds != null) {
            rect = new Rect(this.mTaskRect);
            this.mOrientationState.getOrientationHandler().setSplitTaskSwipeRect(this.mDp, this.mTaskRect, this.mStagedSplitBounds, this.mStagePosition);
            this.mTaskRect.offset(this.mTaskRectTranslationX, this.mTaskRectTranslationY);
        } else {
            rect = this.mTaskRect;
        }
        rect.offset(this.mTaskRectTranslationX, this.mTaskRectTranslationY);
        return this.mOrientationState.getFullScreenScaleAndPivot(rect, this.mDp, this.mPivot);
    }

    public void setPreview(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        setPreviewBounds(remoteAnimationTargetCompat.startScreenSpaceBounds, remoteAnimationTargetCompat.contentInsets);
    }

    public void setPreview(RemoteAnimationTargetCompat remoteAnimationTargetCompat, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds) {
        setPreview(remoteAnimationTargetCompat);
        this.mStagedSplitBounds = stagedSplitBounds;
        if (stagedSplitBounds == null) {
            this.mStagePosition = -1;
        } else {
            this.mStagePosition = this.mThumbnailPosition.equals(stagedSplitBounds.leftTopBounds) ^ true ? 1 : 0;
        }
    }

    public void setPreviewBounds(Rect rect, Rect rect2) {
        this.mThumbnailData.insets.set(rect2);
        this.mThumbnailData.windowingMode = 1;
        this.mThumbnailPosition.set(rect);
        this.mLayoutValid = false;
    }

    public void setScroll(float f) {
        this.recentsViewScroll.value = f;
    }

    public void setDrawsBelowRecents(boolean z) {
        this.mDrawsBelowRecents = z;
    }

    public void setIsGridTask(boolean z) {
        this.mIsGridTask = z;
    }

    public void setTaskRectTranslation(int i, int i2) {
        this.mTaskRectTranslationX = i;
        this.mTaskRectTranslationY = i2;
    }

    public void addAppToOverviewAnim(PendingAnimation pendingAnimation, TimeInterpolator timeInterpolator) {
        pendingAnimation.addFloat(this.fullScreenProgress, AnimatedFloat.VALUE, 1.0f, 0.0f, timeInterpolator);
        pendingAnimation.addFloat(this.recentsViewScale, AnimatedFloat.VALUE, getFullScreenScale(), 1.0f, timeInterpolator);
    }

    public void addOverviewToAppAnim(PendingAnimation pendingAnimation, TimeInterpolator timeInterpolator) {
        pendingAnimation.addFloat(this.fullScreenProgress, AnimatedFloat.VALUE, 0.0f, 1.0f, timeInterpolator);
        pendingAnimation.addFloat(this.recentsViewScale, AnimatedFloat.VALUE, 1.0f, getFullScreenScale(), timeInterpolator);
    }

    public RectF getCurrentCropRect() {
        RectF rectF = this.mCurrentFullscreenParams.mCurrentDrawnInsets;
        this.mTempRectF.set(-rectF.left, -rectF.top, ((float) this.mTaskRect.width()) + rectF.right, ((float) this.mTaskRect.height()) + rectF.bottom);
        this.mInversePositionMatrix.mapRect(this.mTempRectF);
        return this.mTempRectF;
    }

    public RectF getCurrentRect() {
        RectF currentCropRect = getCurrentCropRect();
        this.mMatrixTmp.set(this.mMatrix);
        RecentsOrientedState.preDisplayRotation(this.mOrientationState.getDisplayRotation(), (float) this.mDp.widthPx, (float) this.mDp.heightPx, this.mMatrixTmp);
        this.mMatrixTmp.mapRect(currentCropRect);
        return currentCropRect;
    }

    public RecentsOrientedState getOrientationState() {
        return this.mOrientationState;
    }

    public Matrix getCurrentMatrix() {
        return this.mMatrix;
    }

    public void applyWindowToHomeRotation(Matrix matrix) {
        matrix.postTranslate((float) this.mDp.windowX, (float) this.mDp.windowY);
        RecentsOrientedState.postDisplayRotation(RotationHelper.deltaRotation(this.mOrientationState.getRecentsActivityRotation(), this.mOrientationState.getDisplayRotation()), (float) this.mDp.widthPx, (float) this.mDp.heightPx, matrix);
    }

    public void apply(TransformParams transformParams) {
        if (this.mDp != null && !this.mThumbnailPosition.isEmpty()) {
            if (!this.mLayoutValid || this.mOrientationStateId != this.mOrientationState.getStateId()) {
                this.mLayoutValid = true;
                this.mOrientationStateId = this.mOrientationState.getStateId();
                getFullScreenScale();
                if (TaskAnimationManager.SHELL_TRANSITIONS_ROTATION) {
                    this.mThumbnailData.rotation = this.mOrientationState.getTouchRotation();
                } else {
                    this.mThumbnailData.rotation = this.mOrientationState.getDisplayRotation();
                }
                this.mPositionHelper.updateThumbnailMatrix(this.mThumbnailPosition, this.mThumbnailData, this.mTaskRect.width(), this.mTaskRect.height(), this.mDp, this.mOrientationState.getRecentsActivityRotation(), !this.mIsRecentsRtl);
                this.mPositionHelper.getMatrix().invert(this.mInversePositionMatrix);
            }
            this.mCurrentFullscreenParams.setProgress(Utilities.boundToRange(this.fullScreenProgress.value, 0.0f, 1.0f), this.recentsViewScale.value, 1.0f, this.mTaskRect.width(), this.mDp, this.mPositionHelper);
            RectF rectF = this.mCurrentFullscreenParams.mCurrentDrawnInsets;
            float f = this.mCurrentFullscreenParams.mScale;
            this.mMatrix.set(this.mPositionHelper.getMatrix());
            this.mMatrix.postTranslate(rectF.left, rectF.top);
            this.mMatrix.postScale(f, f);
            this.mMatrix.postTranslate((float) this.mTaskRect.left, (float) this.mTaskRect.top);
            this.mOrientationState.getOrientationHandler().setPrimary(this.mMatrix, PagedOrientationHandler.MATRIX_POST_TRANSLATE, this.taskPrimaryTranslation.value);
            this.mOrientationState.getOrientationHandler().setSecondary(this.mMatrix, PagedOrientationHandler.MATRIX_POST_TRANSLATE, this.taskSecondaryTranslation.value);
            this.mOrientationState.getOrientationHandler().setPrimary(this.mMatrix, PagedOrientationHandler.MATRIX_POST_TRANSLATE, this.recentsViewScroll.value);
            this.mMatrix.postScale(this.recentsViewScale.value, this.recentsViewScale.value, this.mPivot.x, this.mPivot.y);
            this.mOrientationState.getOrientationHandler().setSecondary(this.mMatrix, PagedOrientationHandler.MATRIX_POST_TRANSLATE, this.recentsViewSecondaryTranslation.value);
            this.mOrientationState.getOrientationHandler().setPrimary(this.mMatrix, PagedOrientationHandler.MATRIX_POST_TRANSLATE, this.recentsViewPrimaryTranslation.value);
            applyWindowToHomeRotation(this.mMatrix);
            this.mTempRectF.set(-rectF.left, -rectF.top, ((float) this.mTaskRect.width()) + rectF.right, ((float) this.mTaskRect.height()) + rectF.bottom);
            this.mInversePositionMatrix.mapRect(this.mTempRectF);
            this.mTempRectF.roundOut(this.mTmpCropRect);
            transformParams.applySurfaceParams(transformParams.createSurfaceParams(this));
        }
    }

    public void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
        builder.withMatrix(this.mMatrix).withWindowCrop(this.mTmpCropRect).withCornerRadius(getCurrentCornerRadius());
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && transformParams.getRecentsSurface() != null) {
            builder.withRelativeLayerTo(transformParams.getRecentsSurface(), this.mDrawsBelowRecents ? Integer.MIN_VALUE : 0);
        }
    }

    public float getCurrentCornerRadius() {
        float f = this.mCurrentFullscreenParams.mCurrentDrawnCornerRadius;
        float[] fArr = this.mTempPoint;
        fArr[0] = f;
        fArr[1] = 0.0f;
        this.mInversePositionMatrix.mapVectors(fArr);
        return Math.max(Math.abs(this.mTempPoint[0]), Math.abs(this.mTempPoint[1]));
    }
}
