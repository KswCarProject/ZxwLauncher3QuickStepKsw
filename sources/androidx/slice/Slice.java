package androidx.slice;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.slice.SliceManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Preconditions;
import androidx.slice.SliceItem;
import androidx.slice.compat.SliceProviderCompat;
import androidx.versionedparcelable.VersionedParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class Slice implements VersionedParcelable {
    private static final String HINTS = "hints";
    private static final String ITEMS = "items";
    private static final String SPEC_REVISION = "revision";
    private static final String SPEC_TYPE = "type";
    private static final String URI = "uri";
    String[] mHints = new String[0];
    SliceItem[] mItems = new SliceItem[0];
    SliceSpec mSpec;
    String mUri;

    public @interface SliceHint {
    }

    Slice(ArrayList<SliceItem> arrayList, String[] strArr, Uri uri, SliceSpec sliceSpec) {
        this.mHints = strArr;
        this.mItems = (SliceItem[]) arrayList.toArray(new SliceItem[arrayList.size()]);
        this.mUri = uri.toString();
        this.mSpec = sliceSpec;
    }

    public Slice() {
    }

    public Slice(Bundle bundle) {
        int i = 0;
        this.mHints = bundle.getStringArray(HINTS);
        Parcelable[] parcelableArray = bundle.getParcelableArray(ITEMS);
        this.mItems = new SliceItem[parcelableArray.length];
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i >= sliceItemArr.length) {
                break;
            }
            if (parcelableArray[i] instanceof Bundle) {
                sliceItemArr[i] = new SliceItem((Bundle) parcelableArray[i]);
            }
            i++;
        }
        this.mUri = bundle.getParcelable(URI).toString();
        this.mSpec = bundle.containsKey(SPEC_TYPE) ? new SliceSpec(bundle.getString(SPEC_TYPE), bundle.getInt(SPEC_REVISION)) : null;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putStringArray(HINTS, this.mHints);
        Parcelable[] parcelableArr = new Parcelable[this.mItems.length];
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i >= sliceItemArr.length) {
                break;
            }
            parcelableArr[i] = sliceItemArr[i].toBundle();
            i++;
        }
        bundle.putParcelableArray(ITEMS, parcelableArr);
        bundle.putParcelable(URI, Uri.parse(this.mUri));
        SliceSpec sliceSpec = this.mSpec;
        if (sliceSpec != null) {
            bundle.putString(SPEC_TYPE, sliceSpec.getType());
            bundle.putInt(SPEC_REVISION, this.mSpec.getRevision());
        }
        return bundle;
    }

    public SliceSpec getSpec() {
        return this.mSpec;
    }

    public Uri getUri() {
        return Uri.parse(this.mUri);
    }

    public List<SliceItem> getItems() {
        return Arrays.asList(this.mItems);
    }

    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    public boolean hasHint(String str) {
        return ArrayUtils.contains(this.mHints, str);
    }

    public static class Builder {
        private int mChildId;
        private ArrayList<String> mHints = new ArrayList<>();
        private ArrayList<SliceItem> mItems = new ArrayList<>();
        private SliceSpec mSpec;
        private final Uri mUri;

        public Builder(Uri uri) {
            this.mUri = uri;
        }

        public Builder(Builder builder) {
            this.mUri = builder.getChildUri();
        }

        private Uri getChildUri() {
            Uri.Builder appendPath = this.mUri.buildUpon().appendPath("_gen");
            int i = this.mChildId;
            this.mChildId = i + 1;
            return appendPath.appendPath(String.valueOf(i)).build();
        }

        public Builder setSpec(SliceSpec sliceSpec) {
            this.mSpec = sliceSpec;
            return this;
        }

        public Builder addHints(String... strArr) {
            this.mHints.addAll(Arrays.asList(strArr));
            return this;
        }

        public Builder addHints(List<String> list) {
            return addHints((String[]) list.toArray(new String[list.size()]));
        }

        public Builder addSubSlice(Slice slice) {
            Preconditions.checkNotNull(slice);
            return addSubSlice(slice, (String) null);
        }

        public Builder addSubSlice(Slice slice, String str) {
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem((Object) slice, SliceProviderCompat.EXTRA_SLICE, str, (String[]) slice.getHints().toArray(new String[slice.getHints().size()])));
            return this;
        }

        public Builder addAction(PendingIntent pendingIntent, Slice slice, String str) {
            Preconditions.checkNotNull(pendingIntent);
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(pendingIntent, slice, "action", str, (String[]) slice.getHints().toArray(new String[slice.getHints().size()])));
            return this;
        }

        public Builder addAction(SliceItem.ActionHandler actionHandler, Slice slice, String str) {
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(actionHandler, slice, "action", str, (String[]) slice.getHints().toArray(new String[slice.getHints().size()])));
            return this;
        }

        public Builder addText(CharSequence charSequence, String str, String... strArr) {
            this.mItems.add(new SliceItem((Object) charSequence, "text", str, strArr));
            return this;
        }

        public Builder addText(CharSequence charSequence, String str, List<String> list) {
            return addText(charSequence, str, (String[]) list.toArray(new String[list.size()]));
        }

        public Builder addIcon(IconCompat iconCompat, String str, String... strArr) {
            Preconditions.checkNotNull(iconCompat);
            if (Slice.isValidIcon(iconCompat)) {
                this.mItems.add(new SliceItem((Object) iconCompat, "image", str, strArr));
            }
            return this;
        }

        public Builder addIcon(IconCompat iconCompat, String str, List<String> list) {
            Preconditions.checkNotNull(iconCompat);
            return Slice.isValidIcon(iconCompat) ? addIcon(iconCompat, str, (String[]) list.toArray(new String[list.size()])) : this;
        }

        public Builder addRemoteInput(RemoteInput remoteInput, String str, List<String> list) {
            Preconditions.checkNotNull(remoteInput);
            return addRemoteInput(remoteInput, str, (String[]) list.toArray(new String[list.size()]));
        }

        public Builder addRemoteInput(RemoteInput remoteInput, String str, String... strArr) {
            Preconditions.checkNotNull(remoteInput);
            this.mItems.add(new SliceItem((Object) remoteInput, "input", str, strArr));
            return this;
        }

        public Builder addInt(int i, String str, String... strArr) {
            this.mItems.add(new SliceItem((Object) Integer.valueOf(i), "int", str, strArr));
            return this;
        }

        public Builder addInt(int i, String str, List<String> list) {
            return addInt(i, str, (String[]) list.toArray(new String[list.size()]));
        }

        public Builder addLong(long j, String str, String... strArr) {
            this.mItems.add(new SliceItem((Object) Long.valueOf(j), "long", str, strArr));
            return this;
        }

        public Builder addLong(long j, String str, List<String> list) {
            return addLong(j, str, (String[]) list.toArray(new String[list.size()]));
        }

        @Deprecated
        public Builder addTimestamp(long j, String str, String... strArr) {
            this.mItems.add(new SliceItem((Object) Long.valueOf(j), "long", str, strArr));
            return this;
        }

        public Builder addTimestamp(long j, String str, List<String> list) {
            return addTimestamp(j, str, (String[]) list.toArray(new String[list.size()]));
        }

        public Builder addItem(SliceItem sliceItem) {
            this.mItems.add(sliceItem);
            return this;
        }

        public Slice build() {
            ArrayList<SliceItem> arrayList = this.mItems;
            ArrayList<String> arrayList2 = this.mHints;
            return new Slice(arrayList, (String[]) arrayList2.toArray(new String[arrayList2.size()]), this.mUri, this.mSpec);
        }
    }

    public String toString() {
        return toString("");
    }

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Slice ");
        String[] strArr = this.mHints;
        if (strArr.length > 0) {
            appendHints(sb, strArr);
            sb.append(' ');
        }
        sb.append('[');
        sb.append(this.mUri);
        sb.append("] {\n");
        String str2 = str + "  ";
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i < sliceItemArr.length) {
                sb.append(sliceItemArr[i].toString(str2));
                i++;
            } else {
                sb.append(str);
                sb.append('}');
                return sb.toString();
            }
        }
    }

    public static void appendHints(StringBuilder sb, String[] strArr) {
        if (strArr != null && strArr.length != 0) {
            sb.append('(');
            int length = strArr.length - 1;
            for (int i = 0; i < length; i++) {
                sb.append(strArr[i]);
                sb.append(", ");
            }
            sb.append(strArr[length]);
            sb.append(")");
        }
    }

    public static Slice bindSlice(Context context, Uri uri, Set<SliceSpec> set) {
        if (Build.VERSION.SDK_INT >= 28) {
            return callBindSlice(context, uri, set);
        }
        return SliceProviderCompat.bindSlice(context, uri, set);
    }

    private static Slice callBindSlice(Context context, Uri uri, Set<SliceSpec> set) {
        return SliceConvert.wrap(((SliceManager) context.getSystemService(SliceManager.class)).bindSlice(uri, SliceConvert.unwrap(set)), context);
    }

    static boolean isValidIcon(IconCompat iconCompat) {
        if (iconCompat == null) {
            return false;
        }
        if (iconCompat.mType != 2 || iconCompat.getResId() != 0) {
            return true;
        }
        throw new IllegalArgumentException("Failed to add icon, invalid resource id: " + iconCompat.getResId());
    }
}
