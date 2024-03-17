package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.android.launcher3.DropTarget;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.util.CellAndSpan;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.ParcelableSparseArray;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

public class CellLayout extends ViewGroup {
    /* access modifiers changed from: private */
    public static final Property<ReorderPreviewAnimation, Float> ANIMATION_PROGRESS = new Property<ReorderPreviewAnimation, Float>(Float.TYPE, "animationProgress") {
        public Float get(ReorderPreviewAnimation reorderPreviewAnimation) {
            return Float.valueOf(reorderPreviewAnimation.animationProgress);
        }

        public void set(ReorderPreviewAnimation reorderPreviewAnimation, Float f) {
            reorderPreviewAnimation.setAnimationProgress(f.floatValue());
        }
    };
    private static final int[] BACKGROUND_STATE_ACTIVE = {16842914};
    private static final int[] BACKGROUND_STATE_DEFAULT = EMPTY_STATE_SET;
    private static final boolean DEBUG_VISUALIZE_OCCUPIED = false;
    private static final boolean DESTRUCTIVE_REORDER = false;
    public static final int FOLDER = 2;
    public static final int HOTSEAT = 1;
    private static final int INVALID_DIRECTION = -100;
    private static final boolean LOGD = false;
    public static final int MODE_ACCEPT_DROP = 4;
    public static final int MODE_DRAG_OVER = 1;
    public static final int MODE_ON_DROP = 2;
    public static final int MODE_ON_DROP_EXTERNAL = 3;
    public static final int MODE_SHOW_REORDER_HINT = 0;
    private static final int REORDER_ANIMATION_DURATION = 150;
    private static final float REORDER_PREVIEW_MAGNITUDE = 0.12f;
    public static final FloatProperty<CellLayout> SPRING_LOADED_PROGRESS = new FloatProperty<CellLayout>("spring_loaded_progress") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(CellLayout cellLayout) {
            return Float.valueOf(cellLayout.getSpringLoadedProgress());
        }

        public void setValue(CellLayout cellLayout, float f) {
            cellLayout.setSpringLoadedProgress(f);
        }
    };
    private static final String TAG = "CellLayout";
    public static final int WORKSPACE = 0;
    private static final Paint sPaint = new Paint();
    protected final ActivityContext mActivity;
    private final Drawable mBackground;
    @ViewDebug.ExportedProperty(category = "launcher")
    private Point mBorderSpace;
    @ViewDebug.ExportedProperty(category = "launcher")
    int mCellHeight;
    @ViewDebug.ExportedProperty(category = "launcher")
    int mCellWidth;
    private final float mChildScale;
    private final int mContainerType;
    /* access modifiers changed from: private */
    @ViewDebug.ExportedProperty(category = "launcher")
    public int mCountX;
    /* access modifiers changed from: private */
    @ViewDebug.ExportedProperty(category = "launcher")
    public int mCountY;
    private final ArrayList<DelegatedCellDrawing> mDelegatedCellDrawings;
    private final int[] mDirectionVector;
    private final int[] mDragCell;
    private final int[] mDragCellSpan;
    final float[] mDragOutlineAlphas;
    private final InterruptibleInOutAnimator[] mDragOutlineAnims;
    private int mDragOutlineCurrent;
    private final Paint mDragOutlinePaint;
    final LayoutParams[] mDragOutlines;
    private boolean mDragging;
    private boolean mDropPending;
    private final TimeInterpolator mEaseOutInterpolator;
    private int mFixedCellHeight;
    private int mFixedCellWidth;
    private int mFixedHeight;
    private int mFixedWidth;
    final PreviewBackground mFolderLeaveBehind;
    private float mGridAlpha;
    private int mGridColor;
    private int mGridVisualizationRoundingRadius;
    private View.OnTouchListener mInterceptTouchListener;
    private final ArrayList<View> mIntersectingViews;
    private boolean mIsDragOverlapping;
    private boolean mItemPlacementDirty;
    private GridOccupancy mOccupied;
    private final Rect mOccupiedRect;
    final int[] mPreviousReorderDirection;
    final ArrayMap<LayoutParams, Animator> mReorderAnimators;
    final float mReorderPreviewAnimationMagnitude;
    private float mScrollProgress;
    final ArrayMap<Reorderable, ReorderPreviewAnimation> mShakeAnimators;
    private final ShortcutAndWidgetContainer mShortcutsAndWidgets;
    private float mSpringLoadedProgress;
    final int[] mTempLocation;
    private final Rect mTempRect;
    private final RectF mTempRectF;
    private final Stack<Rect> mTempRectStack;
    private final float[] mTmpFloatArray;
    private GridOccupancy mTmpOccupied;
    final int[] mTmpPoint;
    final PointF mTmpPointF;
    DragAndDropAccessibilityDelegate mTouchHelper;
    private boolean mVisualizeCells;
    private boolean mVisualizeDropLocation;
    private Paint mVisualizeGridPaint;
    private RectF mVisualizeGridRect;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ContainerType {
    }

    public static abstract class DelegatedCellDrawing {
        public int mDelegateCellX;
        public int mDelegateCellY;

        public abstract void drawOverItem(Canvas canvas);

        public abstract void drawUnderItem(Canvas canvas);
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public CellLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public CellLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CellLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Context context2 = context;
        this.mDropPending = false;
        this.mTmpPoint = new int[2];
        this.mTempLocation = new int[2];
        this.mTmpPointF = new PointF();
        this.mDelegatedCellDrawings = new ArrayList<>();
        PreviewBackground previewBackground = new PreviewBackground();
        this.mFolderLeaveBehind = previewBackground;
        this.mFixedWidth = -1;
        this.mFixedHeight = -1;
        this.mIsDragOverlapping = false;
        LayoutParams[] layoutParamsArr = new LayoutParams[4];
        this.mDragOutlines = layoutParamsArr;
        this.mDragOutlineAlphas = new float[layoutParamsArr.length];
        this.mDragOutlineAnims = new InterruptibleInOutAnimator[layoutParamsArr.length];
        this.mDragOutlineCurrent = 0;
        this.mDragOutlinePaint = new Paint();
        this.mReorderAnimators = new ArrayMap<>();
        this.mShakeAnimators = new ArrayMap<>();
        this.mItemPlacementDirty = false;
        this.mVisualizeCells = false;
        this.mVisualizeDropLocation = true;
        this.mVisualizeGridRect = new RectF();
        this.mVisualizeGridPaint = new Paint();
        this.mGridAlpha = 0.0f;
        this.mGridColor = 0;
        this.mSpringLoadedProgress = 0.0f;
        this.mScrollProgress = 0.0f;
        int[] iArr = new int[2];
        this.mDragCell = iArr;
        int[] iArr2 = new int[2];
        this.mDragCellSpan = iArr2;
        this.mDragging = false;
        this.mChildScale = 1.0f;
        this.mIntersectingViews = new ArrayList<>();
        this.mOccupiedRect = new Rect();
        this.mDirectionVector = new int[2];
        int[] iArr3 = new int[2];
        this.mPreviousReorderDirection = iArr3;
        this.mTempRect = new Rect();
        this.mTempRectF = new RectF();
        this.mTmpFloatArray = new float[4];
        this.mTempRectStack = new Stack<>();
        TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, R.styleable.CellLayout, i, 0);
        this.mContainerType = obtainStyledAttributes.getInteger(0, 0);
        obtainStyledAttributes.recycle();
        setWillNotDraw(false);
        setClipToPadding(false);
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(context);
        this.mActivity = activityContext;
        DeviceProfile deviceProfile = activityContext.getDeviceProfile();
        resetCellSizeInternal(deviceProfile);
        this.mCountX = deviceProfile.inv.numColumns;
        this.mCountY = deviceProfile.inv.numRows;
        this.mOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTmpOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        iArr3[0] = -100;
        iArr3[1] = -100;
        previewBackground.mDelegateCellX = -1;
        previewBackground.mDelegateCellY = -1;
        setAlwaysDrawnWithCacheEnabled(false);
        Resources resources = getResources();
        Drawable drawable = getContext().getDrawable(R.drawable.bg_celllayout);
        this.mBackground = drawable;
        drawable.setCallback(this);
        drawable.setAlpha(0);
        this.mGridColor = Themes.getAttrColor(getContext(), R.attr.workspaceAccentColor);
        this.mGridVisualizationRoundingRadius = resources.getDimensionPixelSize(R.dimen.grid_visualization_rounding_radius);
        this.mReorderPreviewAnimationMagnitude = ((float) deviceProfile.iconSizePx) * 0.12f;
        this.mEaseOutInterpolator = Interpolators.DEACCEL_2_5;
        iArr[1] = -1;
        iArr[0] = -1;
        iArr2[1] = -1;
        iArr2[0] = -1;
        int i2 = 0;
        while (true) {
            LayoutParams[] layoutParamsArr2 = this.mDragOutlines;
            if (i2 >= layoutParamsArr2.length) {
                break;
            }
            layoutParamsArr2[i2] = new LayoutParams(0, 0, 0, 0);
            i2++;
        }
        this.mDragOutlinePaint.setColor(Themes.getAttrColor(context2, R.attr.workspaceTextColor));
        int integer = resources.getInteger(R.integer.config_dragOutlineFadeTime);
        float integer2 = (float) resources.getInteger(R.integer.config_dragOutlineMaxAlpha);
        Arrays.fill(this.mDragOutlineAlphas, 0.0f);
        for (final int i3 = 0; i3 < this.mDragOutlineAnims.length; i3++) {
            InterruptibleInOutAnimator interruptibleInOutAnimator = new InterruptibleInOutAnimator((long) integer, 0.0f, integer2);
            interruptibleInOutAnimator.getAnimator().setInterpolator(this.mEaseOutInterpolator);
            interruptibleInOutAnimator.getAnimator().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    CellLayout.this.mDragOutlineAlphas[i3] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    CellLayout.this.invalidate();
                }
            });
            this.mDragOutlineAnims[i3] = interruptibleInOutAnimator;
        }
        ShortcutAndWidgetContainer shortcutAndWidgetContainer = new ShortcutAndWidgetContainer(context2, this.mContainerType);
        this.mShortcutsAndWidgets = shortcutAndWidgetContainer;
        shortcutAndWidgetContainer.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY, this.mBorderSpace);
        addView(shortcutAndWidgetContainer);
    }

    public void setDragAndDropAccessibilityDelegate(DragAndDropAccessibilityDelegate dragAndDropAccessibilityDelegate) {
        setOnClickListener(dragAndDropAccessibilityDelegate);
        ViewCompat.setAccessibilityDelegate(this, dragAndDropAccessibilityDelegate);
        this.mTouchHelper = dragAndDropAccessibilityDelegate;
        int i = dragAndDropAccessibilityDelegate != null ? 1 : 2;
        setImportantForAccessibility(i);
        getShortcutsAndWidgets().setImportantForAccessibility(i);
        setFocusable(dragAndDropAccessibilityDelegate != null);
        if (getParent() != null) {
            getParent().notifySubtreeAccessibilityStateChanged(this, this, 1);
        }
    }

    public DragAndDropAccessibilityDelegate getDragAndDropAccessibilityDelegate() {
        return this.mTouchHelper;
    }

    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        DragAndDropAccessibilityDelegate dragAndDropAccessibilityDelegate = this.mTouchHelper;
        if (dragAndDropAccessibilityDelegate == null || !dragAndDropAccessibilityDelegate.dispatchHoverEvent(motionEvent)) {
            return super.dispatchHoverEvent(motionEvent);
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.mInterceptTouchListener;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r2) {
        /*
            r1 = this;
            com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate r0 = r1.mTouchHelper
            if (r0 != 0) goto L_0x0011
            android.view.View$OnTouchListener r0 = r1.mInterceptTouchListener
            if (r0 == 0) goto L_0x000f
            boolean r2 = r0.onTouch(r1, r2)
            if (r2 == 0) goto L_0x000f
            goto L_0x0011
        L_0x000f:
            r2 = 0
            goto L_0x0012
        L_0x0011:
            r2 = 1
        L_0x0012:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.CellLayout.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public void enableHardwareLayer(boolean z) {
        this.mShortcutsAndWidgets.setLayerType(z ? 2 : 0, sPaint);
    }

    public boolean isHardwareLayerEnabled() {
        return this.mShortcutsAndWidgets.getLayerType() == 2;
    }

    public void setCellDimensions(int i, int i2) {
        this.mCellWidth = i;
        this.mFixedCellWidth = i;
        this.mCellHeight = i2;
        this.mFixedCellHeight = i2;
        this.mShortcutsAndWidgets.setCellDimensions(i, i2, this.mCountX, this.mCountY, this.mBorderSpace);
    }

    private void resetCellSizeInternal(DeviceProfile deviceProfile) {
        int i = this.mContainerType;
        if (i == 1) {
            this.mBorderSpace = new Point(deviceProfile.hotseatBorderSpace, deviceProfile.hotseatBorderSpace);
        } else if (i != 2) {
            this.mBorderSpace = new Point(deviceProfile.cellLayoutBorderSpacePx);
        } else {
            this.mBorderSpace = new Point(deviceProfile.folderCellLayoutBorderSpacePx);
        }
        this.mCellHeight = -1;
        this.mCellWidth = -1;
        this.mFixedCellHeight = -1;
        this.mFixedCellWidth = -1;
    }

    public void resetCellSize(DeviceProfile deviceProfile) {
        resetCellSizeInternal(deviceProfile);
        requestLayout();
    }

    public void setGridSize(int i, int i2) {
        this.mCountX = i;
        this.mCountY = i2;
        this.mOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTmpOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTempRectStack.clear();
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY, this.mBorderSpace);
        requestLayout();
    }

    public void setInvertIfRtl(boolean z) {
        this.mShortcutsAndWidgets.setInvertIfRtl(z);
    }

    public void setDropPending(boolean z) {
        this.mDropPending = z;
    }

    public boolean isDropPending() {
        return this.mDropPending;
    }

    /* access modifiers changed from: package-private */
    public void setIsDragOverlapping(boolean z) {
        if (this.mIsDragOverlapping != z) {
            this.mIsDragOverlapping = z;
            this.mBackground.setState(z ? BACKGROUND_STATE_ACTIVE : BACKGROUND_STATE_DEFAULT);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        ParcelableSparseArray jailedArray = getJailedArray(sparseArray);
        super.dispatchSaveInstanceState(jailedArray);
        sparseArray.put(R.id.cell_layout_jail_id, jailedArray);
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        super.dispatchRestoreInstanceState(getJailedArray(sparseArray));
    }

    private ParcelableSparseArray getJailedArray(SparseArray<Parcelable> sparseArray) {
        Parcelable parcelable = sparseArray.get(R.id.cell_layout_jail_id);
        return parcelable instanceof ParcelableSparseArray ? (ParcelableSparseArray) parcelable : new ParcelableSparseArray();
    }

    public boolean getIsDragOverlapping() {
        return this.mIsDragOverlapping;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mBackground.getAlpha() > 0) {
            this.mBackground.draw(canvas);
        }
        for (int i = 0; i < this.mDelegatedCellDrawings.size(); i++) {
            DelegatedCellDrawing delegatedCellDrawing = this.mDelegatedCellDrawings.get(i);
            cellToPoint(delegatedCellDrawing.mDelegateCellX, delegatedCellDrawing.mDelegateCellY, this.mTempLocation);
            canvas.save();
            int[] iArr = this.mTempLocation;
            canvas.translate((float) iArr[0], (float) iArr[1]);
            delegatedCellDrawing.drawUnderItem(canvas);
            canvas.restore();
        }
        if (this.mFolderLeaveBehind.mDelegateCellX >= 0 && this.mFolderLeaveBehind.mDelegateCellY >= 0) {
            cellToPoint(this.mFolderLeaveBehind.mDelegateCellX, this.mFolderLeaveBehind.mDelegateCellY, this.mTempLocation);
            canvas.save();
            int[] iArr2 = this.mTempLocation;
            canvas.translate((float) iArr2[0], (float) iArr2[1]);
            this.mFolderLeaveBehind.drawLeaveBehind(canvas);
            canvas.restore();
        }
        if (this.mVisualizeCells || this.mVisualizeDropLocation) {
            visualizeGrid(canvas);
        }
    }

    private boolean canCreateFolder(View view) {
        return (view instanceof DraggableView) && ((DraggableView) view).getViewType() == 0;
    }

    public void setSpringLoadedProgress(float f) {
        if (Float.compare(f, this.mSpringLoadedProgress) != 0) {
            this.mSpringLoadedProgress = f;
            updateBgAlpha();
            setGridAlpha(f);
        }
    }

    public float getSpringLoadedProgress() {
        return this.mSpringLoadedProgress;
    }

    private void updateBgAlpha() {
        this.mBackground.setAlpha((int) (this.mSpringLoadedProgress * 255.0f));
    }

    public void setScrollProgress(float f) {
        if (Float.compare(Math.abs(f), this.mScrollProgress) != 0) {
            this.mScrollProgress = Math.abs(f);
            updateBgAlpha();
        }
    }

    private void setGridAlpha(float f) {
        if (Float.compare(f, this.mGridAlpha) != 0) {
            this.mGridAlpha = f;
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void visualizeGrid(Canvas canvas) {
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        int min = Math.min((this.mCellWidth - deviceProfile.iconSizePx) / 2, deviceProfile.gridVisualizationPaddingX);
        int min2 = Math.min((this.mCellHeight - deviceProfile.iconSizePx) / 2, deviceProfile.gridVisualizationPaddingY);
        float f = (float) min;
        float f2 = (float) min2;
        this.mVisualizeGridRect.set(f, f2, (float) (this.mCellWidth - min), (float) (this.mCellHeight - min2));
        this.mVisualizeGridPaint.setStrokeWidth(8.0f);
        this.mVisualizeGridPaint.setColor(ColorUtils.setAlphaComponent(this.mGridColor, (int) (this.mGridAlpha * 120.0f)));
        if (this.mVisualizeCells) {
            for (int i = 0; i < this.mCountX; i++) {
                for (int i2 = 0; i2 < this.mCountY; i2++) {
                    this.mVisualizeGridRect.offsetTo((float) ((this.mCellWidth * i) + (this.mBorderSpace.x * i) + getPaddingLeft() + min), (float) ((this.mCellHeight * i2) + (this.mBorderSpace.y * i2) + getPaddingTop() + min2));
                    this.mVisualizeGridPaint.setStyle(Paint.Style.FILL);
                    RectF rectF = this.mVisualizeGridRect;
                    int i3 = this.mGridVisualizationRoundingRadius;
                    canvas.drawRoundRect(rectF, (float) i3, (float) i3, this.mVisualizeGridPaint);
                }
            }
        }
        if (this.mVisualizeDropLocation) {
            for (int i4 = 0; i4 < this.mDragOutlines.length; i4++) {
                float f3 = this.mDragOutlineAlphas[i4];
                if (f3 > 0.0f) {
                    this.mVisualizeGridPaint.setAlpha(255);
                    int i5 = this.mDragOutlines[i4].cellX;
                    int i6 = this.mDragOutlines[i4].cellY;
                    int i7 = this.mDragOutlines[i4].cellHSpan;
                    int i8 = this.mDragOutlines[i4].cellVSpan;
                    this.mVisualizeGridRect.set(f, f2, (float) (((this.mCellWidth * i7) + (this.mBorderSpace.x * (i7 - 1))) - min), (float) (((this.mCellHeight * i8) + (this.mBorderSpace.y * (i8 - 1))) - min2));
                    this.mVisualizeGridRect.offsetTo((float) ((this.mCellWidth * i5) + (i5 * this.mBorderSpace.x) + getPaddingLeft() + min), (float) ((this.mCellHeight * i6) + (i6 * this.mBorderSpace.y) + getPaddingTop() + min2));
                    this.mVisualizeGridPaint.setStyle(Paint.Style.STROKE);
                    this.mVisualizeGridPaint.setColor(Color.argb((int) f3, Color.red(this.mGridColor), Color.green(this.mGridColor), Color.blue(this.mGridColor)));
                    RectF rectF2 = this.mVisualizeGridRect;
                    int i9 = this.mGridVisualizationRoundingRadius;
                    canvas.drawRoundRect(rectF2, (float) i9, (float) i9, this.mVisualizeGridPaint);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        for (int i = 0; i < this.mDelegatedCellDrawings.size(); i++) {
            DelegatedCellDrawing delegatedCellDrawing = this.mDelegatedCellDrawings.get(i);
            cellToPoint(delegatedCellDrawing.mDelegateCellX, delegatedCellDrawing.mDelegateCellY, this.mTempLocation);
            canvas.save();
            int[] iArr = this.mTempLocation;
            canvas.translate((float) iArr[0], (float) iArr[1]);
            delegatedCellDrawing.drawOverItem(canvas);
            canvas.restore();
        }
    }

    public void addDelegatedCellDrawing(DelegatedCellDrawing delegatedCellDrawing) {
        this.mDelegatedCellDrawings.add(delegatedCellDrawing);
    }

    public void removeDelegatedCellDrawing(DelegatedCellDrawing delegatedCellDrawing) {
        this.mDelegatedCellDrawings.remove(delegatedCellDrawing);
    }

    public void setFolderLeaveBehindCell(int i, int i2) {
        View childAt = getChildAt(i, i2);
        this.mFolderLeaveBehind.setup(getContext(), this.mActivity, (View) null, childAt.getMeasuredWidth(), childAt.getPaddingTop());
        this.mFolderLeaveBehind.mDelegateCellX = i;
        this.mFolderLeaveBehind.mDelegateCellY = i2;
        invalidate();
    }

    public void clearFolderLeaveBehind() {
        this.mFolderLeaveBehind.mDelegateCellX = -1;
        this.mFolderLeaveBehind.mDelegateCellY = -1;
        invalidate();
    }

    public void restoreInstanceState(SparseArray<Parcelable> sparseArray) {
        try {
            dispatchRestoreInstanceState(sparseArray);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Ignoring an error while restoring a view instance state", e);
        }
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).cancelLongPress();
        }
    }

    public void setOnInterceptTouchListener(View.OnTouchListener onTouchListener) {
        this.mInterceptTouchListener = onTouchListener;
    }

    public int getCountX() {
        return this.mCountX;
    }

    public int getCountY() {
        return this.mCountY;
    }

    public boolean acceptsWidget() {
        return this.mContainerType == 0;
    }

    public boolean addViewToCellLayout(View view, int i, int i2, LayoutParams layoutParams, boolean z) {
        if (view instanceof BubbleTextView) {
            ((BubbleTextView) view).setTextVisibility(this.mContainerType != 1);
        }
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        if (layoutParams.cellX < 0 || layoutParams.cellX > this.mCountX - 1 || layoutParams.cellY < 0 || layoutParams.cellY > this.mCountY - 1) {
            return false;
        }
        if (layoutParams.cellHSpan < 0) {
            layoutParams.cellHSpan = this.mCountX;
        }
        if (layoutParams.cellVSpan < 0) {
            layoutParams.cellVSpan = this.mCountY;
        }
        view.setId(i2);
        this.mShortcutsAndWidgets.addView(view, i, layoutParams);
        if (z) {
            markCellsAsOccupiedForView(view);
        }
        return true;
    }

    public void removeAllViews() {
        this.mOccupied.clear();
        this.mShortcutsAndWidgets.removeAllViews();
    }

    public void removeAllViewsInLayout() {
        if (this.mShortcutsAndWidgets.getChildCount() > 0) {
            this.mOccupied.clear();
            this.mShortcutsAndWidgets.removeAllViewsInLayout();
        }
    }

    public void removeView(View view) {
        markCellsAsUnoccupiedForView(view);
        this.mShortcutsAndWidgets.removeView(view);
    }

    public void removeViewAt(int i) {
        markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i));
        this.mShortcutsAndWidgets.removeViewAt(i);
    }

    public void removeViewInLayout(View view) {
        markCellsAsUnoccupiedForView(view);
        this.mShortcutsAndWidgets.removeViewInLayout(view);
    }

    public void removeViews(int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3++) {
            markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i3));
        }
        this.mShortcutsAndWidgets.removeViews(i, i2);
    }

    public void removeViewsInLayout(int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3++) {
            markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i3));
        }
        this.mShortcutsAndWidgets.removeViewsInLayout(i, i2);
    }

    public void pointToCellExact(int i, int i2, int[] iArr) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        iArr[0] = (i - paddingLeft) / this.mCellWidth;
        iArr[1] = (i2 - paddingTop) / this.mCellHeight;
        int i3 = this.mCountX;
        int i4 = this.mCountY;
        if (iArr[0] < 0) {
            iArr[0] = 0;
        }
        if (iArr[0] >= i3) {
            iArr[0] = i3 - 1;
        }
        if (iArr[1] < 0) {
            iArr[1] = 0;
        }
        if (iArr[1] >= i4) {
            iArr[1] = i4 - 1;
        }
    }

    /* access modifiers changed from: package-private */
    public void pointToCellRounded(int i, int i2, int[] iArr) {
        pointToCellExact(i + (this.mCellWidth / 2), i2 + (this.mCellHeight / 2), iArr);
    }

    /* access modifiers changed from: package-private */
    public void cellToPoint(int i, int i2, int[] iArr) {
        cellToRect(i, i2, 1, 1, this.mTempRect);
        iArr[0] = this.mTempRect.left;
        iArr[1] = this.mTempRect.top;
    }

    /* access modifiers changed from: package-private */
    public void cellToCenterPoint(int i, int i2, int[] iArr) {
        regionToCenterPoint(i, i2, 1, 1, iArr);
    }

    /* access modifiers changed from: package-private */
    public void regionToCenterPoint(int i, int i2, int i3, int i4, int[] iArr) {
        cellToRect(i, i2, i3, i4, this.mTempRect);
        iArr[0] = this.mTempRect.centerX();
        iArr[1] = this.mTempRect.centerY();
    }

    public float getDistanceFromWorkspaceCellVisualCenter(float f, float f2, int[] iArr) {
        getWorkspaceCellVisualCenter(iArr[0], iArr[1], this.mTmpPoint);
        int[] iArr2 = this.mTmpPoint;
        return (float) Math.hypot((double) (f - ((float) iArr2[0])), (double) (f2 - ((float) iArr2[1])));
    }

    private void getWorkspaceCellVisualCenter(int i, int i2, int[] iArr) {
        View childAt = getChildAt(i, i2);
        if (childAt instanceof DraggableView) {
            DraggableView draggableView = (DraggableView) childAt;
            if (draggableView.getViewType() == 0) {
                cellToPoint(i, i2, iArr);
                draggableView.getWorkspaceVisualDragBounds(this.mTempRect);
                this.mTempRect.offset(iArr[0], iArr[1]);
                iArr[0] = this.mTempRect.centerX();
                iArr[1] = this.mTempRect.centerY();
                return;
            }
        }
        cellToCenterPoint(i, i2, iArr);
    }

    public float getFolderCreationRadius(int[] iArr) {
        return (getReorderRadius(iArr) + ((((float) this.mActivity.getDeviceProfile().iconSizePx) * 0.92f) / 2.0f)) / 2.0f;
    }

    public float getReorderRadius(int[] iArr) {
        int[] iArr2 = this.mTmpPoint;
        getWorkspaceCellVisualCenter(iArr[0], iArr[1], iArr2);
        Rect rect = this.mTempRect;
        cellToRect(iArr[0], iArr[1], 1, 1, rect);
        rect.inset((-this.mBorderSpace.x) / 2, (-this.mBorderSpace.y) / 2);
        if (canCreateFolder(getChildAt(iArr[0], iArr[1]))) {
            return (float) Math.min(Math.min(Math.min(iArr2[0] - rect.left, iArr2[1] - rect.top), rect.right - iArr2[0]), rect.bottom - iArr2[1]);
        }
        return (float) Math.hypot((double) (((float) rect.width()) / 2.0f), (double) (((float) rect.height()) / 2.0f));
    }

    public int getCellWidth() {
        return this.mCellWidth;
    }

    public int getCellHeight() {
        return this.mCellHeight;
    }

    public void setFixedSize(int i, int i2) {
        this.mFixedWidth = i;
        this.mFixedHeight = i2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int paddingLeft = size - (getPaddingLeft() + getPaddingRight());
        int paddingTop = size2 - (getPaddingTop() + getPaddingBottom());
        if (this.mFixedCellWidth < 0 || this.mFixedCellHeight < 0) {
            int calculateCellWidth = DeviceProfile.calculateCellWidth(paddingLeft, this.mBorderSpace.x, this.mCountX);
            int calculateCellHeight = DeviceProfile.calculateCellHeight(paddingTop, this.mBorderSpace.y, this.mCountY);
            if (!(calculateCellWidth == this.mCellWidth && calculateCellHeight == this.mCellHeight)) {
                this.mCellWidth = calculateCellWidth;
                this.mCellHeight = calculateCellHeight;
                this.mShortcutsAndWidgets.setCellDimensions(calculateCellWidth, calculateCellHeight, this.mCountX, this.mCountY, this.mBorderSpace);
            }
        }
        int i4 = this.mFixedWidth;
        if (i4 > 0 && (i3 = this.mFixedHeight) > 0) {
            paddingLeft = i4;
            paddingTop = i3;
        } else if (mode == 0 || mode2 == 0) {
            throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
        }
        this.mShortcutsAndWidgets.measure(View.MeasureSpec.makeMeasureSpec(paddingLeft, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(paddingTop, BasicMeasure.EXACTLY));
        int measuredWidth = this.mShortcutsAndWidgets.getMeasuredWidth();
        int measuredHeight = this.mShortcutsAndWidgets.getMeasuredHeight();
        if (this.mFixedWidth <= 0 || this.mFixedHeight <= 0) {
            setMeasuredDimension(size, size2);
        } else {
            setMeasuredDimension(measuredWidth, measuredHeight);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft() + ((int) Math.ceil((double) (((float) getUnusedHorizontalSpace()) / 2.0f)));
        int paddingRight = ((i3 - i) - getPaddingRight()) - ((int) Math.ceil((double) (((float) getUnusedHorizontalSpace()) / 2.0f)));
        int paddingTop = getPaddingTop();
        int paddingBottom = (i4 - i2) - getPaddingBottom();
        this.mBackground.getPadding(this.mTempRect);
        this.mBackground.setBounds((paddingLeft - this.mTempRect.left) - getPaddingLeft(), (paddingTop - this.mTempRect.top) - getPaddingTop(), this.mTempRect.right + paddingRight + getPaddingRight(), this.mTempRect.bottom + paddingBottom + getPaddingBottom());
        this.mShortcutsAndWidgets.layout(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public int getUnusedHorizontalSpace() {
        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        int i = this.mCountX;
        return (measuredWidth - (this.mCellWidth * i)) - ((i - 1) * this.mBorderSpace.x);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mBackground;
    }

    public ShortcutAndWidgetContainer getShortcutsAndWidgets() {
        return this.mShortcutsAndWidgets;
    }

    public View getChildAt(int i, int i2) {
        return this.mShortcutsAndWidgets.getChildAt(i, i2);
    }

    public boolean animateChildToPosition(View view, int i, int i2, int i3, int i4, boolean z, boolean z2) {
        final View view2 = view;
        int i5 = i;
        int i6 = i2;
        ShortcutAndWidgetContainer shortcutsAndWidgets = getShortcutsAndWidgets();
        if (shortcutsAndWidgets.indexOfChild(view2) == -1 || !(view2 instanceof Reorderable)) {
            return false;
        }
        final LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        ItemInfo itemInfo = (ItemInfo) view.getTag();
        final Reorderable reorderable = (Reorderable) view2;
        if (this.mReorderAnimators.containsKey(layoutParams)) {
            this.mReorderAnimators.get(layoutParams).cancel();
            this.mReorderAnimators.remove(layoutParams);
        }
        if (z2) {
            GridOccupancy gridOccupancy = z ? this.mOccupied : this.mTmpOccupied;
            gridOccupancy.markCells(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, false);
            gridOccupancy.markCells(i, i2, layoutParams.cellHSpan, layoutParams.cellVSpan, true);
        }
        int i7 = layoutParams.x;
        int i8 = layoutParams.y;
        layoutParams.isLockedToGrid = true;
        if (z) {
            itemInfo.cellX = i5;
            layoutParams.cellX = i5;
            itemInfo.cellY = i6;
            layoutParams.cellY = i6;
        } else {
            layoutParams.tmpCellX = i5;
            layoutParams.tmpCellY = i6;
        }
        shortcutsAndWidgets.setupLp(view2);
        int i9 = layoutParams.x;
        int i10 = layoutParams.y;
        layoutParams.x = i7;
        layoutParams.y = i8;
        layoutParams.isLockedToGrid = false;
        reorderable.getReorderPreviewOffset(this.mTmpPointF);
        float f = this.mTmpPointF.x;
        float f2 = this.mTmpPointF.y;
        float f3 = (float) (i9 - i7);
        float f4 = (float) (i10 - i8);
        if (f3 == 0.0f && f4 == 0.0f && f == 0.0f && f2 == 0.0f) {
            layoutParams.isLockedToGrid = true;
            return true;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration((long) i3);
        this.mReorderAnimators.put(layoutParams, ofFloat);
        final float f5 = f;
        final float f6 = f3;
        final float f7 = f2;
        final float f8 = f4;
        final Reorderable reorderable2 = reorderable;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                float f = 1.0f - floatValue;
                reorderable2.setReorderPreviewOffset((f5 * f) + (f6 * floatValue), (f * f7) + (floatValue * f8));
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            boolean cancelled = false;

            public void onAnimationEnd(Animator animator) {
                if (!this.cancelled) {
                    layoutParams.isLockedToGrid = true;
                    reorderable.setReorderPreviewOffset(0.0f, 0.0f);
                    view2.requestLayout();
                }
                if (CellLayout.this.mReorderAnimators.containsKey(layoutParams)) {
                    CellLayout.this.mReorderAnimators.remove(layoutParams);
                }
            }

            public void onAnimationCancel(Animator animator) {
                this.cancelled = true;
            }
        });
        ofFloat.setStartDelay((long) i4);
        ofFloat.start();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void visualizeDropLocation(int i, int i2, int i3, int i4, DropTarget.DragObject dragObject) {
        int[] iArr = this.mDragCell;
        if (iArr[0] == i && iArr[1] == i2) {
            int[] iArr2 = this.mDragCellSpan;
            if (iArr2[0] == i3 && iArr2[1] == i4) {
                return;
            }
        }
        iArr[0] = i;
        iArr[1] = i2;
        int[] iArr3 = this.mDragCellSpan;
        iArr3[0] = i3;
        iArr3[1] = i4;
        applyColorExtractionOnWidget(dragObject, iArr, i3, i4);
        int i5 = this.mDragOutlineCurrent;
        this.mDragOutlineAnims[i5].animateOut();
        LayoutParams[] layoutParamsArr = this.mDragOutlines;
        int length = (i5 + 1) % layoutParamsArr.length;
        this.mDragOutlineCurrent = length;
        LayoutParams layoutParams = layoutParamsArr[length];
        layoutParams.cellX = i;
        layoutParams.cellY = i2;
        layoutParams.cellHSpan = i3;
        layoutParams.cellVSpan = i4;
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateIn();
        invalidate();
        if (dragObject.stateAnnouncer != null) {
            dragObject.stateAnnouncer.announce(getItemMoveDescription(i, i2));
        }
    }

    private void applyColorExtractionOnWidget(DropTarget.DragObject dragObject, int[] iArr, int i, int i2) {
        View contentView = dragObject.dragView.getContentView();
        if (contentView instanceof LauncherAppWidgetHostView) {
            int idForScreen = getWorkspace().getIdForScreen(this);
            cellToRect(iArr[0], iArr[1], i, i2, this.mTempRect);
            ((LauncherAppWidgetHostView) contentView).handleDrag(this.mTempRect, this, idForScreen);
        }
    }

    public String getItemMoveDescription(int i, int i2) {
        if (this.mContainerType == 1) {
            return getContext().getString(R.string.move_to_hotseat_position, new Object[]{Integer.valueOf(Math.max(i, i2) + 1)});
        }
        Workspace<?> workspace = getWorkspace();
        int i3 = i2 + 1;
        int i4 = workspace.mIsRtl ? this.mCountX - i : i + 1;
        int panelCount = workspace.getPanelCount();
        if (panelCount > 1) {
            i4 += (workspace.getPageIndexForScreenId(workspace.getIdForScreen(this)) % panelCount) * this.mCountX;
        }
        return getContext().getString(R.string.move_to_empty_cell, new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
    }

    private Workspace<?> getWorkspace() {
        return Launcher.cast(this.mActivity).getWorkspace();
    }

    public void clearDragOutlines() {
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        int[] iArr = this.mDragCell;
        iArr[1] = -1;
        iArr[0] = -1;
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestVacantArea(int i, int i2, int i3, int i4, int i5, int i6, int[] iArr, int[] iArr2) {
        return findNearestArea(i, i2, i3, i4, i5, i6, true, iArr, iArr2);
    }

    private void lazyInitTempRectStack() {
        if (this.mTempRectStack.isEmpty()) {
            for (int i = 0; i < this.mCountX * this.mCountY; i++) {
                this.mTempRectStack.push(new Rect());
            }
        }
    }

    private void recycleTempRects(Stack<Rect> stack) {
        while (!stack.isEmpty()) {
            this.mTempRectStack.push(stack.pop());
        }
    }

    private int[] findNearestArea(int i, int i2, int i3, int i4, int i5, int i6, boolean z, int[] iArr, int[] iArr2) {
        int i7;
        int i8;
        int[] iArr3;
        Rect rect;
        Rect rect2;
        boolean z2;
        int i9;
        int i10;
        Rect rect3;
        int i11 = i3;
        int i12 = i4;
        int i13 = i5;
        int i14 = i6;
        lazyInitTempRectStack();
        int i15 = (int) (((float) i) - (((float) (this.mCellWidth * (i13 - 1))) / 2.0f));
        int i16 = (int) (((float) i2) - (((float) (this.mCellHeight * (i14 - 1))) / 2.0f));
        int[] iArr4 = iArr != null ? iArr : new int[2];
        Rect rect4 = new Rect(-1, -1, -1, -1);
        Stack stack = new Stack();
        int i17 = this.mCountX;
        int i18 = this.mCountY;
        if (i11 <= 0 || i12 <= 0 || i13 <= 0 || i14 <= 0 || i13 < i11 || i14 < i12) {
            return iArr4;
        }
        int i19 = 0;
        double d = Double.MAX_VALUE;
        while (i19 < i18 - (i12 - 1)) {
            int i20 = 0;
            while (i20 < i17 - (i11 - 1)) {
                if (z) {
                    for (int i21 = 0; i21 < i11; i21++) {
                        int i22 = 0;
                        while (i22 < i12) {
                            iArr3 = iArr4;
                            if (this.mOccupied.cells[i20 + i21][i19 + i22]) {
                                i7 = i15;
                                i8 = i16;
                                rect = rect4;
                                break;
                            }
                            i22++;
                            iArr4 = iArr3;
                        }
                        int[] iArr5 = iArr4;
                    }
                    iArr3 = iArr4;
                    boolean z3 = i11 >= i13;
                    boolean z4 = i12 >= i14;
                    boolean z5 = z3;
                    boolean z6 = true;
                    while (true) {
                        if (z5 && z4) {
                            break;
                        }
                        if (!z6 || z5) {
                            i9 = i15;
                            i10 = i16;
                            rect3 = rect4;
                            if (!z4) {
                                for (int i23 = 0; i23 < i11; i23++) {
                                    int i24 = i19 + i12;
                                    if (i24 > i18 - 1 || this.mOccupied.cells[i20 + i23][i24]) {
                                        z4 = true;
                                    }
                                }
                                if (!z4) {
                                    i12++;
                                }
                            }
                        } else {
                            rect3 = rect4;
                            int i25 = 0;
                            while (i25 < i12) {
                                int i26 = i16;
                                int i27 = i20 + i11;
                                int i28 = i15;
                                if (i27 > i17 - 1 || this.mOccupied.cells[i27][i19 + i25]) {
                                    z5 = true;
                                }
                                i25++;
                                i16 = i26;
                                i15 = i28;
                            }
                            i9 = i15;
                            i10 = i16;
                            if (!z5) {
                                i11++;
                            }
                        }
                        z5 |= i11 >= i13;
                        z4 |= i12 >= i14;
                        z6 = !z6;
                        rect4 = rect3;
                        i16 = i10;
                        i15 = i9;
                    }
                    i7 = i15;
                    i8 = i16;
                    rect2 = rect4;
                } else {
                    i7 = i15;
                    i8 = i16;
                    iArr3 = iArr4;
                    rect2 = rect4;
                    i11 = -1;
                    i12 = -1;
                }
                int[] iArr6 = this.mTmpPoint;
                cellToCenterPoint(i20, i19, iArr6);
                Rect pop = this.mTempRectStack.pop();
                pop.set(i20, i19, i20 + i11, i19 + i12);
                Iterator it = stack.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((Rect) it.next()).contains(pop)) {
                            z2 = true;
                            break;
                        }
                    } else {
                        z2 = false;
                        break;
                    }
                }
                stack.push(pop);
                double hypot = Math.hypot((double) (iArr6[0] - i7), (double) (iArr6[1] - i8));
                if (hypot > d || z2) {
                    rect = rect2;
                    if (!pop.contains(rect)) {
                        i20++;
                        i11 = i3;
                        i12 = i4;
                        i13 = i5;
                        i14 = i6;
                        rect4 = rect;
                        iArr4 = iArr3;
                        i16 = i8;
                        i15 = i7;
                    }
                } else {
                    rect = rect2;
                }
                iArr3[0] = i20;
                iArr3[1] = i19;
                if (iArr2 != null) {
                    iArr2[0] = i11;
                    iArr2[1] = i12;
                }
                rect.set(pop);
                d = hypot;
                i20++;
                i11 = i3;
                i12 = i4;
                i13 = i5;
                i14 = i6;
                rect4 = rect;
                iArr4 = iArr3;
                i16 = i8;
                i15 = i7;
            }
            int i29 = i15;
            int i30 = i16;
            int[] iArr7 = iArr4;
            Rect rect5 = rect4;
            i19++;
            i11 = i3;
            i12 = i4;
            i13 = i5;
            i14 = i6;
            i15 = i29;
        }
        int[] iArr8 = iArr4;
        if (d == Double.MAX_VALUE) {
            iArr8[0] = -1;
            iArr8[1] = -1;
        }
        recycleTempRects(stack);
        return iArr8;
    }

    private int[] findNearestArea(int i, int i2, int i3, int i4, int[] iArr, boolean[][] zArr, boolean[][] zArr2, int[] iArr2) {
        int i5;
        int i6 = i3;
        int i7 = i4;
        int[] iArr3 = iArr2 != null ? iArr2 : new int[2];
        int i8 = Integer.MIN_VALUE;
        int i9 = this.mCountX;
        int i10 = this.mCountY;
        int i11 = 0;
        float f = Float.MAX_VALUE;
        while (i11 < i10 - (i7 - 1)) {
            int i12 = 0;
            while (i12 < i9 - (i6 - 1)) {
                int i13 = 0;
                while (true) {
                    if (i13 < i6) {
                        int i14 = 0;
                        while (i14 < i7) {
                            if (!zArr[i12 + i13][i11 + i14] || (zArr2 != null && !zArr2[i13][i14])) {
                                i14++;
                            }
                        }
                        i13++;
                    } else {
                        int i15 = i12 - i;
                        int i16 = i11 - i2;
                        i5 = i11;
                        float hypot = (float) Math.hypot((double) i15, (double) i16);
                        int[] iArr4 = this.mTmpPoint;
                        computeDirectionVector((float) i15, (float) i16, iArr4);
                        int i17 = (iArr[0] * iArr4[0]) + (iArr[1] * iArr4[1]);
                        if (Float.compare(hypot, f) < 0 || (Float.compare(hypot, f) == 0 && i17 > i8)) {
                            iArr3[0] = i12;
                            iArr3[1] = i5;
                            f = hypot;
                            i8 = i17;
                        }
                    }
                }
                i5 = i11;
                i12++;
                i11 = i5;
            }
            i11++;
        }
        if (f == Float.MAX_VALUE) {
            iArr3[0] = -1;
            iArr3[1] = -1;
        }
        return iArr3;
    }

    private boolean addViewToTempLocation(View view, Rect rect, int[] iArr, ItemConfiguration itemConfiguration) {
        CellAndSpan cellAndSpan = itemConfiguration.map.get(view);
        boolean z = false;
        this.mTmpOccupied.markCells(cellAndSpan, false);
        this.mTmpOccupied.markCells(rect, true);
        findNearestArea(cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.spanX, cellAndSpan.spanY, iArr, this.mTmpOccupied.cells, (boolean[][]) null, this.mTempLocation);
        int[] iArr2 = this.mTempLocation;
        if (iArr2[0] >= 0 && iArr2[1] >= 0) {
            cellAndSpan.cellX = iArr2[0];
            cellAndSpan.cellY = this.mTempLocation[1];
            z = true;
        }
        this.mTmpOccupied.markCells(cellAndSpan, true);
        return z;
    }

    private class ViewCluster {
        static final int BOTTOM = 8;
        static final int LEFT = 1;
        static final int RIGHT = 4;
        static final int TOP = 2;
        final int[] bottomEdge;
        final Rect boundingRect = new Rect();
        boolean boundingRectDirty;
        final PositionComparator comparator;
        final ItemConfiguration config;
        int dirtyEdges;
        final int[] leftEdge;
        final int[] rightEdge;
        final int[] topEdge;
        final ArrayList<View> views;

        public ViewCluster(ArrayList<View> arrayList, ItemConfiguration itemConfiguration) {
            this.leftEdge = new int[CellLayout.this.mCountY];
            this.rightEdge = new int[CellLayout.this.mCountY];
            this.topEdge = new int[CellLayout.this.mCountX];
            this.bottomEdge = new int[CellLayout.this.mCountX];
            this.comparator = new PositionComparator();
            this.views = (ArrayList) arrayList.clone();
            this.config = itemConfiguration;
            resetEdges();
        }

        /* access modifiers changed from: package-private */
        public void resetEdges() {
            for (int i = 0; i < CellLayout.this.mCountX; i++) {
                this.topEdge[i] = -1;
                this.bottomEdge[i] = -1;
            }
            for (int i2 = 0; i2 < CellLayout.this.mCountY; i2++) {
                this.leftEdge[i2] = -1;
                this.rightEdge[i2] = -1;
            }
            this.dirtyEdges = 15;
            this.boundingRectDirty = true;
        }

        /* access modifiers changed from: package-private */
        public void computeEdge(int i) {
            int size = this.views.size();
            for (int i2 = 0; i2 < size; i2++) {
                CellAndSpan cellAndSpan = this.config.map.get(this.views.get(i2));
                if (i == 1) {
                    int i3 = cellAndSpan.cellX;
                    for (int i4 = cellAndSpan.cellY; i4 < cellAndSpan.cellY + cellAndSpan.spanY; i4++) {
                        int[] iArr = this.leftEdge;
                        if (i3 < iArr[i4] || iArr[i4] < 0) {
                            iArr[i4] = i3;
                        }
                    }
                } else if (i == 2) {
                    int i5 = cellAndSpan.cellY;
                    for (int i6 = cellAndSpan.cellX; i6 < cellAndSpan.cellX + cellAndSpan.spanX; i6++) {
                        int[] iArr2 = this.topEdge;
                        if (i5 < iArr2[i6] || iArr2[i6] < 0) {
                            iArr2[i6] = i5;
                        }
                    }
                } else if (i == 4) {
                    int i7 = cellAndSpan.cellX + cellAndSpan.spanX;
                    for (int i8 = cellAndSpan.cellY; i8 < cellAndSpan.cellY + cellAndSpan.spanY; i8++) {
                        int[] iArr3 = this.rightEdge;
                        if (i7 > iArr3[i8]) {
                            iArr3[i8] = i7;
                        }
                    }
                } else if (i == 8) {
                    int i9 = cellAndSpan.cellY + cellAndSpan.spanY;
                    for (int i10 = cellAndSpan.cellX; i10 < cellAndSpan.cellX + cellAndSpan.spanX; i10++) {
                        int[] iArr4 = this.bottomEdge;
                        if (i9 > iArr4[i10]) {
                            iArr4[i10] = i9;
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isViewTouchingEdge(View view, int i) {
            CellAndSpan cellAndSpan = this.config.map.get(view);
            if ((this.dirtyEdges & i) == i) {
                computeEdge(i);
                this.dirtyEdges &= ~i;
            }
            if (i == 1) {
                for (int i2 = cellAndSpan.cellY; i2 < cellAndSpan.cellY + cellAndSpan.spanY; i2++) {
                    if (this.leftEdge[i2] == cellAndSpan.cellX + cellAndSpan.spanX) {
                        return true;
                    }
                }
                return false;
            } else if (i == 2) {
                for (int i3 = cellAndSpan.cellX; i3 < cellAndSpan.cellX + cellAndSpan.spanX; i3++) {
                    if (this.topEdge[i3] == cellAndSpan.cellY + cellAndSpan.spanY) {
                        return true;
                    }
                }
                return false;
            } else if (i == 4) {
                for (int i4 = cellAndSpan.cellY; i4 < cellAndSpan.cellY + cellAndSpan.spanY; i4++) {
                    if (this.rightEdge[i4] == cellAndSpan.cellX) {
                        return true;
                    }
                }
                return false;
            } else if (i != 8) {
                return false;
            } else {
                for (int i5 = cellAndSpan.cellX; i5 < cellAndSpan.cellX + cellAndSpan.spanX; i5++) {
                    if (this.bottomEdge[i5] == cellAndSpan.cellY) {
                        return true;
                    }
                }
                return false;
            }
        }

        /* access modifiers changed from: package-private */
        public void shift(int i, int i2) {
            Iterator<View> it = this.views.iterator();
            while (it.hasNext()) {
                CellAndSpan cellAndSpan = this.config.map.get(it.next());
                if (i == 1) {
                    cellAndSpan.cellX -= i2;
                } else if (i == 2) {
                    cellAndSpan.cellY -= i2;
                } else if (i != 4) {
                    cellAndSpan.cellY += i2;
                } else {
                    cellAndSpan.cellX += i2;
                }
            }
            resetEdges();
        }

        public void addView(View view) {
            this.views.add(view);
            resetEdges();
        }

        public Rect getBoundingRect() {
            if (this.boundingRectDirty) {
                this.config.getBoundingRectForViews(this.views, this.boundingRect);
            }
            return this.boundingRect;
        }

        class PositionComparator implements Comparator<View> {
            int whichEdge = 0;

            PositionComparator() {
            }

            public int compare(View view, View view2) {
                int i;
                int i2;
                int i3;
                int i4;
                int i5;
                CellAndSpan cellAndSpan = ViewCluster.this.config.map.get(view);
                CellAndSpan cellAndSpan2 = ViewCluster.this.config.map.get(view2);
                int i6 = this.whichEdge;
                if (i6 == 1) {
                    i = cellAndSpan2.cellX + cellAndSpan2.spanX;
                    i2 = cellAndSpan.cellX;
                    i3 = cellAndSpan.spanX;
                } else if (i6 != 2) {
                    if (i6 != 4) {
                        i4 = cellAndSpan.cellY;
                        i5 = cellAndSpan2.cellY;
                    } else {
                        i4 = cellAndSpan.cellX;
                        i5 = cellAndSpan2.cellX;
                    }
                    return i4 - i5;
                } else {
                    i = cellAndSpan2.cellY + cellAndSpan2.spanY;
                    i2 = cellAndSpan.cellY;
                    i3 = cellAndSpan.spanY;
                }
                return i - (i2 + i3);
            }
        }

        public void sortConfigurationForEdgePush(int i) {
            this.comparator.whichEdge = i;
            Collections.sort(this.config.sortedViews, this.comparator);
        }
    }

    private boolean pushViewsToTempLocation(ArrayList<View> arrayList, Rect rect, int[] iArr, View view, ItemConfiguration itemConfiguration) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        ViewCluster viewCluster = new ViewCluster(arrayList, itemConfiguration);
        Rect boundingRect = viewCluster.getBoundingRect();
        boolean z = false;
        if (iArr[0] < 0) {
            i = boundingRect.right - rect.left;
            i2 = 1;
        } else {
            if (iArr[0] > 0) {
                i3 = 4;
                i4 = rect.right;
                i5 = boundingRect.left;
            } else if (iArr[1] < 0) {
                i3 = 2;
                i6 = boundingRect.bottom - rect.top;
                int i7 = i3;
                i = i6;
                i2 = i7;
            } else {
                i3 = 8;
                i4 = rect.bottom;
                i5 = boundingRect.top;
            }
            i6 = i4 - i5;
            int i72 = i3;
            i = i6;
            i2 = i72;
        }
        if (i <= 0) {
            return false;
        }
        Iterator<View> it = arrayList.iterator();
        while (it.hasNext()) {
            this.mTmpOccupied.markCells(itemConfiguration.map.get(it.next()), false);
        }
        itemConfiguration.save();
        viewCluster.sortConfigurationForEdgePush(i2);
        boolean z2 = false;
        while (i > 0 && !z2) {
            Iterator<View> it2 = itemConfiguration.sortedViews.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                View next = it2.next();
                if (!viewCluster.views.contains(next) && next != view && viewCluster.isViewTouchingEdge(next, i2)) {
                    if (!((LayoutParams) next.getLayoutParams()).canReorder) {
                        z2 = true;
                        break;
                    }
                    viewCluster.addView(next);
                    this.mTmpOccupied.markCells(itemConfiguration.map.get(next), false);
                }
            }
            i--;
            viewCluster.shift(i2, 1);
        }
        Rect boundingRect2 = viewCluster.getBoundingRect();
        if (z2 || boundingRect2.left < 0 || boundingRect2.right > this.mCountX || boundingRect2.top < 0 || boundingRect2.bottom > this.mCountY) {
            itemConfiguration.restore();
        } else {
            z = true;
        }
        Iterator<View> it3 = viewCluster.views.iterator();
        while (it3.hasNext()) {
            this.mTmpOccupied.markCells(itemConfiguration.map.get(it3.next()), true);
        }
        return z;
    }

    private boolean addViewsToTempLocation(ArrayList<View> arrayList, Rect rect, int[] iArr, View view, ItemConfiguration itemConfiguration) {
        boolean z;
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        if (arrayList.size() == 0) {
            return true;
        }
        Rect rect2 = new Rect();
        itemConfiguration2.getBoundingRectForViews(arrayList, rect2);
        Iterator<View> it = arrayList.iterator();
        while (true) {
            z = false;
            if (!it.hasNext()) {
                break;
            }
            this.mTmpOccupied.markCells(itemConfiguration2.map.get(it.next()), false);
        }
        GridOccupancy gridOccupancy = new GridOccupancy(rect2.width(), rect2.height());
        int i = rect2.top;
        int i2 = rect2.left;
        Iterator<View> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            CellAndSpan cellAndSpan = itemConfiguration2.map.get(it2.next());
            gridOccupancy.markCells(cellAndSpan.cellX - i2, cellAndSpan.cellY - i, cellAndSpan.spanX, cellAndSpan.spanY, true);
        }
        this.mTmpOccupied.markCells(rect, true);
        findNearestArea(rect2.left, rect2.top, rect2.width(), rect2.height(), iArr, this.mTmpOccupied.cells, gridOccupancy.cells, this.mTempLocation);
        int[] iArr2 = this.mTempLocation;
        if (iArr2[0] >= 0 && iArr2[1] >= 0) {
            int i3 = iArr2[0] - rect2.left;
            int i4 = this.mTempLocation[1] - rect2.top;
            Iterator<View> it3 = arrayList.iterator();
            while (it3.hasNext()) {
                CellAndSpan cellAndSpan2 = itemConfiguration2.map.get(it3.next());
                cellAndSpan2.cellX += i3;
                cellAndSpan2.cellY += i4;
            }
            z = true;
        }
        Iterator<View> it4 = arrayList.iterator();
        while (it4.hasNext()) {
            this.mTmpOccupied.markCells(itemConfiguration2.map.get(it4.next()), true);
        }
        return z;
    }

    private boolean attemptPushInDirection(ArrayList<View> arrayList, Rect rect, int[] iArr, View view, ItemConfiguration itemConfiguration) {
        if (Math.abs(iArr[0]) + Math.abs(iArr[1]) > 1) {
            int i = iArr[1];
            iArr[1] = 0;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[1] = i;
            int i2 = iArr[0];
            iArr[0] = 0;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = i2;
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            int i3 = iArr[1];
            iArr[1] = 0;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[1] = i3;
            int i4 = iArr[0];
            iArr[0] = 0;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = i4;
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
        } else if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
            return true;
        } else {
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            int i5 = iArr[1];
            iArr[1] = iArr[0];
            iArr[0] = i5;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            int i6 = iArr[1];
            iArr[1] = iArr[0];
            iArr[0] = i6;
        }
        return false;
    }

    private boolean rearrangementExists(int i, int i2, int i3, int i4, int[] iArr, View view, ItemConfiguration itemConfiguration) {
        CellAndSpan cellAndSpan;
        if (i < 0 || i2 < 0) {
            return false;
        }
        this.mIntersectingViews.clear();
        int i5 = i3 + i;
        int i6 = i4 + i2;
        this.mOccupiedRect.set(i, i2, i5, i6);
        if (!(view == null || (cellAndSpan = itemConfiguration.map.get(view)) == null)) {
            cellAndSpan.cellX = i;
            cellAndSpan.cellY = i2;
        }
        Rect rect = new Rect(i, i2, i5, i6);
        Rect rect2 = new Rect();
        for (View next : itemConfiguration.map.keySet()) {
            if (next != view) {
                CellAndSpan cellAndSpan2 = itemConfiguration.map.get(next);
                LayoutParams layoutParams = (LayoutParams) next.getLayoutParams();
                rect2.set(cellAndSpan2.cellX, cellAndSpan2.cellY, cellAndSpan2.cellX + cellAndSpan2.spanX, cellAndSpan2.cellY + cellAndSpan2.spanY);
                if (!Rect.intersects(rect, rect2)) {
                    continue;
                } else if (!layoutParams.canReorder) {
                    return false;
                } else {
                    this.mIntersectingViews.add(next);
                }
            }
        }
        itemConfiguration.intersectingViews = new ArrayList<>(this.mIntersectingViews);
        if (attemptPushInDirection(this.mIntersectingViews, this.mOccupiedRect, iArr, view, itemConfiguration)) {
            return true;
        }
        if (addViewsToTempLocation(this.mIntersectingViews, this.mOccupiedRect, iArr, view, itemConfiguration)) {
            return true;
        }
        Iterator<View> it = this.mIntersectingViews.iterator();
        while (it.hasNext()) {
            if (!addViewToTempLocation(it.next(), this.mOccupiedRect, iArr, itemConfiguration)) {
                return false;
            }
        }
        return true;
    }

    private void computeDirectionVector(float f, float f2, int[] iArr) {
        double atan = Math.atan((double) (f2 / f));
        iArr[0] = 0;
        iArr[1] = 0;
        if (Math.abs(Math.cos(atan)) > 0.5d) {
            iArr[0] = (int) Math.signum(f);
        }
        if (Math.abs(Math.sin(atan)) > 0.5d) {
            iArr[1] = (int) Math.signum(f2);
        }
    }

    private ItemConfiguration findReorderSolution(int i, int i2, int i3, int i4, int i5, int i6, int[] iArr, View view, boolean z, ItemConfiguration itemConfiguration) {
        int i7 = i4;
        int i8 = i5;
        int i9 = i6;
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        copyCurrentStateToSolution(itemConfiguration2, false);
        this.mOccupied.copyTo(this.mTmpOccupied);
        int i10 = i5;
        int i11 = i6;
        int[] findNearestArea = findNearestArea(i, i2, i10, i11, new int[2]);
        if (rearrangementExists(findNearestArea[0], findNearestArea[1], i10, i11, iArr, view, itemConfiguration)) {
            itemConfiguration2.isSolution = true;
            itemConfiguration2.cellX = findNearestArea[0];
            itemConfiguration2.cellY = findNearestArea[1];
            itemConfiguration2.spanX = i8;
            itemConfiguration2.spanY = i9;
        } else if (i8 > i3 && (i7 == i9 || z)) {
            return findReorderSolution(i, i2, i3, i4, i8 - 1, i6, iArr, view, false, itemConfiguration);
        } else if (i9 > i7) {
            return findReorderSolution(i, i2, i3, i4, i5, i9 - 1, iArr, view, true, itemConfiguration);
        } else {
            itemConfiguration2.isSolution = false;
        }
        return itemConfiguration2;
    }

    private void copyCurrentStateToSolution(ItemConfiguration itemConfiguration, boolean z) {
        CellAndSpan cellAndSpan;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (z) {
                cellAndSpan = new CellAndSpan(layoutParams.tmpCellX, layoutParams.tmpCellY, layoutParams.cellHSpan, layoutParams.cellVSpan);
            } else {
                cellAndSpan = new CellAndSpan(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan);
            }
            itemConfiguration.add(childAt, cellAndSpan);
        }
    }

    private void copySolutionToTempState(ItemConfiguration itemConfiguration, View view) {
        this.mTmpOccupied.clear();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            if (childAt != view) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                CellAndSpan cellAndSpan = itemConfiguration.map.get(childAt);
                if (cellAndSpan != null) {
                    layoutParams.tmpCellX = cellAndSpan.cellX;
                    layoutParams.tmpCellY = cellAndSpan.cellY;
                    layoutParams.cellHSpan = cellAndSpan.spanX;
                    layoutParams.cellVSpan = cellAndSpan.spanY;
                    this.mTmpOccupied.markCells(cellAndSpan, true);
                }
            }
        }
        this.mTmpOccupied.markCells((CellAndSpan) itemConfiguration, true);
    }

    private void animateItemsToSolution(ItemConfiguration itemConfiguration, View view, boolean z) {
        CellAndSpan cellAndSpan;
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        GridOccupancy gridOccupancy = this.mTmpOccupied;
        gridOccupancy.clear();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            if (!(childAt == view || (cellAndSpan = itemConfiguration2.map.get(childAt)) == null)) {
                animateChildToPosition(childAt, cellAndSpan.cellX, cellAndSpan.cellY, 150, 0, false, false);
                gridOccupancy.markCells(cellAndSpan, true);
            }
        }
        if (z) {
            gridOccupancy.markCells((CellAndSpan) itemConfiguration2, true);
        }
    }

    private void beginOrAdjustReorderPreviewAnimations(ItemConfiguration itemConfiguration, View view, int i) {
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i2);
            if (childAt != view) {
                CellAndSpan cellAndSpan = itemConfiguration2.map.get(childAt);
                boolean z = i == 0 && itemConfiguration2.intersectingViews != null && !itemConfiguration2.intersectingViews.contains(childAt);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (cellAndSpan != null && !z && (childAt instanceof Reorderable)) {
                    new ReorderPreviewAnimation(this, (Reorderable) childAt, i, layoutParams.cellX, layoutParams.cellY, cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.spanX, cellAndSpan.spanY).animate();
                }
            }
        }
    }

    class ReorderPreviewAnimation {
        private static final float CHILD_DIVIDEND = 4.0f;
        private static final int HINT_DURATION = 650;
        public static final int MODE_HINT = 0;
        public static final int MODE_PREVIEW = 1;
        private static final int PREVIEW_DURATION = 300;
        ValueAnimator a;
        float animationProgress = 0.0f;
        final Reorderable child;
        float finalDeltaX;
        float finalDeltaY;
        final float finalScale;
        float initDeltaX;
        float initDeltaY;
        float initScale;
        final int mode;
        boolean repeating = false;
        final /* synthetic */ CellLayout this$0;

        public ReorderPreviewAnimation(CellLayout cellLayout, Reorderable reorderable, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            CellLayout cellLayout2 = cellLayout;
            Reorderable reorderable2 = reorderable;
            int i8 = i;
            this.this$0 = cellLayout2;
            int i9 = i6;
            int i10 = i7;
            cellLayout.regionToCenterPoint(i2, i3, i9, i10, cellLayout2.mTmpPoint);
            int i11 = cellLayout2.mTmpPoint[0];
            int i12 = 1;
            int i13 = cellLayout2.mTmpPoint[1];
            cellLayout.regionToCenterPoint(i4, i5, i9, i10, cellLayout2.mTmpPoint);
            int i14 = cellLayout2.mTmpPoint[0] - i11;
            int i15 = cellLayout2.mTmpPoint[1] - i13;
            this.child = reorderable2;
            this.mode = i8;
            this.finalDeltaX = 0.0f;
            this.finalDeltaY = 0.0f;
            reorderable2.getReorderBounceOffset(cellLayout2.mTmpPointF);
            this.initDeltaX = cellLayout2.mTmpPointF.x;
            this.initDeltaY = cellLayout2.mTmpPointF.y;
            this.initScale = reorderable.getReorderBounceScale();
            this.finalScale = 1.0f - ((CHILD_DIVIDEND / ((float) reorderable.getView().getWidth())) * this.initScale);
            i12 = i8 == 0 ? -1 : i12;
            if (i14 != i15 || i14 != 0) {
                if (i15 == 0) {
                    this.finalDeltaX = ((float) (-i12)) * Math.signum((float) i14) * cellLayout2.mReorderPreviewAnimationMagnitude;
                } else if (i14 == 0) {
                    this.finalDeltaY = ((float) (-i12)) * Math.signum((float) i15) * cellLayout2.mReorderPreviewAnimationMagnitude;
                } else {
                    float f = (float) i15;
                    float f2 = (float) i14;
                    double atan = Math.atan((double) (f / f2));
                    float f3 = (float) (-i12);
                    this.finalDeltaX = (float) ((int) (((double) (Math.signum(f2) * f3)) * Math.abs(Math.cos(atan) * ((double) cellLayout2.mReorderPreviewAnimationMagnitude))));
                    this.finalDeltaY = (float) ((int) (((double) (f3 * Math.signum(f))) * Math.abs(Math.sin(atan) * ((double) cellLayout2.mReorderPreviewAnimationMagnitude))));
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void setInitialAnimationValuesToBaseline() {
            this.initScale = 1.0f;
            this.initDeltaX = 0.0f;
            this.initDeltaY = 0.0f;
        }

        /* access modifiers changed from: package-private */
        public void animate() {
            boolean z = this.finalDeltaX == 0.0f && this.finalDeltaY == 0.0f;
            if (this.this$0.mShakeAnimators.containsKey(this.child)) {
                ReorderPreviewAnimation reorderPreviewAnimation = this.this$0.mShakeAnimators.get(this.child);
                this.this$0.mShakeAnimators.remove(this.child);
                if (z) {
                    reorderPreviewAnimation.finishAnimation();
                    return;
                }
                reorderPreviewAnimation.cancel();
            }
            if (!z) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, CellLayout.ANIMATION_PROGRESS, new float[]{0.0f, 1.0f});
                this.a = ofFloat;
                if (ValueAnimator.areAnimatorsEnabled()) {
                    ofFloat.setRepeatMode(2);
                    ofFloat.setRepeatCount(-1);
                }
                ofFloat.setDuration(this.mode == 0 ? 650 : 300);
                ofFloat.setStartDelay((long) ((int) (Math.random() * 60.0d)));
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationRepeat(Animator animator) {
                        ReorderPreviewAnimation.this.setInitialAnimationValuesToBaseline();
                        ReorderPreviewAnimation.this.repeating = true;
                    }
                });
                this.this$0.mShakeAnimators.put(this.child, this);
                ofFloat.start();
            }
        }

        /* access modifiers changed from: private */
        public void setAnimationProgress(float f) {
            this.animationProgress = f;
            if (this.mode == 0 && this.repeating) {
                f = 1.0f;
            }
            float f2 = 1.0f - f;
            this.child.setReorderBounceOffset((this.finalDeltaX * f) + (this.initDeltaX * f2), (f * this.finalDeltaY) + (f2 * this.initDeltaY));
            float f3 = this.animationProgress;
            this.child.setReorderBounceScale((this.finalScale * f3) + ((1.0f - f3) * this.initScale));
        }

        private void cancel() {
            ValueAnimator valueAnimator = this.a;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
        }

        /* access modifiers changed from: package-private */
        public void finishAnimation() {
            ValueAnimator valueAnimator = this.a;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            setInitialAnimationValuesToBaseline();
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, CellLayout.ANIMATION_PROGRESS, new float[]{this.animationProgress, 0.0f});
            this.a = ofFloat;
            ofFloat.setInterpolator(Interpolators.DEACCEL_1_5);
            this.a.setDuration(150);
            this.a.start();
        }
    }

    private void completeAndClearReorderPreviewAnimations() {
        for (ReorderPreviewAnimation finishAnimation : this.mShakeAnimators.values()) {
            finishAnimation.finishAnimation();
        }
        this.mShakeAnimators.clear();
    }

    private void commitTempPlacement(View view) {
        int i;
        this.mTmpOccupied.copyTo(this.mOccupied);
        int idForScreen = getWorkspace().getIdForScreen(this);
        if (this.mContainerType == 1) {
            idForScreen = -1;
            i = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
        } else {
            i = -100;
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i2);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            ItemInfo itemInfo = (ItemInfo) childAt.getTag();
            if (!(itemInfo == null || childAt == view)) {
                boolean z = (itemInfo.cellX == layoutParams.tmpCellX && itemInfo.cellY == layoutParams.tmpCellY && itemInfo.spanX == layoutParams.cellHSpan && itemInfo.spanY == layoutParams.cellVSpan) ? false : true;
                int i3 = layoutParams.tmpCellX;
                layoutParams.cellX = i3;
                itemInfo.cellX = i3;
                int i4 = layoutParams.tmpCellY;
                layoutParams.cellY = i4;
                itemInfo.cellY = i4;
                itemInfo.spanX = layoutParams.cellHSpan;
                itemInfo.spanY = layoutParams.cellVSpan;
                if (z) {
                    Launcher.cast(this.mActivity).getModelWriter().modifyItemInDatabase(itemInfo, i, idForScreen, itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY);
                }
            }
        }
    }

    private void setUseTempCoords(boolean z) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((LayoutParams) this.mShortcutsAndWidgets.getChildAt(i).getLayoutParams()).useTmpCoords = z;
        }
    }

    private ItemConfiguration findConfigurationNoShuffle(int i, int i2, int i3, int i4, int i5, int i6, View view, ItemConfiguration itemConfiguration) {
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        findNearestVacantArea(i, i2, i3, i4, i5, i6, iArr, iArr2);
        if (iArr[0] < 0 || iArr[1] < 0) {
            itemConfiguration2.isSolution = false;
        } else {
            copyCurrentStateToSolution(itemConfiguration2, false);
            itemConfiguration2.cellX = iArr[0];
            itemConfiguration2.cellY = iArr[1];
            itemConfiguration2.spanX = iArr2[0];
            itemConfiguration2.spanY = iArr2[1];
            itemConfiguration2.isSolution = true;
        }
        return itemConfiguration2;
    }

    private void getDirectionVectorForDrop(int i, int i2, int i3, int i4, View view, int[] iArr) {
        int i5 = i3;
        int i6 = i4;
        int[] iArr2 = iArr;
        int[] iArr3 = new int[2];
        int i7 = i3;
        int i8 = i4;
        findNearestArea(i, i2, i7, i8, iArr3);
        Rect rect = new Rect();
        cellToRect(iArr3[0], iArr3[1], i7, i8, rect);
        rect.offset(i - rect.centerX(), i2 - rect.centerY());
        Rect rect2 = new Rect();
        getViewsIntersectingRegion(iArr3[0], iArr3[1], i7, i8, view, rect2, this.mIntersectingViews);
        int width = rect2.width();
        int height = rect2.height();
        cellToRect(rect2.left, rect2.top, rect2.width(), rect2.height(), rect2);
        int centerX = (rect2.centerX() - i) / i5;
        int centerY = (rect2.centerY() - i2) / i6;
        int i9 = this.mCountX;
        if (width == i9 || i5 == i9) {
            centerX = 0;
        }
        int i10 = this.mCountY;
        if (height == i10 || i6 == i10) {
            centerY = 0;
        }
        if (centerX == 0 && centerY == 0) {
            iArr2[0] = 1;
            iArr2[1] = 0;
            return;
        }
        computeDirectionVector((float) centerX, (float) centerY, iArr2);
    }

    private void getViewsIntersectingRegion(int i, int i2, int i3, int i4, View view, Rect rect, ArrayList<View> arrayList) {
        if (rect != null) {
            rect.set(i, i2, i + i3, i2 + i4);
        }
        arrayList.clear();
        Rect rect2 = new Rect(i, i2, i3 + i, i4 + i2);
        Rect rect3 = new Rect();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i5);
            if (childAt != view) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                rect3.set(layoutParams.cellX, layoutParams.cellY, layoutParams.cellX + layoutParams.cellHSpan, layoutParams.cellY + layoutParams.cellVSpan);
                if (Rect.intersects(rect2, rect3)) {
                    this.mIntersectingViews.add(childAt);
                    if (rect != null) {
                        rect.union(rect3);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isNearestDropLocationOccupied(int i, int i2, int i3, int i4, View view, int[] iArr) {
        int i5 = i3;
        int i6 = i4;
        int[] findNearestArea = findNearestArea(i, i2, i5, i6, iArr);
        getViewsIntersectingRegion(findNearestArea[0], findNearestArea[1], i5, i6, view, (Rect) null, this.mIntersectingViews);
        return !this.mIntersectingViews.isEmpty();
    }

    /* access modifiers changed from: package-private */
    public void revertTempState() {
        completeAndClearReorderPreviewAnimations();
        if (isItemPlacementDirty()) {
            int childCount = this.mShortcutsAndWidgets.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.mShortcutsAndWidgets.getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.tmpCellX != layoutParams.cellX || layoutParams.tmpCellY != layoutParams.cellY) {
                    layoutParams.tmpCellX = layoutParams.cellX;
                    layoutParams.tmpCellY = layoutParams.cellY;
                    animateChildToPosition(childAt, layoutParams.cellX, layoutParams.cellY, 150, 0, false, false);
                }
            }
            setItemPlacementDirty(false);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean createAreaForResize(int i, int i2, int i3, int i4, View view, int[] iArr, boolean z) {
        View view2 = view;
        boolean z2 = z;
        int[] iArr2 = new int[2];
        int i5 = i3;
        int i6 = i4;
        regionToCenterPoint(i, i2, i5, i6, iArr2);
        ItemConfiguration findReorderSolution = findReorderSolution(iArr2[0], iArr2[1], i5, i6, i3, i4, iArr, view, true, new ItemConfiguration());
        setUseTempCoords(true);
        if (findReorderSolution != null && findReorderSolution.isSolution) {
            copySolutionToTempState(findReorderSolution, view2);
            setItemPlacementDirty(true);
            animateItemsToSolution(findReorderSolution, view2, z2);
            if (z2) {
                commitTempPlacement((View) null);
                completeAndClearReorderPreviewAnimations();
                setItemPlacementDirty(false);
            } else {
                beginOrAdjustReorderPreviewAnimations(findReorderSolution, view2, 1);
            }
            this.mShortcutsAndWidgets.requestLayout();
        }
        return findReorderSolution.isSolution;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00ce  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int[] performReorder(int r20, int r21, int r22, int r23, int r24, int r25, android.view.View r26, int[] r27, int[] r28, int r29) {
        /*
            r19 = this;
            r11 = r19
            r12 = r26
            r13 = r29
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r24
            r4 = r25
            r5 = r27
            int[] r14 = r0.findNearestArea(r1, r2, r3, r4, r5)
            r15 = 2
            if (r28 != 0) goto L_0x001e
            int[] r0 = new int[r15]
            r16 = r0
            goto L_0x0020
        L_0x001e:
            r16 = r28
        L_0x0020:
            r10 = 3
            r9 = 1
            r8 = 0
            if (r13 == r15) goto L_0x002a
            if (r13 == r10) goto L_0x002a
            r0 = 4
            if (r13 != r0) goto L_0x0045
        L_0x002a:
            int[] r0 = r11.mPreviousReorderDirection
            r1 = r0[r8]
            r2 = -100
            if (r1 == r2) goto L_0x0045
            int[] r1 = r11.mDirectionVector
            r3 = r0[r8]
            r1[r8] = r3
            r3 = r0[r9]
            r1[r9] = r3
            if (r13 == r15) goto L_0x0040
            if (r13 != r10) goto L_0x0062
        L_0x0040:
            r0[r8] = r2
            r0[r9] = r2
            goto L_0x0062
        L_0x0045:
            int[] r6 = r11.mDirectionVector
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r24
            r4 = r25
            r5 = r26
            r0.getDirectionVectorForDrop(r1, r2, r3, r4, r5, r6)
            int[] r0 = r11.mPreviousReorderDirection
            int[] r1 = r11.mDirectionVector
            r2 = r1[r8]
            r0[r8] = r2
            r1 = r1[r9]
            r0[r9] = r1
        L_0x0062:
            int[] r7 = r11.mDirectionVector
            r17 = 1
            com.android.launcher3.CellLayout$ItemConfiguration r6 = new com.android.launcher3.CellLayout$ItemConfiguration
            r5 = 0
            r6.<init>()
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            r15 = r5
            r5 = r24
            r18 = r6
            r6 = r25
            r8 = r26
            r9 = r17
            r10 = r18
            com.android.launcher3.CellLayout$ItemConfiguration r9 = r0.findReorderSolution(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            com.android.launcher3.CellLayout$ItemConfiguration r8 = new com.android.launcher3.CellLayout$ItemConfiguration
            r8.<init>()
            r7 = r26
            com.android.launcher3.CellLayout$ItemConfiguration r5 = r0.findConfigurationNoShuffle(r1, r2, r3, r4, r5, r6, r7, r8)
            boolean r0 = r9.isSolution
            if (r0 == 0) goto L_0x00a2
            int r0 = r9.area()
            int r1 = r5.area()
            if (r0 < r1) goto L_0x00a2
            r5 = r9
            goto L_0x00a8
        L_0x00a2:
            boolean r0 = r5.isSolution
            if (r0 == 0) goto L_0x00a7
            goto L_0x00a8
        L_0x00a7:
            r5 = r15
        L_0x00a8:
            r0 = -1
            if (r13 != 0) goto L_0x00ce
            if (r5 == 0) goto L_0x00c3
            r1 = 0
            r11.beginOrAdjustReorderPreviewAnimations(r5, r12, r1)
            int r0 = r5.cellX
            r14[r1] = r0
            int r0 = r5.cellY
            r2 = 1
            r14[r2] = r0
            int r0 = r5.spanX
            r16[r1] = r0
            int r0 = r5.spanY
            r16[r2] = r0
            goto L_0x00cd
        L_0x00c3:
            r1 = 0
            r2 = 1
            r16[r2] = r0
            r16[r1] = r0
            r14[r2] = r0
            r14[r1] = r0
        L_0x00cd:
            return r14
        L_0x00ce:
            r1 = 0
            r2 = 1
            r11.setUseTempCoords(r2)
            if (r5 == 0) goto L_0x010f
            int r0 = r5.cellX
            r14[r1] = r0
            int r0 = r5.cellY
            r14[r2] = r0
            int r0 = r5.spanX
            r16[r1] = r0
            int r0 = r5.spanY
            r16[r2] = r0
            r0 = 2
            r3 = 3
            if (r13 == r2) goto L_0x00ed
            if (r13 == r0) goto L_0x00ed
            if (r13 != r3) goto L_0x010d
        L_0x00ed:
            r11.copySolutionToTempState(r5, r12)
            r11.setItemPlacementDirty(r2)
            if (r13 != r0) goto L_0x00f7
            r9 = r2
            goto L_0x00f8
        L_0x00f7:
            r9 = r1
        L_0x00f8:
            r11.animateItemsToSolution(r5, r12, r9)
            if (r13 == r0) goto L_0x0104
            if (r13 != r3) goto L_0x0100
            goto L_0x0104
        L_0x0100:
            r11.beginOrAdjustReorderPreviewAnimations(r5, r12, r2)
            goto L_0x010d
        L_0x0104:
            r11.commitTempPlacement(r12)
            r19.completeAndClearReorderPreviewAnimations()
            r11.setItemPlacementDirty(r1)
        L_0x010d:
            r9 = r2
            goto L_0x0118
        L_0x010f:
            r16[r2] = r0
            r16[r1] = r0
            r14[r2] = r0
            r14[r1] = r0
            r9 = r1
        L_0x0118:
            r0 = 2
            if (r13 == r0) goto L_0x011d
            if (r9 != 0) goto L_0x0120
        L_0x011d:
            r11.setUseTempCoords(r1)
        L_0x0120:
            com.android.launcher3.ShortcutAndWidgetContainer r0 = r11.mShortcutsAndWidgets
            r0.requestLayout()
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.CellLayout.performReorder(int, int, int, int, int, int, android.view.View, int[], int[], int):int[]");
    }

    /* access modifiers changed from: package-private */
    public void setItemPlacementDirty(boolean z) {
        this.mItemPlacementDirty = z;
    }

    /* access modifiers changed from: package-private */
    public boolean isItemPlacementDirty() {
        return this.mItemPlacementDirty;
    }

    private static class ItemConfiguration extends CellAndSpan {
        ArrayList<View> intersectingViews;
        boolean isSolution;
        final ArrayMap<View, CellAndSpan> map;
        private final ArrayMap<View, CellAndSpan> savedMap;
        final ArrayList<View> sortedViews;

        private ItemConfiguration() {
            this.map = new ArrayMap<>();
            this.savedMap = new ArrayMap<>();
            this.sortedViews = new ArrayList<>();
            this.isSolution = false;
        }

        /* access modifiers changed from: package-private */
        public void save() {
            for (View next : this.map.keySet()) {
                this.savedMap.get(next).copyFrom(this.map.get(next));
            }
        }

        /* access modifiers changed from: package-private */
        public void restore() {
            for (View next : this.savedMap.keySet()) {
                this.map.get(next).copyFrom(this.savedMap.get(next));
            }
        }

        /* access modifiers changed from: package-private */
        public void add(View view, CellAndSpan cellAndSpan) {
            this.map.put(view, cellAndSpan);
            this.savedMap.put(view, new CellAndSpan());
            this.sortedViews.add(view);
        }

        /* access modifiers changed from: package-private */
        public int area() {
            return this.spanX * this.spanY;
        }

        /* access modifiers changed from: package-private */
        public void getBoundingRectForViews(ArrayList<View> arrayList, Rect rect) {
            Iterator<View> it = arrayList.iterator();
            boolean z = true;
            while (it.hasNext()) {
                CellAndSpan cellAndSpan = this.map.get(it.next());
                if (z) {
                    rect.set(cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.cellX + cellAndSpan.spanX, cellAndSpan.cellY + cellAndSpan.spanY);
                    z = false;
                } else {
                    rect.union(cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.cellX + cellAndSpan.spanX, cellAndSpan.cellY + cellAndSpan.spanY);
                }
            }
        }
    }

    public int[] findNearestArea(int i, int i2, int i3, int i4, int[] iArr) {
        return findNearestArea(i, i2, i3, i4, i3, i4, false, iArr, (int[]) null);
    }

    /* access modifiers changed from: package-private */
    public boolean existsEmptyCell() {
        return findCellForSpan((int[]) null, 1, 1);
    }

    public boolean findCellForSpan(int[] iArr, int i, int i2) {
        if (iArr == null) {
            iArr = new int[2];
        }
        return this.mOccupied.findVacantCell(iArr, i, i2);
    }

    /* access modifiers changed from: package-private */
    public void onDragEnter() {
        this.mDragging = true;
    }

    /* access modifiers changed from: package-private */
    public void onDragExit() {
        if (this.mDragging) {
            this.mDragging = false;
        }
        int[] iArr = this.mDragCell;
        iArr[1] = -1;
        iArr[0] = -1;
        int[] iArr2 = this.mDragCellSpan;
        iArr2[1] = -1;
        iArr2[0] = -1;
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        this.mDragOutlineCurrent = (this.mDragOutlineCurrent + 1) % this.mDragOutlineAnims.length;
        revertTempState();
        setIsDragOverlapping(false);
    }

    /* access modifiers changed from: package-private */
    public void onDropChild(View view) {
        if (view != null) {
            ((LayoutParams) view.getLayoutParams()).dropped = true;
            view.requestLayout();
            markCellsAsOccupiedForView(view);
        }
    }

    public void cellToRect(int i, int i2, int i3, int i4, Rect rect) {
        int i5 = this.mCellWidth;
        int i6 = this.mCellHeight;
        int paddingLeft = getPaddingLeft() + ((int) Math.ceil((double) (((float) getUnusedHorizontalSpace()) / 2.0f)));
        int paddingTop = getPaddingTop();
        int i7 = paddingLeft + (this.mBorderSpace.x * i) + (i * i5);
        int i8 = paddingTop + (this.mBorderSpace.y * i2) + (i2 * i6);
        rect.set(i7, i8, (i5 * i3) + ((i3 - 1) * this.mBorderSpace.x) + i7, (i6 * i4) + ((i4 - 1) * this.mBorderSpace.y) + i8);
    }

    public void markCellsAsOccupiedForView(View view) {
        if ((view instanceof LauncherAppWidgetHostView) && (view.getTag() instanceof LauncherAppWidgetInfo)) {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) view.getTag();
            this.mOccupied.markCells(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY, launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.spanY, true);
        } else if (view != null && view.getParent() == this.mShortcutsAndWidgets) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            this.mOccupied.markCells(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, true);
        }
    }

    public void markCellsAsUnoccupiedForView(View view) {
        if ((view instanceof LauncherAppWidgetHostView) && (view.getTag() instanceof LauncherAppWidgetInfo)) {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) view.getTag();
            this.mOccupied.markCells(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY, launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.spanY, false);
        } else if (view != null && view.getParent() == this.mShortcutsAndWidgets) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            this.mOccupied.markCells(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, false);
        }
    }

    public int getDesiredWidth() {
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int i = this.mCountX;
        return paddingLeft + (this.mCellWidth * i) + ((i - 1) * this.mBorderSpace.x);
    }

    public int getDesiredHeight() {
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int i = this.mCountY;
        return paddingTop + (this.mCellHeight * i) + ((i - 1) * this.mBorderSpace.y);
    }

    public boolean isOccupied(int i, int i2) {
        if (i < this.mCountX && i2 < this.mCountY) {
            return this.mOccupied.cells[i][i2];
        }
        throw new RuntimeException("Position exceeds the bound of this CellLayout");
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public boolean canReorder;
        @ViewDebug.ExportedProperty
        public int cellHSpan;
        @ViewDebug.ExportedProperty
        public int cellVSpan;
        @ViewDebug.ExportedProperty
        public int cellX;
        @ViewDebug.ExportedProperty
        public int cellY;
        boolean dropped;
        public boolean isLockedToGrid;
        public int tmpCellX;
        public int tmpCellY;
        public boolean useTmpCoords;
        @ViewDebug.ExportedProperty
        public int x;
        @ViewDebug.ExportedProperty
        public int y;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.isLockedToGrid = true;
            this.canReorder = true;
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.isLockedToGrid = true;
            this.canReorder = true;
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.isLockedToGrid = true;
            this.canReorder = true;
            this.cellX = layoutParams.cellX;
            this.cellY = layoutParams.cellY;
            this.cellHSpan = layoutParams.cellHSpan;
            this.cellVSpan = layoutParams.cellVSpan;
        }

        public LayoutParams(int i, int i2, int i3, int i4) {
            super(-1, -1);
            this.isLockedToGrid = true;
            this.canReorder = true;
            this.cellX = i;
            this.cellY = i2;
            this.cellHSpan = i3;
            this.cellVSpan = i4;
        }

        public void setup(int i, int i2, boolean z, int i3, int i4, Point point, Rect rect) {
            setup(i, i2, z, i3, i4, 1.0f, 1.0f, point, rect);
        }

        public void setup(int i, int i2, boolean z, int i3, int i4, float f, float f2, Point point, Rect rect) {
            if (this.isLockedToGrid) {
                int i5 = this.cellHSpan;
                int i6 = this.cellVSpan;
                boolean z2 = this.useTmpCoords;
                int i7 = z2 ? this.tmpCellX : this.cellX;
                int i8 = z2 ? this.tmpCellY : this.cellY;
                if (z) {
                    i7 = (i3 - i7) - i5;
                }
                this.width = (Math.round(((float) ((i5 * i) + ((i5 - 1) * point.x))) / f) - this.leftMargin) - this.rightMargin;
                this.height = (Math.round(((float) ((i6 * i2) + ((i6 - 1) * point.y))) / f2) - this.topMargin) - this.bottomMargin;
                this.x = this.leftMargin + (i * i7) + (i7 * point.x);
                this.y = this.topMargin + (i2 * i8) + (i8 * point.y);
                if (rect != null) {
                    this.x -= rect.left;
                    this.y -= rect.top;
                    this.width += rect.left + rect.right;
                    this.height += rect.top + rect.bottom;
                }
            }
        }

        public void setCellXY(Point point) {
            this.cellX = point.x;
            this.cellY = point.y;
        }

        public String toString() {
            return "(" + this.cellX + ", " + this.cellY + ")";
        }
    }

    public static final class CellInfo extends CellAndSpan {
        public final View cell;
        final int container;
        final int screenId;

        public CellInfo(View view, ItemInfo itemInfo) {
            this.cellX = itemInfo.cellX;
            this.cellY = itemInfo.cellY;
            this.spanX = itemInfo.spanX;
            this.spanY = itemInfo.spanY;
            this.cell = view;
            this.screenId = itemInfo.screenId;
            this.container = itemInfo.container;
        }

        public String toString() {
            StringBuilder append = new StringBuilder().append("Cell[view=");
            View view = this.cell;
            return append.append(view == null ? "null" : view.getClass()).append(", x=").append(this.cellX).append(", y=").append(this.cellY).append("]").toString();
        }
    }

    public boolean hasReorderSolution(ItemInfo itemInfo) {
        ItemInfo itemInfo2 = itemInfo;
        int[] iArr = new int[2];
        boolean z = false;
        int i = 0;
        while (i < getCountX()) {
            int i2 = z;
            char c = z;
            while (i2 < getCountY()) {
                cellToPoint(i, i2, iArr);
                int i3 = i2;
                if (findReorderSolution(iArr[c], iArr[1], itemInfo2.minSpanX, itemInfo2.minSpanY, itemInfo2.spanX, itemInfo2.spanY, this.mDirectionVector, (View) null, true, new ItemConfiguration()).isSolution) {
                    return true;
                }
                i2 = i3 + 1;
                c = 0;
            }
            i++;
            z = false;
        }
        return z;
    }

    public boolean makeSpaceForHotseatMigration(boolean z) {
        int[] iArr = new int[2];
        cellToPoint(0, this.mCountY, iArr);
        ItemConfiguration itemConfiguration = new ItemConfiguration();
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = this.mCountX;
        if (!findReorderSolution(i, i2, i3, 1, i3, 1, new int[]{0, -1}, (View) null, false, itemConfiguration).isSolution) {
            return false;
        }
        if (z) {
            copySolutionToTempState(itemConfiguration, (View) null);
            commitTempPlacement((View) null);
            this.mOccupied.markCells(0, this.mCountY - 1, this.mCountX, 1, false);
        }
        return true;
    }

    public GridOccupancy cloneGridOccupancy() {
        GridOccupancy gridOccupancy = new GridOccupancy(this.mCountX, this.mCountY);
        this.mOccupied.copyTo(gridOccupancy);
        return gridOccupancy;
    }

    public boolean isRegionVacant(int i, int i2, int i3, int i4) {
        return this.mOccupied.isRegionVacant(i, i2, i3, i4);
    }
}
