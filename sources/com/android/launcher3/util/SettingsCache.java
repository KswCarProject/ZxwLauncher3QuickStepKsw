package com.android.launcher3.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SettingsCache extends ContentObserver implements SafeCloseable {
    public static MainThreadInitializedObject<SettingsCache> INSTANCE = new MainThreadInitializedObject<>($$Lambda$SettingsCache$Glvcvksn3jYYaZN0jclBLZlpzro.INSTANCE);
    public static final Uri NOTIFICATION_BADGING_URI = Settings.Secure.getUriFor("notification_badging");
    public static final String ONE_HANDED_ENABLED = "one_handed_mode_enabled";
    public static final String ONE_HANDED_SWIPE_BOTTOM_TO_NOTIFICATION_ENABLED = "swipe_bottom_to_notification_enabled";
    public static final Uri ROTATION_SETTING_URI = Settings.System.getUriFor("accelerometer_rotation");
    private static final String SYSTEM_URI_PREFIX = Settings.System.CONTENT_URI.toString();
    private Map<Uri, Boolean> mKeyCache = new ConcurrentHashMap();
    private final Map<Uri, CopyOnWriteArrayList<OnChangeListener>> mListenerMap = new HashMap();
    protected final ContentResolver mResolver;

    public interface OnChangeListener {
        void onSettingsChanged(boolean z);
    }

    public static /* synthetic */ SettingsCache lambda$Glvcvksn3jYYaZN0jclBLZlpzro(Context context) {
        return new SettingsCache(context);
    }

    private SettingsCache(Context context) {
        super(new Handler());
        this.mResolver = context.getContentResolver();
    }

    public void close() {
        this.mResolver.unregisterContentObserver(this);
    }

    public void onChange(boolean z, Uri uri) {
        boolean updateValue = updateValue(uri, 1);
        if (this.mListenerMap.containsKey(uri)) {
            Iterator it = this.mListenerMap.get(uri).iterator();
            while (it.hasNext()) {
                ((OnChangeListener) it.next()).onSettingsChanged(updateValue);
            }
        }
    }

    public boolean getValue(Uri uri) {
        return getValue(uri, 1);
    }

    public boolean getValue(Uri uri, int i) {
        if (this.mKeyCache.containsKey(uri)) {
            return this.mKeyCache.get(uri).booleanValue();
        }
        return updateValue(uri, i);
    }

    public void register(Uri uri, OnChangeListener onChangeListener) {
        if (this.mListenerMap.containsKey(uri)) {
            this.mListenerMap.get(uri).add(onChangeListener);
            return;
        }
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        copyOnWriteArrayList.add(onChangeListener);
        this.mListenerMap.put(uri, copyOnWriteArrayList);
        this.mResolver.registerContentObserver(uri, false, this);
    }

    private boolean updateValue(Uri uri, int i) {
        String lastPathSegment = uri.getLastPathSegment();
        boolean z = false;
        if (!uri.toString().startsWith(SYSTEM_URI_PREFIX) ? Settings.Secure.getInt(this.mResolver, lastPathSegment, i) == 1 : Settings.System.getInt(this.mResolver, lastPathSegment, i) == 1) {
            z = true;
        }
        this.mKeyCache.put(uri, Boolean.valueOf(z));
        return z;
    }

    public void unregister(Uri uri, OnChangeListener onChangeListener) {
        List list = this.mListenerMap.get(uri);
        if (list.contains(onChangeListener)) {
            list.remove(onChangeListener);
            if (list.isEmpty()) {
                this.mListenerMap.remove(uri);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setKeyCache(Map<Uri, Boolean> map) {
        this.mKeyCache = map;
    }
}
