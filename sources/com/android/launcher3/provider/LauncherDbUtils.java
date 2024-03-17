package com.android.launcher3.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.CancellationSignal;
import android.os.Process;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.IntArray;

public class LauncherDbUtils {
    public static IntArray queryIntArray(boolean z, SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, String str4, String str5) {
        Throwable th;
        IntArray intArray = new IntArray();
        Cursor query = sQLiteDatabase.query(z, str, new String[]{str2}, str3, (String[]) null, str4, (String) null, str5, (String) null);
        while (query.moveToNext()) {
            try {
                intArray.add(query.getInt(0));
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
        }
        if (query != null) {
            query.close();
        }
        return intArray;
        throw th;
    }

    public static boolean tableExists(SQLiteDatabase sQLiteDatabase, String str) {
        boolean z = true;
        Cursor query = sQLiteDatabase.query(true, "sqlite_master", new String[]{"tbl_name"}, "tbl_name = ?", new String[]{str}, (String) null, (String) null, (String) null, (String) null, (CancellationSignal) null);
        try {
            if (query.getCount() <= 0) {
                z = false;
            }
            if (query != null) {
                query.close();
            }
            return z;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, String str) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + str);
    }

    public static void copyTable(SQLiteDatabase sQLiteDatabase, String str, SQLiteDatabase sQLiteDatabase2, String str2, Context context) {
        long serialNumberForUser = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getSerialNumberForUser(Process.myUserHandle());
        dropTable(sQLiteDatabase2, str2);
        LauncherSettings.Favorites.addTableToDb(sQLiteDatabase2, serialNumberForUser, false, str2);
        if (sQLiteDatabase != sQLiteDatabase2) {
            sQLiteDatabase2.execSQL("ATTACH DATABASE '" + sQLiteDatabase.getPath() + "' AS from_db");
            sQLiteDatabase2.execSQL("INSERT INTO " + str2 + " SELECT * FROM from_db." + str);
            sQLiteDatabase2.execSQL("DETACH DATABASE 'from_db'");
            return;
        }
        sQLiteDatabase2.execSQL("INSERT INTO " + str2 + " SELECT * FROM " + str);
    }

    public static class SQLiteTransaction extends Binder implements AutoCloseable {
        private final SQLiteDatabase mDb;

        public SQLiteTransaction(SQLiteDatabase sQLiteDatabase) {
            this.mDb = sQLiteDatabase;
            sQLiteDatabase.beginTransaction();
        }

        public void commit() {
            this.mDb.setTransactionSuccessful();
        }

        public void close() {
            this.mDb.endTransaction();
        }

        public SQLiteDatabase getDb() {
            return this.mDb;
        }
    }
}
