package com.android.launcher3.popup;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

public class RoundedArrowDrawable extends Drawable {
    private final Paint mPaint;
    private final Path mPath;

    public int getOpacity() {
        return -3;
    }

    public RoundedArrowDrawable(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, boolean z, boolean z2, int i) {
        float f9 = f;
        float f10 = f2;
        Path path = new Path();
        this.mPath = path;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(i);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        float f11 = f3;
        addDownPointingRoundedTriangleToPath(f, f2, f3, path);
        clipPopupBodyFromPath(f4, f5, f6, f7, f8, path);
        Matrix matrix = new Matrix();
        matrix.setScale(z2 ? 1.0f : -1.0f, z ? -1.0f : 1.0f, f9 * 0.5f, f10 * 0.5f);
        path.transform(matrix);
    }

    public RoundedArrowDrawable(float f, float f2, float f3, boolean z, int i) {
        Path path = new Path();
        this.mPath = path;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(i);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        addDownPointingRoundedTriangleToPath(f, f2, f3, path);
        Matrix matrix = new Matrix();
        matrix.setRotate(z ? 90.0f : -90.0f, f * 0.5f, f2 * 0.5f);
        path.transform(matrix);
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(this.mPath, this.mPaint);
    }

    public void getOutline(Outline outline) {
        outline.setPath(this.mPath);
    }

    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    private static void addDownPointingRoundedTriangleToPath(float f, float f2, float f3, Path path) {
        float f4 = f;
        float f5 = f2;
        float f6 = f3;
        Path path2 = path;
        float f7 = f4 / (f5 * 2.0f);
        double d = (double) f5;
        double atan = (double) ((float) Math.atan((double) f7));
        float sin = (float) (d - (((double) f6) / Math.sin(atan)));
        double d2 = (double) (f6 / f7);
        float cos = (float) (d - (d2 * Math.cos(atan)));
        float f8 = f4 / 2.0f;
        float degrees = (float) Math.toDegrees(atan);
        path.reset();
        path2.moveTo(0.0f, 0.0f);
        path2.lineTo(f, 0.0f);
        path2.lineTo(((float) (Math.sin(atan) * d2)) + f8, cos);
        path.arcTo(f8 - f6, sin - f6, f8 + f6, sin + f6, degrees, 180.0f - (2.0f * degrees), false);
        path2.lineTo(0.0f, 0.0f);
        path.close();
    }

    private static void clipPopupBodyFromPath(float f, float f2, float f3, float f4, float f5, Path path) {
        Path path2 = new Path();
        path2.addRoundRect(0.0f, 0.0f, f2, f3, f, f, Path.Direction.CW);
        path2.offset(-f4, ((-f3) + f5) - 0.5f);
        path.op(path2, Path.Op.DIFFERENCE);
    }
}
