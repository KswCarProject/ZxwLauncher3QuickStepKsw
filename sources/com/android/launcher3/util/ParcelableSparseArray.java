package com.android.launcher3.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class ParcelableSparseArray extends SparseArray<Parcelable> implements Parcelable {
    public static final Parcelable.Creator<ParcelableSparseArray> CREATOR = new Parcelable.Creator<ParcelableSparseArray>() {
        public ParcelableSparseArray createFromParcel(Parcel parcel) {
            ParcelableSparseArray parcelableSparseArray = new ParcelableSparseArray();
            ClassLoader classLoader = parcelableSparseArray.getClass().getClassLoader();
            int readInt = parcel.readInt();
            for (int i = 0; i < readInt; i++) {
                parcelableSparseArray.put(parcel.readInt(), parcel.readParcelable(classLoader));
            }
            return parcelableSparseArray;
        }

        public ParcelableSparseArray[] newArray(int i) {
            return new ParcelableSparseArray[i];
        }
    };

    public int describeContents() {
        return 0;
    }

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int size = size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            parcel.writeInt(keyAt(i2));
            parcel.writeParcelable((Parcelable) valueAt(i2), 0);
        }
    }
}
