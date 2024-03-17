package com.android.quickstep.util;

import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.IAssistDataReceiver;
import android.app.assist.AssistContent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.android.launcher3.util.Executors;
import com.android.quickstep.util.AssistContentRequester;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

public class AssistContentRequester {
    private static final String ASSIST_KEY_CONTENT = "content";
    private static final String TAG = "AssistContentRequester";
    private final IActivityTaskManager mActivityTaskManager = ActivityTaskManager.getService();
    private final Executor mCallbackExecutor;
    private final String mPackageName;
    /* access modifiers changed from: private */
    public final Map<Object, Callback> mPendingCallbacks = Collections.synchronizedMap(new WeakHashMap());
    private final Executor mSystemInteractionExecutor;

    public interface Callback {
        void onAssistContentAvailable(AssistContent assistContent);
    }

    public AssistContentRequester(Context context) {
        this.mPackageName = context.getApplicationContext().getPackageName();
        this.mCallbackExecutor = Executors.MAIN_EXECUTOR;
        this.mSystemInteractionExecutor = Executors.UI_HELPER_EXECUTOR;
    }

    public void requestAssistContent(int i, Callback callback) {
        this.mSystemInteractionExecutor.execute(new Runnable(callback, i) {
            public final /* synthetic */ AssistContentRequester.Callback f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                AssistContentRequester.this.lambda$requestAssistContent$0$AssistContentRequester(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$requestAssistContent$0$AssistContentRequester(Callback callback, int i) {
        try {
            this.mActivityTaskManager.requestAssistDataForTask(new AssistDataReceiver(callback, this), i, this.mPackageName);
        } catch (RemoteException e) {
            Log.e(TAG, "Requesting assist content failed for task: " + i, e);
        }
    }

    /* access modifiers changed from: private */
    public void executeOnMainExecutor(Runnable runnable) {
        this.mCallbackExecutor.execute(runnable);
    }

    private static final class AssistDataReceiver extends IAssistDataReceiver.Stub {
        private final Object mCallbackKey;
        private final WeakReference<AssistContentRequester> mParentRef;

        public void onHandleAssistScreenshot(Bitmap bitmap) {
        }

        AssistDataReceiver(Callback callback, AssistContentRequester assistContentRequester) {
            Object obj = new Object();
            this.mCallbackKey = obj;
            assistContentRequester.mPendingCallbacks.put(obj, callback);
            this.mParentRef = new WeakReference<>(assistContentRequester);
        }

        public void onHandleAssistData(Bundle bundle) {
            if (bundle != null) {
                AssistContent assistContent = (AssistContent) bundle.getParcelable(AssistContentRequester.ASSIST_KEY_CONTENT);
                if (assistContent == null) {
                    Log.e(AssistContentRequester.TAG, "Received AssistData, but no AssistContent found");
                    return;
                }
                AssistContentRequester assistContentRequester = (AssistContentRequester) this.mParentRef.get();
                if (assistContentRequester != null) {
                    Callback callback = (Callback) assistContentRequester.mPendingCallbacks.get(this.mCallbackKey);
                    if (callback != null) {
                        assistContentRequester.executeOnMainExecutor(new Runnable(assistContent) {
                            public final /* synthetic */ AssistContent f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                AssistContentRequester.Callback.this.onAssistContentAvailable(this.f$1);
                            }
                        });
                    } else {
                        Log.d(AssistContentRequester.TAG, "Callback received after calling UI was disposed of");
                    }
                } else {
                    Log.d(AssistContentRequester.TAG, "Callback received after Requester was collected");
                }
            }
        }
    }
}
