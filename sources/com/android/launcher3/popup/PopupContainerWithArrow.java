package com.android.launcher3.popup;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.accessibility.ShortcutMenuAccessibilityDelegate;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.notification.NotificationContainer;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutDragPreviewProvider;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.ShortcutUtil;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PopupContainerWithArrow<T extends Context & ActivityContext> extends ArrowPopup<T> implements DragSource, DragController.DragListener {
    protected LauncherAccessibilityDelegate mAccessibilityDelegate;
    private int mContainerWidth;
    private ViewGroup mDeepShortcutContainer;
    private final PointF mInterceptTouchDown;
    private NotificationContainer mNotificationContainer;
    private int mNumNotifications;
    /* access modifiers changed from: private */
    public BubbleTextView mOriginalIcon;
    protected PopupItemDragHandler mPopupItemDragHandler;
    private final List<DeepShortcutView> mShortcuts;
    /* access modifiers changed from: private */
    public final int mStartDragThreshold;
    private ViewGroup mSystemShortcutContainer;
    private ViewGroup mWidgetContainer;

    public interface PopupItemDragHandler extends View.OnLongClickListener, View.OnTouchListener {
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 2) != 0;
    }

    public void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
    }

    public PopupContainerWithArrow(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShortcuts = new ArrayList();
        this.mInterceptTouchDown = new PointF();
        this.mStartDragThreshold = getResources().getDimensionPixelSize(R.dimen.deep_shortcuts_start_drag_threshold);
        this.mContainerWidth = getResources().getDimensionPixelSize(R.dimen.bg_popup_item_width);
    }

    public PopupContainerWithArrow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PopupContainerWithArrow(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mInterceptTouchDown.set(motionEvent.getX(), motionEvent.getY());
        }
        NotificationContainer notificationContainer = this.mNotificationContainer;
        if ((notificationContainer == null || !notificationContainer.onInterceptSwipeEvent(motionEvent)) && Utilities.squaredHypot(this.mInterceptTouchDown.x - motionEvent.getX(), this.mInterceptTouchDown.y - motionEvent.getY()) <= Utilities.squaredTouchSlop(getContext())) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        NotificationContainer notificationContainer = this.mNotificationContainer;
        if (notificationContainer != null) {
            return notificationContainer.onSwipeEvent(motionEvent) || super.onTouchEvent(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }

    public View.OnClickListener getItemClickListener() {
        return new View.OnClickListener() {
            public final void onClick(View view) {
                PopupContainerWithArrow.this.lambda$getItemClickListener$0$PopupContainerWithArrow(view);
            }
        };
    }

    public /* synthetic */ void lambda$getItemClickListener$0$PopupContainerWithArrow(View view) {
        ((ActivityContext) this.mActivityContext).getItemOnClickListener().onClick(view);
    }

    public void setPopupItemDragHandler(PopupItemDragHandler popupItemDragHandler) {
        this.mPopupItemDragHandler = popupItemDragHandler;
    }

    public PopupItemDragHandler getItemDragHandler() {
        return this.mPopupItemDragHandler;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0) {
            return false;
        }
        BaseDragLayer popupContainer = getPopupContainer();
        if (popupContainer.isEventOverView(this, motionEvent)) {
            return false;
        }
        close(true);
        BubbleTextView bubbleTextView = this.mOriginalIcon;
        if (bubbleTextView == null || !popupContainer.isEventOverView(bubbleTextView, motionEvent)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void setChildColor(View view, int i, AnimatorSet animatorSet) {
        NotificationContainer notificationContainer;
        super.setChildColor(view, i, animatorSet);
        if (view.getId() == R.id.notification_container && (notificationContainer = this.mNotificationContainer) != null) {
            notificationContainer.updateBackgroundColor(i, animatorSet);
        }
    }

    @Deprecated
    public static boolean canShow(View view, ItemInfo itemInfo) {
        return (view instanceof BubbleTextView) && ShortcutUtil.supportsShortcuts(itemInfo);
    }

    public static PopupContainerWithArrow<Launcher> showForIcon(BubbleTextView bubbleTextView) {
        Launcher launcher = Launcher.getLauncher(bubbleTextView.getContext());
        if (getOpen(launcher) != null) {
            bubbleTextView.clearFocus();
            return null;
        }
        ItemInfo itemInfo = (ItemInfo) bubbleTextView.getTag();
        if (!ShortcutUtil.supportsShortcuts(itemInfo)) {
            return null;
        }
        PopupContainerWithArrow<Launcher> popupContainerWithArrow = (PopupContainerWithArrow) launcher.getLayoutInflater().inflate(R.layout.popup_container, launcher.getDragLayer(), false);
        popupContainerWithArrow.configureForLauncher(launcher);
        PopupDataProvider popupDataProvider = launcher.getPopupDataProvider();
        popupContainerWithArrow.populateAndShow(bubbleTextView, popupDataProvider.getShortcutCountForItem(itemInfo), popupDataProvider.getNotificationKeysForItem(itemInfo), (List) launcher.getSupportedShortcuts().map(new Function(itemInfo, bubbleTextView) {
            public final /* synthetic */ ItemInfo f$1;
            public final /* synthetic */ BubbleTextView f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object apply(Object obj) {
                return ((SystemShortcut.Factory) obj).getShortcut(Launcher.this, this.f$1, this.f$2);
            }
        }).filter($$Lambda$PopupContainerWithArrow$5DTpxP45pqMGaPreGfSqTyTDkt0.INSTANCE).collect(Collectors.toList()));
        launcher.refreshAndBindWidgetsForPackageUser(PackageUserKey.fromItemInfo(itemInfo));
        popupContainerWithArrow.requestFocus();
        return popupContainerWithArrow;
    }

    private void configureForLauncher(Launcher launcher) {
        addOnAttachStateChangeListener(new LauncherPopupLiveUpdateHandler(launcher, this));
        this.mPopupItemDragHandler = new LauncherPopupItemDragHandler(launcher, this);
        this.mAccessibilityDelegate = new ShortcutMenuAccessibilityDelegate(launcher);
        launcher.getDragController().addDragListener(this);
        addPreDrawForColorExtraction(launcher);
    }

    /* access modifiers changed from: protected */
    public List<View> getChildrenForColorExtraction() {
        return Arrays.asList(new View[]{this.mSystemShortcutContainer, this.mWidgetContainer, this.mDeepShortcutContainer, this.mNotificationContainer});
    }

    public void populateAndShow(BubbleTextView bubbleTextView, int i, List<NotificationKeyData> list, List<SystemShortcut> list2) {
        this.mNumNotifications = list.size();
        this.mOriginalIcon = bubbleTextView;
        boolean z = i > 0;
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.bg_popup_item_width);
        this.mContainerWidth = dimensionPixelSize;
        if (z) {
            this.mContainerWidth = Math.max(dimensionPixelSize, list2.size() * getResources().getDimensionPixelSize(R.dimen.system_shortcut_header_icon_touch_size));
        }
        if (this.mNumNotifications > 0) {
            NotificationContainer notificationContainer = this.mNotificationContainer;
            if (notificationContainer == null) {
                NotificationContainer notificationContainer2 = (NotificationContainer) findViewById(R.id.notification_container);
                this.mNotificationContainer = notificationContainer2;
                notificationContainer2.setVisibility(0);
                this.mNotificationContainer.setPopupView(this);
            } else {
                notificationContainer.setVisibility(8);
            }
            updateNotificationHeader();
        }
        int childCount = getChildCount();
        this.mSystemShortcutContainer = this;
        if (this.mDeepShortcutContainer == null) {
            this.mDeepShortcutContainer = (ViewGroup) findViewById(R.id.deep_shortcuts_container);
        }
        if (z) {
            this.mDeepShortcutContainer.setVisibility(0);
            while (i > 0) {
                DeepShortcutView deepShortcutView = (DeepShortcutView) inflateAndAdd(R.layout.deep_shortcut, this.mDeepShortcutContainer);
                deepShortcutView.getLayoutParams().width = this.mContainerWidth;
                this.mShortcuts.add(deepShortcutView);
                i--;
            }
            updateHiddenShortcuts();
            if (!list2.isEmpty()) {
                for (SystemShortcut next : list2) {
                    if (next instanceof SystemShortcut.Widgets) {
                        if (this.mWidgetContainer == null) {
                            this.mWidgetContainer = (ViewGroup) inflateAndAdd(R.layout.widget_shortcut_container, this);
                        }
                        initializeWidgetShortcut(this.mWidgetContainer, next);
                    }
                }
                this.mSystemShortcutContainer = (ViewGroup) inflateAndAdd(R.layout.system_shortcut_icons, this);
                for (SystemShortcut next2 : list2) {
                    if (!(next2 instanceof SystemShortcut.Widgets)) {
                        initializeSystemShortcut(R.layout.system_shortcut_icon_only, this.mSystemShortcutContainer, next2);
                    }
                }
            }
        } else {
            this.mDeepShortcutContainer.setVisibility(8);
            if (!list2.isEmpty()) {
                for (SystemShortcut initializeSystemShortcut : list2) {
                    initializeSystemShortcut(R.layout.system_shortcut, this, initializeSystemShortcut);
                }
            }
        }
        reorderAndShow(childCount);
        ItemInfo itemInfo = (ItemInfo) bubbleTextView.getTag();
        if (Build.VERSION.SDK_INT >= 28) {
            setAccessibilityPaneTitle(getTitleForAccessibility());
        }
        this.mOriginalIcon.setForceHideDot(true);
        setLayoutTransition(new LayoutTransition());
        Executors.MODEL_EXECUTOR.getHandler().postAtFrontOfQueue(PopupPopulator.createUpdateRunnable(this.mActivityContext, itemInfo, new Handler(Looper.getMainLooper()), this, this.mShortcuts, list));
    }

    /* access modifiers changed from: protected */
    public NotificationContainer getNotificationContainer() {
        return this.mNotificationContainer;
    }

    /* access modifiers changed from: protected */
    public BubbleTextView getOriginalIcon() {
        return this.mOriginalIcon;
    }

    /* access modifiers changed from: protected */
    public ViewGroup getSystemShortcutContainer() {
        return this.mSystemShortcutContainer;
    }

    /* access modifiers changed from: protected */
    public ViewGroup getWidgetContainer() {
        return this.mWidgetContainer;
    }

    /* access modifiers changed from: protected */
    public void setWidgetContainer(ViewGroup viewGroup) {
        this.mWidgetContainer = viewGroup;
    }

    private String getTitleForAccessibility() {
        return getContext().getString(this.mNumNotifications == 0 ? R.string.action_deep_shortcut : R.string.shortcuts_menu_with_notifications_description);
    }

    /* access modifiers changed from: protected */
    public void getTargetObjectLocation(Rect rect) {
        int i;
        getPopupContainer().getDescendantRectRelativeToSelf(this.mOriginalIcon, rect);
        rect.top += this.mOriginalIcon.getPaddingTop();
        rect.left += this.mOriginalIcon.getPaddingLeft();
        rect.right -= this.mOriginalIcon.getPaddingRight();
        int i2 = rect.top;
        if (this.mOriginalIcon.getIcon() != null) {
            i = this.mOriginalIcon.getIcon().getBounds().height();
        } else {
            i = this.mOriginalIcon.getHeight();
        }
        rect.bottom = i2 + i;
    }

    public void applyNotificationInfos(List<NotificationInfo> list) {
        NotificationContainer notificationContainer = this.mNotificationContainer;
        if (notificationContainer != null) {
            notificationContainer.applyNotificationInfos(list);
        }
    }

    /* access modifiers changed from: protected */
    public void updateHiddenShortcuts() {
        int i = this.mNotificationContainer != null ? 2 : 4;
        int size = this.mShortcuts.size();
        int i2 = 0;
        while (i2 < size) {
            this.mShortcuts.get(i2).setVisibility(i2 >= i ? 8 : 0);
            i2++;
        }
    }

    /* access modifiers changed from: protected */
    public void initializeWidgetShortcut(ViewGroup viewGroup, SystemShortcut systemShortcut) {
        initializeSystemShortcut(R.layout.system_shortcut, viewGroup, systemShortcut).getLayoutParams().width = this.mContainerWidth;
    }

    /* access modifiers changed from: protected */
    public View initializeSystemShortcut(int i, ViewGroup viewGroup, SystemShortcut systemShortcut) {
        View inflateAndAdd = inflateAndAdd(i, viewGroup, getInsertIndexForSystemShortcut(viewGroup, systemShortcut));
        if (inflateAndAdd instanceof DeepShortcutView) {
            DeepShortcutView deepShortcutView = (DeepShortcutView) inflateAndAdd;
            systemShortcut.setIconAndLabelFor(deepShortcutView.getIconView(), deepShortcutView.getBubbleText());
        } else if (inflateAndAdd instanceof ImageView) {
            systemShortcut.setIconAndContentDescriptionFor((ImageView) inflateAndAdd);
            inflateAndAdd.setTooltipText(inflateAndAdd.getContentDescription());
        }
        inflateAndAdd.setTag(systemShortcut);
        inflateAndAdd.setOnClickListener(systemShortcut);
        return inflateAndAdd;
    }

    private int getInsertIndexForSystemShortcut(ViewGroup viewGroup, SystemShortcut systemShortcut) {
        View findViewById = viewGroup.findViewById(R.id.separator);
        if (findViewById == null || !systemShortcut.isLeftGroup()) {
            return viewGroup.getChildCount();
        }
        return viewGroup.indexOfChild(findViewById);
    }

    public DragOptions.PreDragCondition createPreDragCondition(final boolean z) {
        return new DragOptions.PreDragCondition() {
            public boolean shouldStartDrag(double d) {
                return d > ((double) PopupContainerWithArrow.this.mStartDragThreshold);
            }

            public void onPreDragStart(DropTarget.DragObject dragObject) {
                if (z) {
                    if (PopupContainerWithArrow.this.mIsAboveIcon) {
                        PopupContainerWithArrow.this.mOriginalIcon.setIconVisible(false);
                        PopupContainerWithArrow.this.mOriginalIcon.setVisibility(0);
                        return;
                    }
                    PopupContainerWithArrow.this.mOriginalIcon.setVisibility(4);
                }
            }

            public void onPreDragEnd(DropTarget.DragObject dragObject, boolean z) {
                if (z) {
                    PopupContainerWithArrow.this.mOriginalIcon.setIconVisible(true);
                    if (z) {
                        PopupContainerWithArrow.this.mOriginalIcon.setVisibility(4);
                    } else if (!PopupContainerWithArrow.this.mIsAboveIcon) {
                        PopupContainerWithArrow.this.mOriginalIcon.setVisibility(0);
                        PopupContainerWithArrow.this.mOriginalIcon.setTextVisibility(false);
                    }
                }
            }
        };
    }

    /* access modifiers changed from: protected */
    public void updateNotificationHeader() {
        DotInfo dotInfoForItem = ((ActivityContext) this.mActivityContext).getDotInfoForItem((ItemInfoWithIcon) this.mOriginalIcon.getTag());
        NotificationContainer notificationContainer = this.mNotificationContainer;
        if (notificationContainer != null && dotInfoForItem != null) {
            notificationContainer.updateHeader(dotInfoForItem.getNotificationCount());
        }
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        this.mDeferContainerRemoval = true;
        animateClose();
    }

    public void onDragEnd() {
        if (this.mIsOpen) {
            return;
        }
        if (this.mOpenCloseAnimator != null) {
            this.mDeferContainerRemoval = false;
        } else if (this.mDeferContainerRemoval) {
            closeComplete();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreateCloseAnimation(AnimatorSet animatorSet) {
        animatorSet.play(this.mOriginalIcon.createTextAlphaAnimator(true));
        this.mOriginalIcon.setForceHideDot(false);
    }

    /* access modifiers changed from: protected */
    public void closeComplete() {
        super.closeComplete();
        if (((ActivityContext) this.mActivityContext).getDragController() != null) {
            ((ActivityContext) this.mActivityContext).getDragController().removeDragListener(this);
        }
        PopupContainerWithArrow open = getOpen(this.mActivityContext);
        if (open == null || open.mOriginalIcon != this.mOriginalIcon) {
            BubbleTextView bubbleTextView = this.mOriginalIcon;
            bubbleTextView.setTextVisibility(bubbleTextView.shouldTextBeVisible());
            this.mOriginalIcon.setForceHideDot(false);
        }
    }

    public static <T extends Context & ActivityContext> PopupContainerWithArrow getOpen(T t) {
        return (PopupContainerWithArrow) getOpenView((ActivityContext) t, 2);
    }

    public static void dismissInvalidPopup(BaseDraggingActivity baseDraggingActivity) {
        PopupContainerWithArrow open = getOpen(baseDraggingActivity);
        if (open == null) {
            return;
        }
        if (!open.mOriginalIcon.isAttachedToWindow() || !ShortcutUtil.supportsShortcuts((ItemInfo) open.mOriginalIcon.getTag())) {
            open.animateClose();
        }
    }

    public static class LauncherPopupItemDragHandler implements PopupItemDragHandler {
        private final PopupContainerWithArrow mContainer;
        protected final Point mIconLastTouchPos = new Point();
        private final Launcher mLauncher;

        LauncherPopupItemDragHandler(Launcher launcher, PopupContainerWithArrow popupContainerWithArrow) {
            this.mLauncher = launcher;
            this.mContainer = popupContainerWithArrow;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action != 0 && action != 2) {
                return false;
            }
            this.mIconLastTouchPos.set((int) motionEvent.getX(), (int) motionEvent.getY());
            return false;
        }

        public boolean onLongClick(View view) {
            if (!ItemLongClickListener.canStartDrag(this.mLauncher) || !(view.getParent() instanceof DeepShortcutView)) {
                return false;
            }
            DeepShortcutView deepShortcutView = (DeepShortcutView) view.getParent();
            deepShortcutView.setWillDrawIcon(false);
            Point point = new Point();
            point.x = this.mIconLastTouchPos.x - deepShortcutView.getIconCenter().x;
            point.y = this.mIconLastTouchPos.y - this.mLauncher.getDeviceProfile().iconSizePx;
            DraggableView ofType = DraggableView.ofType(0);
            WorkspaceItemInfo finalInfo = deepShortcutView.getFinalInfo();
            finalInfo.container = LauncherSettings.Favorites.CONTAINER_SHORTCUTS;
            this.mLauncher.getWorkspace().beginDragShared(deepShortcutView.getIconView(), ofType, this.mContainer, finalInfo, new ShortcutDragPreviewProvider(deepShortcutView.getIconView(), point), new DragOptions()).animateShift(-point.x, -point.y);
            AbstractFloatingView.closeOpenContainer(this.mLauncher, 1);
            return false;
        }
    }
}
