package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShortcutRequest {
    public static final int ALL = 11;
    public static final int PINNED = 2;
    public static final int PUBLISHED = 9;
    private static final String TAG = "ShortcutRequest";
    private final Context mContext;
    boolean mFailed = false;
    private final LauncherApps.ShortcutQuery mQuery = new LauncherApps.ShortcutQuery();
    private final UserHandle mUserHandle;

    public ShortcutRequest(Context context, UserHandle userHandle) {
        this.mContext = context;
        this.mUserHandle = userHandle;
    }

    public ShortcutRequest forPackage(String str) {
        return forPackage(str, (List<String>) null);
    }

    public ShortcutRequest forPackage(String str, String... strArr) {
        return forPackage(str, (List<String>) Arrays.asList(strArr));
    }

    public ShortcutRequest forPackage(String str, List<String> list) {
        if (str != null) {
            this.mQuery.setPackage(str);
            this.mQuery.setShortcutIds(list);
        }
        return this;
    }

    public ShortcutRequest withContainer(ComponentName componentName) {
        if (componentName == null) {
            this.mFailed = true;
        } else {
            this.mQuery.setActivity(componentName);
        }
        return this;
    }

    public QueryResult query(int i) {
        if (this.mFailed) {
            return QueryResult.DEFAULT;
        }
        this.mQuery.setQueryFlags(i);
        try {
            return new QueryResult(((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).getShortcuts(this.mQuery, this.mUserHandle));
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to query for shortcuts", e);
            return QueryResult.DEFAULT;
        }
    }

    public static class QueryResult extends ArrayList<ShortcutInfo> {
        static final QueryResult DEFAULT = new QueryResult(false);
        private final boolean mWasSuccess;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        QueryResult(List<ShortcutInfo> list) {
            super(list == null ? Collections.emptyList() : list);
            this.mWasSuccess = true;
        }

        QueryResult(boolean z) {
            this.mWasSuccess = z;
        }

        public boolean wasSuccess() {
            return this.mWasSuccess;
        }
    }
}
