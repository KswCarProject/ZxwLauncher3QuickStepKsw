package com.android.launcher3;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.util.SystemUiController;
import com.android.launcher3.util.ViewCache;
import com.android.launcher3.views.AppLauncher;
import com.android.launcher3.views.ScrimView;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends Activity implements AppLauncher, DeviceProfile.DeviceProfileListenable {
    public static final int ACTIVITY_STATE_DEFERRED_RESUMED = 4;
    public static final int ACTIVITY_STATE_RESUMED = 2;
    public static final int ACTIVITY_STATE_STARTED = 1;
    public static final int ACTIVITY_STATE_TRANSITION_ACTIVE = 64;
    public static final int ACTIVITY_STATE_USER_ACTIVE = 16;
    public static final int ACTIVITY_STATE_USER_WILL_BE_ACTIVE = 32;
    public static final int ACTIVITY_STATE_WINDOW_FOCUSED = 8;
    public static final int INVISIBLE_ALL = 15;
    public static final int INVISIBLE_BY_APP_TRANSITIONS = 2;
    public static final int INVISIBLE_BY_PENDING_FLAGS = 4;
    public static final int INVISIBLE_BY_STATE_HANDLER = 1;
    private static final int INVISIBLE_FLAGS = 7;
    public static final int PENDING_INVISIBLE_BY_WALLPAPER_ANIMATION = 8;
    public static final int STATE_HANDLER_INVISIBILITY_FLAGS = 9;
    private static final String TAG = "BaseActivity";
    private int mActivityFlags;
    private final ArrayList<DeviceProfile.OnDeviceProfileChangeListener> mDPChangeListeners = new ArrayList<>();
    protected DeviceProfile mDeviceProfile;
    private int mForceInvisible;
    private final ArrayList<MultiWindowModeChangedListener> mMultiWindowModeChangedListeners = new ArrayList<>();
    protected StatsLogManager mStatsLogManager;
    protected SystemUiController mSystemUiController;
    private final ViewCache mViewCache = new ViewCache();

    @Retention(RetentionPolicy.SOURCE)
    public @interface ActivityFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface InvisibilityFlags {
    }

    public interface MultiWindowModeChangedListener {
        void onMultiWindowModeChanged(boolean z);
    }

    public ScrimView getScrimView() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void onActivityFlagsChanged(int i) {
    }

    public ViewCache getViewCache() {
        return this.mViewCache;
    }

    public DeviceProfile getDeviceProfile() {
        return this.mDeviceProfile;
    }

    public List<DeviceProfile.OnDeviceProfileChangeListener> getOnDeviceProfileChangeListeners() {
        return this.mDPChangeListeners;
    }

    public StatsLogManager getStatsLogManager() {
        if (this.mStatsLogManager == null) {
            this.mStatsLogManager = StatsLogManager.newInstance(this);
        }
        return this.mStatsLogManager;
    }

    public SystemUiController getSystemUiController() {
        if (this.mSystemUiController == null) {
            this.mSystemUiController = new SystemUiController(getWindow());
        }
        return this.mSystemUiController;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        addActivityFlags(1);
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        addActivityFlags(18);
        removeActivityFlags(32);
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        removeActivityFlags(16);
        super.onUserLeaveHint();
    }

    public void onMultiWindowModeChanged(boolean z, Configuration configuration) {
        super.onMultiWindowModeChanged(z, configuration);
        for (int size = this.mMultiWindowModeChangedListeners.size() - 1; size >= 0; size--) {
            this.mMultiWindowModeChangedListeners.get(size).onMultiWindowModeChanged(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        removeActivityFlags(17);
        this.mForceInvisible = 0;
        super.onStop();
        getSystemUiController().updateUiState(3, 0);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        removeActivityFlags(6);
        super.onPause();
        getSystemUiController().updateUiState(3, 0);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            addActivityFlags(8);
        } else {
            removeActivityFlags(8);
        }
    }

    public boolean isStarted() {
        return (this.mActivityFlags & 1) != 0;
    }

    public boolean hasBeenResumed() {
        return (this.mActivityFlags & 2) != 0;
    }

    public boolean isUserActive() {
        return (this.mActivityFlags & 16) != 0;
    }

    public int getActivityFlags() {
        return this.mActivityFlags;
    }

    /* access modifiers changed from: protected */
    public void addActivityFlags(int i) {
        this.mActivityFlags |= i;
        onActivityFlagsChanged(i);
    }

    /* access modifiers changed from: protected */
    public void removeActivityFlags(int i) {
        this.mActivityFlags &= ~i;
        onActivityFlagsChanged(i);
    }

    public void addMultiWindowModeChangedListener(MultiWindowModeChangedListener multiWindowModeChangedListener) {
        this.mMultiWindowModeChangedListeners.add(multiWindowModeChangedListener);
    }

    public void removeMultiWindowModeChangedListener(MultiWindowModeChangedListener multiWindowModeChangedListener) {
        this.mMultiWindowModeChangedListeners.remove(multiWindowModeChangedListener);
    }

    public void addForceInvisibleFlag(int i) {
        this.mForceInvisible = i | this.mForceInvisible;
    }

    public void clearForceInvisibleFlag(int i) {
        this.mForceInvisible = (~i) & this.mForceInvisible;
    }

    public boolean isForceInvisible() {
        return hasSomeInvisibleFlag(7);
    }

    public boolean hasSomeInvisibleFlag(int i) {
        return (i & this.mForceInvisible) != 0;
    }

    /* access modifiers changed from: protected */
    public void dumpMisc(String str, PrintWriter printWriter) {
        printWriter.println(str + "deviceProfile isTransposed=" + getDeviceProfile().isVerticalBarLayout());
        printWriter.println(str + "orientation=" + getResources().getConfiguration().orientation);
        printWriter.println(str + "mSystemUiController: " + this.mSystemUiController);
        printWriter.println(str + "mActivityFlags: " + this.mActivityFlags);
        printWriter.println(str + "mForceInvisible: " + this.mForceInvisible);
    }

    public static <T extends BaseActivity> T fromContext(Context context) {
        if (context instanceof BaseActivity) {
            return (BaseActivity) context;
        }
        if (context instanceof ContextWrapper) {
            return fromContext(((ContextWrapper) context).getBaseContext());
        }
        throw new IllegalArgumentException("Cannot find BaseActivity in parent tree");
    }
}
