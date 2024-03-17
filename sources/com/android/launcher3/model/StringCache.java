package com.android.launcher3.model;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import java.util.function.Supplier;

public class StringCache {
    private static final String ALL_APPS_PERSONAL_TAB = "Launcher.ALL_APPS_PERSONAL_TAB";
    private static final String ALL_APPS_PERSONAL_TAB_ACCESSIBILITY = "Launcher.ALL_APPS_PERSONAL_TAB_ACCESSIBILITY";
    private static final String ALL_APPS_WORK_TAB = "Launcher.ALL_APPS_WORK_TAB";
    private static final String ALL_APPS_WORK_TAB_ACCESSIBILITY = "Launcher.ALL_APPS_WORK_TAB_ACCESSIBILITY";
    private static final String DISABLED_BY_ADMIN_MESSAGE = "Launcher.DISABLED_BY_ADMIN_MESSAGE";
    private static final String PREFIX = "Launcher.";
    private static final String WIDGETS_PERSONAL_TAB = "Launcher.WIDGETS_PERSONAL_TAB";
    private static final String WIDGETS_WORK_TAB = "Launcher.WIDGETS_WORK_TAB";
    public static final String WORK_FOLDER_NAME = "Launcher.WORK_FOLDER_NAME";
    private static final String WORK_PROFILE_EDU = "Launcher.WORK_PROFILE_EDU";
    private static final String WORK_PROFILE_EDU_ACCEPT = "Launcher.WORK_PROFILE_EDU_ACCEPT";
    private static final String WORK_PROFILE_ENABLE_BUTTON = "Launcher.WORK_PROFILE_ENABLE_BUTTON";
    private static final String WORK_PROFILE_PAUSED_DESCRIPTION = "Launcher.WORK_PROFILE_PAUSED_DESCRIPTION";
    private static final String WORK_PROFILE_PAUSED_TITLE = "Launcher.WORK_PROFILE_PAUSED_TITLE";
    private static final String WORK_PROFILE_PAUSE_BUTTON = "Launcher.WORK_PROFILE_PAUSE_BUTTON";
    public String allAppsPersonalTab;
    public String allAppsPersonalTabAccessibility;
    public String allAppsWorkTab;
    public String allAppsWorkTabAccessibility;
    public String disabledByAdminMessage;
    public String widgetsPersonalTab;
    public String widgetsWorkTab;
    public String workFolderName;
    public String workProfileEdu;
    public String workProfileEduAccept;
    public String workProfileEnableButton;
    public String workProfilePauseButton;
    public String workProfilePausedDescription;
    public String workProfilePausedTitle;

    public void loadStrings(Context context) {
        this.workProfileEdu = getEnterpriseString(context, WORK_PROFILE_EDU, R.string.work_profile_edu_work_apps);
        this.workProfileEduAccept = getEnterpriseString(context, WORK_PROFILE_EDU_ACCEPT, R.string.work_profile_edu_accept);
        this.workProfilePausedTitle = getEnterpriseString(context, WORK_PROFILE_PAUSED_TITLE, R.string.work_apps_paused_title);
        this.workProfilePausedDescription = getEnterpriseString(context, WORK_PROFILE_PAUSED_DESCRIPTION, R.string.work_apps_paused_body);
        this.workProfilePauseButton = getEnterpriseString(context, WORK_PROFILE_PAUSE_BUTTON, R.string.work_apps_pause_btn_text);
        this.workProfileEnableButton = getEnterpriseString(context, WORK_PROFILE_ENABLE_BUTTON, R.string.work_apps_enable_btn_text);
        this.allAppsWorkTab = getEnterpriseString(context, ALL_APPS_WORK_TAB, R.string.all_apps_work_tab);
        this.allAppsPersonalTab = getEnterpriseString(context, ALL_APPS_PERSONAL_TAB, R.string.all_apps_personal_tab);
        this.allAppsWorkTabAccessibility = getEnterpriseString(context, ALL_APPS_WORK_TAB_ACCESSIBILITY, R.string.all_apps_button_work_label);
        this.allAppsPersonalTabAccessibility = getEnterpriseString(context, ALL_APPS_PERSONAL_TAB_ACCESSIBILITY, R.string.all_apps_button_personal_label);
        this.workFolderName = getEnterpriseString(context, WORK_FOLDER_NAME, R.string.work_folder_name);
        this.widgetsWorkTab = getEnterpriseString(context, WIDGETS_WORK_TAB, R.string.widgets_full_sheet_work_tab);
        this.widgetsPersonalTab = getEnterpriseString(context, WIDGETS_PERSONAL_TAB, R.string.widgets_full_sheet_personal_tab);
        this.disabledByAdminMessage = getEnterpriseString(context, DISABLED_BY_ADMIN_MESSAGE, R.string.msg_disabled_by_admin);
    }

    private String getEnterpriseString(Context context, String str, int i) {
        if (Utilities.ATLEAST_T) {
            return getUpdatableEnterpriseSting(context, str, i);
        }
        return context.getString(i);
    }

    private String getUpdatableEnterpriseSting(Context context, String str, int i) {
        return ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getResources().getString(str, new Supplier(context, i) {
            public final /* synthetic */ Context f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final Object get() {
                return this.f$0.getString(this.f$1);
            }
        });
    }

    public StringCache clone() {
        StringCache stringCache = new StringCache();
        stringCache.workProfileEdu = this.workProfileEdu;
        stringCache.workProfileEduAccept = this.workProfileEduAccept;
        stringCache.workProfilePausedTitle = this.workProfilePausedTitle;
        stringCache.workProfilePausedDescription = this.workProfilePausedDescription;
        stringCache.workProfilePauseButton = this.workProfilePauseButton;
        stringCache.workProfileEnableButton = this.workProfileEnableButton;
        stringCache.allAppsWorkTab = this.allAppsWorkTab;
        stringCache.allAppsPersonalTab = this.allAppsPersonalTab;
        stringCache.allAppsWorkTabAccessibility = this.allAppsWorkTabAccessibility;
        stringCache.allAppsPersonalTabAccessibility = this.allAppsPersonalTabAccessibility;
        stringCache.workFolderName = this.workFolderName;
        stringCache.widgetsWorkTab = this.widgetsWorkTab;
        stringCache.widgetsPersonalTab = this.widgetsPersonalTab;
        stringCache.disabledByAdminMessage = this.disabledByAdminMessage;
        return stringCache;
    }
}
