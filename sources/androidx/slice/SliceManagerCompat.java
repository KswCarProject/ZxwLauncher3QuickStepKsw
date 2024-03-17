package androidx.slice;

import android.content.Context;
import android.net.Uri;
import androidx.slice.compat.SliceProviderCompat;
import java.util.List;
import java.util.Set;

class SliceManagerCompat extends SliceManager {
    private final Context mContext;

    SliceManagerCompat(Context context) {
        this.mContext = context;
    }

    public Set<SliceSpec> getPinnedSpecs(Uri uri) {
        return SliceProviderCompat.getPinnedSpecs(this.mContext, uri);
    }

    public int checkSlicePermission(Uri uri, int i, int i2) {
        Context context = this.mContext;
        return SliceProviderCompat.checkSlicePermission(context, context.getPackageName(), uri, i, i2);
    }

    public void grantSlicePermission(String str, Uri uri) {
        Context context = this.mContext;
        SliceProviderCompat.grantSlicePermission(context, context.getPackageName(), str, uri);
    }

    public void revokeSlicePermission(String str, Uri uri) {
        Context context = this.mContext;
        SliceProviderCompat.revokeSlicePermission(context, context.getPackageName(), str, uri);
    }

    public List<Uri> getPinnedSlices() {
        return SliceProviderCompat.getPinnedSlices(this.mContext);
    }
}
