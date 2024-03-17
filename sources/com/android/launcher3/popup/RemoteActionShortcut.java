package com.android.launcher3.popup;

import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.Executors;
import com.android.launcher3.views.ActivityContext;
import java.util.Objects;

public class RemoteActionShortcut extends SystemShortcut<BaseDraggingActivity> {
    private static final boolean DEBUG = Utilities.IS_DEBUG_DEVICE;
    private static final String TAG = "RemoteActionShortcut";
    private final RemoteAction mAction;

    public boolean isLeftGroup() {
        return true;
    }

    public RemoteActionShortcut(RemoteAction remoteAction, BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo, View view) {
        super(0, R.id.action_remote_action_shortcut, baseDraggingActivity, itemInfo, view);
        this.mAction = remoteAction;
    }

    public void setIconAndLabelFor(View view, TextView textView) {
        Icon icon = this.mAction.getIcon();
        Context context = view.getContext();
        Objects.requireNonNull(view);
        icon.loadDrawableAsync(context, new Icon.OnDrawableLoadedListener(view) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void onDrawableLoaded(Drawable drawable) {
                this.f$0.setBackground(drawable);
            }
        }, Executors.MAIN_EXECUTOR.getHandler());
        textView.setText(this.mAction.getTitle());
    }

    public void setIconAndContentDescriptionFor(ImageView imageView) {
        Icon icon = this.mAction.getIcon();
        Context context = imageView.getContext();
        Objects.requireNonNull(imageView);
        icon.loadDrawableAsync(context, new Icon.OnDrawableLoadedListener(imageView) {
            public final /* synthetic */ ImageView f$0;

            {
                this.f$0 = r1;
            }

            public final void onDrawableLoaded(Drawable drawable) {
                this.f$0.setImageDrawable(drawable);
            }
        }, Executors.MAIN_EXECUTOR.getHandler());
        imageView.setContentDescription(this.mAction.getContentDescription());
    }

    public AccessibilityNodeInfo.AccessibilityAction createAccessibilityAction(Context context) {
        return new AccessibilityNodeInfo.AccessibilityAction(R.id.action_remote_action_shortcut, this.mAction.getContentDescription());
    }

    public void onClick(View view) {
        AbstractFloatingView.closeAllOpenViews((ActivityContext) this.mTarget);
        ((BaseDraggingActivity) this.mTarget).getStatsLogManager().logger().withItemInfo(this.mItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_PAUSE_TAP);
        String str = this.mAction.getTitle() + ", " + this.mItemInfo.getTargetComponent().getPackageName();
        try {
            if (DEBUG) {
                Log.d(TAG, "Sending action: " + str);
            }
            this.mAction.getActionIntent().send(this.mTarget, 0, new Intent().putExtra("android.intent.extra.PACKAGE_NAME", this.mItemInfo.getTargetComponent().getPackageName()), new PendingIntent.OnFinished(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onSendFinished(PendingIntent pendingIntent, Intent intent, int i, String str, Bundle bundle) {
                    RemoteActionShortcut.this.lambda$onClick$0$RemoteActionShortcut(this.f$1, pendingIntent, intent, i, str, bundle);
                }
            }, Executors.MAIN_EXECUTOR.getHandler());
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Remote action canceled: " + str, e);
            Toast.makeText(this.mTarget, ((BaseDraggingActivity) this.mTarget).getString(R.string.remote_action_failed, new Object[]{this.mAction.getTitle()}), 0).show();
        }
    }

    public /* synthetic */ void lambda$onClick$0$RemoteActionShortcut(String str, PendingIntent pendingIntent, Intent intent, int i, String str2, Bundle bundle) {
        if (DEBUG) {
            Log.d(TAG, "Action is complete: " + str);
        }
        if (str2 != null && !str2.isEmpty()) {
            Log.e(TAG, "Remote action returned result: " + str + " : " + str2);
            Toast.makeText(this.mTarget, str2, 0).show();
        }
    }
}
