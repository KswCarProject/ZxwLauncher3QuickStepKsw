package com.android.launcher3.util;

import android.graphics.Rect;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class SplitConfigurationOptions {
    public static final float DEFAULT_SPLIT_RATIO = 0.5f;
    public static final int STAGE_POSITION_BOTTOM_OR_RIGHT = 1;
    public static final int STAGE_POSITION_TOP_OR_LEFT = 0;
    public static final int STAGE_POSITION_UNDEFINED = -1;
    public static final int STAGE_TYPE_MAIN = 0;
    public static final int STAGE_TYPE_SIDE = 1;
    public static final int STAGE_TYPE_UNDEFINED = -1;

    @Retention(RetentionPolicy.SOURCE)
    public @interface StagePosition {
    }

    public @interface StageType {
    }

    public static class StagedSplitTaskPosition {
        public int stagePosition = -1;
        public int stageType = -1;
        public int taskId = -1;
    }

    public static class SplitPositionOption {
        public final int iconResId;
        public final int mStageType;
        public final int stagePosition;
        public final int textResId;

        public SplitPositionOption(int i, int i2, int i3, int i4) {
            this.iconResId = i;
            this.textResId = i2;
            this.stagePosition = i3;
            this.mStageType = i4;
        }
    }

    public static class StagedSplitBounds {
        public final boolean appsStackedVertically;
        public final float dividerHeightPercent;
        public final float dividerWidthPercent;
        public final boolean initiatedFromSeascape;
        public final float leftTaskPercent;
        public final Rect leftTopBounds;
        public final int leftTopTaskId;
        public final Rect rightBottomBounds;
        public final int rightBottomTaskId;
        public final float topTaskPercent;
        public final Rect visualDividerBounds;

        public StagedSplitBounds(Rect rect, Rect rect2, int i, int i2) {
            this.leftTopBounds = rect;
            this.rightBottomBounds = rect2;
            this.leftTopTaskId = i;
            this.rightBottomTaskId = i2;
            if (rect2.top > rect.top) {
                this.visualDividerBounds = new Rect(rect.left, rect.bottom, rect.right, rect2.top);
                this.appsStackedVertically = true;
                this.initiatedFromSeascape = false;
            } else {
                this.visualDividerBounds = new Rect(rect.right, rect.top, rect2.left, rect.bottom);
                this.appsStackedVertically = false;
                if (rect2.width() > rect.width()) {
                    this.initiatedFromSeascape = true;
                } else {
                    this.initiatedFromSeascape = false;
                }
            }
            float f = (float) (rect2.right - rect.left);
            float f2 = (float) (rect2.bottom - rect.top);
            this.leftTaskPercent = ((float) rect.width()) / f;
            this.topTaskPercent = ((float) rect.height()) / f2;
            this.dividerWidthPercent = ((float) this.visualDividerBounds.width()) / f;
            this.dividerHeightPercent = ((float) this.visualDividerBounds.height()) / f2;
        }
    }
}
