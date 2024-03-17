package com.android.launcher3.icons;

import android.content.Context;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.graphics.LauncherPreviewRenderer;
import com.android.launcher3.util.Themes;

public class LauncherIcons extends BaseIconFactory implements AutoCloseable {
    private static LauncherIcons sPool;
    private static int sPoolId;
    private static final Object sPoolSync = new Object();
    private final int mPoolId;
    private LauncherIcons next;

    public static LauncherIcons obtain(Context context) {
        if (context instanceof LauncherPreviewRenderer.PreviewContext) {
            return ((LauncherPreviewRenderer.PreviewContext) context).newLauncherIcons(context);
        }
        synchronized (sPoolSync) {
            LauncherIcons launcherIcons = sPool;
            if (launcherIcons != null) {
                sPool = launcherIcons.next;
                launcherIcons.next = null;
                return launcherIcons;
            }
            int i = sPoolId;
            InvariantDeviceProfile invariantDeviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
            return new LauncherIcons(context, invariantDeviceProfile.fillResIconDpi, invariantDeviceProfile.iconBitmapSize, i);
        }
    }

    public static void clearPool() {
        synchronized (sPoolSync) {
            sPool = null;
            sPoolId++;
        }
    }

    protected LauncherIcons(Context context, int i, int i2, int i3) {
        super(context, i, i2, IconShape.getShape().enableShapeDetection());
        this.mMonoIconEnabled = Themes.isThemedIconEnabled(context);
        this.mPoolId = i3;
    }

    public void recycle() {
        synchronized (sPoolSync) {
            if (sPoolId == this.mPoolId) {
                clear();
                this.next = sPool;
                sPool = this;
            }
        }
    }

    public void close() {
        recycle();
    }
}
