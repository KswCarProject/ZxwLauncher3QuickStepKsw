package com.android.launcher3.notification;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.view.View;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.views.ActivityContext;

public class NotificationInfo implements View.OnClickListener {
    public final boolean autoCancel;
    public final boolean dismissable;
    public final PendingIntent intent;
    private int mIconColor;
    private Drawable mIconDrawable;
    private boolean mIsIconLarge;
    private final ItemInfo mItemInfo;
    public final String notificationKey;
    public final PackageUserKey packageUserKey;
    public final CharSequence text;
    public final CharSequence title;

    public NotificationInfo(Context context, StatusBarNotification statusBarNotification, ItemInfo itemInfo) {
        Icon icon;
        this.packageUserKey = PackageUserKey.fromNotification(statusBarNotification);
        this.notificationKey = statusBarNotification.getKey();
        Notification notification = statusBarNotification.getNotification();
        this.title = notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE);
        this.text = notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT);
        Drawable drawable = null;
        boolean z = true;
        if (notification.getBadgeIconType() == 1) {
            icon = null;
        } else {
            icon = notification.getLargeIcon();
        }
        if (icon == null) {
            Icon smallIcon = notification.getSmallIcon();
            this.mIconDrawable = smallIcon != null ? smallIcon.loadDrawable(context) : drawable;
            this.mIconColor = statusBarNotification.getNotification().color;
            this.mIsIconLarge = false;
        } else {
            this.mIconDrawable = icon.loadDrawable(context);
            this.mIsIconLarge = true;
        }
        if (this.mIconDrawable == null) {
            this.mIconDrawable = LauncherAppState.getInstance(context).getIconCache().getDefaultIcon(statusBarNotification.getUser()).newIcon(context);
        }
        this.intent = notification.contentIntent;
        this.autoCancel = (notification.flags & 16) != 0;
        this.dismissable = (notification.flags & 2) != 0 ? false : z;
        this.mItemInfo = itemInfo;
    }

    public void onClick(View view) {
        PopupDataProvider popupDataProvider;
        if (this.intent != null) {
            ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(view.getContext());
            try {
                this.intent.send((Context) null, 0, (Intent) null, (PendingIntent.OnFinished) null, (Handler) null, (String) null, ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle());
                activityContext.getStatsLogManager().logger().withItemInfo(this.mItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_NOTIFICATION_LAUNCH_TAP);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            if (this.autoCancel && (popupDataProvider = activityContext.getPopupDataProvider()) != null) {
                popupDataProvider.cancelNotification(this.notificationKey);
            }
            AbstractFloatingView.closeOpenViews(activityContext, true, 131074);
        }
    }

    public Drawable getIconForBackground(Context context, int i) {
        if (this.mIsIconLarge) {
            return this.mIconDrawable;
        }
        this.mIconColor = IconPalette.resolveContrastColor(context, this.mIconColor, i);
        Drawable mutate = this.mIconDrawable.mutate();
        mutate.setTintList((ColorStateList) null);
        mutate.setTint(this.mIconColor);
        return mutate;
    }
}
