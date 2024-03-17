package com.android.launcher3.icons;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.ComponentWithLabel;

public interface ComponentWithLabelAndIcon extends ComponentWithLabel {
    Drawable getFullResIcon(IconCache iconCache);

    public static class ComponentWithIconCachingLogic extends ComponentWithLabel.ComponentCachingLogic<ComponentWithLabelAndIcon> {
        public ComponentWithIconCachingLogic(Context context, boolean z) {
            super(context, z);
        }

        public BitmapInfo loadIcon(Context context, ComponentWithLabelAndIcon componentWithLabelAndIcon) {
            Drawable fullResIcon = componentWithLabelAndIcon.getFullResIcon(LauncherAppState.getInstance(context).getIconCache());
            if (fullResIcon == null) {
                return super.loadIcon(context, componentWithLabelAndIcon);
            }
            LauncherIcons obtain = LauncherIcons.obtain(context);
            try {
                BitmapInfo createBadgedIconBitmap = obtain.createBadgedIconBitmap(fullResIcon, new BaseIconFactory.IconOptions().setUser(componentWithLabelAndIcon.getUser()));
                if (obtain != null) {
                    obtain.close();
                }
                return createBadgedIconBitmap;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        }
    }
}
