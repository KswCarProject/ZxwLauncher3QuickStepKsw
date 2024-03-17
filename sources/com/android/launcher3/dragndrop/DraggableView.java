package com.android.launcher3.dragndrop;

import android.graphics.Rect;
import com.android.launcher3.util.SafeCloseable;

public interface DraggableView {
    public static final int DRAGGABLE_ICON = 0;
    public static final int DRAGGABLE_WIDGET = 1;

    static /* synthetic */ int lambda$ofType$0(int i) {
        return i;
    }

    static /* synthetic */ void lambda$prepareDrawDragView$1() {
    }

    int getViewType();

    void getWorkspaceVisualDragBounds(Rect rect) {
    }

    static DraggableView ofType(int i) {
        return new DraggableView(i) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final int getViewType() {
                return DraggableView.lambda$ofType$0(this.f$0);
            }
        };
    }

    SafeCloseable prepareDrawDragView() {
        return $$Lambda$DraggableView$8mJA0I1mdkGHoxuWm03sg_h_UY.INSTANCE;
    }

    void getSourceVisualDragBounds(Rect rect) {
        getWorkspaceVisualDragBounds(rect);
    }
}
