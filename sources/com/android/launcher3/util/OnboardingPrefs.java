package com.android.launcher3.util;

import android.content.SharedPreferences;
import android.util.ArrayMap;
import com.android.launcher3.views.ActivityContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;

public class OnboardingPrefs<T extends ActivityContext> {
    public static final String ALL_APPS_VISITED_COUNT = "launcher.all_apps_visited_count";
    public static final Map<String, String[]> ALL_PREF_KEYS = OnboardingPrefs$$ExternalSynthetic1.m0(new Map.Entry[]{new AbstractMap.SimpleEntry("All Apps Bounce", new String[]{HOME_BOUNCE_SEEN, HOME_BOUNCE_COUNT}), new AbstractMap.SimpleEntry("Hybrid Hotseat Education", new String[]{HOTSEAT_DISCOVERY_TIP_COUNT, HOTSEAT_LONGPRESS_TIP_SEEN}), new AbstractMap.SimpleEntry("Search Education", new String[]{SEARCH_KEYBOARD_EDU_SEEN, SEARCH_SNACKBAR_COUNT, SEARCH_ONBOARDING_COUNT}), new AbstractMap.SimpleEntry("Taskbar Education", new String[]{TASKBAR_EDU_SEEN}), new AbstractMap.SimpleEntry("All Apps Visited Count", new String[]{ALL_APPS_VISITED_COUNT})});
    public static final String HOME_BOUNCE_COUNT = "launcher.home_bounce_count";
    public static final String HOME_BOUNCE_SEEN = "launcher.apps_view_shown";
    public static final String HOTSEAT_DISCOVERY_TIP_COUNT = "launcher.hotseat_discovery_tip_count";
    public static final String HOTSEAT_LONGPRESS_TIP_SEEN = "launcher.hotseat_longpress_tip_seen";
    private static final Map<String, Integer> MAX_COUNTS;
    public static final String SEARCH_KEYBOARD_EDU_SEEN = "launcher.search_edu_seen";
    public static final String SEARCH_ONBOARDING_COUNT = "launcher.search_onboarding_count";
    public static final String SEARCH_SNACKBAR_COUNT = "launcher.keyboard_snackbar_count";
    public static final String TASKBAR_EDU_SEEN = "launcher.taskbar_edu_seen";
    /* access modifiers changed from: protected */
    public final T mLauncher;
    /* access modifiers changed from: protected */
    public final SharedPreferences mSharedPrefs;

    @Retention(RetentionPolicy.SOURCE)
    public @interface EventBoolKey {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface EventCountKey {
    }

    static {
        ArrayMap arrayMap = new ArrayMap(5);
        arrayMap.put(HOME_BOUNCE_COUNT, 3);
        arrayMap.put(HOTSEAT_DISCOVERY_TIP_COUNT, 5);
        arrayMap.put(SEARCH_SNACKBAR_COUNT, 3);
        arrayMap.put(SEARCH_ONBOARDING_COUNT, 3);
        arrayMap.put(ALL_APPS_VISITED_COUNT, 20);
        MAX_COUNTS = Collections.unmodifiableMap(arrayMap);
    }

    public OnboardingPrefs(T t, SharedPreferences sharedPreferences) {
        this.mLauncher = t;
        this.mSharedPrefs = sharedPreferences;
    }

    public int getCount(String str) {
        return this.mSharedPrefs.getInt(str, 0);
    }

    public boolean hasReachedMaxCount(String str) {
        return hasReachedMaxCount(getCount(str), str);
    }

    private boolean hasReachedMaxCount(int i, String str) {
        return i >= MAX_COUNTS.get(str).intValue();
    }

    public boolean getBoolean(String str) {
        return this.mSharedPrefs.getBoolean(str, false);
    }

    public void markChecked(String str) {
        this.mSharedPrefs.edit().putBoolean(str, true).apply();
    }

    public boolean incrementEventCount(String str) {
        int count = getCount(str);
        if (hasReachedMaxCount(count, str)) {
            return true;
        }
        int i = count + 1;
        this.mSharedPrefs.edit().putInt(str, i).apply();
        return hasReachedMaxCount(i, str);
    }

    public boolean setEventCount(int i, String str) {
        this.mSharedPrefs.edit().putInt(str, i).apply();
        return hasReachedMaxCount(i, str);
    }
}
