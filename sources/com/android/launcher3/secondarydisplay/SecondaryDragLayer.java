package com.android.launcher3.secondarydisplay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.ShortcutUtil;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import java.util.Arrays;
import java.util.Collections;

public class SecondaryDragLayer extends BaseDragLayer<SecondaryDisplayLauncher> {
    private View mAllAppsButton;
    private ActivityAllAppsContainerView<SecondaryDisplayLauncher> mAppsView;
    private PinnedAppsAdapter mPinnedAppsAdapter;
    private GridView mWorkspace;

    public SecondaryDragLayer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 1);
        recreateControllers();
    }

    public void recreateControllers() {
        this.mControllers = new TouchController[]{new CloseAllAppsTouchController()};
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mAllAppsButton = findViewById(R.id.all_apps_button);
        ActivityAllAppsContainerView<SecondaryDisplayLauncher> activityAllAppsContainerView = (ActivityAllAppsContainerView) findViewById(R.id.apps_view);
        this.mAppsView = activityAllAppsContainerView;
        activityAllAppsContainerView.setOnIconLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return SecondaryDragLayer.this.onIconLongClicked(view);
            }
        });
        this.mWorkspace = (GridView) findViewById(R.id.workspace_grid);
        PinnedAppsAdapter pinnedAppsAdapter = new PinnedAppsAdapter((SecondaryDisplayLauncher) this.mActivity, this.mAppsView.getAppsStore(), new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return SecondaryDragLayer.this.onIconLongClicked(view);
            }
        });
        this.mPinnedAppsAdapter = pinnedAppsAdapter;
        this.mWorkspace.setAdapter(pinnedAppsAdapter);
        this.mWorkspace.setNumColumns(((SecondaryDisplayLauncher) this.mActivity).getDeviceProfile().inv.numColumns);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mPinnedAppsAdapter.init();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mPinnedAppsAdapter.destroy();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        DeviceProfile deviceProfile = ((SecondaryDisplayLauncher) this.mActivity).getDeviceProfile();
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt == this.mAppsView) {
                int i4 = (deviceProfile.desiredWorkspaceHorizontalMarginPx * 2) + deviceProfile.cellLayoutPaddingPx.left + deviceProfile.cellLayoutPaddingPx.right;
                int i5 = deviceProfile.cellLayoutPaddingPx.top + deviceProfile.cellLayoutPaddingPx.bottom;
                this.mAppsView.measure(View.MeasureSpec.makeMeasureSpec(Math.min((size - getPaddingLeft()) - getPaddingRight(), (deviceProfile.allAppsCellWidthPx * deviceProfile.numShownAllAppsColumns) + i4), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(Math.min((size2 - getPaddingTop()) - getPaddingBottom(), (deviceProfile.allAppsCellHeightPx * deviceProfile.numShownAllAppsColumns) + i5), BasicMeasure.EXACTLY));
            } else if (childAt == this.mAllAppsButton) {
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceProfile.iconSizePx, BasicMeasure.EXACTLY);
                this.mAllAppsButton.measure(makeMeasureSpec, makeMeasureSpec);
            } else {
                GridView gridView = this.mWorkspace;
                if (childAt == gridView) {
                    measureChildWithMargins(gridView, i, 0, i2, deviceProfile.iconSizePx + deviceProfile.edgeMarginPx);
                } else {
                    measureChildWithMargins(childAt, i, 0, i2, 0);
                }
            }
        }
    }

    private class CloseAllAppsTouchController implements TouchController {
        public boolean onControllerTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        private CloseAllAppsTouchController() {
        }

        public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
            if (((SecondaryDisplayLauncher) SecondaryDragLayer.this.mActivity).isAppDrawerShown() && AbstractFloatingView.getTopOpenView((ActivityContext) SecondaryDragLayer.this.mActivity) == null && motionEvent.getAction() == 0) {
                SecondaryDragLayer secondaryDragLayer = SecondaryDragLayer.this;
                if (!secondaryDragLayer.isEventOverView(((SecondaryDisplayLauncher) secondaryDragLayer.mActivity).getAppsView(), motionEvent)) {
                    ((SecondaryDisplayLauncher) SecondaryDragLayer.this.mActivity).showAppDrawer(false);
                    return true;
                }
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public boolean onIconLongClicked(View view) {
        PopupDataProvider popupDataProvider;
        if (!(view instanceof BubbleTextView)) {
            return false;
        }
        if (PopupContainerWithArrow.getOpen((SecondaryDisplayLauncher) this.mActivity) != null) {
            view.clearFocus();
            return false;
        }
        ItemInfo itemInfo = (ItemInfo) view.getTag();
        if (!ShortcutUtil.supportsShortcuts(itemInfo) || (popupDataProvider = ((SecondaryDisplayLauncher) this.mActivity).getPopupDataProvider()) == null) {
            return false;
        }
        ((PopupContainerWithArrow) ((SecondaryDisplayLauncher) this.mActivity).getLayoutInflater().inflate(R.layout.popup_container, ((SecondaryDisplayLauncher) this.mActivity).getDragLayer(), false)).populateAndShow((BubbleTextView) view, popupDataProvider.getShortcutCountForItem(itemInfo), Collections.emptyList(), Arrays.asList(new SystemShortcut[]{this.mPinnedAppsAdapter.getSystemShortcut(itemInfo, view), SystemShortcut.APP_INFO.getShortcut((BaseDraggingActivity) this.mActivity, itemInfo, view)}));
        view.getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }
}
