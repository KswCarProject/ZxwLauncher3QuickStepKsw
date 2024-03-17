package com.android.wm.shell.util;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

public class StagedSplitBounds implements Parcelable {
    public static final Parcelable.Creator<StagedSplitBounds> CREATOR = new Parcelable.Creator<StagedSplitBounds>() {
        public StagedSplitBounds createFromParcel(Parcel parcel) {
            return new StagedSplitBounds(parcel);
        }

        public StagedSplitBounds[] newArray(int i) {
            return new StagedSplitBounds[i];
        }
    };
    public final boolean appsStackedVertically;
    public final float leftTaskPercent;
    public final Rect leftTopBounds;
    public final int leftTopTaskId;
    public final Rect rightBottomBounds;
    public final int rightBottomTaskId;
    public final float topTaskPercent;
    public final Rect visualDividerBounds;

    public int describeContents() {
        return 0;
    }

    public StagedSplitBounds(Rect rect, Rect rect2, int i, int i2) {
        this.leftTopBounds = rect;
        this.rightBottomBounds = rect2;
        this.leftTopTaskId = i;
        this.rightBottomTaskId = i2;
        if (rect2.top > rect.top) {
            this.visualDividerBounds = new Rect(rect.left, rect.bottom, rect.right, rect2.top);
            this.appsStackedVertically = true;
        } else {
            this.visualDividerBounds = new Rect(rect.right, rect.top, rect2.left, rect.bottom);
            this.appsStackedVertically = false;
        }
        this.leftTaskPercent = ((float) rect.width()) / ((float) rect2.right);
        this.topTaskPercent = ((float) rect.height()) / ((float) rect2.bottom);
    }

    public StagedSplitBounds(Parcel parcel) {
        this.leftTopBounds = (Rect) parcel.readTypedObject(Rect.CREATOR);
        this.rightBottomBounds = (Rect) parcel.readTypedObject(Rect.CREATOR);
        this.visualDividerBounds = (Rect) parcel.readTypedObject(Rect.CREATOR);
        this.topTaskPercent = parcel.readFloat();
        this.leftTaskPercent = parcel.readFloat();
        this.appsStackedVertically = parcel.readBoolean();
        this.leftTopTaskId = parcel.readInt();
        this.rightBottomTaskId = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedObject(this.leftTopBounds, i);
        parcel.writeTypedObject(this.rightBottomBounds, i);
        parcel.writeTypedObject(this.visualDividerBounds, i);
        parcel.writeFloat(this.topTaskPercent);
        parcel.writeFloat(this.leftTaskPercent);
        parcel.writeBoolean(this.appsStackedVertically);
        parcel.writeInt(this.leftTopTaskId);
        parcel.writeInt(this.rightBottomTaskId);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof StagedSplitBounds)) {
            return false;
        }
        StagedSplitBounds stagedSplitBounds = (StagedSplitBounds) obj;
        if (!Objects.equals(this.leftTopBounds, stagedSplitBounds.leftTopBounds) || !Objects.equals(this.rightBottomBounds, stagedSplitBounds.rightBottomBounds) || this.leftTopTaskId != stagedSplitBounds.leftTopTaskId || this.rightBottomTaskId != stagedSplitBounds.rightBottomTaskId) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.leftTopBounds, this.rightBottomBounds, Integer.valueOf(this.leftTopTaskId), Integer.valueOf(this.rightBottomTaskId)});
    }

    public String toString() {
        return "LeftTop: " + this.leftTopBounds + ", taskId: " + this.leftTopTaskId + "\nRightBottom: " + this.rightBottomBounds + ", taskId: " + this.rightBottomTaskId + "\nDivider: " + this.visualDividerBounds + "\nAppsVertical? " + this.appsStackedVertically;
    }
}
