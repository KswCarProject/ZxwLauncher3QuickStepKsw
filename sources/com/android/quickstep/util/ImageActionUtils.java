package com.android.quickstep.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.prediction.AppTarget;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import androidx.core.content.FileProvider;
import androidx.core.content.pm.ShortcutManagerCompat;
import com.android.internal.util.ScreenshotHelper;
import com.android.launcher3.util.Executors;
import com.android.quickstep.SystemUiProxy;
import com.android.systemui.shared.recents.model.Task;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ImageActionUtils {
    private static final String AUTHORITY = "com.android.launcher3.overview.fileprovider";
    private static final String BASE_NAME = "overview_image_";
    private static final long FILE_LIFE = 86400000;
    private static final String SUB_FOLDER = "Overview";
    private static final String TAG = "ImageActionUtils";

    public static void saveScreenshot(SystemUiProxy systemUiProxy, Bitmap bitmap, Rect rect, Insets insets, Task.TaskKey taskKey) {
        systemUiProxy.handleImageBundleAsScreenshot(ScreenshotHelper.HardwareBitmapBundler.hardwareBitmapToBundle(bitmap), rect, insets, taskKey);
    }

    public static void shareImage(Context context, Supplier<Bitmap> supplier, RectF rectF, ShortcutInfo shortcutInfo, AppTarget appTarget, String str) {
        if (supplier.get() != null) {
            Rect rect = new Rect();
            rectF.round(rect);
            Intent intent = new Intent();
            Uri imageUri = getImageUri(supplier.get(), rect, context, str);
            intent.setAction("android.intent.action.SEND").setComponent(new ComponentName(appTarget.getPackageName(), appTarget.getClassName())).addFlags(268435456).addFlags(1).setType("image/png").putExtra("android.intent.extra.STREAM", imageUri).putExtra(ShortcutManagerCompat.EXTRA_SHORTCUT_ID, shortcutInfo.getId()).setClipData(new ClipData(new ClipDescription("content", new String[]{"image/png"}), new ClipData.Item(imageUri)));
            if (context.getUserId() != appTarget.getUser().getIdentifier()) {
                intent.prepareToLeaveUser(context.getUserId());
                intent.fixUris(context.getUserId());
                context.startActivityAsUser(intent, appTarget.getUser());
                return;
            }
            context.startActivity(intent);
        }
    }

    public static void startShareActivity(Context context, Supplier<Bitmap> supplier, Rect rect, Intent intent, String str) {
        if (supplier.get() == null) {
            Log.e(str, "No snapshot available, not starting share.");
        } else {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(context, supplier, rect, intent, str) {
                public final /* synthetic */ Context f$0;
                public final /* synthetic */ Supplier f$1;
                public final /* synthetic */ Rect f$2;
                public final /* synthetic */ Intent f$3;
                public final /* synthetic */ String f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    ImageActionUtils.persistBitmapAndStartActivity(this.f$0, (Bitmap) this.f$1.get(), this.f$2, this.f$3, $$Lambda$ImageActionUtils$iWnfhZSJ59KYEAx__KXAH75ft8.INSTANCE, this.f$4);
                }
            });
        }
    }

    public static void startShareActivity(Context context, Supplier<Bitmap> supplier, Rect rect, Intent intent, String str, View view) {
        if (supplier.get() == null) {
            Log.e(str, "No snapshot available, not starting share.");
        } else {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(context, supplier, rect, intent, str, view) {
                public final /* synthetic */ Context f$0;
                public final /* synthetic */ Supplier f$1;
                public final /* synthetic */ Rect f$2;
                public final /* synthetic */ Intent f$3;
                public final /* synthetic */ String f$4;
                public final /* synthetic */ View f$5;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    ImageActionUtils.persistBitmapAndStartActivity(this.f$0, (Bitmap) this.f$1.get(), this.f$2, this.f$3, (BiFunction<Uri, Intent, Intent[]>) $$Lambda$ImageActionUtils$iWnfhZSJ59KYEAx__KXAH75ft8.INSTANCE, this.f$4, this.f$5);
                }
            });
        }
    }

    public static void persistBitmapAndStartActivity(Context context, Bitmap bitmap, Rect rect, Intent intent, BiFunction<Uri, Intent, Intent[]> biFunction, String str) {
        persistBitmapAndStartActivity(context, bitmap, rect, intent, biFunction, str, (Runnable) null);
    }

    public static void persistBitmapAndStartActivity(Context context, Bitmap bitmap, Rect rect, Intent intent, BiFunction<Uri, Intent, Intent[]> biFunction, String str, Runnable runnable) {
        Intent[] apply = biFunction.apply(getImageUri(bitmap, rect, context, str), intent);
        try {
            if (apply.length == 1) {
                context.startActivity(apply[0]);
            } else {
                context.startActivities(apply);
            }
        } catch (ActivityNotFoundException unused) {
            Log.e(TAG, "No activity found to receive image intent");
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public static void persistBitmapAndStartActivity(Context context, Bitmap bitmap, Rect rect, Intent intent, BiFunction<Uri, Intent, Intent[]> biFunction, String str, View view) {
        Intent[] apply = biFunction.apply(getImageUri(bitmap, rect, context, str), intent);
        if (apply.length == 1) {
            Executors.MAIN_EXECUTOR.execute(new Runnable(context, apply, view) {
                public final /* synthetic */ Context f$0;
                public final /* synthetic */ Intent[] f$1;
                public final /* synthetic */ View f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    this.f$0.startActivity(this.f$1[0], ActivityOptions.makeSceneTransitionAnimation((Activity) this.f$0, this.f$2, "screenshot_preview_image").toBundle());
                }
            });
        } else {
            Executors.MAIN_EXECUTOR.execute(new Runnable(context, apply, view) {
                public final /* synthetic */ Context f$0;
                public final /* synthetic */ Intent[] f$1;
                public final /* synthetic */ View f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    this.f$0.startActivities(this.f$1, ActivityOptions.makeSceneTransitionAnimation((Activity) this.f$0, this.f$2, "screenshot_preview_image").toBundle());
                }
            });
        }
    }

    public static Uri getImageUri(Bitmap bitmap, Rect rect, Context context, String str) {
        int i;
        FileOutputStream fileOutputStream;
        clearOldCacheFiles(context);
        Bitmap cropBitmap = cropBitmap(bitmap, rect);
        if (rect == null) {
            i = 0;
        } else {
            i = rect.hashCode();
        }
        String str2 = BASE_NAME + bitmap.hashCode() + "_" + i + ".png";
        File file = new File(context.getCacheDir(), SUB_FOLDER);
        file.mkdir();
        File file2 = new File(file, str2);
        try {
            fileOutputStream = new FileOutputStream(file2);
            cropBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e(str, "Error saving image", e);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        return FileProvider.getUriForFile(context, AUTHORITY, file2);
        throw th;
    }

    public static Bitmap cropBitmap(Bitmap bitmap, Rect rect) {
        Rect rect2 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        if (rect == null) {
            rect = new Rect(rect2);
        }
        if (rect.equals(rect2)) {
            return bitmap;
        }
        if (bitmap.getConfig() != Bitmap.Config.HARDWARE) {
            return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
        }
        Picture picture = new Picture();
        picture.beginRecording(rect.width(), rect.height()).drawBitmap(bitmap, (float) (-rect.left), (float) (-rect.top), (Paint) null);
        picture.endRecording();
        return Bitmap.createBitmap(picture, rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
    }

    /* access modifiers changed from: private */
    public static Intent[] getShareIntentForImageUri(Uri uri, Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.setAction("android.intent.action.SEND").setComponent((ComponentName) null).addFlags(268435456).addFlags(1).setType("image/png").putExtra("android.intent.extra.STREAM", uri).setClipData(new ClipData(new ClipDescription("content", new String[]{"image/png"}), new ClipData.Item(uri)));
        return new Intent[]{Intent.createChooser(intent, (CharSequence) null).addFlags(268435456)};
    }

    private static void clearOldCacheFiles(Context context) {
        Executors.THREAD_POOL_EXECUTOR.execute(new Runnable(context) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                ImageActionUtils.lambda$clearOldCacheFiles$5(this.f$0);
            }
        });
    }

    static /* synthetic */ void lambda$clearOldCacheFiles$5(Context context) {
        File[] listFiles = new File(context.getCacheDir(), SUB_FOLDER).listFiles($$Lambda$ImageActionUtils$VUkVCFVMOkMKcJgnCajKCuGDnc.INSTANCE);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.lastModified() + FILE_LIFE < System.currentTimeMillis()) {
                    file.delete();
                }
            }
        }
    }
}
