package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.views.ArrowTipView;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import com.android.launcher3.widget.util.WidgetSizes;
import java.util.ArrayList;
import java.util.List;

public class AppWidgetResizeFrame extends AbstractFloatingView implements View.OnKeyListener {
    private static final float DIMMED_HANDLE_ALPHA = 0.0f;
    private static final int HANDLE_COUNT = 4;
    private static final int INDEX_BOTTOM = 3;
    private static final int INDEX_LEFT = 0;
    private static final int INDEX_RIGHT = 2;
    private static final int INDEX_TOP = 1;
    private static final String KEY_RECONFIGURABLE_WIDGET_EDUCATION_TIP_SEEN = "launcher.reconfigurable_widget_education_tip_seen";
    private static final float MIN_OPACITY_FOR_CELL_LAYOUT_DURING_INVALID_RESIZE = 0.5f;
    private static final float RESIZE_THRESHOLD = 0.66f;
    private static final int SNAP_DURATION = 150;
    private static final Rect sTmpRect = new Rect();
    private static final Rect sTmpRect2 = new Rect();
    private final InstanceId logInstanceId;
    private final int mBackgroundPadding;
    private final IntRange mBaselineX;
    private final IntRange mBaselineY;
    private boolean mBottomBorderActive;
    private int mBottomTouchRegionAdjustment;
    private CellLayout mCellLayout;
    private int mDeltaX;
    private int mDeltaXAddOn;
    private final IntRange mDeltaXRange;
    private int mDeltaY;
    private int mDeltaYAddOn;
    private final IntRange mDeltaYRange;
    private final int[] mDirectionVector;
    private final float mDragAcrossTwoPanelOpacityMargin;
    private final View[] mDragHandles;
    private DragLayer mDragLayer;
    private final ViewGroupFocusHelper mDragLayerRelativeCoordinateHelper;
    private final FirstFrameAnimatorHelper mFirstFrameAnimatorHelper;
    private boolean mHorizontalResizeActive;
    private final int[] mLastDirectionVector;
    private final Launcher mLauncher;
    private boolean mLeftBorderActive;
    private int mMaxHSpan;
    private int mMaxVSpan;
    private int mMinHSpan;
    private int mMinVSpan;
    private ImageButton mReconfigureButton;
    private boolean mRightBorderActive;
    private int mRunningHInc;
    private int mRunningVInc;
    private final DragViewStateAnnouncer mStateAnnouncer;
    private final List<Rect> mSystemGestureExclusionRects;
    private final IntRange mTempRange1;
    private final IntRange mTempRange2;
    private boolean mTopBorderActive;
    private int mTopTouchRegionAdjustment;
    private final int mTouchTargetWidth;
    private boolean mVerticalResizeActive;
    private Rect mWidgetPadding;
    private LauncherAppWidgetHostView mWidgetView;
    private final View.OnAttachStateChangeListener mWidgetViewAttachStateChangeListener;
    private int mXDown;
    private int mYDown;

    public static boolean shouldConsume(int i) {
        return i == 21 || i == 22 || i == 19 || i == 20 || i == 122 || i == 123 || i == 92 || i == 93;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 8) != 0;
    }

    public AppWidgetResizeFrame(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppWidgetResizeFrame(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppWidgetResizeFrame(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDragHandles = new View[4];
        this.mSystemGestureExclusionRects = new ArrayList(4);
        this.mWidgetViewAttachStateChangeListener = new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View view) {
            }

            public void onViewDetachedFromWindow(View view) {
                AppWidgetResizeFrame.this.close(false);
            }
        };
        this.mDirectionVector = new int[2];
        this.mLastDirectionVector = new int[2];
        this.mTempRange1 = new IntRange();
        this.mTempRange2 = new IntRange();
        this.mDeltaXRange = new IntRange();
        this.mBaselineX = new IntRange();
        this.mDeltaYRange = new IntRange();
        this.mBaselineY = new IntRange();
        this.logInstanceId = new InstanceIdSequence().newInstanceId();
        this.mTopTouchRegionAdjustment = 0;
        this.mBottomTouchRegionAdjustment = 0;
        this.mLauncher = Launcher.getLauncher(context);
        this.mStateAnnouncer = DragViewStateAnnouncer.createFor(this);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.resize_frame_background_padding);
        this.mBackgroundPadding = dimensionPixelSize;
        this.mTouchTargetWidth = dimensionPixelSize * 2;
        this.mFirstFrameAnimatorHelper = new FirstFrameAnimatorHelper(this);
        for (int i2 = 0; i2 < 4; i2++) {
            this.mSystemGestureExclusionRects.add(new Rect());
        }
        this.mDragAcrossTwoPanelOpacityMargin = (float) this.mLauncher.getResources().getDimensionPixelSize(R.dimen.resize_frame_invalid_drag_across_two_panel_opacity_margin);
        this.mDragLayerRelativeCoordinateHelper = new ViewGroupFocusHelper(this.mLauncher.getDragLayer());
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDragHandles[0] = findViewById(R.id.widget_resize_left_handle);
        this.mDragHandles[1] = findViewById(R.id.widget_resize_top_handle);
        this.mDragHandles[2] = findViewById(R.id.widget_resize_right_handle);
        this.mDragHandles[3] = findViewById(R.id.widget_resize_bottom_handle);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (Utilities.ATLEAST_Q) {
            for (int i5 = 0; i5 < 4; i5++) {
                View view = this.mDragHandles[i5];
                this.mSystemGestureExclusionRects.get(i5).set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            }
            setSystemGestureExclusionRects(this.mSystemGestureExclusionRects);
        }
    }

    public static void showForWidget(LauncherAppWidgetHostView launcherAppWidgetHostView, CellLayout cellLayout) {
        Launcher launcher = Launcher.getLauncher(cellLayout.getContext());
        AbstractFloatingView.closeAllOpenViews(launcher);
        DragLayer dragLayer = launcher.getDragLayer();
        AppWidgetResizeFrame appWidgetResizeFrame = (AppWidgetResizeFrame) launcher.getLayoutInflater().inflate(R.layout.app_widget_resize_frame, dragLayer, false);
        if (launcherAppWidgetHostView.hasEnforcedCornerRadius()) {
            float enforcedCornerRadius = launcherAppWidgetHostView.getEnforcedCornerRadius();
            Drawable drawable = ((ImageView) appWidgetResizeFrame.findViewById(R.id.widget_resize_frame)).getDrawable();
            if (drawable instanceof GradientDrawable) {
                ((GradientDrawable) drawable.mutate()).setCornerRadius(enforcedCornerRadius);
            }
        }
        appWidgetResizeFrame.setupForWidget(launcherAppWidgetHostView, cellLayout, dragLayer);
        ((BaseDragLayer.LayoutParams) appWidgetResizeFrame.getLayoutParams()).customPosition = true;
        dragLayer.addView(appWidgetResizeFrame);
        appWidgetResizeFrame.mIsOpen = true;
        appWidgetResizeFrame.post(new Runnable() {
            public final void run() {
                AppWidgetResizeFrame.this.snapToWidget(false);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0051, code lost:
        r7 = r5.mMaxVSpan;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setupForWidget(com.android.launcher3.widget.LauncherAppWidgetHostView r6, com.android.launcher3.CellLayout r7, com.android.launcher3.dragndrop.DragLayer r8) {
        /*
            r5 = this;
            r5.mCellLayout = r7
            com.android.launcher3.widget.LauncherAppWidgetHostView r0 = r5.mWidgetView
            if (r0 == 0) goto L_0x000b
            android.view.View$OnAttachStateChangeListener r1 = r5.mWidgetViewAttachStateChangeListener
            r0.removeOnAttachStateChangeListener(r1)
        L_0x000b:
            r5.mWidgetView = r6
            android.view.View$OnAttachStateChangeListener r0 = r5.mWidgetViewAttachStateChangeListener
            r6.addOnAttachStateChangeListener(r0)
            android.appwidget.AppWidgetProviderInfo r0 = r6.getAppWidgetInfo()
            com.android.launcher3.widget.LauncherAppWidgetProviderInfo r0 = (com.android.launcher3.widget.LauncherAppWidgetProviderInfo) r0
            r5.mDragLayer = r8
            int r8 = r0.minSpanX
            r5.mMinHSpan = r8
            int r8 = r0.minSpanY
            r5.mMinVSpan = r8
            int r8 = r0.maxSpanX
            r5.mMaxHSpan = r8
            int r8 = r0.maxSpanY
            r5.mMaxVSpan = r8
            android.content.Context r8 = r5.getContext()
            android.appwidget.AppWidgetProviderInfo r6 = r6.getAppWidgetInfo()
            android.content.ComponentName r6 = r6.provider
            r1 = 0
            android.graphics.Rect r6 = android.appwidget.AppWidgetHostView.getDefaultPaddingForWidget(r8, r6, r1)
            r5.mWidgetPadding = r6
            android.content.Context r6 = r7.getContext()
            com.android.launcher3.InvariantDeviceProfile r6 = com.android.launcher3.LauncherAppState.getIDP(r6)
            int r7 = r0.resizeMode
            r8 = 2
            r7 = r7 & r8
            r1 = 0
            r2 = 1
            if (r7 == 0) goto L_0x005b
            int r7 = r5.mMinVSpan
            int r3 = r6.numRows
            if (r7 >= r3) goto L_0x005b
            int r7 = r5.mMaxVSpan
            if (r7 <= r2) goto L_0x005b
            int r3 = r5.mMinVSpan
            if (r3 >= r7) goto L_0x005b
            r7 = r2
            goto L_0x005c
        L_0x005b:
            r7 = r1
        L_0x005c:
            r5.mVerticalResizeActive = r7
            r3 = 8
            if (r7 != 0) goto L_0x0071
            android.view.View[] r7 = r5.mDragHandles
            r7 = r7[r2]
            r7.setVisibility(r3)
            android.view.View[] r7 = r5.mDragHandles
            r4 = 3
            r7 = r7[r4]
            r7.setVisibility(r3)
        L_0x0071:
            int r7 = r0.resizeMode
            r7 = r7 & r2
            if (r7 == 0) goto L_0x0086
            int r7 = r5.mMinHSpan
            int r6 = r6.numColumns
            if (r7 >= r6) goto L_0x0086
            int r6 = r5.mMaxHSpan
            if (r6 <= r2) goto L_0x0086
            int r7 = r5.mMinHSpan
            if (r7 >= r6) goto L_0x0086
            r6 = r2
            goto L_0x0087
        L_0x0086:
            r6 = r1
        L_0x0087:
            r5.mHorizontalResizeActive = r6
            if (r6 != 0) goto L_0x0099
            android.view.View[] r6 = r5.mDragHandles
            r6 = r6[r1]
            r6.setVisibility(r3)
            android.view.View[] r6 = r5.mDragHandles
            r6 = r6[r8]
            r6.setVisibility(r3)
        L_0x0099:
            r6 = 2131296974(0x7f0902ce, float:1.821188E38)
            android.view.View r6 = r5.findViewById(r6)
            android.widget.ImageButton r6 = (android.widget.ImageButton) r6
            r5.mReconfigureButton = r6
            boolean r6 = r0.isReconfigurable()
            if (r6 == 0) goto L_0x00c7
            android.widget.ImageButton r6 = r5.mReconfigureButton
            r6.setVisibility(r1)
            android.widget.ImageButton r6 = r5.mReconfigureButton
            com.android.launcher3.-$$Lambda$AppWidgetResizeFrame$aTkmXP32FBIvhuRGUS97zx7EVRI r7 = new com.android.launcher3.-$$Lambda$AppWidgetResizeFrame$aTkmXP32FBIvhuRGUS97zx7EVRI
            r7.<init>()
            r6.setOnClickListener(r7)
            boolean r6 = r5.hasSeenReconfigurableWidgetEducationTip()
            if (r6 != 0) goto L_0x00c7
            com.android.launcher3.-$$Lambda$AppWidgetResizeFrame$4JMUCmaw-6LGgYfU4JRBgIVkLRs r6 = new com.android.launcher3.-$$Lambda$AppWidgetResizeFrame$4JMUCmaw-6LGgYfU4JRBgIVkLRs
            r6.<init>()
            r5.post(r6)
        L_0x00c7:
            com.android.launcher3.widget.LauncherAppWidgetHostView r6 = r5.mWidgetView
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r6 = (com.android.launcher3.CellLayout.LayoutParams) r6
            com.android.launcher3.widget.LauncherAppWidgetHostView r7 = r5.mWidgetView
            java.lang.Object r7 = r7.getTag()
            com.android.launcher3.model.data.ItemInfo r7 = (com.android.launcher3.model.data.ItemInfo) r7
            int r8 = r7.cellX
            r6.tmpCellX = r8
            r6.cellX = r8
            int r8 = r7.cellY
            r6.tmpCellY = r8
            r6.cellY = r8
            int r8 = r7.spanX
            r6.cellHSpan = r8
            int r8 = r7.spanY
            r6.cellVSpan = r8
            r6.isLockedToGrid = r2
            com.android.launcher3.CellLayout r6 = r5.mCellLayout
            com.android.launcher3.widget.LauncherAppWidgetHostView r8 = r5.mWidgetView
            r6.markCellsAsUnoccupiedForView(r8)
            com.android.launcher3.Launcher r6 = r5.mLauncher
            com.android.launcher3.logging.StatsLogManager r6 = r6.getStatsLogManager()
            com.android.launcher3.logging.StatsLogManager$StatsLogger r6 = r6.logger()
            com.android.launcher3.logging.InstanceId r8 = r5.logInstanceId
            com.android.launcher3.logging.StatsLogManager$StatsLogger r6 = r6.withInstanceId(r8)
            com.android.launcher3.logging.StatsLogManager$StatsLogger r6 = r6.withItemInfo(r7)
            com.android.launcher3.logging.StatsLogManager$LauncherEvent r7 = com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_WIDGET_RESIZE_STARTED
            r6.log(r7)
            r5.setOnKeyListener(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AppWidgetResizeFrame.setupForWidget(com.android.launcher3.widget.LauncherAppWidgetHostView, com.android.launcher3.CellLayout, com.android.launcher3.dragndrop.DragLayer):void");
    }

    public /* synthetic */ void lambda$setupForWidget$1$AppWidgetResizeFrame(View view) {
        this.mLauncher.setWaitingForResult(PendingRequestArgs.forWidgetInfo(this.mWidgetView.getAppWidgetId(), (WidgetAddFlowHandler) null, (ItemInfo) this.mWidgetView.getTag()));
        this.mLauncher.getAppWidgetHost().startConfigActivity(this.mLauncher, this.mWidgetView.getAppWidgetId(), 13);
    }

    public /* synthetic */ void lambda$setupForWidget$2$AppWidgetResizeFrame() {
        if (showReconfigurableWidgetEducationTip() != null) {
            this.mLauncher.getSharedPrefs().edit().putBoolean(KEY_RECONFIGURABLE_WIDGET_EDUCATION_TIP_SEEN, true).apply();
        }
    }

    public boolean beginResizeIfPointInRegion(int i, int i2) {
        this.mLeftBorderActive = i < this.mTouchTargetWidth && this.mHorizontalResizeActive;
        int width = getWidth();
        int i3 = this.mTouchTargetWidth;
        this.mRightBorderActive = i > width - i3 && this.mHorizontalResizeActive;
        this.mTopBorderActive = i2 < i3 + this.mTopTouchRegionAdjustment && this.mVerticalResizeActive;
        boolean z = i2 > (getHeight() - this.mTouchTargetWidth) + this.mBottomTouchRegionAdjustment && this.mVerticalResizeActive;
        this.mBottomBorderActive = z;
        boolean z2 = this.mLeftBorderActive;
        boolean z3 = z2 || this.mRightBorderActive || this.mTopBorderActive || z;
        if (z3) {
            float f = 1.0f;
            this.mDragHandles[0].setAlpha(z2 ? 1.0f : 0.0f);
            this.mDragHandles[2].setAlpha(this.mRightBorderActive ? 1.0f : 0.0f);
            this.mDragHandles[1].setAlpha(this.mTopBorderActive ? 1.0f : 0.0f);
            View view = this.mDragHandles[3];
            if (!this.mBottomBorderActive) {
                f = 0.0f;
            }
            view.setAlpha(f);
        }
        if (this.mLeftBorderActive) {
            this.mDeltaXRange.set(-getLeft(), getWidth() - (this.mTouchTargetWidth * 2));
        } else if (this.mRightBorderActive) {
            this.mDeltaXRange.set((this.mTouchTargetWidth * 2) - getWidth(), this.mDragLayer.getWidth() - getRight());
        } else {
            this.mDeltaXRange.set(0, 0);
        }
        this.mBaselineX.set(getLeft(), getRight());
        if (this.mTopBorderActive) {
            this.mDeltaYRange.set(-getTop(), getHeight() - (this.mTouchTargetWidth * 2));
        } else if (this.mBottomBorderActive) {
            this.mDeltaYRange.set((this.mTouchTargetWidth * 2) - getHeight(), this.mDragLayer.getHeight() - getBottom());
        } else {
            this.mDeltaYRange.set(0, 0);
        }
        this.mBaselineY.set(getTop(), getBottom());
        return z3;
    }

    public void visualizeResizeForDelta(int i, int i2) {
        Workspace workspace;
        CellLayout screenPair;
        float f;
        float f2;
        float f3;
        this.mDeltaX = this.mDeltaXRange.clamp(i);
        this.mDeltaY = this.mDeltaYRange.clamp(i2);
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        int clamp = this.mDeltaXRange.clamp(i);
        this.mDeltaX = clamp;
        this.mBaselineX.applyDelta(this.mLeftBorderActive, this.mRightBorderActive, clamp, this.mTempRange1);
        layoutParams.x = this.mTempRange1.start;
        layoutParams.width = this.mTempRange1.size();
        int clamp2 = this.mDeltaYRange.clamp(i2);
        this.mDeltaY = clamp2;
        this.mBaselineY.applyDelta(this.mTopBorderActive, this.mBottomBorderActive, clamp2, this.mTempRange1);
        layoutParams.y = this.mTempRange1.start;
        layoutParams.height = this.mTempRange1.size();
        resizeWidgetIfNeeded(false);
        Rect rect = sTmpRect;
        getSnappedRectRelativeToDragLayer(rect);
        if (this.mLeftBorderActive) {
            layoutParams.width = (rect.width() + rect.left) - layoutParams.x;
        }
        if (this.mTopBorderActive) {
            layoutParams.height = (rect.height() + rect.top) - layoutParams.y;
        }
        if (this.mRightBorderActive) {
            layoutParams.x = rect.left;
        }
        if (this.mBottomBorderActive) {
            layoutParams.y = rect.top;
        }
        if ((this.mCellLayout.getParent() instanceof Workspace) && (screenPair = workspace.getScreenPair(this.mCellLayout)) != null) {
            this.mDragLayerRelativeCoordinateHelper.viewToRect((View) this.mCellLayout, rect);
            Rect rect2 = sTmpRect2;
            findViewById(R.id.widget_resize_frame).getGlobalVisibleRect(rect2);
            if ((workspace = (Workspace) this.mCellLayout.getParent()).indexOfChild(screenPair) < workspace.indexOfChild(this.mCellLayout) && this.mDeltaX < 0 && rect2.left < rect.left) {
                f2 = this.mDragAcrossTwoPanelOpacityMargin;
                f3 = ((float) this.mDeltaX) + f2;
            } else if (workspace.indexOfChild(screenPair) <= workspace.indexOfChild(this.mCellLayout) || this.mDeltaX <= 0 || rect2.right <= rect.right) {
                f = 1.0f;
                updateInvalidResizeEffect(this.mCellLayout, screenPair, Math.max(0.5f, f), Math.min(1.0f, 1.0f - f));
            } else {
                f2 = this.mDragAcrossTwoPanelOpacityMargin;
                f3 = f2 - ((float) this.mDeltaX);
            }
            f = f3 / f2;
            updateInvalidResizeEffect(this.mCellLayout, screenPair, Math.max(0.5f, f), Math.min(1.0f, 1.0f - f));
        }
        requestLayout();
    }

    private static int getSpanIncrement(float f) {
        if (Math.abs(f) > RESIZE_THRESHOLD) {
            return Math.round(f);
        }
        return 0;
    }

    private void resizeWidgetIfNeeded(boolean z) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        float cellHeight = (float) (this.mCellLayout.getCellHeight() + deviceProfile.cellLayoutBorderSpacePx.y);
        int spanIncrement = getSpanIncrement((((float) (this.mDeltaX + this.mDeltaXAddOn)) / ((float) (this.mCellLayout.getCellWidth() + deviceProfile.cellLayoutBorderSpacePx.x))) - ((float) this.mRunningHInc));
        int spanIncrement2 = getSpanIncrement((((float) (this.mDeltaY + this.mDeltaYAddOn)) / cellHeight) - ((float) this.mRunningVInc));
        if (z || spanIncrement != 0 || spanIncrement2 != 0) {
            int[] iArr = this.mDirectionVector;
            iArr[0] = 0;
            iArr[1] = 0;
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) this.mWidgetView.getLayoutParams();
            int i = layoutParams.cellHSpan;
            int i2 = layoutParams.cellVSpan;
            int i3 = layoutParams.useTmpCoords ? layoutParams.tmpCellX : layoutParams.cellX;
            int i4 = layoutParams.useTmpCoords ? layoutParams.tmpCellY : layoutParams.cellY;
            this.mTempRange1.set(i3, i + i3);
            int applyDeltaAndBound = this.mTempRange1.applyDeltaAndBound(this.mLeftBorderActive, this.mRightBorderActive, spanIncrement, this.mMinHSpan, this.mMaxHSpan, this.mCellLayout.getCountX(), this.mTempRange2);
            int i5 = this.mTempRange2.start;
            int size = this.mTempRange2.size();
            int i6 = -1;
            if (applyDeltaAndBound != 0) {
                this.mDirectionVector[0] = this.mLeftBorderActive ? -1 : 1;
            }
            this.mTempRange1.set(i4, i2 + i4);
            int applyDeltaAndBound2 = this.mTempRange1.applyDeltaAndBound(this.mTopBorderActive, this.mBottomBorderActive, spanIncrement2, this.mMinVSpan, this.mMaxVSpan, this.mCellLayout.getCountY(), this.mTempRange2);
            int i7 = this.mTempRange2.start;
            int size2 = this.mTempRange2.size();
            if (applyDeltaAndBound2 != 0) {
                int[] iArr2 = this.mDirectionVector;
                if (!this.mTopBorderActive) {
                    i6 = 1;
                }
                iArr2[1] = i6;
            }
            if (z || applyDeltaAndBound2 != 0 || applyDeltaAndBound != 0) {
                if (z) {
                    int[] iArr3 = this.mDirectionVector;
                    int[] iArr4 = this.mLastDirectionVector;
                    iArr3[0] = iArr4[0];
                    iArr3[1] = iArr4[1];
                } else {
                    int[] iArr5 = this.mLastDirectionVector;
                    int[] iArr6 = this.mDirectionVector;
                    iArr5[0] = iArr6[0];
                    iArr5[1] = iArr6[1];
                }
                CellLayout.LayoutParams layoutParams2 = layoutParams;
                int i8 = size;
                int i9 = i5;
                if (this.mCellLayout.createAreaForResize(i5, i7, size, size2, this.mWidgetView, this.mDirectionVector, z)) {
                    if (!(this.mStateAnnouncer == null || (layoutParams2.cellHSpan == i8 && layoutParams2.cellVSpan == size2))) {
                        this.mStateAnnouncer.announce(this.mLauncher.getString(R.string.widget_resized, new Object[]{Integer.valueOf(i8), Integer.valueOf(size2)}));
                    }
                    layoutParams2.tmpCellX = i9;
                    layoutParams2.tmpCellY = i7;
                    layoutParams2.cellHSpan = i8;
                    layoutParams2.cellVSpan = size2;
                    this.mRunningVInc += applyDeltaAndBound2;
                    this.mRunningHInc += applyDeltaAndBound;
                    if (!z) {
                        WidgetSizes.updateWidgetSizeRanges(this.mWidgetView, this.mLauncher, i8, size2);
                    }
                }
                this.mWidgetView.requestLayout();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resizeWidgetIfNeeded(true);
        this.mLauncher.getStatsLogManager().logger().withInstanceId(this.logInstanceId).withItemInfo((ItemInfo) this.mWidgetView.getTag()).log(StatsLogManager.LauncherEvent.LAUNCHER_WIDGET_RESIZE_COMPLETED);
    }

    private void onTouchUp() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int cellWidth = this.mCellLayout.getCellWidth() + deviceProfile.cellLayoutBorderSpacePx.x;
        int cellHeight = this.mCellLayout.getCellHeight() + deviceProfile.cellLayoutBorderSpacePx.y;
        this.mDeltaXAddOn = this.mRunningHInc * cellWidth;
        this.mDeltaYAddOn = this.mRunningVInc * cellHeight;
        this.mDeltaX = 0;
        this.mDeltaY = 0;
        post(new Runnable() {
            public final void run() {
                AppWidgetResizeFrame.this.lambda$onTouchUp$3$AppWidgetResizeFrame();
            }
        });
    }

    public /* synthetic */ void lambda$onTouchUp$3$AppWidgetResizeFrame() {
        snapToWidget(true);
    }

    private void getSnappedRectRelativeToDragLayer(Rect rect) {
        float scaleToFit = this.mWidgetView.getScaleToFit();
        this.mDragLayer.getViewRectRelativeToSelf(this.mWidgetView, rect);
        int width = (this.mBackgroundPadding * 2) + ((int) (((float) ((rect.width() - this.mWidgetPadding.left) - this.mWidgetPadding.right)) * scaleToFit));
        int height = (this.mBackgroundPadding * 2) + ((int) (((float) ((rect.height() - this.mWidgetPadding.top) - this.mWidgetPadding.bottom)) * scaleToFit));
        rect.left = (int) (((float) (rect.left - this.mBackgroundPadding)) + (((float) this.mWidgetPadding.left) * scaleToFit));
        rect.top = (int) (((float) (rect.top - this.mBackgroundPadding)) + (scaleToFit * ((float) this.mWidgetPadding.top)));
        rect.right = rect.left + width;
        rect.bottom = rect.top + height;
    }

    /* access modifiers changed from: private */
    public void snapToWidget(boolean z) {
        Rect rect = sTmpRect;
        getSnappedRectRelativeToDragLayer(rect);
        int width = rect.width();
        int height = rect.height();
        int i = rect.left;
        int i2 = rect.top;
        if (i2 < 0) {
            this.mTopTouchRegionAdjustment = -i2;
        } else {
            this.mTopTouchRegionAdjustment = 0;
        }
        int i3 = i2 + height;
        if (i3 > this.mDragLayer.getHeight()) {
            this.mBottomTouchRegionAdjustment = -(i3 - this.mDragLayer.getHeight());
        } else {
            this.mBottomTouchRegionAdjustment = 0;
        }
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        CellLayout screenPair = this.mCellLayout.getParent() instanceof Workspace ? ((Workspace) this.mCellLayout.getParent()).getScreenPair(this.mCellLayout) : null;
        if (!z) {
            layoutParams.width = width;
            layoutParams.height = height;
            layoutParams.x = i;
            layoutParams.y = i2;
            for (int i4 = 0; i4 < 4; i4++) {
                this.mDragHandles[i4].setAlpha(1.0f);
            }
            if (screenPair != null) {
                updateInvalidResizeEffect(this.mCellLayout, screenPair, 1.0f, 0.0f);
            }
            requestLayout();
        } else {
            ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(layoutParams, new PropertyValuesHolder[]{PropertyValuesHolder.ofInt(LauncherAnimUtils.LAYOUT_WIDTH, new int[]{layoutParams.width, width}), PropertyValuesHolder.ofInt(LauncherAnimUtils.LAYOUT_HEIGHT, new int[]{layoutParams.height, height}), PropertyValuesHolder.ofInt(BaseDragLayer.LAYOUT_X, new int[]{layoutParams.x, i}), PropertyValuesHolder.ofInt(BaseDragLayer.LAYOUT_Y, new int[]{layoutParams.y, i2})});
            ((ObjectAnimator) this.mFirstFrameAnimatorHelper.addTo(ofPropertyValuesHolder)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AppWidgetResizeFrame.this.lambda$snapToWidget$4$AppWidgetResizeFrame(valueAnimator);
                }
            });
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(ofPropertyValuesHolder);
            for (int i5 = 0; i5 < 4; i5++) {
                animatorSet.play(this.mFirstFrameAnimatorHelper.addTo(ObjectAnimator.ofFloat(this.mDragHandles[i5], ALPHA, new float[]{1.0f})));
            }
            if (screenPair != null) {
                updateInvalidResizeEffect(this.mCellLayout, screenPair, 1.0f, 0.0f, animatorSet);
            }
            animatorSet.setDuration(150);
            animatorSet.start();
        }
        setFocusableInTouchMode(true);
        requestFocus();
    }

    public /* synthetic */ void lambda$snapToWidget$4$AppWidgetResizeFrame(ValueAnimator valueAnimator) {
        requestLayout();
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (!shouldConsume(i)) {
            return false;
        }
        close(false);
        this.mWidgetView.requestFocus();
        return true;
    }

    private boolean handleTouchDown(MotionEvent motionEvent) {
        Rect rect = new Rect();
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        getHitRect(rect);
        if (!rect.contains(x, y) || !beginResizeIfPointInRegion(x - getLeft(), y - getTop())) {
            return false;
        }
        this.mXDown = x;
        this.mYDown = y;
        return true;
    }

    private boolean isTouchOnReconfigureButton(MotionEvent motionEvent) {
        int x = ((int) motionEvent.getX()) - getLeft();
        int y = ((int) motionEvent.getY()) - getTop();
        ImageButton imageButton = this.mReconfigureButton;
        Rect rect = sTmpRect;
        imageButton.getHitRect(rect);
        return rect.contains(x, y);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0017, code lost:
        if (r0 != 3) goto L_0x0035;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onControllerTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            int r0 = r5.getAction()
            float r1 = r5.getX()
            int r1 = (int) r1
            float r2 = r5.getY()
            int r2 = (int) r2
            if (r0 == 0) goto L_0x0036
            r5 = 1
            if (r0 == r5) goto L_0x0024
            r3 = 2
            if (r0 == r3) goto L_0x001a
            r3 = 3
            if (r0 == r3) goto L_0x0024
            goto L_0x0035
        L_0x001a:
            int r0 = r4.mXDown
            int r1 = r1 - r0
            int r0 = r4.mYDown
            int r2 = r2 - r0
            r4.visualizeResizeForDelta(r1, r2)
            goto L_0x0035
        L_0x0024:
            int r0 = r4.mXDown
            int r1 = r1 - r0
            int r0 = r4.mYDown
            int r2 = r2 - r0
            r4.visualizeResizeForDelta(r1, r2)
            r4.onTouchUp()
            r0 = 0
            r4.mYDown = r0
            r4.mXDown = r0
        L_0x0035:
            return r5
        L_0x0036:
            boolean r5 = r4.handleTouchDown(r5)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AppWidgetResizeFrame.onControllerTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && handleTouchDown(motionEvent)) {
            return true;
        }
        if (isTouchOnReconfigureButton(motionEvent)) {
            return false;
        }
        close(false);
        return false;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        this.mDragLayer.removeView(this);
        LauncherAppWidgetHostView launcherAppWidgetHostView = this.mWidgetView;
        if (launcherAppWidgetHostView != null) {
            launcherAppWidgetHostView.removeOnAttachStateChangeListener(this.mWidgetViewAttachStateChangeListener);
        }
    }

    private void updateInvalidResizeEffect(CellLayout cellLayout, CellLayout cellLayout2, float f, float f2) {
        updateInvalidResizeEffect(cellLayout, cellLayout2, f, f2, (AnimatorSet) null);
    }

    private void updateInvalidResizeEffect(final CellLayout cellLayout, final CellLayout cellLayout2, float f, float f2, AnimatorSet animatorSet) {
        int childCount = cellLayout2.getChildCount();
        final boolean z = false;
        for (int i = 0; i < childCount; i++) {
            View childAt = cellLayout2.getChildAt(i);
            if (animatorSet != null) {
                animatorSet.play(this.mFirstFrameAnimatorHelper.addTo(ObjectAnimator.ofFloat(childAt, ALPHA, new float[]{f})));
            } else {
                childAt.setAlpha(f);
            }
        }
        if (animatorSet != null) {
            animatorSet.play(this.mFirstFrameAnimatorHelper.addTo(ObjectAnimator.ofFloat(cellLayout, CellLayout.SPRING_LOADED_PROGRESS, new float[]{f2})));
            animatorSet.play(this.mFirstFrameAnimatorHelper.addTo(ObjectAnimator.ofFloat(cellLayout2, CellLayout.SPRING_LOADED_PROGRESS, new float[]{f2})));
        } else {
            cellLayout.setSpringLoadedProgress(f2);
            cellLayout2.setSpringLoadedProgress(f2);
        }
        if (f2 > 0.0f) {
            z = true;
        }
        if (animatorSet != null) {
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    cellLayout.setIsDragOverlapping(z);
                    cellLayout2.setIsDragOverlapping(z);
                }
            });
            return;
        }
        cellLayout.setIsDragOverlapping(z);
        cellLayout2.setIsDragOverlapping(z);
    }

    private static class IntRange {
        public int end;
        public int start;

        private IntRange() {
        }

        public int clamp(int i) {
            return Utilities.boundToRange(i, this.start, this.end);
        }

        public void set(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public int size() {
            return this.end - this.start;
        }

        public void applyDelta(boolean z, boolean z2, int i, IntRange intRange) {
            intRange.start = z ? this.start + i : this.start;
            int i2 = this.end;
            if (z2) {
                i2 += i;
            }
            intRange.end = i2;
        }

        public int applyDeltaAndBound(boolean z, boolean z2, int i, int i2, int i3, int i4, IntRange intRange) {
            int i5;
            int i6;
            applyDelta(z, z2, i, intRange);
            if (intRange.start < 0) {
                intRange.start = 0;
            }
            if (intRange.end > i4) {
                intRange.end = i4;
            }
            if (intRange.size() < i2) {
                if (z) {
                    intRange.start = intRange.end - i2;
                } else if (z2) {
                    intRange.end = intRange.start + i2;
                }
            }
            if (intRange.size() > i3) {
                if (z) {
                    intRange.start = intRange.end - i3;
                } else if (z2) {
                    intRange.end = intRange.start + i3;
                }
            }
            if (z2) {
                i6 = intRange.size();
                i5 = size();
            } else {
                i6 = size();
                i5 = intRange.size();
            }
            return i6 - i5;
        }
    }

    private ArrowTipView showReconfigurableWidgetEducationTip() {
        Rect rect = new Rect();
        if (!this.mReconfigureButton.getGlobalVisibleRect(rect)) {
            return null;
        }
        return new ArrowTipView(this.mLauncher, true).showAroundRect(getContext().getString(R.string.reconfigurable_widget_education_tip), rect.left + (this.mReconfigureButton.getWidth() / 2), rect, this.mLauncher.getResources().getDimensionPixelSize(R.dimen.widget_reconfigure_tip_top_margin));
    }

    private boolean hasSeenReconfigurableWidgetEducationTip() {
        if (this.mLauncher.getSharedPrefs().getBoolean(KEY_RECONFIGURABLE_WIDGET_EDUCATION_TIP_SEEN, false) || Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return true;
        }
        return false;
    }
}
