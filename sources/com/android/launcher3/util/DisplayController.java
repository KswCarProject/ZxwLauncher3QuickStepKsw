package com.android.launcher3.util;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.window.CachedDisplayInfo;
import com.android.launcher3.util.window.WindowManagerProxy;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class DisplayController implements ComponentCallbacks, SafeCloseable {
    private static final String ACTION_OVERLAY_CHANGED = "android.intent.action.OVERLAY_CHANGED";
    public static final int CHANGE_ACTIVE_SCREEN = 1;
    public static final int CHANGE_ALL = 31;
    public static final int CHANGE_DENSITY = 4;
    public static final int CHANGE_NAVIGATION_MODE = 16;
    public static final int CHANGE_ROTATION = 2;
    public static final int CHANGE_SUPPORTED_BOUNDS = 8;
    public static final MainThreadInitializedObject<DisplayController> INSTANCE = new MainThreadInitializedObject<>($$Lambda$DisplayController$QjdXHmv721WSoJWMI3oZlD7FY.INSTANCE);
    private static final String NAV_BAR_INTERACTION_MODE_RES_NAME = "config_navBarInteractionMode";
    private static final String TAG = "DisplayController";
    private static final String TARGET_OVERLAY_PACKAGE = "android";
    private final Context mContext;
    private final DisplayManager mDM;
    private boolean mDestroyed;
    private Info mInfo;
    private final ArrayList<DisplayInfoChangeListener> mListeners = new ArrayList<>();
    private DisplayInfoChangeListener mPriorityListener;
    private final SimpleBroadcastReceiver mReceiver;
    private final Context mWindowContext;

    public interface DisplayInfoChangeListener {
        void onDisplayInfoChanged(Context context, Info info, int i);
    }

    /* renamed from: lambda$QjdXHmv721WSoJW-MI-3oZlD7FY  reason: not valid java name */
    public static /* synthetic */ DisplayController m72lambda$QjdXHmv721WSoJWMI3oZlD7FY(Context context) {
        return new DisplayController(context);
    }

    public final void onLowMemory() {
    }

    private DisplayController(Context context) {
        SimpleBroadcastReceiver simpleBroadcastReceiver = new SimpleBroadcastReceiver(new Consumer() {
            public final void accept(Object obj) {
                DisplayController.this.onIntent((Intent) obj);
            }
        });
        this.mReceiver = simpleBroadcastReceiver;
        this.mDestroyed = false;
        this.mContext = context;
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mDM = displayManager;
        Display display = displayManager.getDisplay(0);
        if (Utilities.ATLEAST_S) {
            Context createWindowContext = context.createWindowContext(display, 2, (Bundle) null);
            this.mWindowContext = createWindowContext;
            createWindowContext.registerComponentCallbacks(this);
        } else {
            this.mWindowContext = null;
            simpleBroadcastReceiver.register(context, "android.intent.action.CONFIGURATION_CHANGED");
        }
        context.registerReceiver(simpleBroadcastReceiver, PackageManagerHelper.getPackageFilter(TARGET_OVERLAY_PACKAGE, ACTION_OVERLAY_CHANGED));
        WindowManagerProxy windowManagerProxy = WindowManagerProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        this.mInfo = new Info(getDisplayInfoContext(display), display, windowManagerProxy, windowManagerProxy.estimateInternalDisplayBounds(context));
    }

    public static NavigationMode getNavigationMode(Context context) {
        return INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo().navigationMode;
    }

    public void close() {
        this.mDestroyed = true;
        Context context = this.mWindowContext;
        if (context != null) {
            context.unregisterComponentCallbacks(this);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003c, code lost:
        if (com.android.launcher3.util.DisplayController.Info.access$000(r4.mInfo) == r5.densityDpi) goto L_0x003f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onIntent(android.content.Intent r5) {
        /*
            r4 = this;
            boolean r0 = r4.mDestroyed
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            java.lang.String r0 = r5.getAction()
            java.lang.String r1 = "android.intent.action.OVERLAY_CHANGED"
            boolean r0 = r1.equals(r0)
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x0014
            goto L_0x0040
        L_0x0014:
            java.lang.String r5 = r5.getAction()
            java.lang.String r0 = "android.intent.action.CONFIGURATION_CHANGED"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x003f
            android.content.Context r5 = r4.mContext
            android.content.res.Resources r5 = r5.getResources()
            android.content.res.Configuration r5 = r5.getConfiguration()
            com.android.launcher3.util.DisplayController$Info r0 = r4.mInfo
            float r0 = r0.fontScale
            float r3 = r5.fontScale
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0040
            com.android.launcher3.util.DisplayController$Info r0 = r4.mInfo
            int r0 = r0.densityDpi
            int r5 = r5.densityDpi
            if (r0 == r5) goto L_0x003f
            goto L_0x0040
        L_0x003f:
            r1 = r2
        L_0x0040:
            if (r1 == 0) goto L_0x0054
            java.lang.String r5 = "DisplayController"
            java.lang.String r0 = "Configuration changed, notifying listeners"
            android.util.Log.d(r5, r0)
            android.hardware.display.DisplayManager r5 = r4.mDM
            android.view.Display r5 = r5.getDisplay(r2)
            if (r5 == 0) goto L_0x0054
            r4.handleInfoChange(r5)
        L_0x0054:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.DisplayController.onIntent(android.content.Intent):void");
    }

    public final void onConfigurationChanged(Configuration configuration) {
        Display display = this.mWindowContext.getDisplay();
        if (configuration.densityDpi != this.mInfo.densityDpi || configuration.fontScale != this.mInfo.fontScale || display.getRotation() != this.mInfo.rotation || !this.mInfo.mScreenSizeDp.equals(new PortraitSize(configuration.screenHeightDp, configuration.screenWidthDp))) {
            handleInfoChange(display);
        }
    }

    public void setPriorityListener(DisplayInfoChangeListener displayInfoChangeListener) {
        this.mPriorityListener = displayInfoChangeListener;
    }

    public void addChangeListener(DisplayInfoChangeListener displayInfoChangeListener) {
        this.mListeners.add(displayInfoChangeListener);
    }

    public void removeChangeListener(DisplayInfoChangeListener displayInfoChangeListener) {
        this.mListeners.remove(displayInfoChangeListener);
    }

    public Info getInfo() {
        return this.mInfo;
    }

    private Context getDisplayInfoContext(Display display) {
        return Utilities.ATLEAST_S ? this.mWindowContext : this.mContext.createDisplayContext(display);
    }

    private void handleInfoChange(Display display) {
        Point point;
        WindowManagerProxy windowManagerProxy = WindowManagerProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext);
        Info info = this.mInfo;
        Context displayInfoContext = getDisplayInfoContext(display);
        Info info2 = new Info(displayInfoContext, display, windowManagerProxy, info.mPerDisplayBounds);
        if (!(info2.densityDpi == info.densityDpi && info2.fontScale == info.fontScale && info2.navigationMode == info.navigationMode)) {
            info2 = new Info(displayInfoContext, display, windowManagerProxy, windowManagerProxy.estimateInternalDisplayBounds(displayInfoContext));
        }
        boolean z = !info2.displayId.equals(info.displayId);
        if (info2.rotation != info.rotation) {
            z |= true;
        }
        if (!(info2.densityDpi == info.densityDpi && info2.fontScale == info.fontScale)) {
            z |= true;
        }
        if (info2.navigationMode != info.navigationMode) {
            z |= true;
        }
        if (!info2.supportedBounds.equals(info.supportedBounds)) {
            z |= true;
            Point point2 = info2.currentSize;
            Pair pair = (Pair) info.mPerDisplayBounds.get(info2.displayId);
            if (pair == null) {
                point = null;
            } else {
                point = ((CachedDisplayInfo) pair.first).size;
            }
            if (info2.supportedBounds.size() != info.supportedBounds.size()) {
                Log.e("b/198965093", "Inconsistent number of displays\ndisplay state: " + display.getState() + "\noldInfo.supportedBounds: " + info.supportedBounds + "\nnewInfo.supportedBounds: " + info2.supportedBounds);
            }
            if (point != null && (!(Math.min(point2.x, point2.y) == Math.min(point.x, point.y) && Math.max(point2.x, point2.y) == Math.max(point.x, point.y)) && display.getState() == 1)) {
                Log.e("b/198965093", "Display size changed while display is off, ignoring change");
                return;
            }
        }
        if (z) {
            this.mInfo = info2;
            Executors.MAIN_EXECUTOR.execute(new Runnable(displayInfoContext, z ? 1 : 0) {
                public final /* synthetic */ Context f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DisplayController.this.lambda$handleInfoChange$0$DisplayController(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: notifyChange */
    public void lambda$handleInfoChange$0$DisplayController(Context context, int i) {
        DisplayInfoChangeListener displayInfoChangeListener = this.mPriorityListener;
        if (displayInfoChangeListener != null) {
            displayInfoChangeListener.onDisplayInfoChanged(context, this.mInfo, i);
        }
        int size = this.mListeners.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mListeners.get(i2).onDisplayInfoChanged(context, this.mInfo, i);
        }
    }

    public static class Info {
        public final Point currentSize;
        public final Rect cutout;
        /* access modifiers changed from: private */
        public final int densityDpi;
        public final String displayId;
        public final float fontScale;
        /* access modifiers changed from: private */
        public final ArrayMap<String, Pair<CachedDisplayInfo, WindowBounds[]>> mPerDisplayBounds;
        /* access modifiers changed from: private */
        public final PortraitSize mScreenSizeDp;
        public final NavigationMode navigationMode;
        public final int rotation;
        public final Set<WindowBounds> supportedBounds;

        public Info(Context context, Display display) {
            this(context, display, new WindowManagerProxy(), new ArrayMap());
        }

        public Info(Context context, Display display, WindowManagerProxy windowManagerProxy, ArrayMap<String, Pair<CachedDisplayInfo, WindowBounds[]>> arrayMap) {
            ArraySet arraySet = new ArraySet();
            this.supportedBounds = arraySet;
            ArrayMap<String, Pair<CachedDisplayInfo, WindowBounds[]>> arrayMap2 = new ArrayMap<>();
            this.mPerDisplayBounds = arrayMap2;
            CachedDisplayInfo displayInfo = windowManagerProxy.getDisplayInfo(context, display);
            this.rotation = displayInfo.rotation;
            Point point = displayInfo.size;
            this.currentSize = point;
            String str = displayInfo.id;
            this.displayId = str;
            this.cutout = displayInfo.cutout;
            Configuration configuration = context.getResources().getConfiguration();
            this.fontScale = configuration.fontScale;
            this.densityDpi = configuration.densityDpi;
            this.mScreenSizeDp = new PortraitSize(configuration.screenHeightDp, configuration.screenWidthDp);
            this.navigationMode = DisplayController.parseNavigationMode(context);
            arrayMap2.putAll(arrayMap);
            Pair pair = arrayMap2.get(str);
            WindowBounds realBounds = windowManagerProxy.getRealBounds(context, display, displayInfo);
            if (pair == null) {
                arraySet.add(realBounds);
            } else if (!realBounds.equals(((WindowBounds[]) pair.second)[displayInfo.rotation])) {
                WindowBounds[] windowBoundsArr = new WindowBounds[4];
                System.arraycopy(pair.second, 0, windowBoundsArr, 0, 4);
                windowBoundsArr[displayInfo.rotation] = realBounds;
                arrayMap2.put(str, Pair.create(displayInfo.normalize(), windowBoundsArr));
            }
            arrayMap2.values().forEach(new Consumer() {
                public final void accept(Object obj) {
                    DisplayController.Info.this.lambda$new$0$DisplayController$Info((Pair) obj);
                }
            });
            Log.d("b/211775278", "displayId: " + str + ", currentSize: " + point);
            Log.d("b/211775278", "perDisplayBounds: " + arrayMap2);
        }

        public /* synthetic */ void lambda$new$0$DisplayController$Info(Pair pair) {
            Collections.addAll(this.supportedBounds, (WindowBounds[]) pair.second);
        }

        public boolean isTablet(WindowBounds windowBounds) {
            return smallestSizeDp(windowBounds) >= 600.0f;
        }

        public float smallestSizeDp(WindowBounds windowBounds) {
            return Utilities.dpiFromPx((float) Math.min(windowBounds.bounds.width(), windowBounds.bounds.height()), this.densityDpi);
        }

        public int getDensityDpi() {
            return this.densityDpi;
        }
    }

    public void dump(PrintWriter printWriter) {
        Info info = this.mInfo;
        printWriter.println("DisplayController.Info:");
        printWriter.println("  id=" + info.displayId);
        printWriter.println("  rotation=" + info.rotation);
        printWriter.println("  fontScale=" + info.fontScale);
        printWriter.println("  densityDpi=" + info.densityDpi);
        printWriter.println("  navigationMode=" + info.navigationMode.name());
        printWriter.println("  currentSize=" + info.currentSize);
        printWriter.println("  supportedBounds=" + info.supportedBounds);
    }

    public static class PortraitSize {
        public final int height;
        public final int width;

        public PortraitSize(int i, int i2) {
            this.width = Math.min(i, i2);
            this.height = Math.max(i, i2);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            PortraitSize portraitSize = (PortraitSize) obj;
            if (this.width == portraitSize.width && this.height == portraitSize.height) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Integer.valueOf(this.width), Integer.valueOf(this.height)});
        }
    }

    public enum NavigationMode {
        THREE_BUTTONS(false, 0, StatsLogManager.LauncherEvent.LAUNCHER_NAVIGATION_MODE_3_BUTTON),
        TWO_BUTTONS(true, 1, StatsLogManager.LauncherEvent.LAUNCHER_NAVIGATION_MODE_2_BUTTON),
        NO_BUTTON(true, 2, StatsLogManager.LauncherEvent.LAUNCHER_NAVIGATION_MODE_GESTURE_BUTTON);
        
        public final boolean hasGestures;
        public final StatsLogManager.LauncherEvent launcherEvent;
        public final int resValue;

        private NavigationMode(boolean z, int i, StatsLogManager.LauncherEvent launcherEvent2) {
            this.hasGestures = z;
            this.resValue = i;
            this.launcherEvent = launcherEvent2;
        }
    }

    /* access modifiers changed from: private */
    public static NavigationMode parseNavigationMode(Context context) {
        int integerByName = ResourceUtils.getIntegerByName(NAV_BAR_INTERACTION_MODE_RES_NAME, context.getResources(), -1);
        if (integerByName == -1) {
            Log.e(TAG, "Failed to get system resource ID. Incompatible framework version?");
        } else {
            for (NavigationMode navigationMode : NavigationMode.values()) {
                if (navigationMode.resValue == integerByName) {
                    return navigationMode;
                }
            }
        }
        return Utilities.ATLEAST_S ? NavigationMode.NO_BUTTON : NavigationMode.THREE_BUTTONS;
    }
}
