package com.android.systemui.shared.recents.view;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.view.AppTransitionAnimationSpec;
import android.view.IAppTransitionAnimationSpecsFuture;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public abstract class AppTransitionAnimationSpecsFuture {
    /* access modifiers changed from: private */
    public FutureTask<List<AppTransitionAnimationSpecCompat>> mComposeTask = new FutureTask<>(new Callable<List<AppTransitionAnimationSpecCompat>>() {
        public List<AppTransitionAnimationSpecCompat> call() throws Exception {
            return AppTransitionAnimationSpecsFuture.this.composeSpecs();
        }
    });
    private final IAppTransitionAnimationSpecsFuture mFuture = new IAppTransitionAnimationSpecsFuture.Stub() {
        public AppTransitionAnimationSpec[] get() throws RemoteException {
            try {
                if (!AppTransitionAnimationSpecsFuture.this.mComposeTask.isDone()) {
                    AppTransitionAnimationSpecsFuture.this.mHandler.post(AppTransitionAnimationSpecsFuture.this.mComposeTask);
                }
                List list = (List) AppTransitionAnimationSpecsFuture.this.mComposeTask.get();
                FutureTask unused = AppTransitionAnimationSpecsFuture.this.mComposeTask = null;
                if (list == null) {
                    return null;
                }
                AppTransitionAnimationSpec[] appTransitionAnimationSpecArr = new AppTransitionAnimationSpec[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    appTransitionAnimationSpecArr[i] = ((AppTransitionAnimationSpecCompat) list.get(i)).toAppTransitionAnimationSpec();
                }
                return appTransitionAnimationSpecArr;
            } catch (Exception unused2) {
                return null;
            }
        }
    };
    /* access modifiers changed from: private */
    public final Handler mHandler;

    public abstract List<AppTransitionAnimationSpecCompat> composeSpecs();

    public AppTransitionAnimationSpecsFuture(Handler handler) {
        this.mHandler = handler;
    }

    public final IAppTransitionAnimationSpecsFuture getFuture() {
        return this.mFuture;
    }

    public final void composeSpecsSynchronous() {
        if (Looper.myLooper() == this.mHandler.getLooper()) {
            this.mComposeTask.run();
            return;
        }
        throw new RuntimeException("composeSpecsSynchronous() called from wrong looper");
    }
}
