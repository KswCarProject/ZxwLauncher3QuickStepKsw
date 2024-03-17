package com.android.quickstep.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.widget.Button;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.touch.PagedOrientationHandler;

public class ClearAllButton extends Button {
    public static final FloatProperty<ClearAllButton> DISMISS_ALPHA = new FloatProperty<ClearAllButton>("dismissAlpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(ClearAllButton clearAllButton) {
            return Float.valueOf(clearAllButton.mDismissAlpha);
        }

        public void setValue(ClearAllButton clearAllButton, float f) {
            clearAllButton.setDismissAlpha(f);
        }
    };
    public static final FloatProperty<ClearAllButton> VISIBILITY_ALPHA = new FloatProperty<ClearAllButton>("visibilityAlpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(ClearAllButton clearAllButton) {
            return Float.valueOf(clearAllButton.mVisibilityAlpha);
        }

        public void setValue(ClearAllButton clearAllButton, float f) {
            clearAllButton.setVisibilityAlpha(f);
        }
    };
    private final StatefulActivity mActivity;
    private float mContentAlpha = 1.0f;
    /* access modifiers changed from: private */
    public float mDismissAlpha = 1.0f;
    private float mFullscreenProgress = 1.0f;
    private float mFullscreenTranslationPrimary;
    private float mGridProgress = 1.0f;
    private float mGridScrollOffset;
    private float mGridTranslationPrimary;
    private boolean mIsRtl;
    private float mNormalTranslationPrimary;
    private float mScrollAlpha = 1.0f;
    private float mScrollOffsetPrimary;
    private int mSidePadding;
    private float mSplitSelectScrollOffsetPrimary;
    /* access modifiers changed from: private */
    public float mVisibilityAlpha = 1.0f;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public ClearAllButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsRtl = getLayoutDirection() != 1 ? false : true;
        this.mActivity = (StatefulActivity) StatefulActivity.fromContext(context);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mSidePadding = getRecentsView().getPagedOrientationHandler().getClearAllSidePadding(getRecentsView(), this.mIsRtl);
    }

    private RecentsView getRecentsView() {
        return (RecentsView) getParent();
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        boolean z = true;
        if (getLayoutDirection() != 1) {
            z = false;
        }
        this.mIsRtl = z;
    }

    public void setContentAlpha(float f) {
        if (this.mContentAlpha != f) {
            this.mContentAlpha = f;
            updateAlpha();
        }
    }

    public void setVisibilityAlpha(float f) {
        if (this.mVisibilityAlpha != f) {
            this.mVisibilityAlpha = f;
            updateAlpha();
        }
    }

    public void setDismissAlpha(float f) {
        if (this.mDismissAlpha != f) {
            this.mDismissAlpha = f;
            updateAlpha();
        }
    }

    public void onRecentsViewScroll(int i, boolean z) {
        RecentsView recentsView = getRecentsView();
        if (recentsView != null) {
            float primaryValue = (float) recentsView.getPagedOrientationHandler().getPrimaryValue(getWidth(), getHeight());
            if (primaryValue != 0.0f) {
                int clearAllScroll = recentsView.getClearAllScroll();
                float min = Math.min((float) Math.abs(i - clearAllScroll), primaryValue);
                if (this.mIsRtl) {
                    min = -min;
                }
                this.mNormalTranslationPrimary = min;
                if (!z) {
                    this.mNormalTranslationPrimary = min + ((float) this.mSidePadding);
                }
                applyPrimaryTranslation();
                applySecondaryTranslation();
                float pageSpacing = (float) (recentsView.getPageSpacing() + recentsView.getClearAllExtraPageSpacing());
                if (this.mIsRtl) {
                    pageSpacing = -pageSpacing;
                }
                this.mScrollAlpha = Math.max(((((float) clearAllScroll) + pageSpacing) - ((float) i)) / pageSpacing, 0.0f);
                updateAlpha();
            }
        }
    }

    private void updateAlpha() {
        float f = this.mScrollAlpha * this.mContentAlpha * this.mVisibilityAlpha * this.mDismissAlpha;
        setAlpha(f);
        setClickable(Math.min(f, 1.0f) == 1.0f);
    }

    public void setFullscreenTranslationPrimary(float f) {
        this.mFullscreenTranslationPrimary = f;
        applyPrimaryTranslation();
    }

    public void setGridTranslationPrimary(float f) {
        this.mGridTranslationPrimary = f;
        applyPrimaryTranslation();
    }

    public void setGridScrollOffset(float f) {
        this.mGridScrollOffset = f;
    }

    public void setScrollOffsetPrimary(float f) {
        this.mScrollOffsetPrimary = f;
    }

    public void setSplitSelectScrollOffsetPrimary(float f) {
        this.mSplitSelectScrollOffsetPrimary = f;
    }

    public float getScrollAdjustment(boolean z, boolean z2) {
        float f = 0.0f;
        if (z) {
            f = 0.0f + this.mFullscreenTranslationPrimary;
        }
        if (z2) {
            f += this.mGridTranslationPrimary + this.mGridScrollOffset;
        }
        return f + this.mScrollOffsetPrimary + this.mSplitSelectScrollOffsetPrimary;
    }

    public float getOffsetAdjustment(boolean z, boolean z2) {
        return getScrollAdjustment(z, z2);
    }

    public void setFullscreenProgress(float f) {
        this.mFullscreenProgress = f;
        applyPrimaryTranslation();
    }

    public void setGridProgress(float f) {
        this.mGridProgress = f;
        applyPrimaryTranslation();
    }

    private void applyPrimaryTranslation() {
        RecentsView recentsView = getRecentsView();
        if (recentsView != null) {
            PagedOrientationHandler pagedOrientationHandler = recentsView.getPagedOrientationHandler();
            pagedOrientationHandler.getPrimaryViewTranslate().set(this, Float.valueOf(pagedOrientationHandler.getPrimaryValue(0.0f, getOriginalTranslationY()) + this.mNormalTranslationPrimary + getFullscreenTrans(this.mFullscreenTranslationPrimary) + getGridTrans(this.mGridTranslationPrimary)));
        }
    }

    private void applySecondaryTranslation() {
        RecentsView recentsView = getRecentsView();
        if (recentsView != null) {
            PagedOrientationHandler pagedOrientationHandler = recentsView.getPagedOrientationHandler();
            pagedOrientationHandler.getSecondaryViewTranslate().set(this, Float.valueOf(pagedOrientationHandler.getSecondaryValue(0.0f, getOriginalTranslationY())));
        }
    }

    private float getFullscreenTrans(float f) {
        if (this.mFullscreenProgress > 0.0f) {
            return f;
        }
        return 0.0f;
    }

    private float getGridTrans(float f) {
        if (this.mGridProgress > 0.0f) {
            return f;
        }
        return 0.0f;
    }

    private float getOriginalTranslationY() {
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        if (deviceProfile.isTablet) {
            return (float) deviceProfile.overviewRowSpacing;
        }
        return ((float) deviceProfile.overviewTaskThumbnailTopMarginPx) / 2.0f;
    }
}
