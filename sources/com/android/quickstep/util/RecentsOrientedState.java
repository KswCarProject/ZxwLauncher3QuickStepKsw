package com.android.quickstep.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.SettingsCache;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.views.TaskView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.IntConsumer;

public class RecentsOrientedState implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final boolean DEBUG = false;
    private static final int FLAG_HOME_ROTATION_ALLOWED_IN_PREFS = 4;
    private static final int FLAG_HOME_ROTATION_FORCE_ENABLED_FOR_TESTING = 128;
    private static final int FLAG_IGNORE_ALLOW_HOME_ROTATION_PREF = 512;
    private static final int FLAG_MULTIPLE_ORIENTATION_SUPPORTED_BY_ACTIVITY = 1;
    private static final int FLAG_MULTIPLE_ORIENTATION_SUPPORTED_BY_DENSITY = 2;
    private static final int FLAG_MULTIWINDOW_ROTATION_ALLOWED = 16;
    private static final int FLAG_ROTATION_WATCHER_ENABLED = 64;
    private static final int FLAG_ROTATION_WATCHER_SUPPORTED = 32;
    private static final int FLAG_SWIPE_UP_NOT_RUNNING = 256;
    private static final int FLAG_SYSTEM_ROTATION_ALLOWED = 8;
    private static final int MASK_MULTIPLE_ORIENTATION_SUPPORTED_BY_DEVICE = 3;
    private static final String TAG = "RecentsOrientedState";
    private static final int VALUE_ROTATION_WATCHER_ENABLED = 363;
    private final Context mContext;
    private int mDisplayRotation = 0;
    private int mFlags;
    private boolean mListenersInitialized = false;
    private PagedOrientationHandler mOrientationHandler = PagedOrientationHandler.PORTRAIT;
    private final OrientationEventListener mOrientationListener;
    /* access modifiers changed from: private */
    public int mPreviousRotation = 0;
    private int mRecentsActivityRotation = 0;
    private int mRecentsRotation = -1;
    private final SettingsCache.OnChangeListener mRotationChangeListener = new SettingsCache.OnChangeListener() {
        public final void onSettingsChanged(boolean z) {
            RecentsOrientedState.this.lambda$new$0$RecentsOrientedState(z);
        }
    };
    private final SettingsCache mSettingsCache;
    private final SharedPreferences mSharedPrefs;
    private int mStateId = 0;
    private final Matrix mTmpMatrix = new Matrix();
    private int mTouchRotation = 0;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SurfaceRotation {
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x0097 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getRotationForUserDegreesRotated(float r8, int r9) {
        /*
            r0 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r0 != 0) goto L_0x0007
            return r9
        L_0x0007:
            r0 = 70
            r1 = 1127481344(0x43340000, float:180.0)
            r2 = 3
            r3 = 1
            if (r9 == 0) goto L_0x0081
            r4 = 0
            r5 = 340(0x154, float:4.76E-43)
            r6 = 1135869952(0x43b40000, float:360.0)
            r7 = 2
            if (r9 == r3) goto L_0x0055
            r0 = 250(0xfa, float:3.5E-43)
            if (r9 == r7) goto L_0x0047
            if (r9 == r2) goto L_0x001f
            goto L_0x0097
        L_0x001f:
            r2 = 20
            float r2 = (float) r2
            int r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x0046
            float r2 = (float) r5
            int r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x0030
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 >= 0) goto L_0x0030
            goto L_0x0046
        L_0x0030:
            r2 = 160(0xa0, float:2.24E-43)
            float r2 = (float) r2
            int r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x003c
            int r1 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x003c
            return r7
        L_0x003c:
            float r0 = (float) r0
            int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0097
            int r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r8 >= 0) goto L_0x0097
            return r3
        L_0x0046:
            return r4
        L_0x0047:
            r1 = 110(0x6e, float:1.54E-43)
            float r1 = (float) r1
            int r1 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x004f
            return r2
        L_0x004f:
            float r0 = (float) r0
            int r8 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r8 <= 0) goto L_0x0097
            return r3
        L_0x0055:
            r3 = 200(0xc8, float:2.8E-43)
            float r3 = (float) r3
            int r3 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0063
            r3 = 1119092736(0x42b40000, float:90.0)
            int r3 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0063
            return r7
        L_0x0063:
            float r3 = (float) r5
            int r3 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x006c
            int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r3 < 0) goto L_0x0076
        L_0x006c:
            r3 = 0
            int r3 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x0077
            float r3 = (float) r0
            int r3 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0077
        L_0x0076:
            return r4
        L_0x0077:
            float r0 = (float) r0
            int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0097
            int r8 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r8 >= 0) goto L_0x0097
            return r2
        L_0x0081:
            int r4 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x008d
            r4 = 290(0x122, float:4.06E-43)
            float r4 = (float) r4
            int r4 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x008d
            return r3
        L_0x008d:
            int r1 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x0097
            float r0 = (float) r0
            int r8 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r8 <= 0) goto L_0x0097
            return r2
        L_0x0097:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.util.RecentsOrientedState.getRotationForUserDegreesRotated(float, int):int");
    }

    public /* synthetic */ void lambda$new$0$RecentsOrientedState(boolean z) {
        updateAutoRotateSetting();
    }

    public RecentsOrientedState(Context context, BaseActivityInterface baseActivityInterface, final IntConsumer intConsumer) {
        this.mContext = context;
        this.mSharedPrefs = Utilities.getPrefs(context);
        this.mOrientationListener = new OrientationEventListener(context) {
            public void onOrientationChanged(int i) {
                int rotationForUserDegreesRotated = RecentsOrientedState.getRotationForUserDegreesRotated((float) i, RecentsOrientedState.this.mPreviousRotation);
                if (rotationForUserDegreesRotated != RecentsOrientedState.this.mPreviousRotation) {
                    int unused = RecentsOrientedState.this.mPreviousRotation = rotationForUserDegreesRotated;
                    intConsumer.accept(rotationForUserDegreesRotated);
                }
            }
        };
        boolean z = baseActivityInterface.rotationSupportedByActivity;
        this.mFlags = z ? 1 : 0;
        this.mFlags = z | true ? 1 : 0;
        this.mSettingsCache = SettingsCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        initFlags();
    }

    public void setDeviceProfile(DeviceProfile deviceProfile) {
        boolean isMultipleOrientationSupportedByDevice;
        boolean isMultipleOrientationSupportedByDevice2 = isMultipleOrientationSupportedByDevice();
        setFlag(2, !deviceProfile.isTablet);
        if (this.mListenersInitialized && (isMultipleOrientationSupportedByDevice = isMultipleOrientationSupportedByDevice()) != isMultipleOrientationSupportedByDevice2) {
            if (isMultipleOrientationSupportedByDevice) {
                initMultipleOrientationListeners();
            } else {
                destroyMultipleOrientationListeners();
            }
        }
    }

    public boolean setRecentsRotation(int i) {
        this.mRecentsRotation = i;
        return updateHandler();
    }

    public void setMultiWindowMode(boolean z) {
        setFlag(16, z);
    }

    public boolean setGestureActive(boolean z) {
        return setFlag(256, !z);
    }

    public boolean update(int i, int i2) {
        this.mDisplayRotation = i2;
        this.mTouchRotation = i;
        this.mPreviousRotation = i;
        return updateHandler();
    }

    private boolean updateHandler() {
        int inferRecentsActivityRotation = inferRecentsActivityRotation(this.mDisplayRotation);
        this.mRecentsActivityRotation = inferRecentsActivityRotation;
        if (inferRecentsActivityRotation == this.mTouchRotation || (isRecentsActivityRotationAllowed() && (this.mFlags & 256) != 0)) {
            this.mOrientationHandler = PagedOrientationHandler.PORTRAIT;
        } else {
            int i = this.mTouchRotation;
            if (i == 1) {
                this.mOrientationHandler = PagedOrientationHandler.LANDSCAPE;
            } else if (i == 3) {
                this.mOrientationHandler = PagedOrientationHandler.SEASCAPE;
            } else {
                this.mOrientationHandler = PagedOrientationHandler.PORTRAIT;
            }
        }
        int i2 = this.mStateId;
        int i3 = ((((this.mFlags << 2) | this.mDisplayRotation) << 2) | this.mTouchRotation) << 3;
        int i4 = this.mRecentsRotation;
        if (i4 < 0) {
            i4 = 7;
        }
        int i5 = i3 | i4;
        this.mStateId = i5;
        if (i5 != i2) {
            return true;
        }
        return false;
    }

    private int inferRecentsActivityRotation(int i) {
        if (!isRecentsActivityRotationAllowed()) {
            return 0;
        }
        int i2 = this.mRecentsRotation;
        return i2 < 0 ? i : i2;
    }

    private boolean setFlag(int i, boolean z) {
        boolean z2 = true;
        boolean z3 = !TestProtocol.sDisableSensorRotation && (this.mFlags & VALUE_ROTATION_WATCHER_ENABLED) == VALUE_ROTATION_WATCHER_ENABLED && !isRecentsActivityRotationAllowed();
        if (z) {
            this.mFlags = i | this.mFlags;
        } else {
            this.mFlags = (~i) & this.mFlags;
        }
        if (TestProtocol.sDisableSensorRotation || (this.mFlags & VALUE_ROTATION_WATCHER_ENABLED) != VALUE_ROTATION_WATCHER_ENABLED || isRecentsActivityRotationAllowed()) {
            z2 = false;
        }
        if (z3 != z2) {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(z2) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    RecentsOrientedState.this.lambda$setFlag$1$RecentsOrientedState(this.f$1);
                }
            });
        }
        return updateHandler();
    }

    public /* synthetic */ void lambda$setFlag$1$RecentsOrientedState(boolean z) {
        if (z) {
            this.mOrientationListener.enable();
        } else {
            this.mOrientationListener.disable();
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (RotationHelper.ALLOW_ROTATION_PREFERENCE_KEY.equals(str)) {
            updateHomeRotationSetting();
        }
    }

    private void updateAutoRotateSetting() {
        setFlag(8, this.mSettingsCache.getValue(SettingsCache.ROTATION_SETTING_URI, 1));
    }

    private void updateHomeRotationSetting() {
        boolean z = this.mSharedPrefs.getBoolean(RotationHelper.ALLOW_ROTATION_PREFERENCE_KEY, false);
        setFlag(4, z);
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).setHomeRotationEnabled(z);
    }

    private void initFlags() {
        setFlag(32, this.mOrientationListener.canDetectOrientation());
        updateAutoRotateSetting();
        updateHomeRotationSetting();
    }

    private void initMultipleOrientationListeners() {
        this.mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
        this.mSettingsCache.register(SettingsCache.ROTATION_SETTING_URI, this.mRotationChangeListener);
        updateAutoRotateSetting();
    }

    private void destroyMultipleOrientationListeners() {
        this.mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
        this.mSettingsCache.unregister(SettingsCache.ROTATION_SETTING_URI, this.mRotationChangeListener);
    }

    public void initListeners() {
        this.mListenersInitialized = true;
        if (isMultipleOrientationSupportedByDevice()) {
            initMultipleOrientationListeners();
        }
        initFlags();
    }

    public void destroyListeners() {
        this.mListenersInitialized = false;
        if (isMultipleOrientationSupportedByDevice()) {
            destroyMultipleOrientationListeners();
        }
        setRotationWatcherEnabled(false);
    }

    public void forceAllowRotationForTesting(boolean z) {
        setFlag(128, z);
    }

    public int getDisplayRotation() {
        if (TaskAnimationManager.SHELL_TRANSITIONS_ROTATION) {
            return this.mRecentsActivityRotation;
        }
        return this.mDisplayRotation;
    }

    public int getTouchRotation() {
        return this.mTouchRotation;
    }

    public int getRecentsActivityRotation() {
        return this.mRecentsActivityRotation;
    }

    public int getStateId() {
        return this.mStateId;
    }

    public boolean isMultipleOrientationSupportedByDevice() {
        return (this.mFlags & 3) == 3;
    }

    public void ignoreAllowHomeRotationPreference() {
        setFlag(512, true);
    }

    public boolean isRecentsActivityRotationAllowed() {
        int i = this.mFlags;
        return ((i & 3) == 3 && (i & 660) == 0) ? false : true;
    }

    public void setRotationWatcherEnabled(boolean z) {
        setFlag(64, z);
    }

    public float getFullScreenScaleAndPivot(Rect rect, DeviceProfile deviceProfile, PointF pointF) {
        Rect insets = deviceProfile.getInsets();
        float f = (float) deviceProfile.widthPx;
        float f2 = (float) deviceProfile.heightPx;
        if (TaskView.clipLeft(deviceProfile)) {
            f -= (float) insets.left;
        }
        if (TaskView.clipRight(deviceProfile)) {
            f -= (float) insets.right;
        }
        if (TaskView.clipTop(deviceProfile)) {
            f2 -= (float) insets.top;
        }
        if (TaskView.clipBottom(deviceProfile)) {
            f2 -= (float) insets.bottom;
        }
        BaseActivityInterface.getTaskDimension(this.mContext, deviceProfile, pointF);
        float min = Math.min(pointF.x / ((float) rect.width()), pointF.y / ((float) rect.height()));
        if (f > 0.0f) {
            min = (min * ((float) deviceProfile.widthPx)) / f;
        }
        if (min == 1.0f) {
            pointF.set(f / 2.0f, f2 / 2.0f);
        } else if (deviceProfile.isMultiWindowMode) {
            float f3 = 1.0f / (min - 1.0f);
            pointF.set(((((float) rect.right) * min) - f) * f3, ((((float) rect.bottom) * min) - f2) * f3);
        } else {
            float f4 = min / (min - 1.0f);
            pointF.set(((float) rect.left) * f4, ((float) rect.top) * f4);
        }
        return min;
    }

    public PagedOrientationHandler getOrientationHandler() {
        return this.mOrientationHandler;
    }

    public void flipVertical(MotionEvent motionEvent) {
        this.mTmpMatrix.setScale(1.0f, -1.0f);
        motionEvent.transform(this.mTmpMatrix);
    }

    public void transformEvent(float f, MotionEvent motionEvent, boolean z) {
        Matrix matrix = this.mTmpMatrix;
        if (z) {
            f = -f;
        }
        matrix.setRotate(f);
        motionEvent.transform(this.mTmpMatrix);
    }

    public boolean isDisplayPhoneNatural() {
        int i = this.mDisplayRotation;
        return i == 0 || i == 2;
    }

    public static void postDisplayRotation(int i, float f, float f2, Matrix matrix) {
        if (i == 1) {
            matrix.postRotate(270.0f);
            matrix.postTranslate(0.0f, f);
        } else if (i == 2) {
            matrix.postRotate(180.0f);
            matrix.postTranslate(f2, f);
        } else if (i == 3) {
            matrix.postRotate(90.0f);
            matrix.postTranslate(f2, 0.0f);
        }
    }

    public static void preDisplayRotation(int i, float f, float f2, Matrix matrix) {
        if (i == 1) {
            matrix.postRotate(90.0f);
            matrix.postTranslate(f, 0.0f);
        } else if (i == 2) {
            matrix.postRotate(180.0f);
            matrix.postTranslate(f2, f);
        } else if (i == 3) {
            matrix.postRotate(270.0f);
            matrix.postTranslate(0.0f, f2);
        }
    }

    public String toString() {
        return "[this=" + nameAndAddress(this) + " mOrientationHandler=" + nameAndAddress(this.mOrientationHandler) + " mDisplayRotation=" + this.mDisplayRotation + " mTouchRotation=" + this.mTouchRotation + " mRecentsActivityRotation=" + this.mRecentsActivityRotation + " mRecentsRotation=" + this.mRecentsRotation + " isRecentsActivityRotationAllowed=" + isRecentsActivityRotationAllowed() + " mSystemRotation=" + ((this.mFlags & 8) != 0) + " mStateId=" + this.mStateId + " mFlags=" + this.mFlags + "]";
    }

    public DeviceProfile getLauncherDeviceProfile() {
        int i;
        int i2;
        InvariantDeviceProfile invariantDeviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext);
        Point point = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getInfo().currentSize;
        int i3 = this.mRecentsActivityRotation;
        if (i3 == 1 || i3 == 3) {
            i = Math.max(point.x, point.y);
            i2 = Math.min(point.x, point.y);
        } else {
            i = Math.min(point.x, point.y);
            i2 = Math.max(point.x, point.y);
        }
        return invariantDeviceProfile.getBestMatch((float) i, (float) i2, this.mRecentsActivityRotation);
    }

    private static String nameAndAddress(Object obj) {
        return obj.getClass().getSimpleName() + "@" + obj.hashCode();
    }
}
