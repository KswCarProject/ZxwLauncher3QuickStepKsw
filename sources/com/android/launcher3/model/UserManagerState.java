package com.android.launcher3.model;

import android.os.UserHandle;
import android.os.UserManager;
import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import com.android.launcher3.pm.UserCache;

public class UserManagerState {
    public final LongSparseArray<UserHandle> allUsers = new LongSparseArray<>();
    private final SparseBooleanArray mQuietUsersHashCodeMap = new SparseBooleanArray();
    private final LongSparseArray<Boolean> mQuietUsersSerialNoMap = new LongSparseArray<>();

    public void init(UserCache userCache, UserManager userManager) {
        for (UserHandle next : userManager.getUserProfiles()) {
            long serialNumberForUser = userCache.getSerialNumberForUser(next);
            boolean isQuietModeEnabled = userManager.isQuietModeEnabled(next);
            this.allUsers.put(serialNumberForUser, next);
            this.mQuietUsersHashCodeMap.put(next.hashCode(), isQuietModeEnabled);
            this.mQuietUsersSerialNoMap.put(serialNumberForUser, Boolean.valueOf(isQuietModeEnabled));
        }
    }

    public boolean isUserQuiet(long j) {
        return this.mQuietUsersSerialNoMap.get(j).booleanValue();
    }

    public boolean isUserQuiet(UserHandle userHandle) {
        return this.mQuietUsersHashCodeMap.get(userHandle.hashCode());
    }

    public boolean isAnyProfileQuietModeEnabled() {
        for (int size = this.mQuietUsersHashCodeMap.size() - 1; size >= 0; size--) {
            if (this.mQuietUsersHashCodeMap.valueAt(size)) {
                return true;
            }
        }
        return false;
    }
}
