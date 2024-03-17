package androidx.slice;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Process;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import androidx.core.app.CoreComponentFactory;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.compat.CompatPermissionManager;
import androidx.slice.compat.SliceProviderCompat;
import androidx.slice.compat.SliceProviderWrapperContainer;
import androidx.slice.core.R;
import com.android.launcher3.LauncherSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class SliceProvider extends ContentProvider implements CoreComponentFactory.CompatWrapped {
    private static final boolean DEBUG = false;
    private static final String TAG = "SliceProvider";
    private static Clock sClock;
    private static Set<SliceSpec> sSpecs;
    private final String[] mAutoGrantPermissions;
    private SliceProviderCompat mCompat;
    private List<Uri> mPinnedSliceUris;

    public final int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        return 0;
    }

    public final Uri canonicalize(Uri uri) {
        return null;
    }

    public final int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public final Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public abstract Slice onBindSlice(Uri uri);

    public abstract boolean onCreateSliceProvider();

    public void onSlicePinned(Uri uri) {
    }

    public void onSliceUnpinned(Uri uri) {
    }

    public final Cursor query(Uri uri, String[] strArr, Bundle bundle, CancellationSignal cancellationSignal) {
        return null;
    }

    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        return null;
    }

    public final int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public SliceProvider(String... strArr) {
        this.mAutoGrantPermissions = strArr;
    }

    public SliceProvider() {
        this.mAutoGrantPermissions = new String[0];
    }

    public Object getWrapper() {
        if (Build.VERSION.SDK_INT >= 28) {
            return new SliceProviderWrapperContainer.SliceProviderWrapper(this, this.mAutoGrantPermissions);
        }
        return null;
    }

    public final boolean onCreate() {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        this.mPinnedSliceUris = new ArrayList(SliceManager.getInstance(getContext()).getPinnedSlices());
        if (Build.VERSION.SDK_INT < 28) {
            this.mCompat = new SliceProviderCompat(this, onCreatePermissionManager(this.mAutoGrantPermissions), getContext());
        }
        return onCreateSliceProvider();
    }

    /* access modifiers changed from: protected */
    public CompatPermissionManager onCreatePermissionManager(String[] strArr) {
        return new CompatPermissionManager(getContext(), SliceProviderCompat.PERMS_PREFIX + getClass().getName(), Process.myUid(), strArr);
    }

    public final String getType(Uri uri) {
        if (Build.VERSION.SDK_INT < 19) {
            return null;
        }
        return "vnd.android.slice";
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        SliceProviderCompat sliceProviderCompat;
        if (Build.VERSION.SDK_INT >= 19 && (sliceProviderCompat = this.mCompat) != null) {
            return sliceProviderCompat.call(str, str2, bundle);
        }
        return null;
    }

    public static Slice createPermissionSlice(Context context, Uri uri, String str) {
        PendingIntent createPermissionIntent = createPermissionIntent(context, uri, str);
        Slice.Builder builder = new Slice.Builder(uri);
        Slice.Builder addAction = new Slice.Builder(builder).addIcon(IconCompat.createWithResource(context, R.drawable.abc_ic_permission), (String) null, new String[0]).addHints((List<String>) Arrays.asList(new String[]{LauncherSettings.Favorites.TITLE, "shortcut"})).addAction(createPermissionIntent, new Slice.Builder(builder).build(), (String) null);
        TypedValue typedValue = new TypedValue();
        new ContextThemeWrapper(context, 16974123).getTheme().resolveAttribute(16843829, typedValue, true);
        builder.addSubSlice(new Slice.Builder(uri.buildUpon().appendPath("permission").build()).addIcon(IconCompat.createWithResource(context, R.drawable.abc_ic_arrow_forward), (String) null, new String[0]).addText(getPermissionString(context, str), (String) null, new String[0]).addInt(typedValue.data, "color", new String[0]).addSubSlice(addAction.build(), (String) null).build(), (String) null);
        return builder.addHints((List<String>) Arrays.asList(new String[]{"permission_request"})).build();
    }

    public static PendingIntent createPermissionIntent(Context context, Uri uri, String str) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context.getPackageName(), "androidx.slice.compat.SlicePermissionActivity"));
        intent.putExtra(SliceProviderCompat.EXTRA_BIND_URI, uri);
        intent.putExtra(SliceProviderCompat.EXTRA_PKG, str);
        intent.putExtra(SliceProviderCompat.EXTRA_PROVIDER_PKG, context.getPackageName());
        intent.setData(uri.buildUpon().appendQueryParameter("package", str).build());
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    public static CharSequence getPermissionString(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return context.getString(R.string.abc_slices_permission_request, new Object[]{packageManager.getApplicationInfo(str, 0).loadLabel(packageManager), context.getApplicationInfo().loadLabel(packageManager)});
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Unknown calling app", e);
        }
    }

    public void handleSlicePinned(Uri uri) {
        if (!this.mPinnedSliceUris.contains(uri)) {
            this.mPinnedSliceUris.add(uri);
        }
    }

    public void handleSliceUnpinned(Uri uri) {
        if (this.mPinnedSliceUris.contains(uri)) {
            this.mPinnedSliceUris.remove(uri);
        }
    }

    public Uri onMapIntentToUri(Intent intent) {
        throw new UnsupportedOperationException("This provider has not implemented intent to uri mapping");
    }

    public Collection<Uri> onGetSliceDescendants(Uri uri) {
        return Collections.emptyList();
    }

    public List<Uri> getPinnedSlices() {
        return this.mPinnedSliceUris;
    }

    public static void setSpecs(Set<SliceSpec> set) {
        sSpecs = set;
    }

    public static Set<SliceSpec> getCurrentSpecs() {
        return sSpecs;
    }

    public static void setClock(Clock clock) {
        sClock = clock;
    }

    public static Clock getClock() {
        return sClock;
    }
}
