package com.android.launcher3.taskbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.android.launcher3.R;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017R\u001a\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"}, d2 = {"Lcom/android/launcher3/taskbar/TaskbarBackgroundRenderer;", "", "context", "Lcom/android/launcher3/taskbar/TaskbarActivityContext;", "(Lcom/android/launcher3/taskbar/TaskbarActivityContext;)V", "backgroundHeight", "", "getBackgroundHeight", "()F", "setBackgroundHeight", "(F)V", "invertedLeftCornerPath", "Landroid/graphics/Path;", "invertedRightCornerPath", "leftCornerRadius", "paint", "Landroid/graphics/Paint;", "getPaint", "()Landroid/graphics/Paint;", "rightCornerRadius", "draw", "", "canvas", "Landroid/graphics/Canvas;", "Launcher3_Android13_aospWithQuickstepRelease"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: TaskbarBackgroundRenderer.kt */
public final class TaskbarBackgroundRenderer {
    private float backgroundHeight;
    private final Path invertedLeftCornerPath;
    private final Path invertedRightCornerPath;
    private final float leftCornerRadius;
    private final Paint paint;
    private final float rightCornerRadius;

    public TaskbarBackgroundRenderer(TaskbarActivityContext taskbarActivityContext) {
        Intrinsics.checkNotNullParameter(taskbarActivityContext, "context");
        Paint paint2 = new Paint();
        this.paint = paint2;
        this.backgroundHeight = (float) taskbarActivityContext.getDeviceProfile().taskbarSize;
        float leftCornerRadius2 = (float) taskbarActivityContext.getLeftCornerRadius();
        this.leftCornerRadius = leftCornerRadius2;
        float rightCornerRadius2 = (float) taskbarActivityContext.getRightCornerRadius();
        this.rightCornerRadius = rightCornerRadius2;
        Path path = new Path();
        this.invertedLeftCornerPath = path;
        Path path2 = new Path();
        this.invertedRightCornerPath = path2;
        paint2.setColor(taskbarActivityContext.getColor(R.color.taskbar_background));
        paint2.setFlags(1);
        paint2.setStyle(Paint.Style.FILL);
        Path path3 = new Path();
        path3.addRect(0.0f, 0.0f, leftCornerRadius2, leftCornerRadius2, Path.Direction.CW);
        Path path4 = new Path();
        path4.addCircle(leftCornerRadius2, 0.0f, leftCornerRadius2, Path.Direction.CW);
        path.op(path3, path4, Path.Op.DIFFERENCE);
        path3.reset();
        path3.addRect(0.0f, 0.0f, rightCornerRadius2, rightCornerRadius2, Path.Direction.CW);
        path4.reset();
        path4.addCircle(0.0f, 0.0f, rightCornerRadius2, Path.Direction.CW);
        path2.op(path3, path4, Path.Op.DIFFERENCE);
    }

    public final Paint getPaint() {
        return this.paint;
    }

    public final float getBackgroundHeight() {
        return this.backgroundHeight;
    }

    public final void setBackgroundHeight(float f) {
        this.backgroundHeight = f;
    }

    public final void draw(Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        canvas.save();
        canvas.translate(0.0f, ((float) canvas.getHeight()) - this.backgroundHeight);
        canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), this.backgroundHeight, this.paint);
        canvas.translate(0.0f, -this.leftCornerRadius);
        canvas.drawPath(this.invertedLeftCornerPath, this.paint);
        canvas.translate(0.0f, this.leftCornerRadius);
        float f = this.rightCornerRadius;
        canvas.translate(((float) canvas.getWidth()) - f, -f);
        canvas.drawPath(this.invertedRightCornerPath, this.paint);
        canvas.restore();
    }
}
