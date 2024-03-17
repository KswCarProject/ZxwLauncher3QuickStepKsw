package com.android.launcher3.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class IntSet implements Iterable<Integer> {
    final IntArray mArray = new IntArray();

    public void add(int i) {
        int binarySearch = Arrays.binarySearch(this.mArray.mValues, 0, this.mArray.mSize, i);
        if (binarySearch < 0) {
            this.mArray.add((-binarySearch) - 1, i);
        }
    }

    public IntSet addAll(IntSet intSet) {
        intSet.forEach(new Consumer() {
            public final void accept(Object obj) {
                IntSet.this.add(((Integer) obj).intValue());
            }
        });
        return this;
    }

    public void remove(int i) {
        int binarySearch = Arrays.binarySearch(this.mArray.mValues, 0, this.mArray.mSize, i);
        if (binarySearch >= 0) {
            this.mArray.removeIndex(binarySearch);
        }
    }

    public boolean contains(int i) {
        return Arrays.binarySearch(this.mArray.mValues, 0, this.mArray.mSize, i) >= 0;
    }

    public boolean isEmpty() {
        return this.mArray.isEmpty();
    }

    public int size() {
        return this.mArray.size();
    }

    public void clear() {
        this.mArray.clear();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return (obj instanceof IntSet) && ((IntSet) obj).mArray.equals(this.mArray);
    }

    public IntArray getArray() {
        return this.mArray;
    }

    public void copyFrom(IntSet intSet) {
        this.mArray.copyFrom(intSet.mArray);
    }

    public static IntSet wrap(IntArray intArray) {
        IntSet intSet = new IntSet();
        intSet.mArray.addAll(intArray);
        Arrays.sort(intSet.mArray.mValues, 0, intSet.mArray.mSize);
        return intSet;
    }

    public static IntSet wrap(int... iArr) {
        return wrap(IntArray.wrap(iArr));
    }

    public static IntSet wrap(Iterable<Integer> iterable) {
        IntSet intSet = new IntSet();
        Objects.requireNonNull(intSet);
        iterable.forEach(new Consumer() {
            public final void accept(Object obj) {
                IntSet.this.add(((Integer) obj).intValue());
            }
        });
        return intSet;
    }

    public Iterator<Integer> iterator() {
        return this.mArray.iterator();
    }

    public String toString() {
        return "IntSet{" + this.mArray.toConcatString() + '}';
    }
}
