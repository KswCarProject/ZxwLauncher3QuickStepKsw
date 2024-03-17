package com.android.launcher3.allapps;

import android.content.SharedPreferences;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.Executors;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.workprofile.PersonalWorkSlidingTabStrip;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Predicate;

public class WorkProfileManager implements PersonalWorkSlidingTabStrip.OnActivePageChangedListener {
    public static final int STATE_DISABLED = 2;
    public static final int STATE_ENABLED = 1;
    public static final int STATE_TRANSITION = 3;
    private static final String TAG = "WorkProfileManager";
    private final WorkAdapterProvider mAdapterProvider;
    private final BaseAllAppsContainerView<?> mAllApps;
    private int mCurrentState;
    private final DeviceProfile mDeviceProfile;
    private final Predicate<ItemInfo> mMatcher;
    private final UserManager mUserManager;
    private WorkModeSwitch mWorkModeSwitch;

    @Retention(RetentionPolicy.SOURCE)
    public @interface WorkProfileState {
    }

    public WorkProfileManager(UserManager userManager, BaseAllAppsContainerView<?> baseAllAppsContainerView, SharedPreferences sharedPreferences, DeviceProfile deviceProfile) {
        this.mUserManager = userManager;
        this.mAllApps = baseAllAppsContainerView;
        this.mDeviceProfile = deviceProfile;
        this.mAdapterProvider = new WorkAdapterProvider((ActivityContext) baseAllAppsContainerView.mActivityContext, sharedPreferences);
        this.mMatcher = baseAllAppsContainerView.mPersonalMatcher.negate();
    }

    public void setWorkProfileEnabled(boolean z) {
        updateCurrentState(3);
        Executors.UI_HELPER_EXECUTOR.post(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WorkProfileManager.this.lambda$setWorkProfileEnabled$0$WorkProfileManager(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setWorkProfileEnabled$0$WorkProfileManager(boolean z) {
        for (UserHandle next : this.mUserManager.getUserProfiles()) {
            if (!Process.myUserHandle().equals(next)) {
                this.mUserManager.requestQuietModeEnabled(!z, next);
            }
        }
    }

    public void onActivePageChanged(int i) {
        WorkModeSwitch workModeSwitch = this.mWorkModeSwitch;
        if (workModeSwitch != null) {
            workModeSwitch.onActivePageChanged(i);
        }
    }

    public void reset() {
        int i = 2;
        if (!this.mAllApps.getAppsStore().hasModelFlag(2)) {
            i = 1;
        }
        updateCurrentState(i);
    }

    private void updateCurrentState(int i) {
        this.mCurrentState = i;
        this.mAdapterProvider.updateCurrentState(i);
        if (getAH() != null) {
            getAH().mAppsList.updateAdapterItems();
        }
        WorkModeSwitch workModeSwitch = this.mWorkModeSwitch;
        if (workModeSwitch != null) {
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            workModeSwitch.updateCurrentState(z);
        }
    }

    public boolean attachWorkModeSwitch() {
        boolean z = false;
        if (!this.mAllApps.getAppsStore().hasModelFlag(5)) {
            Log.e(TAG, "unable to attach work mode switch; Missing required permissions");
            return false;
        }
        if (this.mWorkModeSwitch == null) {
            this.mWorkModeSwitch = (WorkModeSwitch) this.mAllApps.getLayoutInflater().inflate(R.layout.work_mode_fab, this.mAllApps, false);
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mWorkModeSwitch.getLayoutParams();
        int dimensionPixelSize = this.mWorkModeSwitch.getResources().getDimensionPixelSize(R.dimen.work_fab_margin_bottom);
        if (FeatureFlags.ENABLE_FLOATING_SEARCH_BAR.get()) {
            dimensionPixelSize = (dimensionPixelSize << 1) + this.mWorkModeSwitch.getResources().getDimensionPixelSize(R.dimen.qsb_widget_height);
        }
        if (!((DeviceProfile.DeviceProfileListenable) this.mAllApps.mActivityContext).getDeviceProfile().isGestureMode) {
            dimensionPixelSize += ((DeviceProfile.DeviceProfileListenable) this.mAllApps.mActivityContext).getDeviceProfile().getInsets().bottom;
        }
        marginLayoutParams.bottomMargin = dimensionPixelSize;
        int tabWidth = (this.mDeviceProfile.widthPx - ((ActivityContext) this.mAllApps.mActivityContext).getAppsView().getFloatingHeaderView().getTabWidth()) / 2;
        marginLayoutParams.leftMargin = tabWidth;
        marginLayoutParams.rightMargin = tabWidth;
        ViewParent parent = this.mWorkModeSwitch.getParent();
        BaseAllAppsContainerView<?> baseAllAppsContainerView = this.mAllApps;
        if (parent != baseAllAppsContainerView) {
            baseAllAppsContainerView.addView(this.mWorkModeSwitch);
        }
        if (getAH() != null) {
            getAH().applyPadding();
        }
        WorkModeSwitch workModeSwitch = this.mWorkModeSwitch;
        if (this.mCurrentState == 1) {
            z = true;
        }
        workModeSwitch.updateCurrentState(z);
        return true;
    }

    public void detachWorkModeSwitch() {
        BaseAllAppsContainerView<?> baseAllAppsContainerView;
        WorkModeSwitch workModeSwitch = this.mWorkModeSwitch;
        if (workModeSwitch != null && workModeSwitch.getParent() == (baseAllAppsContainerView = this.mAllApps)) {
            baseAllAppsContainerView.removeView(this.mWorkModeSwitch);
        }
        this.mWorkModeSwitch = null;
    }

    public WorkAdapterProvider getAdapterProvider() {
        return this.mAdapterProvider;
    }

    public Predicate<ItemInfo> getMatcher() {
        return this.mMatcher;
    }

    public WorkModeSwitch getWorkModeSwitch() {
        return this.mWorkModeSwitch;
    }

    private BaseAllAppsContainerView<?>.AdapterHolder getAH() {
        return this.mAllApps.mAH.get(1);
    }

    public int getCurrentState() {
        return this.mCurrentState;
    }
}
