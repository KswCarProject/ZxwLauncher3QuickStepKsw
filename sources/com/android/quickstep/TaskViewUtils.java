package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.window.TransitionInfo;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.QuickstepTransitionManager;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.TaskViewUtils;
import com.android.quickstep.util.MultiValueUpdateListener;
import com.android.quickstep.util.SurfaceTransactionApplier;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.util.TransformParams;
import com.android.quickstep.views.GroupedTaskView;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class TaskViewUtils {
    private TaskViewUtils() {
    }

    public static TaskView findTaskViewToLaunch(RecentsView recentsView, View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        int i;
        TaskView taskViewByTaskId;
        if (view instanceof TaskView) {
            TaskView taskView = (TaskView) view;
            if (recentsView.isTaskViewVisible(taskView)) {
                return taskView;
            }
            return null;
        }
        int i2 = 0;
        if (view.getTag() instanceof ItemInfo) {
            ItemInfo itemInfo = (ItemInfo) view.getTag();
            ComponentName targetComponent = itemInfo.getTargetComponent();
            int identifier = itemInfo.user.getIdentifier();
            if (targetComponent != null) {
                for (int i3 = 0; i3 < recentsView.getTaskViewCount(); i3++) {
                    TaskView taskViewAt = recentsView.getTaskViewAt(i3);
                    if (recentsView.isTaskViewVisible(taskViewAt)) {
                        Task.TaskKey taskKey = taskViewAt.getTask().key;
                        if (targetComponent.equals(taskKey.getComponent()) && identifier == taskKey.userId) {
                            return taskViewAt;
                        }
                    }
                }
            }
        }
        if (remoteAnimationTargetCompatArr == null) {
            return null;
        }
        int length = remoteAnimationTargetCompatArr.length;
        while (true) {
            if (i2 >= length) {
                i = -1;
                break;
            }
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargetCompatArr[i2];
            if (remoteAnimationTargetCompat.mode == 0) {
                i = remoteAnimationTargetCompat.taskId;
                break;
            }
            i2++;
        }
        if (i == -1 || (taskViewByTaskId = recentsView.getTaskViewByTaskId(i)) == null || !recentsView.isTaskViewVisible(taskViewByTaskId)) {
            return null;
        }
        return taskViewByTaskId;
    }

    public static void createRecentsWindowAnimator(TaskView taskView, boolean z, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, DepthController depthController, PendingAnimation pendingAnimation) {
        TaskView taskView2 = taskView;
        DepthController depthController2 = depthController;
        PendingAnimation pendingAnimation2 = pendingAnimation;
        RecentsView recentsView = taskView.getRecentsView();
        final boolean isEndQuickswitchCuj = taskView.isEndQuickswitchCuj();
        taskView2.setEndQuickswitchCuj(false);
        final RemoteAnimationTargets remoteAnimationTargets = new RemoteAnimationTargets(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, 0);
        final RemoteAnimationTargetCompat navBarRemoteAnimationTarget = remoteAnimationTargets.getNavBarRemoteAnimationTarget();
        SurfaceTransactionApplier surfaceTransactionApplier = new SurfaceTransactionApplier(taskView2);
        remoteAnimationTargets.addReleaseCheck(surfaceTransactionApplier);
        RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandles = recentsView.getRemoteTargetHandles();
        if (!taskView.isRunningTask() || remoteTargetHandles == null) {
            RemoteTargetGluer remoteTargetGluer = new RemoteTargetGluer(taskView.getContext(), recentsView.getSizeStrategy(), remoteAnimationTargets);
            if (taskView.containsMultipleTasks()) {
                remoteTargetHandles = remoteTargetGluer.assignTargetsForSplitScreen(remoteAnimationTargets, taskView.getTaskIds());
            } else {
                remoteTargetHandles = remoteTargetGluer.assignTargets(remoteAnimationTargets);
            }
        }
        final RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr = remoteTargetHandles;
        for (RemoteTargetGluer.RemoteTargetHandle transformParams : remoteTargetHandleArr) {
            transformParams.getTransformParams().setSyncTransactionApplier(surfaceTransactionApplier);
        }
        int indexOfChild = recentsView.indexOfChild(taskView2);
        Context context = taskView.getContext();
        DeviceProfile deviceProfile = BaseActivity.fromContext(context).getDeviceProfile();
        boolean z2 = deviceProfile.isTablet;
        boolean z3 = indexOfChild != recentsView.getCurrentPage() && !z2;
        int scrollOffset = recentsView.getScrollOffset(indexOfChild);
        int gridTranslationY = z2 ? (int) taskView.getGridTranslationY() : 0;
        if (!taskView.isRunningTask()) {
            int length = remoteTargetHandleArr.length;
            int i = 0;
            while (i < length) {
                RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle = remoteTargetHandleArr[i];
                TaskViewSimulator taskViewSimulator = remoteTargetHandle.getTaskViewSimulator();
                taskViewSimulator.setDp(deviceProfile);
                int i2 = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo().rotation;
                taskViewSimulator.getOrientationState().update(i2, i2);
                taskViewSimulator.fullScreenProgress.value = 0.0f;
                taskViewSimulator.recentsViewScale.value = 1.0f;
                taskViewSimulator.setIsGridTask(taskView.isGridTask());
                taskViewSimulator.getOrientationState().getOrientationHandler().set(taskViewSimulator, $$Lambda$wFNSFuZVSurigPf4I9Zk2Qj8.INSTANCE, scrollOffset, gridTranslationY);
                pendingAnimation.addFloat(remoteTargetHandle.getTransformParams(), TransformParams.TARGET_ALPHA, 0.0f, 1.0f, Interpolators.clampToProgress(Interpolators.LINEAR, 0.0f, 0.2f));
                i++;
                TaskView taskView3 = taskView;
                gridTranslationY = gridTranslationY;
                scrollOffset = scrollOffset;
                length = length;
                deviceProfile = deviceProfile;
            }
        }
        int length2 = remoteTargetHandleArr.length;
        RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr2 = null;
        int i3 = 0;
        while (i3 < length2) {
            TaskViewSimulator taskViewSimulator2 = remoteTargetHandleArr[i3].getTaskViewSimulator();
            pendingAnimation2.setFloat(taskViewSimulator2.fullScreenProgress, AnimatedFloat.VALUE, 1.0f, Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            pendingAnimation2.setFloat(taskViewSimulator2.recentsViewScale, AnimatedFloat.VALUE, taskViewSimulator2.getFullScreenScale(), Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            pendingAnimation2.setFloat(taskViewSimulator2.recentsViewScroll, AnimatedFloat.VALUE, 0.0f, Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            pendingAnimation2.addOnFrameCallback(new Runnable(remoteTargetHandleArr) {
                public final /* synthetic */ RemoteTargetGluer.RemoteTargetHandle[] f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    TaskViewUtils.lambda$createRecentsWindowAnimator$0(this.f$0);
                }
            });
            if (navBarRemoteAnimationTarget != null) {
                final Rect rect = new Rect();
                pendingAnimation2.addOnFrameListener(new MultiValueUpdateListener() {
                    MultiValueUpdateListener.FloatProp mNavFadeIn = new MultiValueUpdateListener.FloatProp(0.0f, 1.0f, 234.0f, 266.0f, QuickstepTransitionManager.NAV_FADE_IN_INTERPOLATOR);
                    MultiValueUpdateListener.FloatProp mNavFadeOut = new MultiValueUpdateListener.FloatProp(1.0f, 0.0f, 0.0f, 133.0f, QuickstepTransitionManager.NAV_FADE_OUT_INTERPOLATOR);

                    public void onUpdate(float f, boolean z) {
                        SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(RemoteAnimationTargetCompat.this.leash);
                        for (RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle : remoteTargetHandleArr) {
                            if (this.mNavFadeIn.value > this.mNavFadeIn.getStartValue()) {
                                TaskViewSimulator taskViewSimulator = remoteTargetHandle.getTaskViewSimulator();
                                taskViewSimulator.getCurrentCropRect().round(rect);
                                builder.withMatrix(taskViewSimulator.getCurrentMatrix()).withWindowCrop(rect).withAlpha(this.mNavFadeIn.value);
                            } else {
                                builder.withAlpha(this.mNavFadeOut.value);
                            }
                            remoteTargetHandle.getTransformParams().applySurfaceParams(builder.build());
                        }
                    }
                });
            } else {
                RecentsAnimationController recentsAnimationController = recentsView.getRecentsAnimationController();
                if (recentsAnimationController != null) {
                    recentsAnimationController.animateNavigationBarToApp(336);
                }
            }
            i3++;
            remoteTargetHandleArr2 = remoteTargetHandleArr;
        }
        if (!z && z3 && remoteTargetHandleArr2 != null && remoteTargetHandleArr2.length > 0) {
            pendingAnimation.addFloat(taskView, LauncherAnimUtils.VIEW_ALPHA, 1.0f, 0.0f, Interpolators.clampToProgress(Interpolators.LINEAR, 0.2f, 0.4f));
            for (RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle2 : remoteTargetHandleArr2) {
                remoteTargetHandle2.getTaskViewSimulator().apply(remoteTargetHandle2.getTransformParams());
            }
            final TaskThumbnailView[] thumbnails = taskView.getThumbnails();
            Matrix[] matrixArr = new Matrix[remoteTargetHandleArr2.length];
            Matrix[] matrixArr2 = new Matrix[remoteTargetHandleArr2.length];
            for (int i4 = 0; i4 < thumbnails.length; i4++) {
                TaskThumbnailView taskThumbnailView = thumbnails[i4];
                RectF rectF = new RectF(0.0f, 0.0f, (float) taskThumbnailView.getWidth(), (float) taskThumbnailView.getHeight());
                float[] fArr = {0.0f, 0.0f, (float) taskThumbnailView.getWidth(), (float) taskThumbnailView.getHeight()};
                Utilities.getDescendantCoordRelativeToAncestor(taskThumbnailView, taskThumbnailView.getRootView(), fArr, false);
                RectF rectF2 = new RectF(fArr[0], fArr[1], fArr[2], fArr[3]);
                Matrix matrix = new Matrix();
                matrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.FILL);
                matrixArr[i4] = matrix;
                Matrix matrix2 = new Matrix();
                matrix.invert(matrix2);
                matrixArr2[i4] = matrix2;
            }
            Matrix[] matrixArr3 = new Matrix[remoteTargetHandleArr2.length];
            for (int i5 = 0; i5 < remoteTargetHandleArr2.length; i5++) {
                matrixArr3[i5] = new Matrix();
                remoteTargetHandleArr2[i5].getTaskViewSimulator().getCurrentMatrix().invert(matrixArr3[i5]);
            }
            pendingAnimation2.addOnFrameCallback(new Runnable(remoteTargetHandleArr2, new Matrix(), matrixArr, matrixArr3, matrixArr2, thumbnails) {
                public final /* synthetic */ RemoteTargetGluer.RemoteTargetHandle[] f$0;
                public final /* synthetic */ Matrix f$1;
                public final /* synthetic */ Matrix[] f$2;
                public final /* synthetic */ Matrix[] f$3;
                public final /* synthetic */ Matrix[] f$4;
                public final /* synthetic */ TaskThumbnailView[] f$5;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    TaskViewUtils.lambda$createRecentsWindowAnimator$1(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
            pendingAnimation2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    for (TaskThumbnailView animationMatrix : thumbnails) {
                        animationMatrix.setAnimationMatrix((Matrix) null);
                    }
                }
            });
        }
        pendingAnimation2.addListener(new AnimationSuccessListener() {
            public void onAnimationSuccess(Animator animator) {
                if (isEndQuickswitchCuj) {
                    InteractionJankMonitorWrapper.end(11);
                }
            }

            public void onAnimationEnd(Animator animator) {
                remoteAnimationTargets.release();
                super.onAnimationEnd(animator);
            }
        });
        if (depthController2 != null) {
            pendingAnimation2.setFloat(depthController2, DepthController.DEPTH, LauncherState.BACKGROUND_APP.getDepth(context), Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
        }
    }

    static /* synthetic */ void lambda$createRecentsWindowAnimator$0(RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr) {
        for (RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle : remoteTargetHandleArr) {
            remoteTargetHandle.getTaskViewSimulator().apply(remoteTargetHandle.getTransformParams());
        }
    }

    static /* synthetic */ void lambda$createRecentsWindowAnimator$1(RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr, Matrix matrix, Matrix[] matrixArr, Matrix[] matrixArr2, Matrix[] matrixArr3, TaskThumbnailView[] taskThumbnailViewArr) {
        for (int i = 0; i < remoteTargetHandleArr.length; i++) {
            matrix.set(matrixArr[i]);
            matrix.postConcat(matrixArr2[i]);
            matrix.postConcat(remoteTargetHandleArr[i].getTaskViewSimulator().getCurrentMatrix());
            matrix.postConcat(matrixArr3[i]);
            taskThumbnailViewArr[i].setAnimationMatrix(matrix);
        }
    }

    public static void composeRecentsSplitLaunchAnimator(int i, PendingIntent pendingIntent, int i2, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, Runnable runnable) {
        TransitionInfo.Change change = null;
        TransitionInfo.Change change2 = null;
        for (int i3 = 0; i3 < transitionInfo.getChanges().size(); i3++) {
            TransitionInfo.Change change3 = (TransitionInfo.Change) transitionInfo.getChanges().get(i3);
            int i4 = change3.getTaskInfo() != null ? change3.getTaskInfo().taskId : -1;
            int mode = change3.getMode();
            if (i4 == i || i4 == i2) {
                if (mode != 1 && mode != 3) {
                    throw new IllegalStateException("Expected task to be showing, but it is " + mode);
                } else if (change3.getParent() == null) {
                    throw new IllegalStateException("Initiating multi-split launch but the splitroot of " + i4 + " is already visible or has broken hierarchy.");
                }
            }
            if (i4 == i && i != -1) {
                change = transitionInfo.getChange(change3.getParent());
            }
            if (i4 == i2) {
                change2 = transitionInfo.getChange(change3.getParent());
            }
        }
        animateSplitRoot(transaction, change);
        animateSplitRoot(transaction, change2);
        transaction.apply();
        runnable.run();
    }

    private static void animateSplitRoot(SurfaceControl.Transaction transaction, TransitionInfo.Change change) {
        if (change != null) {
            transaction.show(change.getLeash());
            transaction.setAlpha(change.getLeash(), 1.0f);
        }
    }

    public static void composeRecentsSplitLaunchAnimatorLegacy(GroupedTaskView groupedTaskView, int i, PendingIntent pendingIntent, int i2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, StateManager stateManager, DepthController depthController, Runnable runnable) {
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr4 = remoteAnimationTargetCompatArr;
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr5 = remoteAnimationTargetCompatArr3;
        final Runnable runnable2 = runnable;
        if (groupedTaskView != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            RecentsView recentsView = groupedTaskView.getRecentsView();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    runnable2.run();
                }
            });
            composeRecentsLaunchAnimator(animatorSet, groupedTaskView, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, true, stateManager, recentsView, depthController);
            animatorSet.start();
            return;
        }
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr4) {
            int i3 = remoteAnimationTargetCompat.taskInfo != null ? remoteAnimationTargetCompat.taskInfo.taskId : -1;
            int i4 = remoteAnimationTargetCompat.mode;
            SurfaceControl surfaceControl = remoteAnimationTargetCompat.leash;
            if (surfaceControl != null) {
                if (i4 == 0) {
                    arrayList.add(surfaceControl);
                } else if (i3 == i || i3 == i2) {
                    throw new IllegalStateException("Expected task to be opening, but it is " + i4);
                } else {
                    if (i4 == 1) {
                        arrayList2.add(surfaceControl);
                    }
                }
            }
            int i5 = i;
            int i6 = i2;
        }
        for (int i7 = 0; i7 < remoteAnimationTargetCompatArr5.length; i7++) {
            SurfaceControl surfaceControl2 = remoteAnimationTargetCompatArr5[i7].leash;
            if (remoteAnimationTargetCompatArr5[i7].windowType == 2034 && surfaceControl2 != null) {
                arrayList.add(surfaceControl2);
            }
        }
        final SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(370);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(arrayList, transaction) {
            public final /* synthetic */ ArrayList f$0;
            public final /* synthetic */ SurfaceControl.Transaction f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TaskViewUtils.lambda$composeRecentsSplitLaunchAnimatorLegacy$2(this.f$0, this.f$1, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    SurfaceControl surfaceControl = (SurfaceControl) it.next();
                    transaction.show(surfaceControl).setAlpha(surfaceControl, 0.0f);
                }
                transaction.apply();
            }

            public void onAnimationEnd(Animator animator) {
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    transaction.hide((SurfaceControl) it.next());
                }
                super.onAnimationEnd(animator);
                runnable2.run();
            }
        });
        ofFloat.start();
    }

    static /* synthetic */ void lambda$composeRecentsSplitLaunchAnimatorLegacy$2(ArrayList arrayList, SurfaceControl.Transaction transaction, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            transaction.setAlpha((SurfaceControl) it.next(), animatedFraction);
        }
        transaction.apply();
    }

    public static void composeRecentsLaunchAnimator(AnimatorSet animatorSet, View view, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, boolean z, StateManager stateManager, RecentsView recentsView, DepthController depthController) {
        Animator animator;
        Animator.AnimatorListener animatorListener;
        AnimatorSet animatorSet2 = animatorSet;
        final StateManager stateManager2 = stateManager;
        final RecentsView recentsView2 = recentsView;
        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr4 = remoteAnimationTargetCompatArr;
        TaskView findTaskViewToLaunch = findTaskViewToLaunch(recentsView2, view, remoteAnimationTargetCompatArr4);
        PendingAnimation pendingAnimation = new PendingAnimation(336);
        createRecentsWindowAnimator(findTaskViewToLaunch, !z, remoteAnimationTargetCompatArr4, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, depthController, pendingAnimation);
        if (z) {
            createSplitAuxiliarySurfacesAnimator(remoteAnimationTargetCompatArr3, true, new Consumer() {
                public final void accept(Object obj) {
                    TaskViewUtils.lambda$composeRecentsLaunchAnimator$3(PendingAnimation.this, (ValueAnimator) obj);
                }
            });
        }
        AnimatorSet animatorSet3 = null;
        if (z) {
            DeviceProfile deviceProfile = BaseActivity.fromContext(view.getContext()).getDeviceProfile();
            if (deviceProfile.isTablet) {
                animator = ObjectAnimator.ofFloat(recentsView2, RecentsView.CONTENT_ALPHA, new float[]{0.0f});
            } else {
                animator = recentsView2.createAdjacentPageAnimForTaskLaunch(findTaskViewToLaunch);
            }
            if (deviceProfile.isTablet) {
                Log.d(TestProtocol.BAD_STATE, "TVU composeRecentsLaunchAnimator alpha=0");
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        Log.d(TestProtocol.BAD_STATE, "TVU composeRecentsLaunchAnimator onStart");
                    }

                    public void onAnimationCancel(Animator animator) {
                        float f;
                        if (RecentsView.this == null) {
                            f = -1.0f;
                        } else {
                            f = ((Float) RecentsView.CONTENT_ALPHA.get(RecentsView.this)).floatValue();
                        }
                        Log.d(TestProtocol.BAD_STATE, "TVU composeRecentsLaunchAnimator onCancel, alpha=" + f);
                    }

                    public void onAnimationEnd(Animator animator) {
                        Log.d(TestProtocol.BAD_STATE, "TVU composeRecentsLaunchAnimator onEnd");
                    }
                });
            }
            animator.setInterpolator(Interpolators.TOUCH_RESPONSE_INTERPOLATOR);
            animator.setDuration(336);
            animatorListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    RecentsView recentsView = RecentsView.this;
                    recentsView.finishRecentsAnimation(false, new Runnable(stateManager2) {
                        public final /* synthetic */ StateManager f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            RecentsView.this.post(new Runnable() {
                                public final void run() {
                                    TaskViewUtils.AnonymousClass7.lambda$onAnimationEnd$0(StateManager.this);
                                }
                            });
                        }
                    });
                }

                static /* synthetic */ void lambda$onAnimationEnd$0(StateManager stateManager) {
                    stateManager.moveToRestState();
                    stateManager.reapplyState();
                }
            };
        } else {
            AnimatorPlaybackController createAnimationToNewWorkspace = stateManager2.createAnimationToNewWorkspace(LauncherState.NORMAL, 336);
            createAnimationToNewWorkspace.dispatchOnStart();
            AnimatorSet target = createAnimationToNewWorkspace.getTarget();
            animator = createAnimationToNewWorkspace.getAnimationPlayer().setDuration(336);
            AnimatorSet animatorSet4 = target;
            animatorListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    RecentsView.this.finishRecentsAnimation(false, new Runnable() {
                        public final void run() {
                            StateManager.this.goToState(LauncherState.NORMAL, false);
                        }
                    });
                }
            };
            animatorSet3 = animatorSet4;
        }
        pendingAnimation.add(animator);
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && recentsView.getRunningTaskIndex() != -1) {
            Objects.requireNonNull(recentsView);
            pendingAnimation.addOnFrameCallback(new Runnable() {
                public final void run() {
                    RecentsView.this.redrawLiveTile();
                }
            });
        }
        animatorSet2.play(pendingAnimation.buildAnim());
        stateManager2.setCurrentAnimation(animatorSet2, animatorSet3);
        animatorSet2.addListener(animatorListener);
    }

    static /* synthetic */ void lambda$composeRecentsLaunchAnimator$3(PendingAnimation pendingAnimation, ValueAnimator valueAnimator) {
        valueAnimator.setStartDelay(pendingAnimation.getDuration() - 100);
        pendingAnimation.add(valueAnimator);
    }

    public static ValueAnimator createSplitAuxiliarySurfacesAnimator(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, final boolean z, Consumer<ValueAnimator> consumer) {
        if (remoteAnimationTargetCompatArr == null || remoteAnimationTargetCompatArr.length == 0) {
            return null;
        }
        final SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        final ArrayList arrayList = new ArrayList(remoteAnimationTargetCompatArr.length);
        boolean z2 = false;
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
            SurfaceControl surfaceControl = remoteAnimationTargetCompat.leash;
            if (remoteAnimationTargetCompat.windowType == 2034 && surfaceControl != null) {
                arrayList.add(surfaceControl);
                z2 = true;
            }
        }
        if (!z2) {
            return null;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(arrayList, transaction, z) {
            public final /* synthetic */ List f$0;
            public final /* synthetic */ SurfaceControl.Transaction f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TaskViewUtils.lambda$createSplitAuxiliarySurfacesAnimator$4(this.f$0, this.f$1, this.f$2, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                if (z) {
                    for (SurfaceControl surfaceControl : arrayList) {
                        transaction.setAlpha(surfaceControl, 0.0f);
                        transaction.show(surfaceControl);
                    }
                    transaction.apply();
                }
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (!z) {
                    for (SurfaceControl hide : arrayList) {
                        transaction.hide(hide);
                    }
                    transaction.apply();
                }
                transaction.close();
            }
        });
        ofFloat.setDuration(100);
        consumer.accept(ofFloat);
        return ofFloat;
    }

    static /* synthetic */ void lambda$createSplitAuxiliarySurfacesAnimator$4(List list, SurfaceControl.Transaction transaction, boolean z, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            transaction.setAlpha((SurfaceControl) it.next(), z ? animatedFraction : 1.0f - animatedFraction);
        }
        transaction.apply();
    }
}
