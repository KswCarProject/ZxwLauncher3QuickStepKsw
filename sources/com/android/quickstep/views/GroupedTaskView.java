package com.android.quickstep.views;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.util.TransformingTouchDelegate;
import com.android.quickstep.RecentsModel;
import com.android.quickstep.TaskIconCache;
import com.android.quickstep.TaskThumbnailCache;
import com.android.quickstep.util.CancellableTask;
import com.android.quickstep.util.RecentsOrientedState;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;
import java.util.HashMap;
import java.util.function.Consumer;

public class GroupedTaskView extends TaskView {
    private final DigitalWellBeingToast mDigitalWellBeingToast2;
    private final float[] mIcon2CenterCoords;
    private TransformingTouchDelegate mIcon2TouchDelegate;
    private CancellableTask mIconLoadRequest2;
    private IconView mIconView2;
    private Task mSecondaryTask;
    private TaskThumbnailView mSnapshotView2;
    private SplitConfigurationOptions.StagedSplitBounds mSplitBoundsConfig;
    private CancellableTask<ThumbnailData> mThumbnailLoadRequest2;

    public GroupedTaskView(Context context) {
        this(context, (AttributeSet) null);
    }

    public GroupedTaskView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GroupedTaskView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIcon2CenterCoords = new float[2];
        this.mDigitalWellBeingToast2 = new DigitalWellBeingToast(this.mActivity, this);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mSnapshotView2 = (TaskThumbnailView) findViewById(R.id.bottomright_snapshot);
        this.mIconView2 = (IconView) findViewById(R.id.bottomRight_icon);
        this.mIcon2TouchDelegate = new TransformingTouchDelegate(this.mIconView2);
    }

    public void bind(Task task, Task task2, RecentsOrientedState recentsOrientedState, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds) {
        super.bind(task, recentsOrientedState);
        this.mSecondaryTask = task2;
        this.mTaskIdContainer[1] = task2.key.id;
        this.mTaskIdAttributeContainer[1] = new TaskView.TaskIdAttributeContainer(task2, this.mSnapshotView2, this.mIconView2, 1);
        this.mTaskIdAttributeContainer[0].setStagePosition(0);
        this.mSnapshotView2.bind(task2);
        this.mSplitBoundsConfig = stagedSplitBounds;
    }

    public void onTaskListVisibilityChanged(boolean z, int i) {
        super.onTaskListVisibilityChanged(z, i);
        if (z) {
            RecentsModel recentsModel = RecentsModel.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext());
            TaskThumbnailCache thumbnailCache = recentsModel.getThumbnailCache();
            TaskIconCache iconCache = recentsModel.getIconCache();
            if (needsUpdate(i, 2)) {
                this.mThumbnailLoadRequest2 = thumbnailCache.updateThumbnailInBackground(this.mSecondaryTask, new Consumer() {
                    public final void accept(Object obj) {
                        GroupedTaskView.this.lambda$onTaskListVisibilityChanged$0$GroupedTaskView((ThumbnailData) obj);
                    }
                });
            }
            if (needsUpdate(i, 1)) {
                this.mIconLoadRequest2 = iconCache.updateIconInBackground(this.mSecondaryTask, new Consumer() {
                    public final void accept(Object obj) {
                        GroupedTaskView.this.lambda$onTaskListVisibilityChanged$1$GroupedTaskView((Task) obj);
                    }
                });
                return;
            }
            return;
        }
        if (needsUpdate(i, 2)) {
            this.mSnapshotView2.setThumbnail((Task) null, (ThumbnailData) null);
            this.mSecondaryTask.thumbnail = null;
        }
        if (needsUpdate(i, 1)) {
            setIcon(this.mIconView2, (Drawable) null);
        }
    }

    public /* synthetic */ void lambda$onTaskListVisibilityChanged$0$GroupedTaskView(ThumbnailData thumbnailData) {
        this.mSnapshotView2.setThumbnail(this.mSecondaryTask, thumbnailData);
    }

    public /* synthetic */ void lambda$onTaskListVisibilityChanged$1$GroupedTaskView(Task task) {
        setIcon(this.mIconView2, task.icon);
        this.mDigitalWellBeingToast2.initialize(this.mSecondaryTask);
        this.mDigitalWellBeingToast2.setSplitConfiguration(this.mSplitBoundsConfig);
        this.mDigitalWellBeingToast.setSplitConfiguration(this.mSplitBoundsConfig);
    }

    public void updateSplitBoundsConfig(SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds) {
        this.mSplitBoundsConfig = stagedSplitBounds;
        invalidate();
    }

    public float getSplitRatio() {
        SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds = this.mSplitBoundsConfig;
        if (stagedSplitBounds != null) {
            return stagedSplitBounds.appsStackedVertically ? this.mSplitBoundsConfig.topTaskPercent : this.mSplitBoundsConfig.leftTaskPercent;
        }
        return 0.5f;
    }

    public boolean offerTouchToChildren(MotionEvent motionEvent) {
        computeAndSetIconTouchDelegate(this.mIconView2, this.mIcon2CenterCoords, this.mIcon2TouchDelegate);
        if (this.mIcon2TouchDelegate.onTouchEvent(motionEvent)) {
            return true;
        }
        return super.offerTouchToChildren(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void cancelPendingLoadTasks() {
        super.cancelPendingLoadTasks();
        CancellableTask<ThumbnailData> cancellableTask = this.mThumbnailLoadRequest2;
        if (cancellableTask != null) {
            cancellableTask.cancel();
            this.mThumbnailLoadRequest2 = null;
        }
        CancellableTask cancellableTask2 = this.mIconLoadRequest2;
        if (cancellableTask2 != null) {
            cancellableTask2.cancel();
            this.mIconLoadRequest2 = null;
        }
    }

    public RunnableList launchTaskAnimated() {
        if (this.mTask == null || this.mSecondaryTask == null) {
            return null;
        }
        RunnableList runnableList = new RunnableList();
        RecentsView recentsView = getRecentsView();
        InteractionJankMonitorWrapper.begin((View) this, 49, "Enter form GroupedTaskView");
        recentsView.getSplitPlaceholder().launchTasks(this, new Consumer() {
            public final void accept(Object obj) {
                GroupedTaskView.lambda$launchTaskAnimated$2(RunnableList.this, (Boolean) obj);
            }
        }, false);
        recentsView.addSideTaskLaunchCallback(runnableList);
        return runnableList;
    }

    static /* synthetic */ void lambda$launchTaskAnimated$2(RunnableList runnableList, Boolean bool) {
        runnableList.executeAllAndDestroy();
        InteractionJankMonitorWrapper.end(49);
    }

    public void launchTask(Consumer<Boolean> consumer, boolean z) {
        getRecentsView().getSplitPlaceholder().launchTasks(this.mTask.key.id, this.mSecondaryTask.key.id, 0, consumer, z, getSplitRatio());
    }

    /* access modifiers changed from: package-private */
    public void refreshThumbnails(HashMap<Integer, ThumbnailData> hashMap) {
        ThumbnailData thumbnailData;
        super.refreshThumbnails(hashMap);
        Task task = this.mSecondaryTask;
        if (task == null || hashMap == null || (thumbnailData = hashMap.get(Integer.valueOf(task.key.id))) == null) {
            this.mSnapshotView2.refresh();
        } else {
            this.mSnapshotView2.setThumbnail(this.mSecondaryTask, thumbnailData);
        }
    }

    public TaskThumbnailView[] getThumbnails() {
        return new TaskThumbnailView[]{this.mSnapshotView, this.mSnapshotView2};
    }

    /* access modifiers changed from: protected */
    public int getChildTaskIndexAtPosition(PointF pointF) {
        if (isCoordInView(this.mIconView2, pointF) || isCoordInView(this.mSnapshotView2, pointF)) {
            return 1;
        }
        return super.getChildTaskIndexAtPosition(pointF);
    }

    private boolean isCoordInView(View view, PointF pointF) {
        float[] fArr = {pointF.x, pointF.y};
        Utilities.mapCoordInSelfToDescendant(view, this, fArr);
        return Utilities.pointInView(view, fArr[0], fArr[1], 0.0f);
    }

    public void onRecycle() {
        super.onRecycle();
        this.mSnapshotView2.setThumbnail(this.mSecondaryTask, (ThumbnailData) null);
        this.mSplitBoundsConfig = null;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        if (this.mSplitBoundsConfig != null && this.mSnapshotView != null && this.mSnapshotView2 != null) {
            getPagedOrientationHandler().measureGroupedTaskViewThumbnailBounds(this.mSnapshotView, this.mSnapshotView2, size, size2, this.mSplitBoundsConfig, this.mActivity.getDeviceProfile(), getLayoutDirection() == 1);
            updateIconPlacement();
        }
    }

    public void setOverlayEnabled(boolean z) {
        super.setOverlayEnabled(z);
        this.mSnapshotView2.setOverlayEnabled(z);
    }

    public void setOrientationState(RecentsOrientedState recentsOrientedState) {
        int i;
        super.setOrientationState(recentsOrientedState);
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        if (deviceProfile.isTablet && !isFocusedTask()) {
            i = deviceProfile.overviewTaskIconDrawableSizeGridPx;
        } else {
            i = deviceProfile.overviewTaskIconDrawableSizePx;
        }
        this.mIconView2.setDrawableSize(i, i);
        this.mIconView2.setRotation(getPagedOrientationHandler().getDegreesRotated());
        updateIconPlacement();
        updateSecondaryDwbPlacement();
    }

    private void updateIconPlacement() {
        if (this.mSplitBoundsConfig != null) {
            DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
            getPagedOrientationHandler().setSplitIconParams(this.mIconView, this.mIconView2, deviceProfile.overviewTaskIconSizePx, this.mSnapshotView.getMeasuredWidth(), this.mSnapshotView.getMeasuredHeight(), getMeasuredHeight(), getMeasuredWidth(), getLayoutDirection() == 1, deviceProfile, this.mSplitBoundsConfig);
        }
    }

    private void updateSecondaryDwbPlacement() {
        Task task = this.mSecondaryTask;
        if (task != null) {
            this.mDigitalWellBeingToast2.initialize(task);
        }
    }

    /* access modifiers changed from: protected */
    public void updateSnapshotRadius() {
        super.updateSnapshotRadius();
        this.mSnapshotView2.setFullscreenParams(this.mCurrentFullscreenParams);
    }

    /* access modifiers changed from: protected */
    public void setIconAndDimTransitionProgress(float f, boolean z) {
        super.setIconAndDimTransitionProgress(f, z);
        float alpha = this.mIconView.getAlpha();
        this.mIconView2.setAlpha(alpha);
        this.mDigitalWellBeingToast2.updateBannerOffset(1.0f - alpha, this.mCurrentFullscreenParams.mCurrentDrawnInsets.top + this.mCurrentFullscreenParams.mCurrentDrawnInsets.bottom);
    }

    public void setColorTint(float f, int i) {
        super.setColorTint(f, i);
        this.mIconView2.setIconColorTint(i, f);
        this.mSnapshotView2.setDimAlpha(f);
        this.mDigitalWellBeingToast2.setBannerColorTint(i, f);
    }
}
