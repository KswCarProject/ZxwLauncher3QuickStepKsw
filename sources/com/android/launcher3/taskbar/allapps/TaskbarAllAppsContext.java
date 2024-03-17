package com.android.launcher3.taskbar.allapps;

import android.content.Context;
import android.graphics.Insets;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.search.DefaultSearchAdapterProvider;
import com.android.launcher3.allapps.search.SearchAdapterProvider;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.taskbar.BaseTaskbarContext;
import com.android.launcher3.taskbar.TaskbarActivityContext;
import com.android.launcher3.taskbar.TaskbarDragController;
import com.android.launcher3.taskbar.TaskbarStashController;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import com.android.systemui.shared.system.ViewTreeObserverWrapper;

class TaskbarAllAppsContext extends BaseTaskbarContext {
    private final TaskbarAllAppsViewController mAllAppsViewController;
    private final TaskbarAllAppsContainerView mAppsView;
    /* access modifiers changed from: private */
    public final TaskbarDragController mDragController = new TaskbarDragController(this);
    private final TaskbarAllAppsDragLayer mDragLayer;
    private final OnboardingPrefs<TaskbarAllAppsContext> mOnboardingPrefs = new OnboardingPrefs<>(this, Utilities.getPrefs(this));
    /* access modifiers changed from: private */
    public final int mStashedTaskbarHeight;
    private final TaskbarActivityContext mTaskbarContext;
    /* access modifiers changed from: private */
    public final boolean mWillTaskbarBeVisuallyStashed;
    private final TaskbarAllAppsController mWindowController;

    public void onDragStart() {
    }

    public void onPopupVisibilityChanged(boolean z) {
    }

    TaskbarAllAppsContext(TaskbarActivityContext taskbarActivityContext, TaskbarAllAppsController taskbarAllAppsController, TaskbarStashController taskbarStashController) {
        super(taskbarActivityContext.createWindowContext(2038, (Bundle) null));
        this.mTaskbarContext = taskbarActivityContext;
        this.mWindowController = taskbarAllAppsController;
        TaskbarAllAppsDragLayer taskbarAllAppsDragLayer = new TaskbarAllAppsDragLayer(this);
        this.mDragLayer = taskbarAllAppsDragLayer;
        TaskbarAllAppsSlideInView taskbarAllAppsSlideInView = (TaskbarAllAppsSlideInView) this.mLayoutInflater.inflate(R.layout.taskbar_all_apps, taskbarAllAppsDragLayer, false);
        this.mAllAppsViewController = new TaskbarAllAppsViewController(this, taskbarAllAppsSlideInView, taskbarAllAppsController, taskbarStashController);
        this.mAppsView = taskbarAllAppsSlideInView.getAppsView();
        this.mWillTaskbarBeVisuallyStashed = taskbarStashController.supportsVisualStashing();
        this.mStashedTaskbarHeight = taskbarStashController.getStashedHeight();
    }

    /* access modifiers changed from: package-private */
    public TaskbarAllAppsViewController getAllAppsViewController() {
        return this.mAllAppsViewController;
    }

    public DeviceProfile getDeviceProfile() {
        return this.mWindowController.getDeviceProfile();
    }

    public TaskbarDragController getDragController() {
        return this.mDragController;
    }

    public TaskbarAllAppsDragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public TaskbarAllAppsContainerView getAppsView() {
        return this.mAppsView;
    }

    public OnboardingPrefs<TaskbarAllAppsContext> getOnboardingPrefs() {
        return this.mOnboardingPrefs;
    }

    public boolean isBindingItems() {
        return this.mTaskbarContext.isBindingItems();
    }

    public View.OnClickListener getItemOnClickListener() {
        return this.mTaskbarContext.getItemOnClickListener();
    }

    public PopupDataProvider getPopupDataProvider() {
        return this.mTaskbarContext.getPopupDataProvider();
    }

    public DotInfo getDotInfoForItem(ItemInfo itemInfo) {
        return this.mTaskbarContext.getDotInfoForItem(itemInfo);
    }

    public void onDragEnd() {
        this.mWindowController.maybeCloseWindow();
    }

    public SearchAdapterProvider<?> createSearchAdapterProvider(ActivityAllAppsContainerView<?> activityAllAppsContainerView) {
        return new DefaultSearchAdapterProvider(this);
    }

    private static class TaskbarAllAppsDragLayer extends BaseDragLayer<TaskbarAllAppsContext> implements ViewTreeObserverWrapper.OnComputeInsetsListener {
        private TaskbarAllAppsDragLayer(Context context) {
            super(context, (AttributeSet) null, 1);
            setClipChildren(false);
            recreateControllers();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            ViewTreeObserverWrapper.addOnComputeInsetsListener(getViewTreeObserver(), this);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ViewTreeObserverWrapper.removeOnComputeInsetsListener(this);
        }

        public void recreateControllers() {
            this.mControllers = new TouchController[]{((TaskbarAllAppsContext) this.mActivity).mDragController};
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            TestLogging.recordMotionEvent(TestProtocol.SEQUENCE_MAIN, "Touch event", motionEvent);
            return super.dispatchTouchEvent(motionEvent);
        }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            AbstractFloatingView topOpenView;
            if (keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 4 || (topOpenView = AbstractFloatingView.getTopOpenView((ActivityContext) this.mActivity)) == null || !topOpenView.onBackPressed()) {
                return super.dispatchKeyEvent(keyEvent);
            }
            return true;
        }

        public void onComputeInsets(ViewTreeObserverWrapper.InsetsInfo insetsInfo) {
            if (((TaskbarAllAppsContext) this.mActivity).mDragController.isSystemDragInProgress()) {
                insetsInfo.touchableRegion.setEmpty();
                insetsInfo.setTouchableInsets(3);
            }
        }

        public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
            return updateInsetsDueToStashing(windowInsets);
        }

        private WindowInsets updateInsetsDueToStashing(WindowInsets windowInsets) {
            if (!((TaskbarAllAppsContext) this.mActivity).mWillTaskbarBeVisuallyStashed) {
                return windowInsets;
            }
            WindowInsets.Builder builder = new WindowInsets.Builder(windowInsets);
            Insets insets = windowInsets.getInsets(WindowInsets.Type.navigationBars());
            builder.setInsets(WindowInsets.Type.navigationBars(), Insets.of(insets.left, insets.top, insets.right, ((TaskbarAllAppsContext) this.mActivity).mStashedTaskbarHeight));
            Insets insets2 = windowInsets.getInsets(WindowInsets.Type.tappableElement());
            builder.setInsets(WindowInsets.Type.tappableElement(), Insets.of(insets2.left, insets2.top, insets2.right, 0));
            return builder.build();
        }
    }
}
