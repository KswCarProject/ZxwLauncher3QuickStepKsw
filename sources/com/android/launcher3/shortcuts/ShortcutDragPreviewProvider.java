package com.android.launcher3.shortcuts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.icons.BitmapRenderer;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.views.ActivityContext;

public class ShortcutDragPreviewProvider extends DragPreviewProvider {
    private final Point mPositionShift;

    public ShortcutDragPreviewProvider(View view, Point point) {
        super(view);
        this.mPositionShift = point;
    }

    public Drawable createDrawable() {
        if (!FeatureFlags.ENABLE_DEEP_SHORTCUT_ICON_CACHE.get()) {
            return new FastBitmapDrawable(createDragBitmapLegacy());
        }
        int i = ((ActivityContext) ActivityContext.lookupContext(this.mView.getContext())).getDeviceProfile().iconSizePx;
        return new FastBitmapDrawable(BitmapRenderer.createHardwareBitmap(this.blurSizeOutline + i, this.blurSizeOutline + i, new BitmapRenderer(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void draw(Canvas canvas) {
                ShortcutDragPreviewProvider.this.lambda$createDrawable$0$ShortcutDragPreviewProvider(this.f$1, canvas);
            }
        }));
    }

    public /* synthetic */ void lambda$createDrawable$0$ShortcutDragPreviewProvider(int i, Canvas canvas) {
        drawDragViewOnBackground(canvas, (float) i);
    }

    private Bitmap createDragBitmapLegacy() {
        Drawable background = this.mView.getBackground();
        Rect drawableBounds = getDrawableBounds(background);
        int i = ((ActivityContext) ActivityContext.lookupContext(this.mView.getContext())).getDeviceProfile().iconSizePx;
        Bitmap createBitmap = Bitmap.createBitmap(this.blurSizeOutline + i, this.blurSizeOutline + i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.translate((float) (this.blurSizeOutline / 2), (float) (this.blurSizeOutline / 2));
        float f = (float) i;
        canvas.scale(f / ((float) drawableBounds.width()), f / ((float) drawableBounds.height()), 0.0f, 0.0f);
        canvas.translate((float) drawableBounds.left, (float) drawableBounds.top);
        background.draw(canvas);
        return createBitmap;
    }

    private void drawDragViewOnBackground(Canvas canvas, float f) {
        Drawable background = this.mView.getBackground();
        Rect drawableBounds = getDrawableBounds(background);
        canvas.translate((float) (this.blurSizeOutline / 2), (float) (this.blurSizeOutline / 2));
        canvas.scale(f / ((float) drawableBounds.width()), f / ((float) drawableBounds.height()), 0.0f, 0.0f);
        canvas.translate((float) drawableBounds.left, (float) drawableBounds.top);
        background.draw(canvas);
    }

    public float getScaleAndPosition(Drawable drawable, int[] iArr) {
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(this.mView.getContext());
        int width = getDrawableBounds(this.mView.getBackground()).width();
        float locationInDragLayer = activityContext.getDragLayer().getLocationInDragLayer(this.mView, iArr);
        int paddingStart = this.mView.getPaddingStart();
        if (Utilities.isRtl(this.mView.getResources())) {
            paddingStart = (this.mView.getWidth() - width) - paddingStart;
        }
        float f = ((float) width) * locationInDragLayer;
        iArr[0] = iArr[0] + Math.round((((float) paddingStart) * locationInDragLayer) + ((f - ((float) drawable.getIntrinsicWidth())) / 2.0f) + ((float) this.mPositionShift.x));
        iArr[1] = iArr[1] + Math.round((((locationInDragLayer * ((float) this.mView.getHeight())) - ((float) drawable.getIntrinsicHeight())) / 2.0f) + ((float) this.mPositionShift.y));
        return f / ((float) activityContext.getDeviceProfile().iconSizePx);
    }
}
