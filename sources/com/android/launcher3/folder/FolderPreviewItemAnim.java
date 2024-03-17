package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatArrayEvaluator;
import android.animation.ObjectAnimator;
import android.util.Property;
import java.util.Arrays;

class FolderPreviewItemAnim {
    private static final Property<FolderPreviewItemAnim, float[]> PARAMS = new Property<FolderPreviewItemAnim, float[]>(float[].class, "params") {
        public float[] get(FolderPreviewItemAnim folderPreviewItemAnim) {
            FolderPreviewItemAnim.sTempParamsArray[0] = folderPreviewItemAnim.mParams.scale;
            FolderPreviewItemAnim.sTempParamsArray[1] = folderPreviewItemAnim.mParams.transX;
            FolderPreviewItemAnim.sTempParamsArray[2] = folderPreviewItemAnim.mParams.transY;
            return FolderPreviewItemAnim.sTempParamsArray;
        }

        public void set(FolderPreviewItemAnim folderPreviewItemAnim, float[] fArr) {
            folderPreviewItemAnim.setParams(fArr);
        }
    };
    /* access modifiers changed from: private */
    public static final float[] sTempParamsArray = new float[3];
    private static final PreviewItemDrawingParams sTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f);
    public final float[] finalState;
    private final ObjectAnimator mAnimator;
    private final PreviewItemManager mItemManager;
    /* access modifiers changed from: private */
    public final PreviewItemDrawingParams mParams;

    FolderPreviewItemAnim(PreviewItemManager previewItemManager, final PreviewItemDrawingParams previewItemDrawingParams, int i, int i2, int i3, int i4, int i5, final Runnable runnable) {
        this.mItemManager = previewItemManager;
        this.mParams = previewItemDrawingParams;
        previewItemDrawingParams.index = (float) i3;
        PreviewItemDrawingParams previewItemDrawingParams2 = sTmpParams;
        previewItemManager.computePreviewItemDrawingParams(i3, i4, previewItemDrawingParams2);
        float[] fArr = {previewItemDrawingParams2.scale, previewItemDrawingParams2.transX, previewItemDrawingParams2.transY};
        this.finalState = fArr;
        previewItemManager.computePreviewItemDrawingParams(i, i2, previewItemDrawingParams2);
        float[] fArr2 = {previewItemDrawingParams2.scale, previewItemDrawingParams2.transX, previewItemDrawingParams2.transY};
        ObjectAnimator ofObject = ObjectAnimator.ofObject(this, PARAMS, new FloatArrayEvaluator(), new float[][]{fArr2, fArr});
        this.mAnimator = ofObject;
        ofObject.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Runnable runnable = runnable;
                if (runnable != null) {
                    runnable.run();
                }
                previewItemDrawingParams.anim = null;
            }
        });
        ofObject.setDuration((long) i5);
    }

    /* access modifiers changed from: private */
    public void setParams(float[] fArr) {
        this.mParams.scale = fArr[0];
        this.mParams.transX = fArr[1];
        this.mParams.transY = fArr[2];
        this.mItemManager.onParamsChanged();
    }

    public void start() {
        this.mAnimator.start();
    }

    public void cancel() {
        this.mAnimator.cancel();
    }

    public boolean hasEqualFinalState(FolderPreviewItemAnim folderPreviewItemAnim) {
        return Arrays.equals(this.finalState, folderPreviewItemAnim.finalState);
    }
}
