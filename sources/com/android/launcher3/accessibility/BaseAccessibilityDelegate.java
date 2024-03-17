package com.android.launcher3.accessibility;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DropTarget;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BubbleTextHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseAccessibilityDelegate<T extends Context & ActivityContext> extends View.AccessibilityDelegate implements DragController.DragListener {
    protected final SparseArray<BaseAccessibilityDelegate<T>.LauncherAction> mActions = new SparseArray<>();
    protected final T mContext;
    protected DragInfo mDragInfo = null;

    public static class DragInfo {
        public DragType dragType;
        public ItemInfo info;
        public View item;
    }

    public enum DragType {
        ICON,
        FOLDER,
        WIDGET
    }

    /* access modifiers changed from: protected */
    public abstract boolean beginAccessibleDrag(View view, ItemInfo itemInfo, boolean z);

    /* access modifiers changed from: protected */
    public abstract void getSupportedActions(View view, ItemInfo itemInfo, List<BaseAccessibilityDelegate<T>.LauncherAction> list);

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
    }

    /* access modifiers changed from: protected */
    public abstract boolean performAction(View view, ItemInfo itemInfo, int i, boolean z);

    protected BaseAccessibilityDelegate(T t) {
        this.mContext = t;
    }

    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        if (view.getTag() instanceof ItemInfo) {
            ArrayList arrayList = new ArrayList();
            getSupportedActions(view, (ItemInfo) view.getTag(), arrayList);
            arrayList.forEach(new Consumer(accessibilityNodeInfo) {
                public final /* synthetic */ AccessibilityNodeInfo f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.addAction(((BaseAccessibilityDelegate.LauncherAction) obj).accessibilityAction);
                }
            });
            if (!itemSupportsLongClick(view)) {
                accessibilityNodeInfo.setLongClickable(false);
                accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
            }
        }
    }

    private boolean itemSupportsLongClick(View view) {
        if (view instanceof BubbleTextView) {
            return ((BubbleTextView) view).canShowLongPressPopup();
        }
        if (!(view instanceof BubbleTextHolder)) {
            return false;
        }
        BubbleTextHolder bubbleTextHolder = (BubbleTextHolder) view;
        if (bubbleTextHolder.getBubbleText() == null || !bubbleTextHolder.getBubbleText().canShowLongPressPopup()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean itemSupportsAccessibleDrag(ItemInfo itemInfo) {
        if (itemInfo instanceof WorkspaceItemInfo) {
            if (itemInfo.screenId < 0 || itemInfo.container == -103) {
                return false;
            }
            return true;
        } else if ((itemInfo instanceof LauncherAppWidgetInfo) || (itemInfo instanceof FolderInfo)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        if (!(view.getTag() instanceof ItemInfo) || !performAction(view, (ItemInfo) view.getTag(), i, false)) {
            return super.performAccessibilityAction(view, i, bundle);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void announceConfirmation(String str) {
        ((ActivityContext) this.mContext).getDragLayer().announceForAccessibility(str);
    }

    public boolean isInAccessibleDrag() {
        return this.mDragInfo != null;
    }

    public DragInfo getDragInfo() {
        return this.mDragInfo;
    }

    public void handleAccessibleDrop(View view, Rect rect, String str) {
        if (isInAccessibleDrag()) {
            int[] iArr = new int[2];
            if (rect == null) {
                iArr[0] = view.getWidth() / 2;
                iArr[1] = view.getHeight() / 2;
            } else {
                iArr[0] = rect.centerX();
                iArr[1] = rect.centerY();
            }
            ((ActivityContext) this.mContext).getDragLayer().getDescendantCoordRelativeToSelf(view, iArr);
            ((ActivityContext) this.mContext).getDragController().completeAccessibleDrag(iArr);
            if (!TextUtils.isEmpty(str)) {
                announceConfirmation(str);
            }
        }
    }

    public void onDragEnd() {
        ((ActivityContext) this.mContext).getDragController().removeDragListener(this);
        this.mDragInfo = null;
    }

    public class LauncherAction {
        public final AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
        public final int keyCode;
        private final BaseAccessibilityDelegate<T> mDelegate;

        public LauncherAction(int i, int i2, int i3) {
            this.keyCode = i3;
            this.accessibilityAction = new AccessibilityNodeInfo.AccessibilityAction(i, BaseAccessibilityDelegate.this.mContext.getString(i2));
            this.mDelegate = BaseAccessibilityDelegate.this;
        }

        public boolean invokeFromKeyboard(View view) {
            if (view == null || !(view.getTag() instanceof ItemInfo)) {
                return false;
            }
            return this.mDelegate.performAction(view, (ItemInfo) view.getTag(), this.accessibilityAction.getId(), true);
        }
    }
}
