package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.MessageFormat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.TextView;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dot.DotInfo;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.graphics.PreloadIconDrawable;
import com.android.launcher3.icons.DotRenderer;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.icons.PlaceHolderIconDrawable;
import com.android.launcher3.icons.cache.HandlerRunnable;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.util.SafeCloseable;
import com.android.launcher3.util.ShortcutUtil;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.IconLabelDotView;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class BubbleTextView extends TextView implements IconCache.ItemInfoUpdateReceiver, IconLabelDotView, DraggableView, Reorderable {
    private static final int DISPLAY_ALL_APPS = 1;
    private static final int DISPLAY_FOLDER = 2;
    private static final int DISPLAY_SEARCH_RESULT = 6;
    private static final int DISPLAY_SEARCH_RESULT_SMALL = 7;
    protected static final int DISPLAY_TASKBAR = 5;
    private static final int DISPLAY_WORKSPACE = 0;
    private static final Property<BubbleTextView, Float> DOT_SCALE_PROPERTY = new Property<BubbleTextView, Float>(Float.TYPE, "dotScale") {
        public Float get(BubbleTextView bubbleTextView) {
            return Float.valueOf(bubbleTextView.mDotParams.scale);
        }

        public void set(BubbleTextView bubbleTextView, Float f) {
            bubbleTextView.mDotParams.scale = f.floatValue();
            bubbleTextView.invalidate();
        }
    };
    private static final int MAX_SEARCH_LOOP_COUNT = 20;
    private static final float MIN_LETTER_SPACING = -0.05f;
    private static final int[] STATE_PRESSED = {16842919};
    public static final Property<BubbleTextView, Float> TEXT_ALPHA_PROPERTY = new Property<BubbleTextView, Float>(Float.class, "textAlpha") {
        public Float get(BubbleTextView bubbleTextView) {
            return Float.valueOf(bubbleTextView.mTextAlpha);
        }

        public void set(BubbleTextView bubbleTextView, Float f) {
            bubbleTextView.setTextAlpha(f.floatValue());
        }
    };
    private final ActivityContext mActivity;
    private boolean mCenterVertically;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mDisableRelayout;
    protected final int mDisplay;
    @ViewDebug.ExportedProperty(category = "launcher")
    private DotInfo mDotInfo;
    @ViewDebug.ExportedProperty(category = "launcher", deepExport = true)
    protected DotRenderer.DrawParams mDotParams;
    private DotRenderer mDotRenderer;
    /* access modifiers changed from: private */
    public Animator mDotScaleAnim;
    private boolean mEnableIconUpdateAnimation;
    private boolean mForceHideDot;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mHideBadge;
    private FastBitmapDrawable mIcon;
    private HandlerRunnable mIconLoadRequest;
    private final int mIconSize;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mIgnorePressedStateChange;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mIsIconVisible;
    private final boolean mIsRtl;
    private final boolean mLayoutHorizontal;
    private final CheckLongPressHelper mLongPressHelper;
    private float mScaleForReorderBounce;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mStayPressed;
    /* access modifiers changed from: private */
    @ViewDebug.ExportedProperty(category = "launcher")
    public float mTextAlpha;
    @ViewDebug.ExportedProperty(category = "launcher")
    private int mTextColor;
    @ViewDebug.ExportedProperty(category = "launcher")
    private ColorStateList mTextColorStateList;
    private final PointF mTranslationForMoveFromCenterAnimation;
    private final PointF mTranslationForReorderBounce;
    private final PointF mTranslationForReorderPreview;
    private float mTranslationXForTaskbarAlignmentAnimation;

    static /* synthetic */ void lambda$prepareDrawDragView$0() {
    }

    public View getView() {
        return this;
    }

    public int getViewType() {
        return 0;
    }

    public BubbleTextView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public BubbleTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BubbleTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int i2;
        this.mTranslationForReorderBounce = new PointF(0.0f, 0.0f);
        this.mTranslationForReorderPreview = new PointF(0.0f, 0.0f);
        this.mTranslationXForTaskbarAlignmentAnimation = 0.0f;
        this.mTranslationForMoveFromCenterAnimation = new PointF(0.0f, 0.0f);
        this.mScaleForReorderBounce = 1.0f;
        this.mHideBadge = false;
        this.mIsIconVisible = true;
        this.mTextAlpha = 1.0f;
        this.mDisableRelayout = false;
        this.mEnableIconUpdateAnimation = false;
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(context);
        this.mActivity = activityContext;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.BubbleTextView, i, 0);
        this.mLayoutHorizontal = obtainStyledAttributes.getBoolean(3, false);
        this.mIsRtl = getResources().getConfiguration().getLayoutDirection() == 1;
        DeviceProfile deviceProfile = activityContext.getDeviceProfile();
        int integer = obtainStyledAttributes.getInteger(1, 0);
        this.mDisplay = integer;
        if (integer == 0) {
            setTextSize(0, (float) deviceProfile.iconTextSizePx);
            setCompoundDrawablePadding(deviceProfile.iconDrawablePaddingPx);
            i2 = deviceProfile.iconSizePx;
            setCenterVertically(deviceProfile.isScalableGrid);
        } else if (integer == 1) {
            setTextSize(0, deviceProfile.allAppsIconTextSizePx);
            setCompoundDrawablePadding(deviceProfile.allAppsIconDrawablePaddingPx);
            i2 = deviceProfile.allAppsIconSizePx;
        } else if (integer == 2) {
            setTextSize(0, (float) deviceProfile.folderChildTextSizePx);
            setCompoundDrawablePadding(deviceProfile.folderChildDrawablePaddingPx);
            i2 = deviceProfile.folderChildIconSizePx;
        } else if (integer == 6) {
            i2 = getResources().getDimensionPixelSize(R.dimen.search_row_icon_size);
        } else if (integer == 7) {
            i2 = getResources().getDimensionPixelSize(R.dimen.search_row_small_icon_size);
        } else if (integer == 5) {
            i2 = deviceProfile.iconSizePx;
        } else {
            i2 = deviceProfile.iconSizePx;
        }
        this.mCenterVertically = obtainStyledAttributes.getBoolean(0, false);
        this.mIconSize = obtainStyledAttributes.getDimensionPixelSize(2, i2);
        obtainStyledAttributes.recycle();
        this.mLongPressHelper = new CheckLongPressHelper(this);
        this.mDotParams = new DotRenderer.DrawParams();
        setEllipsize(TextUtils.TruncateAt.END);
        setAccessibilityDelegate(activityContext.getAccessibilityDelegate());
        setTextAlpha(1.0f);
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        setEllipsize(z ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
        super.onFocusChanged(z, i, rect);
    }

    public void setHideBadge(boolean z) {
        this.mHideBadge = z;
    }

    public void reset() {
        this.mDotInfo = null;
        this.mDotParams.dotColor = 0;
        this.mDotParams.appColor = 0;
        cancelDotScaleAnim();
        this.mDotParams.scale = 0.0f;
        this.mForceHideDot = false;
        setBackground((Drawable) null);
        setTag((Object) null);
        HandlerRunnable handlerRunnable = this.mIconLoadRequest;
        if (handlerRunnable != null) {
            handlerRunnable.cancel();
            this.mIconLoadRequest = null;
        }
    }

    private void cancelDotScaleAnim() {
        Animator animator = this.mDotScaleAnim;
        if (animator != null) {
            animator.cancel();
        }
    }

    private void animateDotScale(float... fArr) {
        cancelDotScaleAnim();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, DOT_SCALE_PROPERTY, fArr);
        this.mDotScaleAnim = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Animator unused = BubbleTextView.this.mDotScaleAnim = null;
            }
        });
        this.mDotScaleAnim.start();
    }

    public void applyFromWorkspaceItem(WorkspaceItemInfo workspaceItemInfo) {
        applyFromWorkspaceItem(workspaceItemInfo, false, 0);
    }

    public void applyFromWorkspaceItem(WorkspaceItemInfo workspaceItemInfo, boolean z, int i) {
        applyFromWorkspaceItem(workspaceItemInfo, false);
    }

    public boolean shouldAnimateIconChange(WorkspaceItemInfo workspaceItemInfo) {
        WorkspaceItemInfo workspaceItemInfo2 = getTag() instanceof WorkspaceItemInfo ? (WorkspaceItemInfo) getTag() : null;
        if (!((workspaceItemInfo2 == null || workspaceItemInfo2.getTargetComponent() == null || workspaceItemInfo.getTargetComponent() == null || workspaceItemInfo2.getTargetComponent().equals(workspaceItemInfo.getTargetComponent())) ? false : true) || !isShown()) {
            return false;
        }
        return true;
    }

    public void setAccessibilityDelegate(View.AccessibilityDelegate accessibilityDelegate) {
        if (accessibilityDelegate instanceof BaseAccessibilityDelegate) {
            super.setAccessibilityDelegate(accessibilityDelegate);
        }
    }

    public void applyFromWorkspaceItem(WorkspaceItemInfo workspaceItemInfo, boolean z) {
        applyIconAndLabel(workspaceItemInfo);
        setItemInfo(workspaceItemInfo);
        applyLoadingState(z);
        applyDotState(workspaceItemInfo, false);
        setDownloadStateContentDescription(workspaceItemInfo, workspaceItemInfo.getProgressLevel());
    }

    public void applyFromApplicationInfo(AppInfo appInfo) {
        applyIconAndLabel(appInfo);
        setItemInfo(appInfo);
        verifyHighRes();
        if ((appInfo.runtimeStatusFlags & ItemInfoWithIcon.FLAG_SHOW_DOWNLOAD_PROGRESS_MASK) != 0) {
            applyProgressLevel();
        }
        applyDotState(appInfo, false);
        setDownloadStateContentDescription(appInfo, appInfo.getProgressLevel());
    }

    public void applyFromItemInfoWithIcon(ItemInfoWithIcon itemInfoWithIcon) {
        applyIconAndLabel(itemInfoWithIcon);
        setItemInfo(itemInfoWithIcon);
        verifyHighRes();
        setDownloadStateContentDescription(itemInfoWithIcon, itemInfoWithIcon.getProgressLevel());
    }

    /* access modifiers changed from: protected */
    public void setItemInfo(ItemInfoWithIcon itemInfoWithIcon) {
        setTag(itemInfoWithIcon);
    }

    /* access modifiers changed from: protected */
    public void applyIconAndLabel(ItemInfoWithIcon itemInfoWithIcon) {
        int i = this.mDisplay;
        int i2 = (i == 0 || i == 2 || i == 5) ? 1 : 0;
        if (this.mHideBadge) {
            i2 |= 2;
        }
        FastBitmapDrawable newIcon = itemInfoWithIcon.newIcon(getContext(), i2);
        this.mDotParams.appColor = newIcon.getIconColor();
        this.mDotParams.dotColor = getContext().getResources().getColor(17170517, getContext().getTheme());
        setIcon(newIcon);
        applyLabel(itemInfoWithIcon);
    }

    private void applyLabel(ItemInfoWithIcon itemInfoWithIcon) {
        CharSequence charSequence;
        setText(itemInfoWithIcon.title);
        if (itemInfoWithIcon.contentDescription != null) {
            if (itemInfoWithIcon.isDisabled()) {
                charSequence = getContext().getString(R.string.disabled_app_label, new Object[]{itemInfoWithIcon.contentDescription});
            } else {
                charSequence = itemInfoWithIcon.contentDescription;
            }
            setContentDescription(charSequence);
        }
    }

    public void setLongPressTimeoutFactor(float f) {
        this.mLongPressHelper.setLongPressTimeoutFactor(f);
    }

    public void refreshDrawableState() {
        if (!this.mIgnorePressedStateChange) {
            super.refreshDrawableState();
        }
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (this.mStayPressed) {
            mergeDrawableStates(onCreateDrawableState, STATE_PRESSED);
        }
        return onCreateDrawableState;
    }

    public FastBitmapDrawable getIcon() {
        return this.mIcon;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && shouldIgnoreTouchDown(motionEvent.getX(), motionEvent.getY())) {
            return false;
        }
        if (!isLongClickable()) {
            return super.onTouchEvent(motionEvent);
        }
        super.onTouchEvent(motionEvent);
        this.mLongPressHelper.onTouchEvent(motionEvent);
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean shouldIgnoreTouchDown(float f, float f2) {
        if (this.mDisplay == 5) {
            return false;
        }
        if (f2 < ((float) getPaddingTop()) || f < ((float) getPaddingLeft()) || f2 > ((float) (getHeight() - getPaddingBottom())) || f > ((float) (getWidth() - getPaddingRight()))) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setStayPressed(boolean z) {
        this.mStayPressed = z;
        refreshDrawableState();
    }

    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        FastBitmapDrawable fastBitmapDrawable = this.mIcon;
        if (fastBitmapDrawable != null) {
            fastBitmapDrawable.setVisible(z, false);
        }
    }

    public void clearPressedBackground() {
        setPressed(false);
        setStayPressed(false);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        this.mIgnorePressedStateChange = true;
        boolean onKeyUp = super.onKeyUp(i, keyEvent);
        this.mIgnorePressedStateChange = false;
        refreshDrawableState();
        return onKeyUp;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        checkForEllipsis();
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        checkForEllipsis();
    }

    private void checkForEllipsis() {
        if (FeatureFlags.ENABLE_ICON_LABEL_AUTO_SCALING.get()) {
            float width = (float) ((getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight());
            if (width > 0.0f) {
                setLetterSpacing(0.0f);
                String charSequence = getText().toString();
                TextPaint paint = getPaint();
                if (paint.measureText(charSequence) >= width) {
                    float findBestSpacingValue = findBestSpacingValue(paint, charSequence, width, MIN_LETTER_SPACING);
                    paint.setLetterSpacing(0.0f);
                    setLetterSpacing(findBestSpacingValue);
                }
            }
        }
    }

    private float findBestSpacingValue(TextPaint textPaint, String str, float f, float f2) {
        textPaint.setLetterSpacing(f2);
        if (textPaint.measureText(str) > f) {
            return f2;
        }
        float f3 = 0.0f;
        for (int i = 0; i < 20; i++) {
            float f4 = (f3 + f2) / 2.0f;
            textPaint.setLetterSpacing(f4);
            if (textPaint.measureText(str) < f) {
                f2 = f4;
            } else {
                f3 = f4;
            }
        }
        return f2;
    }

    /* access modifiers changed from: protected */
    public void drawWithoutDot(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDotIfNecessary(canvas);
    }

    /* access modifiers changed from: protected */
    public void drawDotIfNecessary(Canvas canvas) {
        if (this.mForceHideDot) {
            return;
        }
        if (hasDot() || this.mDotParams.scale > 0.0f) {
            getIconBounds(this.mDotParams.iconBounds);
            Utilities.scaleRectAboutCenter(this.mDotParams.iconBounds, IconShape.getNormalizationScale());
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            canvas.translate((float) scrollX, (float) scrollY);
            this.mDotRenderer.draw(canvas, this.mDotParams);
            canvas.translate((float) (-scrollX), (float) (-scrollY));
        }
    }

    public void setForceHideDot(boolean z) {
        if (this.mForceHideDot != z) {
            this.mForceHideDot = z;
            if (z) {
                invalidate();
            } else if (hasDot()) {
                animateDotScale(0.0f, 1.0f);
            }
        }
    }

    private boolean hasDot() {
        return this.mDotInfo != null;
    }

    public void getIconBounds(Rect rect) {
        getIconBounds(this.mIconSize, rect);
    }

    public void getIconBounds(int i, Rect rect) {
        Utilities.setRectToViewCenter(this, i, rect);
        if (!this.mLayoutHorizontal) {
            rect.offsetTo(rect.left, getPaddingTop());
        } else if (this.mIsRtl) {
            rect.offsetTo((getWidth() - i) - getPaddingRight(), rect.top);
        } else {
            rect.offsetTo(getPaddingLeft(), rect.top);
        }
    }

    public void setCenterVertically(boolean z) {
        this.mCenterVertically = z;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.mCenterVertically) {
            Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
            int compoundDrawablePadding = this.mIconSize + getCompoundDrawablePadding() + ((int) Math.ceil((double) (fontMetrics.bottom - fontMetrics.top)));
            setPadding(getPaddingLeft(), (View.MeasureSpec.getSize(i2) - compoundDrawablePadding) / 2, getPaddingRight(), getPaddingBottom());
        }
        super.onMeasure(i, i2);
    }

    public void setTextColor(int i) {
        this.mTextColor = i;
        this.mTextColorStateList = null;
        super.setTextColor(getModifiedColor());
    }

    public void setTextColor(ColorStateList colorStateList) {
        this.mTextColor = colorStateList.getDefaultColor();
        this.mTextColorStateList = colorStateList;
        if (Float.compare(this.mTextAlpha, 1.0f) == 0) {
            super.setTextColor(colorStateList);
        } else {
            super.setTextColor(getModifiedColor());
        }
    }

    public boolean shouldTextBeVisible() {
        Object tag = getParent() instanceof FolderIcon ? ((View) getParent()).getTag() : getTag();
        ItemInfo itemInfo = tag instanceof ItemInfo ? (ItemInfo) tag : null;
        return itemInfo == null || !(itemInfo.container == -101 || itemInfo.container == -103);
    }

    public void setTextVisibility(boolean z) {
        setTextAlpha(z ? 1.0f : 0.0f);
    }

    /* access modifiers changed from: private */
    public void setTextAlpha(float f) {
        this.mTextAlpha = f;
        ColorStateList colorStateList = this.mTextColorStateList;
        if (colorStateList != null) {
            setTextColor(colorStateList);
        } else {
            super.setTextColor(getModifiedColor());
        }
    }

    private int getModifiedColor() {
        if (this.mTextAlpha == 0.0f) {
            return 0;
        }
        int i = this.mTextColor;
        return GraphicsUtils.setColorAlphaBound(i, Math.round(((float) Color.alpha(i)) * this.mTextAlpha));
    }

    public ObjectAnimator createTextAlphaAnimator(boolean z) {
        return ObjectAnimator.ofFloat(this, TEXT_ALPHA_PROPERTY, new float[]{(!shouldTextBeVisible() || !z) ? 0.0f : 1.0f});
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public void applyLoadingState(boolean z) {
        if (getTag() instanceof ItemInfoWithIcon) {
            WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) getTag();
            if ((workspaceItemInfo.runtimeStatusFlags & 2048) != 0) {
                updateProgressBarUi(workspaceItemInfo.getProgressLevel() == 100);
            } else if (workspaceItemInfo.hasPromiseIconUi() || (workspaceItemInfo.runtimeStatusFlags & 1024) != 0) {
                updateProgressBarUi(z);
            }
        }
    }

    private void updateProgressBarUi(boolean z) {
        PreloadIconDrawable applyProgressLevel = applyProgressLevel();
        if (applyProgressLevel != null && z) {
            applyProgressLevel.maybePerformFinishedAnimation();
        }
    }

    public PreloadIconDrawable applyProgressLevel() {
        if (!(getTag() instanceof ItemInfoWithIcon)) {
            return null;
        }
        ItemInfoWithIcon itemInfoWithIcon = (ItemInfoWithIcon) getTag();
        int progressLevel = itemInfoWithIcon.getProgressLevel();
        if (progressLevel >= 100) {
            setContentDescription(itemInfoWithIcon.contentDescription != null ? itemInfoWithIcon.contentDescription : "");
        } else if (progressLevel > 0) {
            setDownloadStateContentDescription(itemInfoWithIcon, progressLevel);
        } else {
            setContentDescription(getContext().getString(R.string.app_waiting_download_title, new Object[]{itemInfoWithIcon.title}));
        }
        FastBitmapDrawable fastBitmapDrawable = this.mIcon;
        if (fastBitmapDrawable == null) {
            return null;
        }
        if (fastBitmapDrawable instanceof PreloadIconDrawable) {
            PreloadIconDrawable preloadIconDrawable = (PreloadIconDrawable) fastBitmapDrawable;
            preloadIconDrawable.setLevel(progressLevel);
            preloadIconDrawable.setIsDisabled(!itemInfoWithIcon.isAppStartable());
            return preloadIconDrawable;
        }
        PreloadIconDrawable makePreloadIcon = makePreloadIcon();
        setIcon(makePreloadIcon);
        return makePreloadIcon;
    }

    public PreloadIconDrawable makePreloadIcon() {
        if (!(getTag() instanceof ItemInfoWithIcon)) {
            return null;
        }
        ItemInfoWithIcon itemInfoWithIcon = (ItemInfoWithIcon) getTag();
        int progressLevel = itemInfoWithIcon.getProgressLevel();
        PreloadIconDrawable newPendingIcon = PreloadIconDrawable.newPendingIcon(getContext(), itemInfoWithIcon);
        newPendingIcon.setLevel(progressLevel);
        newPendingIcon.setIsDisabled(!itemInfoWithIcon.isAppStartable());
        return newPendingIcon;
    }

    public void applyDotState(ItemInfo itemInfo, boolean z) {
        if (this.mIcon instanceof FastBitmapDrawable) {
            boolean z2 = this.mDotInfo != null;
            DotInfo dotInfoForItem = this.mActivity.getDotInfoForItem(itemInfo);
            this.mDotInfo = dotInfoForItem;
            boolean z3 = dotInfoForItem != null;
            float f = z3 ? 1.0f : 0.0f;
            if (this.mDisplay == 1) {
                this.mDotRenderer = this.mActivity.getDeviceProfile().mDotRendererAllApps;
            } else {
                this.mDotRenderer = this.mActivity.getDeviceProfile().mDotRendererWorkSpace;
            }
            if (z2 || z3) {
                if (!z || !(z2 ^ z3) || !isShown()) {
                    cancelDotScaleAnim();
                    this.mDotParams.scale = f;
                    invalidate();
                } else {
                    animateDotScale(f);
                }
            }
            if (TextUtils.isEmpty(itemInfo.contentDescription)) {
                return;
            }
            if (itemInfo.isDisabled()) {
                setContentDescription(getContext().getString(R.string.disabled_app_label, new Object[]{itemInfo.contentDescription}));
            } else if (hasDot()) {
                setContentDescription(getAppLabelPluralString(itemInfo.contentDescription.toString(), this.mDotInfo.getNotificationCount()));
            } else {
                setContentDescription(itemInfo.contentDescription);
            }
        }
    }

    private void setDownloadStateContentDescription(ItemInfoWithIcon itemInfoWithIcon, int i) {
        if ((itemInfoWithIcon.runtimeStatusFlags & ItemInfoWithIcon.FLAG_SHOW_DOWNLOAD_PROGRESS_MASK) != 0) {
            String format = NumberFormat.getPercentInstance().format(((double) i) * 0.01d);
            if ((itemInfoWithIcon.runtimeStatusFlags & 1024) != 0) {
                setContentDescription(getContext().getString(R.string.app_installing_title, new Object[]{itemInfoWithIcon.title, format}));
            } else if ((itemInfoWithIcon.runtimeStatusFlags & 2048) != 0) {
                setContentDescription(getContext().getString(R.string.app_downloading_title, new Object[]{itemInfoWithIcon.title, format}));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setIcon(FastBitmapDrawable fastBitmapDrawable) {
        if (this.mIsIconVisible) {
            applyCompoundDrawables(fastBitmapDrawable);
        }
        this.mIcon = fastBitmapDrawable;
        if (fastBitmapDrawable != null) {
            fastBitmapDrawable.setVisible(getWindowVisibility() == 0 && isShown(), false);
        }
    }

    public void setIconVisible(boolean z) {
        this.mIsIconVisible = z;
        if (!z) {
            resetIconScale();
        }
        applyCompoundDrawables(z ? this.mIcon : new ColorDrawable(0));
    }

    /* access modifiers changed from: protected */
    public boolean iconUpdateAnimationEnabled() {
        return this.mEnableIconUpdateAnimation;
    }

    /* access modifiers changed from: protected */
    public void applyCompoundDrawables(Drawable drawable) {
        if (drawable != null) {
            this.mDisableRelayout = this.mIcon != null;
            int i = this.mIconSize;
            drawable.setBounds(0, 0, i, i);
            updateIcon(drawable);
            FastBitmapDrawable fastBitmapDrawable = this.mIcon;
            if (fastBitmapDrawable != null && (fastBitmapDrawable instanceof PlaceHolderIconDrawable) && iconUpdateAnimationEnabled()) {
                ((PlaceHolderIconDrawable) this.mIcon).animateIconUpdate(drawable);
            }
            this.mDisableRelayout = false;
        }
    }

    public void requestLayout() {
        if (!this.mDisableRelayout) {
            super.requestLayout();
        }
    }

    public void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon) {
        if (getTag() == itemInfoWithIcon) {
            this.mIconLoadRequest = null;
            this.mDisableRelayout = true;
            this.mEnableIconUpdateAnimation = true;
            itemInfoWithIcon.bitmap.icon.prepareToDraw();
            if (itemInfoWithIcon instanceof AppInfo) {
                applyFromApplicationInfo((AppInfo) itemInfoWithIcon);
            } else if (itemInfoWithIcon instanceof WorkspaceItemInfo) {
                applyFromWorkspaceItem((WorkspaceItemInfo) itemInfoWithIcon);
                this.mActivity.invalidateParent(itemInfoWithIcon);
            } else if (itemInfoWithIcon != null) {
                applyFromItemInfoWithIcon(itemInfoWithIcon);
            }
            this.mDisableRelayout = false;
            this.mEnableIconUpdateAnimation = false;
        }
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

    public int getIconSize() {
        return this.mIconSize;
    }

    private void updateTranslation() {
        super.setTranslationX(this.mTranslationForReorderBounce.x + this.mTranslationForReorderPreview.x + this.mTranslationForMoveFromCenterAnimation.x + this.mTranslationXForTaskbarAlignmentAnimation);
        super.setTranslationY(this.mTranslationForReorderBounce.y + this.mTranslationForReorderPreview.y + this.mTranslationForMoveFromCenterAnimation.y);
    }

    public void setReorderBounceOffset(float f, float f2) {
        this.mTranslationForReorderBounce.set(f, f2);
        updateTranslation();
    }

    public void getReorderBounceOffset(PointF pointF) {
        pointF.set(this.mTranslationForReorderBounce);
    }

    public void setReorderPreviewOffset(float f, float f2) {
        this.mTranslationForReorderPreview.set(f, f2);
        updateTranslation();
    }

    public void getReorderPreviewOffset(PointF pointF) {
        pointF.set(this.mTranslationForReorderPreview);
    }

    public void setReorderBounceScale(float f) {
        this.mScaleForReorderBounce = f;
        super.setScaleX(f);
        super.setScaleY(f);
    }

    public float getReorderBounceScale() {
        return this.mScaleForReorderBounce;
    }

    public void setTranslationForMoveFromCenterAnimation(float f, float f2) {
        this.mTranslationForMoveFromCenterAnimation.set(f, f2);
        updateTranslation();
    }

    public void setTranslationXForTaskbarAlignmentAnimation(float f) {
        this.mTranslationXForTaskbarAlignmentAnimation = f;
        updateTranslation();
    }

    public float getTranslationXForTaskbarAlignmentAnimation() {
        return this.mTranslationXForTaskbarAlignmentAnimation;
    }

    public void getWorkspaceVisualDragBounds(Rect rect) {
        getIconBounds(this.mIconSize, rect);
    }

    public void getSourceVisualDragBounds(Rect rect) {
        getIconBounds(this.mIconSize, rect);
    }

    public SafeCloseable prepareDrawDragView() {
        resetIconScale();
        setForceHideDot(true);
        return $$Lambda$BubbleTextView$GoGT5WZs4kPAAOuEluta6VSFQs.INSTANCE;
    }

    private void resetIconScale() {
        FastBitmapDrawable fastBitmapDrawable = this.mIcon;
        if (fastBitmapDrawable != null) {
            fastBitmapDrawable.resetScale();
        }
    }

    private void updateIcon(Drawable drawable) {
        if (this.mLayoutHorizontal) {
            setCompoundDrawablesRelative(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            setCompoundDrawables((Drawable) null, drawable, (Drawable) null, (Drawable) null);
        }
    }

    private String getAppLabelPluralString(String str, int i) {
        MessageFormat messageFormat = new MessageFormat(getResources().getString(R.string.dotted_app_label), Locale.getDefault());
        HashMap hashMap = new HashMap();
        hashMap.put("app_name", str);
        hashMap.put("count", Integer.valueOf(i));
        return messageFormat.format(hashMap);
    }

    public DragOptions.PreDragCondition startLongPressAction() {
        PopupContainerWithArrow<Launcher> showForIcon = PopupContainerWithArrow.showForIcon(this);
        if (showForIcon != null) {
            return showForIcon.createPreDragCondition(true);
        }
        return null;
    }

    public boolean canShowLongPressPopup() {
        return (getTag() instanceof ItemInfo) && ShortcutUtil.supportsShortcuts((ItemInfo) getTag());
    }
}
