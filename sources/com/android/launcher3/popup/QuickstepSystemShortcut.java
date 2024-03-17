package com.android.launcher3.popup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.quickstep.views.RecentsView;

public interface QuickstepSystemShortcut {
    public static final String TAG = "QuickstepSystemShortcut";

    static SystemShortcut.Factory<BaseQuickstepLauncher> getSplitSelectShortcutByPosition(SplitConfigurationOptions.SplitPositionOption splitPositionOption) {
        return new SystemShortcut.Factory() {
            public final SystemShortcut getShortcut(Context context, ItemInfo itemInfo, View view) {
                return QuickstepSystemShortcut.lambda$getSplitSelectShortcutByPosition$0(SplitConfigurationOptions.SplitPositionOption.this, (BaseQuickstepLauncher) context, itemInfo, view);
            }
        };
    }

    static /* synthetic */ SystemShortcut lambda$getSplitSelectShortcutByPosition$0(SplitConfigurationOptions.SplitPositionOption splitPositionOption, BaseQuickstepLauncher baseQuickstepLauncher, ItemInfo itemInfo, View view) {
        return new SplitSelectSystemShortcut(baseQuickstepLauncher, itemInfo, view, splitPositionOption);
    }

    public static class SplitSelectSystemShortcut extends SystemShortcut<BaseQuickstepLauncher> {
        private final SplitConfigurationOptions.SplitPositionOption mPosition;

        public SplitSelectSystemShortcut(BaseQuickstepLauncher baseQuickstepLauncher, ItemInfo itemInfo, View view, SplitConfigurationOptions.SplitPositionOption splitPositionOption) {
            super(splitPositionOption.iconResId, splitPositionOption.textResId, baseQuickstepLauncher, itemInfo, view);
            this.mPosition = splitPositionOption;
        }

        public void onClick(View view) {
            Intent intent;
            Bitmap bitmap;
            if (this.mItemInfo instanceof WorkspaceItemInfo) {
                WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) this.mItemInfo;
                bitmap = workspaceItemInfo.bitmap.icon;
                intent = workspaceItemInfo.intent;
            } else if (this.mItemInfo instanceof AppInfo) {
                AppInfo appInfo = (AppInfo) this.mItemInfo;
                bitmap = appInfo.bitmap.icon;
                intent = appInfo.intent;
            } else {
                Log.e(QuickstepSystemShortcut.TAG, "unknown item type");
                return;
            }
            ((RecentsView) ((BaseQuickstepLauncher) this.mTarget).getOverviewPanel()).initiateSplitSelect(new SplitSelectSource(this.mOriginalView, new BitmapDrawable(bitmap), intent, this.mPosition));
        }
    }

    public static class SplitSelectSource {
        public final Drawable drawable;
        public final Intent intent;
        public final SplitConfigurationOptions.SplitPositionOption position;
        public final View view;

        public SplitSelectSource(View view2, Drawable drawable2, Intent intent2, SplitConfigurationOptions.SplitPositionOption splitPositionOption) {
            this.view = view2;
            this.drawable = drawable2;
            this.intent = intent2;
            this.position = splitPositionOption;
        }
    }
}
