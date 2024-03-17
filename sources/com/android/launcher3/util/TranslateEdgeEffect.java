package com.android.launcher3.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RenderNode;

public class TranslateEdgeEffect extends EdgeEffectCompat {
    private final RenderNode mNode = new RenderNode("TranslateEdgeEffect");

    public boolean draw(Canvas canvas) {
        return false;
    }

    public TranslateEdgeEffect(Context context) {
        super(context);
    }

    public boolean getTranslationShift(float[] fArr) {
        boolean draw = super.draw(this.mNode.beginRecording(1, 1));
        this.mNode.endRecording();
        fArr[0] = getDistance();
        return draw;
    }
}
