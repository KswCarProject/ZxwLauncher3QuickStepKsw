package com.android.launcher3.testing;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkspaceCellCenterRequest implements TestInformationRequest {
    public static final Parcelable.Creator<WorkspaceCellCenterRequest> CREATOR = new Parcelable.Creator<WorkspaceCellCenterRequest>() {
        public WorkspaceCellCenterRequest createFromParcel(Parcel parcel) {
            return new WorkspaceCellCenterRequest(parcel);
        }

        public WorkspaceCellCenterRequest[] newArray(int i) {
            return new WorkspaceCellCenterRequest[i];
        }
    };
    public final int cellX;
    public final int cellY;
    public final int spanX;
    public final int spanY;

    public int describeContents() {
        return 0;
    }

    public String getRequestName() {
        return TestProtocol.REQUEST_WORKSPACE_CELL_CENTER;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.cellX);
        parcel.writeInt(this.cellY);
        parcel.writeInt(this.spanX);
        parcel.writeInt(this.spanY);
    }

    private WorkspaceCellCenterRequest(int i, int i2, int i3, int i4) {
        this.cellX = i;
        this.cellY = i2;
        this.spanX = i3;
        this.spanY = i4;
    }

    private WorkspaceCellCenterRequest(Parcel parcel) {
        this(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int mCellX;
        private int mCellY;
        private int mSpanX;
        private int mSpanY;

        private Builder() {
            this.mCellX = 0;
            this.mCellY = 0;
            this.mSpanX = 1;
            this.mSpanY = 1;
        }

        public Builder setCellX(int i) {
            this.mCellX = i;
            return this;
        }

        public Builder setCellY(int i) {
            this.mCellY = i;
            return this;
        }

        public Builder setSpanX(int i) {
            this.mSpanX = i;
            return this;
        }

        public Builder setSpanY(int i) {
            this.mCellY = i;
            return this;
        }

        public WorkspaceCellCenterRequest build() {
            return new WorkspaceCellCenterRequest(this.mCellX, this.mCellY, this.mSpanX, this.mSpanY);
        }
    }
}
