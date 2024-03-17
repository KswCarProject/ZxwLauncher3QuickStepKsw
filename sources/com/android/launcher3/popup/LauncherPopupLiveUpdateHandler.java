package com.android.launcher3.popup;

import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;

public class LauncherPopupLiveUpdateHandler extends PopupLiveUpdateHandler<Launcher> {
    public LauncherPopupLiveUpdateHandler(Launcher launcher, PopupContainerWithArrow<Launcher> popupContainerWithArrow) {
        super(launcher, popupContainerWithArrow);
    }

    private View getWidgetsView(ViewGroup viewGroup) {
        for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = viewGroup.getChildAt(childCount);
            if (childAt.getTag() instanceof SystemShortcut.Widgets) {
                return childAt;
            }
        }
        return null;
    }

    public void onWidgetsBound() {
        BubbleTextView originalIcon = this.mPopupContainerWithArrow.getOriginalIcon();
        SystemShortcut<Launcher> shortcut = SystemShortcut.WIDGETS.getShortcut((Launcher) this.mContext, (ItemInfo) originalIcon.getTag(), originalIcon);
        View widgetsView = getWidgetsView(this.mPopupContainerWithArrow);
        if (widgetsView == null && this.mPopupContainerWithArrow.getWidgetContainer() != null) {
            widgetsView = getWidgetsView(this.mPopupContainerWithArrow.getWidgetContainer());
        }
        if (shortcut == null || widgetsView != null) {
            if (shortcut == null && widgetsView != null) {
                if (this.mPopupContainerWithArrow.getSystemShortcutContainer() == this.mPopupContainerWithArrow || this.mPopupContainerWithArrow.getWidgetContainer() == null) {
                    this.mPopupContainerWithArrow.close(false);
                    PopupContainerWithArrow.showForIcon(this.mPopupContainerWithArrow.getOriginalIcon());
                    return;
                }
                this.mPopupContainerWithArrow.getWidgetContainer().removeView(widgetsView);
            }
        } else if (this.mPopupContainerWithArrow.getSystemShortcutContainer() != this.mPopupContainerWithArrow) {
            if (this.mPopupContainerWithArrow.getWidgetContainer() == null) {
                this.mPopupContainerWithArrow.setWidgetContainer((ViewGroup) this.mPopupContainerWithArrow.inflateAndAdd(R.layout.widget_shortcut_container, this.mPopupContainerWithArrow));
            }
            this.mPopupContainerWithArrow.initializeWidgetShortcut(this.mPopupContainerWithArrow.getWidgetContainer(), shortcut);
        } else {
            this.mPopupContainerWithArrow.close(false);
            PopupContainerWithArrow.showForIcon(this.mPopupContainerWithArrow.getOriginalIcon());
        }
    }

    /* access modifiers changed from: protected */
    public void showPopupContainerForIcon(BubbleTextView bubbleTextView) {
        PopupContainerWithArrow.showForIcon(bubbleTextView);
    }
}
