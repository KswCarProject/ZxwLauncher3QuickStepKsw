package com.android.quickstep.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.util.MultiValueUpdateListener;
import com.android.quickstep.util.TaskCornerRadius;
import com.android.systemui.shared.system.QuickStepContract;

public class FloatingTaskView extends FrameLayout {
    private final StatefulActivity mActivity;
    private final FullscreenDrawParams mFullscreenParams;
    private final boolean mIsRtl;
    private PagedOrientationHandler mOrientationHandler;
    private SplitPlaceholderView mSplitPlaceholderView;
    private int mStagePosition;
    private RectF mStartingPosition;
    private FloatingTaskThumbnailView mThumbnailView;

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

    public FloatingTaskView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FloatingTaskView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingTaskView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mActivity = (StatefulActivity) BaseActivity.fromContext(context);
        this.mIsRtl = Utilities.isRtl(getResources());
        this.mFullscreenParams = new FullscreenDrawParams(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mThumbnailView = (FloatingTaskThumbnailView) findViewById(R.id.thumbnail);
        SplitPlaceholderView splitPlaceholderView = (SplitPlaceholderView) findViewById(R.id.split_placeholder);
        this.mSplitPlaceholderView = splitPlaceholderView;
        splitPlaceholderView.setAlpha(0.0f);
    }

    private void init(StatefulActivity statefulActivity, View view, Bitmap bitmap, Drawable drawable, RectF rectF) {
        this.mStartingPosition = rectF;
        updateInitialPositionForView(view);
        InsettableFrameLayout.LayoutParams layoutParams = (InsettableFrameLayout.LayoutParams) getLayoutParams();
        this.mSplitPlaceholderView.setLayoutParams(new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height));
        setPivotX(0.0f);
        setPivotY(0.0f);
        this.mThumbnailView.setThumbnail(bitmap);
        this.mThumbnailView.setVisibility(0);
        RecentsView recentsView = (RecentsView) statefulActivity.getOverviewPanel();
        this.mOrientationHandler = recentsView.getPagedOrientationHandler();
        this.mStagePosition = recentsView.getSplitPlaceholder().getActiveSplitStagePosition();
        this.mSplitPlaceholderView.setIcon(drawable, this.mContext.getResources().getDimensionPixelSize(R.dimen.split_placeholder_icon_size));
        this.mSplitPlaceholderView.getIconView().setRotation(this.mOrientationHandler.getDegreesRotated());
    }

    public static FloatingTaskView getFloatingTaskView(StatefulActivity statefulActivity, View view, Bitmap bitmap, Drawable drawable, RectF rectF) {
        ViewGroup viewGroup = (ViewGroup) statefulActivity.getDragLayer().getParent();
        FloatingTaskView floatingTaskView = (FloatingTaskView) statefulActivity.getLayoutInflater().inflate(R.layout.floating_split_select_view, viewGroup, false);
        floatingTaskView.init(statefulActivity, view, bitmap, drawable, rectF);
        viewGroup.addView(floatingTaskView);
        return floatingTaskView;
    }

    public void updateInitialPositionForView(View view) {
        View view2 = view;
        Utilities.getBoundsForViewInDragLayer(this.mActivity.getDragLayer(), view2, new Rect(0, 0, view.getWidth(), view.getHeight()), false, (float[]) null, this.mStartingPosition);
        InsettableFrameLayout.LayoutParams layoutParams = new InsettableFrameLayout.LayoutParams(Math.round(this.mStartingPosition.width()), Math.round(this.mStartingPosition.height()));
        initPosition(this.mStartingPosition, layoutParams);
        setLayoutParams(layoutParams);
    }

    public void update(RectF rectF, float f) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        float f2 = rectF.left - this.mStartingPosition.left;
        float f3 = rectF.top - ((float) marginLayoutParams.topMargin);
        float width = rectF.width() / ((float) marginLayoutParams.width);
        float height = rectF.height() / ((float) marginLayoutParams.height);
        this.mFullscreenParams.updateParams(rectF, f, width, height);
        setTranslationX(f2);
        setTranslationY(f3);
        setScaleX(width);
        setScaleY(height);
        this.mSplitPlaceholderView.invalidate();
        this.mThumbnailView.invalidate();
        this.mOrientationHandler.setPrimaryScale(this.mSplitPlaceholderView.getIconView(), 1.0f / width);
        this.mOrientationHandler.setSecondaryScale(this.mSplitPlaceholderView.getIconView(), 1.0f / height);
    }

    public void updateOrientationHandler(PagedOrientationHandler pagedOrientationHandler) {
        this.mOrientationHandler = pagedOrientationHandler;
        this.mSplitPlaceholderView.getIconView().setRotation(this.mOrientationHandler.getDegreesRotated());
    }

    /* access modifiers changed from: protected */
    public void initPosition(RectF rectF, InsettableFrameLayout.LayoutParams layoutParams) {
        this.mStartingPosition.set(rectF);
        layoutParams.ignoreInsets = true;
        layoutParams.topMargin = Math.round(rectF.top);
        if (this.mIsRtl) {
            layoutParams.setMarginStart(this.mActivity.getDeviceProfile().widthPx - Math.round(rectF.right));
        } else {
            layoutParams.setMarginStart(Math.round(rectF.left));
        }
        int i = (int) rectF.left;
        layout(i, layoutParams.topMargin, layoutParams.width + i, layoutParams.topMargin + layoutParams.height);
    }

    public void addAnimation(PendingAnimation pendingAnimation, RectF rectF, Rect rect, boolean z, boolean z2) {
        boolean z3 = z2;
        this.mFullscreenParams.setIsStagedTask(z3);
        int[] iArr = new int[2];
        this.mActivity.getDragLayer().getLocationOnScreen(iArr);
        Rect rect2 = rect;
        SplitOverlayProperties splitOverlayProperties = new SplitOverlayProperties(rect2, rectF, iArr[0], iArr[1]);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        pendingAnimation.add(ofFloat);
        long duration = pendingAnimation.getDuration();
        RectF rectF2 = new RectF();
        if (z) {
            PendingAnimation pendingAnimation2 = pendingAnimation;
            pendingAnimation2.addFloat(this.mSplitPlaceholderView, SplitPlaceholderView.ALPHA_FLOAT, 0.0f, 1.0f, Interpolators.ACCEL);
            pendingAnimation2.addFloat(this.mThumbnailView, LauncherAnimUtils.VIEW_ALPHA, 1.0f, 0.0f, Interpolators.DEACCEL_3);
        } else if (z3 && this.mSplitPlaceholderView.getAlpha() == 0.0f) {
            pendingAnimation.addFloat(this.mSplitPlaceholderView, SplitPlaceholderView.ALPHA_FLOAT, 0.3f, 1.0f, Interpolators.ACCEL);
        }
        ofFloat.addUpdateListener(new MultiValueUpdateListener(this, splitOverlayProperties, duration, rectF2, rectF) {
            final MultiValueUpdateListener.FloatProp mDx;
            final MultiValueUpdateListener.FloatProp mDy;
            final MultiValueUpdateListener.FloatProp mTaskViewScaleX;
            final MultiValueUpdateListener.FloatProp mTaskViewScaleY;
            final /* synthetic */ FloatingTaskView this$0;
            final /* synthetic */ long val$animDuration;
            final /* synthetic */ RectF val$floatingTaskViewBounds;
            final /* synthetic */ SplitOverlayProperties val$prop;
            final /* synthetic */ RectF val$startingBounds;

            {
                long j = r15;
                this.this$0 = r13;
                this.val$prop = r14;
                this.val$animDuration = j;
                this.val$floatingTaskViewBounds = r17;
                this.val$startingBounds = r18;
                this.mDx = new MultiValueUpdateListener.FloatProp(0.0f, r14.dX, 0.0f, (float) j, Interpolators.LINEAR);
                this.mDy = new MultiValueUpdateListener.FloatProp(0.0f, r14.dY, 0.0f, (float) j, Interpolators.LINEAR);
                this.mTaskViewScaleX = new MultiValueUpdateListener.FloatProp(1.0f, r14.finalTaskViewScaleX, 0.0f, (float) j, Interpolators.LINEAR);
                this.mTaskViewScaleY = new MultiValueUpdateListener.FloatProp(1.0f, r14.finalTaskViewScaleY, 0.0f, (float) j, Interpolators.LINEAR);
            }

            public void onUpdate(float f, boolean z) {
                this.val$floatingTaskViewBounds.set(this.val$startingBounds);
                this.val$floatingTaskViewBounds.offset(this.mDx.value, this.mDy.value);
                Utilities.scaleRectFAboutCenter(this.val$floatingTaskViewBounds, this.mTaskViewScaleX.value, this.mTaskViewScaleY.value);
                this.this$0.update(this.val$floatingTaskViewBounds, f);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void drawRoundedRect(Canvas canvas, Paint paint) {
        if (this.mFullscreenParams != null) {
            canvas.drawRoundRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.mFullscreenParams.mCurrentDrawnCornerRadius / this.mFullscreenParams.mScaleX, this.mFullscreenParams.mCurrentDrawnCornerRadius / this.mFullscreenParams.mScaleY, paint);
        }
    }

    /* access modifiers changed from: package-private */
    public void centerIconView(IconView iconView, float f, float f2) {
        this.mOrientationHandler.updateStagedSplitIconParams(iconView, f, f2, this.mFullscreenParams.mScaleX, this.mFullscreenParams.mScaleY, iconView.getDrawableWidth(), iconView.getDrawableHeight(), this.mActivity.getDeviceProfile(), this.mStagePosition);
    }

    private static class SplitOverlayProperties {
        /* access modifiers changed from: private */
        public final float dX;
        /* access modifiers changed from: private */
        public final float dY;
        /* access modifiers changed from: private */
        public final float finalTaskViewScaleX;
        /* access modifiers changed from: private */
        public final float finalTaskViewScaleY;

        SplitOverlayProperties(Rect rect, RectF rectF, int i, int i2) {
            this.finalTaskViewScaleX = ((float) rect.width()) / rectF.width();
            this.finalTaskViewScaleY = ((float) rect.height()) / rectF.height();
            this.dX = ((float) (rect.centerX() - i)) - rectF.centerX();
            this.dY = ((float) (rect.centerY() - i2)) - rectF.centerY();
        }
    }

    public static class FullscreenDrawParams {
        public final RectF mBounds = new RectF();
        private final float mCornerRadius;
        public float mCurrentDrawnCornerRadius;
        public boolean mIsStagedTask;
        public float mScaleX = 1.0f;
        public float mScaleY = 1.0f;
        private final float mWindowCornerRadius;

        public FullscreenDrawParams(Context context) {
            float f = TaskCornerRadius.get(context);
            this.mCornerRadius = f;
            this.mWindowCornerRadius = QuickStepContract.getWindowCornerRadius(context);
            this.mCurrentDrawnCornerRadius = f;
        }

        public void updateParams(RectF rectF, float f, float f2, float f3) {
            float f4;
            this.mBounds.set(rectF);
            this.mScaleX = f2;
            this.mScaleY = f3;
            if (this.mIsStagedTask) {
                f4 = this.mWindowCornerRadius;
            } else {
                f4 = Utilities.mapRange(f, this.mCornerRadius, this.mWindowCornerRadius);
            }
            this.mCurrentDrawnCornerRadius = f4;
        }

        public void setIsStagedTask(boolean z) {
            this.mIsStagedTask = z;
        }
    }
}
