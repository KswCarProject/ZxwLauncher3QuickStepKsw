package com.android.launcher3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.icons.GraphicsUtils;

public class DoubleShadowBubbleTextView extends BubbleTextView {
    private final ShadowInfo mShadowInfo;

    public DoubleShadowBubbleTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public DoubleShadowBubbleTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DoubleShadowBubbleTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        ShadowInfo shadowInfo = new ShadowInfo(context, attributeSet, i);
        this.mShadowInfo = shadowInfo;
        setShadowLayer(shadowInfo.ambientShadowBlur, 0.0f, 0.0f, shadowInfo.ambientShadowColor);
    }

    public void onDraw(Canvas canvas) {
        if (this.mShadowInfo.skipDoubleShadow(this)) {
            super.onDraw(canvas);
            return;
        }
        int alpha = Color.alpha(getCurrentTextColor());
        getPaint().setShadowLayer(this.mShadowInfo.ambientShadowBlur, 0.0f, 0.0f, getTextShadowColor(this.mShadowInfo.ambientShadowColor, alpha));
        drawWithoutDot(canvas);
        canvas.save();
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(), getScrollX() + getWidth(), getScrollY() + getHeight());
        getPaint().setShadowLayer(this.mShadowInfo.keyShadowBlur, this.mShadowInfo.keyShadowOffsetX, this.mShadowInfo.keyShadowOffsetY, getTextShadowColor(this.mShadowInfo.keyShadowColor, alpha));
        drawWithoutDot(canvas);
        canvas.restore();
        drawDotIfNecessary(canvas);
    }

    public static class ShadowInfo {
        public final float ambientShadowBlur;
        public final int ambientShadowColor;
        public final float keyShadowBlur;
        public final int keyShadowColor;
        public final float keyShadowOffsetX;
        public final float keyShadowOffsetY;

        public ShadowInfo(Context context, AttributeSet attributeSet, int i) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ShadowInfo, i, 0);
            this.ambientShadowBlur = (float) obtainStyledAttributes.getDimensionPixelSize(0, 0);
            this.ambientShadowColor = obtainStyledAttributes.getColor(1, 0);
            this.keyShadowBlur = (float) obtainStyledAttributes.getDimensionPixelSize(2, 0);
            this.keyShadowOffsetX = (float) obtainStyledAttributes.getDimensionPixelSize(4, 0);
            this.keyShadowOffsetY = (float) obtainStyledAttributes.getDimensionPixelSize(5, 0);
            this.keyShadowColor = obtainStyledAttributes.getColor(3, 0);
            obtainStyledAttributes.recycle();
        }

        public boolean skipDoubleShadow(TextView textView) {
            int alpha = Color.alpha(textView.getCurrentTextColor());
            int alpha2 = Color.alpha(this.keyShadowColor);
            int alpha3 = Color.alpha(this.ambientShadowColor);
            if (alpha == 0 || (alpha2 == 0 && alpha3 == 0)) {
                textView.getPaint().clearShadowLayer();
                return true;
            } else if (alpha3 > 0 && alpha2 == 0) {
                textView.getPaint().setShadowLayer(this.ambientShadowBlur, 0.0f, 0.0f, DoubleShadowBubbleTextView.getTextShadowColor(this.ambientShadowColor, alpha));
                return true;
            } else if (alpha2 <= 0 || alpha3 != 0) {
                return false;
            } else {
                textView.getPaint().setShadowLayer(this.keyShadowBlur, this.keyShadowOffsetX, this.keyShadowOffsetY, DoubleShadowBubbleTextView.getTextShadowColor(this.keyShadowColor, alpha));
                return true;
            }
        }
    }

    /* access modifiers changed from: private */
    public static int getTextShadowColor(int i, int i2) {
        return GraphicsUtils.setColorAlphaBound(i, Math.round(((float) (Color.alpha(i) * i2)) / 255.0f));
    }
}
