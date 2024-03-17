package com.android.quickstep;

import android.content.Intent;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.tracing.GestureStateProto;
import com.android.launcher3.tracing.SwipeHandlerProto;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.TopTaskTracker;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GestureState implements RecentsAnimationCallbacks.RecentsAnimationListener {
    public static final GestureState DEFAULT_STATE = new GestureState();
    private static int FLAG_COUNT = 0;
    public static final int STATE_END_TARGET_ANIMATION_FINISHED = getFlagForIndex("STATE_END_TARGET_ANIMATION_FINISHED");
    public static final int STATE_END_TARGET_SET = getFlagForIndex("STATE_END_TARGET_SET");
    private static final ArrayList<String> STATE_NAMES = new ArrayList<>();
    public static final int STATE_RECENTS_ANIMATION_CANCELED = getFlagForIndex("STATE_RECENTS_ANIMATION_CANCELED");
    public static final int STATE_RECENTS_ANIMATION_ENDED = getFlagForIndex("STATE_RECENTS_ANIMATION_ENDED");
    public static final int STATE_RECENTS_ANIMATION_FINISHED = getFlagForIndex("STATE_RECENTS_ANIMATION_FINISHED");
    public static final int STATE_RECENTS_ANIMATION_INITIALIZED = getFlagForIndex("STATE_RECENTS_ANIMATION_INITIALIZED");
    public static final int STATE_RECENTS_ANIMATION_STARTED = getFlagForIndex("STATE_RECENTS_ANIMATION_STARTED");
    public static final int STATE_RECENTS_SCROLLING_FINISHED = getFlagForIndex("STATE_RECENTS_SCROLLING_FINISHED");
    private static final String TAG = "GestureState";
    private final BaseActivityInterface mActivityInterface;
    private GestureEndTarget mEndTarget;
    private final int mGestureId;
    private boolean mHandlingAtomicEvent;
    private final Intent mHomeIntent;
    private RemoteAnimationTargetCompat mLastAppearedTaskTarget;
    private int mLastStartedTaskId;
    private final Intent mOverviewIntent;
    private Set<Integer> mPreviouslyAppearedTaskIds;
    private HashMap<Integer, ThumbnailData> mRecentsAnimationCanceledSnapshots;
    private RecentsAnimationController mRecentsAnimationController;
    private TopTaskTracker.CachedTaskInfo mRunningTask;
    private final MultiStateCallback mStateCallback;
    private long mSwipeUpStartTimeMs;

    public enum GestureEndTarget {
        HOME(true, 2, false, GestureStateProto.GestureEndTarget.HOME),
        RECENTS(true, 3, true, GestureStateProto.GestureEndTarget.RECENTS),
        NEW_TASK(false, 1, true, GestureStateProto.GestureEndTarget.NEW_TASK),
        LAST_TASK(false, 1, true, GestureStateProto.GestureEndTarget.LAST_TASK);
        
        public final int containerType;
        public final boolean isLauncher;
        public final GestureStateProto.GestureEndTarget protoEndTarget;
        public final boolean recentsAttachedToAppWindow;

        private GestureEndTarget(boolean z, int i, boolean z2, GestureStateProto.GestureEndTarget gestureEndTarget) {
            this.isLauncher = z;
            this.containerType = i;
            this.recentsAttachedToAppWindow = z2;
            this.protoEndTarget = gestureEndTarget;
        }
    }

    private static int getFlagForIndex(String str) {
        int i = FLAG_COUNT;
        int i2 = 1 << i;
        FLAG_COUNT = i + 1;
        return i2;
    }

    public GestureState(OverviewComponentObserver overviewComponentObserver, int i) {
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mHomeIntent = overviewComponentObserver.getHomeIntent();
        this.mOverviewIntent = overviewComponentObserver.getOverviewIntent();
        this.mActivityInterface = overviewComponentObserver.getActivityInterface();
        this.mStateCallback = new MultiStateCallback((String[]) STATE_NAMES.toArray(new String[0]));
        this.mGestureId = i;
    }

    public GestureState(GestureState gestureState) {
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mHomeIntent = gestureState.mHomeIntent;
        this.mOverviewIntent = gestureState.mOverviewIntent;
        this.mActivityInterface = gestureState.mActivityInterface;
        this.mStateCallback = gestureState.mStateCallback;
        this.mGestureId = gestureState.mGestureId;
        this.mRunningTask = gestureState.mRunningTask;
        this.mEndTarget = gestureState.mEndTarget;
        this.mLastAppearedTaskTarget = gestureState.mLastAppearedTaskTarget;
        this.mPreviouslyAppearedTaskIds = gestureState.mPreviouslyAppearedTaskIds;
        this.mLastStartedTaskId = gestureState.mLastStartedTaskId;
    }

    public GestureState() {
        this.mPreviouslyAppearedTaskIds = new HashSet();
        this.mLastStartedTaskId = -1;
        this.mHomeIntent = new Intent();
        this.mOverviewIntent = new Intent();
        this.mActivityInterface = null;
        this.mStateCallback = new MultiStateCallback((String[]) STATE_NAMES.toArray(new String[0]));
        this.mGestureId = -1;
    }

    public boolean hasState(int i) {
        return this.mStateCallback.hasStates(i);
    }

    public void setState(int i) {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(i);
    }

    public void runOnceAtState(int i, Runnable runnable) {
        this.mStateCallback.runOnceAtState(i, runnable);
    }

    public Intent getHomeIntent() {
        return this.mHomeIntent;
    }

    public Intent getOverviewIntent() {
        return this.mOverviewIntent;
    }

    public <S extends BaseState<S>, T extends StatefulActivity<S>> BaseActivityInterface<S, T> getActivityInterface() {
        return this.mActivityInterface;
    }

    public int getGestureId() {
        return this.mGestureId;
    }

    public TopTaskTracker.CachedTaskInfo getRunningTask() {
        return this.mRunningTask;
    }

    public int getRunningTaskId() {
        TopTaskTracker.CachedTaskInfo cachedTaskInfo = this.mRunningTask;
        if (cachedTaskInfo != null) {
            return cachedTaskInfo.getTaskId();
        }
        return -1;
    }

    public void updateRunningTask(TopTaskTracker.CachedTaskInfo cachedTaskInfo) {
        this.mRunningTask = cachedTaskInfo;
    }

    public void updateLastAppearedTaskTarget(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        this.mLastAppearedTaskTarget = remoteAnimationTargetCompat;
        if (remoteAnimationTargetCompat != null) {
            this.mPreviouslyAppearedTaskIds.add(Integer.valueOf(remoteAnimationTargetCompat.taskId));
        }
    }

    public int getLastAppearedTaskId() {
        RemoteAnimationTargetCompat remoteAnimationTargetCompat = this.mLastAppearedTaskTarget;
        if (remoteAnimationTargetCompat != null) {
            return remoteAnimationTargetCompat.taskId;
        }
        return -1;
    }

    public void updatePreviouslyAppearedTaskIds(Set<Integer> set) {
        this.mPreviouslyAppearedTaskIds = set;
    }

    public Set<Integer> getPreviouslyAppearedTaskIds() {
        return this.mPreviouslyAppearedTaskIds;
    }

    public void updateLastStartedTaskId(int i) {
        this.mLastStartedTaskId = i;
    }

    public int getLastStartedTaskId() {
        return this.mLastStartedTaskId;
    }

    public GestureEndTarget getEndTarget() {
        return this.mEndTarget;
    }

    public void setEndTarget(GestureEndTarget gestureEndTarget) {
        setEndTarget(gestureEndTarget, true);
    }

    public void setEndTarget(GestureEndTarget gestureEndTarget, boolean z) {
        this.mEndTarget = gestureEndTarget;
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_END_TARGET_SET);
        ActiveGestureLog.INSTANCE.addLog("setEndTarget " + this.mEndTarget);
        if (z) {
            this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_END_TARGET_ANIMATION_FINISHED);
        }
    }

    public void setHandlingAtomicEvent(boolean z) {
        this.mHandlingAtomicEvent = z;
    }

    public boolean isHandlingAtomicEvent() {
        return this.mHandlingAtomicEvent;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.mEndTarget;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isRunningAnimationToLauncher() {
        /*
            r1 = this;
            boolean r0 = r1.isRecentsAnimationRunning()
            if (r0 == 0) goto L_0x0010
            com.android.quickstep.GestureState$GestureEndTarget r0 = r1.mEndTarget
            if (r0 == 0) goto L_0x0010
            boolean r0 = r0.isLauncher
            if (r0 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.GestureState.isRunningAnimationToLauncher():boolean");
    }

    public boolean isRecentsAnimationRunning() {
        return this.mStateCallback.hasStates(STATE_RECENTS_ANIMATION_STARTED) && !this.mStateCallback.hasStates(STATE_RECENTS_ANIMATION_ENDED);
    }

    public void onRecentsAnimationStart(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
        this.mRecentsAnimationController = recentsAnimationController;
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_STARTED);
    }

    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
        this.mRecentsAnimationCanceledSnapshots = hashMap;
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_CANCELED);
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_ENDED);
        if (this.mRecentsAnimationCanceledSnapshots != null) {
            RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
            if (recentsAnimationController != null) {
                recentsAnimationController.cleanupScreenshot();
            }
            this.mRecentsAnimationCanceledSnapshots = null;
        }
    }

    public void onRecentsAnimationFinished(RecentsAnimationController recentsAnimationController) {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_FINISHED);
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_RECENTS_ANIMATION_ENDED);
    }

    /* access modifiers changed from: package-private */
    public HashMap<Integer, ThumbnailData> consumeRecentsAnimationCanceledSnapshot() {
        if (this.mRecentsAnimationCanceledSnapshots == null) {
            return null;
        }
        HashMap<Integer, ThumbnailData> hashMap = new HashMap<>(this.mRecentsAnimationCanceledSnapshots);
        this.mRecentsAnimationCanceledSnapshots = null;
        return hashMap;
    }

    /* access modifiers changed from: package-private */
    public void setSwipeUpStartTimeMs(long j) {
        this.mSwipeUpStartTimeMs = j;
    }

    /* access modifiers changed from: package-private */
    public long getSwipeUpStartTimeMs() {
        return this.mSwipeUpStartTimeMs;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("GestureState:");
        printWriter.println("  gestureID=" + this.mGestureId);
        printWriter.println("  runningTask=" + this.mRunningTask);
        printWriter.println("  endTarget=" + this.mEndTarget);
        printWriter.println("  lastAppearedTaskTargetId=" + getLastAppearedTaskId());
        printWriter.println("  lastStartedTaskId=" + this.mLastStartedTaskId);
        printWriter.println("  isRecentsAnimationRunning=" + isRecentsAnimationRunning());
    }

    public void writeToProto(SwipeHandlerProto.Builder builder) {
        GestureStateProto.GestureEndTarget gestureEndTarget;
        GestureStateProto.Builder newBuilder = GestureStateProto.newBuilder();
        GestureEndTarget gestureEndTarget2 = this.mEndTarget;
        if (gestureEndTarget2 == null) {
            gestureEndTarget = GestureStateProto.GestureEndTarget.UNSET;
        } else {
            gestureEndTarget = gestureEndTarget2.protoEndTarget;
        }
        newBuilder.setEndTarget(gestureEndTarget);
        builder.setGestureState(newBuilder);
    }
}
