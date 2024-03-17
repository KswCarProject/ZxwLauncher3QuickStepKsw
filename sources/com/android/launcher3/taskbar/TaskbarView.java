package com.android.launcher3.taskbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.Insettable;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.icons.ThemedIconDrawable;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.taskbar.TaskbarViewController;
import com.android.launcher3.uioverrides.ApiWrapper;
import com.android.launcher3.util.LauncherBindableItemsContainer;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.AllAppsButton;
import com.android.launcher3.views.DoubleShadowBubbleTextView;
import java.util.function.Predicate;

public class TaskbarView extends FrameLayout implements FolderIcon.FolderIconParent, Insettable {
    private static final float TASKBAR_BACKGROUND_LUMINANCE = 0.3f;
    private final TaskbarActivityContext mActivityContext;
    private AllAppsButton mAllAppsButton;
    private TaskbarViewController.TaskbarViewCallbacks mControllerCallbacks;
    private View.OnClickListener mIconClickListener;
    private final Rect mIconLayoutBounds;
    private View.OnLongClickListener mIconLongClickListener;
    private final int mIconTouchSize;
    private final int mItemMarginLeftRight;
    private final int mItemPadding;
    private FolderIcon mLeaveBehindFolderIcon;
    private final int[] mTempOutLocation;
    public int mThemeIconsBackground;
    private boolean mTouchEnabled;

    public void setInsets(Rect rect) {
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

    public TaskbarView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TaskbarView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskbarView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TaskbarView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTempOutLocation = new int[2];
        this.mIconLayoutBounds = new Rect();
        this.mTouchEnabled = true;
        TaskbarActivityContext taskbarActivityContext = (TaskbarActivityContext) ActivityContext.lookupContext(context);
        this.mActivityContext = taskbarActivityContext;
        Resources resources = getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.taskbar_icon_touch_size);
        this.mIconTouchSize = dimensionPixelSize;
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.taskbar_icon_spacing);
        int i3 = taskbarActivityContext.getDeviceProfile().iconSizePx;
        this.mItemMarginLeftRight = dimensionPixelSize2 - ((dimensionPixelSize - i3) / 2);
        int i4 = (dimensionPixelSize - i3) / 2;
        this.mItemPadding = i4;
        setWillNotDraw(false);
        this.mThemeIconsBackground = calculateThemeIconsBackground();
        if (FeatureFlags.ENABLE_ALL_APPS_IN_TASKBAR.get()) {
            AllAppsButton allAppsButton = new AllAppsButton(context);
            this.mAllAppsButton = allAppsButton;
            allAppsButton.setLayoutParams(new ViewGroup.LayoutParams(dimensionPixelSize, dimensionPixelSize));
            this.mAllAppsButton.setPadding(i4, i4, i4, i4);
        }
    }

    private int getColorWithGivenLuminance(int i, float f) {
        float[] fArr = new float[3];
        ColorUtils.colorToHSL(i, fArr);
        fArr[2] = f;
        return ColorUtils.HSLToColor(fArr);
    }

    private int calculateThemeIconsBackground() {
        int i = ThemedIconDrawable.getColors(this.mContext)[0];
        return Utilities.isDarkTheme(this.mContext) ? getColorWithGivenLuminance(i, 0.3f) : i;
    }

    /* access modifiers changed from: protected */
    public void init(TaskbarViewController.TaskbarViewCallbacks taskbarViewCallbacks) {
        this.mControllerCallbacks = taskbarViewCallbacks;
        this.mIconClickListener = taskbarViewCallbacks.getIconOnClickListener();
        this.mIconLongClickListener = this.mControllerCallbacks.getIconOnLongClickListener();
        setOnLongClickListener(this.mControllerCallbacks.getBackgroundOnLongClickListener());
        AllAppsButton allAppsButton = this.mAllAppsButton;
        if (allAppsButton != null) {
            allAppsButton.setOnClickListener(this.mControllerCallbacks.getAllAppsButtonClickListener());
        }
    }

    private void removeAndRecycle(View view) {
        removeView(view);
        view.setOnClickListener((View.OnClickListener) null);
        view.setOnLongClickListener((View.OnLongClickListener) null);
        if (!(view.getTag() instanceof FolderInfo)) {
            this.mActivityContext.getViewCache().recycleView(view.getSourceLayoutResId(), view);
        }
        view.setTag((Object) null);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0049 A[EDGE_INSN: B:52:0x0049->B:24:0x0049 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateHotseatItems(com.android.launcher3.model.data.ItemInfo[] r10) {
        /*
            r9 = this;
            com.android.launcher3.views.AllAppsButton r0 = r9.mAllAppsButton
            if (r0 == 0) goto L_0x0007
            r9.removeView(r0)
        L_0x0007:
            r0 = 0
            r1 = r0
            r2 = r1
            r3 = r2
        L_0x000b:
            int r4 = r10.length
            if (r1 >= r4) goto L_0x008f
            r4 = r10[r1]
            if (r4 != 0) goto L_0x0014
            goto L_0x008b
        L_0x0014:
            boolean r5 = r4.isPredictedItem()
            if (r5 == 0) goto L_0x001f
            r5 = 2131493068(0x7f0c00cc, float:1.8609606E38)
        L_0x001d:
            r6 = r0
            goto L_0x002c
        L_0x001f:
            boolean r5 = r4 instanceof com.android.launcher3.model.data.FolderInfo
            if (r5 == 0) goto L_0x0028
            r5 = 2131492940(0x7f0c004c, float:1.8609346E38)
            r6 = 1
            goto L_0x002c
        L_0x0028:
            r5 = 2131493064(0x7f0c00c8, float:1.8609598E38)
            goto L_0x001d
        L_0x002c:
            r7 = 0
            int r8 = r9.getChildCount()
            if (r2 >= r8) goto L_0x0049
            android.view.View r7 = r9.getChildAt(r2)
            int r8 = r7.getSourceLayoutResId()
            if (r8 != r5) goto L_0x0045
            if (r6 == 0) goto L_0x0049
            java.lang.Object r8 = r7.getTag()
            if (r8 == r4) goto L_0x0049
        L_0x0045:
            r9.removeAndRecycle(r7)
            goto L_0x002c
        L_0x0049:
            if (r7 != 0) goto L_0x006e
            if (r6 == 0) goto L_0x005a
            r6 = r4
            com.android.launcher3.model.data.FolderInfo r6 = (com.android.launcher3.model.data.FolderInfo) r6
            com.android.launcher3.taskbar.TaskbarActivityContext r7 = r9.mActivityContext
            com.android.launcher3.folder.FolderIcon r5 = com.android.launcher3.folder.FolderIcon.inflateFolderAndIcon(r5, r7, r9, r6)
            r5.setTextVisible(r0)
            goto L_0x005e
        L_0x005a:
            android.view.View r5 = r9.inflate(r5)
        L_0x005e:
            r7 = r5
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            int r6 = r9.mIconTouchSize
            r5.<init>(r6, r6)
            int r6 = r9.mItemPadding
            r7.setPadding(r6, r6, r6, r6)
            r9.addView(r7, r2, r5)
        L_0x006e:
            boolean r5 = r7 instanceof com.android.launcher3.BubbleTextView
            if (r5 == 0) goto L_0x0086
            boolean r5 = r4 instanceof com.android.launcher3.model.data.WorkspaceItemInfo
            if (r5 == 0) goto L_0x0086
            r5 = r7
            com.android.launcher3.BubbleTextView r5 = (com.android.launcher3.BubbleTextView) r5
            com.android.launcher3.model.data.WorkspaceItemInfo r4 = (com.android.launcher3.model.data.WorkspaceItemInfo) r4
            boolean r6 = r5.shouldAnimateIconChange(r4)
            r5.applyFromWorkspaceItem(r4, r6, r3)
            if (r6 == 0) goto L_0x0086
            int r3 = r3 + 1
        L_0x0086:
            r9.setClickAndLongClickListenersForIcon(r7)
            int r2 = r2 + 1
        L_0x008b:
            int r1 = r1 + 1
            goto L_0x000b
        L_0x008f:
            int r10 = r9.getChildCount()
            if (r2 >= r10) goto L_0x009d
            android.view.View r10 = r9.getChildAt(r2)
            r9.removeAndRecycle(r10)
            goto L_0x008f
        L_0x009d:
            com.android.launcher3.views.AllAppsButton r10 = r9.mAllAppsButton
            if (r10 == 0) goto L_0x00b5
            android.content.res.Resources r10 = r9.getResources()
            boolean r10 = com.android.launcher3.Utilities.isRtl(r10)
            if (r10 == 0) goto L_0x00ac
            goto L_0x00b0
        L_0x00ac:
            int r0 = r9.getChildCount()
        L_0x00b0:
            com.android.launcher3.views.AllAppsButton r10 = r9.mAllAppsButton
            r9.addView(r10, r0)
        L_0x00b5:
            int r10 = r9.calculateThemeIconsBackground()
            r9.mThemeIconsBackground = r10
            r9.setThemedIconsBackgroundColor(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.taskbar.TaskbarView.updateHotseatItems(com.android.launcher3.model.data.ItemInfo[]):void");
    }

    public void setThemedIconsBackgroundColor(int i) {
        for (View view : getIconViews()) {
            if (view instanceof DoubleShadowBubbleTextView) {
                DoubleShadowBubbleTextView doubleShadowBubbleTextView = (DoubleShadowBubbleTextView) view;
                if (doubleShadowBubbleTextView.getIcon() != null && (doubleShadowBubbleTextView.getIcon() instanceof ThemedIconDrawable)) {
                    ((ThemedIconDrawable) doubleShadowBubbleTextView.getIcon()).changeBackgroundColor(i);
                }
            }
        }
    }

    public void setClickAndLongClickListenersForIcon(View view) {
        view.setOnClickListener(this.mIconClickListener);
        view.setOnLongClickListener(this.mIconLongClickListener);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        int i5 = ((this.mItemMarginLeftRight * 2) + this.mIconTouchSize) * childCount;
        int hotseatEndOffset = ApiWrapper.getHotseatEndOffset(getContext());
        boolean isLayoutRtl = isLayoutRtl();
        int i6 = i3 - (((i3 - i) - i5) / 2);
        boolean z2 = false;
        if (!isLayoutRtl ? i6 > i3 - hotseatEndOffset : hotseatEndOffset > i6 - i5) {
            z2 = true;
        }
        if (z2) {
            i6 += isLayoutRtl ? hotseatEndOffset - (i6 - i5) : (i3 - hotseatEndOffset) - i6;
        }
        this.mIconLayoutBounds.right = i6;
        this.mIconLayoutBounds.top = ((i4 - i2) - this.mIconTouchSize) / 2;
        Rect rect = this.mIconLayoutBounds;
        rect.bottom = rect.top + this.mIconTouchSize;
        while (childCount > 0) {
            View childAt = getChildAt(childCount - 1);
            int i7 = i6 - this.mItemMarginLeftRight;
            int i8 = i7 - this.mIconTouchSize;
            childAt.layout(i8, this.mIconLayoutBounds.top, i7, this.mIconLayoutBounds.bottom);
            i6 = i8 - this.mItemMarginLeftRight;
            childCount--;
        }
        this.mIconLayoutBounds.left = i6;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.mTouchEnabled) {
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* JADX INFO: finally extract failed */
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mTouchEnabled) {
            return true;
        }
        if (((float) this.mIconLayoutBounds.left) <= motionEvent.getX() && motionEvent.getX() <= ((float) this.mIconLayoutBounds.right)) {
            return true;
        }
        if (!this.mControllerCallbacks.onTouchEvent(motionEvent)) {
            return super.onTouchEvent(motionEvent);
        }
        int action = motionEvent.getAction();
        try {
            motionEvent.setAction(3);
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            motionEvent.setAction(action);
            return onTouchEvent;
        } catch (Throwable th) {
            motionEvent.setAction(action);
            throw th;
        }
    }

    public void setTouchesEnabled(boolean z) {
        this.mTouchEnabled = z;
    }

    public boolean isEventOverAnyItem(MotionEvent motionEvent) {
        getLocationOnScreen(this.mTempOutLocation);
        int x = ((int) motionEvent.getX()) - this.mTempOutLocation[0];
        int y = ((int) motionEvent.getY()) - this.mTempOutLocation[1];
        if (!isShown() || !this.mIconLayoutBounds.contains(x, y)) {
            return false;
        }
        return true;
    }

    public Rect getIconLayoutBounds() {
        return this.mIconLayoutBounds;
    }

    public View[] getIconViews() {
        int childCount = getChildCount();
        View[] viewArr = new View[childCount];
        for (int i = 0; i < childCount; i++) {
            viewArr[i] = getChildAt(i);
        }
        return viewArr;
    }

    public View getAllAppsButtonView() {
        return this.mAllAppsButton;
    }

    public void drawFolderLeaveBehindForIcon(FolderIcon folderIcon) {
        this.mLeaveBehindFolderIcon = folderIcon;
        invalidate();
    }

    public void clearFolderLeaveBehind(FolderIcon folderIcon) {
        this.mLeaveBehindFolderIcon = null;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mLeaveBehindFolderIcon != null) {
            canvas.save();
            canvas.translate((float) this.mLeaveBehindFolderIcon.getLeft(), (float) this.mLeaveBehindFolderIcon.getTop());
            this.mLeaveBehindFolderIcon.getFolderBackground().drawLeaveBehind(canvas);
            canvas.restore();
        }
    }

    private View inflate(int i) {
        return this.mActivityContext.getViewCache().getView(i, this.mActivityContext, this);
    }

    public boolean areIconsVisible() {
        return getVisibility() == 0;
    }

    public void mapOverItems(LauncherBindableItemsContainer.ItemOperator itemOperator) {
        int i = 0;
        while (i < getChildCount()) {
            View childAt = getChildAt(i);
            if (!itemOperator.evaluate((ItemInfo) childAt.getTag(), childAt)) {
                i++;
            } else {
                return;
            }
        }
    }

    public View getFirstMatch(Predicate<ItemInfo>... predicateArr) {
        for (Predicate<ItemInfo> predicate : predicateArr) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if ((childAt.getTag() instanceof ItemInfo) && predicate.test((ItemInfo) childAt.getTag())) {
                    return childAt;
                }
            }
        }
        return this.mAllAppsButton;
    }
}
