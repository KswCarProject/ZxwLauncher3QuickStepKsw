package com.android.launcher3.statehandlers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.FloatProperty;
import android.view.AttachedSurfaceControl;
import android.view.CrossWindowBlurListeners;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.systemui.shared.system.BlurUtils;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class DepthController implements StateManager.StateHandler<LauncherState>, BaseActivity.MultiWindowModeChangedListener {
    public static final FloatProperty<DepthController> DEPTH = new FloatProperty<DepthController>("depth") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(DepthController depthController, float f) {
            depthController.setDepth(f);
        }

        public Float get(DepthController depthController) {
            return Float.valueOf(depthController.mDepth);
        }
    };
    private static final boolean OVERLAY_SCROLL_ENABLED = false;
    private boolean mBlurDisabledForAppLaunch;
    /* access modifiers changed from: private */
    public final Consumer<Boolean> mCrossWindowBlurListener = new Consumer<Boolean>() {
        public void accept(Boolean bool) {
            boolean unused = DepthController.this.mCrossWindowBlursEnabled = bool.booleanValue();
            DepthController depthController = DepthController.this;
            boolean unused2 = depthController.dispatchTransactionSurface(depthController.mDepth);
        }
    };
    /* access modifiers changed from: private */
    public boolean mCrossWindowBlursEnabled;
    private int mCurrentBlur;
    /* access modifiers changed from: private */
    public float mDepth;
    private boolean mHasContentBehindLauncher;
    /* access modifiers changed from: private */
    public boolean mIgnoreStateChangesDuringMultiWindowAnimation = false;
    private boolean mInEarlyWakeUp;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    private int mMaxBlurRadius;
    private View.OnAttachStateChangeListener mOnAttachListener;
    private final ViewTreeObserver.OnDrawListener mOnDrawListener = new ViewTreeObserver.OnDrawListener() {
        public void onDraw() {
            DragLayer dragLayer = DepthController.this.mLauncher.getDragLayer();
            ViewRootImpl viewRootImpl = dragLayer.getViewRootImpl();
            if (!DepthController.this.setSurface(viewRootImpl != null ? viewRootImpl.getSurfaceControl() : null)) {
                DepthController depthController = DepthController.this;
                boolean unused = depthController.dispatchTransactionSurface(depthController.mDepth);
            }
            dragLayer.post(new Runnable(dragLayer) {
                public final /* synthetic */ View f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DepthController.AnonymousClass2.this.lambda$onDraw$0$DepthController$2(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onDraw$0$DepthController$2(View view) {
            view.getViewTreeObserver().removeOnDrawListener(this);
        }
    };
    /* access modifiers changed from: private */
    public final Runnable mOpaquenessListener = new Runnable() {
        public void run() {
            DepthController depthController = DepthController.this;
            boolean unused = depthController.dispatchTransactionSurface(depthController.mDepth);
        }
    };
    private float mOverlayScrollProgress;
    private SurfaceControl mSurface;
    /* access modifiers changed from: private */
    public WallpaperManager mWallpaperManager;

    public void onOverlayScrollChanged(float f) {
    }

    public static class ClampedDepthProperty extends FloatProperty<DepthController> {
        private final float mMaxValue;
        private final float mMinValue;

        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public ClampedDepthProperty(float f, float f2) {
            super("depthClamped");
            this.mMinValue = f;
            this.mMaxValue = f2;
        }

        public void setValue(DepthController depthController, float f) {
            depthController.setDepth(Utilities.boundToRange(f, this.mMinValue, this.mMaxValue));
        }

        public Float get(DepthController depthController) {
            return Float.valueOf(depthController.mDepth);
        }
    }

    public DepthController(Launcher launcher) {
        this.mLauncher = launcher;
    }

    private void ensureDependencies() {
        if (this.mWallpaperManager == null) {
            this.mMaxBlurRadius = this.mLauncher.getResources().getInteger(R.integer.max_depth_blur_radius);
            this.mWallpaperManager = (WallpaperManager) this.mLauncher.getSystemService(WallpaperManager.class);
        }
        if (this.mLauncher.getRootView() != null && this.mOnAttachListener == null) {
            this.mOnAttachListener = new View.OnAttachStateChangeListener() {
                public void onViewAttachedToWindow(View view) {
                    IBinder windowToken = DepthController.this.mLauncher.getRootView().getWindowToken();
                    if (windowToken != null) {
                        DepthController.this.mWallpaperManager.setWallpaperZoomOut(windowToken, DepthController.this.mDepth);
                    }
                    DepthController.this.onAttached();
                }

                public void onViewDetachedFromWindow(View view) {
                    CrossWindowBlurListeners.getInstance().removeListener(DepthController.this.mCrossWindowBlurListener);
                    DepthController.this.mLauncher.getScrimView().removeOpaquenessListener(DepthController.this.mOpaquenessListener);
                }
            };
            this.mLauncher.getRootView().addOnAttachStateChangeListener(this.mOnAttachListener);
            if (this.mLauncher.getRootView().isAttachedToWindow()) {
                onAttached();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onAttached() {
        CrossWindowBlurListeners.getInstance().addListener(this.mLauncher.getMainExecutor(), this.mCrossWindowBlurListener);
        this.mLauncher.getScrimView().addOpaquenessListener(this.mOpaquenessListener);
    }

    public void setHasContentBehindLauncher(boolean z) {
        this.mHasContentBehindLauncher = z;
    }

    public void setActivityStarted(boolean z) {
        if (z) {
            this.mLauncher.getDragLayer().getViewTreeObserver().addOnDrawListener(this.mOnDrawListener);
            return;
        }
        this.mLauncher.getDragLayer().getViewTreeObserver().removeOnDrawListener(this.mOnDrawListener);
        setSurface((SurfaceControl) null);
    }

    public boolean setSurface(SurfaceControl surfaceControl) {
        if (surfaceControl == null) {
            ViewRootImpl viewRootImpl = this.mLauncher.getDragLayer().getViewRootImpl();
            surfaceControl = viewRootImpl != null ? viewRootImpl.getSurfaceControl() : null;
        }
        if (this.mSurface == surfaceControl) {
            return false;
        }
        this.mSurface = surfaceControl;
        if (surfaceControl == null) {
            return false;
        }
        dispatchTransactionSurface(this.mDepth);
        return true;
    }

    public void setState(LauncherState launcherState) {
        if (this.mSurface != null && !this.mIgnoreStateChangesDuringMultiWindowAnimation) {
            float depth = launcherState.getDepth(this.mLauncher);
            if (Float.compare(this.mDepth, depth) != 0) {
                setDepth(depth);
            } else if (launcherState == LauncherState.OVERVIEW) {
                dispatchTransactionSurface(this.mDepth);
            } else if (launcherState == LauncherState.BACKGROUND_APP) {
                this.mLauncher.getDragLayer().getViewTreeObserver().addOnDrawListener(this.mOnDrawListener);
            }
        }
    }

    public void setStateWithAnimation(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        if (!stateAnimationConfig.hasAnimationFlag(4) && !this.mIgnoreStateChangesDuringMultiWindowAnimation) {
            float depth = launcherState.getDepth(this.mLauncher);
            if (Float.compare(this.mDepth, depth) != 0) {
                pendingAnimation.setFloat(this, DEPTH, depth, stateAnimationConfig.getInterpolator(13, Interpolators.LINEAR));
            }
        }
    }

    public void setIsInLaunchTransition(boolean z) {
        boolean z2 = true;
        boolean z3 = SystemProperties.getBoolean("ro.launcher.blur.appLaunch", true);
        if (!z || z3) {
            z2 = false;
        }
        this.mBlurDisabledForAppLaunch = z2;
        if (!z) {
            setDepth(0.0f);
        }
    }

    /* access modifiers changed from: private */
    public void setDepth(float f) {
        float boundToRange = ((float) ((int) (Utilities.boundToRange(f, 0.0f, 1.0f) * 256.0f))) / 256.0f;
        if (Float.compare(this.mDepth, boundToRange) != 0) {
            dispatchTransactionSurface(boundToRange);
            this.mDepth = boundToRange;
        }
    }

    /* access modifiers changed from: private */
    public boolean dispatchTransactionSurface(float f) {
        SurfaceControl surfaceControl;
        boolean supportsBlursOnWindows = BlurUtils.supportsBlursOnWindows();
        if (supportsBlursOnWindows && ((surfaceControl = this.mSurface) == null || !surfaceControl.isValid())) {
            return false;
        }
        ensureDependencies();
        float max = Math.max(f, this.mOverlayScrollProgress);
        IBinder windowToken = this.mLauncher.getRootView().getWindowToken();
        if (windowToken != null) {
            this.mWallpaperManager.setWallpaperZoomOut(windowToken, max);
        }
        if (supportsBlursOnWindows) {
            boolean isFullyOpaque = this.mLauncher.getScrimView().isFullyOpaque();
            boolean z = !this.mHasContentBehindLauncher && isFullyOpaque;
            this.mCurrentBlur = (!this.mCrossWindowBlursEnabled || this.mBlurDisabledForAppLaunch || isFullyOpaque) ? 0 : (int) (((float) this.mMaxBlurRadius) * max);
            SurfaceControl.Transaction opaque = new SurfaceControl.Transaction().setBackgroundBlurRadius(this.mSurface, this.mCurrentBlur).setOpaque(this.mSurface, z);
            boolean z2 = max > 0.0f && max < 1.0f;
            if (z2 && !this.mInEarlyWakeUp) {
                opaque.setEarlyWakeupStart();
                this.mInEarlyWakeUp = true;
            } else if (!z2 && this.mInEarlyWakeUp) {
                opaque.setEarlyWakeupEnd();
                this.mInEarlyWakeUp = false;
            }
            AttachedSurfaceControl rootSurfaceControl = this.mLauncher.getRootView().getRootSurfaceControl();
            if (rootSurfaceControl != null) {
                rootSurfaceControl.applyTransactionOnDraw(opaque);
            }
        }
        return true;
    }

    public void onMultiWindowModeChanged(boolean z) {
        this.mIgnoreStateChangesDuringMultiWindowAnimation = true;
        ObjectAnimator duration = ObjectAnimator.ofFloat(this, DEPTH, new float[]{this.mLauncher.getStateManager().getState().getDepth(this.mLauncher, z)}).setDuration(300);
        duration.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                boolean unused = DepthController.this.mIgnoreStateChangesDuringMultiWindowAnimation = false;
            }
        });
        duration.setAutoCancel(true);
        duration.start();
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + getClass().getSimpleName());
        printWriter.println(str + "\tmMaxBlurRadius=" + this.mMaxBlurRadius);
        printWriter.println(str + "\tmCrossWindowBlursEnabled=" + this.mCrossWindowBlursEnabled);
        printWriter.println(str + "\tmSurface=" + this.mSurface);
        printWriter.println(str + "\tmOverlayScrollProgress=" + this.mOverlayScrollProgress);
        printWriter.println(str + "\tmDepth=" + this.mDepth);
        printWriter.println(str + "\tmCurrentBlur=" + this.mCurrentBlur);
        printWriter.println(str + "\tmBlurDisabledForAppLaunch=" + this.mBlurDisabledForAppLaunch);
        printWriter.println(str + "\tmInEarlyWakeUp=" + this.mInEarlyWakeUp);
        printWriter.println(str + "\tmIgnoreStateChangesDuringMultiWindowAnimation=" + this.mIgnoreStateChangesDuringMultiWindowAnimation);
    }
}
