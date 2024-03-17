package kotlin;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import kotlin.collections.ArraysKt;
import kotlin.collections.UByteIterator;
import kotlin.jvm.JvmInline;
import kotlin.jvm.internal.CollectionToArray;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

@JvmInline
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u0000\n\u0002\b\f\n\u0002\u0010(\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0004\b@\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u00012B\u0014\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004ø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006B\u0014\b\u0001\u0012\u0006\u0010\u0007\u001a\u00020\bø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\tJ\u001b\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0002H\u0002ø\u0001\u0000¢\u0006\u0004\b\u0011\u0010\u0012J \u0010\u0013\u001a\u00020\u000f2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001H\u0016ø\u0001\u0000¢\u0006\u0004\b\u0015\u0010\u0016J\u001a\u0010\u0017\u001a\u00020\u000f2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019HÖ\u0003¢\u0006\u0004\b\u001a\u0010\u001bJ\u001e\u0010\u001c\u001a\u00020\u00022\u0006\u0010\u001d\u001a\u00020\u0004H\u0002ø\u0001\u0000ø\u0001\u0001¢\u0006\u0004\b\u001e\u0010\u001fJ\u0010\u0010 \u001a\u00020\u0004HÖ\u0001¢\u0006\u0004\b!\u0010\u000bJ\u000f\u0010\"\u001a\u00020\u000fH\u0016¢\u0006\u0004\b#\u0010$J\u0019\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00020&H\u0002ø\u0001\u0000¢\u0006\u0004\b'\u0010(J#\u0010)\u001a\u00020*2\u0006\u0010\u001d\u001a\u00020\u00042\u0006\u0010+\u001a\u00020\u0002H\u0002ø\u0001\u0000¢\u0006\u0004\b,\u0010-J\u0010\u0010.\u001a\u00020/HÖ\u0001¢\u0006\u0004\b0\u00101R\u0014\u0010\u0003\u001a\u00020\u00048VX\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0007\u001a\u00020\b8\u0000X\u0004¢\u0006\b\n\u0000\u0012\u0004\b\f\u0010\r\u0001\u0007\u0001\u00020\bø\u0001\u0000\u0002\b\n\u0002\b\u0019\n\u0002\b!¨\u00063"}, d2 = {"Lkotlin/UByteArray;", "", "Lkotlin/UByte;", "size", "", "constructor-impl", "(I)[B", "storage", "", "([B)[B", "getSize-impl", "([B)I", "getStorage$annotations", "()V", "contains", "", "element", "contains-7apg3OU", "([BB)Z", "containsAll", "elements", "containsAll-impl", "([BLjava/util/Collection;)Z", "equals", "other", "", "equals-impl", "([BLjava/lang/Object;)Z", "get", "index", "get-w2LRezQ", "([BI)B", "hashCode", "hashCode-impl", "isEmpty", "isEmpty-impl", "([B)Z", "iterator", "", "iterator-impl", "([B)Ljava/util/Iterator;", "set", "", "value", "set-VurrAj0", "([BIB)V", "toString", "", "toString-impl", "([B)Ljava/lang/String;", "Iterator", "kotlin-stdlib"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: UByteArray.kt */
public final class UByteArray implements Collection<UByte>, KMappedMarker {
    private final byte[] storage;

    /* renamed from: box-impl  reason: not valid java name */
    public static final /* synthetic */ UByteArray m200boximpl(byte[] bArr) {
        return new UByteArray(bArr);
    }

    /* renamed from: constructor-impl  reason: not valid java name */
    public static byte[] m202constructorimpl(byte[] bArr) {
        Intrinsics.checkNotNullParameter(bArr, "storage");
        return bArr;
    }

    /* renamed from: equals-impl  reason: not valid java name */
    public static boolean m205equalsimpl(byte[] bArr, Object obj) {
        return (obj instanceof UByteArray) && Intrinsics.areEqual((Object) bArr, (Object) ((UByteArray) obj).m216unboximpl());
    }

    /* renamed from: equals-impl0  reason: not valid java name */
    public static final boolean m206equalsimpl0(byte[] bArr, byte[] bArr2) {
        return Intrinsics.areEqual((Object) bArr, (Object) bArr2);
    }

    public static /* synthetic */ void getStorage$annotations() {
    }

    /* renamed from: hashCode-impl  reason: not valid java name */
    public static int m209hashCodeimpl(byte[] bArr) {
        return Arrays.hashCode(bArr);
    }

    /* renamed from: toString-impl  reason: not valid java name */
    public static String m213toStringimpl(byte[] bArr) {
        return "UByteArray(storage=" + Arrays.toString(bArr) + ')';
    }

    public /* bridge */ /* synthetic */ boolean add(Object obj) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    /* renamed from: add-7apg3OU  reason: not valid java name */
    public boolean m214add7apg3OU(byte b) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public boolean addAll(Collection<? extends UByte> collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public void clear() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public boolean equals(Object obj) {
        return m205equalsimpl(this.storage, obj);
    }

    public int hashCode() {
        return m209hashCodeimpl(this.storage);
    }

    public boolean remove(Object obj) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public boolean removeAll(Collection<? extends Object> collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public boolean retainAll(Collection<? extends Object> collection) {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public Object[] toArray() {
        return CollectionToArray.toArray(this);
    }

    public <T> T[] toArray(T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "array");
        return CollectionToArray.toArray(this, tArr);
    }

    public String toString() {
        return m213toStringimpl(this.storage);
    }

    /* renamed from: unbox-impl  reason: not valid java name */
    public final /* synthetic */ byte[] m216unboximpl() {
        return this.storage;
    }

    public final /* bridge */ boolean contains(Object obj) {
        if (!(obj instanceof UByte)) {
            return false;
        }
        return m215contains7apg3OU(((UByte) obj).m199unboximpl());
    }

    private /* synthetic */ UByteArray(byte[] bArr) {
        this.storage = bArr;
    }

    /* renamed from: constructor-impl  reason: not valid java name */
    public static byte[] m201constructorimpl(int i) {
        return m202constructorimpl(new byte[i]);
    }

    /* renamed from: get-w2LRezQ  reason: not valid java name */
    public static final byte m207getw2LRezQ(byte[] bArr, int i) {
        return UByte.m150constructorimpl(bArr[i]);
    }

    /* renamed from: set-VurrAj0  reason: not valid java name */
    public static final void m212setVurrAj0(byte[] bArr, int i, byte b) {
        bArr[i] = b;
    }

    /* renamed from: getSize-impl  reason: not valid java name */
    public static int m208getSizeimpl(byte[] bArr) {
        return bArr.length;
    }

    /* renamed from: getSize */
    public int size() {
        return m208getSizeimpl(this.storage);
    }

    /* renamed from: iterator-impl  reason: not valid java name */
    public static java.util.Iterator<UByte> m211iteratorimpl(byte[] bArr) {
        return new Iterator(bArr);
    }

    public java.util.Iterator<UByte> iterator() {
        return m211iteratorimpl(this.storage);
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\bH\u0002J\u0015\u0010\t\u001a\u00020\nH\u0016ø\u0001\u0000ø\u0001\u0001¢\u0006\u0004\b\u000b\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000\u0002\b\n\u0002\b\u0019\n\u0002\b!¨\u0006\r"}, d2 = {"Lkotlin/UByteArray$Iterator;", "Lkotlin/collections/UByteIterator;", "array", "", "([B)V", "index", "", "hasNext", "", "nextUByte", "Lkotlin/UByte;", "nextUByte-w2LRezQ", "()B", "kotlin-stdlib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UByteArray.kt */
    private static final class Iterator extends UByteIterator {
        private final byte[] array;
        private int index;

        public Iterator(byte[] bArr) {
            Intrinsics.checkNotNullParameter(bArr, "array");
            this.array = bArr;
        }

        public boolean hasNext() {
            return this.index < this.array.length;
        }

        /* renamed from: nextUByte-w2LRezQ  reason: not valid java name */
        public byte m217nextUBytew2LRezQ() {
            int i = this.index;
            byte[] bArr = this.array;
            if (i < bArr.length) {
                this.index = i + 1;
                return UByte.m150constructorimpl(bArr[i]);
            }
            throw new NoSuchElementException(String.valueOf(this.index));
        }
    }

    /* renamed from: contains-7apg3OU  reason: not valid java name */
    public boolean m215contains7apg3OU(byte b) {
        return m203contains7apg3OU(this.storage, b);
    }

    /* renamed from: contains-7apg3OU  reason: not valid java name */
    public static boolean m203contains7apg3OU(byte[] bArr, byte b) {
        return ArraysKt.contains(bArr, b);
    }

    public boolean containsAll(Collection<? extends Object> collection) {
        Intrinsics.checkNotNullParameter(collection, "elements");
        return m204containsAllimpl(this.storage, collection);
    }

    /* renamed from: containsAll-impl  reason: not valid java name */
    public static boolean m204containsAllimpl(byte[] bArr, Collection<UByte> collection) {
        boolean z;
        Intrinsics.checkNotNullParameter(collection, "elements");
        Iterable iterable = collection;
        if (!((Collection) iterable).isEmpty()) {
            for (Object next : iterable) {
                if (!(next instanceof UByte) || !ArraysKt.contains(bArr, ((UByte) next).m199unboximpl())) {
                    z = false;
                    continue;
                } else {
                    z = true;
                    continue;
                }
                if (!z) {
                    return false;
                }
            }
        }
        return true;
    }

    /* renamed from: isEmpty-impl  reason: not valid java name */
    public static boolean m210isEmptyimpl(byte[] bArr) {
        return bArr.length == 0;
    }

    public boolean isEmpty() {
        return m210isEmptyimpl(this.storage);
    }
}
