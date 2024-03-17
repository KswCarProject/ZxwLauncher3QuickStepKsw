package com.android.quickstep;

import android.app.prediction.AppTarget;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import com.android.launcher3.util.Executors;
import com.android.quickstep.util.ImageActionUtils;
import com.android.systemui.shared.recents.model.Task;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ImageActionsApi {
    private static final String TAG = "com.android.launcher3ImageActionsApi";
    protected final Supplier<Bitmap> mBitmapSupplier;
    protected final Context mContext;
    protected final SystemUiProxy mSystemUiProxy;

    public ImageActionsApi(Context context, Supplier<Bitmap> supplier) {
        this.mContext = context;
        this.mBitmapSupplier = supplier;
        this.mSystemUiProxy = SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
    }

    public void shareWithExplicitIntent(Rect rect, Intent intent) {
        addImageAndSendIntent(rect, intent, false, (Runnable) null);
    }

    public void shareAsDataWithExplicitIntent(Rect rect, Intent intent, Runnable runnable) {
        addImageAndSendIntent(rect, intent, true, runnable);
    }

    private void addImageAndSendIntent(Rect rect, Intent intent, boolean z, Runnable runnable) {
        if (this.mBitmapSupplier.get() == null) {
            Log.e(TAG, "No snapshot available, not starting share.");
        } else {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(rect, intent, z, runnable) {
                public final /* synthetic */ Rect f$1;
                public final /* synthetic */ Intent f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ Runnable f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    ImageActionsApi.this.lambda$addImageAndSendIntent$1$ImageActionsApi(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }
    }

    public /* synthetic */ void lambda$addImageAndSendIntent$1$ImageActionsApi(Rect rect, Intent intent, boolean z, Runnable runnable) {
        ImageActionUtils.persistBitmapAndStartActivity(this.mContext, this.mBitmapSupplier.get(), rect, intent, (BiFunction<Uri, Intent, Intent[]>) new BiFunction(z) {
            public final /* synthetic */ boolean f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj, Object obj2) {
                return ImageActionsApi.lambda$addImageAndSendIntent$0(this.f$0, (Uri) obj, (Intent) obj2);
            }
        }, TAG, runnable);
    }

    static /* synthetic */ Intent[] lambda$addImageAndSendIntent$0(boolean z, Uri uri, Intent intent) {
        intent.addFlags(1);
        if (z) {
            intent.setData(uri);
        } else {
            intent.putExtra("android.intent.extra.STREAM", uri);
        }
        return new Intent[]{intent};
    }

    public void startShareActivity(Rect rect) {
        ImageActionUtils.startShareActivity(this.mContext, this.mBitmapSupplier, rect, (Intent) null, TAG);
    }

    public void saveScreenshot(Bitmap bitmap, Rect rect, Insets insets, Task.TaskKey taskKey) {
        ImageActionUtils.saveScreenshot(this.mSystemUiProxy, bitmap, rect, insets, taskKey);
    }

    public void shareImage(RectF rectF, ShortcutInfo shortcutInfo, AppTarget appTarget) {
        ImageActionUtils.shareImage(this.mContext, this.mBitmapSupplier, rectF, shortcutInfo, appTarget, TAG);
    }
}
