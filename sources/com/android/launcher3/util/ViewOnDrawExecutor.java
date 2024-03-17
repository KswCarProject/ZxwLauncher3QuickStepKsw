package com.android.launcher3.util;

import android.view.View;
import android.view.ViewTreeObserver;
import com.android.launcher3.Launcher;
import com.android.launcher3.Workspace;
import java.util.Objects;
import java.util.function.Consumer;

public class ViewOnDrawExecutor implements ViewTreeObserver.OnDrawListener, Runnable, View.OnAttachStateChangeListener {
    private View mAttachedView;
    private boolean mCancelled;
    private boolean mCompleted;
    private boolean mFirstDrawCompleted;
    private boolean mLoadAnimationCompleted;
    private Consumer<ViewOnDrawExecutor> mOnClearCallback;
    private final RunnableList mTasks;

    public void onViewDetachedFromWindow(View view) {
    }

    public ViewOnDrawExecutor(RunnableList runnableList) {
        this.mTasks = runnableList;
    }

    public void attachTo(Launcher launcher) {
        Objects.requireNonNull(launcher);
        this.mOnClearCallback = new Consumer() {
            public final void accept(Object obj) {
                Launcher.this.clearPendingExecutor((ViewOnDrawExecutor) obj);
            }
        };
        Workspace<?> workspace = launcher.getWorkspace();
        this.mAttachedView = workspace;
        workspace.addOnAttachStateChangeListener(this);
        if (this.mAttachedView.isAttachedToWindow()) {
            attachObserver();
        }
    }

    private void attachObserver() {
        if (!this.mCompleted) {
            this.mAttachedView.getViewTreeObserver().addOnDrawListener(this);
        }
    }

    public void onViewAttachedToWindow(View view) {
        attachObserver();
    }

    public void onDraw() {
        this.mFirstDrawCompleted = true;
        this.mAttachedView.post(this);
    }

    public void onLoadAnimationCompleted() {
        this.mLoadAnimationCompleted = true;
        View view = this.mAttachedView;
        if (view != null) {
            view.post(this);
        }
    }

    public void run() {
        if (this.mLoadAnimationCompleted && this.mFirstDrawCompleted && !this.mCompleted) {
            markCompleted();
        }
    }

    public void markCompleted() {
        if (!this.mCancelled) {
            this.mTasks.executeAllAndDestroy();
        }
        this.mCompleted = true;
        View view = this.mAttachedView;
        if (view != null) {
            view.getViewTreeObserver().removeOnDrawListener(this);
            this.mAttachedView.removeOnAttachStateChangeListener(this);
        }
        Consumer<ViewOnDrawExecutor> consumer = this.mOnClearCallback;
        if (consumer != null) {
            consumer.accept(this);
        }
    }

    public void cancel() {
        this.mCancelled = true;
        markCompleted();
    }
}
