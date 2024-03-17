package com.android.systemui.animation;

import android.app.ActivityTaskManager;
import android.app.PendingIntent;
import android.app.TaskInfo;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.IRemoteAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SyncRtSurfaceTransactionApplier;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.testing.TestProtocol;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.animation.LaunchAnimator;
import java.util.LinkedHashSet;
import kotlin.Metadata;
import kotlin.jvm.JvmDefault;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;

@Metadata(d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 '2\u00020\u0001:\u0006&'()*+B\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000eJ\u0014\u0010\u0013\u001a\u00060\u0014R\u00020\u00002\u0006\u0010\u0015\u001a\u00020\u0016H\u0007J\u000e\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000eJH\u0010\u0018\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u001a2\u0014\u0010\u001e\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010 \u0012\u0004\u0012\u00020!0\u001fH\u0007J0\u0010\"\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0006\u0010\u001e\u001a\u00020#H\u0007J\u0014\u0010$\u001a\u00020\u0011*\u00020\u00162\u0006\u0010%\u001a\u00020\u001aH\u0002R\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\u0004\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\f\u001a\u0012\u0012\u0004\u0012\u00020\u000e0\rj\b\u0012\u0004\u0012\u00020\u000e`\u000fX\u0004¢\u0006\u0002\n\u0000¨\u0006,"}, d2 = {"Lcom/android/systemui/animation/ActivityLaunchAnimator;", "", "launchAnimator", "Lcom/android/systemui/animation/LaunchAnimator;", "dialogToAppAnimator", "(Lcom/android/systemui/animation/LaunchAnimator;Lcom/android/systemui/animation/LaunchAnimator;)V", "callback", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Callback;", "getCallback", "()Lcom/android/systemui/animation/ActivityLaunchAnimator$Callback;", "setCallback", "(Lcom/android/systemui/animation/ActivityLaunchAnimator$Callback;)V", "listeners", "Ljava/util/LinkedHashSet;", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Listener;", "Lkotlin/collections/LinkedHashSet;", "addListener", "", "listener", "createRunner", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Runner;", "controller", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "removeListener", "startIntentWithAnimation", "animate", "", "packageName", "", "showOverLockscreen", "intentStarter", "Lkotlin/Function1;", "Landroid/view/RemoteAnimationAdapter;", "", "startPendingIntentWithAnimation", "Lcom/android/systemui/animation/ActivityLaunchAnimator$PendingIntentStarter;", "callOnIntentStartedOnMainThread", "willAnimate", "Callback", "Companion", "Controller", "Listener", "PendingIntentStarter", "Runner", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ActivityLaunchAnimator.kt */
public final class ActivityLaunchAnimator {
    /* access modifiers changed from: private */
    public static final long ANIMATION_DELAY_NAV_FADE_IN;
    private static final long ANIMATION_DURATION_NAV_FADE_IN = 266;
    private static final long ANIMATION_DURATION_NAV_FADE_OUT = 133;
    public static final Companion Companion;
    /* access modifiers changed from: private */
    public static final LaunchAnimator.Timings DIALOG_TIMINGS;
    /* access modifiers changed from: private */
    public static final LaunchAnimator.Interpolators INTERPOLATORS;
    private static final long LAUNCH_TIMEOUT = 1000;
    /* access modifiers changed from: private */
    public static final Interpolator NAV_FADE_IN_INTERPOLATOR = Interpolators.STANDARD_DECELERATE;
    /* access modifiers changed from: private */
    public static final PathInterpolator NAV_FADE_OUT_INTERPOLATOR = new PathInterpolator(0.2f, 0.0f, 1.0f, 1.0f);
    public static final LaunchAnimator.Timings TIMINGS;
    private Callback callback;
    /* access modifiers changed from: private */
    public final LaunchAnimator dialogToAppAnimator;
    /* access modifiers changed from: private */
    public final LaunchAnimator launchAnimator;
    /* access modifiers changed from: private */
    public final LinkedHashSet<Listener> listeners;

    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u000bH&¨\u0006\f"}, d2 = {"Lcom/android/systemui/animation/ActivityLaunchAnimator$Callback;", "", "getBackgroundColor", "", "task", "Landroid/app/TaskInfo;", "hideKeyguardWithAnimation", "", "runner", "Landroid/view/IRemoteAnimationRunner;", "isOnKeyguard", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ActivityLaunchAnimator.kt */
    public interface Callback {
        int getBackgroundColor(TaskInfo taskInfo);

        void hideKeyguardWithAnimation(IRemoteAnimationRunner iRemoteAnimationRunner);

        boolean isOnKeyguard();
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0012\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H&¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/animation/ActivityLaunchAnimator$PendingIntentStarter;", "", "startPendingIntent", "", "animationAdapter", "Landroid/view/RemoteAnimationAdapter;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ActivityLaunchAnimator.kt */
    public interface PendingIntentStarter {
        int startPendingIntent(RemoteAnimationAdapter remoteAnimationAdapter) throws PendingIntent.CanceledException;
    }

    public ActivityLaunchAnimator() {
        this((LaunchAnimator) null, (LaunchAnimator) null, 3, (DefaultConstructorMarker) null);
    }

    public final void startIntentWithAnimation(Controller controller, Function1<? super RemoteAnimationAdapter, Integer> function1) {
        Intrinsics.checkNotNullParameter(function1, "intentStarter");
        startIntentWithAnimation$default(this, controller, false, (String) null, false, function1, 14, (Object) null);
    }

    public final void startIntentWithAnimation(Controller controller, boolean z, String str, Function1<? super RemoteAnimationAdapter, Integer> function1) {
        Intrinsics.checkNotNullParameter(function1, "intentStarter");
        startIntentWithAnimation$default(this, controller, z, str, false, function1, 8, (Object) null);
    }

    public final void startIntentWithAnimation(Controller controller, boolean z, Function1<? super RemoteAnimationAdapter, Integer> function1) {
        Intrinsics.checkNotNullParameter(function1, "intentStarter");
        startIntentWithAnimation$default(this, controller, z, (String) null, false, function1, 12, (Object) null);
    }

    public final void startPendingIntentWithAnimation(Controller controller, PendingIntentStarter pendingIntentStarter) throws PendingIntent.CanceledException {
        Intrinsics.checkNotNullParameter(pendingIntentStarter, "intentStarter");
        startPendingIntentWithAnimation$default(this, controller, false, (String) null, pendingIntentStarter, 6, (Object) null);
    }

    public final void startPendingIntentWithAnimation(Controller controller, boolean z, PendingIntentStarter pendingIntentStarter) throws PendingIntent.CanceledException {
        Intrinsics.checkNotNullParameter(pendingIntentStarter, "intentStarter");
        startPendingIntentWithAnimation$default(this, controller, z, (String) null, pendingIntentStarter, 4, (Object) null);
    }

    public ActivityLaunchAnimator(LaunchAnimator launchAnimator2, LaunchAnimator launchAnimator3) {
        Intrinsics.checkNotNullParameter(launchAnimator2, "launchAnimator");
        Intrinsics.checkNotNullParameter(launchAnimator3, "dialogToAppAnimator");
        this.launchAnimator = launchAnimator2;
        this.dialogToAppAnimator = launchAnimator3;
        this.listeners = new LinkedHashSet<>();
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ ActivityLaunchAnimator(LaunchAnimator launchAnimator2, LaunchAnimator launchAnimator3, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? new LaunchAnimator(TIMINGS, INTERPOLATORS) : launchAnimator2, (i & 2) != 0 ? new LaunchAnimator(DIALOG_TIMINGS, INTERPOLATORS) : launchAnimator3);
    }

    @Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0016\u001a\u00020\u0011H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n \u0012*\u0004\u0018\u00010\u00110\u0011X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u00020\b8\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"}, d2 = {"Lcom/android/systemui/animation/ActivityLaunchAnimator$Companion;", "", "()V", "ANIMATION_DELAY_NAV_FADE_IN", "", "ANIMATION_DURATION_NAV_FADE_IN", "ANIMATION_DURATION_NAV_FADE_OUT", "DIALOG_TIMINGS", "Lcom/android/systemui/animation/LaunchAnimator$Timings;", "getDIALOG_TIMINGS", "()Lcom/android/systemui/animation/LaunchAnimator$Timings;", "INTERPOLATORS", "Lcom/android/systemui/animation/LaunchAnimator$Interpolators;", "getINTERPOLATORS", "()Lcom/android/systemui/animation/LaunchAnimator$Interpolators;", "LAUNCH_TIMEOUT", "NAV_FADE_IN_INTERPOLATOR", "Landroid/view/animation/Interpolator;", "kotlin.jvm.PlatformType", "NAV_FADE_OUT_INTERPOLATOR", "Landroid/view/animation/PathInterpolator;", "TIMINGS", "createPositionXInterpolator", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ActivityLaunchAnimator.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final LaunchAnimator.Timings getDIALOG_TIMINGS() {
            return ActivityLaunchAnimator.DIALOG_TIMINGS;
        }

        public final LaunchAnimator.Interpolators getINTERPOLATORS() {
            return ActivityLaunchAnimator.INTERPOLATORS;
        }

        /* access modifiers changed from: private */
        public final Interpolator createPositionXInterpolator() {
            Path path = new Path();
            path.moveTo(0.0f, 0.0f);
            Path path2 = path;
            path2.cubicTo(0.1217f, 0.0462f, 0.15f, 0.4686f, 0.1667f, 0.66f);
            path2.cubicTo(0.1834f, 0.8878f, 0.1667f, 1.0f, 1.0f, 1.0f);
            return new PathInterpolator(path);
        }
    }

    static {
        Companion companion = new Companion((DefaultConstructorMarker) null);
        Companion = companion;
        LaunchAnimator.Timings timings = new LaunchAnimator.Timings(500, 0, 150, 150, 183);
        TIMINGS = timings;
        DIALOG_TIMINGS = LaunchAnimator.Timings.copy$default(timings, 0, 0, 200, 200, 0, 19, (Object) null);
        Interpolator interpolator = Interpolators.EMPHASIZED;
        Intrinsics.checkNotNullExpressionValue(interpolator, "EMPHASIZED");
        Interpolator access$createPositionXInterpolator = companion.createPositionXInterpolator();
        Interpolator interpolator2 = Interpolators.LINEAR_OUT_SLOW_IN;
        Intrinsics.checkNotNullExpressionValue(interpolator2, "LINEAR_OUT_SLOW_IN");
        INTERPOLATORS = new LaunchAnimator.Interpolators(interpolator, access$createPositionXInterpolator, interpolator2, new PathInterpolator(0.0f, 0.0f, 0.6f, 1.0f));
        ANIMATION_DELAY_NAV_FADE_IN = timings.getTotalDuration() - ANIMATION_DURATION_NAV_FADE_IN;
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    public static /* synthetic */ void startIntentWithAnimation$default(ActivityLaunchAnimator activityLaunchAnimator, Controller controller, boolean z, String str, boolean z2, Function1 function1, int i, Object obj) {
        if ((i & 2) != 0) {
            z = true;
        }
        boolean z3 = z;
        if ((i & 4) != 0) {
            str = null;
        }
        String str2 = str;
        if ((i & 8) != 0) {
            z2 = false;
        }
        activityLaunchAnimator.startIntentWithAnimation(controller, z3, str2, z2, function1);
    }

    public final void startIntentWithAnimation(Controller controller, boolean z, String str, boolean z2, Function1<? super RemoteAnimationAdapter, Integer> function1) {
        RemoteAnimationAdapter remoteAnimationAdapter;
        Controller controller2 = controller;
        String str2 = str;
        Function1<? super RemoteAnimationAdapter, Integer> function12 = function1;
        Intrinsics.checkNotNullParameter(function12, "intentStarter");
        boolean z3 = false;
        if (controller2 == null || !z) {
            Log.i("ActivityLaunchAnimator", "Starting intent with no animation");
            function12.invoke(null);
            if (controller2 != null) {
                callOnIntentStartedOnMainThread(controller2, false);
                return;
            }
            return;
        }
        Callback callback2 = this.callback;
        if (callback2 != null) {
            IRemoteAnimationRunner runner = new Runner(this, controller2);
            boolean z4 = callback2.isOnKeyguard() && !z2;
            if (!z4) {
                LaunchAnimator.Timings timings = TIMINGS;
                remoteAnimationAdapter = new RemoteAnimationAdapter(runner, timings.getTotalDuration(), timings.getTotalDuration() - ((long) DragView.VIEW_ZOOM_DURATION));
            } else {
                remoteAnimationAdapter = null;
            }
            RemoteAnimationAdapter remoteAnimationAdapter2 = remoteAnimationAdapter;
            if (!(str2 == null || remoteAnimationAdapter2 == null)) {
                try {
                    ActivityTaskManager.getService().registerRemoteAnimationForNextActivityStart(str2, remoteAnimationAdapter2, (IBinder) null);
                } catch (RemoteException e) {
                    Log.w("ActivityLaunchAnimator", "Unable to register the remote animation", e);
                }
            }
            int intValue = function12.invoke(remoteAnimationAdapter2).intValue();
            if (intValue == 2 || intValue == 0 || (intValue == 3 && z4)) {
                z3 = true;
            }
            Log.i("ActivityLaunchAnimator", "launchResult=" + intValue + " willAnimate=" + z3 + " hideKeyguardWithAnimation=" + z4);
            callOnIntentStartedOnMainThread(controller2, z3);
            if (z3) {
                runner.postTimeout$SharedLibWrapper_release();
                if (z4) {
                    callback2.hideKeyguardWithAnimation(runner);
                    return;
                }
                return;
            }
            return;
        }
        throw new IllegalStateException("ActivityLaunchAnimator.callback must be set before using this animator");
    }

    private final void callOnIntentStartedOnMainThread(Controller controller, boolean z) {
        if (!Intrinsics.areEqual((Object) Looper.myLooper(), (Object) Looper.getMainLooper())) {
            controller.getLaunchContainer().getContext().getMainExecutor().execute(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ActivityLaunchAnimator.m99callOnIntentStartedOnMainThread$lambda0(ActivityLaunchAnimator.Controller.this, this.f$1);
                }
            });
        } else {
            controller.onIntentStarted(z);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: callOnIntentStartedOnMainThread$lambda-0  reason: not valid java name */
    public static final void m99callOnIntentStartedOnMainThread$lambda0(Controller controller, boolean z) {
        Intrinsics.checkNotNullParameter(controller, "$this_callOnIntentStartedOnMainThread");
        controller.onIntentStarted(z);
    }

    public static /* synthetic */ void startPendingIntentWithAnimation$default(ActivityLaunchAnimator activityLaunchAnimator, Controller controller, boolean z, String str, PendingIntentStarter pendingIntentStarter, int i, Object obj) throws PendingIntent.CanceledException {
        if ((i & 2) != 0) {
            z = true;
        }
        if ((i & 4) != 0) {
            str = null;
        }
        activityLaunchAnimator.startPendingIntentWithAnimation(controller, z, str, pendingIntentStarter);
    }

    public final void startPendingIntentWithAnimation(Controller controller, boolean z, String str, PendingIntentStarter pendingIntentStarter) throws PendingIntent.CanceledException {
        Intrinsics.checkNotNullParameter(pendingIntentStarter, "intentStarter");
        startIntentWithAnimation$default(this, controller, z, str, false, new ActivityLaunchAnimator$startPendingIntentWithAnimation$1(pendingIntentStarter), 8, (Object) null);
    }

    public final void addListener(Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.listeners.add(listener);
    }

    public final void removeListener(Listener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.listeners.remove(listener);
    }

    public final Runner createRunner(Controller controller) {
        Intrinsics.checkNotNullParameter(controller, "controller");
        return new Runner(this, controller);
    }

    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0017J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u0017J\b\u0010\u0007\u001a\u00020\u0003H\u0017¨\u0006\b"}, d2 = {"Lcom/android/systemui/animation/ActivityLaunchAnimator$Listener;", "", "onLaunchAnimationEnd", "", "onLaunchAnimationProgress", "linearProgress", "", "onLaunchAnimationStart", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ActivityLaunchAnimator.kt */
    public interface Listener {
        @JvmDefault
        void onLaunchAnimationEnd() {
        }

        @JvmDefault
        void onLaunchAnimationProgress(float f) {
        }

        @JvmDefault
        void onLaunchAnimationStart() {
        }

        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
        /* compiled from: ActivityLaunchAnimator.kt */
        public static final class DefaultImpls {
            @Deprecated
            @JvmDefault
            public static void onLaunchAnimationStart(Listener listener) {
                Intrinsics.checkNotNullParameter(listener, "this");
                Listener.super.onLaunchAnimationStart();
            }

            @Deprecated
            @JvmDefault
            public static void onLaunchAnimationEnd(Listener listener) {
                Intrinsics.checkNotNullParameter(listener, "this");
                Listener.super.onLaunchAnimationEnd();
            }

            @Deprecated
            @JvmDefault
            public static void onLaunchAnimationProgress(Listener listener, float f) {
                Intrinsics.checkNotNullParameter(listener, "this");
                Listener.super.onLaunchAnimationProgress(f);
            }
        }
    }

    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\bf\u0018\u0000 \t2\u00020\u0001:\u0001\tJ\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0003H\u0016J\b\u0010\b\u001a\u00020\u0006H\u0016R\u0014\u0010\u0002\u001a\u00020\u00038VX\u0004¢\u0006\u0006\u001a\u0004\b\u0002\u0010\u0004¨\u0006\n"}, d2 = {"Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "Lcom/android/systemui/animation/LaunchAnimator$Controller;", "isDialogLaunch", "", "()Z", "onIntentStarted", "", "willAnimate", "onLaunchAnimationCancelled", "Companion", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ActivityLaunchAnimator.kt */
    public interface Controller extends LaunchAnimator.Controller {
        public static final Companion Companion = Companion.$$INSTANCE;

        @JvmStatic
        static Controller fromView(View view, Integer num) {
            return Companion.fromView(view, num);
        }

        boolean isDialogLaunch();

        void onIntentStarted(boolean z);

        void onLaunchAnimationCancelled();

        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
        /* compiled from: ActivityLaunchAnimator.kt */
        public static final class DefaultImpls {
            public static boolean isDialogLaunch(Controller controller) {
                Intrinsics.checkNotNullParameter(controller, "this");
                return false;
            }

            public static void onIntentStarted(Controller controller, boolean z) {
                Intrinsics.checkNotNullParameter(controller, "this");
            }

            public static void onLaunchAnimationCancelled(Controller controller) {
                Intrinsics.checkNotNullParameter(controller, "this");
            }

            public static View getOpeningWindowSyncView(Controller controller) {
                Intrinsics.checkNotNullParameter(controller, "this");
                return LaunchAnimator.Controller.DefaultImpls.getOpeningWindowSyncView(controller);
            }

            public static void onLaunchAnimationEnd(Controller controller, boolean z) {
                Intrinsics.checkNotNullParameter(controller, "this");
                LaunchAnimator.Controller.DefaultImpls.onLaunchAnimationEnd(controller, z);
            }

            public static void onLaunchAnimationProgress(Controller controller, LaunchAnimator.State state, float f, float f2) {
                Intrinsics.checkNotNullParameter(controller, "this");
                Intrinsics.checkNotNullParameter(state, TestProtocol.STATE_FIELD);
                LaunchAnimator.Controller.DefaultImpls.onLaunchAnimationProgress(controller, state, f, f2);
            }

            public static void onLaunchAnimationStart(Controller controller, boolean z) {
                Intrinsics.checkNotNullParameter(controller, "this");
                LaunchAnimator.Controller.DefaultImpls.onLaunchAnimationStart(controller, z);
            }
        }

        @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J#\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0007¢\u0006\u0002\u0010\t¨\u0006\n"}, d2 = {"Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller$Companion;", "", "()V", "fromView", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "view", "Landroid/view/View;", "cujType", "", "(Landroid/view/View;Ljava/lang/Integer;)Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
        /* compiled from: ActivityLaunchAnimator.kt */
        public static final class Companion {
            static final /* synthetic */ Companion $$INSTANCE = new Companion();

            private Companion() {
            }

            public static /* synthetic */ Controller fromView$default(Companion companion, View view, Integer num, int i, Object obj) {
                if ((i & 2) != 0) {
                    num = null;
                }
                return companion.fromView(view, num);
            }

            @JvmStatic
            public final Controller fromView(View view, Integer num) {
                Intrinsics.checkNotNullParameter(view, "view");
                if (view.getParent() instanceof ViewGroup) {
                    return new GhostedViewLaunchAnimatorController(view, num, (InteractionJankMonitor) null, 4, (DefaultConstructorMarker) null);
                }
                Log.wtf("ActivityLaunchAnimator", "Skipping animation as view " + view + " is not attached to a ViewGroup", new Exception());
                return null;
            }
        }
    }

    @Metadata(d1 = {"\u0000\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J \u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0002J\u0018\u0010$\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!H\u0002J\u0010\u0010&\u001a\u00020\u001d2\u0006\u0010'\u001a\u00020\bH\u0016JU\u0010(\u001a\u00020\u001d2\u0006\u0010)\u001a\u00020*2\u0010\u0010+\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u001f\u0018\u00010,2\u0010\u0010-\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u001f\u0018\u00010,2\u0010\u0010.\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u001f\u0018\u00010,2\b\u0010/\u001a\u0004\u0018\u000100H\u0016¢\u0006\u0002\u00101J\b\u00102\u001a\u00020\u001dH\u0002J\r\u00103\u001a\u00020\u001dH\u0000¢\u0006\u0002\b4J\b\u00105\u001a\u00020\u001dH\u0002J;\u00106\u001a\u00020\u001d2\u0010\u0010+\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u001f\u0018\u00010,2\u0010\u0010.\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u001f\u0018\u00010,2\b\u0010/\u001a\u0004\u0018\u000100H\u0002¢\u0006\u0002\u00107J\f\u00108\u001a\u00020\u001d*\u000200H\u0002R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \u000b*\u0004\u0018\u00010\n0\nX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\rX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u000e¢\u0006\u0002\n\u0000¨\u00069"}, d2 = {"Lcom/android/systemui/animation/ActivityLaunchAnimator$Runner;", "Landroid/view/IRemoteAnimationRunner$Stub;", "controller", "Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;", "(Lcom/android/systemui/animation/ActivityLaunchAnimator;Lcom/android/systemui/animation/ActivityLaunchAnimator$Controller;)V", "animation", "Lcom/android/systemui/animation/LaunchAnimator$Animation;", "cancelled", "", "context", "Landroid/content/Context;", "kotlin.jvm.PlatformType", "invertMatrix", "Landroid/graphics/Matrix;", "launchContainer", "Landroid/view/ViewGroup;", "matrix", "onTimeout", "Ljava/lang/Runnable;", "timedOut", "transactionApplier", "Landroid/view/SyncRtSurfaceTransactionApplier;", "transactionApplierView", "Landroid/view/View;", "windowCrop", "Landroid/graphics/Rect;", "windowCropF", "Landroid/graphics/RectF;", "applyStateToNavigationBar", "", "navigationBar", "Landroid/view/RemoteAnimationTarget;", "state", "Lcom/android/systemui/animation/LaunchAnimator$State;", "linearProgress", "", "applyStateToWindow", "window", "onAnimationCancelled", "isKeyguardOccluded", "onAnimationStart", "transit", "", "apps", "", "wallpapers", "nonApps", "iCallback", "Landroid/view/IRemoteAnimationFinishedCallback;", "(I[Landroid/view/RemoteAnimationTarget;[Landroid/view/RemoteAnimationTarget;[Landroid/view/RemoteAnimationTarget;Landroid/view/IRemoteAnimationFinishedCallback;)V", "onAnimationTimedOut", "postTimeout", "postTimeout$SharedLibWrapper_release", "removeTimeout", "startAnimation", "([Landroid/view/RemoteAnimationTarget;[Landroid/view/RemoteAnimationTarget;Landroid/view/IRemoteAnimationFinishedCallback;)V", "invoke", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: ActivityLaunchAnimator.kt */
    public final class Runner extends IRemoteAnimationRunner.Stub {
        private LaunchAnimator.Animation animation;
        private boolean cancelled;
        private final Context context;
        private final Controller controller;
        private final Matrix invertMatrix;
        private final ViewGroup launchContainer;
        private final Matrix matrix;
        private Runnable onTimeout;
        final /* synthetic */ ActivityLaunchAnimator this$0;
        private boolean timedOut;
        private final SyncRtSurfaceTransactionApplier transactionApplier;
        private final View transactionApplierView;
        private Rect windowCrop;
        private RectF windowCropF;

        public Runner(ActivityLaunchAnimator activityLaunchAnimator, Controller controller2) {
            Intrinsics.checkNotNullParameter(activityLaunchAnimator, "this$0");
            Intrinsics.checkNotNullParameter(controller2, "controller");
            this.this$0 = activityLaunchAnimator;
            this.controller = controller2;
            ViewGroup launchContainer2 = controller2.getLaunchContainer();
            this.launchContainer = launchContainer2;
            this.context = launchContainer2.getContext();
            View openingWindowSyncView = controller2.getOpeningWindowSyncView();
            openingWindowSyncView = openingWindowSyncView == null ? controller2.getLaunchContainer() : openingWindowSyncView;
            this.transactionApplierView = openingWindowSyncView;
            this.transactionApplier = new SyncRtSurfaceTransactionApplier(openingWindowSyncView);
            this.matrix = new Matrix();
            this.invertMatrix = new Matrix();
            this.windowCrop = new Rect();
            this.windowCropF = new RectF();
            this.onTimeout = new Runnable() {
                public final void run() {
                    ActivityLaunchAnimator.Runner.m103onTimeout$lambda0(ActivityLaunchAnimator.Runner.this);
                }
            };
        }

        /* access modifiers changed from: private */
        /* renamed from: onTimeout$lambda-0  reason: not valid java name */
        public static final void m103onTimeout$lambda0(Runner runner) {
            Intrinsics.checkNotNullParameter(runner, "this$0");
            runner.onAnimationTimedOut();
        }

        public final void postTimeout$SharedLibWrapper_release() {
            this.launchContainer.postDelayed(this.onTimeout, ActivityLaunchAnimator.LAUNCH_TIMEOUT);
        }

        private final void removeTimeout() {
            this.launchContainer.removeCallbacks(this.onTimeout);
        }

        public void onAnimationStart(int i, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            removeTimeout();
            if (this.timedOut) {
                if (iRemoteAnimationFinishedCallback != null) {
                    invoke(iRemoteAnimationFinishedCallback);
                }
            } else if (!this.cancelled) {
                this.context.getMainExecutor().execute(new Runnable(remoteAnimationTargetArr, remoteAnimationTargetArr3, iRemoteAnimationFinishedCallback) {
                    public final /* synthetic */ RemoteAnimationTarget[] f$1;
                    public final /* synthetic */ RemoteAnimationTarget[] f$2;
                    public final /* synthetic */ IRemoteAnimationFinishedCallback f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        ActivityLaunchAnimator.Runner.m102onAnimationStart$lambda1(ActivityLaunchAnimator.Runner.this, this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: onAnimationStart$lambda-1  reason: not valid java name */
        public static final void m102onAnimationStart$lambda1(Runner runner, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            Intrinsics.checkNotNullParameter(runner, "this$0");
            runner.startAnimation(remoteAnimationTargetArr, remoteAnimationTargetArr2, iRemoteAnimationFinishedCallback);
        }

        /* access modifiers changed from: private */
        public final void applyStateToWindow(RemoteAnimationTarget remoteAnimationTarget, LaunchAnimator.State state) {
            if (this.transactionApplierView.getViewRootImpl() != null) {
                Rect rect = remoteAnimationTarget.screenSpaceBounds;
                float f = ((float) (rect.left + rect.right)) / 2.0f;
                int i = rect.right - rect.left;
                float f2 = (float) (rect.bottom - rect.top);
                float max = Math.max(((float) state.getWidth()) / ((float) i), ((float) state.getHeight()) / f2);
                this.matrix.reset();
                this.matrix.setScale(max, max, f, ((float) (rect.top + rect.bottom)) / 2.0f);
                Matrix matrix2 = this.matrix;
                matrix2.postTranslate(state.getCenterX() - f, ((float) (state.getTop() - rect.top)) + (((f2 * max) - f2) / 2.0f));
                float left = ((float) state.getLeft()) - ((float) rect.left);
                float top = ((float) state.getTop()) - ((float) rect.top);
                this.windowCropF.set(left, top, ((float) state.getWidth()) + left, ((float) state.getHeight()) + top);
                this.matrix.invert(this.invertMatrix);
                this.invertMatrix.mapRect(this.windowCropF);
                this.windowCrop.set(MathKt.roundToInt(this.windowCropF.left), MathKt.roundToInt(this.windowCropF.top), MathKt.roundToInt(this.windowCropF.right), MathKt.roundToInt(this.windowCropF.bottom));
                SyncRtSurfaceTransactionApplier.SurfaceParams build = new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(remoteAnimationTarget.leash).withAlpha(1.0f).withMatrix(this.matrix).withWindowCrop(this.windowCrop).withCornerRadius(Math.max(state.getTopCornerRadius(), state.getBottomCornerRadius()) / max).withVisibility(true).build();
                this.transactionApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{build});
            }
        }

        /* access modifiers changed from: private */
        public final void applyStateToNavigationBar(RemoteAnimationTarget remoteAnimationTarget, LaunchAnimator.State state, float f) {
            RemoteAnimationTarget remoteAnimationTarget2 = remoteAnimationTarget;
            if (this.transactionApplierView.getViewRootImpl() != null) {
                float progress = LaunchAnimator.Companion.getProgress(ActivityLaunchAnimator.TIMINGS, f, ActivityLaunchAnimator.ANIMATION_DELAY_NAV_FADE_IN, ActivityLaunchAnimator.ANIMATION_DURATION_NAV_FADE_OUT);
                SyncRtSurfaceTransactionApplier.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(remoteAnimationTarget2.leash);
                if (progress > 0.0f) {
                    this.matrix.reset();
                    this.matrix.setTranslate(0.0f, (float) (state.getTop() - remoteAnimationTarget2.sourceContainerBounds.top));
                    this.windowCrop.set(state.getLeft(), 0, state.getRight(), state.getHeight());
                    builder.withAlpha(ActivityLaunchAnimator.NAV_FADE_IN_INTERPOLATOR.getInterpolation(progress)).withMatrix(this.matrix).withWindowCrop(this.windowCrop).withVisibility(true);
                } else {
                    builder.withAlpha(1.0f - ActivityLaunchAnimator.NAV_FADE_OUT_INTERPOLATOR.getInterpolation(LaunchAnimator.Companion.getProgress(ActivityLaunchAnimator.TIMINGS, f, 0, ActivityLaunchAnimator.ANIMATION_DURATION_NAV_FADE_OUT)));
                }
                this.transactionApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{builder.build()});
            }
        }

        private final void onAnimationTimedOut() {
            if (!this.cancelled) {
                Log.i("ActivityLaunchAnimator", "Remote animation timed out");
                this.timedOut = true;
                this.controller.onLaunchAnimationCancelled();
            }
        }

        public void onAnimationCancelled(boolean z) {
            if (!this.timedOut) {
                Log.i("ActivityLaunchAnimator", "Remote animation was cancelled");
                this.cancelled = true;
                removeTimeout();
                this.context.getMainExecutor().execute(new Runnable() {
                    public final void run() {
                        ActivityLaunchAnimator.Runner.m101onAnimationCancelled$lambda5(ActivityLaunchAnimator.Runner.this);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: onAnimationCancelled$lambda-5  reason: not valid java name */
        public static final void m101onAnimationCancelled$lambda5(Runner runner) {
            Intrinsics.checkNotNullParameter(runner, "this$0");
            LaunchAnimator.Animation animation2 = runner.animation;
            if (animation2 != null) {
                animation2.cancel();
            }
            runner.controller.onLaunchAnimationCancelled();
        }

        /* access modifiers changed from: private */
        public final void invoke(IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            try {
                iRemoteAnimationFinishedCallback.onAnimationFinished();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0023  */
        /* JADX WARNING: Removed duplicated region for block: B:17:0x0039  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private final void startAnimation(android.view.RemoteAnimationTarget[] r19, android.view.RemoteAnimationTarget[] r20, android.view.IRemoteAnimationFinishedCallback r21) {
            /*
                r18 = this;
                r7 = r18
                r0 = r19
                r1 = r20
                r3 = r21
                r2 = 0
                r8 = 1
                r4 = 0
                if (r0 != 0) goto L_0x000f
            L_0x000d:
                r5 = r4
                goto L_0x0021
            L_0x000f:
                int r5 = r0.length
                r6 = r2
            L_0x0011:
                if (r6 >= r5) goto L_0x000d
                r9 = r0[r6]
                int r6 = r6 + 1
                int r10 = r9.mode
                if (r10 != 0) goto L_0x001d
                r10 = r8
                goto L_0x001e
            L_0x001d:
                r10 = r2
            L_0x001e:
                if (r10 == 0) goto L_0x0011
                r5 = r9
            L_0x0021:
                if (r5 != 0) goto L_0x0039
                java.lang.String r0 = "ActivityLaunchAnimator"
                java.lang.String r1 = "Aborting the animation as no window is opening"
                android.util.Log.i(r0, r1)
                r18.removeTimeout()
                if (r3 != 0) goto L_0x0030
                goto L_0x0033
            L_0x0030:
                r7.invoke(r3)
            L_0x0033:
                com.android.systemui.animation.ActivityLaunchAnimator$Controller r0 = r7.controller
                r0.onLaunchAnimationCancelled()
                return
            L_0x0039:
                if (r1 != 0) goto L_0x003d
            L_0x003b:
                r6 = r4
                goto L_0x0051
            L_0x003d:
                int r0 = r1.length
                r6 = r2
            L_0x003f:
                if (r6 >= r0) goto L_0x003b
                r9 = r1[r6]
                int r6 = r6 + 1
                int r10 = r9.windowType
                r11 = 2019(0x7e3, float:2.829E-42)
                if (r10 != r11) goto L_0x004d
                r10 = r8
                goto L_0x004e
            L_0x004d:
                r10 = r2
            L_0x004e:
                if (r10 == 0) goto L_0x003f
                r6 = r9
            L_0x0051:
                android.graphics.Rect r0 = r5.screenSpaceBounds
                com.android.systemui.animation.LaunchAnimator$State r2 = new com.android.systemui.animation.LaunchAnimator$State
                int r10 = r0.top
                int r11 = r0.bottom
                int r12 = r0.left
                int r13 = r0.right
                r14 = 0
                r15 = 0
                r16 = 48
                r17 = 0
                r9 = r2
                r9.<init>(r10, r11, r12, r13, r14, r15, r16, r17)
                com.android.systemui.animation.ActivityLaunchAnimator r0 = r7.this$0
                com.android.systemui.animation.ActivityLaunchAnimator$Callback r0 = r0.getCallback()
                kotlin.jvm.internal.Intrinsics.checkNotNull(r0)
                android.app.ActivityManager$RunningTaskInfo r1 = r5.taskInfo
                if (r1 != 0) goto L_0x0075
                goto L_0x007f
            L_0x0075:
                android.app.TaskInfo r1 = (android.app.TaskInfo) r1
                int r0 = r0.getBackgroundColor(r1)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
            L_0x007f:
                if (r4 != 0) goto L_0x0084
                int r0 = r5.backgroundColor
                goto L_0x0088
            L_0x0084:
                int r0 = r4.intValue()
            L_0x0088:
                r9 = r0
                com.android.systemui.animation.ActivityLaunchAnimator$Controller r0 = r7.controller
                boolean r0 = r0.isDialogLaunch()
                if (r0 == 0) goto L_0x0098
                com.android.systemui.animation.ActivityLaunchAnimator r0 = r7.this$0
                com.android.systemui.animation.LaunchAnimator r0 = r0.dialogToAppAnimator
                goto L_0x009e
            L_0x0098:
                com.android.systemui.animation.ActivityLaunchAnimator r0 = r7.this$0
                com.android.systemui.animation.LaunchAnimator r0 = r0.launchAnimator
            L_0x009e:
                r10 = r0
                com.android.systemui.animation.ActivityLaunchAnimator$Controller r0 = r7.controller
                android.view.ViewGroup r0 = r0.getLaunchContainer()
                android.view.View r0 = (android.view.View) r0
                boolean r0 = r10.isExpandingFullyAbove$SharedLibWrapper_release(r0, r2)
                if (r0 == 0) goto L_0x00b4
                android.content.Context r0 = r7.context
                float r0 = com.android.internal.policy.ScreenDecorationsUtils.getWindowCornerRadius(r0)
                goto L_0x00b5
            L_0x00b4:
                r0 = 0
            L_0x00b5:
                r2.setTopCornerRadius(r0)
                r2.setBottomCornerRadius(r0)
                com.android.systemui.animation.ActivityLaunchAnimator$Controller r1 = r7.controller
                com.android.systemui.animation.ActivityLaunchAnimator$Runner$startAnimation$controller$1 r11 = new com.android.systemui.animation.ActivityLaunchAnimator$Runner$startAnimation$controller$1
                com.android.systemui.animation.ActivityLaunchAnimator r4 = r7.this$0
                r0 = r11
                r12 = r2
                r2 = r4
                r3 = r21
                r4 = r18
                r0.<init>(r1, r2, r3, r4, r5, r6)
                com.android.systemui.animation.LaunchAnimator$Controller r11 = (com.android.systemui.animation.LaunchAnimator.Controller) r11
                com.android.systemui.animation.LaunchAnimator$Animation r0 = r10.startAnimation(r11, r12, r9, r8)
                r7.animation = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.animation.ActivityLaunchAnimator.Runner.startAnimation(android.view.RemoteAnimationTarget[], android.view.RemoteAnimationTarget[], android.view.IRemoteAnimationFinishedCallback):void");
        }
    }
}
