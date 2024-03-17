package com.android.launcher3.allapps;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Insettable;
import com.android.launcher3.R;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.uioverrides.plugins.PluginManagerWrapper;
import com.android.launcher3.views.ActivityContext;
import com.android.systemui.plugins.AllAppsRow;
import com.android.systemui.plugins.PluginListener;
import java.util.ArrayList;
import java.util.Map;

public class FloatingHeaderView extends LinearLayout implements ValueAnimator.AnimatorUpdateListener, PluginListener<AllAppsRow>, Insettable, AllAppsRow.OnHeightUpdatedListener {
    private FloatingHeaderRow[] mAllRows;
    /* access modifiers changed from: private */
    public final ValueAnimator mAnimator;
    private boolean mCollapsed;
    /* access modifiers changed from: private */
    public AllAppsRecyclerView mCurrentRV;
    private FloatingHeaderRow[] mFixedRows;
    private boolean mForwardToRecyclerView;
    private final int mHeaderBottomAdjustment;
    private final Rect mHeaderClip;
    public boolean mHeaderCollapsed;
    private final boolean mHeaderProtectionSupported;
    private final int mHeaderTopAdjustment;
    private AllAppsRecyclerView mMainRV;
    protected int mMaxTranslation;
    private final RecyclerView.OnScrollListener mOnScrollListener;
    protected final Map<AllAppsRow, PluginHeaderRow> mPluginRows;
    private final Rect mRVClip;
    private SearchRecyclerView mSearchRV;
    protected int mSnappedScrolledY;
    protected ViewGroup mTabLayout;
    protected boolean mTabsHidden;
    private final Point mTempOffset;
    private int mTranslationY;
    private AllAppsRecyclerView mWorkRV;

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

    public FloatingHeaderView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FloatingHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        boolean z = false;
        this.mRVClip = new Rect(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.mHeaderClip = new Rect(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.mAnimator = ValueAnimator.ofInt(new int[]{0, 0});
        this.mTempOffset = new Point();
        this.mOnScrollListener = new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (recyclerView == FloatingHeaderView.this.mCurrentRV) {
                    if (FloatingHeaderView.this.mAnimator.isStarted()) {
                        FloatingHeaderView.this.mAnimator.cancel();
                    }
                    boolean z = FloatingHeaderView.this.mHeaderCollapsed;
                    FloatingHeaderView.this.moved(-FloatingHeaderView.this.mCurrentRV.getCurrentScrollY());
                    FloatingHeaderView.this.applyVerticalMove();
                    if (z != FloatingHeaderView.this.mHeaderCollapsed) {
                        ((BaseAllAppsContainerView) FloatingHeaderView.this.getParent()).invalidateHeader();
                    }
                }
            }
        };
        this.mPluginRows = new ArrayMap();
        this.mCollapsed = false;
        this.mFixedRows = FloatingHeaderRow.NO_ROWS;
        this.mAllRows = FloatingHeaderRow.NO_ROWS;
        this.mHeaderTopAdjustment = context.getResources().getDimensionPixelSize(R.dimen.all_apps_header_top_adjustment);
        this.mHeaderBottomAdjustment = context.getResources().getDimensionPixelSize(R.dimen.all_apps_header_bottom_adjustment);
        if (context.getResources().getBoolean(R.bool.config_header_protection_supported) && !((ActivityContext) ActivityContext.lookupContext(context)).getDeviceProfile().isTablet) {
            z = true;
        }
        this.mHeaderProtectionSupported = z;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTabLayout = (ViewGroup) findViewById(R.id.tabs);
        ArrayList arrayList = new ArrayList();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof FloatingHeaderRow) {
                arrayList.add((FloatingHeaderRow) childAt);
            }
        }
        FloatingHeaderRow[] floatingHeaderRowArr = (FloatingHeaderRow[]) arrayList.toArray(new FloatingHeaderRow[arrayList.size()]);
        this.mFixedRows = floatingHeaderRowArr;
        this.mAllRows = floatingHeaderRowArr;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).addPluginListener(this, AllAppsRow.class, true);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        PluginManagerWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).removePluginListener(this);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        this.mTabLayout.getLayoutParams().width = getTabWidth();
        super.onMeasure(i, i2);
    }

    public int getTabWidth() {
        DeviceProfile deviceProfile = ((ActivityContext) ActivityContext.lookupContext(getContext())).getDeviceProfile();
        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        return (measuredWidth - ((measuredWidth / deviceProfile.numShownAllAppsColumns) - deviceProfile.allAppsIconSizePx)) - deviceProfile.allAppsIconDrawablePaddingPx;
    }

    private void recreateAllRowsArray() {
        int size = this.mPluginRows.size();
        if (size == 0) {
            this.mAllRows = this.mFixedRows;
            return;
        }
        int length = this.mFixedRows.length;
        this.mAllRows = new FloatingHeaderRow[(size + length)];
        for (int i = 0; i < length; i++) {
            this.mAllRows[i] = this.mFixedRows[i];
        }
        for (PluginHeaderRow pluginHeaderRow : this.mPluginRows.values()) {
            this.mAllRows[length] = pluginHeaderRow;
            length++;
        }
    }

    public void onPluginConnected(AllAppsRow allAppsRow, Context context) {
        PluginHeaderRow pluginHeaderRow = new PluginHeaderRow(allAppsRow, this);
        addView(pluginHeaderRow.mView, indexOfChild(this.mTabLayout));
        this.mPluginRows.put(allAppsRow, pluginHeaderRow);
        recreateAllRowsArray();
        allAppsRow.setOnHeightUpdatedListener(this);
    }

    public void onHeightUpdated() {
        BaseAllAppsContainerView baseAllAppsContainerView;
        int i = this.mMaxTranslation;
        updateExpectedHeight();
        if ((this.mMaxTranslation != i || this.mCollapsed) && (baseAllAppsContainerView = (BaseAllAppsContainerView) getParent()) != null) {
            baseAllAppsContainerView.setupHeader();
        }
    }

    public void onPluginDisconnected(AllAppsRow allAppsRow) {
        removeView(this.mPluginRows.get(allAppsRow).mView);
        this.mPluginRows.remove(allAppsRow);
        recreateAllRowsArray();
        onHeightUpdated();
    }

    public View getFocusedChild() {
        if (!FeatureFlags.ENABLE_DEVICE_SEARCH.get()) {
            return super.getFocusedChild();
        }
        for (FloatingHeaderRow floatingHeaderRow : this.mAllRows) {
            if (floatingHeaderRow.hasVisibleContent() && floatingHeaderRow.isVisible()) {
                return floatingHeaderRow.getFocusedChild();
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void setup(AllAppsRecyclerView allAppsRecyclerView, AllAppsRecyclerView allAppsRecyclerView2, SearchRecyclerView searchRecyclerView, int i, boolean z) {
        for (FloatingHeaderRow upVar : this.mAllRows) {
            upVar.setup(this, this.mAllRows, z);
        }
        updateExpectedHeight();
        this.mTabsHidden = z;
        this.mTabLayout.setVisibility(z ? 8 : 0);
        this.mMainRV = allAppsRecyclerView;
        this.mWorkRV = allAppsRecyclerView2;
        this.mSearchRV = searchRecyclerView;
        setActiveRV(i);
        reset(false);
    }

    /* access modifiers changed from: package-private */
    public boolean isSetUp() {
        return this.mMainRV != null;
    }

    /* access modifiers changed from: package-private */
    public void setActiveRV(int i) {
        AllAppsRecyclerView allAppsRecyclerView;
        AllAppsRecyclerView allAppsRecyclerView2 = this.mCurrentRV;
        if (allAppsRecyclerView2 != null) {
            allAppsRecyclerView2.removeOnScrollListener(this.mOnScrollListener);
        }
        if (i == 0) {
            allAppsRecyclerView = this.mMainRV;
        } else {
            allAppsRecyclerView = i == 1 ? this.mWorkRV : this.mSearchRV;
        }
        this.mCurrentRV = allAppsRecyclerView;
        allAppsRecyclerView.addOnScrollListener(this.mOnScrollListener);
    }

    private void updateExpectedHeight() {
        this.mMaxTranslation = 0;
        if (!this.mCollapsed) {
            FloatingHeaderRow[] floatingHeaderRowArr = this.mAllRows;
            int length = floatingHeaderRowArr.length;
            for (int i = 0; i < length; i++) {
                this.mMaxTranslation += floatingHeaderRowArr[i].getExpectedHeight();
            }
            if (!this.mTabsHidden) {
                this.mMaxTranslation += this.mHeaderBottomAdjustment;
            }
        }
    }

    public int getMaxTranslation() {
        int i = this.mMaxTranslation;
        if (i != 0 || !this.mTabsHidden) {
            return (i <= 0 || !this.mTabsHidden) ? i : i + getPaddingTop();
        }
        return getResources().getDimensionPixelSize(R.dimen.all_apps_search_bar_bottom_padding);
    }

    private boolean canSnapAt(int i) {
        return Math.abs(i) <= this.mMaxTranslation;
    }

    /* access modifiers changed from: private */
    public void moved(int i) {
        if (this.mHeaderCollapsed) {
            if (i > this.mSnappedScrolledY) {
                this.mHeaderCollapsed = false;
            } else if (canSnapAt(i)) {
                this.mSnappedScrolledY = i;
            }
            this.mTranslationY = i;
            return;
        }
        int i2 = this.mMaxTranslation;
        int i3 = (i - this.mSnappedScrolledY) - i2;
        this.mTranslationY = i3;
        if (i3 >= 0) {
            this.mTranslationY = 0;
            this.mSnappedScrolledY = i - i2;
        } else if (i3 <= (-i2)) {
            this.mHeaderCollapsed = true;
            this.mSnappedScrolledY = -i2;
        }
    }

    /* access modifiers changed from: protected */
    public void applyVerticalMove() {
        int i = this.mTranslationY;
        int max = Math.max(i, -this.mMaxTranslation);
        this.mTranslationY = max;
        int i2 = 0;
        if (this.mCollapsed || i < max - getPaddingTop()) {
            for (FloatingHeaderRow verticalScroll : this.mAllRows) {
                verticalScroll.setVerticalScroll(0, true);
            }
        } else {
            for (FloatingHeaderRow verticalScroll2 : this.mAllRows) {
                verticalScroll2.setVerticalScroll(i, false);
            }
        }
        this.mTabLayout.setTranslationY((float) this.mTranslationY);
        int paddingTop = getPaddingTop() - this.mHeaderTopAdjustment;
        if (this.mTabsHidden) {
            paddingTop += getPaddingBottom() - this.mHeaderBottomAdjustment;
        }
        Rect rect = this.mRVClip;
        if (this.mTabsHidden) {
            i2 = paddingTop;
        }
        rect.top = i2;
        this.mHeaderClip.top = paddingTop;
        setClipBounds(this.mHeaderClip);
        AllAppsRecyclerView allAppsRecyclerView = this.mMainRV;
        if (allAppsRecyclerView != null) {
            allAppsRecyclerView.setClipBounds(this.mRVClip);
        }
        AllAppsRecyclerView allAppsRecyclerView2 = this.mWorkRV;
        if (allAppsRecyclerView2 != null) {
            allAppsRecyclerView2.setClipBounds(this.mRVClip);
        }
        SearchRecyclerView searchRecyclerView = this.mSearchRV;
        if (searchRecyclerView != null) {
            searchRecyclerView.setClipBounds(this.mRVClip);
        }
    }

    public void setCollapsed(boolean z) {
        if (this.mCollapsed != z) {
            this.mCollapsed = z;
            onHeightUpdated();
        }
    }

    public void reset(boolean z) {
        if (this.mAnimator.isStarted()) {
            this.mAnimator.cancel();
        }
        if (z) {
            this.mAnimator.setIntValues(new int[]{this.mTranslationY, 0});
            this.mAnimator.addUpdateListener(this);
            this.mAnimator.setDuration(150);
            this.mAnimator.start();
        } else {
            this.mTranslationY = 0;
            applyVerticalMove();
        }
        this.mHeaderCollapsed = false;
        this.mSnappedScrolledY = -this.mMaxTranslation;
        this.mCurrentRV.scrollToTop();
    }

    public boolean isExpanded() {
        return !this.mHeaderCollapsed;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.mTranslationY = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        applyVerticalMove();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        calcOffset(this.mTempOffset);
        motionEvent.offsetLocation((float) this.mTempOffset.x, (float) this.mTempOffset.y);
        this.mForwardToRecyclerView = this.mCurrentRV.onInterceptTouchEvent(motionEvent);
        motionEvent.offsetLocation((float) (-this.mTempOffset.x), (float) (-this.mTempOffset.y));
        return this.mForwardToRecyclerView || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mForwardToRecyclerView) {
            return super.onTouchEvent(motionEvent);
        }
        calcOffset(this.mTempOffset);
        motionEvent.offsetLocation((float) this.mTempOffset.x, (float) this.mTempOffset.y);
        try {
            return this.mCurrentRV.onTouchEvent(motionEvent);
        } finally {
            motionEvent.offsetLocation((float) (-this.mTempOffset.x), (float) (-this.mTempOffset.y));
        }
    }

    private void calcOffset(Point point) {
        point.x = (getLeft() - this.mCurrentRV.getLeft()) - ((ViewGroup) this.mCurrentRV.getParent()).getLeft();
        point.y = (getTop() - this.mCurrentRV.getTop()) - ((ViewGroup) this.mCurrentRV.getParent()).getTop();
    }

    public boolean hasVisibleContent() {
        for (FloatingHeaderRow hasVisibleContent : this.mAllRows) {
            if (hasVisibleContent.hasVisibleContent()) {
                return true;
            }
        }
        return false;
    }

    public boolean isHeaderProtectionSupported() {
        return this.mHeaderProtectionSupported;
    }

    public void setInsets(Rect rect) {
        DeviceProfile deviceProfile = ((ActivityContext) ActivityContext.lookupContext(getContext())).getDeviceProfile();
        for (FloatingHeaderRow insets : this.mAllRows) {
            insets.setInsets(rect, deviceProfile);
        }
    }

    public <T extends FloatingHeaderRow> T findFixedRowByType(Class<T> cls) {
        for (T t : this.mAllRows) {
            if (t.getTypeClass() == cls) {
                return t;
            }
        }
        return null;
    }

    public int getPeripheralProtectionHeight() {
        if (this.mHeaderProtectionSupported && !this.mTabsHidden && this.mHeaderCollapsed) {
            return Math.max((getHeight() - getPaddingTop()) + this.mTranslationY, 0);
        }
        return 0;
    }
}
