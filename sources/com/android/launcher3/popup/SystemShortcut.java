package com.android.launcher3.popup;

import android.app.ActivityOptions;
import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.WidgetsBottomSheet;

public abstract class SystemShortcut<T extends Context & ActivityContext> extends ItemInfo implements View.OnClickListener {
    public static final Factory<BaseDraggingActivity> APP_INFO = $$Lambda$bK5UtxJcJIlcZZiCp6isS3vGef4.INSTANCE;
    public static final Factory<BaseDraggingActivity> INSTALL = $$Lambda$SystemShortcut$r7uy1cifcM4uPvUlcixhJHkL_34.INSTANCE;
    public static final Factory<Launcher> WIDGETS = $$Lambda$SystemShortcut$0neFhURrHc2UleM_TSrFxuBcC1Q.INSTANCE;
    private boolean isEnabled = true;
    protected int mAccessibilityActionId;
    private final int mIconResId;
    protected final ItemInfo mItemInfo;
    protected final int mLabelResId;
    protected final View mOriginalView;
    protected final T mTarget;

    public interface Factory<T extends Context & ActivityContext> {
        SystemShortcut<T> getShortcut(T t, ItemInfo itemInfo, View view);
    }

    public boolean isLeftGroup() {
        return false;
    }

    public SystemShortcut(int i, int i2, T t, ItemInfo itemInfo, View view) {
        this.mIconResId = i;
        this.mLabelResId = i2;
        this.mAccessibilityActionId = i2;
        this.mTarget = t;
        this.mItemInfo = itemInfo;
        this.mOriginalView = view;
    }

    public SystemShortcut(SystemShortcut<T> systemShortcut) {
        this.mIconResId = systemShortcut.mIconResId;
        this.mLabelResId = systemShortcut.mLabelResId;
        this.mAccessibilityActionId = systemShortcut.mAccessibilityActionId;
        this.mTarget = systemShortcut.mTarget;
        this.mItemInfo = systemShortcut.mItemInfo;
        this.mOriginalView = systemShortcut.mOriginalView;
    }

    public void setIconAndLabelFor(View view, TextView textView) {
        view.setBackgroundResource(this.mIconResId);
        view.setEnabled(this.isEnabled);
        textView.setText(this.mLabelResId);
        textView.setEnabled(this.isEnabled);
    }

    public void setIconAndContentDescriptionFor(ImageView imageView) {
        imageView.setImageResource(this.mIconResId);
        imageView.setContentDescription(imageView.getContext().getText(this.mLabelResId));
        imageView.setEnabled(this.isEnabled);
    }

    public AccessibilityNodeInfo.AccessibilityAction createAccessibilityAction(Context context) {
        return new AccessibilityNodeInfo.AccessibilityAction(this.mAccessibilityActionId, context.getText(this.mLabelResId));
    }

    public void setEnabled(boolean z) {
        this.isEnabled = z;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean hasHandlerForAction(int i) {
        return this.mAccessibilityActionId == i;
    }

    static /* synthetic */ SystemShortcut lambda$static$0(Launcher launcher, ItemInfo itemInfo, View view) {
        if (itemInfo.getTargetComponent() != null && !launcher.getPopupDataProvider().getWidgetsForPackageUser(new PackageUserKey(itemInfo.getTargetComponent().getPackageName(), itemInfo.user)).isEmpty()) {
            return new Widgets(launcher, itemInfo, view);
        }
        return null;
    }

    public static class Widgets extends SystemShortcut<Launcher> {
        public Widgets(Launcher launcher, ItemInfo itemInfo, View view) {
            super(R.drawable.ic_widget, R.string.widget_button_text, launcher, itemInfo, view);
        }

        public void onClick(View view) {
            AbstractFloatingView.closeAllOpenViews((ActivityContext) this.mTarget);
            ((WidgetsBottomSheet) ((Launcher) this.mTarget).getLayoutInflater().inflate(R.layout.widgets_bottom_sheet, ((Launcher) this.mTarget).getDragLayer(), false)).populateAndShow(this.mItemInfo);
            ((Launcher) this.mTarget).getStatsLogManager().logger().withItemInfo(this.mItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_WIDGETS_TAP);
        }
    }

    public static class AppInfo<T extends Context & ActivityContext> extends SystemShortcut<T> {
        private SplitAccessibilityInfo mSplitA11yInfo;

        public AppInfo(T t, ItemInfo itemInfo, View view) {
            super(R.drawable.ic_info_no_shadow, R.string.app_info_drop_target_label, t, itemInfo, view);
        }

        public AppInfo(T t, ItemInfo itemInfo, View view, SplitAccessibilityInfo splitAccessibilityInfo) {
            this(t, itemInfo, view);
            this.mSplitA11yInfo = splitAccessibilityInfo;
            this.mAccessibilityActionId = splitAccessibilityInfo.nodeId;
        }

        public AccessibilityNodeInfo.AccessibilityAction createAccessibilityAction(Context context) {
            SplitAccessibilityInfo splitAccessibilityInfo = this.mSplitA11yInfo;
            if (splitAccessibilityInfo == null || !splitAccessibilityInfo.containsMultipleTasks) {
                return SystemShortcut.super.createAccessibilityAction(context);
            }
            return new AccessibilityNodeInfo.AccessibilityAction(this.mAccessibilityActionId, context.getString(R.string.split_app_info_accessibility, new Object[]{this.mSplitA11yInfo.taskTitle}));
        }

        public void onClick(View view) {
            dismissTaskMenuView(this.mTarget);
            new PackageManagerHelper(this.mTarget).startDetailsActivityForInfo(this.mItemInfo, Utilities.getViewBounds(view), ActivityOptions.makeBasic().toBundle());
            ((ActivityContext) this.mTarget).getStatsLogManager().logger().withItemInfo(this.mItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_SYSTEM_SHORTCUT_APP_INFO_TAP);
        }

        public static class SplitAccessibilityInfo {
            public final boolean containsMultipleTasks;
            public final int nodeId;
            public final CharSequence taskTitle;

            public SplitAccessibilityInfo(boolean z, CharSequence charSequence, int i) {
                this.containsMultipleTasks = z;
                this.taskTitle = charSequence;
                this.nodeId = i;
            }
        }
    }

    static /* synthetic */ SystemShortcut lambda$static$1(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo, View view) {
        boolean z = true;
        boolean z2 = (itemInfo instanceof WorkspaceItemInfo) && ((WorkspaceItemInfo) itemInfo).hasStatusFlag(8);
        boolean isInstantApp = itemInfo instanceof com.android.launcher3.model.data.AppInfo ? InstantAppResolver.newInstance(baseDraggingActivity).isInstantApp((com.android.launcher3.model.data.AppInfo) itemInfo) : false;
        if (!z2 && !isInstantApp) {
            z = false;
        }
        if (!z) {
            return null;
        }
        return new Install(baseDraggingActivity, itemInfo, view);
    }

    public static class Install extends SystemShortcut<BaseDraggingActivity> {
        public Install(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo, View view) {
            super(R.drawable.ic_install_no_shadow, R.string.install_drop_target_label, baseDraggingActivity, itemInfo, view);
        }

        public void onClick(View view) {
            ((BaseDraggingActivity) this.mTarget).startActivitySafely(view, new PackageManagerHelper(view.getContext()).getMarketIntent(this.mItemInfo.getTargetComponent().getPackageName()), this.mItemInfo);
            AbstractFloatingView.closeAllOpenViews((ActivityContext) this.mTarget);
        }
    }

    public static <T extends Context & ActivityContext> void dismissTaskMenuView(T t) {
        AbstractFloatingView.closeOpenViews((ActivityContext) t, true, 23947);
    }
}
