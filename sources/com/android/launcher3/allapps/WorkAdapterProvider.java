package com.android.launcher3.allapps;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.launcher3.R;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.model.StringCache;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;

public class WorkAdapterProvider extends BaseAdapterProvider {
    public static final String KEY_WORK_EDU_STEP = "showed_work_profile_edu";
    private static final int VIEW_TYPE_WORK_DISABLED_CARD = 2097152;
    private static final int VIEW_TYPE_WORK_EDU_CARD = 1048576;
    private ActivityContext mActivityContext;
    private SharedPreferences mPreferences;
    private int mState;

    public int getItemsPerRow(int i, int i2) {
        return 1;
    }

    public boolean isViewSupported(int i) {
        return i == 2097152 || i == 1048576;
    }

    WorkAdapterProvider(ActivityContext activityContext, SharedPreferences sharedPreferences) {
        this.mActivityContext = activityContext;
        this.mPreferences = sharedPreferences;
    }

    public void onBindView(BaseAllAppsAdapter.ViewHolder viewHolder, int i) {
        if (viewHolder.itemView instanceof WorkEduCard) {
            ((WorkEduCard) viewHolder.itemView).setPosition(i);
        }
    }

    public BaseAllAppsAdapter.ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup, int i) {
        View inflate = layoutInflater.inflate(i == 2097152 ? R.layout.work_apps_paused : R.layout.work_apps_edu, viewGroup, false);
        setDeviceManagementResources(inflate, i);
        return new BaseAllAppsAdapter.ViewHolder(inflate);
    }

    private void setDeviceManagementResources(View view, int i) {
        StringCache stringCache = this.mActivityContext.getStringCache();
        if (stringCache != null) {
            if (i == 2097152) {
                setWorkProfilePausedResources(view, stringCache);
            } else {
                setWorkProfileEduResources(view, stringCache);
            }
        }
    }

    private void setWorkProfilePausedResources(View view, StringCache stringCache) {
        ((TextView) view.findViewById(R.id.work_apps_paused_title)).setText(stringCache.workProfilePausedTitle);
        ((TextView) view.findViewById(R.id.work_apps_paused_content)).setText(stringCache.workProfilePausedDescription);
        ((TextView) view.findViewById(R.id.enable_work_apps)).setText(stringCache.workProfileEnableButton);
    }

    private void setWorkProfileEduResources(View view, StringCache stringCache) {
        ((TextView) view.findViewById(R.id.work_apps_paused_title)).setText(stringCache.workProfileEdu);
    }

    public boolean shouldShowWorkApps() {
        return this.mState != 2;
    }

    public int addWorkItems(ArrayList<BaseAllAppsAdapter.AdapterItem> arrayList) {
        int i = this.mState;
        if (i == 2) {
            arrayList.add(new BaseAllAppsAdapter.AdapterItem(2097152));
        } else if (i == 1 && !isEduSeen()) {
            arrayList.add(new BaseAllAppsAdapter.AdapterItem(1048576));
        }
        return arrayList.size();
    }

    public void updateCurrentState(int i) {
        this.mState = i;
    }

    private boolean isEduSeen() {
        return this.mPreferences.getInt(KEY_WORK_EDU_STEP, 0) != 0;
    }
}
