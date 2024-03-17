package com.android.launcher3.icons;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import androidx.core.view.ViewCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GraphicsUtils {
    private static final String TAG = "GraphicsUtils";
    public static Runnable sOnNewBitmapRunnable = $$Lambda$GraphicsUtils$nWCqLw6Dl8ASiZoBWOYgQdw0.INSTANCE;

    static /* synthetic */ void lambda$static$0() {
    }

    public static int setColorAlphaBound(int i, int i2) {
        if (i2 < 0) {
            i2 = 0;
        } else if (i2 > 255) {
            i2 = 255;
        }
        return (i & ViewCompat.MEASURED_SIZE_MASK) | (i2 << 24);
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(getExpectedBitmapSize(bitmap));
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException unused) {
            Log.w(TAG, "Could not write bitmap");
            return null;
        }
    }

    static int getExpectedBitmapSize(Bitmap bitmap) {
        return bitmap.getWidth() * bitmap.getHeight() * 4;
    }

    public static int getArea(Region region) {
        RegionIterator regionIterator = new RegionIterator(region);
        Rect rect = new Rect();
        int i = 0;
        while (regionIterator.next(rect)) {
            i += rect.width() * rect.height();
        }
        return i;
    }

    public static void noteNewBitmapCreated() {
        sOnNewBitmapRunnable.run();
    }

    public static Path getShapePath(int i) {
        AdaptiveIconDrawable adaptiveIconDrawable = new AdaptiveIconDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK), new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
        adaptiveIconDrawable.setBounds(0, 0, i, i);
        return new Path(adaptiveIconDrawable.getIconMask());
    }

    public static int getAttrColor(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        int color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        return color;
    }

    public static float getFloat(Context context, int i, float f) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{i});
        float f2 = obtainStyledAttributes.getFloat(0, f);
        obtainStyledAttributes.recycle();
        return f2;
    }
}
