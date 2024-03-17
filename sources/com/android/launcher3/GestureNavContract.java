package com.android.launcher3;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.SurfaceControl;
import com.android.launcher3.views.ActivityContext;
import java.lang.ref.WeakReference;

public class GestureNavContract {
    public static final String EXTRA_GESTURE_CONTRACT = "gesture_nav_contract_v1";
    public static final String EXTRA_ICON_POSITION = "gesture_nav_contract_icon_position";
    public static final String EXTRA_ICON_SURFACE = "gesture_nav_contract_surface_control";
    public static final String EXTRA_ON_FINISH_CALLBACK = "gesture_nav_contract_finish_callback";
    public static final String EXTRA_REMOTE_CALLBACK = "android.intent.extra.REMOTE_CALLBACK";
    private static final String TAG = "GestureNavContract";
    private static StaticMessageReceiver sMessageReceiver;
    public final ComponentName componentName;
    private final Message mCallback;
    public final UserHandle user;

    public GestureNavContract(ComponentName componentName2, UserHandle userHandle, Message message) {
        this.componentName = componentName2;
        this.user = userHandle;
        this.mCallback = message;
    }

    public void sendEndPosition(RectF rectF, ActivityContext activityContext, SurfaceControl surfaceControl) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_ICON_POSITION, rectF);
        bundle.putParcelable(EXTRA_ICON_SURFACE, surfaceControl);
        if (sMessageReceiver == null) {
            sMessageReceiver = new StaticMessageReceiver();
        }
        bundle.putParcelable(EXTRA_ON_FINISH_CALLBACK, sMessageReceiver.setCurrentContext(activityContext));
        Message obtain = Message.obtain();
        obtain.copyFrom(this.mCallback);
        obtain.setData(bundle);
        try {
            obtain.replyTo.send(obtain);
        } catch (RemoteException e) {
            Log.e(TAG, "Error sending icon position", e);
        }
    }

    public static GestureNavContract fromIntent(Intent intent) {
        Bundle bundleExtra;
        if (!Utilities.ATLEAST_R || (bundleExtra = intent.getBundleExtra(EXTRA_GESTURE_CONTRACT)) == null) {
            return null;
        }
        intent.removeExtra(EXTRA_GESTURE_CONTRACT);
        ComponentName componentName2 = (ComponentName) bundleExtra.getParcelable("android.intent.extra.COMPONENT_NAME");
        UserHandle userHandle = (UserHandle) bundleExtra.getParcelable("android.intent.extra.USER");
        Message message = (Message) bundleExtra.getParcelable(EXTRA_REMOTE_CALLBACK);
        if (componentName2 == null || userHandle == null || message == null || message.replyTo == null) {
            return null;
        }
        return new GestureNavContract(componentName2, userHandle, message);
    }

    private static class StaticMessageReceiver implements Handler.Callback {
        private static final int MSG_CLOSE_LAST_TARGET = 0;
        private WeakReference<ActivityContext> mLastTarget;
        private final Messenger mMessenger;

        private StaticMessageReceiver() {
            this.mMessenger = new Messenger(new Handler(Looper.getMainLooper(), this));
            this.mLastTarget = new WeakReference<>((Object) null);
        }

        public Message setCurrentContext(ActivityContext activityContext) {
            this.mLastTarget = new WeakReference<>(activityContext);
            Message obtain = Message.obtain();
            obtain.replyTo = this.mMessenger;
            obtain.what = 0;
            return obtain;
        }

        public boolean handleMessage(Message message) {
            if (message.what != 0) {
                return false;
            }
            ActivityContext activityContext = (ActivityContext) this.mLastTarget.get();
            if (activityContext == null) {
                return true;
            }
            AbstractFloatingView.closeOpenViews(activityContext, false, 8192);
            return true;
        }
    }
}
