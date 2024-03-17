package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;

public interface BitmapRenderer {
    public static final boolean USE_HARDWARE_BITMAP = (Build.VERSION.SDK_INT >= 28);

    void draw(Canvas canvas);

    static Bitmap createSoftwareBitmap(int i, int i2, BitmapRenderer bitmapRenderer) {
        GraphicsUtils.noteNewBitmapCreated();
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        bitmapRenderer.draw(new Canvas(createBitmap));
        return createBitmap;
    }

    static Bitmap createHardwareBitmap(int i, int i2, BitmapRenderer bitmapRenderer) {
        if (!USE_HARDWARE_BITMAP) {
            return createSoftwareBitmap(i, i2, bitmapRenderer);
        }
        GraphicsUtils.noteNewBitmapCreated();
        Picture picture = new Picture();
        bitmapRenderer.draw(picture.beginRecording(i, i2));
        picture.endRecording();
        return Bitmap.createBitmap(picture);
    }

    static Bitmap createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4) {
        if (Build.VERSION.SDK_INT >= 26 && bitmap.getConfig() == Bitmap.Config.HARDWARE) {
            return createHardwareBitmap(i3, i4, new BitmapRenderer(bitmap, i, i2, i3, i4) {
                public final /* synthetic */ Bitmap f$0;
                public final /* synthetic */ int f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ int f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void draw(Canvas canvas) {
                    canvas.drawBitmap(this.f$0, new Rect(this.f$1, this.f$2, this.f$1 + this.f$3, this.f$2 + this.f$4), new RectF(0.0f, 0.0f, (float) this.f$3, (float) this.f$4), (Paint) null);
                }
            });
        }
        GraphicsUtils.noteNewBitmapCreated();
        return Bitmap.createBitmap(bitmap, i, i2, i3, i4);
    }
}
