package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.views.BaseDragLayer;

public class WidgetHostViewLoader implements DragController.DragListener {
    private static final boolean LOGD = false;
    private static final String TAG = "WidgetHostViewLoader";
    private Runnable mBindWidgetRunnable = null;
    Handler mHandler;
    Runnable mInflateWidgetRunnable = null;
    final PendingAddWidgetInfo mInfo;
    Launcher mLauncher;
    final View mView;
    int mWidgetLoadingId = -1;

    public WidgetHostViewLoader(Launcher launcher, View view) {
        this.mLauncher = launcher;
        this.mHandler = new Handler();
        this.mView = view;
        this.mInfo = (PendingAddWidgetInfo) view.getTag();
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        preloadWidget();
    }

    public void onDragEnd() {
        this.mLauncher.getDragController().removeDragListener(this);
        this.mHandler.removeCallbacks(this.mBindWidgetRunnable);
        this.mHandler.removeCallbacks(this.mInflateWidgetRunnable);
        if (this.mWidgetLoadingId != -1) {
            this.mLauncher.getAppWidgetHost().deleteAppWidgetId(this.mWidgetLoadingId);
            this.mWidgetLoadingId = -1;
        }
        if (this.mInfo.boundWidget != null) {
            this.mLauncher.getDragLayer().removeView(this.mInfo.boundWidget);
            this.mLauncher.getAppWidgetHost().deleteAppWidgetId(this.mInfo.boundWidget.getAppWidgetId());
            this.mInfo.boundWidget = null;
        }
    }

    private boolean preloadWidget() {
        final LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = this.mInfo.info;
        if (launcherAppWidgetProviderInfo.isCustomWidget()) {
            return false;
        }
        final Bundle defaultSizeOptions = this.mInfo.getDefaultSizeOptions(this.mLauncher);
        if (this.mInfo.getHandler().needsConfigure()) {
            this.mInfo.bindOptions = defaultSizeOptions;
            return false;
        }
        this.mBindWidgetRunnable = new Runnable() {
            public void run() {
                WidgetHostViewLoader widgetHostViewLoader = WidgetHostViewLoader.this;
                widgetHostViewLoader.mWidgetLoadingId = widgetHostViewLoader.mLauncher.getAppWidgetHost().allocateAppWidgetId();
                if (new WidgetManagerHelper(WidgetHostViewLoader.this.mLauncher).bindAppWidgetIdIfAllowed(WidgetHostViewLoader.this.mWidgetLoadingId, launcherAppWidgetProviderInfo, defaultSizeOptions)) {
                    WidgetHostViewLoader.this.mHandler.post(WidgetHostViewLoader.this.mInflateWidgetRunnable);
                }
            }
        };
        this.mInflateWidgetRunnable = new Runnable() {
            public void run() {
                if (WidgetHostViewLoader.this.mWidgetLoadingId != -1) {
                    AppWidgetHostView createView = WidgetHostViewLoader.this.mLauncher.getAppWidgetHost().createView(WidgetHostViewLoader.this.mLauncher, WidgetHostViewLoader.this.mWidgetLoadingId, launcherAppWidgetProviderInfo);
                    WidgetHostViewLoader.this.mInfo.boundWidget = createView;
                    WidgetHostViewLoader.this.mWidgetLoadingId = -1;
                    createView.setVisibility(4);
                    int[] estimateItemSize = WidgetHostViewLoader.this.mLauncher.getWorkspace().estimateItemSize(WidgetHostViewLoader.this.mInfo);
                    BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(estimateItemSize[0], estimateItemSize[1]);
                    layoutParams.y = 0;
                    layoutParams.x = 0;
                    layoutParams.customPosition = true;
                    createView.setLayoutParams(layoutParams);
                    WidgetHostViewLoader.this.mLauncher.getDragLayer().addView(createView);
                    WidgetHostViewLoader.this.mView.setTag(WidgetHostViewLoader.this.mInfo);
                }
            }
        };
        this.mHandler.post(this.mBindWidgetRunnable);
        return true;
    }
}
