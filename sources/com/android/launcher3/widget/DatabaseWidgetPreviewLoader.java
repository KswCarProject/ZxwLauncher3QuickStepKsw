package com.android.launcher3.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Size;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.BitmapRenderer;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.icons.ShadowGenerator;
import com.android.launcher3.icons.cache.HandlerRunnable;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.pm.ShortcutConfigActivityInfo;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.views.ActivityContext;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DatabaseWidgetPreviewLoader {
    private static final String TAG = "WidgetPreviewLoader";
    private final Context mContext;
    private final float mPreviewBoxCornerRadius;

    public DatabaseWidgetPreviewLoader(Context context) {
        this.mContext = context;
        float computeEnforcedRadius = RoundedCornerEnforcement.computeEnforcedRadius(context);
        this.mPreviewBoxCornerRadius = computeEnforcedRadius <= 0.0f ? context.getResources().getDimension(R.dimen.widget_preview_corner_radius) : computeEnforcedRadius;
    }

    public HandlerRunnable loadPreview(WidgetItem widgetItem, Size size, Consumer<Bitmap> consumer) {
        Handler handler = Executors.UI_HELPER_EXECUTOR.getHandler();
        HandlerRunnable handlerRunnable = new HandlerRunnable(handler, new Supplier(widgetItem, size) {
            public final /* synthetic */ WidgetItem f$1;
            public final /* synthetic */ Size f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object get() {
                return DatabaseWidgetPreviewLoader.this.lambda$loadPreview$0$DatabaseWidgetPreviewLoader(this.f$1, this.f$2);
            }
        }, Executors.MAIN_EXECUTOR, consumer);
        Utilities.postAsyncCallback(handler, handlerRunnable);
        return handlerRunnable;
    }

    public /* synthetic */ Bitmap lambda$loadPreview$0$DatabaseWidgetPreviewLoader(WidgetItem widgetItem, Size size) {
        return generatePreview(widgetItem, size.getWidth(), size.getHeight());
    }

    private Bitmap generatePreview(WidgetItem widgetItem, int i, int i2) {
        if (widgetItem.widgetInfo != null) {
            return generateWidgetPreview(widgetItem.widgetInfo, i, (int[]) null);
        }
        return generateShortcutPreview(widgetItem.activityInfo, i, i2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap generateWidgetPreview(com.android.launcher3.widget.LauncherAppWidgetProviderInfo r18, int r19, int[] r20) {
        /*
            r17 = this;
            r12 = r17
            r9 = r18
            java.lang.String r1 = "WidgetPreviewLoader"
            if (r19 >= 0) goto L_0x000d
            r0 = 2147483647(0x7fffffff, float:NaN)
            r2 = r0
            goto L_0x000f
        L_0x000d:
            r2 = r19
        L_0x000f:
            int r0 = r9.previewImage
            r3 = 0
            r4 = 0
            if (r0 == 0) goto L_0x0064
            android.content.Context r0 = r12.mContext     // Catch:{ OutOfMemoryError -> 0x001c }
            android.graphics.drawable.Drawable r3 = r9.loadPreviewImage(r0, r4)     // Catch:{ OutOfMemoryError -> 0x001c }
            goto L_0x0035
        L_0x001c:
            r0 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Error loading widget preview for: "
            java.lang.StringBuilder r5 = r5.append(r6)
            android.content.ComponentName r6 = r9.provider
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            android.util.Log.w(r1, r5, r0)
        L_0x0035:
            if (r3 == 0) goto L_0x003c
            android.graphics.drawable.Drawable r0 = r12.mutateOnMainThread(r3)
            goto L_0x0065
        L_0x003c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "Can't load widget preview drawable 0x"
            java.lang.StringBuilder r0 = r0.append(r5)
            int r5 = r9.previewImage
            java.lang.String r5 = java.lang.Integer.toHexString(r5)
            java.lang.StringBuilder r0 = r0.append(r5)
            java.lang.String r5 = " for provider: "
            java.lang.StringBuilder r0 = r0.append(r5)
            android.content.ComponentName r5 = r9.provider
            java.lang.StringBuilder r0 = r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r1, r0)
        L_0x0064:
            r0 = r3
        L_0x0065:
            r1 = 1
            if (r0 == 0) goto L_0x006a
            r3 = r1
            goto L_0x006b
        L_0x006a:
            r3 = r4
        L_0x006b:
            int r7 = r9.spanX
            int r8 = r9.spanY
            android.content.Context r5 = r12.mContext
            android.content.Context r5 = com.android.launcher3.views.ActivityContext.lookupContext(r5)
            com.android.launcher3.views.ActivityContext r5 = (com.android.launcher3.views.ActivityContext) r5
            com.android.launcher3.DeviceProfile r10 = r5.getDeviceProfile()
            if (r3 == 0) goto L_0x0092
            int r5 = r0.getIntrinsicWidth()
            if (r5 <= 0) goto L_0x0092
            int r5 = r0.getIntrinsicHeight()
            if (r5 <= 0) goto L_0x0092
            int r5 = r0.getIntrinsicWidth()
            int r6 = r0.getIntrinsicHeight()
            goto L_0x00a7
        L_0x0092:
            android.content.Context r5 = r12.mContext
            android.content.ComponentName r6 = r9.provider
            android.util.Size r5 = com.android.launcher3.widget.util.WidgetSizes.getWidgetPaddedSizePx(r5, r6, r10, r7, r8)
            int r6 = r5.getWidth()
            int r5 = r5.getHeight()
            r16 = r6
            r6 = r5
            r5 = r16
        L_0x00a7:
            if (r20 == 0) goto L_0x00ab
            r20[r4] = r5
        L_0x00ab:
            r4 = 1065353216(0x3f800000, float:1.0)
            if (r5 <= r2) goto L_0x00b4
            float r2 = (float) r2
            float r11 = (float) r5
            float r2 = r2 / r11
            r11 = r2
            goto L_0x00b5
        L_0x00b4:
            r11 = r4
        L_0x00b5:
            int r2 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x00c7
            float r2 = (float) r5
            float r2 = r2 * r11
            int r2 = (int) r2
            int r5 = java.lang.Math.max(r2, r1)
            float r2 = (float) r6
            float r2 = r2 * r11
            int r2 = (int) r2
            int r6 = java.lang.Math.max(r2, r1)
        L_0x00c7:
            r13 = r5
            r14 = r6
            com.android.launcher3.widget.-$$Lambda$DatabaseWidgetPreviewLoader$JlL6ly4gEnLqNDaNAHPey32AqI8 r15 = new com.android.launcher3.widget.-$$Lambda$DatabaseWidgetPreviewLoader$JlL6ly4gEnLqNDaNAHPey32AqI8
            r1 = r15
            r2 = r17
            r4 = r0
            r5 = r13
            r6 = r14
            r9 = r18
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11)
            android.graphics.Bitmap r0 = com.android.launcher3.icons.BitmapRenderer.createHardwareBitmap(r13, r14, r15)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.widget.DatabaseWidgetPreviewLoader.generateWidgetPreview(com.android.launcher3.widget.LauncherAppWidgetProviderInfo, int, int[]):android.graphics.Bitmap");
    }

    public /* synthetic */ void lambda$generateWidgetPreview$1$DatabaseWidgetPreviewLoader(boolean z, Drawable drawable, int i, int i2, int i3, int i4, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, DeviceProfile deviceProfile, float f, Canvas canvas) {
        RectF rectF;
        Drawable drawable2 = drawable;
        int i5 = i;
        int i6 = i2;
        int i7 = i3;
        int i8 = i4;
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo2 = launcherAppWidgetProviderInfo;
        Canvas canvas2 = canvas;
        if (z) {
            drawable2.setBounds(0, 0, i5, i6);
            drawable2.draw(canvas2);
            return;
        }
        Paint paint = new Paint(1);
        if (Utilities.ATLEAST_S) {
            rectF = new RectF(0.0f, 0.0f, (float) i5, (float) i6);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(-1);
            float dimension = this.mContext.getResources().getDimension(17104904);
            canvas2.drawRoundRect(rectF, dimension, dimension, paint);
        } else {
            rectF = drawBoxWithShadow(canvas2, i5, i6);
        }
        RectF rectF2 = rectF;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(this.mContext.getResources().getDimension(R.dimen.widget_preview_cell_divider_width));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        float f2 = rectF2.left;
        float width = rectF2.width() / ((float) i7);
        int i9 = 1;
        while (i9 < i7) {
            float f3 = f2 + width;
            canvas.drawLine(f3, 0.0f, f3, (float) i6, paint);
            i9++;
            f2 = f3;
        }
        float f4 = rectF2.top;
        float height = rectF2.height() / ((float) i8);
        for (int i10 = 1; i10 < i8; i10++) {
            f4 += height;
            canvas.drawLine(0.0f, f4, (float) i5, f4, paint);
        }
        try {
            Drawable fullResIcon = LauncherAppState.getInstance(this.mContext).getIconCache().getFullResIcon(launcherAppWidgetProviderInfo2.provider.getPackageName(), launcherAppWidgetProviderInfo2.icon);
            if (fullResIcon != null) {
                int min = (int) Math.min(((float) deviceProfile.iconSizePx) * f, Math.min(rectF2.width(), rectF2.height()));
                Drawable mutateOnMainThread = mutateOnMainThread(fullResIcon);
                int i11 = (i5 - min) / 2;
                int i12 = (i6 - min) / 2;
                mutateOnMainThread.setBounds(i11, i12, i11 + min, min + i12);
                mutateOnMainThread.draw(canvas2);
            }
        } catch (Resources.NotFoundException unused) {
        }
    }

    private RectF drawBoxWithShadow(Canvas canvas, int i, int i2) {
        Resources resources = this.mContext.getResources();
        ShadowGenerator.Builder builder = new ShadowGenerator.Builder(-1);
        builder.shadowBlur = resources.getDimension(R.dimen.widget_preview_shadow_blur);
        builder.radius = this.mPreviewBoxCornerRadius;
        builder.keyShadowDistance = resources.getDimension(R.dimen.widget_preview_key_shadow_distance);
        builder.bounds.set(builder.shadowBlur, builder.shadowBlur, ((float) i) - builder.shadowBlur, (((float) i2) - builder.shadowBlur) - builder.keyShadowDistance);
        builder.drawShadow(canvas);
        return builder.bounds;
    }

    private Bitmap generateShortcutPreview(ShortcutConfigActivityInfo shortcutConfigActivityInfo, int i, int i2) {
        int i3 = ((ActivityContext) ActivityContext.lookupContext(this.mContext)).getDeviceProfile().allAppsIconSizePx;
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.widget_preview_shortcut_padding);
        int i4 = (dimensionPixelSize * 2) + i3;
        if (i2 >= i4 && i >= i4) {
            return BitmapRenderer.createHardwareBitmap(i4, i4, new BitmapRenderer(i4, shortcutConfigActivityInfo, dimensionPixelSize, i3) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ ShortcutConfigActivityInfo f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void draw(Canvas canvas) {
                    DatabaseWidgetPreviewLoader.this.lambda$generateShortcutPreview$2$DatabaseWidgetPreviewLoader(this.f$1, this.f$2, this.f$3, this.f$4, canvas);
                }
            });
        }
        throw new RuntimeException("Max size is too small for preview");
    }

    public /* synthetic */ void lambda$generateShortcutPreview$2$DatabaseWidgetPreviewLoader(int i, ShortcutConfigActivityInfo shortcutConfigActivityInfo, int i2, int i3, Canvas canvas) {
        drawBoxWithShadow(canvas, i, i);
        LauncherIcons obtain = LauncherIcons.obtain(this.mContext);
        FastBitmapDrawable newIcon = obtain.createBadgedIconBitmap(mutateOnMainThread(shortcutConfigActivityInfo.getFullResIcon(LauncherAppState.getInstance(this.mContext).getIconCache()))).newIcon(this.mContext);
        obtain.recycle();
        int i4 = i3 + i2;
        newIcon.setBounds(i2, i2, i4, i4);
        newIcon.draw(canvas);
    }

    private Drawable mutateOnMainThread(Drawable drawable) {
        try {
            LooperExecutor looperExecutor = Executors.MAIN_EXECUTOR;
            Objects.requireNonNull(drawable);
            return (Drawable) looperExecutor.submit(new Callable(drawable) {
                public final /* synthetic */ Drawable f$0;

                {
                    this.f$0 = r1;
                }

                public final Object call() {
                    return this.f$0.mutate();
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e2) {
            throw new RuntimeException(e2);
        }
    }
}
