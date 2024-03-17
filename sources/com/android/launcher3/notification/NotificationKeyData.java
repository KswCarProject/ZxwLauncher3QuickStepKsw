package com.android.launcher3.notification;

import android.app.Notification;
import android.app.Person;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.Utilities;
import java.util.ArrayList;
import java.util.List;

public class NotificationKeyData {
    public int count;
    public final String notificationKey;
    public final String[] personKeysFromNotification;
    public final String shortcutId;

    private NotificationKeyData(String str, String str2, int i, String[] strArr) {
        this.notificationKey = str;
        this.shortcutId = str2;
        this.count = Math.max(1, i);
        this.personKeysFromNotification = strArr;
    }

    public static NotificationKeyData fromNotification(StatusBarNotification statusBarNotification) {
        Notification notification = statusBarNotification.getNotification();
        return new NotificationKeyData(statusBarNotification.getKey(), notification.getShortcutId(), notification.number, extractPersonKeyOnly(notification.extras.getParcelableArrayList(NotificationCompat.EXTRA_PEOPLE_LIST)));
    }

    public static List<String> extractKeysOnly(List<NotificationKeyData> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (NotificationKeyData notificationKeyData : list) {
            arrayList.add(notificationKeyData.notificationKey);
        }
        return arrayList;
    }

    private static String[] extractPersonKeyOnly(ArrayList<Person> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return Utilities.EMPTY_STRING_ARRAY;
        }
        return (String[]) arrayList.stream().filter($$Lambda$NotificationKeyData$OdWvvVIajWKEutNwdTn2pAcrqQ.INSTANCE).map($$Lambda$NotificationKeyData$NLQcSJEFKBLqD04iK_mFvrZ6l0.INSTANCE).sorted().toArray($$Lambda$NotificationKeyData$jyqK7JLUK3uupFGZy9Xwkkwwq4.INSTANCE);
    }

    static /* synthetic */ boolean lambda$extractPersonKeyOnly$0(Person person) {
        return person.getKey() != null;
    }

    static /* synthetic */ String[] lambda$extractPersonKeyOnly$1(int i) {
        return new String[i];
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NotificationKeyData)) {
            return false;
        }
        return ((NotificationKeyData) obj).notificationKey.equals(this.notificationKey);
    }
}
