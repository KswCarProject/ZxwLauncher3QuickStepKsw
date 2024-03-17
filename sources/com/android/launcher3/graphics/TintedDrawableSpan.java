package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.CharacterStyle;
import android.text.style.DynamicDrawableSpan;

public class TintedDrawableSpan extends DynamicDrawableSpan {
    private final Drawable mDrawable;
    private int mOldTint = 0;

    public /* bridge */ /* synthetic */ CharacterStyle getUnderlying() {
        return super.getUnderlying();
    }

    public TintedDrawableSpan(Context context, int i) {
        super(0);
        Drawable mutate = context.getDrawable(i).mutate();
        this.mDrawable = mutate;
        mutate.setTint(0);
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        if (fontMetricsInt == null) {
            fontMetricsInt = paint.getFontMetricsInt();
        }
        Paint.FontMetricsInt fontMetricsInt2 = fontMetricsInt;
        int i3 = fontMetricsInt2.bottom - fontMetricsInt2.top;
        this.mDrawable.setBounds(0, 0, i3, i3);
        return super.getSize(paint, charSequence, i, i2, fontMetricsInt2);
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        int color = paint.getColor();
        if (this.mOldTint != color) {
            this.mOldTint = color;
            this.mDrawable.setTint(color);
        }
        super.draw(canvas, charSequence, i, i2, f, i3, i4, i5, paint);
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }
}
