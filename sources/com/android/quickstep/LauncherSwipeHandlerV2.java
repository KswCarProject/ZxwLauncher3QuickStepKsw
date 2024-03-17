package com.android.quickstep;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Size;
import android.view.View;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.util.ObjectWrapper;
import com.android.launcher3.views.FloatingIconView;
import com.android.launcher3.views.FloatingView;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.StaggeredWorkspaceAnim;
import com.android.quickstep.views.FloatingWidgetView;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class LauncherSwipeHandlerV2 extends AbsSwipeUpHandler<BaseQuickstepLauncher, RecentsView, LauncherState> {
    public LauncherSwipeHandlerV2(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, long j, boolean z, InputConsumerController inputConsumerController) {
        super(context, recentsAnimationDeviceState, taskAnimationManager, gestureState, j, z, inputConsumerController);
    }

    /* access modifiers changed from: protected */
    public SwipeUpAnimationLogic.HomeAnimationFactory createHomeAnimationFactory(ArrayList<IBinder> arrayList, final long j, boolean z, boolean z2, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        if (this.mActivity == null) {
            this.mStateCallback.addChangeListener(STATE_LAUNCHER_PRESENT | STATE_HANDLER_INVALIDATED, new Consumer() {
                public final void accept(Object obj) {
                    LauncherSwipeHandlerV2.this.lambda$createHomeAnimationFactory$0$LauncherSwipeHandlerV2((Boolean) obj);
                }
            });
            return new SwipeUpAnimationLogic.HomeAnimationFactory() {
                public AnimatorPlaybackController createActivityAnimationToHome() {
                    return AnimatorPlaybackController.wrap(new AnimatorSet(), j);
                }
            };
        }
        View findWorkspaceView = findWorkspaceView(arrayList, this.mRecentsView.getRunningTaskView());
        boolean z3 = findWorkspaceView != null && findWorkspaceView.isAttachedToWindow();
        ((BaseQuickstepLauncher) this.mActivity).getRootView().setForceHideBackArrow(true);
        if (!TaskAnimationManager.ENABLE_SHELL_TRANSITIONS) {
            ((BaseQuickstepLauncher) this.mActivity).setHintUserWillBeActive();
        }
        if (!z3 || z2 || this.mIsSwipeForStagedSplit) {
            return new LauncherHomeAnimationFactory();
        }
        if (findWorkspaceView instanceof LauncherAppWidgetHostView) {
            return createWidgetHomeAnimationFactory((LauncherAppWidgetHostView) findWorkspaceView, z, remoteAnimationTargetCompat);
        }
        return createIconHomeAnimationFactory(findWorkspaceView);
    }

    public /* synthetic */ void lambda$createHomeAnimationFactory$0$LauncherSwipeHandlerV2(Boolean bool) {
        this.mRecentsView.startHome();
    }

    private SwipeUpAnimationLogic.HomeAnimationFactory createIconHomeAnimationFactory(View view) {
        final RectF rectF = new RectF();
        final FloatingIconView floatingIconView = FloatingIconView.getFloatingIconView((Launcher) this.mActivity, view, true, rectF, false);
        final View view2 = view;
        return new FloatingViewHomeAnimationFactory(floatingIconView, 0.9f) {
            /* access modifiers changed from: protected */
            public View getViewIgnoredInWorkspaceRevealAnimation() {
                return view2;
            }

            public RectF getWindowTargetRect() {
                return rectF;
            }

            public void setAnimation(RectFSpringAnim rectFSpringAnim) {
                super.setAnimation(rectFSpringAnim);
                rectFSpringAnim.addAnimatorListener(floatingIconView);
                FloatingIconView floatingIconView = floatingIconView;
                Objects.requireNonNull(rectFSpringAnim);
                floatingIconView.setOnTargetChangeListener(new Runnable() {
                    public final void run() {
                        RectFSpringAnim.this.onTargetPositionChanged();
                    }
                });
                FloatingIconView floatingIconView2 = floatingIconView;
                Objects.requireNonNull(rectFSpringAnim);
                floatingIconView2.setFastFinishRunnable(new Runnable() {
                    public final void run() {
                        RectFSpringAnim.this.end();
                    }
                });
            }

            public void update(RectF rectF, float f, float f2) {
                super.update(rectF, f, f2);
                floatingIconView.update(1.0f, 255, rectF, f, 0.9f, f2, false);
            }
        };
    }

    private SwipeUpAnimationLogic.HomeAnimationFactory createWidgetHomeAnimationFactory(LauncherAppWidgetHostView launcherAppWidgetHostView, boolean z, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        final float f = z ? 0.0f : 1.0f;
        RectF rectF = new RectF();
        Rect rect = new Rect();
        this.mRemoteTargetHandles[0].getTaskViewSimulator().getCurrentCropRect().roundOut(rect);
        final FloatingWidgetView floatingWidgetView = FloatingWidgetView.getFloatingWidgetView((Launcher) this.mActivity, launcherAppWidgetHostView, rectF, new Size(rect.width(), rect.height()), this.mRemoteTargetHandles[0].getTaskViewSimulator().getCurrentCornerRadius(), z, FloatingWidgetView.getDefaultBackgroundColor(this.mContext, remoteAnimationTargetCompat));
        final LauncherAppWidgetHostView launcherAppWidgetHostView2 = launcherAppWidgetHostView;
        final RectF rectF2 = rectF;
        return new FloatingViewHomeAnimationFactory(floatingWidgetView) {
            /* access modifiers changed from: protected */
            public View getViewIgnoredInWorkspaceRevealAnimation() {
                return launcherAppWidgetHostView2;
            }

            public RectF getWindowTargetRect() {
                super.getWindowTargetRect();
                return rectF2;
            }

            public float getEndRadius(RectF rectF) {
                return floatingWidgetView.getInitialCornerRadius();
            }

            public void setAnimation(RectFSpringAnim rectFSpringAnim) {
                super.setAnimation(rectFSpringAnim);
                rectFSpringAnim.addAnimatorListener(floatingWidgetView);
                FloatingWidgetView floatingWidgetView = floatingWidgetView;
                Objects.requireNonNull(rectFSpringAnim);
                floatingWidgetView.setOnTargetChangeListener(new Runnable() {
                    public final void run() {
                        RectFSpringAnim.this.onTargetPositionChanged();
                    }
                });
                FloatingWidgetView floatingWidgetView2 = floatingWidgetView;
                Objects.requireNonNull(rectFSpringAnim);
                floatingWidgetView2.setFastFinishRunnable(new Runnable() {
                    public final void run() {
                        RectFSpringAnim.this.end();
                    }
                });
            }

            public void update(RectF rectF, float f, float f2) {
                super.update(rectF, f, f2);
                float mapBoundToRange = Utilities.mapBoundToRange(f, 0.5f, 1.0f, 0.0f, 1.0f, Interpolators.EXAGGERATED_EASE);
                FloatingWidgetView floatingWidgetView = floatingWidgetView;
                float f3 = f;
                RectF rectF2 = rectF;
                floatingWidgetView.update(rectF2, f3, mapBoundToRange, 1.0f - Utilities.mapBoundToRange(f, 0.8f, 1.0f, 0.0f, 1.0f, Interpolators.EXAGGERATED_EASE), 1.0f - f);
            }

            /* access modifiers changed from: protected */
            public float getWindowAlpha(float f) {
                return 1.0f - Utilities.mapBoundToRange(f, 0.0f, 0.5f, 0.0f, 1.0f, Interpolators.LINEAR);
            }
        };
    }

    private View findWorkspaceView(ArrayList<IBinder> arrayList, TaskView taskView) {
        if (this.mIsSwipingPipToHome || taskView == null || taskView.getTask() == null || taskView.getTask().key.getComponent() == null) {
            return null;
        }
        int i = Integer.MIN_VALUE;
        Iterator<IBinder> it = arrayList.iterator();
        while (true) {
            if (it.hasNext()) {
                Integer num = (Integer) ObjectWrapper.unwrap(it.next());
                if (num != null) {
                    i = num.intValue();
                    break;
                }
            } else {
                break;
            }
        }
        return ((BaseQuickstepLauncher) this.mActivity).getFirstMatchForAppClose(i, taskView.getTask().key.getComponent().getPackageName(), UserHandle.of(taskView.getTask().key.userId), false);
    }

    /* access modifiers changed from: protected */
    public void finishRecentsControllerToHome(Runnable runnable) {
        this.mRecentsView.cleanupRemoteTargets();
        this.mRecentsAnimationController.finish(true, runnable, true);
    }

    private class FloatingViewHomeAnimationFactory extends LauncherHomeAnimationFactory {
        private final FloatingView mFloatingView;

        FloatingViewHomeAnimationFactory(FloatingView floatingView) {
            super();
            this.mFloatingView = floatingView;
        }

        public void onCancel() {
            this.mFloatingView.fastFinish();
        }
    }

    private class LauncherHomeAnimationFactory extends SwipeUpAnimationLogic.HomeAnimationFactory {
        /* access modifiers changed from: protected */
        public View getViewIgnoredInWorkspaceRevealAnimation() {
            return null;
        }

        private LauncherHomeAnimationFactory() {
            super();
        }

        public AnimatorPlaybackController createActivityAnimationToHome() {
            return ((BaseQuickstepLauncher) LauncherSwipeHandlerV2.this.mActivity).getStateManager().createAnimationToNewWorkspace(LauncherState.NORMAL, (long) (Math.max(LauncherSwipeHandlerV2.this.mDp.widthPx, LauncherSwipeHandlerV2.this.mDp.heightPx) * 2), 1);
        }

        public void playAtomicAnimation(float f) {
            new StaggeredWorkspaceAnim((Launcher) LauncherSwipeHandlerV2.this.mActivity, f, true, getViewIgnoredInWorkspaceRevealAnimation()).start();
        }
    }
}
