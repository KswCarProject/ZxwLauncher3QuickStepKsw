package com.android.launcher3.shortcuts;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BubbleTextHolder;

public class DeepShortcutView extends FrameLayout implements BubbleTextHolder {
    private static final Point sTempPoint = new Point();
    private BubbleTextView mBubbleText;
    private ShortcutInfo mDetail;
    private View mIconView;
    private WorkspaceItemInfo mInfo;
    private final Drawable mTransparentDrawable;

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

    public DeepShortcutView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public DeepShortcutView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DeepShortcutView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTransparentDrawable = new ColorDrawable(0);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        BubbleTextView bubbleTextView = (BubbleTextView) findViewById(R.id.bubble_text);
        this.mBubbleText = bubbleTextView;
        bubbleTextView.setHideBadge(true);
        this.mIconView = findViewById(R.id.icon);
        tryUpdateTextBackground();
    }

    public void setBackground(Drawable drawable) {
        super.setBackground(drawable);
        tryUpdateTextBackground();
    }

    public void setBackgroundResource(int i) {
        super.setBackgroundResource(i);
        tryUpdateTextBackground();
    }

    private void tryUpdateTextBackground() {
        if ((getBackground() instanceof GradientDrawable) && this.mBubbleText != null) {
            GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
            int attrColor = Themes.getAttrColor(getContext(), 16843820);
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setColor(attrColor);
            gradientDrawable2.setShape(0);
            if (gradientDrawable.getCornerRadii() != null) {
                gradientDrawable2.setCornerRadii(gradientDrawable.getCornerRadii());
            } else {
                gradientDrawable2.setCornerRadius(gradientDrawable.getCornerRadius());
            }
            this.mBubbleText.setBackground(new RippleDrawable(ColorStateList.valueOf(attrColor), this.mTransparentDrawable, gradientDrawable2));
        }
    }

    public BubbleTextView getBubbleText() {
        return this.mBubbleText;
    }

    public void setWillDrawIcon(boolean z) {
        this.mIconView.setVisibility(z ? 0 : 4);
    }

    public boolean willDrawIcon() {
        return this.mIconView.getVisibility() == 0;
    }

    public Point getIconCenter() {
        Point point = sTempPoint;
        int measuredHeight = getMeasuredHeight() / 2;
        point.x = measuredHeight;
        point.y = measuredHeight;
        if (Utilities.isRtl(getResources())) {
            point.x = getMeasuredWidth() - point.x;
        }
        return point;
    }

    public void applyShortcutInfo(WorkspaceItemInfo workspaceItemInfo, ShortcutInfo shortcutInfo, PopupContainerWithArrow popupContainerWithArrow) {
        this.mInfo = workspaceItemInfo;
        this.mDetail = shortcutInfo;
        this.mBubbleText.applyFromWorkspaceItem(workspaceItemInfo);
        this.mIconView.setBackground(this.mBubbleText.getIcon());
        CharSequence longLabel = this.mDetail.getLongLabel();
        boolean z = !TextUtils.isEmpty(longLabel) && this.mBubbleText.getPaint().measureText(longLabel.toString()) <= ((float) ((this.mBubbleText.getWidth() - this.mBubbleText.getTotalPaddingLeft()) - this.mBubbleText.getTotalPaddingRight()));
        BubbleTextView bubbleTextView = this.mBubbleText;
        if (!z) {
            longLabel = this.mDetail.getShortLabel();
        }
        bubbleTextView.setText(longLabel);
        this.mBubbleText.setOnClickListener(popupContainerWithArrow.getItemClickListener());
        this.mBubbleText.setOnLongClickListener(popupContainerWithArrow.getItemDragHandler());
        this.mBubbleText.setOnTouchListener(popupContainerWithArrow.getItemDragHandler());
    }

    public WorkspaceItemInfo getFinalInfo() {
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo(this.mInfo);
        Launcher.getLauncher(getContext()).getModel().updateAndBindWorkspaceItem(workspaceItemInfo, this.mDetail);
        return workspaceItemInfo;
    }

    public View getIconView() {
        return this.mIconView;
    }

    public ShortcutInfo getDetail() {
        return this.mDetail;
    }
}
