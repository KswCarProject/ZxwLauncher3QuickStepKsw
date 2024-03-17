package com.android.launcher3.icons;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.ColorUtils;

public class PlaceHolderIconDrawable extends FastBitmapDrawable {
    private final Path mProgressPath = GraphicsUtils.getShapePath(100);

    public PlaceHolderIconDrawable(BitmapInfo bitmapInfo, Context context) {
        super(bitmapInfo);
        this.mPaint.setColor(ColorUtils.compositeColors(GraphicsUtils.getAttrColor(context, R.attr.loadingIconColor), bitmapInfo.color));
    }

    /* access modifiers changed from: protected */
    public void drawInternal(Canvas canvas, Rect rect) {
        int save = canvas.save();
        canvas.translate((float) rect.left, (float) rect.top);
        canvas.scale(((float) rect.width()) / 100.0f, ((float) rect.height()) / 100.0f);
        canvas.drawPath(this.mProgressPath, this.mPaint);
        canvas.restoreToCount(save);
    }

    public void animateIconUpdate(final Drawable drawable) {
        int color = this.mPaint.getColor();
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{Color.alpha(color), 0});
        ofInt.setDuration(375);
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(color, drawable) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ Drawable f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$1.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(this.f$0, ((Integer) valueAnimator.getAnimatedValue()).intValue()), PorterDuff.Mode.SRC_ATOP));
            }
        });
        ofInt.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                drawable.setColorFilter((ColorFilter) null);
            }
        });
        ofInt.start();
    }
}
