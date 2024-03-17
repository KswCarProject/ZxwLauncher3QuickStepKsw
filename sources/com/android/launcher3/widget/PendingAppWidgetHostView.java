package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SizeF;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.graphics.PreloadIconDrawable;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.util.Themes;
import java.util.List;

public class PendingAppWidgetHostView extends LauncherAppWidgetHostView implements View.OnClickListener, IconCache.ItemInfoUpdateReceiver {
    private static final float MIN_SATUNATION = 0.7f;
    private static final float SETUP_ICON_SIZE_FACTOR = 0.4f;
    private Drawable mCenterDrawable;
    private View.OnClickListener mClickListener;
    private final boolean mDisabledForSafeMode;
    private boolean mDrawableSizeChanged;
    private final LauncherAppWidgetInfo mInfo;
    private final TextPaint mPaint;
    private final Rect mRect = new Rect();
    private Drawable mSettingIconDrawable;
    private Layout mSetupTextLayout;
    private final int mStartState;

    public void updateAppWidgetSize(Bundle bundle, int i, int i2, int i3, int i4) {
    }

    public void updateAppWidgetSize(Bundle bundle, List<SizeF> list) {
    }

    public PendingAppWidgetHostView(Context context, LauncherAppWidgetInfo launcherAppWidgetInfo, IconCache iconCache, boolean z) {
        super(new ContextThemeWrapper(context, R.style.WidgetContainerTheme));
        this.mInfo = launcherAppWidgetInfo;
        this.mStartState = launcherAppWidgetInfo.restoreStatus;
        this.mDisabledForSafeMode = z;
        TextPaint textPaint = new TextPaint();
        this.mPaint = textPaint;
        textPaint.setColor(Themes.getAttrColor(getContext(), 16842806));
        textPaint.setTextSize(TypedValue.applyDimension(0, (float) this.mLauncher.getDeviceProfile().iconTextSizePx, getResources().getDisplayMetrics()));
        setBackgroundResource(R.drawable.pending_widget_bg);
        setWillNotDraw(false);
        super.updateAppWidget((RemoteViews) null);
        setOnClickListener(ItemClickHandler.INSTANCE);
        if (launcherAppWidgetInfo.pendingItemInfo == null) {
            launcherAppWidgetInfo.pendingItemInfo = new PackageItemInfo(launcherAppWidgetInfo.providerName.getPackageName(), launcherAppWidgetInfo.user);
            iconCache.updateIconInBackground(this, launcherAppWidgetInfo.pendingItemInfo);
            return;
        }
        reapplyItemInfo(launcherAppWidgetInfo.pendingItemInfo);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        if (new WidgetManagerHelper(getContext()).isAppWidgetRestored(this.mInfo.appWidgetId)) {
            super.updateAppWidget(remoteViews);
            reInflate();
        }
    }

    /* access modifiers changed from: protected */
    public View getDefaultView() {
        View inflate = this.mInflater.inflate(R.layout.appwidget_not_ready, this, false);
        inflate.setOnClickListener(this);
        applyState();
        invalidate();
        return inflate;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mClickListener = onClickListener;
    }

    public boolean isReinflateIfNeeded() {
        return this.mStartState != this.mInfo.restoreStatus;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mDrawableSizeChanged = true;
    }

    public void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon) {
        Drawable drawable = this.mCenterDrawable;
        if (drawable != null) {
            drawable.setCallback((Drawable.Callback) null);
            this.mCenterDrawable = null;
        }
        if (itemInfoWithIcon.bitmap.icon != null) {
            Drawable widgetCategoryIcon = getWidgetCategoryIcon();
            if (this.mDisabledForSafeMode) {
                if (widgetCategoryIcon == null) {
                    FastBitmapDrawable newIcon = itemInfoWithIcon.newIcon(getContext());
                    newIcon.setIsDisabled(true);
                    this.mCenterDrawable = newIcon;
                } else {
                    widgetCategoryIcon.setColorFilter(FastBitmapDrawable.getDisabledColorFilter());
                    this.mCenterDrawable = widgetCategoryIcon;
                }
                this.mSettingIconDrawable = null;
            } else if (isReadyForClickSetup()) {
                if (widgetCategoryIcon == null) {
                    widgetCategoryIcon = itemInfoWithIcon.newIcon(getContext());
                }
                this.mCenterDrawable = widgetCategoryIcon;
                this.mSettingIconDrawable = getResources().getDrawable(R.drawable.ic_setting).mutate();
                updateSettingColor(itemInfoWithIcon.bitmap.color);
            } else {
                if (widgetCategoryIcon == null) {
                    widgetCategoryIcon = PreloadIconDrawable.newPendingIcon(getContext(), itemInfoWithIcon);
                }
                this.mCenterDrawable = widgetCategoryIcon;
                this.mSettingIconDrawable = null;
                applyState();
            }
            this.mCenterDrawable.setCallback(this);
            this.mDrawableSizeChanged = true;
        }
        invalidate();
    }

    private void updateSettingColor(int i) {
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        fArr[1] = Math.min(fArr[1], 0.7f);
        fArr[2] = 1.0f;
        this.mSettingIconDrawable.setColorFilter(Color.HSVToColor(fArr), PorterDuff.Mode.SRC_IN);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mCenterDrawable || super.verifyDrawable(drawable);
    }

    public void applyState() {
        Drawable drawable = this.mCenterDrawable;
        if (drawable != null) {
            drawable.setLevel(Math.max(this.mInfo.installProgress, 0));
        }
    }

    public void onClick(View view) {
        View.OnClickListener onClickListener = this.mClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public boolean isReadyForClickSetup() {
        if (this.mInfo.hasRestoreFlag(2) || (!this.mInfo.hasRestoreFlag(4) && !this.mInfo.hasRestoreFlag(1))) {
            return false;
        }
        return true;
    }

    private void updateDrawableBounds() {
        int i;
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.pending_widget_min_padding);
        int width = (getWidth() - paddingLeft) - paddingRight;
        int i2 = dimensionPixelSize * 2;
        int i3 = width - i2;
        int height = ((getHeight() - paddingTop) - paddingBottom) - i2;
        if (this.mSettingIconDrawable == null) {
            int min = Math.min(deviceProfile.iconSizePx, Math.min(i3, height));
            this.mRect.set(0, 0, min, min);
            this.mRect.offsetTo((getWidth() - this.mRect.width()) / 2, (getHeight() - this.mRect.height()) / 2);
            this.mCenterDrawable.setBounds(this.mRect);
            return;
        }
        float max = (float) Math.max(0, Math.min(i3, height));
        float max2 = (float) Math.max(i3, height);
        if (max * 1.8f > max2) {
            max = max2 / 1.8f;
        }
        int min2 = (int) Math.min(max, (float) deviceProfile.iconSizePx);
        int height2 = (getHeight() - min2) / 2;
        this.mSetupTextLayout = null;
        if (i3 > 0) {
            StaticLayout staticLayout = r8;
            i = paddingTop;
            StaticLayout staticLayout2 = new StaticLayout(getResources().getText(R.string.gadget_complete_setup_text), this.mPaint, i3, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
            this.mSetupTextLayout = staticLayout;
            int height3 = staticLayout.getHeight();
            if (((float) height3) + (((float) min2) * 1.8f) + ((float) deviceProfile.iconDrawablePaddingPx) < ((float) height)) {
                height2 = (((getHeight() - height3) - deviceProfile.iconDrawablePaddingPx) - min2) / 2;
            } else {
                this.mSetupTextLayout = null;
            }
        } else {
            i = paddingTop;
        }
        this.mRect.set(0, 0, min2, min2);
        this.mRect.offset((getWidth() - min2) / 2, height2);
        this.mCenterDrawable.setBounds(this.mRect);
        int i4 = paddingLeft + dimensionPixelSize;
        this.mRect.left = i4;
        Rect rect = this.mRect;
        int i5 = (int) (((float) min2) * 0.4f);
        rect.right = rect.left + i5;
        this.mRect.top = i + dimensionPixelSize;
        Rect rect2 = this.mRect;
        rect2.bottom = rect2.top + i5;
        this.mSettingIconDrawable.setBounds(this.mRect);
        if (this.mSetupTextLayout != null) {
            this.mRect.left = i4;
            this.mRect.top = this.mCenterDrawable.getBounds().bottom + deviceProfile.iconDrawablePaddingPx;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mCenterDrawable != null) {
            if (this.mDrawableSizeChanged) {
                updateDrawableBounds();
                this.mDrawableSizeChanged = false;
            }
            this.mCenterDrawable.draw(canvas);
            Drawable drawable = this.mSettingIconDrawable;
            if (drawable != null) {
                drawable.draw(canvas);
            }
            if (this.mSetupTextLayout != null) {
                canvas.save();
                canvas.translate((float) this.mRect.left, (float) this.mRect.top);
                this.mSetupTextLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private Drawable getWidgetCategoryIcon() {
        if (this.mInfo.pendingItemInfo.widgetCategory == -1) {
            return null;
        }
        return this.mInfo.pendingItemInfo.newIcon(getContext());
    }
}
