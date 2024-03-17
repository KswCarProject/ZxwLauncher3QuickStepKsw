package com.android.launcher3.accessibility;

import android.appwidget.AppWidgetProviderInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.R;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.keyboard.KeyboardDragAndDropView;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemFactory;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.ShortcutUtil;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.OptionsPopupView;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.util.WidgetSizes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LauncherAccessibilityDelegate extends BaseAccessibilityDelegate<Launcher> {
    protected static final int ADD_TO_WORKSPACE = 2131296307;
    public static final int DEEP_SHORTCUTS = 2131296319;
    public static final int DISMISS_PREDICTION = 2131296321;
    public static final int INVALID = -1;
    protected static final int MOVE = 2131296329;
    protected static final int MOVE_TO_WORKSPACE = 2131296334;
    public static final int PIN_PREDICTION = 2131296335;
    public static final int RECONFIGURE = 2131296336;
    public static final int REMOVE = 2131296338;
    protected static final int RESIZE = 2131296339;
    public static final int SHORTCUTS_AND_NOTIFICATIONS = 2131296341;
    private static final String TAG = "LauncherAccessibilityDelegate";
    public static final int UNINSTALL = 2131296345;

    public LauncherAccessibilityDelegate(Launcher launcher) {
        super(launcher);
        this.mActions.put(R.id.action_remove, new BaseAccessibilityDelegate.LauncherAction(R.id.action_remove, R.string.remove_drop_target_label, 52));
        this.mActions.put(R.id.action_uninstall, new BaseAccessibilityDelegate.LauncherAction(R.id.action_uninstall, R.string.uninstall_drop_target_label, 49));
        this.mActions.put(R.id.action_dismiss_prediction, new BaseAccessibilityDelegate.LauncherAction(R.id.action_dismiss_prediction, R.string.dismiss_prediction_label, 52));
        this.mActions.put(R.id.action_reconfigure, new BaseAccessibilityDelegate.LauncherAction(R.id.action_reconfigure, R.string.gadget_setup_text, 33));
        this.mActions.put(R.id.action_add_to_workspace, new BaseAccessibilityDelegate.LauncherAction(R.id.action_add_to_workspace, R.string.action_add_to_workspace, 44));
        this.mActions.put(R.id.action_move, new BaseAccessibilityDelegate.LauncherAction(R.id.action_move, R.string.action_move, 41));
        this.mActions.put(R.id.action_move_to_workspace, new BaseAccessibilityDelegate.LauncherAction(R.id.action_move_to_workspace, R.string.action_move_to_workspace, 44));
        this.mActions.put(R.id.action_resize, new BaseAccessibilityDelegate.LauncherAction(R.id.action_resize, R.string.action_resize, 46));
        this.mActions.put(R.id.action_deep_shortcuts, new BaseAccessibilityDelegate.LauncherAction(R.id.action_deep_shortcuts, R.string.action_deep_shortcut, 47));
        this.mActions.put(R.id.action_shortcuts_and_notifications, new BaseAccessibilityDelegate.LauncherAction(R.id.action_deep_shortcuts, R.string.shortcuts_menu_with_notifications_description, 47));
    }

    /* access modifiers changed from: protected */
    public void getSupportedActions(View view, ItemInfo itemInfo, List<BaseAccessibilityDelegate<Launcher>.LauncherAction> list) {
        if (ShortcutUtil.supportsShortcuts(itemInfo)) {
            list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(NotificationListener.getInstanceIfConnected() != null ? R.id.action_shortcuts_and_notifications : R.id.action_deep_shortcuts));
        }
        for (ButtonDropTarget buttonDropTarget : ((Launcher) this.mContext).getDropTargetBar().getDropTargets()) {
            if (buttonDropTarget.supportsAccessibilityDrop(itemInfo, view)) {
                list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(buttonDropTarget.getAccessibilityAction()));
            }
        }
        if (itemSupportsAccessibleDrag(itemInfo)) {
            list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(R.id.action_move));
            if (itemInfo.container >= 0) {
                list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(R.id.action_move_to_workspace));
            } else if ((itemInfo instanceof LauncherAppWidgetInfo) && !getSupportedResizeActions(view, (LauncherAppWidgetInfo) itemInfo).isEmpty()) {
                list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(R.id.action_resize));
            }
        }
        if ((itemInfo instanceof WorkspaceItemFactory) || (itemInfo instanceof WorkspaceItemInfo) || (itemInfo instanceof PendingAddItemInfo)) {
            list.add((BaseAccessibilityDelegate.LauncherAction) this.mActions.get(R.id.action_add_to_workspace));
        }
    }

    public static List<BaseAccessibilityDelegate<Launcher>.LauncherAction> getSupportedActions(Launcher launcher, View view) {
        if (view == null || !(view.getTag() instanceof ItemInfo)) {
            return Collections.emptyList();
        }
        PopupContainerWithArrow open = PopupContainerWithArrow.getOpen(launcher);
        LauncherAccessibilityDelegate accessibilityDelegate = open != null ? open.getAccessibilityDelegate() : launcher.getAccessibilityDelegate();
        ArrayList arrayList = new ArrayList();
        accessibilityDelegate.getSupportedActions(view, (ItemInfo) view.getTag(), arrayList);
        return arrayList;
    }

    /* JADX WARNING: type inference failed for: r7v8, types: [com.android.launcher3.dragndrop.DragOptions$PreDragCondition] */
    /* JADX WARNING: type inference failed for: r0v9, types: [com.android.launcher3.dragndrop.DragOptions$PreDragCondition] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean performAction(android.view.View r7, com.android.launcher3.model.data.ItemInfo r8, int r9, boolean r10) {
        /*
            r6 = this;
            r0 = 0
            r1 = 0
            r2 = 1
            r3 = 32
            if (r9 != r3) goto L_0x002c
            boolean r8 = r7 instanceof com.android.launcher3.BubbleTextView
            if (r8 == 0) goto L_0x0012
            com.android.launcher3.BubbleTextView r7 = (com.android.launcher3.BubbleTextView) r7
            com.android.launcher3.dragndrop.DragOptions$PreDragCondition r0 = r7.startLongPressAction()
            goto L_0x0028
        L_0x0012:
            boolean r8 = r7 instanceof com.android.launcher3.views.BubbleTextHolder
            if (r8 == 0) goto L_0x0028
            com.android.launcher3.views.BubbleTextHolder r7 = (com.android.launcher3.views.BubbleTextHolder) r7
            com.android.launcher3.BubbleTextView r8 = r7.getBubbleText()
            if (r8 != 0) goto L_0x001f
            goto L_0x0028
        L_0x001f:
            com.android.launcher3.BubbleTextView r7 = r7.getBubbleText()
            com.android.launcher3.dragndrop.DragOptions$PreDragCondition r7 = r7.startLongPressAction()
            r0 = r7
        L_0x0028:
            if (r0 == 0) goto L_0x002b
            r1 = r2
        L_0x002b:
            return r1
        L_0x002c:
            r3 = 2131296329(0x7f090049, float:1.8210572E38)
            if (r9 != r3) goto L_0x0036
            boolean r7 = r6.beginAccessibleDrag(r7, r8, r10)
            return r7
        L_0x0036:
            r10 = 2131296307(0x7f090033, float:1.8210527E38)
            if (r9 != r10) goto L_0x0040
            boolean r7 = r6.addToWorkspace(r8, r2)
            return r7
        L_0x0040:
            r10 = 2131296334(0x7f09004e, float:1.8210582E38)
            if (r9 != r10) goto L_0x004a
            boolean r7 = r6.moveToWorkspace(r8)
            return r7
        L_0x004a:
            r10 = 2131296339(0x7f090053, float:1.8210592E38)
            if (r9 != r10) goto L_0x0081
            com.android.launcher3.model.data.LauncherAppWidgetInfo r8 = (com.android.launcher3.model.data.LauncherAppWidgetInfo) r8
            java.util.List r8 = r6.getSupportedResizeActions(r7, r8)
            android.graphics.Rect r9 = new android.graphics.Rect
            r9.<init>()
            android.content.Context r10 = r6.mContext
            com.android.launcher3.Launcher r10 = (com.android.launcher3.Launcher) r10
            com.android.launcher3.dragndrop.DragLayer r10 = r10.getDragLayer()
            r10.getDescendantRectRelativeToSelf(r7, r9)
            android.content.Context r10 = r6.mContext
            com.android.launcher3.Launcher r10 = (com.android.launcher3.Launcher) r10
            android.graphics.RectF r0 = new android.graphics.RectF
            r0.<init>(r9)
            com.android.launcher3.views.OptionsPopupView r8 = com.android.launcher3.views.OptionsPopupView.show(r10, r0, r8, r1)
            r8.requestFocus()
            java.util.Objects.requireNonNull(r7)
            com.android.launcher3.accessibility.-$$Lambda$LauncherAccessibilityDelegate$VfYntLYfUVJH6rtK98iXRKTZLKw r9 = new com.android.launcher3.accessibility.-$$Lambda$LauncherAccessibilityDelegate$VfYntLYfUVJH6rtK98iXRKTZLKw
            r9.<init>(r7)
            r8.setOnCloseCallback(r9)
            return r2
        L_0x0081:
            r10 = 2131296319(0x7f09003f, float:1.8210551E38)
            if (r9 == r10) goto L_0x00b2
            r10 = 2131296341(0x7f090055, float:1.8210596E38)
            if (r9 != r10) goto L_0x008c
            goto L_0x00b2
        L_0x008c:
            android.content.Context r10 = r6.mContext
            com.android.launcher3.Launcher r10 = (com.android.launcher3.Launcher) r10
            com.android.launcher3.DropTargetBar r10 = r10.getDropTargetBar()
            com.android.launcher3.ButtonDropTarget[] r10 = r10.getDropTargets()
            int r0 = r10.length
            r3 = r1
        L_0x009a:
            if (r3 >= r0) goto L_0x00b1
            r4 = r10[r3]
            boolean r5 = r4.supportsAccessibilityDrop(r8, r7)
            if (r5 == 0) goto L_0x00ae
            int r5 = r4.getAccessibilityAction()
            if (r9 != r5) goto L_0x00ae
            r4.onAccessibilityDrop(r7, r8)
            return r2
        L_0x00ae:
            int r3 = r3 + 1
            goto L_0x009a
        L_0x00b1:
            return r1
        L_0x00b2:
            boolean r8 = r7 instanceof com.android.launcher3.BubbleTextView
            if (r8 == 0) goto L_0x00ba
            r0 = r7
            com.android.launcher3.BubbleTextView r0 = (com.android.launcher3.BubbleTextView) r0
            goto L_0x00c4
        L_0x00ba:
            boolean r8 = r7 instanceof com.android.launcher3.views.BubbleTextHolder
            if (r8 == 0) goto L_0x00c4
            com.android.launcher3.views.BubbleTextHolder r7 = (com.android.launcher3.views.BubbleTextHolder) r7
            com.android.launcher3.BubbleTextView r0 = r7.getBubbleText()
        L_0x00c4:
            if (r0 == 0) goto L_0x00cd
            com.android.launcher3.popup.PopupContainerWithArrow r7 = com.android.launcher3.popup.PopupContainerWithArrow.showForIcon(r0)
            if (r7 == 0) goto L_0x00cd
            r1 = r2
        L_0x00cd:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.accessibility.LauncherAccessibilityDelegate.performAction(android.view.View, com.android.launcher3.model.data.ItemInfo, int, boolean):boolean");
    }

    private List<OptionsPopupView.OptionItem> getSupportedResizeActions(View view, LauncherAppWidgetInfo launcherAppWidgetInfo) {
        CellLayout cellLayout;
        ArrayList arrayList = new ArrayList();
        AppWidgetProviderInfo appWidgetInfo = ((LauncherAppWidgetHostView) view).getAppWidgetInfo();
        if (appWidgetInfo == null) {
            return arrayList;
        }
        if (view.getParent() instanceof DragView) {
            cellLayout = (CellLayout) ((DragView) view.getParent()).getContentViewParent().getParent();
        } else {
            cellLayout = (CellLayout) view.getParent().getParent();
        }
        if ((appWidgetInfo.resizeMode & 1) != 0) {
            if (cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX + launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY) || cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX - 1, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY)) {
                arrayList.add(new OptionsPopupView.OptionItem(this.mContext, R.string.action_increase_width, R.drawable.ic_widget_width_increase, StatsLogManager.LauncherEvent.IGNORE, new View.OnLongClickListener(view, launcherAppWidgetInfo) {
                    public final /* synthetic */ View f$1;
                    public final /* synthetic */ LauncherAppWidgetInfo f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final boolean onLongClick(View view) {
                        return LauncherAccessibilityDelegate.this.lambda$getSupportedResizeActions$0$LauncherAccessibilityDelegate(this.f$1, this.f$2, view);
                    }
                }));
            }
            if (launcherAppWidgetInfo.spanX > launcherAppWidgetInfo.minSpanX && launcherAppWidgetInfo.spanX > 1) {
                arrayList.add(new OptionsPopupView.OptionItem(this.mContext, R.string.action_decrease_width, R.drawable.ic_widget_width_decrease, StatsLogManager.LauncherEvent.IGNORE, new View.OnLongClickListener(view, launcherAppWidgetInfo) {
                    public final /* synthetic */ View f$1;
                    public final /* synthetic */ LauncherAppWidgetInfo f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final boolean onLongClick(View view) {
                        return LauncherAccessibilityDelegate.this.lambda$getSupportedResizeActions$1$LauncherAccessibilityDelegate(this.f$1, this.f$2, view);
                    }
                }));
            }
        }
        if ((appWidgetInfo.resizeMode & 2) != 0) {
            if (cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY + launcherAppWidgetInfo.spanY, launcherAppWidgetInfo.spanX, 1) || cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY - 1, launcherAppWidgetInfo.spanX, 1)) {
                arrayList.add(new OptionsPopupView.OptionItem(this.mContext, R.string.action_increase_height, R.drawable.ic_widget_height_increase, StatsLogManager.LauncherEvent.IGNORE, new View.OnLongClickListener(view, launcherAppWidgetInfo) {
                    public final /* synthetic */ View f$1;
                    public final /* synthetic */ LauncherAppWidgetInfo f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final boolean onLongClick(View view) {
                        return LauncherAccessibilityDelegate.this.lambda$getSupportedResizeActions$2$LauncherAccessibilityDelegate(this.f$1, this.f$2, view);
                    }
                }));
            }
            if (launcherAppWidgetInfo.spanY > launcherAppWidgetInfo.minSpanY && launcherAppWidgetInfo.spanY > 1) {
                arrayList.add(new OptionsPopupView.OptionItem(this.mContext, R.string.action_decrease_height, R.drawable.ic_widget_height_decrease, StatsLogManager.LauncherEvent.IGNORE, new View.OnLongClickListener(view, launcherAppWidgetInfo) {
                    public final /* synthetic */ View f$1;
                    public final /* synthetic */ LauncherAppWidgetInfo f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final boolean onLongClick(View view) {
                        return LauncherAccessibilityDelegate.this.lambda$getSupportedResizeActions$3$LauncherAccessibilityDelegate(this.f$1, this.f$2, view);
                    }
                }));
            }
        }
        return arrayList;
    }

    public /* synthetic */ boolean lambda$getSupportedResizeActions$0$LauncherAccessibilityDelegate(View view, LauncherAppWidgetInfo launcherAppWidgetInfo, View view2) {
        return performResizeAction(R.string.action_increase_width, view, launcherAppWidgetInfo);
    }

    public /* synthetic */ boolean lambda$getSupportedResizeActions$1$LauncherAccessibilityDelegate(View view, LauncherAppWidgetInfo launcherAppWidgetInfo, View view2) {
        return performResizeAction(R.string.action_decrease_width, view, launcherAppWidgetInfo);
    }

    public /* synthetic */ boolean lambda$getSupportedResizeActions$2$LauncherAccessibilityDelegate(View view, LauncherAppWidgetInfo launcherAppWidgetInfo, View view2) {
        return performResizeAction(R.string.action_increase_height, view, launcherAppWidgetInfo);
    }

    public /* synthetic */ boolean lambda$getSupportedResizeActions$3$LauncherAccessibilityDelegate(View view, LauncherAppWidgetInfo launcherAppWidgetInfo, View view2) {
        return performResizeAction(R.string.action_decrease_height, view, launcherAppWidgetInfo);
    }

    private boolean performResizeAction(int i, View view, LauncherAppWidgetInfo launcherAppWidgetInfo) {
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
        CellLayout cellLayout = (CellLayout) view.getParent().getParent();
        cellLayout.markCellsAsUnoccupiedForView(view);
        if (i == R.string.action_increase_width) {
            if ((view.getLayoutDirection() == 1 && cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX - 1, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY)) || !cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX + launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY)) {
                layoutParams.cellX--;
                launcherAppWidgetInfo.cellX--;
            }
            layoutParams.cellHSpan++;
            launcherAppWidgetInfo.spanX++;
        } else if (i == R.string.action_decrease_width) {
            layoutParams.cellHSpan--;
            launcherAppWidgetInfo.spanX--;
        } else if (i == R.string.action_increase_height) {
            if (!cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY + launcherAppWidgetInfo.spanY, launcherAppWidgetInfo.spanX, 1)) {
                layoutParams.cellY--;
                launcherAppWidgetInfo.cellY--;
            }
            layoutParams.cellVSpan++;
            launcherAppWidgetInfo.spanY++;
        } else if (i == R.string.action_decrease_height) {
            layoutParams.cellVSpan--;
            launcherAppWidgetInfo.spanY--;
        }
        cellLayout.markCellsAsOccupiedForView(view);
        WidgetSizes.updateWidgetSizeRanges((LauncherAppWidgetHostView) view, this.mContext, launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.spanY);
        view.requestLayout();
        ((Launcher) this.mContext).getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
        announceConfirmation(((Launcher) this.mContext).getString(R.string.widget_resized, new Object[]{Integer.valueOf(launcherAppWidgetInfo.spanX), Integer.valueOf(launcherAppWidgetInfo.spanY)}));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void announceConfirmation(int i) {
        announceConfirmation(((Launcher) this.mContext).getResources().getString(i));
    }

    /* access modifiers changed from: protected */
    public boolean beginAccessibleDrag(View view, ItemInfo itemInfo, boolean z) {
        if (!itemSupportsAccessibleDrag(itemInfo)) {
            return false;
        }
        this.mDragInfo = new BaseAccessibilityDelegate.DragInfo();
        this.mDragInfo.info = itemInfo;
        this.mDragInfo.item = view;
        this.mDragInfo.dragType = BaseAccessibilityDelegate.DragType.ICON;
        if (itemInfo instanceof FolderInfo) {
            this.mDragInfo.dragType = BaseAccessibilityDelegate.DragType.FOLDER;
        } else if (itemInfo instanceof LauncherAppWidgetInfo) {
            this.mDragInfo.dragType = BaseAccessibilityDelegate.DragType.WIDGET;
        }
        Rect rect = new Rect();
        ((Launcher) this.mContext).getDragLayer().getDescendantRectRelativeToSelf(view, rect);
        ((Launcher) this.mContext).getDragController().addDragListener(this);
        DragOptions dragOptions = new DragOptions();
        dragOptions.isAccessibleDrag = true;
        dragOptions.isKeyboardDrag = z;
        dragOptions.simulatedDndStartPoint = new Point(rect.centerX(), rect.centerY());
        if (z) {
            ((KeyboardDragAndDropView) ((Launcher) this.mContext).getLayoutInflater().inflate(R.layout.keyboard_drag_and_drop, ((Launcher) this.mContext).getDragLayer(), false)).showForIcon(view, itemInfo, dragOptions);
        } else {
            ItemLongClickListener.beginDrag(view, (Launcher) this.mContext, itemInfo, dragOptions);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public int findSpaceOnWorkspace(ItemInfo itemInfo, int[] iArr) {
        Workspace<?> workspace = ((Launcher) this.mContext).getWorkspace();
        IntArray screenOrder = workspace.getScreenOrder();
        int currentPage = workspace.getCurrentPage();
        int i = screenOrder.get(currentPage);
        boolean findCellForSpan = ((CellLayout) workspace.getPageAt(currentPage)).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY);
        int i2 = 0;
        while (!findCellForSpan && i2 < screenOrder.size()) {
            i = screenOrder.get(i2);
            findCellForSpan = ((CellLayout) workspace.getPageAt(i2)).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY);
            i2++;
        }
        if (findCellForSpan) {
            return i;
        }
        workspace.addExtraEmptyScreens();
        IntSet commitExtraEmptyScreens = workspace.commitExtraEmptyScreens();
        if (commitExtraEmptyScreens.isEmpty()) {
            return -1;
        }
        int i3 = commitExtraEmptyScreens.getArray().get(0);
        if (!workspace.getScreenWithId(i3).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY)) {
            Log.wtf(TAG, "Not enough space on an empty screen");
        }
        return i3;
    }

    public boolean addToWorkspace(ItemInfo itemInfo, boolean z) {
        int[] iArr = new int[2];
        int findSpaceOnWorkspace = findSpaceOnWorkspace(itemInfo, iArr);
        if (findSpaceOnWorkspace == -1) {
            return false;
        }
        ((Launcher) this.mContext).getStateManager().goToState(LauncherState.NORMAL, true, AnimatorListeners.forSuccessCallback(new Runnable(itemInfo, findSpaceOnWorkspace, iArr, z) {
            public final /* synthetic */ ItemInfo f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int[] f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                LauncherAccessibilityDelegate.this.lambda$addToWorkspace$4$LauncherAccessibilityDelegate(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        }));
        return true;
    }

    public /* synthetic */ void lambda$addToWorkspace$4$LauncherAccessibilityDelegate(ItemInfo itemInfo, int i, int[] iArr, boolean z) {
        if (itemInfo instanceof WorkspaceItemFactory) {
            WorkspaceItemInfo makeWorkspaceItem = ((WorkspaceItemFactory) itemInfo).makeWorkspaceItem(this.mContext);
            ((Launcher) this.mContext).getModelWriter().addItemToDatabase(makeWorkspaceItem, -100, i, iArr[0], iArr[1]);
            ((Launcher) this.mContext).bindItems(Collections.singletonList(makeWorkspaceItem), true, z);
            announceConfirmation(R.string.item_added_to_workspace);
        } else if (itemInfo instanceof PendingAddItemInfo) {
            PendingAddItemInfo pendingAddItemInfo = (PendingAddItemInfo) itemInfo;
            Workspace<?> workspace = ((Launcher) this.mContext).getWorkspace();
            workspace.snapToPage(workspace.getPageIndexForScreenId(i));
            ((Launcher) this.mContext).addPendingItem(pendingAddItemInfo, -100, i, iArr, pendingAddItemInfo.spanX, pendingAddItemInfo.spanY);
        } else if (itemInfo instanceof WorkspaceItemInfo) {
            WorkspaceItemInfo clone = ((WorkspaceItemInfo) itemInfo).clone();
            ((Launcher) this.mContext).getModelWriter().addItemToDatabase(clone, -100, i, iArr[0], iArr[1]);
            ((Launcher) this.mContext).bindItems(Collections.singletonList(clone), true, z);
        }
    }

    public boolean moveToWorkspace(ItemInfo itemInfo) {
        Folder open = Folder.getOpen((ActivityContext) this.mContext);
        open.close(true);
        WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) itemInfo;
        open.getInfo().remove(workspaceItemInfo, false);
        int[] iArr = new int[2];
        int findSpaceOnWorkspace = findSpaceOnWorkspace(itemInfo, iArr);
        if (findSpaceOnWorkspace == -1) {
            return false;
        }
        ((Launcher) this.mContext).getModelWriter().moveItemInDatabase(workspaceItemInfo, -100, findSpaceOnWorkspace, iArr[0], iArr[1]);
        new Handler().post(new Runnable(itemInfo) {
            public final /* synthetic */ ItemInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LauncherAccessibilityDelegate.this.lambda$moveToWorkspace$5$LauncherAccessibilityDelegate(this.f$1);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$moveToWorkspace$5$LauncherAccessibilityDelegate(ItemInfo itemInfo) {
        ((Launcher) this.mContext).bindItems(Collections.singletonList(itemInfo), true);
        announceConfirmation(R.string.item_moved);
    }
}
