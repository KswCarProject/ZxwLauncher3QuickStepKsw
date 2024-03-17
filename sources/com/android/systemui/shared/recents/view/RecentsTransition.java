package com.android.systemui.shared.recents.view;

import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.view.View;
import java.util.function.Consumer;

public class RecentsTransition {
    public static ActivityOptions createAspectScaleAnimation(Context context, Handler handler, boolean z, AppTransitionAnimationSpecsFuture appTransitionAnimationSpecsFuture, final Runnable runnable) {
        return ActivityOptions.makeMultiThumbFutureAspectScaleAnimation(context, handler, appTransitionAnimationSpecsFuture != null ? appTransitionAnimationSpecsFuture.getFuture() : null, new ActivityOptions.OnAnimationStartedListener() {
            private boolean mHandled;

            public void onAnimationStarted(long j) {
                if (!this.mHandled) {
                    this.mHandled = true;
                    Runnable runnable = runnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }
        }, z);
    }

    public static IRemoteCallback wrapStartedListener(final Handler handler, final Runnable runnable) {
        if (runnable == null) {
            return null;
        }
        return new IRemoteCallback.Stub() {
            public void sendResult(Bundle bundle) throws RemoteException {
                handler.post(runnable);
            }
        };
    }

    public static Bitmap drawViewIntoHardwareBitmap(int i, int i2, final View view, final float f, final int i3) {
        return createHardwareBitmap(i, i2, new Consumer<Canvas>() {
            public void accept(Canvas canvas) {
                float f = f;
                canvas.scale(f, f);
                int i = i3;
                if (i != 0) {
                    canvas.drawColor(i);
                }
                View view = view;
                if (view != null) {
                    view.draw(canvas);
                }
            }
        });
    }

    public static Bitmap createHardwareBitmap(int i, int i2, Consumer<Canvas> consumer) {
        Picture picture = new Picture();
        consumer.accept(picture.beginRecording(i, i2));
        picture.endRecording();
        return Bitmap.createBitmap(picture);
    }
}
