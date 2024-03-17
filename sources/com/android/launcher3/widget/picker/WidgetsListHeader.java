package com.android.launcher3.widget.picker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.icons.PlaceHolderIconDrawable;
import com.android.launcher3.icons.cache.HandlerRunnable;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.util.PluralMessageFormat;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.model.WidgetsListHeaderEntry;
import com.android.launcher3.widget.model.WidgetsListSearchHeaderEntry;
import com.android.launcher3.widget.picker.WidgetsListHeader;
import java.util.stream.Collectors;

public final class WidgetsListHeader extends LinearLayout implements IconCache.ItemInfoUpdateReceiver {
    private ImageView mAppIcon;
    private boolean mEnableIconUpdateAnimation;
    private CheckBox mExpandToggle;
    private Drawable mIconDrawable;
    private HandlerRunnable mIconLoadRequest;
    private final int mIconSize;
    /* access modifiers changed from: private */
    public boolean mIsExpanded;
    private WidgetsListDrawableState mListDrawableState;
    private TextView mSubtitle;
    private TextView mTitle;

    public interface OnExpansionChangeListener {
        void onExpansionChange(boolean z);
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

    public WidgetsListHeader(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetsListHeader(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetsListHeader(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mEnableIconUpdateAnimation = false;
        this.mIsExpanded = false;
        this.mIconSize = context.obtainStyledAttributes(attributeSet, R.styleable.WidgetsListRowHeader, i, 0).getDimensionPixelSize(0, ((ActivityContext) ActivityContext.lookupContext(context)).getDeviceProfile().iconSizePx);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mAppIcon = (ImageView) findViewById(R.id.app_icon);
        this.mTitle = (TextView) findViewById(R.id.app_title);
        this.mSubtitle = (TextView) findViewById(R.id.app_subtitle);
        this.mExpandToggle = (CheckBox) findViewById(R.id.toggle);
        setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                if (WidgetsListHeader.this.mIsExpanded) {
                    accessibilityNodeInfo.removeAction(262144);
                    accessibilityNodeInfo.addAction(524288);
                } else {
                    accessibilityNodeInfo.removeAction(524288);
                    accessibilityNodeInfo.addAction(262144);
                }
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
            }

            public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
                if (i != 262144 && i != 524288) {
                    return super.performAccessibilityAction(view, i, bundle);
                }
                WidgetsListHeader.this.callOnClick();
                return true;
            }
        });
    }

    public void setOnExpandChangeListener(OnExpansionChangeListener onExpansionChangeListener) {
        setOnClickListener(new View.OnClickListener(onExpansionChangeListener) {
            public final /* synthetic */ WidgetsListHeader.OnExpansionChangeListener f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                WidgetsListHeader.this.lambda$setOnExpandChangeListener$0$WidgetsListHeader(this.f$1, view);
            }
        });
    }

    public /* synthetic */ void lambda$setOnExpandChangeListener$0$WidgetsListHeader(OnExpansionChangeListener onExpansionChangeListener, View view) {
        setExpanded(!this.mIsExpanded);
        if (onExpansionChangeListener != null) {
            onExpansionChangeListener.onExpansionChange(this.mIsExpanded);
        }
    }

    public void setExpanded(boolean z) {
        this.mIsExpanded = z;
        this.mExpandToggle.setChecked(z);
    }

    public void setListDrawableState(WidgetsListDrawableState widgetsListDrawableState) {
        if (widgetsListDrawableState != this.mListDrawableState) {
            this.mListDrawableState = widgetsListDrawableState;
            refreshDrawableState();
        }
    }

    public void applyFromItemInfoWithIcon(WidgetsListHeaderEntry widgetsListHeaderEntry) {
        applyIconAndLabel(widgetsListHeaderEntry);
    }

    private void applyIconAndLabel(WidgetsListHeaderEntry widgetsListHeaderEntry) {
        PackageItemInfo packageItemInfo = widgetsListHeaderEntry.mPkgItem;
        setIcon(packageItemInfo);
        setTitles(widgetsListHeaderEntry);
        setExpanded(widgetsListHeaderEntry.isWidgetListShown());
        super.setTag(packageItemInfo);
        verifyHighRes();
    }

    private void setIcon(PackageItemInfo packageItemInfo) {
        FastBitmapDrawable newIcon = packageItemInfo.newIcon(getContext());
        applyDrawables(newIcon);
        this.mIconDrawable = newIcon;
        if (newIcon != null) {
            newIcon.setVisible(getWindowVisibility() == 0 && isShown(), false);
        }
    }

    private void applyDrawables(Drawable drawable) {
        int i = this.mIconSize;
        drawable.setBounds(0, 0, i, i);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mAppIcon.getLayoutParams();
        layoutParams.width = this.mIconSize;
        layoutParams.height = this.mIconSize;
        this.mAppIcon.setLayoutParams(layoutParams);
        this.mAppIcon.setImageDrawable(drawable);
        Drawable drawable2 = this.mIconDrawable;
        if (drawable2 != null && (drawable2 instanceof PlaceHolderIconDrawable) && this.mEnableIconUpdateAnimation) {
            ((PlaceHolderIconDrawable) drawable2).animateIconUpdate(drawable);
        }
    }

    private void setTitles(WidgetsListHeaderEntry widgetsListHeaderEntry) {
        String str;
        this.mTitle.setText(widgetsListHeaderEntry.mPkgItem.title);
        Resources resources = getContext().getResources();
        if (widgetsListHeaderEntry.widgetsCount == 0 && widgetsListHeaderEntry.shortcutsCount == 0) {
            this.mSubtitle.setVisibility(8);
            return;
        }
        if (widgetsListHeaderEntry.widgetsCount > 0 && widgetsListHeaderEntry.shortcutsCount > 0) {
            str = resources.getString(R.string.widgets_and_shortcuts_count, new Object[]{PluralMessageFormat.getIcuPluralString(getContext(), R.string.widgets_count, widgetsListHeaderEntry.widgetsCount), PluralMessageFormat.getIcuPluralString(getContext(), R.string.shortcuts_count, widgetsListHeaderEntry.shortcutsCount)});
        } else if (widgetsListHeaderEntry.widgetsCount > 0) {
            str = PluralMessageFormat.getIcuPluralString(getContext(), R.string.widgets_count, widgetsListHeaderEntry.widgetsCount);
        } else {
            str = PluralMessageFormat.getIcuPluralString(getContext(), R.string.shortcuts_count, widgetsListHeaderEntry.shortcutsCount);
        }
        this.mSubtitle.setText(str);
        this.mSubtitle.setVisibility(0);
    }

    public void applyFromItemInfoWithIcon(WidgetsListSearchHeaderEntry widgetsListSearchHeaderEntry) {
        applyIconAndLabel(widgetsListSearchHeaderEntry);
    }

    private void applyIconAndLabel(WidgetsListSearchHeaderEntry widgetsListSearchHeaderEntry) {
        PackageItemInfo packageItemInfo = widgetsListSearchHeaderEntry.mPkgItem;
        setIcon(packageItemInfo);
        setTitles(widgetsListSearchHeaderEntry);
        setExpanded(widgetsListSearchHeaderEntry.isWidgetListShown());
        super.setTag(packageItemInfo);
        verifyHighRes();
    }

    private void setTitles(WidgetsListSearchHeaderEntry widgetsListSearchHeaderEntry) {
        this.mTitle.setText(widgetsListSearchHeaderEntry.mPkgItem.title);
        this.mSubtitle.setText((CharSequence) widgetsListSearchHeaderEntry.mWidgets.stream().map($$Lambda$WidgetsListHeader$_0y1HYohT51oD6u4hRX6zK8Ou3E.INSTANCE).sorted().collect(Collectors.joining(", ")));
        this.mSubtitle.setVisibility(0);
    }

    public void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon) {
        if (getTag() == itemInfoWithIcon) {
            this.mIconLoadRequest = null;
            this.mEnableIconUpdateAnimation = true;
            itemInfoWithIcon.bitmap.icon.prepareToDraw();
            setIcon((PackageItemInfo) itemInfoWithIcon);
            this.mEnableIconUpdateAnimation = false;
        }
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int i) {
        WidgetsListDrawableState widgetsListDrawableState = this.mListDrawableState;
        if (widgetsListDrawableState == null) {
            return super.onCreateDrawableState(i);
        }
        int[] onCreateDrawableState = super.onCreateDrawableState(i + widgetsListDrawableState.mStateSet.length);
        mergeDrawableStates(onCreateDrawableState, this.mListDrawableState.mStateSet);
        return onCreateDrawableState;
    }

    public void verifyHighRes() {
        HandlerRunnable handlerRunnable = this.mIconLoadRequest;
        if (handlerRunnable != null) {
            handlerRunnable.cancel();
            this.mIconLoadRequest = null;
        }
        if (getTag() instanceof ItemInfoWithIcon) {
            ItemInfoWithIcon itemInfoWithIcon = (ItemInfoWithIcon) getTag();
            if (itemInfoWithIcon.usingLowResIcon()) {
                this.mIconLoadRequest = LauncherAppState.getInstance(getContext()).getIconCache().updateIconInBackground(this, itemInfoWithIcon);
            }
        }
    }
}
