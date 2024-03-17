package com.android.systemui.shared.rotation;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.systemui.navigationbar.buttons.KeyButtonRipple;

public class FloatingRotationButtonView extends ImageView {
    private static final float BACKGROUND_ALPHA = 0.92f;
    private final Configuration mLastConfiguration;
    private final Paint mOvalBgPaint;
    private KeyButtonRipple mRipple;

    public FloatingRotationButtonView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingRotationButtonView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOvalBgPaint = new Paint(3);
        this.mLastConfiguration = getResources().getConfiguration();
        setClickable(true);
        setWillNotDraw(false);
        forceHasOverlappingRendering(false);
    }

    public void setRipple(int i) {
        KeyButtonRipple keyButtonRipple = new KeyButtonRipple(getContext(), this, i);
        this.mRipple = keyButtonRipple;
        setBackground(keyButtonRipple);
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        if (i != 0) {
            jumpDrawablesToCurrentState();
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        KeyButtonRipple keyButtonRipple;
        int updateFrom = this.mLastConfiguration.updateFrom(configuration);
        if (((updateFrom & 1024) != 0 || (updateFrom & 4096) != 0) && (keyButtonRipple = this.mRipple) != null) {
            keyButtonRipple.updateResources();
        }
    }

    public void setColors(int i, int i2) {
        getDrawable().setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
        this.mOvalBgPaint.setColor(Color.valueOf((float) Color.red(i2), (float) Color.green(i2), (float) Color.blue(i2), 0.92f).toArgb());
        this.mRipple.setType(KeyButtonRipple.Type.OVAL);
    }

    public void setDarkIntensity(float f) {
        this.mRipple.setDarkIntensity(f);
    }

    public void draw(Canvas canvas) {
        float min = (float) Math.min(getWidth(), getHeight());
        canvas.drawOval(0.0f, 0.0f, min, min, this.mOvalBgPaint);
        super.draw(canvas);
    }
}
