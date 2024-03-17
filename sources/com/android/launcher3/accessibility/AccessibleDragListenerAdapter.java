package com.android.launcher3.accessibility;

import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import java.util.function.Function;

public class AccessibleDragListenerAdapter implements DragController.DragListener, ViewGroup.OnHierarchyChangeListener {
    private final Function<CellLayout, DragAndDropAccessibilityDelegate> mDelegateFactory;
    private final ViewGroup mViewGroup;

    public AccessibleDragListenerAdapter(ViewGroup viewGroup, Function<CellLayout, DragAndDropAccessibilityDelegate> function) {
        this.mViewGroup = viewGroup;
        this.mDelegateFactory = function;
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        this.mViewGroup.setOnHierarchyChangeListener(this);
        enableAccessibleDrag(true);
    }

    public void onDragEnd() {
        this.mViewGroup.setOnHierarchyChangeListener((ViewGroup.OnHierarchyChangeListener) null);
        enableAccessibleDrag(false);
        Launcher.getLauncher(this.mViewGroup.getContext()).getDragController().removeDragListener(this);
    }

    public void onChildViewAdded(View view, View view2) {
        if (view == this.mViewGroup) {
            setEnableForLayout((CellLayout) view2, true);
        }
    }

    public void onChildViewRemoved(View view, View view2) {
        if (view == this.mViewGroup) {
            setEnableForLayout((CellLayout) view2, false);
        }
    }

    /* access modifiers changed from: protected */
    public void enableAccessibleDrag(boolean z) {
        for (int i = 0; i < this.mViewGroup.getChildCount(); i++) {
            setEnableForLayout((CellLayout) this.mViewGroup.getChildAt(i), z);
        }
    }

    /* access modifiers changed from: protected */
    public final void setEnableForLayout(CellLayout cellLayout, boolean z) {
        cellLayout.setDragAndDropAccessibilityDelegate(z ? this.mDelegateFactory.apply(cellLayout) : null);
    }
}
