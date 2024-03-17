package androidx.slice;

import android.app.slice.Slice;
import android.app.slice.SliceItem;
import android.app.slice.SliceSpec;
import android.content.Context;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.compat.SliceProviderCompat;
import java.util.Set;

public class SliceConvert {
    private static final String TAG = "SliceConvert";

    public static Slice unwrap(Slice slice) {
        if (slice == null || slice.getUri() == null) {
            return null;
        }
        Slice.Builder builder = new Slice.Builder(slice.getUri(), unwrap(slice.getSpec()));
        builder.addHints(slice.getHints());
        for (SliceItem next : slice.getItems()) {
            String format = next.getFormat();
            format.hashCode();
            char c = 65535;
            switch (format.hashCode()) {
                case -1422950858:
                    if (format.equals("action")) {
                        c = 0;
                        break;
                    }
                    break;
                case 104431:
                    if (format.equals("int")) {
                        c = 1;
                        break;
                    }
                    break;
                case 3327612:
                    if (format.equals("long")) {
                        c = 2;
                        break;
                    }
                    break;
                case 3556653:
                    if (format.equals("text")) {
                        c = 3;
                        break;
                    }
                    break;
                case 100313435:
                    if (format.equals("image")) {
                        c = 4;
                        break;
                    }
                    break;
                case 100358090:
                    if (format.equals("input")) {
                        c = 5;
                        break;
                    }
                    break;
                case 109526418:
                    if (format.equals(SliceProviderCompat.EXTRA_SLICE)) {
                        c = 6;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.addAction(next.getAction(), unwrap(next.getSlice()), next.getSubType());
                    break;
                case 1:
                    builder.addInt(next.getInt(), next.getSubType(), next.getHints());
                    break;
                case 2:
                    builder.addLong(next.getLong(), next.getSubType(), next.getHints());
                    break;
                case 3:
                    builder.addText(next.getText(), next.getSubType(), next.getHints());
                    break;
                case 4:
                    builder.addIcon(next.getIcon().toIcon(), next.getSubType(), next.getHints());
                    break;
                case 5:
                    builder.addRemoteInput(next.getRemoteInput(), next.getSubType(), next.getHints());
                    break;
                case 6:
                    builder.addSubSlice(unwrap(next.getSlice()), next.getSubType());
                    break;
            }
        }
        return builder.build();
    }

    private static SliceSpec unwrap(SliceSpec sliceSpec) {
        if (sliceSpec == null) {
            return null;
        }
        return new SliceSpec(sliceSpec.getType(), sliceSpec.getRevision());
    }

    static Set<SliceSpec> unwrap(Set<SliceSpec> set) {
        ArraySet arraySet = new ArraySet();
        if (set != null) {
            for (SliceSpec unwrap : set) {
                arraySet.add(unwrap(unwrap));
            }
        }
        return arraySet;
    }

    public static Slice wrap(Slice slice, Context context) {
        if (slice == null || slice.getUri() == null) {
            return null;
        }
        Slice.Builder builder = new Slice.Builder(slice.getUri());
        builder.addHints(slice.getHints());
        builder.setSpec(wrap(slice.getSpec()));
        for (SliceItem next : slice.getItems()) {
            String format = next.getFormat();
            format.hashCode();
            char c = 65535;
            switch (format.hashCode()) {
                case -1422950858:
                    if (format.equals("action")) {
                        c = 0;
                        break;
                    }
                    break;
                case 104431:
                    if (format.equals("int")) {
                        c = 1;
                        break;
                    }
                    break;
                case 3327612:
                    if (format.equals("long")) {
                        c = 2;
                        break;
                    }
                    break;
                case 3556653:
                    if (format.equals("text")) {
                        c = 3;
                        break;
                    }
                    break;
                case 100313435:
                    if (format.equals("image")) {
                        c = 4;
                        break;
                    }
                    break;
                case 100358090:
                    if (format.equals("input")) {
                        c = 5;
                        break;
                    }
                    break;
                case 109526418:
                    if (format.equals(SliceProviderCompat.EXTRA_SLICE)) {
                        c = 6;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.addAction(next.getAction(), wrap(next.getSlice(), context), next.getSubType());
                    break;
                case 1:
                    builder.addInt(next.getInt(), next.getSubType(), next.getHints());
                    break;
                case 2:
                    builder.addLong(next.getLong(), next.getSubType(), next.getHints());
                    break;
                case 3:
                    builder.addText(next.getText(), next.getSubType(), next.getHints());
                    break;
                case 4:
                    try {
                        builder.addIcon(IconCompat.createFromIcon(context, next.getIcon()), next.getSubType(), next.getHints());
                        break;
                    } catch (IllegalArgumentException e) {
                        Log.w(TAG, "The icon resource isn't available.", e);
                        break;
                    }
                case 5:
                    builder.addRemoteInput(next.getRemoteInput(), next.getSubType(), next.getHints());
                    break;
                case 6:
                    builder.addSubSlice(wrap(next.getSlice(), context), next.getSubType());
                    break;
            }
        }
        return builder.build();
    }

    private static SliceSpec wrap(SliceSpec sliceSpec) {
        if (sliceSpec == null) {
            return null;
        }
        return new SliceSpec(sliceSpec.getType(), sliceSpec.getRevision());
    }

    public static Set<SliceSpec> wrap(Set<SliceSpec> set) {
        ArraySet arraySet = new ArraySet();
        if (set != null) {
            for (SliceSpec wrap : set) {
                arraySet.add(wrap(wrap));
            }
        }
        return arraySet;
    }

    private SliceConvert() {
    }
}
