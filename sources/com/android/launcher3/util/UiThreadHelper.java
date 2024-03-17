package com.android.launcher3.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;

public class UiThreadHelper {
    private static final MainThreadInitializedObject<Handler> HANDLER = new MainThreadInitializedObject<>($$Lambda$UiThreadHelper$0IHfKpyMUGoHm1HxmydM06ZU0.INSTANCE);
    private static final int MSG_HIDE_KEYBOARD = 1;
    private static final int MSG_RUN_COMMAND = 3;
    private static final int MSG_SET_ORIENTATION = 2;
    private static final String STATS_LOGGER_KEY = "STATS_LOGGER_KEY";

    public interface AsyncCommand {
        void execute(Context context, int i, int i2);
    }

    static /* synthetic */ Handler lambda$static$0(Context context) {
        return new Handler(Executors.UI_HELPER_EXECUTOR.getLooper(), new UiCallbacks(context));
    }

    public static void hideKeyboardAsync(ActivityContext activityContext, IBinder iBinder) {
        BaseDragLayer dragLayer = activityContext.getDragLayer();
        if (Utilities.ATLEAST_R) {
            Preconditions.assertUIThread();
            WindowInsetsController windowInsetsController = dragLayer.getWindowInsetsController();
            WindowInsets rootWindowInsets = dragLayer.getRootWindowInsets();
            boolean z = rootWindowInsets != null && rootWindowInsets.isVisible(WindowInsets.Type.ime());
            if (windowInsetsController != null && z) {
                windowInsetsController.hide(WindowInsets.Type.ime());
                activityContext.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_KEYBOARD_CLOSED);
                return;
            }
        }
        Bundle bundle = new Bundle();
        MainThreadInitializedObject<Handler> mainThreadInitializedObject = HANDLER;
        bundle.putParcelable(STATS_LOGGER_KEY, Message.obtain(mainThreadInitializedObject.lambda$get$1$MainThreadInitializedObject(dragLayer.getContext()), new Runnable() {
            public final void run() {
                ActivityContext.this.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_KEYBOARD_CLOSED);
            }
        }));
        Message obtain = Message.obtain(mainThreadInitializedObject.lambda$get$1$MainThreadInitializedObject(dragLayer.getContext()), 1, iBinder);
        obtain.setData(bundle);
        obtain.sendToTarget();
    }

    public static void setOrientationAsync(Activity activity, int i) {
        Message.obtain(HANDLER.lambda$get$1$MainThreadInitializedObject(activity), 2, i, 0, activity).sendToTarget();
    }

    public static void setBackButtonAlphaAsync(Context context, AsyncCommand asyncCommand, float f, boolean z) {
        runAsyncCommand(context, asyncCommand, Float.floatToIntBits(f), z ? 1 : 0);
    }

    public static void runAsyncCommand(Context context, AsyncCommand asyncCommand, int i, int i2) {
        Message.obtain(HANDLER.lambda$get$1$MainThreadInitializedObject(context), 3, i, i2, asyncCommand).sendToTarget();
    }

    private static class UiCallbacks implements Handler.Callback {
        private final Context mContext;
        private final InputMethodManager mIMM;

        UiCallbacks(Context context) {
            this.mContext = context;
            this.mIMM = (InputMethodManager) context.getSystemService("input_method");
        }

        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                if (this.mIMM.hideSoftInputFromWindow((IBinder) message.obj, 0)) {
                    ((Message) message.getData().getParcelable(UiThreadHelper.STATS_LOGGER_KEY)).sendToTarget();
                }
                return true;
            } else if (i == 2) {
                ((Activity) message.obj).setRequestedOrientation(message.arg1);
                return true;
            } else if (i != 3) {
                return false;
            } else {
                ((AsyncCommand) message.obj).execute(this.mContext, message.arg1, message.arg2);
                return true;
            }
        }
    }
}
