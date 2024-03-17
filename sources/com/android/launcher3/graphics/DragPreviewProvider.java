package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.icons.BitmapRenderer;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.SafeCloseable;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import java.nio.ByteBuffer;
import kotlin.UByte;

public class DragPreviewProvider {
    public final int blurSizeOutline;
    public Bitmap generatedDragOutline;
    private OutlineGeneratorCallback mOutlineGeneratorCallback;
    private final Rect mTempRect;
    protected final View mView;
    public final int previewPadding;

    public DragPreviewProvider(View view) {
        this(view, view.getContext());
    }

    public DragPreviewProvider(View view, Context context) {
        this.mTempRect = new Rect();
        this.mView = view;
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        this.blurSizeOutline = dimensionPixelSize;
        this.previewPadding = dimensionPixelSize;
    }

    /* access modifiers changed from: protected */
    /* renamed from: drawDragView */
    public void lambda$createDrawable$0$DragPreviewProvider(Canvas canvas, float f) {
        int save = canvas.save();
        canvas.scale(f, f);
        View view = this.mView;
        if (view instanceof DraggableView) {
            DraggableView draggableView = (DraggableView) view;
            SafeCloseable prepareDrawDragView = draggableView.prepareDrawDragView();
            try {
                draggableView.getSourceVisualDragBounds(this.mTempRect);
                canvas.translate((float) ((this.blurSizeOutline / 2) - this.mTempRect.left), (float) ((this.blurSizeOutline / 2) - this.mTempRect.top));
                this.mView.draw(canvas);
                if (prepareDrawDragView != null) {
                    prepareDrawDragView.close();
                }
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        }
        canvas.restoreToCount(save);
        return;
        throw th;
    }

    public Drawable createDrawable() {
        int i;
        int i2;
        View view = this.mView;
        if (view instanceof LauncherAppWidgetHostView) {
            return null;
        }
        float scaleX = view.getScaleX();
        View view2 = this.mView;
        if (view2 instanceof DraggableView) {
            ((DraggableView) view2).getSourceVisualDragBounds(this.mTempRect);
            i2 = this.mTempRect.width();
            i = this.mTempRect.height();
        } else {
            i2 = view2.getWidth();
            i = this.mView.getHeight();
        }
        int i3 = this.blurSizeOutline;
        return new FastBitmapDrawable(BitmapRenderer.createHardwareBitmap(i2 + i3, i + i3, new BitmapRenderer(scaleX) {
            public final /* synthetic */ float f$1;

            {
                this.f$1 = r2;
            }

            public final void draw(Canvas canvas) {
                DragPreviewProvider.this.lambda$createDrawable$0$DragPreviewProvider(this.f$1, canvas);
            }
        }));
    }

    public View getContentView() {
        View view = this.mView;
        if (view instanceof LauncherAppWidgetHostView) {
            return view;
        }
        return null;
    }

    public final void generateDragOutline(Bitmap bitmap) {
        this.mOutlineGeneratorCallback = new OutlineGeneratorCallback(bitmap);
        Executors.UI_HELPER_EXECUTOR.post(this.mOutlineGeneratorCallback);
    }

    protected static Rect getDrawableBounds(Drawable drawable) {
        Rect rect = new Rect();
        drawable.copyBounds(rect);
        if (rect.width() == 0 || rect.height() == 0) {
            rect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else {
            rect.offsetTo(0, 0);
        }
        return rect;
    }

    public float getScaleAndPosition(Drawable drawable, int[] iArr) {
        float locationInDragLayer = ((ActivityContext) ActivityContext.lookupContext(this.mView.getContext())).getDragLayer().getLocationInDragLayer(this.mView, iArr);
        View view = this.mView;
        if (view instanceof LauncherAppWidgetHostView) {
            locationInDragLayer /= ((LauncherAppWidgetHostView) view).getScaleToFit();
        }
        iArr[0] = Math.round(((float) iArr[0]) - ((((float) drawable.getIntrinsicWidth()) - ((((float) this.mView.getWidth()) * locationInDragLayer) * this.mView.getScaleX())) / 2.0f));
        iArr[1] = Math.round((((float) iArr[1]) - (((1.0f - locationInDragLayer) * ((float) drawable.getIntrinsicHeight())) / 2.0f)) - ((float) (this.previewPadding / 2)));
        return locationInDragLayer;
    }

    public float getScaleAndPosition(View view, int[] iArr) {
        float locationInDragLayer = ((ActivityContext) ActivityContext.lookupContext(this.mView.getContext())).getDragLayer().getLocationInDragLayer(this.mView, iArr);
        View view2 = this.mView;
        if (view2 instanceof LauncherAppWidgetHostView) {
            locationInDragLayer /= ((LauncherAppWidgetHostView) view2).getScaleToFit();
        }
        iArr[0] = Math.round(((float) iArr[0]) - ((((float) view.getWidth()) - ((((float) this.mView.getWidth()) * locationInDragLayer) * this.mView.getScaleX())) / 2.0f));
        iArr[1] = Math.round((((float) iArr[1]) - (((1.0f - locationInDragLayer) * ((float) view.getHeight())) / 2.0f)) - ((float) (this.previewPadding / 2)));
        return locationInDragLayer;
    }

    /* access modifiers changed from: protected */
    public Bitmap convertPreviewToAlphaBitmap(Bitmap bitmap) {
        return bitmap.copy(Bitmap.Config.ALPHA_8, true);
    }

    private class OutlineGeneratorCallback implements Runnable {
        private final Context mContext;
        private final boolean mIsIcon;
        private final Bitmap mPreviewSnapshot;

        OutlineGeneratorCallback(Bitmap bitmap) {
            this.mPreviewSnapshot = bitmap;
            this.mContext = DragPreviewProvider.this.mView.getContext();
            this.mIsIcon = DragPreviewProvider.this.mView instanceof BubbleTextView;
        }

        public void run() {
            Bitmap convertPreviewToAlphaBitmap = DragPreviewProvider.this.convertPreviewToAlphaBitmap(this.mPreviewSnapshot);
            if (this.mIsIcon) {
                int i = ((ActivityContext) ActivityContext.lookupContext(this.mContext)).getDeviceProfile().iconSizePx;
                convertPreviewToAlphaBitmap = Bitmap.createScaledBitmap(convertPreviewToAlphaBitmap, i, i, false);
            }
            int width = convertPreviewToAlphaBitmap.getWidth() * convertPreviewToAlphaBitmap.getHeight();
            byte[] bArr = new byte[width];
            ByteBuffer wrap = ByteBuffer.wrap(bArr);
            wrap.rewind();
            convertPreviewToAlphaBitmap.copyPixelsToBuffer(wrap);
            for (int i2 = 0; i2 < width; i2++) {
                if ((bArr[i2] & UByte.MAX_VALUE) < 188) {
                    bArr[i2] = 0;
                }
            }
            wrap.rewind();
            convertPreviewToAlphaBitmap.copyPixelsFromBuffer(wrap);
            Paint paint = new Paint(3);
            Canvas canvas = new Canvas();
            paint.setMaskFilter(new BlurMaskFilter((float) DragPreviewProvider.this.blurSizeOutline, BlurMaskFilter.Blur.OUTER));
            int[] iArr = new int[2];
            Bitmap extractAlpha = convertPreviewToAlphaBitmap.extractAlpha(paint, iArr);
            paint.setMaskFilter(new BlurMaskFilter(this.mContext.getResources().getDimension(R.dimen.blur_size_thin_outline), BlurMaskFilter.Blur.OUTER));
            int[] iArr2 = new int[2];
            Bitmap extractAlpha2 = convertPreviewToAlphaBitmap.extractAlpha(paint, iArr2);
            canvas.setBitmap(convertPreviewToAlphaBitmap);
            canvas.drawColor(ViewCompat.MEASURED_STATE_MASK, PorterDuff.Mode.SRC_OUT);
            paint.setMaskFilter(new BlurMaskFilter((float) DragPreviewProvider.this.blurSizeOutline, BlurMaskFilter.Blur.NORMAL));
            int[] iArr3 = new int[2];
            Bitmap extractAlpha3 = convertPreviewToAlphaBitmap.extractAlpha(paint, iArr3);
            paint.setMaskFilter((MaskFilter) null);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.setBitmap(extractAlpha3);
            canvas.drawBitmap(convertPreviewToAlphaBitmap, (float) (-iArr3[0]), (float) (-iArr3[1]), paint);
            float f = (float) (-iArr3[0]);
            float height = (float) extractAlpha3.getHeight();
            Bitmap bitmap = extractAlpha3;
            Paint paint2 = paint;
            canvas.drawRect(0.0f, 0.0f, f, height, paint2);
            canvas.drawRect(0.0f, 0.0f, (float) bitmap.getWidth(), (float) (-iArr3[1]), paint2);
            paint.setXfermode((Xfermode) null);
            canvas.setBitmap(convertPreviewToAlphaBitmap);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            Bitmap bitmap2 = bitmap;
            canvas.drawBitmap(bitmap2, (float) iArr3[0], (float) iArr3[1], paint);
            canvas.drawBitmap(extractAlpha, (float) iArr[0], (float) iArr[1], paint);
            canvas.drawBitmap(extractAlpha2, (float) iArr2[0], (float) iArr2[1], paint);
            canvas.setBitmap((Bitmap) null);
            extractAlpha2.recycle();
            extractAlpha.recycle();
            bitmap2.recycle();
            DragPreviewProvider.this.generatedDragOutline = convertPreviewToAlphaBitmap;
        }
    }
}
