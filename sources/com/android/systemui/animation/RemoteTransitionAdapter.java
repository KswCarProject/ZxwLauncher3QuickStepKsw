package com.android.systemui.animation;

import android.app.ActivityManager;
import android.app.WindowConfiguration;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.RotationUtils;
import android.view.IRemoteAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.RemoteTransition;
import android.window.TransitionInfo;
import android.window.WindowContainerToken;
import java.util.ArrayList;
import java.util.Objects;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\u0018\u0000 \u00032\u00020\u0001:\u0002\u0003\u0004B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/animation/RemoteTransitionAdapter;", "", "()V", "Companion", "CounterRotator", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: RemoteTransitionAdapter.kt */
public final class RemoteTransitionAdapter {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    @JvmStatic
    public static final RemoteTransition adaptRemoteAnimation(RemoteAnimationAdapter remoteAnimationAdapter) {
        return Companion.adaptRemoteAnimation(remoteAnimationAdapter);
    }

    @JvmStatic
    public static final IRemoteTransition.Stub adaptRemoteRunner(IRemoteAnimationRunner iRemoteAnimationRunner) {
        return Companion.adaptRemoteRunner(iRemoteAnimationRunner);
    }

    @Metadata(d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J(\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0003J&\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u0014J\u0010\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0012H\u0002J\u0018\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J0\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010!\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0003J?\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00160#2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010$\u001a\u00020%2\u0006\u0010\u0013\u001a\u00020\u00142\u0014\u0010&\u001a\u0010\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f\u0018\u00010'¢\u0006\u0002\u0010(¨\u0006)"}, d2 = {"Lcom/android/systemui/animation/RemoteTransitionAdapter$Companion;", "", "()V", "adaptRemoteAnimation", "Landroid/window/RemoteTransition;", "adapter", "Landroid/view/RemoteAnimationAdapter;", "adaptRemoteRunner", "Landroid/window/IRemoteTransition$Stub;", "runner", "Landroid/view/IRemoteAnimationRunner;", "createLeash", "Landroid/view/SurfaceControl;", "info", "Landroid/window/TransitionInfo;", "change", "Landroid/window/TransitionInfo$Change;", "order", "", "t", "Landroid/view/SurfaceControl$Transaction;", "createTarget", "Landroid/view/RemoteAnimationTarget;", "newModeToLegacyMode", "newMode", "rectOffsetTo", "Landroid/graphics/Rect;", "rect", "offset", "Landroid/graphics/Point;", "setupLeash", "", "leash", "layer", "wrapTargets", "", "wallpapers", "", "leashMap", "Landroid/util/ArrayMap;", "(Landroid/window/TransitionInfo;ZLandroid/view/SurfaceControl$Transaction;Landroid/util/ArrayMap;)[Landroid/view/RemoteAnimationTarget;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RemoteTransitionAdapter.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private final int newModeToLegacyMode(int i) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            return 2;
                        }
                    }
                }
                return 1;
            }
            return 0;
        }

        private Companion() {
        }

        private final void setupLeash(SurfaceControl surfaceControl, TransitionInfo.Change change, int i, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction) {
            boolean z = transitionInfo.getType() == 1 || transitionInfo.getType() == 3;
            int size = transitionInfo.getChanges().size();
            int mode = change.getMode();
            transaction.reparent(surfaceControl, transitionInfo.getRootLeash());
            transaction.setPosition(surfaceControl, (float) (change.getStartAbsBounds().left - transitionInfo.getRootOffset().x), (float) (change.getStartAbsBounds().top - transitionInfo.getRootOffset().y));
            transaction.show(surfaceControl);
            if (mode != 1) {
                if (mode != 2) {
                    if (mode != 3) {
                        if (mode != 4) {
                            transaction.setLayer(surfaceControl, (size + transitionInfo.getChanges().size()) - i);
                            return;
                        }
                    }
                }
                if (z) {
                    transaction.setLayer(surfaceControl, size - i);
                    return;
                } else {
                    transaction.setLayer(surfaceControl, (size + transitionInfo.getChanges().size()) - i);
                    return;
                }
            }
            if (z) {
                transaction.setLayer(surfaceControl, (size + transitionInfo.getChanges().size()) - i);
                if ((change.getFlags() & 8) == 0) {
                    transaction.setAlpha(surfaceControl, 0.0f);
                    return;
                }
                return;
            }
            transaction.setLayer(surfaceControl, size - i);
        }

        private final SurfaceControl createLeash(TransitionInfo transitionInfo, TransitionInfo.Change change, int i, SurfaceControl.Transaction transaction) {
            SurfaceControl surfaceControl;
            if (change.getParent() == null || (change.getFlags() & 2) == 0) {
                SurfaceControl.Builder containerLayer = new SurfaceControl.Builder().setName(Intrinsics.stringPlus(change.getLeash().toString(), "_transition-leash")).setContainerLayer();
                if (change.getParent() == null) {
                    surfaceControl = transitionInfo.getRootLeash();
                } else {
                    WindowContainerToken parent = change.getParent();
                    Intrinsics.checkNotNull(parent);
                    TransitionInfo.Change change2 = transitionInfo.getChange(parent);
                    Intrinsics.checkNotNull(change2);
                    surfaceControl = change2.getLeash();
                }
                SurfaceControl build = containerLayer.setParent(surfaceControl).build();
                Intrinsics.checkNotNullExpressionValue(build, "leashSurface");
                setupLeash(build, change, transitionInfo.getChanges().size() - i, transitionInfo, transaction);
                transaction.reparent(change.getLeash(), build);
                transaction.setAlpha(change.getLeash(), 1.0f);
                transaction.show(change.getLeash());
                transaction.setPosition(change.getLeash(), 0.0f, 0.0f);
                transaction.setLayer(change.getLeash(), 0);
                return build;
            }
            SurfaceControl leash = change.getLeash();
            Intrinsics.checkNotNullExpressionValue(leash, "change.leash");
            return leash;
        }

        private final Rect rectOffsetTo(Rect rect, Point point) {
            Rect rect2 = new Rect(rect);
            rect2.offsetTo(point.x, point.y);
            return rect2;
        }

        public final RemoteAnimationTarget createTarget(TransitionInfo.Change change, int i, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction) {
            int i2;
            WindowConfiguration windowConfiguration;
            boolean z;
            TransitionInfo.Change change2 = change;
            TransitionInfo transitionInfo2 = transitionInfo;
            SurfaceControl.Transaction transaction2 = transaction;
            Intrinsics.checkNotNullParameter(change2, "change");
            Intrinsics.checkNotNullParameter(transitionInfo2, "info");
            Intrinsics.checkNotNullParameter(transaction2, "t");
            if (change.getTaskInfo() != null) {
                ActivityManager.RunningTaskInfo taskInfo = change.getTaskInfo();
                Intrinsics.checkNotNull(taskInfo);
                i2 = taskInfo.taskId;
            } else {
                i2 = -1;
            }
            int i3 = i2;
            int newModeToLegacyMode = newModeToLegacyMode(change.getMode());
            SurfaceControl createLeash = createLeash(transitionInfo2, change2, i, transaction2);
            boolean z2 = ((change.getFlags() & 4) == 0 && (change.getFlags() & 1) == 0) ? false : true;
            Rect rect = new Rect(0, 0, 0, 0);
            Rect endAbsBounds = change.getEndAbsBounds();
            Intrinsics.checkNotNullExpressionValue(endAbsBounds, "change.endAbsBounds");
            Point endRelOffset = change.getEndRelOffset();
            Intrinsics.checkNotNullExpressionValue(endRelOffset, "change.endRelOffset");
            Rect rectOffsetTo = rectOffsetTo(endAbsBounds, endRelOffset);
            Rect rect2 = new Rect(change.getEndAbsBounds());
            if (change.getTaskInfo() != null) {
                ActivityManager.RunningTaskInfo taskInfo2 = change.getTaskInfo();
                Intrinsics.checkNotNull(taskInfo2);
                windowConfiguration = taskInfo2.configuration.windowConfiguration;
            } else {
                windowConfiguration = new WindowConfiguration();
            }
            WindowConfiguration windowConfiguration2 = windowConfiguration;
            if (change.getTaskInfo() != null) {
                ActivityManager.RunningTaskInfo taskInfo3 = change.getTaskInfo();
                Intrinsics.checkNotNull(taskInfo3);
                if (taskInfo3.isRunning) {
                    z = false;
                    Rect rect3 = r2;
                    Rect rect4 = new Rect(change.getStartAbsBounds());
                    RemoteAnimationTarget remoteAnimationTarget = new RemoteAnimationTarget(i3, newModeToLegacyMode, createLeash, z2, (Rect) null, rect, i, (Point) null, rectOffsetTo, rect2, windowConfiguration2, z, (SurfaceControl) null, rect3, change.getTaskInfo(), change.getAllowEnterPip(), -1);
                    remoteAnimationTarget.backgroundColor = change.getBackgroundColor();
                    return remoteAnimationTarget;
                }
            }
            z = true;
            Rect rect32 = rect4;
            Rect rect42 = new Rect(change.getStartAbsBounds());
            RemoteAnimationTarget remoteAnimationTarget2 = new RemoteAnimationTarget(i3, newModeToLegacyMode, createLeash, z2, (Rect) null, rect, i, (Point) null, rectOffsetTo, rect2, windowConfiguration2, z, (SurfaceControl) null, rect32, change.getTaskInfo(), change.getAllowEnterPip(), -1);
            remoteAnimationTarget2.backgroundColor = change.getBackgroundColor();
            return remoteAnimationTarget2;
        }

        public final RemoteAnimationTarget[] wrapTargets(TransitionInfo transitionInfo, boolean z, SurfaceControl.Transaction transaction, ArrayMap<SurfaceControl, SurfaceControl> arrayMap) {
            Intrinsics.checkNotNullParameter(transitionInfo, "info");
            Intrinsics.checkNotNullParameter(transaction, "t");
            ArrayList arrayList = new ArrayList();
            int size = transitionInfo.getChanges().size();
            int i = 0;
            while (i < size) {
                int i2 = i + 1;
                TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(i);
                if (z == ((change.getFlags() & 2) != 0)) {
                    Intrinsics.checkNotNullExpressionValue(change, "change");
                    arrayList.add(createTarget(change, transitionInfo.getChanges().size() - i, transitionInfo, transaction));
                    if (arrayMap != null) {
                        arrayMap.put(change.getLeash(), ((RemoteAnimationTarget) arrayList.get(arrayList.size() - 1)).leash);
                    }
                }
                i = i2;
            }
            Object[] array = arrayList.toArray(new RemoteAnimationTarget[0]);
            Objects.requireNonNull(array, "null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>");
            return (RemoteAnimationTarget[]) array;
        }

        @JvmStatic
        public final IRemoteTransition.Stub adaptRemoteRunner(IRemoteAnimationRunner iRemoteAnimationRunner) {
            Intrinsics.checkNotNullParameter(iRemoteAnimationRunner, "runner");
            return new RemoteTransitionAdapter$Companion$adaptRemoteRunner$1(iRemoteAnimationRunner);
        }

        @JvmStatic
        public final RemoteTransition adaptRemoteAnimation(RemoteAnimationAdapter remoteAnimationAdapter) {
            Intrinsics.checkNotNullParameter(remoteAnimationAdapter, "adapter");
            IRemoteAnimationRunner runner = remoteAnimationAdapter.getRunner();
            Intrinsics.checkNotNullExpressionValue(runner, "adapter.runner");
            return new RemoteTransition(adaptRemoteRunner(runner), remoteAnimationAdapter.getCallingApplication());
        }
    }

    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0004J\u000e\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000bJ.\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014R\"\u0010\u0005\u001a\u0004\u0018\u00010\u00042\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004@BX\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0016"}, d2 = {"Lcom/android/systemui/animation/RemoteTransitionAdapter$CounterRotator;", "", "()V", "<set-?>", "Landroid/view/SurfaceControl;", "surface", "getSurface", "()Landroid/view/SurfaceControl;", "addChild", "", "t", "Landroid/view/SurfaceControl$Transaction;", "child", "cleanUp", "finishTransaction", "setup", "parent", "rotateDelta", "", "parentW", "", "parentH", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: RemoteTransitionAdapter.kt */
    public static final class CounterRotator {
        private SurfaceControl surface;

        public final SurfaceControl getSurface() {
            return this.surface;
        }

        public final void setup(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, int i, float f, float f2) {
            Intrinsics.checkNotNullParameter(transaction, "t");
            Intrinsics.checkNotNullParameter(surfaceControl, "parent");
            if (i != 0) {
                SurfaceControl build = new SurfaceControl.Builder().setName("Transition Unrotate").setContainerLayer().setParent(surfaceControl).build();
                RotationUtils.rotateSurface(transaction, build, i);
                boolean z = false;
                Point point = new Point(0, 0);
                if (i % 2 != 0) {
                    z = true;
                }
                float f3 = z ? f2 : f;
                if (!z) {
                    f = f2;
                }
                RotationUtils.rotatePoint(point, i, (int) f3, (int) f);
                transaction.setPosition(build, (float) point.x, (float) point.y);
                transaction.show(build);
            }
        }

        public final void addChild(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
            Intrinsics.checkNotNullParameter(transaction, "t");
            if (this.surface != null) {
                Intrinsics.checkNotNull(surfaceControl);
                transaction.reparent(surfaceControl, this.surface);
            }
        }

        public final void cleanUp(SurfaceControl.Transaction transaction) {
            Intrinsics.checkNotNullParameter(transaction, "finishTransaction");
            SurfaceControl surfaceControl = this.surface;
            if (surfaceControl != null) {
                Intrinsics.checkNotNull(surfaceControl);
                transaction.remove(surfaceControl);
            }
        }
    }
}
