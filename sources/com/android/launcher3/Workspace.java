package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTarget;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.AccessibleDragListenerAdapter;
import com.android.launcher3.accessibility.WorkspaceAccessibilityHelper;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.dot.FolderDotInfo;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.dragndrop.SpringLoadedDragController;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.icons.BitmapRenderer;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.WorkspaceTouchListener;
import com.android.launcher3.util.EdgeEffectCompat;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.IntSparseArrayMap;
import com.android.launcher3.util.LauncherBindableItemsContainer;
import com.android.launcher3.util.OverlayEdgeEffect;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.WallpaperOffsetInterpolator;
import com.android.launcher3.widget.LauncherAppWidgetHost;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.NavigableAppWidgetHostView;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.PendingAppWidgetHostView;
import com.android.launcher3.widget.WidgetManagerHelper;
import com.android.launcher3.widget.util.WidgetSizes;
import com.android.systemui.plugins.shared.LauncherOverlayManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Workspace<T extends View & PageIndicator> extends PagedView<T> implements DropTarget, DragSource, View.OnTouchListener, DragController.DragListener, Insettable, StateManager.StateHandler<LauncherState>, WorkspaceLayoutManager, LauncherBindableItemsContainer {
    private static final int ADJACENT_SCREEN_DROP_DURATION = 300;
    private static final float ALLOW_DROP_TRANSITION_PROGRESS = 0.25f;
    public static final int ANIMATE_INTO_POSITION_AND_DISAPPEAR = 0;
    public static final int ANIMATE_INTO_POSITION_AND_REMAIN = 1;
    public static final int ANIMATE_INTO_POSITION_AND_RESIZE = 2;
    public static final int CANCEL_TWO_STAGE_WIDGET_DROP_ANIMATION = 4;
    public static final int COMPLETE_TWO_STAGE_WIDGET_DROP_ANIMATION = 3;
    public static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SMARTSPACE_HEIGHT = 1;
    private static final int DRAG_MODE_ADD_TO_FOLDER = 2;
    private static final int DRAG_MODE_CREATE_FOLDER = 1;
    private static final int DRAG_MODE_NONE = 0;
    private static final int DRAG_MODE_REORDER = 3;
    private static final boolean ENFORCE_DRAG_EVENT_ORDER = false;
    private static final int EXPANDED_SMARTSPACE_HEIGHT = 2;
    private static final float FINISHED_SWITCHING_STATE_TRANSITION_PROGRESS = 0.5f;
    static final float MAX_SWIPE_ANGLE = 1.0471976f;
    public static final int REORDER_TIMEOUT = 650;
    private static final float SIGNIFICANT_MOVE_SCREEN_WIDTH_PERCENTAGE = 0.15f;
    static final float START_DAMPING_TOUCH_SLOP_ANGLE = 0.5235988f;
    static final float TOUCH_SLOP_DAMPING_FACTOR = 4.0f;
    private boolean mAddToExistingFolderOnDrop;
    boolean mChildrenLayersEnabled;
    private boolean mCreateUserFolderOnDrop;
    private float mCurrentScale;
    boolean mDeferRemoveExtraEmptyScreen;
    DragController mDragController;
    private CellLayout.CellInfo mDragInfo;
    private int mDragMode;
    private FolderIcon mDragOverFolderIcon;
    private int mDragOverX;
    private int mDragOverY;
    private CellLayout mDragOverlappingLayout;
    private ShortcutAndWidgetContainer mDragSourceInternal;
    CellLayout mDragTargetLayout;
    float[] mDragViewVisualCenter;
    private CellLayout mDropToLayout;
    private PreviewBackground mFolderCreateBg;
    private boolean mForceDrawAdjacentPages;
    private boolean mIsEventOverQsb;
    private boolean mIsSwitchingState;
    int mLastReorderX;
    int mLastReorderY;
    final Launcher mLauncher;
    private LayoutTransition mLayoutTransition;
    private Runnable mOnOverlayHiddenCallback;
    private OverlayEdgeEffect mOverlayEdgeEffect;
    boolean mOverlayShown;
    private float mOverlayTranslation;
    private View mQsb;
    private final Alarm mReorderAlarm;
    private final IntArray mRestoredPages;
    private SparseArray<Parcelable> mSavedStates;
    final IntArray mScreenOrder;
    private SpringLoadedDragController mSpringLoadedDragController;
    private final WorkspaceStateTransitionAnimation mStateTransitionAnimation;
    /* access modifiers changed from: private */
    public final StatsLogManager mStatsLogManager;
    private boolean mStripScreensOnPageStopMoving;
    int[] mTargetCell;
    private final float[] mTempFXY;
    private final Rect mTempRect;
    private final int[] mTempXY;
    /* access modifiers changed from: private */
    public float mTransitionProgress;
    private boolean mUnlockWallpaperFromDefaultPageOnLayout;
    final WallpaperManager mWallpaperManager;
    final WallpaperOffsetInterpolator mWallpaperOffset;
    private boolean mWorkspaceFadeInAdjacentScreens;
    final IntSparseArrayMap<CellLayout> mWorkspaceScreens;
    private float mXDown;
    private float mYDown;

    public void bindAndInitFirstWorkspaceScreen() {
    }

    public boolean isDropEnabled() {
        return true;
    }

    public void prepareAccessibilityDrop() {
    }

    public Workspace(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Workspace(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mWorkspaceScreens = new IntSparseArrayMap<>();
        this.mScreenOrder = new IntArray();
        this.mDeferRemoveExtraEmptyScreen = false;
        this.mTargetCell = new int[2];
        this.mDragOverX = -1;
        this.mDragOverY = -1;
        this.mDragTargetLayout = null;
        this.mDragOverlappingLayout = null;
        this.mDropToLayout = null;
        this.mTempXY = new int[2];
        this.mTempFXY = new float[2];
        this.mTempRect = new Rect();
        this.mDragViewVisualCenter = new float[2];
        this.mIsSwitchingState = false;
        this.mChildrenLayersEnabled = true;
        this.mStripScreensOnPageStopMoving = false;
        this.mReorderAlarm = new Alarm();
        this.mDragOverFolderIcon = null;
        this.mCreateUserFolderOnDrop = false;
        this.mAddToExistingFolderOnDrop = false;
        this.mDragMode = 0;
        this.mLastReorderX = -1;
        this.mLastReorderY = -1;
        this.mRestoredPages = new IntArray();
        this.mOverlayShown = false;
        this.mForceDrawAdjacentPages = false;
        Launcher launcher = Launcher.getLauncher(context);
        this.mLauncher = launcher;
        this.mStateTransitionAnimation = new WorkspaceStateTransitionAnimation(launcher, this);
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        this.mWallpaperOffset = new WallpaperOffsetInterpolator(this);
        setHapticFeedbackEnabled(false);
        initWorkspace();
        setMotionEventSplittingEnabled(true);
        setOnTouchListener(new WorkspaceTouchListener(launcher, this));
        this.mStatsLogManager = StatsLogManager.newInstance(context);
    }

    public void setInsets(Rect rect) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mWorkspaceFadeInAdjacentScreens = deviceProfile.shouldFadeAdjacentWorkspaceScreens();
        Rect rect2 = deviceProfile.workspacePadding;
        setPadding(rect2.left, rect2.top, rect2.right, rect2.bottom);
        this.mInsets.set(rect);
        if (this.mWorkspaceFadeInAdjacentScreens) {
            setPageSpacing(deviceProfile.edgeMarginPx);
        } else {
            setPageSpacing(Math.max(Math.max(rect.left, rect.right), Math.max(deviceProfile.edgeMarginPx, rect2.left + 1)));
        }
        updateCellLayoutPadding();
        updateWorkspaceWidgetsSizes();
    }

    private void updateCellLayoutPadding() {
        this.mWorkspaceScreens.forEach(new Consumer(this.mLauncher.getDeviceProfile().cellLayoutPaddingPx) {
            public final /* synthetic */ Rect f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                ((CellLayout) obj).setPadding(this.f$0.left, this.f$0.top, this.f$0.right, this.f$0.bottom);
            }
        });
    }

    private void updateWorkspaceWidgetsSizes() {
        int size = this.mScreenOrder.size();
        for (int i = 0; i < size; i++) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = ((CellLayout) this.mWorkspaceScreens.get(this.mScreenOrder.get(i))).getShortcutsAndWidgets();
            int childCount = shortcutsAndWidgets.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = shortcutsAndWidgets.getChildAt(i2);
                if ((childAt instanceof LauncherAppWidgetHostView) && (childAt.getTag() instanceof LauncherAppWidgetInfo)) {
                    LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) childAt.getTag();
                    WidgetSizes.updateWidgetSizeRanges((LauncherAppWidgetHostView) childAt, this.mLauncher, launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.spanY);
                }
            }
        }
    }

    public int[] estimateItemSize(ItemInfo itemInfo) {
        int[] iArr = new int[2];
        if (getChildCount() > 0) {
            CellLayout cellLayout = (CellLayout) getChildAt(0);
            boolean z = itemInfo.itemType == 4;
            Rect estimateItemPosition = estimateItemPosition(cellLayout, 0, 0, itemInfo.spanX, itemInfo.spanY);
            float f = 1.0f;
            if (z) {
                DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
                f = Utilities.shrinkRect(estimateItemPosition, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y);
            }
            iArr[0] = estimateItemPosition.width();
            iArr[1] = estimateItemPosition.height();
            if (z) {
                iArr[0] = (int) (((float) iArr[0]) / f);
                iArr[1] = (int) (((float) iArr[1]) / f);
            }
            return iArr;
        }
        iArr[0] = Integer.MAX_VALUE;
        iArr[1] = Integer.MAX_VALUE;
        return iArr;
    }

    public float getWallpaperOffsetForCenterPage() {
        return getWallpaperOffsetForPage(getPageNearestToCenterOfScreen());
    }

    private float getWallpaperOffsetForPage(int i) {
        return this.mWallpaperOffset.wallpaperOffsetForScroll(getScrollForPage(i));
    }

    public int getNumPagesForWallpaperParallax() {
        return this.mWallpaperOffset.getNumPagesForWallpaperParallax();
    }

    public Rect estimateItemPosition(CellLayout cellLayout, int i, int i2, int i3, int i4) {
        Rect rect = new Rect();
        cellLayout.cellToRect(i, i2, i3, i4, rect);
        return rect;
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        ViewParent viewParent;
        CellLayout.CellInfo cellInfo = this.mDragInfo;
        if (!(cellInfo == null || cellInfo.cell == null)) {
            if (this.mDragInfo.cell instanceof LauncherAppWidgetHostView) {
                viewParent = dragObject.dragView.getContentViewParent().getParent();
            } else {
                viewParent = this.mDragInfo.cell.getParent().getParent();
            }
            ((CellLayout) viewParent).markCellsAsUnoccupiedForView(this.mDragInfo.cell);
        }
        updateChildrenLayersEnabled();
        if (!dragOptions.isAccessibleDrag || dragObject.dragSource == this) {
            this.mDeferRemoveExtraEmptyScreen = false;
            addExtraEmptyScreenOnDrag(dragObject);
            if (dragObject.dragInfo.itemType == 4 && dragObject.dragSource != this) {
                int destinationPage = getDestinationPage();
                while (true) {
                    if (destinationPage >= getPageCount()) {
                        break;
                    } else if (((CellLayout) getPageAt(destinationPage)).hasReorderSolution(dragObject.dragInfo)) {
                        setCurrentPage(destinationPage);
                        break;
                    } else {
                        destinationPage++;
                    }
                }
            }
        }
        this.mLauncher.getStateManager().goToState(LauncherState.SPRING_LOADED);
        this.mStatsLogManager.logger().withItemInfo(dragObject.dragInfo).withInstanceId(dragObject.logInstanceId).log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DRAG_STARTED);
    }

    private boolean isTwoPanelEnabled() {
        return this.mLauncher.mDeviceProfile.isTwoPanels;
    }

    public int getPanelCount() {
        if (isTwoPanelEnabled()) {
            return 2;
        }
        return super.getPanelCount();
    }

    public void deferRemoveExtraEmptyScreen() {
        this.mDeferRemoveExtraEmptyScreen = true;
    }

    public void onDragEnd() {
        updateChildrenLayersEnabled();
        final StateManager<LauncherState> stateManager = this.mLauncher.getStateManager();
        stateManager.addStateListener(new StateManager.StateListener<LauncherState>() {
            public void onStateTransitionComplete(LauncherState launcherState) {
                if (launcherState == LauncherState.NORMAL) {
                    if (!Workspace.this.mDeferRemoveExtraEmptyScreen) {
                        Workspace.this.removeExtraEmptyScreen(true);
                    }
                    stateManager.removeStateListener(this);
                }
            }
        });
        this.mDragInfo = null;
        this.mDragSourceInternal = null;
    }

    /* access modifiers changed from: protected */
    public void initWorkspace() {
        this.mCurrentPage = 0;
        setClipToPadding(false);
        setupLayoutTransition();
        setWallpaperDimension();
    }

    private void setupLayoutTransition() {
        LayoutTransition layoutTransition = new LayoutTransition();
        this.mLayoutTransition = layoutTransition;
        layoutTransition.enableTransitionType(3);
        this.mLayoutTransition.enableTransitionType(1);
        this.mLayoutTransition.setInterpolator(3, Interpolators.clampToProgress(Interpolators.ACCEL_DEACCEL, 0.0f, 0.5f));
        this.mLayoutTransition.setInterpolator(1, Interpolators.clampToProgress(Interpolators.ACCEL_DEACCEL, 0.5f, 1.0f));
        this.mLayoutTransition.disableTransitionType(2);
        this.mLayoutTransition.disableTransitionType(0);
        setLayoutTransition(this.mLayoutTransition);
    }

    /* access modifiers changed from: package-private */
    public void enableLayoutTransitions() {
        setLayoutTransition(this.mLayoutTransition);
    }

    /* access modifiers changed from: package-private */
    public void disableLayoutTransitions() {
        setLayoutTransition((LayoutTransition) null);
    }

    public void onViewAdded(View view) {
        if (view instanceof CellLayout) {
            CellLayout cellLayout = (CellLayout) view;
            cellLayout.setOnInterceptTouchListener(this);
            cellLayout.setImportantForAccessibility(2);
            super.onViewAdded(view);
            return;
        }
        throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
    }

    public void removeAllWorkspaceScreens() {
        disableLayoutTransitions();
        View view = this.mQsb;
        if (view != null) {
            ((ViewGroup) view.getParent()).removeView(this.mQsb);
        }
        removeFolderListeners();
        removeAllViews();
        this.mScreenOrder.clear();
        this.mWorkspaceScreens.clear();
        this.mLauncher.mHandler.removeCallbacksAndMessages(DeferredWidgetRefresh.class);
        bindAndInitFirstWorkspaceScreen();
        enableLayoutTransitions();
    }

    public void insertNewWorkspaceScreenBeforeEmptyScreen(int i) {
        int indexOf = this.mScreenOrder.indexOf(WorkspaceLayoutManager.EXTRA_EMPTY_SCREEN_ID);
        if (indexOf < 0) {
            indexOf = this.mScreenOrder.size();
        }
        insertNewWorkspaceScreen(i, indexOf);
    }

    public void insertNewWorkspaceScreen(int i) {
        insertNewWorkspaceScreen(i, getChildCount());
    }

    public CellLayout insertNewWorkspaceScreen(int i, int i2) {
        if (!this.mWorkspaceScreens.containsKey(i)) {
            CellLayout cellLayout = (CellLayout) LayoutInflater.from(getContext()).inflate(R.layout.workspace_screen, this, false);
            this.mWorkspaceScreens.put(i, cellLayout);
            this.mScreenOrder.add(i2, i);
            addView(cellLayout, i2);
            this.mStateTransitionAnimation.applyChildState(this.mLauncher.getStateManager().getState(), cellLayout, i2);
            updatePageScrollValues();
            updateCellLayoutPadding();
            return cellLayout;
        }
        throw new RuntimeException("Screen id " + i + " already exists!");
    }

    private void addExtraEmptyScreenOnDrag(DropTarget.DragObject dragObject) {
        ShortcutAndWidgetContainer shortcutAndWidgetContainer = this.mDragSourceInternal;
        boolean z = false;
        boolean z2 = true;
        if (shortcutAndWidgetContainer != null) {
            int childCount = shortcutAndWidgetContainer.getChildCount();
            if (isTwoPanelEnabled() && !(this.mDragSourceInternal.getParent() instanceof Hotseat)) {
                childCount += ((CellLayout) this.mWorkspaceScreens.get(getScreenPair(dragObject.dragInfo.screenId))).getShortcutsAndWidgets().getChildCount();
            }
            if (dragObject.dragView.getContentView() instanceof LauncherAppWidgetHostView) {
                childCount++;
            }
            boolean z3 = childCount == 1;
            if (getLeftmostVisiblePageForIndex(indexOfChild((CellLayout) this.mDragSourceInternal.getParent())) != getLeftmostVisiblePageForIndex(getPageCount() - 1)) {
                z2 = false;
            }
            z = z3;
        } else {
            z2 = false;
        }
        if (!z || !z2) {
            forEachExtraEmptyPageId(new Consumer() {
                public final void accept(Object obj) {
                    Workspace.this.lambda$addExtraEmptyScreenOnDrag$1$Workspace((Integer) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$addExtraEmptyScreenOnDrag$1$Workspace(Integer num) {
        if (!this.mWorkspaceScreens.containsKey(num.intValue())) {
            insertNewWorkspaceScreen(num.intValue());
        }
    }

    public void addExtraEmptyScreens() {
        forEachExtraEmptyPageId(new Consumer() {
            public final void accept(Object obj) {
                Workspace.this.lambda$addExtraEmptyScreens$2$Workspace((Integer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$addExtraEmptyScreens$2$Workspace(Integer num) {
        if (!this.mWorkspaceScreens.containsKey(num.intValue())) {
            insertNewWorkspaceScreen(num.intValue());
        }
    }

    private void forEachExtraEmptyPageId(Consumer<Integer> consumer) {
        consumer.accept(Integer.valueOf(WorkspaceLayoutManager.EXTRA_EMPTY_SCREEN_ID));
        if (isTwoPanelEnabled()) {
            consumer.accept(-200);
        }
    }

    private void convertFinalScreenToEmptyScreenIfNecessary() {
        if (!this.mLauncher.isWorkspaceLoading()) {
            int panelCount = getPanelCount();
            if (!hasExtraEmptyScreens() && this.mScreenOrder.size() >= panelCount) {
                SparseArray sparseArray = new SparseArray();
                int size = this.mScreenOrder.size();
                int i = size - panelCount;
                while (i < size) {
                    int i2 = this.mScreenOrder.get(i);
                    CellLayout cellLayout = (CellLayout) this.mWorkspaceScreens.get(i2);
                    if (cellLayout != null && cellLayout.getShortcutsAndWidgets().getChildCount() == 0 && !cellLayout.isDropPending()) {
                        sparseArray.append(i2, cellLayout);
                        i++;
                    } else {
                        return;
                    }
                }
                for (int i3 = 0; i3 < sparseArray.size(); i3++) {
                    int keyAt = sparseArray.keyAt(i3);
                    CellLayout cellLayout2 = (CellLayout) sparseArray.get(keyAt);
                    this.mWorkspaceScreens.remove(keyAt);
                    this.mScreenOrder.removeValue(keyAt);
                    IntSparseArrayMap<CellLayout> intSparseArrayMap = this.mWorkspaceScreens;
                    int i4 = WorkspaceLayoutManager.EXTRA_EMPTY_SCREEN_ID;
                    if (intSparseArrayMap.containsKey(WorkspaceLayoutManager.EXTRA_EMPTY_SCREEN_ID)) {
                        i4 = -200;
                    }
                    this.mWorkspaceScreens.put(i4, cellLayout2);
                    this.mScreenOrder.add(i4);
                }
            }
        }
    }

    public void removeExtraEmptyScreen(boolean z) {
        removeExtraEmptyScreenDelayed(0, z, (Runnable) null);
    }

    public void removeExtraEmptyScreenDelayed(int i, boolean z, Runnable runnable) {
        if (!this.mLauncher.isWorkspaceLoading()) {
            if (i > 0) {
                postDelayed(new Runnable(z, runnable) {
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ Runnable f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        Workspace.this.lambda$removeExtraEmptyScreenDelayed$3$Workspace(this.f$1, this.f$2);
                    }
                }, (long) i);
                return;
            }
            convertFinalScreenToEmptyScreenIfNecessary();
            if (hasExtraEmptyScreens()) {
                forEachExtraEmptyPageId(new Consumer() {
                    public final void accept(Object obj) {
                        Workspace.this.lambda$removeExtraEmptyScreenDelayed$4$Workspace((Integer) obj);
                    }
                });
                setCurrentPage(getNextPage());
                showPageIndicatorAtCurrentScroll();
            }
            if (z) {
                stripEmptyScreens();
            }
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public /* synthetic */ void lambda$removeExtraEmptyScreenDelayed$3$Workspace(boolean z, Runnable runnable) {
        removeExtraEmptyScreenDelayed(0, z, runnable);
    }

    public /* synthetic */ void lambda$removeExtraEmptyScreenDelayed$4$Workspace(Integer num) {
        removeView((View) this.mWorkspaceScreens.get(num.intValue()));
        this.mWorkspaceScreens.remove(num.intValue());
        this.mScreenOrder.removeValue(num.intValue());
    }

    public boolean hasExtraEmptyScreens() {
        return this.mWorkspaceScreens.containsKey(WorkspaceLayoutManager.EXTRA_EMPTY_SCREEN_ID) && getChildCount() > getPanelCount() && (!isTwoPanelEnabled() || this.mWorkspaceScreens.containsKey(-200));
    }

    public IntSet commitExtraEmptyScreens() {
        if (this.mLauncher.isWorkspaceLoading()) {
            return new IntSet();
        }
        IntSet intSet = new IntSet();
        forEachExtraEmptyPageId(new Consumer(intSet) {
            public final /* synthetic */ IntSet f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                Workspace.this.lambda$commitExtraEmptyScreens$5$Workspace(this.f$1, (Integer) obj);
            }
        });
        return intSet;
    }

    public /* synthetic */ void lambda$commitExtraEmptyScreens$5$Workspace(IntSet intSet, Integer num) {
        intSet.add(commitExtraEmptyScreen(num.intValue()));
    }

    private int commitExtraEmptyScreen(int i) {
        CellLayout cellLayout = (CellLayout) this.mWorkspaceScreens.get(i);
        this.mWorkspaceScreens.remove(i);
        this.mScreenOrder.removeValue(i);
        int i2 = LauncherSettings.Settings.call(getContext().getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getInt("value");
        while (this.mWorkspaceScreens.containsKey(i2)) {
            i2++;
        }
        this.mWorkspaceScreens.put(i2, cellLayout);
        this.mScreenOrder.add(i2);
        return i2;
    }

    public Hotseat getHotseat() {
        return this.mLauncher.getHotseat();
    }

    public void onAddDropTarget(DropTarget dropTarget) {
        this.mDragController.addDropTarget(dropTarget);
    }

    public CellLayout getScreenWithId(int i) {
        return (CellLayout) this.mWorkspaceScreens.get(i);
    }

    public int getIdForScreen(CellLayout cellLayout) {
        int indexOfValue = this.mWorkspaceScreens.indexOfValue(cellLayout);
        if (indexOfValue != -1) {
            return this.mWorkspaceScreens.keyAt(indexOfValue);
        }
        return -1;
    }

    public int getPageIndexForScreenId(int i) {
        return indexOfChild((View) this.mWorkspaceScreens.get(i));
    }

    public IntSet getCurrentPageScreenIds() {
        return IntSet.wrap(getScreenIdForPageIndex(getCurrentPage()));
    }

    public int getScreenIdForPageIndex(int i) {
        if (i < 0 || i >= this.mScreenOrder.size()) {
            return -1;
        }
        return this.mScreenOrder.get(i);
    }

    public IntArray getScreenOrder() {
        return this.mScreenOrder;
    }

    public int getScreenPair(int i) {
        if (i == -201) {
            return -200;
        }
        if (i == -200) {
            return WorkspaceLayoutManager.EXTRA_EMPTY_SCREEN_ID;
        }
        return i % 2 == 0 ? i + 1 : i - 1;
    }

    public CellLayout getScreenPair(CellLayout cellLayout) {
        int idForScreen;
        if (isTwoPanelEnabled() && (idForScreen = getIdForScreen(cellLayout)) != -1) {
            return getScreenWithId(getScreenPair(idForScreen));
        }
        return null;
    }

    public void stripEmptyScreens() {
        if (!this.mLauncher.isWorkspaceLoading()) {
            if (isPageInTransition()) {
                this.mStripScreensOnPageStopMoving = true;
                return;
            }
            int nextPage = getNextPage();
            IntArray intArray = new IntArray();
            int size = this.mWorkspaceScreens.size();
            for (int i = 0; i < size; i++) {
                int keyAt = this.mWorkspaceScreens.keyAt(i);
                if (((CellLayout) this.mWorkspaceScreens.valueAt(i)).getShortcutsAndWidgets().getChildCount() == 0) {
                    intArray.add(keyAt);
                }
            }
            if (isTwoPanelEnabled()) {
                Iterator<Integer> it = intArray.iterator();
                while (it.hasNext()) {
                    if (!intArray.contains(getScreenPair(it.next().intValue()))) {
                        it.remove();
                    }
                }
            }
            int panelCount = getPanelCount();
            int i2 = 0;
            for (int i3 = 0; i3 < intArray.size(); i3++) {
                int i4 = intArray.get(i3);
                CellLayout cellLayout = (CellLayout) this.mWorkspaceScreens.get(i4);
                this.mWorkspaceScreens.remove(i4);
                this.mScreenOrder.removeValue(i4);
                if (getChildCount() > panelCount) {
                    if (indexOfChild(cellLayout) < nextPage) {
                        i2++;
                    }
                    removeView(cellLayout);
                } else {
                    int i5 = (!isTwoPanelEnabled() || i4 % 2 != 1) ? WorkspaceLayoutManager.EXTRA_EMPTY_SCREEN_ID : -200;
                    this.mWorkspaceScreens.put(i5, cellLayout);
                    this.mScreenOrder.add(i5);
                }
            }
            if (i2 >= 0) {
                setCurrentPage(nextPage - i2);
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return shouldConsumeTouch(view);
    }

    private boolean shouldConsumeTouch(View view) {
        return !workspaceIconsCanBeDragged() || (!workspaceInModalState() && !isVisible(view));
    }

    public boolean isSwitchingState() {
        return this.mIsSwitchingState;
    }

    public boolean isFinishedSwitchingState() {
        return !this.mIsSwitchingState || this.mTransitionProgress > 0.5f;
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        if (workspaceInModalState() || !isFinishedSwitchingState()) {
            return false;
        }
        return super.dispatchUnhandledMove(view, i);
    }

    /* access modifiers changed from: protected */
    public void updateIsBeingDraggedOnTouchDown(MotionEvent motionEvent) {
        super.updateIsBeingDraggedOnTouchDown(motionEvent);
        this.mXDown = motionEvent.getX();
        this.mYDown = motionEvent.getY();
        boolean z = false;
        if (this.mQsb != null) {
            this.mTempFXY[0] = this.mXDown + ((float) getScrollX());
            this.mTempFXY[1] = this.mYDown + ((float) getScrollY());
            Utilities.mapCoordInSelfToDescendant(this.mQsb, this, this.mTempFXY);
            if (((float) this.mQsb.getLeft()) <= this.mTempFXY[0] && ((float) this.mQsb.getRight()) >= this.mTempFXY[0] && ((float) this.mQsb.getTop()) <= this.mTempFXY[1] && ((float) this.mQsb.getBottom()) >= this.mTempFXY[1]) {
                z = true;
            }
            this.mIsEventOverQsb = z;
            return;
        }
        this.mIsEventOverQsb = false;
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent) {
        if (isFinishedSwitchingState() && !this.mIsEventOverQsb) {
            float abs = Math.abs(motionEvent.getX() - this.mXDown);
            float abs2 = Math.abs(motionEvent.getY() - this.mYDown);
            if (Float.compare(abs, 0.0f) != 0) {
                float atan = (float) Math.atan((double) (abs2 / abs));
                if (abs > ((float) this.mTouchSlop) || abs2 > ((float) this.mTouchSlop)) {
                    cancelCurrentPageLongPress();
                }
                if (atan <= MAX_SWIPE_ANGLE) {
                    if (atan > START_DAMPING_TOUCH_SLOP_ANGLE) {
                        super.determineScrollingStart(motionEvent, (((float) Math.sqrt((double) ((atan - START_DAMPING_TOUCH_SLOP_ANGLE) / START_DAMPING_TOUCH_SLOP_ANGLE))) * TOUCH_SLOP_DAMPING_FACTOR) + 1.0f);
                    } else {
                        super.determineScrollingStart(motionEvent);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
        super.onPageBeginTransition();
        updateChildrenLayersEnabled();
    }

    /* access modifiers changed from: protected */
    public void onPageEndTransition() {
        super.onPageEndTransition();
        updateChildrenLayersEnabled();
        if (this.mDragController.isDragging() && workspaceInModalState()) {
            this.mDragController.forceTouchMove();
        }
        if (this.mStripScreensOnPageStopMoving) {
            stripEmptyScreens();
            this.mStripScreensOnPageStopMoving = false;
        }
        this.mLauncher.onPageEndTransition();
    }

    public void setLauncherOverlay(LauncherOverlayManager.LauncherOverlay launcherOverlay) {
        OverlayEdgeEffect overlayEdgeEffect = launcherOverlay == null ? null : new OverlayEdgeEffect(getContext(), launcherOverlay);
        this.mOverlayEdgeEffect = overlayEdgeEffect;
        EdgeEffectCompat edgeEffectCompat = overlayEdgeEffect;
        if (launcherOverlay == null) {
            edgeEffectCompat = new EdgeEffectCompat(getContext());
        }
        if (this.mIsRtl) {
            this.mEdgeGlowRight = edgeEffectCompat;
        } else {
            this.mEdgeGlowLeft = edgeEffectCompat;
        }
        onOverlayScrollChanged(0.0f);
    }

    public boolean hasOverlay() {
        return this.mOverlayEdgeEffect != null;
    }

    /* access modifiers changed from: protected */
    public void snapToDestination() {
        OverlayEdgeEffect overlayEdgeEffect = this.mOverlayEdgeEffect;
        if (overlayEdgeEffect == null || overlayEdgeEffect.isFinished()) {
            super.snapToDestination();
        } else {
            snapToPageImmediately(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        boolean z = true;
        if (!(this.mIsSwitchingState && this.mLauncher.getStateManager().getCurrentStableState() != LauncherState.HINT_STATE) && (getLayoutTransition() == null || !getLayoutTransition().isRunning())) {
            z = false;
        }
        if (!z) {
            showPageIndicatorAtCurrentScroll();
        }
        updatePageAlphaValues();
        updatePageScrollValues();
        enableHwLayersOnVisiblePages();
    }

    public void showPageIndicatorAtCurrentScroll() {
        if (this.mPageIndicator != null) {
            ((PageIndicator) this.mPageIndicator).setScroll(getScrollX(), computeMaxScroll());
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldFlingForVelocity(int i) {
        return Float.compare(Math.abs(this.mOverlayTranslation), 0.0f) == 0 && super.shouldFlingForVelocity(i);
    }

    public void onOverlayScrollChanged(float f) {
        if (Float.compare(f, 1.0f) == 0) {
            if (!this.mOverlayShown) {
                this.mLauncher.getStatsLogManager().logger().withSrcState(2).withDstState(2).withContainerInfo((LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(0)).build()).log(StatsLogManager.LauncherEvent.LAUNCHER_SWIPELEFT);
            }
            this.mOverlayShown = true;
            this.mLauncher.onOverlayVisibilityChanged(true);
        } else if (Float.compare(f, 0.0f) == 0) {
            if (this.mOverlayShown) {
                this.mLauncher.getStatsLogManager().logger().withSrcState(2).withDstState(2).withContainerInfo((LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(-1)).build()).log(StatsLogManager.LauncherEvent.LAUNCHER_SWIPERIGHT);
            } else if (Float.compare(this.mOverlayTranslation, 0.0f) != 0) {
                announcePageForAccessibility();
            }
            this.mOverlayShown = false;
            this.mLauncher.onOverlayVisibilityChanged(false);
            tryRunOverlayCallback();
        }
        float min = Math.min(1.0f, Math.max(f - 0.0f, 0.0f) / 1.0f);
        float interpolation = 1.0f - Interpolators.DEACCEL_3.getInterpolation(min);
        float measuredWidth = ((float) this.mLauncher.getDragLayer().getMeasuredWidth()) * min;
        if (this.mIsRtl) {
            measuredWidth = -measuredWidth;
        }
        this.mOverlayTranslation = measuredWidth;
        this.mLauncher.getDragLayer().setTranslationX(measuredWidth);
        Log.d(TestProtocol.BAD_STATE, "Workspace onOverlayScrollChanged DragLayer ALPHA_INDEX_OVERLAY=" + interpolation);
        this.mLauncher.getDragLayer().getAlphaProperty(0).setValue(interpolation);
    }

    /* access modifiers changed from: private */
    public boolean tryRunOverlayCallback() {
        if (this.mOnOverlayHiddenCallback == null) {
            return true;
        }
        if (this.mOverlayShown || !hasWindowFocus()) {
            return false;
        }
        this.mOnOverlayHiddenCallback.run();
        this.mOnOverlayHiddenCallback = null;
        return true;
    }

    public boolean runOnOverlayHidden(Runnable runnable) {
        Runnable runnable2 = this.mOnOverlayHiddenCallback;
        if (runnable2 == null) {
            this.mOnOverlayHiddenCallback = runnable;
        } else {
            this.mOnOverlayHiddenCallback = new Runnable(runnable2, runnable) {
                public final /* synthetic */ Runnable f$0;
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    Workspace.lambda$runOnOverlayHidden$6(this.f$0, this.f$1);
                }
            };
        }
        if (tryRunOverlayCallback()) {
            return false;
        }
        final ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver == null || !viewTreeObserver.isAlive()) {
            return true;
        }
        viewTreeObserver.addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
            public void onWindowFocusChanged(boolean z) {
                if (Workspace.this.tryRunOverlayCallback() && viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnWindowFocusChangeListener(this);
                }
            }
        });
        return true;
    }

    static /* synthetic */ void lambda$runOnOverlayHidden$6(Runnable runnable, Runnable runnable2) {
        runnable.run();
        runnable2.run();
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int i) {
        super.notifyPageSwitchListener(i);
        if (i != this.mCurrentPage) {
            this.mLauncher.getStatsLogManager().logger().withSrcState(2).withDstState(2).withContainerInfo((LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(i)).build()).log(i < this.mCurrentPage ? StatsLogManager.LauncherEvent.LAUNCHER_SWIPERIGHT : StatsLogManager.LauncherEvent.LAUNCHER_SWIPELEFT);
        }
    }

    /* access modifiers changed from: protected */
    public void setWallpaperDimension() {
        Executors.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            public void run() {
                Point point = LauncherAppState.getIDP(Workspace.this.getContext()).defaultWallpaperSize;
                if (point.x != Workspace.this.mWallpaperManager.getDesiredMinimumWidth() || point.y != Workspace.this.mWallpaperManager.getDesiredMinimumHeight()) {
                    Workspace.this.mWallpaperManager.suggestDesiredDimensions(point.x, point.y);
                }
            }
        });
    }

    public void lockWallpaperToDefaultPage() {
        this.mWallpaperOffset.setLockToDefaultPage(true);
    }

    public void unlockWallpaperFromDefaultPageOnNextLayout() {
        if (this.mWallpaperOffset.isLockedToDefaultPage()) {
            this.mUnlockWallpaperFromDefaultPageOnLayout = true;
            requestLayout();
        }
    }

    public void computeScroll() {
        super.computeScroll();
        this.mWallpaperOffset.syncWithScroll();
    }

    public void announceForAccessibility(CharSequence charSequence) {
        if (!this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            super.announceForAccessibility(charSequence);
        }
    }

    private void updatePageAlphaValues() {
        if (!workspaceInModalState() && !this.mIsSwitchingState && !this.mDragController.isDragging()) {
            int scrollX = getScrollX() + (getMeasuredWidth() / 2);
            for (int i = 0; i < getChildCount(); i++) {
                CellLayout cellLayout = (CellLayout) getChildAt(i);
                if (cellLayout != null) {
                    float abs = 1.0f - Math.abs(getScrollProgress(scrollX, cellLayout, i));
                    if (this.mWorkspaceFadeInAdjacentScreens) {
                        cellLayout.getShortcutsAndWidgets().setAlpha(abs);
                    } else {
                        cellLayout.getShortcutsAndWidgets().setImportantForAccessibility(abs > 0.0f ? 0 : 4);
                    }
                }
            }
        }
    }

    private void updatePageScrollValues() {
        int scrollX = getScrollX() + (getMeasuredWidth() / 2);
        for (int i = 0; i < getChildCount(); i++) {
            CellLayout cellLayout = (CellLayout) getChildAt(i);
            if (cellLayout != null) {
                cellLayout.setScrollProgress(getScrollProgress(scrollX, cellLayout, i));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mWallpaperOffset.setWindowToken(getWindowToken());
        computeScroll();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWallpaperOffset.setWindowToken((IBinder) null);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mUnlockWallpaperFromDefaultPageOnLayout) {
            this.mWallpaperOffset.setLockToDefaultPage(false);
            this.mUnlockWallpaperFromDefaultPageOnLayout = false;
        }
        if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < getChildCount()) {
            this.mWallpaperOffset.syncWithScroll();
            this.mWallpaperOffset.jumpToFinal();
        }
        super.onLayout(z, i, i2, i3, i4);
        updatePageAlphaValues();
    }

    public int getDescendantFocusability() {
        if (workspaceInModalState()) {
            return 393216;
        }
        return super.getDescendantFocusability();
    }

    private boolean workspaceInModalState() {
        return !this.mLauncher.isInState(LauncherState.NORMAL);
    }

    private boolean workspaceInScrollableState() {
        return this.mLauncher.isInState(LauncherState.SPRING_LOADED) || !workspaceInModalState();
    }

    public boolean workspaceIconsCanBeDragged() {
        return this.mLauncher.getStateManager().getState().hasFlag(LauncherState.FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED);
    }

    private void updateChildrenLayersEnabled() {
        boolean z = this.mIsSwitchingState || isPageInTransition();
        if (z != this.mChildrenLayersEnabled) {
            this.mChildrenLayersEnabled = z;
            if (z) {
                enableHwLayersOnVisiblePages();
                return;
            }
            for (int i = 0; i < getPageCount(); i++) {
                ((CellLayout) getChildAt(i)).enableHardwareLayer(false);
            }
        }
    }

    private void enableHwLayersOnVisiblePages() {
        if (this.mChildrenLayersEnabled) {
            int childCount = getChildCount();
            int[] visibleChildrenRange = getVisibleChildrenRange();
            int i = visibleChildrenRange[0];
            int i2 = visibleChildrenRange[1];
            if (this.mForceDrawAdjacentPages) {
                i = Utilities.boundToRange(getCurrentPage() - 1, 0, i2);
                i2 = Utilities.boundToRange(getCurrentPage() + 1, i, getPageCount() - 1);
            }
            if (i == i2) {
                if (i2 < childCount - 1) {
                    i2++;
                } else if (i > 0) {
                    i--;
                }
            }
            int i3 = 0;
            while (i3 < childCount) {
                ((CellLayout) getPageAt(i3)).enableHardwareLayer(i <= i3 && i3 <= i2);
                i3++;
            }
        }
    }

    public void onWallpaperTap(MotionEvent motionEvent) {
        int[] iArr = this.mTempXY;
        getLocationOnScreen(iArr);
        int actionIndex = motionEvent.getActionIndex();
        iArr[0] = iArr[0] + ((int) motionEvent.getX(actionIndex));
        iArr[1] = iArr[1] + ((int) motionEvent.getY(actionIndex));
        this.mWallpaperManager.sendWallpaperCommand(getWindowToken(), motionEvent.getAction() == 1 ? "android.wallpaper.tap" : "android.wallpaper.secondaryTap", iArr[0], iArr[1], 0, (Bundle) null);
    }

    /* access modifiers changed from: private */
    public void onStartStateTransition() {
        this.mIsSwitchingState = true;
        this.mTransitionProgress = 0.0f;
        updateChildrenLayersEnabled();
    }

    /* access modifiers changed from: private */
    public void onEndStateTransition() {
        this.mIsSwitchingState = false;
        this.mForceDrawAdjacentPages = false;
        this.mTransitionProgress = 1.0f;
        updateChildrenLayersEnabled();
        updateAccessibilityFlags();
    }

    public void setState(LauncherState launcherState) {
        onStartStateTransition();
        this.mStateTransitionAnimation.setState(launcherState);
        onEndStateTransition();
    }

    public void setStateWithAnimation(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        StateTransitionListener stateTransitionListener = new StateTransitionListener();
        this.mStateTransitionAnimation.setStateWithAnimation(launcherState, stateAnimationConfig, pendingAnimation);
        if (launcherState.hasFlag(LauncherState.FLAG_MULTI_PAGE)) {
            this.mForceDrawAdjacentPages = true;
        }
        invalidate();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(stateTransitionListener);
        ofFloat.addListener(stateTransitionListener);
        pendingAnimation.add(ofFloat);
    }

    public WorkspaceStateTransitionAnimation getStateTransitionAnimation() {
        return this.mStateTransitionAnimation;
    }

    public void updateAccessibilityFlags() {
        int i = this.mLauncher.getStateManager().getState().hasFlag(LauncherState.FLAG_WORKSPACE_INACCESSIBLE) ? 4 : 0;
        if (!this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag()) {
            int pageCount = getPageCount();
            for (int i2 = 0; i2 < pageCount; i2++) {
                updateAccessibilityFlags(i, (CellLayout) getPageAt(i2));
            }
            setImportantForAccessibility(i);
        }
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        if (getImportantForAccessibility() == 4) {
            return AccessibilityNodeInfo.obtain();
        }
        return super.createAccessibilityNodeInfo();
    }

    private void updateAccessibilityFlags(int i, CellLayout cellLayout) {
        cellLayout.setImportantForAccessibility(2);
        cellLayout.getShortcutsAndWidgets().setImportantForAccessibility(i);
        cellLayout.setContentDescription((CharSequence) null);
        cellLayout.setAccessibilityDelegate((View.AccessibilityDelegate) null);
    }

    public void startDrag(CellLayout.CellInfo cellInfo, DragOptions dragOptions) {
        View view = cellInfo.cell;
        this.mDragInfo = cellInfo;
        view.setVisibility(4);
        if (dragOptions.isAccessibleDrag) {
            this.mDragController.addDragListener(new AccessibleDragListenerAdapter(this, $$Lambda$e7bMoUNPH4pc8ZilNa9SChpFpw.INSTANCE) {
                /* access modifiers changed from: protected */
                public void enableAccessibleDrag(boolean z) {
                    super.enableAccessibleDrag(z);
                    setEnableForLayout(Workspace.this.mLauncher.getHotseat(), z);
                }
            });
        }
        beginDragShared(view, this, dragOptions);
    }

    public void beginDragShared(View view, DragSource dragSource, DragOptions dragOptions) {
        Object tag = view.getTag();
        if (tag instanceof ItemInfo) {
            beginDragShared(view, (DraggableView) null, dragSource, (ItemInfo) tag, new DragPreviewProvider(view), dragOptions);
            return;
        }
        throw new IllegalStateException("Drag started with a view that has no tag set. This will cause a crash (issue 11627249) down the line. View: " + view + "  tag: " + view.getTag());
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0034  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00ba  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0028  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.launcher3.dragndrop.DragView beginDragShared(android.view.View r16, com.android.launcher3.dragndrop.DraggableView r17, com.android.launcher3.DragSource r18, com.android.launcher3.model.data.ItemInfo r19, com.android.launcher3.graphics.DragPreviewProvider r20, com.android.launcher3.dragndrop.DragOptions r21) {
        /*
            r15 = this;
            r0 = r15
            r1 = r16
            r2 = r20
            r12 = r21
            boolean r3 = r1 instanceof com.android.launcher3.BubbleTextView
            if (r3 == 0) goto L_0x001d
            r4 = r1
            com.android.launcher3.BubbleTextView r4 = (com.android.launcher3.BubbleTextView) r4
            com.android.launcher3.icons.FastBitmapDrawable r4 = r4.getIcon()
            boolean r5 = r4 instanceof com.android.launcher3.icons.FastBitmapDrawable
            if (r5 == 0) goto L_0x001d
            com.android.launcher3.icons.FastBitmapDrawable r4 = (com.android.launcher3.icons.FastBitmapDrawable) r4
            float r4 = r4.getAnimatedScale()
            goto L_0x001f
        L_0x001d:
            r4 = 1065353216(0x3f800000, float:1.0)
        L_0x001f:
            r16.clearFocus()
            r5 = 0
            r1.setPressed(r5)
            if (r3 == 0) goto L_0x002e
            r6 = r1
            com.android.launcher3.BubbleTextView r6 = (com.android.launcher3.BubbleTextView) r6
            r6.clearPressedBackground()
        L_0x002e:
            if (r17 != 0) goto L_0x0038
            boolean r6 = r1 instanceof com.android.launcher3.dragndrop.DraggableView
            if (r6 == 0) goto L_0x0038
            r6 = r1
            com.android.launcher3.dragndrop.DraggableView r6 = (com.android.launcher3.dragndrop.DraggableView) r6
            goto L_0x003a
        L_0x0038:
            r6 = r17
        L_0x003a:
            android.view.View r7 = r20.getContentView()
            r8 = 0
            if (r7 != 0) goto L_0x004d
            android.graphics.drawable.Drawable r9 = r20.createDrawable()
            int[] r10 = r0.mTempXY
            float r10 = r2.getScaleAndPosition((android.graphics.drawable.Drawable) r9, (int[]) r10)
            r11 = r10
            goto L_0x0055
        L_0x004d:
            int[] r9 = r0.mTempXY
            float r9 = r2.getScaleAndPosition((android.view.View) r7, (int[]) r9)
            r11 = r9
            r9 = r8
        L_0x0055:
            int r2 = r2.previewPadding
            int r2 = r2 / 2
            int[] r10 = r0.mTempXY
            r5 = r10[r5]
            r13 = 1
            r10 = r10[r13]
            android.graphics.Rect r13 = new android.graphics.Rect
            r13.<init>()
            if (r6 == 0) goto L_0x0073
            r6.getSourceVisualDragBounds(r13)
            int r8 = r13.top
            int r10 = r10 + r8
            android.graphics.Point r8 = new android.graphics.Point
            int r14 = -r2
            r8.<init>(r14, r2)
        L_0x0073:
            android.view.ViewParent r2 = r16.getParent()
            boolean r2 = r2 instanceof com.android.launcher3.ShortcutAndWidgetContainer
            if (r2 == 0) goto L_0x0083
            android.view.ViewParent r2 = r16.getParent()
            com.android.launcher3.ShortcutAndWidgetContainer r2 = (com.android.launcher3.ShortcutAndWidgetContainer) r2
            r0.mDragSourceInternal = r2
        L_0x0083:
            if (r3 == 0) goto L_0x0091
            boolean r2 = r12.isAccessibleDrag
            if (r2 != 0) goto L_0x0091
            com.android.launcher3.BubbleTextView r1 = (com.android.launcher3.BubbleTextView) r1
            com.android.launcher3.dragndrop.DragOptions$PreDragCondition r1 = r1.startLongPressAction()
            r12.preDragCondition = r1
        L_0x0091:
            boolean r1 = r7 instanceof android.view.View
            if (r1 == 0) goto L_0x00ba
            boolean r1 = r7 instanceof com.android.launcher3.widget.LauncherAppWidgetHostView
            if (r1 == 0) goto L_0x00a5
            com.android.launcher3.dragndrop.DragController r1 = r0.mDragController
            com.android.launcher3.widget.dragndrop.AppWidgetHostViewDragListener r2 = new com.android.launcher3.widget.dragndrop.AppWidgetHostViewDragListener
            com.android.launcher3.Launcher r3 = r0.mLauncher
            r2.<init>(r3)
            r1.addDragListener(r2)
        L_0x00a5:
            com.android.launcher3.dragndrop.DragController r1 = r0.mDragController
            float r14 = r11 * r4
            r2 = r7
            r3 = r6
            r4 = r5
            r5 = r10
            r6 = r18
            r7 = r19
            r9 = r13
            r10 = r14
            r12 = r21
            com.android.launcher3.dragndrop.DragView r1 = r1.startDrag((android.view.View) r2, (com.android.launcher3.dragndrop.DraggableView) r3, (int) r4, (int) r5, (com.android.launcher3.DragSource) r6, (com.android.launcher3.model.data.ItemInfo) r7, (android.graphics.Point) r8, (android.graphics.Rect) r9, (float) r10, (float) r11, (com.android.launcher3.dragndrop.DragOptions) r12)
            goto L_0x00ce
        L_0x00ba:
            com.android.launcher3.dragndrop.DragController r1 = r0.mDragController
            float r14 = r11 * r4
            r2 = r9
            r3 = r6
            r4 = r5
            r5 = r10
            r6 = r18
            r7 = r19
            r9 = r13
            r10 = r14
            r12 = r21
            com.android.launcher3.dragndrop.DragView r1 = r1.startDrag((android.graphics.drawable.Drawable) r2, (com.android.launcher3.dragndrop.DraggableView) r3, (int) r4, (int) r5, (com.android.launcher3.DragSource) r6, (com.android.launcher3.model.data.ItemInfo) r7, (android.graphics.Point) r8, (android.graphics.Rect) r9, (float) r10, (float) r11, (com.android.launcher3.dragndrop.DragOptions) r12)
        L_0x00ce:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.beginDragShared(android.view.View, com.android.launcher3.dragndrop.DraggableView, com.android.launcher3.DragSource, com.android.launcher3.model.data.ItemInfo, com.android.launcher3.graphics.DragPreviewProvider, com.android.launcher3.dragndrop.DragOptions):com.android.launcher3.dragndrop.DragView");
    }

    private boolean transitionStateShouldAllowDrop() {
        return (!isSwitchingState() || this.mTransitionProgress > ALLOW_DROP_TRANSITION_PROGRESS) && workspaceIconsCanBeDragged();
    }

    public boolean acceptDrop(DropTarget.DragObject dragObject) {
        CellLayout cellLayout;
        int i;
        int i2;
        int i3;
        int i4;
        DropTarget.DragObject dragObject2 = dragObject;
        CellLayout cellLayout2 = this.mDropToLayout;
        if (dragObject2.dragSource == this) {
            cellLayout = cellLayout2;
        } else if (cellLayout2 == null || !transitionStateShouldAllowDrop()) {
            return false;
        } else {
            float[] visualCenter = dragObject2.getVisualCenter(this.mDragViewVisualCenter);
            this.mDragViewVisualCenter = visualCenter;
            mapPointFromDropLayout(cellLayout2, visualCenter);
            CellLayout.CellInfo cellInfo = this.mDragInfo;
            if (cellInfo != null) {
                int i5 = cellInfo.spanX;
                i = cellInfo.spanY;
                i2 = i5;
            } else {
                i2 = dragObject2.dragInfo.spanX;
                i = dragObject2.dragInfo.spanY;
            }
            if (dragObject2.dragInfo instanceof PendingAddWidgetInfo) {
                i4 = ((PendingAddWidgetInfo) dragObject2.dragInfo).minSpanX;
                i3 = ((PendingAddWidgetInfo) dragObject2.dragInfo).minSpanY;
            } else {
                i4 = i2;
                i3 = i;
            }
            float[] fArr = this.mDragViewVisualCenter;
            int[] findNearestArea = findNearestArea((int) fArr[0], (int) fArr[1], i4, i3, cellLayout2, this.mTargetCell);
            this.mTargetCell = findNearestArea;
            float[] fArr2 = this.mDragViewVisualCenter;
            float distanceFromWorkspaceCellVisualCenter = cellLayout2.getDistanceFromWorkspaceCellVisualCenter(fArr2[0], fArr2[1], findNearestArea);
            if (this.mCreateUserFolderOnDrop) {
                if (willCreateUserFolder(dragObject2.dragInfo, cellLayout2, this.mTargetCell, distanceFromWorkspaceCellVisualCenter, true)) {
                    return true;
                }
            }
            if (this.mAddToExistingFolderOnDrop && willAddToExistingUserFolder(dragObject2.dragInfo, cellLayout2, this.mTargetCell, distanceFromWorkspaceCellVisualCenter)) {
                return true;
            }
            float[] fArr3 = this.mDragViewVisualCenter;
            cellLayout = cellLayout2;
            int[] performReorder = cellLayout2.performReorder((int) fArr3[0], (int) fArr3[1], i4, i3, i2, i, (View) null, this.mTargetCell, new int[2], 4);
            this.mTargetCell = performReorder;
            if (!(performReorder[0] >= 0 && performReorder[1] >= 0)) {
                onNoCellFound(cellLayout, dragObject2.dragInfo, dragObject2.logInstanceId);
                return false;
            }
        }
        if (EXTRA_EMPTY_SCREEN_IDS.contains(getIdForScreen(cellLayout))) {
            commitExtraEmptyScreens();
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean willCreateUserFolder(ItemInfo itemInfo, CellLayout cellLayout, int[] iArr, float f, boolean z) {
        if (f > cellLayout.getFolderCreationRadius(iArr)) {
            return false;
        }
        return willCreateUserFolder(itemInfo, cellLayout.getChildAt(iArr[0], iArr[1]), z);
    }

    /* access modifiers changed from: package-private */
    public boolean willCreateUserFolder(ItemInfo itemInfo, View view, boolean z) {
        if (view != null) {
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
            if (layoutParams.useTmpCoords && !(layoutParams.tmpCellX == layoutParams.cellX && layoutParams.tmpCellY == layoutParams.cellY)) {
                return false;
            }
        }
        CellLayout.CellInfo cellInfo = this.mDragInfo;
        boolean z2 = cellInfo != null && view == cellInfo.cell;
        if (view == null || z2) {
            return false;
        }
        if (z && !this.mCreateUserFolderOnDrop) {
            return false;
        }
        boolean z3 = (view.getTag() instanceof WorkspaceItemInfo) && ((WorkspaceItemInfo) view.getTag()).container != -103;
        boolean z4 = itemInfo.itemType == 0 || itemInfo.itemType == 1 || itemInfo.itemType == 6;
        if (!z3 || !z4) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean willAddToExistingUserFolder(ItemInfo itemInfo, CellLayout cellLayout, int[] iArr, float f) {
        if (f > cellLayout.getFolderCreationRadius(iArr)) {
            return false;
        }
        return willAddToExistingUserFolder(itemInfo, cellLayout.getChildAt(iArr[0], iArr[1]));
    }

    /* access modifiers changed from: package-private */
    public boolean willAddToExistingUserFolder(ItemInfo itemInfo, View view) {
        if (view != null) {
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
            if (layoutParams.useTmpCoords && !(layoutParams.tmpCellX == layoutParams.cellX && layoutParams.tmpCellY == layoutParams.cellY)) {
                return false;
            }
        }
        if (!(view instanceof FolderIcon) || !((FolderIcon) view).acceptDrop(itemInfo)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00d0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean createUserFolderIfNecessary(android.view.View r16, int r17, com.android.launcher3.CellLayout r18, int[] r19, float r20, boolean r21, com.android.launcher3.DropTarget.DragObject r22) {
        /*
            r15 = this;
            r0 = r15
            r2 = r18
            r7 = r22
            float r1 = r18.getFolderCreationRadius(r19)
            int r1 = (r20 > r1 ? 1 : (r20 == r1 ? 0 : -1))
            r8 = 0
            if (r1 <= 0) goto L_0x000f
            return r8
        L_0x000f:
            r1 = r19[r8]
            r9 = 1
            r3 = r19[r9]
            android.view.View r10 = r2.getChildAt(r1, r3)
            com.android.launcher3.CellLayout$CellInfo r1 = r0.mDragInfo
            if (r1 == 0) goto L_0x0036
            android.view.View r1 = r1.cell
            com.android.launcher3.CellLayout r1 = r15.getParentCellLayoutForView(r1)
            com.android.launcher3.CellLayout$CellInfo r3 = r0.mDragInfo
            int r3 = r3.cellX
            r4 = r19[r8]
            if (r3 != r4) goto L_0x0036
            com.android.launcher3.CellLayout$CellInfo r3 = r0.mDragInfo
            int r3 = r3.cellY
            r4 = r19[r9]
            if (r3 != r4) goto L_0x0036
            if (r1 != r2) goto L_0x0036
            r1 = r9
            goto L_0x0037
        L_0x0036:
            r1 = r8
        L_0x0037:
            if (r10 == 0) goto L_0x00da
            if (r1 != 0) goto L_0x00da
            boolean r1 = r0.mCreateUserFolderOnDrop
            if (r1 != 0) goto L_0x0041
            goto L_0x00da
        L_0x0041:
            r0.mCreateUserFolderOnDrop = r8
            int r4 = r15.getIdForScreen(r2)
            java.lang.Object r1 = r10.getTag()
            boolean r1 = r1 instanceof com.android.launcher3.model.data.WorkspaceItemInfo
            java.lang.Object r3 = r16.getTag()
            boolean r3 = r3 instanceof com.android.launcher3.model.data.WorkspaceItemInfo
            if (r1 == 0) goto L_0x00da
            if (r3 == 0) goto L_0x00da
            java.lang.Object r1 = r16.getTag()
            r11 = r1
            com.android.launcher3.model.data.WorkspaceItemInfo r11 = (com.android.launcher3.model.data.WorkspaceItemInfo) r11
            java.lang.Object r1 = r10.getTag()
            r12 = r1
            com.android.launcher3.model.data.WorkspaceItemInfo r12 = (com.android.launcher3.model.data.WorkspaceItemInfo) r12
            if (r21 != 0) goto L_0x0076
            com.android.launcher3.CellLayout$CellInfo r1 = r0.mDragInfo
            android.view.View r1 = r1.cell
            com.android.launcher3.CellLayout r1 = r15.getParentCellLayoutForView(r1)
            com.android.launcher3.CellLayout$CellInfo r3 = r0.mDragInfo
            android.view.View r3 = r3.cell
            r1.removeView(r3)
        L_0x0076:
            android.graphics.Rect r13 = new android.graphics.Rect
            r13.<init>()
            com.android.launcher3.Launcher r1 = r0.mLauncher
            com.android.launcher3.dragndrop.DragLayer r1 = r1.getDragLayer()
            float r14 = r1.getDescendantRectRelativeToSelf(r10, r13)
            r2.removeView(r10)
            com.android.launcher3.logging.StatsLogManager r1 = r0.mStatsLogManager
            com.android.launcher3.logging.StatsLogManager$StatsLogger r1 = r1.logger()
            com.android.launcher3.logging.StatsLogManager$StatsLogger r1 = r1.withItemInfo(r12)
            com.android.launcher3.logging.InstanceId r3 = r7.logInstanceId
            com.android.launcher3.logging.StatsLogManager$StatsLogger r1 = r1.withInstanceId(r3)
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r3 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROP_FOLDER_CREATED
            r1.log(r3)
            com.android.launcher3.Launcher r1 = r0.mLauncher
            r5 = r19[r8]
            r6 = r19[r9]
            r2 = r18
            r3 = r17
            com.android.launcher3.folder.FolderIcon r1 = r1.addFolder(r2, r3, r4, r5, r6)
            r2 = -1
            r12.cellX = r2
            r12.cellY = r2
            r11.cellX = r2
            r11.cellY = r2
            if (r7 == 0) goto L_0x00b7
            r8 = r9
        L_0x00b7:
            if (r8 == 0) goto L_0x00d0
            com.android.launcher3.folder.PreviewBackground r2 = r0.mFolderCreateBg
            r1.setFolderBackground(r2)
            com.android.launcher3.folder.PreviewBackground r2 = new com.android.launcher3.folder.PreviewBackground
            r2.<init>()
            r0.mFolderCreateBg = r2
            r2 = r12
            r3 = r10
            r4 = r11
            r5 = r22
            r6 = r13
            r7 = r14
            r1.performCreateAnimation(r2, r3, r4, r5, r6, r7)
            goto L_0x00d9
        L_0x00d0:
            r1.prepareCreateAnimation(r10)
            r1.addItem(r12)
            r1.addItem(r11)
        L_0x00d9:
            return r9
        L_0x00da:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.createUserFolderIfNecessary(android.view.View, int, com.android.launcher3.CellLayout, int[], float, boolean, com.android.launcher3.DropTarget$DragObject):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean addToExistingFolderIfNecessary(View view, CellLayout cellLayout, int[] iArr, float f, DropTarget.DragObject dragObject, boolean z) {
        if (f > cellLayout.getFolderCreationRadius(iArr)) {
            return false;
        }
        View childAt = cellLayout.getChildAt(iArr[0], iArr[1]);
        if (!this.mAddToExistingFolderOnDrop) {
            return false;
        }
        this.mAddToExistingFolderOnDrop = false;
        if (childAt instanceof FolderIcon) {
            FolderIcon folderIcon = (FolderIcon) childAt;
            if (folderIcon.acceptDrop(dragObject.dragInfo)) {
                this.mStatsLogManager.logger().withItemInfo(folderIcon.mInfo).withInstanceId(dragObject.logInstanceId).log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROP_COMPLETED_ON_FOLDER_ICON);
                folderIcon.onDrop(dragObject, false);
                if (!z) {
                    getParentCellLayoutForView(this.mDragInfo.cell).removeView(this.mDragInfo.cell);
                }
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: type inference failed for: r6v5, types: [boolean] */
    /* JADX WARNING: type inference failed for: r3v10 */
    /* JADX WARNING: type inference failed for: r13v6 */
    /* JADX WARNING: type inference failed for: r13v8 */
    /* JADX WARNING: type inference failed for: r13v9 */
    /* JADX WARNING: type inference failed for: r13v11 */
    /* JADX WARNING: type inference failed for: r6v7 */
    /* JADX WARNING: type inference failed for: r3v25 */
    /* JADX WARNING: type inference failed for: r3v26 */
    /* JADX WARNING: type inference failed for: r6v8 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0264  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0169  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0199  */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDrop(com.android.launcher3.DropTarget.DragObject r38, com.android.launcher3.dragndrop.DragOptions r39) {
        /*
            r37 = this;
            r8 = r37
            r9 = r38
            float[] r0 = r8.mDragViewVisualCenter
            float[] r0 = r9.getVisualCenter(r0)
            r8.mDragViewVisualCenter = r0
            com.android.launcher3.CellLayout r15 = r8.mDropToLayout
            if (r15 == 0) goto L_0x0013
            r8.mapPointFromDropLayout(r15, r0)
        L_0x0013:
            com.android.launcher3.DragSource r0 = r9.dragSource
            r13 = 1
            r12 = 0
            if (r0 != r8) goto L_0x039e
            com.android.launcher3.CellLayout$CellInfo r0 = r8.mDragInfo
            if (r0 != 0) goto L_0x001f
            goto L_0x039e
        L_0x001f:
            android.view.View r11 = r0.cell
            r21 = -1
            r22 = 0
            if (r15 == 0) goto L_0x02b6
            boolean r0 = r9.cancelled
            if (r0 != 0) goto L_0x02b6
            com.android.launcher3.CellLayout r0 = r8.getParentCellLayoutForView(r11)
            if (r0 == r15) goto L_0x0034
            r23 = r13
            goto L_0x0036
        L_0x0034:
            r23 = r12
        L_0x0036:
            com.android.launcher3.Launcher r0 = r8.mLauncher
            boolean r24 = r0.isHotseatLayout(r15)
            if (r24 == 0) goto L_0x0041
            r5 = -101(0xffffffffffffff9b, float:NaN)
            goto L_0x0044
        L_0x0041:
            r0 = -100
            r5 = r0
        L_0x0044:
            int[] r0 = r8.mTargetCell
            r0 = r0[r12]
            if (r0 >= 0) goto L_0x004f
            com.android.launcher3.CellLayout$CellInfo r0 = r8.mDragInfo
            int r0 = r0.screenId
            goto L_0x0053
        L_0x004f:
            int r0 = r8.getIdForScreen(r15)
        L_0x0053:
            r4 = r0
            com.android.launcher3.CellLayout$CellInfo r0 = r8.mDragInfo
            if (r0 == 0) goto L_0x005c
            int r0 = r0.spanX
            r3 = r0
            goto L_0x005d
        L_0x005c:
            r3 = r13
        L_0x005d:
            com.android.launcher3.CellLayout$CellInfo r0 = r8.mDragInfo
            if (r0 == 0) goto L_0x0065
            int r0 = r0.spanY
            r2 = r0
            goto L_0x0066
        L_0x0065:
            r2 = r13
        L_0x0066:
            float[] r0 = r8.mDragViewVisualCenter
            r1 = r0[r12]
            int r1 = (int) r1
            r0 = r0[r13]
            int r0 = (int) r0
            int[] r6 = r8.mTargetCell
            r7 = r0
            r0 = r37
            r18 = r2
            r2 = r7
            r7 = r3
            r14 = r4
            r4 = r18
            r27 = r5
            r5 = r15
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r8.mTargetCell = r0
            float[] r1 = r8.mDragViewVisualCenter
            r2 = r1[r12]
            r1 = r1[r13]
            float r16 = r15.getDistanceFromWorkspaceCellVisualCenter(r2, r1, r0)
            int[] r4 = r8.mTargetCell
            r6 = 0
            r0 = r37
            r1 = r11
            r2 = r27
            r3 = r15
            r5 = r16
            r33 = r7
            r7 = r38
            boolean r0 = r0.createUserFolderIfNecessary(r1, r2, r3, r4, r5, r6, r7)
            if (r0 != 0) goto L_0x02a8
            int[] r3 = r8.mTargetCell
            r6 = 0
            r0 = r37
            r1 = r11
            r2 = r15
            r4 = r16
            r5 = r38
            boolean r0 = r0.addToExistingFolderIfNecessary(r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L_0x00b5
            goto L_0x02a8
        L_0x00b5:
            com.android.launcher3.model.data.ItemInfo r7 = r9.dragInfo
            int r0 = r7.spanX
            int r1 = r7.spanY
            int r2 = r7.minSpanX
            if (r2 <= 0) goto L_0x00c7
            int r2 = r7.minSpanY
            if (r2 <= 0) goto L_0x00c7
            int r0 = r7.minSpanX
            int r1 = r7.minSpanY
        L_0x00c7:
            int r2 = r7.screenId
            if (r2 != r14) goto L_0x00e4
            int r2 = r7.container
            r6 = r27
            if (r2 != r6) goto L_0x00e6
            int r2 = r7.cellX
            int[] r3 = r8.mTargetCell
            r3 = r3[r12]
            if (r2 != r3) goto L_0x00e6
            int r2 = r7.cellY
            int[] r3 = r8.mTargetCell
            r3 = r3[r13]
            if (r2 != r3) goto L_0x00e6
            r34 = r13
            goto L_0x00e8
        L_0x00e4:
            r6 = r27
        L_0x00e6:
            r34 = r12
        L_0x00e8:
            if (r34 == 0) goto L_0x00f1
            boolean r2 = r8.mIsSwitchingState
            if (r2 == 0) goto L_0x00f1
            r35 = r13
            goto L_0x00f3
        L_0x00f1:
            r35 = r12
        L_0x00f3:
            boolean r2 = r37.isFinishedSwitchingState()
            if (r2 != 0) goto L_0x010d
            if (r35 != 0) goto L_0x010d
            int[] r2 = r8.mTargetCell
            r3 = r2[r12]
            r2 = r2[r13]
            r5 = r18
            r4 = r33
            boolean r2 = r15.isRegionVacant(r3, r2, r4, r5)
            if (r2 != 0) goto L_0x0111
            r2 = r13
            goto L_0x0112
        L_0x010d:
            r5 = r18
            r4 = r33
        L_0x0111:
            r2 = r12
        L_0x0112:
            r3 = 2
            int[] r12 = new int[r3]
            if (r2 == 0) goto L_0x012d
            int[] r0 = r8.mTargetCell
            r0[r13] = r21
            r16 = 0
            r0[r16] = r21
            r25 = r2
            r27 = r6
            r33 = r11
            r26 = r12
            r3 = r13
            r0 = r14
            r1 = r15
            r6 = r16
            goto L_0x015f
        L_0x012d:
            r16 = 0
            float[] r3 = r8.mDragViewVisualCenter
            r10 = r3[r16]
            int r10 = (int) r10
            r3 = r3[r13]
            int r3 = (int) r3
            r25 = r2
            int[] r2 = r8.mTargetCell
            r20 = 2
            r17 = r10
            r10 = r15
            r33 = r11
            r11 = r17
            r27 = r6
            r26 = r12
            r6 = r16
            r12 = r3
            r3 = r13
            r13 = r0
            r0 = r14
            r14 = r1
            r1 = r15
            r15 = r4
            r16 = r5
            r17 = r33
            r18 = r2
            r19 = r26
            int[] r2 = r10.performReorder(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            r8.mTargetCell = r2
        L_0x015f:
            int[] r2 = r8.mTargetCell
            r4 = r2[r6]
            if (r4 < 0) goto L_0x016b
            r2 = r2[r3]
            if (r2 < 0) goto L_0x016b
            r13 = r3
            goto L_0x016c
        L_0x016b:
            r13 = r6
        L_0x016c:
            r10 = r33
            if (r13 == 0) goto L_0x0196
            boolean r2 = r10 instanceof android.appwidget.AppWidgetHostView
            if (r2 == 0) goto L_0x0196
            r2 = r26[r6]
            int r4 = r7.spanX
            if (r2 != r4) goto L_0x0180
            r2 = r26[r3]
            int r4 = r7.spanY
            if (r2 == r4) goto L_0x0196
        L_0x0180:
            r2 = r26[r6]
            r7.spanX = r2
            r2 = r26[r3]
            r7.spanY = r2
            r11 = r10
            android.appwidget.AppWidgetHostView r11 = (android.appwidget.AppWidgetHostView) r11
            com.android.launcher3.Launcher r2 = r8.mLauncher
            r4 = r26[r6]
            r5 = r26[r3]
            com.android.launcher3.widget.util.WidgetSizes.updateWidgetSizeRanges(r11, r2, r4, r5)
            r11 = r3
            goto L_0x0197
        L_0x0196:
            r11 = r6
        L_0x0197:
            if (r13 == 0) goto L_0x0264
            int r2 = r8.getPageIndexForScreenId(r0)
            int r2 = r8.getLeftmostVisiblePageForIndex(r2)
            int r4 = r8.mCurrentPage
            if (r2 == r4) goto L_0x01ac
            if (r24 != 0) goto L_0x01ac
            r8.snapToPage(r2)
            r13 = r3
            goto L_0x01ad
        L_0x01ac:
            r13 = r6
        L_0x01ad:
            java.lang.Object r2 = r10.getTag()
            r12 = r2
            com.android.launcher3.model.data.ItemInfo r12 = (com.android.launcher3.model.data.ItemInfo) r12
            if (r23 == 0) goto L_0x01f1
            com.android.launcher3.CellLayout r2 = r8.getParentCellLayoutForView(r10)
            if (r2 == 0) goto L_0x01c0
            r2.removeView(r10)
            goto L_0x01cd
        L_0x01c0:
            com.android.launcher3.CellLayout$CellInfo r2 = r8.mDragInfo
            android.view.View r2 = r2.cell
            boolean r2 = r2 instanceof com.android.launcher3.widget.LauncherAppWidgetHostView
            if (r2 == 0) goto L_0x01cd
            com.android.launcher3.dragndrop.DragView r2 = r9.dragView
            r2.detachContentView(r6)
        L_0x01cd:
            int[] r2 = r8.mTargetCell
            r4 = r2[r6]
            r5 = r2[r3]
            int r14 = r12.spanX
            int r15 = r12.spanY
            r16 = r0
            r0 = r37
            r2 = r1
            r1 = r10
            r17 = r11
            r11 = r2
            r2 = r27
            r18 = r13
            r13 = r3
            r3 = r16
            r13 = r6
            r36 = r27
            r6 = r14
            r14 = r7
            r7 = r15
            r0.addInScreen(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x01fc
        L_0x01f1:
            r16 = r0
            r14 = r7
            r17 = r11
            r18 = r13
            r36 = r27
            r11 = r1
            r13 = r6
        L_0x01fc:
            android.view.ViewGroup$LayoutParams r0 = r10.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r0 = (com.android.launcher3.CellLayout.LayoutParams) r0
            int[] r1 = r8.mTargetCell
            r1 = r1[r13]
            r0.tmpCellX = r1
            r0.cellX = r1
            int[] r1 = r8.mTargetCell
            r2 = 1
            r1 = r1[r2]
            r0.tmpCellY = r1
            r0.cellY = r1
            int r1 = r14.spanX
            r0.cellHSpan = r1
            int r1 = r14.spanY
            r0.cellVSpan = r1
            r0.isLockedToGrid = r2
            r1 = r36
            r2 = -101(0xffffffffffffff9b, float:NaN)
            if (r1 == r2) goto L_0x0240
            boolean r2 = r10 instanceof com.android.launcher3.widget.LauncherAppWidgetHostView
            if (r2 == 0) goto L_0x0240
            r2 = r10
            com.android.launcher3.widget.LauncherAppWidgetHostView r2 = (com.android.launcher3.widget.LauncherAppWidgetHostView) r2
            android.appwidget.AppWidgetProviderInfo r3 = r2.getAppWidgetInfo()
            if (r3 == 0) goto L_0x0240
            int r3 = r3.resizeMode
            if (r3 == 0) goto L_0x0240
            r3 = r39
            boolean r3 = r3.isAccessibleDrag
            if (r3 != 0) goto L_0x0240
            com.android.launcher3.-$$Lambda$Workspace$yiZDdavOhEtT6eOODtX0YWlO950 r3 = new com.android.launcher3.-$$Lambda$Workspace$yiZDdavOhEtT6eOODtX0YWlO950
            r3.<init>(r2, r11)
            goto L_0x0242
        L_0x0240:
            r3 = r22
        L_0x0242:
            com.android.launcher3.Launcher r2 = r8.mLauncher
            com.android.launcher3.model.ModelWriter r25 = r2.getModelWriter()
            int r2 = r0.cellX
            int r0 = r0.cellY
            int r4 = r14.spanX
            int r5 = r14.spanY
            r26 = r12
            r27 = r1
            r28 = r16
            r29 = r2
            r30 = r0
            r31 = r4
            r32 = r5
            r25.modifyItemInDatabase(r26, r27, r28, r29, r30, r31, r32)
            r12 = r18
            goto L_0x02a3
        L_0x0264:
            r13 = r6
            r17 = r11
            r11 = r1
            if (r25 != 0) goto L_0x0271
            com.android.launcher3.model.data.ItemInfo r0 = r9.dragInfo
            com.android.launcher3.logging.InstanceId r1 = r9.logInstanceId
            r8.onNoCellFound(r11, r0, r1)
        L_0x0271:
            com.android.launcher3.CellLayout$CellInfo r0 = r8.mDragInfo
            android.view.View r0 = r0.cell
            boolean r0 = r0 instanceof com.android.launcher3.widget.LauncherAppWidgetHostView
            if (r0 == 0) goto L_0x0280
            com.android.launcher3.dragndrop.DragView r0 = r9.dragView
            r1 = 1
            r0.detachContentView(r1)
            goto L_0x0281
        L_0x0280:
            r1 = 1
        L_0x0281:
            android.view.ViewGroup$LayoutParams r0 = r10.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r0 = (com.android.launcher3.CellLayout.LayoutParams) r0
            int[] r2 = r8.mTargetCell
            int r3 = r0.cellX
            r2[r13] = r3
            int[] r2 = r8.mTargetCell
            int r0 = r0.cellY
            r2[r1] = r0
            android.view.ViewParent r0 = r10.getParent()
            android.view.ViewParent r0 = r0.getParent()
            com.android.launcher3.CellLayout r0 = (com.android.launcher3.CellLayout) r0
            r0.markCellsAsOccupiedForView(r10)
            r12 = r13
            r3 = r22
        L_0x02a3:
            r14 = r3
            r0 = r12
            r11 = 500(0x1f4, double:2.47E-321)
            goto L_0x02d1
        L_0x02a8:
            com.android.launcher3.Launcher r0 = r8.mLauncher
            com.android.launcher3.statemanager.StateManager r0 = r0.getStateManager()
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.NORMAL
            r11 = 500(0x1f4, double:2.47E-321)
            r0.goToState(r1, (long) r11)
            return
        L_0x02b6:
            r10 = r11
            r13 = r12
            r11 = 500(0x1f4, double:2.47E-321)
            com.android.launcher3.CellLayout$CellInfo r0 = r8.mDragInfo
            android.view.View r0 = r0.cell
            boolean r0 = r0 instanceof com.android.launcher3.widget.LauncherAppWidgetHostView
            if (r0 == 0) goto L_0x02c8
            com.android.launcher3.dragndrop.DragView r0 = r9.dragView
            r1 = 1
            r0.detachContentView(r1)
        L_0x02c8:
            r0 = r13
            r17 = r0
            r34 = r17
            r35 = r34
            r14 = r22
        L_0x02d1:
            android.view.ViewParent r1 = r10.getParent()
            android.view.ViewParent r1 = r1.getParent()
            r15 = r1
            com.android.launcher3.CellLayout r15 = (com.android.launcher3.CellLayout) r15
            com.android.launcher3.dragndrop.DragView r1 = r9.dragView
            boolean r1 = r1.hasDrawn()
            if (r1 == 0) goto L_0x0368
            if (r35 == 0) goto L_0x032d
            com.android.launcher3.util.RunnableList r0 = new com.android.launcher3.util.RunnableList
            r0.<init>()
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.dragndrop.DragController r1 = r1.getDragController()
            java.util.Objects.requireNonNull(r0)
            com.android.launcher3.-$$Lambda$hQD1JeGZDm5kxmTgspSnjaDiUIY r2 = new com.android.launcher3.-$$Lambda$hQD1JeGZDm5kxmTgspSnjaDiUIY
            r2.<init>()
            com.android.launcher3.LauncherState r3 = com.android.launcher3.LauncherState.SPRING_LOADED
            com.android.launcher3.Launcher r4 = r8.mLauncher
            r5 = 1
            int r3 = r3.getTransitionDuration(r4, r5)
            r1.animateDragViewToOriginalPosition(r2, r10, r3)
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.statemanager.StateManager r1 = r1.getStateManager()
            com.android.launcher3.LauncherState r2 = com.android.launcher3.LauncherState.NORMAL
            r3 = 0
            if (r14 != 0) goto L_0x0312
            goto L_0x031b
        L_0x0312:
            com.android.launcher3.-$$Lambda$Workspace$lhMTDgEDOFXA7WVyyQesT1tumxw r5 = new com.android.launcher3.-$$Lambda$Workspace$lhMTDgEDOFXA7WVyyQesT1tumxw
            r5.<init>(r14)
            android.animation.Animator$AnimatorListener r22 = com.android.launcher3.anim.AnimatorListeners.forSuccessCallback(r5)
        L_0x031b:
            r0 = r22
            r1.goToState(r2, (long) r3, (android.animation.Animator.AnimatorListener) r0)
            com.android.launcher3.Launcher r0 = r8.mLauncher
            com.android.launcher3.DropTargetBar r0 = r0.getDropTargetBar()
            r0.onDragEnd()
            r15.onDropChild(r10)
            return
        L_0x032d:
            java.lang.Object r1 = r10.getTag()
            com.android.launcher3.model.data.ItemInfo r1 = (com.android.launcher3.model.data.ItemInfo) r1
            int r2 = r1.itemType
            r3 = 4
            if (r2 == r3) goto L_0x0341
            int r2 = r1.itemType
            r3 = 5
            if (r2 != r3) goto L_0x033e
            goto L_0x0341
        L_0x033e:
            r19 = r13
            goto L_0x0343
        L_0x0341:
            r19 = 1
        L_0x0343:
            if (r19 == 0) goto L_0x0356
            if (r17 == 0) goto L_0x0349
            r5 = 2
            goto L_0x034a
        L_0x0349:
            r5 = r13
        L_0x034a:
            com.android.launcher3.dragndrop.DragView r3 = r9.dragView
            r4 = 0
            r7 = 0
            r0 = r37
            r2 = r15
            r6 = r10
            r0.animateWidgetDrop(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x036d
        L_0x0356:
            if (r0 == 0) goto L_0x035a
            r21 = 300(0x12c, float:4.2E-43)
        L_0x035a:
            r0 = r21
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.dragndrop.DragLayer r1 = r1.getDragLayer()
            com.android.launcher3.dragndrop.DragView r2 = r9.dragView
            r1.animateViewIntoPosition(r2, r10, r0, r8)
            goto L_0x036d
        L_0x0368:
            r9.deferDragViewCleanupPostAnimation = r13
            r10.setVisibility(r13)
        L_0x036d:
            r15.onDropChild(r10)
            com.android.launcher3.Launcher r0 = r8.mLauncher
            com.android.launcher3.statemanager.StateManager r0 = r0.getStateManager()
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.NORMAL
            if (r14 != 0) goto L_0x037b
            goto L_0x037f
        L_0x037b:
            android.animation.Animator$AnimatorListener r22 = com.android.launcher3.anim.AnimatorListeners.forSuccessCallback(r14)
        L_0x037f:
            r2 = r22
            r0.goToState(r1, (long) r11, (android.animation.Animator.AnimatorListener) r2)
            com.android.launcher3.logging.StatsLogManager r0 = r8.mStatsLogManager
            com.android.launcher3.logging.StatsLogManager$StatsLogger r0 = r0.logger()
            com.android.launcher3.model.data.ItemInfo r1 = r9.dragInfo
            com.android.launcher3.logging.StatsLogManager$StatsLogger r0 = r0.withItemInfo(r1)
            com.android.launcher3.logging.InstanceId r1 = r9.logInstanceId
            com.android.launcher3.logging.StatsLogManager$StatsLogger r0 = r0.withInstanceId(r1)
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r1 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROP_COMPLETED
            r0.log(r1)
            r12 = r34
            goto L_0x03b4
        L_0x039e:
            r13 = r12
            r11 = r15
            r0 = 2
            int[] r0 = new int[r0]
            float[] r1 = r8.mDragViewVisualCenter
            r2 = r1[r13]
            int r2 = (int) r2
            r0[r13] = r2
            r2 = 1
            r1 = r1[r2]
            int r1 = (int) r1
            r0[r2] = r1
            r8.onDropExternal(r0, r11, r9)
            r12 = r13
        L_0x03b4:
            com.android.launcher3.accessibility.DragViewStateAnnouncer r0 = r9.stateAnnouncer
            if (r0 == 0) goto L_0x03c2
            if (r12 != 0) goto L_0x03c2
            com.android.launcher3.accessibility.DragViewStateAnnouncer r0 = r9.stateAnnouncer
            r1 = 2131755197(0x7f1000bd, float:1.9141266E38)
            r0.completeAction(r1)
        L_0x03c2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.onDrop(com.android.launcher3.DropTarget$DragObject, com.android.launcher3.dragndrop.DragOptions):void");
    }

    public /* synthetic */ void lambda$onDrop$7$Workspace(LauncherAppWidgetHostView launcherAppWidgetHostView, CellLayout cellLayout) {
        if (!isPageInTransition()) {
            AppWidgetResizeFrame.showForWidget(launcherAppWidgetHostView, cellLayout);
        }
    }

    public void onNoCellFound(View view, ItemInfo itemInfo, InstanceId instanceId) {
        int i = this.mLauncher.isHotseatLayout(view) ? R.string.hotseat_out_of_space : R.string.out_of_space;
        Launcher launcher = this.mLauncher;
        Toast.makeText(launcher, launcher.getString(i), 0).show();
        StatsLogManager.StatsLogger withItemInfo = this.mStatsLogManager.logger().withItemInfo(itemInfo);
        if (instanceId != null) {
            withItemInfo = withItemInfo.withInstanceId(instanceId);
        }
        withItemInfo.log(StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROP_FAILED_INSUFFICIENT_SPACE);
    }

    public Rect getPageAreaRelativeToDragLayer() {
        CellLayout cellLayout;
        Rect rect = new Rect();
        int nextPage = getNextPage();
        int panelCount = getPanelCount();
        int i = nextPage;
        while (i < nextPage + panelCount && (cellLayout = (CellLayout) getChildAt(i)) != null) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
            Rect rect2 = new Rect();
            this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(shortcutsAndWidgets, rect2);
            rect.union(rect2);
            i++;
        }
        return rect;
    }

    public void onDragEnter(DropTarget.DragObject dragObject) {
        this.mCreateUserFolderOnDrop = false;
        this.mAddToExistingFolderOnDrop = false;
        this.mDropToLayout = null;
        float[] visualCenter = dragObject.getVisualCenter(this.mDragViewVisualCenter);
        this.mDragViewVisualCenter = visualCenter;
        setDropLayoutForDragObject(dragObject, visualCenter[0], visualCenter[1]);
    }

    public void onDragExit(DropTarget.DragObject dragObject) {
        this.mDropToLayout = this.mDragTargetLayout;
        int i = this.mDragMode;
        if (i == 1) {
            this.mCreateUserFolderOnDrop = true;
        } else if (i == 2) {
            this.mAddToExistingFolderOnDrop = true;
        }
        setCurrentDropLayout((CellLayout) null);
        setCurrentDragOverlappingLayout((CellLayout) null);
        this.mSpringLoadedDragController.cancel();
    }

    private void enforceDragParity(String str, int i, int i2) {
        enforceDragParity(this, str, i, i2);
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            enforceDragParity(getChildAt(i3), str, i, i2);
        }
    }

    private void enforceDragParity(View view, String str, int i, int i2) {
        int i3;
        Object tag = view.getTag(R.id.drag_event_parity);
        if (tag == null) {
            i3 = 0;
        } else {
            i3 = ((Integer) tag).intValue();
        }
        int i4 = i3 + i;
        view.setTag(R.id.drag_event_parity, Integer.valueOf(i4));
        if (i4 != i2) {
            Log.e(WorkspaceLayoutManager.TAG, str + ": Drag contract violated: " + i4);
        }
    }

    /* access modifiers changed from: package-private */
    public void setCurrentDropLayout(CellLayout cellLayout) {
        CellLayout cellLayout2 = this.mDragTargetLayout;
        if (cellLayout2 != null) {
            cellLayout2.revertTempState();
            this.mDragTargetLayout.onDragExit();
        }
        this.mDragTargetLayout = cellLayout;
        if (cellLayout != null) {
            cellLayout.onDragEnter();
        }
        cleanupReorder(true);
        cleanupFolderCreation();
        setCurrentDropOverCell(-1, -1);
    }

    /* access modifiers changed from: package-private */
    public void setCurrentDragOverlappingLayout(CellLayout cellLayout) {
        CellLayout cellLayout2 = this.mDragOverlappingLayout;
        if (cellLayout2 != null) {
            cellLayout2.setIsDragOverlapping(false);
        }
        this.mDragOverlappingLayout = cellLayout;
        if (cellLayout != null) {
            cellLayout.setIsDragOverlapping(true);
        }
    }

    public CellLayout getCurrentDragOverlappingLayout() {
        return this.mDragOverlappingLayout;
    }

    /* access modifiers changed from: package-private */
    public void setCurrentDropOverCell(int i, int i2) {
        if (i != this.mDragOverX || i2 != this.mDragOverY) {
            this.mDragOverX = i;
            this.mDragOverY = i2;
            setDragMode(0);
        }
    }

    /* access modifiers changed from: package-private */
    public void setDragMode(int i) {
        if (i != this.mDragMode) {
            if (i == 0) {
                cleanupAddToFolder();
                cleanupReorder(false);
                cleanupFolderCreation();
            } else if (i == 2) {
                cleanupReorder(true);
                cleanupFolderCreation();
            } else if (i == 1) {
                cleanupAddToFolder();
                cleanupReorder(true);
            } else if (i == 3) {
                cleanupAddToFolder();
                cleanupFolderCreation();
            }
            this.mDragMode = i;
        }
    }

    private void cleanupFolderCreation() {
        PreviewBackground previewBackground = this.mFolderCreateBg;
        if (previewBackground != null) {
            previewBackground.animateToRest();
        }
    }

    private void cleanupAddToFolder() {
        FolderIcon folderIcon = this.mDragOverFolderIcon;
        if (folderIcon != null) {
            folderIcon.onDragExit();
            this.mDragOverFolderIcon = null;
        }
    }

    private void cleanupReorder(boolean z) {
        if (z) {
            this.mReorderAlarm.cancelAlarm();
        }
        this.mLastReorderX = -1;
        this.mLastReorderY = -1;
    }

    private void mapPointFromSelfToChild(View view, float[] fArr) {
        fArr[0] = fArr[0] - ((float) view.getLeft());
        fArr[1] = fArr[1] - ((float) view.getTop());
    }

    private void mapPointFromDropLayout(CellLayout cellLayout, float[] fArr) {
        if (this.mLauncher.isHotseatLayout(cellLayout)) {
            this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, fArr, true);
            this.mLauncher.getDragLayer().mapCoordInSelfToDescendant((View) cellLayout, fArr);
            return;
        }
        mapPointFromSelfToChild(cellLayout, fArr);
    }

    private boolean isDragWidget(DropTarget.DragObject dragObject) {
        return (dragObject.dragInfo instanceof LauncherAppWidgetInfo) || (dragObject.dragInfo instanceof PendingAddWidgetInfo);
    }

    public void onDragOver(DropTarget.DragObject dragObject) {
        ItemInfo itemInfo;
        int i;
        int i2;
        int i3;
        CellLayout cellLayout;
        DropTarget.DragObject dragObject2 = dragObject;
        if (!transitionStateShouldAllowDrop() || (itemInfo = dragObject2.dragInfo) == null) {
            return;
        }
        if (itemInfo.spanX < 0 || itemInfo.spanY < 0) {
            throw new RuntimeException("Improper spans found");
        }
        this.mDragViewVisualCenter = dragObject2.getVisualCenter(this.mDragViewVisualCenter);
        CellLayout.CellInfo cellInfo = this.mDragInfo;
        View view = cellInfo == null ? null : cellInfo.cell;
        float[] fArr = this.mDragViewVisualCenter;
        if (setDropLayoutForDragObject(dragObject2, fArr[0], fArr[1])) {
            CellLayout cellLayout2 = this.mDragTargetLayout;
            if (cellLayout2 == null || this.mLauncher.isHotseatLayout(cellLayout2)) {
                this.mSpringLoadedDragController.cancel();
            } else {
                this.mSpringLoadedDragController.setAlarm(this.mDragTargetLayout);
            }
        }
        CellLayout cellLayout3 = this.mDragTargetLayout;
        if (cellLayout3 != null) {
            mapPointFromDropLayout(cellLayout3, this.mDragViewVisualCenter);
            int i4 = itemInfo.spanX;
            int i5 = itemInfo.spanY;
            if (itemInfo.minSpanX > 0 && itemInfo.minSpanY > 0) {
                i4 = itemInfo.minSpanX;
                i5 = itemInfo.minSpanY;
            }
            int i6 = i4;
            int i7 = i5;
            float[] fArr2 = this.mDragViewVisualCenter;
            int[] findNearestArea = findNearestArea((int) fArr2[0], (int) fArr2[1], i6, i7, this.mDragTargetLayout, this.mTargetCell);
            this.mTargetCell = findNearestArea;
            int i8 = findNearestArea[0];
            int i9 = findNearestArea[1];
            setCurrentDropOverCell(findNearestArea[0], findNearestArea[1]);
            CellLayout cellLayout4 = this.mDragTargetLayout;
            float[] fArr3 = this.mDragViewVisualCenter;
            float distanceFromWorkspaceCellVisualCenter = cellLayout4.getDistanceFromWorkspaceCellVisualCenter(fArr3[0], fArr3[1], this.mTargetCell);
            manageFolderFeedback(distanceFromWorkspaceCellVisualCenter, dragObject2);
            CellLayout cellLayout5 = this.mDragTargetLayout;
            float[] fArr4 = this.mDragViewVisualCenter;
            boolean isNearestDropLocationOccupied = cellLayout5.isNearestDropLocationOccupied((int) fArr4[0], (int) fArr4[1], itemInfo.spanX, itemInfo.spanY, view, this.mTargetCell);
            if (!isNearestDropLocationOccupied) {
                CellLayout cellLayout6 = this.mDragTargetLayout;
                int[] iArr = this.mTargetCell;
                cellLayout6.visualizeDropLocation(iArr[0], iArr[1], itemInfo.spanX, itemInfo.spanY, dragObject);
            } else {
                int i10 = this.mDragMode;
                if ((i10 == 0 || i10 == 3) && !this.mReorderAlarm.alarmPending() && (!(this.mLastReorderX == i8 && this.mLastReorderY == i9) && distanceFromWorkspaceCellVisualCenter < this.mDragTargetLayout.getReorderRadius(this.mTargetCell))) {
                    CellLayout cellLayout7 = this.mDragTargetLayout;
                    float[] fArr5 = this.mDragViewVisualCenter;
                    cellLayout7.performReorder((int) fArr5[0], (int) fArr5[1], i6, i7, itemInfo.spanX, itemInfo.spanY, view, this.mTargetCell, new int[2], 0);
                    i = 2;
                    i2 = 1;
                    this.mReorderAlarm.setOnAlarmListener(new ReorderAlarmListener(this.mDragViewVisualCenter, i6, i7, itemInfo.spanX, itemInfo.spanY, dragObject, view));
                    this.mReorderAlarm.setAlarm(650);
                    i3 = this.mDragMode;
                    if ((i3 != i2 || i3 == i || !isNearestDropLocationOccupied) && (cellLayout = this.mDragTargetLayout) != null) {
                        cellLayout.revertTempState();
                    }
                    return;
                }
            }
            i2 = 1;
            i = 2;
            i3 = this.mDragMode;
            if (i3 != i2) {
            }
            cellLayout.revertTempState();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0021 A[LOOP:0: B:8:0x0021->B:11:0x003b, LOOP_START, PHI: r4 
      PHI: (r4v4 com.android.launcher3.CellLayout) = (r4v2 com.android.launcher3.CellLayout), (r4v8 com.android.launcher3.CellLayout) binds: [B:7:0x0019, B:11:0x003b] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean setDropLayoutForDragObject(com.android.launcher3.DropTarget.DragObject r3, float r4, float r5) {
        /*
            r2 = this;
            boolean r5 = r2.shouldUseHotseatAsDropLayout(r3)
            if (r5 == 0) goto L_0x000d
            com.android.launcher3.Launcher r3 = r2.mLauncher
            com.android.launcher3.Hotseat r3 = r3.getHotseat()
            goto L_0x0040
        L_0x000d:
            boolean r5 = r2.isDragObjectOverSmartSpace(r3)
            if (r5 != 0) goto L_0x003f
            com.android.launcher3.CellLayout r4 = r2.checkDragObjectIsOverNeighbourPages(r3, r4)
            if (r4 != 0) goto L_0x003d
            com.android.launcher3.util.IntSet r5 = r2.getVisiblePageIndices()
            java.util.Iterator r5 = r5.iterator()
        L_0x0021:
            boolean r0 = r5.hasNext()
            if (r0 == 0) goto L_0x003d
            java.lang.Object r4 = r5.next()
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            int r0 = r3.x
            float r0 = (float) r0
            int r1 = r3.y
            float r1 = (float) r1
            com.android.launcher3.CellLayout r4 = r2.verifyInsidePage(r4, r0, r1)
            if (r4 == 0) goto L_0x0021
        L_0x003d:
            r3 = r4
            goto L_0x0040
        L_0x003f:
            r3 = 0
        L_0x0040:
            com.android.launcher3.CellLayout r4 = r2.mDragTargetLayout
            if (r3 == r4) goto L_0x004c
            r2.setCurrentDropLayout(r3)
            r2.setCurrentDragOverlappingLayout(r3)
            r3 = 1
            return r3
        L_0x004c:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.setDropLayoutForDragObject(com.android.launcher3.DropTarget$DragObject, float, float):boolean");
    }

    private boolean shouldUseHotseatAsDropLayout(DropTarget.DragObject dragObject) {
        if (this.mLauncher.getHotseat() == null || this.mLauncher.getHotseat().getShortcutsAndWidgets() == null || isDragWidget(dragObject)) {
            return false;
        }
        getViewBoundsRelativeToWorkspace(this.mLauncher.getHotseat().getShortcutsAndWidgets(), this.mTempRect);
        return this.mTempRect.contains(dragObject.x, dragObject.y);
    }

    private boolean isDragObjectOverSmartSpace(DropTarget.DragObject dragObject) {
        View view = this.mQsb;
        if (view == null) {
            return false;
        }
        getViewBoundsRelativeToWorkspace(view, this.mTempRect);
        return this.mTempRect.contains(dragObject.x, dragObject.y);
    }

    private CellLayout checkDragObjectIsOverNeighbourPages(DropTarget.DragObject dragObject, float f) {
        if (isPageInTransition()) {
            return null;
        }
        float f2 = (float) dragObject.y;
        int nextPage = getNextPage();
        int i = 2;
        int[] iArr = new int[2];
        iArr[0] = nextPage - 1;
        if (!isTwoPanelEnabled()) {
            i = 1;
        }
        iArr[1] = i + nextPage;
        Iterator<Integer> it = IntSet.wrap(iArr).iterator();
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            CellLayout verifyInsidePage = verifyInsidePage(intValue, ((intValue >= nextPage || this.mIsRtl) && (intValue <= nextPage || !this.mIsRtl)) ? Math.max((float) dragObject.x, f) : Math.min((float) dragObject.x, f), f2);
            if (verifyInsidePage != null) {
                return verifyInsidePage;
            }
        }
        return null;
    }

    private void getViewBoundsRelativeToWorkspace(View view, Rect rect) {
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(view, this.mTempRect);
        this.mLauncher.getDragLayer().mapRectInSelfToDescendant(this, this.mTempRect);
        rect.set(this.mTempRect);
    }

    private CellLayout verifyInsidePage(int i, float f, float f2) {
        if (i < 0 || i >= getPageCount()) {
            return null;
        }
        CellLayout cellLayout = (CellLayout) getChildAt(i);
        if (f < ((float) cellLayout.getLeft()) || f > ((float) cellLayout.getRight()) || f2 < ((float) cellLayout.getTop()) || f2 > ((float) cellLayout.getBottom())) {
            return null;
        }
        return cellLayout;
    }

    private void manageFolderFeedback(float f, DropTarget.DragObject dragObject) {
        if (f > this.mDragTargetLayout.getFolderCreationRadius(this.mTargetCell)) {
            int i = this.mDragMode;
            if (i == 2 || i == 1) {
                setDragMode(0);
                return;
            }
            return;
        }
        CellLayout cellLayout = this.mDragTargetLayout;
        int[] iArr = this.mTargetCell;
        View childAt = cellLayout.getChildAt(iArr[0], iArr[1]);
        ItemInfo itemInfo = dragObject.dragInfo;
        boolean willCreateUserFolder = willCreateUserFolder(itemInfo, childAt, false);
        if (this.mDragMode != 0 || !willCreateUserFolder) {
            boolean willAddToExistingUserFolder = willAddToExistingUserFolder(itemInfo, childAt);
            if (!willAddToExistingUserFolder || this.mDragMode != 0) {
                if (this.mDragMode == 2 && !willAddToExistingUserFolder) {
                    setDragMode(0);
                }
                if (this.mDragMode == 1 && !willCreateUserFolder) {
                    setDragMode(0);
                    return;
                }
                return;
            }
            FolderIcon folderIcon = (FolderIcon) childAt;
            this.mDragOverFolderIcon = folderIcon;
            folderIcon.onDragEnter(itemInfo);
            CellLayout cellLayout2 = this.mDragTargetLayout;
            if (cellLayout2 != null) {
                cellLayout2.clearDragOutlines();
            }
            setDragMode(2);
            if (dragObject.stateAnnouncer != null) {
                dragObject.stateAnnouncer.announce(WorkspaceAccessibilityHelper.getDescriptionForDropOver(childAt, getContext()));
                return;
            }
            return;
        }
        PreviewBackground previewBackground = new PreviewBackground();
        this.mFolderCreateBg = previewBackground;
        Launcher launcher = this.mLauncher;
        previewBackground.setup(launcher, launcher, (View) null, childAt.getMeasuredWidth(), childAt.getPaddingTop());
        this.mFolderCreateBg.isClipping = false;
        PreviewBackground previewBackground2 = this.mFolderCreateBg;
        CellLayout cellLayout3 = this.mDragTargetLayout;
        int[] iArr2 = this.mTargetCell;
        previewBackground2.animateToAccept(cellLayout3, iArr2[0], iArr2[1]);
        this.mDragTargetLayout.clearDragOutlines();
        setDragMode(1);
        if (dragObject.stateAnnouncer != null) {
            dragObject.stateAnnouncer.announce(WorkspaceAccessibilityHelper.getDescriptionForDropOver(childAt, getContext()));
        }
    }

    class ReorderAlarmListener implements OnAlarmListener {
        final View child;
        final DropTarget.DragObject dragObject;
        final float[] dragViewCenter;
        final int minSpanX;
        final int minSpanY;
        final int spanX;
        final int spanY;

        public ReorderAlarmListener(float[] fArr, int i, int i2, int i3, int i4, DropTarget.DragObject dragObject2, View view) {
            this.dragViewCenter = fArr;
            this.minSpanX = i;
            this.minSpanY = i2;
            this.spanX = i3;
            this.spanY = i4;
            this.child = view;
            this.dragObject = dragObject2;
        }

        public void onAlarm(Alarm alarm) {
            int[] iArr = new int[2];
            Workspace workspace = Workspace.this;
            workspace.mTargetCell = workspace.findNearestArea((int) workspace.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], this.minSpanX, this.minSpanY, Workspace.this.mDragTargetLayout, Workspace.this.mTargetCell);
            Workspace workspace2 = Workspace.this;
            workspace2.mLastReorderX = workspace2.mTargetCell[0];
            Workspace workspace3 = Workspace.this;
            workspace3.mLastReorderY = workspace3.mTargetCell[1];
            Workspace workspace4 = Workspace.this;
            workspace4.mTargetCell = workspace4.mDragTargetLayout.performReorder((int) Workspace.this.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], this.minSpanX, this.minSpanY, this.spanX, this.spanY, this.child, Workspace.this.mTargetCell, iArr, 1);
            if (Workspace.this.mTargetCell[0] < 0 || Workspace.this.mTargetCell[1] < 0) {
                Workspace.this.mDragTargetLayout.revertTempState();
            } else {
                Workspace.this.setDragMode(3);
            }
            if (iArr[0] == this.spanX) {
                int i = iArr[1];
                int i2 = this.spanY;
            }
            Workspace.this.mDragTargetLayout.visualizeDropLocation(Workspace.this.mTargetCell[0], Workspace.this.mTargetCell[1], iArr[0], iArr[1], this.dragObject);
        }
    }

    public void getHitRectRelativeToDragLayer(Rect rect) {
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(this, rect);
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0132  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01ea  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x022b  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x022e  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0257  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x029d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onDropExternal(int[] r25, com.android.launcher3.CellLayout r26, com.android.launcher3.DropTarget.DragObject r27) {
        /*
            r24 = this;
            r8 = r24
            r7 = r26
            r15 = r27
            com.android.launcher3.model.data.ItemInfo r0 = r15.dragInfo
            boolean r0 = r0 instanceof com.android.launcher3.widget.PendingAddShortcutInfo
            if (r0 == 0) goto L_0x001a
            com.android.launcher3.model.data.ItemInfo r0 = r15.dragInfo
            com.android.launcher3.widget.PendingAddShortcutInfo r0 = (com.android.launcher3.widget.PendingAddShortcutInfo) r0
            com.android.launcher3.pm.ShortcutConfigActivityInfo r0 = r0.activityInfo
            com.android.launcher3.model.data.WorkspaceItemInfo r0 = r0.createWorkspaceItemInfo()
            if (r0 == 0) goto L_0x001a
            r15.dragInfo = r0
        L_0x001a:
            com.android.launcher3.model.data.ItemInfo r14 = r15.dragInfo
            int r0 = r14.spanX
            int r1 = r14.spanY
            com.android.launcher3.CellLayout$CellInfo r2 = r8.mDragInfo
            if (r2 == 0) goto L_0x002a
            int r0 = r2.spanX
            com.android.launcher3.CellLayout$CellInfo r1 = r8.mDragInfo
            int r1 = r1.spanY
        L_0x002a:
            r3 = r0
            r4 = r1
            com.android.launcher3.Launcher r0 = r8.mLauncher
            boolean r0 = r0.isHotseatLayout(r7)
            if (r0 == 0) goto L_0x0037
            r0 = -101(0xffffffffffffff9b, float:NaN)
            goto L_0x0039
        L_0x0037:
            r0 = -100
        L_0x0039:
            r21 = r0
            int r13 = r8.getIdForScreen(r7)
            com.android.launcher3.Launcher r0 = r8.mLauncher
            boolean r0 = r0.isHotseatLayout(r7)
            if (r0 != 0) goto L_0x0060
            int r0 = r8.mCurrentPage
            int r0 = r8.getScreenIdForPageIndex(r0)
            if (r13 == r0) goto L_0x0060
            com.android.launcher3.Launcher r0 = r8.mLauncher
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.SPRING_LOADED
            boolean r0 = r0.isInState(r1)
            if (r0 != 0) goto L_0x0060
            int r0 = r8.getPageIndexForScreenId(r13)
            r8.snapToPage(r0)
        L_0x0060:
            boolean r0 = r14 instanceof com.android.launcher3.PendingAddItemInfo
            r9 = 2
            r22 = 0
            r12 = 1
            if (r0 == 0) goto L_0x016d
            r11 = r14
            com.android.launcher3.PendingAddItemInfo r11 = (com.android.launcher3.PendingAddItemInfo) r11
            int r0 = r11.itemType
            if (r0 != r12) goto L_0x00a6
            r1 = r25[r22]
            r2 = r25[r12]
            int[] r6 = r8.mTargetCell
            r0 = r24
            r5 = r26
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r8.mTargetCell = r0
            float[] r1 = r8.mDragViewVisualCenter
            r2 = r1[r22]
            r1 = r1[r12]
            float r6 = r7.getDistanceFromWorkspaceCellVisualCenter(r2, r1, r0)
            com.android.launcher3.model.data.ItemInfo r1 = r15.dragInfo
            int[] r3 = r8.mTargetCell
            r5 = 1
            r0 = r24
            r2 = r26
            r4 = r6
            boolean r0 = r0.willCreateUserFolder(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x00a3
            com.android.launcher3.model.data.ItemInfo r0 = r15.dragInfo
            int[] r1 = r8.mTargetCell
            boolean r0 = r8.willAddToExistingUserFolder(r0, r7, r1, r6)
            if (r0 == 0) goto L_0x00a6
        L_0x00a3:
            r0 = r22
            goto L_0x00a7
        L_0x00a6:
            r0 = r12
        L_0x00a7:
            com.android.launcher3.model.data.ItemInfo r6 = r15.dragInfo
            if (r0 == 0) goto L_0x0101
            int r0 = r6.spanX
            int r1 = r6.spanY
            int r2 = r6.minSpanX
            if (r2 <= 0) goto L_0x00bb
            int r2 = r6.minSpanY
            if (r2 <= 0) goto L_0x00bb
            int r0 = r6.minSpanX
            int r1 = r6.minSpanY
        L_0x00bb:
            int[] r2 = new int[r9]
            float[] r3 = r8.mDragViewVisualCenter
            r4 = r3[r22]
            int r10 = (int) r4
            r3 = r3[r12]
            int r3 = (int) r3
            int r4 = r14.spanX
            int r5 = r14.spanY
            r16 = 0
            int[] r9 = r8.mTargetCell
            r19 = 3
            r17 = r9
            r9 = r26
            r25 = r11
            r11 = r3
            r3 = r12
            r12 = r0
            r23 = r13
            r13 = r1
            r1 = r14
            r14 = r4
            r4 = r15
            r15 = r5
            r18 = r2
            int[] r0 = r9.performReorder(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r8.mTargetCell = r0
            r0 = r2[r22]
            int r5 = r6.spanX
            if (r0 != r5) goto L_0x00f7
            r0 = r2[r3]
            int r5 = r6.spanY
            if (r0 == r5) goto L_0x00f4
            goto L_0x00f7
        L_0x00f4:
            r12 = r22
            goto L_0x00f8
        L_0x00f7:
            r12 = r3
        L_0x00f8:
            r0 = r2[r22]
            r6.spanX = r0
            r0 = r2[r3]
            r6.spanY = r0
            goto L_0x010a
        L_0x0101:
            r25 = r11
            r3 = r12
            r23 = r13
            r1 = r14
            r4 = r15
            r12 = r22
        L_0x010a:
            com.android.launcher3.Workspace$5 r9 = new com.android.launcher3.Workspace$5
            r0 = r9
            r10 = r1
            r1 = r24
            r2 = r25
            r15 = r3
            r3 = r21
            r14 = r4
            r4 = r23
            r5 = r6
            r11 = r6
            r6 = r27
            r0.<init>(r2, r3, r4, r5, r6)
            r0 = r25
            int r1 = r0.itemType
            r2 = 4
            if (r1 == r2) goto L_0x012f
            int r1 = r0.itemType
            r2 = 5
            if (r1 != r2) goto L_0x012c
            goto L_0x012f
        L_0x012c:
            r1 = r22
            goto L_0x0130
        L_0x012f:
            r1 = r15
        L_0x0130:
            if (r1 == 0) goto L_0x0138
            r2 = r0
            com.android.launcher3.widget.PendingAddWidgetInfo r2 = (com.android.launcher3.widget.PendingAddWidgetInfo) r2
            android.appwidget.AppWidgetHostView r2 = r2.boundWidget
            goto L_0x0139
        L_0x0138:
            r2 = 0
        L_0x0139:
            r6 = r2
            if (r6 == 0) goto L_0x0147
            if (r12 == 0) goto L_0x0147
            com.android.launcher3.Launcher r2 = r8.mLauncher
            int r3 = r11.spanX
            int r4 = r11.spanY
            com.android.launcher3.widget.util.WidgetSizes.updateWidgetSizeRanges(r6, r2, r3, r4)
        L_0x0147:
            if (r1 == 0) goto L_0x015c
            r11 = r0
            com.android.launcher3.widget.PendingAddWidgetInfo r11 = (com.android.launcher3.widget.PendingAddWidgetInfo) r11
            com.android.launcher3.widget.LauncherAppWidgetProviderInfo r0 = r11.info
            if (r0 == 0) goto L_0x015c
            com.android.launcher3.widget.WidgetAddFlowHandler r0 = r11.getHandler()
            boolean r0 = r0.needsConfigure()
            if (r0 == 0) goto L_0x015c
            r5 = r15
            goto L_0x015e
        L_0x015c:
            r5 = r22
        L_0x015e:
            com.android.launcher3.dragndrop.DragView r3 = r14.dragView
            r11 = 1
            r0 = r24
            r1 = r10
            r2 = r26
            r4 = r9
            r7 = r11
            r0.animateWidgetDrop(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x02c5
        L_0x016d:
            r23 = r13
            r10 = r14
            r14 = r15
            r15 = r12
            com.android.launcher3.Launcher r0 = r8.mLauncher
            com.android.launcher3.statemanager.StateManager r0 = r0.getStateManager()
            com.android.launcher3.LauncherState r1 = com.android.launcher3.LauncherState.NORMAL
            r5 = 500(0x1f4, double:2.47E-321)
            r0.goToState(r1, (long) r5)
            int r0 = r10.itemType
            if (r0 == 0) goto L_0x01b8
            if (r0 == r15) goto L_0x01b8
            if (r0 == r9) goto L_0x01a9
            r1 = 6
            if (r0 == r1) goto L_0x01b8
            r1 = 7
            if (r0 != r1) goto L_0x018e
            goto L_0x01b8
        L_0x018e:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unknown item type: "
            java.lang.StringBuilder r1 = r1.append(r2)
            int r2 = r10.itemType
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x01a9:
            r0 = 2131492940(0x7f0c004c, float:1.8609346E38)
            com.android.launcher3.Launcher r1 = r8.mLauncher
            r2 = r10
            com.android.launcher3.model.data.FolderInfo r2 = (com.android.launcher3.model.data.FolderInfo) r2
            com.android.launcher3.folder.FolderIcon r0 = com.android.launcher3.folder.FolderIcon.inflateFolderAndIcon(r0, r1, r7, r2)
            r13 = r0
            r12 = r10
            goto L_0x01e8
        L_0x01b8:
            boolean r0 = r10 instanceof com.android.launcher3.model.data.WorkspaceItemFactory
            if (r0 == 0) goto L_0x01c8
            r0 = r10
            com.android.launcher3.model.data.WorkspaceItemFactory r0 = (com.android.launcher3.model.data.WorkspaceItemFactory) r0
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.model.data.WorkspaceItemInfo r0 = r0.makeWorkspaceItem(r1)
            r14.dragInfo = r0
            goto L_0x01c9
        L_0x01c8:
            r0 = r10
        L_0x01c9:
            boolean r1 = r0 instanceof com.android.launcher3.model.data.WorkspaceItemInfo
            if (r1 == 0) goto L_0x01dd
            int r1 = r0.container
            r2 = -102(0xffffffffffffff9a, float:NaN)
            if (r1 != r2) goto L_0x01dd
            com.android.launcher3.model.data.WorkspaceItemInfo r1 = new com.android.launcher3.model.data.WorkspaceItemInfo
            com.android.launcher3.model.data.WorkspaceItemInfo r0 = (com.android.launcher3.model.data.WorkspaceItemInfo) r0
            r1.<init>((com.android.launcher3.model.data.WorkspaceItemInfo) r0)
            r14.dragInfo = r1
            r0 = r1
        L_0x01dd:
            com.android.launcher3.Launcher r1 = r8.mLauncher
            r2 = r0
            com.android.launcher3.model.data.WorkspaceItemInfo r2 = (com.android.launcher3.model.data.WorkspaceItemInfo) r2
            android.view.View r1 = r1.createShortcut(r7, r2)
            r12 = r0
            r13 = r1
        L_0x01e8:
            if (r25 == 0) goto L_0x022b
            r1 = r25[r22]
            r2 = r25[r15]
            int[] r6 = r8.mTargetCell
            r0 = r24
            r5 = r26
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r8.mTargetCell = r0
            float[] r1 = r8.mDragViewVisualCenter
            r2 = r1[r22]
            r1 = r1[r15]
            float r9 = r7.getDistanceFromWorkspaceCellVisualCenter(r2, r1, r0)
            int[] r4 = r8.mTargetCell
            r6 = 1
            r0 = r24
            r1 = r13
            r2 = r21
            r3 = r26
            r5 = r9
            r11 = r7
            r7 = r27
            boolean r0 = r0.createUserFolderIfNecessary(r1, r2, r3, r4, r5, r6, r7)
            if (r0 == 0) goto L_0x0219
            return
        L_0x0219:
            int[] r3 = r8.mTargetCell
            r6 = 1
            r0 = r24
            r1 = r13
            r2 = r26
            r4 = r9
            r5 = r27
            boolean r0 = r0.addToExistingFolderIfNecessary(r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L_0x022c
            return
        L_0x022b:
            r11 = r7
        L_0x022c:
            if (r25 == 0) goto L_0x0257
            float[] r0 = r8.mDragViewVisualCenter
            r1 = r0[r22]
            int r10 = (int) r1
            r0 = r0[r15]
            int r0 = (int) r0
            r1 = 1
            r2 = 1
            r3 = 1
            r4 = 1
            r16 = 0
            int[] r5 = r8.mTargetCell
            r18 = 0
            r19 = 3
            r9 = r26
            r7 = r11
            r11 = r0
            r0 = r12
            r12 = r1
            r6 = r13
            r13 = r2
            r2 = r14
            r14 = r3
            r1 = r15
            r15 = r4
            r17 = r5
            int[] r3 = r9.performReorder(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r8.mTargetCell = r3
            goto L_0x0261
        L_0x0257:
            r7 = r11
            r0 = r12
            r6 = r13
            r2 = r14
            r1 = r15
            int[] r3 = r8.mTargetCell
            r7.findCellForSpan(r3, r1, r1)
        L_0x0261:
            com.android.launcher3.Launcher r3 = r8.mLauncher
            com.android.launcher3.model.ModelWriter r15 = r3.getModelWriter()
            int[] r3 = r8.mTargetCell
            r19 = r3[r22]
            r20 = r3[r1]
            r16 = r0
            r17 = r21
            r18 = r23
            r15.addOrMoveItemInDatabase(r16, r17, r18, r19, r20)
            int[] r3 = r8.mTargetCell
            r4 = r3[r22]
            r5 = r3[r1]
            int r9 = r0.spanX
            int r10 = r0.spanY
            r0 = r24
            r1 = r6
            r11 = r2
            r2 = r21
            r3 = r23
            r12 = r6
            r6 = r9
            r9 = r7
            r7 = r10
            r0.addInScreen(r1, r2, r3, r4, r5, r6, r7)
            r9.onDropChild(r12)
            com.android.launcher3.ShortcutAndWidgetContainer r0 = r26.getShortcutsAndWidgets()
            r0.measureChild(r12)
            com.android.launcher3.dragndrop.DragView r0 = r11.dragView
            if (r0 == 0) goto L_0x02ae
            r24.setFinalTransitionTransform()
            com.android.launcher3.Launcher r0 = r8.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            com.android.launcher3.dragndrop.DragView r1 = r11.dragView
            r0.animateViewIntoPosition(r1, r12, r8)
            r24.resetTransitionTransform()
        L_0x02ae:
            com.android.launcher3.logging.StatsLogManager r0 = r8.mStatsLogManager
            com.android.launcher3.logging.StatsLogManager$StatsLogger r0 = r0.logger()
            com.android.launcher3.model.data.ItemInfo r1 = r11.dragInfo
            com.android.launcher3.logging.StatsLogManager$StatsLogger r0 = r0.withItemInfo(r1)
            com.android.launcher3.logging.InstanceId r1 = r11.logInstanceId
            com.android.launcher3.logging.StatsLogManager$StatsLogger r0 = r0.withInstanceId(r1)
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r1 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROP_COMPLETED
            r0.log(r1)
        L_0x02c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.onDropExternal(int[], com.android.launcher3.CellLayout, com.android.launcher3.DropTarget$DragObject):void");
    }

    private Drawable createWidgetDrawable(ItemInfo itemInfo, View view) {
        int[] estimateItemSize = estimateItemSize(itemInfo);
        int visibility = view.getVisibility();
        view.setVisibility(0);
        view.measure(View.MeasureSpec.makeMeasureSpec(estimateItemSize[0], BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(estimateItemSize[1], BasicMeasure.EXACTLY));
        view.layout(0, 0, estimateItemSize[0], estimateItemSize[1]);
        int i = estimateItemSize[0];
        int i2 = estimateItemSize[1];
        Objects.requireNonNull(view);
        Bitmap createHardwareBitmap = BitmapRenderer.createHardwareBitmap(i, i2, new BitmapRenderer(view) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void draw(Canvas canvas) {
                this.f$0.draw(canvas);
            }
        });
        view.setVisibility(visibility);
        return new FastBitmapDrawable(createHardwareBitmap);
    }

    private void getFinalPositionForDropAnimation(int[] iArr, float[] fArr, DragView dragView, CellLayout cellLayout, ItemInfo itemInfo, int[] iArr2, boolean z, View view) {
        int[] iArr3 = iArr;
        ItemInfo itemInfo2 = itemInfo;
        View view2 = view;
        Rect estimateItemPosition = estimateItemPosition(cellLayout, iArr2[0], iArr2[1], itemInfo2.spanX, itemInfo2.spanY);
        if (itemInfo2.itemType == 4) {
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            if (deviceProfile.shouldInsetWidgets() && (view2 instanceof NavigableAppWidgetHostView)) {
                Rect rect = new Rect();
                ((NavigableAppWidgetHostView) view2).getWidgetInset(deviceProfile, rect);
                estimateItemPosition.left -= rect.left;
                estimateItemPosition.right += rect.right;
                estimateItemPosition.top -= rect.top;
                estimateItemPosition.bottom += rect.bottom;
            }
            Utilities.shrinkRect(estimateItemPosition, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y);
        }
        this.mTempFXY[0] = (float) estimateItemPosition.left;
        this.mTempFXY[1] = (float) estimateItemPosition.top;
        setFinalTransitionTransform();
        float descendantCoordRelativeToSelf = this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(cellLayout, this.mTempFXY, true);
        resetTransitionTransform();
        Utilities.roundArray(this.mTempFXY, iArr3);
        if (z) {
            float width = (((float) estimateItemPosition.width()) * 1.0f) / ((float) dragView.getMeasuredWidth());
            float height = (((float) estimateItemPosition.height()) * 1.0f) / ((float) dragView.getMeasuredHeight());
            iArr3[0] = (int) (((double) iArr3[0]) - (((double) ((((float) dragView.getMeasuredWidth()) - (((float) estimateItemPosition.width()) * descendantCoordRelativeToSelf)) / 2.0f)) - Math.ceil((double) (((float) cellLayout.getUnusedHorizontalSpace()) / 2.0f))));
            iArr3[1] = (int) (((float) iArr3[1]) - ((((float) dragView.getMeasuredHeight()) - (((float) estimateItemPosition.height()) * descendantCoordRelativeToSelf)) / 2.0f));
            fArr[0] = width * descendantCoordRelativeToSelf;
            fArr[1] = height * descendantCoordRelativeToSelf;
            return;
        }
        float initialScale = dragView.getInitialScale() * descendantCoordRelativeToSelf;
        float f = initialScale - 1.0f;
        iArr3[0] = (int) (((float) iArr3[0]) + ((((float) dragView.getWidth()) * f) / 2.0f));
        iArr3[1] = (int) (((float) iArr3[1]) + ((f * ((float) dragView.getHeight())) / 2.0f));
        fArr[1] = initialScale;
        fArr[0] = initialScale;
        Rect dragRegion = dragView.getDragRegion();
        if (dragRegion != null) {
            iArr3[0] = (int) (((float) iArr3[0]) + (((float) dragRegion.left) * descendantCoordRelativeToSelf));
            iArr3[1] = (int) (((float) iArr3[1]) + (descendantCoordRelativeToSelf * ((float) dragRegion.top)));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0094  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void animateWidgetDrop(com.android.launcher3.model.data.ItemInfo r17, com.android.launcher3.CellLayout r18, com.android.launcher3.dragndrop.DragView r19, java.lang.Runnable r20, int r21, android.view.View r22, boolean r23) {
        /*
            r16 = this;
            r11 = r16
            r9 = r17
            r10 = r21
            r12 = r22
            r13 = 2
            int[] r14 = new int[r13]
            float[] r15 = new float[r13]
            boolean r0 = r9 instanceof com.android.launcher3.widget.PendingAddShortcutInfo
            r8 = 1
            r7 = r0 ^ 1
            int[] r6 = r11.mTargetCell
            r0 = r16
            r1 = r14
            r2 = r15
            r3 = r19
            r4 = r18
            r5 = r17
            r8 = r22
            r0.getFinalPositionForDropAnimation(r1, r2, r3, r4, r5, r6, r7, r8)
            com.android.launcher3.Launcher r0 = r11.mLauncher
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131361803(0x7f0a000b, float:1.8343369E38)
            int r0 = r0.getInteger(r1)
            int r8 = r0 + -200
            int r0 = r9.itemType
            r1 = 4
            r2 = 0
            if (r0 == r1) goto L_0x0040
            int r0 = r9.itemType
            r3 = 5
            if (r0 != r3) goto L_0x003e
            goto L_0x0040
        L_0x003e:
            r0 = r2
            goto L_0x0041
        L_0x0040:
            r0 = 1
        L_0x0041:
            if (r10 == r13) goto L_0x0045
            if (r23 == 0) goto L_0x005d
        L_0x0045:
            if (r12 == 0) goto L_0x005d
            android.view.View r3 = r19.getContentView()
            if (r3 == r12) goto L_0x005d
            android.graphics.drawable.Drawable r0 = r11.createWidgetDrawable(r9, r12)
            float r3 = (float) r8
            r4 = 1061997773(0x3f4ccccd, float:0.8)
            float r3 = r3 * r4
            int r3 = (int) r3
            r4 = r19
            r4.crossFadeContent(r0, r3)
            goto L_0x0071
        L_0x005d:
            r4 = r19
            if (r0 == 0) goto L_0x0071
            if (r23 == 0) goto L_0x0071
            r0 = r15[r2]
            r3 = 1
            r5 = r15[r3]
            float r0 = java.lang.Math.min(r0, r5)
            r15[r3] = r0
            r15[r2] = r0
            goto L_0x0072
        L_0x0071:
            r3 = 1
        L_0x0072:
            com.android.launcher3.Launcher r0 = r11.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            if (r10 != r1) goto L_0x0094
            com.android.launcher3.Launcher r0 = r11.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            r3 = 0
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r6 = 1036831949(0x3dcccccd, float:0.1)
            r7 = 0
            r1 = r19
            r2 = r14
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r20
            r0.animateViewIntoPosition(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x00b7
        L_0x0094:
            if (r10 != r3) goto L_0x0097
            goto L_0x0098
        L_0x0097:
            r13 = r2
        L_0x0098:
            com.android.launcher3.Workspace$6 r7 = new com.android.launcher3.Workspace$6
            r1 = r20
            r7.<init>(r12, r1)
            r5 = r14[r2]
            r6 = r14[r3]
            r9 = 1065353216(0x3f800000, float:1.0)
            r10 = r15[r2]
            r12 = r15[r3]
            r1 = r19
            r2 = r5
            r3 = r6
            r4 = r9
            r5 = r10
            r6 = r12
            r9 = r8
            r8 = r13
            r10 = r16
            r0.animateViewIntoPosition(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
        L_0x00b7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.animateWidgetDrop(com.android.launcher3.model.data.ItemInfo, com.android.launcher3.CellLayout, com.android.launcher3.dragndrop.DragView, java.lang.Runnable, int, android.view.View, boolean):void");
    }

    public void setFinalTransitionTransform() {
        if (isSwitchingState()) {
            this.mCurrentScale = getScaleX();
            setScaleX(this.mStateTransitionAnimation.getFinalScale());
            setScaleY(this.mStateTransitionAnimation.getFinalScale());
        }
    }

    public void resetTransitionTransform() {
        if (isSwitchingState()) {
            setScaleX(this.mCurrentScale);
            setScaleY(this.mCurrentScale);
        }
    }

    public CellLayout.CellInfo getDragInfo() {
        return this.mDragInfo;
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestArea(int i, int i2, int i3, int i4, CellLayout cellLayout, int[] iArr) {
        return cellLayout.findNearestArea(i, i2, i3, i4, iArr);
    }

    /* access modifiers changed from: package-private */
    public void setup(DragController dragController) {
        this.mSpringLoadedDragController = new SpringLoadedDragController(this.mLauncher);
        this.mDragController = dragController;
        updateChildrenLayersEnabled();
    }

    public void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
        CellLayout.CellInfo cellInfo;
        if (!z) {
            CellLayout.CellInfo cellInfo2 = this.mDragInfo;
            if (cellInfo2 != null) {
                if ((cellInfo2.cell instanceof LauncherAppWidgetHostView) && dragObject.dragView != null) {
                    dragObject.dragView.detachContentView(true);
                }
                CellLayout cellLayout = this.mLauncher.getCellLayout(this.mDragInfo.container, this.mDragInfo.screenId);
                if (cellLayout != null) {
                    cellLayout.onDropChild(this.mDragInfo.cell);
                }
            }
        } else if (!(view == this || (cellInfo = this.mDragInfo) == null)) {
            removeWorkspaceItem(cellInfo.cell);
        }
        View homescreenIconByItemId = getHomescreenIconByItemId(dragObject.originalDragInfo.id);
        if (dragObject.cancelled && homescreenIconByItemId != null) {
            homescreenIconByItemId.setVisibility(0);
        }
        this.mDragInfo = null;
    }

    public void removeWorkspaceItem(View view) {
        CellLayout parentCellLayoutForView = getParentCellLayoutForView(view);
        if (parentCellLayoutForView != null) {
            parentCellLayoutForView.removeView(view);
        }
        if (view instanceof DropTarget) {
            this.mDragController.removeDropTarget((DropTarget) view);
        }
    }

    public void removeWidget(int i) {
        mapOverItems(new LauncherBindableItemsContainer.ItemOperator(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return Workspace.this.lambda$removeWidget$9$Workspace(this.f$1, itemInfo, view);
            }
        });
    }

    public /* synthetic */ boolean lambda$removeWidget$9$Workspace(int i, ItemInfo itemInfo, View view) {
        if (!(itemInfo instanceof LauncherAppWidgetInfo)) {
            return false;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
        if (launcherAppWidgetInfo.appWidgetId != i) {
            return false;
        }
        this.mLauncher.removeItem(view, launcherAppWidgetInfo, true, "widget is removed in response to widget remove broadcast");
        return true;
    }

    public void removeFolderListeners() {
        mapOverItems(new LauncherBindableItemsContainer.ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if (!(view instanceof FolderIcon)) {
                    return false;
                }
                ((FolderIcon) view).removeListeners();
                return false;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        this.mSavedStates = sparseArray;
    }

    public void restoreInstanceStateForChild(int i) {
        if (this.mSavedStates != null) {
            this.mRestoredPages.add(i);
            CellLayout cellLayout = (CellLayout) getChildAt(i);
            if (cellLayout != null) {
                cellLayout.restoreInstanceState(this.mSavedStates);
            }
        }
    }

    public void restoreInstanceStateForRemainingPages() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (!this.mRestoredPages.contains(i)) {
                restoreInstanceStateForChild(i);
            }
        }
        this.mRestoredPages.clear();
        this.mSavedStates = null;
    }

    public boolean scrollLeft() {
        boolean scrollLeft = (this.mIsSwitchingState || !workspaceInScrollableState()) ? false : super.scrollLeft();
        Folder open = Folder.getOpen(this.mLauncher);
        if (open != null) {
            open.completeDragExit();
        }
        return scrollLeft;
    }

    public boolean scrollRight() {
        boolean scrollRight = (this.mIsSwitchingState || !workspaceInScrollableState()) ? false : super.scrollRight();
        Folder open = Folder.getOpen(this.mLauncher);
        if (open != null) {
            open.completeDragExit();
        }
        return scrollRight;
    }

    /* access modifiers changed from: package-private */
    public CellLayout getParentCellLayoutForView(View view) {
        for (CellLayout cellLayout : getWorkspaceAndHotseatCellLayouts()) {
            if (cellLayout.getShortcutsAndWidgets().indexOfChild(view) > -1) {
                return cellLayout;
            }
        }
        return null;
    }

    private CellLayout[] getWorkspaceAndHotseatCellLayouts() {
        CellLayout[] cellLayoutArr;
        int childCount = getChildCount();
        if (this.mLauncher.getHotseat() != null) {
            cellLayoutArr = new CellLayout[(childCount + 1)];
            cellLayoutArr[childCount] = this.mLauncher.getHotseat();
        } else {
            cellLayoutArr = new CellLayout[childCount];
        }
        for (int i = 0; i < childCount; i++) {
            cellLayoutArr[i] = (CellLayout) getChildAt(i);
        }
        return cellLayoutArr;
    }

    static /* synthetic */ boolean lambda$getHomescreenIconByItemId$10(int i, ItemInfo itemInfo, View view) {
        return itemInfo != null && itemInfo.id == i;
    }

    public View getHomescreenIconByItemId(int i) {
        return getFirstMatch(new LauncherBindableItemsContainer.ItemOperator(i) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return Workspace.lambda$getHomescreenIconByItemId$10(this.f$0, itemInfo, view);
            }
        });
    }

    public LauncherAppWidgetHostView getWidgetForAppWidgetId(int i) {
        return (LauncherAppWidgetHostView) getFirstMatch(new LauncherBindableItemsContainer.ItemOperator(i) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return Workspace.lambda$getWidgetForAppWidgetId$11(this.f$0, itemInfo, view);
            }
        });
    }

    static /* synthetic */ boolean lambda$getWidgetForAppWidgetId$11(int i, ItemInfo itemInfo, View view) {
        return (itemInfo instanceof LauncherAppWidgetInfo) && ((LauncherAppWidgetInfo) itemInfo).appWidgetId == i;
    }

    public View getFirstMatch(final LauncherBindableItemsContainer.ItemOperator itemOperator) {
        final View[] viewArr = new View[1];
        mapOverItems(new LauncherBindableItemsContainer.ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if (!itemOperator.evaluate(itemInfo, view)) {
                    return false;
                }
                viewArr[0] = view;
                return true;
            }
        });
        return viewArr[0];
    }

    /* access modifiers changed from: package-private */
    public void clearDropTargets() {
        mapOverItems(new LauncherBindableItemsContainer.ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if (!(view instanceof DropTarget)) {
                    return false;
                }
                Workspace.this.mDragController.removeDropTarget((DropTarget) view);
                return false;
            }
        });
    }

    public void removeItemsByMatcher(Predicate<ItemInfo> predicate) {
        for (CellLayout cellLayout : getWorkspaceAndHotseatCellLayouts()) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
            for (int childCount = shortcutsAndWidgets.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = shortcutsAndWidgets.getChildAt(childCount);
                ItemInfo itemInfo = (ItemInfo) childAt.getTag();
                if (predicate.test(itemInfo)) {
                    cellLayout.removeViewInLayout(childAt);
                    if (childAt instanceof DropTarget) {
                        this.mDragController.removeDropTarget((DropTarget) childAt);
                    }
                } else if (childAt instanceof FolderIcon) {
                    FolderInfo folderInfo = (FolderInfo) itemInfo;
                    List list = (List) folderInfo.contents.stream().filter(predicate).collect(Collectors.toList());
                    if (!list.isEmpty()) {
                        folderInfo.removeAll(list, false);
                        FolderIcon folderIcon = (FolderIcon) childAt;
                        if (folderIcon.getFolder().isOpen()) {
                            folderIcon.getFolder().close(false);
                        }
                    }
                }
            }
        }
        stripEmptyScreens();
    }

    public void mapOverItems(LauncherBindableItemsContainer.ItemOperator itemOperator) {
        CellLayout[] workspaceAndHotseatCellLayouts = getWorkspaceAndHotseatCellLayouts();
        int length = workspaceAndHotseatCellLayouts.length;
        int i = 0;
        while (i < length && mapOverCellLayout(workspaceAndHotseatCellLayouts[i], itemOperator) == null) {
            i++;
        }
    }

    public View mapOverCellLayout(CellLayout cellLayout, LauncherBindableItemsContainer.ItemOperator itemOperator) {
        if (cellLayout == null) {
            return null;
        }
        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
        int childCount = shortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = shortcutsAndWidgets.getChildAt(i);
            if (itemOperator.evaluate((ItemInfo) childAt.getTag(), childAt)) {
                return childAt;
            }
        }
        return null;
    }

    public void updateNotificationDots(Predicate<PackageUserKey> predicate) {
        $$Lambda$Workspace$0Yrt_M3RYnkx3ZJm2gsIl5gIkq8 r3 = new LauncherBindableItemsContainer.ItemOperator(new Predicate(predicate) {
            public final /* synthetic */ Predicate f$1;

            {
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return Workspace.lambda$updateNotificationDots$12(PackageUserKey.this, this.f$1, (ItemInfo) obj);
            }
        }) {
            public final /* synthetic */ Predicate f$1;

            {
                this.f$1 = r2;
            }

            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return Workspace.this.lambda$updateNotificationDots$13$Workspace(this.f$1, itemInfo, view);
            }
        };
        mapOverItems(r3);
        Folder open = Folder.getOpen(this.mLauncher);
        if (open != null) {
            open.iterateOverItems(r3);
        }
    }

    static /* synthetic */ boolean lambda$updateNotificationDots$12(PackageUserKey packageUserKey, Predicate predicate, ItemInfo itemInfo) {
        return !packageUserKey.updateFromItemInfo(itemInfo) || predicate.test(packageUserKey);
    }

    public /* synthetic */ boolean lambda$updateNotificationDots$13$Workspace(Predicate predicate, ItemInfo itemInfo, View view) {
        if (!(itemInfo instanceof WorkspaceItemInfo) || !(view instanceof BubbleTextView)) {
            if (!(itemInfo instanceof FolderInfo) || !(view instanceof FolderIcon)) {
                return false;
            }
            FolderInfo folderInfo = (FolderInfo) itemInfo;
            if (!folderInfo.contents.stream().anyMatch(predicate)) {
                return false;
            }
            FolderDotInfo folderDotInfo = new FolderDotInfo();
            Iterator<WorkspaceItemInfo> it = folderInfo.contents.iterator();
            while (it.hasNext()) {
                folderDotInfo.addDotInfo(this.mLauncher.getDotInfoForItem(it.next()));
            }
            ((FolderIcon) view).setDotInfo(folderDotInfo);
            return false;
        } else if (!predicate.test(itemInfo)) {
            return false;
        } else {
            ((BubbleTextView) view).applyDotState(itemInfo, true);
            return false;
        }
    }

    public void persistRemoveItemsByMatcher(Predicate<ItemInfo> predicate, String str) {
        this.mLauncher.getModelWriter().deleteItemsFromDatabase(predicate, str);
        removeItemsByMatcher(predicate);
    }

    public void widgetsRestored(final ArrayList<LauncherAppWidgetInfo> arrayList) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo;
        if (!arrayList.isEmpty()) {
            DeferredWidgetRefresh deferredWidgetRefresh = new DeferredWidgetRefresh(arrayList, this.mLauncher.getAppWidgetHost());
            LauncherAppWidgetInfo launcherAppWidgetInfo = arrayList.get(0);
            WidgetManagerHelper widgetManagerHelper = new WidgetManagerHelper(getContext());
            if (launcherAppWidgetInfo.hasRestoreFlag(1)) {
                launcherAppWidgetProviderInfo = widgetManagerHelper.findProvider(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
            } else {
                launcherAppWidgetProviderInfo = widgetManagerHelper.getLauncherAppWidgetInfo(launcherAppWidgetInfo.appWidgetId);
            }
            if (launcherAppWidgetProviderInfo != null) {
                deferredWidgetRefresh.run();
            } else {
                mapOverItems(new LauncherBindableItemsContainer.ItemOperator() {
                    public boolean evaluate(ItemInfo itemInfo, View view) {
                        if (!(view instanceof PendingAppWidgetHostView) || !arrayList.contains(itemInfo)) {
                            return false;
                        }
                        ((LauncherAppWidgetInfo) itemInfo).installProgress = 100;
                        ((PendingAppWidgetHostView) view).applyState();
                        return false;
                    }
                });
            }
        }
    }

    public boolean isOverlayShown() {
        return this.mOverlayShown;
    }

    public void moveToDefaultScreen() {
        if (!workspaceInModalState() && getNextPage() != 0) {
            snapToPage(0);
        }
        View childAt = getChildAt(0);
        if (childAt != null) {
            childAt.requestFocus();
        }
    }

    public void setPivotToScaleWithSelf(View view) {
        view.setPivotY(((getPivotY() + ((float) getTop())) - ((float) view.getTop())) - view.getTranslationY());
        view.setPivotX(((getPivotX() + ((float) getLeft())) - ((float) view.getLeft())) - view.getTranslationX());
    }

    public int getExpectedHeight() {
        return (getMeasuredHeight() <= 0 || !this.mIsLayoutValid) ? this.mLauncher.getDeviceProfile().heightPx : getMeasuredHeight();
    }

    public int getExpectedWidth() {
        return (getMeasuredWidth() <= 0 || !this.mIsLayoutValid) ? this.mLauncher.getDeviceProfile().widthPx : getMeasuredWidth();
    }

    /* access modifiers changed from: protected */
    public boolean canAnnouncePageDescription() {
        return Float.compare(this.mOverlayTranslation, 0.0f) == 0;
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return getPageDescription(this.mNextPage != -1 ? this.mNextPage : this.mCurrentPage);
    }

    private String getPageDescription(int i) {
        int childCount = getChildCount();
        int indexOf = this.mScreenOrder.indexOf(WorkspaceLayoutManager.EXTRA_EMPTY_SCREEN_ID);
        if (indexOf >= 0 && childCount > 1) {
            if (i == indexOf) {
                return getContext().getString(R.string.workspace_new_page);
            }
            childCount--;
        }
        if (childCount == 0) {
            return getContext().getString(R.string.home_screen);
        }
        int panelCount = getPanelCount();
        return getContext().getString(R.string.workspace_scroll_format, new Object[]{Integer.valueOf((i / panelCount) + 1), Integer.valueOf((childCount / panelCount) + (childCount % panelCount))});
    }

    /* access modifiers changed from: protected */
    public boolean isSignificantMove(float f, int i) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        if (!deviceProfile.isTablet) {
            return super.isSignificantMove(f, i);
        }
        return f > ((float) deviceProfile.availableWidthPx) * SIGNIFICANT_MOVE_SCREEN_WIDTH_PERCENTAGE;
    }

    private class DeferredWidgetRefresh implements Runnable, LauncherAppWidgetHost.ProviderChangedListener {
        private final Handler mHandler;
        private final LauncherAppWidgetHost mHost;
        private final ArrayList<LauncherAppWidgetInfo> mInfos;
        private boolean mRefreshPending = true;

        DeferredWidgetRefresh(ArrayList<LauncherAppWidgetInfo> arrayList, LauncherAppWidgetHost launcherAppWidgetHost) {
            this.mInfos = arrayList;
            this.mHost = launcherAppWidgetHost;
            Handler handler = Workspace.this.mLauncher.mHandler;
            this.mHandler = handler;
            launcherAppWidgetHost.addProviderChangeListener(this);
            Message obtain = Message.obtain(handler, this);
            obtain.obj = DeferredWidgetRefresh.class;
            handler.sendMessageDelayed(obtain, 10000);
        }

        public void run() {
            this.mHost.removeProviderChangeListener(this);
            this.mHandler.removeCallbacks(this);
            if (this.mRefreshPending) {
                this.mRefreshPending = false;
                ArrayList arrayList = new ArrayList(this.mInfos.size());
                Workspace.this.mapOverItems(new LauncherBindableItemsContainer.ItemOperator(arrayList) {
                    public final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean evaluate(ItemInfo itemInfo, View view) {
                        return Workspace.DeferredWidgetRefresh.this.lambda$run$0$Workspace$DeferredWidgetRefresh(this.f$1, itemInfo, view);
                    }
                });
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ((PendingAppWidgetHostView) it.next()).reInflate();
                }
            }
        }

        public /* synthetic */ boolean lambda$run$0$Workspace$DeferredWidgetRefresh(ArrayList arrayList, ItemInfo itemInfo, View view) {
            if (!(view instanceof PendingAppWidgetHostView) || !this.mInfos.contains(itemInfo)) {
                return false;
            }
            arrayList.add((PendingAppWidgetHostView) view);
            return false;
        }

        public void notifyWidgetProvidersChanged() {
            run();
        }
    }

    private class StateTransitionListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
        private StateTransitionListener() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float unused = Workspace.this.mTransitionProgress = valueAnimator.getAnimatedFraction();
        }

        public void onAnimationStart(Animator animator) {
            Workspace.this.onStartStateTransition();
        }

        public void onAnimationEnd(Animator animator) {
            Workspace.this.onEndStateTransition();
        }
    }
}
