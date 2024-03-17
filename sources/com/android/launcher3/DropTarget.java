package com.android.launcher3;

import android.content.Context;
import android.graphics.Rect;
import com.android.launcher3.DropTarget;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.folder.FolderNameProvider;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.Executors;

public interface DropTarget {
    boolean acceptDrop(DragObject dragObject);

    void getHitRectRelativeToDragLayer(Rect rect);

    boolean isDropEnabled();

    void onDragEnter(DragObject dragObject);

    void onDragExit(DragObject dragObject);

    void onDragOver(DragObject dragObject);

    void onDrop(DragObject dragObject, DragOptions dragOptions);

    void prepareAccessibilityDrop();

    public static class DragObject {
        public boolean cancelled = false;
        public boolean deferDragViewCleanupPostAnimation = true;
        public boolean dragComplete = false;
        public ItemInfo dragInfo = null;
        public DragSource dragSource = null;
        public DragView dragView = null;
        public FolderNameProvider folderNameProvider;
        public final InstanceId logInstanceId = new InstanceIdSequence().newInstanceId();
        public ItemInfo originalDragInfo = null;
        public DraggableView originalView = null;
        public DragViewStateAnnouncer stateAnnouncer;
        public int x = -1;
        public int xOffset = -1;
        public int y = -1;
        public int yOffset = -1;

        public DragObject(Context context) {
            if (FeatureFlags.FOLDER_NAME_SUGGEST.get()) {
                Executors.MODEL_EXECUTOR.post(new Runnable(context) {
                    public final /* synthetic */ Context f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        DropTarget.DragObject.this.lambda$new$0$DropTarget$DragObject(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$new$0$DropTarget$DragObject(Context context) {
            this.folderNameProvider = FolderNameProvider.newInstance(context);
        }

        public final float[] getVisualCenter(float[] fArr) {
            if (fArr == null) {
                fArr = new float[2];
            }
            Rect dragRegion = this.dragView.getDragRegion();
            fArr[0] = (float) (((this.x - this.xOffset) - dragRegion.left) + (dragRegion.width() / 2));
            fArr[1] = (float) (((this.y - this.yOffset) - dragRegion.top) + (dragRegion.height() / 2));
            return fArr;
        }
    }
}
