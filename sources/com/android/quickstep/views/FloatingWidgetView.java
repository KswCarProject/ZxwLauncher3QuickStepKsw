package com.android.quickstep.views;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Size;
import android.view.GhostView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.FloatingView;
import com.android.launcher3.views.ListenerView;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.RoundedCornerEnforcement;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

public class FloatingWidgetView extends FrameLayout implements Animator.AnimatorListener, ViewTreeObserver.OnGlobalLayoutListener, FloatingView {
    private static final Matrix sTmpMatrix = new Matrix();
    private boolean mAppTargetIsTranslucent;
    private View mAppWidgetBackgroundView;
    private LauncherAppWidgetHostView mAppWidgetView;
    private final RectF mBackgroundOffset;
    private RectF mBackgroundPosition;
    private final FloatingWidgetBackgroundView mBackgroundView;
    private Runnable mEndRunnable;
    private Runnable mFastFinishRunnable;
    private GhostView mForegroundOverlayView;
    private float mIconOffsetY;
    private final Launcher mLauncher;
    private final ListenerView mListenerView;
    private Runnable mOnTargetChangeRunnable;

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationRepeat(Animator animator) {
    }

    public void onAnimationStart(Animator animator) {
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

    public FloatingWidgetView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FloatingWidgetView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingWidgetView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBackgroundOffset = new RectF();
        this.mLauncher = Launcher.getLauncher(context);
        this.mListenerView = new ListenerView(context, attributeSet);
        FloatingWidgetBackgroundView floatingWidgetBackgroundView = new FloatingWidgetBackgroundView(context, attributeSet, i);
        this.mBackgroundView = floatingWidgetBackgroundView;
        addView(floatingWidgetBackgroundView);
        setWillNotDraw(false);
    }

    public void onAnimationEnd(Animator animator) {
        Runnable runnable = this.mEndRunnable;
        this.mEndRunnable = null;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        super.onDetachedFromWindow();
    }

    public void onGlobalLayout() {
        if (!isUninitialized()) {
            positionViews();
            Runnable runnable = this.mOnTargetChangeRunnable;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public void setOnTargetChangeListener(Runnable runnable) {
        this.mOnTargetChangeRunnable = runnable;
    }

    public void setFastFinishRunnable(Runnable runnable) {
        this.mFastFinishRunnable = runnable;
    }

    public void fastFinish() {
        if (!isUninitialized()) {
            Runnable runnable = this.mFastFinishRunnable;
            if (runnable != null) {
                runnable.run();
            }
            Runnable runnable2 = this.mEndRunnable;
            this.mEndRunnable = null;
            if (runnable2 != null) {
                runnable2.run();
            }
        }
    }

    private void init(DragLayer dragLayer, LauncherAppWidgetHostView launcherAppWidgetHostView, RectF rectF, Size size, float f, boolean z, int i) {
        this.mAppWidgetView = launcherAppWidgetHostView;
        launcherAppWidgetHostView.beginDeferringUpdates();
        this.mBackgroundPosition = rectF;
        this.mAppTargetIsTranslucent = z;
        this.mEndRunnable = new Runnable(dragLayer) {
            public final /* synthetic */ DragLayer f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                FloatingWidgetView.this.lambda$init$0$FloatingWidgetView(this.f$1);
            }
        };
        View findBackground = RoundedCornerEnforcement.findBackground(this.mAppWidgetView);
        this.mAppWidgetBackgroundView = findBackground;
        if (findBackground == null) {
            this.mAppWidgetBackgroundView = this.mAppWidgetView;
        }
        getRelativePosition(this.mAppWidgetBackgroundView, dragLayer, this.mBackgroundPosition);
        getRelativePosition(this.mAppWidgetBackgroundView, this.mAppWidgetView, this.mBackgroundOffset);
        if (!this.mAppTargetIsTranslucent) {
            this.mBackgroundView.init(this.mAppWidgetView, this.mAppWidgetBackgroundView, f, i);
            layout(0, 0, size.getWidth(), size.getHeight());
            this.mForegroundOverlayView = GhostView.addGhost(this.mAppWidgetView, this);
            positionViews();
        }
        this.mListenerView.setListener(new Runnable() {
            public final void run() {
                FloatingWidgetView.this.fastFinish();
            }
        });
        dragLayer.addView(this.mListenerView);
    }

    public void update(RectF rectF, float f, float f2, float f3, float f4) {
        if (!isUninitialized() && !this.mAppTargetIsTranslucent) {
            setAlpha(f);
            this.mBackgroundView.update(f4, f3);
            this.mAppWidgetView.setAlpha(f2);
            this.mBackgroundPosition = rectF;
            positionViews();
        }
    }

    public void setPositionOffsetY(float f) {
        this.mIconOffsetY = f;
        onGlobalLayout();
    }

    private void positionViews() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        setLayoutParams(layoutParams);
        this.mBackgroundView.setTranslationX(this.mBackgroundPosition.left);
        this.mBackgroundView.setTranslationY(this.mBackgroundPosition.top + this.mIconOffsetY);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mBackgroundView.getLayoutParams();
        layoutParams2.leftMargin = 0;
        layoutParams2.topMargin = 0;
        layoutParams2.width = (int) this.mBackgroundPosition.width();
        layoutParams2.height = (int) this.mBackgroundPosition.height();
        this.mBackgroundView.setLayoutParams(layoutParams2);
        if (this.mForegroundOverlayView != null) {
            Matrix matrix = sTmpMatrix;
            matrix.reset();
            float width = this.mBackgroundPosition.width() / ((float) this.mAppWidgetBackgroundView.getWidth());
            matrix.setTranslate((-this.mBackgroundOffset.left) - ((float) this.mAppWidgetView.getLeft()), (-this.mBackgroundOffset.top) - ((float) this.mAppWidgetView.getTop()));
            matrix.postScale(width, width);
            matrix.postTranslate(this.mBackgroundPosition.left, this.mBackgroundPosition.top + this.mIconOffsetY);
            this.mForegroundOverlayView.setMatrix(matrix);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: finish */
    public void lambda$init$0$FloatingWidgetView(DragLayer dragLayer) {
        this.mAppWidgetView.setAlpha(1.0f);
        GhostView.removeGhost(this.mAppWidgetView);
        ((ViewGroup) dragLayer.getParent()).removeView(this);
        dragLayer.removeView(this.mListenerView);
        this.mBackgroundView.finish();
        this.mAppWidgetView.endDeferringUpdates();
        recycle();
        this.mLauncher.getViewCache().recycleView(R.layout.floating_widget_view, this);
    }

    public float getInitialCornerRadius() {
        return this.mBackgroundView.getMaximumRadius();
    }

    private boolean isUninitialized() {
        return this.mForegroundOverlayView == null;
    }

    private void recycle() {
        this.mIconOffsetY = 0.0f;
        this.mEndRunnable = null;
        this.mFastFinishRunnable = null;
        this.mOnTargetChangeRunnable = null;
        this.mBackgroundPosition = null;
        this.mListenerView.setListener((Runnable) null);
        this.mAppWidgetView = null;
        this.mForegroundOverlayView = null;
        this.mAppWidgetBackgroundView = null;
        this.mBackgroundView.recycle();
    }

    public static FloatingWidgetView getFloatingWidgetView(Launcher launcher, LauncherAppWidgetHostView launcherAppWidgetHostView, RectF rectF, Size size, float f, boolean z, int i) {
        DragLayer dragLayer = launcher.getDragLayer();
        ViewGroup viewGroup = (ViewGroup) dragLayer.getParent();
        FloatingWidgetView floatingWidgetView = (FloatingWidgetView) launcher.getViewCache().getView(R.layout.floating_widget_view, launcher, viewGroup);
        floatingWidgetView.recycle();
        floatingWidgetView.init(dragLayer, launcherAppWidgetHostView, rectF, size, f, z, i);
        viewGroup.addView(floatingWidgetView);
        return floatingWidgetView;
    }

    public static int getDefaultBackgroundColor(Context context, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        if (remoteAnimationTargetCompat == null || remoteAnimationTargetCompat.taskInfo.taskDescription == null) {
            return Themes.getColorBackground(context);
        }
        return remoteAnimationTargetCompat.taskInfo.taskDescription.getBackgroundColor();
    }

    private static void getRelativePosition(View view, View view2, RectF rectF) {
        float[] fArr = {0.0f, 0.0f, (float) view.getWidth(), (float) view.getHeight()};
        Utilities.getDescendantCoordRelativeToAncestor(view, view2, fArr, false, true);
        rectF.set(Math.min(fArr[0], fArr[2]), Math.min(fArr[1], fArr[3]), Math.max(fArr[0], fArr[2]), Math.max(fArr[1], fArr[3]));
    }
}
