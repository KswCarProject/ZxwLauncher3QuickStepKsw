package androidx.slice;

import androidx.versionedparcelable.VersionedParcel;

public final class SliceItemHolderParcelizer {
    public static SliceItemHolder read(VersionedParcel versionedParcel) {
        SliceItemHolder sliceItemHolder = new SliceItemHolder();
        sliceItemHolder.mVersionedParcelable = versionedParcel.readVersionedParcelable(sliceItemHolder.mVersionedParcelable, 1);
        sliceItemHolder.mParcelable = versionedParcel.readParcelable(sliceItemHolder.mParcelable, 2);
        sliceItemHolder.mStr = versionedParcel.readString(sliceItemHolder.mStr, 3);
        sliceItemHolder.mInt = versionedParcel.readInt(sliceItemHolder.mInt, 4);
        sliceItemHolder.mLong = versionedParcel.readLong(sliceItemHolder.mLong, 5);
        return sliceItemHolder;
    }

    public static void write(SliceItemHolder sliceItemHolder, VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, true);
        versionedParcel.writeVersionedParcelable(sliceItemHolder.mVersionedParcelable, 1);
        versionedParcel.writeParcelable(sliceItemHolder.mParcelable, 2);
        versionedParcel.writeString(sliceItemHolder.mStr, 3);
        versionedParcel.writeInt(sliceItemHolder.mInt, 4);
        versionedParcel.writeLong(sliceItemHolder.mLong, 5);
    }
}
