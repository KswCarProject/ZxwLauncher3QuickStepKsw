package com.android.quickstep;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.Arrays;
import java.util.function.Consumer;

public abstract class SwipeUpAnimationLogic implements RecentsAnimationCallbacks.RecentsAnimationListener {
    protected static final Rect TEMP_RECT = new Rect();
    protected final Context mContext;
    protected final AnimatedFloat mCurrentShift = new AnimatedFloat(new Runnable() {
        public final void run() {
            SwipeUpAnimationLogic.this.updateFinalShift();
        }
    });
    protected final RecentsAnimationDeviceState mDeviceState;
    protected DeviceProfile mDp;
    protected float mDragLengthFactor = 1.0f;
    protected final GestureState mGestureState;
    protected boolean mIsSwipeForStagedSplit;
    protected RemoteTargetGluer.RemoteTargetHandle[] mRemoteTargetHandles;
    protected final RemoteTargetGluer mTargetGluer;
    protected int mTransitionDragLength;

    public abstract void updateFinalShift();

    public SwipeUpAnimationLogic(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, GestureState gestureState) {
        this.mContext = context;
        this.mDeviceState = recentsAnimationDeviceState;
        this.mGestureState = gestureState;
        boolean z = true;
        this.mIsSwipeForStagedSplit = (!FeatureFlags.ENABLE_SPLIT_SELECT.get() || TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getRunningSplitTaskIds().length <= 1) ? false : z;
        RemoteTargetGluer remoteTargetGluer = new RemoteTargetGluer(context, gestureState.getActivityInterface());
        this.mTargetGluer = remoteTargetGluer;
        this.mRemoteTargetHandles = remoteTargetGluer.getRemoteTargetHandles();
        runActionOnRemoteHandles(new Consumer() {
            public final void accept(Object obj) {
                SwipeUpAnimationLogic.this.lambda$new$0$SwipeUpAnimationLogic((RemoteTargetGluer.RemoteTargetHandle) obj);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$SwipeUpAnimationLogic(RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle) {
        remoteTargetHandle.getTaskViewSimulator().getOrientationState().update(this.mDeviceState.getRotationTouchHelper().getCurrentActiveRotation(), this.mDeviceState.getRotationTouchHelper().getDisplayRotation());
    }

    /* access modifiers changed from: protected */
    public void initTransitionEndpoints(DeviceProfile deviceProfile) {
        DeviceProfile deviceProfile2 = deviceProfile;
        this.mDp = deviceProfile2;
        this.mTransitionDragLength = this.mGestureState.getActivityInterface().getSwipeUpDestinationAndLength(deviceProfile2, this.mContext, TEMP_RECT, this.mRemoteTargetHandles[0].getTaskViewSimulator().getOrientationState().getOrientationHandler());
        this.mDragLengthFactor = ((float) deviceProfile2.heightPx) / ((float) this.mTransitionDragLength);
        for (RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle : this.mRemoteTargetHandles) {
            PendingAnimation pendingAnimation = new PendingAnimation((long) (this.mTransitionDragLength * 2));
            TaskViewSimulator taskViewSimulator = remoteTargetHandle.getTaskViewSimulator();
            taskViewSimulator.setDp(deviceProfile2);
            taskViewSimulator.addAppToOverviewAnim(pendingAnimation, Interpolators.LINEAR);
            remoteTargetHandle.setPlaybackController(AnimatorControllerWithResistance.createForRecents(pendingAnimation.createPlaybackController(), this.mContext, taskViewSimulator.getOrientationState(), this.mDp, taskViewSimulator.recentsViewScale, AnimatedFloat.VALUE, taskViewSimulator.recentsViewSecondaryTranslation, AnimatedFloat.VALUE));
        }
    }

    public void updateDisplacement(float f) {
        float f2 = -f;
        int i = this.mTransitionDragLength;
        float f3 = this.mDragLengthFactor;
        if (f2 <= ((float) i) * f3 || i <= 0) {
            float max = Math.max(f2, 0.0f);
            int i2 = this.mTransitionDragLength;
            f3 = i2 == 0 ? 0.0f : max / ((float) i2);
        }
        this.mCurrentShift.updateValue(f3);
    }

    /* access modifiers changed from: protected */
    public PagedOrientationHandler getOrientationHandler() {
        return this.mRemoteTargetHandles[0].getTaskViewSimulator().getOrientationState().getOrientationHandler();
    }

    protected abstract class HomeAnimationFactory {
        protected float mSwipeVelocity;

        public abstract AnimatorPlaybackController createActivityAnimationToHome();

        public void onCancel() {
        }

        public void playAtomicAnimation(float f) {
        }

        public void setAnimation(RectFSpringAnim rectFSpringAnim) {
        }

        public void update(RectF rectF, float f, float f2) {
        }

        protected HomeAnimationFactory() {
        }

        public RectF getWindowTargetRect() {
            PagedOrientationHandler orientationHandler = SwipeUpAnimationLogic.this.getOrientationHandler();
            DeviceProfile deviceProfile = SwipeUpAnimationLogic.this.mDp;
            float primaryValue = ((float) orientationHandler.getPrimaryValue(deviceProfile.availableWidthPx, deviceProfile.availableHeightPx)) / 2.0f;
            float secondaryValue = ((float) orientationHandler.getSecondaryValue(deviceProfile.availableWidthPx, deviceProfile.availableHeightPx)) - ((float) deviceProfile.hotseatBarSizePx);
            float f = (float) (deviceProfile.iconSizePx / 2);
            return new RectF(primaryValue - f, secondaryValue - f, primaryValue + f, secondaryValue + f);
        }

        public float getEndRadius(RectF rectF) {
            return rectF.width() / 2.0f;
        }

        public void setSwipeVelocity(float f) {
            this.mSwipeVelocity = f;
        }

        /* access modifiers changed from: protected */
        public float getWindowAlpha(float f) {
            if (f <= 0.0f) {
                return 1.0f;
            }
            if (f >= 0.85f) {
                return 0.0f;
            }
            return Utilities.mapToRange(f, 0.0f, 0.85f, 1.0f, 0.0f, Interpolators.ACCEL_1_5);
        }
    }

    /* access modifiers changed from: protected */
    public RectF[] updateProgressForStartRect(Matrix[] matrixArr, float f) {
        this.mCurrentShift.updateValue(f);
        RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr = this.mRemoteTargetHandles;
        RectF[] rectFArr = new RectF[remoteTargetHandleArr.length];
        int length = remoteTargetHandleArr.length;
        for (int i = 0; i < length; i++) {
            RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle = this.mRemoteTargetHandles[i];
            TaskViewSimulator taskViewSimulator = remoteTargetHandle.getTaskViewSimulator();
            taskViewSimulator.apply(remoteTargetHandle.getTransformParams().setProgress(f));
            rectFArr[i] = new RectF(taskViewSimulator.getCurrentCropRect());
            matrixArr[i] = new Matrix();
            taskViewSimulator.applyWindowToHomeRotation(matrixArr[i]);
            taskViewSimulator.getCurrentMatrix().mapRect(rectFArr[i]);
        }
        return rectFArr;
    }

    /* access modifiers changed from: protected */
    public void runActionOnRemoteHandles(Consumer<RemoteTargetGluer.RemoteTargetHandle> consumer) {
        for (RemoteTargetGluer.RemoteTargetHandle accept : this.mRemoteTargetHandles) {
            consumer.accept(accept);
        }
    }

    /* access modifiers changed from: protected */
    public TaskViewSimulator[] getRemoteTaskViewSimulators() {
        return (TaskViewSimulator[]) Arrays.stream(this.mRemoteTargetHandles).map($$Lambda$SwipeUpAnimationLogic$8VV8L7vok6ys3zNOwWGEkKM2ik.INSTANCE).toArray($$Lambda$SwipeUpAnimationLogic$Syij3Cn5ykG9U9a6bNJDOzfM4ms.INSTANCE);
    }

    static /* synthetic */ TaskViewSimulator[] lambda$getRemoteTaskViewSimulators$2(int i) {
        return new TaskViewSimulator[i];
    }

    /* access modifiers changed from: protected */
    public RectFSpringAnim[] createWindowAnimationToHome(float f, HomeAnimationFactory homeAnimationFactory) {
        RectF windowTargetRect = homeAnimationFactory.getWindowTargetRect();
        RemoteTargetGluer.RemoteTargetHandle[] remoteTargetHandleArr = this.mRemoteTargetHandles;
        RectFSpringAnim[] rectFSpringAnimArr = new RectFSpringAnim[remoteTargetHandleArr.length];
        Matrix[] matrixArr = new Matrix[remoteTargetHandleArr.length];
        RectF[] updateProgressForStartRect = updateProgressForStartRect(matrixArr, f);
        int length = this.mRemoteTargetHandles.length;
        for (int i = 0; i < length; i++) {
            RemoteTargetGluer.RemoteTargetHandle remoteTargetHandle = this.mRemoteTargetHandles[i];
            rectFSpringAnimArr[i] = getWindowAnimationToHomeInternal(homeAnimationFactory, windowTargetRect, remoteTargetHandle.getTransformParams(), remoteTargetHandle.getTaskViewSimulator(), updateProgressForStartRect[i], matrixArr[i]);
        }
        return rectFSpringAnimArr;
    }

    private RectFSpringAnim getWindowAnimationToHomeInternal(HomeAnimationFactory homeAnimationFactory, RectF rectF, TransformParams transformParams, TaskViewSimulator taskViewSimulator, RectF rectF2, Matrix matrix) {
        RectF rectF3 = new RectF(taskViewSimulator.getCurrentCropRect());
        Matrix matrix2 = new Matrix();
        this.mRemoteTargetHandles[0].getTaskViewSimulator().getOrientationState().getOrientationHandler().fixBoundsForHomeAnimStartRect(rectF2, this.mDp);
        matrix.invert(matrix2);
        matrix2.mapRect(rectF2);
        RectFSpringAnim rectFSpringAnim = new RectFSpringAnim(rectF2, rectF, this.mContext, this.mDp);
        homeAnimationFactory.setAnimation(rectFSpringAnim);
        SpringAnimationRunner springAnimationRunner = new SpringAnimationRunner(homeAnimationFactory, rectF3, matrix, transformParams, taskViewSimulator);
        rectFSpringAnim.addAnimatorListener(springAnimationRunner);
        rectFSpringAnim.addOnUpdateListener(springAnimationRunner);
        return rectFSpringAnim;
    }

    protected class SpringAnimationRunner extends AnimationSuccessListener implements RectFSpringAnim.OnUpdateListener, TransformParams.BuilderProxy {
        final HomeAnimationFactory mAnimationFactory;
        final Rect mCropRect;
        final RectF mCropRectF;
        final float mEndRadius;
        final AnimatorPlaybackController mHomeAnim;
        final Matrix mHomeToWindowPositionMap;
        private final TransformParams mLocalTransformParams;
        final Matrix mMatrix = new Matrix();
        final float mStartRadius;
        final RectF mWindowCurrentRect = new RectF();

        SpringAnimationRunner(HomeAnimationFactory homeAnimationFactory, RectF rectF, Matrix matrix, TransformParams transformParams, TaskViewSimulator taskViewSimulator) {
            Rect rect = new Rect();
            this.mCropRect = rect;
            this.mAnimationFactory = homeAnimationFactory;
            this.mHomeAnim = homeAnimationFactory.createActivityAnimationToHome();
            this.mCropRectF = rectF;
            this.mHomeToWindowPositionMap = matrix;
            this.mLocalTransformParams = transformParams;
            rectF.roundOut(rect);
            this.mStartRadius = taskViewSimulator.getCurrentCornerRadius();
            this.mEndRadius = homeAnimationFactory.getEndRadius(rectF);
        }

        public void onUpdate(RectF rectF, float f) {
            this.mHomeAnim.setPlayFraction(f);
            this.mHomeToWindowPositionMap.mapRect(this.mWindowCurrentRect, rectF);
            this.mMatrix.setRectToRect(this.mCropRectF, this.mWindowCurrentRect, Matrix.ScaleToFit.FILL);
            float mapRange = Utilities.mapRange(f, this.mStartRadius, this.mEndRadius);
            this.mLocalTransformParams.setTargetAlpha(this.mAnimationFactory.getWindowAlpha(f)).setCornerRadius(mapRange);
            TransformParams transformParams = this.mLocalTransformParams;
            transformParams.applySurfaceParams(transformParams.createSurfaceParams(this));
            this.mAnimationFactory.update(rectF, f, this.mMatrix.mapRadius(mapRange));
        }

        public void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
            builder.withMatrix(this.mMatrix).withWindowCrop(this.mCropRect).withCornerRadius(transformParams.getCornerRadius());
        }

        public void onCancel() {
            this.mAnimationFactory.onCancel();
        }

        public void onAnimationStart(Animator animator) {
            this.mHomeAnim.dispatchOnStart();
        }

        public void onAnimationSuccess(Animator animator) {
            this.mHomeAnim.getAnimationPlayer().end();
        }
    }

    public interface RunningWindowAnim {
        void cancel();

        void end();

        static RunningWindowAnim wrap(final Animator animator) {
            return new RunningWindowAnim() {
                public void end() {
                    animator.end();
                }

                public void cancel() {
                    animator.cancel();
                }
            };
        }

        static RunningWindowAnim wrap(final RectFSpringAnim rectFSpringAnim) {
            return new RunningWindowAnim() {
                public void end() {
                    RectFSpringAnim.this.end();
                }

                public void cancel() {
                    RectFSpringAnim.this.cancel();
                }
            };
        }
    }
}
