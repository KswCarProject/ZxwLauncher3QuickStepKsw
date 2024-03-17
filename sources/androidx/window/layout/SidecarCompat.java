package androidx.window.layout;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.slice.core.SliceHints;
import androidx.window.core.SpecificationComputer;
import androidx.window.core.Version;
import androidx.window.layout.ExtensionInterfaceCompat;
import androidx.window.sidecar.SidecarDeviceState;
import androidx.window.sidecar.SidecarInterface;
import androidx.window.sidecar.SidecarProvider;
import androidx.window.sidecar.SidecarWindowLayoutInfo;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0000\u0018\u0000 !2\u00020\u0001:\u0005!\"#$%B\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u001b\b\u0007\u0012\n\b\u0001\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\fH\u0007J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0016\u001a\u00020\fH\u0016J\u0010\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u0016\u001a\u00020\fH\u0016J\u0016\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\fJ\u0010\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u0016\u001a\u00020\fH\u0002J\u0010\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0010\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u0016\u001a\u00020\fH\u0002J\b\u0010\u001f\u001a\u00020 H\u0017R\u001a\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000bX\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u000e¢\u0006\u0002\n\u0000R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0007\u001a\u00020\bX\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\f0\u000bX\u0004¢\u0006\u0002\n\u0000¨\u0006&"}, d2 = {"Landroidx/window/layout/SidecarCompat;", "Landroidx/window/layout/ExtensionInterfaceCompat;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "sidecar", "Landroidx/window/sidecar/SidecarInterface;", "sidecarAdapter", "Landroidx/window/layout/SidecarAdapter;", "(Landroidx/window/sidecar/SidecarInterface;Landroidx/window/layout/SidecarAdapter;)V", "componentCallbackMap", "", "Landroid/app/Activity;", "Landroid/content/ComponentCallbacks;", "extensionCallback", "Landroidx/window/layout/ExtensionInterfaceCompat$ExtensionCallbackInterface;", "getSidecar", "()Landroidx/window/sidecar/SidecarInterface;", "windowListenerRegisteredContexts", "Landroid/os/IBinder;", "getWindowLayoutInfo", "Landroidx/window/layout/WindowLayoutInfo;", "activity", "onWindowLayoutChangeListenerAdded", "", "onWindowLayoutChangeListenerRemoved", "register", "windowToken", "registerConfigurationChangeListener", "setExtensionCallback", "unregisterComponentCallback", "validateExtensionInterface", "", "Companion", "DistinctElementCallback", "DistinctSidecarElementCallback", "FirstAttachAdapter", "TranslatingCallback", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: SidecarCompat.kt */
public final class SidecarCompat implements ExtensionInterfaceCompat {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private static final String TAG = "SidecarCompat";
    private final Map<Activity, ComponentCallbacks> componentCallbackMap;
    /* access modifiers changed from: private */
    public ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallback;
    private final SidecarInterface sidecar;
    /* access modifiers changed from: private */
    public final SidecarAdapter sidecarAdapter;
    /* access modifiers changed from: private */
    public final Map<IBinder, Activity> windowListenerRegisteredContexts;

    public SidecarCompat(SidecarInterface sidecarInterface, SidecarAdapter sidecarAdapter2) {
        Intrinsics.checkNotNullParameter(sidecarAdapter2, "sidecarAdapter");
        this.sidecar = sidecarInterface;
        this.sidecarAdapter = sidecarAdapter2;
        this.windowListenerRegisteredContexts = new LinkedHashMap();
        this.componentCallbackMap = new LinkedHashMap();
    }

    public final SidecarInterface getSidecar() {
        return this.sidecar;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public SidecarCompat(Context context) {
        this(Companion.getSidecarCompat$window_release(context), new SidecarAdapter((SpecificationComputer.VerificationMode) null, 1, (DefaultConstructorMarker) null));
        Intrinsics.checkNotNullParameter(context, "context");
    }

    public void setExtensionCallback(ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallbackInterface) {
        Intrinsics.checkNotNullParameter(extensionCallbackInterface, "extensionCallback");
        this.extensionCallback = new DistinctElementCallback(extensionCallbackInterface);
        SidecarInterface sidecarInterface = this.sidecar;
        if (sidecarInterface != null) {
            sidecarInterface.setSidecarCallback(new DistinctSidecarElementCallback(this.sidecarAdapter, new TranslatingCallback(this)));
        }
    }

    public final WindowLayoutInfo getWindowLayoutInfo(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, SliceHints.HINT_ACTIVITY);
        IBinder activityWindowToken$window_release = Companion.getActivityWindowToken$window_release(activity);
        if (activityWindowToken$window_release == null) {
            return new WindowLayoutInfo(CollectionsKt.emptyList());
        }
        SidecarInterface sidecarInterface = this.sidecar;
        SidecarDeviceState sidecarDeviceState = null;
        SidecarWindowLayoutInfo windowLayoutInfo = sidecarInterface == null ? null : sidecarInterface.getWindowLayoutInfo(activityWindowToken$window_release);
        SidecarAdapter sidecarAdapter2 = this.sidecarAdapter;
        SidecarInterface sidecarInterface2 = this.sidecar;
        if (sidecarInterface2 != null) {
            sidecarDeviceState = sidecarInterface2.getDeviceState();
        }
        if (sidecarDeviceState == null) {
            sidecarDeviceState = new SidecarDeviceState();
        }
        return sidecarAdapter2.translate(windowLayoutInfo, sidecarDeviceState);
    }

    public void onWindowLayoutChangeListenerAdded(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, SliceHints.HINT_ACTIVITY);
        IBinder activityWindowToken$window_release = Companion.getActivityWindowToken$window_release(activity);
        if (activityWindowToken$window_release != null) {
            register(activityWindowToken$window_release, activity);
            return;
        }
        activity.getWindow().getDecorView().addOnAttachStateChangeListener(new FirstAttachAdapter(this, activity));
    }

    public final void register(IBinder iBinder, Activity activity) {
        SidecarInterface sidecarInterface;
        Intrinsics.checkNotNullParameter(iBinder, "windowToken");
        Intrinsics.checkNotNullParameter(activity, SliceHints.HINT_ACTIVITY);
        this.windowListenerRegisteredContexts.put(iBinder, activity);
        SidecarInterface sidecarInterface2 = this.sidecar;
        if (sidecarInterface2 != null) {
            sidecarInterface2.onWindowLayoutChangeListenerAdded(iBinder);
        }
        if (this.windowListenerRegisteredContexts.size() == 1 && (sidecarInterface = this.sidecar) != null) {
            sidecarInterface.onDeviceStateListenersChanged(false);
        }
        ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallbackInterface = this.extensionCallback;
        if (extensionCallbackInterface != null) {
            extensionCallbackInterface.onWindowLayoutChanged(activity, getWindowLayoutInfo(activity));
        }
        registerConfigurationChangeListener(activity);
    }

    private final void registerConfigurationChangeListener(Activity activity) {
        if (this.componentCallbackMap.get(activity) == null) {
            SidecarCompat$registerConfigurationChangeListener$configChangeObserver$1 sidecarCompat$registerConfigurationChangeListener$configChangeObserver$1 = new SidecarCompat$registerConfigurationChangeListener$configChangeObserver$1(this, activity);
            this.componentCallbackMap.put(activity, sidecarCompat$registerConfigurationChangeListener$configChangeObserver$1);
            activity.registerComponentCallbacks(sidecarCompat$registerConfigurationChangeListener$configChangeObserver$1);
        }
    }

    public void onWindowLayoutChangeListenerRemoved(Activity activity) {
        SidecarInterface sidecarInterface;
        Intrinsics.checkNotNullParameter(activity, SliceHints.HINT_ACTIVITY);
        IBinder activityWindowToken$window_release = Companion.getActivityWindowToken$window_release(activity);
        if (activityWindowToken$window_release != null) {
            SidecarInterface sidecarInterface2 = this.sidecar;
            if (sidecarInterface2 != null) {
                sidecarInterface2.onWindowLayoutChangeListenerRemoved(activityWindowToken$window_release);
            }
            unregisterComponentCallback(activity);
            boolean z = this.windowListenerRegisteredContexts.size() == 1;
            this.windowListenerRegisteredContexts.remove(activityWindowToken$window_release);
            if (z && (sidecarInterface = this.sidecar) != null) {
                sidecarInterface.onDeviceStateListenersChanged(true);
            }
        }
    }

    private final void unregisterComponentCallback(Activity activity) {
        activity.unregisterComponentCallbacks(this.componentCallbackMap.get(activity));
        this.componentCallbackMap.remove(activity);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: int} */
    /* JADX WARNING: type inference failed for: r1v3, types: [java.util.List] */
    /* JADX WARNING: Can't wrap try/catch for region: R(10:58|59|60|61|69|70|71|72|73|(2:75|(2:77|97)(2:78|79))(2:80|81)) */
    /* JADX WARNING: Code restructure failed: missing block: B:98:?, code lost:
        return true;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:72:0x0112 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x001e A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0020 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x002c A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0057 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0059 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0065 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0080 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0082 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x008e A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00a9 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00aa A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00b6 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0143 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0154 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x016c A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0178 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0184 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0190 A[Catch:{ NoSuchFieldError -> 0x00bf, all -> 0x019c }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean validateExtensionInterface() {
        /*
            r8 = this;
            r0 = 1
            r1 = 0
            androidx.window.sidecar.SidecarInterface r2 = r8.sidecar     // Catch:{ all -> 0x019c }
            r3 = 0
            if (r2 != 0) goto L_0x0009
        L_0x0007:
            r2 = r3
            goto L_0x001c
        L_0x0009:
            java.lang.Class r2 = r2.getClass()     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x0010
            goto L_0x0007
        L_0x0010:
            java.lang.String r4 = "setSidecarCallback"
            java.lang.Class[] r5 = new java.lang.Class[r0]     // Catch:{ all -> 0x019c }
            java.lang.Class<androidx.window.sidecar.SidecarInterface$SidecarCallback> r6 = androidx.window.sidecar.SidecarInterface.SidecarCallback.class
            r5[r1] = r6     // Catch:{ all -> 0x019c }
            java.lang.reflect.Method r2 = r2.getMethod(r4, r5)     // Catch:{ all -> 0x019c }
        L_0x001c:
            if (r2 != 0) goto L_0x0020
            r2 = r3
            goto L_0x0024
        L_0x0020:
            java.lang.Class r2 = r2.getReturnType()     // Catch:{ all -> 0x019c }
        L_0x0024:
            java.lang.Class r4 = java.lang.Void.TYPE     // Catch:{ all -> 0x019c }
            boolean r4 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2, (java.lang.Object) r4)     // Catch:{ all -> 0x019c }
            if (r4 == 0) goto L_0x0190
            androidx.window.sidecar.SidecarInterface r2 = r8.sidecar     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x0031
            goto L_0x0034
        L_0x0031:
            r2.getDeviceState()     // Catch:{ all -> 0x019c }
        L_0x0034:
            androidx.window.sidecar.SidecarInterface r2 = r8.sidecar     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x0039
            goto L_0x003c
        L_0x0039:
            r2.onDeviceStateListenersChanged(r0)     // Catch:{ all -> 0x019c }
        L_0x003c:
            androidx.window.sidecar.SidecarInterface r2 = r8.sidecar     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x0042
        L_0x0040:
            r2 = r3
            goto L_0x0055
        L_0x0042:
            java.lang.Class r2 = r2.getClass()     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x0049
            goto L_0x0040
        L_0x0049:
            java.lang.String r4 = "getWindowLayoutInfo"
            java.lang.Class[] r5 = new java.lang.Class[r0]     // Catch:{ all -> 0x019c }
            java.lang.Class<android.os.IBinder> r6 = android.os.IBinder.class
            r5[r1] = r6     // Catch:{ all -> 0x019c }
            java.lang.reflect.Method r2 = r2.getMethod(r4, r5)     // Catch:{ all -> 0x019c }
        L_0x0055:
            if (r2 != 0) goto L_0x0059
            r2 = r3
            goto L_0x005d
        L_0x0059:
            java.lang.Class r2 = r2.getReturnType()     // Catch:{ all -> 0x019c }
        L_0x005d:
            java.lang.Class<androidx.window.sidecar.SidecarWindowLayoutInfo> r4 = androidx.window.sidecar.SidecarWindowLayoutInfo.class
            boolean r4 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2, (java.lang.Object) r4)     // Catch:{ all -> 0x019c }
            if (r4 == 0) goto L_0x0184
            androidx.window.sidecar.SidecarInterface r2 = r8.sidecar     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x006b
        L_0x0069:
            r2 = r3
            goto L_0x007e
        L_0x006b:
            java.lang.Class r2 = r2.getClass()     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x0072
            goto L_0x0069
        L_0x0072:
            java.lang.String r4 = "onWindowLayoutChangeListenerAdded"
            java.lang.Class[] r5 = new java.lang.Class[r0]     // Catch:{ all -> 0x019c }
            java.lang.Class<android.os.IBinder> r6 = android.os.IBinder.class
            r5[r1] = r6     // Catch:{ all -> 0x019c }
            java.lang.reflect.Method r2 = r2.getMethod(r4, r5)     // Catch:{ all -> 0x019c }
        L_0x007e:
            if (r2 != 0) goto L_0x0082
            r2 = r3
            goto L_0x0086
        L_0x0082:
            java.lang.Class r2 = r2.getReturnType()     // Catch:{ all -> 0x019c }
        L_0x0086:
            java.lang.Class r4 = java.lang.Void.TYPE     // Catch:{ all -> 0x019c }
            boolean r4 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2, (java.lang.Object) r4)     // Catch:{ all -> 0x019c }
            if (r4 == 0) goto L_0x0178
            androidx.window.sidecar.SidecarInterface r2 = r8.sidecar     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x0094
        L_0x0092:
            r2 = r3
            goto L_0x00a7
        L_0x0094:
            java.lang.Class r2 = r2.getClass()     // Catch:{ all -> 0x019c }
            if (r2 != 0) goto L_0x009b
            goto L_0x0092
        L_0x009b:
            java.lang.String r4 = "onWindowLayoutChangeListenerRemoved"
            java.lang.Class[] r5 = new java.lang.Class[r0]     // Catch:{ all -> 0x019c }
            java.lang.Class<android.os.IBinder> r6 = android.os.IBinder.class
            r5[r1] = r6     // Catch:{ all -> 0x019c }
            java.lang.reflect.Method r2 = r2.getMethod(r4, r5)     // Catch:{ all -> 0x019c }
        L_0x00a7:
            if (r2 != 0) goto L_0x00aa
            goto L_0x00ae
        L_0x00aa:
            java.lang.Class r3 = r2.getReturnType()     // Catch:{ all -> 0x019c }
        L_0x00ae:
            java.lang.Class r2 = java.lang.Void.TYPE     // Catch:{ all -> 0x019c }
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r3, (java.lang.Object) r2)     // Catch:{ all -> 0x019c }
            if (r2 == 0) goto L_0x016c
            androidx.window.sidecar.SidecarDeviceState r2 = new androidx.window.sidecar.SidecarDeviceState     // Catch:{ all -> 0x019c }
            r2.<init>()     // Catch:{ all -> 0x019c }
            r3 = 3
            r2.posture = r3     // Catch:{ NoSuchFieldError -> 0x00bf }
            goto L_0x00f2
        L_0x00bf:
            java.lang.Class<androidx.window.sidecar.SidecarDeviceState> r4 = androidx.window.sidecar.SidecarDeviceState.class
            java.lang.String r5 = "setPosture"
            java.lang.Class[] r6 = new java.lang.Class[r0]     // Catch:{ all -> 0x019c }
            java.lang.Class r7 = java.lang.Integer.TYPE     // Catch:{ all -> 0x019c }
            r6[r1] = r7     // Catch:{ all -> 0x019c }
            java.lang.reflect.Method r4 = r4.getMethod(r5, r6)     // Catch:{ all -> 0x019c }
            java.lang.Object[] r5 = new java.lang.Object[r0]     // Catch:{ all -> 0x019c }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x019c }
            r5[r1] = r6     // Catch:{ all -> 0x019c }
            r4.invoke(r2, r5)     // Catch:{ all -> 0x019c }
            java.lang.Class<androidx.window.sidecar.SidecarDeviceState> r4 = androidx.window.sidecar.SidecarDeviceState.class
            java.lang.String r5 = "getPosture"
            java.lang.Class[] r6 = new java.lang.Class[r1]     // Catch:{ all -> 0x019c }
            java.lang.reflect.Method r4 = r4.getMethod(r5, r6)     // Catch:{ all -> 0x019c }
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ all -> 0x019c }
            java.lang.Object r2 = r4.invoke(r2, r5)     // Catch:{ all -> 0x019c }
            if (r2 == 0) goto L_0x0164
            java.lang.Integer r2 = (java.lang.Integer) r2     // Catch:{ all -> 0x019c }
            int r2 = r2.intValue()     // Catch:{ all -> 0x019c }
            if (r2 != r3) goto L_0x015c
        L_0x00f2:
            androidx.window.sidecar.SidecarDisplayFeature r2 = new androidx.window.sidecar.SidecarDisplayFeature     // Catch:{ all -> 0x019c }
            r2.<init>()     // Catch:{ all -> 0x019c }
            android.graphics.Rect r3 = r2.getRect()     // Catch:{ all -> 0x019c }
            java.lang.String r4 = "displayFeature.rect"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r4)     // Catch:{ all -> 0x019c }
            r2.setRect(r3)     // Catch:{ all -> 0x019c }
            r2.getType()     // Catch:{ all -> 0x019c }
            r2.setType(r0)     // Catch:{ all -> 0x019c }
            androidx.window.sidecar.SidecarWindowLayoutInfo r3 = new androidx.window.sidecar.SidecarWindowLayoutInfo     // Catch:{ all -> 0x019c }
            r3.<init>()     // Catch:{ all -> 0x019c }
            java.util.List r1 = r3.displayFeatures     // Catch:{ NoSuchFieldError -> 0x0112 }
            goto L_0x019d
        L_0x0112:
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x019c }
            r4.<init>()     // Catch:{ all -> 0x019c }
            java.util.List r4 = (java.util.List) r4     // Catch:{ all -> 0x019c }
            r4.add(r2)     // Catch:{ all -> 0x019c }
            java.lang.Class<androidx.window.sidecar.SidecarWindowLayoutInfo> r2 = androidx.window.sidecar.SidecarWindowLayoutInfo.class
            java.lang.String r5 = "setDisplayFeatures"
            java.lang.Class[] r6 = new java.lang.Class[r0]     // Catch:{ all -> 0x019c }
            java.lang.Class<java.util.List> r7 = java.util.List.class
            r6[r1] = r7     // Catch:{ all -> 0x019c }
            java.lang.reflect.Method r2 = r2.getMethod(r5, r6)     // Catch:{ all -> 0x019c }
            java.lang.Object[] r5 = new java.lang.Object[r0]     // Catch:{ all -> 0x019c }
            r5[r1] = r4     // Catch:{ all -> 0x019c }
            r2.invoke(r3, r5)     // Catch:{ all -> 0x019c }
            java.lang.Class<androidx.window.sidecar.SidecarWindowLayoutInfo> r2 = androidx.window.sidecar.SidecarWindowLayoutInfo.class
            java.lang.String r5 = "getDisplayFeatures"
            java.lang.Class[] r6 = new java.lang.Class[r1]     // Catch:{ all -> 0x019c }
            java.lang.reflect.Method r2 = r2.getMethod(r5, r6)     // Catch:{ all -> 0x019c }
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ all -> 0x019c }
            java.lang.Object r2 = r2.invoke(r3, r5)     // Catch:{ all -> 0x019c }
            if (r2 == 0) goto L_0x0154
            java.util.List r2 = (java.util.List) r2     // Catch:{ all -> 0x019c }
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r4, (java.lang.Object) r2)     // Catch:{ all -> 0x019c }
            if (r2 == 0) goto L_0x014c
            goto L_0x019d
        L_0x014c:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ all -> 0x019c }
            java.lang.String r2 = "Invalid display feature getter/setter"
            r0.<init>(r2)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x019c }
        L_0x0154:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x019c }
            java.lang.String r2 = "null cannot be cast to non-null type kotlin.collections.List<androidx.window.sidecar.SidecarDisplayFeature>"
            r0.<init>(r2)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x019c }
        L_0x015c:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ all -> 0x019c }
            java.lang.String r2 = "Invalid device posture getter/setter"
            r0.<init>(r2)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x019c }
        L_0x0164:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException     // Catch:{ all -> 0x019c }
            java.lang.String r2 = "null cannot be cast to non-null type kotlin.Int"
            r0.<init>(r2)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x019c }
        L_0x016c:
            java.lang.NoSuchMethodException r0 = new java.lang.NoSuchMethodException     // Catch:{ all -> 0x019c }
            java.lang.String r2 = "Illegal return type for 'onWindowLayoutChangeListenerRemoved': "
            java.lang.String r2 = kotlin.jvm.internal.Intrinsics.stringPlus(r2, r3)     // Catch:{ all -> 0x019c }
            r0.<init>(r2)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x019c }
        L_0x0178:
            java.lang.NoSuchMethodException r0 = new java.lang.NoSuchMethodException     // Catch:{ all -> 0x019c }
            java.lang.String r3 = "Illegal return type for 'onWindowLayoutChangeListenerAdded': "
            java.lang.String r2 = kotlin.jvm.internal.Intrinsics.stringPlus(r3, r2)     // Catch:{ all -> 0x019c }
            r0.<init>(r2)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x019c }
        L_0x0184:
            java.lang.NoSuchMethodException r0 = new java.lang.NoSuchMethodException     // Catch:{ all -> 0x019c }
            java.lang.String r3 = "Illegal return type for 'getWindowLayoutInfo': "
            java.lang.String r2 = kotlin.jvm.internal.Intrinsics.stringPlus(r3, r2)     // Catch:{ all -> 0x019c }
            r0.<init>(r2)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x019c }
        L_0x0190:
            java.lang.NoSuchMethodException r0 = new java.lang.NoSuchMethodException     // Catch:{ all -> 0x019c }
            java.lang.String r3 = "Illegal return type for 'setSidecarCallback': "
            java.lang.String r2 = kotlin.jvm.internal.Intrinsics.stringPlus(r3, r2)     // Catch:{ all -> 0x019c }
            r0.<init>(r2)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x019c }
        L_0x019c:
            r0 = r1
        L_0x019d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.layout.SidecarCompat.validateExtensionInterface():boolean");
    }

    @Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0010\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016R\u001c\u0010\u0007\u001a\u0010\u0012\f\u0012\n \t*\u0004\u0018\u00010\u00050\u00050\bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Landroidx/window/layout/SidecarCompat$FirstAttachAdapter;", "Landroid/view/View$OnAttachStateChangeListener;", "sidecarCompat", "Landroidx/window/layout/SidecarCompat;", "activity", "Landroid/app/Activity;", "(Landroidx/window/layout/SidecarCompat;Landroid/app/Activity;)V", "activityWeakReference", "Ljava/lang/ref/WeakReference;", "kotlin.jvm.PlatformType", "onViewAttachedToWindow", "", "view", "Landroid/view/View;", "onViewDetachedFromWindow", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: SidecarCompat.kt */
    private static final class FirstAttachAdapter implements View.OnAttachStateChangeListener {
        private final WeakReference<Activity> activityWeakReference;
        private final SidecarCompat sidecarCompat;

        public void onViewDetachedFromWindow(View view) {
            Intrinsics.checkNotNullParameter(view, "view");
        }

        public FirstAttachAdapter(SidecarCompat sidecarCompat2, Activity activity) {
            Intrinsics.checkNotNullParameter(sidecarCompat2, "sidecarCompat");
            Intrinsics.checkNotNullParameter(activity, SliceHints.HINT_ACTIVITY);
            this.sidecarCompat = sidecarCompat2;
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public void onViewAttachedToWindow(View view) {
            Intrinsics.checkNotNullParameter(view, "view");
            view.removeOnAttachStateChangeListener(this);
            Activity activity = (Activity) this.activityWeakReference.get();
            IBinder activityWindowToken$window_release = SidecarCompat.Companion.getActivityWindowToken$window_release(activity);
            if (activity != null && activityWindowToken$window_release != null) {
                this.sidecarCompat.register(activityWindowToken$window_release, activity);
            }
        }
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0017J\u0018\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0017¨\u0006\f"}, d2 = {"Landroidx/window/layout/SidecarCompat$TranslatingCallback;", "Landroidx/window/sidecar/SidecarInterface$SidecarCallback;", "(Landroidx/window/layout/SidecarCompat;)V", "onDeviceStateChanged", "", "newDeviceState", "Landroidx/window/sidecar/SidecarDeviceState;", "onWindowLayoutChanged", "windowToken", "Landroid/os/IBinder;", "newLayout", "Landroidx/window/sidecar/SidecarWindowLayoutInfo;", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: SidecarCompat.kt */
    public final class TranslatingCallback implements SidecarInterface.SidecarCallback {
        final /* synthetic */ SidecarCompat this$0;

        public TranslatingCallback(SidecarCompat sidecarCompat) {
            Intrinsics.checkNotNullParameter(sidecarCompat, "this$0");
            this.this$0 = sidecarCompat;
        }

        public void onDeviceStateChanged(SidecarDeviceState sidecarDeviceState) {
            SidecarInterface sidecar;
            Intrinsics.checkNotNullParameter(sidecarDeviceState, "newDeviceState");
            SidecarCompat sidecarCompat = this.this$0;
            for (Activity activity : this.this$0.windowListenerRegisteredContexts.values()) {
                IBinder activityWindowToken$window_release = SidecarCompat.Companion.getActivityWindowToken$window_release(activity);
                SidecarWindowLayoutInfo sidecarWindowLayoutInfo = null;
                if (!(activityWindowToken$window_release == null || (sidecar = sidecarCompat.getSidecar()) == null)) {
                    sidecarWindowLayoutInfo = sidecar.getWindowLayoutInfo(activityWindowToken$window_release);
                }
                ExtensionInterfaceCompat.ExtensionCallbackInterface access$getExtensionCallback$p = sidecarCompat.extensionCallback;
                if (access$getExtensionCallback$p != null) {
                    access$getExtensionCallback$p.onWindowLayoutChanged(activity, sidecarCompat.sidecarAdapter.translate(sidecarWindowLayoutInfo, sidecarDeviceState));
                }
            }
        }

        public void onWindowLayoutChanged(IBinder iBinder, SidecarWindowLayoutInfo sidecarWindowLayoutInfo) {
            Intrinsics.checkNotNullParameter(iBinder, "windowToken");
            Intrinsics.checkNotNullParameter(sidecarWindowLayoutInfo, "newLayout");
            Activity activity = (Activity) this.this$0.windowListenerRegisteredContexts.get(iBinder);
            if (activity == null) {
                Log.w(SidecarCompat.TAG, "Unable to resolve activity from window token. Missing a call to #onWindowLayoutChangeListenerAdded()?");
                return;
            }
            SidecarAdapter access$getSidecarAdapter$p = this.this$0.sidecarAdapter;
            SidecarInterface sidecar = this.this$0.getSidecar();
            SidecarDeviceState deviceState = sidecar == null ? null : sidecar.getDeviceState();
            if (deviceState == null) {
                deviceState = new SidecarDeviceState();
            }
            WindowLayoutInfo translate = access$getSidecarAdapter$p.translate(sidecarWindowLayoutInfo, deviceState);
            ExtensionInterfaceCompat.ExtensionCallbackInterface access$getExtensionCallback$p = this.this$0.extensionCallback;
            if (access$getExtensionCallback$p != null) {
                access$getExtensionCallback$p.onWindowLayoutChanged(activity, translate);
            }
        }
    }

    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0002\u0010\u0003J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u0007H\u0016R\u001c\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00058\u0002X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0001X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Landroidx/window/layout/SidecarCompat$DistinctElementCallback;", "Landroidx/window/layout/ExtensionInterfaceCompat$ExtensionCallbackInterface;", "callbackInterface", "(Landroidx/window/layout/ExtensionInterfaceCompat$ExtensionCallbackInterface;)V", "activityWindowLayoutInfo", "Ljava/util/WeakHashMap;", "Landroid/app/Activity;", "Landroidx/window/layout/WindowLayoutInfo;", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "onWindowLayoutChanged", "", "activity", "newLayout", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: SidecarCompat.kt */
    private static final class DistinctElementCallback implements ExtensionInterfaceCompat.ExtensionCallbackInterface {
        private final WeakHashMap<Activity, WindowLayoutInfo> activityWindowLayoutInfo = new WeakHashMap<>();
        private final ExtensionInterfaceCompat.ExtensionCallbackInterface callbackInterface;
        private final ReentrantLock lock = new ReentrantLock();

        public DistinctElementCallback(ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallbackInterface) {
            Intrinsics.checkNotNullParameter(extensionCallbackInterface, "callbackInterface");
            this.callbackInterface = extensionCallbackInterface;
        }

        public void onWindowLayoutChanged(Activity activity, WindowLayoutInfo windowLayoutInfo) {
            Intrinsics.checkNotNullParameter(activity, SliceHints.HINT_ACTIVITY);
            Intrinsics.checkNotNullParameter(windowLayoutInfo, "newLayout");
            Lock lock2 = this.lock;
            lock2.lock();
            try {
                if (!Intrinsics.areEqual((Object) windowLayoutInfo, (Object) this.activityWindowLayoutInfo.get(activity))) {
                    WindowLayoutInfo put = this.activityWindowLayoutInfo.put(activity, windowLayoutInfo);
                    lock2.unlock();
                    this.callbackInterface.onWindowLayoutChanged(activity, windowLayoutInfo);
                }
            } finally {
                lock2.unlock();
            }
        }
    }

    @Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0001¢\u0006\u0002\u0010\u0005J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0007H\u0016J\u0018\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\rH\u0016R\u000e\u0010\u0004\u001a\u00020\u0001X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0002@\u0002X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000b8\u0002X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Landroidx/window/layout/SidecarCompat$DistinctSidecarElementCallback;", "Landroidx/window/sidecar/SidecarInterface$SidecarCallback;", "sidecarAdapter", "Landroidx/window/layout/SidecarAdapter;", "callbackInterface", "(Landroidx/window/layout/SidecarAdapter;Landroidx/window/sidecar/SidecarInterface$SidecarCallback;)V", "lastDeviceState", "Landroidx/window/sidecar/SidecarDeviceState;", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "mActivityWindowLayoutInfo", "Ljava/util/WeakHashMap;", "Landroid/os/IBinder;", "Landroidx/window/sidecar/SidecarWindowLayoutInfo;", "onDeviceStateChanged", "", "newDeviceState", "onWindowLayoutChanged", "token", "newLayout", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: SidecarCompat.kt */
    private static final class DistinctSidecarElementCallback implements SidecarInterface.SidecarCallback {
        private final SidecarInterface.SidecarCallback callbackInterface;
        private SidecarDeviceState lastDeviceState;
        private final ReentrantLock lock = new ReentrantLock();
        private final WeakHashMap<IBinder, SidecarWindowLayoutInfo> mActivityWindowLayoutInfo = new WeakHashMap<>();
        private final SidecarAdapter sidecarAdapter;

        public DistinctSidecarElementCallback(SidecarAdapter sidecarAdapter2, SidecarInterface.SidecarCallback sidecarCallback) {
            Intrinsics.checkNotNullParameter(sidecarAdapter2, "sidecarAdapter");
            Intrinsics.checkNotNullParameter(sidecarCallback, "callbackInterface");
            this.sidecarAdapter = sidecarAdapter2;
            this.callbackInterface = sidecarCallback;
        }

        public void onDeviceStateChanged(SidecarDeviceState sidecarDeviceState) {
            Intrinsics.checkNotNullParameter(sidecarDeviceState, "newDeviceState");
            Lock lock2 = this.lock;
            lock2.lock();
            try {
                if (!this.sidecarAdapter.isEqualSidecarDeviceState(this.lastDeviceState, sidecarDeviceState)) {
                    this.lastDeviceState = sidecarDeviceState;
                    this.callbackInterface.onDeviceStateChanged(sidecarDeviceState);
                    Unit unit = Unit.INSTANCE;
                    lock2.unlock();
                }
            } finally {
                lock2.unlock();
            }
        }

        public void onWindowLayoutChanged(IBinder iBinder, SidecarWindowLayoutInfo sidecarWindowLayoutInfo) {
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(sidecarWindowLayoutInfo, "newLayout");
            synchronized (this.lock) {
                if (!this.sidecarAdapter.isEqualSidecarWindowLayoutInfo(this.mActivityWindowLayoutInfo.get(iBinder), sidecarWindowLayoutInfo)) {
                    SidecarWindowLayoutInfo put = this.mActivityWindowLayoutInfo.put(iBinder, sidecarWindowLayoutInfo);
                    this.callbackInterface.onWindowLayoutChanged(iBinder, sidecarWindowLayoutInfo);
                }
            }
        }
    }

    @Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0019\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0000¢\u0006\u0002\b\rJ\u0017\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0000¢\u0006\u0002\b\u0012R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u00068F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b¨\u0006\u0013"}, d2 = {"Landroidx/window/layout/SidecarCompat$Companion;", "", "()V", "TAG", "", "sidecarVersion", "Landroidx/window/core/Version;", "getSidecarVersion", "()Landroidx/window/core/Version;", "getActivityWindowToken", "Landroid/os/IBinder;", "activity", "Landroid/app/Activity;", "getActivityWindowToken$window_release", "getSidecarCompat", "Landroidx/window/sidecar/SidecarInterface;", "context", "Landroid/content/Context;", "getSidecarCompat$window_release", "window_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: SidecarCompat.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Version getSidecarVersion() {
            try {
                String apiVersion = SidecarProvider.getApiVersion();
                if (!TextUtils.isEmpty(apiVersion)) {
                    return Version.Companion.parse(apiVersion);
                }
                return null;
            } catch (NoClassDefFoundError unused) {
                return null;
            } catch (UnsupportedOperationException unused2) {
                return null;
            }
        }

        public final SidecarInterface getSidecarCompat$window_release(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return SidecarProvider.getSidecarImpl(context.getApplicationContext());
        }

        public final IBinder getActivityWindowToken$window_release(Activity activity) {
            Window window;
            WindowManager.LayoutParams attributes;
            if (activity == null || (window = activity.getWindow()) == null || (attributes = window.getAttributes()) == null) {
                return null;
            }
            return attributes.token;
        }
    }
}
