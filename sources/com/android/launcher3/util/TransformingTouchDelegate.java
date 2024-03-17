package com.android.launcher3.util;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.TouchDelegate;
import android.view.View;

public class TransformingTouchDelegate extends TouchDelegate {
    private static final Rect sTempRect = new Rect();
    private final RectF mBounds = new RectF();
    private boolean mDelegateTargeted;
    private View mDelegateView;
    private final RectF mTouchCheckBounds = new RectF();
    private float mTouchExtension;
    private boolean mWasTouchOutsideBounds;

    public TransformingTouchDelegate(View view) {
        super(sTempRect, view);
        this.mDelegateView = view;
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        this.mBounds.set((float) i, (float) i2, (float) i3, (float) i4);
        updateTouchBounds();
    }

    public void extendTouchBounds(float f) {
        this.mTouchExtension = f;
        updateTouchBounds();
    }

    private void updateTouchBounds() {
        this.mTouchCheckBounds.set(this.mBounds);
        RectF rectF = this.mTouchCheckBounds;
        float f = this.mTouchExtension;
        rectF.inset(-f, -f);
    }

    public void setDelegateView(View view) {
        this.mDelegateView = view;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
        if (r0 != 3) goto L_0x003d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0040  */
    /* JADX WARNING: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getAction()
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x0019
            if (r0 == r1) goto L_0x0014
            r1 = 2
            if (r0 == r1) goto L_0x0011
            r1 = 3
            if (r0 == r1) goto L_0x0014
            goto L_0x003d
        L_0x0011:
            boolean r1 = r5.mDelegateTargeted
            goto L_0x003e
        L_0x0014:
            boolean r1 = r5.mDelegateTargeted
            r5.mDelegateTargeted = r2
            goto L_0x003e
        L_0x0019:
            android.graphics.RectF r0 = r5.mTouchCheckBounds
            float r3 = r6.getX()
            float r4 = r6.getY()
            boolean r0 = r0.contains(r3, r4)
            r5.mDelegateTargeted = r0
            if (r0 == 0) goto L_0x003d
            android.graphics.RectF r0 = r5.mBounds
            float r3 = r6.getX()
            float r4 = r6.getY()
            boolean r0 = r0.contains(r3, r4)
            r0 = r0 ^ r1
            r5.mWasTouchOutsideBounds = r0
            goto L_0x003e
        L_0x003d:
            r1 = r2
        L_0x003e:
            if (r1 == 0) goto L_0x0072
            float r0 = r6.getX()
            float r1 = r6.getY()
            boolean r2 = r5.mWasTouchOutsideBounds
            if (r2 == 0) goto L_0x005c
            android.graphics.RectF r2 = r5.mBounds
            float r2 = r2.centerX()
            android.graphics.RectF r3 = r5.mBounds
            float r3 = r3.centerY()
            r6.setLocation(r2, r3)
            goto L_0x0069
        L_0x005c:
            android.graphics.RectF r2 = r5.mBounds
            float r2 = r2.left
            float r2 = -r2
            android.graphics.RectF r3 = r5.mBounds
            float r3 = r3.top
            float r3 = -r3
            r6.offsetLocation(r2, r3)
        L_0x0069:
            android.view.View r2 = r5.mDelegateView
            boolean r2 = r2.dispatchTouchEvent(r6)
            r6.setLocation(r0, r1)
        L_0x0072:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.TransformingTouchDelegate.onTouchEvent(android.view.MotionEvent):boolean");
    }
}
