package androidx.slice;

import androidx.versionedparcelable.VersionedParcel;

public final class SliceParcelizer {
    public static Slice read(VersionedParcel versionedParcel) {
        Slice slice = new Slice();
        slice.mSpec = (SliceSpec) versionedParcel.readVersionedParcelable(slice.mSpec, 1);
        slice.mItems = (SliceItem[]) versionedParcel.readArray(slice.mItems, 2);
        slice.mHints = (String[]) versionedParcel.readArray(slice.mHints, 3);
        slice.mUri = versionedParcel.readString(slice.mUri, 4);
        return slice;
    }

    public static void write(Slice slice, VersionedParcel versionedParcel) {
        versionedParcel.setSerializationFlags(true, false);
        versionedParcel.writeVersionedParcelable(slice.mSpec, 1);
        versionedParcel.writeArray(slice.mItems, 2);
        versionedParcel.writeArray(slice.mHints, 3);
        versionedParcel.writeString(slice.mUri, 4);
    }
}
