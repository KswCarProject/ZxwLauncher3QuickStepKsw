package com.android.launcher3.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.EdgeEffect;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.Utilities;

public class SpringRelativeLayout extends RelativeLayout {
    private final EdgeEffect mEdgeGlowBottom;
    /* access modifiers changed from: private */
    public final EdgeEffect mEdgeGlowTop;

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public SpringRelativeLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public SpringRelativeLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SpringRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mEdgeGlowTop = Utilities.ATLEAST_S ? new EdgeEffect(context, attributeSet) : new EdgeEffect(context);
        this.mEdgeGlowBottom = Utilities.ATLEAST_S ? new EdgeEffect(context, attributeSet) : new EdgeEffect(context);
        setWillNotDraw(false);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (!this.mEdgeGlowTop.isFinished()) {
            int save = canvas.save();
            canvas.translate(0.0f, 0.0f);
            this.mEdgeGlowTop.setSize(getWidth(), getHeight());
            if (this.mEdgeGlowTop.draw(canvas)) {
                postInvalidateOnAnimation();
            }
            canvas.restoreToCount(save);
        }
        if (!this.mEdgeGlowBottom.isFinished()) {
            int save2 = canvas.save();
            int width = getWidth();
            int height = getHeight();
            canvas.translate((float) (-width), (float) height);
            canvas.rotate(180.0f, (float) width, 0.0f);
            this.mEdgeGlowBottom.setSize(width, height);
            if (this.mEdgeGlowBottom.draw(canvas)) {
                postInvalidateOnAnimation();
            }
            canvas.restoreToCount(save2);
        }
    }

    /* access modifiers changed from: protected */
    public void absorbSwipeUpVelocity(int i) {
        this.mEdgeGlowBottom.onAbsorb(i);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void absorbPullDeltaDistance(float f, float f2) {
        this.mEdgeGlowBottom.onPull(f, f2);
        invalidate();
    }

    public void onRelease() {
        this.mEdgeGlowBottom.onRelease();
    }

    public RecyclerView.EdgeEffectFactory createEdgeEffectFactory() {
        return new ProxyEdgeEffectFactory();
    }

    private class ProxyEdgeEffectFactory extends RecyclerView.EdgeEffectFactory {
        private ProxyEdgeEffectFactory() {
        }

        /* access modifiers changed from: protected */
        public EdgeEffect createEdgeEffect(RecyclerView recyclerView, int i) {
            if (i != 1) {
                return super.createEdgeEffect(recyclerView, i);
            }
            SpringRelativeLayout springRelativeLayout = SpringRelativeLayout.this;
            return new EdgeEffectProxy(springRelativeLayout.getContext(), SpringRelativeLayout.this.mEdgeGlowTop);
        }
    }

    private class EdgeEffectProxy extends EdgeEffect {
        private final EdgeEffect mParent;

        public boolean draw(Canvas canvas) {
            return false;
        }

        EdgeEffectProxy(Context context, EdgeEffect edgeEffect) {
            super(context);
            this.mParent = edgeEffect;
        }

        private void invalidateParentScrollEffect() {
            if (!this.mParent.isFinished()) {
                SpringRelativeLayout.this.invalidate();
            }
        }

        public void onAbsorb(int i) {
            this.mParent.onAbsorb(i);
            invalidateParentScrollEffect();
        }

        public void onPull(float f) {
            this.mParent.onPull(f);
            invalidateParentScrollEffect();
        }

        public void onPull(float f, float f2) {
            this.mParent.onPull(f, f2);
            invalidateParentScrollEffect();
        }

        public void onRelease() {
            this.mParent.onRelease();
            invalidateParentScrollEffect();
        }

        public void finish() {
            this.mParent.finish();
        }

        public boolean isFinished() {
            return this.mParent.isFinished();
        }
    }
}
