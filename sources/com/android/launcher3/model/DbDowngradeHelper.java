package com.android.launcher3.model;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.SparseArray;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.util.IOUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DbDowngradeHelper {
    private static final String KEY_DOWNGRADE_TO = "downgrade_to_";
    private static final String KEY_VERSION = "version";
    private static final String TAG = "DbDowngradeHelper";
    private final SparseArray<String[]> mStatements = new SparseArray<>();
    public final int version;

    private DbDowngradeHelper(int i) {
        this.version = i;
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        int i3 = i - 1;
        while (i3 >= i2) {
            String[] strArr = this.mStatements.get(i3);
            if (strArr != null) {
                Collections.addAll(arrayList, strArr);
                i3--;
            } else {
                throw new SQLiteException("Downgrade path not supported to version " + i3);
            }
        }
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(sQLiteDatabase);
        try {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                sQLiteDatabase.execSQL((String) it.next());
            }
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            return;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public static DbDowngradeHelper parse(File file) throws JSONException, IOException {
        JSONObject jSONObject = new JSONObject(new String(IOUtils.toByteArray(file)));
        DbDowngradeHelper dbDowngradeHelper = new DbDowngradeHelper(jSONObject.getInt("version"));
        for (int i = dbDowngradeHelper.version - 1; i > 0; i--) {
            if (jSONObject.has(KEY_DOWNGRADE_TO + i)) {
                JSONArray jSONArray = jSONObject.getJSONArray(KEY_DOWNGRADE_TO + i);
                int length = jSONArray.length();
                String[] strArr = new String[length];
                for (int i2 = 0; i2 < length; i2++) {
                    strArr[i2] = jSONArray.getString(i2);
                }
                dbDowngradeHelper.mStatements.put(i, strArr);
            }
        }
        return dbDowngradeHelper;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|(1:3)|4|5|6|7|8|9|(2:11|12)|13|14|33|(1:(1:22))) */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0036, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        r1.addSuppressed(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x003b, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x003c, code lost:
        android.util.Log.e(TAG, "Error writing schema file", r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0009 */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x001e A[SYNTHETIC, Splitter:B:11:0x001e] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void updateSchemaFile(java.io.File r1, int r2, android.content.Context r3) {
        /*
            com.android.launcher3.model.DbDowngradeHelper r0 = parse(r1)     // Catch:{ Exception -> 0x0009 }
            int r0 = r0.version     // Catch:{ Exception -> 0x0009 }
            if (r0 < r2) goto L_0x0009
            return
        L_0x0009:
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x003b }
            r2.<init>(r1)     // Catch:{ IOException -> 0x003b }
            android.content.res.Resources r1 = r3.getResources()     // Catch:{ all -> 0x0031 }
            r3 = 2131689473(0x7f0f0001, float:1.9007962E38)
            java.io.InputStream r1 = r1.openRawResource(r3)     // Catch:{ all -> 0x0031 }
            com.android.launcher3.util.IOUtils.copy(r1, r2)     // Catch:{ all -> 0x0025 }
            if (r1 == 0) goto L_0x0021
            r1.close()     // Catch:{ all -> 0x0031 }
        L_0x0021:
            r2.close()     // Catch:{ IOException -> 0x003b }
            goto L_0x0043
        L_0x0025:
            r3 = move-exception
            if (r1 == 0) goto L_0x0030
            r1.close()     // Catch:{ all -> 0x002c }
            goto L_0x0030
        L_0x002c:
            r1 = move-exception
            r3.addSuppressed(r1)     // Catch:{ all -> 0x0031 }
        L_0x0030:
            throw r3     // Catch:{ all -> 0x0031 }
        L_0x0031:
            r1 = move-exception
            r2.close()     // Catch:{ all -> 0x0036 }
            goto L_0x003a
        L_0x0036:
            r2 = move-exception
            r1.addSuppressed(r2)     // Catch:{ IOException -> 0x003b }
        L_0x003a:
            throw r1     // Catch:{ IOException -> 0x003b }
        L_0x003b:
            r1 = move-exception
            java.lang.String r2 = "DbDowngradeHelper"
            java.lang.String r3 = "Error writing schema file"
            android.util.Log.e(r2, r3, r1)
        L_0x0043:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.DbDowngradeHelper.updateSchemaFile(java.io.File, int, android.content.Context):void");
    }
}
