package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.util.SystemUiController;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.window.WindowManagerProxy;
import com.android.launcher3.views.AbstractSlideInView;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.ArrowTipView;

public abstract class BaseWidgetSheet extends AbstractSlideInView<Launcher> implements View.OnClickListener, View.OnLongClickListener, DragSource, PopupDataProvider.PopupDataChangeListener, Insettable {
    protected static final int DEFAULT_MAX_HORIZONTAL_SPANS = 4;
    protected static final String KEY_WIDGETS_EDUCATION_TIP_SEEN = "launcher.widgets_education_tip_seen";
    private int mContentHorizontalMarginInPx = getResources().getDimensionPixelSize(R.dimen.widget_list_horizontal_margin);
    protected final Rect mInsets = new Rect();
    protected int mNavBarScrimHeight;
    private final Paint mNavBarScrimPaint;
    private Toast mWidgetInstructionToast;

    /* access modifiers changed from: protected */
    public abstract void onContentHorizontalMarginChanged(int i);

    public void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
    }

    public BaseWidgetSheet(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Paint paint = new Paint();
        this.mNavBarScrimPaint = paint;
        paint.setColor(Themes.getAttrColor(context, R.attr.allAppsNavBarScrimColor));
    }

    /* access modifiers changed from: protected */
    public int getScrimColor(Context context) {
        return context.getResources().getColor(R.color.widgets_picker_scrim);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mNavBarScrimHeight = getNavBarScrimHeight(WindowManagerProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).normalizeWindowInsets(getContext(), getRootWindowInsets(), new Rect()));
        ((Launcher) this.mActivityContext).getPopupDataProvider().setChangeListener(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((Launcher) this.mActivityContext).getPopupDataProvider().setChangeListener((PopupDataProvider.PopupDataChangeListener) null);
    }

    public final void onClick(View view) {
        Object obj;
        if (view instanceof WidgetCell) {
            obj = view.getTag();
        } else {
            obj = view.getParent() instanceof WidgetCell ? ((WidgetCell) view.getParent()).getTag() : null;
        }
        if (obj instanceof PendingAddShortcutInfo) {
            this.mWidgetInstructionToast = showShortcutToast(getContext(), this.mWidgetInstructionToast);
        } else {
            this.mWidgetInstructionToast = showWidgetToast(getContext(), this.mWidgetInstructionToast);
        }
    }

    public boolean onLongClick(View view) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DROP_TARGET, "1");
        }
        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "Widgets.onLongClick");
        view.cancelLongPress();
        if (!ItemLongClickListener.canStartDrag((Launcher) this.mActivityContext)) {
            return false;
        }
        if (view instanceof WidgetCell) {
            return beginDraggingWidget((WidgetCell) view);
        }
        if (view.getParent() instanceof WidgetCell) {
            return beginDraggingWidget((WidgetCell) view.getParent());
        }
        return true;
    }

    public void setInsets(Rect rect) {
        this.mInsets.set(rect);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.widget_list_horizontal_margin);
        if (dimensionPixelSize != this.mContentHorizontalMarginInPx) {
            onContentHorizontalMarginChanged(dimensionPixelSize);
            this.mContentHorizontalMarginInPx = dimensionPixelSize;
        }
    }

    private int getNavBarScrimHeight(WindowInsets windowInsets) {
        if (Utilities.ATLEAST_Q) {
            return windowInsets.getTappableElementInsets().bottom;
        }
        return windowInsets.getStableInsetBottom();
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        this.mNavBarScrimHeight = getNavBarScrimHeight(windowInsets);
        return super.onApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mNavBarScrimHeight > 0) {
            canvas.drawRect(0.0f, (float) (getHeight() - this.mNavBarScrimHeight), (float) getWidth(), (float) getHeight(), this.mNavBarScrimPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void doMeasure(int i, int i2) {
        int max;
        DeviceProfile deviceProfile = ((Launcher) this.mActivityContext).getDeviceProfile();
        if (deviceProfile.isTablet) {
            max = Math.max(deviceProfile.allAppsLeftRightMargin * 2, (this.mInsets.left + this.mInsets.right) * 2);
        } else if (this.mInsets.bottom > 0) {
            max = this.mInsets.left + this.mInsets.right;
        } else {
            Rect rect = deviceProfile.workspacePadding;
            max = Math.max(rect.left + rect.right, (this.mInsets.left + this.mInsets.right) * 2);
        }
        measureChildWithMargins(this.mContent, i, max, i2, deviceProfile.bottomSheetTopPadding);
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
    }

    /* access modifiers changed from: protected */
    public int computeMaxHorizontalSpans(View view, int i) {
        DeviceProfile deviceProfile = ((Launcher) this.mActivityContext).getDeviceProfile();
        int measuredWidth = (view.getMeasuredWidth() - i) - (this.mContentHorizontalMarginInPx * 2);
        Point cellSize = deviceProfile.getCellSize();
        if (cellSize.x > 0) {
            return measuredWidth / cellSize.x;
        }
        return 4;
    }

    private boolean beginDraggingWidget(WidgetCell widgetCell) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DROP_TARGET, "2");
        }
        WidgetImageView widgetView = widgetCell.getWidgetView();
        if (widgetView.getDrawable() == null && widgetCell.getAppWidgetHostViewPreview() == null) {
            return false;
        }
        PendingItemDragHelper pendingItemDragHelper = new PendingItemDragHelper(widgetCell);
        pendingItemDragHelper.setRemoteViewsPreview(widgetCell.getRemoteViewsPreview(), widgetCell.getAppWidgetHostViewScale());
        pendingItemDragHelper.setAppWidgetHostViewPreview(widgetCell.getAppWidgetHostViewPreview());
        if (widgetView.getDrawable() != null) {
            int[] iArr = new int[2];
            getPopupContainer().getLocationInDragLayer(widgetView, iArr);
            pendingItemDragHelper.startDrag(widgetView.getBitmapBounds(), widgetView.getDrawable().getIntrinsicWidth(), widgetView.getWidth(), new Point(iArr[0], iArr[1]), this, new DragOptions());
        } else {
            NavigableAppWidgetHostView appWidgetHostViewPreview = widgetCell.getAppWidgetHostViewPreview();
            int[] iArr2 = new int[2];
            getPopupContainer().getLocationInDragLayer(appWidgetHostViewPreview, iArr2);
            Rect rect = new Rect();
            appWidgetHostViewPreview.getWorkspaceVisualDragBounds(rect);
            pendingItemDragHelper.startDrag(rect, appWidgetHostViewPreview.getMeasuredWidth(), appWidgetHostViewPreview.getMeasuredWidth(), new Point(iArr2[0], iArr2[1]), this, new DragOptions());
        }
        close(true);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCloseComplete() {
        super.onCloseComplete();
        clearNavBarColor();
    }

    /* access modifiers changed from: protected */
    public void clearNavBarColor() {
        getSystemUiController().updateUiState(2, 0);
    }

    /* access modifiers changed from: protected */
    public void setupNavBarColor() {
        getSystemUiController().updateUiState(2, Themes.getAttrBoolean(getContext(), R.attr.isMainColorDark) ? 2 : 1);
    }

    /* access modifiers changed from: protected */
    public SystemUiController getSystemUiController() {
        return ((Launcher) this.mActivityContext).getSystemUiController();
    }

    public static Toast showWidgetToast(Context context, Toast toast) {
        if (toast != null) {
            toast.cancel();
        }
        Toast makeText = Toast.makeText(context, Utilities.wrapForTts(context.getText(R.string.long_press_widget_to_add), context.getString(R.string.long_accessible_way_to_add)), 0);
        makeText.show();
        return makeText;
    }

    private static Toast showShortcutToast(Context context, Toast toast) {
        if (toast != null) {
            toast.cancel();
        }
        Toast makeText = Toast.makeText(context, Utilities.wrapForTts(context.getText(R.string.long_press_shortcut_to_add), context.getString(R.string.long_accessible_way_to_add_shortcut)), 0);
        makeText.show();
        return makeText;
    }

    /* access modifiers changed from: protected */
    public ArrowTipView showEducationTipOnViewIfPossible(View view) {
        if (view == null || !ViewCompat.isLaidOut(view)) {
            return null;
        }
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        ArrowTipView showAtLocation = new ArrowTipView(this.mActivityContext, false).showAtLocation(getContext().getString(R.string.long_press_widget_to_add), iArr[0] + (view.getWidth() / 2), iArr[1]);
        if (showAtLocation != null) {
            ((Launcher) this.mActivityContext).getSharedPrefs().edit().putBoolean(KEY_WIDGETS_EDUCATION_TIP_SEEN, true).apply();
        }
        return showAtLocation;
    }

    /* access modifiers changed from: protected */
    public boolean hasSeenEducationTip() {
        return ((Launcher) this.mActivityContext).getSharedPrefs().getBoolean(KEY_WIDGETS_EDUCATION_TIP_SEEN, false) || Utilities.IS_RUNNING_IN_TEST_HARNESS;
    }

    /* access modifiers changed from: protected */
    public void setTranslationShift(float f) {
        super.setTranslationShift(f);
        ((Launcher) ActivityContext.lookupContext(getContext())).onWidgetsTransition(1.0f - f);
    }
}
