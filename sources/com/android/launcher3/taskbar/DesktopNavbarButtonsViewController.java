package com.android.launcher3.taskbar;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.R;

public class DesktopNavbarButtonsViewController extends NavbarButtonsViewController {
    private final TaskbarActivityContext mContext;
    private TaskbarControllers mControllers;
    private final ViewGroup mNavButtonContainer;
    private final FrameLayout mNavButtonsView;

    public void onDestroy() {
    }

    public DesktopNavbarButtonsViewController(TaskbarActivityContext taskbarActivityContext, FrameLayout frameLayout) {
        super(taskbarActivityContext, frameLayout);
        this.mContext = taskbarActivityContext;
        this.mNavButtonsView = frameLayout;
        this.mNavButtonContainer = (ViewGroup) frameLayout.findViewById(R.id.end_nav_buttons);
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
        this.mNavButtonsView.getLayoutParams().height = this.mContext.getDeviceProfile().taskbarSize;
        addButton(R.drawable.ic_sysbar_quick_settings, 32, this.mNavButtonContainer, this.mControllers.navButtonController, R.id.quick_settings_button);
        addButton(R.drawable.ic_sysbar_notifications, 64, this.mNavButtonContainer, this.mControllers.navButtonController, R.id.notifications_button);
    }
}
