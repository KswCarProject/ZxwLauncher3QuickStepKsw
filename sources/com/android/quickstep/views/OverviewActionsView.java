package com.android.quickstep.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.R;
import com.android.launcher3.uioverrides.ApiWrapper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.TaskOverlayFactory.OverlayUICallbacks;
import com.android.quickstep.util.LayoutUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class OverviewActionsView<T extends TaskOverlayFactory.OverlayUICallbacks> extends FrameLayout implements View.OnClickListener, Insettable {
    public static final int DISABLED_NO_THUMBNAIL = 4;
    public static final int DISABLED_ROTATED = 2;
    public static final int DISABLED_SCROLLING = 1;
    public static final int HIDDEN_NON_ZERO_ROTATION = 1;
    public static final int HIDDEN_NO_RECENTS = 4;
    public static final int HIDDEN_NO_TASKS = 2;
    public static final int HIDDEN_SPLIT_SCREEN = 8;
    private static final int INDEX_CONTENT_ALPHA = 0;
    private static final int INDEX_FULLSCREEN_ALPHA = 2;
    private static final int INDEX_HIDDEN_FLAGS_ALPHA = 3;
    private static final int INDEX_VISIBILITY_ALPHA = 1;
    protected T mCallbacks;
    protected int mDisabledFlags;
    protected DeviceProfile mDp;
    private int mHiddenFlags;
    private final Rect mInsets;
    private final MultiValueAlpha mMultiValueAlpha;
    private Button mSplitButton;
    private final Rect mTaskSize;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionsDisabledFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionsHiddenFlags {
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public OverviewActionsView(Context context) {
        this(context, (AttributeSet) null);
    }

    public OverviewActionsView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OverviewActionsView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, 0);
        this.mInsets = new Rect();
        this.mTaskSize = new Rect();
        MultiValueAlpha multiValueAlpha = new MultiValueAlpha(this, 5);
        this.mMultiValueAlpha = multiValueAlpha;
        multiValueAlpha.setUpdateVisibility(true);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.action_screenshot).setOnClickListener(this);
        Button button = (Button) findViewById(R.id.action_split);
        this.mSplitButton = button;
        button.setOnClickListener(this);
    }

    public void setCallbacks(T t) {
        this.mCallbacks = t;
    }

    public void onClick(View view) {
        if (this.mCallbacks != null) {
            int id = view.getId();
            if (id == R.id.action_screenshot) {
                this.mCallbacks.onScreenshot();
            } else if (id == R.id.action_split) {
                Log.d("OverviewActions", "onClick action_split");
                this.mCallbacks.onSplit();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateVerticalMargin(DisplayController.getNavigationMode(getContext()));
    }

    public void setInsets(Rect rect) {
        this.mInsets.set(rect);
        updateVerticalMargin(DisplayController.getNavigationMode(getContext()));
        updatePadding();
    }

    public void updateHiddenFlags(int i, boolean z) {
        if (z) {
            this.mHiddenFlags = i | this.mHiddenFlags;
        } else {
            this.mHiddenFlags = (~i) & this.mHiddenFlags;
        }
        this.mMultiValueAlpha.getProperty(3).setValue(this.mHiddenFlags != 0 ? 0.0f : 1.0f);
    }

    public void updateDisabledFlags(int i, boolean z) {
        if (z) {
            this.mDisabledFlags = i | this.mDisabledFlags;
        } else {
            this.mDisabledFlags = (~i) & this.mDisabledFlags;
        }
        LayoutUtils.setViewEnabled(this, (this.mDisabledFlags & -3) == 0);
    }

    public MultiValueAlpha.AlphaProperty getContentAlpha() {
        return this.mMultiValueAlpha.getProperty(0);
    }

    public MultiValueAlpha.AlphaProperty getVisibilityAlpha() {
        return this.mMultiValueAlpha.getProperty(1);
    }

    public MultiValueAlpha.AlphaProperty getFullscreenAlpha() {
        return this.mMultiValueAlpha.getProperty(2);
    }

    private void updatePadding() {
        DeviceProfile deviceProfile = this.mDp;
        if (deviceProfile != null) {
            if (deviceProfile.isTaskbarPresent && !this.mDp.isGestureMode) {
                int hotseatEndOffset = ApiWrapper.getHotseatEndOffset(getContext());
                if (isLayoutRtl()) {
                    setPadding(this.mInsets.left + hotseatEndOffset, 0, this.mInsets.right, 0);
                } else {
                    setPadding(this.mInsets.left, 0, this.mInsets.right + hotseatEndOffset, 0);
                }
            } else {
                setPadding(this.mInsets.left, 0, this.mInsets.right, 0);
            }
        }
    }

    public void updateVerticalMargin(DisplayController.NavigationMode navigationMode) {
        if (this.mDp != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) findViewById(R.id.action_buttons).getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, this.mDp.overviewActionsTopMarginPx, layoutParams.rightMargin, getBottomMargin());
        }
    }

    private int getBottomMargin() {
        DeviceProfile deviceProfile = this.mDp;
        if (deviceProfile == null) {
            return 0;
        }
        if (deviceProfile.isVerticalBarLayout()) {
            return this.mDp.getInsets().bottom;
        }
        if (this.mDp.isGestureMode || !this.mDp.isTaskbarPresent) {
            return ((this.mDp.heightPx - this.mTaskSize.bottom) - this.mDp.overviewActionsTopMarginPx) - this.mDp.overviewActionsHeight;
        }
        return this.mDp.getOverviewActionsClaimedSpaceBelow();
    }

    public void updateDimension(DeviceProfile deviceProfile, Rect rect) {
        this.mDp = deviceProfile;
        this.mTaskSize.set(rect);
        updateVerticalMargin(DisplayController.getNavigationMode(getContext()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(deviceProfile.isVerticalBarLayout() ? 0 : deviceProfile.overviewActionsButtonSpacing, -1);
        layoutParams.weight = deviceProfile.isVerticalBarLayout() ? 1.0f : 0.0f;
        findViewById(R.id.action_split_space).setLayoutParams(layoutParams);
        requestLayout();
        this.mSplitButton.setCompoundDrawablesWithIntrinsicBounds(deviceProfile.isLandscape ? R.drawable.ic_split_horizontal : R.drawable.ic_split_vertical, 0, 0, 0);
    }

    public void setSplitButtonVisible(boolean z) {
        Button button = this.mSplitButton;
        if (button != null) {
            int i = 0;
            button.setVisibility(z ? 0 : 8);
            View findViewById = findViewById(R.id.action_split_space);
            if (!z) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
    }
}
