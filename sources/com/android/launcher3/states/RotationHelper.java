package com.android.launcher3.states;

import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Utilities;

public class RotationHelper implements SharedPreferences.OnSharedPreferenceChangeListener, DeviceProfile.OnDeviceProfileChangeListener {
    public static final String ALLOW_ROTATION_PREFERENCE_KEY = "pref_allowRotation";
    public static final int REQUEST_LOCK = 2;
    public static final int REQUEST_NONE = 0;
    public static final int REQUEST_ROTATE = 1;
    private static final String TAG = "RotationHelper";
    private BaseActivity mActivity;
    private int mCurrentStateRequest = 0;
    private int mCurrentTransitionRequest = 0;
    private boolean mDestroyed;
    private boolean mForceAllowRotationForTesting;
    private boolean mHomeRotationEnabled;
    private boolean mIgnoreAutoRotateSettings;
    private boolean mInitialized;
    private int mLastActivityFlags = -999;
    private SharedPreferences mSharedPrefs = null;
    private int mStateHandlerRequest = 0;

    public static int deltaRotation(int i, int i2) {
        int i3 = i2 - i;
        return i3 < 0 ? i3 + 4 : i3;
    }

    public static boolean getAllowRotationDefaultValue(DeviceProfile deviceProfile) {
        return Utilities.dpiFromPx((float) Math.min(deviceProfile.widthPx, deviceProfile.heightPx), DisplayMetrics.DENSITY_DEVICE_STABLE) >= 600.0f;
    }

    public RotationHelper(BaseActivity baseActivity) {
        this.mActivity = baseActivity;
    }

    private void setIgnoreAutoRotateSettings(boolean z) {
        this.mIgnoreAutoRotateSettings = z;
        if (!z) {
            if (this.mSharedPrefs == null) {
                SharedPreferences prefs = Utilities.getPrefs(this.mActivity);
                this.mSharedPrefs = prefs;
                prefs.registerOnSharedPreferenceChangeListener(this);
            }
            this.mHomeRotationEnabled = this.mSharedPrefs.getBoolean(ALLOW_ROTATION_PREFERENCE_KEY, getAllowRotationDefaultValue(this.mActivity.getDeviceProfile()));
            return;
        }
        SharedPreferences sharedPreferences = this.mSharedPrefs;
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            this.mSharedPrefs = null;
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (!this.mDestroyed && !this.mIgnoreAutoRotateSettings) {
            boolean z = this.mHomeRotationEnabled;
            boolean z2 = this.mSharedPrefs.getBoolean(ALLOW_ROTATION_PREFERENCE_KEY, getAllowRotationDefaultValue(this.mActivity.getDeviceProfile()));
            this.mHomeRotationEnabled = z2;
            if (z2 != z) {
                notifyChange();
            }
        }
    }

    public void onDeviceProfileChanged(DeviceProfile deviceProfile) {
        boolean z = deviceProfile.isTablet;
        if (this.mIgnoreAutoRotateSettings != z) {
            setIgnoreAutoRotateSettings(z);
            notifyChange();
        }
    }

    public void setStateHandlerRequest(int i) {
        if (this.mStateHandlerRequest != i) {
            this.mStateHandlerRequest = i;
            notifyChange();
        }
    }

    public void setCurrentTransitionRequest(int i) {
        if (this.mCurrentTransitionRequest != i) {
            this.mCurrentTransitionRequest = i;
            notifyChange();
        }
    }

    public void setCurrentStateRequest(int i) {
        if (this.mCurrentStateRequest != i) {
            this.mCurrentStateRequest = i;
            notifyChange();
        }
    }

    public void forceAllowRotationForTesting(boolean z) {
        this.mForceAllowRotationForTesting = z;
        notifyChange();
    }

    public void initialize() {
        if (!this.mInitialized) {
            this.mInitialized = true;
            setIgnoreAutoRotateSettings(this.mActivity.getDeviceProfile().isTablet);
            this.mActivity.addOnDeviceProfileChangeListener(this);
            notifyChange();
        }
    }

    public void destroy() {
        if (!this.mDestroyed) {
            this.mDestroyed = true;
            this.mActivity.removeOnDeviceProfileChangeListener(this);
            this.mActivity = null;
            SharedPreferences sharedPreferences = this.mSharedPrefs;
            if (sharedPreferences != null) {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0011, code lost:
        if (r0 == 2) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0018, code lost:
        if (r0 == 2) goto L_0x0033;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void notifyChange() {
        /*
            r4 = this;
            boolean r0 = r4.mInitialized
            if (r0 == 0) goto L_0x003e
            boolean r0 = r4.mDestroyed
            if (r0 == 0) goto L_0x0009
            goto L_0x003e
        L_0x0009:
            int r0 = r4.mStateHandlerRequest
            r1 = 14
            r2 = -1
            r3 = 2
            if (r0 == 0) goto L_0x0014
            if (r0 != r3) goto L_0x0032
            goto L_0x0033
        L_0x0014:
            int r0 = r4.mCurrentTransitionRequest
            if (r0 == 0) goto L_0x001b
            if (r0 != r3) goto L_0x0032
            goto L_0x0033
        L_0x001b:
            int r0 = r4.mCurrentStateRequest
            if (r0 != r3) goto L_0x0020
            goto L_0x0033
        L_0x0020:
            boolean r1 = r4.mIgnoreAutoRotateSettings
            if (r1 != 0) goto L_0x0032
            r1 = 1
            if (r0 == r1) goto L_0x0032
            boolean r0 = r4.mHomeRotationEnabled
            if (r0 != 0) goto L_0x0032
            boolean r0 = r4.mForceAllowRotationForTesting
            if (r0 == 0) goto L_0x0030
            goto L_0x0032
        L_0x0030:
            r1 = 5
            goto L_0x0033
        L_0x0032:
            r1 = r2
        L_0x0033:
            int r0 = r4.mLastActivityFlags
            if (r1 == r0) goto L_0x003e
            r4.mLastActivityFlags = r1
            com.android.launcher3.BaseActivity r0 = r4.mActivity
            com.android.launcher3.util.UiThreadHelper.setOrientationAsync(r0, r1)
        L_0x003e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.states.RotationHelper.notifyChange():void");
    }

    public String toString() {
        return String.format("[mStateHandlerRequest=%d, mCurrentStateRequest=%d, mLastActivityFlags=%d, mIgnoreAutoRotateSettings=%b, mHomeRotationEnabled=%b, mForceAllowRotationForTesting=%b]", new Object[]{Integer.valueOf(this.mStateHandlerRequest), Integer.valueOf(this.mCurrentStateRequest), Integer.valueOf(this.mLastActivityFlags), Boolean.valueOf(this.mIgnoreAutoRotateSettings), Boolean.valueOf(this.mHomeRotationEnabled), Boolean.valueOf(this.mForceAllowRotationForTesting)});
    }
}
