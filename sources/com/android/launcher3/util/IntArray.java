package com.android.launcher3.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

public class IntArray implements Cloneable, Iterable<Integer> {
    private static final int[] EMPTY_INT = new int[0];
    private static final int MIN_CAPACITY_INCREMENT = 12;
    int mSize;
    int[] mValues;

    private IntArray(int[] iArr, int i) {
        this.mValues = iArr;
        this.mSize = i;
    }

    public IntArray() {
        this(10);
    }

    public IntArray(int i) {
        if (i == 0) {
            this.mValues = EMPTY_INT;
        } else {
            this.mValues = new int[i];
        }
        this.mSize = 0;
    }

    public static IntArray wrap(int... iArr) {
        return new IntArray(iArr, iArr.length);
    }

    public void add(int i) {
        add(this.mSize, i);
    }

    public void add(int i, int i2) {
        ensureCapacity(1);
        int i3 = this.mSize;
        int i4 = i3 - i;
        int i5 = i3 + 1;
        this.mSize = i5;
        checkBounds(i5, i);
        if (i4 != 0) {
            int[] iArr = this.mValues;
            System.arraycopy(iArr, i, iArr, i + 1, i4);
        }
        this.mValues[i] = i2;
    }

    public void addAll(IntArray intArray) {
        int i = intArray.mSize;
        ensureCapacity(i);
        System.arraycopy(intArray.mValues, 0, this.mValues, this.mSize, i);
        this.mSize += i;
    }

    public void copyFrom(IntArray intArray) {
        clear();
        addAll(intArray);
    }

    private void ensureCapacity(int i) {
        int i2 = this.mSize;
        int i3 = i + i2;
        int[] iArr = this.mValues;
        if (i3 >= iArr.length) {
            int i4 = (i2 < 6 ? 12 : i2 >> 1) + i2;
            if (i4 > i3) {
                i3 = i4;
            }
            int[] iArr2 = new int[i3];
            System.arraycopy(iArr, 0, iArr2, 0, i2);
            this.mValues = iArr2;
        }
    }

    public void clear() {
        this.mSize = 0;
    }

    public IntArray clone() {
        return wrap(toArray());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IntArray) {
            IntArray intArray = (IntArray) obj;
            if (this.mSize == intArray.mSize) {
                for (int i = 0; i < this.mSize; i++) {
                    if (intArray.mValues[i] != this.mValues[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int get(int i) {
        checkBounds(this.mSize, i);
        return this.mValues[i];
    }

    public void set(int i, int i2) {
        checkBounds(this.mSize, i);
        this.mValues[i] = i2;
    }

    public int indexOf(int i) {
        int i2 = this.mSize;
        for (int i3 = 0; i3 < i2; i3++) {
            if (this.mValues[i3] == i) {
                return i3;
            }
        }
        return -1;
    }

    public boolean contains(int i) {
        return indexOf(i) >= 0;
    }

    public boolean isEmpty() {
        return this.mSize == 0;
    }

    public void removeIndex(int i) {
        checkBounds(this.mSize, i);
        int[] iArr = this.mValues;
        System.arraycopy(iArr, i + 1, iArr, i, (this.mSize - i) - 1);
        this.mSize--;
    }

    public void removeValue(int i) {
        int indexOf = indexOf(i);
        if (indexOf >= 0) {
            removeIndex(indexOf);
        }
    }

    public void removeAllValues(IntArray intArray) {
        for (int i = 0; i < intArray.mSize; i++) {
            removeValue(intArray.mValues[i]);
        }
    }

    public int size() {
        return this.mSize;
    }

    public int[] toArray() {
        int i = this.mSize;
        return i == 0 ? EMPTY_INT : Arrays.copyOf(this.mValues, i);
    }

    public String toConcatString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.mValues[i]);
        }
        return sb.toString();
    }

    public static IntArray fromConcatString(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        int countTokens = stringTokenizer.countTokens();
        int[] iArr = new int[countTokens];
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            iArr[i] = Integer.parseInt(stringTokenizer.nextToken().trim());
            i++;
        }
        return new IntArray(iArr, countTokens);
    }

    private static void checkBounds(int i, int i2) {
        if (i2 < 0 || i <= i2) {
            throw new ArrayIndexOutOfBoundsException("length=" + i + "; index=" + i2);
        }
    }

    public Iterator<Integer> iterator() {
        return new ValueIterator();
    }

    class ValueIterator implements Iterator<Integer> {
        private int mNextIndex = 0;

        ValueIterator() {
        }

        public boolean hasNext() {
            return this.mNextIndex < IntArray.this.size();
        }

        public Integer next() {
            IntArray intArray = IntArray.this;
            int i = this.mNextIndex;
            this.mNextIndex = i + 1;
            return Integer.valueOf(intArray.get(i));
        }

        public void remove() {
            IntArray intArray = IntArray.this;
            int i = this.mNextIndex - 1;
            this.mNextIndex = i;
            intArray.removeIndex(i);
        }
    }
}
