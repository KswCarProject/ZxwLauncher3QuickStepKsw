package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.DropTarget;
import com.android.launcher3.SecondaryDropTarget;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.views.Snackbar;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import java.net.URISyntaxException;
import java.util.Objects;

public class SecondaryDropTarget extends ButtonDropTarget implements OnAlarmListener {
    private static final long CACHE_EXPIRE_TIMEOUT = 5000;
    private static final String TAG = "SecondaryDropTarget";
    private final Alarm mCacheExpireAlarm;
    protected int mCurrentAccessibilityAction;
    private boolean mHadPendingAlarm;
    /* access modifiers changed from: private */
    public final StatsLogManager mStatsLogManager;
    private final ArrayMap<UserHandle, Boolean> mUninstallDisabledCache;

    static /* synthetic */ void lambda$performDropAction$0() {
    }

    public SecondaryDropTarget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SecondaryDropTarget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mUninstallDisabledCache = new ArrayMap<>(1);
        this.mCurrentAccessibilityAction = -1;
        this.mCacheExpireAlarm = new Alarm();
        this.mStatsLogManager = StatsLogManager.newInstance(context);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mHadPendingAlarm) {
            this.mCacheExpireAlarm.setAlarm(CACHE_EXPIRE_TIMEOUT);
            this.mCacheExpireAlarm.setOnAlarmListener(this);
            this.mHadPendingAlarm = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mCacheExpireAlarm.alarmPending()) {
            this.mCacheExpireAlarm.cancelAlarm();
            this.mCacheExpireAlarm.setOnAlarmListener((OnAlarmListener) null);
            this.mHadPendingAlarm = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setupUi(R.id.action_uninstall);
    }

    /* access modifiers changed from: protected */
    public void setupUi(int i) {
        if (i != this.mCurrentAccessibilityAction) {
            this.mCurrentAccessibilityAction = i;
            if (i == R.id.action_uninstall) {
                setDrawable(R.drawable.ic_uninstall_no_shadow);
                updateText(R.string.uninstall_drop_target_label);
            } else if (i == R.id.action_dismiss_prediction) {
                setDrawable(R.drawable.ic_block_no_shadow);
                updateText(R.string.dismiss_prediction_label);
            } else if (i == R.id.action_reconfigure) {
                setDrawable(R.drawable.ic_setting);
                updateText(R.string.gadget_setup_text);
            }
        }
    }

    public void onAlarm(Alarm alarm) {
        this.mUninstallDisabledCache.clear();
    }

    public int getAccessibilityAction() {
        return this.mCurrentAccessibilityAction;
    }

    /* access modifiers changed from: protected */
    public void setupItemInfo(ItemInfo itemInfo) {
        int buttonType = getButtonType(itemInfo, getViewUnderDrag(itemInfo));
        if (buttonType != -1) {
            setupUi(buttonType);
        }
    }

    /* access modifiers changed from: protected */
    public boolean supportsDrop(ItemInfo itemInfo) {
        return getButtonType(itemInfo, getViewUnderDrag(itemInfo)) != -1;
    }

    public boolean supportsAccessibilityDrop(ItemInfo itemInfo, View view) {
        return getButtonType(itemInfo, view) != -1;
    }

    private int getButtonType(ItemInfo itemInfo, View view) {
        if (view instanceof AppWidgetHostView) {
            if (getReconfigurableWidgetId(view) != 0) {
                return R.id.action_reconfigure;
            }
            return -1;
        } else if (FeatureFlags.ENABLE_PREDICTION_DISMISS.get() && itemInfo.isPredictedItem()) {
            return R.id.action_dismiss_prediction;
        } else {
            Boolean bool = this.mUninstallDisabledCache.get(itemInfo.user);
            if (bool == null) {
                Bundle userRestrictions = ((UserManager) getContext().getSystemService("user")).getUserRestrictions(itemInfo.user);
                boolean z = false;
                if (userRestrictions.getBoolean("no_control_apps", false) || userRestrictions.getBoolean("no_uninstall_apps", false)) {
                    z = true;
                }
                bool = Boolean.valueOf(z);
                this.mUninstallDisabledCache.put(itemInfo.user, bool);
            }
            this.mCacheExpireAlarm.setAlarm(CACHE_EXPIRE_TIMEOUT);
            this.mCacheExpireAlarm.setOnAlarmListener(this);
            if (bool.booleanValue()) {
                return -1;
            }
            if (itemInfo instanceof ItemInfoWithIcon) {
                ItemInfoWithIcon itemInfoWithIcon = (ItemInfoWithIcon) itemInfo;
                if ((itemInfoWithIcon.runtimeStatusFlags & ItemInfoWithIcon.FLAG_SYSTEM_MASK) != 0 && (itemInfoWithIcon.runtimeStatusFlags & 128) == 0) {
                    return -1;
                }
            }
            if (getUninstallTarget(itemInfo) == null) {
                return -1;
            }
            return R.id.action_uninstall;
        }
    }

    private ComponentName getUninstallTarget(ItemInfo itemInfo) {
        UserHandle userHandle;
        Intent intent;
        LauncherActivityInfo resolveActivity;
        if (itemInfo == null || itemInfo.itemType != 0) {
            userHandle = null;
            intent = null;
        } else {
            intent = itemInfo.getIntent();
            userHandle = itemInfo.user;
        }
        if (intent == null || (resolveActivity = ((LauncherApps) this.mLauncher.getSystemService(LauncherApps.class)).resolveActivity(intent, userHandle)) == null || (resolveActivity.getApplicationInfo().flags & 1) != 0) {
            return null;
        }
        return resolveActivity.getComponentName();
    }

    public void onDrop(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        dragObject.dragSource = new DeferredOnComplete(dragObject.dragSource, getContext());
        super.onDrop(dragObject, dragOptions);
        doLog(dragObject.logInstanceId, dragObject.originalDragInfo);
    }

    private void doLog(InstanceId instanceId, ItemInfo itemInfo) {
        StatsLogManager.StatsLogger withInstanceId = this.mStatsLogManager.logger().withInstanceId(instanceId);
        if (itemInfo != null) {
            withInstanceId.withItemInfo(itemInfo);
        }
        int i = this.mCurrentAccessibilityAction;
        if (i == R.id.action_uninstall) {
            withInstanceId.log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROPPED_ON_UNINSTALL);
        } else if (i == R.id.action_dismiss_prediction) {
            withInstanceId.log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROPPED_ON_DONT_SUGGEST);
        }
    }

    public void completeDrop(DropTarget.DragObject dragObject) {
        ComponentName performDropAction = performDropAction(getViewUnderDrag(dragObject.dragInfo), dragObject.dragInfo, dragObject.logInstanceId);
        if (dragObject.dragSource instanceof DeferredOnComplete) {
            DeferredOnComplete deferredOnComplete = (DeferredOnComplete) dragObject.dragSource;
            if (performDropAction != null) {
                String unused = deferredOnComplete.mPackageName = performDropAction.getPackageName();
                Launcher launcher = this.mLauncher;
                Objects.requireNonNull(deferredOnComplete);
                launcher.addOnResumeCallback(new Runnable() {
                    public final void run() {
                        SecondaryDropTarget.DeferredOnComplete.this.onLauncherResume();
                    }
                });
                return;
            }
            deferredOnComplete.sendFailure();
        }
    }

    private View getViewUnderDrag(ItemInfo itemInfo) {
        if (!(itemInfo instanceof LauncherAppWidgetInfo) || itemInfo.container != -100 || this.mLauncher.getWorkspace().getDragInfo() == null) {
            return null;
        }
        return this.mLauncher.getWorkspace().getDragInfo().cell;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
        r4 = (android.appwidget.AppWidgetHostView) r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getReconfigurableWidgetId(android.view.View r4) {
        /*
            r3 = this;
            boolean r0 = r4 instanceof android.appwidget.AppWidgetHostView
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            android.appwidget.AppWidgetHostView r4 = (android.appwidget.AppWidgetHostView) r4
            android.appwidget.AppWidgetProviderInfo r0 = r4.getAppWidgetInfo()
            if (r0 == 0) goto L_0x0029
            android.content.ComponentName r2 = r0.configure
            if (r2 != 0) goto L_0x0013
            goto L_0x0029
        L_0x0013:
            android.content.Context r2 = r3.getContext()
            com.android.launcher3.widget.LauncherAppWidgetProviderInfo r0 = com.android.launcher3.widget.LauncherAppWidgetProviderInfo.fromProviderInfo(r2, r0)
            int r0 = r0.getWidgetFeatures()
            r0 = r0 & 1
            if (r0 != 0) goto L_0x0024
            return r1
        L_0x0024:
            int r4 = r4.getAppWidgetId()
            return r4
        L_0x0029:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.SecondaryDropTarget.getReconfigurableWidgetId(android.view.View):int");
    }

    /* access modifiers changed from: protected */
    public ComponentName performDropAction(View view, ItemInfo itemInfo, InstanceId instanceId) {
        int i = this.mCurrentAccessibilityAction;
        if (i == R.id.action_reconfigure) {
            int reconfigurableWidgetId = getReconfigurableWidgetId(view);
            if (reconfigurableWidgetId != 0) {
                this.mLauncher.setWaitingForResult(PendingRequestArgs.forWidgetInfo(reconfigurableWidgetId, (WidgetAddFlowHandler) null, itemInfo));
                this.mLauncher.getAppWidgetHost().startConfigActivity(this.mLauncher, reconfigurableWidgetId, 13);
            }
            return null;
        } else if (i == R.id.action_dismiss_prediction) {
            if (FeatureFlags.ENABLE_DISMISS_PREDICTION_UNDO.get()) {
                this.mLauncher.getDragLayer().announceForAccessibility(getContext().getString(R.string.item_removed));
                Snackbar.show(this.mLauncher, R.string.item_removed, R.string.undo, $$Lambda$SecondaryDropTarget$ThStv7riJDodh3903usA6mI0ITw.INSTANCE, new Runnable(instanceId, itemInfo) {
                    public final /* synthetic */ InstanceId f$1;
                    public final /* synthetic */ ItemInfo f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        SecondaryDropTarget.this.lambda$performDropAction$1$SecondaryDropTarget(this.f$1, this.f$2);
                    }
                });
            }
            return null;
        } else {
            ComponentName uninstallTarget = getUninstallTarget(itemInfo);
            if (uninstallTarget == null) {
                Toast.makeText(this.mLauncher, R.string.uninstall_system_app_text, 0).show();
                return null;
            }
            try {
                this.mLauncher.startActivity(Intent.parseUri(this.mLauncher.getString(R.string.delete_package_intent), 0).setData(Uri.fromParts("package", uninstallTarget.getPackageName(), uninstallTarget.getClassName())).putExtra("android.intent.extra.USER", itemInfo.user));
                FileLog.d(TAG, "start uninstall activity " + uninstallTarget.getPackageName());
                return uninstallTarget;
            } catch (URISyntaxException unused) {
                Log.e(TAG, "Failed to parse intent to start uninstall activity for item=" + itemInfo);
                return null;
            }
        }
    }

    public /* synthetic */ void lambda$performDropAction$1$SecondaryDropTarget(InstanceId instanceId, ItemInfo itemInfo) {
        this.mStatsLogManager.logger().withInstanceId(instanceId).withItemInfo(itemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_DISMISS_PREDICTION_UNDO);
    }

    public void onAccessibilityDrop(View view, ItemInfo itemInfo) {
        InstanceId newInstanceId = new InstanceIdSequence().newInstanceId();
        doLog(newInstanceId, itemInfo);
        performDropAction(view, itemInfo, newInstanceId);
    }

    private class DeferredOnComplete implements DragSource {
        private final Context mContext;
        private DropTarget.DragObject mDragObject;
        private final DragSource mOriginal;
        /* access modifiers changed from: private */
        public String mPackageName;

        public DeferredOnComplete(DragSource dragSource, Context context) {
            this.mOriginal = dragSource;
            this.mContext = context;
        }

        public void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
            this.mDragObject = dragObject;
        }

        public void onLauncherResume() {
            if (new PackageManagerHelper(this.mContext).getApplicationInfo(this.mPackageName, this.mDragObject.dragInfo.user, 8192) == null) {
                this.mDragObject.dragSource = this.mOriginal;
                this.mOriginal.onDropCompleted(SecondaryDropTarget.this, this.mDragObject, true);
                SecondaryDropTarget.this.mStatsLogManager.logger().withInstanceId(this.mDragObject.logInstanceId).log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_UNINSTALL_COMPLETED);
                return;
            }
            sendFailure();
            SecondaryDropTarget.this.mStatsLogManager.logger().withInstanceId(this.mDragObject.logInstanceId).log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_UNINSTALL_CANCELLED);
        }

        public void sendFailure() {
            this.mDragObject.dragSource = this.mOriginal;
            this.mDragObject.cancelled = true;
            this.mOriginal.onDropCompleted(SecondaryDropTarget.this, this.mDragObject, false);
        }
    }
}
