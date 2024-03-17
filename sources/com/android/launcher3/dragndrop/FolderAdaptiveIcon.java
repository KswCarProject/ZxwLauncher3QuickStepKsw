package com.android.launcher3.dragndrop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.launcher3.Utilities;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.icons.BitmapRenderer;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.views.ActivityContext;

public class FolderAdaptiveIcon extends AdaptiveIconDrawable {
    private static final String TAG = "FolderAdaptiveIcon";
    private static final Rect sTmpRect = new Rect();
    private final Drawable mBadge;
    private final Drawable.ConstantState mConstantState;
    private final Path mMask;

    private FolderAdaptiveIcon(Drawable drawable, Drawable drawable2, Drawable drawable3, Path path) {
        super(drawable, drawable2);
        this.mBadge = drawable3;
        this.mMask = path;
        this.mConstantState = new MyConstantState(drawable.getConstantState(), drawable2.getConstantState(), drawable3.getConstantState(), path);
    }

    public Path getIconMask() {
        return this.mMask;
    }

    public Drawable getBadge() {
        return this.mBadge;
    }

    public static FolderAdaptiveIcon createFolderAdaptiveIcon(ActivityContext activityContext, int i, Point point) {
        Point point2 = point;
        Preconditions.assertNonUiThread();
        if (!Utilities.ATLEAST_P || point2.x != point2.y) {
            return null;
        }
        int i2 = point2.x;
        int max = Math.max(i2, activityContext.getDeviceProfile().folderIconSizePx);
        float f = (float) (max - i2);
        Picture picture = new Picture();
        Picture picture2 = new Picture();
        Picture picture3 = new Picture();
        Canvas beginRecording = picture.beginRecording(i2, i2);
        Canvas beginRecording2 = picture3.beginRecording(i2, i2);
        Canvas beginRecording3 = picture2.beginRecording(max, max);
        beginRecording3.translate(f, f);
        Path path = new Path();
        float f2 = -f;
        float f3 = ((float) i2) + f;
        path.addRect(f2, f2, f3, f3, Path.Direction.CCW);
        try {
            Executors.MAIN_EXECUTOR.submit(new Runnable(i, i2, beginRecording, beginRecording3, beginRecording2) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ Canvas f$3;
                public final /* synthetic */ Canvas f$4;
                public final /* synthetic */ Canvas f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    FolderAdaptiveIcon.lambda$createFolderAdaptiveIcon$0(ActivityContext.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            }).get();
            picture.endRecording();
            picture2.endRecording();
            picture3.endRecording();
            return new FolderAdaptiveIcon(new BitmapRendererDrawable(new BitmapRenderer(picture) {
                public final /* synthetic */ Picture f$0;

                {
                    this.f$0 = r1;
                }

                public final void draw(Canvas canvas) {
                    canvas.drawPicture(this.f$0);
                }
            }), new BitmapRendererDrawable(new BitmapRenderer(Bitmap.createBitmap(picture2), f, new Paint(1)) {
                public final /* synthetic */ Bitmap f$0;
                public final /* synthetic */ float f$1;
                public final /* synthetic */ Paint f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void draw(Canvas canvas) {
                    FolderAdaptiveIcon.lambda$createFolderAdaptiveIcon$2(this.f$0, this.f$1, this.f$2, canvas);
                }
            }), new BitmapRendererDrawable(new BitmapRenderer(picture3) {
                public final /* synthetic */ Picture f$0;

                {
                    this.f$0 = r1;
                }

                public final void draw(Canvas canvas) {
                    canvas.drawPicture(this.f$0);
                }
            }), path);
        } catch (Exception e) {
            Log.e(TAG, "Unable to create folder icon", e);
            picture.endRecording();
            picture2.endRecording();
            picture3.endRecording();
            return null;
        } catch (Throwable th) {
            picture.endRecording();
            picture2.endRecording();
            picture3.endRecording();
            throw th;
        }
    }

    static /* synthetic */ void lambda$createFolderAdaptiveIcon$0(ActivityContext activityContext, int i, int i2, Canvas canvas, Canvas canvas2, Canvas canvas3) {
        FolderIcon findFolderIcon = activityContext.findFolderIcon(i);
        if (findFolderIcon != null) {
            initLayersOnUiThread(findFolderIcon, i2, canvas, canvas2, canvas3);
            return;
        }
        throw new IllegalArgumentException("Folder not found with id: " + i);
    }

    static /* synthetic */ void lambda$createFolderAdaptiveIcon$2(Bitmap bitmap, float f, Paint paint, Canvas canvas) {
        float f2 = -f;
        canvas.drawBitmap(bitmap, f2, f2, paint);
    }

    private static void initLayersOnUiThread(FolderIcon folderIcon, int i, Canvas canvas, Canvas canvas2, Canvas canvas3) {
        Rect rect = sTmpRect;
        folderIcon.getPreviewBounds(rect);
        int width = rect.width();
        PreviewBackground folderBackground = folderIcon.getFolderBackground();
        int i2 = (i - width) / 2;
        float f = (float) ((-rect.left) + i2);
        float f2 = (float) ((-rect.top) + i2);
        canvas3.save();
        canvas3.translate(f, f2);
        folderIcon.drawDot(canvas3);
        canvas3.restore();
        canvas2.save();
        canvas2.translate(f, f2);
        folderIcon.getPreviewItemManager().draw(canvas2);
        canvas2.restore();
        Paint paint = new Paint(1);
        paint.setColor(folderBackground.getBgColor());
        folderBackground.drawShadow(canvas);
        float f3 = ((float) i) / 2.0f;
        canvas.drawCircle(f3, f3, (float) folderBackground.getRadius(), paint);
        folderBackground.drawBackgroundStroke(canvas);
    }

    public Drawable.ConstantState getConstantState() {
        return this.mConstantState;
    }

    private static class MyConstantState extends Drawable.ConstantState {
        private final Drawable.ConstantState mBadge;
        private final Drawable.ConstantState mBg;
        private final Drawable.ConstantState mFg;
        private final Path mMask;

        MyConstantState(Drawable.ConstantState constantState, Drawable.ConstantState constantState2, Drawable.ConstantState constantState3, Path path) {
            this.mBg = constantState;
            this.mFg = constantState2;
            this.mBadge = constantState3;
            this.mMask = path;
        }

        public Drawable newDrawable() {
            return new FolderAdaptiveIcon(this.mBg.newDrawable(), this.mFg.newDrawable(), this.mBadge.newDrawable(), this.mMask);
        }

        public int getChangingConfigurations() {
            return this.mBg.getChangingConfigurations() & this.mFg.getChangingConfigurations() & this.mBadge.getChangingConfigurations();
        }
    }

    private static class BitmapRendererDrawable extends Drawable {
        private final BitmapRenderer mRenderer;

        public int getOpacity() {
            return -3;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        BitmapRendererDrawable(BitmapRenderer bitmapRenderer) {
            this.mRenderer = bitmapRenderer;
        }

        public void draw(Canvas canvas) {
            this.mRenderer.draw(canvas);
        }

        public Drawable.ConstantState getConstantState() {
            return new MyConstantState(this.mRenderer);
        }

        private static class MyConstantState extends Drawable.ConstantState {
            private final BitmapRenderer mRenderer;

            public int getChangingConfigurations() {
                return 0;
            }

            MyConstantState(BitmapRenderer bitmapRenderer) {
                this.mRenderer = bitmapRenderer;
            }

            public Drawable newDrawable() {
                return new BitmapRendererDrawable(this.mRenderer);
            }
        }
    }
}
