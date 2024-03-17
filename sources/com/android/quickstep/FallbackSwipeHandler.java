package com.android.quickstep;

import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.SurfaceControl;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.GestureNavContract;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.SpringAnimationBuilder;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.FallbackSwipeHandler;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.fallback.FallbackRecentsView;
import com.android.quickstep.fallback.RecentsState;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class FallbackSwipeHandler extends AbsSwipeUpHandler<RecentsActivity, FallbackRecentsView, RecentsState> {
    private static final String TAG = "FallbackSwipeHandler";
    /* access modifiers changed from: private */
    public static StaticMessageReceiver sMessageReceiver;
    private FallbackHomeAnimationFactory mActiveAnimationFactory;
    private boolean mAppCanEnterPip;
    private float mMaxLauncherScale = 1.0f;
    /* access modifiers changed from: private */
    public final boolean mRunningOverHome;
    private final Matrix mTmpMatrix = new Matrix();

    public FallbackSwipeHandler(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, long j, boolean z, InputConsumerController inputConsumerController) {
        super(context, recentsAnimationDeviceState, taskAnimationManager, gestureState, j, z, inputConsumerController);
        boolean isHomeTask = this.mGestureState.getRunningTask().isHomeTask();
        this.mRunningOverHome = isHomeTask;
        if (isHomeTask) {
            runActionOnRemoteHandles(new Consumer() {
                public final void accept(Object obj) {
                    FallbackSwipeHandler.this.lambda$new$0$FallbackSwipeHandler((RemoteTargetGluer.RemoteTargetHandle) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$new$0$FallbackSwipeHandler(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTransformParams().setHomeBuilderProxy(new TransformParams.BuilderProxy() {
            public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                FallbackSwipeHandler.this.updateHomeActivityTransformDuringSwipeUp(builder, remoteAnimationTargetCompat, transformParams);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void initTransitionEndpoints(DeviceProfile deviceProfile) {
        super.initTransitionEndpoints(deviceProfile);
        if (this.mRunningOverHome) {
            this.mMaxLauncherScale = 1.0f / this.mRemoteTargetHandles[0].getTaskViewSimulator().getFullScreenScale();
        }
    }

    /* access modifiers changed from: private */
    public void updateHomeActivityTransformDuringSwipeUp(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
        setHomeScaleAndAlpha(builder, remoteAnimationTargetCompat, this.mCurrentShift.value, Utilities.boundToRange(1.0f - this.mCurrentShift.value, 0.0f, 1.0f));
    }

    /* access modifiers changed from: private */
    public void setHomeScaleAndAlpha(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, float f, float f2) {
        float mapRange = Utilities.mapRange(f, 1.0f, this.mMaxLauncherScale);
        this.mTmpMatrix.setScale(mapRange, mapRange, remoteAnimationTargetCompat.localBounds.exactCenterX(), remoteAnimationTargetCompat.localBounds.exactCenterY());
        builder.withMatrix(this.mTmpMatrix).withAlpha(f2);
    }

    /* access modifiers changed from: protected */
    public SwipeUpAnimationLogic.HomeAnimationFactory createHomeAnimationFactory(ArrayList<IBinder> arrayList, long j, boolean z, boolean z2, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        this.mAppCanEnterPip = z2;
        if (z2) {
            return new FallbackPipToHomeAnimationFactory();
        }
        FallbackHomeAnimationFactory fallbackHomeAnimationFactory = new FallbackHomeAnimationFactory(j);
        this.mActiveAnimationFactory = fallbackHomeAnimationFactory;
        startHomeIntent(fallbackHomeAnimationFactory, remoteAnimationTargetCompat);
        return this.mActiveAnimationFactory;
    }

    private void startHomeIntent(FallbackHomeAnimationFactory fallbackHomeAnimationFactory, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        ActivityOptions makeCustomAnimation = ActivityOptions.makeCustomAnimation(this.mContext, 0, 0);
        Intent intent = new Intent(this.mGestureState.getHomeIntent());
        if (!(fallbackHomeAnimationFactory == null || remoteAnimationTargetCompat == null)) {
            fallbackHomeAnimationFactory.addGestureContract(intent, remoteAnimationTargetCompat.taskInfo);
        }
        try {
            this.mContext.startActivity(intent, makeCustomAnimation.toBundle());
        } catch (ActivityNotFoundException | NullPointerException | SecurityException unused) {
            this.mContext.startActivity(Utilities.createHomeIntent());
        }
    }

    /* access modifiers changed from: protected */
    public boolean handleTaskAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
        FallbackHomeAnimationFactory fallbackHomeAnimationFactory = this.mActiveAnimationFactory;
        if (fallbackHomeAnimationFactory == null || !fallbackHomeAnimationFactory.handleHomeTaskAppeared(remoteAnimationTargetCompatArr)) {
            return super.handleTaskAppeared(remoteAnimationTargetCompatArr);
        }
        this.mActiveAnimationFactory = null;
        return false;
    }

    /* access modifiers changed from: protected */
    public void finishRecentsControllerToHome(Runnable runnable) {
        if (this.mAppCanEnterPip) {
            runnable = new Runnable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FallbackSwipeHandler.this.lambda$finishRecentsControllerToHome$1$FallbackSwipeHandler(this.f$1);
                }
            };
        }
        ((FallbackRecentsView) this.mRecentsView).cleanupRemoteTargets();
        this.mRecentsAnimationController.finish(this.mAppCanEnterPip, runnable, true);
    }

    public /* synthetic */ void lambda$finishRecentsControllerToHome$1$FallbackSwipeHandler(Runnable runnable) {
        runnable.run();
        startHomeIntent((FallbackHomeAnimationFactory) null, (RemoteAnimationTargetCompat) null);
    }

    /* access modifiers changed from: protected */
    public void switchToScreenshot() {
        if (this.mRunningOverHome) {
            this.mStateCallback.setStateOnUiThread(STATE_SCREENSHOT_CAPTURED);
        } else {
            super.switchToScreenshot();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyGestureAnimationStartToRecents() {
        if (!this.mRunningOverHome) {
            super.notifyGestureAnimationStartToRecents();
        } else if (DisplayController.getNavigationMode(this.mContext).hasGestures) {
            ((FallbackRecentsView) this.mRecentsView).onGestureAnimationStartOnHome(this.mGestureState.getRunningTask().getPlaceholderTasks(), this.mDeviceState.getRotationTouchHelper());
        }
    }

    private class FallbackPipToHomeAnimationFactory extends SwipeUpAnimationLogic.HomeAnimationFactory {
        private FallbackPipToHomeAnimationFactory() {
            super();
        }

        public AnimatorPlaybackController createActivityAnimationToHome() {
            return ((RecentsActivity) FallbackSwipeHandler.this.mActivity).getStateManager().createAnimationToNewWorkspace(RecentsState.HOME, (long) (Math.max(FallbackSwipeHandler.this.mDp.widthPx, FallbackSwipeHandler.this.mDp.heightPx) * 2), 1);
        }
    }

    private class FallbackHomeAnimationFactory extends SwipeUpAnimationLogic.HomeAnimationFactory implements Consumer<Message> {
        private boolean mAnimationFinished;
        private final long mDuration;
        private final AnimatedFloat mHomeAlpha;
        private final TransformParams mHomeAlphaParams;
        private Message mOnFinishCallback;
        private final AnimatedFloat mRecentsAlpha;
        private RectFSpringAnim mSpringAnim;
        private SurfaceControl mSurfaceControl;
        private final RectF mTargetRect;
        private final Rect mTempRect = new Rect();
        private final AnimatedFloat mVerticalShiftForScale;

        FallbackHomeAnimationFactory(long j) {
            super();
            TransformParams transformParams = new TransformParams();
            this.mHomeAlphaParams = transformParams;
            AnimatedFloat animatedFloat = new AnimatedFloat();
            this.mVerticalShiftForScale = animatedFloat;
            AnimatedFloat animatedFloat2 = new AnimatedFloat();
            this.mRecentsAlpha = animatedFloat2;
            this.mTargetRect = new RectF();
            this.mDuration = j;
            if (FallbackSwipeHandler.this.mRunningOverHome) {
                AnimatedFloat animatedFloat3 = new AnimatedFloat();
                this.mHomeAlpha = animatedFloat3;
                animatedFloat3.value = Utilities.boundToRange(1.0f - FallbackSwipeHandler.this.mCurrentShift.value, 0.0f, 1.0f);
                animatedFloat.value = FallbackSwipeHandler.this.mCurrentShift.value;
                FallbackSwipeHandler.this.runActionOnRemoteHandles(new Consumer() {
                    public final void accept(Object obj) {
                        FallbackSwipeHandler.FallbackHomeAnimationFactory.this.lambda$new$0$FallbackSwipeHandler$FallbackHomeAnimationFactory((RemoteTargetGluer.RemoteTargetHandle) obj);
                    }
                });
            } else {
                AnimatedFloat animatedFloat4 = new AnimatedFloat(new Runnable() {
                    public final void run() {
                        FallbackSwipeHandler.FallbackHomeAnimationFactory.this.updateHomeAlpha();
                    }
                });
                this.mHomeAlpha = animatedFloat4;
                animatedFloat4.value = 0.0f;
                transformParams.setHomeBuilderProxy(new TransformParams.BuilderProxy() {
                    public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                        FallbackSwipeHandler.FallbackHomeAnimationFactory.this.updateHomeActivityTransformDuringHomeAnim(builder, remoteAnimationTargetCompat, transformParams);
                    }
                });
            }
            animatedFloat2.value = 1.0f;
            FallbackSwipeHandler.this.runActionOnRemoteHandles(new Consumer() {
                public final void accept(Object obj) {
                    FallbackSwipeHandler.FallbackHomeAnimationFactory.this.lambda$new$1$FallbackSwipeHandler$FallbackHomeAnimationFactory((RemoteTargetGluer.RemoteTargetHandle) obj);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$FallbackSwipeHandler$FallbackHomeAnimationFactory(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
            remoteTargetHandle.getTransformParams().setHomeBuilderProxy(new TransformParams.BuilderProxy() {
                public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                    FallbackSwipeHandler.FallbackHomeAnimationFactory.this.updateHomeActivityTransformDuringHomeAnim(builder, remoteAnimationTargetCompat, transformParams);
                }
            });
        }

        public /* synthetic */ void lambda$new$1$FallbackSwipeHandler$FallbackHomeAnimationFactory(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
            remoteTargetHandle.getTransformParams().setBaseBuilderProxy(new TransformParams.BuilderProxy() {
                public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                    FallbackSwipeHandler.FallbackHomeAnimationFactory.this.updateRecentsActivityTransformDuringHomeAnim(builder, remoteAnimationTargetCompat, transformParams);
                }
            });
        }

        public RectF getWindowTargetRect() {
            if (this.mTargetRect.isEmpty()) {
                this.mTargetRect.set(super.getWindowTargetRect());
            }
            return this.mTargetRect;
        }

        /* access modifiers changed from: private */
        public void updateRecentsActivityTransformDuringHomeAnim(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
            builder.withAlpha(this.mRecentsAlpha.value);
        }

        /* access modifiers changed from: private */
        public void updateHomeActivityTransformDuringHomeAnim(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
            FallbackSwipeHandler.this.setHomeScaleAndAlpha(builder, remoteAnimationTargetCompat, this.mVerticalShiftForScale.value, this.mHomeAlpha.value);
        }

        public AnimatorPlaybackController createActivityAnimationToHome() {
            PendingAnimation pendingAnimation = new PendingAnimation(this.mDuration);
            pendingAnimation.setFloat(this.mRecentsAlpha, AnimatedFloat.VALUE, 0.0f, Interpolators.ACCEL);
            return pendingAnimation.createPlaybackController();
        }

        /* access modifiers changed from: private */
        public void updateHomeAlpha() {
            if (this.mHomeAlphaParams.getTargetSet() != null) {
                TransformParams transformParams = this.mHomeAlphaParams;
                transformParams.applySurfaceParams(transformParams.createSurfaceParams(TransformParams.BuilderProxy.NO_OP));
            }
        }

        public boolean handleHomeTaskAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargetCompatArr[0];
            if (remoteAnimationTargetCompat.activityType != 2) {
                return false;
            }
            this.mHomeAlphaParams.setTargetSet(new RemoteAnimationTargets(new RemoteAnimationTargetCompat[]{remoteAnimationTargetCompat}, new RemoteAnimationTargetCompat[0], new RemoteAnimationTargetCompat[0], remoteAnimationTargetCompat.mode));
            updateHomeAlpha();
            return true;
        }

        public void playAtomicAnimation(float f) {
            AnimatedFloat animatedFloat = this.mHomeAlpha;
            ObjectAnimator animateToValue = animatedFloat.animateToValue(animatedFloat.value, 1.0f);
            animateToValue.setDuration(this.mDuration).setInterpolator(Interpolators.ACCEL);
            animateToValue.start();
            if (FallbackSwipeHandler.this.mRunningOverHome) {
                new SpringAnimationBuilder(FallbackSwipeHandler.this.mContext).setStartValue(this.mVerticalShiftForScale.value).setEndValue(0.0f).setStartVelocity((-f) / ((float) FallbackSwipeHandler.this.mTransitionDragLength)).setMinimumVisibleChange(1.0f / ((float) FallbackSwipeHandler.this.mDp.heightPx)).setDampingRatio(0.6f).setStiffness(800.0f).build(this.mVerticalShiftForScale, AnimatedFloat.VALUE).start();
            }
        }

        public void setAnimation(RectFSpringAnim rectFSpringAnim) {
            this.mSpringAnim = rectFSpringAnim;
            rectFSpringAnim.addAnimatorListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
                public final void run() {
                    FallbackSwipeHandler.FallbackHomeAnimationFactory.this.onRectAnimationEnd();
                }
            }));
        }

        /* access modifiers changed from: private */
        public void onRectAnimationEnd() {
            this.mAnimationFinished = true;
            maybeSendEndMessage();
        }

        private void maybeSendEndMessage() {
            Message message;
            if (this.mAnimationFinished && (message = this.mOnFinishCallback) != null) {
                try {
                    message.replyTo.send(this.mOnFinishCallback);
                } catch (RemoteException e) {
                    Log.e(FallbackSwipeHandler.TAG, "Error sending icon position", e);
                }
            }
        }

        public void accept(Message message) {
            try {
                Bundle data = message.getData();
                RectF rectF = (RectF) data.getParcelable(GestureNavContract.EXTRA_ICON_POSITION);
                if (!rectF.isEmpty()) {
                    this.mSurfaceControl = (SurfaceControl) data.getParcelable(GestureNavContract.EXTRA_ICON_SURFACE);
                    this.mTargetRect.set(rectF);
                    RectFSpringAnim rectFSpringAnim = this.mSpringAnim;
                    if (rectFSpringAnim != null) {
                        rectFSpringAnim.onTargetPositionChanged();
                    }
                    this.mOnFinishCallback = (Message) data.getParcelable(GestureNavContract.EXTRA_ON_FINISH_CALLBACK);
                }
                maybeSendEndMessage();
            } catch (Exception unused) {
            }
        }

        public void update(RectF rectF, float f, float f2) {
            if (this.mSurfaceControl != null) {
                rectF.roundOut(this.mTempRect);
                SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                try {
                    transaction.setGeometry(this.mSurfaceControl, (Rect) null, this.mTempRect, 0);
                    transaction.apply();
                } catch (RuntimeException unused) {
                }
            }
        }

        /* access modifiers changed from: private */
        public void addGestureContract(Intent intent, ActivityManager.RunningTaskInfo runningTaskInfo) {
            if (!FallbackSwipeHandler.this.mRunningOverHome && runningTaskInfo != null) {
                Task.TaskKey taskKey = new Task.TaskKey(runningTaskInfo);
                if (taskKey.getComponent() != null) {
                    if (FallbackSwipeHandler.sMessageReceiver == null) {
                        StaticMessageReceiver unused = FallbackSwipeHandler.sMessageReceiver = new StaticMessageReceiver();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("android.intent.extra.COMPONENT_NAME", taskKey.getComponent());
                    bundle.putParcelable("android.intent.extra.USER", UserHandle.of(taskKey.userId));
                    bundle.putParcelable(GestureNavContract.EXTRA_REMOTE_CALLBACK, FallbackSwipeHandler.sMessageReceiver.newCallback(this));
                    intent.putExtra(GestureNavContract.EXTRA_GESTURE_CONTRACT, bundle);
                }
            }
        }
    }

    private static class StaticMessageReceiver implements Handler.Callback {
        private WeakReference<Consumer<Message>> mCurrentCallback;
        private ParcelUuid mCurrentUID;
        private final Messenger mMessenger;

        private StaticMessageReceiver() {
            this.mMessenger = new Messenger(new Handler(Looper.getMainLooper(), this));
            this.mCurrentUID = new ParcelUuid(UUID.randomUUID());
            this.mCurrentCallback = new WeakReference<>((Object) null);
        }

        public Message newCallback(Consumer<Message> consumer) {
            this.mCurrentUID = new ParcelUuid(UUID.randomUUID());
            this.mCurrentCallback = new WeakReference<>(consumer);
            Message obtain = Message.obtain();
            obtain.replyTo = this.mMessenger;
            obtain.obj = this.mCurrentUID;
            return obtain;
        }

        public boolean handleMessage(Message message) {
            Consumer consumer;
            if (!this.mCurrentUID.equals(message.obj) || (consumer = (Consumer) this.mCurrentCallback.get()) == null) {
                return false;
            }
            consumer.accept(message);
            return true;
        }
    }
}
