package com.android.quickstep.logging;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Xml;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.DeviceGridState;
import com.android.launcher3.model.QuickstepModelDelegate;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.SettingsCache;
import com.android.launcher3.util.Themes;
import com.android.quickstep.logging.SettingsChangeLogger;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParserException;

public class SettingsChangeLogger implements DisplayController.DisplayInfoChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String BOOLEAN_PREF = "SwitchPreference";
    public static MainThreadInitializedObject<SettingsChangeLogger> INSTANCE = new MainThreadInitializedObject<>($$Lambda$SettingsChangeLogger$VxhAIejPU8p0nkI8yr6FLMiGkeA.INSTANCE);
    private static final String ROOT_TAG = "androidx.preference.PreferenceScreen";
    private static final String TAG = "SettingsChangeLogger";
    private final Context mContext;
    private StatsLogManager.LauncherEvent mHomeScreenSuggestionEvent;
    private final ArrayMap<String, LoggablePref> mLoggablePrefs;
    private DisplayController.NavigationMode mNavMode;
    private StatsLogManager.LauncherEvent mNotificationDotsEvent;
    private final StatsLogManager mStatsLogManager;

    public static /* synthetic */ SettingsChangeLogger lambda$VxhAIejPU8p0nkI8yr6FLMiGkeA(Context context) {
        return new SettingsChangeLogger(context);
    }

    private SettingsChangeLogger(Context context) {
        this.mContext = context;
        this.mStatsLogManager = StatsLogManager.newInstance(context);
        this.mLoggablePrefs = loadPrefKeys(context);
        DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).addChangeListener(this);
        this.mNavMode = DisplayController.getNavigationMode(context);
        Utilities.getPrefs(context).registerOnSharedPreferenceChangeListener(this);
        Utilities.getDevicePrefs(context).registerOnSharedPreferenceChangeListener(this);
        SettingsCache settingsCache = SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        settingsCache.register(SettingsCache.NOTIFICATION_BADGING_URI, new SettingsCache.OnChangeListener() {
            public final void onSettingsChanged(boolean z) {
                SettingsChangeLogger.this.onNotificationDotsChanged(z);
            }
        });
        onNotificationDotsChanged(settingsCache.getValue(SettingsCache.NOTIFICATION_BADGING_URI));
    }

    private static ArrayMap<String, LoggablePref> loadPrefKeys(Context context) {
        XmlResourceParser xml = context.getResources().getXml(R.xml.launcher_preferences);
        ArrayMap<String, LoggablePref> arrayMap = new ArrayMap<>();
        try {
            AutoInstallsLayout.beginDocument(xml, ROOT_TAG);
            int depth = xml.getDepth();
            while (true) {
                int next = xml.next();
                if ((next == 3 && xml.getDepth() <= depth) || next == 1) {
                    break;
                } else if (next == 2) {
                    if (BOOLEAN_PREF.equals(xml.getName())) {
                        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xml), R.styleable.LoggablePref);
                        String string = obtainStyledAttributes.getString(0);
                        LoggablePref loggablePref = new LoggablePref();
                        loggablePref.defaultValue = obtainStyledAttributes.getBoolean(1, true);
                        loggablePref.eventIdOn = obtainStyledAttributes.getInt(3, 0);
                        loggablePref.eventIdOff = obtainStyledAttributes.getInt(2, 0);
                        if (loggablePref.eventIdOff > 0 && loggablePref.eventIdOn > 0) {
                            arrayMap.put(string, loggablePref);
                        }
                    }
                }
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Error parsing preference xml", e);
        }
        return arrayMap;
    }

    /* access modifiers changed from: private */
    public void onNotificationDotsChanged(boolean z) {
        StatsLogManager.LauncherEvent launcherEvent;
        if (z) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_NOTIFICATION_DOT_ENABLED;
        } else {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_NOTIFICATION_DOT_DISABLED;
        }
        StatsLogManager.LauncherEvent launcherEvent2 = this.mNotificationDotsEvent;
        if (!(launcherEvent2 == null || launcherEvent2 == launcherEvent)) {
            this.mStatsLogManager.logger().log(this.mNotificationDotsEvent);
        }
        this.mNotificationDotsEvent = launcherEvent;
    }

    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int i) {
        if ((i & 16) != 0) {
            this.mNavMode = info.navigationMode;
            this.mStatsLogManager.logger().log(this.mNavMode.launcherEvent);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        StatsLogManager.LauncherEvent launcherEvent;
        if (QuickstepModelDelegate.LAST_PREDICTION_ENABLED_STATE.equals(str) || DeviceGridState.KEY_WORKSPACE_SIZE.equals(str) || Themes.KEY_THEMED_ICONS.equals(str) || this.mLoggablePrefs.containsKey(str)) {
            if (Utilities.getDevicePrefs(this.mContext).getBoolean(QuickstepModelDelegate.LAST_PREDICTION_ENABLED_STATE, true)) {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_HOME_SCREEN_SUGGESTIONS_ENABLED;
            } else {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_HOME_SCREEN_SUGGESTIONS_DISABLED;
            }
            this.mHomeScreenSuggestionEvent = launcherEvent;
            this.mStatsLogManager.logger().log(this.mHomeScreenSuggestionEvent);
        }
    }

    public void logSnapshot(InstanceId instanceId) {
        StatsLogManager.LauncherEvent launcherEvent;
        StatsLogManager.StatsLogger withInstanceId = this.mStatsLogManager.logger().withInstanceId(instanceId);
        Optional ofNullable = Optional.ofNullable(this.mNotificationDotsEvent);
        Objects.requireNonNull(withInstanceId);
        ofNullable.ifPresent(new Consumer() {
            public final void accept(Object obj) {
                StatsLogManager.StatsLogger.this.log((StatsLogManager.LauncherEvent) obj);
            }
        });
        Optional map = Optional.ofNullable(this.mNavMode).map($$Lambda$SettingsChangeLogger$xfMz7Uo0dNHmRn0VmMeEE4R9VVE.INSTANCE);
        Objects.requireNonNull(withInstanceId);
        map.ifPresent(new Consumer() {
            public final void accept(Object obj) {
                StatsLogManager.StatsLogger.this.log((StatsLogManager.LauncherEvent) obj);
            }
        });
        Optional ofNullable2 = Optional.ofNullable(this.mHomeScreenSuggestionEvent);
        Objects.requireNonNull(withInstanceId);
        ofNullable2.ifPresent(new Consumer() {
            public final void accept(Object obj) {
                StatsLogManager.StatsLogger.this.log((StatsLogManager.LauncherEvent) obj);
            }
        });
        Optional ofNullable3 = Optional.ofNullable(new DeviceGridState(this.mContext).getWorkspaceSizeEvent());
        Objects.requireNonNull(withInstanceId);
        ofNullable3.ifPresent(new Consumer() {
            public final void accept(Object obj) {
                StatsLogManager.StatsLogger.this.log((StatsLogManager.LauncherEvent) obj);
            }
        });
        SharedPreferences prefs = Utilities.getPrefs(this.mContext);
        if (FeatureFlags.ENABLE_THEMED_ICONS.get()) {
            if (prefs.getBoolean(Themes.KEY_THEMED_ICONS, false)) {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_THEMED_ICON_ENABLED;
            } else {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_THEMED_ICON_DISABLED;
            }
            withInstanceId.log(launcherEvent);
        }
        this.mLoggablePrefs.forEach(new BiConsumer(prefs) {
            public final /* synthetic */ SharedPreferences f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj, Object obj2) {
                StatsLogManager.StatsLogger.this.log(new StatsLogManager.EventEnum(this.f$1, (String) obj, (SettingsChangeLogger.LoggablePref) obj2) {
                    public final /* synthetic */ SharedPreferences f$0;
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ SettingsChangeLogger.LoggablePref f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final int getId() {
                        return SettingsChangeLogger.lambda$logSnapshot$1(this.f$0, this.f$1, this.f$2);
                    }
                });
            }
        });
    }

    static /* synthetic */ int lambda$logSnapshot$1(SharedPreferences sharedPreferences, String str, LoggablePref loggablePref) {
        return sharedPreferences.getBoolean(str, loggablePref.defaultValue) ? loggablePref.eventIdOn : loggablePref.eventIdOff;
    }

    private static class LoggablePref {
        public boolean defaultValue;
        public int eventIdOff;
        public int eventIdOn;

        private LoggablePref() {
        }
    }
}
