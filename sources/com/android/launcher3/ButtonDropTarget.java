package com.android.launcher3;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.android.launcher3.DropTarget;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.model.data.ItemInfo;

public abstract class ButtonDropTarget extends TextView implements DropTarget, DragController.DragListener, View.OnClickListener {
    private static final int DRAG_VIEW_DROP_DURATION = 285;
    private static final float DRAG_VIEW_HOVER_OVER_OPACITY = 0.65f;
    private static final int MAX_LINES_TEXT_MULTI_LINE = 2;
    private static final int MAX_LINES_TEXT_SINGLE_LINE = 1;
    public static final int TOOLTIP_DEFAULT = 0;
    public static final int TOOLTIP_LEFT = 1;
    public static final int TOOLTIP_RIGHT = 2;
    private static final int[] sTempCords = new int[2];
    private boolean mAccessibleDrag;
    protected boolean mActive;
    private final int mDragDistanceThreshold;
    protected Drawable mDrawable;
    private final int mDrawablePadding;
    private final int mDrawableSize;
    protected DropTargetBar mDropTargetBar;
    private boolean mIconVisible;
    protected final Launcher mLauncher;
    protected CharSequence mText;
    private boolean mTextMultiLine;
    private boolean mTextVisible;
    private PopupWindow mToolTip;
    private int mToolTipLocation;

    public abstract void completeDrop(DropTarget.DragObject dragObject);

    public abstract int getAccessibilityAction();

    public abstract void onAccessibilityDrop(View view, ItemInfo itemInfo);

    public void onDragOver(DropTarget.DragObject dragObject) {
    }

    public void prepareAccessibilityDrop() {
    }

    /* access modifiers changed from: protected */
    public abstract void setupItemInfo(ItemInfo itemInfo);

    public abstract boolean supportsAccessibilityDrop(ItemInfo itemInfo, View view);

    /* access modifiers changed from: protected */
    public abstract boolean supportsDrop(ItemInfo itemInfo);

    public ButtonDropTarget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ButtonDropTarget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextVisible = true;
        this.mIconVisible = true;
        this.mTextMultiLine = true;
        this.mLauncher = Launcher.getLauncher(context);
        Resources resources = getResources();
        this.mDragDistanceThreshold = resources.getDimensionPixelSize(R.dimen.drag_distanceThreshold);
        this.mDrawableSize = resources.getDimensionPixelSize(R.dimen.drop_target_text_size);
        this.mDrawablePadding = resources.getDimensionPixelSize(R.dimen.drop_target_button_drawable_padding);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        CharSequence text = getText();
        this.mText = text;
        setContentDescription(text);
    }

    /* access modifiers changed from: protected */
    public void updateText(int i) {
        setText(i);
        CharSequence text = getText();
        this.mText = text;
        setContentDescription(text);
    }

    /* access modifiers changed from: protected */
    public void setDrawable(int i) {
        Drawable mutate = getContext().getDrawable(i).mutate();
        this.mDrawable = mutate;
        mutate.setTintList(getTextColors());
        updateIconVisibility();
    }

    public void setDropTargetBar(DropTargetBar dropTargetBar) {
        this.mDropTargetBar = dropTargetBar;
    }

    private void hideTooltip() {
        PopupWindow popupWindow = this.mToolTip;
        if (popupWindow != null) {
            popupWindow.dismiss();
            this.mToolTip = null;
        }
    }

    public final void onDragEnter(DropTarget.DragObject dragObject) {
        int i;
        if (!this.mAccessibleDrag && !this.mTextVisible) {
            hideTooltip();
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.drop_target_tool_tip, (ViewGroup) null);
            textView.setText(this.mText);
            this.mToolTip = new PopupWindow(textView, -2, -2);
            int i2 = 0;
            if (this.mToolTipLocation != 0) {
                i = -getMeasuredHeight();
                textView.measure(0, 0);
                if (this.mToolTipLocation == 1) {
                    i2 = (-getMeasuredWidth()) - (textView.getMeasuredWidth() / 2);
                } else {
                    i2 = (getMeasuredWidth() / 2) + (textView.getMeasuredWidth() / 2);
                }
            } else {
                i = 0;
            }
            this.mToolTip.showAsDropDown(this, i2, i);
        }
        dragObject.dragView.setAlpha(DRAG_VIEW_HOVER_OVER_OPACITY);
        setSelected(true);
        if (dragObject.stateAnnouncer != null) {
            dragObject.stateAnnouncer.cancel();
        }
        sendAccessibilityEvent(4);
    }

    public final void onDragExit(DropTarget.DragObject dragObject) {
        hideTooltip();
        if (!dragObject.dragComplete) {
            dragObject.dragView.setAlpha(1.0f);
            setSelected(false);
            return;
        }
        dragObject.dragView.setAlpha(DRAG_VIEW_HOVER_OVER_OPACITY);
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        int i = 0;
        if (dragOptions.isKeyboardDrag) {
            this.mActive = false;
        } else {
            setupItemInfo(dragObject.dragInfo);
            this.mActive = supportsDrop(dragObject.dragInfo);
        }
        if (!this.mActive) {
            i = 8;
        }
        setVisibility(i);
        boolean z = dragOptions.isAccessibleDrag;
        this.mAccessibleDrag = z;
        setOnClickListener(z ? this : null);
    }

    public final boolean acceptDrop(DropTarget.DragObject dragObject) {
        return supportsDrop(dragObject.dragInfo);
    }

    public boolean isDropEnabled() {
        return this.mActive && (this.mAccessibleDrag || this.mLauncher.getDragController().getDistanceDragged() >= ((float) this.mDragDistanceThreshold));
    }

    public void onDragEnd() {
        this.mActive = false;
        setOnClickListener((View.OnClickListener) null);
        setSelected(false);
    }

    public void onDrop(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        if (!dragOptions.isFlingToDelete) {
            DragLayer dragLayer = this.mLauncher.getDragLayer();
            DragView dragView = dragObject.dragView;
            Rect iconRect = getIconRect(dragObject);
            float width = ((float) iconRect.width()) / ((float) dragView.getMeasuredWidth());
            dragView.detachContentView(true);
            this.mDropTargetBar.deferOnDragEnd();
            dragLayer.animateView(dragObject.dragView, iconRect, width, 0.1f, 0.1f, DRAG_VIEW_DROP_DURATION, Interpolators.DEACCEL_2, new Runnable(dragObject) {
                public final /* synthetic */ DropTarget.DragObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ButtonDropTarget.this.lambda$onDrop$0$ButtonDropTarget(this.f$1);
                }
            }, 0, (View) null);
        }
    }

    public /* synthetic */ void lambda$onDrop$0$ButtonDropTarget(DropTarget.DragObject dragObject) {
        completeDrop(dragObject);
        this.mDropTargetBar.onDragEnd();
        this.mLauncher.getStateManager().goToState(LauncherState.NORMAL);
    }

    public void getHitRectRelativeToDragLayer(Rect rect) {
        super.getHitRect(rect);
        rect.bottom += this.mLauncher.getDeviceProfile().dropTargetDragPaddingPx;
        int[] iArr = sTempCords;
        iArr[1] = 0;
        iArr[0] = 0;
        this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf((View) this, iArr);
        rect.offsetTo(iArr[0], iArr[1]);
    }

    public Rect getIconRect(DropTarget.DragObject dragObject) {
        int i;
        int i2;
        int measuredWidth = dragObject.dragView.getMeasuredWidth();
        int measuredHeight = dragObject.dragView.getMeasuredHeight();
        int intrinsicWidth = this.mDrawable.getIntrinsicWidth();
        int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect rect = new Rect();
        dragLayer.getViewRectRelativeToSelf(this, rect);
        if (Utilities.isRtl(getResources())) {
            i2 = rect.right - getPaddingRight();
            i = i2 - intrinsicWidth;
        } else {
            i = getPaddingLeft() + rect.left;
            i2 = i + intrinsicWidth;
        }
        int measuredHeight2 = rect.top + ((getMeasuredHeight() - intrinsicHeight) / 2);
        rect.set(i, measuredHeight2, i2, measuredHeight2 + intrinsicHeight);
        rect.offset((-(measuredWidth - intrinsicWidth)) / 2, (-(measuredHeight - intrinsicHeight)) / 2);
        return rect;
    }

    private void centerIcon() {
        int i;
        if (this.mTextVisible) {
            i = 0;
        } else {
            i = (((getWidth() - getPaddingLeft()) - getPaddingRight()) / 2) - (this.mDrawableSize / 2);
        }
        Drawable drawable = this.mDrawable;
        int i2 = this.mDrawableSize;
        drawable.setBounds(i, 0, i + i2, i2);
    }

    public void onClick(View view) {
        this.mLauncher.getAccessibilityDelegate().handleAccessibleDrop(this, (Rect) null, (String) null);
    }

    public void setTextVisible(boolean z) {
        String str = z ? this.mText : "";
        if (this.mTextVisible != z || !TextUtils.equals(str, getText())) {
            this.mTextVisible = z;
            setText(str);
            updateIconVisibility();
        }
    }

    public void setTextMultiLine(boolean z) {
        if (this.mTextMultiLine != z) {
            this.mTextMultiLine = z;
            setSingleLine(!z);
            int i = 1;
            setMaxLines(z ? 2 : 1);
            if (z) {
                i = 131073;
            }
            setInputType(i);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isTextMultiLine() {
        return this.mTextMultiLine;
    }

    public void setIconVisible(boolean z) {
        if (this.mIconVisible != z) {
            this.mIconVisible = z;
            updateIconVisibility();
        }
    }

    private void updateIconVisibility() {
        if (this.mIconVisible) {
            centerIcon();
        }
        setCompoundDrawablesRelative(this.mIconVisible ? this.mDrawable : null, (Drawable) null, (Drawable) null, (Drawable) null);
        setCompoundDrawablePadding((!this.mIconVisible || !this.mTextVisible) ? 0 : this.mDrawablePadding);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        centerIcon();
    }

    public void setToolTipLocation(int i) {
        this.mToolTipLocation = i;
        hideTooltip();
    }

    public boolean isTextTruncated(int i) {
        return !this.mText.equals(TextUtils.ellipsize(this.mText, getPaint(), (float) (i - (((getPaddingLeft() + getPaddingRight()) + this.mDrawable.getIntrinsicWidth()) + getCompoundDrawablePadding())), TextUtils.TruncateAt.END));
    }
}
