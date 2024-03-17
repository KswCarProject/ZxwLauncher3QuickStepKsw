package com.android.launcher3.dragndrop;

import android.view.View;
import com.android.launcher3.Alarm;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.Workspace;

public class SpringLoadedDragController implements OnAlarmListener {
    final long ENTER_SPRING_LOAD_CANCEL_HOVER_TIME = 950;
    final long ENTER_SPRING_LOAD_HOVER_TIME = 500;
    Alarm mAlarm;
    private Launcher mLauncher;
    private CellLayout mScreen;

    public SpringLoadedDragController(Launcher launcher) {
        this.mLauncher = launcher;
        Alarm alarm = new Alarm();
        this.mAlarm = alarm;
        alarm.setOnAlarmListener(this);
    }

    public void cancel() {
        this.mAlarm.cancelAlarm();
    }

    public void setAlarm(CellLayout cellLayout) {
        this.mAlarm.cancelAlarm();
        this.mAlarm.setAlarm(cellLayout == null ? 950 : 500);
        this.mScreen = cellLayout;
    }

    public void onAlarm(Alarm alarm) {
        if (this.mScreen != null) {
            Workspace<?> workspace = this.mLauncher.getWorkspace();
            if (!workspace.isVisible((View) this.mScreen)) {
                workspace.snapToPage(workspace.indexOfChild(this.mScreen));
                return;
            }
            return;
        }
        this.mLauncher.getDragController().cancelDrag();
    }
}
