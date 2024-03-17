package com.android.launcher3.touch;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInstaller;
import android.graphics.Rect;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.SearchActionItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.InstallSessionHelper;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.PendingAppWidgetHostView;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import com.android.launcher3.widget.WidgetManagerHelper;
import java.util.Collections;

public class ItemClickHandler {
    public static final View.OnClickListener INSTANCE = $$Lambda$ItemClickHandler$c3IcSovkrXGdCZtXy0f_A5Sz5VA.INSTANCE;
    private static final String TAG = "ItemClickHandler";

    /* access modifiers changed from: private */
    public static void onClick(View view) {
        if (view.getWindowToken() != null) {
            Launcher launcher = Launcher.getLauncher(view.getContext());
            if (launcher.getWorkspace().isFinishedSwitchingState()) {
                Object tag = view.getTag();
                if (tag instanceof WorkspaceItemInfo) {
                    onClickAppShortcut(view, (WorkspaceItemInfo) tag, launcher);
                } else if (tag instanceof FolderInfo) {
                    if (view instanceof FolderIcon) {
                        onClickFolderIcon(view);
                    }
                } else if (tag instanceof AppInfo) {
                    startAppShortcutOrInfoActivity(view, (AppInfo) tag, launcher);
                } else if (tag instanceof LauncherAppWidgetInfo) {
                    if (view instanceof PendingAppWidgetHostView) {
                        onClickPendingWidget((PendingAppWidgetHostView) view, launcher);
                    }
                } else if (tag instanceof SearchActionItemInfo) {
                    onClickSearchAction(launcher, (SearchActionItemInfo) tag);
                }
            }
        }
    }

    private static void onClickFolderIcon(View view) {
        Folder folder = ((FolderIcon) view).getFolder();
        if (!folder.isOpen() && !folder.isDestroyed()) {
            folder.animateOpen();
            StatsLogManager.newInstance(view.getContext()).logger().withItemInfo(folder.mInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_OPEN);
        }
    }

    private static void onClickPendingWidget(PendingAppWidgetHostView pendingAppWidgetHostView, Launcher launcher) {
        boolean z = false;
        if (launcher.getPackageManager().isSafeMode()) {
            Toast.makeText(launcher, R.string.safemode_widget_error, 0).show();
            return;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) pendingAppWidgetHostView.getTag();
        if (pendingAppWidgetHostView.isReadyForClickSetup()) {
            LauncherAppWidgetProviderInfo findProvider = new WidgetManagerHelper(launcher).findProvider(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
            if (findProvider != null) {
                WidgetAddFlowHandler widgetAddFlowHandler = new WidgetAddFlowHandler((AppWidgetProviderInfo) findProvider);
                if (!launcherAppWidgetInfo.hasRestoreFlag(1)) {
                    widgetAddFlowHandler.startConfigActivity(launcher, launcherAppWidgetInfo, 13);
                } else if (launcherAppWidgetInfo.hasRestoreFlag(16)) {
                    widgetAddFlowHandler.startBindFlow(launcher, launcherAppWidgetInfo.appWidgetId, launcherAppWidgetInfo, 12);
                }
            }
        } else {
            String packageName = launcherAppWidgetInfo.providerName.getPackageName();
            if (launcherAppWidgetInfo.installProgress >= 0) {
                z = true;
            }
            onClickPendingAppItem(pendingAppWidgetHostView, launcher, packageName, z);
        }
    }

    private static void onClickPendingAppItem(View view, Launcher launcher, String str, boolean z) {
        if (z) {
            startMarketIntentForPackage(view, launcher, str);
        } else {
            new AlertDialog.Builder(launcher).setTitle(R.string.abandoned_promises_title).setMessage(R.string.abandoned_promise_explanation).setPositiveButton(R.string.abandoned_search, new DialogInterface.OnClickListener(view, launcher, str) {
                public final /* synthetic */ View f$0;
                public final /* synthetic */ Launcher f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ItemClickHandler.startMarketIntentForPackage(this.f$0, this.f$1, this.f$2);
                }
            }).setNeutralButton(R.string.abandoned_clean_this, new DialogInterface.OnClickListener(str, view.getTag() instanceof ItemInfo ? ((ItemInfo) view.getTag()).user : Process.myUserHandle()) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ UserHandle f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    Launcher.this.getWorkspace().persistRemoveItemsByMatcher(ItemInfoMatcher.ofPackages(Collections.singleton(this.f$1), this.f$2), "user explicitly removes the promise app icon");
                }
            }).create().show();
        }
    }

    /* access modifiers changed from: private */
    public static void startMarketIntentForPackage(View view, Launcher launcher, String str) {
        PackageInstaller.SessionInfo activeSessionInfo;
        ItemInfo itemInfo = (ItemInfo) view.getTag();
        if (Utilities.ATLEAST_Q && (activeSessionInfo = InstallSessionHelper.INSTANCE.lambda$get$1$MainThreadInitializedObject(launcher).getActiveSessionInfo(itemInfo.user, str)) != null) {
            try {
                ((LauncherApps) launcher.getSystemService(LauncherApps.class)).startPackageInstallerSessionDetailsActivity(activeSessionInfo, (Rect) null, launcher.getActivityLaunchOptions(view, itemInfo).toBundle());
                return;
            } catch (Exception e) {
                Log.e(TAG, "Unable to launch market intent for package=" + str, e);
            }
        }
        launcher.lambda$startActivitySafely$7$Launcher(view, new PackageManagerHelper(launcher).getMarketIntent(str), itemInfo);
    }

    public static boolean handleDisabledItemClicked(WorkspaceItemInfo workspaceItemInfo, Context context) {
        int i = workspaceItemInfo.runtimeStatusFlags & ItemInfoWithIcon.FLAG_DISABLED_MASK;
        if (maybeCreateAlertDialogForShortcut(workspaceItemInfo, context)) {
            return true;
        }
        if ((i & -5 & -9) == 0) {
            return false;
        }
        if (!TextUtils.isEmpty(workspaceItemInfo.disabledMessage)) {
            Toast.makeText(context, workspaceItemInfo.disabledMessage, 0).show();
            return true;
        }
        int i2 = R.string.activity_not_available;
        if ((workspaceItemInfo.runtimeStatusFlags & 1) != 0) {
            i2 = R.string.safemode_shortcut_error;
        } else if (!((workspaceItemInfo.runtimeStatusFlags & 16) == 0 && (workspaceItemInfo.runtimeStatusFlags & 32) == 0)) {
            i2 = R.string.shortcut_not_available;
        }
        Toast.makeText(context, i2, 0).show();
        return true;
    }

    private static boolean maybeCreateAlertDialogForShortcut(WorkspaceItemInfo workspaceItemInfo, Context context) {
        try {
            Launcher launcher = Launcher.getLauncher(context);
            if (workspaceItemInfo.itemType != 6 || !workspaceItemInfo.isDisabledVersionLower()) {
                return false;
            }
            new AlertDialog.Builder(context).setTitle(R.string.dialog_update_title).setMessage(R.string.dialog_update_message).setPositiveButton(R.string.dialog_update, new DialogInterface.OnClickListener(context, workspaceItemInfo) {
                public final /* synthetic */ Context f$0;
                public final /* synthetic */ WorkspaceItemInfo f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.startActivity(this.f$1.getMarketIntent(this.f$0));
                }
            }).setNeutralButton(R.string.dialog_remove, new DialogInterface.OnClickListener(workspaceItemInfo) {
                public final /* synthetic */ WorkspaceItemInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    Launcher.this.getWorkspace().persistRemoveItemsByMatcher(ItemInfoMatcher.ofShortcutKeys(Collections.singleton(ShortcutKey.fromItemInfo(this.f$1))), "user explicitly removes disabled shortcut");
                }
            }).create().show();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating alert dialog", e);
            return false;
        }
    }

    public static void onClickAppShortcut(View view, WorkspaceItemInfo workspaceItemInfo, Launcher launcher) {
        String str;
        if (!workspaceItemInfo.isDisabled() || !handleDisabledItemClicked(workspaceItemInfo, launcher)) {
            if ((view instanceof BubbleTextView) && workspaceItemInfo.hasPromiseIconUi()) {
                if (workspaceItemInfo.getIntent().getComponent() != null) {
                    str = workspaceItemInfo.getIntent().getComponent().getPackageName();
                } else {
                    str = workspaceItemInfo.getIntent().getPackage();
                }
                if (!TextUtils.isEmpty(str)) {
                    onClickPendingAppItem(view, launcher, str, (workspaceItemInfo.runtimeStatusFlags & 1024) != 0);
                    return;
                }
            }
            startAppShortcutOrInfoActivity(view, workspaceItemInfo, launcher);
        }
    }

    public static void onClickSearchAction(Launcher launcher, SearchActionItemInfo searchActionItemInfo) {
        if (searchActionItemInfo.getIntent() != null) {
            if (searchActionItemInfo.hasFlags(6)) {
                launcher.startActivityForResult(searchActionItemInfo.getIntent(), 0);
            } else {
                launcher.startActivity(searchActionItemInfo.getIntent());
            }
        } else if (searchActionItemInfo.getPendingIntent() != null) {
            try {
                PendingIntent pendingIntent = searchActionItemInfo.getPendingIntent();
                if (!searchActionItemInfo.hasFlags(2)) {
                    pendingIntent.send();
                } else if (searchActionItemInfo.hasFlags(6)) {
                    launcher.startIntentSenderForResult(pendingIntent.getIntentSender(), 0, (Intent) null, 0, 0, 0);
                } else {
                    launcher.startIntentSender(pendingIntent.getIntentSender(), (Intent) null, 0, 0, 0);
                }
            } catch (PendingIntent.CanceledException | IntentSender.SendIntentException unused) {
                Toast.makeText(launcher, launcher.getResources().getText(R.string.shortcut_not_available), 0).show();
            }
        }
        if (searchActionItemInfo.hasFlags(128)) {
            launcher.getStatsLogManager().logger().withItemInfo(searchActionItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_SEARCHINAPP_LAUNCH);
        } else {
            launcher.getStatsLogManager().logger().withItemInfo(searchActionItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_APP_LAUNCH_TAP);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x002c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void startAppShortcutOrInfoActivity(android.view.View r4, com.android.launcher3.model.data.ItemInfo r5, com.android.launcher3.Launcher r6) {
        /*
            java.lang.String r0 = "Main"
            java.lang.String r1 = "start: startAppShortcutOrInfoActivity"
            com.android.launcher3.testing.TestLogging.recordEvent(r0, r1)
            boolean r0 = r5 instanceof com.android.launcher3.model.data.ItemInfoWithIcon
            if (r0 == 0) goto L_0x0026
            r0 = r5
            com.android.launcher3.model.data.ItemInfoWithIcon r0 = (com.android.launcher3.model.data.ItemInfoWithIcon) r0
            int r1 = r0.runtimeStatusFlags
            r1 = r1 & 1024(0x400, float:1.435E-42)
            if (r1 == 0) goto L_0x0026
            com.android.launcher3.util.PackageManagerHelper r1 = new com.android.launcher3.util.PackageManagerHelper
            r1.<init>(r6)
            android.content.ComponentName r0 = r0.getTargetComponent()
            java.lang.String r0 = r0.getPackageName()
            android.content.Intent r0 = r1.getMarketIntent(r0)
            goto L_0x002a
        L_0x0026:
            android.content.Intent r0 = r5.getIntent()
        L_0x002a:
            if (r0 == 0) goto L_0x0080
            boolean r1 = r5 instanceof com.android.launcher3.model.data.WorkspaceItemInfo
            if (r1 == 0) goto L_0x0070
            r1 = r5
            com.android.launcher3.model.data.WorkspaceItemInfo r1 = (com.android.launcher3.model.data.WorkspaceItemInfo) r1
            r2 = 8
            boolean r2 = r1.hasStatusFlag(r2)
            if (r2 == 0) goto L_0x0051
            java.lang.String r2 = r0.getAction()
            java.lang.String r3 = "android.intent.action.VIEW"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0051
            android.content.Intent r2 = new android.content.Intent
            r2.<init>(r0)
            r0 = 0
            r2.setPackage(r0)
            r0 = r2
        L_0x0051:
            int r1 = r1.options
            r1 = r1 & 16
            if (r1 == 0) goto L_0x0070
            android.content.Intent r4 = r5.getIntent()
            r0 = 0
            r6.startActivityForResult(r4, r0)
            com.android.launcher3.logging.InstanceIdSequence r4 = new com.android.launcher3.logging.InstanceIdSequence
            r4.<init>()
            com.android.launcher3.logging.InstanceId r4 = r4.newInstanceId()
            com.android.launcher3.logging.StatsLogManager r0 = r6.getStatsLogManager()
            r6.logAppLaunch(r0, r5, r4)
            return
        L_0x0070:
            if (r4 == 0) goto L_0x007c
            boolean r1 = r6.supportsAdaptiveIconAnimation(r4)
            if (r1 == 0) goto L_0x007c
            r1 = 1
            com.android.launcher3.views.FloatingIconView.fetchIcon(r6, r4, r5, r1)
        L_0x007c:
            r6.lambda$startActivitySafely$7$Launcher(r4, r0, r5)
            return
        L_0x0080:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException
            java.lang.String r5 = "Input must have a valid intent"
            r4.<init>(r5)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.touch.ItemClickHandler.startAppShortcutOrInfoActivity(android.view.View, com.android.launcher3.model.data.ItemInfo, com.android.launcher3.Launcher):void");
    }
}
