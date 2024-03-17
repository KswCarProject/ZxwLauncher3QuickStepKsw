package androidx.slice.compat;

import android.app.slice.Slice;
import android.app.slice.SliceProvider;
import android.app.slice.SliceSpec;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.util.Log;
import androidx.slice.SliceConvert;
import java.util.Collection;
import java.util.Set;

public class SliceProviderWrapperContainer {

    public static class SliceProviderWrapper extends SliceProvider {
        private static final String TAG = "SliceProviderWrapper";
        private androidx.slice.SliceProvider mSliceProvider;

        public boolean onCreate() {
            return true;
        }

        public SliceProviderWrapper(androidx.slice.SliceProvider sliceProvider, String[] strArr) {
            super(strArr);
            this.mSliceProvider = sliceProvider;
        }

        public void attachInfo(Context context, ProviderInfo providerInfo) {
            this.mSliceProvider.attachInfo(context, providerInfo);
            super.attachInfo(context, providerInfo);
        }

        public Slice onBindSlice(Uri uri, Set<SliceSpec> set) {
            androidx.slice.SliceProvider.setSpecs(SliceConvert.wrap(set));
            try {
                return SliceConvert.unwrap(this.mSliceProvider.onBindSlice(uri));
            } catch (Exception e) {
                Log.wtf(TAG, "Slice with URI " + uri.toString() + " is invalid.", e);
                return null;
            } finally {
                androidx.slice.SliceProvider.setSpecs((Set<androidx.slice.SliceSpec>) null);
            }
        }

        public void onSlicePinned(Uri uri) {
            this.mSliceProvider.onSlicePinned(uri);
            this.mSliceProvider.handleSlicePinned(uri);
        }

        public void onSliceUnpinned(Uri uri) {
            this.mSliceProvider.onSliceUnpinned(uri);
            this.mSliceProvider.handleSliceUnpinned(uri);
        }

        public Collection<Uri> onGetSliceDescendants(Uri uri) {
            return this.mSliceProvider.onGetSliceDescendants(uri);
        }

        public Uri onMapIntentToUri(Intent intent) {
            return this.mSliceProvider.onMapIntentToUri(intent);
        }
    }

    private SliceProviderWrapperContainer() {
    }
}
