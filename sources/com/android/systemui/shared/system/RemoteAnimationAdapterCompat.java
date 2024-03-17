package com.android.systemui.shared.system;

import android.app.IApplicationThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.IRemoteAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.RemoteTransition;
import android.window.TransitionInfo;
import android.window.WindowContainerTransaction;
import com.android.wm.shell.util.CounterRotator;

public class RemoteAnimationAdapterCompat {
    private final RemoteTransitionCompat mRemoteTransition;
    private final RemoteAnimationAdapter mWrapped;

    public RemoteAnimationAdapterCompat(RemoteAnimationRunnerCompat remoteAnimationRunnerCompat, long j, long j2, IApplicationThread iApplicationThread) {
        this.mWrapped = new RemoteAnimationAdapter(wrapRemoteAnimationRunner(remoteAnimationRunnerCompat), j, j2);
        this.mRemoteTransition = buildRemoteTransition(remoteAnimationRunnerCompat, iApplicationThread);
    }

    /* access modifiers changed from: package-private */
    public RemoteAnimationAdapter getWrapped() {
        return this.mWrapped;
    }

    public static RemoteTransitionCompat buildRemoteTransition(RemoteAnimationRunnerCompat remoteAnimationRunnerCompat, IApplicationThread iApplicationThread) {
        return new RemoteTransitionCompat(new RemoteTransition(wrapRemoteTransition(remoteAnimationRunnerCompat), iApplicationThread));
    }

    public RemoteTransitionCompat getRemoteTransition() {
        return this.mRemoteTransition;
    }

    public static IRemoteAnimationRunner.Stub wrapRemoteAnimationRunner(final RemoteAnimationRunnerCompat remoteAnimationRunnerCompat) {
        return new IRemoteAnimationRunner.Stub() {
            public void onAnimationStart(int i, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, final IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
                RemoteAnimationRunnerCompat.this.onAnimationStart(i, RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr2), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr3), new Runnable() {
                    public void run() {
                        try {
                            iRemoteAnimationFinishedCallback.onAnimationFinished();
                        } catch (RemoteException e) {
                            Log.e("ActivityOptionsCompat", "Failed to call app controlled animation finished callback", e);
                        }
                    }
                });
            }

            public void onAnimationCancelled(boolean z) {
                RemoteAnimationRunnerCompat.this.onAnimationCancelled();
            }
        };
    }

    private static IRemoteTransition.Stub wrapRemoteTransition(final RemoteAnimationRunnerCompat remoteAnimationRunnerCompat) {
        return new IRemoteTransition.Stub() {
            public void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IBinder iBinder2, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
            }

            public void startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                TransitionInfo transitionInfo2 = transitionInfo;
                SurfaceControl.Transaction transaction2 = transaction;
                ArrayMap arrayMap = new ArrayMap();
                RemoteAnimationTargetCompat[] wrap = RemoteAnimationTargetCompat.wrap(transitionInfo2, false, transaction2, arrayMap);
                RemoteAnimationTargetCompat[] wrap2 = RemoteAnimationTargetCompat.wrap(transitionInfo2, true, transaction2, arrayMap);
                RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = new RemoteAnimationTargetCompat[0];
                int i = 0;
                int i2 = 0;
                boolean z = false;
                float f = 0.0f;
                float f2 = 0.0f;
                TransitionInfo.Change change = null;
                TransitionInfo.Change change2 = null;
                for (int size = transitionInfo.getChanges().size() - 1; size >= 0; size--) {
                    TransitionInfo.Change change3 = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
                    if (change3.getTaskInfo() != null && change3.getTaskInfo().getActivityType() == 2) {
                        z = change3.getMode() == 1 || change3.getMode() == 3;
                        i = transitionInfo.getChanges().size() - size;
                        change = change3;
                    } else if ((change3.getFlags() & 2) != 0) {
                        change2 = change3;
                    }
                    if (change3.getParent() == null && change3.getEndRotation() >= 0 && change3.getEndRotation() != change3.getStartRotation()) {
                        i2 = change3.getEndRotation() - change3.getStartRotation();
                        f2 = (float) change3.getEndAbsBounds().height();
                        f = (float) change3.getEndAbsBounds().width();
                    }
                }
                CounterRotator counterRotator = new CounterRotator();
                CounterRotator counterRotator2 = new CounterRotator();
                if (change != null && i2 != 0 && change.getParent() != null) {
                    int i3 = i;
                    counterRotator.setup(transaction, transitionInfo2.getChange(change.getParent()).getLeash(), i2, f, f2);
                    if (counterRotator.getSurface() != null) {
                        transaction2.setLayer(counterRotator.getSurface(), i3);
                    }
                }
                if (z) {
                    if (counterRotator.getSurface() != null) {
                        transaction2.setLayer(counterRotator.getSurface(), transitionInfo.getChanges().size() * 3);
                    }
                    for (int size2 = transitionInfo.getChanges().size() - 1; size2 >= 0; size2--) {
                        TransitionInfo.Change change4 = (TransitionInfo.Change) transitionInfo.getChanges().get(size2);
                        SurfaceControl surfaceControl = (SurfaceControl) arrayMap.get(change4.getLeash());
                        int mode = ((TransitionInfo.Change) transitionInfo.getChanges().get(size2)).getMode();
                        if (TransitionInfo.isIndependent(change4, transitionInfo2)) {
                            if (mode == 2 || mode == 4) {
                                transaction2.setLayer(surfaceControl, (transitionInfo.getChanges().size() * 3) - size2);
                                counterRotator.addChild(transaction2, surfaceControl);
                            }
                        }
                    }
                    for (int length = wrap2.length - 1; length >= 0; length--) {
                        transaction2.show(wrap2[length].leash);
                        transaction2.setAlpha(wrap2[length].leash, 1.0f);
                    }
                } else {
                    if (change != null) {
                        counterRotator.addChild(transaction2, (SurfaceControl) arrayMap.get(change.getLeash()));
                    }
                    if (!(change2 == null || i2 == 0 || change2.getParent() == null)) {
                        counterRotator2.setup(transaction, transitionInfo2.getChange(change2.getParent()).getLeash(), i2, f, f2);
                        if (counterRotator2.getSurface() != null) {
                            transaction2.setLayer(counterRotator2.getSurface(), -1);
                            counterRotator2.addChild(transaction2, (SurfaceControl) arrayMap.get(change2.getLeash()));
                        }
                    }
                }
                transaction.apply();
                final CounterRotator counterRotator3 = counterRotator;
                final CounterRotator counterRotator4 = counterRotator2;
                final TransitionInfo transitionInfo3 = transitionInfo;
                final ArrayMap arrayMap2 = arrayMap;
                final IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback2 = iRemoteTransitionFinishedCallback;
                RemoteAnimationRunnerCompat.this.onAnimationStart(0, wrap, wrap2, remoteAnimationTargetCompatArr, new Runnable() {
                    public void run() {
                        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                        counterRotator3.cleanUp(transaction);
                        counterRotator4.cleanUp(transaction);
                        for (int size = transitionInfo3.getChanges().size() - 1; size >= 0; size--) {
                            ((TransitionInfo.Change) transitionInfo3.getChanges().get(size)).getLeash().release();
                        }
                        for (int size2 = arrayMap2.size() - 1; size2 >= 0; size2--) {
                            ((SurfaceControl) arrayMap2.valueAt(size2)).release();
                        }
                        try {
                            iRemoteTransitionFinishedCallback2.onTransitionFinished((WindowContainerTransaction) null, transaction);
                        } catch (RemoteException e) {
                            Log.e("ActivityOptionsCompat", "Failed to call app controlled animation finished callback", e);
                        }
                    }
                });
            }
        };
    }
}
