package com.android.quickstep.util;

import android.animation.RectEvaluator;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.window.PictureInPictureSurfaceTransaction;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.quickstep.TaskAnimationManager;
import com.android.systemui.shared.pip.PipSurfaceTransactionHelper;

public class SwipePipToHomeAnimator extends RectFSpringAnim {
    private static final float END_PROGRESS = 1.0f;
    /* access modifiers changed from: private */
    public static final String TAG = "SwipePipToHomeAnimator";
    private final Rect mAppBounds;
    private final ComponentName mComponentName;
    private SurfaceControl mContentOverlay;
    private final Rect mCurrentBounds;
    private final RectF mCurrentBoundsF;
    private final Rect mDestinationBounds;
    private final Rect mDestinationBoundsTransformed;
    private final int mFromRotation;
    /* access modifiers changed from: private */
    public boolean mHasAnimationEnded;
    private final Matrix mHomeToWindowPositionMap;
    private final RectEvaluator mInsetsEvaluator;
    private final SurfaceControl mLeash;
    private final Rect mSourceHintRectInsets;
    private final Rect mSourceInsets;
    private final Rect mSourceRectHint;
    private final Rect mStartBounds;
    private final PipSurfaceTransactionHelper mSurfaceTransactionHelper;
    private final int mTaskId;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private SwipePipToHomeAnimator(android.content.Context r14, int r15, android.content.ComponentName r16, android.view.SurfaceControl r17, android.graphics.Rect r18, android.graphics.Rect r19, android.graphics.Matrix r20, android.graphics.RectF r21, android.graphics.Rect r22, int r23, android.graphics.Rect r24, int r25, int r26, android.view.View r27) {
        /*
            r13 = this;
            r0 = r13
            r1 = r17
            r2 = r19
            r3 = r21
            r4 = r24
            android.graphics.RectF r5 = new android.graphics.RectF
            r5.<init>(r4)
            r6 = 0
            r7 = r14
            r13.<init>(r3, r5, r14, r6)
            android.graphics.Rect r5 = new android.graphics.Rect
            r5.<init>()
            r0.mSourceRectHint = r5
            android.graphics.Rect r7 = new android.graphics.Rect
            r7.<init>()
            r0.mAppBounds = r7
            android.graphics.Matrix r8 = new android.graphics.Matrix
            r8.<init>()
            r0.mHomeToWindowPositionMap = r8
            android.graphics.Rect r9 = new android.graphics.Rect
            r9.<init>()
            r0.mStartBounds = r9
            android.graphics.RectF r10 = new android.graphics.RectF
            r10.<init>()
            r0.mCurrentBoundsF = r10
            android.graphics.Rect r10 = new android.graphics.Rect
            r10.<init>()
            r0.mCurrentBounds = r10
            android.graphics.Rect r10 = new android.graphics.Rect
            r10.<init>()
            r0.mDestinationBounds = r10
            android.animation.RectEvaluator r11 = new android.animation.RectEvaluator
            android.graphics.Rect r12 = new android.graphics.Rect
            r12.<init>()
            r11.<init>(r12)
            r0.mInsetsEvaluator = r11
            android.graphics.Rect r11 = new android.graphics.Rect
            r11.<init>()
            r0.mSourceInsets = r11
            android.graphics.Rect r11 = new android.graphics.Rect
            r11.<init>()
            r0.mDestinationBoundsTransformed = r11
            r12 = r15
            r0.mTaskId = r12
            r12 = r16
            r0.mComponentName = r12
            r0.mLeash = r1
            r7.set(r2)
            r7 = r20
            r8.set(r7)
            r3.round(r9)
            r3 = r22
            r10.set(r3)
            r7 = r23
            r0.mFromRotation = r7
            r11.set(r4)
            com.android.systemui.shared.pip.PipSurfaceTransactionHelper r4 = new com.android.systemui.shared.pip.PipSurfaceTransactionHelper
            r7 = r25
            r8 = r26
            r4.<init>(r7, r8)
            r0.mSurfaceTransactionHelper = r4
            if (r18 == 0) goto L_0x00a1
            int r4 = r18.width()
            int r7 = r22.width()
            if (r4 < r7) goto L_0x009f
            int r4 = r18.height()
            int r3 = r22.height()
            if (r4 >= r3) goto L_0x00a1
        L_0x009f:
            r3 = r6
            goto L_0x00a3
        L_0x00a1:
            r3 = r18
        L_0x00a3:
            if (r3 != 0) goto L_0x0120
            r5.setEmpty()
            r0.mSourceHintRectInsets = r6
            android.view.SurfaceSession r2 = new android.view.SurfaceSession
            r2.<init>()
            android.view.SurfaceControl$Builder r3 = new android.view.SurfaceControl$Builder
            r3.<init>(r2)
            java.lang.String r2 = "SwipePipToHomeAnimator"
            android.view.SurfaceControl$Builder r2 = r3.setCallsite(r2)
            java.lang.String r3 = "PipContentOverlay"
            android.view.SurfaceControl$Builder r2 = r2.setName(r3)
            android.view.SurfaceControl$Builder r2 = r2.setColorLayer()
            android.view.SurfaceControl r2 = r2.build()
            r0.mContentOverlay = r2
            android.view.SurfaceControl$Transaction r2 = new android.view.SurfaceControl$Transaction
            r2.<init>()
            android.view.SurfaceControl r3 = r0.mContentOverlay
            r2.show(r3)
            android.view.SurfaceControl r3 = r0.mContentOverlay
            r4 = 2147483647(0x7fffffff, float:NaN)
            r2.setLayer(r3, r4)
            android.content.Context r3 = r27.getContext()
            int r3 = com.android.launcher3.util.Themes.getColorBackground(r3)
            r4 = 3
            float[] r4 = new float[r4]
            r5 = 0
            int r6 = android.graphics.Color.red(r3)
            float r6 = (float) r6
            r7 = 1132396544(0x437f0000, float:255.0)
            float r6 = r6 / r7
            r4[r5] = r6
            r5 = 1
            int r6 = android.graphics.Color.green(r3)
            float r6 = (float) r6
            float r6 = r6 / r7
            r4[r5] = r6
            r5 = 2
            int r3 = android.graphics.Color.blue(r3)
            float r3 = (float) r3
            float r3 = r3 / r7
            r4[r5] = r3
            android.view.SurfaceControl r3 = r0.mContentOverlay
            r2.setColor(r3, r4)
            android.view.SurfaceControl r3 = r0.mContentOverlay
            r4 = 0
            r2.setAlpha(r3, r4)
            android.view.SurfaceControl r3 = r0.mContentOverlay
            r2.reparent(r3, r1)
            r2.apply()
            com.android.quickstep.util.-$$Lambda$SwipePipToHomeAnimator$WVRucJi2oq5x2kuSWCtcd-RiHXQ r1 = new com.android.quickstep.util.-$$Lambda$SwipePipToHomeAnimator$WVRucJi2oq5x2kuSWCtcd-RiHXQ
            r1.<init>(r2)
            r13.addOnUpdateListener(r1)
            goto L_0x013e
        L_0x0120:
            r5.set(r3)
            android.graphics.Rect r1 = new android.graphics.Rect
            int r4 = r3.left
            int r5 = r2.left
            int r4 = r4 - r5
            int r5 = r3.top
            int r6 = r2.top
            int r5 = r5 - r6
            int r6 = r2.right
            int r7 = r3.right
            int r6 = r6 - r7
            int r2 = r2.bottom
            int r3 = r3.bottom
            int r2 = r2 - r3
            r1.<init>(r4, r5, r6, r2)
            r0.mSourceHintRectInsets = r1
        L_0x013e:
            com.android.quickstep.util.SwipePipToHomeAnimator$1 r1 = new com.android.quickstep.util.SwipePipToHomeAnimator$1
            r2 = r27
            r1.<init>(r2)
            r13.addAnimatorListener(r1)
            com.android.quickstep.util.-$$Lambda$SwipePipToHomeAnimator$fJALBBz2USxdC4QKgVVF6rScYbQ r1 = new com.android.quickstep.util.-$$Lambda$SwipePipToHomeAnimator$fJALBBz2USxdC4QKgVVF6rScYbQ
            r1.<init>()
            r13.addOnUpdateListener(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.util.SwipePipToHomeAnimator.<init>(android.content.Context, int, android.content.ComponentName, android.view.SurfaceControl, android.graphics.Rect, android.graphics.Rect, android.graphics.Matrix, android.graphics.RectF, android.graphics.Rect, int, android.graphics.Rect, int, int, android.view.View):void");
    }

    public /* synthetic */ void lambda$new$0$SwipePipToHomeAnimator(SurfaceControl.Transaction transaction, RectF rectF, float f) {
        transaction.setAlpha(this.mContentOverlay, f < 0.5f ? 0.0f : Utilities.mapToRange(Math.min(f, 1.0f), 0.5f, 1.0f, 0.0f, 1.0f, Interpolators.FAST_OUT_SLOW_IN));
        transaction.apply();
    }

    /* access modifiers changed from: private */
    public void onAnimationUpdate(RectF rectF, float f) {
        if (!this.mHasAnimationEnded) {
            SurfaceControl.Transaction newSurfaceControlTransaction = PipSurfaceTransactionHelper.newSurfaceControlTransaction();
            this.mHomeToWindowPositionMap.mapRect(this.mCurrentBoundsF, rectF);
            onAnimationUpdate(newSurfaceControlTransaction, this.mCurrentBoundsF, f);
            newSurfaceControlTransaction.apply();
        }
    }

    private PictureInPictureSurfaceTransaction onAnimationUpdate(SurfaceControl.Transaction transaction, RectF rectF, float f) {
        rectF.round(this.mCurrentBounds);
        if (this.mSourceHintRectInsets == null) {
            return onAnimationScale(f, transaction, this.mCurrentBounds);
        }
        return onAnimationScaleAndCrop(f, transaction, this.mCurrentBounds);
    }

    private PictureInPictureSurfaceTransaction onAnimationScale(float f, SurfaceControl.Transaction transaction, Rect rect) {
        int i = this.mFromRotation;
        if (i != 1 && i != 3) {
            return this.mSurfaceTransactionHelper.scale(transaction, this.mLeash, this.mAppBounds, rect);
        }
        RotatedPosition rotatedPosition = getRotatedPosition(f);
        return this.mSurfaceTransactionHelper.scale(transaction, this.mLeash, this.mAppBounds, rect, rotatedPosition.degree, rotatedPosition.positionX, rotatedPosition.positionY);
    }

    private PictureInPictureSurfaceTransaction onAnimationScaleAndCrop(float f, SurfaceControl.Transaction transaction, Rect rect) {
        Rect evaluate = this.mInsetsEvaluator.evaluate(f, this.mSourceInsets, this.mSourceHintRectInsets);
        int i = this.mFromRotation;
        if (i == 1 || i == 3) {
            RotatedPosition rotatedPosition = getRotatedPosition(f);
            return this.mSurfaceTransactionHelper.scaleAndRotate(transaction, this.mLeash, this.mAppBounds, rect, evaluate, rotatedPosition.degree, rotatedPosition.positionX, rotatedPosition.positionY);
        }
        return this.mSurfaceTransactionHelper.scaleAndCrop(transaction, this.mLeash, this.mSourceRectHint, this.mAppBounds, rect, evaluate);
    }

    public int getTaskId() {
        return this.mTaskId;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public Rect getDestinationBounds() {
        return this.mDestinationBounds;
    }

    public SurfaceControl getContentOverlay() {
        return this.mContentOverlay;
    }

    public PictureInPictureSurfaceTransaction getFinishTransaction() {
        return onAnimationUpdate(PipSurfaceTransactionHelper.newSurfaceControlTransaction(), new RectF(this.mDestinationBounds), 1.0f);
    }

    private RotatedPosition getRotatedPosition(float f) {
        float f2;
        float f3;
        float f4;
        float f5;
        int i;
        if (TaskAnimationManager.SHELL_TRANSITIONS_ROTATION) {
            if (this.mFromRotation == 1) {
                float f6 = 1.0f - f;
                f4 = -90.0f * f6;
                f5 = (((float) (this.mDestinationBoundsTransformed.left - this.mStartBounds.left)) * f) + ((float) this.mStartBounds.left);
                f2 = (f * ((float) (this.mDestinationBoundsTransformed.top - this.mStartBounds.top))) + ((float) this.mStartBounds.top);
                f3 = ((float) this.mStartBounds.bottom) * f6;
                return new RotatedPosition(f4, f5, f2 + f3);
            }
            float f7 = 1.0f - f;
            f4 = f7 * 90.0f;
            f5 = (((float) (this.mDestinationBoundsTransformed.left - this.mStartBounds.left)) * f) + ((float) this.mStartBounds.left) + (((float) this.mStartBounds.right) * f7);
            f2 = f * ((float) (this.mDestinationBoundsTransformed.top - this.mStartBounds.top));
            i = this.mStartBounds.top;
        } else if (this.mFromRotation == 1) {
            f4 = -90.0f * f;
            f5 = (((float) (this.mDestinationBoundsTransformed.left - this.mStartBounds.left)) * f) + ((float) this.mStartBounds.left);
            f2 = f * ((float) (this.mDestinationBoundsTransformed.bottom - this.mStartBounds.top));
            i = this.mStartBounds.top;
        } else {
            f4 = f * 90.0f;
            f5 = (((float) (this.mDestinationBoundsTransformed.right - this.mStartBounds.left)) * f) + ((float) this.mStartBounds.left);
            f2 = f * ((float) (this.mDestinationBoundsTransformed.top - this.mStartBounds.top));
            i = this.mStartBounds.top;
        }
        f3 = (float) i;
        return new RotatedPosition(f4, f5, f2 + f3);
    }

    public static class Builder {
        private Rect mAppBounds;
        private View mAttachedView;
        private ComponentName mComponentName;
        private Context mContext;
        private int mCornerRadius;
        private Rect mDestinationBounds;
        private final Rect mDestinationBoundsTransformed = new Rect();
        private Rect mDisplayCutoutInsets;
        private int mFromRotation = 0;
        private Matrix mHomeToWindowPositionMap;
        private SurfaceControl mLeash;
        private int mShadowRadius;
        private Rect mSourceRectHint;
        private RectF mStartBounds;
        private int mTaskId;

        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setTaskId(int i) {
            this.mTaskId = i;
            return this;
        }

        public Builder setComponentName(ComponentName componentName) {
            this.mComponentName = componentName;
            return this;
        }

        public Builder setLeash(SurfaceControl surfaceControl) {
            this.mLeash = surfaceControl;
            return this;
        }

        public Builder setSourceRectHint(Rect rect) {
            this.mSourceRectHint = new Rect(rect);
            return this;
        }

        public Builder setAppBounds(Rect rect) {
            this.mAppBounds = new Rect(rect);
            return this;
        }

        public Builder setHomeToWindowPositionMap(Matrix matrix) {
            this.mHomeToWindowPositionMap = new Matrix(matrix);
            return this;
        }

        public Builder setStartBounds(RectF rectF) {
            this.mStartBounds = new RectF(rectF);
            return this;
        }

        public Builder setDestinationBounds(Rect rect) {
            this.mDestinationBounds = new Rect(rect);
            return this;
        }

        public Builder setCornerRadius(int i) {
            this.mCornerRadius = i;
            return this;
        }

        public Builder setShadowRadius(int i) {
            this.mShadowRadius = i;
            return this;
        }

        public Builder setAttachedView(View view) {
            this.mAttachedView = view;
            return this;
        }

        public Builder setFromRotation(TaskViewSimulator taskViewSimulator, int i, Rect rect) {
            if (i == 1 || i == 3) {
                Matrix matrix = new Matrix();
                taskViewSimulator.applyWindowToHomeRotation(matrix);
                RectF rectF = new RectF(this.mDestinationBounds);
                matrix.mapRect(rectF, new RectF(this.mDestinationBounds));
                rectF.round(this.mDestinationBoundsTransformed);
                this.mFromRotation = i;
                if (rect != null) {
                    this.mDisplayCutoutInsets = new Rect(rect);
                }
                return this;
            }
            Log.wtf(SwipePipToHomeAnimator.TAG, "Not a supported rotation, rotation=" + i);
            return this;
        }

        public SwipePipToHomeAnimator build() {
            Rect rect;
            if (this.mDestinationBoundsTransformed.isEmpty()) {
                this.mDestinationBoundsTransformed.set(this.mDestinationBounds);
            }
            Rect rect2 = this.mSourceRectHint;
            if (!(rect2 == null || (rect = this.mDisplayCutoutInsets) == null)) {
                int i = this.mFromRotation;
                if (i == 1) {
                    rect2.offset(rect.left, this.mDisplayCutoutInsets.top);
                } else if (i == 3) {
                    this.mAppBounds.inset(rect);
                }
            }
            return new SwipePipToHomeAnimator(this.mContext, this.mTaskId, this.mComponentName, this.mLeash, this.mSourceRectHint, this.mAppBounds, this.mHomeToWindowPositionMap, this.mStartBounds, this.mDestinationBounds, this.mFromRotation, this.mDestinationBoundsTransformed, this.mCornerRadius, this.mShadowRadius, this.mAttachedView);
        }
    }

    private static class RotatedPosition {
        /* access modifiers changed from: private */
        public final float degree;
        /* access modifiers changed from: private */
        public final float positionX;
        /* access modifiers changed from: private */
        public final float positionY;

        private RotatedPosition(float f, float f2, float f3) {
            this.degree = f;
            this.positionX = f2;
            this.positionY = f3;
        }
    }
}
