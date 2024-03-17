package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.util.AttributeSet;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.launcher3.CheckLongPressHelper;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.RoundDrawableWrapper;
import com.android.launcher3.icons.cache.HandlerRunnable;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.util.WidgetSizes;
import java.util.function.Consumer;

public class WidgetCell extends LinearLayout {
    private static final boolean DEBUG = false;
    private static final int FADE_IN_DURATION_MS = 90;
    private static final int MAX_MEASURE_SPEC_DIMENSION = 1073741823;
    private static final float PREVIEW_SCALE = 0.8f;
    private static final String TAG = "WidgetCell";
    private static final float WIDTH_SCALE = 3.0f;
    protected HandlerRunnable mActiveRequest;
    protected final ActivityContext mActivity;
    private boolean mAnimatePreview;
    private NavigableAppWidgetHostView mAppWidgetHostViewPreview;
    private float mAppWidgetHostViewScale;
    private int mCellSize;
    private final float mEnforcedCornerRadius;
    protected WidgetItem mItem;
    private final CheckLongPressHelper mLongPressHelper;
    protected int mPresetPreviewSize;
    private float mPreviewContainerScale;
    private RemoteViews mRemoteViewsPreview;
    private int mSourceContainer;
    protected int mTargetPreviewHeight;
    protected int mTargetPreviewWidth;
    private ImageView mWidgetBadge;
    private TextView mWidgetDescription;
    private TextView mWidgetDims;
    private WidgetImageView mWidgetImage;
    private FrameLayout mWidgetImageContainer;
    private TextView mWidgetName;
    private final DatabaseWidgetPreviewLoader mWidgetPreviewLoader;

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

    public WidgetCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetCell(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetCell(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPreviewContainerScale = 1.0f;
        this.mAnimatePreview = true;
        this.mAppWidgetHostViewScale = 1.0f;
        this.mSourceContainer = LauncherSettings.Favorites.CONTAINER_WIDGETS_TRAY;
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(context);
        this.mActivity = activityContext;
        this.mWidgetPreviewLoader = new DatabaseWidgetPreviewLoader(context);
        CheckLongPressHelper checkLongPressHelper = new CheckLongPressHelper(this);
        this.mLongPressHelper = checkLongPressHelper;
        checkLongPressHelper.setLongPressTimeoutFactor(1.0f);
        setContainerWidth();
        setWillNotDraw(false);
        setClipToPadding(false);
        setAccessibilityDelegate(activityContext.getAccessibilityDelegate());
        this.mEnforcedCornerRadius = RoundedCornerEnforcement.computeEnforcedRadius(context);
    }

    private void setContainerWidth() {
        int i = (int) (((float) this.mActivity.getDeviceProfile().allAppsIconSizePx) * 3.0f);
        this.mCellSize = i;
        int i2 = (int) (((float) i) * 0.8f);
        this.mPresetPreviewSize = i2;
        this.mTargetPreviewHeight = i2;
        this.mTargetPreviewWidth = i2;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mWidgetImageContainer = (FrameLayout) findViewById(R.id.widget_preview_container);
        this.mWidgetImage = (WidgetImageView) findViewById(R.id.widget_preview);
        this.mWidgetBadge = (ImageView) findViewById(R.id.widget_badge);
        this.mWidgetName = (TextView) findViewById(R.id.widget_name);
        this.mWidgetDims = (TextView) findViewById(R.id.widget_dims);
        this.mWidgetDescription = (TextView) findViewById(R.id.widget_description);
    }

    public void setRemoteViewsPreview(RemoteViews remoteViews) {
        this.mRemoteViewsPreview = remoteViews;
    }

    public RemoteViews getRemoteViewsPreview() {
        return this.mRemoteViewsPreview;
    }

    public float getAppWidgetHostViewScale() {
        return this.mAppWidgetHostViewScale;
    }

    public void clear() {
        this.mWidgetImage.animate().cancel();
        this.mWidgetImage.setDrawable((Drawable) null);
        this.mWidgetImage.setVisibility(0);
        this.mWidgetBadge.setImageDrawable((Drawable) null);
        this.mWidgetBadge.setVisibility(8);
        this.mWidgetName.setText((CharSequence) null);
        this.mWidgetDims.setText((CharSequence) null);
        this.mWidgetDescription.setText((CharSequence) null);
        this.mWidgetDescription.setVisibility(8);
        int i = this.mPresetPreviewSize;
        this.mTargetPreviewHeight = i;
        this.mTargetPreviewWidth = i;
        HandlerRunnable handlerRunnable = this.mActiveRequest;
        if (handlerRunnable != null) {
            handlerRunnable.cancel();
            this.mActiveRequest = null;
        }
        this.mRemoteViewsPreview = null;
        NavigableAppWidgetHostView navigableAppWidgetHostView = this.mAppWidgetHostViewPreview;
        if (navigableAppWidgetHostView != null) {
            this.mWidgetImageContainer.removeView(navigableAppWidgetHostView);
        }
        this.mAppWidgetHostViewPreview = null;
        this.mAppWidgetHostViewScale = 1.0f;
        this.mItem = null;
    }

    public void setSourceContainer(int i) {
        this.mSourceContainer = i;
    }

    public void applyFromCellItem(WidgetItem widgetItem) {
        applyFromCellItem(widgetItem, 1.0f);
    }

    public void applyFromCellItem(WidgetItem widgetItem, float f) {
        applyFromCellItem(widgetItem, f, new Consumer() {
            public final void accept(Object obj) {
                WidgetCell.this.applyPreview((Bitmap) obj);
            }
        }, (Bitmap) null);
    }

    public void applyFromCellItem(WidgetItem widgetItem, float f, Consumer<Bitmap> consumer, Bitmap bitmap) {
        Size widgetItemSizePx = WidgetSizes.getWidgetItemSizePx(getContext(), this.mActivity.getDeviceProfile(), widgetItem);
        this.mTargetPreviewWidth = widgetItemSizePx.getWidth();
        this.mTargetPreviewHeight = widgetItemSizePx.getHeight();
        this.mPreviewContainerScale = f;
        applyPreviewOnAppWidgetHostView(widgetItem);
        Context context = getContext();
        this.mItem = widgetItem;
        this.mWidgetName.setText(widgetItem.label);
        this.mWidgetName.setContentDescription(context.getString(R.string.widget_preview_context_description, new Object[]{this.mItem.label}));
        this.mWidgetDims.setText(context.getString(R.string.widget_dims_format, new Object[]{Integer.valueOf(this.mItem.spanX), Integer.valueOf(this.mItem.spanY)}));
        this.mWidgetDims.setContentDescription(context.getString(R.string.widget_accessible_dims_format, new Object[]{Integer.valueOf(this.mItem.spanX), Integer.valueOf(this.mItem.spanY)}));
        if (Utilities.ATLEAST_S && this.mItem.widgetInfo != null) {
            CharSequence loadDescription = this.mItem.widgetInfo.loadDescription(context);
            if (loadDescription == null || loadDescription.length() <= 0) {
                this.mWidgetDescription.setVisibility(8);
            } else {
                this.mWidgetDescription.setText(loadDescription);
                this.mWidgetDescription.setVisibility(0);
            }
        }
        if (widgetItem.activityInfo != null) {
            setTag(new PendingAddShortcutInfo(widgetItem.activityInfo));
        } else {
            setTag(new PendingAddWidgetInfo(widgetItem.widgetInfo, this.mSourceContainer));
        }
        ensurePreviewWithCallback(consumer, bitmap);
    }

    private void applyPreviewOnAppWidgetHostView(WidgetItem widgetItem) {
        NavigableAppWidgetHostView navigableAppWidgetHostView;
        if (this.mRemoteViewsPreview != null) {
            NavigableAppWidgetHostView createAppWidgetHostView = createAppWidgetHostView(getContext());
            this.mAppWidgetHostViewPreview = createAppWidgetHostView;
            setAppWidgetHostViewPreview(createAppWidgetHostView, widgetItem.widgetInfo, this.mRemoteViewsPreview);
        } else if (widgetItem.hasPreviewLayout()) {
            Context context = getContext();
            if (isLauncherContext(context)) {
                navigableAppWidgetHostView = new LauncherAppWidgetHostView(context);
            } else {
                navigableAppWidgetHostView = createAppWidgetHostView(context);
            }
            this.mAppWidgetHostViewPreview = navigableAppWidgetHostView;
            LauncherAppWidgetProviderInfo fromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(context, widgetItem.widgetInfo.clone());
            fromProviderInfo.initialLayout = widgetItem.widgetInfo.previewLayout;
            setAppWidgetHostViewPreview(this.mAppWidgetHostViewPreview, fromProviderInfo, (RemoteViews) null);
        }
    }

    private void setAppWidgetHostViewPreview(NavigableAppWidgetHostView navigableAppWidgetHostView, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, RemoteViews remoteViews) {
        navigableAppWidgetHostView.setImportantForAccessibility(2);
        navigableAppWidgetHostView.setAppWidget(-1, launcherAppWidgetProviderInfo);
        navigableAppWidgetHostView.updateAppWidget(remoteViews);
    }

    public WidgetImageView getWidgetView() {
        return this.mWidgetImage;
    }

    public NavigableAppWidgetHostView getAppWidgetHostViewPreview() {
        return this.mAppWidgetHostViewPreview;
    }

    public void setAnimatePreview(boolean z) {
        this.mAnimatePreview = z;
    }

    /* access modifiers changed from: private */
    public void applyPreview(Bitmap bitmap) {
        if (bitmap != null) {
            RoundDrawableWrapper roundDrawableWrapper = new RoundDrawableWrapper(new FastBitmapDrawable(bitmap), this.mEnforcedCornerRadius);
            int i = this.mTargetPreviewWidth;
            float min = i > 0 ? Math.min(((float) i) / (((float) roundDrawableWrapper.getIntrinsicWidth()) * this.mPreviewContainerScale), 1.0f) : 1.0f;
            setContainerSize(Math.round(((float) roundDrawableWrapper.getIntrinsicWidth()) * min * this.mPreviewContainerScale), Math.round(((float) roundDrawableWrapper.getIntrinsicHeight()) * min * this.mPreviewContainerScale));
            this.mWidgetImage.setDrawable(roundDrawableWrapper);
            this.mWidgetImage.setVisibility(0);
            NavigableAppWidgetHostView navigableAppWidgetHostView = this.mAppWidgetHostViewPreview;
            if (navigableAppWidgetHostView != null) {
                removeView(navigableAppWidgetHostView);
                this.mAppWidgetHostViewPreview = null;
            }
        }
        if (this.mAnimatePreview) {
            this.mWidgetImageContainer.setAlpha(0.0f);
            this.mWidgetImageContainer.animate().alpha(1.0f).setDuration(90);
        } else {
            this.mWidgetImageContainer.setAlpha(1.0f);
        }
        HandlerRunnable handlerRunnable = this.mActiveRequest;
        if (handlerRunnable != null) {
            handlerRunnable.cancel();
            this.mActiveRequest = null;
        }
    }

    public void showBadge() {
        if (Process.myUserHandle().equals(this.mItem.user)) {
            this.mWidgetBadge.setVisibility(8);
            return;
        }
        this.mWidgetBadge.setVisibility(0);
        this.mWidgetBadge.setImageResource(R.drawable.ic_work_app_badge);
    }

    private void setContainerSize(int i, int i2) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mWidgetImageContainer.getLayoutParams();
        layoutParams.width = i;
        layoutParams.height = i2;
        this.mWidgetImageContainer.setLayoutParams(layoutParams);
    }

    private void ensurePreviewWithCallback(Consumer<Bitmap> consumer, Bitmap bitmap) {
        if (this.mAppWidgetHostViewPreview != null) {
            float f = this.mPreviewContainerScale;
            int i = (int) (((float) this.mTargetPreviewWidth) * f);
            int i2 = (int) (((float) this.mTargetPreviewHeight) * f);
            setContainerSize(i, i2);
            boolean z = true;
            if (this.mAppWidgetHostViewPreview.getChildCount() == 1) {
                ViewGroup.LayoutParams layoutParams = this.mAppWidgetHostViewPreview.getChildAt(0).getLayoutParams();
                if (layoutParams.width == -1 || layoutParams.height == -1) {
                    z = false;
                }
                if (z) {
                    setNoClip(this.mWidgetImageContainer);
                    setNoClip(this.mAppWidgetHostViewPreview);
                    float measureAndComputeWidgetPreviewScale = measureAndComputeWidgetPreviewScale();
                    this.mAppWidgetHostViewScale = measureAndComputeWidgetPreviewScale;
                    this.mAppWidgetHostViewPreview.setScaleToFit(measureAndComputeWidgetPreviewScale);
                }
            }
            this.mAppWidgetHostViewPreview.setLayoutParams(new FrameLayout.LayoutParams(i, i2, 119));
            this.mWidgetImageContainer.addView(this.mAppWidgetHostViewPreview, 0);
            this.mWidgetImage.setVisibility(8);
            applyPreview((Bitmap) null);
        } else if (bitmap != null) {
            applyPreview(bitmap);
        } else if (this.mActiveRequest == null) {
            this.mActiveRequest = this.mWidgetPreviewLoader.loadPreview(this.mItem, new Size(this.mTargetPreviewWidth, this.mTargetPreviewHeight), consumer);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        this.mLongPressHelper.onTouchEvent(motionEvent);
        return true;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    private static NavigableAppWidgetHostView createAppWidgetHostView(Context context) {
        return new NavigableAppWidgetHostView(context) {
            /* access modifiers changed from: protected */
            public boolean shouldAllowDirectClick() {
                return false;
            }
        };
    }

    private static boolean isLauncherContext(Context context) {
        return ActivityContext.lookupContext(context) instanceof Launcher;
    }

    public CharSequence getAccessibilityClassName() {
        return WidgetCell.class.getName();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
    }

    private static void setNoClip(ViewGroup viewGroup) {
        viewGroup.setClipChildren(false);
        viewGroup.setClipToPadding(false);
    }

    private float measureAndComputeWidgetPreviewScale() {
        if (this.mAppWidgetHostViewPreview.getChildCount() != 1) {
            return 1.0f;
        }
        this.mAppWidgetHostViewPreview.measure(View.MeasureSpec.makeMeasureSpec(1073741823, 0), View.MeasureSpec.makeMeasureSpec(1073741823, 0));
        if (this.mRemoteViewsPreview != null) {
            this.mAppWidgetHostViewPreview.layout(0, 0, this.mTargetPreviewWidth, this.mTargetPreviewHeight);
            this.mAppWidgetHostViewPreview.measure(View.MeasureSpec.makeMeasureSpec(this.mTargetPreviewWidth, 0), View.MeasureSpec.makeMeasureSpec(this.mTargetPreviewHeight, 0));
        }
        View childAt = this.mAppWidgetHostViewPreview.getChildAt(0);
        int measuredWidth = childAt.getMeasuredWidth();
        int measuredHeight = childAt.getMeasuredHeight();
        if (measuredWidth == 0 || measuredHeight == 0) {
            return 1.0f;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
        if (layoutParams.width == -2) {
            layoutParams.width = childAt.getMeasuredWidth();
        }
        if (layoutParams.height == -2) {
            layoutParams.height = childAt.getMeasuredHeight();
        }
        childAt.setLayoutParams(layoutParams);
        int paddingStart = this.mAppWidgetHostViewPreview.getPaddingStart() + this.mAppWidgetHostViewPreview.getPaddingEnd();
        int paddingTop = this.mAppWidgetHostViewPreview.getPaddingTop() + this.mAppWidgetHostViewPreview.getPaddingBottom();
        float f = (float) (this.mTargetPreviewWidth - paddingStart);
        float f2 = this.mPreviewContainerScale;
        return Math.min((f * f2) / ((float) measuredWidth), (((float) (this.mTargetPreviewHeight - paddingTop)) * f2) / ((float) measuredHeight));
    }
}
