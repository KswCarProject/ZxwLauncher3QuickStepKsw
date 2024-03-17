package com.android.systemui.unfold.updates;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import androidx.core.util.Consumer;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.unfold.updates.FoldStateProvider;
import com.android.systemui.unfold.updates.hinge.HingeAngleProvider;
import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0011\u0018\u00002\u00020\u0001:\u000489:;BC\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\b\u0001\u0010\f\u001a\u00020\r\u0012\b\b\u0001\u0010\u000e\u001a\u00020\u000f¢\u0006\u0002\u0010\u0010J\u0010\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020%H\u0016J\b\u0010-\u001a\u00020+H\u0002J\u000f\u0010.\u001a\u0004\u0018\u00010\u0014H\u0002¢\u0006\u0002\u0010/J\u0010\u00100\u001a\u00020+2\u0006\u00101\u001a\u00020\u0014H\u0002J\u0010\u00102\u001a\u00020+2\u0006\u00103\u001a\u00020\"H\u0002J\u0010\u00104\u001a\u00020+2\u0006\u0010,\u001a\u00020%H\u0016J\b\u00105\u001a\u00020+H\u0002J\b\u00106\u001a\u00020+H\u0016J\b\u00107\u001a\u00020+H\u0016R\u000e\u0010\n\u001a\u00020\u000bX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0011\u001a\u00060\u0012R\u00020\u0000X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0015\u001a\u00060\u0016R\u00020\u0000X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\u00020\u00188VX\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0019R\u000e\u0010\u001a\u001a\u00020\u0018X\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\u00020\u00188BX\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0019R\u000e\u0010\u001c\u001a\u00020\u0018X\u000e¢\u0006\u0002\n\u0000R\u0018\u0010\u001d\u001a\u0004\u0018\u00010\u0014X\u000e¢\u0006\n\n\u0002\u0010 \u0012\u0004\b\u001e\u0010\u001fR\u000e\u0010!\u001a\u00020\"X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0004¢\u0006\u0002\n\u0000R\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020%0$X\u0004¢\u0006\u0002\n\u0000R\u0012\u0010&\u001a\u00060'R\u00020\u0000X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u0012\u0010(\u001a\u00060)R\u00020\u0000X\u0004¢\u0006\u0002\n\u0000¨\u0006<"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;", "Lcom/android/systemui/unfold/updates/FoldStateProvider;", "context", "Landroid/content/Context;", "hingeAngleProvider", "Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;", "screenStatusProvider", "Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider;", "deviceStateManager", "Landroid/hardware/devicestate/DeviceStateManager;", "activityManager", "Landroid/app/ActivityManager;", "mainExecutor", "Ljava/util/concurrent/Executor;", "handler", "Landroid/os/Handler;", "(Landroid/content/Context;Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider;Landroid/hardware/devicestate/DeviceStateManager;Landroid/app/ActivityManager;Ljava/util/concurrent/Executor;Landroid/os/Handler;)V", "foldStateListener", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$FoldStateListener;", "halfOpenedTimeoutMillis", "", "hingeAngleListener", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$HingeAngleListener;", "isFinishedOpening", "", "()Z", "isFolded", "isTransitionInProgress", "isUnfoldHandled", "lastFoldUpdate", "getLastFoldUpdate$annotations", "()V", "Ljava/lang/Integer;", "lastHingeAngle", "", "outputListeners", "", "Lcom/android/systemui/unfold/updates/FoldStateProvider$FoldUpdatesListener;", "screenListener", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$ScreenStatusListener;", "timeoutRunnable", "Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$TimeoutRunnable;", "addCallback", "", "listener", "cancelTimeout", "getClosingThreshold", "()Ljava/lang/Integer;", "notifyFoldUpdate", "update", "onHingeAngle", "angle", "removeCallback", "rescheduleAbortAnimationTimeout", "start", "stop", "FoldStateListener", "HingeAngleListener", "ScreenStatusListener", "TimeoutRunnable", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: DeviceFoldStateProvider.kt */
public final class DeviceFoldStateProvider implements FoldStateProvider {
    private final ActivityManager activityManager;
    private final DeviceStateManager deviceStateManager;
    private final FoldStateListener foldStateListener;
    private final int halfOpenedTimeoutMillis;
    private final Handler handler;
    private final HingeAngleListener hingeAngleListener = new HingeAngleListener(this);
    /* access modifiers changed from: private */
    public final HingeAngleProvider hingeAngleProvider;
    /* access modifiers changed from: private */
    public boolean isFolded;
    /* access modifiers changed from: private */
    public boolean isUnfoldHandled;
    private Integer lastFoldUpdate;
    /* access modifiers changed from: private */
    public float lastHingeAngle;
    private final Executor mainExecutor;
    /* access modifiers changed from: private */
    public final List<FoldStateProvider.FoldUpdatesListener> outputListeners = new ArrayList();
    private final ScreenStatusListener screenListener = new ScreenStatusListener(this);
    private final ScreenStatusProvider screenStatusProvider;
    private final TimeoutRunnable timeoutRunnable;

    private static /* synthetic */ void getLastFoldUpdate$annotations() {
    }

    @Inject
    public DeviceFoldStateProvider(Context context, HingeAngleProvider hingeAngleProvider2, ScreenStatusProvider screenStatusProvider2, DeviceStateManager deviceStateManager2, ActivityManager activityManager2, @Main Executor executor, @Main Handler handler2) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(hingeAngleProvider2, "hingeAngleProvider");
        Intrinsics.checkNotNullParameter(screenStatusProvider2, "screenStatusProvider");
        Intrinsics.checkNotNullParameter(deviceStateManager2, "deviceStateManager");
        Intrinsics.checkNotNullParameter(activityManager2, "activityManager");
        Intrinsics.checkNotNullParameter(executor, "mainExecutor");
        Intrinsics.checkNotNullParameter(handler2, "handler");
        this.hingeAngleProvider = hingeAngleProvider2;
        this.screenStatusProvider = screenStatusProvider2;
        this.deviceStateManager = deviceStateManager2;
        this.activityManager = activityManager2;
        this.mainExecutor = executor;
        this.handler = handler2;
        this.foldStateListener = new FoldStateListener(this, context);
        this.timeoutRunnable = new TimeoutRunnable(this);
        this.halfOpenedTimeoutMillis = context.getResources().getInteger(17694957);
        this.isUnfoldHandled = true;
    }

    public void start() {
        this.deviceStateManager.registerCallback(this.mainExecutor, this.foldStateListener);
        this.screenStatusProvider.addCallback(this.screenListener);
        this.hingeAngleProvider.addCallback(this.hingeAngleListener);
    }

    public void stop() {
        this.screenStatusProvider.removeCallback(this.screenListener);
        this.deviceStateManager.unregisterCallback(this.foldStateListener);
        this.hingeAngleProvider.removeCallback(this.hingeAngleListener);
        this.hingeAngleProvider.stop();
    }

    public void addCallback(FoldStateProvider.FoldUpdatesListener foldUpdatesListener) {
        Intrinsics.checkNotNullParameter(foldUpdatesListener, "listener");
        this.outputListeners.add(foldUpdatesListener);
    }

    public void removeCallback(FoldStateProvider.FoldUpdatesListener foldUpdatesListener) {
        Intrinsics.checkNotNullParameter(foldUpdatesListener, "listener");
        this.outputListeners.remove(foldUpdatesListener);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r2.lastFoldUpdate;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0010, code lost:
        r0 = r2.lastFoldUpdate;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isFinishedOpening() {
        /*
            r2 = this;
            boolean r0 = r2.isFolded
            if (r0 != 0) goto L_0x001e
            java.lang.Integer r0 = r2.lastFoldUpdate
            r1 = 4
            if (r0 != 0) goto L_0x000a
            goto L_0x0010
        L_0x000a:
            int r0 = r0.intValue()
            if (r0 == r1) goto L_0x001c
        L_0x0010:
            java.lang.Integer r0 = r2.lastFoldUpdate
            r1 = 3
            if (r0 != 0) goto L_0x0016
            goto L_0x001e
        L_0x0016:
            int r0 = r0.intValue()
            if (r0 != r1) goto L_0x001e
        L_0x001c:
            r0 = 1
            goto L_0x001f
        L_0x001e:
            r0 = 0
        L_0x001f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.unfold.updates.DeviceFoldStateProvider.isFinishedOpening():boolean");
    }

    private final boolean isTransitionInProgress() {
        Integer num = this.lastFoldUpdate;
        if (num != null && num.intValue() == 0) {
            return true;
        }
        Integer num2 = this.lastFoldUpdate;
        if (num2 != null && num2.intValue() == 1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public final void onHingeAngle(float f) {
        boolean z = false;
        boolean z2 = f < this.lastHingeAngle;
        Integer closingThreshold = getClosingThreshold();
        boolean z3 = closingThreshold == null || f < ((float) closingThreshold.intValue());
        boolean z4 = 180.0f - f < 15.0f;
        Integer num = this.lastFoldUpdate;
        if (num != null && num.intValue() == 1) {
            z = true;
        }
        if (z2 && z3 && !z && !z4) {
            notifyFoldUpdate(1);
        }
        if (isTransitionInProgress()) {
            if (z4) {
                notifyFoldUpdate(4);
                cancelTimeout();
            } else {
                rescheduleAbortAnimationTimeout();
            }
        }
        this.lastHingeAngle = f;
        for (FoldStateProvider.FoldUpdatesListener onHingeAngleUpdate : this.outputListeners) {
            onHingeAngleUpdate.onHingeAngleUpdate(f);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000c, code lost:
        r0 = (android.app.ActivityManager.RunningTaskInfo) kotlin.collections.CollectionsKt.getOrNull(r0, 0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final java.lang.Integer getClosingThreshold() {
        /*
            r3 = this;
            android.app.ActivityManager r0 = r3.activityManager
            r1 = 1
            java.util.List r0 = r0.getRunningTasks(r1)
            r1 = 0
            if (r0 != 0) goto L_0x000c
        L_0x000a:
            r0 = r1
            goto L_0x001c
        L_0x000c:
            r2 = 0
            java.lang.Object r0 = kotlin.collections.CollectionsKt.getOrNull(r0, r2)
            android.app.ActivityManager$RunningTaskInfo r0 = (android.app.ActivityManager.RunningTaskInfo) r0
            if (r0 != 0) goto L_0x0016
            goto L_0x000a
        L_0x0016:
            int r0 = r0.topActivityType
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
        L_0x001c:
            if (r0 != 0) goto L_0x001f
            return r1
        L_0x001f:
            int r0 = r0.intValue()
            r2 = 2
            if (r0 != r2) goto L_0x0029
            java.lang.Integer r1 = (java.lang.Integer) r1
            goto L_0x002f
        L_0x0029:
            r0 = 60
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
        L_0x002f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.unfold.updates.DeviceFoldStateProvider.getClosingThreshold():java.lang.Integer");
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$FoldStateListener;", "Landroid/hardware/devicestate/DeviceStateManager$FoldStateListener;", "context", "Landroid/content/Context;", "(Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;Landroid/content/Context;)V", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DeviceFoldStateProvider.kt */
    private final class FoldStateListener extends DeviceStateManager.FoldStateListener {
        final /* synthetic */ DeviceFoldStateProvider this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public FoldStateListener(DeviceFoldStateProvider deviceFoldStateProvider, Context context) {
            super(context, 
            /*  JADX ERROR: Method code generation error
                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0011: CONSTRUCTOR  
                  (r3v0 'context' android.content.Context)
                  (wrap: com.android.systemui.unfold.updates.-$$Lambda$DeviceFoldStateProvider$FoldStateListener$qWWx_D6UmF0XzUitkEo4Cauc1DQ : 0x000e: CONSTRUCTOR  (r0v2 com.android.systemui.unfold.updates.-$$Lambda$DeviceFoldStateProvider$FoldStateListener$qWWx_D6UmF0XzUitkEo4Cauc1DQ) = 
                  (r2v0 'deviceFoldStateProvider' com.android.systemui.unfold.updates.DeviceFoldStateProvider)
                 call: com.android.systemui.unfold.updates.-$$Lambda$DeviceFoldStateProvider$FoldStateListener$qWWx_D6UmF0XzUitkEo4Cauc1DQ.<init>(com.android.systemui.unfold.updates.DeviceFoldStateProvider):void type: CONSTRUCTOR)
                 call: android.hardware.devicestate.DeviceStateManager.FoldStateListener.<init>(android.content.Context, java.util.function.Consumer):void type: SUPER in method: com.android.systemui.unfold.updates.DeviceFoldStateProvider.FoldStateListener.<init>(com.android.systemui.unfold.updates.DeviceFoldStateProvider, android.content.Context):void, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000e: CONSTRUCTOR  (r0v2 com.android.systemui.unfold.updates.-$$Lambda$DeviceFoldStateProvider$FoldStateListener$qWWx_D6UmF0XzUitkEo4Cauc1DQ) = 
                  (r2v0 'deviceFoldStateProvider' com.android.systemui.unfold.updates.DeviceFoldStateProvider)
                 call: com.android.systemui.unfold.updates.-$$Lambda$DeviceFoldStateProvider$FoldStateListener$qWWx_D6UmF0XzUitkEo4Cauc1DQ.<init>(com.android.systemui.unfold.updates.DeviceFoldStateProvider):void type: CONSTRUCTOR in method: com.android.systemui.unfold.updates.DeviceFoldStateProvider.FoldStateListener.<init>(com.android.systemui.unfold.updates.DeviceFoldStateProvider, android.content.Context):void, dex: classes.dex
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	... 44 more
                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.systemui.unfold.updates.-$$Lambda$DeviceFoldStateProvider$FoldStateListener$qWWx_D6UmF0XzUitkEo4Cauc1DQ, state: NOT_LOADED
                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	... 50 more
                */
            /*
                this = this;
                java.lang.String r0 = "this$0"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r2, r0)
                java.lang.String r0 = "context"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r0)
                r1.this$0 = r2
                com.android.systemui.unfold.updates.-$$Lambda$DeviceFoldStateProvider$FoldStateListener$qWWx_D6UmF0XzUitkEo4Cauc1DQ r0 = new com.android.systemui.unfold.updates.-$$Lambda$DeviceFoldStateProvider$FoldStateListener$qWWx_D6UmF0XzUitkEo4Cauc1DQ
                r0.<init>(r2)
                r1.<init>(r3, r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.unfold.updates.DeviceFoldStateProvider.FoldStateListener.<init>(com.android.systemui.unfold.updates.DeviceFoldStateProvider, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        /* renamed from: _init_$lambda-0  reason: not valid java name */
        public static final void m124_init_$lambda0(DeviceFoldStateProvider deviceFoldStateProvider, boolean z) {
            Intrinsics.checkNotNullParameter(deviceFoldStateProvider, "this$0");
            deviceFoldStateProvider.isFolded = z;
            deviceFoldStateProvider.lastHingeAngle = 0.0f;
            if (z) {
                deviceFoldStateProvider.hingeAngleProvider.stop();
                deviceFoldStateProvider.notifyFoldUpdate(5);
                deviceFoldStateProvider.cancelTimeout();
                deviceFoldStateProvider.isUnfoldHandled = false;
                return;
            }
            deviceFoldStateProvider.notifyFoldUpdate(0);
            deviceFoldStateProvider.rescheduleAbortAnimationTimeout();
            deviceFoldStateProvider.hingeAngleProvider.start();
        }
    }

    /* access modifiers changed from: private */
    public final void notifyFoldUpdate(int i) {
        for (FoldStateProvider.FoldUpdatesListener onFoldUpdate : this.outputListeners) {
            onFoldUpdate.onFoldUpdate(i);
        }
        this.lastFoldUpdate = Integer.valueOf(i);
    }

    /* access modifiers changed from: private */
    public final void rescheduleAbortAnimationTimeout() {
        if (isTransitionInProgress()) {
            cancelTimeout();
        }
        this.handler.postDelayed(this.timeoutRunnable, (long) this.halfOpenedTimeoutMillis);
    }

    /* access modifiers changed from: private */
    public final void cancelTimeout() {
        this.handler.removeCallbacks(this.timeoutRunnable);
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$ScreenStatusListener;", "Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider$ScreenListener;", "(Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;)V", "onScreenTurnedOn", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DeviceFoldStateProvider.kt */
    private final class ScreenStatusListener implements ScreenStatusProvider.ScreenListener {
        final /* synthetic */ DeviceFoldStateProvider this$0;

        public ScreenStatusListener(DeviceFoldStateProvider deviceFoldStateProvider) {
            Intrinsics.checkNotNullParameter(deviceFoldStateProvider, "this$0");
            this.this$0 = deviceFoldStateProvider;
        }

        public void onScreenTurnedOn() {
            if (!this.this$0.isFolded && !this.this$0.isUnfoldHandled) {
                for (FoldStateProvider.FoldUpdatesListener onFoldUpdate : this.this$0.outputListeners) {
                    onFoldUpdate.onFoldUpdate(2);
                }
                this.this$0.isUnfoldHandled = true;
            }
        }
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0016¨\u0006\u0007"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$HingeAngleListener;", "Landroidx/core/util/Consumer;", "", "(Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;)V", "accept", "", "angle", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DeviceFoldStateProvider.kt */
    private final class HingeAngleListener implements Consumer<Float> {
        final /* synthetic */ DeviceFoldStateProvider this$0;

        public HingeAngleListener(DeviceFoldStateProvider deviceFoldStateProvider) {
            Intrinsics.checkNotNullParameter(deviceFoldStateProvider, "this$0");
            this.this$0 = deviceFoldStateProvider;
        }

        public /* bridge */ /* synthetic */ void accept(Object obj) {
            accept(((Number) obj).floatValue());
        }

        public void accept(float f) {
            this.this$0.onHingeAngle(f);
        }
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider$TimeoutRunnable;", "Ljava/lang/Runnable;", "(Lcom/android/systemui/unfold/updates/DeviceFoldStateProvider;)V", "run", "", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: DeviceFoldStateProvider.kt */
    private final class TimeoutRunnable implements Runnable {
        final /* synthetic */ DeviceFoldStateProvider this$0;

        public TimeoutRunnable(DeviceFoldStateProvider deviceFoldStateProvider) {
            Intrinsics.checkNotNullParameter(deviceFoldStateProvider, "this$0");
            this.this$0 = deviceFoldStateProvider;
        }

        public void run() {
            this.this$0.notifyFoldUpdate(3);
        }
    }
}
