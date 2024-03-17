package com.android.launcher3.testing;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.android.launcher3.Utilities;

public class TestInformationProvider extends ContentProvider {
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if (!Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return null;
        }
        TestInformationHandler newInstance = TestInformationHandler.newInstance(getContext());
        newInstance.init(getContext());
        return newInstance.call(str, str2, bundle);
    }
}
