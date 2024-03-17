package com.android.launcher3.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.RemoteViews;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.R;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.icons.RoundDrawableWrapper;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.widget.dragndrop.AppWidgetHostViewDragListener;
import com.android.launcher3.widget.util.WidgetSizes;

public class PendingItemDragHelper extends DragPreviewProvider {
    private static final float MAX_WIDGET_SCALE = 1.25f;
    private final PendingAddItemInfo mAddInfo;
    private NavigableAppWidgetHostView mAppWidgetHostViewPreview;
    private final float mEnforcedRoundedCornersForWidget;
    private int[] mEstimatedCellSize;
    private RemoteViews mRemoteViewsPreview;
    private float mRemoteViewsPreviewScale = 1.0f;

    public PendingItemDragHelper(View view) {
        super(view);
        this.mAddInfo = (PendingAddItemInfo) view.getTag();
        this.mEnforcedRoundedCornersForWidget = RoundedCornerEnforcement.computeEnforcedRadius(view.getContext());
    }

    public void setRemoteViewsPreview(RemoteViews remoteViews, float f) {
        this.mRemoteViewsPreview = remoteViews;
        this.mRemoteViewsPreviewScale = f;
    }

    public void setAppWidgetHostViewPreview(NavigableAppWidgetHostView navigableAppWidgetHostView) {
        this.mAppWidgetHostViewPreview = navigableAppWidgetHostView;
    }

    public void startDrag(Rect rect, int i, int i2, Point point, DragSource dragSource, DragOptions dragOptions) {
        float f;
        Rect rect2;
        Point point2;
        DraggableView draggableView;
        FastBitmapDrawable fastBitmapDrawable;
        int i3;
        int i4;
        FastBitmapDrawable fastBitmapDrawable2;
        Rect rect3 = rect;
        int i5 = i;
        int i6 = i2;
        Point point3 = point;
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DROP_TARGET, "3");
        }
        Launcher launcher = Launcher.getLauncher(this.mView.getContext());
        LauncherAppState instance = LauncherAppState.getInstance(launcher);
        int[] estimateItemSize = launcher.getWorkspace().estimateItemSize(this.mAddInfo);
        this.mEstimatedCellSize = estimateItemSize;
        PendingAddItemInfo pendingAddItemInfo = this.mAddInfo;
        if (pendingAddItemInfo instanceof PendingAddWidgetInfo) {
            PendingAddWidgetInfo pendingAddWidgetInfo = (PendingAddWidgetInfo) pendingAddItemInfo;
            int min = Math.min((int) (((float) i5) * MAX_WIDGET_SCALE), estimateItemSize[0]);
            int[] iArr = new int[1];
            if (this.mRemoteViewsPreview != null) {
                LauncherAppWidgetHostView launcherAppWidgetHostView = new LauncherAppWidgetHostView(launcher);
                this.mAppWidgetHostViewPreview = launcherAppWidgetHostView;
                launcherAppWidgetHostView.setAppWidget(-1, ((PendingAddWidgetInfo) this.mAddInfo).info);
                DeviceProfile deviceProfile = launcher.getDeviceProfile();
                Rect rect4 = new Rect();
                this.mAppWidgetHostViewPreview.getWidgetInset(deviceProfile, rect4);
                this.mAppWidgetHostViewPreview.setPadding(rect4.left, rect4.top, rect4.right, rect4.bottom);
                this.mAppWidgetHostViewPreview.updateAppWidget(this.mRemoteViewsPreview);
                Size widgetPaddedSizePx = WidgetSizes.getWidgetPaddedSizePx(launcher, this.mAddInfo.componentName, deviceProfile, this.mAddInfo.spanX, this.mAddInfo.spanY);
                this.mAppWidgetHostViewPreview.measure(View.MeasureSpec.makeMeasureSpec(widgetPaddedSizePx.getWidth(), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(widgetPaddedSizePx.getHeight(), BasicMeasure.EXACTLY));
                this.mAppWidgetHostViewPreview.setClipChildren(false);
                this.mAppWidgetHostViewPreview.setClipToPadding(false);
                this.mAppWidgetHostViewPreview.setScaleToFit(this.mRemoteViewsPreviewScale);
            }
            NavigableAppWidgetHostView navigableAppWidgetHostView = this.mAppWidgetHostViewPreview;
            if (navigableAppWidgetHostView != null) {
                iArr[0] = navigableAppWidgetHostView.getMeasuredWidth();
                launcher.getDragController().addDragListener(new AppWidgetHostViewDragListener(launcher));
            }
            if (this.mAppWidgetHostViewPreview == null) {
                fastBitmapDrawable2 = new FastBitmapDrawable(new DatabaseWidgetPreviewLoader(launcher).generateWidgetPreview(pendingAddWidgetInfo.info, min, iArr));
                if (RoundedCornerEnforcement.isRoundedCornerEnabled()) {
                    fastBitmapDrawable2 = new RoundDrawableWrapper(fastBitmapDrawable2, this.mEnforcedRoundedCornersForWidget);
                }
            } else {
                fastBitmapDrawable2 = null;
            }
            if (iArr[0] < i5) {
                int i7 = (i5 - iArr[0]) / 2;
                if (i5 > i6) {
                    i7 = (i7 * i6) / i5;
                }
                rect3.left += i7;
                rect3.right -= i7;
            }
            NavigableAppWidgetHostView navigableAppWidgetHostView2 = this.mAppWidgetHostViewPreview;
            if (navigableAppWidgetHostView2 != null) {
                i4 = navigableAppWidgetHostView2.getMeasuredWidth();
                i3 = this.mAppWidgetHostViewPreview.getMeasuredHeight();
            } else {
                i4 = fastBitmapDrawable2.getIntrinsicWidth();
                i3 = fastBitmapDrawable2.getIntrinsicHeight();
            }
            float width = ((float) rect.width()) / ((float) i4);
            launcher.getDragController().addDragListener(new WidgetHostViewLoader(launcher, this.mView));
            f = width;
            draggableView = DraggableView.ofType(1);
            fastBitmapDrawable = fastBitmapDrawable2;
            point2 = null;
            rect2 = null;
        } else {
            Drawable fullResIcon = ((PendingAddShortcutInfo) pendingAddItemInfo).activityInfo.getFullResIcon(instance.getIconCache());
            LauncherIcons obtain = LauncherIcons.obtain(launcher);
            FastBitmapDrawable fastBitmapDrawable3 = new FastBitmapDrawable(obtain.createScaledBitmapWithoutShadow(fullResIcon));
            i4 = fastBitmapDrawable3.getIntrinsicWidth();
            int intrinsicHeight = fastBitmapDrawable3.getIntrinsicHeight();
            obtain.recycle();
            float f2 = ((float) launcher.getDeviceProfile().iconSizePx) / ((float) i4);
            Point point4 = new Point(this.previewPadding / 2, this.previewPadding / 2);
            DeviceProfile deviceProfile2 = launcher.getDeviceProfile();
            int i8 = deviceProfile2.iconSizePx;
            int dimensionPixelSize = launcher.getResources().getDimensionPixelSize(R.dimen.widget_preview_shortcut_padding);
            rect3.left += dimensionPixelSize;
            rect3.top += dimensionPixelSize;
            Rect rect5 = new Rect();
            rect5.left = (this.mEstimatedCellSize[0] - i8) / 2;
            rect5.right = rect5.left + i8;
            rect5.top = (((this.mEstimatedCellSize[1] - i8) - deviceProfile2.iconTextSizePx) - deviceProfile2.iconDrawablePaddingPx) / 2;
            rect5.bottom = rect5.top + i8;
            f = f2;
            i3 = intrinsicHeight;
            draggableView = DraggableView.ofType(0);
            fastBitmapDrawable = fastBitmapDrawable3;
            rect2 = rect5;
            point2 = point4;
        }
        float f3 = (float) i4;
        int i9 = ((int) (((f * f3) - f3) / 2.0f)) + point3.x + rect3.left;
        int i10 = point3.y + rect3.top;
        float f4 = (float) i3;
        int i11 = i10 + ((int) (((f * f4) - f4) / 2.0f));
        if (this.mAppWidgetHostViewPreview != null) {
            launcher.getDragController().startDrag((View) this.mAppWidgetHostViewPreview, draggableView, i9, i11, dragSource, (ItemInfo) this.mAddInfo, point2, rect2, f, f, dragOptions);
            return;
        }
        launcher.getDragController().startDrag((Drawable) fastBitmapDrawable, draggableView, i9, i11, dragSource, (ItemInfo) this.mAddInfo, point2, rect2, f, f, dragOptions);
    }

    /* access modifiers changed from: protected */
    public Bitmap convertPreviewToAlphaBitmap(Bitmap bitmap) {
        int[] iArr;
        if ((this.mAddInfo instanceof PendingAddShortcutInfo) || (iArr = this.mEstimatedCellSize) == null) {
            return super.convertPreviewToAlphaBitmap(bitmap);
        }
        int i = iArr[0];
        int i2 = iArr[1];
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ALPHA_8);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        float min = Math.min(((float) (i - this.blurSizeOutline)) / ((float) bitmap.getWidth()), ((float) (i2 - this.blurSizeOutline)) / ((float) bitmap.getHeight()));
        int width = (int) (((float) bitmap.getWidth()) * min);
        int height = (int) (min * ((float) bitmap.getHeight()));
        Rect rect2 = new Rect(0, 0, width, height);
        rect2.offset((i - width) / 2, (i2 - height) / 2);
        new Canvas(createBitmap).drawBitmap(bitmap, rect, rect2, new Paint(2));
        return createBitmap;
    }
}
