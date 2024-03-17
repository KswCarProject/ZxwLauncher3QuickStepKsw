package com.android.launcher3.folder;

public class ClippedFolderIconLayoutRule {
    public static final int ENTER_INDEX = -3;
    public static final int EXIT_INDEX = -2;
    public static final float ICON_OVERLAP_FACTOR = 1.125f;
    private static final float ITEM_RADIUS_SCALE_FACTOR = 1.15f;
    public static final int MAX_NUM_ITEMS_IN_PREVIEW = 4;
    private static final float MAX_RADIUS_DILATION = 0.25f;
    private static final float MAX_SCALE = 0.51f;
    private static final int MIN_NUM_ITEMS_IN_PREVIEW = 2;
    private static final float MIN_SCALE = 0.44f;
    private float mAvailableSpace;
    private float mBaselineIconScale;
    private float mIconSize;
    private boolean mIsRtl;
    private float mRadius;
    private float[] mTmpPoint = new float[2];

    public void init(int i, float f, boolean z) {
        float f2 = (float) i;
        this.mAvailableSpace = f2;
        this.mRadius = (ITEM_RADIUS_SCALE_FACTOR * f2) / 2.0f;
        this.mIconSize = f;
        this.mIsRtl = z;
        this.mBaselineIconScale = f2 / (f * 1.0f);
    }

    public PreviewItemDrawingParams computePreviewItemDrawingParams(int i, int i2, PreviewItemDrawingParams previewItemDrawingParams) {
        float scaleForItem = scaleForItem(i2);
        if (i == -2) {
            getGridPosition(0, 2, this.mTmpPoint);
        } else if (i == -3) {
            getGridPosition(1, 2, this.mTmpPoint);
        } else if (i >= 4) {
            float[] fArr = this.mTmpPoint;
            float f = (this.mAvailableSpace / 2.0f) - ((this.mIconSize * scaleForItem) / 2.0f);
            fArr[1] = f;
            fArr[0] = f;
        } else {
            getPosition(i, i2, this.mTmpPoint);
        }
        float[] fArr2 = this.mTmpPoint;
        float f2 = fArr2[0];
        float f3 = fArr2[1];
        if (previewItemDrawingParams == null) {
            return new PreviewItemDrawingParams(f2, f3, scaleForItem);
        }
        previewItemDrawingParams.update(f2, f3, scaleForItem);
        return previewItemDrawingParams;
    }

    private void getGridPosition(int i, int i2, float[] fArr) {
        getPosition(0, 4, fArr);
        float f = fArr[0];
        float f2 = fArr[1];
        getPosition(3, 4, fArr);
        fArr[0] = f + (((float) i2) * (fArr[0] - f));
        fArr[1] = f2 + (((float) i) * (fArr[1] - f2));
    }

    private void getPosition(int i, int i2, float[] fArr) {
        int i3 = i;
        int max = Math.max(i2, 2);
        boolean z = this.mIsRtl;
        double d = 0.0d;
        double d2 = z ? 0.0d : 3.141592653589793d;
        int i4 = z ? 1 : -1;
        if (max == 3) {
            d = 1.5707963267948966d;
        } else if (max == 4) {
            d = 0.7853981633974483d;
        }
        double d3 = (double) i4;
        double d4 = d2 + (d * d3);
        if (max == 4 && i3 == 3) {
            i3 = 2;
        } else if (max == 4 && i3 == 2) {
            i3 = 3;
        }
        float f = this.mRadius * (((((float) (max - 2)) * MAX_RADIUS_DILATION) / 2.0f) + 1.0f);
        double d5 = d4 + (((double) i3) * (6.283185307179586d / ((double) max)) * d3);
        float scaleForItem = (this.mIconSize * scaleForItem(max)) / 2.0f;
        fArr[0] = ((this.mAvailableSpace / 2.0f) + ((float) ((((double) f) * Math.cos(d5)) / 2.0d))) - scaleForItem;
        fArr[1] = ((this.mAvailableSpace / 2.0f) + ((float) ((((double) (-f)) * Math.sin(d5)) / 2.0d))) - scaleForItem;
    }

    public float scaleForItem(int i) {
        return (i <= 3 ? MAX_SCALE : MIN_SCALE) * this.mBaselineIconScale;
    }

    public float getIconSize() {
        return this.mIconSize;
    }
}
