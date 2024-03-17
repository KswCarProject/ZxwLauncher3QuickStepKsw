package com.android.launcher3.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.ArrowPopup;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.widget.picker.WidgetsFullSheet;
import java.util.ArrayList;
import java.util.List;

public class OptionsPopupView extends ArrowPopup<Launcher> implements View.OnClickListener, View.OnLongClickListener {
    private final ArrayMap<View, OptionItem> mItemMap;
    private boolean mShouldAddArrow;
    private RectF mTargetRect;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 4096) != 0;
    }

    public OptionsPopupView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OptionsPopupView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mItemMap = new ArrayMap<>();
    }

    public void setTargetRect(RectF rectF) {
        this.mTargetRect = rectF;
    }

    public void onClick(View view) {
        handleViewClick(view);
    }

    public boolean onLongClick(View view) {
        return handleViewClick(view);
    }

    private boolean handleViewClick(View view) {
        OptionItem optionItem = this.mItemMap.get(view);
        if (optionItem == null) {
            return false;
        }
        if (optionItem.eventId.getId() > 0) {
            ((Launcher) this.mActivityContext).getStatsLogManager().logger().log(optionItem.eventId);
        }
        if (!optionItem.clickListener.onLongClick(view)) {
            return false;
        }
        close(true);
        return true;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || getPopupContainer().isEventOverView(this, motionEvent)) {
            return false;
        }
        close(true);
        return true;
    }

    public void setShouldAddArrow(boolean z) {
        this.mShouldAddArrow = z;
    }

    /* access modifiers changed from: protected */
    public boolean shouldAddArrow() {
        return this.mShouldAddArrow;
    }

    /* access modifiers changed from: protected */
    public void getTargetObjectLocation(Rect rect) {
        this.mTargetRect.roundOut(rect);
    }

    public static OptionsPopupView show(Launcher launcher, RectF rectF, List<OptionItem> list, boolean z) {
        return show(launcher, rectF, list, z, 0);
    }

    public static OptionsPopupView show(Launcher launcher, RectF rectF, List<OptionItem> list, boolean z, int i) {
        OptionsPopupView optionsPopupView = (OptionsPopupView) launcher.getLayoutInflater().inflate(R.layout.longpress_options_menu, launcher.getDragLayer(), false);
        optionsPopupView.mTargetRect = rectF;
        optionsPopupView.setShouldAddArrow(z);
        for (OptionItem next : list) {
            DeepShortcutView deepShortcutView = (DeepShortcutView) optionsPopupView.inflateAndAdd(R.layout.system_shortcut, optionsPopupView);
            if (i > 0) {
                deepShortcutView.getLayoutParams().width = i;
            }
            deepShortcutView.getIconView().setBackgroundDrawable(next.icon);
            deepShortcutView.getBubbleText().setText(next.label);
            deepShortcutView.setOnClickListener(optionsPopupView);
            deepShortcutView.setOnLongClickListener(optionsPopupView);
            optionsPopupView.mItemMap.put(deepShortcutView, next);
        }
        optionsPopupView.addPreDrawForColorExtraction(launcher);
        optionsPopupView.show();
        return optionsPopupView;
    }

    /* access modifiers changed from: protected */
    public List<View> getChildrenForColorExtraction() {
        int childCount = getChildCount();
        ArrayList arrayList = new ArrayList(childCount);
        for (int i = 0; i < childCount; i++) {
            arrayList.add(getChildAt(i));
        }
        return arrayList;
    }

    public static ArrayList<OptionItem> getOptions(Launcher launcher) {
        ArrayList<OptionItem> arrayList = new ArrayList<>();
        Launcher launcher2 = launcher;
        arrayList.add(new OptionItem(launcher2, Utilities.existsStyleWallpapers(launcher) ? R.string.styles_wallpaper_button_text : R.string.wallpaper_button_text, Utilities.existsStyleWallpapers(launcher) ? R.drawable.ic_palette : R.drawable.ic_wallpaper, StatsLogManager.LauncherEvent.IGNORE, $$Lambda$OptionsPopupView$NF9IRgQh6XF9g8qF1c_COh67JiE.INSTANCE));
        arrayList.add(new OptionItem(launcher, R.string.widget_button_text, R.drawable.ic_widget, StatsLogManager.LauncherEvent.LAUNCHER_WIDGETSTRAY_BUTTON_TAP_OR_LONGPRESS, $$Lambda$OptionsPopupView$pAmZ81tgdasPvyIfkVDZFKhjAk.INSTANCE));
        arrayList.add(new OptionItem(launcher2, R.string.settings_button_text, R.drawable.ic_setting, StatsLogManager.LauncherEvent.LAUNCHER_SETTINGS_BUTTON_TAP_OR_LONGPRESS, $$Lambda$OptionsPopupView$6qGgmQNuWcO7ZripzAwtPnpRTgk.INSTANCE));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public static boolean onWidgetsClicked(View view) {
        return openWidgets(Launcher.getLauncher(view.getContext())) != null;
    }

    public static WidgetsFullSheet openWidgets(Launcher launcher) {
        if (launcher.getPackageManager().isSafeMode()) {
            Toast.makeText(launcher, R.string.safemode_widget_error, 0).show();
            return null;
        }
        AbstractFloatingView topOpenViewWithType = AbstractFloatingView.getTopOpenViewWithType(launcher, 16);
        if (topOpenViewWithType != null) {
            return (WidgetsFullSheet) topOpenViewWithType;
        }
        return WidgetsFullSheet.show(launcher, true);
    }

    /* access modifiers changed from: private */
    public static boolean startSettings(View view) {
        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "start: startSettings");
        Launcher launcher = Launcher.getLauncher(view.getContext());
        launcher.startActivity(new Intent("android.intent.action.APPLICATION_PREFERENCES").setPackage(launcher.getPackageName()).addFlags(268468224));
        return true;
    }

    /* access modifiers changed from: private */
    public static boolean startWallpaperPicker(View view) {
        String str;
        Launcher launcher = Launcher.getLauncher(view.getContext());
        if (!Utilities.isWallpaperAllowed(launcher)) {
            if (launcher.getStringCache() != null) {
                str = launcher.getStringCache().disabledByAdminMessage;
            } else {
                str = launcher.getString(R.string.msg_disabled_by_admin);
            }
            Toast.makeText(launcher, str, 0).show();
            return false;
        }
        Intent putExtra = new Intent("android.intent.action.SET_WALLPAPER").addFlags(32768).putExtra(Utilities.EXTRA_WALLPAPER_OFFSET, launcher.getWorkspace().getWallpaperOffsetForCenterPage()).putExtra(Utilities.EXTRA_WALLPAPER_LAUNCH_SOURCE, "app_launched_launcher");
        if (!Utilities.existsStyleWallpapers(launcher)) {
            putExtra.putExtra(Utilities.EXTRA_WALLPAPER_FLAVOR, "wallpaper_only");
        } else {
            putExtra.putExtra(Utilities.EXTRA_WALLPAPER_FLAVOR, "focus_wallpaper");
        }
        String string = launcher.getString(R.string.wallpaper_picker_package);
        if (!TextUtils.isEmpty(string)) {
            putExtra.setPackage(string);
        }
        return launcher.lambda$startActivitySafely$7$Launcher(view, putExtra, placeholderInfo(putExtra));
    }

    static WorkspaceItemInfo placeholderInfo(Intent intent) {
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
        workspaceItemInfo.intent = intent;
        workspaceItemInfo.itemType = 1;
        workspaceItemInfo.container = LauncherSettings.Favorites.CONTAINER_SETTINGS;
        return workspaceItemInfo;
    }

    public static class OptionItem {
        public final View.OnLongClickListener clickListener;
        public final StatsLogManager.EventEnum eventId;
        public final Drawable icon;
        public final CharSequence label;
        public final int labelRes;

        public OptionItem(Context context, int i, int i2, StatsLogManager.EventEnum eventEnum, View.OnLongClickListener onLongClickListener) {
            this.labelRes = i;
            this.label = context.getText(i);
            this.icon = ContextCompat.getDrawable(context, i2);
            this.eventId = eventEnum;
            this.clickListener = onLongClickListener;
        }

        public OptionItem(CharSequence charSequence, Drawable drawable, StatsLogManager.EventEnum eventEnum, View.OnLongClickListener onLongClickListener) {
            this.labelRes = 0;
            this.label = charSequence;
            this.icon = drawable;
            this.eventId = eventEnum;
            this.clickListener = onLongClickListener;
        }
    }
}
