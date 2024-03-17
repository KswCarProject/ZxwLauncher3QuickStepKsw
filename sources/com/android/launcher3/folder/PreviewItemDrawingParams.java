package com.android.launcher3.folder;

import android.graphics.drawable.Drawable;
import com.android.launcher3.model.data.WorkspaceItemInfo;

class PreviewItemDrawingParams {
    public FolderPreviewItemAnim anim;
    public Drawable drawable;
    public boolean hidden;
    float index;
    public WorkspaceItemInfo item;
    float scale;
    float transX;
    float transY;

    PreviewItemDrawingParams(float f, float f2, float f3) {
        this.transX = f;
        this.transY = f2;
        this.scale = f3;
    }

    public void update(float f, float f2, float f3) {
        FolderPreviewItemAnim folderPreviewItemAnim = this.anim;
        if (folderPreviewItemAnim != null) {
            if (folderPreviewItemAnim.finalState[1] != f && this.anim.finalState[2] != f2 && this.anim.finalState[0] != f3) {
                this.anim.cancel();
            } else {
                return;
            }
        }
        this.transX = f;
        this.transY = f2;
        this.scale = f3;
    }
}
