package com.android.launcher3.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.PagedView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.util.Themes;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

public class KeyboardDragAndDropView extends AbstractFloatingView implements Insettable, StateManager.StateListener<LauncherState> {
    private static final long MINOR_AXIS_WEIGHT = 13;
    private VirtualNodeInfo mCurrentSelection;
    private final ArrayList<DragAndDropAccessibilityDelegate> mDelegates;
    private final RectFocusIndicator mFocusIndicator;
    private final ArrayList<Integer> mIntList;
    private final Launcher mLauncher;
    private final ArrayList<VirtualNodeInfo> mNodes;
    private final AccessibilityNodeInfoCompat mTempNodeInfo;
    private final Rect mTempRect;
    private final Rect mTempRect2;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 1024) != 0;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public KeyboardDragAndDropView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyboardDragAndDropView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIntList = new ArrayList<>();
        this.mDelegates = new ArrayList<>();
        this.mNodes = new ArrayList<>();
        this.mTempRect = new Rect();
        this.mTempRect2 = new Rect();
        this.mTempNodeInfo = AccessibilityNodeInfoCompat.obtain();
        this.mLauncher = Launcher.getLauncher(context);
        this.mFocusIndicator = new RectFocusIndicator(this);
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        this.mLauncher.getDragLayer().removeView(this);
        this.mLauncher.getStateManager().removeStateListener(this);
        this.mLauncher.setDefaultKeyMode(3);
        this.mIsOpen = false;
    }

    public void setInsets(Rect rect) {
        setPadding(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void onStateTransitionStart(LauncherState launcherState) {
        if (launcherState != LauncherState.SPRING_LOADED) {
            close(false);
        }
    }

    public void onStateTransitionComplete(LauncherState launcherState) {
        VirtualNodeInfo virtualNodeInfo = this.mCurrentSelection;
        if (virtualNodeInfo != null) {
            setCurrentSelection(virtualNodeInfo);
        }
    }

    private void setCurrentSelection(VirtualNodeInfo virtualNodeInfo) {
        this.mCurrentSelection = virtualNodeInfo;
        ((TextView) findViewById(R.id.label)).setText(virtualNodeInfo.populate(this.mTempNodeInfo).getContentDescription());
        Rect rect = new Rect();
        this.mTempNodeInfo.getBoundsInParent(rect);
        View host = virtualNodeInfo.delegate.getHost();
        ViewParent parent = host.getParent();
        if (parent instanceof PagedView) {
            PagedView pagedView = (PagedView) parent;
            int indexOfChild = pagedView.indexOfChild(host);
            pagedView.setCurrentPage(indexOfChild);
            rect.offset(pagedView.getScrollX() - pagedView.getScrollForPage(indexOfChild), 0);
        }
        float[] fArr = {(float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom};
        Utilities.getDescendantCoordRelativeToAncestor(host, this.mLauncher.getDragLayer(), fArr, true);
        new RectF(fArr[0], fArr[1], fArr[2], fArr[3]).roundOut(rect);
        this.mFocusIndicator.changeFocus(rect, true);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mFocusIndicator.draw(canvas);
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        VirtualNodeInfo nextSelection = getNextSelection(i);
        if (nextSelection == null) {
            return false;
        }
        setCurrentSelection(nextSelection);
        return true;
    }

    private VirtualNodeInfo getNextSelection(int i) {
        ToIntBiFunction toIntBiFunction;
        ToIntFunction toIntFunction;
        this.mDelegates.clear();
        this.mNodes.clear();
        Folder open = Folder.getOpen(this.mLauncher);
        PagedView workspace = open == null ? this.mLauncher.getWorkspace() : open.getContent();
        int pageCount = workspace.getPageCount();
        for (int i2 = 0; i2 < pageCount; i2++) {
            this.mDelegates.add(((CellLayout) workspace.getChildAt(i2)).getDragAndDropAccessibilityDelegate());
        }
        if (open == null) {
            this.mDelegates.add(workspace.getNextPage() + 1, this.mLauncher.getHotseat().getDragAndDropAccessibilityDelegate());
        }
        this.mDelegates.forEach(new Consumer() {
            public final void accept(Object obj) {
                KeyboardDragAndDropView.this.lambda$getNextSelection$1$KeyboardDragAndDropView((DragAndDropAccessibilityDelegate) obj);
            }
        });
        VirtualNodeInfo virtualNodeInfo = null;
        if (this.mNodes.isEmpty()) {
            return null;
        }
        int indexOf = this.mNodes.indexOf(this.mCurrentSelection);
        if (this.mCurrentSelection == null || indexOf < 0) {
            return null;
        }
        int size = this.mNodes.size();
        if (i == 1) {
            return this.mNodes.get(((indexOf + size) - 1) % size);
        }
        if (i == 2) {
            return this.mNodes.get((indexOf + 1) % size);
        }
        if (i == 17) {
            toIntBiFunction = $$Lambda$KeyboardDragAndDropView$qiHszqdHl6vRtZvnGaTfTxrujGU.INSTANCE;
            toIntFunction = $$Lambda$KeyboardDragAndDropView$8h9ZKp5MgQQYy1vIK8jKQdKbNek.INSTANCE;
        } else if (i == 33) {
            toIntBiFunction = $$Lambda$KeyboardDragAndDropView$rg0PEFKbuxaqTNqbDJMP6evPhI.INSTANCE;
            toIntFunction = $$Lambda$KeyboardDragAndDropView$7krswmVEmS33m36WpSNQ6RGUnrI.INSTANCE;
        } else if (i == 66) {
            toIntBiFunction = $$Lambda$KeyboardDragAndDropView$YGtqxNKnwTvgeHiSYK36m8XG5Qk.INSTANCE;
            toIntFunction = $$Lambda$KeyboardDragAndDropView$8h9ZKp5MgQQYy1vIK8jKQdKbNek.INSTANCE;
        } else if (i != 130) {
            return null;
        } else {
            toIntBiFunction = $$Lambda$KeyboardDragAndDropView$krCK49g9Jo2OXHpZiMMcCkk5uo.INSTANCE;
            toIntFunction = $$Lambda$KeyboardDragAndDropView$7krswmVEmS33m36WpSNQ6RGUnrI.INSTANCE;
        }
        this.mCurrentSelection.populate(this.mTempNodeInfo).getBoundsInScreen(this.mTempRect);
        float f = Float.MAX_VALUE;
        for (int i3 = 0; i3 < size; i3++) {
            VirtualNodeInfo virtualNodeInfo2 = this.mNodes.get(i3);
            virtualNodeInfo2.populate(this.mTempNodeInfo).getBoundsInScreen(this.mTempRect2);
            int applyAsInt = toIntBiFunction.applyAsInt(this.mTempRect, this.mTempRect2);
            if (applyAsInt > 0) {
                int applyAsInt2 = toIntFunction.applyAsInt(this.mTempRect2) - toIntFunction.applyAsInt(this.mTempRect);
                float f2 = (float) (((long) (applyAsInt * applyAsInt)) + (((long) (applyAsInt2 * applyAsInt2)) * MINOR_AXIS_WEIGHT));
                if (f2 < f) {
                    virtualNodeInfo = virtualNodeInfo2;
                    f = f2;
                }
            }
        }
        return virtualNodeInfo;
    }

    public /* synthetic */ void lambda$getNextSelection$1$KeyboardDragAndDropView(DragAndDropAccessibilityDelegate dragAndDropAccessibilityDelegate) {
        this.mIntList.clear();
        dragAndDropAccessibilityDelegate.getVisibleVirtualViews(this.mIntList);
        this.mIntList.forEach(new Consumer(dragAndDropAccessibilityDelegate) {
            public final /* synthetic */ DragAndDropAccessibilityDelegate f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                KeyboardDragAndDropView.this.lambda$getNextSelection$0$KeyboardDragAndDropView(this.f$1, (Integer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$getNextSelection$0$KeyboardDragAndDropView(DragAndDropAccessibilityDelegate dragAndDropAccessibilityDelegate, Integer num) {
        this.mNodes.add(new VirtualNodeInfo(dragAndDropAccessibilityDelegate, num.intValue()));
    }

    static /* synthetic */ int lambda$getNextSelection$2(Rect rect, Rect rect2) {
        return rect2.left - rect.left;
    }

    static /* synthetic */ int lambda$getNextSelection$3(Rect rect, Rect rect2) {
        return rect.left - rect2.left;
    }

    static /* synthetic */ int lambda$getNextSelection$4(Rect rect, Rect rect2) {
        return rect.top - rect2.top;
    }

    static /* synthetic */ int lambda$getNextSelection$5(Rect rect, Rect rect2) {
        return rect2.top - rect.top;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        VirtualNodeInfo virtualNodeInfo;
        if (i != 66 || (virtualNodeInfo = this.mCurrentSelection) == null) {
            return super.onKeyUp(i, keyEvent);
        }
        virtualNodeInfo.delegate.onPerformActionForVirtualView(this.mCurrentSelection.id, 16, (Bundle) null);
        return true;
    }

    public void showForIcon(View view, ItemInfo itemInfo, DragOptions dragOptions) {
        this.mIsOpen = true;
        this.mLauncher.getDragLayer().addView(this);
        this.mLauncher.getStateManager().addStateListener(this);
        CellLayout cellLayout = (CellLayout) view.getParent().getParent();
        float[] fArr = {(float) (cellLayout.getCellWidth() / 2), (float) (cellLayout.getCellHeight() / 2)};
        Utilities.getDescendantCoordRelativeToAncestor(view, cellLayout, fArr, false);
        ItemLongClickListener.beginDrag(view, this.mLauncher, itemInfo, dragOptions);
        DragAndDropAccessibilityDelegate dragAndDropAccessibilityDelegate = cellLayout.getDragAndDropAccessibilityDelegate();
        setCurrentSelection(new VirtualNodeInfo(dragAndDropAccessibilityDelegate, dragAndDropAccessibilityDelegate.getVirtualViewAt(fArr[0], fArr[1])));
        this.mLauncher.setDefaultKeyMode(0);
        requestFocus();
    }

    private static class VirtualNodeInfo {
        public final DragAndDropAccessibilityDelegate delegate;
        public final int id;

        VirtualNodeInfo(DragAndDropAccessibilityDelegate dragAndDropAccessibilityDelegate, int i) {
            this.id = i;
            this.delegate = dragAndDropAccessibilityDelegate;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof VirtualNodeInfo)) {
                return false;
            }
            VirtualNodeInfo virtualNodeInfo = (VirtualNodeInfo) obj;
            if (this.id != virtualNodeInfo.id || !this.delegate.equals(virtualNodeInfo.delegate)) {
                return false;
            }
            return true;
        }

        public AccessibilityNodeInfoCompat populate(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            this.delegate.onPopulateNodeForVirtualView(this.id, accessibilityNodeInfoCompat);
            return accessibilityNodeInfoCompat;
        }

        public void getBounds(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, Rect rect) {
            this.delegate.onPopulateNodeForVirtualView(this.id, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.getBoundsInScreen(rect);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(this.id), this.delegate});
        }
    }

    private static class RectFocusIndicator extends ItemFocusIndicatorHelper<Rect> {
        RectFocusIndicator(View view) {
            super(view, Themes.getColorAccent(view.getContext()));
            this.mPaint.setStrokeWidth(view.getResources().getDimension(R.dimen.keyboard_drag_stroke_width));
            this.mPaint.setStyle(Paint.Style.STROKE);
        }

        public void viewToRect(Rect rect, Rect rect2) {
            rect2.set(rect);
        }
    }
}
