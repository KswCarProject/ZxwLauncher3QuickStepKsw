package com.android.quickstep;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import com.android.launcher3.testing.TestInformationHandler;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.util.LayoutUtils;

public class QuickstepTestInformationHandler extends TestInformationHandler {
    protected final Context mContext;

    public QuickstepTestInformationHandler(Context context) {
        this.mContext = context;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -352687499:
                if (str.equals(TestProtocol.REQUEST_GET_FOCUSED_TASK_HEIGHT_FOR_TABLET)) {
                    c = 0;
                    break;
                }
                break;
            case 451177691:
                if (str.equals(TestProtocol.REQUEST_BACKGROUND_TO_OVERVIEW_SWIPE_HEIGHT)) {
                    c = 1;
                    break;
                }
                break;
            case 599032057:
                if (str.equals(TestProtocol.REQUEST_HAS_TIS)) {
                    c = 2;
                    break;
                }
                break;
            case 898000802:
                if (str.equals(TestProtocol.REQUEST_GET_OVERVIEW_PAGE_SPACING)) {
                    c = 3;
                    break;
                }
                break;
            case 1211049546:
                if (str.equals(TestProtocol.REQUEST_HOME_TO_OVERVIEW_SWIPE_HEIGHT)) {
                    c = 4;
                    break;
                }
                break;
            case 2119863935:
                if (str.equals(TestProtocol.REQUEST_GET_GRID_TASK_SIZE_RECT_FOR_TABLET)) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (!this.mDeviceProfile.isTablet) {
                    return null;
                }
                Rect rect = new Rect();
                LauncherActivityInterface.INSTANCE.calculateTaskSize(this.mContext, this.mDeviceProfile, rect);
                bundle2.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, rect.height());
                return bundle2;
            case 1:
                bundle2.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, (int) ((float) LayoutUtils.getShelfTrackingDistance(this.mContext, this.mDeviceProfile, PagedOrientationHandler.PORTRAIT)));
                return bundle2;
            case 2:
                bundle2.putBoolean(TestProtocol.REQUEST_HAS_TIS, true);
                return bundle2;
            case 3:
                bundle2.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, this.mDeviceProfile.overviewPageSpacing);
                return bundle2;
            case 4:
                bundle2.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, (int) LayoutUtils.getDefaultSwipeHeight(this.mContext, this.mDeviceProfile));
                return bundle2;
            case 5:
                if (!this.mDeviceProfile.isTablet) {
                    return null;
                }
                Rect rect2 = new Rect();
                LauncherActivityInterface.INSTANCE.calculateGridTaskSize(this.mContext, this.mDeviceProfile, rect2, PagedOrientationHandler.PORTRAIT);
                bundle2.putParcelable(TestProtocol.TEST_INFO_RESPONSE_FIELD, rect2);
                return bundle2;
            default:
                return super.call(str, str2, bundle);
        }
    }

    /* access modifiers changed from: protected */
    public Activity getCurrentActivity() {
        RecentsAnimationDeviceState recentsAnimationDeviceState = new RecentsAnimationDeviceState(this.mContext);
        OverviewComponentObserver overviewComponentObserver = new OverviewComponentObserver(this.mContext, recentsAnimationDeviceState);
        try {
            return overviewComponentObserver.getActivityInterface().getCreatedActivity();
        } finally {
            overviewComponentObserver.onDestroy();
            recentsAnimationDeviceState.destroy();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isLauncherInitialized() {
        return super.isLauncherInitialized() && TouchInteractionService.isInitialized();
    }
}
