package com.android.launcher3.graphics;

import android.graphics.Canvas;
import android.util.FloatProperty;
import android.view.View;
import com.android.launcher3.R;
import com.android.launcher3.icons.GraphicsUtils;

public class Scrim {
    public static final FloatProperty<Scrim> SCRIM_PROGRESS = new FloatProperty<Scrim>("scrimProgress") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(Scrim scrim) {
            return Float.valueOf(scrim.mScrimProgress);
        }

        public void setValue(Scrim scrim, float f) {
            scrim.setScrimProgress(f);
        }
    };
    protected final View mRoot;
    protected int mScrimAlpha = 0;
    protected int mScrimColor;
    protected float mScrimProgress;

    public Scrim(View view) {
        this.mRoot = view;
        this.mScrimColor = view.getContext().getColor(R.color.wallpaper_popup_scrim);
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(GraphicsUtils.setColorAlphaBound(this.mScrimColor, this.mScrimAlpha));
    }

    /* access modifiers changed from: private */
    public void setScrimProgress(float f) {
        if (this.mScrimProgress != f) {
            this.mScrimProgress = f;
            this.mScrimAlpha = Math.round(f * 255.0f);
            this.mRoot.invalidate();
        }
    }
}
