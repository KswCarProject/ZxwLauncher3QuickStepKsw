package com.android.launcher3.folder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.PagedView;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pageindicators.PageIndicatorDots;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.util.LauncherBindableItemsContainer;
import com.android.launcher3.util.ViewCache;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.ClipPathView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class FolderPagedView extends PagedView<PageIndicatorDots> implements ClipPathView {
    private static final int REORDER_ANIMATION_DURATION = 230;
    private static final float SCROLL_HINT_FRACTION = 0.07f;
    private static final int START_VIEW_REORDER_DELAY = 30;
    private static final String TAG = "FolderPagedView";
    private static final float VIEW_REORDER_DELAY_FACTOR = 0.9f;
    private static final int[] sTmpArray = new int[2];
    private int mAllocatedContentSize;
    private Path mClipPath;
    private final ViewGroupFocusHelper mFocusIndicatorHelper;
    private Folder mFolder;
    @ViewDebug.ExportedProperty(category = "launcher")
    private int mGridCountX;
    @ViewDebug.ExportedProperty(category = "launcher")
    private int mGridCountY;
    public final boolean mIsRtl;
    private final FolderGridOrganizer mOrganizer;
    final ArrayMap<View, Runnable> mPendingAnimations = new ArrayMap<>();
    private final ViewCache mViewCache;
    private boolean mViewsBound = false;

    static /* synthetic */ int lambda$getFirstItem$0(ShortcutAndWidgetContainer shortcutAndWidgetContainer) {
        return 0;
    }

    public FolderPagedView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOrganizer = new FolderGridOrganizer(LauncherAppState.getIDP(context));
        this.mIsRtl = Utilities.isRtl(getResources());
        setImportantForAccessibility(1);
        this.mFocusIndicatorHelper = new ViewGroupFocusHelper(this);
        this.mViewCache = ((ActivityContext) ActivityContext.lookupContext(context)).getViewCache();
    }

    public void setFolder(Folder folder) {
        this.mFolder = folder;
        this.mPageIndicator = folder.findViewById(R.id.folder_page_indicator);
        initParentViews(folder);
    }

    private void setupContentDimensions(int i) {
        this.mAllocatedContentSize = i;
        this.mOrganizer.setContentSize(i);
        this.mGridCountX = this.mOrganizer.getCountX();
        this.mGridCountY = this.mOrganizer.getCountY();
        for (int pageCount = getPageCount() - 1; pageCount >= 0; pageCount--) {
            getPageAt(pageCount).setGridSize(this.mGridCountX, this.mGridCountY);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.mClipPath != null) {
            int save = canvas.save();
            canvas.clipPath(this.mClipPath);
            this.mFocusIndicatorHelper.draw(canvas);
            super.dispatchDraw(canvas);
            canvas.restoreToCount(save);
            return;
        }
        this.mFocusIndicatorHelper.draw(canvas);
        super.dispatchDraw(canvas);
    }

    public void bindItems(List<WorkspaceItemInfo> list) {
        if (this.mViewsBound) {
            unbindItems();
        }
        arrangeChildren((List) list.stream().map(new Function() {
            public final Object apply(Object obj) {
                return FolderPagedView.this.createNewView((WorkspaceItemInfo) obj);
            }
        }).collect(Collectors.toList()));
        this.mViewsBound = true;
    }

    public void unbindItems() {
        int childCount = getChildCount();
        while (true) {
            childCount--;
            if (childCount >= 0) {
                CellLayout cellLayout = (CellLayout) getChildAt(childCount);
                ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
                for (int childCount2 = shortcutsAndWidgets.getChildCount() - 1; childCount2 >= 0; childCount2--) {
                    shortcutsAndWidgets.getChildAt(childCount2).setVisibility(0);
                    this.mViewCache.recycleView(R.layout.folder_application, shortcutsAndWidgets.getChildAt(childCount2));
                }
                cellLayout.removeAllViews();
                this.mViewCache.recycleView(R.layout.folder_page, cellLayout);
            } else {
                removeAllViews();
                this.mViewsBound = false;
                return;
            }
        }
    }

    public boolean areViewsBound() {
        return this.mViewsBound;
    }

    public View createAndAddViewForRank(WorkspaceItemInfo workspaceItemInfo, int i) {
        View createNewView = createNewView(workspaceItemInfo);
        if (!this.mViewsBound) {
            return createNewView;
        }
        ArrayList arrayList = new ArrayList(this.mFolder.getIconsInReadingOrder());
        arrayList.add(i, createNewView);
        arrangeChildren(arrayList);
        return createNewView;
    }

    public void addViewForRank(View view, WorkspaceItemInfo workspaceItemInfo, int i) {
        int maxItemsPerPage = i / this.mOrganizer.getMaxItemsPerPage();
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setCellXY(this.mOrganizer.getPosForRank(i));
        getPageAt(maxItemsPerPage).addViewToCellLayout(view, -1, workspaceItemInfo.getViewId(), layoutParams, true);
    }

    public View createNewView(WorkspaceItemInfo workspaceItemInfo) {
        if (workspaceItemInfo == null) {
            return null;
        }
        BubbleTextView bubbleTextView = (BubbleTextView) this.mViewCache.getView(R.layout.folder_application, getContext(), (ViewGroup) null);
        bubbleTextView.applyFromWorkspaceItem(workspaceItemInfo);
        bubbleTextView.setOnClickListener(ItemClickHandler.INSTANCE);
        bubbleTextView.setOnLongClickListener(this.mFolder);
        bubbleTextView.setOnFocusChangeListener(this.mFocusIndicatorHelper);
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) bubbleTextView.getLayoutParams();
        if (layoutParams == null) {
            bubbleTextView.setLayoutParams(new CellLayout.LayoutParams(workspaceItemInfo.cellX, workspaceItemInfo.cellY, workspaceItemInfo.spanX, workspaceItemInfo.spanY));
        } else {
            layoutParams.cellX = workspaceItemInfo.cellX;
            layoutParams.cellY = workspaceItemInfo.cellY;
            layoutParams.cellVSpan = 1;
            layoutParams.cellHSpan = 1;
        }
        return bubbleTextView;
    }

    public CellLayout getPageAt(int i) {
        return (CellLayout) getChildAt(i);
    }

    public CellLayout getCurrentCellLayout() {
        return getPageAt(getNextPage());
    }

    private CellLayout createAndAddNewPage() {
        DeviceProfile deviceProfile = this.mFolder.mActivityContext.getDeviceProfile();
        CellLayout cellLayout = (CellLayout) this.mViewCache.getView(R.layout.folder_page, getContext(), this);
        cellLayout.setCellDimensions(deviceProfile.folderCellWidthPx, deviceProfile.folderCellHeightPx);
        cellLayout.getShortcutsAndWidgets().setMotionEventSplittingEnabled(false);
        cellLayout.setInvertIfRtl(true);
        cellLayout.setGridSize(this.mGridCountX, this.mGridCountY);
        addView(cellLayout, -1, generateDefaultLayoutParams());
        return cellLayout;
    }

    /* access modifiers changed from: protected */
    public int getChildGap(int i, int i2) {
        return getPaddingLeft() + getPaddingRight();
    }

    public void setFixedSize(int i, int i2) {
        int paddingLeft = i - (getPaddingLeft() + getPaddingRight());
        int paddingTop = i2 - (getPaddingTop() + getPaddingBottom());
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            ((CellLayout) getChildAt(childCount)).setFixedSize(paddingLeft, paddingTop);
        }
    }

    public void removeItem(View view) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            getPageAt(childCount).removeView(view);
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (this.mMaxScroll > 0) {
            ((PageIndicatorDots) this.mPageIndicator).setScroll(i, this.mMaxScroll);
        }
    }

    public void arrangeChildren(List<View> list) {
        int i;
        View view;
        int size = list.size();
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            CellLayout cellLayout = (CellLayout) getChildAt(i3);
            cellLayout.removeAllViews();
            arrayList.add(cellLayout);
        }
        this.mOrganizer.setFolderInfo(this.mFolder.getInfo());
        setupContentDimensions(size);
        Iterator it = arrayList.iterator();
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        CellLayout cellLayout2 = null;
        while (true) {
            i = 1;
            if (i4 >= size) {
                break;
            }
            if (list.size() > i4) {
                view = list.get(i4);
            } else {
                List<View> list2 = list;
                view = null;
            }
            if (cellLayout2 == null || i5 >= this.mOrganizer.getMaxItemsPerPage()) {
                if (it.hasNext()) {
                    cellLayout2 = (CellLayout) it.next();
                } else {
                    cellLayout2 = createAndAddNewPage();
                }
                i5 = 0;
            }
            if (view != null) {
                CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
                layoutParams.setCellXY(this.mOrganizer.getPosForRank(i6));
                cellLayout2.addViewToCellLayout(view, -1, ((ItemInfo) view.getTag()).getViewId(), layoutParams, true);
                if (this.mOrganizer.isItemInPreview(i6) && (view instanceof BubbleTextView)) {
                    ((BubbleTextView) view).verifyHighRes();
                }
            }
            i6++;
            i5++;
            i4++;
        }
        boolean z = false;
        while (it.hasNext()) {
            removeView((View) it.next());
            z = true;
        }
        if (z) {
            setCurrentPage(0);
        }
        setEnableOverscroll(getPageCount() > 1);
        PageIndicatorDots pageIndicatorDots = (PageIndicatorDots) this.mPageIndicator;
        if (getPageCount() <= 1) {
            i2 = 8;
        }
        pageIndicatorDots.setVisibility(i2);
        FolderNameEditText folderNameEditText = this.mFolder.mFolderName;
        if (getPageCount() > 1) {
            i = this.mIsRtl ? 5 : 3;
        }
        folderNameEditText.setGravity(i);
    }

    public int getDesiredWidth() {
        if (getPageCount() <= 0) {
            return 0;
        }
        return getPaddingRight() + getPageAt(0).getDesiredWidth() + getPaddingLeft();
    }

    public int getDesiredHeight() {
        if (getPageCount() <= 0) {
            return 0;
        }
        return getPaddingBottom() + getPageAt(0).getDesiredHeight() + getPaddingTop();
    }

    public int findNearestArea(int i, int i2) {
        int nextPage = getNextPage();
        CellLayout pageAt = getPageAt(nextPage);
        int[] iArr = sTmpArray;
        pageAt.findNearestArea(i, i2, 1, 1, iArr);
        if (this.mFolder.isLayoutRtl()) {
            iArr[0] = (pageAt.getCountX() - iArr[0]) - 1;
        }
        return Math.min(this.mAllocatedContentSize - 1, (nextPage * this.mOrganizer.getMaxItemsPerPage()) + (iArr[1] * this.mGridCountX) + iArr[0]);
    }

    public View getFirstItem() {
        return getViewInCurrentPage($$Lambda$FolderPagedView$UqeA0KM8mY4zUkQrY3oQ4qBx6Es.INSTANCE);
    }

    static /* synthetic */ int lambda$getLastItem$1(ShortcutAndWidgetContainer shortcutAndWidgetContainer) {
        return shortcutAndWidgetContainer.getChildCount() - 1;
    }

    public View getLastItem() {
        return getViewInCurrentPage($$Lambda$FolderPagedView$FN_iQFu2mqmiZNMhLSroMvN5v0.INSTANCE);
    }

    private View getViewInCurrentPage(ToIntFunction<ShortcutAndWidgetContainer> toIntFunction) {
        if (getChildCount() < 1) {
            return null;
        }
        ShortcutAndWidgetContainer shortcutsAndWidgets = getCurrentCellLayout().getShortcutsAndWidgets();
        int applyAsInt = toIntFunction.applyAsInt(shortcutsAndWidgets);
        int i = this.mGridCountX;
        if (i > 0) {
            return shortcutsAndWidgets.getChildAt(applyAsInt % i, applyAsInt / i);
        }
        return shortcutsAndWidgets.getChildAt(applyAsInt);
    }

    public View iterateOverItems(LauncherBindableItemsContainer.ItemOperator itemOperator) {
        for (int i = 0; i < getChildCount(); i++) {
            CellLayout pageAt = getPageAt(i);
            for (int i2 = 0; i2 < pageAt.getCountY(); i2++) {
                for (int i3 = 0; i3 < pageAt.getCountX(); i3++) {
                    View childAt = pageAt.getChildAt(i3, i2);
                    if (childAt != null && itemOperator.evaluate((ItemInfo) childAt.getTag(), childAt)) {
                        return childAt;
                    }
                }
            }
        }
        return null;
    }

    public String getAccessibilityDescription() {
        return getContext().getString(R.string.folder_opened, new Object[]{Integer.valueOf(this.mGridCountX), Integer.valueOf(this.mGridCountY)});
    }

    public void setFocusOnFirstChild() {
        View childAt = getCurrentCellLayout().getChildAt(0, 0);
        if (childAt != null) {
            childAt.requestFocus();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int i) {
        super.notifyPageSwitchListener(i);
        Folder folder = this.mFolder;
        if (folder != null) {
            folder.updateTextViewFocus();
        }
    }

    public void showScrollHint(int i) {
        int scrollForPage = (getScrollForPage(getNextPage()) + ((int) (((i == 0) ^ this.mIsRtl ? -0.07f : 0.07f) * ((float) getWidth())))) - getScrollX();
        if (scrollForPage != 0) {
            this.mScroller.startScroll(getScrollX(), 0, scrollForPage, 0, 500);
            invalidate();
        }
    }

    public void clearScrollHint() {
        if (getScrollX() != getScrollForPage(getNextPage())) {
            snapToPage(getNextPage());
        }
    }

    public void completePendingPageChanges() {
        if (!this.mPendingAnimations.isEmpty()) {
            for (Map.Entry entry : new ArrayMap(this.mPendingAnimations).entrySet()) {
                ((View) entry.getKey()).animate().cancel();
                ((Runnable) entry.getValue()).run();
            }
        }
    }

    public boolean rankOnCurrentPage(int i) {
        return i / this.mOrganizer.getMaxItemsPerPage() == getNextPage();
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
        super.onPageBeginTransition();
        verifyVisibleHighResIcons(getCurrentPage() - 1);
        verifyVisibleHighResIcons(getCurrentPage() + 1);
    }

    public void verifyVisibleHighResIcons(int i) {
        CellLayout pageAt = getPageAt(i);
        if (pageAt != null) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = pageAt.getShortcutsAndWidgets();
            for (int childCount = shortcutsAndWidgets.getChildCount() - 1; childCount >= 0; childCount--) {
                BubbleTextView bubbleTextView = (BubbleTextView) shortcutsAndWidgets.getChildAt(childCount);
                bubbleTextView.verifyHighRes();
                FastBitmapDrawable icon = bubbleTextView.getIcon();
                if (icon != null) {
                    icon.setCallback(bubbleTextView);
                }
            }
        }
    }

    public int getAllocatedContentSize() {
        return this.mAllocatedContentSize;
    }

    public void realTimeReorder(int i, int i2) {
        int i3;
        int i4;
        final int i5 = i;
        int i6 = i2;
        if (this.mViewsBound) {
            completePendingPageChanges();
            float f = 30.0f;
            int nextPage = getNextPage();
            int maxItemsPerPage = this.mOrganizer.getMaxItemsPerPage();
            int i7 = i6 % maxItemsPerPage;
            if (i6 / maxItemsPerPage != nextPage) {
                Log.e(TAG, "Cannot animate when the target cell is invisible");
            }
            int i8 = i5 % maxItemsPerPage;
            int i9 = i5 / maxItemsPerPage;
            if (i6 != i5) {
                int i10 = -1;
                boolean z = true;
                if (i6 > i5) {
                    if (i9 < nextPage) {
                        i10 = nextPage * maxItemsPerPage;
                        i8 = 0;
                    } else {
                        i5 = -1;
                    }
                    i3 = 1;
                } else {
                    if (i9 > nextPage) {
                        i4 = ((nextPage + 1) * maxItemsPerPage) - 1;
                        i8 = maxItemsPerPage - 1;
                    } else {
                        i5 = -1;
                        i4 = -1;
                    }
                    i10 = i4;
                    i3 = -1;
                }
                while (i5 != i10) {
                    int i11 = i5 + i3;
                    int i12 = i11 / maxItemsPerPage;
                    int i13 = i11 % maxItemsPerPage;
                    int i14 = this.mGridCountX;
                    int i15 = i13 % i14;
                    int i16 = i13 / i14;
                    CellLayout pageAt = getPageAt(i12);
                    final View childAt = pageAt.getChildAt(i15, i16);
                    if (childAt != null) {
                        if (nextPage != i12) {
                            pageAt.removeView(childAt);
                            addViewForRank(childAt, (WorkspaceItemInfo) childAt.getTag(), i5);
                        } else {
                            final float translationX = childAt.getTranslationX();
                            AnonymousClass1 r14 = new Runnable() {
                                public void run() {
                                    FolderPagedView.this.mPendingAnimations.remove(childAt);
                                    childAt.setTranslationX(translationX);
                                    ((CellLayout) childAt.getParent().getParent()).removeView(childAt);
                                    FolderPagedView folderPagedView = FolderPagedView.this;
                                    View view = childAt;
                                    folderPagedView.addViewForRank(view, (WorkspaceItemInfo) view.getTag(), i5);
                                }
                            };
                            childAt.animate().translationXBy((float) ((i3 > 0 ? z : false) ^ this.mIsRtl ? -childAt.getWidth() : childAt.getWidth())).setDuration(230).setStartDelay(0).withEndAction(r14);
                            this.mPendingAnimations.put(childAt, r14);
                        }
                    }
                    i5 = i11;
                    z = true;
                }
                if ((i7 - i8) * i3 > 0) {
                    CellLayout pageAt2 = getPageAt(nextPage);
                    int i17 = 0;
                    while (i8 != i7) {
                        int i18 = i8 + i3;
                        int i19 = this.mGridCountX;
                        View childAt2 = pageAt2.getChildAt(i18 % i19, i18 / i19);
                        int i20 = this.mGridCountX;
                        if (pageAt2.animateChildToPosition(childAt2, i8 % i20, i8 / i20, REORDER_ANIMATION_DURATION, i17, true, true)) {
                            f *= VIEW_REORDER_DELAY_FACTOR;
                            i17 = (int) (((float) i17) + f);
                        }
                        i8 = i18;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean canScroll(float f, float f2) {
        return AbstractFloatingView.getTopOpenViewWithType(this.mFolder.mActivityContext, 524286) == null;
    }

    public int itemsPerPage() {
        return this.mOrganizer.getMaxItemsPerPage();
    }

    public void setClipPath(Path path) {
        this.mClipPath = path;
        invalidate();
    }
}
