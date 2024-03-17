package com.android.systemui.shared.system;

import android.annotation.NonNull;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.PictureInPictureSurfaceTransaction;
import android.window.RemoteTransition;
import android.window.TaskSnapshot;
import android.window.TransitionFilter;
import android.window.TransitionInfo;
import android.window.WindowContainerToken;
import android.window.WindowContainerTransaction;
import com.android.internal.util.AnnotationValidations;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class RemoteTransitionCompat implements Parcelable {
    public static final Parcelable.Creator<RemoteTransitionCompat> CREATOR = new Parcelable.Creator<RemoteTransitionCompat>() {
        public RemoteTransitionCompat[] newArray(int i) {
            return new RemoteTransitionCompat[i];
        }

        public RemoteTransitionCompat createFromParcel(Parcel parcel) {
            return new RemoteTransitionCompat(parcel);
        }
    };
    private static final String TAG = "RemoteTransitionCompat";
    TransitionFilter mFilter = null;
    final RemoteTransition mTransition;

    @Deprecated
    private void __metadata() {
    }

    public int describeContents() {
        return 0;
    }

    RemoteTransitionCompat(RemoteTransition remoteTransition) {
        this.mTransition = remoteTransition;
    }

    public RemoteTransitionCompat(final RemoteTransitionRunner remoteTransitionRunner, final Executor executor, IApplicationThread iApplicationThread) {
        this.mTransition = new RemoteTransition(new IRemoteTransition.Stub() {
            public void startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                executor.execute(new Runnable(iBinder, transitionInfo, transaction, new Runnable(iRemoteTransitionFinishedCallback) {
                    public final /* synthetic */ IRemoteTransitionFinishedCallback f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        RemoteTransitionCompat.AnonymousClass1.lambda$startAnimation$0(this.f$0);
                    }
                }) {
                    public final /* synthetic */ IBinder f$1;
                    public final /* synthetic */ TransitionInfo f$2;
                    public final /* synthetic */ SurfaceControl.Transaction f$3;
                    public final /* synthetic */ Runnable f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        RemoteTransitionRunner.this.startAnimation(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }

            static /* synthetic */ void lambda$startAnimation$0(IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                try {
                    iRemoteTransitionFinishedCallback.onTransitionFinished((WindowContainerTransaction) null, (SurfaceControl.Transaction) null);
                } catch (RemoteException e) {
                    Log.e(RemoteTransitionCompat.TAG, "Failed to call transition finished callback", e);
                }
            }

            public void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IBinder iBinder2, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                executor.execute(new Runnable(iBinder, transitionInfo, transaction, iBinder2, new Runnable(iRemoteTransitionFinishedCallback) {
                    public final /* synthetic */ IRemoteTransitionFinishedCallback f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        RemoteTransitionCompat.AnonymousClass1.lambda$mergeAnimation$2(this.f$0);
                    }
                }) {
                    public final /* synthetic */ IBinder f$1;
                    public final /* synthetic */ TransitionInfo f$2;
                    public final /* synthetic */ SurfaceControl.Transaction f$3;
                    public final /* synthetic */ IBinder f$4;
                    public final /* synthetic */ Runnable f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run() {
                        RemoteTransitionRunner.this.mergeAnimation(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                    }
                });
            }

            static /* synthetic */ void lambda$mergeAnimation$2(IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                try {
                    iRemoteTransitionFinishedCallback.onTransitionFinished((WindowContainerTransaction) null, (SurfaceControl.Transaction) null);
                } catch (RemoteException e) {
                    Log.e(RemoteTransitionCompat.TAG, "Failed to call transition finished callback", e);
                }
            }
        }, iApplicationThread);
    }

    public RemoteTransitionCompat(final RecentsAnimationListener recentsAnimationListener, final RecentsAnimationControllerCompat recentsAnimationControllerCompat, IApplicationThread iApplicationThread) {
        this.mTransition = new RemoteTransition(new IRemoteTransition.Stub() {
            final RecentsControllerWrap mRecentsSession = new RecentsControllerWrap();
            IBinder mToken = null;

            public void startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                TransitionInfo transitionInfo2 = transitionInfo;
                SurfaceControl.Transaction transaction2 = transaction;
                ArrayMap arrayMap = new ArrayMap();
                RemoteAnimationTargetCompat[] wrap = RemoteAnimationTargetCompat.wrap(transitionInfo2, false, transaction2, arrayMap);
                RemoteAnimationTargetCompat[] wrap2 = RemoteAnimationTargetCompat.wrap(transitionInfo2, true, transaction2, arrayMap);
                this.mToken = iBinder;
                ArrayList arrayList = new ArrayList();
                WindowContainerToken windowContainerToken = null;
                WindowContainerToken windowContainerToken2 = null;
                for (int size = transitionInfo.getChanges().size() - 1; size >= 0; size--) {
                    TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
                    ActivityManager.RunningTaskInfo taskInfo = change.getTaskInfo();
                    if (change.getMode() == 2 || change.getMode() == 4) {
                        transaction2.setLayer((SurfaceControl) arrayMap.get(change.getLeash()), (transitionInfo.getChanges().size() * 3) - size);
                        if (taskInfo != null) {
                            arrayList.add(0, taskInfo.token);
                            if (taskInfo.pictureInPictureParams != null && taskInfo.pictureInPictureParams.isAutoEnterEnabled()) {
                                windowContainerToken = taskInfo.token;
                            }
                        }
                    } else if (taskInfo != null && taskInfo.topActivityType == 3) {
                        transaction2.setLayer((SurfaceControl) arrayMap.get(change.getLeash()), (transitionInfo.getChanges().size() * 3) - size);
                        windowContainerToken2 = taskInfo.token;
                    } else if (taskInfo != null && taskInfo.topActivityType == 2) {
                        windowContainerToken2 = taskInfo.token;
                    }
                }
                for (int length = wrap2.length - 1; length >= 0; length--) {
                    transaction2.setAlpha(wrap2[length].leash, 1.0f);
                }
                transaction.apply();
                this.mRecentsSession.setup(recentsAnimationControllerCompat, transitionInfo, iRemoteTransitionFinishedCallback, arrayList, windowContainerToken, windowContainerToken2, arrayMap, this.mToken);
                recentsAnimationListener.onAnimationStart(this.mRecentsSession, wrap, wrap2, new Rect(0, 0, 0, 0), new Rect());
            }

            public void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, IBinder iBinder2, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback) {
                if (iBinder2.equals(this.mToken) && this.mRecentsSession.merge(transitionInfo, transaction, recentsAnimationListener)) {
                    try {
                        iRemoteTransitionFinishedCallback.onTransitionFinished((WindowContainerTransaction) null, (SurfaceControl.Transaction) null);
                    } catch (RemoteException e) {
                        Log.e(RemoteTransitionCompat.TAG, "Error merging transition.", e);
                    }
                }
            }
        }, iApplicationThread);
    }

    public void addHomeOpenCheck(ComponentName componentName) {
        if (this.mFilter == null) {
            this.mFilter = new TransitionFilter();
        }
        this.mFilter.mNotFlags = 256;
        TransitionFilter transitionFilter = this.mFilter;
        transitionFilter.mRequirements = new TransitionFilter.Requirement[]{new TransitionFilter.Requirement(), new TransitionFilter.Requirement()};
        this.mFilter.mRequirements[0].mActivityType = 2;
        this.mFilter.mRequirements[0].mTopActivity = componentName;
        this.mFilter.mRequirements[0].mModes = new int[]{1, 3};
        this.mFilter.mRequirements[0].mOrder = 1;
        this.mFilter.mRequirements[1].mActivityType = 1;
        this.mFilter.mRequirements[1].mModes = new int[]{2, 4};
    }

    static class RecentsControllerWrap extends RecentsAnimationControllerCompat {
        private IRemoteTransitionFinishedCallback mFinishCB = null;
        private TransitionInfo mInfo = null;
        private ArrayMap<SurfaceControl, SurfaceControl> mLeashMap = null;
        private ArrayList<SurfaceControl> mOpeningLeashes = null;
        private ArrayList<WindowContainerToken> mPausingTasks = null;
        private WindowContainerToken mPipTask = null;
        private PictureInPictureSurfaceTransaction mPipTransaction = null;
        private WindowContainerToken mRecentsTask = null;
        private IBinder mTransition = null;
        private RecentsAnimationControllerCompat mWrapped = null;

        public void animateNavigationBarToApp(long j) {
        }

        RecentsControllerWrap() {
        }

        /* access modifiers changed from: package-private */
        public void setup(RecentsAnimationControllerCompat recentsAnimationControllerCompat, TransitionInfo transitionInfo, IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback, ArrayList<WindowContainerToken> arrayList, WindowContainerToken windowContainerToken, WindowContainerToken windowContainerToken2, ArrayMap<SurfaceControl, SurfaceControl> arrayMap, IBinder iBinder) {
            if (this.mInfo == null) {
                this.mWrapped = recentsAnimationControllerCompat;
                this.mInfo = transitionInfo;
                this.mFinishCB = iRemoteTransitionFinishedCallback;
                this.mPausingTasks = arrayList;
                this.mPipTask = windowContainerToken;
                this.mRecentsTask = windowContainerToken2;
                this.mLeashMap = arrayMap;
                this.mTransition = iBinder;
                return;
            }
            throw new IllegalStateException("Trying to run a new recents animation while recents is already active.");
        }

        /* access modifiers changed from: package-private */
        public boolean merge(TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, RecentsAnimationListener recentsAnimationListener) {
            int i;
            SparseArray sparseArray = null;
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            for (int size = transitionInfo.getChanges().size() - 1; size >= 0; size--) {
                TransitionInfo.Change change = (TransitionInfo.Change) transitionInfo.getChanges().get(size);
                if (change.getMode() == 1 || change.getMode() == 3) {
                    ActivityManager.RunningTaskInfo taskInfo = change.getTaskInfo();
                    if (taskInfo != null) {
                        if (taskInfo.topActivityType == 2) {
                            z3 = true;
                        }
                        if (sparseArray == null) {
                            sparseArray = new SparseArray();
                        }
                        if (taskInfo.hasParentTask()) {
                            sparseArray.remove(taskInfo.parentTaskId);
                        }
                        sparseArray.put(taskInfo.taskId, change);
                    }
                } else if (change.getMode() == 2 || change.getMode() == 4) {
                    if (this.mRecentsTask.equals(change.getContainer())) {
                        z2 = true;
                    }
                } else if (change.getMode() == 6) {
                    z = true;
                }
            }
            if (z && z2) {
                if (!recentsAnimationListener.onSwitchToScreenshot(new Runnable() {
                    public final void run() {
                        RemoteTransitionCompat.RecentsControllerWrap.this.lambda$merge$0$RemoteTransitionCompat$RecentsControllerWrap();
                    }
                })) {
                    Log.w(RemoteTransitionCompat.TAG, "Recents callback doesn't support support switching to screenshot, there might be a flicker.");
                    finish(true, false);
                }
                return false;
            } else if (sparseArray == null) {
                return false;
            } else {
                if (!z3) {
                    i = 0;
                    for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                        if (this.mPausingTasks.contains(((TransitionInfo.Change) sparseArray.valueAt(i2)).getContainer())) {
                            i++;
                        }
                    }
                } else {
                    i = 0;
                }
                if (i <= 0) {
                    int size2 = this.mInfo.getChanges().size() * 3;
                    this.mOpeningLeashes = new ArrayList<>();
                    RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = new RemoteAnimationTargetCompat[sparseArray.size()];
                    for (int i3 = 0; i3 < sparseArray.size(); i3++) {
                        TransitionInfo.Change change2 = (TransitionInfo.Change) sparseArray.valueAt(i3);
                        this.mOpeningLeashes.add(change2.getLeash());
                        RemoteAnimationTargetCompat remoteAnimationTargetCompat = new RemoteAnimationTargetCompat(change2, size2, transitionInfo, transaction);
                        this.mLeashMap.put(this.mOpeningLeashes.get(i3), remoteAnimationTargetCompat.leash);
                        transaction.reparent(remoteAnimationTargetCompat.leash, this.mInfo.getRootLeash());
                        transaction.setLayer(remoteAnimationTargetCompat.leash, size2);
                        remoteAnimationTargetCompatArr[i3] = remoteAnimationTargetCompat;
                    }
                    transaction.apply();
                    recentsAnimationListener.onTasksAppeared(remoteAnimationTargetCompatArr);
                    return true;
                } else if (i == this.mPausingTasks.size()) {
                    return true;
                } else {
                    throw new IllegalStateException("\"Concelling\" a recents transitions by unpausing " + i + " apps after pausing " + this.mPausingTasks.size() + " apps.");
                }
            }
        }

        public /* synthetic */ void lambda$merge$0$RemoteTransitionCompat$RecentsControllerWrap() {
            finish(true, false);
        }

        public ThumbnailData screenshotTask(int i) {
            try {
                TaskSnapshot takeTaskSnapshot = ActivityTaskManager.getService().takeTaskSnapshot(i);
                if (takeTaskSnapshot != null) {
                    return new ThumbnailData(takeTaskSnapshot);
                }
                return null;
            } catch (RemoteException e) {
                Log.e(RemoteTransitionCompat.TAG, "Failed to screenshot task", e);
                return null;
            }
        }

        public void setInputConsumerEnabled(boolean z) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setInputConsumerEnabled(z);
            }
        }

        public void setAnimationTargetsBehindSystemBars(boolean z) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setAnimationTargetsBehindSystemBars(z);
            }
        }

        public void hideCurrentInputMethod() {
            this.mWrapped.hideCurrentInputMethod();
        }

        public void setFinishTaskTransaction(int i, PictureInPictureSurfaceTransaction pictureInPictureSurfaceTransaction, SurfaceControl surfaceControl) {
            this.mPipTransaction = pictureInPictureSurfaceTransaction;
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setFinishTaskTransaction(i, pictureInPictureSurfaceTransaction, surfaceControl);
            }
        }

        public void finish(boolean z, boolean z2) {
            ArrayList<WindowContainerToken> arrayList;
            if (this.mFinishCB == null) {
                Log.e(RemoteTransitionCompat.TAG, "Duplicate call to finish", new RuntimeException());
                return;
            }
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.finish(z, z2);
            }
            SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            if (z || (arrayList = this.mPausingTasks) == null || this.mOpeningLeashes != null) {
                if (!z2) {
                    for (int i = 0; i < this.mPausingTasks.size(); i++) {
                        windowContainerTransaction.setDoNotPip(this.mPausingTasks.get(i));
                    }
                }
                WindowContainerToken windowContainerToken = this.mPipTask;
                if (!(windowContainerToken == null || this.mPipTransaction == null || !z2)) {
                    transaction.show(this.mInfo.getChange(windowContainerToken).getLeash());
                    PictureInPictureSurfaceTransaction.apply(this.mPipTransaction, this.mInfo.getChange(this.mPipTask).getLeash(), transaction);
                    this.mPipTask = null;
                    this.mPipTransaction = null;
                }
            } else {
                for (int size = arrayList.size() - 1; size >= 0; size--) {
                    windowContainerTransaction.reorder(this.mPausingTasks.get(size), true);
                    transaction.show(this.mInfo.getChange(this.mPausingTasks.get(size)).getLeash());
                }
                WindowContainerToken windowContainerToken2 = this.mRecentsTask;
                if (windowContainerToken2 != null) {
                    windowContainerTransaction.restoreTransientOrder(windowContainerToken2);
                }
            }
            for (int i2 = 0; i2 < this.mLeashMap.size(); i2++) {
                if (this.mLeashMap.keyAt(i2) != this.mLeashMap.valueAt(i2)) {
                    transaction.remove(this.mLeashMap.valueAt(i2));
                }
            }
            try {
                IRemoteTransitionFinishedCallback iRemoteTransitionFinishedCallback = this.mFinishCB;
                if (windowContainerTransaction.isEmpty()) {
                    windowContainerTransaction = null;
                }
                iRemoteTransitionFinishedCallback.onTransitionFinished(windowContainerTransaction, transaction);
            } catch (RemoteException e) {
                Log.e(RemoteTransitionCompat.TAG, "Failed to call animation finish callback", e);
                transaction.apply();
            }
            for (int i3 = 0; i3 < this.mInfo.getChanges().size(); i3++) {
                ((TransitionInfo.Change) this.mInfo.getChanges().get(i3)).getLeash().release();
            }
            this.mWrapped = null;
            this.mFinishCB = null;
            this.mPausingTasks = null;
            this.mInfo = null;
            this.mOpeningLeashes = null;
            this.mLeashMap = null;
            this.mTransition = null;
        }

        public void setDeferCancelUntilNextTransition(boolean z, boolean z2) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setDeferCancelUntilNextTransition(z, z2);
            }
        }

        public void cleanupScreenshot() {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.cleanupScreenshot();
            }
        }

        public void setWillFinishToHome(boolean z) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                recentsAnimationControllerCompat.setWillFinishToHome(z);
            }
        }

        public boolean removeTask(int i) {
            RecentsAnimationControllerCompat recentsAnimationControllerCompat = this.mWrapped;
            if (recentsAnimationControllerCompat != null) {
                return recentsAnimationControllerCompat.removeTask(i);
            }
            return false;
        }

        public void detachNavigationBarFromApp(boolean z) {
            try {
                ActivityTaskManager.getService().detachNavigationBarFromApp(this.mTransition);
            } catch (RemoteException e) {
                Log.e(RemoteTransitionCompat.TAG, "Failed to detach the navigation bar from app", e);
            }
        }
    }

    RemoteTransitionCompat(RemoteTransition remoteTransition, TransitionFilter transitionFilter) {
        this.mTransition = remoteTransition;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, remoteTransition);
        this.mFilter = transitionFilter;
    }

    public RemoteTransition getTransition() {
        return this.mTransition;
    }

    public TransitionFilter getFilter() {
        return this.mFilter;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.mFilter != null ? (byte) 2 : 0);
        parcel.writeTypedObject(this.mTransition, i);
        TransitionFilter transitionFilter = this.mFilter;
        if (transitionFilter != null) {
            parcel.writeTypedObject(transitionFilter, i);
        }
    }

    protected RemoteTransitionCompat(Parcel parcel) {
        TransitionFilter transitionFilter;
        byte readByte = parcel.readByte();
        RemoteTransition remoteTransition = (RemoteTransition) parcel.readTypedObject(RemoteTransition.CREATOR);
        if ((readByte & 2) == 0) {
            transitionFilter = null;
        } else {
            transitionFilter = (TransitionFilter) parcel.readTypedObject(TransitionFilter.CREATOR);
        }
        this.mTransition = remoteTransition;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, remoteTransition);
        this.mFilter = transitionFilter;
    }

    public static class Builder {
        private long mBuilderFieldsSet = 0;
        private TransitionFilter mFilter;
        private RemoteTransition mTransition;

        public Builder(RemoteTransition remoteTransition) {
            this.mTransition = remoteTransition;
            AnnotationValidations.validate(NonNull.class, (NonNull) null, remoteTransition);
        }

        public Builder setTransition(RemoteTransition remoteTransition) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mTransition = remoteTransition;
            return this;
        }

        public Builder setFilter(TransitionFilter transitionFilter) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mFilter = transitionFilter;
            return this;
        }

        public RemoteTransitionCompat build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 4;
            this.mBuilderFieldsSet = j;
            if ((j & 2) == 0) {
                this.mFilter = null;
            }
            return new RemoteTransitionCompat(this.mTransition, this.mFilter);
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 4) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
