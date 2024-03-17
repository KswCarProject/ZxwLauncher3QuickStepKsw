package dagger.hilt.internal;

public final class UnsafeCasts {
    public static <T> T unsafeCast(Object obj) {
        return obj;
    }

    private UnsafeCasts() {
    }
}
