package com.android.launcher3.appprediction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.LinearLayout;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DeviceProfile.DeviceProfileListenable;
import com.android.launcher3.R;
import com.android.launcher3.allapps.FloatingHeaderRow;
import com.android.launcher3.allapps.FloatingHeaderView;
import com.android.launcher3.anim.AlphaUpdateListener;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.keyboard.FocusIndicatorHelper;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PredictionRowView<T extends Context & ActivityContext & DeviceProfile.DeviceProfileListenable> extends LinearLayout implements DeviceProfile.OnDeviceProfileChangeListener, FloatingHeaderRow {
    private final T mActivityContext;
    private final FocusIndicatorHelper mFocusHelper;
    private int mNumPredictedAppsPerRow;
    private View.OnLongClickListener mOnIconLongClickListener;
    private FloatingHeaderView mParent;
    private List<ItemInfo> mPendingPredictedItems;
    private final List<WorkspaceItemInfo> mPredictedApps;
    private boolean mPredictionsEnabled;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.generateLayoutParams(layoutParams);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public PredictionRowView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PredictionRowView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPredictedApps = new ArrayList();
        this.mPredictionsEnabled = false;
        this.mOnIconLongClickListener = ItemLongClickListener.INSTANCE_ALL_APPS;
        setOrientation(0);
        this.mFocusHelper = new FocusIndicatorHelper.SimpleFocusIndicatorHelper(this);
        T lookupContext = ActivityContext.lookupContext(context);
        this.mActivityContext = lookupContext;
        ((DeviceProfile.DeviceProfileListenable) lookupContext).addOnDeviceProfileChangeListener(this);
        this.mNumPredictedAppsPerRow = ((DeviceProfile.DeviceProfileListenable) lookupContext).getDeviceProfile().numShownAllAppsColumns;
        updateVisibility();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setup(FloatingHeaderView floatingHeaderView, FloatingHeaderRow[] floatingHeaderRowArr, boolean z) {
        this.mParent = floatingHeaderView;
    }

    private void updateVisibility() {
        setVisibility(this.mPredictionsEnabled ? 0 : 8);
        if (((ActivityContext) this.mActivityContext).getAppsView() == null) {
            return;
        }
        if (this.mPredictionsEnabled) {
            ((ActivityContext) this.mActivityContext).getAppsView().getAppsStore().registerIconContainer(this);
        } else {
            ((ActivityContext) this.mActivityContext).getAppsView().getAppsStore().unregisterIconContainer(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(getExpectedHeight(), BasicMeasure.EXACTLY));
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.mFocusHelper.draw(canvas);
        super.dispatchDraw(canvas);
    }

    public int getExpectedHeight() {
        if (getVisibility() == 8) {
            return 0;
        }
        return ((DeviceProfile.DeviceProfileListenable) this.mActivityContext).getDeviceProfile().allAppsCellHeightPx + getPaddingTop() + getPaddingBottom();
    }

    public boolean shouldDraw() {
        return getVisibility() != 8;
    }

    public boolean hasVisibleContent() {
        return this.mPredictionsEnabled;
    }

    public boolean isVisible() {
        return getVisibility() == 0;
    }

    public List<ItemInfoWithIcon> getPredictedApps() {
        return new ArrayList(this.mPredictedApps);
    }

    public void setPredictedApps(List<ItemInfo> list) {
        if (FeatureFlags.ENABLE_APP_PREDICTIONS_WHILE_VISIBLE.get() || ((ActivityContext) this.mActivityContext).isBindingItems() || !isShown() || getWindowVisibility() != 0) {
            applyPredictedApps(list);
        } else {
            this.mPendingPredictedItems = list;
        }
    }

    private void applyPredictedApps(List<ItemInfo> list) {
        this.mPendingPredictedItems = null;
        this.mPredictedApps.clear();
        this.mPredictedApps.addAll((Collection) list.stream().filter($$Lambda$PredictionRowView$makHrPxKWRPnPR3GTKWNqtBikVc.INSTANCE).map($$Lambda$PredictionRowView$8GQoRrHEo8uKMrzC3wBg_8QIBA.INSTANCE).collect(Collectors.toList()));
        applyPredictionApps();
    }

    static /* synthetic */ boolean lambda$applyPredictedApps$0(ItemInfo itemInfo) {
        return itemInfo instanceof WorkspaceItemInfo;
    }

    static /* synthetic */ WorkspaceItemInfo lambda$applyPredictedApps$1(ItemInfo itemInfo) {
        return (WorkspaceItemInfo) itemInfo;
    }

    public void setOnIconLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mOnIconLongClickListener = onLongClickListener;
    }

    public void onDeviceProfileChanged(DeviceProfile deviceProfile) {
        this.mNumPredictedAppsPerRow = deviceProfile.numShownAllAppsColumns;
        removeAllViews();
        applyPredictionApps();
    }

    private void applyPredictionApps() {
        boolean z = false;
        if (getChildCount() != this.mNumPredictedAppsPerRow) {
            while (getChildCount() > this.mNumPredictedAppsPerRow) {
                removeViewAt(0);
            }
            LayoutInflater layoutInflater = ((ActivityContext) this.mActivityContext).getAppsView().getLayoutInflater();
            while (getChildCount() < this.mNumPredictedAppsPerRow) {
                BubbleTextView bubbleTextView = (BubbleTextView) layoutInflater.inflate(R.layout.all_apps_icon, this, false);
                bubbleTextView.setOnClickListener(((ActivityContext) this.mActivityContext).getItemOnClickListener());
                bubbleTextView.setOnLongClickListener(this.mOnIconLongClickListener);
                bubbleTextView.setLongPressTimeoutFactor(1.0f);
                bubbleTextView.setOnFocusChangeListener(this.mFocusHelper);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bubbleTextView.getLayoutParams();
                layoutParams.height = ((DeviceProfile.DeviceProfileListenable) this.mActivityContext).getDeviceProfile().allAppsCellHeightPx;
                layoutParams.width = 0;
                layoutParams.weight = 1.0f;
                addView(bubbleTextView);
            }
        }
        int size = this.mPredictedApps.size();
        for (int i = 0; i < getChildCount(); i++) {
            BubbleTextView bubbleTextView2 = (BubbleTextView) getChildAt(i);
            bubbleTextView2.reset();
            if (size > i) {
                bubbleTextView2.setVisibility(0);
                bubbleTextView2.applyFromWorkspaceItem(this.mPredictedApps.get(i));
            } else {
                bubbleTextView2.setVisibility(size == 0 ? 8 : 4);
            }
        }
        if (size > 0) {
            z = true;
        }
        if (z != this.mPredictionsEnabled) {
            this.mPredictionsEnabled = z;
            updateVisibility();
        }
        this.mParent.onHeightUpdated();
    }

    public void setVerticalScroll(int i, boolean z) {
        if (!z) {
            setTranslationY((float) i);
        }
        setAlpha(z ? 0.0f : 1.0f);
        if (getVisibility() != 8) {
            AlphaUpdateListener.updateVisibility(this);
        }
    }

    public void setInsets(Rect rect, DeviceProfile deviceProfile) {
        int i = deviceProfile.allAppsLeftRightPadding;
        setPadding(i, getPaddingTop(), i, getPaddingBottom());
    }

    public Class<PredictionRowView> getTypeClass() {
        return PredictionRowView.class;
    }

    public View getFocusedChild() {
        return getChildAt(0);
    }

    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        List<ItemInfo> list = this.mPendingPredictedItems;
        if (list != null && !z) {
            applyPredictedApps(list);
        }
    }
}
