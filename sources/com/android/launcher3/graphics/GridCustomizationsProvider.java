package com.android.launcher3.graphics;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.ArrayMap;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.Themes;
import java.util.Iterator;

public class GridCustomizationsProvider extends ContentProvider {
    private static final String BOOLEAN_VALUE = "boolean_value";
    private static final String GET_ICON_THEMED = "/get_icon_themed";
    private static final String ICON_THEMED = "/icon_themed";
    private static final String KEY_CALLBACK = "callback";
    private static final String KEY_COLS = "cols";
    private static final String KEY_DEFAULT_GRID = "/default_grid";
    private static final String KEY_IS_DEFAULT = "is_default";
    private static final String KEY_LIST_OPTIONS = "/list_options";
    private static final String KEY_NAME = "name";
    private static final String KEY_PREVIEW_COUNT = "preview_count";
    private static final String KEY_ROWS = "rows";
    private static final String KEY_SURFACE_PACKAGE = "surface_package";
    private static final String METHOD_GET_PREVIEW = "get_preview";
    private static final String SET_ICON_THEMED = "/set_icon_themed";
    private static final String TAG = "GridCustomizationsProvider";
    private final ArrayMap<IBinder, PreviewLifecycleObserver> mActivePreviews = new ArrayMap<>();

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/launcher_grid";
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String path = uri.getPath();
        path.hashCode();
        char c = 65535;
        switch (path.hashCode()) {
            case 1222873516:
                if (path.equals(KEY_LIST_OPTIONS)) {
                    c = 0;
                    break;
                }
                break;
            case 1300895410:
                if (path.equals(ICON_THEMED)) {
                    c = 1;
                    break;
                }
                break;
            case 2143818441:
                if (path.equals(GET_ICON_THEMED)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                MatrixCursor matrixCursor = new MatrixCursor(new String[]{KEY_NAME, KEY_ROWS, KEY_COLS, KEY_PREVIEW_COUNT, KEY_IS_DEFAULT});
                InvariantDeviceProfile invariantDeviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext());
                for (InvariantDeviceProfile.GridOption next : invariantDeviceProfile.parseAllGridOptions(getContext())) {
                    matrixCursor.newRow().add(KEY_NAME, next.name).add(KEY_ROWS, Integer.valueOf(next.numRows)).add(KEY_COLS, Integer.valueOf(next.numColumns)).add(KEY_PREVIEW_COUNT, 1).add(KEY_IS_DEFAULT, Boolean.valueOf(invariantDeviceProfile.numColumns == next.numColumns && invariantDeviceProfile.numRows == next.numRows));
                }
                return matrixCursor;
            case 1:
            case 2:
                MatrixCursor matrixCursor2 = new MatrixCursor(new String[]{BOOLEAN_VALUE});
                matrixCursor2.newRow().add(BOOLEAN_VALUE, Integer.valueOf(Themes.isThemedIconEnabled(getContext()) ? 1 : 0));
                return matrixCursor2;
            default:
                return null;
        }
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        String path = uri.getPath();
        path.hashCode();
        char c = 65535;
        switch (path.hashCode()) {
            case -1555668941:
                if (path.equals(KEY_DEFAULT_GRID)) {
                    c = 0;
                    break;
                }
                break;
            case -1240396331:
                if (path.equals(SET_ICON_THEMED)) {
                    c = 1;
                    break;
                }
                break;
            case 1300895410:
                if (path.equals(ICON_THEMED)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                String asString = contentValues.getAsString(KEY_NAME);
                InvariantDeviceProfile invariantDeviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext());
                InvariantDeviceProfile.GridOption gridOption = null;
                Iterator<InvariantDeviceProfile.GridOption> it = invariantDeviceProfile.parseAllGridOptions(getContext()).iterator();
                while (true) {
                    if (it.hasNext()) {
                        InvariantDeviceProfile.GridOption next = it.next();
                        if (next.name.equals(asString)) {
                            gridOption = next;
                        }
                    }
                }
                if (gridOption == null) {
                    return 0;
                }
                invariantDeviceProfile.setCurrentGrid(getContext(), asString);
                return 1;
            case 1:
            case 2:
                if (FeatureFlags.ENABLE_THEMED_ICONS.get()) {
                    Utilities.getPrefs(getContext()).edit().putBoolean(Themes.KEY_THEMED_ICONS, contentValues.getAsBoolean(BOOLEAN_VALUE).booleanValue()).apply();
                }
                return 1;
            default:
                return 0;
        }
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if (getContext().checkPermission("android.permission.BIND_WALLPAPER", Binder.getCallingPid(), Binder.getCallingUid()) == 0 && Utilities.ATLEAST_R && METHOD_GET_PREVIEW.equals(str)) {
            return getPreview(bundle);
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x006d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized android.os.Bundle getPreview(android.os.Bundle r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            com.android.launcher3.graphics.PreviewSurfaceRenderer r1 = new com.android.launcher3.graphics.PreviewSurfaceRenderer     // Catch:{ Exception -> 0x0062 }
            android.content.Context r2 = r5.getContext()     // Catch:{ Exception -> 0x0062 }
            r1.<init>(r2, r6)     // Catch:{ Exception -> 0x0062 }
            android.util.ArrayMap<android.os.IBinder, com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver> r6 = r5.mActivePreviews     // Catch:{ Exception -> 0x0062 }
            android.os.IBinder r2 = r1.getHostToken()     // Catch:{ Exception -> 0x0062 }
            java.lang.Object r6 = r6.get(r2)     // Catch:{ Exception -> 0x0062 }
            com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver r6 = (com.android.launcher3.graphics.GridCustomizationsProvider.PreviewLifecycleObserver) r6     // Catch:{ Exception -> 0x0062 }
            r5.destroyObserver(r6)     // Catch:{ Exception -> 0x0062 }
            com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver r6 = new com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver     // Catch:{ Exception -> 0x0062 }
            r6.<init>(r1)     // Catch:{ Exception -> 0x0062 }
            android.util.ArrayMap<android.os.IBinder, com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver> r2 = r5.mActivePreviews     // Catch:{ Exception -> 0x005e }
            android.os.IBinder r3 = r1.getHostToken()     // Catch:{ Exception -> 0x005e }
            r2.put(r3, r6)     // Catch:{ Exception -> 0x005e }
            r1.loadAsync()     // Catch:{ Exception -> 0x005e }
            android.os.IBinder r2 = r1.getHostToken()     // Catch:{ Exception -> 0x005e }
            r3 = 0
            r2.linkToDeath(r6, r3)     // Catch:{ Exception -> 0x005e }
            android.os.Bundle r2 = new android.os.Bundle     // Catch:{ Exception -> 0x005e }
            r2.<init>()     // Catch:{ Exception -> 0x005e }
            java.lang.String r3 = "surface_package"
            android.view.SurfaceControlViewHost$SurfacePackage r1 = r1.getSurfacePackage()     // Catch:{ Exception -> 0x005e }
            r2.putParcelable(r3, r1)     // Catch:{ Exception -> 0x005e }
            android.os.Messenger r1 = new android.os.Messenger     // Catch:{ Exception -> 0x005e }
            android.os.Handler r3 = new android.os.Handler     // Catch:{ Exception -> 0x005e }
            com.android.launcher3.util.LooperExecutor r4 = com.android.launcher3.util.Executors.UI_HELPER_EXECUTOR     // Catch:{ Exception -> 0x005e }
            android.os.Looper r4 = r4.getLooper()     // Catch:{ Exception -> 0x005e }
            r3.<init>(r4, r6)     // Catch:{ Exception -> 0x005e }
            r1.<init>(r3)     // Catch:{ Exception -> 0x005e }
            android.os.Message r3 = android.os.Message.obtain()     // Catch:{ Exception -> 0x005e }
            r3.replyTo = r1     // Catch:{ Exception -> 0x005e }
            java.lang.String r1 = "callback"
            r2.putParcelable(r1, r3)     // Catch:{ Exception -> 0x005e }
            monitor-exit(r5)
            return r2
        L_0x005e:
            r1 = move-exception
            goto L_0x0064
        L_0x0060:
            r6 = move-exception
            goto L_0x0072
        L_0x0062:
            r1 = move-exception
            r6 = r0
        L_0x0064:
            java.lang.String r2 = "GridCustomizationsProvider"
            java.lang.String r3 = "Unable to generate preview"
            android.util.Log.e(r2, r3, r1)     // Catch:{ all -> 0x0060 }
            if (r6 == 0) goto L_0x0070
            r5.destroyObserver(r6)     // Catch:{ all -> 0x0060 }
        L_0x0070:
            monitor-exit(r5)
            return r0
        L_0x0072:
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.graphics.GridCustomizationsProvider.getPreview(android.os.Bundle):android.os.Bundle");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0040, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0045, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void destroyObserver(com.android.launcher3.graphics.GridCustomizationsProvider.PreviewLifecycleObserver r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            if (r4 == 0) goto L_0x0044
            boolean r0 = r4.destroyed     // Catch:{ all -> 0x0041 }
            if (r0 == 0) goto L_0x0008
            goto L_0x0044
        L_0x0008:
            r0 = 1
            r4.destroyed = r0     // Catch:{ all -> 0x0041 }
            com.android.launcher3.graphics.PreviewSurfaceRenderer r0 = r4.renderer     // Catch:{ all -> 0x0041 }
            android.os.IBinder r0 = r0.getHostToken()     // Catch:{ all -> 0x0041 }
            r1 = 0
            r0.unlinkToDeath(r4, r1)     // Catch:{ all -> 0x0041 }
            com.android.launcher3.util.LooperExecutor r0 = com.android.launcher3.util.Executors.MAIN_EXECUTOR     // Catch:{ all -> 0x0041 }
            com.android.launcher3.graphics.PreviewSurfaceRenderer r1 = r4.renderer     // Catch:{ all -> 0x0041 }
            java.util.Objects.requireNonNull(r1)     // Catch:{ all -> 0x0041 }
            com.android.launcher3.graphics.-$$Lambda$7hV8D39WqiL3uSkigN9E4qJPEtY r2 = new com.android.launcher3.graphics.-$$Lambda$7hV8D39WqiL3uSkigN9E4qJPEtY     // Catch:{ all -> 0x0041 }
            r2.<init>()     // Catch:{ all -> 0x0041 }
            r0.execute(r2)     // Catch:{ all -> 0x0041 }
            android.util.ArrayMap<android.os.IBinder, com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver> r0 = r3.mActivePreviews     // Catch:{ all -> 0x0041 }
            com.android.launcher3.graphics.PreviewSurfaceRenderer r1 = r4.renderer     // Catch:{ all -> 0x0041 }
            android.os.IBinder r1 = r1.getHostToken()     // Catch:{ all -> 0x0041 }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x0041 }
            com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver r0 = (com.android.launcher3.graphics.GridCustomizationsProvider.PreviewLifecycleObserver) r0     // Catch:{ all -> 0x0041 }
            if (r0 != r4) goto L_0x003f
            android.util.ArrayMap<android.os.IBinder, com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver> r0 = r3.mActivePreviews     // Catch:{ all -> 0x0041 }
            com.android.launcher3.graphics.PreviewSurfaceRenderer r4 = r4.renderer     // Catch:{ all -> 0x0041 }
            android.os.IBinder r4 = r4.getHostToken()     // Catch:{ all -> 0x0041 }
            r0.remove(r4)     // Catch:{ all -> 0x0041 }
        L_0x003f:
            monitor-exit(r3)
            return
        L_0x0041:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        L_0x0044:
            monitor-exit(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.graphics.GridCustomizationsProvider.destroyObserver(com.android.launcher3.graphics.GridCustomizationsProvider$PreviewLifecycleObserver):void");
    }

    private class PreviewLifecycleObserver implements Handler.Callback, IBinder.DeathRecipient {
        public boolean destroyed = false;
        public final PreviewSurfaceRenderer renderer;

        PreviewLifecycleObserver(PreviewSurfaceRenderer previewSurfaceRenderer) {
            this.renderer = previewSurfaceRenderer;
        }

        public boolean handleMessage(Message message) {
            GridCustomizationsProvider.this.destroyObserver(this);
            return true;
        }

        public void binderDied() {
            GridCustomizationsProvider.this.destroyObserver(this);
        }
    }
}
