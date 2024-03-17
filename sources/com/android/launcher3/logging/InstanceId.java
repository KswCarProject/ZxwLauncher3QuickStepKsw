package com.android.launcher3.logging;

import android.os.Parcel;
import android.os.Parcelable;

public final class InstanceId implements Parcelable {
    public static final Parcelable.Creator<InstanceId> CREATOR = new Parcelable.Creator<InstanceId>() {
        public InstanceId createFromParcel(Parcel parcel) {
            return new InstanceId(parcel);
        }

        public InstanceId[] newArray(int i) {
            return new InstanceId[i];
        }
    };
    public static final int INSTANCE_ID_MAX = 1048576;
    private final int mId;

    public int describeContents() {
        return 0;
    }

    public InstanceId(int i) {
        this.mId = Math.min(Math.max(0, i), 1048576);
    }

    private InstanceId(Parcel parcel) {
        this(parcel.readInt());
    }

    public int getId() {
        return this.mId;
    }

    public String toString() {
        return this.mId + "";
    }

    public static InstanceId fakeInstanceId(int i) {
        return new InstanceId(i);
    }

    public int hashCode() {
        return this.mId;
    }

    public boolean equals(Object obj) {
        if ((obj instanceof InstanceId) && this.mId == ((InstanceId) obj).mId) {
            return true;
        }
        return false;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mId);
    }
}
