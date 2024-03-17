package com.android.launcher3.uioverrides;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.icons.IconNormalizer;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.util.SafeCloseable;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.DoubleShadowBubbleTextView;
import com.android.systemui.shared.system.SysUiStatsLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class PredictedAppIcon extends DoubleShadowBubbleTextView {
    private static final long ICON_CHANGE_ANIM_DURATION = 360;
    private static final long ICON_CHANGE_ANIM_STAGGER = 50;
    private static final float RING_EFFECT_RATIO = 0.095f;
    private static final int RING_SHADOW_COLOR = -1728053248;
    private static final FloatProperty<PredictedAppIcon> SLOT_MACHINE_TRANSLATION_Y = new FloatProperty<PredictedAppIcon>("slotMachineTranslationY") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(PredictedAppIcon predictedAppIcon, float f) {
            float unused = predictedAppIcon.mSlotMachineIconTranslationY = f;
            predictedAppIcon.invalidate();
        }

        public Float get(PredictedAppIcon predictedAppIcon) {
            return Float.valueOf(predictedAppIcon.mSlotMachineIconTranslationY);
        }
    };
    private final DeviceProfile mDeviceProfile;
    boolean mDrawForDrag;
    private final Paint mIconRingPaint;
    boolean mIsDrawingDot;
    private boolean mIsPinned;
    private final int mNormalizedIconSize;
    private int mPlateColor;
    private final Path mRingPath;
    private final BlurMaskFilter mShadowFilter;
    /* access modifiers changed from: private */
    public final Path mShapePath;
    private Animator mSlotMachineAnim;
    /* access modifiers changed from: private */
    public float mSlotMachineIconTranslationY;
    private List<Drawable> mSlotMachineIcons;
    private final Matrix mTmpMatrix;

    public PredictedAppIcon(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public PredictedAppIcon(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PredictedAppIcon(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsDrawingDot = false;
        this.mIconRingPaint = new Paint(1);
        this.mRingPath = new Path();
        this.mTmpMatrix = new Matrix();
        this.mIsPinned = false;
        this.mDrawForDrag = false;
        this.mDeviceProfile = ((ActivityContext) ActivityContext.lookupContext(context)).getDeviceProfile();
        int normalizedCircleSize = IconNormalizer.getNormalizedCircleSize(getIconSize());
        this.mNormalizedIconSize = normalizedCircleSize;
        this.mShadowFilter = new BlurMaskFilter((float) context.getResources().getDimensionPixelSize(R.dimen.blur_size_thin_outline), BlurMaskFilter.Blur.OUTER);
        this.mShapePath = GraphicsUtils.getShapePath(normalizedCircleSize);
    }

    public void onDraw(Canvas canvas) {
        int save = canvas.save();
        boolean z = this.mSlotMachineAnim != null;
        if (!this.mIsPinned) {
            drawEffect(canvas);
            if (z) {
                canvas.clipPath(this.mRingPath);
            }
            canvas.translate(((float) getWidth()) * RING_EFFECT_RATIO, ((float) getHeight()) * RING_EFFECT_RATIO);
            canvas.scale(0.81f, 0.81f);
        }
        if (z) {
            drawSlotMachineIcons(canvas);
        } else {
            super.onDraw(canvas);
        }
        canvas.restoreToCount(save);
    }

    private void drawSlotMachineIcons(Canvas canvas) {
        canvas.translate(((float) (getWidth() - getIconSize())) / 2.0f, (((float) (getHeight() - getIconSize())) / 2.0f) + this.mSlotMachineIconTranslationY);
        for (Drawable next : this.mSlotMachineIcons) {
            next.setBounds(0, 0, getIconSize(), getIconSize());
            next.draw(canvas);
            canvas.translate(0.0f, getSlotMachineIconPlusSpacingSize());
        }
    }

    private float getSlotMachineIconPlusSpacingSize() {
        return (float) (getIconSize() + getOutlineOffsetY());
    }

    /* access modifiers changed from: protected */
    public void drawDotIfNecessary(Canvas canvas) {
        this.mIsDrawingDot = true;
        int save = canvas.save();
        canvas.translate(((float) (-getWidth())) * RING_EFFECT_RATIO, ((float) (-getHeight())) * RING_EFFECT_RATIO);
        canvas.scale(1.19f, 1.19f);
        super.drawDotIfNecessary(canvas);
        canvas.restoreToCount(save);
        this.mIsDrawingDot = false;
    }

    public void applyFromWorkspaceItem(WorkspaceItemInfo workspaceItemInfo, boolean z, int i) {
        Animator createSlotMachineAnim = z ? createSlotMachineAnim(Collections.singletonList(workspaceItemInfo.bitmap), false) : null;
        super.applyFromWorkspaceItem(workspaceItemInfo, z, i);
        int i2 = this.mPlateColor;
        int alphaComponent = ColorUtils.setAlphaComponent(this.mDotParams.appColor, 200);
        if (!z) {
            this.mPlateColor = alphaComponent;
        }
        if (this.mIsPinned) {
            setContentDescription(workspaceItemInfo.contentDescription);
        } else {
            setContentDescription(getContext().getString(R.string.hotseat_prediction_content_description, new Object[]{workspaceItemInfo.contentDescription}));
        }
        if (z) {
            ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{Integer.valueOf(i2), Integer.valueOf(alphaComponent)});
            ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PredictedAppIcon.this.lambda$applyFromWorkspaceItem$0$PredictedAppIcon(valueAnimator);
                }
            });
            AnimatorSet animatorSet = new AnimatorSet();
            if (createSlotMachineAnim != null) {
                animatorSet.play(createSlotMachineAnim);
            }
            animatorSet.play(ofObject);
            animatorSet.setStartDelay(((long) i) * ICON_CHANGE_ANIM_STAGGER);
            animatorSet.setDuration(ICON_CHANGE_ANIM_DURATION).start();
        }
    }

    public /* synthetic */ void lambda$applyFromWorkspaceItem$0$PredictedAppIcon(ValueAnimator valueAnimator) {
        this.mPlateColor = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        invalidate();
    }

    public Animator createSlotMachineAnim(List<BitmapInfo> list) {
        return createSlotMachineAnim(list, true);
    }

    public Animator createSlotMachineAnim(List<BitmapInfo> list, boolean z) {
        if (this.mIsPinned || list == null || list.isEmpty()) {
            return null;
        }
        Animator animator = this.mSlotMachineAnim;
        if (animator != null) {
            animator.end();
        }
        ArrayList arrayList = new ArrayList(list.size() + 2);
        this.mSlotMachineIcons = arrayList;
        arrayList.add(getIcon());
        Stream map = list.stream().map(new Function() {
            public final Object apply(Object obj) {
                return PredictedAppIcon.this.lambda$createSlotMachineAnim$1$PredictedAppIcon((BitmapInfo) obj);
            }
        });
        List<Drawable> list2 = this.mSlotMachineIcons;
        Objects.requireNonNull(list2);
        map.forEach(new Consumer(list2) {
            public final /* synthetic */ List f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                this.f$0.add((FastBitmapDrawable) obj);
            }
        });
        if (z) {
            this.mSlotMachineIcons.add(getIcon());
        }
        float size = (-getSlotMachineIconPlusSpacingSize()) * ((float) (this.mSlotMachineIcons.size() - 1));
        Keyframe[] keyframeArr = {Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.82f, size - (((float) getOutlineOffsetY()) / 2.0f)), Keyframe.ofFloat(1.0f, size)};
        keyframeArr[1].setInterpolator(Interpolators.ACCEL_DEACCEL);
        keyframeArr[2].setInterpolator(Interpolators.ACCEL_DEACCEL);
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofKeyframe(SLOT_MACHINE_TRANSLATION_Y, keyframeArr)});
        this.mSlotMachineAnim = ofPropertyValuesHolder;
        ofPropertyValuesHolder.addListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
            public final void run() {
                PredictedAppIcon.this.lambda$createSlotMachineAnim$2$PredictedAppIcon();
            }
        }));
        return this.mSlotMachineAnim;
    }

    public /* synthetic */ FastBitmapDrawable lambda$createSlotMachineAnim$1$PredictedAppIcon(BitmapInfo bitmapInfo) {
        return bitmapInfo.newIcon(this.mContext, 1);
    }

    public /* synthetic */ void lambda$createSlotMachineAnim$2$PredictedAppIcon() {
        this.mSlotMachineIcons = null;
        this.mSlotMachineAnim = null;
        this.mSlotMachineIconTranslationY = 0.0f;
        invalidate();
    }

    public void pin(WorkspaceItemInfo workspaceItemInfo) {
        if (!this.mIsPinned) {
            this.mIsPinned = true;
            applyFromWorkspaceItem(workspaceItemInfo);
            setOnLongClickListener(ItemLongClickListener.INSTANCE_WORKSPACE);
            ((CellLayout.LayoutParams) getLayoutParams()).canReorder = true;
            invalidate();
        }
    }

    public void finishBinding(View.OnLongClickListener onLongClickListener) {
        setOnLongClickListener(onLongClickListener);
        ((CellLayout.LayoutParams) getLayoutParams()).canReorder = false;
        setTextVisibility(false);
        verifyHighRes();
    }

    public void getIconBounds(Rect rect) {
        super.getIconBounds(rect);
        if (!this.mIsPinned && !this.mIsDrawingDot) {
            int iconSize = (int) (((float) getIconSize()) * RING_EFFECT_RATIO);
            rect.inset(iconSize, iconSize);
        }
    }

    public boolean isPinned() {
        return this.mIsPinned;
    }

    /* access modifiers changed from: private */
    public int getOutlineOffsetX() {
        return (getMeasuredWidth() - this.mNormalizedIconSize) / 2;
    }

    /* access modifiers changed from: private */
    public int getOutlineOffsetY() {
        if (this.mDisplay != 5) {
            return getPaddingTop() + this.mDeviceProfile.folderIconOffsetYPx;
        }
        return (getMeasuredHeight() - this.mNormalizedIconSize) / 2;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateRingPath();
    }

    public void setTag(Object obj) {
        super.setTag(obj);
        updateRingPath();
    }

    private void updateRingPath() {
        boolean z = false;
        if (getTag() instanceof WorkspaceItemInfo) {
            WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) getTag();
            if (!Process.myUserHandle().equals(workspaceItemInfo.user) || workspaceItemInfo.itemType == 1 || workspaceItemInfo.itemType == 6) {
                z = true;
            }
        }
        this.mRingPath.reset();
        this.mTmpMatrix.setTranslate((float) getOutlineOffsetX(), (float) getOutlineOffsetY());
        this.mRingPath.addPath(this.mShapePath, this.mTmpMatrix);
        if (z) {
            float badgeSizeForIconSize = ((float) LauncherIcons.getBadgeSizeForIconSize((int) (((float) getIconSize()) * 0.81f))) + (((float) this.mNormalizedIconSize) * RING_EFFECT_RATIO);
            int i = this.mNormalizedIconSize;
            float f = badgeSizeForIconSize / ((float) i);
            this.mTmpMatrix.postTranslate((float) i, (float) i);
            this.mTmpMatrix.preScale(f, f);
            Matrix matrix = this.mTmpMatrix;
            int i2 = this.mNormalizedIconSize;
            matrix.preTranslate((float) (-i2), (float) (-i2));
            this.mRingPath.addPath(this.mShapePath, this.mTmpMatrix);
        }
    }

    private void drawEffect(Canvas canvas) {
        if (!this.mDrawForDrag) {
            this.mIconRingPaint.setColor(RING_SHADOW_COLOR);
            this.mIconRingPaint.setMaskFilter(this.mShadowFilter);
            canvas.drawPath(this.mRingPath, this.mIconRingPaint);
            this.mIconRingPaint.setColor(this.mPlateColor);
            this.mIconRingPaint.setMaskFilter((MaskFilter) null);
            canvas.drawPath(this.mRingPath, this.mIconRingPaint);
        }
    }

    public void getSourceVisualDragBounds(Rect rect) {
        super.getSourceVisualDragBounds(rect);
        if (!this.mIsPinned) {
            int width = (int) (((float) rect.width()) * RING_EFFECT_RATIO);
            rect.inset(width, width);
        }
    }

    public SafeCloseable prepareDrawDragView() {
        this.mDrawForDrag = true;
        invalidate();
        return new SafeCloseable(super.prepareDrawDragView()) {
            public final /* synthetic */ SafeCloseable f$1;

            {
                this.f$1 = r2;
            }

            public final void close() {
                PredictedAppIcon.this.lambda$prepareDrawDragView$3$PredictedAppIcon(this.f$1);
            }
        };
    }

    public /* synthetic */ void lambda$prepareDrawDragView$3$PredictedAppIcon(SafeCloseable safeCloseable) {
        safeCloseable.close();
        this.mDrawForDrag = false;
    }

    public static PredictedAppIcon createIcon(ViewGroup viewGroup, WorkspaceItemInfo workspaceItemInfo) {
        PredictedAppIcon predictedAppIcon = (PredictedAppIcon) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.predicted_app_icon, viewGroup, false);
        predictedAppIcon.applyFromWorkspaceItem(workspaceItemInfo);
        predictedAppIcon.setOnClickListener(ItemClickHandler.INSTANCE);
        predictedAppIcon.setOnFocusChangeListener(Launcher.getLauncher(viewGroup.getContext()).getFocusHandler());
        return predictedAppIcon;
    }

    public static class PredictedIconOutlineDrawing extends CellLayout.DelegatedCellDrawing {
        private final PredictedAppIcon mIcon;
        private final Paint mOutlinePaint;

        public void drawOverItem(Canvas canvas) {
        }

        public PredictedIconOutlineDrawing(int i, int i2, PredictedAppIcon predictedAppIcon) {
            Paint paint = new Paint(1);
            this.mOutlinePaint = paint;
            this.mDelegateCellX = i;
            this.mDelegateCellY = i2;
            this.mIcon = predictedAppIcon;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(24, SysUiStatsLog.NOTIFICATION_PANEL_REPORTED, SysUiStatsLog.NOTIFICATION_PANEL_REPORTED, SysUiStatsLog.NOTIFICATION_PANEL_REPORTED));
        }

        public void drawUnderItem(Canvas canvas) {
            canvas.save();
            canvas.translate((float) this.mIcon.getOutlineOffsetX(), (float) this.mIcon.getOutlineOffsetY());
            canvas.drawPath(this.mIcon.mShapePath, this.mOutlinePaint);
            canvas.restore();
        }
    }
}
