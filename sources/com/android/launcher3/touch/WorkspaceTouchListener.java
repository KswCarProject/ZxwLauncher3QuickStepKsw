package com.android.launcher3.touch;

import android.graphics.PointF;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;

public class WorkspaceTouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    private static final int STATE_CANCELLED = 0;
    private static final int STATE_COMPLETED = 3;
    private static final int STATE_PENDING_PARENT_INFORM = 2;
    private static final int STATE_REQUESTED = 1;
    private final GestureDetector mGestureDetector;
    private final Launcher mLauncher;
    private int mLongPressState = 0;
    private final Rect mTempRect = new Rect();
    private final PointF mTouchDownPoint = new PointF();
    private final float mTouchSlop;
    private final Workspace<?> mWorkspace;

    public WorkspaceTouchListener(Launcher launcher, Workspace<?> workspace) {
        this.mLauncher = launcher;
        this.mWorkspace = workspace;
        this.mTouchSlop = (float) (ViewConfiguration.get(launcher).getScaledTouchSlop() * 2);
        this.mGestureDetector = new GestureDetector(workspace.getContext(), this);
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00ea  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.View r9, android.view.MotionEvent r10) {
        /*
            r8 = this;
            android.view.GestureDetector r9 = r8.mGestureDetector
            r9.onTouchEvent(r10)
            int r9 = r10.getActionMasked()
            r0 = 1
            if (r9 != 0) goto L_0x0069
            boolean r9 = r8.canHandleLongPress()
            if (r9 == 0) goto L_0x0052
            com.android.launcher3.Launcher r9 = r8.mLauncher
            com.android.launcher3.DeviceProfile r9 = r9.getDeviceProfile()
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.dragndrop.DragLayer r1 = r1.getDragLayer()
            android.graphics.Rect r2 = r9.getInsets()
            android.graphics.Rect r3 = r8.mTempRect
            int r4 = r2.left
            int r5 = r2.top
            int r6 = r1.getWidth()
            int r7 = r2.right
            int r6 = r6 - r7
            int r1 = r1.getHeight()
            int r2 = r2.bottom
            int r1 = r1 - r2
            r3.set(r4, r5, r6, r1)
            android.graphics.Rect r1 = r8.mTempRect
            int r2 = r9.edgeMarginPx
            int r9 = r9.edgeMarginPx
            r1.inset(r2, r9)
            android.graphics.Rect r9 = r8.mTempRect
            float r1 = r10.getX()
            int r1 = (int) r1
            float r2 = r10.getY()
            int r2 = (int) r2
            boolean r9 = r9.contains(r1, r2)
        L_0x0052:
            if (r9 == 0) goto L_0x0063
            r8.mLongPressState = r0
            android.graphics.PointF r9 = r8.mTouchDownPoint
            float r1 = r10.getX()
            float r2 = r10.getY()
            r9.set(r1, r2)
        L_0x0063:
            com.android.launcher3.Workspace<?> r9 = r8.mWorkspace
            r9.onTouchEvent(r10)
            return r0
        L_0x0069:
            int r1 = r8.mLongPressState
            r2 = 2
            r3 = 3
            if (r1 != r2) goto L_0x007c
            r10.setAction(r3)
            com.android.launcher3.Workspace<?> r1 = r8.mWorkspace
            r1.onTouchEvent(r10)
            r10.setAction(r9)
            r8.mLongPressState = r3
        L_0x007c:
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.LauncherState r4 = com.android.launcher3.LauncherState.ALL_APPS
            boolean r1 = r1.isInState(r4)
            if (r1 == 0) goto L_0x0092
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            boolean r1 = r1.isTablet
            if (r1 == 0) goto L_0x0092
            r1 = r0
            goto L_0x0093
        L_0x0092:
            r1 = 0
        L_0x0093:
            int r4 = r8.mLongPressState
            if (r4 != r3) goto L_0x0099
        L_0x0097:
            r2 = r0
            goto L_0x00cf
        L_0x0099:
            if (r4 != r0) goto L_0x00ce
            com.android.launcher3.Workspace<?> r4 = r8.mWorkspace
            r4.onTouchEvent(r10)
            com.android.launcher3.Workspace<?> r4 = r8.mWorkspace
            boolean r4 = r4.isHandlingTouch()
            if (r4 == 0) goto L_0x00ac
            r8.cancelLongPress()
            goto L_0x0097
        L_0x00ac:
            if (r9 != r2) goto L_0x0097
            android.graphics.PointF r2 = r8.mTouchDownPoint
            float r2 = r2.x
            float r4 = r10.getX()
            float r2 = r2 - r4
            android.graphics.PointF r4 = r8.mTouchDownPoint
            float r4 = r4.y
            float r5 = r10.getY()
            float r4 = r4 - r5
            float r2 = android.graphics.PointF.length(r2, r4)
            float r4 = r8.mTouchSlop
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0097
            r8.cancelLongPress()
            goto L_0x0097
        L_0x00ce:
            r2 = r1
        L_0x00cf:
            if (r9 == r0) goto L_0x00d4
            r4 = 6
            if (r9 != r4) goto L_0x00ef
        L_0x00d4:
            com.android.launcher3.Workspace<?> r4 = r8.mWorkspace
            boolean r4 = r4.isHandlingTouch()
            if (r4 != 0) goto L_0x00ef
            com.android.launcher3.Workspace<?> r4 = r8.mWorkspace
            int r5 = r4.getCurrentPage()
            android.view.View r4 = r4.getChildAt(r5)
            com.android.launcher3.CellLayout r4 = (com.android.launcher3.CellLayout) r4
            if (r4 == 0) goto L_0x00ef
            com.android.launcher3.Workspace<?> r4 = r8.mWorkspace
            r4.onWallpaperTap(r10)
        L_0x00ef:
            if (r9 == r0) goto L_0x00f3
            if (r9 != r3) goto L_0x00f6
        L_0x00f3:
            r8.cancelLongPress()
        L_0x00f6:
            if (r9 != r0) goto L_0x0148
            if (r1 == 0) goto L_0x0148
            com.android.launcher3.Launcher r9 = r8.mLauncher
            com.android.launcher3.statemanager.StateManager r9 = r9.getStateManager()
            com.android.launcher3.LauncherState r10 = com.android.launcher3.LauncherState.NORMAL
            r9.goToState(r10)
            com.android.launcher3.Launcher r9 = r8.mLauncher
            com.android.launcher3.logging.StatsLogManager r9 = r9.getStatsLogManager()
            com.android.launcher3.logging.StatsLogManager$StatsLogger r9 = r9.logger()
            com.android.launcher3.LauncherState r10 = com.android.launcher3.LauncherState.ALL_APPS
            int r10 = r10.statsLogOrdinal
            com.android.launcher3.logging.StatsLogManager$StatsLogger r9 = r9.withSrcState(r10)
            com.android.launcher3.LauncherState r10 = com.android.launcher3.LauncherState.NORMAL
            int r10 = r10.statsLogOrdinal
            com.android.launcher3.logging.StatsLogManager$StatsLogger r9 = r9.withDstState(r10)
            com.android.launcher3.logger.LauncherAtom$ContainerInfo$Builder r10 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.newBuilder()
            com.android.launcher3.logger.LauncherAtom$WorkspaceContainer$Builder r0 = com.android.launcher3.logger.LauncherAtom.WorkspaceContainer.newBuilder()
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.Workspace r1 = r1.getWorkspace()
            int r1 = r1.getCurrentPage()
            com.android.launcher3.logger.LauncherAtom$WorkspaceContainer$Builder r0 = r0.setPageIndex(r1)
            com.android.launcher3.logger.LauncherAtom$ContainerInfo$Builder r10 = r10.setWorkspace((com.android.launcher3.logger.LauncherAtom.WorkspaceContainer.Builder) r0)
            com.google.protobuf.GeneratedMessageLite r10 = r10.build()
            com.android.launcher3.logger.LauncherAtom$ContainerInfo r10 = (com.android.launcher3.logger.LauncherAtom.ContainerInfo) r10
            com.android.launcher3.logging.StatsLogManager$StatsLogger r9 = r9.withContainerInfo(r10)
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r10 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_CLOSE_TAP_OUTSIDE
            r9.log(r10)
        L_0x0148:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.touch.WorkspaceTouchListener.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    private boolean canHandleLongPress() {
        return AbstractFloatingView.getTopOpenView(this.mLauncher) == null && this.mLauncher.isInState(LauncherState.NORMAL);
    }

    private void cancelLongPress() {
        this.mLongPressState = 0;
    }

    public void onLongPress(MotionEvent motionEvent) {
        if (this.mLongPressState == 1) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "Workspace.longPress");
            if (canHandleLongPress()) {
                this.mLongPressState = 2;
                this.mWorkspace.getParent().requestDisallowInterceptTouchEvent(true);
                this.mWorkspace.performHapticFeedback(0, 1);
                this.mLauncher.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_WORKSPACE_LONGPRESS);
                this.mLauncher.showDefaultOptions(this.mTouchDownPoint.x, this.mTouchDownPoint.y);
                return;
            }
            cancelLongPress();
        }
    }
}
