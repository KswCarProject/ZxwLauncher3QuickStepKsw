package androidx.slice;

import android.app.slice.SliceManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

class SliceManagerWrapper extends SliceManager {
    private final Context mContext;
    private final SliceManager mManager;

    SliceManagerWrapper(Context context) {
        this(context, (SliceManager) context.getSystemService(SliceManager.class));
    }

    SliceManagerWrapper(Context context, SliceManager sliceManager) {
        this.mContext = context;
        this.mManager = sliceManager;
    }

    public Set<SliceSpec> getPinnedSpecs(Uri uri) {
        if (Build.VERSION.SDK_INT == 28) {
            uri = maybeAddCurrentUserId(uri);
        }
        return SliceConvert.wrap(this.mManager.getPinnedSpecs(uri));
    }

    public int checkSlicePermission(Uri uri, int i, int i2) {
        return this.mManager.checkSlicePermission(uri, i, i2);
    }

    public void grantSlicePermission(String str, Uri uri) {
        this.mManager.grantSlicePermission(str, uri);
    }

    public void revokeSlicePermission(String str, Uri uri) {
        this.mManager.revokeSlicePermission(str, uri);
    }

    public List<Uri> getPinnedSlices() {
        return this.mManager.getPinnedSlices();
    }

    private Uri maybeAddCurrentUserId(Uri uri) {
        if (uri == null || uri.getAuthority().contains("@")) {
            return uri;
        }
        return uri.buildUpon().encodedAuthority(getCurrentUserId() + "@" + uri.getAuthority()).build();
    }

    private int getCurrentUserId() {
        UserHandle myUserHandle = Process.myUserHandle();
        try {
            return ((Integer) myUserHandle.getClass().getDeclaredMethod("getIdentifier", new Class[0]).invoke(myUserHandle, new Object[0])).intValue();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            return 0;
        }
    }
}
