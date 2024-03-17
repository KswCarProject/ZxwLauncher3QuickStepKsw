package com.android.launcher3.hybridhotseat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Hotseat;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.uioverrides.PredictedAppIcon;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.views.Snackbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HotseatPredictionController implements DragController.DragListener, SystemShortcut.Factory<QuickstepLauncher>, DeviceProfile.OnDeviceProfileChangeListener, DragSource, ViewGroup.OnHierarchyChangeListener {
    private static final int FLAG_DRAG_IN_PROGRESS = 2;
    private static final int FLAG_FILL_IN_PROGRESS = 4;
    private static final int FLAG_REMOVING_PREDICTED_ICON = 8;
    private static final int FLAG_UPDATE_PAUSED = 1;
    private int mHotSeatItemsCount;
    private final Hotseat mHotseat;
    /* access modifiers changed from: private */
    public AnimatorSet mIconRemoveAnimators;
    private QuickstepLauncher mLauncher;
    private List<PredictedAppIcon.PredictedIconOutlineDrawing> mOutlineDrawings = new ArrayList();
    private int mPauseFlags = 0;
    private List<ItemInfo> mPredictedItems = Collections.emptyList();
    private final View.OnLongClickListener mPredictionLongClickListener = new View.OnLongClickListener() {
        public final boolean onLongClick(View view) {
            return HotseatPredictionController.this.lambda$new$1$HotseatPredictionController(view);
        }
    };
    private final Runnable mUpdateFillIfNotLoading = new Runnable() {
        public final void run() {
            HotseatPredictionController.this.updateFillIfNotLoading();
        }
    };

    public void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
    }

    public /* synthetic */ boolean lambda$new$1$HotseatPredictionController(View view) {
        if (!ItemLongClickListener.canStartDrag(this.mLauncher) || this.mLauncher.getWorkspace().isSwitchingState()) {
            return false;
        }
        if (!this.mLauncher.getOnboardingPrefs().getBoolean(OnboardingPrefs.HOTSEAT_LONGPRESS_TIP_SEEN)) {
            Snackbar.show(this.mLauncher, R.string.hotseat_tip_gaps_filled, R.string.hotseat_prediction_settings, (Runnable) null, new Runnable() {
                public final void run() {
                    HotseatPredictionController.this.lambda$new$0$HotseatPredictionController();
                }
            });
            this.mLauncher.getOnboardingPrefs().markChecked(OnboardingPrefs.HOTSEAT_LONGPRESS_TIP_SEEN);
            this.mLauncher.getDragLayer().performHapticFeedback(0);
            return true;
        }
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo((WorkspaceItemInfo) view.getTag());
        view.setVisibility(4);
        this.mLauncher.getWorkspace().beginDragShared(view, (DraggableView) null, this, workspaceItemInfo, new DragPreviewProvider(view), this.mLauncher.getDefaultWorkspaceDragOptions());
        return true;
    }

    public /* synthetic */ void lambda$new$0$HotseatPredictionController() {
        this.mLauncher.startActivity(HotseatEduController.getSettingsIntent());
    }

    public HotseatPredictionController(QuickstepLauncher quickstepLauncher) {
        this.mLauncher = quickstepLauncher;
        Hotseat hotseat = quickstepLauncher.getHotseat();
        this.mHotseat = hotseat;
        this.mHotSeatItemsCount = this.mLauncher.getDeviceProfile().numShownHotseatIcons;
        this.mLauncher.getDragController().addDragListener(this);
        quickstepLauncher.addOnDeviceProfileChangeListener(this);
        hotseat.getShortcutsAndWidgets().setOnHierarchyChangeListener(this);
    }

    public void onChildViewAdded(View view, View view2) {
        onHotseatHierarchyChanged();
    }

    public void onChildViewRemoved(View view, View view2) {
        onHotseatHierarchyChanged();
    }

    private void onHotseatHierarchyChanged() {
        if (this.mPauseFlags == 0 && !this.mLauncher.isWorkspaceLoading()) {
            Executors.MAIN_EXECUTOR.getHandler().removeCallbacks(this.mUpdateFillIfNotLoading);
            Executors.MAIN_EXECUTOR.getHandler().post(this.mUpdateFillIfNotLoading);
        }
    }

    /* access modifiers changed from: private */
    public void updateFillIfNotLoading() {
        if (this.mPauseFlags == 0 && !this.mLauncher.isWorkspaceLoading()) {
            fillGapsWithPrediction(true);
        }
    }

    public void showEdu() {
        this.mLauncher.getStateManager().goToState(LauncherState.NORMAL, true, AnimatorListeners.forSuccessCallback(new Runnable() {
            public final void run() {
                HotseatPredictionController.this.lambda$showEdu$3$HotseatPredictionController();
            }
        }));
    }

    public /* synthetic */ void lambda$showEdu$3$HotseatPredictionController() {
        HotseatEduController hotseatEduController = new HotseatEduController(this.mLauncher);
        hotseatEduController.setPredictedApps((List) this.mPredictedItems.stream().map($$Lambda$HotseatPredictionController$bAkGHj2Dreux4DcTOPnGggMbjQ.INSTANCE).collect(Collectors.toList()));
        hotseatEduController.showEdu();
    }

    static /* synthetic */ WorkspaceItemInfo lambda$showEdu$2(ItemInfo itemInfo) {
        return (WorkspaceItemInfo) itemInfo;
    }

    public boolean hasPredictions() {
        return !this.mPredictedItems.isEmpty();
    }

    private void fillGapsWithPrediction() {
        fillGapsWithPrediction(false);
    }

    /* access modifiers changed from: private */
    public void fillGapsWithPrediction(final boolean z) {
        if (this.mPauseFlags == 0) {
            ArrayList arrayList = new ArrayList();
            AnimatorSet animatorSet = this.mIconRemoveAnimators;
            if (animatorSet == null || !animatorSet.isRunning()) {
                this.mPauseFlags |= 4;
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < this.mHotSeatItemsCount; i3++) {
                    Hotseat hotseat = this.mHotseat;
                    View childAt = hotseat.getChildAt(hotseat.getCellXFromOrder(i3), this.mHotseat.getCellYFromOrder(i3));
                    if (childAt == null || isPredictedIcon(childAt)) {
                        if (this.mPredictedItems.size() > i) {
                            int i4 = i + 1;
                            WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) this.mPredictedItems.get(i);
                            if (!isPredictedIcon(childAt) || !childAt.isEnabled()) {
                                arrayList.add(workspaceItemInfo);
                            } else {
                                PredictedAppIcon predictedAppIcon = (PredictedAppIcon) childAt;
                                boolean shouldAnimateIconChange = predictedAppIcon.shouldAnimateIconChange(workspaceItemInfo);
                                predictedAppIcon.applyFromWorkspaceItem(workspaceItemInfo, shouldAnimateIconChange, i2);
                                if (shouldAnimateIconChange) {
                                    i2++;
                                }
                                predictedAppIcon.finishBinding(this.mPredictionLongClickListener);
                            }
                            preparePredictionInfo(workspaceItemInfo, i3);
                            i = i4;
                        } else if (isPredictedIcon(childAt)) {
                            this.mHotseat.removeView(childAt);
                        }
                    }
                }
                bindItems(arrayList, z);
                this.mPauseFlags &= -5;
                return;
            }
            this.mIconRemoveAnimators.addListener(new AnimationSuccessListener() {
                public void onAnimationSuccess(Animator animator) {
                    HotseatPredictionController.this.fillGapsWithPrediction(z);
                    HotseatPredictionController.this.mIconRemoveAnimators.removeListener(this);
                }
            });
        }
    }

    private void bindItems(List<WorkspaceItemInfo> list, boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        for (WorkspaceItemInfo next : list) {
            PredictedAppIcon createIcon = PredictedAppIcon.createIcon(this.mHotseat, next);
            this.mLauncher.getWorkspace().addInScreenFromBind(createIcon, next);
            createIcon.finishBinding(this.mPredictionLongClickListener);
            if (z) {
                animatorSet.play(ObjectAnimator.ofFloat(createIcon, LauncherAnimUtils.SCALE_PROPERTY, new float[]{0.2f, 1.0f}));
            }
        }
        if (z) {
            animatorSet.addListener(AnimatorListeners.forSuccessCallback(new Runnable() {
                public final void run() {
                    HotseatPredictionController.this.removeOutlineDrawings();
                }
            }));
            animatorSet.start();
            return;
        }
        removeOutlineDrawings();
    }

    /* access modifiers changed from: private */
    public void removeOutlineDrawings() {
        if (!this.mOutlineDrawings.isEmpty()) {
            for (PredictedAppIcon.PredictedIconOutlineDrawing removeDelegatedCellDrawing : this.mOutlineDrawings) {
                this.mHotseat.removeDelegatedCellDrawing(removeDelegatedCellDrawing);
            }
            this.mHotseat.invalidate();
            this.mOutlineDrawings.clear();
        }
    }

    public void destroy() {
        this.mLauncher.removeOnDeviceProfileChangeListener(this);
    }

    public void setPauseUIUpdate(boolean z) {
        int i;
        if (z) {
            i = this.mPauseFlags | 1;
        } else {
            i = this.mPauseFlags & -2;
        }
        this.mPauseFlags = i;
        if (!z) {
            fillGapsWithPrediction();
        }
    }

    public void setPredictedItems(BgDataModel.FixedContainerItems fixedContainerItems) {
        if ((FeatureFlags.ENABLE_APP_PREDICTIONS_WHILE_VISIBLE.get() || this.mLauncher.isWorkspaceLoading() || this.mPredictedItems.equals(fixedContainerItems.items) || this.mHotseat.getShortcutsAndWidgets().getChildCount() < this.mHotSeatItemsCount) || !this.mHotseat.isShown() || this.mHotseat.getWindowVisibility() != 0) {
            this.mHotseat.setOnVisibilityAggregatedCallback((Consumer<Boolean>) null);
            applyPredictedItems(fixedContainerItems);
            return;
        }
        this.mHotseat.setOnVisibilityAggregatedCallback(new Consumer(fixedContainerItems) {
            public final /* synthetic */ BgDataModel.FixedContainerItems f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                HotseatPredictionController.this.lambda$setPredictedItems$4$HotseatPredictionController(this.f$1, (Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$setPredictedItems$4$HotseatPredictionController(BgDataModel.FixedContainerItems fixedContainerItems, Boolean bool) {
        if (!bool.booleanValue()) {
            this.mHotseat.setOnVisibilityAggregatedCallback((Consumer<Boolean>) null);
            applyPredictedItems(fixedContainerItems);
        }
    }

    private void applyPredictedItems(BgDataModel.FixedContainerItems fixedContainerItems) {
        List<ItemInfo> list = fixedContainerItems.items;
        this.mPredictedItems = list;
        if (list.isEmpty()) {
            HotseatRestoreHelper.restoreBackup(this.mLauncher);
        }
        fillGapsWithPrediction();
    }

    public void pinPrediction(ItemInfo itemInfo) {
        Hotseat hotseat = this.mHotseat;
        PredictedAppIcon predictedAppIcon = (PredictedAppIcon) hotseat.getChildAt(hotseat.getCellXFromOrder(itemInfo.rank), this.mHotseat.getCellYFromOrder(itemInfo.rank));
        if (predictedAppIcon != null) {
            WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo((WorkspaceItemInfo) itemInfo);
            this.mLauncher.getModelWriter().addItemToDatabase(workspaceItemInfo, LauncherSettings.Favorites.CONTAINER_HOTSEAT, workspaceItemInfo.screenId, workspaceItemInfo.cellX, workspaceItemInfo.cellY);
            ObjectAnimator.ofFloat(predictedAppIcon, LauncherAnimUtils.SCALE_PROPERTY, new float[]{1.0f, 0.8f, 1.0f}).start();
            predictedAppIcon.pin(workspaceItemInfo);
            this.mLauncher.getStatsLogManager().logger().withItemInfo(workspaceItemInfo).log(StatsLogManager.LauncherEvent.LAUNCHER_HOTSEAT_PREDICTION_PINNED);
        }
    }

    private List<PredictedAppIcon> getPredictedIcons() {
        ArrayList arrayList = new ArrayList();
        ShortcutAndWidgetContainer shortcutsAndWidgets = this.mHotseat.getShortcutsAndWidgets();
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            View childAt = shortcutsAndWidgets.getChildAt(i);
            if (isPredictedIcon(childAt)) {
                arrayList.add((PredictedAppIcon) childAt);
            }
        }
        return arrayList;
    }

    private void removePredictedApps(List<PredictedAppIcon.PredictedIconOutlineDrawing> list, DropTarget.DragObject dragObject) {
        AnimatorSet animatorSet = this.mIconRemoveAnimators;
        if (animatorSet != null) {
            animatorSet.end();
        }
        this.mIconRemoveAnimators = new AnimatorSet();
        removeOutlineDrawings();
        for (final PredictedAppIcon next : getPredictedIcons()) {
            if (next.isEnabled()) {
                if (dragObject.dragSource != this || !next.equals(dragObject.originalView)) {
                    int i = ((WorkspaceItemInfo) next.getTag()).rank;
                    list.add(new PredictedAppIcon.PredictedIconOutlineDrawing(this.mHotseat.getCellXFromOrder(i), this.mHotseat.getCellYFromOrder(i), next));
                    next.setEnabled(false);
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(next, LauncherAnimUtils.SCALE_PROPERTY, new float[]{0.0f});
                    ofFloat.addListener(new AnimationSuccessListener() {
                        public void onAnimationSuccess(Animator animator) {
                            if (next.getParent() != null) {
                                HotseatPredictionController.this.removeIconWithoutNotify(next);
                            }
                        }
                    });
                    this.mIconRemoveAnimators.play(ofFloat);
                } else {
                    removeIconWithoutNotify(next);
                }
            }
        }
        this.mIconRemoveAnimators.start();
    }

    /* access modifiers changed from: private */
    public void removeIconWithoutNotify(PredictedAppIcon predictedAppIcon) {
        this.mPauseFlags |= 8;
        this.mHotseat.removeView(predictedAppIcon);
        this.mPauseFlags &= -9;
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        removePredictedApps(this.mOutlineDrawings, dragObject);
        if (!this.mOutlineDrawings.isEmpty()) {
            for (PredictedAppIcon.PredictedIconOutlineDrawing addDelegatedCellDrawing : this.mOutlineDrawings) {
                this.mHotseat.addDelegatedCellDrawing(addDelegatedCellDrawing);
            }
            this.mPauseFlags |= 2;
            this.mHotseat.invalidate();
        }
    }

    public void onDragEnd() {
        this.mPauseFlags &= -3;
        fillGapsWithPrediction(true);
    }

    public SystemShortcut<QuickstepLauncher> getShortcut(QuickstepLauncher quickstepLauncher, ItemInfo itemInfo, View view) {
        if (itemInfo.container != -103) {
            return null;
        }
        return new PinPrediction(quickstepLauncher, itemInfo, view);
    }

    private void preparePredictionInfo(WorkspaceItemInfo workspaceItemInfo, int i) {
        workspaceItemInfo.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION;
        workspaceItemInfo.rank = i;
        workspaceItemInfo.cellX = this.mHotseat.getCellXFromOrder(i);
        workspaceItemInfo.cellY = this.mHotseat.getCellYFromOrder(i);
        workspaceItemInfo.screenId = i;
    }

    public void onDeviceProfileChanged(DeviceProfile deviceProfile) {
        this.mHotSeatItemsCount = deviceProfile.numShownHotseatIcons;
    }

    public void logLaunchedAppRankingInfo(ItemInfo itemInfo, InstanceId instanceId) {
        ComponentName targetComponent = itemInfo.getTargetComponent();
        if (targetComponent != null) {
            int size = this.mPredictedItems.size() - 1;
            while (true) {
                if (size < 0) {
                    size = -1;
                    break;
                }
                ItemInfo itemInfo2 = this.mPredictedItems.get(size);
                if (targetComponent.equals(itemInfo2.getTargetComponent()) && itemInfo.user.equals(itemInfo2.user)) {
                    break;
                }
                size--;
            }
            if (size >= 0) {
                int i = 0;
                for (PredictedAppIcon tag : getPredictedIcons()) {
                    i |= 1 << ((ItemInfo) tag.getTag()).screenId;
                }
                LauncherAtom.PredictedHotseatContainer.Builder newBuilder = LauncherAtom.PredictedHotseatContainer.newBuilder();
                newBuilder.setCardinality(i);
                if (itemInfo.container == -103) {
                    newBuilder.setIndex(size);
                }
                this.mLauncher.getStatsLogManager().logger().withInstanceId(instanceId).withRank(size).withContainerInfo((LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setPredictedHotseatContainer(newBuilder).build()).log(StatsLogManager.LauncherEvent.LAUNCHER_HOTSEAT_RANKED);
            }
        }
    }

    public void onModelItemsRemoved(Predicate<ItemInfo> predicate) {
        if (this.mPredictedItems.removeIf(predicate)) {
            fillGapsWithPrediction(true);
        }
    }

    public void onDeferredDrop(int i, int i2) {
        View childAt = this.mHotseat.getChildAt(i, i2);
        if (childAt instanceof PredictedAppIcon) {
            removeIconWithoutNotify((PredictedAppIcon) childAt);
        }
    }

    private class PinPrediction extends SystemShortcut<QuickstepLauncher> {
        private PinPrediction(QuickstepLauncher quickstepLauncher, ItemInfo itemInfo, View view) {
            super(R.drawable.ic_pin, R.string.pin_prediction, quickstepLauncher, itemInfo, view);
        }

        public void onClick(View view) {
            dismissTaskMenuView((QuickstepLauncher) this.mTarget);
            HotseatPredictionController.this.pinPrediction(this.mItemInfo);
        }
    }

    private static boolean isPredictedIcon(View view) {
        return (view instanceof PredictedAppIcon) && (view.getTag() instanceof WorkspaceItemInfo) && ((WorkspaceItemInfo) view.getTag()).container == -103;
    }
}
