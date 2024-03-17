package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.slice.core.SliceHints;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.ResourceBasedOverride;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.views.ActivityContext;
import com.android.quickstep.TaskShortcutFactory;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.quickstep.views.OverviewActionsView;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class TaskOverlayFactory implements ResourceBasedOverride {
    private static final TaskShortcutFactory[] MENU_OPTIONS = {TaskShortcutFactory.APP_INFO, TaskShortcutFactory.SPLIT_SCREEN, TaskShortcutFactory.PIN, TaskShortcutFactory.INSTALL, TaskShortcutFactory.FREE_FORM, TaskShortcutFactory.WELLBEING};

    public interface OverlayUICallbacks {
        void onScreenshot();

        void onSplit();
    }

    public void initListeners() {
    }

    public void removeListeners() {
    }

    public static List<SystemShortcut> getEnabledShortcuts(TaskView taskView, DeviceProfile deviceProfile, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        SystemShortcut shortcut;
        SystemShortcut shortcut2;
        ArrayList arrayList = new ArrayList();
        BaseDraggingActivity baseDraggingActivity = (BaseDraggingActivity) BaseActivity.fromContext(taskView.getContext());
        boolean z = true;
        boolean z2 = taskView.getTaskIds()[1] != -1;
        for (TaskShortcutFactory taskShortcutFactory : MENU_OPTIONS) {
            if ((!z2 || taskShortcutFactory.showForSplitscreen()) && (shortcut2 = taskShortcutFactory.getShortcut(baseDraggingActivity, taskIdAttributeContainer)) != null) {
                if (taskShortcutFactory != TaskShortcutFactory.SPLIT_SCREEN || !FeatureFlags.ENABLE_SPLIT_SELECT.get()) {
                    arrayList.add(shortcut2);
                } else {
                    addSplitOptions(arrayList, baseDraggingActivity, taskView, deviceProfile);
                }
            }
        }
        RecentsOrientedState pagedViewOrientedState = taskView.getRecentsView().getPagedViewOrientedState();
        boolean isRecentsActivityRotationAllowed = pagedViewOrientedState.isRecentsActivityRotationAllowed();
        if (pagedViewOrientedState.getTouchRotation() == 0) {
            z = false;
        }
        if (!isRecentsActivityRotationAllowed && z) {
            SystemShortcut shortcut3 = TaskShortcutFactory.SCREENSHOT.getShortcut(baseDraggingActivity, taskIdAttributeContainer);
            if (shortcut3 != null) {
                arrayList.add(shortcut3);
            }
            if (pagedViewOrientedState.getDisplayRotation() == 0 && (shortcut = TaskShortcutFactory.MODAL.getShortcut(baseDraggingActivity, taskIdAttributeContainer)) != null) {
                arrayList.add(shortcut);
            }
        }
        return arrayList;
    }

    private static void addSplitOptions(List<SystemShortcut> list, BaseDraggingActivity baseDraggingActivity, TaskView taskView, DeviceProfile deviceProfile) {
        RecentsView recentsView = taskView.getRecentsView();
        PagedOrientationHandler pagedOrientationHandler = recentsView.getPagedOrientationHandler();
        int[] taskIds = taskView.getTaskIds();
        boolean z = false;
        boolean z2 = (taskIds[0] == -1 || taskIds[1] == -1) ? false : true;
        boolean z3 = recentsView.getTaskViewCount() < 2;
        if (deviceProfile.isTablet && taskView.isFocusedTask()) {
            z = true;
        }
        boolean isTaskInExpectedScrollPosition = recentsView.isTaskInExpectedScrollPosition(recentsView.indexOfChild(taskView));
        boolean isInLockTaskMode = ((ActivityManager) taskView.getContext().getSystemService(SliceHints.HINT_ACTIVITY)).isInLockTaskMode();
        if (!z2 && !z3 && !isInLockTaskMode) {
            if (!z || !isTaskInExpectedScrollPosition) {
                Log.d("TaskOverlayFactory", "addSplitOptions");
                String packageName = taskView.getItemInfo().getTargetComponent().getPackageName();
                packageName + "/" + taskView.getItemInfo().getTargetComponent().getClassName();
                if (!"com.zjinnova.zlink".equals(packageName) && !"com.carletter.car".equals(packageName)) {
                    for (SplitConfigurationOptions.SplitPositionOption splitSelectSystemShortcut : pagedOrientationHandler.getSplitPositionOptions(deviceProfile)) {
                        list.add(new TaskShortcutFactory.SplitSelectSystemShortcut(baseDraggingActivity, taskView, splitSelectSystemShortcut));
                    }
                }
            }
        }
    }

    public TaskOverlay createOverlay(TaskThumbnailView taskThumbnailView) {
        return new TaskOverlay(taskThumbnailView);
    }

    public static class TaskOverlay<T extends OverviewActionsView> {
        private T mActionsView;
        protected final Context mApplicationContext;
        protected ImageActionsApi mImageApi;
        protected final TaskThumbnailView mThumbnailView;

        public SystemShortcut getModalStateSystemShortcut(WorkspaceItemInfo workspaceItemInfo, View view) {
            return null;
        }

        public void reset() {
        }

        public void resetModalVisuals() {
        }

        public void setFullscreenParams(TaskView.FullscreenDrawParams fullscreenDrawParams) {
        }

        public void setFullscreenProgress(float f) {
        }

        public void updateOrientationState(RecentsOrientedState recentsOrientedState) {
        }

        protected TaskOverlay(TaskThumbnailView taskThumbnailView) {
            Context applicationContext = taskThumbnailView.getContext().getApplicationContext();
            this.mApplicationContext = applicationContext;
            this.mThumbnailView = taskThumbnailView;
            Objects.requireNonNull(taskThumbnailView);
            this.mImageApi = new ImageActionsApi(applicationContext, new Supplier() {
                public final Object get() {
                    return TaskThumbnailView.this.getThumbnail();
                }
            });
        }

        /* access modifiers changed from: protected */
        public T getActionsView() {
            if (this.mActionsView == null) {
                this.mActionsView = (OverviewActionsView) BaseActivity.fromContext(this.mThumbnailView.getContext()).findViewById(R.id.overview_actions_view);
            }
            return this.mActionsView;
        }

        public void initOverlay(Task task, ThumbnailData thumbnailData, Matrix matrix, boolean z) {
            getActionsView().updateDisabledFlags(4, thumbnailData == null);
            if (thumbnailData != null) {
                getActionsView().updateDisabledFlags(2, z);
                getActionsView().setCallbacks(new OverlayUICallbacksImpl(this.mThumbnailView.isRealSnapshot(), task));
            }
        }

        public void endLiveTileMode(Runnable runnable) {
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
                RecentsView recentsView = this.mThumbnailView.getTaskView().getRecentsView();
                recentsView.switchToScreenshot(new Runnable(runnable) {
                    public final /* synthetic */ Runnable f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        RecentsView.this.finishRecentsAnimation(true, false, this.f$1);
                    }
                });
                return;
            }
            runnable.run();
        }

        /* access modifiers changed from: protected */
        public void saveScreenshot(Task task) {
            if (this.mThumbnailView.isRealSnapshot()) {
                this.mImageApi.saveScreenshot(this.mThumbnailView.getThumbnail(), getTaskSnapshotBounds(), getTaskSnapshotInsets(), task.key);
            } else {
                showBlockedByPolicyMessage();
            }
        }

        /* access modifiers changed from: private */
        public void enterSplitSelect() {
            Log.d("TaskOverlayFactory", "enterSplitSelect");
            this.mThumbnailView.getTaskView().getRecentsView().initiateSplitSelect(this.mThumbnailView.getTaskView());
        }

        public SystemShortcut getScreenshotShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo, View view) {
            return new ScreenshotSystemShortcut(baseDraggingActivity, itemInfo, view);
        }

        public Rect getTaskSnapshotBounds() {
            int[] iArr = new int[2];
            this.mThumbnailView.getLocationOnScreen(iArr);
            return new Rect(iArr[0], iArr[1], this.mThumbnailView.getWidth() + iArr[0], this.mThumbnailView.getHeight() + iArr[1]);
        }

        public Insets getTaskSnapshotInsets() {
            return this.mThumbnailView.getScaledInsets();
        }

        /* access modifiers changed from: protected */
        public void showBlockedByPolicyMessage() {
            String str;
            ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(this.mThumbnailView.getContext());
            if (activityContext.getStringCache() != null) {
                str = activityContext.getStringCache().disabledByAdminMessage;
            } else {
                str = this.mThumbnailView.getContext().getString(R.string.blocked_by_policy);
            }
            Toast.makeText(this.mThumbnailView.getContext(), str, 1).show();
        }

        private class ScreenshotSystemShortcut extends SystemShortcut {
            private final BaseDraggingActivity mActivity;

            ScreenshotSystemShortcut(BaseDraggingActivity baseDraggingActivity, ItemInfo itemInfo, View view) {
                super(R.drawable.ic_screenshot, R.string.action_screenshot, baseDraggingActivity, itemInfo, view);
                this.mActivity = baseDraggingActivity;
            }

            public void onClick(View view) {
                TaskOverlay taskOverlay = TaskOverlay.this;
                taskOverlay.saveScreenshot(taskOverlay.mThumbnailView.getTaskView().getTask());
                dismissTaskMenuView(this.mActivity);
            }
        }

        protected class OverlayUICallbacksImpl implements OverlayUICallbacks {
            protected final boolean mIsAllowedByPolicy;
            protected final Task mTask;

            public OverlayUICallbacksImpl(boolean z, Task task) {
                this.mIsAllowedByPolicy = z;
                this.mTask = task;
            }

            public /* synthetic */ void lambda$onScreenshot$0$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl() {
                TaskOverlay.this.saveScreenshot(this.mTask);
            }

            public void onScreenshot() {
                TaskOverlay.this.endLiveTileMode(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0007: INVOKE  
                      (wrap: com.android.quickstep.TaskOverlayFactory$TaskOverlay : 0x0000: IGET  (r0v0 com.android.quickstep.TaskOverlayFactory$TaskOverlay) = 
                      (r2v0 'this' com.android.quickstep.TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl A[THIS])
                     com.android.quickstep.TaskOverlayFactory.TaskOverlay.OverlayUICallbacksImpl.this$0 com.android.quickstep.TaskOverlayFactory$TaskOverlay)
                      (wrap: com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$5eaEMO9KFe_PapsiWzpxk60CvqI : 0x0004: CONSTRUCTOR  (r1v0 com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$5eaEMO9KFe_PapsiWzpxk60CvqI) = 
                      (r2v0 'this' com.android.quickstep.TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl A[THIS])
                     call: com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$5eaEMO9KFe_PapsiWzpxk60CvqI.<init>(com.android.quickstep.TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl):void type: CONSTRUCTOR)
                     com.android.quickstep.TaskOverlayFactory.TaskOverlay.endLiveTileMode(java.lang.Runnable):void type: VIRTUAL in method: com.android.quickstep.TaskOverlayFactory.TaskOverlay.OverlayUICallbacksImpl.onScreenshot():void, dex: classes.dex
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
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0004: CONSTRUCTOR  (r1v0 com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$5eaEMO9KFe_PapsiWzpxk60CvqI) = 
                      (r2v0 'this' com.android.quickstep.TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl A[THIS])
                     call: com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$5eaEMO9KFe_PapsiWzpxk60CvqI.<init>(com.android.quickstep.TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl):void type: CONSTRUCTOR in method: com.android.quickstep.TaskOverlayFactory.TaskOverlay.OverlayUICallbacksImpl.onScreenshot():void, dex: classes.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$5eaEMO9KFe_PapsiWzpxk60CvqI, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    com.android.quickstep.TaskOverlayFactory$TaskOverlay r0 = com.android.quickstep.TaskOverlayFactory.TaskOverlay.this
                    com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$5eaEMO9KFe_PapsiWzpxk60CvqI r1 = new com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$5eaEMO9KFe_PapsiWzpxk60CvqI
                    r1.<init>(r2)
                    r0.endLiveTileMode(r1)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.TaskOverlayFactory.TaskOverlay.OverlayUICallbacksImpl.onScreenshot():void");
            }

            public void onSplit() {
                TaskOverlay taskOverlay = TaskOverlay.this;
                taskOverlay.endLiveTileMode(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0007: INVOKE  
                      (r0v0 'taskOverlay' com.android.quickstep.TaskOverlayFactory$TaskOverlay)
                      (wrap: com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$RBr-v0Q2wLnjejwFTEzsRZ70Rus : 0x0004: CONSTRUCTOR  (r1v0 com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$RBr-v0Q2wLnjejwFTEzsRZ70Rus) = 
                      (r0v0 'taskOverlay' com.android.quickstep.TaskOverlayFactory$TaskOverlay)
                     call: com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$RBr-v0Q2wLnjejwFTEzsRZ70Rus.<init>(com.android.quickstep.TaskOverlayFactory$TaskOverlay):void type: CONSTRUCTOR)
                     com.android.quickstep.TaskOverlayFactory.TaskOverlay.endLiveTileMode(java.lang.Runnable):void type: VIRTUAL in method: com.android.quickstep.TaskOverlayFactory.TaskOverlay.OverlayUICallbacksImpl.onSplit():void, dex: classes.dex
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
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0004: CONSTRUCTOR  (r1v0 com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$RBr-v0Q2wLnjejwFTEzsRZ70Rus) = 
                      (r0v0 'taskOverlay' com.android.quickstep.TaskOverlayFactory$TaskOverlay)
                     call: com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$RBr-v0Q2wLnjejwFTEzsRZ70Rus.<init>(com.android.quickstep.TaskOverlayFactory$TaskOverlay):void type: CONSTRUCTOR in method: com.android.quickstep.TaskOverlayFactory.TaskOverlay.OverlayUICallbacksImpl.onSplit():void, dex: classes.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$RBr-v0Q2wLnjejwFTEzsRZ70Rus, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    com.android.quickstep.TaskOverlayFactory$TaskOverlay r0 = com.android.quickstep.TaskOverlayFactory.TaskOverlay.this
                    com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$RBr-v0Q2wLnjejwFTEzsRZ70Rus r1 = new com.android.quickstep.-$$Lambda$TaskOverlayFactory$TaskOverlay$OverlayUICallbacksImpl$RBr-v0Q2wLnjejwFTEzsRZ70Rus
                    r1.<init>(r0)
                    r0.endLiveTileMode(r1)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.TaskOverlayFactory.TaskOverlay.OverlayUICallbacksImpl.onSplit():void");
            }
        }
    }
}
