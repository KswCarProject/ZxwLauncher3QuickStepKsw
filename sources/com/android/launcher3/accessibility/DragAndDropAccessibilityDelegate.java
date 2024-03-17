package com.android.launcher3.accessibility;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.dragndrop.DragLayer;
import java.util.List;

public abstract class DragAndDropAccessibilityDelegate extends ExploreByTouchHelper implements View.OnClickListener, View.OnHoverListener {
    protected static final int INVALID_POSITION = -1;
    protected final Context mContext;
    protected final LauncherAccessibilityDelegate mDelegate;
    protected final DragLayer mDragLayer;
    protected final int[] mTempCords = new int[2];
    protected final Rect mTempRect = new Rect();
    protected final CellLayout mView;

    /* access modifiers changed from: protected */
    public abstract String getConfirmationForIconDrop(int i);

    /* access modifiers changed from: protected */
    public abstract String getLocationDescriptionForIconDrop(int i);

    /* access modifiers changed from: protected */
    public abstract int intersectsValidDropTarget(int i);

    public DragAndDropAccessibilityDelegate(CellLayout cellLayout) {
        super(cellLayout);
        this.mView = cellLayout;
        Context context = cellLayout.getContext();
        this.mContext = context;
        Launcher launcher = Launcher.getLauncher(context);
        this.mDelegate = launcher.getAccessibilityDelegate();
        this.mDragLayer = launcher.getDragLayer();
    }

    public int getVirtualViewAt(float f, float f2) {
        if (f < 0.0f || f2 < 0.0f || f > ((float) this.mView.getMeasuredWidth()) || f2 > ((float) this.mView.getMeasuredHeight())) {
            return Integer.MIN_VALUE;
        }
        this.mView.pointToCellExact((int) f, (int) f2, this.mTempCords);
        int[] iArr = this.mTempCords;
        return intersectsValidDropTarget(iArr[0] + (iArr[1] * this.mView.getCountX()));
    }

    public void getVisibleVirtualViews(List<Integer> list) {
        int countX = this.mView.getCountX() * this.mView.getCountY();
        for (int i = 0; i < countX; i++) {
            if (intersectsValidDropTarget(i) == i) {
                list.add(Integer.valueOf(i));
            }
        }
    }

    public boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
        if (i2 != 16 || i == Integer.MIN_VALUE) {
            return false;
        }
        this.mDelegate.handleAccessibleDrop(this.mView, getItemBounds(i), getConfirmationForIconDrop(i));
        return true;
    }

    public void onClick(View view) {
        onPerformActionForVirtualView(getFocusedVirtualView(), 16, (Bundle) null);
    }

    /* access modifiers changed from: protected */
    public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
        if (i != Integer.MIN_VALUE) {
            accessibilityEvent.setContentDescription(this.mContext.getString(R.string.action_move_here));
            return;
        }
        throw new IllegalArgumentException("Invalid virtual view id");
    }

    public void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        if (i != Integer.MIN_VALUE) {
            accessibilityNodeInfoCompat.setContentDescription(getLocationDescriptionForIconDrop(i));
            Rect itemBounds = getItemBounds(i);
            accessibilityNodeInfoCompat.setBoundsInParent(itemBounds);
            int[] iArr = this.mTempCords;
            iArr[1] = 0;
            iArr[0] = 0;
            float descendantCoordRelativeToSelf = this.mDragLayer.getDescendantCoordRelativeToSelf((View) this.mView, iArr);
            this.mTempRect.left = this.mTempCords[0] + ((int) (((float) itemBounds.left) * descendantCoordRelativeToSelf));
            this.mTempRect.right = this.mTempCords[0] + ((int) (((float) itemBounds.right) * descendantCoordRelativeToSelf));
            this.mTempRect.top = this.mTempCords[1] + ((int) (((float) itemBounds.top) * descendantCoordRelativeToSelf));
            this.mTempRect.bottom = this.mTempCords[1] + ((int) (((float) itemBounds.bottom) * descendantCoordRelativeToSelf));
            accessibilityNodeInfoCompat.setBoundsInScreen(this.mTempRect);
            accessibilityNodeInfoCompat.addAction(16);
            accessibilityNodeInfoCompat.setClickable(true);
            accessibilityNodeInfoCompat.setFocusable(true);
            return;
        }
        throw new IllegalArgumentException("Invalid virtual view id");
    }

    public boolean onHover(View view, MotionEvent motionEvent) {
        return dispatchHoverEvent(motionEvent);
    }

    public View getHost() {
        return this.mView;
    }

    private Rect getItemBounds(int i) {
        int countX = i % this.mView.getCountX();
        int countX2 = i / this.mView.getCountX();
        BaseAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        this.mView.cellToRect(countX, countX2, dragInfo.info.spanX, dragInfo.info.spanY, this.mTempRect);
        return this.mTempRect;
    }
}
