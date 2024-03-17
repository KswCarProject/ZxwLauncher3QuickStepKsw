package com.android.launcher3;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.DropTarget;
import com.android.launcher3.anim.AlphaUpdateListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.testing.TestProtocol;

public class DropTargetBar extends FrameLayout implements DragController.DragListener, Insettable {
    protected static final int DEFAULT_DRAG_FADE_DURATION = 175;
    protected static final TimeInterpolator DEFAULT_INTERPOLATOR = Interpolators.ACCEL;
    private ViewPropertyAnimator mCurrentAnimation;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected boolean mDeferOnDragEnd;
    private ButtonDropTarget[] mDropTargets;
    private final Runnable mFadeAnimationEndRunnable = new Runnable() {
        public final void run() {
            DropTargetBar.this.lambda$new$0$DropTargetBar();
        }
    };
    private boolean mIsVertical = true;
    private final Launcher mLauncher;
    private ButtonDropTarget[] mTempTargets;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected boolean mVisible = false;

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

    public /* synthetic */ void lambda$new$0$DropTargetBar() {
        AlphaUpdateListener.updateVisibility(this);
    }

    public DropTargetBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLauncher = Launcher.getLauncher(context);
    }

    public DropTargetBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLauncher = Launcher.getLauncher(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDropTargets = new ButtonDropTarget[getChildCount()];
        int i = 0;
        while (true) {
            ButtonDropTarget[] buttonDropTargetArr = this.mDropTargets;
            if (i < buttonDropTargetArr.length) {
                buttonDropTargetArr[i] = (ButtonDropTarget) getChildAt(i);
                this.mDropTargets[i].setDropTargetBar(this);
                i++;
            } else {
                this.mTempTargets = new ButtonDropTarget[getChildCount()];
                return;
            }
        }
    }

    public void setInsets(Rect rect) {
        int i;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mIsVertical = deviceProfile.isVerticalBarLayout();
        layoutParams.leftMargin = rect.left;
        layoutParams.topMargin = rect.top;
        layoutParams.bottomMargin = rect.bottom;
        layoutParams.rightMargin = rect.right;
        if (deviceProfile.isTablet) {
            i = (((deviceProfile.widthPx - (deviceProfile.edgeMarginPx * 2)) - (deviceProfile.inv.numColumns * deviceProfile.cellWidthPx)) / ((deviceProfile.inv.numColumns + 1) * 2)) + deviceProfile.edgeMarginPx;
        } else {
            i = getContext().getResources().getDimensionPixelSize(R.dimen.drop_target_bar_margin_horizontal);
        }
        layoutParams.topMargin += deviceProfile.dropTargetBarTopMarginPx;
        layoutParams.bottomMargin += deviceProfile.dropTargetBarBottomMarginPx;
        layoutParams.width = deviceProfile.availableWidthPx - (i * 2);
        if (this.mIsVertical) {
            layoutParams.leftMargin = (deviceProfile.widthPx - layoutParams.width) / 2;
            layoutParams.rightMargin = (deviceProfile.widthPx - layoutParams.width) / 2;
        }
        layoutParams.height = deviceProfile.dropTargetBarSizePx;
        layoutParams.gravity = 49;
        DeviceProfile deviceProfile2 = this.mLauncher.getDeviceProfile();
        int i2 = deviceProfile2.dropTargetHorizontalPaddingPx;
        int i3 = deviceProfile2.dropTargetVerticalPaddingPx;
        setLayoutParams(layoutParams);
        for (ButtonDropTarget buttonDropTarget : this.mDropTargets) {
            buttonDropTarget.setTextSize(0, (float) deviceProfile.dropTargetTextSizePx);
            buttonDropTarget.setToolTipLocation(0);
            buttonDropTarget.setPadding(i2, i3, i2, i3);
        }
    }

    public void setup(DragController dragController) {
        dragController.addDragListener(this);
        int i = 0;
        while (true) {
            ButtonDropTarget[] buttonDropTargetArr = this.mDropTargets;
            if (i < buttonDropTargetArr.length) {
                dragController.addDragListener(buttonDropTargetArr[i]);
                dragController.addDropTarget(this.mDropTargets[i]);
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size2, BasicMeasure.EXACTLY);
        int visibleButtons = getVisibleButtons(this.mTempTargets);
        if (visibleButtons == 1) {
            int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
            ButtonDropTarget buttonDropTarget = this.mTempTargets[0];
            buttonDropTarget.setTextVisible(true);
            buttonDropTarget.setIconVisible(true);
            buttonDropTarget.measure(makeMeasureSpec2, makeMeasureSpec);
        } else if (visibleButtons == 2) {
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            int i4 = deviceProfile.dropTargetVerticalPaddingPx;
            int i5 = deviceProfile.dropTargetHorizontalPaddingPx;
            ButtonDropTarget buttonDropTarget2 = this.mTempTargets[0];
            buttonDropTarget2.setTextVisible(true);
            buttonDropTarget2.setIconVisible(true);
            buttonDropTarget2.setTextMultiLine(false);
            buttonDropTarget2.setPadding(i5, i4, i5, i4);
            ButtonDropTarget buttonDropTarget3 = this.mTempTargets[1];
            buttonDropTarget3.setTextVisible(true);
            buttonDropTarget3.setIconVisible(true);
            buttonDropTarget3.setTextMultiLine(false);
            buttonDropTarget3.setPadding(i5, i4, i5, i4);
            int cellLayoutWidth = (int) (((float) deviceProfile.getCellLayoutWidth()) * deviceProfile.getWorkspaceSpringLoadScale());
            if (deviceProfile.isTwoPanels) {
                i3 = (deviceProfile.dropTargetGapPx / 2) / 2;
            } else {
                cellLayoutWidth -= deviceProfile.dropTargetGapPx;
                i3 = deviceProfile.dropTargetButtonWorkspaceEdgeGapPx * 2;
            }
            int i6 = cellLayoutWidth - i3;
            int makeMeasureSpec3 = View.MeasureSpec.makeMeasureSpec(i6, Integer.MIN_VALUE);
            buttonDropTarget2.measure(makeMeasureSpec3, makeMeasureSpec);
            if (!this.mIsVertical && buttonDropTarget2.isTextTruncated(i6)) {
                buttonDropTarget2.setIconVisible(false);
                buttonDropTarget2.setTextMultiLine(true);
                int i7 = i4 / 2;
                buttonDropTarget2.setPadding(i5, i7, i5, i7);
            }
            if (!deviceProfile.isTwoPanels) {
                i6 -= buttonDropTarget2.getMeasuredWidth() + deviceProfile.dropTargetGapPx;
                makeMeasureSpec3 = View.MeasureSpec.makeMeasureSpec(i6, Integer.MIN_VALUE);
            }
            buttonDropTarget3.measure(makeMeasureSpec3, makeMeasureSpec);
            if (!this.mIsVertical && buttonDropTarget3.isTextTruncated(i6)) {
                buttonDropTarget3.setIconVisible(false);
                buttonDropTarget3.setTextMultiLine(true);
                int i8 = i4 / 2;
                buttonDropTarget3.setPadding(i5, i8, i5, i8);
            }
        }
        setMeasuredDimension(size, size2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int visibleButtons = getVisibleButtons(this.mTempTargets);
        if (visibleButtons != 0) {
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            float workspaceSpringLoadScale = deviceProfile.getWorkspaceSpringLoadScale();
            Workspace<?> workspace = this.mLauncher.getWorkspace();
            if (deviceProfile.isTwoPanels) {
                i5 = (i3 - i) / 2;
            } else {
                int left = (workspace.getLeft() + workspace.getRight()) / 2;
                i5 = (left + ((int) (((float) ((((deviceProfile.getInsets().left + deviceProfile.workspacePadding.left) + ((deviceProfile.widthPx - deviceProfile.getInsets().right) - deviceProfile.workspacePadding.right)) / 2) - left)) * workspaceSpringLoadScale))) - i;
            }
            if (visibleButtons == 1) {
                ButtonDropTarget buttonDropTarget = this.mTempTargets[0];
                buttonDropTarget.layout(i5 - (buttonDropTarget.getMeasuredWidth() / 2), 0, i5 + (buttonDropTarget.getMeasuredWidth() / 2), buttonDropTarget.getMeasuredHeight());
            } else if (visibleButtons == 2) {
                int i6 = deviceProfile.dropTargetGapPx;
                ButtonDropTarget[] buttonDropTargetArr = this.mTempTargets;
                ButtonDropTarget buttonDropTarget2 = buttonDropTargetArr[0];
                ButtonDropTarget buttonDropTarget3 = buttonDropTargetArr[1];
                if (deviceProfile.isTwoPanels) {
                    int i7 = i6 / 2;
                    buttonDropTarget2.layout((i5 - buttonDropTarget2.getMeasuredWidth()) - i7, 0, i5 - i7, buttonDropTarget2.getMeasuredHeight());
                    int i8 = i5 + i7;
                    buttonDropTarget3.layout(i8, 0, buttonDropTarget3.getMeasuredWidth() + i8, buttonDropTarget3.getMeasuredHeight());
                    return;
                }
                int cellLayoutWidth = (int) (((float) deviceProfile.getCellLayoutWidth()) * workspaceSpringLoadScale);
                int measuredWidth = buttonDropTarget2.getMeasuredWidth();
                int measuredWidth2 = buttonDropTarget3.getMeasuredWidth();
                int i9 = (i5 - (cellLayoutWidth / 2)) + ((((cellLayoutWidth - measuredWidth) - measuredWidth2) - i6) / 2);
                int i10 = measuredWidth + i9;
                int i11 = i6 + i10;
                buttonDropTarget2.layout(i9, 0, i10, buttonDropTarget2.getMeasuredHeight());
                buttonDropTarget3.layout(i11, 0, measuredWidth2 + i11, buttonDropTarget3.getMeasuredHeight());
            }
        }
    }

    private int getVisibleButtons(ButtonDropTarget[] buttonDropTargetArr) {
        int i = 0;
        for (ButtonDropTarget buttonDropTarget : this.mDropTargets) {
            if (buttonDropTarget.getVisibility() != 8) {
                buttonDropTargetArr[i] = buttonDropTarget;
                i++;
            }
        }
        return i;
    }

    public void animateToVisibility(boolean z) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DROP_TARGET, "8");
        }
        if (this.mVisible != z) {
            this.mVisible = z;
            ViewPropertyAnimator viewPropertyAnimator = this.mCurrentAnimation;
            if (viewPropertyAnimator != null) {
                viewPropertyAnimator.cancel();
                this.mCurrentAnimation = null;
            }
            float f = this.mVisible ? 1.0f : 0.0f;
            if (Float.compare(getAlpha(), f) != 0) {
                setVisibility(0);
                this.mCurrentAnimation = animate().alpha(f).setInterpolator(DEFAULT_INTERPOLATOR).setDuration(175).withEndAction(this.mFadeAnimationEndRunnable);
            }
        }
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DROP_TARGET, "7");
        }
        animateToVisibility(true);
    }

    /* access modifiers changed from: protected */
    public void deferOnDragEnd() {
        this.mDeferOnDragEnd = true;
    }

    public void onDragEnd() {
        if (!this.mDeferOnDragEnd) {
            animateToVisibility(false);
        } else {
            this.mDeferOnDragEnd = false;
        }
    }

    public ButtonDropTarget[] getDropTargets() {
        return getVisibility() == 0 ? this.mDropTargets : new ButtonDropTarget[0];
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (!TestProtocol.sDebugTracing) {
            return;
        }
        if (i == 0) {
            Log.d(TestProtocol.NO_DROP_TARGET, "9");
        } else {
            Log.d(TestProtocol.NO_DROP_TARGET, "Hiding drop target", new Exception());
        }
    }
}
