package androidx.slice.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SliceHints {
    public static final String HINT_ACTIVITY = "activity";
    public static final int ICON_IMAGE = 0;
    public static final long INFINITY = -1;
    public static final int LARGE_IMAGE = 2;
    public static final String SLICE_METADATA_KEY = "android.metadata.SLICE_URI";
    public static final int SMALL_IMAGE = 1;
    public static final String SUBTYPE_MILLIS = "millis";
    public static final String SUBTYPE_MIN = "min";
    public static final int UNKNOWN_IMAGE = 3;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageMode {
    }

    private SliceHints() {
    }
}
