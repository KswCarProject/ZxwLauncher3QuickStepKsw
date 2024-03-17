package com.android.quickstep;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.quickstep.GestureState;
import com.android.quickstep.OrientationTouchTransformer;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class RotationTouchHelper implements DisplayController.DisplayInfoChangeListener {
    public static final MainThreadInitializedObject<RotationTouchHelper> INSTANCE = new MainThreadInitializedObject<>($$Lambda$RotationTouchHelper$tPFEMsbousNZYQBdRd6mUXtGUyk.INSTANCE);
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentAppRotation = -1;
    private DisplayController mDisplayController;
    /* access modifiers changed from: private */
    public int mDisplayId;
    private int mDisplayRotation;
    /* access modifiers changed from: private */
    public Runnable mExitOverviewRunnable = new Runnable() {
        public void run() {
            boolean unused = RotationTouchHelper.this.mInOverview = false;
            RotationTouchHelper.this.enableMultipleRegions(false);
        }
    };
    private TaskStackChangeListener mFrozenTaskListener = new TaskStackChangeListener() {
        public void onRecentTaskListFrozenChanged(boolean z) {
            boolean unused = RotationTouchHelper.this.mTaskListFrozen = z;
            if (!z && !RotationTouchHelper.this.mInOverview) {
                RotationTouchHelper.this.enableMultipleRegions(false);
            }
        }

        public void onActivityRotation(int i) {
            if (i == RotationTouchHelper.this.mDisplayId) {
                boolean unused = RotationTouchHelper.this.mPrioritizeDeviceRotation = true;
                if (RotationTouchHelper.this.mInOverview) {
                    RotationTouchHelper.this.mExitOverviewRunnable.run();
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mInOverview;
    private DisplayController.NavigationMode mMode = DisplayController.NavigationMode.THREE_BUTTONS;
    private boolean mNeedsInit = true;
    private final ArrayList<Runnable> mOnDestroyActions = new ArrayList<>();
    private Runnable mOnDestroyFrozenTaskRunnable;
    private OrientationEventListener mOrientationListener;
    private OrientationTouchTransformer mOrientationTouchTransformer;
    /* access modifiers changed from: private */
    public boolean mPrioritizeDeviceRotation = false;
    /* access modifiers changed from: private */
    public int mSensorRotation = 0;
    /* access modifiers changed from: private */
    public boolean mTaskListFrozen;

    public static /* synthetic */ RotationTouchHelper lambda$tPFEMsbousNZYQBdRd6mUXtGUyk(Context context) {
        return new RotationTouchHelper(context);
    }

    private RotationTouchHelper(Context context) {
        this.mContext = context;
        if (1 != 0) {
            init();
        }
    }

    public void init() {
        if (this.mNeedsInit) {
            this.mDisplayController = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext);
            Resources resources = this.mContext.getResources();
            this.mDisplayId = 0;
            this.mOrientationTouchTransformer = new OrientationTouchTransformer(resources, this.mMode, new OrientationTouchTransformer.QuickStepContractInfo() {
                public final float getWindowCornerRadius() {
                    return RotationTouchHelper.this.lambda$init$0$RotationTouchHelper();
                }
            });
            this.mDisplayController.addChangeListener(this);
            DisplayController.Info info = this.mDisplayController.getInfo();
            onDisplayInfoChangedInternal(info, 31, info.navigationMode.hasGestures);
            runOnDestroy(new Runnable() {
                public final void run() {
                    RotationTouchHelper.this.lambda$init$1$RotationTouchHelper();
                }
            });
            this.mOrientationListener = new OrientationEventListener(this.mContext) {
                public void onOrientationChanged(int i) {
                    int rotationForUserDegreesRotated = RecentsOrientedState.getRotationForUserDegreesRotated((float) i, RotationTouchHelper.this.mSensorRotation);
                    if (rotationForUserDegreesRotated != RotationTouchHelper.this.mSensorRotation) {
                        int unused = RotationTouchHelper.this.mSensorRotation = rotationForUserDegreesRotated;
                        boolean unused2 = RotationTouchHelper.this.mPrioritizeDeviceRotation = true;
                        if (rotationForUserDegreesRotated == RotationTouchHelper.this.mCurrentAppRotation) {
                            RotationTouchHelper.this.toggleSecondaryNavBarsForRotation();
                        }
                    }
                }
            };
            this.mNeedsInit = false;
        }
    }

    public /* synthetic */ float lambda$init$0$RotationTouchHelper() {
        return QuickStepContract.getWindowCornerRadius(this.mContext);
    }

    public /* synthetic */ void lambda$init$1$RotationTouchHelper() {
        this.mDisplayController.removeChangeListener(this);
    }

    private void setupOrientationSwipeHandler() {
        TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mFrozenTaskListener);
        $$Lambda$RotationTouchHelper$1Fnwh__xeGOVDjZLxvzC1iV2pZQ r0 = new Runnable() {
            public final void run() {
                RotationTouchHelper.this.lambda$setupOrientationSwipeHandler$2$RotationTouchHelper();
            }
        };
        this.mOnDestroyFrozenTaskRunnable = r0;
        runOnDestroy(r0);
    }

    public /* synthetic */ void lambda$setupOrientationSwipeHandler$2$RotationTouchHelper() {
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mFrozenTaskListener);
    }

    private void destroyOrientationSwipeHandlerCallback() {
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mFrozenTaskListener);
        this.mOnDestroyActions.remove(this.mOnDestroyFrozenTaskRunnable);
    }

    private void runOnDestroy(Runnable runnable) {
        this.mOnDestroyActions.add(runnable);
    }

    public void destroy() {
        Iterator<Runnable> it = this.mOnDestroyActions.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mNeedsInit = true;
    }

    public boolean isTaskListFrozen() {
        return this.mTaskListFrozen;
    }

    public boolean touchInAssistantRegion(MotionEvent motionEvent) {
        return this.mOrientationTouchTransformer.touchInAssistantRegion(motionEvent);
    }

    public boolean touchInOneHandedModeRegion(MotionEvent motionEvent) {
        return this.mOrientationTouchTransformer.touchInOneHandedModeRegion(motionEvent);
    }

    public void updateGestureTouchRegions() {
        if (this.mMode.hasGestures) {
            this.mOrientationTouchTransformer.createOrAddTouchRegion(this.mDisplayController.getInfo());
        }
    }

    public boolean isInSwipeUpTouchRegion(MotionEvent motionEvent) {
        return this.mOrientationTouchTransformer.touchInValidSwipeRegions(motionEvent.getX(), motionEvent.getY());
    }

    public boolean isInSwipeUpTouchRegion(MotionEvent motionEvent, int i) {
        return this.mOrientationTouchTransformer.touchInValidSwipeRegions(motionEvent.getX(i), motionEvent.getY(i));
    }

    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
        onDisplayInfoChangedInternal(info, i, false);
    }

    private void onDisplayInfoChangedInternal(DisplayController.Info info, int i, boolean z) {
        if ((i & 27) != 0) {
            this.mDisplayRotation = info.rotation;
            if (this.mMode.hasGestures) {
                updateGestureTouchRegions();
                this.mOrientationTouchTransformer.createOrAddTouchRegion(info);
                int i2 = this.mDisplayRotation;
                this.mCurrentAppRotation = i2;
                if ((this.mPrioritizeDeviceRotation || i2 == this.mSensorRotation) && !this.mInOverview && this.mTaskListFrozen) {
                    toggleSecondaryNavBarsForRotation();
                }
            }
        }
        if ((i & 16) != 0) {
            DisplayController.NavigationMode navigationMode = info.navigationMode;
            this.mOrientationTouchTransformer.setNavigationMode(navigationMode, this.mDisplayController.getInfo(), this.mContext.getResources());
            if (z || (!this.mMode.hasGestures && navigationMode.hasGestures)) {
                setupOrientationSwipeHandler();
            } else if (this.mMode.hasGestures && !navigationMode.hasGestures) {
                destroyOrientationSwipeHandlerCallback();
            }
            this.mMode = navigationMode;
        }
    }

    public int getDisplayRotation() {
        return this.mDisplayRotation;
    }

    /* access modifiers changed from: package-private */
    public void setGesturalHeight(int i) {
        this.mOrientationTouchTransformer.setGesturalHeight(i, this.mDisplayController.getInfo(), this.mContext.getResources());
    }

    /* access modifiers changed from: package-private */
    public void setOrientationTransformIfNeeded(MotionEvent motionEvent) {
        if (motionEvent.getX() < 0.0f || motionEvent.getY() < 0.0f) {
            motionEvent.setLocation(Math.max(0.0f, motionEvent.getX()), Math.max(0.0f, motionEvent.getY()));
        }
        this.mOrientationTouchTransformer.transform(motionEvent);
    }

    /* access modifiers changed from: private */
    public void enableMultipleRegions(boolean z) {
        this.mOrientationTouchTransformer.enableMultipleRegions(z, this.mDisplayController.getInfo());
        notifySysuiOfCurrentRotation(this.mOrientationTouchTransformer.getQuickStepStartingRotation());
        if (!z || this.mInOverview || TestProtocol.sDisableSensorRotation) {
            this.mOrientationListener.disable();
            return;
        }
        this.mSensorRotation = this.mCurrentAppRotation;
        this.mOrientationListener.enable();
    }

    public void onStartGesture() {
        if (this.mTaskListFrozen) {
            notifySysuiOfCurrentRotation(this.mOrientationTouchTransformer.getCurrentActiveRotation());
        }
    }

    /* access modifiers changed from: package-private */
    public void onEndTargetCalculated(GestureState.GestureEndTarget gestureEndTarget, BaseActivityInterface baseActivityInterface) {
        if (gestureEndTarget == GestureState.GestureEndTarget.RECENTS) {
            this.mInOverview = true;
            if (!this.mTaskListFrozen) {
                enableMultipleRegions(true);
            }
            baseActivityInterface.onExitOverview(this, this.mExitOverviewRunnable);
        } else if (gestureEndTarget == GestureState.GestureEndTarget.HOME) {
            enableMultipleRegions(false);
        } else if (gestureEndTarget == GestureState.GestureEndTarget.NEW_TASK) {
            if (this.mOrientationTouchTransformer.getQuickStepStartingRotation() == -1) {
                enableMultipleRegions(true);
            } else {
                notifySysuiOfCurrentRotation(this.mOrientationTouchTransformer.getCurrentActiveRotation());
            }
            this.mPrioritizeDeviceRotation = false;
        } else if (gestureEndTarget == GestureState.GestureEndTarget.LAST_TASK && this.mTaskListFrozen) {
            notifySysuiOfCurrentRotation(this.mOrientationTouchTransformer.getCurrentActiveRotation());
        }
    }

    private void notifySysuiOfCurrentRotation(int i) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RotationTouchHelper.this.lambda$notifySysuiOfCurrentRotation$3$RotationTouchHelper(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$notifySysuiOfCurrentRotation$3$RotationTouchHelper(int i) {
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).notifyPrioritizedRotation(i);
    }

    /* access modifiers changed from: private */
    public void toggleSecondaryNavBarsForRotation() {
        this.mOrientationTouchTransformer.setSingleActiveRegion(this.mDisplayController.getInfo());
        notifySysuiOfCurrentRotation(this.mOrientationTouchTransformer.getCurrentActiveRotation());
    }

    public int getCurrentActiveRotation() {
        if (!this.mMode.hasGestures) {
            return this.mDisplayRotation;
        }
        return this.mOrientationTouchTransformer.getCurrentActiveRotation();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("RotationTouchHelper:");
        printWriter.println("  currentActiveRotation=" + getCurrentActiveRotation());
        printWriter.println("  displayRotation=" + getDisplayRotation());
        this.mOrientationTouchTransformer.dump(printWriter);
    }

    public OrientationTouchTransformer getOrientationTouchTransformer() {
        return this.mOrientationTouchTransformer;
    }
}
