package androidx.slice.core;

import android.net.Uri;
import android.text.TextUtils;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.compat.SliceProviderCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SliceQuery {

    private interface Filter<T> {
        boolean filter(T t);
    }

    public static boolean hasAnyHints(SliceItem sliceItem, String... strArr) {
        if (strArr == null) {
            return false;
        }
        List<String> hints = sliceItem.getHints();
        for (String contains : strArr) {
            if (hints.contains(contains)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasHints(SliceItem sliceItem, String... strArr) {
        if (strArr == null) {
            return true;
        }
        List<String> hints = sliceItem.getHints();
        for (String str : strArr) {
            if (!TextUtils.isEmpty(str) && !hints.contains(str)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasHints(Slice slice, String... strArr) {
        if (strArr == null) {
            return true;
        }
        List<String> hints = slice.getHints();
        for (String str : strArr) {
            if (!TextUtils.isEmpty(str) && !hints.contains(str)) {
                return false;
            }
        }
        return true;
    }

    public static SliceItem findNotContaining(SliceItem sliceItem, List<SliceItem> list) {
        SliceItem sliceItem2 = null;
        while (sliceItem2 == null && list.size() != 0) {
            SliceItem remove = list.remove(0);
            if (!contains(sliceItem, remove)) {
                sliceItem2 = remove;
            }
        }
        return sliceItem2;
    }

    private static boolean contains(SliceItem sliceItem, final SliceItem sliceItem2) {
        return (sliceItem == null || sliceItem2 == null || findFirst(filter(stream(sliceItem), new Filter<SliceItem>() {
            public boolean filter(SliceItem sliceItem) {
                return sliceItem == sliceItem2;
            }
        }), (Object) null) == null) ? false : true;
    }

    public static List<SliceItem> findAll(SliceItem sliceItem, String str) {
        return findAll(sliceItem, str, (String[]) null, (String[]) null);
    }

    public static List<SliceItem> findAll(Slice slice, String str, String str2, String str3) {
        return findAll(slice, str, new String[]{str2}, new String[]{str3});
    }

    public static List<SliceItem> findAll(SliceItem sliceItem, String str, String str2, String str3) {
        return findAll(sliceItem, str, new String[]{str2}, new String[]{str3});
    }

    public static List<SliceItem> findAll(Slice slice, final String str, final String[] strArr, final String[] strArr2) {
        return collect(filter(stream(slice), new Filter<SliceItem>() {
            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.hasHints(sliceItem, strArr) && !SliceQuery.hasAnyHints(sliceItem, strArr2);
            }
        }));
    }

    public static List<SliceItem> findAll(SliceItem sliceItem, final String str, final String[] strArr, final String[] strArr2) {
        return collect(filter(stream(sliceItem), new Filter<SliceItem>() {
            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.hasHints(sliceItem, strArr) && !SliceQuery.hasAnyHints(sliceItem, strArr2);
            }
        }));
    }

    public static SliceItem find(Slice slice, String str, String str2, String str3) {
        return find(slice, str, new String[]{str2}, new String[]{str3});
    }

    public static SliceItem find(Slice slice, String str) {
        return find(slice, str, (String[]) null, (String[]) null);
    }

    public static SliceItem find(SliceItem sliceItem, String str) {
        return find(sliceItem, str, (String[]) null, (String[]) null);
    }

    public static SliceItem find(SliceItem sliceItem, String str, String str2, String str3) {
        return find(sliceItem, str, new String[]{str2}, new String[]{str3});
    }

    public static SliceItem find(Slice slice, final String str, final String[] strArr, final String[] strArr2) {
        return (SliceItem) findFirst(filter(stream(slice), new Filter<SliceItem>() {
            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.hasHints(sliceItem, strArr) && !SliceQuery.hasAnyHints(sliceItem, strArr2);
            }
        }), (Object) null);
    }

    public static SliceItem findSubtype(Slice slice, final String str, final String str2) {
        return (SliceItem) findFirst(filter(stream(slice), new Filter<SliceItem>() {
            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.checkSubtype(sliceItem, str2);
            }
        }), (Object) null);
    }

    public static SliceItem findSubtype(SliceItem sliceItem, final String str, final String str2) {
        return (SliceItem) findFirst(filter(stream(sliceItem), new Filter<SliceItem>() {
            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.checkSubtype(sliceItem, str2);
            }
        }), (Object) null);
    }

    public static SliceItem find(SliceItem sliceItem, final String str, final String[] strArr, final String[] strArr2) {
        return (SliceItem) findFirst(filter(stream(sliceItem), new Filter<SliceItem>() {
            public boolean filter(SliceItem sliceItem) {
                return SliceQuery.checkFormat(sliceItem, str) && SliceQuery.hasHints(sliceItem, strArr) && !SliceQuery.hasAnyHints(sliceItem, strArr2);
            }
        }), (Object) null);
    }

    static boolean checkFormat(SliceItem sliceItem, String str) {
        return str == null || str.equals(sliceItem.getFormat());
    }

    static boolean checkSubtype(SliceItem sliceItem, String str) {
        return str == null || str.equals(sliceItem.getSubType());
    }

    public static Iterator<SliceItem> stream(SliceItem sliceItem) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(sliceItem);
        return getSliceItemStream(arrayList);
    }

    public static Iterator<SliceItem> stream(Slice slice) {
        ArrayList arrayList = new ArrayList();
        if (slice != null) {
            arrayList.addAll(slice.getItems());
        }
        return getSliceItemStream(arrayList);
    }

    private static Iterator<SliceItem> getSliceItemStream(final ArrayList<SliceItem> arrayList) {
        return new Iterator<SliceItem>() {
            public boolean hasNext() {
                return arrayList.size() != 0;
            }

            public SliceItem next() {
                SliceItem sliceItem = (SliceItem) arrayList.remove(0);
                if (SliceProviderCompat.EXTRA_SLICE.equals(sliceItem.getFormat()) || "action".equals(sliceItem.getFormat())) {
                    arrayList.addAll(sliceItem.getSlice().getItems());
                }
                return sliceItem;
            }
        };
    }

    public static SliceItem findTopLevelItem(Slice slice, String str, String str2, String[] strArr, String[] strArr2) {
        List<SliceItem> items = slice.getItems();
        for (int i = 0; i < items.size(); i++) {
            SliceItem sliceItem = items.get(i);
            if (checkFormat(sliceItem, str) && checkSubtype(sliceItem, str2) && hasHints(sliceItem, strArr) && !hasAnyHints(sliceItem, strArr2)) {
                return sliceItem;
            }
        }
        return null;
    }

    private static <T> List<T> collect(Iterator<T> it) {
        ArrayList arrayList = new ArrayList();
        while (it.hasNext()) {
            arrayList.add(it.next());
        }
        return arrayList;
    }

    private static <T> Iterator<T> filter(final Iterator<T> it, final Filter<T> filter) {
        return new Iterator<T>() {
            T mNext = findNext();

            private T findNext() {
                while (it.hasNext()) {
                    T next = it.next();
                    if (filter.filter(next)) {
                        return next;
                    }
                }
                return null;
            }

            public boolean hasNext() {
                return this.mNext != null;
            }

            public T next() {
                T t = this.mNext;
                this.mNext = findNext();
                return t;
            }
        };
    }

    private static <T> T findFirst(Iterator<T> it, T t) {
        while (it.hasNext()) {
            T next = it.next();
            if (next != null) {
                return next;
            }
        }
        return t;
    }

    public static SliceItem findItem(Slice slice, final Uri uri) {
        return (SliceItem) findFirst(filter(stream(slice), new Filter<SliceItem>() {
            public boolean filter(SliceItem sliceItem) {
                if ("action".equals(sliceItem.getFormat()) || SliceProviderCompat.EXTRA_SLICE.equals(sliceItem.getFormat())) {
                    return uri.equals(sliceItem.getSlice().getUri());
                }
                return false;
            }
        }), (Object) null);
    }

    private SliceQuery() {
    }
}
