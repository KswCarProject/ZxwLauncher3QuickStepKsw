package com.android.launcher3.popup;

import android.content.Context;
import android.view.View;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.notification.NotificationContainer;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.views.ActivityContext;
import java.util.Map;
import java.util.function.Predicate;

public abstract class PopupLiveUpdateHandler<T extends Context & ActivityContext> implements PopupDataProvider.PopupDataChangeListener, View.OnAttachStateChangeListener {
    protected final T mContext;
    protected final PopupContainerWithArrow<T> mPopupContainerWithArrow;

    /* access modifiers changed from: protected */
    public abstract void showPopupContainerForIcon(BubbleTextView bubbleTextView);

    public PopupLiveUpdateHandler(T t, PopupContainerWithArrow<T> popupContainerWithArrow) {
        this.mContext = t;
        this.mPopupContainerWithArrow = popupContainerWithArrow;
    }

    public void onViewAttachedToWindow(View view) {
        PopupDataProvider popupDataProvider = ((ActivityContext) this.mContext).getPopupDataProvider();
        if (popupDataProvider != null) {
            popupDataProvider.setChangeListener(this);
        }
    }

    public void onViewDetachedFromWindow(View view) {
        PopupDataProvider popupDataProvider = ((ActivityContext) this.mContext).getPopupDataProvider();
        if (popupDataProvider != null) {
            popupDataProvider.setChangeListener((PopupDataProvider.PopupDataChangeListener) null);
        }
    }

    public void onNotificationDotsUpdated(Predicate<PackageUserKey> predicate) {
        if (predicate.test(PackageUserKey.fromItemInfo((ItemInfo) this.mPopupContainerWithArrow.getOriginalIcon().getTag()))) {
            this.mPopupContainerWithArrow.updateNotificationHeader();
        }
    }

    public void trimNotifications(Map<PackageUserKey, DotInfo> map) {
        NotificationContainer notificationContainer = this.mPopupContainerWithArrow.getNotificationContainer();
        if (notificationContainer != null) {
            DotInfo dotInfo = map.get(PackageUserKey.fromItemInfo((ItemInfo) this.mPopupContainerWithArrow.getOriginalIcon().getTag()));
            if (dotInfo == null || dotInfo.getNotificationKeys().size() == 0) {
                notificationContainer.setVisibility(8);
                this.mPopupContainerWithArrow.updateHiddenShortcuts();
                PopupContainerWithArrow<T> popupContainerWithArrow = this.mPopupContainerWithArrow;
                popupContainerWithArrow.assignMarginsAndBackgrounds(popupContainerWithArrow);
                this.mPopupContainerWithArrow.updateArrowColor();
                return;
            }
            notificationContainer.trimNotifications(NotificationKeyData.extractKeysOnly(dotInfo.getNotificationKeys()));
        }
    }

    public void onSystemShortcutsUpdated() {
        this.mPopupContainerWithArrow.close(true);
        showPopupContainerForIcon(this.mPopupContainerWithArrow.getOriginalIcon());
    }
}
