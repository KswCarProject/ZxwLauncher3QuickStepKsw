package com.android.launcher3.notification;

import android.app.Notification;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.SettingsCache;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NotificationListener extends NotificationListenerService {
    private static final int MSG_CANCEL_NOTIFICATION = 4;
    private static final int MSG_NOTIFICATION_FULL_REFRESH = 3;
    private static final int MSG_NOTIFICATION_POSTED = 1;
    private static final int MSG_NOTIFICATION_REMOVED = 2;
    private static final int MSG_RANKING_UPDATE = 5;
    public static final String TAG = "NotificationListener";
    private static boolean sIsConnected;
    private static NotificationListener sNotificationListenerInstance;
    private static final ArraySet<NotificationsChangedListener> sNotificationsChangedListeners = new ArraySet<>();
    private String mLastKeyDismissedByLauncher;
    private final Map<String, String> mNotificationGroupKeyMap = new HashMap();
    private final Map<String, NotificationGroup> mNotificationGroupMap = new HashMap();
    private SettingsCache.OnChangeListener mNotificationSettingsChangedListener;
    private SettingsCache mSettingsCache;
    private final NotificationListenerService.Ranking mTempRanking = new NotificationListenerService.Ranking();
    private final Handler mUiHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        public final boolean handleMessage(Message message) {
            return NotificationListener.this.handleUiMessage(message);
        }
    });
    private final Handler mWorkerHandler = new Handler(Executors.MODEL_EXECUTOR.getLooper(), new Handler.Callback() {
        public final boolean handleMessage(Message message) {
            return NotificationListener.this.handleWorkerMessage(message);
        }
    });

    public interface NotificationsChangedListener {
        void onNotificationFullRefresh(List<StatusBarNotification> list);

        void onNotificationPosted(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData);

        void onNotificationRemoved(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData);
    }

    public NotificationListener() {
        sNotificationListenerInstance = this;
    }

    public static NotificationListener getInstanceIfConnected() {
        if (sIsConnected) {
            return sNotificationListenerInstance;
        }
        return null;
    }

    public static void addNotificationsChangedListener(NotificationsChangedListener notificationsChangedListener) {
        if (notificationsChangedListener != null) {
            sNotificationsChangedListeners.add(notificationsChangedListener);
            NotificationListener instanceIfConnected = getInstanceIfConnected();
            if (instanceIfConnected != null) {
                instanceIfConnected.onNotificationFullRefresh();
            } else {
                Executors.MODEL_EXECUTOR.submit(new Callable() {
                    public final Object call() {
                        return Executors.MAIN_EXECUTOR.submit(new Runnable() {
                            public final void run() {
                                NotificationListener.NotificationsChangedListener.this.onNotificationFullRefresh(Collections.emptyList());
                            }
                        });
                    }
                });
            }
        }
    }

    public static void removeNotificationsChangedListener(NotificationsChangedListener notificationsChangedListener) {
        if (notificationsChangedListener != null) {
            sNotificationsChangedListeners.remove(notificationsChangedListener);
        }
    }

    /* access modifiers changed from: private */
    public boolean handleWorkerMessage(Message message) {
        Object obj;
        int i = message.what;
        int i2 = 2;
        if (i == 1) {
            StatusBarNotification statusBarNotification = (StatusBarNotification) message.obj;
            Handler handler = this.mUiHandler;
            if (notificationIsValidForUI(statusBarNotification)) {
                i2 = 1;
            }
            handler.obtainMessage(i2, toKeyPair(statusBarNotification)).sendToTarget();
            return true;
        } else if (i == 2) {
            StatusBarNotification statusBarNotification2 = (StatusBarNotification) message.obj;
            this.mUiHandler.obtainMessage(2, toKeyPair(statusBarNotification2)).sendToTarget();
            NotificationGroup notificationGroup = this.mNotificationGroupMap.get(statusBarNotification2.getGroupKey());
            String key = statusBarNotification2.getKey();
            if (notificationGroup != null) {
                notificationGroup.removeChildKey(key);
                if (notificationGroup.isEmpty()) {
                    if (key.equals(this.mLastKeyDismissedByLauncher)) {
                        cancelNotification(notificationGroup.getGroupSummaryKey());
                    }
                    this.mNotificationGroupMap.remove(statusBarNotification2.getGroupKey());
                }
            }
            if (key.equals(this.mLastKeyDismissedByLauncher)) {
                this.mLastKeyDismissedByLauncher = null;
            }
            return true;
        } else if (i == 3) {
            if (sIsConnected) {
                obj = (List) Arrays.stream(getActiveNotificationsSafely((String[]) null)).filter(new Predicate() {
                    public final boolean test(Object obj) {
                        return NotificationListener.this.notificationIsValidForUI((StatusBarNotification) obj);
                    }
                }).collect(Collectors.toList());
            } else {
                obj = new ArrayList();
            }
            this.mUiHandler.obtainMessage(message.what, obj).sendToTarget();
            return true;
        } else if (i != 4) {
            if (i != 5) {
                return false;
            }
            for (StatusBarNotification updateGroupKeyIfNecessary : getActiveNotificationsSafely(((NotificationListenerService.RankingMap) message.obj).getOrderedKeys())) {
                updateGroupKeyIfNecessary(updateGroupKeyIfNecessary);
            }
            return true;
        } else {
            String str = (String) message.obj;
            this.mLastKeyDismissedByLauncher = str;
            cancelNotification(str);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public boolean handleUiMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            ArraySet<NotificationsChangedListener> arraySet = sNotificationsChangedListeners;
            if (arraySet.size() > 0) {
                Pair pair = (Pair) message.obj;
                Iterator<NotificationsChangedListener> it = arraySet.iterator();
                while (it.hasNext()) {
                    it.next().onNotificationPosted((PackageUserKey) pair.first, (NotificationKeyData) pair.second);
                }
            }
        } else if (i == 2) {
            ArraySet<NotificationsChangedListener> arraySet2 = sNotificationsChangedListeners;
            if (arraySet2.size() > 0) {
                Pair pair2 = (Pair) message.obj;
                Iterator<NotificationsChangedListener> it2 = arraySet2.iterator();
                while (it2.hasNext()) {
                    it2.next().onNotificationRemoved((PackageUserKey) pair2.first, (NotificationKeyData) pair2.second);
                }
            }
        } else if (i == 3) {
            ArraySet<NotificationsChangedListener> arraySet3 = sNotificationsChangedListeners;
            if (arraySet3.size() > 0) {
                Iterator<NotificationsChangedListener> it3 = arraySet3.iterator();
                while (it3.hasNext()) {
                    it3.next().onNotificationFullRefresh((List) message.obj);
                }
            }
        }
        return true;
    }

    private StatusBarNotification[] getActiveNotificationsSafely(String[] strArr) {
        StatusBarNotification[] statusBarNotificationArr;
        try {
            statusBarNotificationArr = getActiveNotifications(strArr);
        } catch (SecurityException unused) {
            Log.e(TAG, "SecurityException: failed to fetch notifications");
            statusBarNotificationArr = null;
        }
        return statusBarNotificationArr == null ? new StatusBarNotification[0] : statusBarNotificationArr;
    }

    public void onListenerConnected() {
        super.onListenerConnected();
        sIsConnected = true;
        this.mSettingsCache = SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this);
        this.mNotificationSettingsChangedListener = new SettingsCache.OnChangeListener() {
            public final void onSettingsChanged(boolean z) {
                NotificationListener.this.onNotificationSettingsChanged(z);
            }
        };
        this.mSettingsCache.register(SettingsCache.NOTIFICATION_BADGING_URI, this.mNotificationSettingsChangedListener);
        onNotificationSettingsChanged(this.mSettingsCache.getValue(SettingsCache.NOTIFICATION_BADGING_URI));
        onNotificationFullRefresh();
    }

    /* access modifiers changed from: private */
    public void onNotificationSettingsChanged(boolean z) {
        if (!z && sIsConnected) {
            requestUnbind();
        }
    }

    private void onNotificationFullRefresh() {
        this.mWorkerHandler.obtainMessage(3).sendToTarget();
    }

    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        sIsConnected = false;
        this.mSettingsCache.unregister(SettingsCache.NOTIFICATION_BADGING_URI, this.mNotificationSettingsChangedListener);
        onNotificationFullRefresh();
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        if (statusBarNotification != null) {
            this.mWorkerHandler.obtainMessage(1, statusBarNotification).sendToTarget();
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        if (statusBarNotification != null) {
            this.mWorkerHandler.obtainMessage(2, statusBarNotification).sendToTarget();
        }
    }

    public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
        this.mWorkerHandler.obtainMessage(5, rankingMap).sendToTarget();
    }

    public void cancelNotificationFromLauncher(String str) {
        this.mWorkerHandler.obtainMessage(4, str).sendToTarget();
    }

    private void updateGroupKeyIfNecessary(StatusBarNotification statusBarNotification) {
        String key = statusBarNotification.getKey();
        String str = this.mNotificationGroupKeyMap.get(key);
        String groupKey = statusBarNotification.getGroupKey();
        if (str == null || !str.equals(groupKey)) {
            this.mNotificationGroupKeyMap.put(key, groupKey);
            if (str != null && this.mNotificationGroupMap.containsKey(str)) {
                NotificationGroup notificationGroup = this.mNotificationGroupMap.get(str);
                notificationGroup.removeChildKey(key);
                if (notificationGroup.isEmpty()) {
                    this.mNotificationGroupMap.remove(str);
                }
            }
        }
        if (statusBarNotification.isGroup() && groupKey != null) {
            NotificationGroup notificationGroup2 = this.mNotificationGroupMap.get(groupKey);
            if (notificationGroup2 == null) {
                notificationGroup2 = new NotificationGroup();
                this.mNotificationGroupMap.put(groupKey, notificationGroup2);
            }
            if ((statusBarNotification.getNotification().flags & 512) != 0) {
                notificationGroup2.setGroupSummaryKey(key);
            } else {
                notificationGroup2.addChildKey(key);
            }
        }
    }

    static /* synthetic */ String[] lambda$getNotificationsForKeys$3(int i) {
        return new String[i];
    }

    public List<StatusBarNotification> getNotificationsForKeys(List<NotificationKeyData> list) {
        return Arrays.asList(getActiveNotificationsSafely((String[]) list.stream().map($$Lambda$NotificationListener$RXoToevJBibcNOhyNfEswztWUGU.INSTANCE).toArray($$Lambda$NotificationListener$8hRjUTqxATp1pgvNe39Tj2joqOc.INSTANCE)));
    }

    /* access modifiers changed from: private */
    public boolean notificationIsValidForUI(StatusBarNotification statusBarNotification) {
        Notification notification = statusBarNotification.getNotification();
        updateGroupKeyIfNecessary(statusBarNotification);
        getCurrentRanking().getRanking(statusBarNotification.getKey(), this.mTempRanking);
        if (!this.mTempRanking.canShowBadge()) {
            return false;
        }
        if (this.mTempRanking.getChannel().getId().equals(NotificationChannelCompat.DEFAULT_CHANNEL_ID) && (notification.flags & 2) != 0) {
            return false;
        }
        boolean z = TextUtils.isEmpty(notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE)) && TextUtils.isEmpty(notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT));
        if (((notification.flags & 512) != 0) || z) {
            return false;
        }
        return true;
    }

    private static Pair<PackageUserKey, NotificationKeyData> toKeyPair(StatusBarNotification statusBarNotification) {
        return Pair.create(PackageUserKey.fromNotification(statusBarNotification), NotificationKeyData.fromNotification(statusBarNotification));
    }
}
