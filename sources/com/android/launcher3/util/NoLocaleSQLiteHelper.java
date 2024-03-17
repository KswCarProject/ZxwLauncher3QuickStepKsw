package com.android.launcher3.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public abstract class NoLocaleSQLiteHelper extends SQLiteOpenHelper {
    private static final boolean ATLEAST_P = (Build.VERSION.SDK_INT >= 28);

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NoLocaleSQLiteHelper(android.content.Context r3, java.lang.String r4, int r5) {
        /*
            r2 = this;
            boolean r0 = ATLEAST_P
            if (r0 == 0) goto L_0x0005
            goto L_0x000b
        L_0x0005:
            com.android.launcher3.util.NoLocaleSQLiteHelper$NoLocalContext r1 = new com.android.launcher3.util.NoLocaleSQLiteHelper$NoLocalContext
            r1.<init>(r3)
            r3 = r1
        L_0x000b:
            r1 = 0
            r2.<init>(r3, r4, r1, r5)
            if (r0 == 0) goto L_0x0023
            android.database.sqlite.SQLiteDatabase$OpenParams$Builder r3 = new android.database.sqlite.SQLiteDatabase$OpenParams$Builder
            r3.<init>()
            r4 = 16
            android.database.sqlite.SQLiteDatabase$OpenParams$Builder r3 = r3.addOpenFlags(r4)
            android.database.sqlite.SQLiteDatabase$OpenParams r3 = r3.build()
            r2.setOpenParams(r3)
        L_0x0023:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.util.NoLocaleSQLiteHelper.<init>(android.content.Context, java.lang.String, int):void");
    }

    private static class NoLocalContext extends ContextWrapper {
        public NoLocalContext(Context context) {
            super(context);
        }

        public SQLiteDatabase openOrCreateDatabase(String str, int i, SQLiteDatabase.CursorFactory cursorFactory, DatabaseErrorHandler databaseErrorHandler) {
            return super.openOrCreateDatabase(str, i | 16, cursorFactory, databaseErrorHandler);
        }
    }
}
