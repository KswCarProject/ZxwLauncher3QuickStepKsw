package com.android.quickstep;

import android.content.Intent;
import android.graphics.PointF;
import android.os.SystemClock;
import android.os.Trace;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.RunnableList;
import com.android.quickstep.OverviewCommandHelper;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class OverviewCommandHelper {
    private static final int MAX_QUEUE_SIZE = 3;
    private static final String TRANSITION_NAME = "Transition:toOverview";
    public static final int TYPE_HIDE = 3;
    public static final int TYPE_HOME = 5;
    public static final int TYPE_SHOW = 1;
    public static final int TYPE_SHOW_NEXT_FOCUS = 2;
    public static final int TYPE_TOGGLE = 4;
    private final OverviewComponentObserver mOverviewComponentObserver;
    private final ArrayList<CommandInfo> mPendingCommands = new ArrayList<>();
    private final TouchInteractionService mService;
    private final TaskAnimationManager mTaskAnimationManager;

    public OverviewCommandHelper(TouchInteractionService touchInteractionService, OverviewComponentObserver overviewComponentObserver, TaskAnimationManager taskAnimationManager) {
        this.mService = touchInteractionService;
        this.mOverviewComponentObserver = overviewComponentObserver;
        this.mTaskAnimationManager = taskAnimationManager;
    }

    /* access modifiers changed from: private */
    /* renamed from: scheduleNextTask */
    public void lambda$launchTask$1$OverviewCommandHelper(CommandInfo commandInfo) {
        if (!this.mPendingCommands.isEmpty() && this.mPendingCommands.get(0) == commandInfo) {
            this.mPendingCommands.remove(0);
            executeNext();
        }
    }

    private void executeNext() {
        if (!this.mPendingCommands.isEmpty()) {
            CommandInfo commandInfo = this.mPendingCommands.get(0);
            if (executeCommand(commandInfo)) {
                lambda$launchTask$1$OverviewCommandHelper(commandInfo);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: addCommand */
    public void lambda$addCommand$0$OverviewCommandHelper(CommandInfo commandInfo) {
        boolean isEmpty = this.mPendingCommands.isEmpty();
        this.mPendingCommands.add(commandInfo);
        if (isEmpty) {
            executeNext();
        }
    }

    public void addCommand(int i) {
        if (this.mPendingCommands.size() <= 3) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(new CommandInfo(i)) {
                public final /* synthetic */ OverviewCommandHelper.CommandInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    OverviewCommandHelper.this.lambda$addCommand$0$OverviewCommandHelper(this.f$1);
                }
            });
        }
    }

    public void clearPendingCommands() {
        this.mPendingCommands.clear();
    }

    private TaskView getNextTask(RecentsView recentsView) {
        TaskView runningTaskView = recentsView.getRunningTaskView();
        if (runningTaskView == null) {
            return recentsView.getTaskViewAt(0);
        }
        TaskView nextTaskView = recentsView.getNextTaskView();
        return nextTaskView != null ? nextTaskView : runningTaskView;
    }

    private boolean launchTask(RecentsView recentsView, TaskView taskView, CommandInfo commandInfo) {
        RunnableList runnableList;
        if (taskView != null) {
            taskView.setEndQuickswitchCuj(true);
            runnableList = taskView.launchTaskAnimated();
        } else {
            runnableList = null;
        }
        if (runnableList != null) {
            runnableList.add(new Runnable(commandInfo) {
                public final /* synthetic */ OverviewCommandHelper.CommandInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    OverviewCommandHelper.this.lambda$launchTask$1$OverviewCommandHelper(this.f$1);
                }
            });
            return false;
        }
        recentsView.startHome();
        return true;
    }

    private <T extends StatefulActivity<?>> boolean executeCommand(final CommandInfo commandInfo) {
        final BaseActivityInterface activityInterface = this.mOverviewComponentObserver.getActivityInterface();
        RecentsView visibleRecentsView = activityInterface.getVisibleRecentsView();
        if (visibleRecentsView != null) {
            int i = commandInfo.type;
            if (i == 1) {
                return true;
            }
            if (i == 3) {
                int nextPage = visibleRecentsView.getNextPage();
                return launchTask(visibleRecentsView, (nextPage < 0 || nextPage >= visibleRecentsView.getTaskViewCount()) ? null : (TaskView) visibleRecentsView.getPageAt(nextPage), commandInfo);
            } else if (i == 4) {
                return launchTask(visibleRecentsView, getNextTask(visibleRecentsView), commandInfo);
            } else {
                if (i == 5) {
                    visibleRecentsView.startHome();
                    return true;
                }
            }
        } else if (commandInfo.type == 3) {
            return true;
        } else {
            if (commandInfo.type == 5) {
                this.mService.startActivity(this.mOverviewComponentObserver.getHomeIntent());
                return true;
            }
        }
        if (activityInterface.switchToRecentsIfVisible(new Runnable(commandInfo) {
            public final /* synthetic */ OverviewCommandHelper.CommandInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                OverviewCommandHelper.this.lambda$executeCommand$2$OverviewCommandHelper(this.f$1);
            }
        })) {
            return false;
        }
        StatefulActivity createdActivity = activityInterface.getCreatedActivity();
        if (createdActivity != null) {
            InteractionJankMonitorWrapper.begin(createdActivity.getRootView(), 11);
        }
        GestureState createGestureState = this.mService.createGestureState(GestureState.DEFAULT_STATE);
        createGestureState.setHandlingAtomicEvent(true);
        final AbsSwipeUpHandler newHandler = this.mService.getSwipeUpHandlerFactory().newHandler(createGestureState, commandInfo.createTime);
        newHandler.setGestureEndCallback(new Runnable(commandInfo, newHandler) {
            public final /* synthetic */ OverviewCommandHelper.CommandInfo f$1;
            public final /* synthetic */ AbsSwipeUpHandler f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                OverviewCommandHelper.this.lambda$executeCommand$3$OverviewCommandHelper(this.f$1, this.f$2);
            }
        });
        newHandler.initWhenReady();
        AnonymousClass1 r5 = new RecentsAnimationCallbacks.RecentsAnimationListener() {
            public void onRecentsAnimationStart(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
                activityInterface.runOnInitBackgroundStateUI(new Runnable() {
                    public final void run() {
                        AbsSwipeUpHandler.this.onGestureEnded(0.0f, new PointF(), new PointF());
                    }
                });
                commandInfo.removeListener(this);
            }

            public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
                newHandler.onGestureCancelled();
                commandInfo.removeListener(this);
                RecentsView recentsView = (RecentsView) activityInterface.getCreatedActivity().getOverviewPanel();
                if (recentsView != null) {
                    recentsView.onRecentsAnimationComplete();
                }
            }
        };
        if (this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            commandInfo.mActiveCallbacks = this.mTaskAnimationManager.continueRecentsAnimation(createGestureState);
            commandInfo.mActiveCallbacks.addListener(newHandler);
            this.mTaskAnimationManager.notifyRecentsAnimationState(newHandler);
            newHandler.onGestureStarted(true);
            commandInfo.mActiveCallbacks.addListener(r5);
            this.mTaskAnimationManager.notifyRecentsAnimationState(r5);
        } else {
            Intent intent = new Intent(newHandler.getLaunchIntent());
            intent.putExtra(ActiveGestureLog.INTENT_EXTRA_LOG_TRACE_ID, createGestureState.getGestureId());
            commandInfo.mActiveCallbacks = this.mTaskAnimationManager.startRecentsAnimation(createGestureState, intent, newHandler);
            newHandler.onGestureStarted(false);
            commandInfo.mActiveCallbacks.addListener(r5);
        }
        Trace.beginAsyncSection(TRANSITION_NAME, 0);
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: onTransitionComplete */
    public void lambda$executeCommand$3$OverviewCommandHelper(CommandInfo commandInfo, AbsSwipeUpHandler absSwipeUpHandler) {
        RecentsView visibleRecentsView;
        commandInfo.removeListener(absSwipeUpHandler);
        Trace.endAsyncSection(TRANSITION_NAME, 0);
        if (commandInfo.type == 2 && (visibleRecentsView = this.mOverviewComponentObserver.getActivityInterface().getVisibleRecentsView()) != null) {
            TaskView nextTaskView = visibleRecentsView.getNextTaskView();
            if (nextTaskView == null) {
                TaskView taskViewAt = visibleRecentsView.getTaskViewAt(0);
                if (taskViewAt != null) {
                    taskViewAt.requestFocus();
                } else {
                    visibleRecentsView.requestFocus();
                }
            } else {
                nextTaskView.requestFocus();
            }
        }
        lambda$launchTask$1$OverviewCommandHelper(commandInfo);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("OverviewCommandHelper:");
        printWriter.println("  mPendingCommands=" + this.mPendingCommands.size());
        if (!this.mPendingCommands.isEmpty()) {
            printWriter.println("    pendingCommandType=" + this.mPendingCommands.get(0).type);
        }
    }

    private static class CommandInfo {
        public final long createTime = SystemClock.elapsedRealtime();
        RecentsAnimationCallbacks mActiveCallbacks;
        public final int type;

        CommandInfo(int i) {
            this.type = i;
        }

        /* access modifiers changed from: package-private */
        public void removeListener(RecentsAnimationCallbacks.RecentsAnimationListener recentsAnimationListener) {
            RecentsAnimationCallbacks recentsAnimationCallbacks = this.mActiveCallbacks;
            if (recentsAnimationCallbacks != null) {
                recentsAnimationCallbacks.removeListener(recentsAnimationListener);
            }
        }
    }
}
