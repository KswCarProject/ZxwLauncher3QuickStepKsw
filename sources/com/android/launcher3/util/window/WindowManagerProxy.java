package com.android.launcher3.util.window;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Pair;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.WindowManager;
import android.view.WindowMetrics;
import com.android.launcher3.R;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.ResourceBasedOverride;
import com.android.launcher3.util.WindowBounds;

public class WindowManagerProxy implements ResourceBasedOverride {
    public static final MainThreadInitializedObject<WindowManagerProxy> INSTANCE = MainThreadInitializedObject.forOverride(WindowManagerProxy.class, R.string.window_manager_proxy_class);
    public static final int MIN_TABLET_WIDTH = 600;
    protected final boolean mTaskbarDrawnInProcess;

    public static WindowManagerProxy newInstance(Context context) {
        return (WindowManagerProxy) ResourceBasedOverride.Overrides.getObject(WindowManagerProxy.class, context, R.string.window_manager_proxy_class);
    }

    public WindowManagerProxy() {
        this(false);
    }

    protected WindowManagerProxy(boolean z) {
        this.mTaskbarDrawnInProcess = z;
    }

    public ArrayMap<String, Pair<CachedDisplayInfo, WindowBounds[]>> estimateInternalDisplayBounds(Context context) {
        Context context2;
        Display[] displays = getDisplays(context);
        ArrayMap<String, Pair<CachedDisplayInfo, WindowBounds[]>> arrayMap = new ArrayMap<>();
        for (Display display : displays) {
            if (isInternalDisplay(display)) {
                if (Utilities.ATLEAST_S) {
                    context2 = context.createWindowContext(display, 2, (Bundle) null);
                } else {
                    context2 = context.createDisplayContext(display);
                }
                CachedDisplayInfo normalize = getDisplayInfo(context2, display).normalize();
                arrayMap.put(normalize.id, Pair.create(normalize, estimateWindowBounds(context, normalize)));
            }
        }
        return arrayMap;
    }

    public WindowBounds getRealBounds(Context context, Display display, CachedDisplayInfo cachedDisplayInfo) {
        Context context2 = context;
        CachedDisplayInfo cachedDisplayInfo2 = cachedDisplayInfo;
        if (!Utilities.ATLEAST_R) {
            Point point = new Point();
            Point point2 = new Point();
            display.getCurrentSizeRange(point, point2);
            if (cachedDisplayInfo2.size.y > cachedDisplayInfo2.size.x) {
                return new WindowBounds(cachedDisplayInfo2.size.x, cachedDisplayInfo2.size.y, point.x, point2.y, cachedDisplayInfo2.rotation);
            }
            new WindowBounds(cachedDisplayInfo2.size.x, cachedDisplayInfo2.size.y, point2.x, point.y, cachedDisplayInfo2.rotation);
        }
        WindowMetrics maximumWindowMetrics = ((WindowManager) context2.getSystemService(WindowManager.class)).getMaximumWindowMetrics();
        Rect rect = new Rect();
        normalizeWindowInsets(context2, maximumWindowMetrics.getWindowInsets(), rect);
        return new WindowBounds(maximumWindowMetrics.getBounds(), rect, cachedDisplayInfo2.rotation);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0098  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.WindowInsets normalizeWindowInsets(android.content.Context r9, android.view.WindowInsets r10, android.graphics.Rect r11) {
        /*
            r8 = this;
            boolean r0 = com.android.launcher3.Utilities.ATLEAST_R
            if (r0 == 0) goto L_0x00ce
            boolean r0 = r8.mTaskbarDrawnInProcess
            if (r0 != 0) goto L_0x000a
            goto L_0x00ce
        L_0x000a:
            android.view.WindowInsets$Builder r0 = new android.view.WindowInsets$Builder
            r0.<init>(r10)
            int r1 = android.view.WindowInsets.Type.navigationBars()
            android.graphics.Insets r1 = r10.getInsets(r1)
            android.content.res.Resources r2 = r9.getResources()
            android.content.res.Configuration r3 = r2.getConfiguration()
            int r4 = r3.smallestScreenWidthDp
            r5 = 600(0x258, float:8.41E-43)
            r6 = 1
            r7 = 0
            if (r4 <= r5) goto L_0x0029
            r4 = r6
            goto L_0x002a
        L_0x0029:
            r4 = r7
        L_0x002a:
            boolean r9 = r8.isGestureNav(r9)
            int r5 = r3.screenHeightDp
            int r3 = r3.screenWidthDp
            if (r5 <= r3) goto L_0x0035
            goto L_0x0036
        L_0x0035:
            r6 = r7
        L_0x0036:
            if (r4 == 0) goto L_0x003a
        L_0x0038:
            r3 = r7
            goto L_0x004b
        L_0x003a:
            if (r6 == 0) goto L_0x0043
            java.lang.String r3 = "navigation_bar_height"
            int r3 = r8.getDimenByName(r2, r3)
            goto L_0x004b
        L_0x0043:
            if (r9 == 0) goto L_0x0038
            java.lang.String r3 = "navigation_bar_height_landscape"
            int r3 = r8.getDimenByName(r2, r3)
        L_0x004b:
            int r4 = r1.left
            int r5 = r1.top
            int r1 = r1.right
            android.graphics.Insets r1 = android.graphics.Insets.of(r4, r5, r1, r3)
            int r3 = android.view.WindowInsets.Type.navigationBars()
            r0.setInsets(r3, r1)
            int r3 = android.view.WindowInsets.Type.navigationBars()
            r0.setInsetsIgnoringVisibility(r3, r1)
            int r1 = android.view.WindowInsets.Type.statusBars()
            android.graphics.Insets r1 = r10.getInsets(r1)
            if (r6 == 0) goto L_0x0070
            java.lang.String r3 = "status_bar_height_portrait"
            goto L_0x0072
        L_0x0070:
            java.lang.String r3 = "status_bar_height_landscape"
        L_0x0072:
            java.lang.String r4 = "status_bar_height"
            int r2 = r8.getDimenByName(r2, r3, r4)
            int r3 = r1.left
            int r4 = r1.top
            int r2 = java.lang.Math.max(r4, r2)
            int r4 = r1.right
            int r1 = r1.bottom
            android.graphics.Insets r1 = android.graphics.Insets.of(r3, r2, r4, r1)
            int r2 = android.view.WindowInsets.Type.statusBars()
            r0.setInsets(r2, r1)
            int r2 = android.view.WindowInsets.Type.statusBars()
            r0.setInsetsIgnoringVisibility(r2, r1)
            if (r9 == 0) goto L_0x00b1
            int r9 = android.view.WindowInsets.Type.tappableElement()
            android.graphics.Insets r9 = r10.getInsets(r9)
            int r10 = r9.left
            int r1 = r9.top
            int r9 = r9.right
            android.graphics.Insets r9 = android.graphics.Insets.of(r10, r1, r9, r7)
            int r10 = android.view.WindowInsets.Type.tappableElement()
            r0.setInsets(r10, r9)
        L_0x00b1:
            android.view.WindowInsets r9 = r0.build()
            int r10 = android.view.WindowInsets.Type.systemBars()
            int r0 = android.view.WindowInsets.Type.displayCutout()
            r10 = r10 | r0
            android.graphics.Insets r10 = r9.getInsetsIgnoringVisibility(r10)
            int r0 = r10.left
            int r1 = r10.top
            int r2 = r10.right
            int r10 = r10.bottom
            r11.set(r0, r1, r2, r10)
            return r9
        L_0x00ce:
            int r9 = r10.getSystemWindowInsetLeft()
            int r0 = r10.getSystemWindowInsetTop()
            int r1 = r10.getSystemWindowInsetRight()
            int r2 = r10.getSystemWindowInsetBottom()
            r11.set(r9, r0, r1, r2)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.window.WindowManagerProxy.normalizeWindowInsets(android.content.Context, android.view.WindowInsets, android.graphics.Rect):android.view.WindowInsets");
    }

    /* access modifiers changed from: protected */
    public boolean isInternalDisplay(Display display) {
        return display.getDisplayId() == 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x009b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.launcher3.util.WindowBounds[] estimateWindowBounds(android.content.Context r17, com.android.launcher3.util.window.CachedDisplayInfo r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r18
            android.content.res.Resources r2 = r17.getResources()
            android.content.res.Configuration r2 = r2.getConfiguration()
            int r2 = r2.densityDpi
            int r3 = r1.rotation
            android.graphics.Rect r4 = r1.cutout
            android.graphics.Point r5 = r1.size
            int r5 = r5.x
            android.graphics.Point r6 = r1.size
            int r6 = r6.y
            int r5 = java.lang.Math.min(r5, r6)
            float r5 = (float) r5
            float r2 = com.android.launcher3.Utilities.dpiFromPx(r5, r2)
            int r2 = (int) r2
            android.content.res.Configuration r5 = new android.content.res.Configuration
            r5.<init>()
            r5.smallestScreenWidthDp = r2
            r6 = r17
            android.content.Context r5 = r6.createConfigurationContext(r5)
            android.content.res.Resources r5 = r5.getResources()
            r7 = 1
            r8 = 0
            r9 = 600(0x258, float:8.41E-43)
            if (r2 < r9) goto L_0x003d
            r2 = r7
            goto L_0x003e
        L_0x003d:
            r2 = r8
        L_0x003e:
            if (r2 != 0) goto L_0x004c
            boolean r9 = com.android.launcher3.Utilities.ATLEAST_R
            if (r9 == 0) goto L_0x004b
            boolean r6 = r16.isGestureNav(r17)
            if (r6 == 0) goto L_0x004b
            goto L_0x004c
        L_0x004b:
            r7 = r8
        L_0x004c:
            java.lang.String r6 = "status_bar_height_portrait"
            java.lang.String r9 = "status_bar_height"
            int r6 = r0.getDimenByName(r5, r6, r9)
            java.lang.String r10 = "status_bar_height_landscape"
            int r9 = r0.getDimenByName(r5, r10, r9)
            r10 = 2131166000(0x7f070330, float:1.7946233E38)
            if (r2 == 0) goto L_0x006a
            boolean r11 = r0.mTaskbarDrawnInProcess
            if (r11 == 0) goto L_0x0065
            r11 = r8
            goto L_0x0070
        L_0x0065:
            int r11 = r5.getDimensionPixelSize(r10)
            goto L_0x0070
        L_0x006a:
            java.lang.String r11 = "navigation_bar_height"
            int r11 = r0.getDimenByName(r5, r11)
        L_0x0070:
            if (r2 == 0) goto L_0x007c
            boolean r2 = r0.mTaskbarDrawnInProcess
            if (r2 == 0) goto L_0x0077
            goto L_0x0085
        L_0x0077:
            int r2 = r5.getDimensionPixelSize(r10)
            goto L_0x0086
        L_0x007c:
            if (r7 == 0) goto L_0x0085
            java.lang.String r2 = "navigation_bar_height_landscape"
            int r2 = r0.getDimenByName(r5, r2)
            goto L_0x0086
        L_0x0085:
            r2 = r8
        L_0x0086:
            if (r7 == 0) goto L_0x008a
            r5 = r8
            goto L_0x0090
        L_0x008a:
            java.lang.String r7 = "navigation_bar_width"
            int r5 = r0.getDimenByName(r5, r7)
        L_0x0090:
            r7 = 4
            com.android.launcher3.util.WindowBounds[] r10 = new com.android.launcher3.util.WindowBounds[r7]
            android.graphics.Point r12 = new android.graphics.Point
            r12.<init>()
            r13 = r8
        L_0x0099:
            if (r13 >= r7) goto L_0x0102
            int r14 = com.android.launcher3.util.RotationUtils.deltaRotation(r3, r13)
            android.graphics.Point r15 = r1.size
            int r15 = r15.x
            android.graphics.Point r7 = r1.size
            int r7 = r7.y
            r12.set(r15, r7)
            com.android.launcher3.util.RotationUtils.rotateSize(r12, r14)
            android.graphics.Rect r7 = new android.graphics.Rect
            int r15 = r12.x
            int r0 = r12.y
            r7.<init>(r8, r8, r15, r0)
            int r0 = r12.y
            int r15 = r12.x
            if (r0 <= r15) goto L_0x00bf
            r0 = r6
            r15 = r11
            goto L_0x00c2
        L_0x00bf:
            r15 = r2
            r8 = r5
            r0 = r9
        L_0x00c2:
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>(r4)
            com.android.launcher3.util.RotationUtils.rotateRect(r1, r14)
            int r14 = r1.top
            int r0 = java.lang.Math.max(r14, r0)
            r1.top = r0
            int r0 = r1.bottom
            int r0 = java.lang.Math.max(r0, r15)
            r1.bottom = r0
            r0 = 3
            if (r13 == r0) goto L_0x00ea
            r0 = 2
            if (r13 != r0) goto L_0x00e1
            goto L_0x00ea
        L_0x00e1:
            int r0 = r1.right
            int r0 = java.lang.Math.max(r0, r8)
            r1.right = r0
            goto L_0x00f2
        L_0x00ea:
            int r0 = r1.left
            int r0 = java.lang.Math.max(r0, r8)
            r1.left = r0
        L_0x00f2:
            com.android.launcher3.util.WindowBounds r0 = new com.android.launcher3.util.WindowBounds
            r0.<init>(r7, r1, r13)
            r10[r13] = r0
            int r13 = r13 + 1
            r0 = r16
            r1 = r18
            r7 = 4
            r8 = 0
            goto L_0x0099
        L_0x0102:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.window.WindowManagerProxy.estimateWindowBounds(android.content.Context, com.android.launcher3.util.window.CachedDisplayInfo):com.android.launcher3.util.WindowBounds[]");
    }

    /* access modifiers changed from: protected */
    public int getDimenByName(Resources resources, String str) {
        return ResourceUtils.getDimenByName(str, resources, 0);
    }

    /* access modifiers changed from: protected */
    public int getDimenByName(Resources resources, String str, String str2) {
        int dimenByName = ResourceUtils.getDimenByName(str, resources, -1);
        return dimenByName > -1 ? dimenByName : getDimenByName(resources, str2);
    }

    /* access modifiers changed from: protected */
    public boolean isGestureNav(Context context) {
        return ResourceUtils.getIntegerByName("config_navBarInteractionMode", context.getResources(), -1) == 2;
    }

    public CachedDisplayInfo getDisplayInfo(Context context, Display display) {
        int rotation = getRotation(context);
        Rect rect = new Rect();
        Point point = new Point();
        if (Utilities.ATLEAST_S) {
            WindowMetrics maximumWindowMetrics = ((WindowManager) context.getSystemService(WindowManager.class)).getMaximumWindowMetrics();
            DisplayCutout displayCutout = maximumWindowMetrics.getWindowInsets().getDisplayCutout();
            if (displayCutout != null) {
                rect.set(displayCutout.getSafeInsetLeft(), displayCutout.getSafeInsetTop(), displayCutout.getSafeInsetRight(), displayCutout.getSafeInsetBottom());
            }
            point.set(maximumWindowMetrics.getBounds().right, maximumWindowMetrics.getBounds().bottom);
        } else {
            display.getRealSize(point);
        }
        return new CachedDisplayInfo(getDisplayId(display), point, rotation, rect);
    }

    /* access modifiers changed from: protected */
    public String getDisplayId(Display display) {
        return Integer.toString(display.getDisplayId());
    }

    /* JADX WARNING: Removed duplicated region for block: B:7:0x000c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getRotation(android.content.Context r2) {
        /*
            r1 = this;
            boolean r0 = com.android.launcher3.Utilities.ATLEAST_R
            if (r0 == 0) goto L_0x0009
            android.view.Display r0 = r2.getDisplay()     // Catch:{ UnsupportedOperationException -> 0x0009 }
            goto L_0x000a
        L_0x0009:
            r0 = 0
        L_0x000a:
            if (r0 != 0) goto L_0x0019
            java.lang.Class<android.hardware.display.DisplayManager> r0 = android.hardware.display.DisplayManager.class
            java.lang.Object r2 = r2.getSystemService(r0)
            android.hardware.display.DisplayManager r2 = (android.hardware.display.DisplayManager) r2
            r0 = 0
            android.view.Display r0 = r2.getDisplay(r0)
        L_0x0019:
            int r2 = r0.getRotation()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.window.WindowManagerProxy.getRotation(android.content.Context):int");
    }

    /* access modifiers changed from: protected */
    public Display[] getDisplays(Context context) {
        return ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplays();
    }
}
