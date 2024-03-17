package com.android.launcher3.util;

import android.util.SparseArray;
import java.util.Iterator;

public class IntSparseArrayMap<E> extends SparseArray<E> implements Iterable<E> {
    public boolean containsKey(int i) {
        return indexOfKey(i) >= 0;
    }

    public boolean isEmpty() {
        return size() <= 0;
    }

    public IntSparseArrayMap<E> clone() {
        return (IntSparseArrayMap) super.clone();
    }

    public Iterator<E> iterator() {
        return new ValueIterator();
    }

    class ValueIterator implements Iterator<E> {
        private int mNextIndex = 0;

        ValueIterator() {
        }

        public boolean hasNext() {
            return this.mNextIndex < IntSparseArrayMap.this.size();
        }

        public E next() {
            IntSparseArrayMap intSparseArrayMap = IntSparseArrayMap.this;
            int i = this.mNextIndex;
            this.mNextIndex = i + 1;
            return intSparseArrayMap.valueAt(i);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
